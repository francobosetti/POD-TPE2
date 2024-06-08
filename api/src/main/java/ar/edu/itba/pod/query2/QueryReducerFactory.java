package ar.edu.itba.pod.query2;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

// [CountyInfraction, [partial_count1, partial_count2, ...]] -> [CountyInfraction, count]
public class QueryReducerFactory implements ReducerFactory<CountyInfraction, Long, Long> {
    @Override
    public Reducer<Long, Long> newReducer(CountyInfraction key) {
        return new QueryReducer();
    }

    private class QueryReducer extends Reducer<Long, Long> {
        private long count;

        @Override
        public void beginReduce() {
            count = 0;
        }

        @Override
        public void reduce(Long value) {
            count += value;
        }

        @Override
        public Long finalizeReduce() {
            return count;
        }
    }
}
