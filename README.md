<div align="center">

# 🚀 Predictive Hazard & Bottleneck Alerter

### _Intelligent Route Navigator for Urban Mobility_

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Leaflet](https://img.shields.io/badge/Leaflet.js-1.9.4-199900?style=for-the-badge&logo=leaflet&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**A real-time urban mobility intelligence platform that predicts hazards, scores multi-route alternatives, and visualizes safe navigation paths on an interactive Leaflet.js map — powered by OpenRouteService and Nominatim geocoding.**

[Features](#-features) · [Architecture](#-architecture) · [Quick Start](#-quick-start) · [API Reference](#-api-reference) · [Tech Stack](#-tech-stack) · [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Problem Statement](#-problem-statement)
- [Our Solution](#-our-solution)
- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Running with Docker](#-running-with-docker)
- [API Reference](#-api-reference)
- [Design Patterns](#-design-patterns)
- [Hazard Types & Strategies](#-hazard-types--strategies)
- [Frontend Overview](#-frontend-overview)
- [Configuration](#%EF%B8%8F-configuration)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Problem Statement

Urban commuters face **unpredictable delays**, **unsafe routes**, and **inefficient navigation** due to:

| Problem | Impact |
|---|---|
| 🌊 **Waterlogging & Flooding** | Vehicles get stuck, detours cause cascading delays |
| 🚨 **Accidents & Road Closures** | Critical routes become impassable without prior warning |
| 🚦 **Traffic Gridlock** | Commuters waste hours stuck in bottlenecks |
| 🏗️ **Construction Zones & Debris** | No real-time info on which routes are passable |
| 📍 **Lack of Multi-Route Analysis** | GPS apps often suggest one path — no comparative scoring |

Existing navigation tools provide basic traffic data but **lack predictive hazard modeling, severity-based scoring, and interactive obstacle simulation** for urban planning and commuter safety.

---

## 💡 Our Solution

The **Predictive Hazard & Bottleneck Alerter** solves this by providing:

1. **🔍 Quick Scan Mode** — Instantly check any known Bangalore route for hazards with pre-configured severity data
2. **🗺️ Real Routes Mode** — Enter any two locations, get up to 3 real driving routes from OpenRouteService, each scored and ranked
3. **📊 Composite Scoring Engine** — Every route is scored using `(distance × 0.4) + (time × 0.6)` factoring in hazard delays
4. **🎨 Color-Coded Map Visualization** — Routes drawn as **green** (clear), **yellow** (hazardous), or **red** (impassable)
5. **📍 Interactive Obstacle Placement** — Click on the map to place custom obstacles (road closures, flooding, etc.) and re-analyze
6. **🎲 Obstacle Randomization** — Randomize hazards across routes for simulation and urban planning scenarios

---

## ✨ Features

### Core Capabilities

- ✅ **Multi-Route Analysis** — Fetches up to 3 alternative driving routes between any two locations
- ✅ **Predictive Hazard Scoring** — 8 hazard types with unique delay calculation strategies
- ✅ **Route Ranking** — Composite score algorithm ranks routes; best route is highlighted
- ✅ **Impassable Detection** — Routes with severe road closures or flooding (severity ≥ 8) marked as impassable
- ✅ **Real-Time Geocoding** — Converts addresses to coordinates via Nominatim (OpenStreetMap)
- ✅ **Interactive Leaflet Map** — Polyline rendering with hazard circle markers and click-to-inspect popups
- ✅ **User Obstacle Injection** — Place custom obstacles on the map with configurable type and severity
- ✅ **Responsive Glassmorphism UI** — Premium dark-mode interface with animations, gradients, and micro-interactions

### User Experience

- 🖱️ **One-Click Quick Pills** — Tap a hazard type to instantly load the matching route
- 🔄 **Randomize Obstacles** — Re-roll hazard distribution for scenario testing
- ⌨️ **Keyboard Navigation** — Press Enter to scan in both modes
- 📱 **Fully Responsive** — Works on desktop and mobile viewports
- ♿ **Accessible** — ARIA labels, roles, and live regions throughout

---

## 🏗 Architecture

```
┌──────────────────────────────────────────────────────────┐
│                    FRONTEND (SPA)                        │
│     index.html · Leaflet.js · Vanilla JS · CSS3         │
│         ↓ POST /api/v1/mobility/analyze                 │
│         ↓ POST /api/v1/mobility/check-route             │
├──────────────────────────────────────────────────────────┤
│                  SPRING BOOT BACKEND                     │
│                                                          │
│  ┌─────────────────────┐   ┌──────────────────────────┐ │
│  │ HazardPrediction    │   │   HealthController       │ │
│  │ Controller          │   │   GET /                   │ │
│  │  • /check-route     │   └──────────────────────────┘ │
│  │  • /analyze         │                                │
│  └────────┬────────────┘                                │
│           │                                              │
│  ┌────────▼────────────────────────────────────────────┐ │
│  │          HazardPredictionService                    │ │
│  │  Orchestrates geocoding → routing → obstacle        │ │
│  │  injection → scoring → response building            │ │
│  └──┬──────────┬──────────────┬────────────────┬──────┘ │
│     │          │              │                │        │
│  ┌──▼────┐ ┌──▼───────┐ ┌───▼──────────┐ ┌───▼─────┐  │
│  │Geocod-│ │ Routing  │ │  Obstacle    │ │ Route   │  │
│  │ing    │ │ Service  │ │  Injection   │ │ Scorer  │  │
│  │Service│ │          │ │  Service     │ │         │  │
│  └──┬────┘ └──┬───────┘ └──────────────┘ └───┬─────┘  │
│     │         │                               │        │
│     │         │      ┌────────────────────────┘        │
│     │         │      │  Strategy Pattern               │
│     │         │      │  ┌────────────────────────────┐ │
│     │         │      │  │ HazardCalculationStrategy  │ │
│     │         │      │  ├────────────────────────────┤ │
│     │         │      │  │ • WaterloggingStrategy     │ │
│     │         │      │  │ • AccidentStrategy         │ │
│     │         │      │  │ • GridlockStrategy         │ │
│     │         │      │  │ • RoadClosureStrategy      │ │
│     │         │      │  │ • FloodingStrategy         │ │
│     │         │      │  │ • ConstructionStrategy     │ │
│     │         │      │  │ • DebrisStrategy           │ │
│     │         │      │  │ • ClearRouteStrategy       │ │
│     │         │      │  └────────────────────────────┘ │
│     │         │                                        │
├─────▼─────────▼────────────────────────────────────────┤
│               EXTERNAL APIs                            │
│  ┌───────────────────┐  ┌────────────────────────────┐ │
│  │    Nominatim      │  │    OpenRouteService (ORS)  │ │
│  │  (Geocoding)      │  │  (Driving Directions +     │ │
│  │                   │  │   Alternative Routes)      │ │
│  └───────────────────┘  └────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

---

## 🛠 Tech Stack

### Backend

| Technology | Version | Purpose |
|---|---|---|
| **Java** | 17 | Core language (records, sealed types, pattern matching) |
| **Spring Boot** | 3.2.5 | Application framework, REST controllers, DI container |
| **Spring WebFlux** | 3.2.5 | Non-blocking `WebClient` for external API calls (ORS, Nominatim) |
| **Spring Validation** | 3.2.5 | `@Valid` + Jakarta Bean Validation on DTOs |
| **Jackson** | — | JSON serialization/deserialization |
| **Maven** | 3.8+ | Build tool and dependency management |

### Frontend

| Technology | Version | Purpose |
|---|---|---|
| **HTML5 / CSS3** | — | Semantic structure with glassmorphism design |
| **Vanilla JavaScript** | ES2020+ | Client-side logic (no framework overhead) |
| **Leaflet.js** | 1.9.4 | Interactive map rendering with polylines and markers |
| **Inter (Google Fonts)** | — | Premium typography |

### External APIs

| API | Purpose |
|---|---|
| **[OpenRouteService](https://openrouteservice.org/)** | Multi-route driving directions with polyline geometry |
| **[Nominatim (OpenStreetMap)](https://nominatim.openstreetmap.org/)** | Forward geocoding — address → lat/lng coordinates |

### DevOps

| Tool | Purpose |
|---|---|
| **Docker** | Multi-stage containerized deployment |
| **Git** | Version control |

---

## 📁 Project Structure

```
PromptWars/
├── 📄 pom.xml                          # Maven build config
├── 🐳 Dockerfile                       # Multi-stage Docker build
├── 📄 .gitignore
├── 📖 README.md
│
└── src/
    ├── main/
    │   ├── java/com/hackathon/mobility/
    │   │   │
    │   │   ├── 🚀 MobilityApplication.java         # Spring Boot entry point
    │   │   │
    │   │   ├── controller/
    │   │   │   ├── HazardPredictionController.java  # REST endpoints (/check-route, /analyze)
    │   │   │   └── HealthController.java            # Health check (GET /)
    │   │   │
    │   │   ├── domain/                              # Immutable Java Records
    │   │   │   ├── Coordinates.java                 # lat, lng, displayName
    │   │   │   ├── Hazard.java                      # id, type, severity, location
    │   │   │   ├── HazardType.java                  # Enum: 8 hazard categories
    │   │   │   ├── RouteSegment.java                # Raw route from ORS
    │   │   │   ├── RouteStatus.java                 # Enum: CLEAR | HAZARDOUS | IMPASSABLE
    │   │   │   ├── ScoredRoute.java                 # Scored + ranked route with polyline
    │   │   │   └── UserObstacle.java                # User-placed obstacle from frontend
    │   │   │
    │   │   ├── dto/                                 # Request/Response DTOs
    │   │   │   ├── RouteAnalysisRequest.java        # origin, destination, userObstacles
    │   │   │   ├── RouteAnalysisResponse.java       # rankedRoutes, bestRoute, message
    │   │   │   ├── RouteCheckRequest.java           # routeName (V1 quick scan)
    │   │   │   ├── RouteCheckResponse.java          # hazardDetected, delay, alternate
    │   │   │   └── ApiErrorResponse.java            # Standardized error envelope
    │   │   │
    │   │   ├── exception/                           # Global error handling
    │   │   │   ├── GlobalExceptionHandler.java      # @RestControllerAdvice
    │   │   │   ├── ExternalApiException.java        # 502 — ORS/Nominatim failures
    │   │   │   └── InvalidRouteException.java       # 404 — unknown route in V1
    │   │   │
    │   │   ├── service/                             # Business logic layer
    │   │   │   ├── HazardPredictionService.java     # Core orchestrator
    │   │   │   ├── GeocodingService.java            # Nominatim integration
    │   │   │   ├── RoutingService.java              # ORS integration + polyline decoder
    │   │   │   ├── ObstacleInjectionService.java    # Random + user obstacle injection
    │   │   │   └── RouteScorer.java                 # Composite scoring & ranking
    │   │   │
    │   │   └── strategy/                            # Strategy Pattern implementations
    │   │       ├── HazardCalculationStrategy.java   # Interface
    │   │       ├── WaterloggingStrategy.java         # severity × 3 min delay
    │   │       ├── AccidentStrategy.java             # severity × 5 min delay
    │   │       ├── GridlockStrategy.java             # severity × 4 min delay
    │   │       ├── RoadClosureStrategy.java          # severity × 10 or IMPASSABLE (≥8)
    │   │       ├── FloodingStrategy.java             # severity × 6 or IMPASSABLE (≥8)
    │   │       ├── ConstructionStrategy.java         # severity × 2 min delay
    │   │       ├── DebrisStrategy.java               # severity × 3 min delay
    │   │       └── ClearRouteStrategy.java           # 0 delay (safe route)
    │   │
    │   └── resources/
    │       ├── application.properties               # Server config, API keys, URLs
    │       └── static/
    │           └── index.html                       # Full SPA frontend (40KB)
    │
    └── test/
        └── java/com/hackathon/mobility/            # Test package
```

---

## ⚡ Quick Start

### Prerequisites

| Requirement | Version | Check |
|---|---|---|
| **Java JDK** | 17+ | `java --version` |
| **Apache Maven** | 3.8+ | `mvn --version` |
| **Internet Connection** | — | Required for ORS + Nominatim API calls |

> [!IMPORTANT]
> An **OpenRouteService API key** is bundled in `application.properties` for demo purposes. For production use, register for a free key at [openrouteservice.org](https://openrouteservice.org/dev/#/signup).

### Steps

**1. Clone the repository**

```bash
git clone https://github.com/your-username/PromptWars.git
cd PromptWars
```

**2. Build the project**

```bash
mvn clean package -DskipTests
```

**3. Run the application**

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/mobility-0.0.1-SNAPSHOT.jar
```

**4. Open the application**

Navigate to **[http://localhost:8080](http://localhost:8080)** in your browser.

> [!TIP]
> The frontend is served as a static file by Spring Boot — no separate dev server needed!

---

## 🐳 Running with Docker

The project includes a **multi-stage Dockerfile** for optimized containerized deployment.

**Build the image:**

```bash
docker build -t predictive-hazard-alerter .
```

**Run the container:**

```bash
docker run -p 8080:8080 predictive-hazard-alerter
```

**Docker Compose (optional):**

```yaml
version: '3.8'
services:
  hazard-alerter:
    build: .
    ports:
      - "8080:8080"
    environment:
      - ORS_API_KEY=your_api_key_here
```

---

## 📡 API Reference

Base URL: `http://localhost:8080`

### `GET /` — Health Check

Returns the service status.

**Response:**
```json
{
  "status": "running",
  "service": "Predictive Hazard Alerter"
}
```

---

### `POST /api/v1/mobility/check-route` — Quick Scan (V1)

Check a known Bangalore route for pre-configured hazard data.

**Request:**
```json
{
  "routeName": "Outer Ring Road"
}
```

**Supported Routes:** `Outer Ring Road`, `MG Road`, `Silk Board Junction`, `Bannerghatta Road`

**Success Response (hazard detected):**
```json
{
  "hazardDetected": true,
  "message": "⚠ ALERT: WATERLOGGING detected on Outer Ring Road. Estimated delay: 24 mins. Reroute via 3rd Main Road to save 14 mins.",
  "estimatedDelayMinutes": 24,
  "alternateRoute": "3rd Main Road",
  "timeSavedMinutes": 14
}
```

**Success Response (clear route):**
```json
{
  "hazardDetected": false,
  "message": "Route is clear. Safe travels!",
  "estimatedDelayMinutes": 0,
  "alternateRoute": null,
  "timeSavedMinutes": 0
}
```

**Error Response (unknown route):**
```json
{
  "timestamp": "2026-05-02T11:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Route 'Unknown Road' is not recognized by the system."
}
```

---

### `POST /api/v1/mobility/analyze` — Real Route Analysis (V2)

Geocode two locations, fetch real driving routes, inject hazards, score, and rank.

**Request:**
```json
{
  "origin": "Majestic Bus Station",
  "destination": "Electronic City",
  "userObstacles": [
    {
      "lat": 12.9352,
      "lng": 77.6245,
      "type": "ROAD_CLOSURE",
      "severity": 9
    }
  ]
}
```

> [!NOTE]
> The `userObstacles` array is optional. Send `[]` or omit it if no obstacles are placed.

**Success Response:**
```json
{
  "origin": "Majestic, Bengaluru, Karnataka, India",
  "destination": "Electronic City, Bengaluru, Karnataka, India",
  "totalRoutesAnalyzed": 3,
  "rankedRoutes": [
    {
      "routeIndex": 0,
      "distanceKm": 18.5,
      "estimatedTotalMinutes": 42,
      "hazards": [],
      "compositeScore": 32.6,
      "isBestRoute": true,
      "recommendation": "⭐ BEST RECOMMENDED ROUTE ⭐",
      "status": "CLEAR",
      "polyline": [[12.977, 77.572], [12.935, 77.624], ...]
    },
    {
      "routeIndex": 1,
      "distanceKm": 22.1,
      "estimatedTotalMinutes": 65,
      "hazards": [
        {
          "id": "H-1-0",
          "type": "GRIDLOCK",
          "routeName": "Route Option 2",
          "severityScore": 7,
          "alternateRoute": null,
          "segmentLat": 12.945,
          "segmentLng": 77.610
        }
      ],
      "compositeScore": 47.8,
      "isBestRoute": false,
      "recommendation": "⚠ CAUTION: Single disruption.",
      "status": "HAZARDOUS",
      "polyline": [[12.977, 77.572], ...]
    }
  ],
  "bestRoute": { ... },
  "analysisMessage": "Analyzed 3 real routes. Showing best option."
}
```

---

### Error Responses

All errors follow a consistent envelope:

| Status | Cause |
|---|---|
| `400` | Missing or blank required fields |
| `404` | Unknown route name in V1 quick scan |
| `502` | External API failure (ORS or Nominatim unreachable) |
| `500` | Unexpected server error |

---

## 🧩 Design Patterns

### 1. Strategy Pattern

The core hazard delay calculation uses the **Strategy Pattern**, allowing each hazard type to define its own delay algorithm without modifying the scorer.

```java
public interface HazardCalculationStrategy {
    int calculateDelayMinutes(int severityScore);
}
```

Each of the 8 hazard types implements this interface as a Spring `@Component`, automatically injected via constructor DI:

```
HazardCalculationStrategy
├── WaterloggingStrategy   → severity × 3
├── AccidentStrategy       → severity × 5
├── GridlockStrategy       → severity × 4
├── RoadClosureStrategy    → severity × 10 (or 9999 if severity ≥ 8 → IMPASSABLE)
├── FloodingStrategy       → severity × 6  (or 9999 if severity ≥ 8 → IMPASSABLE)
├── ConstructionStrategy   → severity × 2
├── DebrisStrategy         → severity × 3
└── ClearRouteStrategy     → 0 (always safe)
```

### 2. Immutable Records

All domain objects use **Java 17 Records** for immutability and concise data modeling:

```java
public record Hazard(String id, HazardType type, String routeName,
                     int severityScore, String alternateRoute,
                     Double segmentLat, Double segmentLng) { }
```

### 3. Global Exception Handling

A `@RestControllerAdvice` class provides centralized, consistent error responses across the entire API surface.

### 4. Service Layer Orchestration

`HazardPredictionService` acts as the **orchestrator**, coordinating the geocoding → routing → obstacle injection → scoring pipeline.

---

## ⚠️ Hazard Types & Strategies

| Hazard Type | Emoji | Delay Formula | Impassable? |
|---|---|---|---|
| `WATERLOGGING` | 💧 | `severity × 3 min` | No |
| `ACCIDENT` | 💥 | `severity × 5 min` | No |
| `GRIDLOCK` | 🚦 | `severity × 4 min` | No |
| `ROAD_CLOSURE` | 🚧 | `severity × 10 min` | **Yes** (severity ≥ 8) |
| `FLOODING` | 🌊 | `severity × 6 min` | **Yes** (severity ≥ 8) |
| `CONSTRUCTION` | 🏗️ | `severity × 2 min` | No |
| `DEBRIS` | 🪨 | `severity × 3 min` | No |
| `CLEAR` | ✅ | `0 min` | No |

### Route Status Color Mapping

| Status | Map Color | Meaning |
|---|---|---|
| `CLEAR` | 🟢 Green | No hazards — safe to take |
| `HAZARDOUS` | 🟡 Yellow | Has hazards but passable with delays |
| `IMPASSABLE` | 🔴 Red | Blocked — do **not** take this route |

### Composite Scoring Formula

```
compositeScore = (distanceKm × 0.4) + (estimatedTotalMinutes × 0.6)
```

Impassable routes receive `Double.MAX_VALUE` to sink to the bottom of the ranking.

---

## 🎨 Frontend Overview

The frontend is a **single-page application** (SPA) built in a single `index.html` file (712 lines, ~40KB) with:

| Feature | Implementation |
|---|---|
| **Design System** | CSS custom properties (`:root` variables) for colors, radius, transitions |
| **Theme** | Dark mode glassmorphism with floating animated orbs |
| **Layout** | CSS Grid — 2-column on desktop, stacked on mobile |
| **Map** | Leaflet.js with OpenStreetMap tiles |
| **Typography** | Inter font family (300–800 weights) |
| **Animations** | `fadeUp`, `fadeDown`, `fadeIn`, `spin`, `drift`, `pulse-dot` keyframe animations |
| **Accessibility** | `role`, `aria-label`, `aria-live`, `aria-selected` attributes |
| **Interaction Modes** | Tab-based switching between Quick Scan and Real Routes |

### Map Interaction

- **Click on route polylines** → Popup with distance, time, score, and status
- **Click on hazard circles** → Popup with hazard type and severity
- **Place obstacles** → Toggle placement mode, click on map, choose type/severity
- **Clear obstacles** → One-click clear all user-placed markers

---

## ⚙️ Configuration

All configuration lives in `src/main/resources/application.properties`:

```properties
# Server
server.port=8080
spring.application.name=predictive-hazard-alerter

# JSON serialization
spring.jackson.serialization.write-dates-as-timestamps=false

# OpenRouteService API
ors.api.key=<YOUR_ORS_API_KEY>
ors.api.url=https://api.openrouteservice.org/v2/directions/driving-car

# Nominatim Geocoding
nominatim.api.url=https://nominatim.openstreetmap.org/search
```

> [!WARNING]
> **Do not commit production API keys to version control.** Use environment variables or Spring profiles for production deployments.

### Environment Variable Overrides

You can override any property via environment variables:

```bash
ORS_API_KEY=your_key java -jar target/mobility-0.0.1-SNAPSHOT.jar
```

Or in Docker:

```bash
docker run -e ORS_API_KEY=your_key -p 8080:8080 predictive-hazard-alerter
```

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 🤝 Contributing

Contributions are welcome! Here's how to get started:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Coding Standards

- Java 17 features (records, switch expressions, text blocks)
- Immutable domain objects
- Strategy pattern for new hazard types
- `@RestControllerAdvice` for error handling
- Meaningful commit messages

---

## 🗺️ Roadmap

- [ ] 🔌 Real-time traffic data integration (Google Maps / HERE)
- [ ] 📊 Historical hazard heatmap overlay
- [ ] 🔔 Push notifications for route condition changes
- [ ] 🧠 ML-based hazard prediction from weather + event data
- [ ] 🚶 Multi-modal transport support (walking, cycling, public transit)
- [ ] 💾 Persistent hazard database (PostgreSQL)
- [ ] 🔐 User authentication and saved routes
- [ ] 📱 Progressive Web App (PWA) with offline support

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with ❤️ for Hackathon 2026**

_Intelligent Route Navigator · Predictive Hazard & Bottleneck Alerter for Urban Mobility_

⭐ **Star this repo if you found it useful!** ⭐

</div>
