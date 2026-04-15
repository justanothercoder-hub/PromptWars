package com.hackathon.mobility.controller;

import com.hackathon.mobility.dto.RouteAnalysisRequest;
import com.hackathon.mobility.dto.RouteAnalysisResponse;
import com.hackathon.mobility.dto.RouteCheckRequest;
import com.hackathon.mobility.dto.RouteCheckResponse;
import com.hackathon.mobility.service.HazardPredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mobility")
@CrossOrigin("*")
public class HazardPredictionController {

    private final HazardPredictionService hazardPredictionService;

    public HazardPredictionController(HazardPredictionService hazardPredictionService) {
        this.hazardPredictionService = hazardPredictionService;
    }

    @PostMapping("/check-route")
    public ResponseEntity<RouteCheckResponse> checkRoute(
            @Valid @RequestBody RouteCheckRequest request) {
        return ResponseEntity.ok(hazardPredictionService.analyzeRoute(request));
    }

    @PostMapping("/analyze")
    public ResponseEntity<RouteAnalysisResponse> analyzeJourney(
            @Valid @RequestBody RouteAnalysisRequest request) {
        return ResponseEntity.ok(hazardPredictionService.analyzeJourney(request));
    }
}
