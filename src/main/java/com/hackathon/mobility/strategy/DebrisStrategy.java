package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

/**
 * Road debris (fallen trees, rubble). Passable with light delays.
 */
@Component
public class DebrisStrategy implements HazardCalculationStrategy {

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore * 3;
    }
}
