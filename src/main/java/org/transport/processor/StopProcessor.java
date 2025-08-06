package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Stop;
import org.transport.repository.StopRepository;

import java.util.Comparator;
import java.util.List;

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

	@Override
	protected void saveToRepository(List<Stop> dataList) {
		dataList.sort(Comparator.comparing(StopProcessor::getParentStopId));
		super.saveToRepository(dataList);
	}

	private static String getParentStopId(Stop stop) {
		return stop.getParentStation() == null ? "" : stop.getParentStation().getStopId();
	}
}
