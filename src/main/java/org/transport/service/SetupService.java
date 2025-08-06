package org.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.transport.processor.GtfsStaticProcessor;
import org.transport.type.Source;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@ConditionalOnProperty(name = "setup.enabled", havingValue = "true")
public final class SetupService {

	private final GtfsStaticProcessor gtfsStaticProcessor;

	public SetupService(GtfsStaticProcessor gtfsStaticProcessor) {
		this.gtfsStaticProcessor = gtfsStaticProcessor;
	}

	@PostConstruct
	public void Setup() throws IOException {
		log.info("Starting setup");

		final Source[] sources = new ObjectMapper().readValue(new File("src/main/resources/sources.json"), Source[].class);
		for (int i = 0; i < sources.length; i++) {
			final Source source = sources[i];
			final String[] staticSourceSplit = source.staticSource().split("/");
			log.info("Reading source [{}]", staticSourceSplit[staticSourceSplit.length - 1]);
			if (source.staticSource().startsWith("https://")) {
				gtfsStaticProcessor.readZip(new URL(source.staticSource()).openStream(), i);
			} else if (Files.exists(Path.of(source.staticSource()))) {
				gtfsStaticProcessor.readZip(FileUtils.openInputStream(new File(source.staticSource())), i);
			} else {
				log.warn("Invalid source [{}]!", source);
			}
		}

		gtfsStaticProcessor.process();
		log.info("Finished setup");
	}
}
