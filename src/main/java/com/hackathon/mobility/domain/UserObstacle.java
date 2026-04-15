package com.hackathon.mobility.domain;

/**
 * An obstacle placed by the user by clicking on the map.
 * Sent from the frontend via RouteAnalysisRequest.userObstacles.
 */
public record UserObstacle(
        double lat,
        double lng,
        HazardType type,
        int severity
) {}
