package org.transport.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.transport.generated.*;
import org.transport.tool.CSVReader;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public final class GtfsStaticProcessor {

	private final AgencyProcessor agencyProcessor;

	private final StopProcessor stopProcessor;
	private final RouteProcessor routeProcessor;
	private final TripProcessor tripProcessor;
	private final StopTimeProcessor stopTimeProcessor;
	private final CalendarProcessor calendarProcessor;
	private final CalendarDateProcessor calendarDateProcessor;
	private final AreaProcessor areaProcessor;
	private final StopAreaProcessor stopAreaProcessor;
	private final NetworkProcessor networkProcessor;
	private final RouteNetworkProcessor routeNetworkProcessor;
	private final ShapeProcessor shapeProcessor;
	private final FrequencyProcessor frequencyProcessor;
	private final LevelProcessor levelProcessor;
	private final LocationGroupProcessor locationGroupProcessor;
	private final LocationGroupStopProcessor locationGroupStopProcessor;
	private final BookingRuleProcessor bookingRuleProcessor;

	public GtfsStaticProcessor(
			AgencyProcessor agencyProcessor,
			StopProcessor stopProcessor,
			RouteProcessor routeProcessor,
			TripProcessor tripProcessor,
			StopTimeProcessor stopTimeProcessor,
			CalendarProcessor calendarProcessor,
			CalendarDateProcessor calendarDateProcessor,
			AreaProcessor areaProcessor,
			StopAreaProcessor stopAreaProcessor,
			NetworkProcessor networkProcessor,
			RouteNetworkProcessor routeNetworkProcessor,
			ShapeProcessor shapeProcessor,
			FrequencyProcessor frequencyProcessor,
			LevelProcessor levelProcessor,
			LocationGroupProcessor locationGroupProcessor,
			LocationGroupStopProcessor locationGroupStopProcessor,
			BookingRuleProcessor bookingRuleProcessor
	) {
		this.agencyProcessor = agencyProcessor;
		this.stopProcessor = stopProcessor;
		this.routeProcessor = routeProcessor;
		this.tripProcessor = tripProcessor;
		this.stopTimeProcessor = stopTimeProcessor;
		this.calendarProcessor = calendarProcessor;
		this.calendarDateProcessor = calendarDateProcessor;
		this.areaProcessor = areaProcessor;
		this.stopAreaProcessor = stopAreaProcessor;
		this.networkProcessor = networkProcessor;
		this.routeNetworkProcessor = routeNetworkProcessor;
		this.shapeProcessor = shapeProcessor;
		this.frequencyProcessor = frequencyProcessor;
		this.levelProcessor = levelProcessor;
		this.locationGroupProcessor = locationGroupProcessor;
		this.locationGroupStopProcessor = locationGroupStopProcessor;
		this.bookingRuleProcessor = bookingRuleProcessor;
	}

	public void readZip(InputStream inputStream, int sourceIndex) {
		try (final InputStream newInputStream = inputStream; final ZipInputStream zipInputStream = new ZipInputStream(newInputStream)) {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				final String name = zipEntry.getName();
				switch (name) {
					case "agency.txt":
						agencyProcessor.initialize(CSVReader.read(zipInputStream, Agency.AgencyDTO.class), sourceIndex);
						break;
					case "stops.txt":
						stopProcessor.initialize(CSVReader.read(zipInputStream, Stop.StopDTO.class), sourceIndex);
						break;
					case "routes.txt":
						routeProcessor.initialize(CSVReader.read(zipInputStream, Route.RouteDTO.class), sourceIndex);
						break;
					case "trips.txt":
						tripProcessor.initialize(CSVReader.read(zipInputStream, Trip.TripDTO.class), sourceIndex);
						break;
					case "stop_times.txt":
						stopTimeProcessor.initialize(CSVReader.read(zipInputStream, StopTime.StopTimeDTO.class), sourceIndex);
						break;
					case "calendar.txt":
						calendarProcessor.initialize(CSVReader.read(zipInputStream, Calendar.CalendarDTO.class), sourceIndex);
						break;
					case "calendar_dates.txt":
						calendarDateProcessor.initialize(CSVReader.read(zipInputStream, CalendarDate.CalendarDateDTO.class), sourceIndex);
						break;
					case "areas.txt":
						areaProcessor.initialize(CSVReader.read(zipInputStream, Area.AreaDTO.class), sourceIndex);
						break;
					case "stop_areas.txt":
						stopAreaProcessor.initialize(CSVReader.read(zipInputStream, StopArea.StopAreaDTO.class), sourceIndex);
						break;
					case "networks.txt":
						networkProcessor.initialize(CSVReader.read(zipInputStream, Network.NetworkDTO.class), sourceIndex);
						break;
					case "route_networks.txt":
						routeNetworkProcessor.initialize(CSVReader.read(zipInputStream, RouteNetwork.RouteNetworkDTO.class), sourceIndex);
						break;
					case "shapes.txt":
						shapeProcessor.initialize(CSVReader.read(zipInputStream, Shape.ShapeDTO.class), sourceIndex);
						break;
					case "frequencies.txt":
						frequencyProcessor.initialize(CSVReader.read(zipInputStream, Frequency.FrequencyDTO.class), sourceIndex);
						break;
					case "levels.txt":
						levelProcessor.initialize(CSVReader.read(zipInputStream, Level.LevelDTO.class), sourceIndex);
						break;
					case "location_groups.txt":
						locationGroupProcessor.initialize(CSVReader.read(zipInputStream, LocationGroup.LocationGroupDTO.class), sourceIndex);
						break;
					case "location_group_stops.txt":
						locationGroupStopProcessor.initialize(CSVReader.read(zipInputStream, LocationGroupStop.LocationGroupStopDTO.class), sourceIndex);
						break;
					case "booking_rules.txt":
						bookingRuleProcessor.initialize(CSVReader.read(zipInputStream, BookingRule.BookingRuleDTO.class), sourceIndex);
						break;
				}
				zipInputStream.closeEntry();
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	public void process() {
		agencyProcessor.processAll();
		levelProcessor.processAll();
		stopProcessor.processAll();
		routeProcessor.processAll();
		calendarProcessor.processAll();
		calendarDateProcessor.processAll();
		shapeProcessor.processAll();
		tripProcessor.processAll();
		locationGroupProcessor.processAll();
		bookingRuleProcessor.processAll();
		stopTimeProcessor.processAll();
		areaProcessor.processAll();
		stopAreaProcessor.processAll();
		networkProcessor.processAll();
		routeNetworkProcessor.processAll();
		frequencyProcessor.processAll();
		locationGroupStopProcessor.processAll();
	}
}
