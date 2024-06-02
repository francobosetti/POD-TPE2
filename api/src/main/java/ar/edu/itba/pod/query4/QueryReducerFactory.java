package ar.edu.itba.pod.query4;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashMap;
import java.util.Map;

// [county, (plate1, plate2, ...)] -> [county, (max_plate, max_plate_count)]
public class QueryReducerFactory implements ReducerFactory<String, String, PlateCountPair> {

    @Override
    public QueryReducer newReducer(String key) {
        return new QueryReducer();
    }

    private class QueryReducer extends Reducer<String, PlateCountPair> {
        private Map<String, Long> plateCountMap;

        @Override
        public void beginReduce() {
            plateCountMap = new HashMap<>();
        }

        @Override
        public void reduce(String plate) {
            plateCountMap.compute(plate, (k, v) -> v == null ? 1 : v + 1);
        }

        @Override
        public PlateCountPair finalizeReduce() {
            return plateCountMap.entrySet().stream()
                    .map(e -> new PlateCountPair(e.getKey(), e.getValue()))
                    .max(PlateCountPair::compareTo)
                    .orElseThrow(IllegalStateException::new);
        }
    }
}
