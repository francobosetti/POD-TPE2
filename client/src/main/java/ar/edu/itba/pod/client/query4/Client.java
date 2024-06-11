package ar.edu.itba.pod.client.query4;

import ar.edu.itba.pod.client.utils.City;
import ar.edu.itba.pod.client.utils.CsvUtils;
import ar.edu.itba.pod.client.utils.HazelcastUtils;
import ar.edu.itba.pod.client.utils.TimeLogger;
import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;
import ar.edu.itba.pod.query4.*;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.KeyValueSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        logger.info("tpe2-g4 query4 Client Starting ...");

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

        // -DmaxRecords='maxRecords' | Cuantos registros se quieren leer
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // -Dfrom='DD/MM/YYYY'
        // -Dto='DD/MM/YYYY'
        String fromString = System.getProperty("from");
        String toString = System.getProperty("to");

        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(fromString, formatter);
            to = LocalDate.parse(toString, formatter);
        } catch (Exception e) {
            logger.error("Invalid date format");
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

        try (TimeLogger timeLogger = new TimeLogger(outPath + "/query4-time.txt")) {

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

             MultiMap<String, PlateDatePair> ticketsMap = client.getMultiMap("query4");

            tickets.forEach(
                    ticket -> {
                        ticketsMap.put(
                                ticket.area(),
                                new PlateDatePair(ticket.plateNumber(), ticket.date()));
                    });

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            final var source = KeyValueSource.fromMultiMap(ticketsMap);
            final var jobTracker = client.getJobTracker("g4-query4");
            final var job = jobTracker.newJob(source);

            final var future =
                    job.mapper(new QueryMapper(from, to))
                            .reducer(new QueryReducerFactory())
                            .submit(new QueryCollator());

            final List<Map.Entry<String, PlateCountPair>> result = future.get();

            timeLogger.logFinishedMapReduce();

            // -------- Write output --------
            logger.info("Writing output to file");
            timeLogger.logStartedWriting();

            CsvUtils.writeCsv(
                    outPath + "/query4.csv",
                    new String[] {"County", "Plate", "Tickets"},
                    result,
                    entry ->
                            entry.getKey()
                                    + ";"
                                    + entry.getValue().plate()
                                    + ";"
                                    + entry.getValue().count());

            timeLogger.logFinishedWriting();

        } catch (IOException e) {
            logger.error("Error reading or writing files, {}", e.getMessage());
            client.shutdown();
            System.exit(1);
            return;
        }

        client.shutdown();
        logger.info("tpe2-g4 query4 Client Finished");
    }
}
