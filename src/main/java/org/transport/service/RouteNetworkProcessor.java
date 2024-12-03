package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.RouteNetwork;
import org.transport.repository.RouteNetworkRepository;

@Component
public final class RouteNetworkProcessor extends GtfsStaticDataProcessorBase<RouteNetwork, RouteNetworkRepository, RouteNetwork.RouteNetworkDTO> {

	public RouteNetworkProcessor(RouteNetworkRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected RouteNetwork.RouteNetworkDTO process(RouteNetwork.RouteNetworkDTO data, int sourceIndex) {
		return data;
	}
}
