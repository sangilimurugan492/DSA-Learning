# Structured Interview Answer: Design a Food Delivery App

> **Question**: *"Design a mobile food delivery application like Uber Eats/Swiggy/Zomato."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Android, iOS, or Flutter?
- Are we designing customer app, restaurant app, or delivery partner app?
- Real-time order tracking required?
- Payment integration in scope?
- Restaurant search and filtering?
- Push notifications for order status?

**Assumed answers:** Flutter, customer app (mention other apps briefly), real-time tracking yes, payments yes, search yes, push yes.

---

## Step 2 — Define Scope

```
IN SCOPE: Customer app — browse restaurants, place order, real-time tracking, payment, push notifications
OUT OF SCOPE: Restaurant partner app, delivery partner app (mention architecture briefly)
```

---

## Step 3 — Constraints

```
Functional: Browse restaurants, search/filter, place order, track delivery in real-time, pay
Non-Functional: 20M users, ~2M concurrent at peak (lunch/dinner), < 1s restaurant list load, < 5s GPS update interval
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
              REST (orders, menu) + WebSocket (tracking) + FCM (push)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
 Restaurant Service   Order Service         Tracking Service
    │                      │                  (WebSocket)
 Search Service       Payment Service           │
 (Elasticsearch)      (Stripe/Razorpay)    GPS Store (Redis Geo)
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis      │
                    │ + S3 (images)│
                    └──────────────┘
```

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  RestaurantList, MenuScreen, │
│  OrderTracking, CartScreen   │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  RestaurantBloc, OrderBloc, │
│  TrackingBloc, CartBloc      │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  SearchRestaurantsUseCase    │
│  PlaceOrderUseCase           │
│  TrackOrderUseCase            │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  RestaurantRepository        │
│  OrderRepository             │
│  TrackingRepository (WS)     │
└──────────┬───────────┬──────┘
           ↓           ↓
    REST + WebSocket  Local DB
    (Dio)             (Drift)
```

---

## Step 6 — Data Flow

### Restaurant Browsing (Cache-First):
```
User opens app
  ↓
Check local DB for nearby restaurants (cached)
  ├── Hit → Show immediately + background refresh
  └── Miss → GET /restaurants?lat=X&lng=Y
              ↓
              Cache in local DB (TTL: 10 min)
              ↓
              Show to user
```

### Order Placement:
```
User places order
  ↓
OrderBloc → PlaceOrderUseCase
  ↓
POST /api/orders (with idempotency key)
  ↓
Server: validate → charge payment → assign restaurant → assign driver
  ↓
Return order_id + estimated delivery time
  ↓
Subscribe to WebSocket for tracking updates
  ↓
UI shows "Order confirmed" + tracking screen
```

### Real-Time Tracking:
```
WebSocket: /ws/track/{order_id}
  ↓
Server pushes updates every 5s:
  { driver_lat, driver_lng, status, eta }
  ↓
TrackingBloc emits UpdateLocation
  ↓
UI updates driver marker on map (animated)
  ↓
When status = DELIVERED → show "Delivered!" + stop tracking
```

| Data | Strategy | TTL |
|------|----------|-----|
| Restaurant list | Cache-first | 10 min |
| Menu items | Cache-first | 30 min |
| Order status | WebSocket (real-time) | N/A |
| Order history | Cache-first | 5 min |
| Cart | Local-first | N/A |

---

## Step 7 — Networking

```
REST:
  connectTimeout: 10s, receiveTimeout: 15s
  Retry: 3x exponential backoff for 5xx
  Idempotency key on POST /orders

WebSocket (tracking):
  /ws/track/{order_id}
  Heartbeat: 25s
  Auto-reconnect with backoff
  On reconnect: GET /orders/{id}/status (sync missed updates)

FCM Push:
  Order confirmed, driver assigned, order picked up, order delivered
  Deep link to tracking screen on tap
```

---

## Step 8 — Offline Support & Sync

```
User adds items to cart while offline
  ↓
Cart saved locally (Drift)
  ↓
App shows "You're offline — cart saved"
  ↓
Network restored → user can place order
  ↓
Cart synced to server with idempotency key

Note: Can't place order offline (payment + restaurant confirmation needed)
      But cart persists across offline → online transition
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load nearby restaurants from local DB first, then refresh |
| **UI** | Map rendering with marker animation, ListView.builder for menus |
| **Network** | Compress menu images (WebP), cursor pagination for restaurant list |
| **Memory** | Map controller disposed on screen exit, image cache per restaurant |
| **Battery** | GPS only during active tracking (not whole app), WebSocket closed after delivery |

> "GPS tracking is battery-intensive. I only request location updates during active order tracking. Once delivered, the WebSocket closes and location updates stop."

---

## Step 10 — Security

```
Auth: OAuth2, tokens in Keystore/Keychain
Payment: Stripe/Razorpay SDK (tokenized, PCI-compliant)
Network: TLS 1.2+, certificate pinning
Data: EncryptedSharedPreferences for saved addresses
Address: Not logged in analytics (PII)
```

---

## Step 11 — Scalability

- Restaurant search: Elasticsearch (filter by cuisine, rating, distance)
- Redis Geo for driver location (GEORADIUS for nearby drivers)
- Cursor pagination for restaurant list
- Image CDN for food/restaurant photos
- Read replicas for restaurant/menu reads (read-heavy)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Network down | Show cached restaurants, cart persists locally |
| WebSocket disconnect | Reconnect + sync missed tracking updates via REST |
| Payment fails | Show error, allow retry with different method |
| Restaurant rejects order | Show "Restaurant unable to fulfill" + refund |
| No driver available | Show extended ETA or "High demand" message |
| GPS lost | Show last known driver position + "Reconnecting..." |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Tracking | WebSocket | Polling every 5s | WebSocket is real-time, less battery than polling |
| Restaurant search | Elasticsearch | PostgreSQL LIKE | Fast full-text + geo search, faceted filtering |
| Cart offline | Local persistence | Server-only | Cart survives offline; can't place order offline anyway |
| Map | Google Maps SDK | Custom map | Proven rendering, markers, routing — don't reinvent |
| GPS | Only during tracking | Always on | Battery conservation — GPS only when needed |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Order calculation, cart logic, status transitions |
| Integration | Repository + API mock, WebSocket tracking flow |
| E2E | Browse → Add to cart → Place order → Track delivery |
| Observability | API latency, WebSocket connection rate, order funnel analytics |
