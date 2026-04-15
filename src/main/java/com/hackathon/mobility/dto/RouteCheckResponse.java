package com.hackathon.mobility.dto;

public record RouteCheckResponse(
        boolean hazardDetected,
        String message,
        int estimatedDelayMinutes,
        String alternateRoute,
        int timeSavedMinutes
) {
}
