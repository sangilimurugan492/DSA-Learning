# Example: Design a Mobile Notification System

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile notification system for an e-commerce app that handles push notifications, in-app notifications, and notification preferences for 50 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter cross-platform."*
2. Notification types? → *"Push (FCM/APNS), in-app (real-time), local (scheduled)."*
3. Categories? → *"Order updates, promos, price drops, back-in-stock, social, system."*
4. User preferences? → *"Yes, per-category opt-in/opt-out, quiet hours."*
5. Deep linking? → *"Yes, tap notification → specific screen."*
6. Offline support? → *"Show cached notifications. Queue actions."*
7. Scale? → *"50M users, ~200M notifications/day."*

**Summary:**
- **Functional**: Push notifications (FCM/APNS), in-app notifications (WebSocket), local scheduled notifications, notification center, preferences, deep linking
- **Non-functional**: <5s push delivery, real-time in-app, <2s notification center load, 50M users, 200M notifs/day

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Push notification delivery (FCM/APNS)
- In-app notification center (real-time + history)
- Notification preferences (per-category, quiet hours)
- Deep linking to specific screens
- Local scheduled notifications (reminders)
- Notification grouping/batching

**Out of scope:**
- Email/SMS notifications (separate system)
- Notification template management (admin tool)
- A/B testing of notification content

---

## Step 3 — Identify Constraints (5 min)

```
50M users, ~200M notifications/day
Peak: sale events → 50M notifications in 1 hour (bulk push)
Notification types: 6 categories
Avg notifications/user/day: ~4
Push delivery target: <5s from event to device
In-app notification latency: <1s (WebSocket)
Notification center load: <2s
Devices: 70% Android, 30% iOS
FCM rate limit: 4,000 messages/second per project
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Mobile App (Flutter)                       │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │Notification│ │Prefs   │ │ Deep     │ │  App        │ │
│  │ Center    │ │Screen  │ │ Link     │ │  Shell      │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   WebSocket   FCM/APNS                    │
│   (notif hist, (real-time   (push when                 │
│    prefs)     in-app)       background)               │
│                    │                                    │
│              Local Notifications                       │
│              (flutter_local_notifications)             │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + WS Gateway
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │Notif    │ │Preference│ │  Push     │
  │ Service │ │ Service  │ │  Service  │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │Cassandra│ │PostgreSQL│ │FCM/APNS    │
  │(notif   │ │(prefs)  │ │API         │
  │ history)│ └────────┘ └───────────┘
  └────────┘
       │
  ┌────▼────┐
  │Kafka    │ ← Events from order, promo, social services
  │(events) │
  └─────────┘
  
Redis ← Online status, notification batching, rate limiting
FCM (Android) / APNS (iOS) ← Push delivery
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: NotificationCenterPage,            │
│           NotificationItemCard,               │
│           PreferencesPage, DeepLinkHandler    │
│  BLoCs: NotificationBloc,                     │
│         PreferenceBloc                        │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - GetNotificationsUseCase (paginated)        │
│  - MarkAsReadUseCase                          │
│  - UpdatePreferencesUseCase                   │
│  - HandleNotificationTapUseCase (deep link)  │
│  - ScheduleLocalNotificationUseCase           │
│  Repositories (abstract):                     │
│  - NotificationRepository                     │
│  - PreferenceRepository                       │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: NotificationApi (Dio)               │
│  Real-time: NotificationWebSocket             │
│  Push: FCM/ApnsHandler (firebase_messaging)  │
│  Local: NotificationDao (Drift)              │
│  Local Notif: flutter_local_notifications     │
└─────────────────────────────────────────────┘
```

### Key Design: NotificationBloc (Multi-Source)

```
┌──────────────────────────────────────────────┐
│           NotificationBloc                     │
├──────────────────────────────────────────────┤
│  Sources:                                     │
│  1. WebSocket (real-time, app foreground)    │
│     → New notification → prepend to list     │
│     → Update badge count                      │
│                                                │
│  2. FCM/APNS (push, app background/killed)   │
│     → System notification shown by OS        │
│     → On tap: deep link → open screen        │
│     → On app open: sync missed notifications│
│                                                │
│  3. REST (history, pagination)               │
│     → Load older notifications on scroll     │
│                                                │
│  4. Local (scheduled)                        │
│     → Reminder, price drop alert (scheduled) │
└──────────────────────────────────────────────┘
```

### Key Design: Push Notification Handler

```
┌──────────────────────────────────────────────┐
│           FCM/ApnsHandler                      │
├──────────────────────────────────────────────┤
│  onMessage (foreground):                      │
│    → App is open → show in-app banner        │
│    → Add to NotificationBloc                  │
│    → DON'T show system notification           │
│                                                │
│  onBackgroundMessage (background):           │
│    → Show system notification (high priority) │
│    → Store in Drift for sync on app open     │
│                                                │
│  onLaunch / onResume (notification tap):      │
│    → Parse deep link from payload             │
│    → Navigate to target screen               │
│    → Mark notification as read                │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"I unify three notification channels (WebSocket for foreground, FCM/APNS for background, REST for history) into a single NotificationBloc. When the app is foregrounded, notifications arrive via WebSocket for instant delivery (<1s). When backgrounded, FCM/APNS handles delivery. The key insight is that the NotificationBloc reconciles all sources — on app open, it syncs any missed notifications via REST, deduplicates against local cache, and presents a unified view. Deep links are parsed from FCM payloads and WebSocket messages uniformly."*

---

## Step 6 — Data Flow (25–30 min)

### Real-Time In-App Notification Flow (Foreground):

```
[App foreground, WebSocket connected]
Server event (e.g., order shipped)
  → Kafka → Notification Service
  → Check user preferences (is this category enabled?)
  → Check quiet hours (is user in quiet period?)
  → Send via WebSocket:
    { type: "ORDER_UPDATE", title: "Order Shipped", body: "...",
      deep_link: "/orders/123", category: "ORDER", timestamp: ... }
  → NotificationBloc receives
    → Save to Drift (notification cache)
    → Prepend to notification list
    → Update badge count
    → Show in-app banner (snackbar/banner widget)
    → emit NotificationReceived
```

### Push Notification Flow (Background):

```
[App backgrounded or killed]
Server event
  → Kafka → Notification Service
  → Check preferences + quiet hours
  → Send to Push Service
  → Push Service sends to FCM (Android) / APNS (iOS)
    → FCM payload:
      {
        "notification": { "title": "Order Shipped", "body": "..." },
        "data": { "deep_link": "/orders/123", "category": "ORDER", "notif_id": "abc" }
      }
  → OS shows system notification
  → User taps notification
    → App opens → onLaunch/onResume
    → Parse deep_link from data payload
    → Navigate to /orders/123
    → Mark notif_id as read (POST /api/notifications/{id}/read)
```

### Notification Center Load Flow:

```
NotificationCenterPage
  → NotificationBloc.add(LoadNotifications)
    → GetNotificationsUseCase.call()
      → Check Drift (cached notifications)
        → Has cache? → show immediately
        → Fetch from API: GET /api/notifications?cursor=xxx
        → Merge with WebSocket-received items
        → Deduplicate by notif_id
        → Save to Drift
        → emit NotificationsLoaded
```

### Sync Missed Notifications (on App Open):

```
App opens
  → NotificationBloc.add(SyncNotifications)
    → Last sync timestamp from Drift
    → GET /api/notifications?since={last_sync_timestamp}
    → Merge new notifications into local cache
    → Update badge count
    → Reconcile with WebSocket-received items
    → emit NotificationsSynced
```

---

## Step 7 — Networking (30–35 min)

### Channel Strategy:

| Channel | Protocol | Use Case |
|---------|----------|----------|
| **WebSocket** | WSS | Real-time notifications (app foreground) |
| **FCM** | Google FCM | Push notifications (Android, background) |
| **APNS** | Apple APNS | Push notifications (iOS, background) |
| **REST** | HTTPS | Notification history, preferences, mark-as-read |
| **Local** | OS-level | Scheduled reminders (no network needed) |

### REST API Design:

```
GET  /api/notifications?cursor=&limit=20     → Notification history (paginated)
GET  /api/notifications/unread/count          → Unread count
POST /api/notifications/{id}/read             → Mark as read
POST /api/notifications/read-all              → Mark all as read
GET  /api/notifications/preferences           → Get user preferences
PUT  /api/notifications/preferences           → Update preferences
POST /api/notifications/register-token        → Register FCM/APNS token
DELETE /api/notifications/token               → Unregister token (on logout)
```

### FCM Token Management:

```
On app launch:
  → FirebaseMessaging.instance.getToken()
  → POST /api/notifications/register-token { token, platform: "android" }
  → Store token association: user_id ↔ device_token

On token refresh:
  → FCM calls onTokenRefresh
  → POST /api/notifications/register-token (replaces old)

On logout:
  → DELETE /api/notifications/token
  → Server removes token association
  → No more push notifications for this device
```

### Notification Payload Structure:

```json
{
  "notification_id": "uuid-123",
  "category": "ORDER_UPDATE",
  "type": "ORDER_SHIPPED",
  "title": "Your order has shipped!",
  "body": "Order #12345 is on its way. Track it here.",
  "deep_link": "/orders/12345",
  "image_url": "https://cdn.app.com/products/123/thumb.webp",
  "priority": "high",
  "timestamp": 1700000000,
  "data": {
    "order_id": "12345",
    "tracking_url": "https://..."
  }
}
```

### Retry & Timeout:
- REST: connect 10s, receive 10s, max 3 retries (GET only)
- WebSocket: reconnect backoff 1s → 2s → 4s → 8s → 30s (cap)
- FCM: handled by Google/Apple infrastructure (no client retry needed)

---

## Step 8 — Offline Support & Sync (35–40 min)

### Notification Cache (Drift):

```sql
CREATE TABLE cached_notifications (
  id TEXT PRIMARY KEY,
  category TEXT,
  type TEXT,
  title TEXT,
  body TEXT,
  deep_link TEXT,
  image_url TEXT,
  is_read INTEGER DEFAULT 0,
  timestamp INTEGER,
  data TEXT,          -- JSON
  cached_at INTEGER
);

CREATE TABLE notification_preferences (
  category TEXT PRIMARY KEY,
  is_enabled INTEGER DEFAULT 1,
  quiet_hours_start INTEGER,  -- minutes from midnight
  quiet_hours_end INTEGER
);

CREATE TABLE pending_reads (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  notification_id TEXT,
  created_at INTEGER
);
```

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| View notification center | Show cached notifications from Drift. |
| Mark as read | Update Drift. Queue in pending_reads. Sync when online. |
| Update preferences | Update Drift. Queue. Sync when online. |
| Receive push (FCM) | ✅ OS handles delivery even offline (shown when online). |
| Receive WebSocket (in-app) | ❌ Missed. Synced via REST on reconnect. |
| Scheduled local notification | ✅ Works offline (OS-level scheduling). |

### Sync Flow:

```
[Network restored]
  → Sync pending_reads:
    → POST /api/notifications/batch-read { ids: [...] }
    → Clear pending_reads table
  → Sync missed notifications:
    → GET /api/notifications?since={last_sync_timestamp}
    → Merge into Drift cache
  → Sync preferences:
    → PUT /api/notifications/preferences (if changed offline)
```

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: auth + Drift. Notification center loads from cache. WebSocket connects after first frame. Defer FCM token registration. |
| **UI** | `ListView.builder` for notification list. `Dismissible` for swipe-to-dismiss. `RepaintBoundary` on notification cards. Badge count: cached, updated via stream. Image: `cached_network_image` for notification images. |
| **Network** | Cursor pagination (20/page). WebSocket: single connection, multiplexed. Batch mark-as-read API. Compress (gzip). |
| **Memory** | Notification list: keep max 200 in memory. Images: 50MB cache. Clear old notifications (30-day retention in Drift). |
| **Battery** | WebSocket only when app is foregrounded. Background: FCM only (no polling). Local notifications: scheduled via OS, no app process needed. |
| **Push Delivery** | FCM high-priority for transactional (order updates). Normal priority for promotional. FCM data-only messages for silent processing. |

### Notification Batching:

```
Server-side batching (reduce push fatigue):
  → If user has 5 unread notifications in 10 minutes:
    → Send 1 summary push: "You have 5 new notifications"
    → Don't send 5 individual pushes
  → Group by category:
    → 3 order updates → 1 push: "3 order updates"

Client-side batching:
  → In-app banners: show max 1 banner at a time
  → Queue subsequent → show after current dismisses (3s each)
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2/JWT. Token required for WebSocket + REST. FCM token tied to user session. |
| **FCM Token Security** | Token registered after auth. Unregistered on logout. Server validates token ownership before sending. |
| **WebSocket Auth** | Auth token passed in connection header. Server validates before accepting connection. |
| **Deep Link Security** | Validate deep link targets. Whitelist allowed routes. Don't navigate to arbitrary URLs from notification payload. |
| **PII in Notifications** | Don't include sensitive data in push notification body (visible on lock screen). Use "You have a new order" not "Order #12345 for ₹500." |
| **Local DB** | SQLCipher for cached notifications (may contain order/personal info). |
| **Quiet Hours** | Enforced server-side (don't send push during quiet hours). Client also suppresses in-app banners. |

### Notification Privacy:

```
Lock screen visibility:
  → Android: Notification.VISIBILITY_PRIVATE (shows "1 new notification" on lock screen)
  → iOS: Show "Notification" not content on lock screen (user setting)

Sensitive categories (payment, security):
  → Body: "New activity on your account" (vague)
  → Deep link opens app → user authenticates → sees details

Promotional notifications:
  → User must opt-in (default: off for promos)
  → Respect quiet hours (no push 10pm-8am)
  → Rate limit: max 2 promo pushes/day
```

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Notification Service: stateless, auto-scaling. Consumes Kafka events.
- Push Service: fans out to FCM/APNS. Rate-limited (FCM: 4K/sec). Batch sends.
- Cassandra: notification history, partitioned by user_id. 30-day retention.
- Redis: user online status (for WebSocket vs push routing), notification batching state.
- Kafka: event ingestion from order, promo, social services → Notification Service.

### Bulk Push (Sale Event):

```
Scenario: 50M users, flash sale notification
  → Notification Service receives bulk event
  → Batch users by FCM/APNS token
  → Push Service rate-limits: 4K/sec to FCM
  → 50M / 4K = ~3.5 hours for full delivery
  → Priority: send to active users first (DAU)
  → Spread over time to avoid thundering herd on app open
```

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| WebSocket disconnects | Auto-reconnect. Sync missed notifications via REST on reconnect. |
| FCM token expired | onTokenRefresh fires → re-register with server. |
| FCM delivery fails | FCM retries ( exponential backoff by Google). If permanently failed, mark device as inactive. |
| App killed, push received | OS shows notification. On tap: app opens → deep link → sync. |
| Push received but app can't process (corrupt data) | Show generic notification. Log error. Don't crash. |
| Server sends duplicate notification | Client deduplicates by notification_id in Drift. |
| Notification center load fails | Show cached notifications + "couldn't refresh." |
| User changes preferences while offline | Queue in Drift. Sync on reconnect. Server is source of truth. |
| Device token changes (app reinstall) | onTokenRefresh → re-register. Old token invalidated server-side. |
| Quiet hours overlap with critical alert | Critical alerts (security) bypass quiet hours. Transactional alerts respect quiet hours. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| WebSocket (foreground) + FCM (background) | Best of both worlds, but two code paths for notification handling. Unified in NotificationBloc. |
| Cursor pagination for history | Stable, but can't jump to specific date. Acceptable for notifications. |
| Notification batching | Reduces spam, but user may miss individual notification context. Show summary + expand on tap. |
| 30-day notification history | Balances storage vs. usefulness. Old notifications rarely accessed. |
| FCM data-only messages | More control (custom UI), but no system notification if app is killed (Android). Use notification+data for reliability. |
| Server-side quiet hours | Prevents unnecessary pushes, but adds latency (check before send). Acceptable. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Deep link parsing, notification deduplication, quiet hours logic, preference management |
| **Integration** | WebSocket notification flow, FCM token registration, REST sync with mock server |
| **E2E** | Trigger notification server-side → receive via WebSocket (foreground) → verify display → background app → send push → verify system notification → tap → verify deep link |
| **Performance** | Notification center load (1000 items), WebSocket reconnect time, badge count update latency |
| **Observability** | Crashlytics, Firebase Performance (WebSocket latency, API latency), custom events (delivery rate, open rate, dismiss rate, sync failure rate) |

---

## Summary

> *"The app uses Flutter Clean Architecture with a unified NotificationBloc that reconciles three notification channels: WebSocket for real-time in-app notifications (foreground), FCM/APNS for push notifications (background), and REST for history/sync. On app open, the bloc syncs missed notifications via REST, deduplicates against the Drift cache, and presents a unified view. Deep links are parsed uniformly from both WebSocket and FCM payloads. User preferences (per-category opt-in, quiet hours) are enforced server-side and cached locally. Push notifications use VISIBILITY_PRIVATE on lock screen for privacy. The system handles bulk pushes (sale events) with rate-limited FCM delivery and user batching. Offline support includes cached notifications, queued mark-as-read actions, and local scheduled notifications."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
