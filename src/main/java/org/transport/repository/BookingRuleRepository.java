package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Area;
import org.transport.generated.BookingRule;

@Repository
public interface BookingRuleRepository extends JpaRepository<BookingRule, String> {
}
