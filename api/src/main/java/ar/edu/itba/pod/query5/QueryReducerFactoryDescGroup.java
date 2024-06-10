package ar.edu.itba.pod.query5;


import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class QueryReducerFactoryDescGroup implements ReducerFactory<String, Double, Double> {
    @Override
    public Reducer<Double, Double> newReducer(String s) {
        return new QueryReducer();
    }

    private class QueryReducer extends Reducer<Double, Double> {
        private double fineTotal;
        private long count;

        @Override
        public void beginReduce() {
            fineTotal = 0;
            count = 0;
        }

        @Override
        public void reduce(Double fine) {
            fineTotal += fine;
            count++;
        }

        @Override
        public Double finalizeReduce() {
            return fineTotal/count;
        }
    }
}
