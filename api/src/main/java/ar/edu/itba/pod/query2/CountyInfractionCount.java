package ar.edu.itba.pod.query2;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public class CountyInfractionCount implements DataSerializable, Comparable<CountyInfractionCount> {
    private String infraction;
    private Long count;

    public CountyInfractionCount() {}

    public CountyInfractionCount(String infraction, Long count) {
        this.infraction = infraction;
        this.count = count;
    }

    public String infraction() {
        return infraction;
    }

    public Long count() {
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CountyInfractionCount) obj;
        return Objects.equals(this.infraction, that.infraction)
                && Objects.equals(this.count, that.count);
    }

    @Override
    public int compareTo(CountyInfractionCount that) {
        return this.count.compareTo(that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(infraction, count);
    }

    @Override
    public String toString() {
        return "PlateCountPair[" + "infraction=" + infraction + ", " + "count=" + count + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(infraction);
        out.writeLong(count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        infraction = in.readUTF();
        count = in.readLong();
    }
}
