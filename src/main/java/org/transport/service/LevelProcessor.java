package org.transport.service;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Level;
import org.transport.repository.LevelRepository;

@Component
public final class LevelProcessor extends GtfsStaticDataProcessorBase<Level, LevelRepository, Level.LevelDTO> {

	public LevelProcessor(LevelRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Level.LevelDTO process(Level.LevelDTO data, int sourceIndex) {
		return data;
	}
}
