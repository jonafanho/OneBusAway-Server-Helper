package org.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.transport.type.Source;

import java.io.File;
import java.io.IOException;

@Service
public final class SourceService {

	public final Source[] sources = new ObjectMapper().readValue(new File("src/main/resources/sources.json"), Source[].class);

	public SourceService() throws IOException {
	}
}
