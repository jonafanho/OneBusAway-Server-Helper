package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.StopTimeCache;

import java.util.List;

@Repository
public interface StopTimeCacheRepository extends JpaRepository<StopTimeCache, String> {

	List<StopTimeCache> findAllByStopIdAndDepartureTimeAfterAndArrivalTimeBefore(String stopId, long startTime, long endTime);

	List<StopTimeCache> findAllByStopIdAndDepartureTimeAfterAndArrivalTimeBeforeAndIsTerminating(String stopId, long startTime, long endTime, boolean isTerminating);
}
