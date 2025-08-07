package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.CalendarDate;
import org.transport.repository.CalendarDateRepository;
import org.transport.type.DTOBase;

@Component
public final class CalendarDateProcessor extends GtfsStaticDataProcessorBase<CalendarDate, CalendarDateRepository, CalendarDate.CalendarDateDTO> {

	public CalendarDateProcessor(CalendarDateRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected CalendarDate.CalendarDateDTO process(CalendarDate.CalendarDateDTO data, int sourceIndex) {
		data.serviceId = DTOBase.convertId(sourceIndex, data.serviceId);
		return data;
	}
}
