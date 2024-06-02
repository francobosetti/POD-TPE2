package ar.edu.itba.pod.query1;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

// [infraction_desc, 1] -> [infraction_desc, count]
public class QueryCombinerFactory implements CombinerFactory<String, Long, Long> {
    @Override
    public Combiner<Long, Long> newCombiner(String infractionDescription) {
        return new QueryCombiner();
    }

    private static class QueryCombiner extends Combiner<Long, Long> {
        private long count = 0;

        @Override
        public void combine(Long value) {
            count += value;
        }

        @Override
        public Long finalizeChunk() {
            return count;
        }

        @Override
        public void reset() {
            count = 0;
        }
    }
}
