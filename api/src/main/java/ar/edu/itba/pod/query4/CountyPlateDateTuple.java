package ar.edu.itba.pod.query4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public final class CountyPlateDateTuple implements DataSerializable {
    private String county;
    private String plate;
    private LocalDate date;

    public CountyPlateDateTuple() {
    }

    public CountyPlateDateTuple(String county, String plate, LocalDate date) {
        this.county = county;
        this.plate = plate;
        this.date = date;
    }

    public String county() {
        return county;
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
        var that = (CountyPlateDateTuple) obj;
        return Objects.equals(this.county, that.county)
                && Objects.equals(this.plate, that.plate)
                && Objects.equals(this.date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(county, plate, date);
    }

    @Override
    public String toString() {
        return "CountyPlateDateTuple["
                + "county="
                + county
                + ", "
                + "plate="
                + plate
                + ", "
                + "date="
                + date
                + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(county);
        out.writeUTF(plate);
        out.writeLong(date.toEpochDay());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        county = in.readUTF();
        plate = in.readUTF();
        date = LocalDate.ofEpochDay(in.readLong());
    }
}
