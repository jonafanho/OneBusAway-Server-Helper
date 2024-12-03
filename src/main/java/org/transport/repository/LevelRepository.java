package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.BookingRule;
import org.transport.generated.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, String> {
}
