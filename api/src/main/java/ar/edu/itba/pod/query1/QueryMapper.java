package ar.edu.itba.pod.query1;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

// [infraction_desc, infraction_desc] -> [infraction_desc, 1]
public class QueryMapper implements Mapper<String, String, String, Long> {

    @Override
    public void map(String s, String infraction, Context<String, Long> context) {
        context.emit(infraction, 1L);
    }
}
