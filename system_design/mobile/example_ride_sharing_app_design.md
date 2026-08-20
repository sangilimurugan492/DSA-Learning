# Example: Design a Ride-Sharing App (Uber/Ola-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile ride-sharing application like Uber/Ola for 30 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter for rider app."*
2. Core features? → *"Request ride, driver matching, real-time tracking, fare estimation, payment, ratings."*
3. Two apps? → *"Yes, rider app and driver app. Focus on rider app, mention driver app."*
4. Ride types? → *"Multiple: bike, auto, car (economy, premium)."*
5. Real-time tracking? → *"Yes, live driver location, ETA updates."*
6. Payments? → *"Yes, in-app wallet + UPI/card + COD."*
7. Scale? → *"30M users, ~5M daily rides."*

**Summary:**
- **Functional**: Set pickup/destination, fare estimate, request ride, driver matching, real-time tracking, payment, ratings, ride history
- **Non-functional**: <5s driver match, <2s location update, 30M users, 5M rides/day, battery-efficient location tracking

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Pickup/destination selection (map + search)
- Fare estimation (dynamic pricing)
- Ride request & driver matching
- Real-time driver tracking
- Payment processing
- Ride history & ratings

**Out of scope:**
- Driver app (mention architecture, don't deep dive)
- Surge pricing algorithm (mention, not deep dive)
- Carpooling/shared rides

---

## Step 3 — Identify Constraints (5 min)

```
30M users, ~5M daily rides
Peak: 8-10am, 5-8pm → 3x avg traffic
Concurrent active riders: ~500K during peak
Concurrent active drivers: ~300K during peak
Driver location updates: every 3s per active driver
Rider location: every 5s during ride request
Target: <5s driver match, <2s location update latency
Devices: 70% Android, many low-end
Battery: critical — driver app runs 8+ hours/day
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Rider App (Flutter)                        │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │  Map /   │ │ Ride   │ │ Payment  │ │  Ride       │ │
│  │ Location │ │ Request│ │ / Wallet │ │  History    │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   WebSocket   Location                   │
│   (fare, ride  (driver loc  (GPS,                     │
│    history)    ride status)  Geolocator)              │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + Load Balancer
        │ + WS Gateway │
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │ Matching│ │  Ride    │ │  Payment  │
  │ Service │ │ Service  │ │  Service  │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │Redis   │ │PostgreSQL│ │Payment     │
  │(geo +  │ │(rides)  │ │Gateway     │
  │driver  │ └─────────┘ └───────────┘
  │pool)   │
  └────────┘
  
Kafka → Ride events (requested, matched, started, completed)
FCM/APNS → Push notifications (driver assigned, ride arrived)
Google Maps API → Geocoding, routing, ETA
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: MapScreen, RideRequestSheet,      │
│           TrackingMap, PaymentSelection,     │
│           RideHistoryPage                     │
│  BLoCs: LocationBloc, RideBloc,              │
│         TrackingBloc, PaymentBloc            │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - EstimateFareUseCase                        │
│  - RequestRideUseCase                         │
│  - TrackRideUseCase                           │
│  - CompletePaymentUseCase                     │
│  - RateDriverUseCase                          │
│  Repositories (abstract):                     │
│  - RideRepository                             │
│  - LocationRepository                         │
│  - PaymentRepository                          │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: RideApi, FareApi (Dio)              │
│  Real-time: RideWebSocket (driver loc,       │
│             ride status)                      │
│  Location: Geolocator (GPS)                   │
│  Maps: GoogleMapsSdk (routing, geocoding)     │
│  Local: CacheDao (Drift - ride history)       │
└─────────────────────────────────────────────┘
```

### Key Design: LocationBloc (Critical for Ride-Sharing)

```
┌──────────────────────────────────────────────┐
│              LocationBloc                     │
├──────────────────────────────────────────────┤
│  Responsibilities:                             │
│  - Subscribe to GPS updates                   │
│  - Manage location accuracy based on context: │
│    * Browsing map → medium accuracy, 10s      │
│    * Ride requested → high accuracy, 3s       │
│    * Ride in progress → high accuracy, 5s    │
│  - Emit LocationUpdated state                 │
│  - Provide current location to other BLoCs    │
│  - Handle permission denial gracefully        │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"Location is the most critical component in a ride-sharing app. I isolate it in a dedicated LocationBloc that manages GPS accuracy dynamically — medium accuracy when browsing (saves battery), high accuracy when a ride is active (precise tracking). The RideBloc subscribes to LocationBloc for pickup coordinates, and the TrackingBloc subscribes to the WebSocket for driver location. This separation ensures GPS failures don't block ride requests, and WebSocket failures don't block location display."*

---

## Step 6 — Data Flow (25–30 min)

### Ride Request Flow:

```
MapScreen (user confirms pickup + destination)
  → RideBloc.add(RequestRide(pickup, destination, rideType))
    → EstimateFareUseCase → GET /api/fare/estimate
      → Show fare estimate + ETA
    → User confirms → RequestRideUseCase
      → Generate idempotency_key (UUID)
      → POST /api/rides (with Idempotency-Key)
      → RideBloc emits RideRequested
      → Matching Service finds nearest driver (Redis geo query)
      → Server pushes via FCM to driver app
      → Driver accepts
      → Server sends: DRIVER_ASSIGNED via WebSocket
      → TrackingBloc starts tracking driver location
      → BLoC emits DriverAssigned (show driver info on map)
```

### Real-Time Tracking Flow:

```
TrackingBloc
  → RideWebSocket.subscribe(ride_id)
  → Server → Client events:
    - DRIVER_LOCATION { lat, lng, heading, speed }
    - RIDE_STATUS { status: EN_ROUTE | ARRIVED | STARTED | COMPLETING | COMPLETED }
    - ETA_UPDATE { eta_seconds }
  → On DRIVER_LOCATION:
    → Animate driver marker on map
    → Update ETA
  → On RIDE_STATUS:
    → Update UI (status banner)
    → If ARRIVED: show "Your driver has arrived" notification
    → If COMPLETED: navigate to payment + rating
```

### Fare Estimation Flow:

```
User enters destination
  → RideBloc.add(EstimateFare(pickup, destination, rideType))
    → EstimateFareUseCase
      → Google Maps API: get route, distance, duration
      → FareApi: POST /api/fare/estimate { distance, duration, ride_type }
      → Server calculates: base fare + (distance × rate) + (time × rate) + surge
      → Return fare range + ETA
      → BLoC emits FareEstimated
```

---

## Step 7 — Networking (30–35 min)

### Triple-Channel Strategy:

| Channel | Protocol | Use Case |
|---------|----------|----------|
| **REST** | HTTPS | Fare estimate, ride request, payment, ride history |
| **WebSocket** | WSS | Driver location (real-time), ride status changes |
| **FCM/APNS** | Push | Background: driver assigned, driver arrived, ride completed |
| **Google Maps** | HTTPS | Geocoding, routing, ETA, places autocomplete |

### WebSocket Events:

```
Client → Server:
  - SUBSCRIBE_RIDE { ride_id }
  - UNSUBSCRIBE_RIDE { ride_id }

Server → Client:
  - DRIVER_ASSIGNED { ride_id, driver: {name, photo, rating, vehicle, plate} }
  - DRIVER_LOCATION { ride_id, lat, lng, heading, speed }
  - RIDE_STATUS { ride_id, status, timestamp }
  - ETA_UPDATE { ride_id, eta_seconds }
  - RIDE_CANCELLED { ride_id, reason }
```

### Location Update Strategy (Battery-Critical):

| App State | GPS Accuracy | Update Interval | Rationale |
|-----------|-------------|-----------------|-----------|
| Map browsing | Medium | 10s | Good enough for pickup selection |
| Ride requested, waiting for driver | High | 5s | Need accurate pickup location |
| Ride in progress | High | 5s | Track driver, but driver's location is primary |
| App backgrounded | None | — | Rely on FCM for status updates |
| No active ride | Low | 30s | Periodic location for nearby drivers estimate |

### Retry & Timeout:
- REST: connect 8s, receive 10s, max 3 retries
- WebSocket: reconnect backoff 1s → 2s → 4s → 8s → 15s (cap)
- Ride request: timeout 10s → if no driver matched, show "no drivers available"

### Idempotency:
```
POST /api/rides
Headers:
  Idempotency-Key: <UUID>
Body: { pickup, destination, ride_type, payment_method }
```
- Prevents duplicate ride requests on retry
- UUID stored locally until ride is confirmed

---

## Step 8 — Offline Support & Sync (35–40 min)

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| Browse map | Show cached map tiles. Use last known location. |
| Set destination | Use Google Maps offline cache (if available). Otherwise, allow text input. |
| Estimate fare | **Block**: Requires server + Maps API. Show "Connect to internet." |
| Request ride | **Block**: Requires server. Show "No internet connection." |
| Track active ride | **Block**: Requires WebSocket. Show last known status + "reconnecting..." |
| View ride history | Show cached ride history from Drift. Mark "offline." |
| Payment | **Block**: Requires server. |

### Minimal Offline Strategy:
> Ride-sharing is inherently **online-only** for core functionality. Offline support is limited to:
> - Map tiles caching (Google Maps handles this)
> - Ride history caching
> - User profile caching
> - Last known location for map initialization

### Cache Strategy:
```
Ride history: Network-first with cache fallback (cache in Drift, 7-day TTL)
User profile: Cache-first (rarely changes)
Map tiles: Google Maps built-in cache (offline areas)
Location: Always cached (last known location for instant map load)
```

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: location + maps + auth. Ride history deferred. Analytics deferred 3s. Target: <2s to interactive map. |
| **UI** | Map rendering: use lite mode when possible. Driver marker: custom icon with rotation (heading). `RepaintBoundary` on bottom sheet. Animate marker smoothly between positions (interpolation). |
| **Network** | WebSocket: single connection, multiplexed. REST: compress (gzip). Fare estimate: cache for 2 min (same route). Google Maps: cache geocoding results. |
| **Memory** | Map: limit to current viewport markers. Ride history: lazy load (20/page). Clear map markers when navigating away from tracking. |
| **Battery** | GPS: dynamic accuracy (see table above). WebSocket: only active during ride request/tracking. Background: FCM only. No background location for rider app (unlike driver app). |
| **Maps** | Use vector tiles (lighter). Preload route polyline. Throttle camera animation (don't follow every 3s update — interpolate). |

### Driver Marker Animation (Critical UX):

```dart
// Don't jump marker to new position — animate smoothly
// Interpolate between old and new position over 3s (until next update)
void updateDriverMarker(LatLng newLocation) {
  final oldLocation = currentDriverLocation;
  final animationController = AnimationController(
    duration: Duration(seconds: 3),
    vsync: this,
  );
  
  // Tween between old and new lat/lng
  // Update marker position on each animation frame
  // Rotate marker based on heading
}
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2 with PKCE. Phone OTP for registration. Tokens in Keystore/Keychain. |
| **API Security** | SSL pinning on all endpoints. TLS 1.2+. API key rotation via remote config. |
| **Payment** | Tokenized via payment SDK (Razorpay/Stripe). Wallet balance from server, never cached locally. No card data on device. |
| **Location Privacy** | Only track location when app is foregrounded (rider app). Don't share rider location with driver until ride is accepted. Clear location history on logout. |
| **Phone Number** | Masked number between rider and driver (privacy). Real number not exposed. |
| **Local DB** | Encrypt ride history (SQLCipher). No payment data stored locally. |
| **Fraud Prevention** | Device fingerprinting. Rate limiting on ride requests. Detect GPS spoofing (compare network location vs GPS). |

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Matching Service: Redis GEO queries (`GEORADIUS` for nearby drivers)
- WebSocket Gateway: horizontally scalable, sticky sessions per ride
- Kafka: ride events → billing, analytics, surge pricing computation
- Database: PostgreSQL sharded by region for rides
- Google Maps: cache geocoding results in Redis (5 min TTL)

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| WebSocket disconnects during ride | Auto-reconnect. Show "reconnecting..." Fallback: REST poll ride status every 5s. |
| GPS unavailable | Prompt user to enable location. Use network-based location as fallback. |
| No drivers available | Show "high demand" message. Offer surge pricing or wait estimate. |
| Driver cancels | Server matches new driver automatically. Notify rider. Update map. |
| Payment fails after ride | Mark ride as "payment pending." Allow retry. Don't block future rides (soft block after 3 unpaid). |
| Server timeout on ride request | Retry with idempotency key. If still failing, show error + retry button. |
| App killed during ride | FCM push with ride status. On reopen: resume WebSocket, fetch current status, resume tracking. |
| Google Maps API quota exceeded | Fallback to OpenStreetMap. Cache more aggressively. |
| Surge pricing changes mid-ride | Only applies to new requests. Active ride fare is locked at request time. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| WebSocket vs polling for tracking | WebSocket: real-time, efficient. Chose WebSocket with REST fallback. |
| Dynamic GPS accuracy | Battery-efficient, but slight delay in accuracy switch. Acceptable — saves 30-40% battery. |
| Google Maps vs OpenStreetMap | Google Maps: better routing/ETA, expensive at scale. OSM: free but less accurate. Chose Google for MVP, abstract behind interface for future swap. |
| Online-only core | Simpler, but no offline ride requests. Acceptable — ride-sharing inherently needs network. |
| Driver marker interpolation | Smoother UX, but marker position may lag 1-2s behind actual. Acceptable for rider UX. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Fare calculation, location accuracy logic, BLoC state transitions, ride status mapping |
| **Integration** | WebSocket tracking with mock server, Google Maps integration, payment SDK |
| **E2E** | Full flow: set location → estimate fare → request ride → track → complete → pay → rate |
| **Performance** | Map scroll smoothness, marker animation, WebSocket reconnect time, battery drain over 1hr |
| **Observability** | Crashlytics, Firebase Performance (API latency, WS latency), custom events (match time, ride completion rate, payment failure rate) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. Location management is isolated in a dedicated LocationBloc with dynamic GPS accuracy for battery optimization. Ride requests use REST with idempotency keys. Real-time tracking uses WebSocket with REST polling fallback. The matching service uses Redis GEO queries for nearest-driver lookup. Payment is tokenized — no card data on device. Performance focuses on battery efficiency (dynamic GPS accuracy, WebSocket only during active rides) and smooth map UX (marker interpolation). Security includes OAuth2, SSL pinning, masked phone numbers, and encrypted local DB. Core functionality is online-only — ride-sharing inherently requires real-time server communication."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
