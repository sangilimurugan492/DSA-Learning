# Structured Interview Answer: Design a Ride-Sharing App

> **Question**: *"Design a mobile ride-sharing application like Uber/Lyft/Ola."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Rider app, driver app, or both?
- Real-time driver location tracking required?
- Dynamic pricing / surge pricing in scope?
- Payment integration?
- Ride history and receipts?
- Rating system?

**Assumed:** Flutter, rider app (mention driver app briefly), real-time tracking yes, surge pricing yes, payments yes, ratings yes.

---

## Step 2 — Define Scope

```
IN SCOPE: Rider app — request ride, real-time driver tracking, fare estimate, payment, ride history, ratings, surge pricing
OUT OF SCOPE: Driver app (mention briefly), admin dashboard, driver onboarding
```

---

## Step 3 — Constraints

```
Functional: Request ride, match driver, track in real-time, dynamic pricing, pay, rate
Non-Functional: 30M users, ~3M concurrent at peak, < 3s driver match, < 3s GPS update, battery-efficient location
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Rider App   │
                    │   Flutter    │
                    └──────┬───────┘
                           │
              REST (ride request) + WebSocket (tracking) + FCM
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
  Ride Service       Matching Service       Tracking Service
  (create, complete)  (find nearest driver)  (WebSocket, Redis Geo)
    │                      │                      │
  Pricing Service     Payment Service       Notification Service
  (surge calc)        (Stripe)               (FCM)
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis Geo  │
                    │ + Kafka      │
                    └──────────────┘
```

> "Redis Geo stores driver locations for O(log N) proximity search. The matching service uses GEORADIUS to find the nearest available drivers within a radius."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  MapScreen, RideRequest,     │
│  DriverTracking, Payment     │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  MapBloc, RideBloc,         │
│  TrackingBloc, PaymentBloc  │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  RequestRideUseCase          │
│  EstimateFareUseCase         │
│  TrackRideUseCase            │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  RideRepository              │
│  TrackingRepository (WS)     │
│  LocationRepository (GPS)    │
└──────────┬───────────┬──────┘
           ↓           ↓
   REST + WebSocket   Local DB
```

---

## Step 6 — Data Flow

### Requesting a Ride:
```
User sets pickup & destination on map
  ↓
EstimateFareUseCase → GET /fare/estimate?from=X&to=Y
  ↓
Server: calculate distance, surge multiplier → return fare estimate
  ↓
User confirms → POST /rides (idempotency key)
  ↓
Server: match nearest driver (Redis GEORADIUS) → notify driver
  ↓
WebSocket: /ws/ride/{ride_id} → subscribe for updates
  ↓
Driver accepts → status = ACCEPTED → UI shows driver info + ETA
  ↓
Driver arrives → status = ARRIVED → UI shows "Your driver is here"
  ↓
Trip starts → status = IN_PROGRESS → UI tracks driver on map
  ↓
Trip ends → status = COMPLETED → payment charged → show receipt + rating
```

### GPS Location Management (Battery-Critical):
```
GPS accuracy adapts to ride state:

  IDLE (browsing):     BALANCED power (coarse, ~1km)
  REQUESTING:          HIGH accuracy (fine, ~10m)
  DRIVER_APPROACHING:  HIGH accuracy (fine, ~10m, 3s updates)
  IN_PROGRESS:         HIGH accuracy (fine, ~10m, 3s updates)
  COMPLETED:           Stop GPS updates entirely

This adaptive GPS strategy saves battery when idle while
providing precise tracking during active rides.
```

---

## Step 7 — Networking

```
REST:
  POST /rides (idempotency key)
  GET /rides/{id} (ride details, receipt)
  POST /payments (tokenized)
  connectTimeout: 8s (fast — users are waiting)

WebSocket:
  /ws/ride/{ride_id}
  Push: driver location (lat, lng, heading) every 3s
  Push: status changes (ACCEPTED, ARRIVED, IN_PROGRESS, COMPLETED)
  Heartbeat: 20s
  Auto-reconnect with sync via REST on reconnect

FCM:
  Driver assigned, driver arrived, trip completed
  Deep link to ride tracking screen
```

---

## Step 8 — Offline Support

```
Ride request requires network (can't match driver offline)
  → If offline: show "No internet" + retry button

Cached data:
  → Ride history (local DB, cache-first)
  → Saved locations (Home, Work) — local DB
  → Payment methods — local DB (encrypted)

GPS continues offline:
  → User's location still tracked locally
  → On reconnect, sync missed location data if in active ride
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load saved locations from local DB, show map immediately |
| **UI** | Google Maps SDK, smooth marker animation (interpolate between GPS points), 60fps target |
| **Network** | WebSocket for tracking (not polling), compress ride data |
| **Memory** | Map controller lifecycle management, clear tracking streams on ride end |
| **Battery** | Adaptive GPS accuracy (see above), WebSocket closed when no active ride |

> "Battery is the #1 concern for ride-sharing apps because GPS is always running. I use adaptive GPS accuracy — coarse when idle, fine when tracking. I also close the WebSocket when no active ride is in progress."

---

## Step 10 — Security

```
Auth: OAuth2, tokens in Keystore/Keychain
Payment: Stripe SDK (tokenized, PCI-compliant)
Location: Only share rider location during active ride request/trip
Network: TLS 1.2+, certificate pinning
Data: SQLCipher for payment method storage
Privacy: Don't log exact GPS coordinates in analytics (privacy concern)
```

---

## Step 11 — Scalability

- Redis Geo for driver proximity (GEORADIUS — O(log N))
- Kafka for ride events (ride_created, driver_assigned, completed) → downstream consumers
- WebSocket sticky sessions (load balancer affinity)
- Read replicas for ride history
- Dynamic pricing: precompute surge zones, cache in Redis (refresh every 5 min)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| No driver available | Show "High demand" + surge pricing or "Try again later" |
| WebSocket disconnect | Reconnect + sync ride status via REST |
| Driver cancels | Re-match → find new driver, notify rider |
| GPS lost (rider) | Use last known location, show "Locating..." |
| Payment fails | Show error, allow retry, don't release rider until paid |
| App killed during ride | FCM wakes app, restore ride state from server on relaunch |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Driver matching | Redis GEORADIUS | PostGIS | Redis is faster for real-time proximity, in-memory |
| Tracking | WebSocket | Polling | Real-time, less battery, bidirectional |
| GPS | Adaptive accuracy | Always high | Battery savings critical for ride-sharing |
| Map | Google Maps SDK | OpenStreetMap | Proven, traffic, routing — worth the cost |
| Pricing | Redis-cached surge | Real-time calculation | Precompute reduces API latency |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Fare calculation, surge logic, GPS accuracy transitions |
| Integration | WebSocket tracking flow, ride request → match → complete |
| E2E | Full ride flow (mock driver) |
| Observability | Driver match time p95, WebSocket uptime, GPS accuracy distribution |
| Battery | Measure battery drain per hour during active ride vs idle |
