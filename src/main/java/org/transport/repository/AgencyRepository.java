package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Agency;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, String> {
}
