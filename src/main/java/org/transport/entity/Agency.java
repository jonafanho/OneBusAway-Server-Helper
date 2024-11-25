package org.transport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.TimeZone;

@Entity
@Getter
public final class Agency {

	@Id
	private final String agencyId = "";

	@Column(nullable = false)
	private final String agencyName = "";

	@Column(nullable = false)
	private final String agencyUrl = "";

	@Column(nullable = false)
	private final TimeZone agencyTimezone = TimeZone.getDefault();

	@Column
	private final String agencyLang = null;

	@Column
	private final String agencyPhone = null;

	@Column
	private final String fareUrl = null;

	@Column
	private final String agencyEmail = null;
}
