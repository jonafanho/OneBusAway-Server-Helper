package org.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.transport.type.Source;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(name = "setup.enabled", havingValue = "true")
public final class SetupService {

	private static final Logger LOGGER = LogManager.getLogger(SetupService.class);

	public SetupService(GtfsStaticProcessor gtfsStaticProcessor, @Value("${spring.datasource.url}") String datasourceUrl) throws IOException {
		LOGGER.info("Starting setup");
		FileUtils.cleanDirectory(Paths.get(datasourceUrl.split("file:")[1].split(";")[0]).getParent().toFile());

		final Source[] sources = new ObjectMapper().readValue(new File("src/main/resources/sources.json"), Source[].class);
		for (final Source source : sources) {
			final String[] staticSourceSplit = source.staticSource().split("/");
			LOGGER.info("Reading source [{}]", staticSourceSplit[staticSourceSplit.length - 1]);
			if (source.staticSource().startsWith("https://")) {
				gtfsStaticProcessor.readZip(new URL(source.staticSource()).openStream());
			} else if (Files.exists(Path.of(source.staticSource()))) {
				gtfsStaticProcessor.readZip(FileUtils.openInputStream(new File(source.staticSource())));
			} else {
				LOGGER.warn("Invalid source [{}]!", source);
			}
		}

		gtfsStaticProcessor.process();
		LOGGER.info("Finished setup");
	}
}
