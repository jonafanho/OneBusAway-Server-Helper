package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.LocationGroupStop;
import org.transport.generated.Network;

@Repository
public interface NetworkRepository extends JpaRepository<Network, String> {
}
