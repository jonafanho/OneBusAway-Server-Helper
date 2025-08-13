package org.transport.entity;

import org.onebusaway.gtfs.model.Stop;

import java.util.List;

public record StopExtension(Stop stop, Double direction, List<String> routes) {
}
