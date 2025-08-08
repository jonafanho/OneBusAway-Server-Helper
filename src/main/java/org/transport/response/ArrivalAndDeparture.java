package org.transport.response;

import jakarta.annotation.Nullable;

public record ArrivalAndDeparture(
		long arrivalTime,
		long departureTime,
		boolean isTerminating,
		@Nullable String routeShortName,
		@Nullable String routeLongName,
		@Nullable String stopHeadsign,
		@Nullable String tripHeadsign,
		boolean isTimepoint,
		@Nullable String vehicleId,
		@Nullable String year,
		@Nullable String make,
		@Nullable String model,
		@Nullable String fuel,
		@Nullable Integer deviation,
		@Nullable Boolean isAnomaly,
		@Nullable Integer length
) {
}
