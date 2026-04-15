package com.hackathon.mobility.strategy;

public interface HazardCalculationStrategy {
    int calculateDelayMinutes(int severityScore);
}
