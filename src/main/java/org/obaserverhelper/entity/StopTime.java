package org.obaserverhelper.entity;

import lombok.Getter;

@Getter
public final class StopTime {

	private int arrivalTime;
	private int departureTime;
	private double distanceAlongTrip;
	private String historicalOccupancy;
	private String stopHeadsign;
	private String stopId;
}
