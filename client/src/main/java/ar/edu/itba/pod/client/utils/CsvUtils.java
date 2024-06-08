package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvUtils {

    public static Map<String, Infraction> parseInfractions(String directory, City city)
            throws IOException {

        String file = directory + '/' + city.getInfractionsFile();

        return Files.readAllLines(Path.of(file)).stream()
                .skip(1)
                .map(line -> line.split(";"))
                .map(city::parseInfraction)
                .collect(Collectors.toMap(Infraction::id, infraction -> infraction));
    }

    public static List<Ticket> parseTickets(
            String directory, City city, Map<String, Infraction> infractions, Long recordCount) throws IOException {

        String file = directory + '/' + city.getTicketsFile();

        List<String> lines;

        try (Stream<String> stream = Files.lines(Path.of(file))) {
            if (recordCount != null) {
                lines = stream.skip(1).limit(recordCount).collect(Collectors.toList());
            } else {
                lines = stream.skip(1).collect(Collectors.toList());
            }
        }

        return lines.stream()
                .map(line -> line.split(";"))
                .map(
                        values ->
                                city.parseTicket(
                                        values,
                                        infractions.get(values[city.getInfractionCodeIndex()])))
                .toList();
    }

    public static <T> void writeCsv(String path, String[] headers, List<T> data, Function<T, String> toRow) throws IOException {
        var content = new StringBuilder();
        content.append(String.join(";", headers)).append("\n");

        data.forEach(d -> content.append(toRow.apply(d)).append("\n"));

        Files.writeString(Path.of(path), content.toString());
    }
}
