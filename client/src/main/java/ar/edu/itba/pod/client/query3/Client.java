package ar.edu.itba.pod.client.query3;

import ar.edu.itba.pod.client.utils.City;
import ar.edu.itba.pod.client.utils.CsvUtils;
import ar.edu.itba.pod.client.utils.HazelcastUtils;
import ar.edu.itba.pod.client.utils.TimeLogger;
import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;

import com.hazelcast.core.HazelcastInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Client {
    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        logger.info("tpe1-g4 query3 Client Starting ...");

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

            tickets = CsvUtils.parseTickets(inPath, city, infractions);

            timeLogger.logFinishedReading();

            // -------- Load data --------

            logger.info("Loading data into Hazelcast");
            timeLogger.logStartedLoadingToHazelcast();

            timeLogger.logFinishedLoadingToHazelcast();

            // -------- MapReduce --------
            logger.info("Executing MapReduce");
            timeLogger.logStartedMapReduce();

            timeLogger.logFinishedMapReduce();

            // -------- Write output --------
            logger.info("Writing output to file");
            timeLogger.logStartedWriting();


            timeLogger.logFinishedWriting();

        } catch (IOException e) {
            logger.error("Error reading or writing files, {}", e.getMessage());
            client.shutdown();
            System.exit(1);
            return;
        }

        client.shutdown();
        logger.info("tpe1-g4 query3 Client Finished");
    }
}
