package com.hackathon.mobility.domain;

public record Hazard(
        String id,
        HazardType type,
        String routeName,
        int severityScore,
        String alternateRoute,
        Double segmentLat,
        Double segmentLng
) {
    public Hazard {
        if (severityScore < 0 || severityScore > 10) {
            throw new IllegalArgumentException(
                    "Severity score must be between 0 and 10, got: " + severityScore
            );
        }
    }
}
