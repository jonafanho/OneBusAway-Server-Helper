package org.obaserverhelper.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class TripDetails {

	private String tripId;
	private long serviceDate;
	private final Frequency frequency = new Frequency();
	private final TripStatus status = new TripStatus();
	private final Schedule schedule = new Schedule();
	private final List<String> situationIds = new ArrayList<>();

	public static final class TripDetailsEntry extends AbstractData<TripDetails> {
	}
}
