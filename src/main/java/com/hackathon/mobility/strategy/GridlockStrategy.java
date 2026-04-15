package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

@Component
public class GridlockStrategy implements HazardCalculationStrategy {

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore * 4;
    }
}
