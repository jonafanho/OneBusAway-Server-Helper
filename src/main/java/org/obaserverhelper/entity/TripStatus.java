package org.obaserverhelper.entity;

import lombok.Getter;
import lombok.Setter;
import org.obaserverhelper.controller.VehicleLookup;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class TripStatus {

	private String activeTripId;
	private int blockTripSequence;
	private long serviceDate;
	private final Frequency frequency = new Frequency();
	private double scheduledDistanceAlongTrip;
	private double totalDistanceAlongTrip;
	private final LatLon position = new LatLon();
	private double orientation;
	private String closestStop;
	private int closestStopTimeOffset;
	private String nextStop;
	private String occupancyStatus;
	private String phase;
	private String status;
	private boolean predicted;
	private long lastUpdateTime;
	private long lastLocationUpdateTime;
	private final LatLon lastKnownLocation = new LatLon();
	private double lastKnownDistanceAlongTrip;
	private double lastKnownOrientation;
	private double distanceAlongTrip;
	private int scheduleDeviation;
	private String vehicleId;
	private final Set<String> situationIds = new HashSet<>();
	@Setter
	private VehicleLookup.VehicleGroup vehicleGroup;
}
