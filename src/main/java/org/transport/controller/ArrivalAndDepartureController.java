package org.transport.controller;

import jakarta.annotation.Nullable;
import org.onebusaway.gtfs.model.*;
import org.springframework.web.bind.annotation.*;
import org.transport.entity.GtfsData;
import org.transport.response.ArrivalAndDeparture;
import org.transport.response.DataResponse;
import org.transport.response.ListResult;
import org.transport.response.RealtimeResponse;
import org.transport.service.GtfsService;
import org.transport.tool.Constants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public final class ArrivalAndDepartureController {

	private final GtfsService gtfsService;

	public ArrivalAndDepartureController(GtfsService gtfsService) {
		this.gtfsService = gtfsService;
	}

	@GetMapping("/arrivals-and-departures-for-stop")
	public Mono<DataResponse> arrivalsAndDeparturesForStop(
			@RequestParam String stopId,
			@RequestParam(defaultValue = "0") long time,
			@RequestParam(defaultValue = "120") long minutesAfter,
			@RequestParam(defaultValue = "false") boolean showTerminating
	) {
		final long requestStartMillis = System.currentTimeMillis();
		final long queryStartMillis = time <= 0 ? requestStartMillis : time;
		final long queryEndMillis = queryStartMillis + minutesAfter * Constants.SECONDS_PER_MINUTE * Constants.MILLIS_PER_SECOND;

		final Map<String, List<Integer>> stopSequenceListCache = new HashMap<>();
		final List<Mono<ArrivalAndDeparture>> arrivalAndDepartureMonoList = new ArrayList<>();

		iterateByStopId(stopId, queryStartMillis, queryEndMillis, (gtfsData, stop, offsetMillis, validServiceIds) -> gtfsData.gtfsDao.getStopTimesForStop(stop).forEach(stopTime -> {
			if (stopTime.isArrivalTimeSet() && stopTime.isDepartureTimeSet() && validServiceIds.contains(stopTime.getTrip().getServiceId())) {
				final Trip trip = stopTime.getTrip();
				final List<Integer> stopSequences = stopSequenceListCache.computeIfAbsent(trip.getId().toString(), key -> gtfsData.gtfsDao.getStopTimesForTrip(trip).stream().map(StopTime::getStopSequence).toList());
				final boolean isTerminating = stopSequences.indexOf(stopTime.getStopSequence()) == stopSequences.size() - 1;

				if (!isTerminating || showTerminating) {
					final Route route = trip.getRoute();

					arrivalAndDepartureMonoList.add(gtfsData.realtimeData.fetch(stopTime).mapNotNull(optionalRealtimeResponse -> {
						final RealtimeResponse realtimeResponse = optionalRealtimeResponse.orElse(null);
						final int deviation = realtimeResponse == null ? 0 : realtimeResponse.deviation() * Constants.MILLIS_PER_SECOND;
						final long arrivalMillis = (long) stopTime.getArrivalTime() * Constants.MILLIS_PER_SECOND + offsetMillis + deviation;
						final long scheduledDepartureMillis = (long) stopTime.getDepartureTime() * Constants.MILLIS_PER_SECOND + offsetMillis;
						final long departureMillis = Math.min(Math.max(arrivalMillis, scheduledDepartureMillis), scheduledDepartureMillis + deviation);

						if (isBetween(arrivalMillis, queryStartMillis, queryEndMillis) || isBetween(departureMillis, queryStartMillis, queryEndMillis)) {
							return new ArrivalAndDeparture(
									arrivalMillis,
									departureMillis,
									isTerminating,
									route.getShortName(),
									cleanName(route.getLongName(), route.getShortName()),
									cleanName(stopTime.getStopHeadsign(), route.getShortName()),
									cleanName(trip.getTripHeadsign(), route.getShortName()),
									stopTime.isTimepointSet() && stopTime.getTimepoint() > 0,
									realtimeResponse == null ? null : realtimeResponse.vehicleId(),
									realtimeResponse == null ? null : realtimeResponse.year(),
									realtimeResponse == null ? null : realtimeResponse.make(),
									realtimeResponse == null ? null : realtimeResponse.model(),
									realtimeResponse == null ? null : realtimeResponse.fuel(),
									realtimeResponse == null ? null : deviation,
									realtimeResponse == null ? null : realtimeResponse.isAnomaly(),
									realtimeResponse == null ? null : Math.round(realtimeResponse.length() * Constants.METERS_PER_FOOT)
							);
						} else {
							return null;
						}
					}));
				}
			}
		}));

		return Flux.fromIterable(arrivalAndDepartureMonoList).flatMap(mono -> mono).collectList().map(arrivalAndDepartureList -> {
			final List<ArrivalAndDeparture> sortedArrivalAndDepartureList = new ArrayList<>(arrivalAndDepartureList);
			sortedArrivalAndDepartureList.sort(Comparator.comparingLong(ArrivalAndDeparture::departureTime));
			return ListResult.fromList(queryStartMillis, sortedArrivalAndDepartureList);
		});
	}

	private void iterateByStopId(String stopId, long queryStartMillis, long queryEndMillis, IterateCallback callback) {
		gtfsService.gtfsDataList.forEach(gtfsData -> {
			final Stop stop = gtfsData.gtfsDao.getStopForId(AgencyAndId.convertFromString(stopId));

			if (stop != null) {
				final Agency agency = gtfsData.gtfsDao.getAgencyForId(stop.getId().getAgencyId());

				if (agency != null) {
					final ZoneId zoneId = ZoneId.of(agency.getTimezone());
					final LocalDate startDate = Instant.ofEpochMilli(queryStartMillis - Constants.MILLIS_PER_DAY).atZone(zoneId).toLocalDate();
					final LocalDate endDate = Instant.ofEpochMilli(queryEndMillis + Constants.MILLIS_PER_DAY).atZone(zoneId).toLocalDate();

					for (LocalDate localDate = startDate; !localDate.isAfter(endDate); localDate = localDate.plusDays(1)) {
						callback.accept(
								gtfsData,
								stop,
								localDate.atStartOfDay(zoneId).toInstant().toEpochMilli(),
								gtfsData.getServiceIdsOnDate(localDate)
						);
					}

				}
			}
		});
	}

	private static boolean isBetween(long value, long min, long max) {
		return value >= min && value <= max;
	}

	@Nullable
	private static String cleanName(@Nullable String name, @Nullable String routeShortName) {
		return routeShortName == null ? name : name == null ? null : (name.startsWith(routeShortName + " ") ? name.substring(routeShortName.length()) : name).trim();
	}

	@FunctionalInterface
	private interface IterateCallback {
		void accept(GtfsData gtfsData, Stop stop, long offsetMillis, Set<AgencyAndId> validServiceIds);
	}
}
