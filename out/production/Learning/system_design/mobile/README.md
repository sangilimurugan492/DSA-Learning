# Mobile System Design

> Mobile system design interview preparation for Senior/Lead Mobile Application Developer (Android + Flutter).

## What's Inside

| Document | Description |
|----------|-------------|
| [**Mobile System Design Interview Guide**](./Mobile_System_Design_Interview_Guide.md) | The complete structured guide — 14-step framework, all 7 evaluation areas, architecture deep dives, offline sync, security, performance, and 10 must-master problems |
| [**Cheat Sheet**](./Mobile_System_Design_Cheat_Sheet.md) | One-page quick reference — print and memorize before the interview |

## The 14-Step Framework (Memorize This)

```
 1. Clarify Requirements       8.  Offline / Sync
 2. Define Scope               9.  Performance
 3. Identify Constraints       10. Security
 4. High-Level Architecture    11. Scalability
 5. Mobile Architecture        12. Failure Scenarios
 6. Data Flow                  13. Trade-offs
 7. Networking                 14. Testing / Observability
```

## 7 Evaluation Areas

```
📱 Mobile Architecture   →  Clean Arch, BLoC, MVVM, modularization
🌐 Networking            →  REST, WebSocket, retries, timeouts, pagination
💾 Data                  →  Room/Drift, caching, offline-first, sync
⚡ Performance           →  Startup, memory, battery, rendering
🔐 Security              →  OAuth/JWT, Keystore, encryption, SSL pinning
🔄 Reliability           →  Offline mode, retry, idempotency, failure handling
☁ Backend Awareness     →  API Gateway, cache, queues, DB, CDN
```

## 10 Must-Master Problems — All with Full Examples

### Tier 1 — Essential

| # | Problem | Document | Key Mobile Challenges |
|---|---------|----------|----------------------|
| 1 | **E-Commerce App** | [📄 Example](./example_ecommerce_app_design.md) | Offline cart, cache-first catalog, payment security, sync queue |
| 2 | **Chat Application** | [📄 Example](./example_chat_app_design.md) | WebSocket management, E2E encryption, offline message queue, media uploads |
| 3 | **Food Delivery App** | [📄 Example](./example_food_delivery_app_design.md) | Real-time driver tracking, map optimization, cart offline persistence |
| 4 | **Ride-Sharing App** | [📄 Example](./example_ride_sharing_app_design.md) | Dynamic GPS accuracy, driver marker animation, Redis geo-matching, battery |
| 5 | **Social Media Feed** | [📄 Example](./example_social_media_feed_design.md) | Cursor pagination, optimistic likes, stale-while-revalidate, 60fps scroll |

### Tier 2 — Senior/Lead

| # | Problem | Document | Key Mobile Challenges |
|---|---------|----------|----------------------|
| 6 | **Video Streaming App** | [📄 Example](./example_video_streaming_app_design.md) | HLS adaptive bitrate, DRM licensing, offline download, buffer management |
| 7 | **Payment Application** | [📄 Example](./example_payment_app_design.md) | Biometric auth per payment, idempotency, fraud detection, SQLCipher |
| 8 | **Notification System** | [📄 Example](./example_notification_system_design.md) | Multi-channel (WebSocket + FCM + REST), deep linking, quiet hours, batching |
| 9 | **Job Search Application** | [📄 Example](./example_job_search_app_design.md) | Debounced search, offline-first saved jobs, application queue, job alerts |
| 10 | **Offline-First Mobile App** | [📄 Example](./example_offline_first_app_design.md) | Local DB as source of truth, sync engine, conflict resolution, zero data loss |

## Preparation Loop

```
Learn concept → Design system → Build feature → Document architecture → Explain verbally → Mock interview → Improve
```

> **Turn your GitHub projects into interview prep.** Every feature you build is a system design story.

## How to Use These Examples

1. **Read the guide first** — Understand the 14-step framework and 7 evaluation areas.
2. **Study 2-3 examples deeply** — Don't skim. Read every step, understand every trade-off.
3. **Practice out loud** — Explain the design as if you're in an interview. Time yourself (60 min).
4. **Write your own** — Pick a problem not covered here and design it using the 14-step framework.
5. **Memorize the cheat sheet** — Print it, review before every interview.

## Related

- [Parent: System Design](../README.md) — Backend system design fundamentals, components, patterns, case studies
