package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

@Component
public class AccidentStrategy implements HazardCalculationStrategy {

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return severityScore * 5;
    }
}
