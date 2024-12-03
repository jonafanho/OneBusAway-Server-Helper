package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Agency;
import org.transport.repository.AgencyRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Component
public final class AgencyProcessor extends GtfsStaticDataProcessorBase<Agency, AgencyRepository, Agency.AgencyDTO> {

	private Map<Integer, TimeZone> timeZones = new HashMap<>();

	public AgencyProcessor(AgencyRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Agency.AgencyDTO process(Agency.AgencyDTO data, int sourceIndex) {
		if (!timeZones.containsKey(sourceIndex)) {
			timeZones.put(sourceIndex, data.agencyTimezone);
		}
		return data;
	}

	TimeZone getTimeZone(int sourceIndex) {
		return timeZones.getOrDefault(sourceIndex, TimeZone.getDefault());
	}
}
