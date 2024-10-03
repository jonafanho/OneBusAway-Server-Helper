package org.obaserverhelper.entity;

import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public final class ArrivalAndDeparture implements Comparable<ArrivalAndDeparture> {

	private String routeId;
	private String tripId;
	private long serviceDate;
	private String stopId;
	private int stopSequence;
	private int totalStopsInTrip;
	private int blockTripSequence;
	private String routeShortName;
	private String routeLongName;
	private String tripHeadsign;
	private boolean arrivalEnabled;
	private boolean departureEnabled;
	private long scheduledArrivalTime;
	private long scheduledDepartureTime;
	private final Frequency frequency = new Frequency();
	private boolean predicted;
	private long predictedArrivalTime;
	private long predictedDepartureTime;
	private double distanceFromStop;
	private int numberOfStopsAway;
	private final TripStatus tripStatus = new TripStatus();

	void replaceRouteShortName(String[] routeShortNameReplacements) {
		for (final String routeShortNameReplacement : routeShortNameReplacements) {
			routeShortName = routeShortName.replaceAll(routeShortNameReplacement, "");
		}
	}

	private long getDepartureTime() {
		return predictedDepartureTime == 0 ? scheduledDepartureTime : predictedDepartureTime;
	}

	@Override
	public int compareTo(@NonNull ArrivalAndDeparture arrivalAndDeparture) {
		return Long.compare(getDepartureTime(), arrivalAndDeparture.getDepartureTime());
	}
}
