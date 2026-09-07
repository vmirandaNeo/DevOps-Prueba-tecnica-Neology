package mx.neology.parking.repository;

import java.util.List;
import java.util.Optional;
import mx.neology.parking.domain.Stay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StayRepository extends JpaRepository<Stay, Long> {

    Optional<Stay> findFirstByVehiclePlateAndExitTimeIsNullOrderByEntryTimeDesc(String plate);

    boolean existsByVehiclePlateAndExitTimeIsNull(String plate);

    List<Stay> findAllByExitTimeIsNull();

    List<Stay> findByVehiclePlateOrderByEntryTimeDesc(String plate);
}

