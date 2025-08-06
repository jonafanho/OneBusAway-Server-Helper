package org.transport.processor;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.transport.generated.Area;
import org.transport.repository.AreaRepository;

@Component
public final class AreaProcessor extends GtfsStaticDataProcessorBase<Area, AreaRepository, Area.AreaDTO> {

	public AreaProcessor(AreaRepository repository) {
		super(repository);
	}

	@Nonnull
	@Override
	protected Area.AreaDTO process(Area.AreaDTO data, int sourceIndex) {
		return data;
	}
}
