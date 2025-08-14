package org.transport.controller;

import org.onebusaway.gtfs.model.Stop;
import org.springframework.web.bind.annotation.*;
import org.transport.entity.GtfsData;
import org.transport.entity.StopExtension;
import org.transport.response.DataResponse;
import org.transport.response.ListResult;
import org.transport.service.GtfsService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public final class StopController {

	private final GtfsService gtfsService;

	public StopController(GtfsService gtfsService) {
		this.gtfsService = gtfsService;
	}

	@GetMapping("/stops-for-location")
	public DataResponse stopsForLocation(@RequestParam double lat, @RequestParam double lon, @RequestParam double latSpan, @RequestParam double lonSpan, @RequestParam(defaultValue = "32") int maxCount) {
		final long requestStartMillis = System.currentTimeMillis();
		final double halfLatSpan = Math.abs(latSpan / 2);
		final double halfLonSpan = Math.abs(lonSpan / 2);

		final List<StopExtension> stopExtensions = new ArrayList<>();
		for (final GtfsData gtfsData : gtfsService.gtfsDataList) {
			for (final StopExtension stopExtension : gtfsData.gtfsDao.getAllStopExtensions()) {
				final Stop stop = stopExtension.stop();
				if (stop.isLatSet() && stop.isLonSet() && isBetween(stop.getLat(), lat - halfLatSpan, lat + halfLatSpan) && isBetween(stop.getLon(), lon - halfLonSpan, lon + halfLonSpan)) {
					stopExtensions.add(stopExtension);
					if (stopExtensions.size() >= maxCount) {
						return ListResult.fromList(requestStartMillis, stopExtensions);
					}
				}
			}
		}

		return ListResult.fromList(requestStartMillis, stopExtensions);
	}

	private static boolean isBetween(double value, double min, double max) {
		return value >= min && value <= max;
	}
}
