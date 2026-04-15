package com.hackathon.mobility.domain;

import java.util.List;

/**
 * One route option returned from OpenRouteService.
 * polyline: list of [lat, lng] pairs extracted from ORS geometry.coordinates (already flipped from [lng,lat]).
 */
public record RouteSegment(
        int routeIndex,
        double distanceMeters,
        double durationSeconds,
        List<Hazard> hazards,
        List<List<Double>> polyline
) {}
