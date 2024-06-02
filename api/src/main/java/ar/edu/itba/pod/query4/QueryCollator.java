package ar.edu.itba.pod.query4;

import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

// Ordena alfabeticamente por county, ASC
public class QueryCollator
        implements Collator<
                Map.Entry<String, PlateCountPair>, List<Map.Entry<String, PlateCountPair>>> {

    @Override
    public List<Map.Entry<String, PlateCountPair>> collate(
            Iterable<Map.Entry<String, PlateCountPair>> values) {

        final Comparator<Map.Entry<String, PlateCountPair>> comparator =
                Comparator.comparing(Map.Entry<String, PlateCountPair>::getKey);

        return StreamSupport.stream(values.spliterator(), false).sorted(comparator).toList();
    }
}
