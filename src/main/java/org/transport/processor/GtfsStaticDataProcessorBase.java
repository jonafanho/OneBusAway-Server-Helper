package org.transport.processor;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.transport.type.DTOBase;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class GtfsStaticDataProcessorBase<T, U extends JpaRepository<T, String>, V extends DTOBase<T>> {

	private final U repository;
	private final List<List<V>> dataLists = new ArrayList<>();

	public GtfsStaticDataProcessorBase(U repository) {
		this.repository = repository;
	}

	public final void initialize(List<V> dataList, int sourceIndex) {
		while (dataLists.size() < sourceIndex) {
			dataLists.add(new ArrayList<>());
		}
		dataLists.add(dataList);
	}

	public final void processAll() {
		final List<T> dataList = new ArrayList<>();

		for (int i = 0; i < dataLists.size(); i++) {
			for (final V data : dataLists.get(i)) {
				try {
					dataList.add(process(data, i).convert(i));
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}

		try {
			final long startMillis = System.currentTimeMillis();
			saveToRepository(dataList);
			log.info("Finished save in {} ms [{}]", System.currentTimeMillis() - startMillis, getClass().getSimpleName());
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Nonnull
	protected abstract V process(V data, int sourceIndex);

	protected void saveToRepository(List<T> dataList) {
		repository.saveAll(dataList);
	}
}
