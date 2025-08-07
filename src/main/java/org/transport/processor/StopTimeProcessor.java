package org.transport.processor;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.transport.generated.*;
import org.transport.generated.Calendar;
import org.transport.repository.CalendarDateRepository;
import org.transport.repository.CalendarRepository;
import org.transport.repository.StopTimeCacheRepository;
import org.transport.repository.StopTimeRepository;
import org.transport.tool.Constants;
import org.transport.type.Date;
import org.transport.type.ExceptionType;
import org.transport.type.NoYes;
import org.transport.type.Time;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
public final class StopTimeProcessor extends GtfsStaticDataProcessorBase<StopTime, StopTimeRepository, StopTime.StopTimeDTO> {

	private final StopTimeRepository stopTimeRepository;
	private final CalendarRepository calendarRepository;
	private final CalendarDateRepository calendarDateRepository;
	private final StopTimeCacheRepository stopTimeCacheRepository;

	private static final int CACHE_DAYS = 2; // 2 days
	private static final int CACHE_MILLIS = CACHE_DAYS * Constants.HOURS_PER_DAY * Constants.MINUTES_PER_HOUR * Constants.SECONDS_PER_MINUTE * Constants.MILLIS_PER_SECOND;

	public StopTimeProcessor(StopTimeRepository stopTimeRepository, CalendarRepository calendarRepository, CalendarDateRepository calendarDateRepository, StopTimeCacheRepository stopTimeCacheRepository) {
		super(stopTimeRepository);
		this.stopTimeRepository = stopTimeRepository;
		this.calendarRepository = calendarRepository;
		this.calendarDateRepository = calendarDateRepository;
		this.stopTimeCacheRepository = stopTimeCacheRepository;
	}

	@Nonnull
	@Override
	protected StopTime.StopTimeDTO process(StopTime.StopTimeDTO data, int sourceIndex) {
		return data;
	}

	@Override
	protected void saveToRepository(List<StopTime> dataList) {
		super.saveToRepository(dataList);
		log.info("Creating stop time cache");
		final List<StopTime> stopTimes = stopTimeRepository.findAll();

		final long startMillis = System.currentTimeMillis();
		final long endMillis = startMillis + CACHE_MILLIS;
		final LocalDate nextLocalDateUtc = Instant.ofEpochMilli(startMillis).atZone(ZoneOffset.UTC).toLocalDate().minusDays(1);
		final List<StopTimeCache> stopTimeCacheList = new ArrayList<>();
		final Map<String, Calendar> calendarCache = new HashMap<>();
		final Map<String, List<CalendarDate>> calendarDatesCache = new HashMap<>();
		final Map<String, List<Integer>> stopSequenceListCache = new HashMap<>();

		stopTimes.forEach(stopTime -> stopSequenceListCache.computeIfAbsent(stopTime.getTripId().getTripId(), key -> new ArrayList<>()).add(stopTime.getStopSequence()));
		stopSequenceListCache.values().forEach(stopSequenceList -> stopSequenceList.sort(Comparator.comparingInt(stopSequence -> stopSequence)));

		stopTimes.forEach(stopTime -> {
			final Stop stop = stopTime.getStopId();
			final Time arrivalTime = stopTime.getArrivalTime();
			final Time departureTime = stopTime.getDepartureTime();
			if (stop == null || arrivalTime == null || departureTime == null) {
				return;
			}

			final Trip trip = stopTime.getTripId();
			final Route route = trip.getRouteId();
			final String serviceId = trip.getServiceId();
			final Calendar calendar = calendarCache.computeIfAbsent(serviceId, key -> calendarRepository.findById(serviceId).orElse(null));
			final List<CalendarDate> calendarDates = calendarDatesCache.computeIfAbsent(serviceId, key -> calendarDateRepository.findAllByServiceId(serviceId));
			final ZoneId zoneId = stop.getStopTimezone().toZoneId();

			for (int i = 0; i <= CACHE_DAYS; i++) {
				final ZonedDateTime localZonedDateTime = nextLocalDateUtc.plusDays(i).atStartOfDay().atZone(zoneId);

				if (matchesCalendar(calendar, calendarDates, localZonedDateTime)) {
					final long arrivalMillis = localZonedDateTime.plus(arrivalTime.millisAfterMidnight(), ChronoUnit.MILLIS).toInstant().toEpochMilli();
					final long departureMillis = localZonedDateTime.plus(departureTime.millisAfterMidnight(), ChronoUnit.MILLIS).toInstant().toEpochMilli();

					if (isBetween(arrivalMillis, startMillis, endMillis) || isBetween(departureMillis, startMillis, endMillis)) {
						final List<Integer> stopSequences = stopSequenceListCache.computeIfAbsent(trip.getTripId(), key -> stopTimes.stream()
								.filter(tripStopTime -> tripStopTime.getTripId().getTripId().equals(trip.getTripId()))
								.map(StopTime::getStopSequence)
								.sorted()
								.toList()
						);

						final StopTimeCache.StopTimeCacheDTO stopTimeCacheDTO = new StopTimeCache.StopTimeCacheDTO();
						stopTimeCacheDTO.stopId = stop.getStopId();
						stopTimeCacheDTO.tripId = trip.getTripId();
						stopTimeCacheDTO.blockId = trip.getBlockId();
						stopTimeCacheDTO.routeId = route.getRouteId();
						stopTimeCacheDTO.arrivalTime = arrivalMillis;
						stopTimeCacheDTO.departureTime = departureMillis;
						stopTimeCacheDTO.stopIndexInRoute = stopSequences.indexOf(stopTime.getStopSequence());
						stopTimeCacheDTO.totalStopsInRoute = stopSequences.size();
						stopTimeCacheDTO.isTerminating = stopTimeCacheDTO.totalStopsInRoute - 1 == stopTimeCacheDTO.stopIndexInRoute;
						stopTimeCacheDTO.routeShortName = route.getRouteShortName();
						stopTimeCacheDTO.routeLongName = route.getRouteLongName();
						stopTimeCacheDTO.stopHeadsign = stopTime.getStopHeadsign();
						stopTimeCacheDTO.tripHeadsign = trip.getTripHeadsign();
						stopTimeCacheDTO.isTimepoint = stopTime.getTimepoint() == NoYes.YES;
						stopTimeCacheList.add(stopTimeCacheDTO.convert(0));
					}
				}
			}
		});

		stopTimeCacheRepository.saveAll(stopTimeCacheList);
	}

	private static boolean matchesCalendarDates(List<CalendarDate> calendarDates, boolean hasCalendar, ZonedDateTime zonedDateTime) {
		for (final CalendarDate calendarDate : calendarDates) {
			if (isBetweenDate(calendarDate.getDate(), calendarDate.getDate(), zonedDateTime)) {
				return calendarDate.getExceptionType() == ExceptionType.ADDED;
			}
		}
		return hasCalendar;
	}

	private static boolean matchesCalendar(@Nullable Calendar calendar, List<CalendarDate> calendarDates, ZonedDateTime zonedDateTime) {
		if (calendar == null) {
			return matchesCalendarDates(calendarDates, false, zonedDateTime);
		}

		return isBetweenDate(calendar.getStartDate(), calendar.getEndDate(), zonedDateTime) && matchesCalendarDates(calendarDates, true, zonedDateTime) && switch (zonedDateTime.getDayOfWeek()) {
			case MONDAY -> calendar.getMonday() == NoYes.YES;
			case TUESDAY -> calendar.getTuesday() == NoYes.YES;
			case WEDNESDAY -> calendar.getWednesday() == NoYes.YES;
			case THURSDAY -> calendar.getThursday() == NoYes.YES;
			case FRIDAY -> calendar.getFriday() == NoYes.YES;
			case SATURDAY -> calendar.getSaturday() == NoYes.YES;
			case SUNDAY -> calendar.getSunday() == NoYes.YES;
		};
	}

	private static boolean isBetweenDate(Date startDate, Date endDate, ZonedDateTime zonedDateTime) {
		final LocalDate localDate = zonedDateTime.toLocalDate();
		return !localDate.isBefore(startDate.localDate()) && !localDate.isAfter(endDate.localDate());
	}

	private static boolean isBetween(long value, long min, long max) {
		return value >= min && value <= max;
	}
}
