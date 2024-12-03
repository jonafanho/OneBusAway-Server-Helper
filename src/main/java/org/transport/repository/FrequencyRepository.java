package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Frequency;

@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, String> {
}
