package org.transport.entity;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;

public record GtfsData(GtfsRelationalDaoImpl gtfsRelationalDao, RealtimeData realtimeData) {
}
