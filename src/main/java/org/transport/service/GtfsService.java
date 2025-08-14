package org.transport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs_merge.GtfsMergeContext;
import org.onebusaway.gtfs_merge.GtfsMerger;
import org.onebusaway.gtfs_merge.strategies.*;
import org.onebusaway.gtfs_merge.strategies.scoring.AndDuplicateScoringStrategy;
import org.onebusaway.gtfs_merge.strategies.scoring.RouteStopsInCommonDuplicateScoringStrategy;
import org.onebusaway.gtfs_merge.strategies.scoring.StopDistanceDuplicateScoringStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.transport.entity.GtfsDao;
import org.transport.entity.GtfsData;
import org.transport.entity.GtfsSource;
import org.transport.entity.RealtimeData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public final class GtfsService {

	public final List<GtfsData> gtfsDataList;

	public GtfsService(@Value("${sources.location}") String sourceLocation, WebClient webClient) throws IOException {
		log.info("Starting setup");
		final List<GtfsData> gtfsDataList = Mono.fromCallable(() -> new ObjectMapper().readValue(new File(sourceLocation), GtfsSource[].class)).subscribeOn(Schedulers.boundedElastic())
				.flatMapMany(Flux::fromArray)
				.flatMap(gtfsSource -> setupSource(gtfsSource, webClient).map(gtfsDao -> {
					final GtfsSource.Realtime realtime = gtfsSource.realtime();
					return new GtfsData(gtfsDao, new RealtimeData(webClient, realtime.sources(), realtime.agencies()));
				}))
				.collectList()
				.block();
		this.gtfsDataList = gtfsDataList == null ? List.of() : gtfsDataList;
		log.info("Finished setup");
	}

	private static Mono<GtfsDao> setupSource(GtfsSource gtfsSource, WebClient webClient) {
		return Flux.fromStream(gtfsSource.schedule().sources().stream().map(source -> getFileFromSource(source, webClient).onErrorResume(e -> {
			log.warn("Skipping failed GTFS source [{}]", source, e);
			return Mono.empty();
		}))).flatMap(mono -> mono).collectList().flatMap(files -> {
			if (files.isEmpty()) {
				return Mono.empty();
			} else if (files.size() == 1) {
				return Mono.just(files.get(0));
			} else {
				final GtfsMerger gtfsMerger = new GtfsMerger();

				final NewStopMergeStrategy newStopMergeStrategy = new NewStopMergeStrategy();
				newStopMergeStrategy.setDuplicateDetectionStrategy(EDuplicateDetectionStrategy.FUZZY);
				newStopMergeStrategy.setDuplicateRenamingStrategy(EDuplicateRenamingStrategy.CONTEXT);
				newStopMergeStrategy.setLogDuplicatesStrategy(ELogDuplicatesStrategy.WARNING);
				gtfsMerger.setStopStrategy(newStopMergeStrategy);

				final NewRouteMergeStrategy newRouteMergeStrategy = new NewRouteMergeStrategy();
				newRouteMergeStrategy.setDuplicateDetectionStrategy(EDuplicateDetectionStrategy.FUZZY);
				newRouteMergeStrategy.setDuplicateRenamingStrategy(EDuplicateRenamingStrategy.CONTEXT);
				newRouteMergeStrategy.setLogDuplicatesStrategy(ELogDuplicatesStrategy.WARNING);
				gtfsMerger.setRouteStrategy(newRouteMergeStrategy);

				return createTempZipFile("gtfs_merged_").flatMap(mergedFile -> Mono.fromCallable(() -> {
					gtfsMerger.run(files, mergedFile);
					return mergedFile;
				}).subscribeOn(Schedulers.boundedElastic()));
			}
		}).flatMap(sourceFile -> Mono.fromCallable(() -> {
			final GtfsReader gtfsReader = new GtfsReader();
			gtfsReader.setInputLocation(sourceFile);
			final GtfsDao gtfsDao = new GtfsDao();
			gtfsReader.setEntityStore(gtfsDao);
			gtfsReader.run();
			return gtfsDao;
		}).subscribeOn(Schedulers.boundedElastic()));
	}

	private static Mono<File> getFileFromSource(String source, WebClient webClient) {
		if (source.startsWith("https://")) {
			return createTempZipFile("gtfs_source_").flatMap(sourceFile -> webClient.get()
					.uri(source)
					.retrieve()
					.bodyToFlux(DataBuffer.class)
					.as(dataBuffer -> DataBufferUtils.write(dataBuffer, sourceFile.toPath()))
					.thenReturn(sourceFile)
			).retryWhen(Retry.backoff(5, Duration.ofSeconds(10))).onErrorResume(e -> {
				log.error("Failed to download file from [{}]", source, e);
				return Mono.empty();
			});
		} else {
			return Mono.just(new File(source));
		}
	}

	private static Mono<File> createTempZipFile(String prefix) {
		return Mono.fromCallable(() -> {
			final File file = File.createTempFile(prefix, ".zip");
			file.deleteOnExit();
			return file;
		}).subscribeOn(Schedulers.boundedElastic());
	}

	private static final class NewStopMergeStrategy extends StopMergeStrategy {

		public NewStopMergeStrategy() {
			super();
			_duplicateScoringStrategy = new AndDuplicateScoringStrategy<>();
			_duplicateScoringStrategy.addStrategy(new NewStopDistanceDuplicateScoringStrategy());
		}
	}

	private static final class NewStopDistanceDuplicateScoringStrategy extends StopDistanceDuplicateScoringStrategy {

		@Override
		public double score(GtfsMergeContext context, Stop source, Stop target) {
			final double distanceScore = super.score(context, source, target);
			final boolean sameAgency = source.getId().getAgencyId().equalsIgnoreCase(target.getId().getAgencyId());
			final boolean sameName = source.getName().equalsIgnoreCase(target.getName());
			final boolean merge = (!sameAgency || sameName) && distanceScore == 1;
			if (merge) {
				log.warn("Merging stops [{}] and [{}]", source.getName(), target.getName());
			}
			return merge ? 1 : 0;
		}
	}

	private static final class NewRouteMergeStrategy extends RouteMergeStrategy {

		public NewRouteMergeStrategy() {
			super();
			_duplicateScoringStrategy = new AndDuplicateScoringStrategy<>();
			_duplicateScoringStrategy.addPropertyMatch("shortName");
			_duplicateScoringStrategy.addStrategy(new RouteStopsInCommonDuplicateScoringStrategy());
		}
	}
}
