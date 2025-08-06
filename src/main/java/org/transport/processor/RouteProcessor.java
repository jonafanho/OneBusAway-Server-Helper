package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Route;
import org.transport.repository.RouteRepository;

@Component
public final class RouteProcessor extends GtfsStaticDataProcessorBase<Route, RouteRepository, Route.RouteDTO> {

	public RouteProcessor(RouteRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Route.RouteDTO process(Route.RouteDTO data, int sourceIndex) {
		return data;
	}
}
