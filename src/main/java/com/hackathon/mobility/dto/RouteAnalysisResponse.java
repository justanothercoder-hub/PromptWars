package com.hackathon.mobility.dto;

import com.hackathon.mobility.domain.ScoredRoute;

import java.util.List;

public record RouteAnalysisResponse(String origin, String destination, int totalRoutesAnalyzed, List<ScoredRoute> rankedRoutes, ScoredRoute bestRoute, String analysisMessage) {
}
