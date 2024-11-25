package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.entity.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
}
