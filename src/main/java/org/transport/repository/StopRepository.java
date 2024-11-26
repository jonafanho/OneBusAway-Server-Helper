package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.entity.Stop;

@Repository
public interface StopRepository extends JpaRepository<Stop, String> {
}
