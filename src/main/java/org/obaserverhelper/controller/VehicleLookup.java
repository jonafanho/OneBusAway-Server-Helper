package org.obaserverhelper.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public final class VehicleLookup {

	private final Map<String, List<VehicleGroupExtension>> vehicleGroups = new HashMap<>();

	public VehicleLookup(@Value("${regions}") String[] regions) {
		final String vehiclesPage = RestClient.create().get().uri(String.format("https://www.pantographapp.com/%s/vehicles", regions[0])).retrieve().body(String.class);
		if (vehiclesPage != null) {
			final Matcher matcher = Pattern.compile("\"buildId\":\\s*\"[a-zA-Z]+\"").matcher(vehiclesPage);
			if (matcher.find()) {
				final String[] match = matcher.group().split("\"");
				final VehicleResponse vehiclesResponse = RestClient.create().get().uri(String.format("https://www.pantographapp.com/_next/data/%s/en/%s/vehicles.json", match[match.length - 1], regions[0])).retrieve().body(VehicleResponse.class);
				if (vehiclesResponse != null) {
					vehiclesResponse.pageProps.agencies.forEach((agencyId, agencyResponse) -> vehicleGroups.put(agencyId, agencyResponse.vehicles));
				}
			}
		}
	}

	@Nullable
	public VehicleGroup getVehicleGroup(String vehicleId) {
		final String[] vehicleIdSplit = vehicleId.split("_");
		if (vehicleIdSplit.length == 2) {
			try {
				final int vehicleIdNumber = Integer.parseInt(vehicleIdSplit[1]);
				final VehicleGroupExtension vehicleGroupExtension = vehicleGroups.getOrDefault(vehicleIdSplit[0], new ArrayList<>()).stream()
						.filter(vehicleGroup -> vehicleIdNumber >= vehicleGroup.min && vehicleIdNumber <= vehicleGroup.max).findFirst()
						.orElse(vehicleGroups.getOrDefault(vehicleIdSplit[0], new ArrayList<>()).stream().filter(vehicleGroup -> vehicleIdNumber == vehicleGroup.getIdInt()).findFirst().orElse(null));
				return vehicleGroupExtension == null ? null : vehicleGroupExtension.convert();
			} catch (Exception ignored) {
				return null;
			}
		} else {
			return null;
		}
	}

	private static class VehicleResponse {

		public final PagePropsResponse pageProps = new PagePropsResponse();
	}

	private static class PagePropsResponse {

		public final Map<String, AgencyResponse> agencies = new HashMap<>();
	}

	private static class AgencyResponse {

		public final List<VehicleGroupExtension> vehicles = new ArrayList<>();
	}

	private static class VehicleGroupExtension extends VehicleGroupBase {

		public int min;
		public int max;
		public String id;

		public int getIdInt() {
			try {
				return Integer.parseInt(id.replaceAll("\\D", ""));
			} catch (Exception ignored) {
				return -1;
			}
		}

		public VehicleGroup convert() {
			VehicleGroup vehicleGroup = new VehicleGroup();
			vehicleGroup.year = year;
			vehicleGroup.make = make;
			vehicleGroup.model = model;
			vehicleGroup.fuel = fuel;
			vehicleGroup.length = length;
			return vehicleGroup;
		}
	}

	private static class VehicleGroupBase {

		public int year;
		public String make;
		public String model;
		public String fuel;
		public int length;
	}

	public static class VehicleGroup extends VehicleGroupBase {
	}
}
