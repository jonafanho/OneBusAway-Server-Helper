package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Calendar;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, String> {
}
