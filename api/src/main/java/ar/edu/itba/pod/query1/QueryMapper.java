package ar.edu.itba.pod.query1;

import ar.edu.itba.pod.models.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class QueryMapper implements Mapper<String, String, String, Long> {

    @Override
    public void map(String s, String infraction, Context<String, Long> context) {
        context.emit(infraction, 1L);
    }
}
