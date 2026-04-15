package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

/**
 * Flooding. Severity >= 8 is physically impassable for vehicles.
 */
@Component
public class FloodingStrategy implements HazardCalculationStrategy {

    private static final int IMPASSABLE_SENTINEL = 9999;

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore >= 8 ? IMPASSABLE_SENTINEL : severityScore * 6;
    }
}
