package ar.edu.itba.pod.query1;

import ar.edu.itba.pod.models.Ticket;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class QueryMapper implements Mapper<String, Ticket, String, Long> {

    @Override
    public void map(String s, Ticket ticket, Context<String, Long> context) {
        context.emit(ticket.infraction().description(), 1L);
    }
}
