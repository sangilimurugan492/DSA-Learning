# Example: Design a Job Search Application (LinkedIn/Naukri-style)

> **Full 60-minute mock interview answer using the 14-step framework.**
>
> **Prompt**: *"Design a mobile job search application like LinkedIn/Naukri for 30 million users."*

---

## Step 1 — Clarify Requirements (0–5 min)

**You ask:**
1. Platform? → *"Android and iOS, Flutter cross-platform."*
2. Core features? → *"Job search, job feed, saved jobs, apply, alerts, profile."*
3. Search filters? → *"Location, salary, experience, job type, company."*
4. Push notifications? → *"Yes, job alerts and application status."*
5. Offline support? → *"Yes — saved jobs, job details cached, apply queue."*
6. Scale? → *"30M users, ~5M active job seekers, ~500K jobs listed."*

**Summary:**
- **Functional**: Job search (filters, sort), job feed (recommended), save/bookmark jobs, apply with resume, job alerts, application tracking, user profile
- **Non-functional**: <2s search results, <500ms filter response, offline job saving, 30M users, 5M active seekers

---

## Step 2 — Define Scope (5 min)

**In scope:**
- Job search with filters (location, salary, experience, type, company)
- Personalized job feed (recommendations)
- Save/bookmark jobs (offline)
- Apply to jobs (resume + cover letter)
- Job alerts (push notifications based on saved search criteria)
- Application tracking (applied, reviewed, interviewed, offered, rejected)

**Out of scope:**
- Recruiter/employer app (separate)
- Messaging between candidate and recruiter (mention as future)
- Resume builder (mention as future)
- Salary comparison tools

---

## Step 3 — Identify Constraints (5 min)

```
30M users, ~5M active job seekers
Jobs listed: ~500K
Avg jobs viewed per session: ~20
Applications/day: ~500K
Search queries/day: ~20M
Feed requests/day: ~10M
Target: <2s search results, <500ms filter apply
Offline: save jobs, view cached details, queue applications
Devices: 70% Android, must support 2GB RAM
```

---

## Step 4 — High-Level Architecture (5–10 min)

```
┌───────────────────────────────────────────────────────┐
│              Mobile App (Flutter)                       │
│  ┌──────────┐ ┌────────┐ ┌──────────┐ ┌─────────────┐ │
│  │   Feed   │ │ Search │ │  Saved   │ │  Applica-   │ │
│  │  ( reco ) │ │+ Filters│ │  Jobs    │ │  tions     │ │
│  └─────┬────┘ └───┬────┘ └────┬─────┘ └──────┬──────┘ │
│        └──────────┴───────────┴──────────────┘        │
│                    │                                    │
│         ┌──────────┼──────────┐                        │
│         ▼          ▼          ▼                        │
│     REST API   WebSocket   Local DB                    │
│   (search,    (app status   (Drift: saved jobs,       │
│    feed,      updates)      cached details,           │
│    apply)                    pending applies)         │
└──────────────┬────────────────────────────────────────┘
               │
        ┌──────▼───────┐
        │ API Gateway  │ + CDN (company logos, job images)
        │ + Load Bal.  │
        └──────┬───────┘
       ┌───────┼────────────┐
       ▼       ▼            ▼
  ┌────────┐ ┌──────────┐ ┌───────────┐
  │ Search  │ │  Feed    │ │Application│
  │ Service │ │ Service  │ │  Service  │
  └────┬───┘ └────┬─────┘ └─────┬─────┘
       │          │             │
  ┌────▼───┐ ┌───▼────┐ ┌──────▼──────┐
  │Elastic │ │PostgreSQL│ │PostgreSQL  │
  │search  │ │+ Redis  │ │(applications)│
  │(jobs)  │ │(feed)   │ └───────────┘
  └────────┘ └────────┘
       │
  ┌────▼────┐
  │Kafka    │ ← Job events (new job, application status)
  │(events) │
  └─────────┘
  
S3/GCS + CDN ← Resume storage, company logos
FCM/APNS ← Job alerts, application status notifications
```

---

## Step 5 — Mobile Architecture (10–25 min)

### Flutter Clean Architecture:

```
┌─────────────────────────────────────────────┐
│              Presentation                     │
│  Widgets: FeedPage, SearchPage,              │
│           JobDetailPage, SavedJobsPage,       │
│           ApplicationsPage, ProfilePage       │
│  BLoCs: FeedBloc, SearchBloc,                │
│         SavedJobsBloc, ApplicationBloc        │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                Domain                         │
│  Use Cases:                                   │
│  - GetFeedUseCase (recommendations)           │
│  - SearchJobsUseCase (filters, pagination)    │
│  - SaveJobUseCase                             │
│  - ApplyToJobUseCase                          │
│  - GetApplicationsUseCase                     │
│  - CreateJobAlertUseCase                      │
│  Repositories (abstract):                     │
│  - JobRepository                              │
│  - ApplicationRepository                      │
│  - SavedJobRepository                         │
└───────────────────┬───────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│                  Data                         │
│  Remote: JobApi, SearchApi (Dio)            │
│  Real-time: ApplicationWebSocket (status)    │
│  Local: SavedJobDao, CachedJobDao (Drift)    │
│  File: ResumeManager (PDF upload/download)   │
└─────────────────────────────────────────────┘
```

### Key Design: SearchBloc (Filter State Management)

```
┌──────────────────────────────────────────────┐
│              SearchBloc                        │
├──────────────────────────────────────────────┤
│  State:                                       │
│  - SearchInitial                              │
│  - SearchLoading (show skeleton)              │
│  - SearchLoaded(List<Job>, filters, cursor)   │
│  - SearchError(message)                       │
│                                                │
│  Events:                                      │
│  - SearchQueryChanged(query)                  │
│  - FilterChanged(key, value)                  │
│  - FilterCleared                              │
│  - LoadMoreResults(cursor)                    │
│                                                │
│  Key Logic:                                   │
│  - Debounce search query (500ms)              │
│  - Filters trigger immediate search            │
│  - Maintain filter state across pagination    │
│  - Save last search as "recent search"        │
└──────────────────────────────────────────────┘
```

### Key Design: SavedJobsBloc (Offline-First)

```
┌──────────────────────────────────────────────┐
│              SavedJobsBloc                    │
├──────────────────────────────────────────────┤
│  Offline-first: saved jobs are local-first   │
│                                                │
│  Save job:                                    │
│  1. Save to Drift immediately (optimistic)   │
│  2. Sync to server (if online)               │
│  3. If offline: queue sync                    │
│                                                │
│  Load saved jobs:                             │
│  1. Read from Drift (always works offline)   │
│  2. Sync from server in background            │
│  3. Merge: server + local (server is truth   │
│     for removed, local is truth for pending)  │
└──────────────────────────────────────────────┘
```

### Why (Lead-Level Justification):

> *"I separate search and feed into distinct BLoCs because they have different data characteristics — search is user-driven with filters, while feed is algorithmically recommended. The SearchBloc debounces query input (500ms) to avoid excessive API calls, while filter changes trigger immediate search. Saved jobs are offline-first — saved to Drift immediately, synced to server when online. This ensures users can save jobs even with poor connectivity, which is common for users commuting or in areas with weak signal."*

---

## Step 6 — Data Flow (25–30 min)

### Job Search Flow:

```
SearchPage
  → User types "Flutter Developer"
    → SearchBloc debounces 500ms
    → SearchBloc.add(SearchQueryChanged("Flutter Developer"))
      → SearchJobsUseCase.call(query, filters)
        → JobRepository.search()
          → POST /api/jobs/search { query, filters, cursor }
            → Elasticsearch query with filters
          → Return jobs + next_cursor
          → Save results to Drift (cached_jobs)
          → emit SearchLoaded(jobs, cursor)
```

### Filter Application Flow:

```
User selects filter: Salary > ₹10LPA
  → SearchBloc.add(FilterChanged("min_salary", 1000000))
    → Immediately trigger search with updated filters
    → POST /api/jobs/search { query, filters: {min_salary: 1000000} }
    → Replace results (not append — new filter = new result set)
    → emit SearchLoaded(newJobs, cursor)
```

### Save Job Flow (Offline-First):

```
JobDetailPage (user taps "Save")
  → SavedJobsBloc.add(SaveJob(job))
    → Save to Drift (saved_jobs table) immediately
    → Optimistic UI: show "Saved" state
    → If online:
      → POST /api/jobs/{id}/save
      → On success: done
      → On failure: keep saved locally, retry later
    → If offline:
      → Queue in pending_actions (action: SAVE_JOB)
      → WorkManager syncs when online
```

### Apply to Job Flow:

```
JobDetailPage (user taps "Apply")
  → Check: resume uploaded? If not → prompt upload
  → ApplicationBloc.add(ApplyToJob(jobId, resumeId, coverLetter))
    → Generate idempotency_key (UUID)
    → Save to Drift (applications table, status = PENDING)
    → ApplicationRepository.apply()
      → If online:
        → POST /api/applications (with Idempotency-Key)
        → On success: update status = SUBMITTED
        → emit ApplicationSubmitted
      → If offline:
        → Queue in pending_applications
        → Show "Will apply when online"
        → WorkManager syncs when online
```

### Application Status Update Flow:

```
WebSocket receives event:
  { type: "APPLICATION_STATUS", application_id, status: "REVIEWED" }
  → ApplicationBloc.add(ApplicationStatusUpdated)
    → Update Drift (applications table)
    → Update UI (move to "Reviewed" tab)
    → Show notification: "Your application was reviewed"
```

---

## Step 7 — Networking (30–35 min)

### REST API Design:

```
GET  /api/feed?cursor=                       → Recommended jobs (paginated)
POST /api/jobs/search                         → Search with filters (paginated)
GET  /api/jobs/{id}                           → Job detail
POST /api/jobs/{id}/save                      → Save job
DELETE /api/jobs/{id}/save                    → Unsave job
GET  /api/jobs/saved                          → Saved jobs (sync)
POST /api/applications                        → Apply to job (idempotency key)
GET  /api/applications?status=                → My applications (paginated)
POST /api/alerts                              → Create job alert
GET  /api/alerts                              → Get alerts
DELETE /api/alerts/{id}                       → Delete alert
POST /api/resume/upload                       → Upload resume (presigned URL)
```

### Search API (POST for Complex Filters):

```
POST /api/jobs/search
Body: {
  "query": "Flutter Developer",
  "filters": {
    "location": ["Bangalore", "Remote"],
    "min_salary": 1000000,
    "experience": "3-5 years",
    "job_type": ["Full-time"],
    "company_size": ["50-500"]
  },
  "sort": "relevance",  // or "date", "salary"
  "cursor": "eyJpZCI...",
  "limit": 20
}

Response: {
  "jobs": [...20 jobs...],
  "next_cursor": "eyJpZCI6...",
  "total_count": 342
}
```

> **Why POST for search?** Complex filters with arrays are messy in GET query params. POST body handles structured filters cleanly.

### Pagination: Cursor-Based

```
→ Stable across new job postings
→ Works with Elasticsearch search_after
→ No count-based offset issues
```

### Timeout & Retry:
- REST: connect 10s, receive 15s, max 3 retries (GET only)
- WebSocket: reconnect backoff 1s → 2s → 4s → 8s → 30s (cap)
- Apply API: max 0 retries (user-initiated retry only, with idempotency key)

---

## Step 8 — Offline Support & Sync (35–40 min)

### Local Cache (Drift):

```sql
CREATE TABLE saved_jobs (
  job_id TEXT PRIMARY KEY,
  title TEXT,
  company TEXT,
  logo_url TEXT,
  location TEXT,
  salary TEXT,
  description TEXT,
  saved_at INTEGER,
  sync_status TEXT DEFAULT 'SYNCED'  -- SYNCED, PENDING
);

CREATE TABLE cached_jobs (
  job_id TEXT PRIMARY KEY,
  title TEXT,
  company TEXT,
  -- ... full job data
  cached_at INTEGER
);

CREATE TABLE pending_applications (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  job_id TEXT,
  resume_id TEXT,
  cover_letter TEXT,
  idempotency_key TEXT,
  status TEXT,          -- PENDING, SYNCING, DONE, ERROR
  created_at INTEGER,
  retry_count INTEGER DEFAULT 0
);

CREATE TABLE applications (
  application_id TEXT PRIMARY KEY,
  job_id TEXT,
  job_title TEXT,
  company TEXT,
  status TEXT,          -- PENDING, SUBMITTED, REVIEWED, INTERVIEW, OFFER, REJECTED
  applied_at INTEGER,
  last_updated INTEGER
);

CREATE TABLE saved_searches (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  query TEXT,
  filters TEXT,         -- JSON
  created_at INTEGER,
  alert_enabled INTEGER DEFAULT 0
);
```

### Offline Behavior:

| Action | Offline Handling |
|--------|-----------------|
| Search jobs | **Block**: "Connect to search." Show last search results from cache. |
| View job feed | Show cached feed. Mark "offline." |
| View job detail | Show cached job detail (if previously viewed). |
| Save job | ✅ Save to Drift. Queue sync. |
| Unsave job | ✅ Remove from Drift. Queue sync. |
| Apply to job | ✅ Queue in pending_applications. Apply when online. |
| View saved jobs | ✅ Read from Drift (always works). |
| View applications | Show cached applications with last known status. |
| Create job alert | ✅ Queue. Create on server when online. |

### Sync Flow:

```
[Network restored]
  → WorkManager triggers:
    1. Sync saved jobs:
       → POST/DELETE saved job changes
    2. Flush pending applications:
       → For each: POST /api/applications (Idempotency-Key)
       → Update status to SUBMITTED
    3. Sync application statuses:
       → GET /api/applications → update Drift
    4. Create pending alerts:
       → POST /api/alerts
```

---

## Step 9 — Performance (40–45 min)

| Category | Optimization |
|----------|-------------|
| **Startup** | Lazy init: auth + Drift. Feed loads from cache. Search deferred until user interaction. Defer analytics. |
| **UI** | `ListView.builder` for job list. `RepaintBoundary` on JobCard. `const` widgets. Company logo: `cached_network_image` with placeholder. Filter chips: animated expand/collapse. |
| **Network** | Cursor pagination (20/page). CDN for company logos. Gzip. Debounced search (500ms). Cache search results (5 min TTL). |
| **Memory** | Job list: keep max 100 in memory. Job detail: clear when navigating away. Image cache: 75MB. |
| **Battery** | No background polling. WebSocket only for application status (or FCM only). No location tracking. |
| **Search UX** | Debounce input (500ms). Show skeleton during search. Cache last search results. Instant filter apply (no debounce on filters). |

### Search Debounce:

```dart
// SearchBloc uses rxdart's debounce
on<SearchQueryChanged>((event, emit) async {
  await Future.delayed(Duration(milliseconds: 500));
  // Only proceed if no newer event arrived
  // rxdart .debounceTime handles this
  await _performSearch(event.query, currentFilters);
});
```

---

## Step 10 — Security (45–50 min)

| Topic | Implementation |
|-------|---------------|
| **Auth** | OAuth2/JWT. Access token (15 min) + refresh token (30 days) in Keystore/Keychain. |
| **API Security** | SSL pinning. TLS 1.2+. Rate limiting on search API. |
| **Resume Privacy** | Resume stored on S3 with signed URLs (expire in 1 hour). Only accessible by user + authorized recruiters. |
| **Local DB** | SQLCipher for cached jobs + applications (may contain salary expectations, personal info). |
| **PII Protection** | Don't store contact info in plain text in local DB. Encrypt resume file path. |
| **Job Alert Privacy** | Notification body: "New jobs matching your search" (not specific company/salary on lock screen). |
| **Application Data** | Application details encrypted in local DB. No cover letter text in logs. |

---

## Step 11–12 — Scalability & Failure Scenarios (50–55 min)

### Scalability:
- Elasticsearch: job search, sharded by region/category. Faceted search for filters.
- Redis: feed cache (per-user recommendations), search result cache (5 min TTL).
- Feed Service: ML model ranks jobs by user profile, location, experience.
- Kafka: job events (new posting → trigger alerts), application events.
- CDN: company logos, job banner images.

### Job Alert Pipeline:

```
New job posted
  → Kafka event
  → Alert Service:
    → Find users with matching saved search criteria
    → For each user:
      → Check: alert enabled? Last alert > 1 hour ago?
      → Send push notification via FCM/APNS
      → Throttle: max 5 alerts/day per user
```

### Failure Scenarios:

| Scenario | Handling |
|----------|---------|
| Search API fails | Show cached last search results + "couldn't refresh." |
| Elasticsearch slow | Timeout after 10s. Show "search is taking longer than usual." |
| Apply fails (network) | Queue in pending_applications. Retry when online. |
| Apply fails (server: already applied) | Mark as SUBMITTED. Show "already applied." |
| WebSocket disconnects | Reconnect. FCM still delivers application status updates. |
| Job expired after saving | Show "job no longer available" in saved jobs list. |
| Resume upload fails | Retry 3x. If still failing, show error + retry button. Don't block apply (use last uploaded resume). |
| Cache full | Evict oldest cached jobs. Keep saved jobs always. |
| App killed during apply | On reopen: check pending_applications queue. Resume sync. |

---

## Step 13–14 — Trade-offs & Testing (55–60 min)

### Trade-offs:

| Decision | Trade-off |
|----------|-----------|
| POST for search (not GET) | Cleaner filter handling, but not cacheable by CDN/HTTP. Cache in Redis instead. |
| Offline-first saved jobs | Works offline, but may diverge from server if user unsaves on web. Reconciled on sync. |
| Debounced search (500ms) | Reduces API load, but 500ms feels slightly laggy. Acceptable — better than excessive calls. |
| Cursor pagination | Stable, but can't jump to page 5. Acceptable for job search (users scroll linearly). |
| WebSocket for application status | Real-time, but extra connection. Fallback: FCM + REST polling on open. |
| Cached job details (5 min TTL) | May show stale info (e.g., job filled). Show "last updated" timestamp. |

### Testing & Observability:

| Type | Scope |
|------|-------|
| **Unit** | Search filter logic, debounce, pagination cursor, saved job sync, idempotency for applications |
| **Integration** | Search API + Elasticsearch mock, Drift saved jobs sync, application queue flush |
| **E2E** | Search → filter → view detail → save → apply (online) → apply (offline) → reconnect → verify sync |
| **Performance** | Search response time (<2s), filter apply (<500ms), infinite scroll smoothness (60fps) |
| **Observability** | Crashlytics, Firebase Performance (search latency, API latency), custom events (search-to-apply conversion, save rate, alert open rate) |

---

## Summary

> *"The app uses Flutter Clean Architecture with BLoC. Job search uses POST with structured filters and cursor-based pagination (20/page). Search input is debounced (500ms) while filters trigger immediate search. Saved jobs are offline-first — saved to Drift immediately, synced to server when online. Job applications support offline queuing with idempotency keys via WorkManager. Application status updates arrive via WebSocket (foreground) or FCM (background). Job alerts are server-side, matching new postings against saved search criteria with rate limiting (max 5/day). The feed is algorithmically recommended based on user profile. Security includes OAuth2, SSL pinning, signed CDN URLs for resumes, and SQLCipher for local DB. Performance targets <2s search and <500ms filter response with 60fps scroll."*

---

[← Back to Guide](./Mobile_System_Design_Interview_Guide.md)
