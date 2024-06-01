package ar.edu.itba.pod.query1;

import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class QueryCollator
        implements Collator<Map.Entry<String, Long>, List<Map.Entry<String, Long>>> {
    @Override
    public List<Map.Entry<String, Long>> collate(Iterable<Map.Entry<String, Long>> values) {
        // Sort by amount of infractions descending
        // Then by description descending
        final Comparator<Map.Entry<String, Long>> comparator =
                Comparator.comparing(Map.Entry<String, Long>::getValue)
                        .thenComparing(Map.Entry::getKey)
                        .reversed();

        return StreamSupport.stream(values.spliterator(), false).sorted(comparator).toList();
    }
}
