package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Level;
import org.transport.generated.LocationGroup;

@Repository
public interface LocationGroupRepository extends JpaRepository<LocationGroup, String> {
}
