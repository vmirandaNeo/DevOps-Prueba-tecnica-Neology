package mx.neology.parking.repository;

import java.util.List;
import mx.neology.parking.domain.Vehicle;
import mx.neology.parking.domain.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findAllByOrderByPlateAsc();

    List<Vehicle> findAllByTypeOrderByPlateAsc(VehicleType type);
}

