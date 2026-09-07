package mx.neology.parking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @Column(name = "plate", nullable = false, length = 12)
    private String plate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType type;

    @Column(name = "accumulated_minutes", nullable = false)
    private long accumulatedMinutes;

    @Version
    private long version;

    protected Vehicle() {
    }

    public Vehicle(String plate, VehicleType type) {
        this.plate = plate;
        this.type = type;
        this.accumulatedMinutes = 0;
    }

    public String getPlate() {
        return plate;
    }

    public VehicleType getType() {
        return type;
    }

    public long getAccumulatedMinutes() {
        return accumulatedMinutes;
    }

    public void addAccumulatedMinutes(long minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Los minutos no pueden ser negativos");
        }
        this.accumulatedMinutes += minutes;
    }

    public void resetAccumulatedMinutes() {
        this.accumulatedMinutes = 0;
    }
}

