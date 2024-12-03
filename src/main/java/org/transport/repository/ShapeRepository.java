package org.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.transport.generated.RouteNetwork;
import org.transport.generated.Shape;

@Repository
public interface ShapeRepository extends JpaRepository<Shape, String> {
}
