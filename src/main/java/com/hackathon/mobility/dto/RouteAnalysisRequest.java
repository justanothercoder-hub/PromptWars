package com.hackathon.mobility.dto;

import jakarta.validation.constraints.NotBlank;

public record RouteAnalysisRequest(
        @NotBlank(message = "Origin address cannot be blank") String origin,
        @NotBlank(message = "Destination address cannot be blank") String destination
) {
}
