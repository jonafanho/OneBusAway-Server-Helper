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
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.transport.entity.GtfsDao;
import org.transport.entity.GtfsData;
import org.transport.entity.GtfsSource;
import org.transport.entity.RealtimeData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public final class GtfsService {

	public List<GtfsData> gtfsDataList = new ArrayList<>();

	public GtfsService(@Value("${sources.location}") String sourceLocation, WebClient webClient) throws IOException {
		log.info("Starting setup");
		final GtfsSource[] gtfsSources = new ObjectMapper().readValue(new File(sourceLocation), GtfsSource[].class);
		for (final GtfsSource gtfsSource : gtfsSources) {
			final GtfsSource.Realtime realtime = gtfsSource.realtime();
			gtfsDataList.add(new GtfsData(setupSource(gtfsSource), new RealtimeData(webClient, realtime.sources(), realtime.agencies())));
		}
		log.info("Finished setup");
	}

	private static GtfsDao setupSource(GtfsSource gtfsSource) throws IOException {
		final List<String> sources = gtfsSource.schedule().sources();
		final File sourceFile;

		if (sources.size() == 1) {
			sourceFile = new File(sources.get(0));
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

			sourceFile = File.createTempFile("gtfs_merged_", ".zip");
			sourceFile.deleteOnExit();
			gtfsMerger.run(sources.stream().map(File::new).toList(), sourceFile);
		}

		final GtfsReader gtfsReader = new GtfsReader();
		gtfsReader.setInputLocation(sourceFile);
		final GtfsDao gtfsDao = new GtfsDao();
		gtfsReader.setEntityStore(gtfsDao);
		gtfsReader.run();

		return gtfsDao;
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
