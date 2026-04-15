package com.hackathon.mobility.service;

import com.hackathon.mobility.domain.Coordinates;
import com.hackathon.mobility.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeocodingService {

    private final WebClient webClient;
    private final String nominatimUrl;

    public GeocodingService(@Value("${nominatim.api.url}") String nominatimUrl) {
        this.webClient = WebClient.builder().build();
        this.nominatimUrl = nominatimUrl;
    }

    public Coordinates geocode(String rawAddress) {
        String address = rawAddress.trim() + ", Bangalore, India";
        try {
            JsonNode[] response = webClient.get()
                    .uri(nominatimUrl + "?q={addr}&format=json&limit=1", address)
                    .header("User-Agent", "PredictiveHazardAlerter/1.0")
                    .retrieve()
                    .bodyToMono(JsonNode[].class)
                    .block();

            if (response == null || response.length == 0) {
                throw new ExternalApiException("Could not find location: " + rawAddress);
            }
            JsonNode firstMatch = response[0];
            return new Coordinates(
                    firstMatch.get("lat").asDouble(),
                    firstMatch.get("lon").asDouble(),
                    firstMatch.get("display_name").asText()
            );
        } catch (Exception e) {
            if (e instanceof ExternalApiException) throw e;
            throw new ExternalApiException("Error communicating with Geocoding service: " + e.getMessage());
        }
    }
}
