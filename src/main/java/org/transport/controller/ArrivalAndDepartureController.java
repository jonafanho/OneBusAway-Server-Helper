package org.transport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.transport.generated.Calendar;
import org.transport.repository.StopTimeRepository;
import org.transport.response.DataResponse;
import org.transport.type.NoYes;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api")
public final class ArrivalAndDepartureController {

	private final StopTimeRepository stopTimeRepository;

	public ArrivalAndDepartureController(StopTimeRepository stopTimeRepository) {
		this.stopTimeRepository = stopTimeRepository;
	}

	@GetMapping("/arrivals-and-departures-for-stop")
	public DataResponse arrivalsAndDeparturesForStop(@RequestParam String stopId) {
		final long startMillis = System.currentTimeMillis();
		final long endMillis = startMillis + 120 * 60 * 1000;
		return DataResponse.createError(new Exception("TODO"));
	}

	private static boolean matchesCalendar(Calendar calendar, ZonedDateTime zonedDateTime) {
		return switch (zonedDateTime.getDayOfWeek()) {
			case MONDAY -> calendar.getMonday() == NoYes.YES;
			case TUESDAY -> calendar.getTuesday() == NoYes.YES;
			case WEDNESDAY -> calendar.getWednesday() == NoYes.YES;
			case THURSDAY -> calendar.getThursday() == NoYes.YES;
			case FRIDAY -> calendar.getFriday() == NoYes.YES;
			case SATURDAY -> calendar.getSaturday() == NoYes.YES;
			case SUNDAY -> calendar.getSunday() == NoYes.YES;
		};
	}
}
