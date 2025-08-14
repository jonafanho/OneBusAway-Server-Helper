package org.transport.entity;

import org.onebusaway.gtfs.impl.GtfsDataServiceImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class GtfsData {

	public final GtfsDao gtfsDao;
	public final RealtimeData realtimeData;
	private final Map<LocalDate, Set<AgencyAndId>> serviceIdsOnDateCache = new HashMap<>();

	public GtfsData(GtfsDao gtfsDao, RealtimeData realtimeData) {
		this.gtfsDao = gtfsDao;
		this.realtimeData = realtimeData;

		final LocalDate localDate = LocalDate.now();
		for (int i = -1; i <= 2; i++) {
			getServiceIdsOnDate(localDate.plusDays(i));
		}
	}

	public Set<AgencyAndId> getServiceIdsOnDate(LocalDate localDate) {
		final GtfsDataServiceImpl gtfsDataService = new GtfsDataServiceImpl();
		gtfsDataService.setGtfsDao(gtfsDao);
		return serviceIdsOnDateCache.computeIfAbsent(localDate, key -> gtfsDataService.getServiceIdsOnDate(new ServiceDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth())));
	}
}
