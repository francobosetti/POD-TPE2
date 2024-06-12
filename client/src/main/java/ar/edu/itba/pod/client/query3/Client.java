package ar.edu.itba.pod.client.query3;

import ar.edu.itba.pod.client.utils.City;
import ar.edu.itba.pod.client.utils.CsvUtils;
import ar.edu.itba.pod.client.utils.HazelcastUtils;
import ar.edu.itba.pod.client.utils.TimeLogger;
import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;

import ar.edu.itba.pod.query3.QueryCollator;
import ar.edu.itba.pod.query3.QueryMapper;
import ar.edu.itba.pod.query3.QueryReducerFactory;
import com.hazelcast.core.HazelcastInstance;

import com.hazelcast.core.MultiMap;
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
        logger.info("tpe2-g4 query3 Client Starting ...");

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

        // -Dn='n' | Cantidad de agencias a mostrar
        String n = System.getProperty("n");

        Long agenciesCount = null;

        try {
            agenciesCount = Long.parseLong(n);
        } catch (NumberFormatException e) {
            logger.error("Invalid agencies count");
            System.exit(1);
            return;
        }

        HazelcastInstance client;
        try {
            client = HazelcastUtils.getHazelcastInstance(addressesString);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid addresses provided");
            System.exit(1);
            return;
        }

        try (TimeLogger timeLogger = new TimeLogger(outPath + "/query3-time.txt")) {

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

            final MultiMap<String, Double> finesMap = client.getMultiMap("query3");

            tickets.forEach(ticket -> finesMap.put(ticket.agency(), ticket.fine()));

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            final var source = KeyValueSource.fromMultiMap(finesMap);
            final var jobTracker = client.getJobTracker("g4-query3");
            final var job = jobTracker.newJob(source);

            final var future =
                    job.mapper(new QueryMapper())
                            .reducer(new QueryReducerFactory())
                            .submit(new QueryCollator(agenciesCount));

            final List<Map.Entry<String, Double>> result = future.get();

            timeLogger.logFinishedMapReduce();

            // -------- Write output --------
            logger.info("Writing output to file");
            timeLogger.logStartedWriting();

            CsvUtils.writeCsv(
                    outPath + "/query3.csv",
                    new String[] {"Issuing Agency", "Percentage"},
                    result,
                    entry ->
                            entry.getKey()
                                    + ";"
                                    + entry.getValue());

            timeLogger.logFinishedWriting();

        } catch (IOException e) {
            logger.error("Error reading or writing files, {}", e.getMessage());
            client.shutdown();
            System.exit(1);
            return;
        }

        client.shutdown();
        logger.info("tpe2-g4 query3 Client Finished");
    }
}
