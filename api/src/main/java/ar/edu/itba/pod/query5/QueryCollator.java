package ar.edu.itba.pod.query5;

import com.hazelcast.mapreduce.Collator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class QueryCollator
        implements Collator<
                Map.Entry<Integer, List<InfractionPair>>,
                List<Map.Entry<Integer, InfractionPair>>> {

    @Override
    public List<Map.Entry<Integer, InfractionPair>> collate(
            Iterable<Map.Entry<Integer, List<InfractionPair>>> values) {
        List<Map.Entry<Integer, InfractionPair>> result = new ArrayList<>();
        values.forEach(
                entry ->
                        entry.getValue()
                                .forEach(pair -> result.add(Map.entry(entry.getKey(), pair))));

        final Comparator<Map.Entry<Integer, InfractionPair>> comparator =
                Map.Entry.<Integer, InfractionPair>comparingByKey()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByValue());
        return result.stream().sorted(comparator).toList();
    }
}
