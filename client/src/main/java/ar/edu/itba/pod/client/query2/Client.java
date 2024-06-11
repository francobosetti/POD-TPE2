package ar.edu.itba.pod.client.query2;

import ar.edu.itba.pod.client.utils.City;
import ar.edu.itba.pod.client.utils.CsvUtils;
import ar.edu.itba.pod.client.utils.HazelcastUtils;
import ar.edu.itba.pod.client.utils.TimeLogger;
import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;

import ar.edu.itba.pod.query2.QueryCollator;
import ar.edu.itba.pod.query2.QueryCombinerFactory;
import ar.edu.itba.pod.query2.QueryMapper;
import ar.edu.itba.pod.query2.QueryReducerFactory;
import ar.edu.itba.pod.query2.CountyInfraction;
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
        logger.info("tpe1-g4 query2 Client Starting ...");

        // -------- Get options --------
        // -Daddresses='10.0.0.2:5701;10.0.0.1:5701'
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

        // -DmaxRecords='maxRecords'  | Cuantos registros se quieren leer
        String maxRecords = System.getProperty("maxRecords");

        Long recordCount = null;

        if (maxRecords != null) {
            try {
                recordCount = Long.parseLong(maxRecords);
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

        try (TimeLogger timeLogger = new TimeLogger(outPath + "/query2-time.txt")) {

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

            final IList<CountyInfraction> ticketInfractions = client.getList("query2");

            ticketInfractions.addAll(tickets.stream()
                    .map(ticket -> new CountyInfraction(ticket.area(), ticket.infraction().description()))
                    .toList());

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            final var source = KeyValueSource.fromList(ticketInfractions);
            final var jobTracker = client.getJobTracker("g4-query2");
            final var job = jobTracker.newJob(source);

            final ICompletableFuture<List<Map.Entry<String, List<String>>>> future;

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

            final List<Map.Entry<String, List<String>>> result = future.get();

            timeLogger.logFinishedMapReduce();

            // -------- Write output --------
            logger.info("Writing output to file");
            timeLogger.logStartedWriting();

            CsvUtils.writeCsv(
                    outPath + "/query2.csv",
                    new String[] {"County", "InfractionTop1", "InfractionTop2", "InfractionTop3"},
                    result,
                    entry -> entry.getKey()
                            + ";" + (entry.getValue().isEmpty()? "-" : entry.getValue().get(0))
                            + ";" + (entry.getValue().size()<2? "-" : entry.getValue().get(1))
                            + ";" + (entry.getValue().size()<3? "-" : entry.getValue().get(2)));

            timeLogger.logFinishedWriting();

        } catch (IOException e) {
            logger.error("Error reading or writing files, {}", e.getMessage());
            client.shutdown();
            System.exit(1);
            return;
        }

        client.shutdown();
        logger.info("tpe1-g4 query2 Client Finished");
    }
}
