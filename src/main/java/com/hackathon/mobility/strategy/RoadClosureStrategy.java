package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

/**
 * Road closure. Severity >= 8 triggers IMPASSABLE status via sentinel value 9999.
 * Lower severities represent partial closures with heavy delay.
 */
@Component
public class RoadClosureStrategy implements HazardCalculationStrategy {

    private static final int IMPASSABLE_SENTINEL = 9999;

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore >= 8 ? IMPASSABLE_SENTINEL : severityScore * 10;
    }
}
