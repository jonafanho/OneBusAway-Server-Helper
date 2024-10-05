package org.obaserverhelper.entity;

import lombok.Getter;

@Getter
public final class Trip {

	private String routeId;
	private String serviceId;
	private String id;
	private String tripHeadsign;
	private String tripShortName;
	private String blockId;
	private String shapeId;
	private int wheelchairAccessible;
	private int bikesAllowed;
}
