package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.StopTime;

@Repository
public interface StopTimeRepository extends JpaRepository<StopTime, String> {
}
