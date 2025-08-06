package org.transport.response;

import org.transport.generated.Frequency;

public record ArrivalAndDeparture(
		String routeId,
		String tripId,
		long serviceDate,
		String stopId,
		int stopSequence,
		int totalStopsInTrip,
		int blockTripSequence,
		String routeShortName,
		String routeLongName,
		String tripHeadsign,
		boolean arrivalEnabled,
		boolean departureEnabled,
		long scheduledArrivalTime,
		long scheduledDepartureTime,
		Frequency frequency,
		String predicted,
		String predictedArrivalTime,
		String predictedDepartureTime,
		String distanceFromStop,
		String numberOfStopsAway,
		String tripStatus
) {
}
