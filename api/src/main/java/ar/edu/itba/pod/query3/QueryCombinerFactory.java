package ar.edu.itba.pod.query3;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

// [agency, fine value] -> [agency, partial revenue]
public class QueryCombinerFactory implements CombinerFactory<String, Double, Double> {
    @Override
    public Combiner<Double, Double> newCombiner(String infractionDescription) {
        return new QueryCombiner();
    }

    private static class QueryCombiner extends Combiner<Double, Double> {
        private double count = 0;

        @Override
        public void combine(Double value) {
            count += value;
        }

        @Override
        public Double finalizeChunk() {
            return count;
        }

        @Override
        public void reset() {
            count = 0;
        }
    }
}
