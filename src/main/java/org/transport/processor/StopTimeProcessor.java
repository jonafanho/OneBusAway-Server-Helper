package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.StopTime;
import org.transport.repository.StopTimeRepository;

@Component
public final class StopTimeProcessor extends GtfsStaticDataProcessorBase<StopTime, StopTimeRepository, StopTime.StopTimeDTO> {

	public StopTimeProcessor(StopTimeRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected StopTime.StopTimeDTO process(StopTime.StopTimeDTO data, int sourceIndex) {
		return data;
	}
}
