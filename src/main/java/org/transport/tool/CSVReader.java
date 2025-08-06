package org.transport.tool;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

@Slf4j
public final class CSVReader {

	public static <T> List<T> read(ZipInputStream zipInputStream, Class<T> dataClass) {
		final List<T> dataList = Collections.synchronizedList(new ArrayList<>());
		final long startMillis = System.currentTimeMillis();
		final ExecutorService executorService = Executors.newCachedThreadPool();
		final ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());

		try {
			CSVFormat.DEFAULT.builder()
					.setHeader()
					.setSkipHeaderRecord(true)
					.get()
					.parse(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8))
					.forEach(csvRecord -> executorService.execute(() -> {
						final Map<String, String> csvMap = new HashMap<>();
						csvRecord.toMap().forEach((key, value) -> {
							if (StringUtils.hasText(value)) {
								csvMap.put(key, value);
							}
						});
						try {
							dataList.add(objectMapper.convertValue(csvMap, dataClass));
						} catch (Exception e) {
							log.error("", e);
						}
					}));

			executorService.shutdown();
			if (executorService.awaitTermination(1, TimeUnit.HOURS)) {
				log.info("Read successful in {} ms [{}]", System.currentTimeMillis() - startMillis, dataClass.getSimpleName());
			} else {
				log.error("Read failed in {} ms [{}]", System.currentTimeMillis() - startMillis, dataClass.getName());
			}
		} catch (Exception e) {
			log.error("", e);
		}

		return dataList;
	}
}
