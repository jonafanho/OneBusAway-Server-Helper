package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.CalendarDate;

import java.util.List;

@Repository
public interface CalendarDateRepository extends JpaRepository<CalendarDate, String> {

	List<CalendarDate> findAllByServiceId(String serviceId);
}
