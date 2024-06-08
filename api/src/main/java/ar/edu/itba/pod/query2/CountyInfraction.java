package ar.edu.itba.pod.query2;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;


import java.io.IOException;
import java.util.Objects;

public class CountyInfraction implements DataSerializable {
    private String county;
    private String infraction;

    public CountyInfraction(String county, String infraction) {
        this.county = county;
        this.infraction = infraction;
    }

    public String county() {
        return county;
    }

    public String infraction() {
        return infraction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CountyInfraction) obj;
        return Objects.equals(this.county, that.county)
                && Objects.equals(this.infraction, that.infraction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(county, infraction);
    }

    @Override
    public String toString() {
        return "CountyPlateDateTuple["
                + "county="
                + county
                + ", "
                + "infraction="
                + infraction
                + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(county);
        out.writeUTF(infraction);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        county = in.readUTF();
        infraction = in.readUTF();
    }
}
