package org.transport.response;

import jakarta.annotation.Nullable;

public record ArrivalAndDeparture(
		long arrivalTime,
		long departureTime,
		boolean isTerminating,
		String routeShortName,
		String routeLongName,
		String stopHeadsign,
		String tripHeadsign,
		boolean isTimepoint,
		@Nullable String vehicleId,
		@Nullable String year,
		@Nullable String make,
		@Nullable String model,
		@Nullable String fuel,
		int deviation,
		boolean isAnomaly,
		int length
) {
}
