package ar.edu.itba.pod.client.query5;

import ar.edu.itba.pod.client.utils.City;
import ar.edu.itba.pod.client.utils.CsvUtils;
import ar.edu.itba.pod.client.utils.HazelcastUtils;
import ar.edu.itba.pod.client.utils.TimeLogger;
import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;

import ar.edu.itba.pod.query5.*;
import com.hazelcast.core.HazelcastInstance;

import com.hazelcast.core.IMap;
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
        logger.info("tpe2-g4 query5 Client Starting ...");

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

        HazelcastInstance client;
        try {
            client = HazelcastUtils.getHazelcastInstance(addressesString);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid addresses provided");
            System.exit(1);
            return;
        }

        try (TimeLogger timeLogger = new TimeLogger(outPath + "/query5-time.txt")) {

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

            MultiMap<String, Double> map = client.getMultiMap("query5");

            tickets.forEach(
                    t -> map.put(t.infraction().description(), t.fine())
            );

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            // map desc -> monto
            // red desc -> promedio
            // cargo datos...

            final var source = KeyValueSource.fromMultiMap(map);
            final var jobTracker = client.getJobTracker("g4-query5");
            final var job = jobTracker.newJob(source);

            final var future =
                    job.mapper(new QueryMapperDescriptionFine())
                            .reducer(new QueryReducerFactoryDescriptionAverage())
                            .submit();

            final Map<String, Double> result = future.get();

            timeLogger.logFinishedMapReduce();

            // -------- Load data 2 --------

            logger.info("Loading data into Hazelcast");
            timeLogger.logStartedLoadingToHazelcast();

            IMap<String, Double> map2 = client.getMap("query5");

            map2.putAll(result);

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            // map group (prom/100, con division entera) -> desc
            // red grup -> pares orden alfabetico
            // ordenar may -> men grupo, ordena los pares por primer valor y segundo en orden alfabetico

            final var source2 = KeyValueSource.fromMap(map2);
            final var jobTracker2 = client.getJobTracker("g4-query5-2");
            final var job2 = jobTracker2.newJob(source2);

            final var future2 =
                    job2.mapper(new QueryMapperGroupDescription())
                            .reducer(new QueryReducerFactoryGroupPair())
                            .submit(new QueryCollator());

            final List<Map.Entry<Integer, InfractionPair>> result2 = future2.get();

            timeLogger.logFinishedMapReduce();

            // -------- Write output --------
            logger.info("Writing output to file");
            timeLogger.logStartedWriting();

            CsvUtils.writeCsv(
                    outPath + "/query5.csv",
                    new String[] {"Group", "Infraction A", "Infraction B"},
                    result2,
                    entry ->
                            entry.getKey()
                                    + ";"
                                    + entry.getValue().in1()
                                    + ";"
                                    + entry.getValue().in2());

            timeLogger.logFinishedWriting();

        } catch (IOException e) {
            logger.error("Error reading or writing files, {}", e.getMessage());
            client.shutdown();
            System.exit(1);
            return;
        }

        client.shutdown();
        logger.info("tpe2-g4 query5 Client Finished");
    }
}
