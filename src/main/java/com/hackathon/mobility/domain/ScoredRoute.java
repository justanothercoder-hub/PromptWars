package com.hackathon.mobility.domain;

import java.util.List;

/**
 * A RouteSegment after scoring. Added fields:
 *   status   — determines map polyline color (CLEAR=green, HAZARDOUS=yellow, IMPASSABLE=red)
 *   polyline — [[lat,lng],...] array passed through from RouteSegment for Leaflet rendering
 */
public record ScoredRoute(
        int routeIndex,
        double distanceKm,
        int estimatedTotalMinutes,
        List<Hazard> hazards,
        double compositeScore,
        boolean isBestRoute,
        String recommendation,
        RouteStatus status,
        List<List<Double>> polyline
) {}
