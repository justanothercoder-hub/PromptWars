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
                JsonNode route = routes.get(i);
                JsonNode summary = route.get("summary");

                // Extract polyline — ORS returns encoded polyline string by default
                String encodedGeometry = route.get("geometry").asText();
                List<List<Double>> polyline = decodePolyline(encodedGeometry);

                segments.add(new RouteSegment(
                        i,
                        summary.get("distance").asDouble(),
                        summary.get("duration").asDouble(),
                        new ArrayList<>(),
                        polyline
                ));
            }
            return segments;
        } catch (Exception e) {
            throw new ExternalApiException("Error fetching routes from Routing service: " + e.getMessage());
        }
    }

    private List<List<Double>> decodePolyline(String encoded) {
        List<List<Double>> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            
            poly.add(List.of(((double) lat / 1E5), ((double) lng / 1E5))); // ORS uses 1E5 (5 decimals)
        }
        return poly;
    }
}
