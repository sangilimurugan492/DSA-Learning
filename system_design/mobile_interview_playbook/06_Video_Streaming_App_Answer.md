# Structured Interview Answer: Design a Video Streaming App

> **Question**: *"Design a mobile video streaming application like Netflix/YouTube."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Streaming live content, on-demand, or both?
- Video quality selection (auto/manual)?
- Offline download for later viewing?
- Subtitles/captions required?
- DRM (Digital Rights Management) for premium content?
- Search and recommendations?
- Chromecast/AirPlay support?

**Assumed:** Flutter, on-demand + live, adaptive bitrate (auto), offline downloads yes, subtitles yes, DRM yes, search yes, cast out of scope.

---

## Step 2 — Define Scope

```
IN SCOPE: Browse content, stream video (HLS adaptive), offline download, subtitles, search, watch history, resume playback
OUT OF SCOPE: Content creation/upload, live streaming production, Chromecast/AirPlay, admin panel
```

---

## Step 3 — Constraints

```
Functional: Browse catalog, stream video with adaptive bitrate, download for offline, subtitles, resume playback
Non-Functional: 50M users, ~5M concurrent streams, < 2s video start time, minimal buffering, battery-efficient playback
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
         REST (catalog, search) + HLS (video) + DRM License API
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
 Catalog Service    Streaming Service       DRM Service
 (browse, search)   (HLS manifest, CDN)    (license, keys)
    │                      │                      │
 Recommendation      Media Storage           Widevine/FairPlay
 Service             (S3 + CloudFront)       License Server
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis      │
                    │ + S3 + CDN   │
                    └──────────────┘
```

> "Video segments are served from CDN (CloudFront). The mobile client uses HLS (HTTP Live Streaming) for adaptive bitrate. DRM licenses are fetched from Widevine (Android) / FairPlay (iOS)."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  CatalogGrid, VideoPlayer,   │
│  DownloadManager, SearchBar  │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  CatalogBloc, PlayerBloc,   │
│  DownloadBloc, SearchBloc    │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  GetCatalogUseCase            │
│  StreamVideoUseCase           │
│  DownloadVideoUseCase         │
│  ResumePlaybackUseCase        │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  CatalogRepository            │
│  VideoRepository (HLS)        │
│  DRMRepository (license)      │
└──────────┬───────────┬──────┘
           ↓           ↓
      REST + HLS     Local DB
      (Dio)          (Drift)
```

---

## Step 6 — Data Flow

### Video Playback:
```
User taps video
  ↓
PlayerBloc → StreamVideoUseCase
  ↓
GET /api/videos/{id}/manifest → HLS manifest URL
  ↓
Fetch DRM license: POST /drm/license (content_id, device_id)
  ↓
Widevine/FairPlay returns decryption key
  ↓
VideoPlayer widget loads HLS manifest
  ↓
HLS adaptive bitrate:
  - Player measures bandwidth
  - Selects appropriate quality (240p, 480p, 720p, 1080p)
  - Switches seamlessly mid-playback
  ↓
Buffer ~10s ahead → start playback
  ↓
Every 10s: POST /api/progress (video_id, position) → resume later
```

### Offline Download:
```
User taps "Download"
  ↓
DownloadBloc → DownloadVideoUseCase
  ↓
GET /api/videos/{id}/download-manifest
  → Returns segment URLs + DRM offline license
  ↓
WorkManager downloads segments in background
  ↓
Progress bar updates UI
  ↓
Store encrypted segments in app sandbox
  ↓
DRM offline license (time-limited, e.g., 30 days)
  ↓
Playback: decrypt locally with offline license
```

| Data | Strategy | TTL |
|------|----------|-----|
| Catalog | Cache-first | 30 min |
| Search results | Network-first | 5 min |
| Video segments | CDN + disk cache | Varies |
| Watch progress | Local-first + sync | N/A |
| Downloads | Local (encrypted) | DRM license expiry |

---

## Step 7 — Networking

```
REST:
  GET /catalog (cursor pagination)
  GET /videos/{id}/manifest (HLS URL)
  POST /drm/license (fetch decryption key)
  POST /progress (watch position, fire-and-forget, batch every 10s)

HLS Streaming:
  GET manifest.m3u8 → list of segments + bitrate variants
  GET segment_001.ts, segment_002.ts, ... (from CDN)
  Adaptive bitrate: player auto-selects based on bandwidth

DRM:
  Android: Widevine (ExoPlayer integration)
  iOS: FairPlay (AVPlayer integration)
  Flutter: platform channels to native players

Buffering strategy:
  - Initial buffer: 10s before playback starts
  - Running buffer: maintain 10-30s ahead
  - ABR: switch to lower quality if buffer < 5s
```

---

## Step 8 — Offline Support

```
Download flow:
  → User selects quality (480p recommended for storage)
  → WorkManager downloads HLS segments in background
  → Segments encrypted with DRM offline license
  → Stored in app sandbox (not accessible to other apps)
  → License valid for N days (configurable per content)
  → On expiry: delete segments, show "Download expired"

Playback (offline):
  → Read segments from local storage
  → Decrypt with offline DRM license
  → No network needed
  → Progress still tracked locally, synced when online
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load catalog from local DB, show "Continue Watching" immediately |
| **UI** | Video player as platform view, thumbnail lazy loading, preload next row thumbnails |
| **Network** | CDN for segments, ABR adapts to bandwidth, progressive download |
| **Memory** | Dispose video player on screen exit, release DRM session, clear segment buffer |
| **Battery** | Hardware decoding (not software), dim screen option, avoid background downloads on cellular |

> "Video start time is critical — under 2 seconds. I achieve this by starting playback after just 10s of buffer and using ABR to start at a lower quality, then ramp up."

---

## Step 10 — Security

```
DRM:
  → Widevine (Android) / FairPlay (iOS) for content protection
  → Encrypted segments — can't be extracted and played elsewhere
  → Offline licenses are time-limited and device-bound
  → License server validates device + user entitlements

Network:
  → TLS 1.2+ for all API calls
  → Certificate pinning on REST + DRM license API
  → Signed CDN URLs (time-expiring) for video segments

Auth:
  → OAuth2, token in Keystore/Keychain
  → Entitlement check: does user have subscription? → serve or deny
  → Device limit: max N concurrent streams per account

Data:
  → Downloaded videos encrypted (DRM), in app sandbox
  → Never store decryption keys outside DRM system
```

---

## Step 11 — Scalability

- CDN for all video segments (CloudFront/Cloudflare) — edge caching
- HLS adaptive bitrate reduces bandwidth for slow connections
- Catalog: Redis cache + cursor pagination
- Recommendations: precompute and cache per user
- Watch progress: write to Kafka → batch process (don't write to DB per event)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Network slow | ABR drops to lower quality, maintain playback |
| Network down (streaming) | Show "No internet", pause playback, resume on reconnect |
| CDN segment 404 | Retry next segment, fallback to different CDN |
| DRM license fetch fails | Show "License error", cannot play content |
| Download interrupted | Resume download from last segment (not restart) |
| Offline license expired | Delete download, show "Download expired, re-download" |
| App killed during playback | On relaunch: resume from last progress position |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Streaming protocol | HLS | DASH, RTMP | HLS is industry standard, native iOS support, wide CDN compatibility |
| Player | Native (ExoPlayer/AVPlayer) | Pure Flutter video | DRM support, ABR, hardware decoding |
| DRM | Widevine + FairPlay | Custom encryption | Industry standard, studio-approved, device-bound |
| Downloads | HLS segments + offline DRM | Single MP4 file | ABR, standard format, DRM-compatible |
| Progress sync | Batch every 10s | Real-time | Reduce API load, acceptable latency for resume |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Download progress logic, ABR quality selection, resume position |
| Integration | HLS manifest parsing, DRM license flow, catalog + local DB |
| E2E | Browse → play → pause → resume → download → offline play |
| Performance | Video start time (< 2s), rebuffer rate, ABR switch frequency |
| Observability | Playback failures, DRM error rate, CDN cache hit ratio, download completion rate |
