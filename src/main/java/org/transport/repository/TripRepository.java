package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Trip;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {
}
