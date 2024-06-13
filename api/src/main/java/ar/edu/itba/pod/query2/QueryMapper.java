package ar.edu.itba.pod.query2;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

// [_, CountyInfraction] -> [CountyInfraction, 1]
public class QueryMapper implements Mapper<String, CountyInfraction, CountyInfraction, Long> {

    @Override
    public void map(
            String s, CountyInfraction countyInfraction, Context<CountyInfraction, Long> context) {
        context.emit(countyInfraction, 1L);
    }
}
