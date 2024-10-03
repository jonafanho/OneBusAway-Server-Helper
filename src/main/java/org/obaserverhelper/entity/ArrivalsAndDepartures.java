package org.obaserverhelper.entity;

import lombok.Getter;
import org.obaserverhelper.controller.VehicleLookup;
import org.springframework.lang.Nullable;

import java.util.*;

@Getter
public final class ArrivalsAndDepartures {

	private final List<ArrivalAndDeparture> arrivalsAndDepartures = new ArrayList<>();
	private final Set<String> nearbyStopIds = new HashSet<>();
	private final Set<String> situationIds = new HashSet<>();
	private String stopId;

	public void trim(VehicleLookup vehicleLookup, @Nullable String trackDestinations, @Nullable Boolean skipTerminating, @Nullable Integer count, @Nullable String[] routeShortNameReplacements) {
		final String[] destinationSplit = trackDestinations == null ? null : trackDestinations.toLowerCase(Locale.ROOT).split(",");
		arrivalsAndDepartures.removeIf(arrivalAndDeparture -> {
			final boolean destinationMismatch = destinationSplit != null && (arrivalAndDeparture.getTripHeadsign() == null || Arrays.stream(destinationSplit).noneMatch(destination -> arrivalAndDeparture.getTripHeadsign().toLowerCase(Locale.ROOT).contains(destination)));
			final boolean isTerminating = Boolean.TRUE.equals(skipTerminating) && arrivalAndDeparture.getStopSequence() + 1 >= arrivalAndDeparture.getTotalStopsInTrip();
			return destinationMismatch || isTerminating;
		});

		Collections.sort(arrivalsAndDepartures);

		while (count != null && count >= 0 && arrivalsAndDepartures.size() > count) {
			arrivalsAndDepartures.remove(count.intValue());
		}

		if (routeShortNameReplacements != null) {
			arrivalsAndDepartures.forEach(arrivalAndDeparture -> arrivalAndDeparture.replaceRouteShortName(routeShortNameReplacements));
		}

		arrivalsAndDepartures.forEach(arrivalAndDeparture -> arrivalAndDeparture.getTripStatus().setVehicleGroup(vehicleLookup.getVehicleGroup(arrivalAndDeparture.getTripStatus().getVehicleId())));
	}

	public static final class ArrivalsAndDeparturesEntry extends AbstractData<ArrivalsAndDepartures> {

		public ArrivalsAndDeparturesEntry() {
			super(new ArrivalsAndDepartures());
		}

		public void merge(ArrivalsAndDeparturesEntry arrivalsAndDeparturesEntry) {
			final List<String> existingTripIds = new ArrayList<>();
			getEntry().arrivalsAndDepartures.forEach(arrivalAndDeparture -> existingTripIds.add(arrivalAndDeparture.getTripId()));
			arrivalsAndDeparturesEntry.getEntry().arrivalsAndDepartures.forEach(arrivalAndDeparture -> {
				if (!existingTripIds.contains(arrivalAndDeparture.getTripId())) {
					getEntry().arrivalsAndDepartures.add(arrivalAndDeparture);
				}
			});
		}
	}
}
