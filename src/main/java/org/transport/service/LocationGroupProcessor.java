package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.LocationGroup;
import org.transport.repository.LocationGroupRepository;

@Component
public final class LocationGroupProcessor extends GtfsStaticDataProcessorBase<LocationGroup, LocationGroupRepository, LocationGroup.LocationGroupDTO> {

	public LocationGroupProcessor(LocationGroupRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected LocationGroup.LocationGroupDTO process(LocationGroup.LocationGroupDTO data, int sourceIndex) {
		return data;
	}
}
