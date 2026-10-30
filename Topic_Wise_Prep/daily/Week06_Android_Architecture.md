# Week 6 · Android — Architecture & Data Layer
**Mon Dec 7 – Sun Dec 13** · 11 files / ~3,780 lines · Phase 2 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-2 detail](../weekly/Phase2_Week05-07_Android.md) · bank [android_questions/](../../android_questions/)

### Mon Dec 7 — Patterns & local persistence (90 min)
- [ ] **Read** `intermediate/` ArchitecturePatterns, DataBinding, DataStore (~985 l)
- [ ] **Say it** — MVVM vs MVI vs Clean: state ownership, unidirectional flow, when MVI is overkill (120 s)
- [ ] **Task** — take one screen you own and draw its *actual* data flow; mark every place state is duplicated (the usual smell)
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Dec 8 — Navigation & Paging (90 min)
- [ ] **Read** `intermediate/` Navigation, Paging3 (~951 l)
- [ ] **Say it** — Paging 3 keys/placeholders/retry; safe-arg design + deep links (120 s)
- [ ] **Task** — wire one list to `Pager`/`PagingSource` (or write the `PagingSource` from memory) including `InvalidatingPagingSourceFactory` on refresh
- [ ] **Log** — summary + tracker

### Wed Dec 9 — Networking, Room, ViewModel/LiveData (90 min)
- [ ] **Read** `intermediate/` Retrofit, RoomDatabase, ViewModelLiveData (~869 l)
- [ ] **Say it** — Room migration failure modes; `StateFlow` vs `LiveData` collection lifecycle (120 s)
- [ ] **Task** — write a Room migration + a `MigrationTest` skeleton (schema JSON before/after); list the 3 things that break migrations
- [ ] **Log** — summary + tracker

### Thu Dec 10 — Background work + scenario day (90 min)
- [ ] **Read** `intermediate/WorkManager` + `scenario_based/` NetworkingScenarios, BackgroundTaskScenarios (~975 l)
- [ ] **Say it** — WorkManager vs `Service` vs coroutine+`ProcessLifecycleOwner`; retry/backoff policy (90 s)
- [ ] **Task** — design the retry policy for a flaky upload endpoint: interceptor vs repository vs WorkManager — write the 5-line decision note
- [ ] **Log** — summary + tracker

### Sat Dec 12 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 2 + Week 3** — 10 Qs each (Java collections + Kotlin idioms)
- [ ] **Task** — 3 flashcards: "question I could not answer while explaining architecture aloud"

### Sun Dec 13 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-6 quiz Qs](../weekly/Phase2_Week05-07_Android.md) + 5 recall: ① `DataStore` proto vs preferences trade-off ② `@Transaction` + `Flow` invalidation ③ Retrofit `suspend` vs `Call` ④ `PagingConfig` knobs you'd tune ⑤ `WorkManager` unique work + `EXISTING_WORK_POLICY`
- [ ] 🎤 STAR #6 — [JavaBehavioralQuestions.md](../../behavioral_questions/JavaBehavioralQuestions.md) — "performance/quality fix with a number" — 90 s
- [ ] 🏗️ SD drill — repository → cache → DB → UI flow for "list that must work offline"; 2 slides + one conflict rule

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
