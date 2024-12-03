package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.StopArea;
import org.transport.repository.StopAreaRepository;

@Component
public final class StopAreaProcessor extends GtfsStaticDataProcessorBase<StopArea, StopAreaRepository, StopArea.StopAreaDTO> {

	public StopAreaProcessor(StopAreaRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected StopArea.StopAreaDTO process(StopArea.StopAreaDTO data, int sourceIndex) {
		return data;
	}
}
