package ar.edu.itba.pod.client.utils;

import ar.edu.itba.pod.models.Infraction;
import ar.edu.itba.pod.models.Ticket;

import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum City {
    NYC(
            "ticketsNYC.csv",
            /*
                "Plate",
                "Issue Date", // YYYY-MM-DD
                "InfractionCode",
                "Fine Amount",
                "County Name",
                "Issuing Agency"
            */
            (values, infraction) ->
                    new Ticket(
                            values[0],
                            LocalDate.parse(values[1]),
                            infraction,
                            Double.parseDouble(values[3]),
                            values[5],
                            values[4]),
            "infractionsNYC.csv",
            /* "CODE", "DEFINITION" */
            values -> new Infraction(values[0], values[1]),
            2),
    CHI(
            "ticketsCHI_100000.csv",
            /*
                "issue_date", // YYYY-MM-DD hh:mm:ss
                "license_plate_number",
                "violation_code",
                "unit_description",
                "fine_level1_amount",
                "community_area_name"
            */
            (values, infraction) ->
                    new Ticket(
                            values[1],
                            LocalDate.parse(values[0].split(" ")[0]),
                            infraction,
                            Double.parseDouble(values[4]),
                            values[3],
                            values[5]),
            "infractionsCHI.csv",
            /* "violation_code", "violation_description" */
            values -> new Infraction(values[0], values[1]),
            2);

    private final String ticketsFile;
    private final BiFunction<String[], Infraction, Ticket> ticketParser;
    private final String infractionsFile;
    private final Function<String[], Infraction> infractionParser;
    private final int infractionCodeIndex;

    City(
            String ticketsFile,
            BiFunction<String[], Infraction, Ticket> ticketParser,
            String infractionsFile,
            Function<String[], Infraction> infractionParser,
            int infractionCodeIndex) {
        this.ticketsFile = ticketsFile;
        this.ticketParser = ticketParser;
        this.infractionsFile = infractionsFile;
        this.infractionParser = infractionParser;
        this.infractionCodeIndex = infractionCodeIndex;
    }

    public String getTicketsFile() {
        return ticketsFile;
    }

    public String getInfractionsFile() {
        return infractionsFile;
    }

    public int getInfractionCodeIndex() {
        return infractionCodeIndex;
    }

    public Ticket parseTicket(String[] values, Infraction infraction) {
        return ticketParser.apply(values, infraction);
    }

    public Infraction parseInfraction(String[] values) {
        return infractionParser.apply(values);
    }
}
