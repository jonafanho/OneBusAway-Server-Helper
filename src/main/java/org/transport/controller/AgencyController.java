package org.transport.controller;

import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.Stop;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.transport.entity.GtfsData;
import org.transport.response.DataResponse;
import org.transport.response.ListResult;
import org.transport.service.GtfsService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public final class AgencyController {

	private final GtfsService gtfsService;
	private static final LatLon ZERO = new LatLon(0, 0);

	public AgencyController(GtfsService gtfsService) {
		this.gtfsService = gtfsService;
	}

	@GetMapping("/agencies")
	public DataResponse agencies() {
		final long requestStartMillis = System.currentTimeMillis();
		final List<Agency> agencies = new ArrayList<>();
		gtfsService.gtfsDataList.forEach(gtfsData -> agencies.addAll(gtfsData.gtfsDao().getAllAgencies()));
		return ListResult.fromList(requestStartMillis, agencies);
	}

	@GetMapping("/centerPoint")
	public DataResponse centerPoint() {
		final long requestStartMillis = System.currentTimeMillis();
		double latSum = 0;
		double lonSum = 0;
		int count = 0;

		for (final GtfsData gtfsData : gtfsService.gtfsDataList) {
			for (final Stop stop : gtfsData.gtfsDao().getAllStops()) {
				if (stop.isLatSet() && stop.isLonSet()) {
					latSum += stop.getLat();
					lonSum += stop.getLon();
					count++;
				}
			}
		}

		return DataResponse.create(requestStartMillis, count == 0 ? ZERO : new LatLon(latSum / count, lonSum / count));
	}

	private record LatLon(double lat, double lon) {

	}
}
