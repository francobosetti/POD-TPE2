package ar.edu.itba.pod.client.utils;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeLogger implements Closeable {

    private final BufferedWriter writer;

    public TimeLogger(String path) throws IOException {
        writer =
                Files.newBufferedWriter(
                        Path.of(path),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void logStartedReading() throws IOException {
        writeWithTimeStamps("Started reading file");
    }

    public void logFinishedReading() throws IOException {
        writeWithTimeStamps("Finished reading file");
    }

    public void logStartedLoadingToHazelcast() throws IOException {
        writeWithTimeStamps("Started loading data to hazelcast cluster");
    }

    public void logFinishedLoadingToHazelcast() throws IOException {
        writeWithTimeStamps("Finished loading data to hazelcast cluster");
    }

    public void logStartedMapReduce() throws IOException {
        writeWithTimeStamps("Started map reduce");
    }

    public void logFinishedMapReduce() throws IOException {
        writeWithTimeStamps("Finished map reduce");
    }

    public void logStartedWriting() throws IOException {
        writeWithTimeStamps("Started writing file");
    }

    public void logFinishedWriting() throws IOException {
        writeWithTimeStamps("Finished writing file");
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    private void writeWithTimeStamps(String message) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        writer.write(formattedNow + " - " + message + "\n");
    }
}
