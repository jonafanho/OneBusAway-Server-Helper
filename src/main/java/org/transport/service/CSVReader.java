package org.transport.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.apache.commons.csv.CSVFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

public final class CSVReader {

	private static final Logger LOGGER = LogManager.getLogger(CSVReader.class);

	public static <T> List<T> read(ZipInputStream zipInputStream, Class<T> dataClass) {
		final List<T> dataList = new ArrayList<>();

		try {
			CSVFormat.DEFAULT.builder()
					.setHeader()
					.setSkipHeaderRecord(true)
					.build()
					.parse(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8))
					.getRecords()
					.forEach(csvRecord -> {
						final Map<String, String> csvMap = new HashMap<>();
						csvRecord.toMap().forEach((key, value) -> {
							if (StringUtils.hasText(value)) {
								csvMap.put(key, value);
							}
						});
						try {
							dataList.add(new ObjectMapper()
									.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
									.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy())
									.convertValue(csvMap, dataClass));
						} catch (Exception e) {
							LOGGER.error("", e);
						}
					});
		} catch (Exception e) {
			LOGGER.error("", e);
		}

		return dataList;
	}
}
