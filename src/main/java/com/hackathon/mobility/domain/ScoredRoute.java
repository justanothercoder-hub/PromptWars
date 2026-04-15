package com.hackathon.mobility.domain;

import java.util.List;

public record ScoredRoute(int routeIndex, double distanceKm, int estimatedTotalMinutes, List<Hazard> hazards, double compositeScore, boolean isBestRoute, String recommendation) {
}
