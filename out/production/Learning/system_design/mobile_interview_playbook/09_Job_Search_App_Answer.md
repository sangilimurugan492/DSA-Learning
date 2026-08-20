# Structured Interview Answer: Design a Job Search Application

> **Question**: *"Design a mobile job search application like LinkedIn Jobs/Indeed/Naukri."*

---

## Step 1 — Clarify Requirements

**Questions:**
- Job seeker app, employer app, or both?
- Search with filters (location, salary, experience)?
- Save jobs and apply within app?
- Job alerts / push notifications for matching jobs?
- Resume/profile management?
- Offline support for saved jobs?

**Assumed:** Flutter, job seeker app, search + filters yes, save + apply yes, alerts yes, resume yes, offline saved jobs yes.

---

## Step 2 — Define Scope

```
IN SCOPE: Job search (filters), save jobs, apply, job alerts (push), resume/profile, application tracking
OUT OF SCOPE: Employer/recruiter app, messaging/interview scheduling, premium subscriptions, admin panel
```

---

## Step 3 — Constraints

```
Functional: Search jobs with filters, save jobs, apply, receive alerts, track applications, manage profile
Non-Functional: 20M users, ~2M concurrent at peak (Mon-Fri 9-5), < 500ms search results, offline saved jobs
```

---

## Step 4 — High-Level Architecture

```
                    ┌──────────────┐
                    │  Mobile App  │
                    │   Flutter    │
                    └──────┬───────┘
                           │
           REST (search, applications) + FCM (job alerts)
                           │
                    ┌──────▼───────┐
                    │ API Gateway  │
                    └──────┬───────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
  Search Service     Application Service     Alert Service
  (Elasticsearch)    (apply, track)          (matching + FCM)
    │                      │                      │
  Job Service        Resume Service           Kafka (job events)
  (CRUD, details)    (profile, resume)             │
    │                      │                      │
    └──────────────────────┼──────────────────────┘
                           │
                    ┌──────▼───────┐
                    │ PostgreSQL   │
                    │ + Elasticsearch│
                    │ + Redis      │
                    └──────────────┘
```

---

## Step 5 — Mobile Architecture

```
┌─────────────────────────────┐
│            UI               │
│  SearchScreen, JobDetail,    │
│  SavedJobs, Applications,   │
│  ProfileScreen               │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│          BLoC               │
│  SearchBloc, JobBloc,        │
│  ApplicationBloc, AlertBloc  │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│         Use Cases           │
│  SearchJobsUseCase            │
│  SaveJobUseCase               │
│  ApplyJobUseCase              │
│  GetAlertsUseCase              │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│      Repository             │
│  JobRepository                │
│  ApplicationRepository        │
└──────────┬───────────┬──────┘
           ↓           ↓
       REST API     Local DB
       (Dio)        (Drift)
```

---

## Step 6 — Data Flow

### Job Search (Debounced):
```
User types "Flutter Developer"
  ↓
SearchBloc debounces (500ms — wait for typing to pause)
  ↓
SearchJobsUseCase → JobRepository.search()
  ↓
Check local cache for identical query? (cache key = query + filters)
  ├── Hit & fresh (< 5 min) → Show cached results immediately
  └── Miss → GET /jobs/search?q=flutter&location=Bangalore&salary_min=10L
              ↓
              Cache results in local DB (query → results, TTL: 5 min)
              ↓
              Show results with pagination
```

### Save Job (Offline-Capable):
```
User taps "Save"
  ↓
SaveJobUseCase → save to local DB (status: SAVED)
  ↓
UI shows saved icon (optimistic)
  ↓
If online → POST /jobs/{id}/save (idempotent)
If offline → queue in sync queue
  ↓
WorkManager syncs when network restores
```

### Apply for Job:
```
User taps "Apply"
  ↓
Check: resume complete? → If not, prompt to complete
  ↓
ApplyJobUseCase → POST /applications (idempotency key)
  Body: { job_id, resume_id, cover_letter }
  ↓
Server: validate → store application → notify employer
  ↓
ApplicationBloc updates status: APPLIED
  ↓
UI shows "Application submitted" + moves to "Applied" tab
  ↓
FCM push when employer responds (interview invite, rejection)
```

| Data | Strategy | TTL |
|------|----------|-----|
| Search results | Cache-first (per query) | 5 min |
| Job details | Cache-first | 30 min |
| Saved jobs | Local-first (source of truth) | N/A |
| Applications | Cache-first | 5 min |
| Profile/resume | Local + sync | N/A |

---

## Step 7 — Networking

```
REST:
  GET /jobs/search?q=X&filters=Y&cursor=Z (cursor pagination)
  POST /jobs/{id}/save (idempotent)
  POST /applications (idempotency key)
  GET /applications?status=APPLIED
  connectTimeout: 10s, receiveTimeout: 15s

Debounce:
  Search input: 500ms debounce (avoid API call per keystroke)
  Filters: immediate (user expects quick filter)

FCM (Job Alerts):
  Push when new job matches saved search criteria
  Deep link to job detail screen
  User-configurable: frequency (instant, daily, weekly)

Retry:
  Search GET: retry 3x (safe, idempotent)
  Apply POST: idempotency key → safe to retry
```

---

## Step 8 — Offline Support

```
Saved jobs: FULLY offline
  → Local DB is source of truth
  → Save/unsave works offline → sync queue
  → Read saved jobs anytime

Applications: Read offline, apply online
  → View applied jobs from local cache
  → Can't apply offline (employer needs to receive)

Job search: Cache last search results
  → Show cached results when offline
  → "Showing cached results — you're offline"

Profile/Resume: Local-first
  → Edit offline → sync when online
```

---

## Step 9 — Performance

| Category | Techniques |
|----------|-----------|
| **Startup** | Load saved jobs + recent search from local DB immediately |
| **UI** | ListView.builder for job list, debounced search, shimmer placeholders |
| **Network** | Debounce search (500ms), cursor pagination, gzip |
| **Memory** | Cancel in-flight search when new query typed, dispose controllers |
| **Battery** | No polling (FCM for alerts), WorkManager for sync |

---

## Step 10 — Security

```
Auth: OAuth2, tokens in Keystore/Keychain
Resume: Encrypted storage (PII — name, phone, address)
Network: TLS 1.2+, certificate pinning
Apply: Idempotency key prevents duplicate applications
Privacy: Don't log search queries in analytics (sensitive — salary, location preferences)
```

---

## Step 11 — Scalability

- Elasticsearch for job search (full-text + filters + faceted search)
- Cursor pagination (not offset — deep pagination is slow)
- Redis cache for popular searches
- Kafka for job events (new job posted → alert service → FCM to matching users)
- Read replicas for job listings (read-heavy)

---

## Step 12 — Failure Scenarios

| Failure | Response |
|---------|----------|
| Search API down | Show cached results + "Offline" banner |
| Apply fails | Show error, keep application draft, allow retry |
| FCM not delivered | User sees new alerts on next app open (REST sync) |
| Resume upload fails | Retry in WorkManager, show upload progress |
| Duplicate application | Idempotency key → server returns existing application |
| Saved job sync conflict | Last-write-wins (saved/unsaved is binary, low conflict risk) |

---

## Step 13 — Trade-offs

| Decision | Choice | Alternative | Why |
|----------|--------|-------------|-----|
| Search | Elasticsearch | PostgreSQL LIKE | Fast full-text, filters, faceted results |
| Saved jobs | Local-first | Server-only | Works offline, instant response |
| Search debounce | 500ms | Immediate | Reduces API calls, better UX (no flicker) |
| Alerts | FCM + REST sync | Polling | Battery-efficient, instant delivery |
| Apply | Idempotency key | No key | Prevents duplicate applications on retry |

---

## Step 14 — Testing & Observability

| Level | What |
|-------|------|
| Unit | Search debounce, filter logic, application state machine |
| Integration | Search API + local cache, application flow |
| E2E | Search → save → apply → receive alert → view application |
| Observability | Search latency p95, apply success rate, alert delivery rate, open rate |
