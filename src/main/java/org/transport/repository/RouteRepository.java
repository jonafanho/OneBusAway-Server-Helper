package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
}
