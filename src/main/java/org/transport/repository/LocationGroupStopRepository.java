package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.LocationGroup;
import org.transport.generated.LocationGroupStop;

@Repository
public interface LocationGroupStopRepository extends JpaRepository<LocationGroupStop, String> {
}
