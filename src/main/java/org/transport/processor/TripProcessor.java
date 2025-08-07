package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Trip;
import org.transport.repository.TripRepository;
import org.transport.type.DTOBase;

@Component
public final class TripProcessor extends GtfsStaticDataProcessorBase<Trip, TripRepository, Trip.TripDTO> {

	public TripProcessor(TripRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Trip.TripDTO process(Trip.TripDTO data, int sourceIndex) {
		data.serviceId = DTOBase.convertId(sourceIndex, data.serviceId);
		return data;
	}
}
