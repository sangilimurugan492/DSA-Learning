# Mobile System Design — One-Page Cheat Sheet

> Print this. Memorize this. Use it as your interview scaffold.

---

## The 14-Step Framework

| # | Step | Key Question |
|---|------|-------------|
| 1 | Clarify Requirements | Platform? Offline? Scale? Real-time? |
| 2 | Define Scope | Payments? Notifications? Auth? |
| 3 | Identify Constraints | Users, devices, network, latency targets |
| 4 | High-Level Architecture | Mobile → API Gateway → Services → DB |
| 5 | Mobile Architecture | Clean Architecture, BLoC, layers, modularization |
| 6 | Data Flow | UI → BLoC → UseCase → Repo → Cache → API |
| 7 | Networking | Timeouts, retries, backoff, idempotency |
| 8 | Offline / Sync | Local DB, sync queue, conflict resolution |
| 9 | Performance | Startup, UI, network, memory, battery |
| 10 | Security | OAuth, Keystore, SSL pinning, encryption |
| 11 | Scalability | Pagination, caching, background work |
| 12 | Failure Scenarios | Network down, API 500, stale cache |
| 13 | Trade-offs | Cache-first vs network-first, etc. |
| 14 | Testing / Observability | Unit, integration, E2E, crash reporting |

---

## 7 Evaluation Areas

| Area | Key Topics |
|------|-----------|
| 📱 Architecture | Clean Arch, BLoC, MVVM, modularization |
| 🌐 Networking | REST, WebSocket, retries, timeouts, pagination |
| 💾 Data | Room/Drift, caching, offline-first, sync |
| ⚡ Performance | Startup, memory, battery, rendering |
| 🔐 Security | OAuth/JWT, Keystore, encryption, SSL pinning |
| 🔄 Reliability | Offline, retry, idempotency, failure handling |
| ☁ Backend | API Gateway, cache, queues, DB, CDN |

---

## Architecture Layers (Flutter + Android)

```
UI (Widgets/Compose) → State (BLoC/ViewModel) → Domain (UseCase) → Data (Repository)
                                                                         ↓
                                                              ┌──────────┴──────────┐
                                                          Remote (Dio/Retrofit)  Local (Drift/Room)
```

---

## Caching Strategies

| Strategy | Use For | Trade-off |
|----------|---------|-----------|
| Cache-First | Catalogs, static content | May show stale data |
| Network-First | Cart, pricing, inventory | Fails offline without cache |
| Stale-While-Revalidate | Images, feeds | Best UX, more complex |
| Cache-Then-Network | Feeds, real-time | Always fresh, slower first paint |

---

## Offline Sync Flow

```
Offline Action → Save to Local DB → Operation Queue (WorkManager)
    → Network Restored → Sync Worker → Backend (with Idempotency Key)
    → Update Local DB → Emit State
```

---

## Idempotency Rule

- ✅ **Safe to retry**: `GET /products`, `GET /cart`
- ⚠️ **Use idempotency key**: `POST /orders`, `POST /payment`, `POST /checkout`
- Backend deduplicates based on `Idempotency-Key` header (UUID)

---

## Security Checklist

- [ ] OAuth2/OIDC for auth
- [ ] Access token (short-lived) + Refresh token (long-lived)
- [ ] Store tokens in **Keystore/Keychain** (never SharedPreferences)
- [ ] SSL/Certificate pinning
- [ ] TLS 1.2+ for all traffic
- [ ] Encryption at rest (SQLCipher / EncryptedSharedPreferences)
- [ ] No sensitive data in logs
- [ ] No hardcoded secrets in the app
- [ ] ProGuard/R8 obfuscation (Android)
- [ ] Root/jailbreak detection

---

## Performance Categories

| Category | Key Optimizations |
|----------|------------------|
| **Startup** | Lazy init, defer SDKs, reduce main thread work |
| **UI** | Avoid rebuilds, list virtualization, image optimization |
| **Network** | HTTP caching, batching, compression, pagination |
| **Memory** | Image cache limits, lifecycle awareness, leak detection |
| **Battery** | Batch background work, optimize location, avoid polling |

---

## Key Phrases to Use

| Situation | Phrase |
|-----------|--------|
| Architecture | *"I separate presentation, domain, and data concerns so business logic isn't coupled to the UI or networking."* |
| Caching | *"Cache-first for catalogs; network-first for pricing/inventory with cache fallback."* |
| Idempotency | *"I attach an idempotency key for POST operations so retries don't create duplicates."* |
| Offline sync | *"Operations saved to a local queue via WorkManager; synced in order with idempotency keys when network restores."* |
| Security | *"Tokens in Keystore/Keychain, TLS 1.2+ with certificate pinning, no hardcoded secrets."* |

---

## 10 Must-Master Problems

**Tier 1 (Essential):** E-Commerce · Chat · Food Delivery · Ride-Sharing · Social Feed
**Tier 2 (Senior/Lead):** Video Streaming · Payment · Notification · Job Search · Offline-First

---

## Preparation Loop

```
Learn → Design → Build → Document → Explain → Mock Interview → Improve
```

> **Turn your GitHub projects into interview prep.** Every feature you build is a system design story.
