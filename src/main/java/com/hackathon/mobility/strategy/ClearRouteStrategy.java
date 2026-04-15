package com.hackathon.mobility.strategy;

import org.springframework.stereotype.Component;

@Component
public class ClearRouteStrategy implements HazardCalculationStrategy {

    @Override
    public int calculateDelayMinutes(int severityScore) {
        return 0;
    }
}
