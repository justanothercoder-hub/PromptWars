package com.hackathon.mobility.domain;

import java.util.List;

public record RouteSegment(int routeIndex, double distanceMeters, double durationSeconds, List<Hazard> hazards) {
}
