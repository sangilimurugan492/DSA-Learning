# Structured Interview Answer: Design a Udemy-Like Learning App

> **Question**: *"Design a mobile e-learning application like Udemy/Coursera with video streaming, offline downloads, and progress tracking."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Android, iOS, or cross-platform (Flutter)?
- Video streaming with adaptive quality?
- Offline download for lectures?
- Course purchase (one-time) or subscription model?
- Quizzes and assignments?
- Multi-language subtitles?
- Certificate of completion?
- Instructor features or student-only?

**Assumed:** Flutter, HLS adaptive streaming, offline yes, one-time purchase + subscription, quizzes yes, subtitles yes, certificates yes, student app focus.

---

## Step 2 — Define Scope

```
IN SCOPE:
  • Course browse, search, detail
  • Video streaming (HLS, subtitles, speed control)
  • Offline download (DRM-protected)
  • Course purchase (one-time + subscription)
  • Progress tracking + resume
  • Quizzes & assignments
  • Certificate of completion
  • Reviews & ratings
  • Push notifications
  • Wishlist

OUT OF SCOPE:
  • Instructor app (course creation)
  • Live streaming / webinars
  • Discussion forums
  • Admin dashboard
```

---

## Step 3 — Constraints

```
Functional: Browse/search courses, stream video, download offline, purchase, track progress, take quizzes, earn certificate
Non-Functional: 50M users, ~3M concurrent streams, < 2s video start, < 500ms catalog load (cached), offline-capable, battery-efficient
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
      REST (catalog) + HLS (video) + DRM License + FCM (push)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    │ (Auth, Rate  │
                    │  Limiting)   │
                    └──────┬───────┘
                           │
    ┌──────────┬───────────┼───────────┬──────────┐
    │          │           │           │          │
 Catalog   Course     Payment     Streaming    DRM
 Service   Service    Service     Service     Service
    │          │           │       (HLS,CDN) (Widevine/
    │          │           │           │       FairPlay)
 Search     Enrollment  Stripe/     S3+CDN       │
 Service    Progress    Razorpay                 │
 (Elastic)  Quiz/Cert                             │
    │          │           │           │          │
    └──────────┴───────────┼───────────┴──────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis      │
                    │ + S3/CDN     │
                    │ + Kafka      │
                    └──────────────┘
```

> "Catalog and search use Elasticsearch for fast filtering. Video is served from CDN via HLS. DRM licenses from Widevine (Android) / FairPlay (iOS). Payment is tokenized via Stripe/Razorpay SDK."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────────────────────┐
│                    UI                        │
│  HomeScreen, CourseDetail, VideoPlayer,      │
│  MyLearning, QuizScreen, CertificateView     │
└──────────────────┬──────────────────────────┘
                   ↓
┌─────────────────────────────────────────────┐
│                  BLoC Layer                  │
│  CatalogBloc, CourseBloc, PlayerBloc,        │
│  EnrollmentBloc, ProgressBloc, QuizBloc,     │
│  DownloadBloc, PaymentBloc, AuthBloc          │
└──────────────────┬──────────────────────────┘
                   ↓
┌─────────────────────────────────────────────┐
│                Use Cases                     │
│  SearchCoursesUseCase, GetCourseDetailUseCase │
│  StreamLectureUseCase, DownloadLectureUseCase  │
│  PurchaseCourseUseCase, SubmitQuizUseCase      │
│  TrackProgressUseCase, GenerateCertificateUC   │
└──────────────────┬──────────────────────────┘
                   ↓
┌─────────────────────────────────────────────┐
│              Repository Layer                │
│  CatalogRepository, CourseRepository           │
│  VideoRepository (HLS), DRMRepository          │
│  EnrollmentRepository, ProgressRepository      │
│  DownloadRepository                            │
└────────┬───────────────────────────┬─────────┘
         ↓                           ↓
    Remote Data Sources         Local Data Sources
    ┌─────────────────┐         ┌──────────────────┐
    │ Dio (REST)      │         │ Drift (SQLite)    │
    │ HLS Client      │         │ Download Store    │
    │ DRM License API │         │ Progress Cache    │
    │ Payment SDK     │         │ Wishlist Cache    │
    └─────────────────┘         └──────────────────┘
```

### Modularization:
```
:app                        ← App shell, navigation, DI
:core:ui                    ← Shared widgets, theme, design system
:core:network               ← Dio setup, interceptors, retry
:core:database              ← Drift setup, DAOs
:core:video                 ← Video player wrapper, HLS, DRM
:core:download              ← Download manager, WorkManager
:core:common                ← Utilities, extensions, constants
:feature:auth               ← Login, register, token management
:feature:home               ← Home feed, categories, banners
:feature:catalog            ← Course list, search, filters
:feature:course_detail      ← Course detail, curriculum, reviews
:feature:player             ← Video player, subtitles, PiP
:feature:my_learning        ← Enrolled courses, progress, resume
:feature:quiz               ← Quizzes, assignments, coding exercises
:feature:checkout           ← Cart, payment, coupons
:feature:download           ← Download manager, storage settings
:feature:profile            ← Profile, settings, certificates
```

---

## Step 6 — Data Flow

### Course Browse (Cache-First):
```
User opens home
  ↓
HomeBloc → GetRecommendedCoursesUseCase
  ↓
CatalogRepository → check local DB (cache)
  ├── Hit (< 15 min old) → Show immediately + background refresh
  └── Miss → GET /catalog/recommendations
              ↓
              Cache in local DB
              ↓
              Show to user
```

### Video Playback:
```
User taps lecture
  ↓
PlayerBloc → StreamLectureUseCase
  ↓
Check: is lecture downloaded?
  ├── YES → Play from local storage (offline)
  └── NO  → GET /lectures/{id}/manifest → HLS URL
              ↓
              Fetch DRM license: POST /drm/license
              ↓
              Load HLS in video_player (platform channel)
              ↓
              ABR: auto-select quality based on bandwidth
              ↓
              Buffer 10s → start playback
              ↓
              Every 10s: POST /progress (lecture_id, position)
              ↓
              On lecture end → mark complete → update progress %
```

### Course Purchase:
```
User taps "Buy Now"
  ↓
PaymentBloc → PurchaseCourseUseCase
  ↓
POST /api/orders (idempotency key)
  Body: { course_id, payment_method, coupon_code }
  ↓
Server: charge payment → enroll user → return enrollment
  ↓
UI: "Purchase successful!" → navigate to My Learning
  ↓
FCM: "You're enrolled! Start learning"
```

### Progress Tracking & Resume:
```
User opens My Learning
  ↓
ProgressBloc → GetEnrolledCoursesUseCase
  ↓
Load from local DB (instant) + sync from API
  ↓
For each course:
  → Show progress bar (X% complete)
  → "Continue" button → resumes last lecture at last position
  ↓
ResumeLectureUseCase:
  → GET /progress/{course_id} → last_lecture_id, position_seconds
  → Navigate to player with seek position
```

### Offline Download:
```
User taps "Download" on lecture
  ↓
DownloadBloc → DownloadLectureUseCase
  ↓
GET /lectures/{id}/download-manifest
  → Returns HLS segment URLs + offline DRM license
  ↓
WorkManager downloads segments in background
  ↓
Store encrypted segments in app sandbox
  ↓
Progress bar updates in Download Manager UI
  ↓
On complete: lecture available offline
```

| Data | Strategy | TTL |
|------|----------|-----|
| Catalog / recommendations | Cache-first | 15 min |
| Course detail | Cache-first | 30 min |
| Search results | Cache-first (per query) | 5 min |
| Video segments | CDN + disk (for downloads) | DRM expiry |
| Progress | Local-first + sync | N/A |
| Enrollment | Local + sync | N/A |
| Wishlist | Local-first | N/A |

---

## Step 7 — Networking

```
REST (Dio):
  GET /catalog/recommendations
  GET /courses/{id} (detail)
  GET /courses/search?q=X&filters=Y&cursor=Z
  POST /orders (purchase — idempotency key)
  GET /progress/{course_id}
  POST /progress (update — batched every 10s)
  POST /drm/license (fetch decryption key)
  connectTimeout: 10s, receiveTimeout: 15s

HLS Streaming:
  GET manifest.m3u8 → bitrate variants + segment list
  GET segment_001.ts ... (from CDN)
  ABR: player auto-selects based on bandwidth

Retry Policy:
  GET (catalog, search): retry 3x exponential backoff
  POST /orders: idempotency key → safe to retry
  POST /progress: fire-and-forget (batched, lossy OK)

Interceptors:
  1. Auth Interceptor → Bearer token, refresh on 401
  2. Retry Interceptor → backoff for 5xx
  3. Cache Interceptor → ETag / If-None-Match
  4. Analytics Interceptor → log API metrics
```

---

## Step 8 — Offline Support & Sync

### Downloaded Content:
```
Video Lectures:
  → HLS segments downloaded via WorkManager
  → Encrypted with DRM offline license (time-limited, e.g., 30 days)
  → Stored in app sandbox
  → Playback: decrypt locally, no network needed

Attachments (PDFs, slides, source code):
  → Downloaded to app storage
  → Available offline without DRM

Quizzes:
  → Quiz questions cached locally when lecture downloaded
  → Take quiz offline → results queued
  → Sync results when online (idempotency key)
```

### Progress Sync:
```
Offline lecture completion:
  → Mark complete in local DB (sync_status = PENDING)
  → UI shows correct progress immediately
  ↓
Network restored
  ↓
WorkManager syncs:
  → POST /progress/batch [{ lecture_id, completed, position }]
  → Update sync_status = SYNCED
  → If course 100% complete → POST /certificates → generate certificate
```

### Download Manager:
```
┌──────────────────────────────────────────┐
│         Download Manager                  │
├──────────────────────────────────────────┤
│ lecture_id | status   | progress | size  │
├──────────────────────────────────────────┤
│ lec_001    | DONE     | 100%     | 45MB  │
│ lec_002    | DOWNLOAD | 67%      | 30MB  │
│ lec_003    | PAUSED   | 23%      | 12MB  │
│ lec_004    | PENDING  | 0%       | 0MB   │
└──────────────────────────────────────────┘

Settings:
  → WiFi-only downloads (default ON)
  → Max storage limit (e.g., 5GB)
  → Auto-delete after course completion (optional)
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load "Continue Learning" from local DB instantly, defer analytics 3s |
| **UI** | ListView.builder for course lists, CachedNetworkImage for thumbnails, const widgets |
| **Video** | HLS ABR (start low, ramp up), 10s initial buffer, preload next lecture manifest |
| **Network** | Cursor pagination, gzip, ETag conditional requests, CDN for all media |
| **Memory** | Video player disposed on screen exit, DRM session released, image cache 100MB |
| **Battery** | No background polling (FCM only), downloads batched via WorkManager, hardware decode |

> "Video start time under 2 seconds is critical. I achieve this by starting with a low-quality stream and ramping up, plus preloading the next lecture's manifest while the current one plays."

---

## Step 10 — Security

```
Authentication:
  → OAuth2 / OIDC
  → Access Token (15 min) + Refresh Token (30 days)
  → Stored in Flutter Secure Storage / Android Keystore / iOS Keychain
  → NEVER SharedPreferences

DRM (Video Content Protection):
  → Android: Widevine (ExoPlayer / media3)
  → iOS: FairPlay (AVPlayer)
  → Flutter: platform channels to native player
  → Offline licenses: time-limited, device-bound
  → License server validates user entitlement (purchased? subscribed?)

Network:
  → TLS 1.2+ enforced
  → Certificate pinning on REST + DRM API
  → Signed CDN URLs (time-expiring) for video segments

Payment:
  → Stripe/Razorpay SDK (tokenized, PCI-DSS compliant)
  → Never store card numbers on device
  → Idempotency key per purchase

Data at Rest:
  → SQLCipher for local DB (progress, enrollment data)
  → EncryptedSharedPreferences for prefs
  → Downloaded videos encrypted (DRM)

Anti-Piracy:
  → DRM prevents screen recording (Widevine L1)
  → Device fingerprinting (detect account sharing)
  → Concurrent stream limit (max 2 devices)
```

> "Course content is a paid asset — DRM is non-negotiable. Widevine L1 on Android prevents screen recording. Offline licenses are device-bound and time-limited."

---

## Step 11 — Scalability

- **Catalog/Search**: Elasticsearch (full-text, filters, faceted search)
- **Video Delivery**: CDN (CloudFront/Cloudflare) for HLS segments — edge caching
- **Recommendations**: Precompute per user, cache in Redis, refresh daily
- **Progress Tracking**: Write to Kafka → batch process (don't write to DB per event)
- **Cursor Pagination**: For course lists, search results (not offset)
- **Read Replicas**: Course catalog is read-heavy → scale reads
- **ABR**: Reduces bandwidth for slow connections (saves CDN costs)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Network down | Browse cached catalog, play downloaded lectures, queue progress sync |
| Video buffering | ABR drops quality, show buffering indicator |
| CDN segment 404 | Fallback to alternate CDN, retry |
| DRM license fails | Show "License error", cannot play — prompt to re-login |
| Payment fails | Show error, keep cart intact, allow retry |
| Download interrupted | Resume from last segment (not restart) |
| Offline license expired | Delete download, prompt "Re-download" |
| App killed during video | On relaunch: resume from last progress position |
| Progress sync fails | Retry in WorkManager, local DB still correct |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Video streaming | HLS | DASH, RTMP | Industry standard, native iOS, CDN-compatible |
| Video player | Native (ExoPlayer/AVPlayer) | Pure Flutter | DRM support, ABR, hardware decode, PiP |
| DRM | Widevine + FairPlay | Custom encryption | Industry standard, studio-approved, device-bound |
| Catalog cache | Cache-first (15 min TTL) | Network-first | Fast browse, offline-capable, catalog changes infrequently |
| Progress sync | Batched every 10s | Real-time per event | Reduce API load, acceptable for resume |
| Search | Elasticsearch | PostgreSQL LIKE | Fast full-text, faceted filters, relevance scoring |
| Downloads | HLS segments + offline DRM | Single MP4 | ABR, standard format, DRM-compatible |
| Payment | SDK (Stripe/Razorpay) | Custom form | PCI compliance, reduces liability |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Progress calculation, quiz grading, cache TTL logic, BLoC states |
| Integration | Repository + Drift + Dio mock, HLS manifest parsing |
| E2E | Browse → purchase → stream → complete → certificate |
| Video | Start time < 2s, rebuffer rate, ABR switch frequency |
| DRM | License fetch flow, offline playback, license expiry |
| Download | Pause/resume, WiFi-only enforcement, storage limits |
| Crash | Crashlytics — non-fatal + fatal |
| Analytics | Funnel: browse → view → purchase → start → complete |
| Observability | API latency p95, video start time p95, download success rate, DRM error rate |

---

## 🎤 Key Phrases

| When | Phrase |
|------|--------|
| Architecture | "Clean Architecture with BLoC — presentation, domain, and data layers separated for testability and flexibility." |
| Video | "HLS adaptive bitrate starts low and ramps up, achieving < 2s start time. DRM via Widevine/FairPlay protects paid content." |
| Offline | "Downloaded lectures are encrypted with DRM offline licenses. Progress is tracked locally and synced when online." |
| Caching | "Cache-first for catalog because course metadata changes infrequently. Progress is local-first — instant resume." |
| Security | "DRM is non-negotiable for paid video content. Widevine L1 prevents screen recording. Offline licenses are device-bound." |
| Trade-off | "The trade-off with DRM is complexity, but it's essential — without it, paid content can be freely pirated." |
