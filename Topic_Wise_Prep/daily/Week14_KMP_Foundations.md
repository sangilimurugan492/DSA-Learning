# Week 14 · Kotlin Multiplatform — Foundations & Intermediate
**Mon Feb 1 – Sun Feb 7** · 12 files / ~3,750 lines · Phase 5 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-5 detail](../weekly/Phase5_Week14-15_KMP.md) · bank [kmp_questions/](../../kmp_questions/)

### Mon Feb 1 — Common code & expect/actual (90 min)
- [ ] **Read** `README` + `beginner/` Basics, CommonCode, ExpectActual (~942 l)
- [ ] **Say it** — source sets & dependency direction; `expect`/`actual` rules; the moment you know the abstraction is wrong (120 s)
- [ ] **Task** — create a KMP module from scratch (CLI or Studio wizard) with one `expect`/`actual` pair (platform name + a platform DB driver); build Android + iOS targets
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Feb 2 — Project setup & architecture (90 min)
- [ ] **Read** `beginner/` ProjectSetup, PlatformSpecific + `intermediate/Architecture` (~961 l)
- [ ] **Say it** — what belongs in `commonMain` vs `androidMain`/`iosMain`, and the leaky-abstraction smell (90 s)
- [ ] **Task** — take one repository from an app you know and mark, line by line, what would be common / expect-actual / platform-only; draw the boundary
- [ ] **Log** — summary + tracker

### Wed Feb 3 — Coroutines & database in common code (90 min)
- [ ] **Read** `intermediate/` Coroutines, Database (~782 l)
- [ ] **Say it** — main dispatcher on iOS, cold Flow behaviour, why `runBlocking` is a trap on iOS (90 s)
- [ ] **Task** — wire SQLDelight (or Room-KMP) in the sample module: one table, one Flow query, call it from a shared ViewModel; run on both targets if you have a Mac
- [ ] **Log** — summary + tracker

### Thu Feb 4 — DI, networking, testing (90 min)
- [ ] **Read** `intermediate/` DependencyInjection, Networking, Testing (~1,065 l)
- [ ] **Say it** — Ktor + kotlinx.serialization in common, Koin vs manual DI trade-off, what still needs XCTest (90 s)
- [ ] **Task** — add a Ktor client with a common test double (fake engine) and one common test that runs on both platforms (`kotlin.test`)
- [ ] **Log** — summary + tracker

### Sat Feb 6 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 12 + Week 13** — 10 Qs each (Flutter state mgmt — same "where does state live" question)
- [ ] **Task** — draft the *KMP decision memo* skeleton: adopt / don't adopt, shared-layer boundary, 3 risks + mitigation

### Sun Feb 7 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-14 quiz Qs](../weekly/Phase5_Week14-15_KMP.md) + 5 recall: ① `cinterop` vs Kotlin/Native ObjC export ② source-set hierarchy (`commonJvmMain` etc.) ③ expect/actual annotations rules ④ what cannot be shared ⑤ how iOS team consumes the framework (XCFramework)
- [ ] 🎤 STAR #14 — [critical_issues_star_report.md](../../behavioral_questions/critical_issues_star_report.md) (pass 1) — "hardest production incident" — 90 s
- [ ] 🏗️ SD drill — where the KMP shared layer sits in a system-design diagram (client edge vs backend contract), 1 slide

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
