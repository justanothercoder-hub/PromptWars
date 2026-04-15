package com.hackathon.mobility.service;

import com.hackathon.mobility.domain.Coordinates;
import com.hackathon.mobility.domain.RouteSegment;
import com.hackathon.mobility.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class RoutingService {

    private final WebClient webClient;
    private final String orsUrl;
    private final String apiKey;

    public RoutingService(@Value("${ors.api.url}") String orsUrl, @Value("${ors.api.key}") String apiKey) {
        this.webClient = WebClient.builder().build();
        this.orsUrl = orsUrl;
        this.apiKey = apiKey;
    }

    public List<RouteSegment> getRoutes(Coordinates origin, Coordinates dest) {
        try {
            Map<String, Object> bodyMap = Map.of(
                    "coordinates", List.of(
                            List.of(origin.lng(), origin.lat()),
                            List.of(dest.lng(), dest.lat())
                    ),
                    "alternative_routes", Map.of("target_count", 3, "weight_factor", 1.6)
            );

            JsonNode response = webClient.post()
                    .uri(orsUrl)
                    .header("Authorization", apiKey)
                    .bodyValue(bodyMap)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            List<RouteSegment> segments = new ArrayList<>();
            JsonNode routes = response.get("routes");
            for (int i = 0; i < routes.size(); i++) {
                JsonNode summary = routes.get(i).get("summary");
                segments.add(new RouteSegment(
                        i,
                        summary.get("distance").asDouble(),
                        summary.get("duration").asDouble(),
                        new ArrayList<>()
                ));
            }
            return segments;
        } catch (Exception e) {
            throw new ExternalApiException("Error fetching routes from Routing service: " + e.getMessage());
        }
    }
}
