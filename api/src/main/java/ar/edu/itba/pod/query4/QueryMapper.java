package ar.edu.itba.pod.query4;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

// Filtra los tickets que no estan dentro del rango de fechas
// [, (county, plate, date)] -> [county, plate]
public class QueryMapper implements Mapper<String, CountyPlateDateTuple, String, String> {

    private final LocalDate startDate;
    private final LocalDate endDate;

    public QueryMapper(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void map(
            String s, CountyPlateDateTuple countyPlateDateTuple, Context<String, String> context) {

        if (countyPlateDateTuple.date().isBefore(startDate)
                || countyPlateDateTuple.date().isAfter(endDate)) {
            return;
        }

        context.emit(countyPlateDateTuple.county(), countyPlateDateTuple.plate());
    }
}
