package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.query4.PlateDatePair;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.time.LocalDate;

// [agency, fine value] -> [agency, fine value]
public class QueryMapper implements Mapper<String, Double, String, Double> {
    @Override
    public void map(String agency, Double fineValue, Context<String, Double> context) {
        context.emit(agency, fineValue);
    }
}
