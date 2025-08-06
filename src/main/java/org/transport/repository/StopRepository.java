package org.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Stop;

@Repository
public interface StopRepository extends JpaRepository<Stop, String> {

	Page<Stop> findByStopLatBetweenAndStopLonBetween(double stopLat1, double stopLat2, double stopLon1, double stopLon2, Pageable pageable);
}
