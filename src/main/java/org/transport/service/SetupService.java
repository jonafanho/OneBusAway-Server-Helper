package org.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.transport.entity.Source;
import org.transport.repository.AgencyRepository;
import org.transport.repository.RouteRepository;
import org.transport.repository.StopRepository;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@ConditionalOnProperty(name = "setup.enabled", havingValue = "true")
public final class SetupService {

	private static final Logger LOGGER = LogManager.getLogger(SetupService.class);

	public SetupService(
			AgencyRepository agencyRepository,
			StopRepository stopRepository,
			RouteRepository routeRepository,
			GtfsStaticProcessor gtfsStaticProcessor
	) throws IOException {
		LOGGER.info("Starting setup");
		agencyRepository.deleteAll();
		stopRepository.deleteAll();
		routeRepository.deleteAll();

		final Source[] sources = new ObjectMapper().readValue(new File("src/main/resources/sources.json"), Source[].class);
		for (final Source source : sources) {
			if (source.staticSource().startsWith("https://")) {
				gtfsStaticProcessor.readZip(new URL(source.staticSource()).openStream());
			} else if (Files.exists(Path.of(source.staticSource()))) {
				gtfsStaticProcessor.readZip(FileUtils.openInputStream(new File(source.staticSource())));
			} else {
				LOGGER.warn("Invalid source [{}]!", source);
			}
		}

		gtfsStaticProcessor.process();
	}
}
