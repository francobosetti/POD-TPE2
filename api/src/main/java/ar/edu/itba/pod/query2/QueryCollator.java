package ar.edu.itba.pod.query2;

import com.hazelcast.mapreduce.Collator;

import java.util.*;

//Ordena Alfabeticamente por County e imprime Top 3 en ese County
public class QueryCollator implements Collator<Map.Entry<CountyInfraction, Long>, List<Map.Entry<String, List<String>>>> {
    @Override
    public List<Map.Entry<String, List<String>>> collate(Iterable<Map.Entry<CountyInfraction, Long>> values) {


        //Primero creo un mapa de los county: [county, List<CountyInfractionCount>]
        Map<String, List<CountyInfractionCount>> countyMap = new HashMap<>();
        for (Map.Entry<CountyInfraction, Long> entry : values) {
            String county = entry.getKey().county();
            countyMap.computeIfAbsent(county, k -> new ArrayList<>()).add(
                    new CountyInfractionCount(entry.getKey().infraction(), entry.getValue()));
        }


        List<Map.Entry<String, List<String>>> result = new ArrayList<>();
        //Ahora ordeno esa lista de cada entry para que primeros me quede el top 3
        for (Map.Entry<String, List<CountyInfractionCount>> entry: countyMap.entrySet()){
            String key= entry.getKey();
            List<String> value= entry.getValue().stream()
                    .sorted(Comparator.comparingLong(CountyInfractionCount::count).reversed())
                    .limit(3).map(CountyInfractionCount::infraction).toList();

            result.add(Map.entry(key, value));
        }


        final Comparator<Map.Entry<String, List<String>>> comparator =
                Comparator.comparing(Map.Entry<String, List<String>>::getKey);

        result.sort(comparator);

        return result;
    }
}
