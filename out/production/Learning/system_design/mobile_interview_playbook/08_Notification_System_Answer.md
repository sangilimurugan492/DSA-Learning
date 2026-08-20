# Structured Interview Answer: Design a Notification System

> **Question**: *"Design a mobile notification system that handles push, in-app, and real-time notifications."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Push notifications (FCM/APNS), in-app, or both?
- Real-time notifications (WebSocket) or pull-based?
- Multi-channel (push, SMS, email)?
- User preferences (quiet hours, categories)?
- Notification grouping/batching?
- Deep linking to specific screens?

**Assumed:** Flutter, push + in-app + real-time, multi-channel, quiet hours yes, batching yes, deep linking yes.

---

## Step 2 — Define Scope

```
IN SCOPE: Push (FCM), in-app notifications, real-time (WebSocket), quiet hours, notification categories, batching, deep linking, notification center
OUT OF SCOPE: SMS/Email gateway, admin notification console, A/B testing of notifications
```

---

## Step 3 — Constraints

```
Functional: Receive push, show in-app, real-time updates, user preferences, batching, deep link
Non-Functional: 50M users, ~10M notifications/day, < 5s delivery latency, respect quiet hours, batch to avoid spam
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
        FCM (push) + WebSocket (real-time) + REST (history)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
 Notification        WebSocket Gateway       Push Service
 Service              (real-time delivery)   (FCM/APNS)
    │                      │                      │
 User Preferences    Connection Manager      FCM/APNS API
 (quiet hours, cats)  (sticky sessions)       │
    │                      │                      │
 Kafka (events)             │                      │
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis      │
                    │ + Kafka      │
                    └──────────────┘
```

> "Three delivery channels: WebSocket for in-app real-time, FCM for push when backgrounded, REST for notification history. The notification service fans out to the appropriate channel based on app state and user preferences."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  NotificationCenter,          │
│  InAppToast, DeepLinkHandler │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  NotificationBloc (state)    │
│  (unread count, list)       │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│     Notification Router     │
│  (routes by type: push/     │
│   in-app/real-time)         │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  NotificationRepository       │
└──────────┬───────────┬──────┘
           ↓           ↓
   FCM + WebSocket   Local DB
   + REST API        (Drift)
```

---

## Step 6 — Data Flow

### Notification Delivery (Multi-Channel):
```
Server generates notification (Kafka event)
  ↓
Notification Service checks user preferences:
  → Is notification category enabled?
  → Is it within quiet hours? → If yes, hold for later
  → Batch: group with similar notifications (max 5 per 10 min)
  ↓
Determine delivery channel:
  ├── App in FOREGROUND → WebSocket push → In-app toast + add to center
  ├── App in BACKGROUND → FCM push → System notification tray
  └── App KILLED → FCM push with data payload → tap → deep link → app opens

  ↓
Store in notification history (DB)
  ↓
Update unread count (WebSocket or REST sync)
```

### Deep Linking:
```
User taps push notification
  ↓
FCM data payload: { type: "order_update", order_id: "123" }
  ↓
App launches → DeepLinkHandler parses payload
  ↓
Navigate to OrderDetailScreen(order_id: "123")
  ↓
Mark notification as read → POST /notifications/{id}/read
```

| Data | Strategy | TTL |
|------|----------|-----|
| Notification history | Cache-first | 5 min |
| Unread count | WebSocket sync | Real-time |
| User preferences | Local + sync | Permanent |
| Notification templates | Cache-first | 1 hour |

---

## Step 7 — Networking

```
WebSocket:
  /ws/notifications/{user_id}
  Push: { id, type, title, body, data, timestamp }
  Heartbeat: 30s
  Used when app is in foreground

FCM (Push):
  Background/killed state
  Data payload: { type, id, deep_link_params }
  Notification payload: { title, body } (displayed by system)
  High priority: for urgent (payment, security)
  Normal priority: for non-urgent (promotions, social)

REST:
  GET /notifications?cursor=xxx (history, cursor pagination)
  POST /notifications/{id}/read (mark as read)
  PUT /preferences (update quiet hours, categories)
  connectTimeout: 10s
```

---

## Step 8 — Offline Support

```
Notifications while offline:
  → FCM stores messages (retry for ~4 weeks)
  → On reconnect: FCM delivers pending pushes
  → WebSocket reconnects → sync missed notifications via REST
  → GET /notifications?since={last_notification_id}

Notification center:
  → Cached locally (Drift)
  → Read while offline
  → Mark-as-read queued, synced when online
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load notification count from local DB, sync in background |
| **UI** | ListView.builder for notification list, smooth animation for toast |
| **Network** | WebSocket for real-time (no polling), FCM for push (no app polling) |
| **Memory** | Dispose WebSocket on logout, cancel streams on screen exit |
| **Battery** | No polling! WebSocket (foreground) + FCM (background) only |

> "The key battery optimization is: no polling. When foregrounded, WebSocket delivers real-time. When backgrounded, FCM handles push. The app never wakes up to poll."

---

## Step 10 — Security

```
Auth: OAuth2, token in Keystore/Keychain
FCM: Token registered per device, refreshed on app reinstall
WebSocket: Authenticated with JWT, connection per user
Data: Notification content may contain sensitive info → encrypt payload
Privacy: Don't include full content in FCM (use data-only + fetch on tap for sensitive)
Deep links: Validate deep link parameters (prevent injection)
```

---

## Step 11 — Scalability

- Kafka for notification event pipeline (decouple producers from delivery)
- WebSocket gateway with sticky sessions (load balancer affinity)
- FCM batching: group notifications to avoid spam (max N per time window)
- Redis for: user online status, notification queue, rate limiting
- Notification history: cursor pagination + read replicas

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| WebSocket disconnects | Reconnect + sync missed notifications via REST |
| FCM delivery fails | Server retries (FCM queues for ~4 weeks) |
| App killed | FCM data payload → tap → app opens → deep link |
| Quiet hours active | Hold notification → deliver after quiet hours end |
| Batch overflow | If > N notifications in window → summarize ("You have 5 new likes") |
| Duplicate notification | Dedup by notification_id on client |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Real-time | WebSocket (foreground) + FCM (background) | Polling | No polling = battery efficient |
| Batching | Server-side (time window) | Per-notification | Avoid spam, better UX |
| Quiet hours | Server-enforced | Client-only | Server knows → doesn't even send |
| Notification center | Local DB (cached) | API-only | Read offline, fast access |
| Sensitive content | Data-only FCM + fetch on tap | Full content in FCM | Privacy — FCM payload can be intercepted |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Quiet hours logic, batching window, deep link parsing, BLoC state |
| Integration | WebSocket delivery, FCM mock, notification history sync |
| E2E | Server sends → WebSocket delivers → tap → deep link → correct screen |
| Observability | Delivery latency p95, FCM success rate, WebSocket connection uptime, open rate per category |
