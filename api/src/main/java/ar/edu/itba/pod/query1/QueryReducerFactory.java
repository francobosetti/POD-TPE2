package ar.edu.itba.pod.query1;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class QueryReducerFactory implements ReducerFactory<String, Long, Long> {

    @Override
    public QueryReducer newReducer(String key) {
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
        public Long  finalizeReduce() {
            return count;
        }
    }
}
