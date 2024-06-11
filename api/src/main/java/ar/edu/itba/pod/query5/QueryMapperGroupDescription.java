package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class QueryMapperGroupDescription implements Mapper<String, Double, Integer, String> {

    @Override
    public void map(String s, Double aDouble, Context<Integer,String> context) {
        int groupId = (int) (aDouble / 100);
        if (groupId == 0) {
            return;
        }
        context.emit(groupId*100, s);
    }
}