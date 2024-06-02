package ar.edu.itba.pod.query4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public final class PlateCountPair implements DataSerializable, Comparable<PlateCountPair> {
    private String plate;
    private Long count;

    public PlateCountPair() {
    }

    public PlateCountPair(String plate, Long count) {
        this.plate = plate;
        this.count = count;
    }

    public String plate() {
        return plate;
    }

    public Long count() {
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PlateCountPair) obj;
        return Objects.equals(this.plate, that.plate) && Objects.equals(this.count, that.count);
    }

    @Override
    public int compareTo(PlateCountPair that) {
        return this.count.compareTo(that.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plate, count);
    }

    @Override
    public String toString() {
        return "PlateCountPair[" + "plate=" + plate + ", " + "count=" + count + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plate);
        out.writeLong(count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plate = in.readUTF();
        count = in.readLong();
    }
}
