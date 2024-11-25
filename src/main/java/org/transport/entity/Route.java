package org.transport.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public final class Route {

	@Id
	private final String routeId;

	@ManyToOne
	@JoinColumn(nullable = false, name = "AGENCY_ID")
	private final Agency agency;

	@Column
	private final String routeShortName;

	@Column
	private final String routeLongName;

	@Column
	private final String routeDesc;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private final RouteType routeType;

	@Column
	private final String routeUrl;

	@Column
	private final String routeColor;

	@Column
	private final String routeTextColor;

	public Route() {
		this.routeId = "";
		this.agency = new Agency();
		this.routeShortName = "";
		this.routeLongName = "";
		this.routeDesc = "";
		this.routeType = RouteType.BUS;
		this.routeUrl = "";
		this.routeColor = "";
		this.routeTextColor = "";
	}

	public Route(Route route, Agency agency) {
		this.routeId = route.routeId;
		this.agency = agency;
		this.routeShortName = route.routeShortName;
		this.routeLongName = route.routeLongName;
		this.routeDesc = route.routeDesc;
		this.routeType = route.routeType;
		this.routeUrl = route.routeUrl;
		this.routeColor = route.routeColor;
		this.routeTextColor = route.routeTextColor;
	}
}
