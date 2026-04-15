package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

@Component
public class WaterloggingStrategy implements HazardCalculationStrategy {

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore * 3;
    }
}
