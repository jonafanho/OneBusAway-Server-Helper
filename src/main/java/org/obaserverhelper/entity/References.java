package org.obaserverhelper.entity;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class References {

	private final Set<Agency> agencies = new HashSet<>();
	private final Set<Route> routes = new HashSet<>();
	private final Set<Stop> stops = new HashSet<>();
	private final Set<Trip> trips = new HashSet<>();
	private final Set<Situation> situations = new HashSet<>();

	public void clear() {
		agencies.clear();
		routes.clear();
		stops.clear();
		trips.clear();
		situations.clear();
	}
}
