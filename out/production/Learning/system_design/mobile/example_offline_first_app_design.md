# Example: Design an Offline-First Mobile Application

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design an offline-first mobile application architecture. The app is a field data collection tool used by surveyors in areas with unreliable network. Surveyors collect data (forms, photos, GPS), and it must sync to the backend when connectivity returns."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android (primary, rugged devices), Flutter cross-platform."*
2. Data types? → *"Forms (text, numbers, selections), photos, GPS coordinates, timestamps."*
3. Multi-user? → *"Yes, 10,000+ surveyors, each assigned to a region."*
4. Conflict possible? → *"Yes — same location surveyed by different surveyors."*
5. Sync direction? → *"Primarily client→server (data collection). Some server→client (form templates, assignments)."*
6. Partial connectivity? → *"Yes — surveyors work in remote areas, sync when back in office/hotel."*
7. Scale? → *"10K surveyors, ~100 forms/day each, ~1M records/day."*

**Summary:**
- **Functional**: Collect survey data (forms, photos, GPS), work fully offline, sync when online, receive form template updates, track sync status
- **Non-functional**: 100% offline-capable data collection, conflict-free sync, no data loss, 10K surveyors, 1M records/day, work on rugged low-end Android devices

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Offline form data collection (dynamic forms from templates)
- Photo capture with GPS tagging
- Local storage with full offline capability
- Sync engine (client → server) with conflict resolution
- Form template distribution (server → client)
- Sync status tracking and retry

**Out of scope:**
- Real-time collaboration between surveyors
- Analytics/dashboard (separate system)
- User management (admin tool)

---

## Step 3 — Identify Constraints (5 min)

```
10,000 surveyors
~100 forms/day per surveyor → 1M records/day
Avg form: 20 fields, 2 photos (2MB each), 1 GPS coordinate
Storage per device: ~500MB for 30 days of data
Sync window: evening (6pm-10pm) when surveyors return to office
Network: 2G/3G in remote areas, Wi-Fi in office
Battery: full day of field work (8+ hours)
Devices: rugged Android, 2-4GB RAM, limited storage (8-16GB)
No data loss tolerance — every form must reach the server
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│           Field Surveyor App (Flutter/Android)          │
│                                                         │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │  Form    │ │ Camera │ │   Sync   │ │  Dashboard  │ │
│  │ Collection│ │ (Photo │ │  Manager │ │  (status)   │ │
│  │          │ │ + GPS) │ │          │ │             │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│              ┌─────▼─────┐                              │
│              │ Local DB  │ ← OFFLINE-FIRST CORE         │
│              │ (Drift +  │   All data written here      │
│              │  SQLCipher│   before anywhere else       │
│              │  )        │                              │
│              └─────┬─────┘                              │
│                    │                                    │
│              ┌─────▼─────┐                              │
│              │ Sync Engine│ ← WorkManager               │
│              │ (Queue +   │   Background sync            │
│              │  Worker)  │   when network available     │
│              └─────┬─────┘                              │
└────────────────────┼────────────────────────────────────┘
                     │ HTTPS (when available)
              ┌──────▼───────┐
              │ API Gateway  │
              └──────┬───────┘
                     │
           ┌─────────┼─────────┐
           ▼         ▼         ▼
     ┌──────────┐ ┌────────┐ ┌───────────┐
     │ Survey   │ │ Form   │ │  Media    │
     │ Service  │ │Template│ │  Service  │
     │          │ │Service │ │  (S3)     │
     └────┬─────┘ └───┬────┘ └─────┬─────┘
          │           │            │
     ┌────▼───┐ ┌────▼───┐ ┌──────▼──────┐
     │PostgreSQL│ │PostgreSQL│ │S3/GCS     │
     │(survey  │ │(templates)│ │(photos)   │
     │ data)   │ └────────┘ └───────────┘
     └────────┘
          │
     ┌────▼────┐
     │Kafka    │ ← Sync events (for analytics, audit)
     └─────────┘
     
Redis ← Idempotency keys, sync state, template version cache
CDN ← Form template distribution
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture (Offline-First Core):

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: FormListPage, FormEntryPage,       │
│           CameraCaptureWidget,                │
│           SyncDashboardPage, SyncStatusBadge │
│  BLoCs: FormBloc, SyncBloc,                  │
│         TemplateBloc, DashboardBloc          │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - SaveSurveyUseCase (always writes locally)  │
│  - SubmitSurveyUseCase (queues for sync)      │
│  - GetFormsUseCase (from local templates)     │
│  - CheckSyncStatusUseCase                     │
│  - SyncPendingDataUseCase                     │
│  - DownloadTemplatesUseCase                   │
│  Repositories (abstract):                     │
│  - SurveyRepository                           │
│  - TemplateRepository                          │
│  - SyncRepository                             │
│  - MediaRepository                            │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Local: SurveyDao, TemplateDao (Drift +      │
│         SQLCipher) — PRIMARY DATA SOURCE     │
│  Remote: SurveyApi, TemplateApi (Dio)        │
│  Sync: SyncEngine (WorkManager)              │
│  Media: MediaStorage (local files + S3)      │
│  GPS: Geolocator                              │
│  Camera: CameraPlugin                         │
└─────────────────────────────────────────────┘
```

### Key Design: The Sync Engine (Heart of Offline-First)

```
┌──────────────────────────────────────────────────┐
│                  Sync Engine                      │
├──────────────────────────────────────────────────┤
│  Components:                                       │
│                                                    │
│  1. SyncQueue (Drift table)                       │
│     - Stores all pending operations               │
│     - Ordered by created_at                        │
│     - Tracks: status, retry_count, idempotency_key│
│                                                    │
│  2. SyncWorker (WorkManager)                      │
│     - Triggers on: network available, manual,     │
│       periodic (every 15 min when online)         │
│     - Processes queue in order                     │
│     - Handles retries with backoff                │
│                                                    │
│  3. ConflictResolver                              │
│     - Detects conflicts (same entity, different   │
│       surveyor)                                    │
│     - Applies resolution strategy                  │
│                                                    │
│  4. SyncStatusTracker                              │
│     - Tracks: pending count, failed count,         │
│       last sync time                                │
│     - Exposes stream for UI (badge, dashboard)     │
└──────────────────────────────────────────────────┘
```

### Offline-First Principle:

> **"The local database is the source of truth. The server is a replica that eventually catches up."**

```
ALL writes go to local DB FIRST.
Reads ALWAYS come from local DB.
Sync engine pushes to server in background.
User NEVER waits for network.
```

### Why (Lead-Level Justification):

> *"In an offline-first architecture, the local database is the single source of truth for the user. Every form submission writes to Drift immediately — the user never waits for network. The Sync Engine, running via WorkManager, processes a queue of pending operations when connectivity is available. Each operation has an idempotency key so retries are safe. Conflict resolution handles cases where multiple surveyors collect data for the same location. The UI always reflects local state, with a sync status badge showing pending/ syncing/ synced/ failed counts."*

---

## Step 6 — Data Flow (25–30 min)

### Form Collection Flow (100% Offline):

```
FormEntryPage (surveyor fills form)
  → User fills: text fields, selections, captures photo, GPS auto-captured
  → User taps "Submit"
    → FormBloc.add(SubmitForm(formData))
      → SubmitSurveyUseCase.call()
        → Generate survey_id (UUID)
        → Generate idempotency_key (UUID)
        → Save to Drift:
          → surveys table (form data, GPS, timestamp, surveyor_id)
          → sync_queue table (operation: CREATE_SURVEY, entity_id, idempotency_key)
        → Save photo to local storage:
          → /surveys/{survey_id}/photo_1.jpg
          → sync_queue: (operation: UPLOAD_PHOTO, entity_id, file_path)
        → FormBloc emits FormSubmitted (show "Saved locally — will sync")
        → SyncStatusTracker: increment pending count
        → If online: trigger SyncWorker immediately
        → If offline: WorkManager will trigger when network returns
```

### Sync Flow (Background):

```
[Network available — WorkManager triggers SyncWorker]

SyncWorker:
  1. Query sync_queue WHERE status = PENDING ORDER BY created_at ASC
  
  2. For each operation:
     → UPLOAD_PHOTO:
       → Read file from local storage
       → Request presigned URL: POST /api/media/upload { type, size }
       → Upload to S3: PUT { presigned_url } (binary)
       → On success: mark DONE, save media_id
       → On failure: increment retry_count, backoff
     
     → CREATE_SURVEY:
       → Assemble payload: form_data + media_ids + GPS + surveyor_id
       → POST /api/surveys (with Idempotency-Key)
       → On success (201): mark DONE, save server_survey_id
       → On conflict (409): apply ConflictResolver
       → On failure: increment retry_count, backoff
     
     → UPDATE_SURVEY:
       → PUT /api/surveys/{id} (with Idempotency-Key)
       → Same handling
  
  3. Update SyncStatusTracker:
     → Decrement pending, increment synced or failed
     → Emit stream event → UI updates badge
  
  4. Download new templates (server → client):
     → GET /api/templates?since={last_sync_version}
     → Save to Drift (templates table)
     → Notify TemplateBloc → UI shows new forms available
```

### Conflict Resolution Flow:

```
Two surveyors collect data for Location X:

Surveyor A submits (syncs first):
  → Server stores: survey_X by surveyor_A

Surveyor B submits (syncs later):
  → POST /api/surveys (same location, different data)
  → Server detects: existing survey for Location X
  → Returns 409 Conflict: { conflict_id, existing_survey, new_survey }

ConflictResolver (client-side for B):
  → Strategy: LAST_WRITE_WINS (with timestamp)
  → If B's timestamp > A's: server accepts B's data, returns 200
  → If A's timestamp > B's: server rejects, returns 409 with A's data
  → Client marks as DONE (server is authoritative)
  → Log conflict for admin review
```

---

## Step 7 — Networking (30–35 min)

### REST API Design:

```
POST /api/surveys                        → Create survey (idempotency key)
PUT  /api/surveys/{id}                   → Update survey (idempotency key)
GET  /api/surveys?since={timestamp}      → Get survey updates (for multi-device)
POST /api/media/upload                   → Get presigned URL for photo upload
GET  /api/templates?version={v}          → Get form templates (versioned)
GET  /api/assignments?surveyor_id=       → Get surveyor assignments
GET  /api/sync/status                    → Server-side sync state
```

### Sync-Specific Headers:

```
POST /api/surveys
Headers:
  Idempotency-Key: <UUID>
  X-Surveyor-ID: <surveyor_id>
  X-Device-ID: <device_id>
  X-Client-Timestamp: <unix_timestamp>
  Authorization: Bearer <token>
Body: { form_data, gps, media_ids, template_version }
```

### Timeout & Retry:

```
Survey upload:
  → Connect timeout: 30s (slow networks in remote areas)
  → Receive timeout: 60s (large form with multiple photos)
  → Max retries: 5 (via WorkManager, not Dio)
  → Backoff: 30s → 1min → 5min → 15min → 30min

Photo upload:
  → Connect timeout: 30s
  → Receive timeout: 120s (large file, slow network)
  → Max retries: 5

Template download:
  → Connect timeout: 10s
  → Receive timeout: 30s
  → Max retries: 3
```

### WorkManager Configuration:

```dart
// Sync worker constraints
WorkManager().enqueue(
  OneTimeWorkRequest(SyncWorker)
    .setConstraints(
      Constraints(
        networkType: NetworkType.connected,  // Any network
        // OR NetworkType.unmetered for Wi-Fi only (optional)
      ),
    )
    .setBackoffCriteria(
      BackoffPolicy.exponential,
      Duration(seconds: 30),
    )
    .setExpedited(false),  // Not expedited — can wait
)

// Also periodic sync every 15 min when online
WorkManager().enqueue(
  PeriodicWorkRequest(SyncWorker, Duration(minutes: 15))
    .setConstraints(Constraints(networkType: NetworkType.connected))
)
```

---

## Step 8 — Offline Support & Sync (35–40 min) — Core Section

### Database Schema (Drift + SQLCipher):

```sql
-- Survey data (local source of truth)
CREATE TABLE surveys (
  id TEXT PRIMARY KEY,           -- UUID generated on device
  server_id TEXT,                 -- Set after successful sync
  template_id TEXT,
  surveyor_id TEXT,
  form_data TEXT,                -- JSON
  gps_lat REAL,
  gps_lng REAL,
  accuracy REAL,                 -- GPS accuracy
  client_timestamp INTEGER,      -- When surveyor submitted
  server_timestamp INTEGER,      -- When server received (null until synced)
  sync_status TEXT DEFAULT 'PENDING',  -- PENDING, SYNCING, SYNCED, FAILED, CONFLICT
  created_at INTEGER,
  updated_at INTEGER
);

-- Sync queue (ordered operations)
CREATE TABLE sync_queue (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  operation_type TEXT,           -- CREATE_SURVEY, UPDATE_SURVEY, UPLOAD_PHOTO
  entity_id TEXT,                -- survey_id
  entity_type TEXT,              -- survey, photo
  payload TEXT,                  -- JSON operation data
  idempotency_key TEXT,          -- UUID for dedup
  status TEXT DEFAULT 'PENDING', -- PENDING, SYNCING, DONE, FAILED
  retry_count INTEGER DEFAULT 0,
  max_retries INTEGER DEFAULT 5,
  next_retry_at INTEGER,         -- For backoff scheduling
  created_at INTEGER,
  completed_at INTEGER,
  error_message TEXT
);

-- Media (photos)
CREATE TABLE media (
  id TEXT PRIMARY KEY,           -- UUID
  survey_id TEXT,
  local_path TEXT,               -- /surveys/{id}/photo_1.jpg
  server_url TEXT,               -- S3 URL (set after upload)
  media_type TEXT,               -- photo, signature
  sync_status TEXT DEFAULT 'PENDING',
  file_size INTEGER,
  checksum TEXT,                 -- MD5 for integrity verification
  created_at INTEGER
);

-- Form templates (server → client)
CREATE TABLE templates (
  id TEXT PRIMARY KEY,
  version INTEGER,
  name TEXT,
  fields TEXT,                   -- JSON form definition
  is_active INTEGER DEFAULT 1,
  downloaded_at INTEGER
);

-- Sync metadata
CREATE TABLE sync_metadata (
  key TEXT PRIMARY KEY,
  value TEXT
  -- Keys: last_sync_at, last_template_version, last_assignment_sync
);

-- Conflicts
CREATE TABLE conflicts (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  survey_id TEXT,
  conflict_data TEXT,            -- JSON: { local, remote, reason }
  status TEXT DEFAULT 'UNRESOLVED',
  created_at INTEGER
);
```

### Sync State Machine:

```
Survey lifecycle:

  DRAFT → SUBMITTED → PENDING_SYNC → SYNCING → SYNCED
                                           ↘ FAILED → (retry) → SYNCING
                                           ↘ CONFLICT → (resolve) → SYNCED

  PENDING_SYNC: in local DB, in sync_queue
  SYNCING: SyncWorker is processing
  SYNCED: server confirmed receipt
  FAILED: max retries exceeded, needs manual intervention
  CONFLICT: server rejected, needs resolution
```

### Conflict Resolution Strategies:

| Strategy | When to Use | Implementation |
|----------|------------|----------------|
| **Server-Authoritative** | Default — server is truth | Server rejects duplicate, client accepts server's version |
| **Last-Write-Wins** | Timestamp-based | Compare client_timestamp, latest wins |
| **Merge** | Additive data (e.g., counts) | Server sums values from both submissions |
| **Manual Resolution** | Complex conflicts | Store in conflicts table, admin reviews via dashboard |

### Data Integrity:

```
Photo checksum verification:
  → Before upload: calculate MD5 of photo file
  → After upload: server verifies MD5 matches
  → If mismatch: re-upload (corrupted during transfer)

Survey integrity:
  → Each survey has a checksum of form_data
  → Server verifies on receipt
  → If corrupted: client re-sends
```

### Storage Management:

```
→ Max local storage: 500MB for surveys + photos
→ Auto-cleanup: after sync confirmed, compress photos (keep thumbnail)
→ After 30 days synced: move to archive table (compressed)
→ If storage <100MB free: warn user, prevent new form collection
→ Photos: keep original until sync confirmed, then compress to 100KB thumbnail
```

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Init: Drift (encrypted) + auth. Form list loads from local DB. Templates from local. Defer sync, analytics. Target: <2s to form list. |
| **UI** | `ListView.builder` for form/survey list. `const` widgets. Photo thumbnails (100px) in list, full on tap. Camera: native camera plugin, not in-app. |
| **Database** | Drift with WAL mode (Write-Ahead Logging) for concurrent read/write. Index on surveyor_id, sync_status. Batch inserts for multi-page forms. |
| **Network** | Compress survey data (gzip). Photo: resize before upload (max 2MB). Batch sync: upload multiple surveys in one request (optional). Presigned URL for direct S3 upload. |
| **Memory** | Form list: keep max 200 in memory. Photos: load thumbnails, not full. Camera: release immediately after capture. |
| **Battery** | GPS: acquire once per form (not continuous). Camera: native, no preview stream. Sync: WorkManager (battery-aware). No background location. |
| **Storage** | Compress synced photos to thumbnails. Archive old surveys. Monitor free space. SQLite vacuum periodically. |

### GPS Optimization:

```dart
// Acquire GPS only when needed (form submission)
// Not continuous tracking
final position = await Geolocator.getCurrentPosition(
  desiredAccuracy: LocationAccuracy.high,
  timeLimit: Duration(seconds: 10),
);
// If timeout: use last known position with lower accuracy
// Store: lat, lng, accuracy, timestamp
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2/JWT. Token stored in Keystore. Token may expire during offline work — allow offline work with expired token, re-auth on sync. |
| **Local DB** | SQLCipher (AES-256). Encryption key derived from device + user credentials. All survey data encrypted at rest. |
| **Photo Security** | Photos stored in app-private storage (not accessible by other apps). Encrypted with SQLCipher key. |
| **API Security** | SSL pinning. TLS 1.2+. Idempotency keys for all mutations. Request signing (HMAC). |
| **Data Integrity** | MD5 checksums on photos. Form data checksums. Server verifies integrity. |
| **Audit Trail** | Every operation logged: who, what, when, device_id. Sync queue retains history for audit. |
| **Offline Auth** | Allow offline work even with expired token. On sync: if token expired, auto-refresh. If refresh fails: queue data, notify user to re-auth. |
| **Device Security** | Device fingerprinting. App only works on registered devices. Root detection (block on rooted). |

### Offline Authentication Strategy:

```
Problem: Surveyor works offline for days. Token expires.

Solution:
  → Allow offline work with expired token (trust local auth)
  → Local auth: app PIN/biometric on launch
  → On sync attempt:
    → If token expired: try refresh
    → If refresh succeeds: proceed with sync
    → If refresh fails (server unreachable): queue, try next sync
    → If refresh fails (token revoked): block sync, notify user to re-auth
  → Never block data collection due to auth — only block sync
```

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Survey Service: stateless, auto-scaling. Idempotency keys in Redis (48h TTL).
- PostgreSQL: partitioned by region, sharded by surveyor_id.
- S3: photo storage with lifecycle policies (move to Glacier after 90 days).
- Kafka: sync events → audit, analytics, conflict review queue.
- Template distribution: CDN + versioned (immutable per version).

### Bulk Sync Scenario (Evening):

```
10,000 surveyors return to office at 6pm
  → All trigger sync simultaneously
  → 1M records hitting server in 2-4 hours
  → API Gateway rate limiting: 100 req/sec per surveyor
  → Server auto-scales: 10 → 50 instances during sync window
  → Queue excess requests (Kafka) → process asynchronously
  → Surveyors see "syncing..." → "synced" as their data uploads
```

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| Network drops during sync | SyncWorker stops. Resumes from where it left off (next PENDING item). No data loss — each item is atomic. |
| Server 500 during survey upload | Retry with backoff. Same idempotency key → no duplicate. After 5 retries: mark FAILED, show in dashboard for manual retry. |
| Photo upload fails | Retry independently. Survey data can sync without photo (photo syncs separately when it succeeds). |
| Conflict detected | Store in conflicts table. Mark survey as CONFLICT. Notify surveyor. Admin reviews. |
| App killed during form entry | Auto-save draft to Drift every 30s. On reopen: offer "continue draft." |
| App killed during sync | WorkManager resumes on next trigger. Each queue item is independent. |
| Device storage full | Warn user. Prevent new form collection. Compress old synced photos. |
| Token expired offline | Allow work. On sync: refresh token. If revoked: block sync, prompt re-auth. |
| Duplicate submission (double-tap) | Idempotency key on sync queue. Only one operation processed. |
| GPS unavailable | Allow manual location entry. Mark GPS as "manual." |
| Template updated mid-survey | Continue with current template version. Server accepts old version (backward compatible). |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| Local DB as source of truth | Always works offline, but sync conflicts possible. Mitigated with conflict resolution. |
| SQLCipher (encrypted DB) | Security, but ~10-15% performance overhead. Essential for sensitive survey data. |
| WorkManager for sync | Battery-efficient, OS-managed. But sync timing not guaranteed (OS may delay). |
| Server-authoritative conflicts | Simple, but surveyor's data may be overwritten. Acceptable for field data (admin reviews). |
| Photo compression after sync | Saves storage, but loses original quality. Acceptable for field surveys. |
| Allow offline work with expired token | Better UX, but security risk if device is stolen. Mitigated by local PIN/biometric + device registration. |
| Long timeouts (30-60s) | Handles slow networks, but sync may feel slow. Acceptable for field conditions. |
| Presigned URL for photos | Offloads traffic from API, but adds complexity. Worth it at 1M records/day with photos. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Sync queue ordering, idempotency key generation, conflict resolution logic, checksum verification, storage management |
| **Integration** | Full sync cycle: create survey → queue → sync → server confirms. Photo upload with checksum. Template download. |
| **E2E** | Offline: collect 10 forms + 20 photos → go offline → submit all → go online → verify sync → verify server data matches |
| **Conflict Testing** | Two devices submit for same location → verify conflict resolution |
| **Performance** | 1000 pending items sync time, DB query time (10K surveys), storage usage after 30 days |
| **Reliability** | Kill app mid-sync → verify resume. Network drop mid-upload → verify retry. 100 forms with photos → verify no data loss |
| **Observability** | Crashlytics, custom sync metrics (pending count, failure rate, avg sync time, conflict count), audit log (every sync operation) |

---

## Summary

> *"This is a true offline-first architecture where the local encrypted database (Drift + SQLCipher) is the single source of truth. Surveyors collect data (forms, photos, GPS) entirely offline — every submission writes to the local DB immediately with zero network dependency. The Sync Engine, powered by WorkManager, processes an ordered queue of operations when connectivity is available. Each operation carries an idempotency key for safe retries. Photos upload via presigned S3 URLs with MD5 checksum verification. Conflict resolution is server-authoritative with admin review for complex cases. The app handles 10K surveyors syncing 1M records/day, with bulk sync windows in the evening. Security includes SQLCipher encryption, offline authentication (local PIN/biometric), SSL pinning, and device registration. The architecture guarantees zero data loss — every form reaches the server eventually, even through intermittent connectivity and app kills."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
