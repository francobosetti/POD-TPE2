package ar.edu.itba.pod.query5;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.Objects;

public final class InfractionPair implements DataSerializable, Comparable<InfractionPair> {
    private String in1;
    private String in2;

    public InfractionPair() {}

    public InfractionPair(String in1, String in2) {
        this.in1 = in1;
        this.in2 = in2;
    }

    public String in1() {
        return in1;
    }

    public String in2() {
        return in2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InfractionPair) obj;
        return Objects.equals(this.in1, that.in1) && Objects.equals(this.in2, that.in2);
    }

    @Override
    public int compareTo(InfractionPair that) {
        int result = this.in1.compareTo(that.in1);
        if (result == 0) {
            result = this.in2.compareTo(that.in2);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(in1, in2);
    }

    @Override
    public String toString() {
        return "Pair[in1=" + in1 + ", in2=" + in2 + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(in1);
        out.writeUTF(in2);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        in1 = in.readUTF();
        in2 = in.readUTF();
    }
}
