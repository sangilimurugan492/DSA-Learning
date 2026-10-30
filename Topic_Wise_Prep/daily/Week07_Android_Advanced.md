# Week 7 · Android — Advanced & Hardening · 🔒 phase gate
**Mon Dec 14 – Sun Dec 20** · 11 files / ~3,629 lines · Phase 2 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-2 detail](../weekly/Phase2_Week05-07_Android.md) · bank [android_questions/](../../android_questions/)

### Mon Dec 14 — DI, custom views, CI (90 min)
- [ ] **Read** `advanced/` DependencyInjection, CustomViews, CICD (~908 l)
- [ ] **Say it** — Hilt components/scopes, `@Inject` vs `@Provides`; what breaks in a custom View (measure/layout/draw contract) (120 s)
- [ ] **Task** — draw your DI graph for one feature (modules, scopes, what leaks if you scope it wrong); fix one over-scoped dependency you actually have
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Dec 15 — Modularisation & performance (90 min)
- [ ] **Read** `advanced/` Modularization, Performance, PlayIntegrity (~1,158 l)
- [ ] **Say it** — feature vs layer modules, dependency direction, build-time vs runtime cost; startup measurement plan (120 s)
- [ ] **Task** — run Macrobenchmark (or `reportFullyDrawn` + Perfetto trace) on cold start; write down 3 numbers and the cheapest win
- [ ] **Log** — summary + tracker

### Wed Dec 16 — Security, testing, architecture scenarios (90 min)
- [ ] **Read** `advanced/` Security, Testing + `scenario_based/ArchitectureScenarios` (~1,067 l)
- [ ] **Say it** — what must never live in a `SharedPreferences` file, and the 3 tests you'd refuse to ship without (90 s)
- [ ] **Task** — add one architectural guard: a `Konsist`/ArchUnit rule or a DI compile check that fails if a UI class touches a DAO
- [ ] **Log** — summary + tracker

### Thu Dec 17 — Debugging scenarios (90 min)
- [ ] **Read** `scenario_based/DebuggingScenarios` + `scenario_based/README` (~496 l)
- [ ] **Say it** — pick 3 scenarios and narrate symptom → tool → root cause → prevention (60 s each)
- [ ] **Task** — write your **2-minute ANR/jank story** (STAR-shaped) using a real incident; rehearse aloud twice, record the second
- [ ] **Log** — summary + tracker

### Sat Dec 19 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 3 + Week 4** — 10 Qs each (Kotlin idioms + coroutines/Flow)
- [ ] **Task** — finish the *Android architecture one-pager* (Phase-2 artifact #3)

### Sun Dec 20 — Quiz + phase gate (90 min)
- [ ] 🧪 Quiz /10: [Week-7 quiz Qs](../weekly/Phase2_Week05-07_Android.md) + 5 recall: ① baseline profile effect ② `StrictMode` policies ③ LeakCanary report sections ④ R8 keep-rule failure symptoms ⑤ what Play Integrity attestation gives your server
- [ ] 🔒 **PHASE-2 GATE** — closed-book 20-Q re-quiz across W5–W7 + deliver the "default architecture answer" in 5 minutes aloud; below 14/20 → re-run misses on W8 Saturday
- [ ] 🎤 STAR #7 — [ComposeBehavioralQuestions.md](../../behavioral_questions/ComposeBehavioralQuestions.md) (1st half) — 90 s

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
