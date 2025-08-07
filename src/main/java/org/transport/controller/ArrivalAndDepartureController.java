package org.transport.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.transport.generated.StopTimeCache;
import org.transport.repository.StopTimeCacheRepository;
import org.transport.response.ArrivalAndDeparture;
import org.transport.response.DataResponse;
import org.transport.response.ListResult;
import org.transport.response.RealtimeResponse;
import org.transport.service.RealtimeService;
import org.transport.tool.Constants;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public final class ArrivalAndDepartureController {

	private final RealtimeService realtimeService;
	private final StopTimeCacheRepository stopTimeCacheRepository;

	public ArrivalAndDepartureController(RealtimeService realtimeService, StopTimeCacheRepository stopTimeCacheRepository) {
		this.realtimeService = realtimeService;
		this.stopTimeCacheRepository = stopTimeCacheRepository;
	}

	@GetMapping("/arrivals-and-departures-for-stop")
	public DataResponse arrivalsAndDeparturesForStop(
			@RequestParam String stopId,
			@RequestParam(defaultValue = "0") long time,
			@RequestParam(defaultValue = "120") long minutesAfter,
			@RequestParam(defaultValue = "false") boolean showTerminating
	) {
		final long requestStartMillis = System.currentTimeMillis();
		final long queryStartMillis = time <= 0 ? requestStartMillis : time;
		final long queryEndMillis = queryStartMillis + minutesAfter * Constants.SECONDS_PER_MINUTE * Constants.MILLIS_PER_SECOND;
		final int sourceIndex = getSourceIndex(stopId);

		final List<StopTimeCache> stopTimeCacheList;
		if (showTerminating) {
			stopTimeCacheList = stopTimeCacheRepository.findAllByStopIdAndDepartureTimeAfterAndArrivalTimeBefore(stopId, queryStartMillis, queryEndMillis);
		} else {
			stopTimeCacheList = stopTimeCacheRepository.findAllByStopIdAndDepartureTimeAfterAndArrivalTimeBeforeAndIsTerminating(stopId, queryStartMillis, queryEndMillis, false);
		}

		final List<ArrivalAndDeparture> newStopTimeCacheList = stopTimeCacheList.stream()
				.map(stopTimeCache -> {
					final RealtimeResponse realtimeResponse = realtimeService.fetch(sourceIndex, stopTimeCache);
					final int deviation = realtimeResponse == null ? 0 : realtimeResponse.deviation() * Constants.MILLIS_PER_SECOND;
					return new ArrivalAndDeparture(
							stopTimeCache.getArrivalTime() + deviation,
							stopTimeCache.getDepartureTime() + deviation,
							stopTimeCache.isTerminating(),
							stopTimeCache.getRouteShortName(),
							cleanName(stopTimeCache.getRouteLongName(), stopTimeCache.getRouteShortName()),
							cleanName(stopTimeCache.getStopHeadsign(), stopTimeCache.getRouteShortName()),
							cleanName(stopTimeCache.getTripHeadsign(), stopTimeCache.getRouteShortName()),
							stopTimeCache.isTimepoint(),
							realtimeResponse == null ? null : realtimeResponse.vehicleId(),
							realtimeResponse == null ? null : realtimeResponse.year(),
							realtimeResponse == null ? null : realtimeResponse.make(),
							realtimeResponse == null ? null : realtimeResponse.model(),
							realtimeResponse == null ? null : realtimeResponse.fuel(),
							realtimeResponse == null ? 0 : deviation,
							realtimeResponse != null && realtimeResponse.isAnomaly(),
							realtimeResponse == null ? 0 : Math.round(realtimeResponse.length() * Constants.METERS_PER_FOOT)
					);
				})
				.sorted(Comparator.comparingLong(ArrivalAndDeparture::departureTime))
				.toList();

		return ListResult.fromList(requestStartMillis, newStopTimeCacheList);
	}

	private static int getSourceIndex(String stopId) {
		try {
			return Integer.parseInt(stopId.split("_", 2)[0]) - 1;
		} catch (Exception ignored) {
			return -1;
		}
	}

	private static String cleanName(String name, String routeShortName) {
		return (name.startsWith(routeShortName) ? name.substring(routeShortName.length()) : name).trim();
	}
}
