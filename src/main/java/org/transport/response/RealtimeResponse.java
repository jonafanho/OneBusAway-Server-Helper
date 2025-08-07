package org.transport.response;

import jakarta.annotation.Nullable;
import org.transport.generated.StopTimeCache;

import java.util.Objects;

public record RealtimeResponse(
		String agency,
		String tripHeadsign,
		double lat,
		double lon,
		String vehicleId,
		String tripId,
		@Nullable String blockId,
		String routeId,
		int deviation,
		String year,
		String make,
		String model,
		String fuel,
		boolean isAnomaly,
		int length
) {

	public boolean matchesStopTimeCache(StopTimeCache stopTimeCache) {
		return Objects.equals(stopTimeCache.getBlockId(), blockId);
	}
}
