package ar.edu.itba.pod.query1;

import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

// Ordena los resultados de la query por cantidad de infracciones y luego por descripción DESC
public class QueryCollator
        implements Collator<Map.Entry<String, Long>, List<Map.Entry<String, Long>>> {
    @Override
    public List<Map.Entry<String, Long>> collate(Iterable<Map.Entry<String, Long>> values) {
        // Sort by amount of infractions descending
        // Then by description ascending
        final Comparator<Map.Entry<String, Long>> comparator =
                Map.Entry.<String, Long>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey());

        return StreamSupport.stream(values.spliterator(), false).sorted(comparator).toList();
    }
}
