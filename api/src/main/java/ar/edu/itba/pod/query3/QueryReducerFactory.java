package ar.edu.itba.pod.query3;

import ar.edu.itba.pod.query4.PlateCountPair;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

public class QueryReducerFactory implements ReducerFactory<String, Double, Double> {

    @Override
    public QueryReducer newReducer(String key) {
        return new QueryReducer();
    }

    private class QueryReducer extends Reducer<Double, Double> {
        private double revenue;
        @Override
        public void beginReduce() {
            revenue = 0;
        }

        @Override
        public void reduce(Double fineValue) {
            revenue += fineValue;
        }

        @Override
        public Double finalizeReduce() {
            return revenue;
        }
    }
}
