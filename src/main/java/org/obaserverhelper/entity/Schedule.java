package org.obaserverhelper.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class Schedule {

	private String timeZone;
	private final Frequency frequency = new Frequency();
	private final List<StopTime> stopTimes = new ArrayList<>();
	private String previousTripId;
	private String nextTripId;
}
