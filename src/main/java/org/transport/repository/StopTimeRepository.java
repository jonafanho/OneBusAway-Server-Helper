package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.StopTime;

import java.util.List;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, String> {

	List<StopTime> findAllByStopId_StopId(String stopId);
}
