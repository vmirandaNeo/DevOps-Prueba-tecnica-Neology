package mx.neology.parking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_stays")
public class Stay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_plate", nullable = false)
    private Vehicle vehicle;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "duration_minutes")
    private Long durationMinutes;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    protected Stay() {
    }

    public Stay(Vehicle vehicle, LocalDateTime entryTime) {
        this.vehicle = vehicle;
        this.entryTime = entryTime;
    }

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isOpen() {
        return exitTime == null;
    }

    public void close(LocalDateTime exitTime, long durationMinutes, BigDecimal amount) {
        if (!isOpen()) {
            throw new IllegalStateException("La estancia ya está cerrada");
        }
        this.exitTime = exitTime;
        this.durationMinutes = durationMinutes;
        this.amount = amount;
    }
}

