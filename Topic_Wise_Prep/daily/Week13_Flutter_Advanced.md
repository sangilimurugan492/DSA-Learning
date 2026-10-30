# Week 13 · Flutter — Advanced & Scenario Bootcamp · 🔒 phase gate
**Mon Jan 25 – Sun Jan 31** · 13 files / ~3,804 lines · Phase 4 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-4 detail](../weekly/Phase4_Week11-13_Flutter.md) · bank [flutter_questions/](../../flutter_questions/)

### Mon Jan 25 — Architecture, Dart advanced, CI/CD (90 min)
- [ ] **Read** `advanced/` ArchitecturePatterns, DartAdvanced, CICD (~1,508 l)
- [ ] **Say it** — feature-first vs layer-first, clean architecture layers in Flutter, how `dart:isolate` changes your architecture (120 s)
- [ ] **Task** — write one isolate-based JSON parse (`compute` or raw `Isolate.run`) on a 5 MB payload; report the UI-thread frame time saved
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Jan 26 — Internals, performance, DI, channels (90 min)
- [ ] **Read** `advanced/` FlutterInternals, Performance, DependencyInjection, PlatformChannels (~769 l)
- [ ] **Say it** — 3 trees + build/layout/paint/raster stages; method vs event vs message channels + threading rules (120 s)
- [ ] **Task** — add one method channel + one event channel to a plugin or app; prove on which thread each callback runs by logging `Platform.threadId`-style evidence
- [ ] **Log** — summary + tracker

### Wed Jan 27 — Scenario bootcamp I (90 min)
- [ ] **Read** `scenario_based/` DebuggingScenarios, NavigationScenarios, PerformanceScenarios (~837 l)
- [ ] **Say it** — for each: symptom → profiling tool (`--profile`, DevTools timeline, `PerformanceOverlay`) → fix
- [ ] **Task** — build the "jank triage" table: build-bound vs raster-bound vs platform-bound → the 2 fixes you try first for each
- [ ] **Log** — summary + tracker

### Thu Jan 28 — Scenario bootcamp II (90 min)
- [ ] **Read** `scenario_based/` StateManagementScenarios, UIScenarios, README (~690 l)
- [ ] **Say it** — pick the state-management answer for 3 given scenarios and justify in one sentence each
- [ ] **Task** — record a 2-minute "Flutter vs native vs Compose" answer with trade-offs (startup, size, hot reload, ceiling, hiring pool); listen back once
- [ ] **Log** — summary + tracker

### Sat Jan 30 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 11 + Week 12** — 10 Qs each
- [ ] **Task** — finish the *state-management comparison table* (Phase-4 artifact #5)

### Sun Jan 31 — Quiz + phase gate (90 min)
- [ ] 🧪 Quiz /10: [Week-13 quiz Qs](../weekly/Phase4_Week11-13_Flutter.md) + 5 recall: ① `RepaintBoundary` effect ② `const` widget subtree benefit ③ `WidgetsBindingObserver` lifecycle ④ tree-shaking/release build differences ⑤ `AutomaticKeepAliveClientMixin` use case
- [ ] 🔒 **PHASE-4 GATE** — closed-book 20-Q re-quiz across W11–W13
- [ ] 🎤 STAR #13 — [FlutterBehavioralQuestions.md](../../behavioral_questions/FlutterBehavioralQuestions.md) — "a migration or framework decision" — 90 s

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
