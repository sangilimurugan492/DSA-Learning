# Structured Interview Answer: Design an Offline-First Mobile App

> **Question**: *"Design an offline-first mobile application that syncs with a backend when network is available."*

> **This is the most mobile-specific system design question.** It directly tests your understanding of local databases, sync engines, conflict resolution, and eventual consistency — topics a backend candidate would never discuss.

---

## Step 1 — Clarify Requirements

**Questions:**
- What domain? (notes app, task manager, field data collection, CRM?)
- Multiple devices per user? (sync across devices?)
- Real-time collaboration required?
- What happens on conflict — who wins?
- Image/media attachments?
- Scale and device constraints?

**Assumed:** Flutter, note-taking app (like Notion/Google Keep), multi-device sync yes, no real-time collaboration, server-authoritative with last-write-wins fallback, media yes, 10M users, low-end devices.

---

## Step 2 — Define Scope

```
IN SCOPE: Create/edit/delete notes offline, multi-device sync, conflict resolution, media attachments, sync status UI, full offline operation
OUT OF SCOPE: Real-time collaborative editing, sharing/permissions, version history, admin panel
```

---

## Step 3 — Constraints

```
Functional: CRUD notes fully offline, sync when online, multi-device, attachments, sync status
Non-Functional: 10M users, zero data loss, < 100ms local read/write, sync within 30s of network restore, low-end device support
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    │ (Local DB =  │
                    │  source of   │
                    │   truth)     │
                    └──────┬───────┘
                           │
              REST (sync) + presigned S3 (media)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
  Sync Service       Note Service          Media Service
  (receive changes,   (CRUD, versioning)    (S3 presigned)
   send changes)          │                      │
    │                      │                      │
  Conflict Resolver   Version Store          S3 + CDN
  (merge logic)       (last_modified)             │
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Redis      │
                    │ + S3         │
                    └──────────────┘
```

> "The local database IS the source of truth. The server is a sync target, not the primary read path. This is the fundamental difference from a typical online-first app."

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  NoteList, NoteEditor,       │
│  SyncStatusIndicator         │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  NoteBloc, SyncBloc          │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  CreateNoteUseCase           │
│  EditNoteUseCase              │
│  DeleteNoteUseCase            │
│  SyncNotesUseCase             │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│     Sync Engine             │
│  (push local, pull remote,  │
│   resolve conflicts)         │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  NoteRepository               │
└──────────┬───────────┬──────┘
           ↓           ↓
       REST API     Local DB
       (Dio)        (Drift — SOURCE OF TRUTH)
```

---

## Step 6 — Data Flow

### Local CRUD (Always Works, Always Fast):
```
User creates/edits a note
  ↓
NoteBloc → CreateNoteUseCase / EditNoteUseCase
  ↓
Write to local DB (Drift) IMMEDIATELY
  → Set sync_status = PENDING
  → Set last_modified = now()
  ↓
UI updates instantly (< 100ms)
  ↓
SyncBloc notified → triggers sync if network available
```

### Sync Engine (The Heart of Offline-First):
```
┌─────────────────────────────────────────────┐
│                SYNC ENGINE                   │
├─────────────────────────────────────────────┤
│                                              │
│  Step 1: PUSH (send local changes to server)│
│  ─────────────────────────────────────────   │
│  Get all notes with sync_status = PENDING   │
│       ↓                                      │
│  POST /sync/push                             │
│  Body: [                                     │
│    { note_id, content, last_modified,        │
│      deleted: false, idempotency_key }       │
│  ]                                           │
│       ↓                                      │
│  Server: process each, return results        │
│       ↓                                      │
│  Update local: sync_status = SYNCED          │
│                                              │
│  Step 2: PULL (get remote changes)          │
│  ─────────────────────────────────────────   │
│  GET /sync/pull?since={last_sync_timestamp}  │
│       ↓                                      │
│  Server: return all changes since timestamp  │
│       ↓                                      │
│  For each remote change:                     │
│    → Conflict? (local also modified?)        │
│       ├── No → Apply to local DB             │
│       └── Yes → Resolve conflict (see below) │
│                                              │
│  Step 3: Update last_sync_timestamp          │
│                                              │
└─────────────────────────────────────────────┘
```

### Conflict Resolution:
```
Conflict: Note modified BOTH locally AND remotely since last sync
  ↓
Check timestamps:
  ├── Remote newer → Use remote (server-authoritative)
  ├── Local newer → Use local (last-write-wins)
  └── Same timestamp → Merge (if possible) or prefer local

For note-taking app: last-write-wins is acceptable
  → User's latest edit wins
  → Log conflict for audit (rare in single-user notes)

Advanced: CRDT (for collaborative editing — out of scope here)
```

| Data | Strategy | TTL |
|------|----------|-----|
| Notes | **Local DB = source of truth** | N/A |
| Sync state | Local (last_sync_timestamp) | N/A |
| Attachments | Local cache + S3 | Permanent |
| User profile | Local-first + sync | N/A |

---

## Step 7 — Networking

```
Sync API:
  POST /sync/push — send local changes (batch)
  GET /sync/pull?since={timestamp} — get remote changes
  → Uses last_sync_timestamp (server time) for delta sync

Sync Triggers:
  1. On app launch (if last sync > 60s ago)
  2. On network restore (WorkManager callback)
  3. After local change (debounced 5s — batch rapid edits)
  4. Manual sync (pull-to-refresh on note list)

Idempotency:
  Every local change gets a UUID idempotency_key
  → Safe to retry push if network fails mid-sync
  → Server deduplicates by idempotency_key

Media sync:
  → Upload: presigned S3 URL → upload directly
  → Download: CDN URL → cache locally
  → If upload fails: retry in WorkManager
  → Note references media by ID; media syncs independently
```

---

## Step 8 — Offline Support & Sync (CORE OF THIS DESIGN)

### Local DB Schema (Drift):
```sql
notes table:
  id              TEXT PRIMARY KEY    -- UUID generated on client
  title           TEXT
  content         TEXT
  last_modified   INTEGER             -- epoch millis
  sync_status     TEXT                -- PENDING / SYNCED / CONFLICT
  deleted         INTEGER             -- soft delete (0/1)
  idempotency_key TEXT                -- UUID per edit

sync_metadata table:
  key             TEXT PRIMARY KEY    -- 'last_sync_timestamp'
  value           INTEGER             -- server timestamp
```

### Sync Status UI:
```
┌─────────────────────────────────┐
│  📝 My Notes          [⟳ Synced]│  ← Synced (green)
├─────────────────────────────────┤
│  📝 Meeting Notes       [⏳ ...]│  ← Pending sync (yellow)
├─────────────────────────────────┤
│  📝 Grocery List    [⚠️ Error]  │  ← Sync failed (red, retry)
└─────────────────────────────────┘

Status: [● Online — All synced]  or  [○ Offline — Changes pending]
```

### Sync Queue:
```
┌──────────────────────────────────────────┐
│            Sync Queue                     │
├──────────────────────────────────────────┤
│ note_id | action  | status   | attempts   │
├──────────────────────────────────────────┤
│ n_001   | CREATE  | SYNCED   | 0          │
│ n_002   | UPDATE  | PENDING  | 0          │
│ n_003   | UPDATE  | PENDING  | 0          │
│ n_004   | DELETE  | ERROR    | 3          │
└──────────────────────────────────────────┘
```

### WorkManager Strategy:
```
Constraints:
  → Network: CONNECTED
  → Battery: not low
  → Backoff: exponential (30s → 1min → 5min → 15min)

Triggers:
  → Network restored → immediate sync
  → Periodic: every 15 min (if pending changes)
  → One-time: after local edit (debounced 5s)
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load notes from local DB immediately (< 100ms — no network wait) |
| **UI** | ListView.builder, const widgets, instant local write → no waiting on network |
| **Network** | Batch sync (not per-edit), delta sync (only changes since last sync) |
| **Memory** | Note content lazy-loaded from DB, pagination for large note collections |
| **Battery** | Sync only on network restore or periodic (not continuous), batch uploads |
| **Storage** | Compact note format, image compression before upload, prune old media cache |

> "Offline-first apps are inherently fast because every read and write hits the local database — no network latency. The sync engine runs in the background without blocking the UI."

---

## Step 10 — Security

```
Auth: OAuth2, tokens in Keystore/Keychain
Local DB: SQLCipher (encrypt notes — may contain sensitive info)
Media: Encrypted in local cache
Network: TLS 1.2+, certificate pinning
Sync: Authenticated REST, JWT in header
Multi-device: Server validates device registration before sending sync data
```

---

## Step 11 — Scalability

- Delta sync: only changes since last_sync_timestamp (not full sync)
- Server-side: versioned notes, last_modified per note
- Sync API: batch push/pull (not per-note round trips)
- Media: S3 + CDN (don't route through API server)
- Database: PostgreSQL with last_modified index for efficient delta queries

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Network down | App fully functional — all reads/writes local, sync queues |
| Sync push fails | Retry in WorkManager (exponential backoff), keep status = PENDING |
| Sync pull fails | Keep local data, retry on next trigger |
| Conflict (multi-device) | Last-write-wins, log for audit |
| App killed during sync | On relaunch: check sync_status = SYNCING → re-sync |
| Server data loss | Local DB has all data → re-push to server (idempotent) |
| Clock skew (device vs server) | Use server timestamps for sync (not device clock) |
| Large sync (1000+ changes) | Paginate sync (50 per batch), show progress |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Source of truth | Local DB | Server | Offline-first = local is truth, server is sync target |
| Conflict resolution | Last-write-wins | CRDT / merge | Simple, acceptable for single-user notes; CRDT overkill |
| Sync model | Delta (timestamp-based) | Full sync | Efficient — only transfer changes, not entire dataset |
| Sync trigger | Event-driven + periodic | Continuous polling | Battery-efficient, responsive |
| IDs | Client-generated UUID | Server-generated | Works offline, no ID conflicts on merge |
| Soft delete | deleted flag + sync | Hard delete | Can sync deletions across devices |

> "The biggest trade-off is: eventual consistency. The local DB and server may diverge temporarily. For a notes app, this is acceptable — users get instant local response, and sync resolves differences within seconds of network restore."

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Sync engine logic, conflict resolution, delta calculation, status transitions |
| Integration | Local DB + sync API mock, push/pull flow |
| E2E | Create offline → go online → verify sync → edit on device 2 → verify on device 1 |
| Sync testing | Simulate network flakiness, rapid edits, large sync, clock skew |
| Observability | Sync success rate, sync latency p95, conflict rate, data loss incidents (should be 0) |
| Critical metric | **Zero data loss** — every local change must eventually reach the server |

---

## 🎤 Key Phrases for This Answer

| When | Phrase |
|------|--------|
| Architecture | "The local database is the source of truth. The server is a sync target, not the primary read path." |
| Sync | "Delta sync — only changes since the last sync timestamp are transferred, not the entire dataset." |
| Conflict | "I use last-write-wins for single-user notes. For collaborative editing, I'd use CRDTs, but that's overkill here." |
| Reliability | "Every local change gets a UUID idempotency key. If sync fails mid-way, retry is safe — the server deduplicates." |
| Trade-off | "The trade-off is eventual consistency. Local and server may diverge temporarily, but for a notes app, instant local response is more valuable than strict consistency." |
