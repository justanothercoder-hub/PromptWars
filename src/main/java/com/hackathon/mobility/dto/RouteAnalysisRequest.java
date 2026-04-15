package com.hackathon.mobility.dto;

import com.hackathon.mobility.domain.UserObstacle;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RouteAnalysisRequest(
        @NotBlank(message = "Origin address cannot be blank") String origin,
        @NotBlank(message = "Destination address cannot be blank") String destination,
        List<UserObstacle> userObstacles   // nullable — frontend sends [] if none placed
) {
}
