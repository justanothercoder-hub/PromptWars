package com.hackathon.mobility.service;

import com.hackathon.mobility.domain.HazardType;
import com.hackathon.mobility.domain.RouteSegment;
import com.hackathon.mobility.domain.ScoredRoute;
import com.hackathon.mobility.strategy.AccidentStrategy;
import com.hackathon.mobility.strategy.ClearRouteStrategy;
import com.hackathon.mobility.strategy.GridlockStrategy;
import com.hackathon.mobility.strategy.HazardCalculationStrategy;
import com.hackathon.mobility.strategy.WaterloggingStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RouteScorer {

    private final WaterloggingStrategy w;
    private final AccidentStrategy a;
    private final GridlockStrategy g;
    private final ClearRouteStrategy c;

    public RouteScorer(WaterloggingStrategy w, AccidentStrategy a, GridlockStrategy g, ClearRouteStrategy c) {
        this.w = w;
        this.a = a;
        this.g = g;
        this.c = c;
    }

    private HazardCalculationStrategy resolve(HazardType type) {
        return switch (type) {
            case WATERLOGGING -> w;
            case ACCIDENT -> a;
            case GRIDLOCK -> g;
            case CLEAR -> c;
        };
    }

    public List<ScoredRoute> scoreAndRank(List<RouteSegment> routes) {
        List<ScoredRoute> scored = new ArrayList<>();
        for (RouteSegment r : routes) {
            double distanceKm = r.distanceMeters() / 1000.0;
            int baseDurationMin = (int) (r.durationSeconds() / 60.0);

            int hazardDelayMin = r.hazards().stream()
                    .mapToInt(h -> resolve(h.type()).calculateDelayMinutes(h.severityScore()))
                    .sum();

            int totalMin = baseDurationMin + hazardDelayMin;
            double composite = (distanceKm * 0.4) + (totalMin * 0.6);

            String rec = r.hazards().isEmpty()
                    ? "✅ OPTIMAL: Clear path."
                    : (r.hazards().size() == 1
                            ? "⚠ CAUTION: Single disruption."
                            : "🚨 AVOID: Multiple severe hazards.");

            scored.add(new ScoredRoute(r.routeIndex(), distanceKm, totalMin, r.hazards(), composite, false, rec));
        }

        scored.sort(Comparator.comparingDouble(ScoredRoute::compositeScore));

        if (!scored.isEmpty()) {
            ScoredRoute top = scored.get(0);
            scored.set(0, new ScoredRoute(
                    top.routeIndex(), top.distanceKm(), top.estimatedTotalMinutes(),
                    top.hazards(), top.compositeScore(), true,
                    "⭐ BEST RECOMMENDED ROUTE ⭐"
            ));
        }
        return scored;
    }
}
