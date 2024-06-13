package ar.edu.itba.pod.query4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public final class PlateDatePair implements DataSerializable {
    private String plate;
    private LocalDate date;

    public PlateDatePair() {}

    public PlateDatePair(String plate, LocalDate date) {
        this.plate = plate;
        this.date = date;
    }

    public String plate() {
        return plate;
    }

    public LocalDate date() {
        return date;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PlateDatePair) obj;
        return Objects.equals(this.plate, that.plate) && Objects.equals(this.date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plate, date);
    }

    @Override
    public String toString() {
        return "CountyPlateDateTuple[" + "plate=" + plate + ", " + "date=" + date + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plate);
        out.writeLong(date.toEpochDay());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plate = in.readUTF();
        date = LocalDate.ofEpochDay(in.readLong());
    }
}
