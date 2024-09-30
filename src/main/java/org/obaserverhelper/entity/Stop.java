package org.obaserverhelper.entity;

import lombok.Getter;

@Getter
public final class Stop {

	private String id;
	private String code;
	private String name;
	private String ttsStopName;
	private String desc;
	private double lat;
	private double lon;
	private String zoneId;
	private String url;
	private int locationType;
	private String parentStation;
	private String timezone;
	private WheelchairBoarding wheelchairBoarding;
	private String levelId;
	private String platformCode;
}
