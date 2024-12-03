package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Calendar;
import org.transport.repository.CalendarRepository;

@Component
public final class CalendarProcessor extends GtfsStaticDataProcessorBase<Calendar, CalendarRepository, Calendar.CalendarDTO> {

	public CalendarProcessor(CalendarRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Calendar.CalendarDTO process(Calendar.CalendarDTO data, int sourceIndex) {
		return data;
	}
}
