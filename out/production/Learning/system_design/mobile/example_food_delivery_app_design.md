# Example: Design a Food Delivery App (Swiggy/Zomato-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile food delivery application like Swiggy/Zomato for 20 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter cross-platform."*
2. Core features? → *"Restaurant listing, menu browsing, cart, checkout, real-time order tracking, delivery."*
3. Real-time delivery tracking? → *"Yes, live driver location on map."*
4. Payments? → *"Yes, in-app payment + COD."*
5. Push notifications? → *"Yes, order status updates."*
6. Offline support? → *"Limited — cart should persist offline."*
7. Scale? → *"20M users, ~2M daily orders."*

**Summary:**
- **Functional**: Restaurant search/listing, menu, cart, checkout, payment, real-time order tracking, delivery agent assignment, push notifications, ratings
- **Non-functional**: Real-time driver tracking (<3s update), <2s screen load, 20M users, 2M orders/day

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Restaurant discovery (search, filter, sort)
- Menu browsing with customization
- Cart with offline persistence
- Checkout & payment
- Real-time order tracking (map + driver location)
- Push notifications (order status)
- Delivery agent app (mention, not deep dive)

**Out of scope:**
- Restaurant partner app (admin panel)
- Subscription/membership
- Group ordering

---

## Step 3 — Identify Constraints (5 min)

```
20M users, ~2M daily orders
Peak: lunch (12-2pm) + dinner (7-10pm) → 5x avg traffic
Restaurants: ~200K listed
Avg order value: ₹350
Concurrent drivers during peak: ~200K
Driver location updates: every 5s per driver
Target: <2s screen load, <3s location update latency
Devices: 70% Android, many low-end (2GB RAM)
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Customer App (Flutter)                     │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │Restaurant│ │ Menu   │ │ Checkout │ │ Order Track │ │
│  │ Listing  │ │ Browse │ │ Payment  │ │ Map + Driver│ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   WebSocket   Local DB                    │
│   (catalog,   (live driver  (Drift: cart,             │
│    orders)    tracking)      cache, prefs)             │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + Load Balancer
        │ + WS Gateway │
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │Restaurant│ │  Order   │ │ Tracking  │
  │ Service │ │ Service  │ │ Service   │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │PostgreSQL│ │PostgreSQL│ │   Redis    │
  │+ Redis  │ │(orders) │ │(driver loc)│
  │(catalog)│ └─────────┘ └───────────┘
       │          │
       │     ┌────▼────┐
       │     │ Payment │
       │     │ Gateway │
       │     └─────────┘
       │
  ┌────▼────┐
  │Elastic  │
  │search   │
  │(search) │
  └─────────┘

Kafka → Order events (placed, accepted, preparing, dispatched, delivered)
FCM/APNS → Push notifications
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: RestaurantList, MenuPage,          │
│           CartPage, CheckoutPage,            │
│           OrderTrackingMap                    │
│  BLoCs: RestaurantBloc, MenuBloc,            │
│         CartBloc, OrderBloc, TrackingBloc    │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - SearchRestaurantsUseCase                   │
│  - GetMenuUseCase                             │
│  - ManageCartUseCase                          │
│  - PlaceOrderUseCase                          │
│  - TrackOrderUseCase                          │
│  Repositories (abstract):                     │
│  - RestaurantRepository                       │
│  - CartRepository                             │
│  - OrderRepository                            │
│  - TrackingRepository                         │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: RestaurantApi, OrderApi (Dio)       │
│  Real-time: TrackingWebSocket (driver loc)    │
│  Local: CartDao, CacheDao (Drift)            │
│  Location: Geolocator (user location)         │
└─────────────────────────────────────────────┘
```

### Modularization:

```
:app                    ← Shell, navigation, DI
:core:network           ← Dio, interceptors, WebSocket
:core:database          ← Drift setup
:core:location          ← Geolocator wrapper
:core:maps              ← Google Maps wrapper
:feature:restaurant     ← Search, listing, detail
:feature:menu           ← Menu browsing, customization
:feature:cart           ← Cart, offline persistence
:feature:checkout       ← Payment, address
:feature:tracking       ← Live order tracking, map
:feature:orders         ← Order history
```

### Why (Lead-Level Justification):

> *"I separate concerns into Presentation, Domain, and Data layers. The TrackingBloc subscribes to a WebSocket stream for driver location updates, while the OrderBloc uses REST for order lifecycle. This separation means tracking can fail independently without affecting order placement. The CartRepository persists to Drift so the cart survives app kills and works offline."*

---

## Step 6 — Data Flow (25–30 min)

### Restaurant Search Flow:

```
HomePage
  → RestaurantBloc.add(SearchRestaurants(query, filters))
    → SearchRestaurantsUseCase.call()
      → RestaurantRepository.search()
        → Check Drift cache (TTL: 5 min for listing)
          → Cache valid? 
            → YES: return cached list
            → NO: call RestaurantApi
              → Update Drift cache
              → return fresh data
        → BLoC emits RestaurantListLoaded
```

### Order Placement Flow:

```
CheckoutPage
  → OrderBloc.add(PlaceOrder(cart, address, payment))
    → PlaceOrderUseCase.call()
      → Generate idempotency_key (UUID)
      → OrderRepository.placeOrder()
        → POST /api/orders (with Idempotency-Key)
        → On success:
            → Clear cart (Drift)
            → Navigate to OrderTrackingPage
            → Start WebSocket connection for tracking
            → BLoC emits OrderPlaced
        → On failure:
            → Keep cart intact
            → Show error with retry
```

### Real-Time Tracking Flow:

```
OrderTrackingPage
  → TrackingBloc.add(StartTracking(orderId))
    → TrackingRepository.connect(orderId)
      → WebSocket: subscribe to order:{orderId}/location
      → On message (driver lat/lng, order status):
        → Update map marker position
        → Animate camera to driver location
        → Update ETA
        → If status changed (e.g., "dispatched"):
          → Show notification banner
          → BLoC emits OrderStatusChanged
      → On disconnect:
        → Auto-reconnect with backoff
        → Fallback: poll REST every 10s
```

---

## Step 7 — Networking (30–35 min)

### Dual-Channel Strategy:

| Channel | Protocol | Use Case |
|---------|----------|----------|
| **REST** | HTTPS | Restaurant catalog, menu, orders, payment |
| **WebSocket** | WSS | Live driver location, order status updates |
| **FCM/APNS** | Push | Background order status notifications |

### REST API Design:

```
GET  /api/restaurants?lat=&lng=&query=&filter=  → Search (paginated)
GET  /api/restaurants/{id}/menu                  → Menu items
POST /api/orders                                  → Place order (idempotency key)
GET  /api/orders/{id}                            → Order details
GET  /api/orders/{id}/status                     → Order status (REST fallback)
POST /api/payments                                → Process payment
```

### WebSocket Events:

```
Client → Server:
  - SUBSCRIBE_TRACKING { order_id }
  - UNSUBSCRIBE_TRACKING { order_id }

Server → Client:
  - DRIVER_LOCATION { order_id, lat, lng, heading, eta }
  - ORDER_STATUS { order_id, status, timestamp }
  - DRIVER_ASSIGNED { order_id, driver_name, rating, photo }
```

### Retry & Timeout:
- REST: connect 10s, receive 15s, max 3 retries with backoff
- WebSocket: reconnect backoff 1s → 2s → 4s → 8s → 15s (cap)
- Fallback: if WebSocket fails 3x, switch to REST polling every 10s

### Idempotency:
```
POST /api/orders
Headers:
  Idempotency-Key: <UUID>
Body: { restaurant_id, items, address_id, payment_method }
```
- Prevents duplicate orders on network retry
- UUID stored locally until order confirmation

---

## Step 8 — Offline Support & Sync (35–40 min)

### Cart Offline Persistence:

```sql
CREATE TABLE cart_items (
  id INTEGER PRIMARY KEY,
  restaurant_id TEXT,
  menu_item_id TEXT,
  name TEXT,
  quantity INTEGER,
  price REAL,
  customizations TEXT,  -- JSON
  added_at INTEGER
);
```

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| Add to cart | Save to Drift immediately. Show in cart. |
| Modify quantity | Update Drift. No API call needed (cart is client-side). |
| Place order | **Block**: Require network. Show "Connect to internet to place order." |
| View order tracking | **Block**: Require network for live tracking. Show last known status from cache. |
| Browse restaurants | Show cached restaurant list (5 min TTL). Mark "showing cached results." |

### Sync Strategy:
- Cart is **client-authoritative** until order is placed
- No sync queue needed — cart lives entirely in local DB
- Order placement is online-only (requires payment + server validation)
- Order status: cache last known status in Drift, refresh on reconnect

### Location Caching:
- User's last known location cached in Drift
- On app launch: use cached location for restaurant search while GPS acquires
- GPS update: `LocationAccuracy.medium` for battery efficiency (not high accuracy for listing)

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: auth + location + Drift. Restaurant list loads with cached location first, refines with GPS. Defer analytics. |
| **UI** | `ListView.builder` for restaurant list. Image caching with `cached_network_image`. `const` widgets. Shimmer placeholders while loading. `RepaintBoundary` on map. |
| **Network** | Cursor-based pagination (20 restaurants/page). Compress responses (gzip). ETag for menu caching. Batch driver location updates (don't render every 5s frame if 1s apart). |
| **Memory** | Restaurant images: 100MB cache. Menu page: unload restaurant list from memory. Map: limit markers (only current driver). |
| **Battery** | GPS: `medium` accuracy for listing, `high` only during tracking. WebSocket only active during active order. Background: FCM only, no polling. Driver location updates: throttle to every 3s on screen (server sends every 5s). |
| **Maps** | Use Google Maps lite mode for list view. Full map only on tracking page. Cluster nearby restaurant pins. |

### Map Performance:
```
Driver marker update:
  → Receive location (every 5s from WebSocket)
  → Animate marker from old position to new (smooth animation)
  → Don't re-render entire map — only update marker position
  → Throttle camera movement (follow driver, but don't jerk)
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2 with PKCE. Access token (15 min) + refresh token (30 days) in Keystore/Keychain. |
| **API Security** | SSL pinning on API gateway + WebSocket gateway. TLS 1.2+. |
| **Payment** | Never handle card data directly. Use payment SDK (Razorpay/Stripe) with tokenization. Payment token sent to backend. |
| **Location Privacy** | Only send location when necessary (restaurant search, delivery address). Don't track background location. Clear location cache on logout. |
| **Local DB** | Encrypt cart with address info using SQLCipher. No payment data stored locally. |
| **Secure Logging** | No PII, no payment details, no addresses in logs. Debug-only. |
| **Address Security** | Delivery addresses encrypted at rest. Mask address in logs. |

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Restaurant search: Elasticsearch for full-text + geo queries
- Redis: cache restaurant listings by geo-grid (lat/lng buckets)
- WebSocket Gateway: horizontally scalable with connection affinity per order
- Kafka: order lifecycle events → notifications, analytics, delivery assignment
- Database: PostgreSQL with read replicas for catalog, sharded by region for orders

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| WebSocket disconnects during tracking | Auto-reconnect. Fallback to REST polling (10s interval). Show "reconnecting..." indicator. |
| GPS unavailable | Use last known cached location. Show manual location picker. |
| Payment fails | Order not placed. Keep cart intact. Show payment error, allow retry with different method. |
| Restaurant goes offline after order | Server-side validation: reject if restaurant closed. If accepted but kitchen closed mid-order → refund + notification. |
| Driver unassigned for long time | Server-side timeout: reassign. Client shows "finding nearest driver..." with ETA estimate. |
| App killed during tracking | FCM push notification with order status. On app reopen: resume WebSocket, fetch current status. |
| Network slow during peak | Show cached restaurant list. Skeleton loading states. Graceful degradation. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| WebSocket vs polling for tracking | WebSocket: real-time, lower battery than frequent polling. Chose WebSocket with REST fallback. |
| Client-authoritative cart | Simple, offline-capable. But cart may have stale prices → server validates on order placement. |
| Medium GPS accuracy for listing | Battery savings, but slightly less precise restaurant distance. Acceptable. |
| Google Maps vs custom map | Google Maps: faster to implement, reliable. Custom: cheaper at scale, more control. Chose Google Maps for MVP. |
| REST polling fallback | Simpler than WebSocket recovery, but 10s latency vs real-time. Only used when WebSocket fails 3x. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Use Cases, Repository, BLoC, cart calculations, location utilities |
| **Integration** | WebSocket tracking with mock server, Drift cart persistence, payment SDK integration |
| **E2E** | Full flow: search → menu → cart → checkout → payment → tracking → delivered |
| **Performance** | Restaurant list scroll (1000 items), map marker animation smoothness, WebSocket reconnect time |
| **Observability** | Crashlytics, Firebase Performance (API latency, WS connection time), custom events (order placement success rate, tracking reconnect count) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. Restaurant discovery is cache-first with Elasticsearch backend and geo-based Redis caching. Cart is client-authoritative, persisted in Drift for offline support. Order placement is online-only with idempotency keys to prevent duplicates. Real-time tracking uses WebSocket with auto-reconnect and REST polling fallback. Payment is tokenized via SDK — no raw card data on device. Performance is optimized with medium GPS accuracy, image caching, and WebSocket only active during active orders. Security includes OAuth2, SSL pinning, encrypted local DB, and location privacy controls."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
