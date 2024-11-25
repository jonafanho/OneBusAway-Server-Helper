package org.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.TimeZone;

@Entity
@Getter
public final class Stop {

	@Id
	private final String stopId = "";

	@Column
	private final String stopCode = null;

	@Column
	private final String stopName = null;

	@Column
	private final String stopDesc = null;

	@Column
	private final Double stopLat = null;

	@Column
	private final Double stopLon = null;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private final LocationType locationType = LocationType.STOP;

	@Column(nullable = false)
	private final TimeZone stopTimezone = TimeZone.getDefault();

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private final WheelchairBoarding wheelchairBoarding = WheelchairBoarding.NONE;
}
