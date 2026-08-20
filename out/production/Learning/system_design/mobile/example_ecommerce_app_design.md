# Example: Design an E-Commerce Mobile App

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile e-commerce application like Amazon/Samsung Shop for 10 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Are we designing Android, iOS, or cross-platform? → *"Both, but Flutter cross-platform."*
2. Do we need offline support? → *"Yes, cart should work offline."*
3. Are payments in scope? → *"Yes, in-app payment."*
4. Real-time order tracking? → *"Yes, push notifications + live status."*
5. Scale? → *"10M users, ~1M DAU."*
6. Push notifications? → *"Yes, for order updates and promos."*

**Summary on whiteboard:**
- **Functional**: Browse products, search/filter, cart, checkout, payment, order tracking, push notifications
- **Non-functional**: Offline cart, <2s screen load, secure payments, 10M users, Flutter cross-platform

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Product browsing (catalog, search, detail)
- Cart management (offline-capable)
- Checkout & payment
- Order history & tracking
- Push notifications

**Out of scope (for this interview):**
- Seller portal
- Reviews & ratings (mention as future)
- Recommendation engine (mention as future)

---

## Step 3 — Identify Constraints (5 min)

```
10M total users, ~1M DAU
~50K concurrent during peak (sale events)
Product catalog: ~10M products
Avg session: 10 min
Read-heavy: browsing >> purchasing (100:1)
Target: <2s screen load, <500ms API response
Devices: 60% low-end Android, must support 2GB RAM
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌──────────────────────────────────────────────────────┐
│                   Mobile App (Flutter)                │
│  ┌─────────┐ ┌─────────┐ ┌──────────┐ ┌───────────┐ │
│  │ Product │ │  Cart   │ │ Checkout │ │  Orders   │ │
│  │ Feature │ │ Feature │ │ Feature  │ │  Feature  │ │
│  └────┬────┘ └────┬────┘ └─────┬────┘ └─────┬─────┘ │
│       └───────────┴───────────┴─────────────┘       │
│                    │                                  │
│              API Client (Dio)                         │
│              Local DB (Drift)                         │
│              Sync Queue (WorkManager)                  │
└──────────────────────┬───────────────────────────────┘
                       │ HTTPS
                ┌──────▼───────┐
                │ API Gateway  │ (Auth, rate limit, routing)
                └──────┬───────┘
           ┌──────────┼──────────┐
           ▼          ▼          ▼
     ┌──────────┐ ┌────────┐ ┌──────────┐
     │ Product  │ │  Cart  │ │  Order   │
     │ Service  │ │Service │ │ Service  │
     └─────┬────┘ └───┬────┘ └─────┬────┘
           │          │            │
     ┌─────▼────┐ ┌───▼────┐ ┌────▼─────┐
     │PostgreSQL│ │ Redis  │ │PostgreSQL│
     │ + Redis  │ │(Cart)  │ │(Orders)  │
     └──────────┘ └────────┘ └──────────┘
                       │
                 ┌─────▼─────┐
                 │ Payment   │
                 │ Gateway   │
                 └───────────┘

CDN ← Product images, static assets
FCM/APNS ← Push notifications
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────┐
│              Presentation                │
│  ┌─────────────────────────────────┐    │
│  │  Widgets (ProductList, CartPage)│    │
│  └──────────────┬──────────────────┘   │
│                 ↓                        │
│  ┌─────────────────────────────────┐    │
│  │  BLoC (ProductBloc, CartBloc)   │    │
│  │  Events → States                │    │
│  └──────────────┬──────────────────┘   │
└─────────────────┼───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│                Domain                    │
│  ┌─────────────────────────────────┐    │
│  │  Use Cases                       │    │
│  │  - GetProductsUseCase            │    │
│  │  - AddToCartUseCase              │    │
│  │  - PlaceOrderUseCase             │    │
│  └──────────────┬──────────────────┘   │
│                 ↓                        │
│  ┌─────────────────────────────────┐    │
│  │  Repository Interfaces           │    │
│  │  - ProductRepository (abstract)  │    │
│  │  - CartRepository (abstract)     │    │
│  └──────────────┬──────────────────┘   │
└─────────────────┼───────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│                 Data                     │
│  ┌────────────────┐  ┌────────────────┐ │
│  │ Remote          │  │ Local           │ │
│  │ - ProductApi    │  │ - ProductDao    │ │
│  │ - CartApi       │  │ - CartDao       │ │
│  │ - OrderApi      │  │ - OrderDao      │ │
│  │   (Dio)         │  │   (Drift)       │ │
│  └────────────────┘  └────────────────┘ │
│  ┌─────────────────────────────────┐    │
│  │  Repository Implementations      │    │
│  │  - ProductRepositoryImpl         │    │
│  │  - CartRepositoryImpl            │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"I use Clean Architecture with three layers — Presentation, Domain, and Data. This separation ensures business logic in Use Cases is independent of Flutter widgets and networking details. The Repository abstraction allows me to swap data sources (remote vs local) without touching the domain or UI layers. This improves testability — I can mock the repository and test Use Cases in isolation."*

### Modularization:

```
:app                    ← Shell, navigation, DI
:core:network           ← Dio, interceptors, retry logic
:core:database          ← Drift setup, DAOs
:core:ui                ← Shared widgets, theme
:core:auth              ← OAuth, token management
:feature:product        ← Browse, search, detail
:feature:cart           ← Cart, offline sync
:feature:checkout       ← Payment, order placement
:feature:orders         ← History, tracking
```

---

## Step 6 — Data Flow (25–30 min)

### Product Listing Flow (Cache-First):

```
ProductPage
  → ProductBloc.add(LoadProducts)
    → GetProductsUseCase.call()
      → ProductRepository.getProducts()
        → Check Drift (local DB)
          → Cache valid? (TTL: 15 min)
            → YES: return cached → Bloc emits ProductLoaded
            → NO: call ProductApi (Dio)
              → Update Drift
              → return fresh data → Bloc emits ProductLoaded
```

### Cart Add Flow (Offline-Capable):

```
ProductDetailPage
  → CartBloc.add(AddToCart(product, qty))
    → AddToCartUseCase.call()
      → CartRepository.addToCart()
        → Save to Drift (local cart table)
        → If online: sync to Cart Service immediately
        → If offline: enqueue in SyncQueue
          → WorkManager picks up when network restores
        → Bloc emits CartUpdated
```

---

## Step 7 — Networking (30–35 min)

### Dio Interceptor Chain:

```
Request → AuthInterceptor (attach Bearer token)
       → LoggingInterceptor (debug only, no PII)
       → RetryInterceptor (exponential backoff)
       → Response

401 → RefreshTokenInterceptor → refresh → retry original
```

### Timeout Configuration:

```dart
Dio(BaseOptions(
  connectTimeout: Duration(seconds: 10),
  receiveTimeout: Duration(seconds: 15),
  sendTimeout: Duration(seconds: 10),
));
```

### Retry Policy:
- Max 3 retries
- Exponential backoff: 2s → 4s → 8s
- Only retry on: timeout, 5xx, network error
- **Never retry** `POST /orders` without idempotency key

### Idempotency:

```
POST /orders
Headers:
  Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
  Authorization: Bearer <access_token>
Body: { cart_id: "abc", payment_method: "card" }
```

- UUID generated on mobile, stored with the order in local DB
- If retry hits backend, deduplication returns original order
- Prevents duplicate orders on network retry

---

## Step 8 — Offline Support & Sync (35–40 min)

### Sync Queue Schema (Drift):

```sql
CREATE TABLE sync_operations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  operation_type TEXT,      -- ADD_TO_CART, UPDATE_QTY, PLACE_ORDER
  entity_id TEXT,           -- product_id, cart_id, order_id
  payload TEXT,             -- JSON serialized
  idempotency_key TEXT,     -- UUID for dedup
  status TEXT,              -- PENDING, SYNCING, DONE, ERROR
  created_at INTEGER,
  retry_count INTEGER DEFAULT 0
);
```

### Sync Flow:

```
[Offline] User adds to cart
  → Save to cart table (Drift)
  → Insert into sync_operations (PENDING)
  → Update UI immediately (optimistic)

[Network restored]
  → WorkManager triggers SyncWorker
  → Query sync_operations WHERE status = PENDING ORDER BY created_at
  → For each operation:
      → Call API with Idempotency-Key header
      → Success: mark DONE
      → Failure: increment retry_count, backoff
      → Max retries (5): mark ERROR, notify user
```

### Conflict Resolution:
- **Cart**: Server-authoritative. On sync, server response replaces local cart.
- **Order**: If order already exists (idempotency key match), accept server's order ID.

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: only auth + database on launch. Analytics, crash reporting deferred 3s after first frame. |
| **UI** | `ListView.builder` for product list, `cached_network_image` with placeholder, `const` widgets, `RepaintBoundary` for complex cards. |
| **Network** | Cursor-based pagination (20 items/page), ETag caching, gzip compression, image CDN with proper `Cache-Control`. |
| **Memory** | Image cache limit: 100MB, `clearImageCache()` on low memory warning, dispose BLoC on widget unmount. |
| **Battery** | WorkManager for sync (not foreground service), batch push notification processing, no background polling. |

---

## Step 10 — Security (45–50 min)

- **Auth**: OAuth2 with PKCE, access token (15 min) + refresh token (30 days)
- **Token storage**: `flutter_secure_storage` → Android Keystore / iOS Keychain
- **SSL pinning**: Pin API gateway certificate public key (Dio `IOHttpClientAdapter`)
- **Payment**: Never handle raw card data — use payment SDK (Stripe/Adyen) with tokenization
- **Encryption**: `EncryptedSharedPreferences` for non-token sensitive data
- **Obfuscation**: R8/ProGuard for Android, `--obfuscate` flag for Flutter release builds
- **No secrets in app**: API keys via remote config or backend proxy

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Cursor-based pagination prevents deep pagination performance issues
- Image CDN (CloudFront/Cloudflare) offloads image traffic from API
- Local cache reduces API calls by ~70% (cache-first for product catalog)
- WorkManager constraints: only sync on Wi-Fi for large operations

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| Network down | Show cached data, queue writes, show "offline" banner |
| API 500 | Retry with backoff (max 3), show error state with retry button |
| API 401 | Auto-refresh token, retry request once |
| Token refresh fails | Force logout, redirect to login |
| Stale cache | Show cached data + silent background refresh (stale-while-revalidate) |
| Payment fails | Don't mark order as placed, keep in pending state, allow retry |
| Sync conflict | Server-authoritative: accept server's cart, show toast notification |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| Cache-first for products | May show stale prices → mitigate with 15 min TTL + pull-to-refresh |
| Optimistic UI for cart | User sees update immediately, but may need rollback if sync fails |
| Server-authoritative cart | Simpler, but user's offline changes may be overwritten |
| Flutter cross-platform | Faster development, but platform-specific features need native channels |
| WorkManager vs foreground sync | Battery-friendly, but sync may be delayed by OS |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Use Cases, Repository, BLoC (bloc_test), mappers |
| **Integration** | Repository + Drift (in-memory DB), API client with mock server |
| **E2E** | Flutter integration tests for critical flows (browse → cart → checkout) |
| **Observability** | Crashlytics (crashes), Firebase Performance (API timing), custom events (sync failures) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC for state management. Product browsing is cache-first with a 15-minute TTL for offline resilience. The cart is offline-capable with a sync queue backed by WorkManager and idempotency keys to prevent duplicate orders. Security follows OAuth2 with tokens stored in Keystore/Keychain, SSL pinning, and tokenized payments. Performance is optimized through lazy initialization, list virtualization, image caching, and WorkManager for battery-efficient background sync."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
