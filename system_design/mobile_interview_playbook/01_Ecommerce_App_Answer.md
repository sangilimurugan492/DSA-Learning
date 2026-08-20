# Structured Interview Answer: Design an E-Commerce Mobile App

> **Question**: *"Design a mobile application like Amazon/Shopify/Samsung Shop."*
>
> This is a full 60-minute structured answer. Use this as a script to practice.

---

## Step 1 — Clarify Requirements (0–2 min)

**You say:**
> "Before I start designing, I'd like to clarify a few requirements."

**Questions to ask:**
- Are we designing for Android, iOS, or cross-platform (Flutter)?
- Do we need offline support for browsing and cart?
- Are payments in scope?
- Do users need real-time order tracking?
- What scale should we design for? (e.g., 10 million users)
- Are push notifications required (order updates, deals)?

**Assume the interviewer says:** Flutter, offline support yes, payments in scope, real-time tracking yes, 10M users, push notifications yes.

---

## Step 2 — Define Scope (2–3 min)

**You say:**
> "Based on that, here's my scope."

```
IN SCOPE:
  • Product browsing (catalog, search, detail)
  • Cart management (offline-capable)
  • Checkout & payment
  • Order tracking (real-time)
  • Push notifications (order status, deals)
  • User authentication

OUT OF SCOPE:
  • Seller dashboard / admin panel
  • Recommendation engine internals
  • Logistics / delivery routing
```

---

## Step 3 — Identify Constraints (3–5 min)

```
Functional:
  • Users can browse products by category and search
  • Users can add/remove items to cart (works offline)
  • Users can checkout and pay
  • Users can track orders in real-time
  • Users receive push notifications

Non-Functional:
  • Scale: 10M users, ~500K concurrent
  • Platform: Flutter (Android + iOS)
  • Offline: Cart and browsing must work offline
  • Real-time: Order tracking via WebSocket
  • Latency: < 500ms screen load (from cache)
  • Battery: Minimal background drain
```

---

## Step 4 — High-Level Architecture (5–10 min)

**You say:**
> "Let me draw the high-level architecture — the mobile app talking to backend services through an API Gateway."

```
                    ┌──────────────┐
                    │   Mobile App │
                    │   Flutter    │
                    └──────┬───────┘
                           │
              HTTPS / REST / WebSocket
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    │ (Auth, Rate  │
                    │  Limiting)   │
                    └──────┬───────┘
                           │
       ┌───────────────────┼───────────────────┐
       │                   │                   │
 Product Service     Cart Service        Order Service
       │                   │                   │
 Payment Service     Search Service    Notification Service
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                    ┌──────▼───────┐
                    │  PostgreSQL  │
                    │  + Redis     │
                    │  + CDN       │
                    └──────────────┘
```

**You say:**
> "The mobile app communicates via REST for most operations and WebSocket for real-time order tracking. The API Gateway handles auth, rate limiting, and routing."

---

## Step 5 — Mobile Architecture (10–20 min)

**You say:**
> "Now let me zoom into the mobile side. I use Clean Architecture with BLoC for state management."

```
┌─────────────────────────────┐
│            UI               │
│       Flutter Widgets       │
│  (ProductList, Cart, etc.)  │
└──────────────┬──────────────┘
               ↓ Events / States
┌─────────────────────────────┐
│          BLoC               │
│  ProductBloc, CartBloc,     │
│  OrderBloc, AuthBloc        │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  GetProductsUseCase         │
│  AddToCartUseCase            │
│  PlaceOrderUseCase           │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│        Repository           │
│  ProductRepository           │
│  CartRepository              │
│  OrderRepository             │
└──────────┬───────────┬──────┘
           ↓           ↓
       Remote API    Local DB
       (Dio)        (Drift/SQLite)
```

**You say (Lead-level justification):**
> "I separate presentation, domain, and data concerns so that business logic isn't coupled to Flutter widgets or the networking implementation. This improves testability and allows us to change the data source independently — for example, swapping Dio for a different HTTP client wouldn't touch the BLoC layer."

**Modularization:**
```
:app                    ← App shell, navigation, DI
:core:ui                ← Shared widgets, theme
:core:network           ← Dio setup, interceptors, retry
:core:database          ← Drift setup, DAOs
:core:common            ← Utilities, extensions
:feature:product        ← Product listing, detail
:feature:cart           ← Cart management, offline sync
:feature:checkout       ← Payment, order placement
:feature:orders         ← Order history, real-time tracking
:feature:auth           ← Login, registration, token management
```

---

## Step 6 — Data Flow (20–25 min)

**You say:**
> "Let me walk through the data flow for loading products."

```
User opens product list
  ↓
ProductBloc emits LoadProductsEvent
  ↓
GetProductsUseCase.execute()
  ↓
ProductRepository.getProducts()
  ↓
Check local DB (Drift) — cache valid? (TTL: 15 min)
  ├── YES → Return cached products → BLoC emits LoadedState → UI renders
  └── NO  → Call Remote API (Dio)
              ↓
              GET /api/products?page=1&cursor=xxx
              ↓
              Update local DB with fresh data
              ↓
              BLoC emits LoadedState → UI renders
```

**Cache Strategy:**
> "I use **cache-first** for product catalogs because they change infrequently. For pricing and inventory, I use **network-first** with cache fallback — pricing accuracy matters, but if the network is down, showing cached price is better than showing nothing."

| Data | Strategy | TTL | Reason |
|------|----------|-----|--------|
| Product catalog | Cache-first | 15 min | Changes infrequently |
| Product images | Cache-first | 24 hours | CDN-cached, rarely change |
| Price & inventory | Network-first | 5 min | Accuracy critical |
| Cart | Local-first | N/A | User's own data, local DB is source of truth |
| Order history | Cache-first | 5 min | Historical, rarely changes |

---

## Step 7 — Networking (25–30 min)

**You say:**
> "For networking, I configure timeouts, retries with exponential backoff, and idempotency for write operations."

```
Dio Configuration:
  connectTimeout: 10s
  receiveTimeout: 15s
  sendTimeout: 10s

Retry Policy:
  Max retries: 3
  Backoff: exponential (2s → 4s → 8s)
  Retry on: 5xx, timeout, network error
  Do NOT retry: 4xx (except 429)

Interceptors:
  1. Auth Interceptor → Attach Bearer token, refresh on 401
  2. Retry Interceptor → Exponential backoff for 5xx
  3. Logging Interceptor → Debug-only, sanitized
  4. Cache Interceptor → ETag / If-None-Match
```

**Idempotency (critical for e-commerce):**
```
POST /api/orders
Headers:
  Idempotency-Key: <UUID generated on mobile>
  Authorization: Bearer <access_token>
Body:
  { cart_id, payment_method, shipping_address }

→ Backend deduplicates based on Idempotency-Key
→ Safe to retry without creating duplicate orders
→ Mobile stores UUID in sync queue until order confirmed
```

**You say:**
> "Retrying GET /products is safe. Retrying POST /orders without an idempotency key could create duplicate orders — which is catastrophic. So every write operation gets a UUID idempotency key."

---

## Step 8 — Offline Support & Sync (30–35 min)

**You say:**
> "Offline support is critical for e-commerce. Users should be able to browse and add to cart even without network."

### Offline Cart Scenario:
```
User adds product to cart while offline
  ↓
Save to local DB (Drift) with status PENDING
  ↓
Cart UI updates immediately (optimistic)
  ↓
Operation added to Sync Queue
  ↓
WorkManager detects network restored
  ↓
Sync Worker processes queue in order:
  [1] ADD_TO_CART (product_id: 123, qty: 1)
  [2] UPDATE_QTY  (product_id: 123, qty: 2)
  ↓
Each operation sent with idempotency key
  ↓
Status updated to DONE in local DB
```

### Sync Queue Schema:
```
┌──────────────────────────────────────────┐
│            Operation Queue               │
├──────────────────────────────────────────┤
│ id | operation    | payload    | status  │
├──────────────────────────────────────────┤
│ 1  | ADD_TO_CART  | {pid:123} | DONE    │
│ 2  | UPDATE_QTY   | {pid:123} | SYNCING  │
│ 3  | PLACE_ORDER  | {cart:abc} | PENDING │
└──────────────────────────────────────────┘
Status: PENDING → SYNCING → DONE / ERROR
```

**Conflict Resolution:**
> "I use **server-authoritative** for inventory (server knows real stock) and **last-write-wins** for cart (user's latest action wins). If there's a stock conflict, the server returns an error and the app shows 'Item out of stock' to the user."

---

## Step 9 — Performance (35–38 min)

| Category | Techniques |
|----------|-----------|
| **Startup** | Lazy init — only auth + database on launch. Analytics/crash reporting deferred 3s. |
| **UI** | ListView.builder for product lists, cached images via CachedNetworkImage, const widgets |
| **Network** | Cursor pagination (not offset), gzip compression, ETag conditional requests |
| **Memory** | Image cache limit 100MB, dispose controllers, LeakCanary in debug |
| **Battery** | WorkManager for sync (not foreground service), batch push notifications |

---

## Step 10 — Security (38–41 min)

```
Authentication:
  OAuth2 / OIDC
  → Access Token (15 min) + Refresh Token (30 days)
  → Stored in Flutter Secure Storage / Android Keystore
  → NEVER SharedPreferences

Network:
  TLS 1.2+ enforced
  Certificate pinning (prevent MITM)
  ProGuard/R8 obfuscation (Android)

Data at Rest:
  SQLCipher for sensitive DB tables (payment methods, addresses)
  EncryptedSharedPreferences for non-token secrets

Payments:
  Never store card numbers on device
  Use payment gateway SDK (Stripe/Razorpay) — tokenized
  Biometric prompt for high-value transactions
```

---

## Step 11 — Scalability (41–43 min)

- Cursor-based pagination for product lists (not offset — avoids slow COUNT queries)
- Image CDN (CloudFront/Cloudflare) — proper Cache-Control headers
- ETag / If-None-Match — server returns 304 if data unchanged → saves bandwidth
- Request deduplication in repository layer (don't fire same GET twice)
- WorkManager for background sync — respects system Doze mode

---

## Step 12 — Failure Scenarios (43–48 min)

| Failure | Mobile Response |
|---------|----------------|
| Network down | Show cached products, allow offline cart, queue operations |
| API 500 | Retry 3x with backoff, then show error state with retry button |
| API 401 | Refresh token → retry. If refresh fails → redirect to login |
| Stale cache | Show cached data + background refresh (stale-while-revalidate) |
| Payment fails | Show error, keep cart intact, allow retry with different method |
| Order sync conflict | Server-authoritative, show "item out of stock" if inventory changed |
| App crash | Crashlytics reports, restart restores state from local DB |

---

## Step 13 — Trade-offs (48–52 min)

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Architecture | Clean Arch + BLoC | MVVM + Provider | BLoC is more testable, event-driven, scales for complex state |
| Caching | Cache-first for catalog | Network-first | Catalogs change infrequently; cache-first enables offline browsing |
| Offline cart | Local DB as source of truth | Server-authoritative cart | Users expect cart to work offline; local-first is more responsive |
| Payment | Tokenized via SDK | Custom payment form | Security compliance (PCI DSS), reduces liability |
| Sync | WorkManager | Foreground service | WorkManager respects battery, survives app kill, system-scheduled |

---

## Step 14 — Testing & Observability (52–55 min)

| Level | What |
|-------|------|
| Unit | Use cases, repository logic, BLoC state transitions (mock deps) |
| Integration | Repository + real Drift DB + mocked Dio |
| E2E | Login → Browse → Add to cart → Checkout (integration_test package) |
| Crash | Firebase Crashlytics — non-fatal + fatal exceptions |
| Analytics | Firebase Analytics — screen views, add_to_cart, checkout_complete funnels |
| Performance | App startup time, API latency p50/p95, frame drop rate |

---

## 🎤 Key Phrases Used in This Answer

| When | Phrase |
|------|--------|
| Architecture | "I separate presentation, domain, and data concerns so business logic isn't coupled to the UI." |
| Caching | "Cache-first for catalogs because they change infrequently. Network-first for pricing because accuracy matters." |
| Idempotency | "Every write operation gets a UUID idempotency key so retries don't create duplicate orders." |
| Offline | "Cart operations are saved to a local queue. WorkManager syncs them in order when network restores." |
| Security | "Tokens in Keystore, never SharedPreferences. TLS 1.2+ with certificate pinning on all traffic." |
