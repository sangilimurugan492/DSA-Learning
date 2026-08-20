# Mobile System Design Interview — Master Answer Template

> **Fill-in-the-blanks template for ANY mobile system design question.**
> Print this. Memorize the structure. Practice filling it in for different apps.

---

## ⏱️ Time Allocation (60 Minutes)

| Time | Step | What You Do |
|------|------|-------------|
| 0–5 min | Steps 1–3 | Clarify requirements, scope, constraints |
| 5–10 min | Step 4 | Draw high-level architecture |
| 10–25 min | Steps 5–6 | Mobile architecture + data flow |
| 25–35 min | Steps 7–8 | Networking + offline/sync |
| 35–45 min | Steps 9–11 | Performance + security + scalability |
| 45–52 min | Step 12 | Failure scenarios |
| 52–60 min | Steps 13–14 | Trade-offs + testing + Q&A |

---

## Step 1 — Clarify Requirements (2 min)

**What to say:**
> "Before I start designing, I'd like to clarify a few requirements to make sure I'm solving the right problem."

**Questions to ask (pick 4–5 relevant ones):**
- [ ] Are we designing for **Android, iOS, or cross-platform** (Flutter)?
- [ ] Do we need **offline support**?
- [ ] Is **[domain-specific feature]** in scope? (e.g., payments, real-time tracking)
- [ ] Do users need **real-time updates**?
- [ ] What **scale** should we design for? (e.g., 10 million users)
- [ ] Are **push notifications** required?
- [ ] What's the **target device range**? (low-end vs flagship)

---

## Step 2 — Define Scope (1 min)

**What to say:**
> "Based on your answers, here's what I'll include in scope and what I'll exclude."

**Fill in:**
```
IN SCOPE:
  • 
  • 
  • 

OUT OF SCOPE (for this interview):
  • 
  • 
```

---

## Step 3 — Identify Constraints (2 min)

**What to say:**
> "Let me note the key constraints that will drive my architecture decisions."

**Fill in:**
```
Functional Requirements:
  • Users can ___
  • Users can ___
  • Users can ___

Non-Functional Requirements:
  • Scale: ___ million users
  • Platform: Android + iOS (Flutter)
  • Offline support: Yes/No
  • Real-time: Yes/No
  • Latency target: < ___ ms for screen load
  • Battery: Minimize background drain
```

---

## Step 4 — High-Level Architecture (5 min)

**What to say:**
> "Let me start with the high-level architecture — the mobile client communicating with backend services."

**Draw this:**
```
                    ┌──────────────┐
                    │   Mobile App │
                    │ Flutter /    │
                    │ Android      │
                    └──────┬───────┘
                           │
                    HTTPS / REST / WebSocket
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
           ┌───────────────┼───────────────┐
           │               │               │
     Service A       Service B       Service C
           │               │               │
           └───────────────┼───────────────┘
                           │
                    ┌──────▼───────┐
                    │  Databases   │
                    │  + Cache     │
                    └──────────────┘
```

**Fill in the services for your app:**
```
Service A: _________ (e.g., Product Service)
Service B: _________ (e.g., Cart Service)
Service C: _________ (e.g., Order Service)
```

---

## Step 5 — Mobile Architecture (10 min)

**What to say:**
> "Now let me zoom into the mobile side. I use Clean Architecture with BLoC for state management."

**Draw this:**
```
┌─────────────────────────────┐
│            UI               │
│       Flutter Widgets       │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│       UI State/Event        │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│        Repository           │
└──────────┬───────────┬──────┘
           ↓           ↓
       Remote API    Local DB
           ↓           ↓
         Dio       SQLite/Drift
```

**Then say (Lead-level justification):**
> "I separate presentation, domain, and data concerns so that business logic isn't coupled to Flutter widgets or the networking implementation. This also improves testability and allows us to change the data source independently."

**Modularization (mention briefly):**
```
:app                    ← App shell, navigation
:core:network           ← Dio/OkHttp, interceptors
:core:database          ← Room/Drift
:feature:[name]         ← Feature modules
```

---

## Step 6 — Data Flow (5 min)

**What to say:**
> "Let me walk through the data flow for [primary feature]."

**Draw this:**
```
UI → BLoC → UseCase → Repository → Local Cache → Remote API → Update DB → Emit State → UI
```

**Cache strategy — say:**
> "I use a **[cache-first / network-first / stale-while-revalidate]** strategy for this data because [reason]."

| Strategy | When | Why |
|----------|------|-----|
| Cache-First | Static catalogs, images | Fast, offline-capable |
| Network-First | Cart, pricing, real-time inventory | Freshness critical |
| Stale-While-Revalidate | Feeds, social posts | Balance speed + freshness |

---

## Step 7 — Networking (5 min)

**What to say:**
> "For networking, I configure timeouts, retries with exponential backoff, and idempotency for write operations."

**Discuss:**
```
Timeouts:     Connect 10s, Read 15s
Retries:       Max 3, exponential backoff (2s → 4s → 8s)
GET requests:  Safe to retry ✅
POST requests: Need idempotency key ⚠️

POST /orders
Headers:
  Idempotency-Key: <UUID>
  Authorization: Bearer <token>
```

**Key phrase:**
> "Retrying GET is safe. Retrying POST without an idempotency key could create duplicate orders — which is catastrophic."

---

## Step 8 — Offline / Sync (5 min)

**What to say:**
> "Offline support is critical for mobile. Here's how I handle it."

**Draw this:**
```
Offline → Save operation locally → Operation Queue
                                         ↓
                               Network restored
                                         ↓
                                    Sync Worker
                                         ↓
                                    Backend API
                                    (with idempotency keys)
```

**Discuss:**
- Local DB (Room/Drift) stores pending operations
- Sync queue with PENDING / SYNCING / DONE / ERROR status
- Conflict resolution: server-authoritative / last-write-wins
- WorkManager for background sync
- Eventual consistency — local and remote may diverge temporarily

---

## Step 9 — Performance (3 min)

**What to say:**
> "I break performance into five categories."

| Category | Key Techniques |
|----------|---------------|
| **Startup** | Lazy initialization, defer non-critical SDKs, reduce main thread work |
| **UI** | ListView.builder, const widgets, avoid unnecessary rebuilds |
| **Network** | HTTP caching, compression, pagination, dedup requests |
| **Memory** | Image cache limits, lifecycle awareness, LeakCanary |
| **Battery** | Batch background work, WorkManager, avoid polling |

---

## Step 10 — Security (3 min)

**What to say:**
> "For security, I implement OAuth2 with token-based auth, secure storage, and SSL pinning."

```
Mobile → OAuth/OIDC → Access Token (short) + Refresh Token (long)
                      → Store in Keystore/Keychain (NEVER SharedPreferences)
                      → API calls with Bearer token
                      → 401? → Refresh token → Retry
```

**Mention:**
- SSL/Certificate pinning (prevent MITM)
- Encryption at rest (SQLCipher / EncryptedSharedPreferences)
- Never hardcode secrets in the app
- ProGuard/R8 obfuscation

---

## Step 11 — Scalability (2 min)

**What to say:**
> "For scalability, the mobile client contributes through efficient data loading and caching."

- Cursor-based pagination (not offset)
- Image CDN with proper caching headers
- ETag / If-None-Match for conditional requests
- Request batching where appropriate
- Background sync with WorkManager (not foreground service)

---

## Step 12 — Failure Scenarios (3 min)

**What to say:**
> "Let me walk through what happens when things go wrong."

| Failure | Mobile Response |
|---------|----------------|
| Network down | Show cached data, queue writes for sync |
| API returns 500 | Retry with backoff, show error state |
| API returns 401 | Refresh token → retry, or redirect to login |
| Stale cache | Show cached + background refresh (stale-while-revalidate) |
| Sync conflict | Server-authoritative, log conflict for review |
| App crash | Crashlytics, restart with last known state from local DB |

---

## Step 13 — Trade-offs (3 min)

**What to say:**
> "Let me summarize the key trade-offs I made."

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Architecture | Clean Arch + BLoC | MVVM | Separation of concerns, testability |
| Caching | Cache-first | Network-first | [reason for this app] |
| Offline | Sync queue | No offline | [reason] |
| State mgmt | BLoC | Riverpod/Provider | Team familiarity, testability |
| Local DB | Drift/Room | Hive | SQL queries, relational data |

---

## Step 14 — Testing & Observability (2 min)

**What to say:**
> "For testing and observability, I ensure coverage at every layer."

| Level | What |
|-------|------|
| **Unit** | Use cases, repositories, BLoC logic (mock dependencies) |
| **Integration** | Repository + real local DB + mocked API |
| **E2E** | Critical user flows (login, browse, checkout) |
| **Crash reporting** | Firebase Crashlytics / Sentry |
| **Analytics** | Screen views, feature usage, funnel events |
| **Performance monitoring** | App startup time, API latency, frame rate |

---

## 🎤 Key Phrases to Memorize

| Situation | Phrase |
|-----------|--------|
| Architecture | "I separate presentation, domain, and data concerns so business logic isn't coupled to the UI or networking." |
| Caching | "I chose cache-first because [data] changes infrequently. For [real-time data], I use network-first with cache fallback." |
| Idempotency | "For POST operations, I attach an idempotency key so retries don't create duplicates." |
| Offline sync | "Operations are saved to a local queue with WorkManager. When network restores, the sync worker processes them in order." |
| Security | "Tokens are stored in Keystore/Keychain, never in SharedPreferences. All traffic uses TLS 1.2+ with certificate pinning." |
| Trade-offs | "The trade-off is [X]. I accept this because [Y], and I mitigate it by [Z]." |
