package ar.edu.itba.pod.client.query1;

import ar.edu.itba.pod.client.utils.City;
import ar.edu.itba.pod.client.utils.CsvUtils;
import ar.edu.itba.pod.client.utils.HazelcastUtils;
import ar.edu.itba.pod.client.utils.TimeLogger;
import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.query1.QueryCollator;
import ar.edu.itba.pod.query1.QueryCombinerFactory;
import ar.edu.itba.pod.query1.QueryMapper;
import ar.edu.itba.pod.query1.QueryReducerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.KeyValueSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        logger.info("tpe1-g4 query1 Client Starting ...");

        // -------- Get options --------
        String addressesString = System.getProperty("addresses");
        if (addressesString == null) {
            logger.error("No addresses provided");
            System.exit(1);
            return;
        }

        // -DinPath='path/to/input'
        String inPath = System.getProperty("inPath");
        // -DoutPath='path/to/output'
        String outPath = System.getProperty("outPath");

        // -Dcity='city'
        City city = City.valueOf(System.getProperty("city"));

        // -Dn='n'  | Cuantos registros se quieren leer
        String n = System.getProperty("n");

        Long recordCount = null;

        if (n != null) {
            try {
                recordCount = Long.parseLong(n);
            } catch (NumberFormatException e) {
                logger.error("Invalid record count");
                System.exit(1);
                return;
            }
        }

        // -Dcombiner='true' | Si se quiere usar combiner true por defecto
        String combiner = System.getProperty("combiner");
        boolean useCombiner = combiner == null || Boolean.parseBoolean(combiner);

        HazelcastInstance client;
        try {
            client = HazelcastUtils.getHazelcastInstance(addressesString);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid addresses provided");
            System.exit(1);
            return;
        }

        try (TimeLogger timeLogger = new TimeLogger(outPath + "/query1-time.txt")) {

            // -------- Read data --------
            logger.info("Reading data from files");
            timeLogger.logStartedReading();

            final Map<String, Infraction> infractions;

            infractions = CsvUtils.parseInfractions(inPath, city);

            // Read tickets
            final List<Ticket> tickets;

            tickets = CsvUtils.parseTickets(inPath, city, infractions, recordCount);

            timeLogger.logFinishedReading();

            // -------- Load data --------

            logger.info("Loading data into Hazelcast");
            timeLogger.logStartedLoadingToHazelcast();

            final IList<String> ticketInfractions = client.getList("query1-ticketInfractions");

            ticketInfractions.addAll(tickets.stream()
                    .map(ticket -> ticket.infraction().description())
                    .toList());

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            final var source = KeyValueSource.fromList(ticketInfractions);
            final var jobTracker = client.getJobTracker("g4-query1");
            final var job = jobTracker.newJob(source);

            final ICompletableFuture<List<Map.Entry<String, Long>>> future;

            if (useCombiner) {
                future = job
                        .mapper(new QueryMapper())
                        .combiner(new QueryCombinerFactory())
                        .reducer(new QueryReducerFactory())
                        .submit(new QueryCollator());
            } else {
                future = job
                        .mapper(new QueryMapper())
                        .reducer(new QueryReducerFactory())
                        .submit(new QueryCollator());
            }

            final List<Map.Entry<String, Long>> result = future.get();

            timeLogger.logFinishedMapReduce();

            // -------- Write output --------
            logger.info("Writing output to file");
            timeLogger.logStartedWriting();

            CsvUtils.writeCsv(
                    outPath + "/query1.csv",
                    new String[] {"Infraction", "Tickets"},
                    result,
                    entry -> entry.getKey() + ";" + entry.getValue());

            timeLogger.logFinishedWriting();

        } catch (IOException e) {
            logger.error("Error reading or writing files, {}", e.getMessage());
            client.shutdown();
            System.exit(1);
            return;
        }

        client.shutdown();
        logger.info("tpe1-g4 query1 Client Finished");
    }
}
