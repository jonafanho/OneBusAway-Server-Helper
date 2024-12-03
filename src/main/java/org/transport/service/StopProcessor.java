package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Stop;
import org.transport.repository.StopRepository;

@Component
public final class StopProcessor extends GtfsStaticDataProcessorBase<Stop, StopRepository, Stop.StopDTO> {

	private final AgencyProcessor agencyProcessor;

	public StopProcessor(StopRepository repository, AgencyProcessor agencyProcessor) {
		super(repository);
		this.agencyProcessor = agencyProcessor;
	}

	@Nonnull
	@Override
	protected Stop.StopDTO process(Stop.StopDTO data, int sourceIndex) {
		if (data.stopTimezone == null) {
			data.stopTimezone = agencyProcessor.getTimeZone(sourceIndex);
		}
		return data;
	}
}
