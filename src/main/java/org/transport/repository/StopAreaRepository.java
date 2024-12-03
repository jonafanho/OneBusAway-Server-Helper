package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.StopArea;

@Repository
public interface StopAreaRepository extends JpaRepository<StopArea, String> {
}
