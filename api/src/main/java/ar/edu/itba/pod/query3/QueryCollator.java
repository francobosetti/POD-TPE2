package ar.edu.itba.pod.query3;

import com.hazelcast.mapreduce.Collator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class QueryCollator implements Collator<Map.Entry<String, Double>, List<Map.Entry<String, Double>>> {
    private final long N;

    public QueryCollator(long N) {
        this.N = N;
    }

    @Override
    public List<Map.Entry<String, Double>> collate(Iterable<Map.Entry<String, Double>> values) {
        double totalRevenue = getTotalRevenue(values);

        final List<Map.Entry<String, Double>> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : values) {
            double percentage = entry.getValue() / totalRevenue * 100;
            entries.add(Map.entry(entry.getKey(), percentage));
        }

        final Comparator<Map.Entry<String, Double>> comparator = Map.Entry.<String, Double>comparingByValue().reversed();

        return entries.stream().sorted(comparator).limit(N).toList();
    }

    private double getTotalRevenue(Iterable<Map.Entry<String, Double>> values) {
        double revenue = 0;
        for (Map.Entry<String, Double> entry : values) {
            revenue += entry.getValue();
        }
        return revenue;
    }
}
