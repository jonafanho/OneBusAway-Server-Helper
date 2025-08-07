package org.transport.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.transport.generated.StopTimeCache;
import org.transport.response.RealtimeResponse;
import org.transport.type.Source;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public final class RealtimeService {

	private final Map<String, RealtimeSource> realtimeSources = new HashMap<>();

	private final SourceService sourceService;
	private final RestTemplate restTemplate;

	public RealtimeService(SourceService sourceService, RestTemplate restTemplate) {
		this.sourceService = sourceService;
		this.restTemplate = restTemplate;
	}

	@Nullable
	public RealtimeResponse fetch(int sourceIndex, StopTimeCache stopTimeCache) {
		final Source.Realtime realtime = sourceService.sources[sourceIndex].realtime();
		final String source = realtime.source();
		final RealtimeResponse[] realtimeResponses = realtimeSources.computeIfAbsent(source, key -> new RealtimeSource()).fetch(restTemplate, source);
		RealtimeResponse filteredRealtimeResponse = null;

		for (final RealtimeResponse realtimeResponse : realtimeResponses) {
			if (realtime.agencies().contains(realtimeResponse.agency()) && realtimeResponse.matchesStopTimeCache(stopTimeCache)) {
				if (filteredRealtimeResponse == null) {
					filteredRealtimeResponse = realtimeResponse;
				} else {
					log.warn("Duplicate vehicle on block! [{}] [{}]", stopTimeCache.getRouteShortName(), stopTimeCache.getStopHeadsign());
					if (realtimeResponse.deviation() != 0) {
						filteredRealtimeResponse = realtimeResponse;
					}
				}
			}
		}

		return filteredRealtimeResponse;
	}

	private static final class RealtimeSource {

		private RealtimeResponse[] realtimeResponses;
		private long lastFetchMillis;

		private static final int FETCH_COOLDOWN = 5000;

		private RealtimeResponse[] fetch(RestTemplate restTemplate, String source) {
			final long currentMillis = System.currentTimeMillis();

			if (currentMillis - lastFetchMillis > FETCH_COOLDOWN) {
				final ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
				try {
					realtimeResponses = objectMapper.readValue(restTemplate.getForObject(source, String.class), RealtimeResponse[].class);
				} catch (Exception ignored) {
					realtimeResponses = new RealtimeResponse[0];
				}
				lastFetchMillis = currentMillis;
			}

			return realtimeResponses;
		}
	}
}
