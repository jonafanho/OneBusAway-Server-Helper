package org.transport.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.transport.dto.AgencyDTO;
import org.transport.dto.RouteDTO;
import org.transport.dto.StopDTO;
import org.transport.entity.Agency;
import org.transport.entity.Route;
import org.transport.entity.Stop;
import org.transport.repository.AgencyRepository;
import org.transport.repository.RouteRepository;
import org.transport.repository.StopRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public final class GtfsStaticProcessor {

	private final List<DataHolder> dataHolderList = new ArrayList<>();

	private final AgencyRepository agencyRepository;
	private final StopRepository stopRepository;
	private final RouteRepository routeRepository;

	private static final Logger LOGGER = LogManager.getLogger(GtfsStaticProcessor.class);

	public GtfsStaticProcessor(AgencyRepository agencyRepository, StopRepository stopRepository, RouteRepository routeRepository) {
		this.agencyRepository = agencyRepository;
		this.stopRepository = stopRepository;
		this.routeRepository = routeRepository;
	}

	public void readZip(InputStream inputStream) {
		try (final InputStream newInputStream = inputStream; final ZipInputStream zipInputStream = new ZipInputStream(newInputStream)) {
			final DataHolder dataHolder = new DataHolder();
			ZipEntry zipEntry;

			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				final String name = zipEntry.getName();
				switch (name) {
					case "agency.txt":
						dataHolder.agencyList.addAll(CSVReader.read(zipInputStream, AgencyDTO.class));
						break;
					case "stops.txt":
						dataHolder.stopsList.addAll(CSVReader.read(zipInputStream, StopDTO.class));
						break;
					case "routes.txt":
						dataHolder.routeList.addAll(CSVReader.read(zipInputStream, RouteDTO.class));
						break;
//					case "trips.txt":
//						trips.put(name, csvRecords);
//						break;
//					case "stop_times.txt":
//						stopTimes.put(name, csvRecords);
//						break;
//					case "calendar.txt":
//						calendar.put(name, csvRecords);
//						break;
//					case "calendar_dates.txt":
//						calendarDates.put(name, csvRecords);
//						break;
				}
				zipInputStream.closeEntry();
			}

			dataHolderList.add(dataHolder);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void process() {
		for (int i = 0; i < dataHolderList.size(); i++) {
			try {
				final DataHolder dataHolder = dataHolderList.get(i);

				for (AgencyDTO agencyDTO : dataHolder.agencyList) {
					// Append source index to ID
					agencyDTO.agencyId = formatId(i, agencyDTO.agencyId);
				}

				// Save agencies to database
				dataHolder.agencyList.forEach(agencyDTO -> agencyRepository.save(new ObjectMapper().convertValue(agencyDTO, Agency.class)));

				for (StopDTO stopDTO : dataHolder.stopsList) {
					// Append source index to ID
					stopDTO.stopId = formatId(i, stopDTO.stopId);
					// Use agency timezone if none set
					if (stopDTO.stopTimezone == null) {
						stopDTO.stopTimezone = dataHolder.agencyList.get(0).agencyTimezone;
					}
				}

				// Save stops to database
				dataHolder.stopsList.forEach(stopDTO -> stopRepository.save(new ObjectMapper().convertValue(stopDTO, Stop.class)));

				for (RouteDTO routeDTO : dataHolder.routeList) {
					// Append source index to ID
					routeDTO.routeId = formatId(i, routeDTO.routeId);
					// Save route to database
					routeRepository.save(new Route(
							new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).convertValue(routeDTO, Route.class),
							agencyRepository.findById(formatId(i, routeDTO.agencyId)).orElse(null)
					));
				}
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
	}

	private static String formatId(int sourceIndex, @Nullable String agencyId) {
		return StringUtils.hasText(agencyId) ? String.format("%s_%s", sourceIndex + 1, agencyId) : String.valueOf(sourceIndex + 1);
	}

	private static class DataHolder {

		private final List<AgencyDTO> agencyList = new ArrayList<>();
		private final List<StopDTO> stopsList = new ArrayList<>();
		private final List<RouteDTO> routeList = new ArrayList<>();
	}
}
