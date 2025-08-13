package org.transport.entity;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.StopTime;
import org.transport.tool.Utilities;
import org.transport.tool.Vector2d;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class GtfsDao extends GtfsRelationalDaoImpl {

	private Map<String, StopExtension> stopExtensions;

	private static final double SMALL_VALUE = 1E-32;

	@Override
	public void clearAllCaches() {
		super.clearAllCaches();
		stopExtensions.clear();
		stopExtensions = null;
	}

	public Collection<StopExtension> getAllStopExtensions() {
		if (stopExtensions == null) {
			stopExtensions = getAllStops()
					.stream()
					.collect(Collectors.toMap(stop -> stop.getId().toString(), stop -> {
						final List<StopTime> stopTimes = getStopTimesForStop(stop);
						final Vector2d directionVector = new Vector2d();

						stopTimes.stream()
								.map(stopTime -> stopTime.getTrip().getShapeId())
								.distinct()
								.forEach(shapeId -> {
									if (shapeId != null) {
										final List<ShapePoint> shapePoints = getShapePointsForShapeId(shapeId);

										if (shapePoints != null && shapePoints.size() >= 2) {
											int closestIndex = -1;
											double closestDistance = Double.MAX_VALUE;

											for (int i = 0; i < shapePoints.size(); i++) {
												final ShapePoint shapePoint = shapePoints.get(i);
												final double distance = Utilities.getDistance(shapePoint.getLon() - stop.getLon(), shapePoint.getLat() - stop.getLat());
												if (distance < closestDistance) {
													closestIndex = i;
													closestDistance = distance;
												}
											}

											if (closestIndex >= 0) {
												final ShapePoint closestShapePoint = Utilities.getElement(shapePoints, closestIndex);
												ShapePoint shapePoint1 = null;
												ShapePoint shapePoint2 = null;

												for (int i = 2; i >= 0; i--) {
													if (shapePoint1 == null) {
														shapePoint1 = Utilities.getElement(shapePoints, closestIndex - i);
													}
													if (shapePoint2 == null) {
														shapePoint2 = Utilities.getElement(shapePoints, closestIndex + i);
													}
												}

												if (closestShapePoint != null && shapePoint1 != null && shapePoint2 != null) {
													final Vector2d directionPartVector = new Vector2d();
													directionPartVector.resizeAndAdd(closestShapePoint.getLon() - shapePoint1.getLon(), closestShapePoint.getLat() - shapePoint1.getLat(), SMALL_VALUE);
													directionPartVector.resizeAndAdd(shapePoint2.getLon() - closestShapePoint.getLon(), shapePoint2.getLat() - closestShapePoint.getLat(), SMALL_VALUE);
													directionVector.normalizeAndAdd(directionPartVector.getX(), directionPartVector.getY());
												}
											}
										}
									}
								});

						return new StopExtension(
								stop,
								Utilities.getDistance(directionVector.getX(), directionVector.getY()) > 0.5 ? Math.atan2(directionVector.getY(), directionVector.getX()) : null,
								stopTimes.stream()
										.map(stopTime -> stopTime.getTrip().getRoute())
										.distinct()
										.sorted(Comparator.comparing((Route route) -> route.isSortOrderSet() ? route.getSortOrder() : Integer.MAX_VALUE)
												.thenComparingInt((Route route) -> {
													final String routeNumber = getRouteName(route).replaceAll("\\D+", "");
													return routeNumber.isEmpty() ? 0 : Integer.parseInt(routeNumber);
												})
												.thenComparing((Route route) -> getRouteName(route).replaceAll("\\d", "").toLowerCase())
												.thenComparing(GtfsDao::getRouteName)
										)
										.map(GtfsDao::getRouteName)
										.toList()
						);
					}));
		}

		return stopExtensions.values();
	}

	private static String getRouteName(Route route) {
		return route.getShortName() != null ? route.getShortName() : route.getLongName() != null ? route.getLongName() : route.getDesc();
	}
}
