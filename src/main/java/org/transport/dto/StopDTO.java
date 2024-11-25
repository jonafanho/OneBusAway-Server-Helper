package org.transport.dto;

import org.transport.entity.LocationType;
import org.transport.entity.WheelchairBoarding;

import java.util.TimeZone;

public final class StopDTO {

	public String stopId;
	public String stopCode;
	public String stopName;
	public String stopDesc;
	public double stopLat;
	public double stopLon;
	public LocationType locationType = LocationType.STOP;
	public TimeZone stopTimezone;
	public WheelchairBoarding wheelchairBoarding = WheelchairBoarding.NONE;
}
