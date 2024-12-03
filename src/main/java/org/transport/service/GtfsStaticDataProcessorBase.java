package org.transport.service;

import jakarta.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.transport.type.DTOBase;

import java.util.ArrayList;
import java.util.List;

public abstract class GtfsStaticDataProcessorBase<T, U extends JpaRepository<T, String>, V extends DTOBase<T>> {

	private final U repository;
	private final List<List<V>> dataLists = new ArrayList<>();

	private static final Logger LOGGER = LogManager.getLogger(GtfsStaticDataProcessorBase.class);

	public GtfsStaticDataProcessorBase(U repository) {
		this.repository = repository;
	}

	public final void initialize(List<V> dataList) {
		this.dataLists.add(dataList);
	}

	public final void processAll() {
		final List<T> dataList = new ArrayList<>();

		for (int i = 0; i < dataLists.size(); i++) {
			for (final V data : dataLists.get(i)) {
				try {
					dataList.add(process(data, i).convert(i));
				} catch (Exception e) {
					LOGGER.error("", e);
				}
			}
		}

		try {
			repository.saveAll(dataList);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	@Nonnull
	protected abstract V process(V data, int sourceIndex);
}
