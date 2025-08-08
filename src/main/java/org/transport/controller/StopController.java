package org.transport.controller;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.transport.entity.GtfsData;
import org.transport.response.DataResponse;
import org.transport.response.ListResult;
import org.transport.service.GtfsService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public final class StopController {

	private final GtfsService gtfsService;

	public StopController(GtfsService gtfsService) {
		this.gtfsService = gtfsService;
	}

	@GetMapping("/stop")
	public DataResponse stop(@RequestParam String stopId) {
		final long requestStartMillis = System.currentTimeMillis();

		for (final GtfsData gtfsData : gtfsService.gtfsDataList) {
			final Stop stop = gtfsData.gtfsRelationalDao().getStopForId(AgencyAndId.convertFromString(stopId));
			if (stop != null) {
				return DataResponse.create(requestStartMillis, stop);
			}
		}

		return DataResponse.createError(requestStartMillis, new Exception("Stop not found"));
	}

	@GetMapping("/stops-for-location")
	public DataResponse stopsForLocation(@RequestParam double lat, @RequestParam double lon, @RequestParam double latSpan, @RequestParam double lonSpan, @RequestParam(defaultValue = "32") int maxCount) {
		final long requestStartMillis = System.currentTimeMillis();
		final double halfLatSpan = Math.abs(latSpan / 2);
		final double halfLonSpan = Math.abs(lonSpan / 2);

		final List<Stop> stops = new ArrayList<>();
		for (final GtfsData gtfsData : gtfsService.gtfsDataList) {
			for (final Stop stop : gtfsData.gtfsRelationalDao().getAllStops()) {
				if (stop.isLatSet() && stop.isLonSet() && isBetween(stop.getLat(), lat - halfLatSpan, lat + halfLatSpan) && isBetween(stop.getLon(), lon - halfLonSpan, lon + halfLonSpan)) {
					stops.add(stop);
					if (stops.size() >= maxCount) {
						return ListResult.fromList(requestStartMillis, stops);
					}
				}
			}
		}

		return ListResult.fromList(requestStartMillis, stops);
	}

	private static boolean isBetween(double value, double min, double max) {
		return value >= min && value <= max;
	}
}
