package com.hackathon.mobility.dto;

import jakarta.validation.constraints.NotBlank;

public record RouteCheckRequest(
        @NotBlank(message = "Route name cannot be blank")
        String routeName
) {
}
