package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

/**
 * Active construction zone. Passable but causes moderate delays.
 */
@Component
public class ConstructionStrategy implements HazardCalculationStrategy {

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore * 2;
    }
}
