package ar.edu.itba.pod.query3;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

// [agency, fine value] -> [agency, fine value]
public class QueryMapper implements Mapper<String, Double, String, Double> {
    @Override
    public void map(String agency, Double fineValue, Context<String, Double> context) {
        context.emit(agency, fineValue);
    }
}
