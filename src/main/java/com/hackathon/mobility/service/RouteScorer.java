package com.hackathon.mobility.service;

import com.hackathon.mobility.domain.HazardType;
import com.hackathon.mobility.domain.RouteSegment;
import com.hackathon.mobility.domain.ScoredRoute;
import com.hackathon.mobility.domain.RouteStatus;
import com.hackathon.mobility.strategy.AccidentStrategy;
import com.hackathon.mobility.strategy.ClearRouteStrategy;
import com.hackathon.mobility.strategy.GridlockStrategy;
import com.hackathon.mobility.strategy.HazardCalculationStrategy;
import com.hackathon.mobility.strategy.WaterloggingStrategy;
import com.hackathon.mobility.strategy.RoadClosureStrategy;
import com.hackathon.mobility.strategy.ConstructionStrategy;
import com.hackathon.mobility.strategy.FloodingStrategy;
import com.hackathon.mobility.strategy.DebrisStrategy;
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
    private final RoadClosureStrategy rc;
    private final ConstructionStrategy cs;
    private final FloodingStrategy fl;
    private final DebrisStrategy db;

    public RouteScorer(WaterloggingStrategy w, AccidentStrategy a, GridlockStrategy g, ClearRouteStrategy c,
                       RoadClosureStrategy rc, ConstructionStrategy cs, FloodingStrategy fl, DebrisStrategy db) {
        this.w = w; this.a = a; this.g = g; this.c = c;
        this.rc = rc; this.cs = cs; this.fl = fl; this.db = db;
    }

    private HazardCalculationStrategy resolve(HazardType type) {
        return switch (type) {
            case WATERLOGGING  -> w;
            case ACCIDENT      -> a;
            case GRIDLOCK      -> g;
            case CLEAR         -> c;
            case ROAD_CLOSURE  -> rc;
            case CONSTRUCTION  -> cs;
            case FLOODING      -> fl;
            case DEBRIS        -> db;
        };
    }

    public List<ScoredRoute> scoreAndRank(List<RouteSegment> routes) {
        List<ScoredRoute> scored = new ArrayList<>();

        for (RouteSegment r : routes) {
            double distanceKm = r.distanceMeters() / 1000.0;
            int baseDurationMin = (int) (r.durationSeconds() / 60.0);

            // Sum hazard delays — 9999 is the impassable sentinel
            int hazardDelayMin = r.hazards().stream()
                    .mapToInt(h -> resolve(h.type()).calculateDelayMinutes(h.severityScore()))
                    .sum();

            // Detect impassable: any hazard produced the 9999 sentinel
            boolean isImpassable = r.hazards().stream()
                    .anyMatch(h -> resolve(h.type()).calculateDelayMinutes(h.severityScore()) >= 9999);

            // Determine RouteStatus — used by frontend for map coloring
            RouteStatus status = isImpassable ? RouteStatus.IMPASSABLE
                    : r.hazards().isEmpty() ? RouteStatus.CLEAR
                    : RouteStatus.HAZARDOUS;

            // Cap totalMin for display (do not show 9999 to user)
            int totalMin = isImpassable ? baseDurationMin : baseDurationMin + hazardDelayMin;

            // Impassable routes get MAX_VALUE score so they sink to the bottom
            double composite = isImpassable
                    ? Double.MAX_VALUE
                    : (distanceKm * 0.4) + (totalMin * 0.6);

            String rec = isImpassable ? "🚫 IMPASSABLE: Do not take this route."
                    : r.hazards().isEmpty() ? "✅ OPTIMAL: Clear path."
                    : (r.hazards().size() == 1 ? "⚠ CAUTION: Single disruption."
                            : "🚨 AVOID: Multiple severe hazards.");

            // Pass polyline through from RouteSegment — needed by frontend to draw route lines
            scored.add(new ScoredRoute(
                    r.routeIndex(), distanceKm, totalMin,
                    r.hazards(), composite, false, rec,
                    status, r.polyline()
            ));
        }

        // Sort: lowest composite score first (impassable routes go last due to MAX_VALUE)
        scored.sort(Comparator.comparingDouble(ScoredRoute::compositeScore));

        // Mark the best passable route
        if (!scored.isEmpty() && scored.get(0).status() != RouteStatus.IMPASSABLE) {
            ScoredRoute top = scored.get(0);
            scored.set(0, new ScoredRoute(
                    top.routeIndex(), top.distanceKm(), top.estimatedTotalMinutes(),
                    top.hazards(), top.compositeScore(), true,
                    "⭐ BEST RECOMMENDED ROUTE ⭐",
                    top.status(), top.polyline()
            ));
        }
        return scored;
    }
}
