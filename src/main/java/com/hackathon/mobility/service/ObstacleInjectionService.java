package com.hackathon.mobility.service;

import com.hackathon.mobility.domain.Coordinates;
import com.hackathon.mobility.domain.Hazard;
import com.hackathon.mobility.domain.HazardType;
import com.hackathon.mobility.domain.RouteSegment;
import com.hackathon.mobility.domain.UserObstacle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ObstacleInjectionService {

    public List<RouteSegment> injectObstacles(List<RouteSegment> routes, Coordinates origin, Coordinates dest, List<UserObstacle> userObstacles) {
        Random random = new Random(System.currentTimeMillis());
        HazardType[] hazardPool = {
            HazardType.WATERLOGGING,
            HazardType.ACCIDENT,
            HazardType.GRIDLOCK,
            HazardType.ROAD_CLOSURE,
            HazardType.CONSTRUCTION,
            HazardType.FLOODING,
            HazardType.DEBRIS
        };

        for (RouteSegment route : routes) {
            int maxHazards = route.routeIndex() == 0 ? 3 : 2;
            int hazardCount = random.nextInt(maxHazards + 1);

            for (int i = 0; i < hazardCount; i++) {
                HazardType type = hazardPool[random.nextInt(hazardPool.length)];
                int severity = 3 + random.nextInt(6);

                double fraction = (i + 1.0) / (hazardCount + 1.0);
                double hazardLat = origin.lat() + fraction * (dest.lat() - origin.lat());
                double hazardLng = origin.lng() + fraction * (dest.lng() - origin.lng());

                route.hazards().add(new Hazard(
                        "H-" + route.routeIndex() + "-" + i,
                        type,
                        "Route Option " + (route.routeIndex() + 1),
                        severity,
                        null,
                        hazardLat,
                        hazardLng
                ));
            }
        }
        // Inject user-placed obstacles into all routes
        if (userObstacles != null && !userObstacles.isEmpty()) {
            for (RouteSegment route : routes) {
                for (int u = 0; u < userObstacles.size(); u++) {
                    UserObstacle uo = userObstacles.get(u);
                    route.hazards().add(new Hazard(
                            "UO-" + route.routeIndex() + "-" + u,
                            uo.type(),
                            "Route Option " + (route.routeIndex() + 1),
                            uo.severity(),
                            null,
                            uo.lat(),
                            uo.lng()
                    ));
                }
            }
        }
        return routes;
    }
}
