package org.transport.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Source(@JsonProperty("static") String staticSource, Realtime realtime) {

	public record Realtime() {
	}
}