# Example: Design a Social Media Feed (Instagram/Twitter-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile social media feed application like Instagram/Twitter for 100 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter cross-platform."*
2. Feed type? → *"Chronological + algorithmic hybrid."*
3. Content types? → *"Text posts, images, videos, carousel."*
4. Interactions? → *"Like, comment, share, bookmark."*
5. Infinite scroll? → *"Yes."*
6. Offline support? → *"Read cached feed offline, actions queued."*
7. Push notifications? → *"Yes, for likes/comments/follows."*
8. Scale? → *"100M users, ~50M DAU."*

**Summary:**
- **Functional**: Feed (infinite scroll), post creation, like/comment/share/bookmark, user profiles, follow/unfollow, push notifications
- **Non-functional**: <2s feed load, smooth infinite scroll (60fps), offline feed reading, 100M users, 50M DAU

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Feed (algorithmic + chronological)
- Post interactions (like, comment, share, bookmark)
- Image/video display in feed
- Infinite scroll pagination
- Offline feed caching + action queue
- Push notifications

**Out of scope:**
- Post creation flow (mention, don't deep dive)
- Stories/Reels (mention as future)
- DM/messaging (separate system)
- Search/discovery (mention as future)

---

## Step 3 — Identify Constraints (5 min)

```
100M users, ~50M DAU
Feed requests: ~5B/day (avg user scrolls 100 posts/day)
Posts/day: ~500M
Avg post: 1 image (500KB) + text (200B)
Video posts: 20% of feed, avg 15s, 5MB
Feed page size: 10 posts
Cache hit target: 80%+ for feed
Target: <2s feed load, <500ms pagination, 60fps scroll
Devices: 65% Android, must support 2GB RAM
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Mobile App (Flutter)                       │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │   Feed   │ │ Post   │ │ Profile  │ │  Notif      │ │
│  │  Screen  │ │ Detail │ │  Screen  │ │  Screen     │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   WebSocket   Local DB                    │
│   (feed, posts (real-time    (Drift:                   │
│    actions)   notifs)        cached feed,             │
│                                    pending actions)    │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + CDN (images, videos)
        │ + Load Bal.  │
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │  Feed   │ │  Post    │ │  Notif    │
  │ Service │ │ Service  │ │  Service  │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │ Redis  │ │Cassandra│ │PostgreSQL  │
  │(feed   │ │(posts)  │ │+ Redis    │
  │ cache) │ └─────────┘ │(notifs)   │
  └────────┘             └───────────┘
       │
  ┌────▼────┐
  │Kafka    │
  │(events: │
  │ likes,  │
  │ follows)│
  └─────────┘
  
S3/GCS + CDN ← Media storage (images, videos)
FCM/APNS ← Push notifications
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: FeedPage (CustomScrollView),       │
│           PostCard, CommentSheet,             │
│           ProfilePage                         │
│  BLoCs: FeedBloc, PostBloc,                   │
│         NotificationBloc, ProfileBloc         │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - GetFeedUseCase (paginated)                 │
│  - LikePostUseCase                            │
│  - CommentOnPostUseCase                       │
│  - BookmarkPostUseCase                        │
│  - GetCommentsUseCase                         │
│  Repositories (abstract):                     │
│  - FeedRepository                             │
│  - PostRepository                             │
│  - NotificationRepository                     │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: FeedApi, PostApi (Dio)              │
│  Real-time: NotificationWebSocket             │
│  Local: FeedDao, PendingActionDao (Drift)    │
│  Media: ImageCacheManager, VideoPlayer        │
└─────────────────────────────────────────────┘
```

### Key Design: FeedBloc (Pagination + Cache)

```
┌──────────────────────────────────────────────┐
│              FeedBloc                          │
├──────────────────────────────────────────────┤
│  State:                                       │
│  - FeedLoaded(List<Post>, hasMore, isLoading)│
│  - FeedLoading (initial)                     │
│  - FeedError(message)                         │
│                                                │
│  Events:                                      │
│  - LoadFeed (initial: cursor = null)          │
│  - LoadMoreFeed (cursor = lastPostId)         │
│  - RefreshFeed (pull-to-refresh)              │
│  - PostLiked(postId)                          │
│  - PostBookmarked(postId)                     │
│                                                │
│  Logic:                                       │
│  - Initial load: cache-first, then refresh   │
│  - Pagination: network-only, append to list  │
│  - Like: optimistic UI + queue if offline     │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"The FeedBloc manages pagination state separately from initial load. On first open, it shows cached feed immediately (cache-first), then silently refreshes from network. Pagination is network-only because we can't predict what comes next. Likes and bookmarks use optimistic UI — the user sees the change instantly, and if offline, the action is queued. This gives a snappy, always-responsive feel."*

---

## Step 6 — Data Flow (25–30 min)

### Feed Load Flow (Cache-First + Stale-While-Revalidate):

```
FeedPage
  → FeedBloc.add(LoadFeed)
    → GetFeedUseCase.call(cursor: null)
      → FeedRepository.getFeed()
        → Check Drift (cached feed)
          → Has cache?
            → YES: emit FeedLoaded(cached) immediately
                   THEN fetch from network in background
                   → On network success: update Drift, emit FeedLoaded(fresh)
            → NO: fetch from FeedApi
              → Save to Drift
              → emit FeedLoaded(fresh)
```

### Pagination Flow:

```
User scrolls to bottom
  → FeedBloc.add(LoadMoreFeed(cursor: lastPostId))
    → FeedBloc sets isLoading = true (show loading indicator)
    → GetFeedUseCase.call(cursor: lastPostId)
      → FeedRepository.getFeed(cursor)
        → FeedApi: GET /api/feed?cursor=xxx&limit=10
          → Append new posts to existing list
          → Save appended posts to Drift
          → emit FeedLoaded(allPosts, hasMore: newPosts.length == 10)
    → FeedBloc sets isLoading = false
```

### Like Flow (Optimistic + Offline Queue):

```
PostCard (user taps like)
  → FeedBloc.add(PostLiked(postId))
    → Optimistic: immediately update like count + heart animation
    → LikePostUseCase.call(postId)
      → PostRepository.like(postId)
        → If online:
            → POST /api/posts/{id}/like
            → On success: done
            → On failure: revert optimistic update, show error
        → If offline:
            → Save to pending_actions table (action: LIKE, postId)
            → WorkManager syncs when online
            → Don't revert — keep optimistic state
```

### Notification Flow:

```
NotificationWebSocket receives event
  → NotificationBloc.add(NotificationReceived)
    → Update notification badge count
    → If on notification screen: prepend to list
    → If app in background: show local notification + update badge
```

---

## Step 7 — Networking (30–35 min)

### REST API Design:

```
GET  /api/feed?cursor=&limit=10            → Feed (cursor pagination)
GET  /api/posts/{id}                        → Post detail
GET  /api/posts/{id}/comments?cursor=       → Comments (paginated)
POST /api/posts/{id}/like                    → Like post
DELETE /api/posts/{id}/like                  → Unlike post
POST /api/posts/{id}/comments               → Comment
POST /api/posts/{id}/bookmark               → Bookmark
GET  /api/notifications?cursor=            → Notifications
```

### Pagination Strategy — Cursor-Based:

```
GET /api/feed?cursor=eyJpZCI6IjEyMzQ1In0&limit=10

Response:
{
  "posts": [...10 posts...],
  "next_cursor": "eyJpZCI6IjEyMzU2In0",  // null if no more
  "has_more": true
}
```

> **Why cursor, not offset?** Offset breaks when new posts are inserted between paginated requests. Cursor (last post ID) is stable.

### Image Loading Strategy:

```
Post image:
  1. Check memory cache (Flutter ImageCache)
  2. Check disk cache (cached_network_image)
  3. Network: fetch from CDN
     → CDN serves WebP (smaller) for supported devices
     → Multiple resolutions: thumbnail (150px), medium (750px), full (1080px)
     → CDN Cache-Control: public, max-age=31536000 (1 year, immutable URLs)
```

### Retry & Timeout:
- REST: connect 10s, receive 15s, max 3 retries (only GET, not POST)
- WebSocket: reconnect backoff 1s → 2s → 4s → 8s → 30s (cap)
- Image loading: no retry (just show placeholder on failure)

---

## Step 8 — Offline Support & Sync (35–40 min)

### Cached Feed (Drift):

```sql
CREATE TABLE cached_posts (
  id TEXT PRIMARY KEY,
  author_id TEXT,
  author_name TEXT,
  author_avatar TEXT,
  content TEXT,
  image_url TEXT,
  video_url TEXT,
  like_count INTEGER,
  comment_count INTEGER,
  is_liked INTEGER DEFAULT 0,
  is_bookmarked INTEGER DEFAULT 0,
  created_at INTEGER,
  cached_at INTEGER
);
```

### Pending Actions Queue (Drift):

```sql
CREATE TABLE pending_actions (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  action_type TEXT,      -- LIKE, UNLIKE, BOOKMARK, COMMENT
  post_id TEXT,
  payload TEXT,          -- JSON (comment text, etc.)
  idempotency_key TEXT,
  status TEXT,           -- PENDING, SYNCING, DONE, ERROR
  created_at INTEGER,
  retry_count INTEGER DEFAULT 0
);
```

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| View feed | Show cached feed from Drift. Mark "offline — showing cached." |
| Like/Unlike | Optimistic update. Queue in pending_actions. Sync when online. |
| Bookmark | Optimistic update. Queue. Sync when online. |
| Comment | Save locally. Queue. Sync when online. Show "pending" label. |
| Paginate | **Block**: Can't paginate offline. Show "Connect for more." |
| View notifications | Show cached notifications. No real-time updates. |

### Sync Flow:

```
[Network restored]
  → WorkManager triggers ActionSyncWorker
  → Query pending_actions WHERE status = PENDING ORDER BY created_at
  → For each action:
      → POST/DELETE with Idempotency-Key
      → Success: mark DONE, remove from queue
      → Conflict (e.g., already liked): mark DONE (idempotent)
      → Failure: increment retry_count, backoff
      → Max retries (5): mark ERROR
```

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: auth + Drift. Feed loads from cache immediately. Defer analytics, notifications WebSocket. |
| **UI** | `CustomScrollView` with `SliverList` for feed. `RepaintBoundary` on each PostCard. `const` widgets. `AutomaticKeepAliveClientMixin` to preserve scroll position. Image: `cached_network_image` with fade-in placeholder. Video: auto-pause off-screen, autoplay only on-screen. |
| **Network** | Cursor pagination (10/page). CDN for all media. WebP images. Gzip. ETag for feed cache validation. |
| **Memory** | Image cache: 100MB memory, 500MB disk. Feed list: keep max 200 posts in memory (evict oldest when scrolling far). Video: preload only current + next. |
| **Battery** | No background polling. WebSocket only for notifications (or FCM only). Video autoplay disabled on cellular (user setting). |
| **Scroll** | 60fps target: decode images off main thread. Use `precacheImage` for next visible post. Throttle like animation. |

### Feed Scroll Optimization (Critical):

```dart
// Use AutomaticKeepAliveClientMixin to preserve state
class FeedPage extends StatefulWidget {
  // Keeps feed position when switching tabs
}

// Each PostCard wrapped in RepaintBoundary
RepaintBoundary(
  child: PostCard(post: post),
)

// Video: only play when visible
VisibilityDetector(
  key: Key(post.id),
  onVisibilityChanged: (info) {
    if (info.visibleFraction > 0.5) {
      videoPlayer.play();
    } else {
      videoPlayer.pause();
    }
  },
  child: VideoPlayer(post.videoUrl),
)
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2/JWT. Access token (15 min) + refresh token (30 days) in Keystore/Keychain. |
| **API Security** | SSL pinning. TLS 1.2+. Rate limiting on API gateway. |
| **Content Security** | CDN signed URLs for media (expire in 1 hour). Prevent hotlinking. |
| **Local DB** | SQLCipher for cached posts (may contain private content). |
| **Deep Link Security** | Validate deep links. Don't auto-navigate to arbitrary URLs. |
| **User Privacy** | Don't track scroll behavior in logs. Anonymize analytics events. |
| **Report/Block** | Blocked users filtered server-side. Client applies hide immediately. |

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Feed Service: pre-computed feed in Redis (fan-out on write for timeline)
- CDN: all media served from edge (CloudFront/Cloudflare)
- Cassandra: posts stored, partitioned by user_id
- Kafka: events for like counts, notification fan-out, analytics
- Read replicas: heavy read load (5B feed requests/day)

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| Network down | Show cached feed. Queue likes/bookmarks. |
| API 500 on feed load | Show cached feed + "couldn't refresh" banner. |
| API 500 on pagination | Show "failed to load more" with retry button. Don't lose existing posts. |
| Image fails to load | Show placeholder. Retry on scroll back into view. |
| Video fails to load | Show thumbnail + play button. Retry on tap. |
| Like fails to sync | Keep optimistic state. Retry in background. If conflict (already liked), accept server state. |
| WebSocket disconnects | Reconnect. FCM still delivers notifications. Missed notifications fetched on reconnect. |
| Cache corruption | Detect on Drift open. Clear and re-fetch from network. |
| Scroll position lost | Use `PageStorageKey` to persist scroll offset across tab switches. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| Cache-first feed | Instant load, but may show stale posts. Mitigated by stale-while-revalidate. |
| Optimistic likes | Snappy UX, but like count may be wrong if sync fails. Acceptable — corrected on next feed refresh. |
| Cursor pagination | Stable, but can't jump to page 5. Acceptable for feeds (users scroll linearly). |
| WebP images | 30% smaller, but not supported on very old Android (<4.0). Fallback to JPEG. |
| Video autoplay | Engaging, but data + battery cost. Disabled on cellular by default. |
| Feed cache size (200 posts) | Balances memory vs. scroll-back experience. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Pagination logic, like optimistic update, cursor generation, cache TTL logic |
| **Integration** | FeedRepository + Drift (in-memory), pending action sync with mock server |
| **E2E** | Scroll feed → like → comment → bookmark → go offline → like → reconnect → verify sync |
| **Performance** | 1000-post scroll (fps, memory), image cache hit rate, feed load time |
| **Observability** | Crashlytics, Firebase Performance (API latency), custom events (feed cache hit rate, sync failure rate, scroll depth) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. The feed is cache-first with stale-while-revalidate for instant load. Pagination is cursor-based (10 posts/page) for stability under new post insertion. Likes and bookmarks use optimistic UI with offline action queue synced via WorkManager. Images load from CDN via cached_network_image with multi-resolution WebP. Videos autoplay only when visible using VisibilityDetector. Performance targets 60fps scroll through RepaintBoundary, memory-bounded image cache, and lazy video loading. Security includes OAuth2, SSL pinning, signed CDN URLs, and encrypted local DB. The feed works offline with cached posts and queued actions."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
