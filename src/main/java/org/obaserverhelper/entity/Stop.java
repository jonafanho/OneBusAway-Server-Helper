package org.obaserverhelper.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class Stop {

	private String id;
	private String code;
	private String direction;
	private String name;
	private String ttsStopName;
	private String desc;
	private double lat;
	private double lon;
	private String zoneId;
	private String url;
	private int locationType;
	private String parent;
	private String timezone;
	private WheelchairBoarding wheelchairBoarding;
	private String levelId;
	private String platformCode;
	private final List<String> routeIds = new ArrayList<>();
	private final List<String> staticRouteIds = new ArrayList<>();
}
