package org.transport.entity;

import java.util.List;
import java.util.Map;

public record GtfsSource(Schedule schedule, Realtime realtime) {

	public record Schedule(List<String> sources) {
	}

	public record Realtime(List<String> sources, Map<String, String> agencies) {
	}
}
