# Structured Interview Answer: Design a Social Media Feed

> **Question**: *"Design a mobile social media feed like Instagram/Twitter/LinkedIn."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Text posts, images, video, or all?
- Infinite scroll feed?
- Like, comment, share functionality?
- Push notifications for interactions?
- Offline support (read cached feed)?
- Stories/reels in scope?

**Assumed:** Flutter, text + images + video, infinite scroll, likes/comments/shares yes, push yes, offline read yes, stories out of scope.

---

## Step 2 — Define Scope

```
IN SCOPE: Feed (infinite scroll), create post (text/image/video), like, comment, share, push notifications, offline feed reading
OUT OF SCOPE: Stories/reels, DM/messaging, live streaming, admin/moderation
```

---

## Step 3 — Constraints

```
Functional: Scroll feed (infinite), create post, like/comment/share, receive notifications
Non-Functional: 100M users, ~10M concurrent, < 500ms feed load (cached), 60fps scroll, offline feed reading
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
              REST (feed, posts) + FCM (notifications)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
  Feed Service       Post Service          Notification Service
  (timeline generation)  (create, like)    (FCM push)
    │                      │                      │
  Timeline Cache      Media Service         Kafka (events)
  (Redis sorted set)  (S3 + CDN)                 │
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis      │
                    │ + S3/CDN     │
                    └──────────────┘
```

> "The feed service generates timelines. For scale, we precompute feeds (fan-out on write) and cache in Redis sorted sets. The mobile client paginates with cursors."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  FeedScreen, PostCard,       │
│  CommentThread, ComposePost  │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  FeedBloc, PostBloc,         │
│  CommentBloc, NotificationBloc│
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  GetFeedUseCase (pagination) │
│  LikePostUseCase             │
│  CreatePostUseCase            │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  FeedRepository               │
│  PostRepository               │
└──────────┬───────────┬──────┘
           ↓           ↓
       REST API     Local DB
       (Dio)        (Drift/SQLite)
```

---

## Step 6 — Data Flow

### Feed Loading (Stale-While-Revalidate):
```
User opens feed
  ↓
FeedBloc → GetFeedUseCase
  ↓
FeedRepository.getFeed(cursor: last_post_id)
  ↓
Check local DB for cached feed
  ├── Hit → Show cached feed IMMEDIATELY (fast, < 100ms)
  │        ↓
  │        Background: fetch fresh feed from API
  │        ↓
  │        If new posts → prepend to feed (stale-while-revalidate)
  │
  └── Miss → GET /feed?cursor=xxx
              ↓
              Cache in local DB
              ↓
              Show feed
```

### Infinite Scroll Pagination:
```
User scrolls to bottom
  ↓
ListView detects scroll near end (200px from bottom)
  ↓
FeedBloc → GetFeedUseCase(cursor: last_post_id)
  ↓
GET /feed?cursor={last_post_id}&limit=20
  ↓
Append new posts to feed list
  ↓
Show loading indicator while fetching
  ↓
If no more posts → show "You're all caught up"
```

### Optimistic Like:
```
User taps like
  ↓
UI IMMEDIATELY shows heart filled + count+1 (optimistic)
  ↓
POST /posts/{id}/like (idempotency: same user+post = no-op)
  ↓
  ├── Success → update local DB, keep UI as-is
  └── Fail → revert UI (heart outline, count-1), show "Failed to like"
```

| Data | Strategy | TTL |
|------|----------|-----|
| Feed | Stale-while-revalidate | 5 min |
| Post images | Cache-first (CDN) | 24 hours |
| Post videos | Cache-first (CDN) | 24 hours |
| User profile | Cache-first | 10 min |
| Likes/comments | Network-first | N/A |

---

## Step 7 — Networking

```
REST:
  GET /feed?cursor={id}&limit=20  (cursor pagination)
  POST /posts (create)
  POST /posts/{id}/like (idempotent)
  POST /posts/{id}/comments
  connectTimeout: 10s, receiveTimeout: 15s

Cursor pagination (NOT offset):
  → offset pagination is O(N) for deep pages (database must skip N rows)
  → cursor pagination is O(log N) — WHERE id < cursor ORDER BY id DESC LIMIT 20

Image loading:
  → CachedNetworkImage (disk + memory cache)
  → Placeholder shimmer while loading
  → Fade-in on load

Video:
  → Preload thumbnail, lazy load video player
  → Autoplay only when in viewport (visibility detector)
```

---

## Step 8 — Offline Support

```
Offline feed reading:
  → Cached feed in local DB (last N posts)
  → Images cached on disk (CachedNetworkImage)
  → User can scroll and read cached posts

Offline actions:
  → Like: save locally, sync when online (idempotent — safe)
  → Comment: save locally, sync queue (idempotency key)
  → Create post: save draft locally, upload when online

Sync:
  → WorkManager processes queued likes/comments
  → If conflict (post deleted) → server returns 404 → remove from local DB
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load cached feed from local DB immediately (< 100ms), refresh in background |
| **UI** | ListView.builder (virtualization), const PostCard widgets, RepaintBoundary for complex cards |
| **Network** | Cursor pagination, image CDN, gzip, dedup requests |
| **Memory** | Image cache limit 100MB, video player disposed when off-screen, cancel in-flight requests on screen exit |
| **Battery** | No background polling (FCM for notifications), batch sync in WorkManager |

> "The key to 60fps scroll is virtualization — only render visible PostCards. I use ListView.builder with const widgets and RepaintBoundary to isolate repaints."

---

## Step 10 — Security

```
Auth: OAuth2, JWT in Keystore/Keychain
Network: TLS 1.2+, certificate pinning
Media: Presigned S3 URLs for upload (short-lived, time-expiring)
Content: Sanitize user-generated content (XSS prevention in webview if used)
Privacy: Respect block/mute lists (filter on client + server)
```

---

## Step 11 — Scalability

- Feed generation: fan-out on write (precompute timelines) vs fan-out on read
- For 100M users: hybrid — precompute for active users, compute on-demand for inactive
- Redis sorted set for timeline (score = timestamp)
- Cursor pagination (not offset — avoids slow COUNT queries at depth)
- CDN for all media (images, videos)
- Kafka for event streaming (likes, comments → notifications, analytics)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Network down | Show cached feed, allow offline likes (sync later) |
| API 500 | Show error, keep cached feed visible, retry button |
| Image fails to load | Placeholder + retry on tap |
| Feed exhausted | Show "You're all caught up" (no infinite spinner) |
| Like fails | Revert optimistic UI, show toast |
| Duplicate like | Server is idempotent (same user+post = no-op) |
| Post deleted while in feed | On next refresh, server returns 404 → remove from list |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Feed cache | Stale-while-revalidate | Network-first | Fast initial load + freshness |
| Pagination | Cursor | Offset | O(log N) vs O(N) at depth |
| Like | Optimistic UI | Wait for server | Instant feedback, better UX |
| Feed generation | Fan-out hybrid | Pure fan-out on write | Memory trade-off for inactive users |
| Images | CachedNetworkImage | Custom cache | Proven, disk+memory cache, placeholder support |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Pagination cursor logic, optimistic revert logic, BLoC state |
| Integration | Feed repository + local DB + API mock |
| E2E | Open feed → scroll → like → comment → create post |
| Performance | Scroll frame rate (60fps), feed load time p95, image cache hit rate |
| Observability | Feed load latency, pagination depth distribution, like success rate |
