package org.obaserverhelper.entity;

import lombok.Getter;

@Getter
public final class Route {

	private String id;
	private String agencyId;
	private String shortName;
	private String longName;
	private String desc;
	private int type;
	private String url;
	private String color;
	private String textColor;
	private int sortOrder;
	private int continuousPickup;
	private int continuousDropoff;
	private String networkId;
}
