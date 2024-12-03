package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Network;
import org.transport.repository.NetworkRepository;

@Component
public final class NetworkProcessor extends GtfsStaticDataProcessorBase<Network, NetworkRepository, Network.NetworkDTO> {

	public NetworkProcessor(NetworkRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Network.NetworkDTO process(Network.NetworkDTO data, int sourceIndex) {
		return data;
	}
}
