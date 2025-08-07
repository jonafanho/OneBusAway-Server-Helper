package org.transport.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Source(@JsonProperty("static") String staticSource, Realtime realtime) {

	public record Realtime(String source, List<String> agencies) {

	}
}
