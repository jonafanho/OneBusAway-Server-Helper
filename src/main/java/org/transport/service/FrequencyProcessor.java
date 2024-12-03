package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Frequency;
import org.transport.repository.FrequencyRepository;

@Component
public final class FrequencyProcessor extends GtfsStaticDataProcessorBase<Frequency, FrequencyRepository, Frequency.FrequencyDTO> {

	public FrequencyProcessor(FrequencyRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Frequency.FrequencyDTO process(Frequency.FrequencyDTO data, int sourceIndex) {
		return data;
	}
}
