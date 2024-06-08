package ar.edu.itba.pod.query2;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

//[CountyInfraction, 1]->[CountyInfraction, count]
public class QueryCombinerFactory implements CombinerFactory<CountyInfraction, Long, Long> {
    @Override
    public Combiner<Long, Long> newCombiner(CountyInfraction countyInfraction) {
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
