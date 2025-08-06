package org.transport.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.transport.generated.Stop;
import org.transport.repository.StopRepository;
import org.transport.response.DataResponse;
import org.transport.response.ListResult;

@RestController
@RequestMapping("/api")
public final class StopController {

	private final StopRepository stopRepository;

	public StopController(StopRepository stopRepository) {
		this.stopRepository = stopRepository;
	}

	@GetMapping("/stop")
	public DataResponse stop(@RequestParam String stopId) {
		final Stop stop = stopRepository.findById(stopId).orElse(null);
		return stop == null ? DataResponse.createError(new Exception("Stop not found")) : DataResponse.create(stop);
	}

	@GetMapping("/stops-for-location")
	public DataResponse stopsForLocation(@RequestParam double lat, @RequestParam double lon, @RequestParam double latSpan, @RequestParam double lonSpan, @RequestParam(required = false, defaultValue = "32") int maxCount) {
		final double halfLatSpan = Math.abs(latSpan / 2);
		final double halfLonSpan = Math.abs(lonSpan / 2);
		return ListResult.fromPage(stopRepository.findByStopLatBetweenAndStopLonBetween(lat - halfLatSpan, lat + halfLatSpan, lon - halfLonSpan, lon + halfLonSpan, PageRequest.of(0, maxCount)));
	}
}
