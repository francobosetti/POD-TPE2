package ar.edu.itba.pod.models;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

public final class Ticket implements DataSerializable {
    private String plateNumber;
    private LocalDate date;
    private Infraction infraction;
    private double fine;
    private String agency;
    private String area;

    public Ticket() {}

    public Ticket(
            String plateNumber,
            LocalDate date,
            Infraction infraction,
            double fine,
            String agency,
            String area) {
        this.plateNumber = plateNumber;
        this.date = date;
        this.infraction = infraction;
        this.fine = fine;
        this.agency = agency;
        this.area = area;
    }

    public String plateNumber() {
        return plateNumber;
    }

    public LocalDate date() {
        return date;
    }

    public Infraction infraction() {
        return infraction;
    }

    public double fine() {
        return fine;
    }

    public String agency() {
        return agency;
    }

    public String area() {
        return area;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Ticket) obj;
        return Objects.equals(this.plateNumber, that.plateNumber)
                && Objects.equals(this.date, that.date)
                && Objects.equals(this.infraction, that.infraction)
                && Double.doubleToLongBits(this.fine) == Double.doubleToLongBits(that.fine)
                && Objects.equals(this.agency, that.agency)
                && Objects.equals(this.area, that.area);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plateNumber, date, infraction, fine, agency, area);
    }

    @Override
    public String toString() {
        return "Ticket["
                + "plateNumber="
                + plateNumber
                + ", "
                + "date="
                + date
                + ", "
                + "infraction="
                + infraction
                + ", "
                + "fine="
                + fine
                + ", "
                + "agency="
                + agency
                + ", "
                + "area="
                + area
                + ']';
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(plateNumber);
        out.writeLong(date.toEpochDay());
        out.writeObject(infraction);
        out.writeDouble(fine);
        out.writeUTF(agency);
        out.writeUTF(area);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        plateNumber = in.readUTF();
        date = LocalDate.ofEpochDay(in.readLong());
        infraction = in.readObject();
        fine = in.readDouble();
        agency = in.readUTF();
        area = in.readUTF();
    }
}
