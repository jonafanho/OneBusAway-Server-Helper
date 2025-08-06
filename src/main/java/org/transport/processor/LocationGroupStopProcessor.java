package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.LocationGroupStop;
import org.transport.repository.LocationGroupStopRepository;

@Component
public final class LocationGroupStopProcessor extends GtfsStaticDataProcessorBase<LocationGroupStop, LocationGroupStopRepository, LocationGroupStop.LocationGroupStopDTO> {

	public LocationGroupStopProcessor(LocationGroupStopRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected LocationGroupStop.LocationGroupStopDTO process(LocationGroupStop.LocationGroupStopDTO data, int sourceIndex) {
		return data;
	}
}
