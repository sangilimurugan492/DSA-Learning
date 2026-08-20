# Example: Design a Video Streaming App (YouTube/Netflix-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile video streaming application like YouTube/Netflix for 200 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter cross-platform."*
2. Content type? → *"Short-form (reels) + long-form (10min-2hr videos)."*
3. Live streaming? → *"No, focus on VOD (video on demand)."*
4. Offline download? → *"Yes, users can download for offline viewing."*
5. Adaptive quality? → *"Yes, auto-adjust based on network."*
6. Recommendations? → *"Yes, personalized feed."*
7. Scale? → *"200M users, ~20M DAU."*

**Summary:**
- **Functional**: Video feed, playback (adaptive streaming), search, recommendations, offline download, watch history, continue watching
- **Non-functional**: <3s video start, adaptive bitrate, battery-efficient playback, 200M users, 20M DAU, offline download support

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Video feed (recommended + categories)
- Video playback with adaptive bitrate streaming (HLS/DASH)
- Offline download with DRM
- Watch history + continue watching
- Search

**Out of scope:**
- Video upload/creation (mention, don't deep dive)
- Live streaming (mention as future)
- Comments/social features (mention as future)

---

## Step 3 — Identify Constraints (5 min)

```
200M users, ~20M DAU
Video views/day: ~2B
Avg video length: 10 min (long-form), 30s (short-form)
Video qualities: 144p, 240p, 360p, 480p, 720p, 1080p, 4K
Storage: ~10PB video content (encoded in multiple resolutions)
Bandwidth: ~500 Gbps peak
Target: <3s video start (time-to-first-frame), <500ms seek latency
Download: max 10 videos offline, 30-day expiry
Devices: 65% Android, must support 2GB RAM low-end at 360p
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Mobile App (Flutter)                       │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │  Feed /  │ │ Video  │ │ Download │ │  Search     │ │
│  │ Discover │ │ Player │ │ Manager  │ │  Screen     │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   HLS/DASH    Local Storage               │
│   (catalog,   (video CDN   (downloads,                │
│    history)   streaming)   cache, DRM)                │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + CDN (HLS segments, thumbnails)
        │ + Load Bal.  │
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │Catalog  │ │Recommend │ │  History  │
  │Service  │ │ Service  │ │  Service  │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │PostgreSQL│ │ML Model│ │Cassandra   │
  │+ Redis  │ │(rankings)│ │(watch hist)│
  └────────┘ └────────┘ └───────────┘
       │
  ┌────▼──────────────────────┐
  │  Video Pipeline:           │
  │  Upload → Transcode →      │
  │  HLS/DASH segments →       │
  │  Multi-resolution → CDN    │
  └───────────────────────────┘
  
S3/GCS ← Original video storage
CDN (CloudFront/Akamai) ← HLS segments, thumbnails
DRM (Widevine/FairPlay) ← Content protection
FCM/APNS ← Push notifications
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: FeedPage, VideoPlayerPage,         │
│           DownloadQueuePage, SearchPage       │
│  BLoCs: FeedBloc, PlayerBloc,                │
│         DownloadBloc, SearchBloc             │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - GetFeedUseCase                             │
│  - PlayVideoUseCase (resolution selection)    │
│  - DownloadVideoUseCase                       │
│  - SaveWatchProgressUseCase                   │
│  - SearchVideosUseCase                        │
│  Repositories (abstract):                     │
│  - VideoRepository                            │
│  - DownloadRepository                         │
│  - WatchHistoryRepository                     │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: VideoApi, SearchApi (Dio)           │
│  Streaming: HLS Player (video_player /       │
│             better_player)                    │
│  Downloads: DownloadManager (WorkManager)    │
│  Local: DownloadDao, CacheDao (Drift)        │
│  DRM: Widevine (Android) / FairPlay (iOS)   │
└─────────────────────────────────────────────┘
```

### Key Design: PlayerBloc (Adaptive Streaming)

```
┌──────────────────────────────────────────────┐
│              PlayerBloc                        │
├──────────────────────────────────────────────┤
│  Responsibilities:                             │
│  - Manage HLS/DASH playback                    │
│  - Monitor network bandwidth                   │
│  - Auto-switch resolution:                     │
│    * <1 Mbps → 240p                            │
│    * 1-3 Mbps → 480p                           │
│    * 3-6 Mbps → 720p                           │
│    * >6 Mbps → 1080p                           │
│  - Handle buffering state                      │
│  - Track watch progress (save every 10s)      │
│  - Handle seek (preload segment)              │
│  - Battery: pause on background               │
├──────────────────────────────────────────────┤
│  States:                                       │
│  - Playing(position, duration, resolution)    │
│  - Buffering(position)                         │
│  - Paused(position)                            │
│  - Error(message)                              │
└──────────────────────────────────────────────┘
```

### Key Design: DownloadBloc (Offline Downloads)

```
┌──────────────────────────────────────────────┐
│              DownloadBloc                     │
├──────────────────────────────────────────────┤
│  Responsibilities:                             │
│  - Queue download requests                     │
│  - Download HLS segments via WorkManager       │
│  - Store encrypted locally (DRM license)      │
│  - Track download progress                     │
│  - Enforce limits: max 10 downloads, 30-day   │
│    expiry, Wi-Fi only (optional)              │
│  - Check DRM license validity before playback │
├──────────────────────────────────────────────┤
│  Download states per video:                   │
│  - QUEUED → DOWNLOADING → DOWNLOADED          │
│  - QUEUED → DOWNLOADING → FAILED (retry)      │
│  - DOWNLOADED → EXPIRED (after 30 days)      │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"I isolate video playback in PlayerBloc which manages adaptive bitrate switching based on real-time bandwidth monitoring. The DownloadBloc handles offline downloads as a background WorkManager task with DRM license management. This separation means playback and download are independent — a user can watch one video while another downloads. The HLS protocol handles resolution switching server-side via manifest files, so the player just needs to select the right variant stream."*

---

## Step 6 — Data Flow (25–30 min)

### Video Playback Flow:

```
FeedPage (user taps video)
  → Navigate to VideoPlayerPage
  → PlayerBloc.add(PlayVideo(videoId, startPosition))
    → PlayVideoUseCase.call()
      → VideoRepository.getStreamUrl(videoId)
        → GET /api/videos/{id}/stream → returns HLS manifest URL
      → Initialize HLS player with manifest URL
      → Player loads manifest, selects initial quality based on:
        → Last known bandwidth
        → User preference (default: auto)
        → Device capability (low-end → start at 360p)
      → Time-to-first-frame: target <3s
      → Player emits Playing state
      → Every 10s: save watch progress
        → Save to Drift (local)
        → Sync to History Service (fire-and-forget)
```

### Adaptive Bitrate Switching:

```
During playback:
  → Player monitors download speed of HLS segments
  → If buffer is healthy and bandwidth allows → upgrade quality
  → If buffer is depleting → downgrade quality
  → If user manually selects quality → lock to that resolution

Manifest structure (HLS):
#EXTM3U
#EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=640x360
360p.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=2000000,RESOLUTION=1280x720
720p.m3u8
#EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080
1080p.m3u8
```

### Download Flow:

```
DownloadPage (user taps download)
  → DownloadBloc.add(DownloadVideo(videoId, quality))
    → Check: max 10 downloads? → reject if exceeded
    → Check: Wi-Fi only setting? → queue if on mobile
    → DownloadRepository.download(videoId, quality)
      → Fetch HLS manifest for selected quality
      → Download all video segments via WorkManager
      → Download DRM license (Widevine/FairPlay)
      → Store encrypted segments in app-private storage
      → Update progress: 0% → 100%
      → On completion: mark as DOWNLOADED, set 30-day expiry
      → On failure: retry 3x, then mark FAILED
```

### Continue Watching Flow:

```
App launch / Feed load
  → FeedBloc.add(LoadFeed)
    → GetFeedUseCase → includes "Continue Watching" section
    → For each in-progress video:
      → Show thumbnail + progress bar (watched %)
      → Tap → resume from last position
    → Watch progress synced from History Service
```

---

## Step 7 — Networking (30–35 min)

### Channel Strategy:

| Channel | Protocol | Use Case |
|---------|----------|----------|
| **REST** | HTTPS | Catalog, search, watch history, recommendations |
| **HLS/DASH** | HTTPS (CDN) | Video segment streaming |
| **WorkManager** | Background | Download segments, sync progress |
| **FCM/APNS** | Push | New content notifications |

### REST API Design:

```
GET  /api/feed                          → Recommended videos (paginated)
GET  /api/videos/{id}                   → Video metadata
GET  /api/videos/{id}/stream            → HLS manifest URL (CDN)
GET  /api/videos/{id}/download?quality= → Download manifest URL
GET  /api/search?q=                     → Search results (paginated)
POST /api/history/progress              → Save watch progress
GET  /api/history                       → Watch history (paginated)
```

### CDN Strategy:

```
Video segments served from CDN (CloudFront/Akamai):
  → Edge cache: popular videos cached at edge locations
  → Cache miss: fetch from origin (S3)
  → Segment size: 2-10 seconds (shorter = faster start, more requests)
  → Cache-Control: public, max-age=31536000 (immutable URLs per version)

Thumbnail images:
  → CDN with WebP format
  → Multiple sizes: 150px (feed), 480px (detail)
```

### Timeout & Retry:
- REST (metadata): connect 10s, receive 15s, max 3 retries
- HLS segment fetch: handled by player (HLS has built-in retry)
- Download: WorkManager handles retry with backoff (15 min, 30 min, 1hr)

### Bandwidth Estimation:
```dart
// Player monitors segment download time
// Estimates available bandwidth
// Adjusts quality variant accordingly

// On initial load: use last known bandwidth from Drift
// On network change (Wi-Fi → cellular): re-evaluate
```

---

## Step 8 — Offline Support & Download (35–40 min)

### Download Storage:

```
App-private storage (not accessible by other apps):
  /data/data/com.app/files/downloads/
    ├── {video_id}/
    │   ├── manifest.m3u8          ← Local HLS manifest
    │   ├── segment_001.ts          ← Encrypted video segment
    │   ├── segment_002.ts
    │   └── ...
    ├── {video_id}/
    └── download_metadata.db       ← Drift: download tracking
```

### Download Metadata (Drift):

```sql
CREATE TABLE downloaded_videos (
  video_id TEXT PRIMARY KEY,
  title TEXT,
  thumbnail_url TEXT,
  quality TEXT,              -- 360p, 720p, etc.
  file_path TEXT,           -- Local path
  total_bytes INTEGER,
  downloaded_bytes INTEGER,
  status TEXT,              -- QUEUED, DOWNLOADING, DOWNLOADED, FAILED, EXPIRED
  download_date INTEGER,
  expiry_date INTEGER,      -- download_date + 30 days
  drm_license_id TEXT,
  last_watched_position INTEGER DEFAULT 0
);
```

### DRM License Management:

```
Before playback of downloaded video:
  1. Check DRM license validity (offline license)
  2. If license expired (>30 days):
     → Connect to DRM server to renew
     → If can't renew (offline): block playback, show "license expired"
  3. If license valid: play from local storage

License renewal:
  → WorkManager checks daily for soon-to-expire licenses
  → Renews licenses for downloaded videos (if online)
  → If can't renew before expiry: mark as EXPIRED
```

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| Watch downloaded video | ✅ Play from local storage (if DRM license valid) |
| Browse feed | Show cached feed. Mark "offline." |
| Search | **Block**: "Connect to search." |
| Watch progress | Save locally. Sync when online. |
| Download new video | **Block**: "Connect to download." |
| Continue watching | Show only downloaded videos with progress. |

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: auth + Drift. Feed loads from cache. Player initialized only on video tap. Defer analytics. |
| **Video Start (TTFF)** | <3s target: Start with lowest acceptable quality (based on device), then upgrade. Preload first segment. Use CDN edge. |
| **UI** | Feed: `ListView.builder` with video thumbnails. `VisibilityDetector` to autoplay short-form only when visible. `RepaintBoundary` on thumbnails. `const` widgets. |
| **Network** | HLS adaptive streaming (auto-quality). CDN edge caching. WebP thumbnails. Gzip on REST. Preload next video in feed (Wi-Fi only). |
| **Memory** | Video player: only one active at a time. Feed: keep max 50 video metadata in memory. Image cache: 150MB. Release player on dispose. |
| **Battery** | Pause video on background. No background video preload on cellular. Download only on Wi-Fi (user setting). Screen brightness not controlled by app. |
| **Buffering** | Buffer: 10-30s ahead (adaptive). Show buffering indicator. Reduce quality before stopping. Preload on seek. |

### Video Player Optimization:

```dart
// Only one video player active at a time
// Dispose player when leaving video page
// Use better_player or chewie for HLS support

BetterPlayerConfiguration(
  autoDetectQuality: true,  // Adaptive bitrate
  startAt: startPosition,   // Resume from last position
  allowedQualityRange: {360, 480, 720, 1080},
  bufferingConfiguration: BetterPlayerBufferingConfiguration(
    minBufferMs: 10000,     // 10s buffer
    maxBufferMs: 30000,     // 30s max
  ),
)

// Short-form autoplay in feed:
VisibilityDetector(
  onVisibilityChanged: (info) {
    if (info.visibleFraction > 0.6 && isShortForm) {
      player.play();
    } else {
      player.pause();
    }
  },
)
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2/JWT. Tokens in Keystore/Keychain. |
| **Content Protection (DRM)** | Widevine (Android) / FairPlay (iOS). Encrypted HLS segments. License server issues playback keys. |
| **Download Security** | Downloaded videos encrypted with DRM. Can't be played outside the app. License expires in 30 days. |
| **API Security** | SSL pinning. TLS 1.2+. Signed CDN URLs (expire in 1 hour). |
| **Local DB** | SQLCipher for watch history (may reveal viewing preferences). |
| **Screen Recording** | `FLAG_SECURE` (Android) / screen capture prevention on DRM content. |
| **Root/Jailbreak** | Widevine security level degrades on rooted devices (L3 instead of L1). Block HD on rooted devices. |

### DRM Architecture:

```
┌──────────┐     ┌──────────┐     ┌──────────────┐
│  Mobile  │────→│ CDN      │────→│ Encrypted    │
│  Player  │     │ (HLS     │     │ Segments     │
│          │     │  stream) │     │ (S3 origin)  │
└────┬─────┘     └──────────┘     └──────────────┘
     │
     │ Request license
     ▼
┌──────────────┐
│ DRM License  │
│ Server       │
│ (Widevine/   │
│  FairPlay)   │
└──────────────┘
     │
     │ Returns decryption key
     ▼
Player decrypts segments in memory → displays video
     (Key never stored permanently on device for streaming)
```

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- CDN: multi-tier caching (edge → regional → origin). 90%+ cache hit for popular videos.
- Transcoding pipeline: upload → transcode to 5 qualities → segment → store to S3 → CDN
- Recommendation Service: ML model re-ranks feed based on watch history (batch + real-time)
- Cassandra: watch history (partitioned by user_id), high write throughput
- Redis: feed cache, trending videos, session state

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| Video fails to load | Show error + retry. Check network. Fallback to lower quality. |
| Buffering too long | Auto-downgrade quality. Show "slow connection" message. |
| CDN segment 404 | Player retries next segment. If persistent, show error. |
| Download interrupted | WorkManager resumes from where it left off (partial segments). |
| DRM license expired (offline) | Block playback. Show "license expired, connect to renew." |
| App killed during playback | Save watch progress every 10s. On reopen: offer "continue from X:XX." |
| Network switch (Wi-Fi → cellular) | Player re-evaluates bandwidth. Downgrade quality if needed. Pause downloads. |
| Storage full (download) | Check available space before download. Warn user if <500MB. |
| Background → foreground | Resume playback from last position. Re-validate DRM license. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| HLS vs DASH | HLS: better device support (native iOS). DASH: more flexible. Chose HLS for broad compatibility. |
| Adaptive bitrate vs fixed quality | Adaptive: better UX, more complex. Chose adaptive with manual override. |
| DRM (Widevine/FairPlay) | Strong content protection, but adds complexity + slight performance overhead. Required for licensing. |
| 2-10s HLS segments | Shorter = faster start/seek, but more requests. Chose 4s segments (balance). |
| Max 10 downloads | Storage management. May frustrate binge users. Acceptable — configurable. |
| 30-day download expiry | DRM/licensing requirement. Users must re-download. Acceptable for licensing compliance. |
| Preload next video | Better UX (instant play), but wastes bandwidth if user doesn't watch. Only on Wi-Fi. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Quality selection logic, download queue management, progress tracking, expiry logic |
| **Integration** | HLS player + mock CDN, DRM license flow, download + offline playback |
| **E2E** | Feed → play → seek → pause → resume → download → go offline → play downloaded → resume online |
| **Performance** | Time-to-first-frame (target <3s), seek latency (<500ms), memory during 1hr playback, battery drain |
| **Observability** | Crashlytics, Firebase Performance (API latency, CDN latency), custom events (TTFF, rebuffer rate, quality switches, download success rate) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. Video streaming uses HLS adaptive bitrate — the PlayerBloc monitors bandwidth and auto-switches between 240p-1080p. The DownloadBloc manages offline downloads via WorkManager with DRM license management (Widevine/FairPlay). Downloaded videos are encrypted and expire after 30 days. Performance targets <3s time-to-first-frame through CDN edge caching, low initial quality, and 4-second HLS segments. The feed is cache-first with stale-while-revalidate. Security includes DRM content protection, SSL pinning, signed CDN URLs, and screen recording prevention. Watch progress is saved every 10 seconds locally and synced to the backend for continue-watching."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
