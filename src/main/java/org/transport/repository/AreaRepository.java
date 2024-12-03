package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Agency;
import org.transport.generated.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, String> {
}
