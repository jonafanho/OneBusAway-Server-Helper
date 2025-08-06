package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Shape;
import org.transport.repository.ShapeRepository;

@Component
public final class ShapeProcessor extends GtfsStaticDataProcessorBase<Shape, ShapeRepository, Shape.ShapeDTO> {

	public ShapeProcessor(ShapeRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Shape.ShapeDTO process(Shape.ShapeDTO data, int sourceIndex) {
		return data;
	}
}
