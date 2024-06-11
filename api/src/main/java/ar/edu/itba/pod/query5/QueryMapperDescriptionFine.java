package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class QueryMapperDescriptionFine implements Mapper<String, Double, String, Double> {

    @Override
    public void map(String s, Double aDouble, Context<String, Double> context) {
        context.emit(s, aDouble);
    }
}