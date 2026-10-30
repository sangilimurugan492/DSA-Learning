# Week 10 · Compose — Advanced & Scenario Bootcamp · 🔒 phase gate
**Mon Jan 4 – Sun Jan 10** · 12 files / ~3,737 lines · Phase 3 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-3 detail](../weekly/Phase3_Week08-10_Compose.md) · bank [jetpack_compose_questions/](../../jetpack_compose_questions/)

### Mon Jan 4 — Internals, custom layout, architecture (90 min)
- [ ] **Read** `advanced/` Internals, CustomLayouts, Architecture (~1,024 l)
- [ ] **Say it** — slot tables/reuse, `MeasureScope` contract (measure then place), Compose module layering (120 s)
- [ ] **Task** — write a custom `Layout` (e.g. flow/chip layout) with correct measure/place; add one `Modifier` parameter of your own
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Jan 5 — Performance & interop (90 min)
- [ ] **Read** `advanced/` Performance, Interop (~1,208 l)* → `advanced/Performance.md`, `advanced/Interop.md`
- [ ] **Say it** — 5 causes of jank with fixes; `AndroidView`/`ComposeView` pitfalls (focus, nested scroll, insets) (120 s)
- [ ] **Task** — profile one real Compose screen (Macrobenchmark frame timings or `Profile GPU Renderer`); name the pipeline stage that loses frames and the fix
- [ ] **Log** — summary + tracker

### Wed Jan 6 — Scenario bootcamp I (90 min)
- [ ] **Read** `scenario_based/` DebuggingScenarios, NavigationScenarios, PerformanceScenarios (~845 l)
- [ ] **Say it** — each scenario aloud: symptom → tool → root cause → prevention (60 s each)
- [ ] **Task** — build a 6-row "symptom → first tool → likely cause" table for Compose issues (recomposition storm, jank, state loss, nav crash, inset bug, keyboard overlap)
- [ ] **Log** — summary + tracker

### Thu Jan 7 — Scenario bootcamp II + testing (90 min)
- [ ] **Read** `scenario_based/` StateScenarios, UIScenarios, README + `advanced/Testing.md` (~660 l + testing recap)
- [ ] **Say it** — semantics matchers, `createComposeRule`, testing a VM-driven screen without rendering (90 s)
- [ ] **Task** — write 2 Compose UI tests for a screen you built this week (one interaction test, one state-restoration test); run them
- [ ] **Log** — summary + tracker

### Sat Jan 9 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 7 + Week 8** — 10 Qs each (advanced Android + Compose fundamentals)
- [ ] **Task** — finish the *Compose recomposition/performance one-pager* (Phase-3 artifact #4)

### Sun Jan 10 — Quiz + phase gate (90 min)
- [ ] 🧪 Quiz /10: [Week-10 quiz Qs](../weekly/Phase3_Week08-10_Compose.md) + 5 recall: ① `Modifier` immutability ② `Modifier.composed` cost ③ `SubcomposeLayout` when-to-use ④ `movableContentOf` ⑤ stability inference failure signals
- [ ] 🔒 **PHASE-3 GATE** — closed-book 20-Q re-quiz across W8–W10 + deliver "Compose vs Views on a large list screen" in 2 minutes with numbers
- [ ] 🎤 STAR #10 — [FlutterBehavioralQuestions.md](../../behavioral_questions/FlutterBehavioralQuestions.md) (2nd half) — 90 s

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
