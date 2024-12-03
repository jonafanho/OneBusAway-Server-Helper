package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.Network;
import org.transport.generated.RouteNetwork;

@Repository
public interface RouteNetworkRepository extends JpaRepository<RouteNetwork, String> {
}
