package org.obaserverhelper.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.obaserverhelper.entity.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@RestController
@RequestMapping("api/where")
public final class ForwardingController {

	@Value("${servers}")
	private String[] servers;
	@Value("${keys}")
	private String[] keys;

	private final VehicleLookup vehicleLookup;

	public ForwardingController(VehicleLookup vehicleLookup) {
		this.vehicleLookup = vehicleLookup;
	}

	@GetMapping("/arrivals-and-departures-for-stop/{id}.json")
	public Response<ArrivalsAndDepartures.ArrivalsAndDeparturesEntry> getArrivalsAndDeparturesForStop(
			HttpServletRequest request,
			@PathVariable String id,
			@RequestParam(required = false) @Nullable String trackDestinations,
			@RequestParam(required = false) @Nullable Integer maxCount,
			@RequestParam(required = false) @Nullable Boolean skipTerminating,
			@RequestParam(required = false) @Nullable String routeShortNameReplacements,
			@RequestParam(required = false) @Nullable Boolean includeReferences
	) {
		return getArrivalsAndDeparturesForStops(request, id, trackDestinations, maxCount, skipTerminating, routeShortNameReplacements, includeReferences);
	}

	@GetMapping("/arrivals-and-departures-for-stops/{ids}.json")
	public Response<ArrivalsAndDepartures.ArrivalsAndDeparturesEntry> getArrivalsAndDeparturesForStops(
			HttpServletRequest request,
			@PathVariable String ids,
			@RequestParam(required = false) @Nullable String trackDestinations,
			@RequestParam(required = false) @Nullable Integer maxCount,
			@RequestParam(required = false) @Nullable Boolean skipTerminating,
			@RequestParam(required = false) @Nullable String routeShortNameReplacements,
			@RequestParam(required = false) @Nullable Boolean includeReferences
	) {
		Response<ArrivalsAndDepartures.ArrivalsAndDeparturesEntry> response = null;

		for (final String id : ids.split(",")) {
			final Response<ArrivalsAndDepartures.ArrivalsAndDeparturesEntry> newResponse = get(String.format("/api/where/arrivals-and-departures-for-stop/%s.json", id), request.getQueryString(), includeReferences, new ParameterizedTypeReference<>() {
			});
			if (response == null) {
				response = newResponse;
			}
			response.getData().merge(newResponse.getData());
		}

		if (response != null) {
			response.getData().getEntry().trim(vehicleLookup, trackDestinations, skipTerminating, maxCount, routeShortNameReplacements == null ? new String[0] : routeShortNameReplacements.split(","));
		}

		return response;
	}

	@GetMapping("/trip-details/{id}.json")
	public Response<TripDetails.TripDetailsEntry> getTripDetails(
			HttpServletRequest request,
			@PathVariable String id,
			@RequestParam(required = false) @Nullable Boolean includeReferences
	) {
		final Response<TripDetails.TripDetailsEntry> response = get(request.getRequestURI(), request.getQueryString(), includeReferences, new ParameterizedTypeReference<>() {
		});
		final TripStatus tripStatus = response.getData().getEntry().getStatus();
		if (tripStatus != null) {
			tripStatus.updateVehicleGroup(vehicleLookup);
		}
		return response;
	}

	@GetMapping("/**")
	public Response<GenericData> get(
			HttpServletRequest request,
			@RequestParam(required = false) @Nullable Boolean includeReferences
	) {
		return get(request.getRequestURI(), request.getQueryString(), includeReferences, new ParameterizedTypeReference<>() {
		});
	}

	private <T extends AbstractData<U>, U> Response<T> get(String requestUri, String queryString, @Nullable Boolean includeReferences, ParameterizedTypeReference<Response<T>> responseType) {
		// includeReferences parameter not working for some endpoints
		final Response<T> response = RestClient.create()
				.get()
				.uri(new DefaultUriBuilderFactory(servers[0]).builder().path(requestUri).query(queryString).replaceQueryParam("key", keys[0]).replaceQueryParam("includeReferences", true).build())
				.retrieve()
				.body(responseType);
		if (response != null && Boolean.FALSE.equals(includeReferences)) {
			response.clearReferences();
		}
		return response;
	}
}
