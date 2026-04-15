package com.hackathon.mobility.service;

import com.hackathon.mobility.domain.Coordinates;
import com.hackathon.mobility.domain.Hazard;
import com.hackathon.mobility.domain.HazardType;
import com.hackathon.mobility.domain.RouteSegment;
import com.hackathon.mobility.domain.ScoredRoute;
import com.hackathon.mobility.dto.RouteAnalysisRequest;
import com.hackathon.mobility.dto.RouteAnalysisResponse;
import com.hackathon.mobility.dto.RouteCheckRequest;
import com.hackathon.mobility.dto.RouteCheckResponse;
import com.hackathon.mobility.exception.InvalidRouteException;
import com.hackathon.mobility.strategy.AccidentStrategy;
import com.hackathon.mobility.strategy.ClearRouteStrategy;
import com.hackathon.mobility.strategy.GridlockStrategy;
import com.hackathon.mobility.strategy.HazardCalculationStrategy;
import com.hackathon.mobility.strategy.WaterloggingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HazardPredictionService {

    private final WaterloggingStrategy waterloggingStrategy;
    private final AccidentStrategy accidentStrategy;
    private final GridlockStrategy gridlockStrategy;
    private final ClearRouteStrategy clearRouteStrategy;
    private final List<Hazard> mockHazards;
    private final GeocodingService geocodingService;
    private final RoutingService routingService;
    private final ObstacleInjectionService obstacleInjectionService;
    private final RouteScorer routeScorer;

    public HazardPredictionService(
            WaterloggingStrategy waterloggingStrategy,
            AccidentStrategy accidentStrategy,
            GridlockStrategy gridlockStrategy,
            ClearRouteStrategy clearRouteStrategy,
            GeocodingService geocodingService,
            RoutingService routingService,
            ObstacleInjectionService obstacleInjectionService,
            RouteScorer routeScorer
    ) {
        this.waterloggingStrategy = waterloggingStrategy;
        this.accidentStrategy = accidentStrategy;
        this.gridlockStrategy = gridlockStrategy;
        this.clearRouteStrategy = clearRouteStrategy;
        this.geocodingService = geocodingService;
        this.routingService = routingService;
        this.obstacleInjectionService = obstacleInjectionService;
        this.routeScorer = routeScorer;

        this.mockHazards = List.of(
                new Hazard("H1", HazardType.WATERLOGGING, "Outer Ring Road",    8, "3rd Main Road",    null, null),
                new Hazard("H2", HazardType.ACCIDENT,     "MG Road",            6, "Residency Road",   null, null),
                new Hazard("H3", HazardType.GRIDLOCK,     "Silk Board Junction",9, "HSR Layout Route", null, null),
                new Hazard("H4", HazardType.CLEAR,        "Bannerghatta Road",  0, null,               null, null)
        );
    }

    private HazardCalculationStrategy resolveStrategy(HazardType type) {
        return switch (type) {
            case WATERLOGGING -> waterloggingStrategy;
            case ACCIDENT     -> accidentStrategy;
            case GRIDLOCK     -> gridlockStrategy;
            case CLEAR        -> clearRouteStrategy;
        };
    }

    public RouteCheckResponse analyzeRoute(RouteCheckRequest request) {

        // Step A — simulate AI processing latency
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Step B — route lookup (case-insensitive)
        Hazard hazard = mockHazards.stream()
                .filter(h -> h.routeName().equalsIgnoreCase(request.routeName()))
                .findFirst()
                .orElseThrow(() -> new InvalidRouteException(
                        "Route '" + request.routeName() + "' is not recognized by the system."
                ));

        // Step C — delegate to strategy
        HazardCalculationStrategy strategy = resolveStrategy(hazard.type());
        int delayMinutes = strategy.calculateDelayMinutes(hazard.severityScore());

        // Step D — build response
        if (hazard.type() == HazardType.CLEAR) {
            return new RouteCheckResponse(
                    false,
                    "Route is clear. Safe travels!",
                    0,
                    null,
                    0
            );
        }

        int timeSaved = (int) (delayMinutes * 0.6);

        return new RouteCheckResponse(
                true,
                "⚠ ALERT: " + hazard.type() + " detected on " + hazard.routeName()
                        + ". Estimated delay: " + delayMinutes + " mins. Reroute via "
                        + hazard.alternateRoute() + " to save " + timeSaved + " mins.",
                delayMinutes,
                hazard.alternateRoute(),
                timeSaved
        );
    }

    public RouteAnalysisResponse analyzeJourney(RouteAnalysisRequest request) {
        Coordinates origin = geocodingService.geocode(request.origin());
        Coordinates dest = geocodingService.geocode(request.destination());

        List<RouteSegment> rawRoutes = routingService.getRoutes(origin, dest);
        List<RouteSegment> hazardRoutes = obstacleInjectionService.injectObstacles(rawRoutes, origin, dest);
        List<ScoredRoute> ranked = routeScorer.scoreAndRank(hazardRoutes);

        return new RouteAnalysisResponse(
                origin.displayName(),
                dest.displayName(),
                ranked.size(),
                ranked,
                ranked.isEmpty() ? null : ranked.get(0),
                "Analyzed " + ranked.size() + " real routes. Showing best option."
        );
    }
}
