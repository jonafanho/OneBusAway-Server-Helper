package org.transport.dto;

import org.transport.entity.RouteType;

public final class RouteDTO {

	public String routeId;
	public String agencyId;
	public String routeShortName;
	public String routeLongName;
	public String routeDesc;
	public RouteType routeType = RouteType.BUS;
	public String routeUrl;
	public String routeColor;
	public String routeTextColor;
}
