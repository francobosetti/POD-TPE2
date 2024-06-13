package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.ArrayList;
import java.util.List;

public class QueryReducerFactoryGroupPair
        implements ReducerFactory<Integer, String, List<InfractionPair>> {
    @Override
    public Reducer<String, List<InfractionPair>> newReducer(Integer group) {
        return new QueryReducerFactoryGroupPair.QueryReducer();
    }

    private class QueryReducer extends Reducer<String, List<InfractionPair>> {
        private List<String> inf;

        @Override
        public void beginReduce() {
            inf = new ArrayList<>();
        }

        @Override
        public void reduce(String s) {
            inf.add(s);
        }

        @Override
        public List<InfractionPair> finalizeReduce() {
            List<InfractionPair> result = new ArrayList<>();
            for (int i = 0; i < inf.size(); i++) {
                for (int j = i + 1; j < inf.size(); j++) {
                    String first = inf.get(i);
                    String second = inf.get(j);
                    InfractionPair pair =
                            (first.compareTo(second) < 0)
                                    ? new InfractionPair(first, second)
                                    : new InfractionPair(second, first);
                    result.add(pair);
                }
            }
            return result;
        }
    }
}
