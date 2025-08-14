package org.transport.response;

import jakarta.annotation.Nullable;

import java.util.List;

public record ArrivalAndDeparture(
		@Nullable Long arrivalTime,
		@Nullable Long departureTime,
		boolean isTerminating,
		@Nullable String routeShortName,
		@Nullable String routeLongName,
		@Nullable String stopHeadsign,
		@Nullable String tripHeadsign,
		@Nullable String lastStopName,
		boolean isTimepoint,
		List<Frequency> frequencies,
		@Nullable String vehicleId,
		@Nullable String year,
		@Nullable String make,
		@Nullable String model,
		@Nullable String fuel,
		@Nullable Integer deviation,
		@Nullable Boolean isAnomaly,
		@Nullable Integer length
) implements Comparable<ArrivalAndDeparture> {

	private long getCompareValue() {
		return departureTime == null ? frequencies.isEmpty() ? 0 : frequencies.get(0).startTime : departureTime;
	}

	@Override
	public int compareTo(ArrivalAndDeparture arrivalAndDeparture) {
		return Long.compare(getCompareValue(), arrivalAndDeparture.getCompareValue());
	}

	public record Frequency(long startTime, long endTime, int headway, boolean exactTimes) {
	}
}
