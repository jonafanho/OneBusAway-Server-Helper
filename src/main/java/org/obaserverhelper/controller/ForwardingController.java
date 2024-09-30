package org.obaserverhelper.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.obaserverhelper.entity.AbstractData;
import org.obaserverhelper.entity.ArrivalsAndDepartures;
import org.obaserverhelper.entity.Response;
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
			response.merge(newResponse);
		}

		if (response != null) {
			response.getData().getEntry().trim(trackDestinations, skipTerminating, maxCount, routeShortNameReplacements == null ? new String[0] : routeShortNameReplacements.split(","));
		}

		return response;
	}

	private <T extends AbstractData> Response<T> get(String requestUri, String queryString, @Nullable Boolean includeReferences, ParameterizedTypeReference<Response<T>> responseType) {
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
