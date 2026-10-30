# Week 4 · Kotlin — Coroutines, Flows & Advanced
**Mon Nov 23 – Sun Nov 29** · 17 files / ~4,463 lines · Phase 1 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-1 detail](../weekly/Phase1_Week01-04_Languages.md) · bank [kotlin_questions/](../../kotlin_questions/)

### Mon Nov 23 — Coroutines + function idioms (90 min)
- [ ] **Read** `intermediate/` Collections, LambdasAndHigherOrderFunctions, InlineAndReified, OperatorOverloading + `advanced/Coroutines` (~1,010 l)
- [ ] **Say it** — scope, dispatcher, cancellation: what cancels a coroutine and how cleanup stays safe (120 s)
- [ ] **Task** — write one snippet that proves you understand it: `launch` vs `async` + `withTimeout` + `try/finally` inside a cancelled coroutine; run it and read the output
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Nov 24 — Flows, delegation, DSL, generics (90 min)
- [ ] **Read** `advanced/` Flows, DelegatedProperties, DSL, Generics (~941 l)
- [ ] **Say it** — Flow vs StateFlow vs SharedFlow: cold/hot, conflation, buffering, who collects (120 s)
- [ ] **Task** — write a `callbackFlow` wrapping a listener API (e.g. location/sensor) with proper `awaitClose` cancellation
- [ ] **Log** — summary + tracker

### Wed Nov 25 — Value classes, serialization, reflection (90 min)
- [ ] **Read** `advanced/` ValueClasses, Serialization, ReflectionAndAnnotations (~1,037 l)
- [ ] **Say it** — `inline`/`reified` restrictions; value-class erasure surprises (90 s)
- [ ] **Task** — write a custom property delegate (`getValue`/`setValue`) backing a field with SharedPreferences; use it twice
- [ ] **Log** — summary + tracker

### Thu Nov 26 — Scenario day (90 min)
- [ ] **Read** `scenario_based/` CollectionScenarios, ConcurrencyScenarios, PerformanceScenarios (~1,037 l)
- [ ] **Say it** — for each scenario: root cause in one sentence, then the coroutine/Flow API that fixes it
- [ ] **Task** — pick the worst race-condition scenario and write the fix with `Mutex` vs `Atomic` vs single-thread confinement; state which you'd ship
- [ ] **Log** — summary + tracker

### Sat Nov 28 — Revision (60 min)
- [ ] `scenario_based/` RealWorldScenarios + README (~438 l)
- [ ] 10 hardest of this week + **Week 2** 10 Qs (Java concurrency) — answer both stacks for the same question
- [ ] **Task** — finish the *Kotlin coroutines & Flows* one-pager (Phase-1 artifact #2)

### Sun Nov 29 — Quiz + STAR + phase gate (90 min)
- [ ] 🧪 Quiz /10: [Week-4 quiz Qs](../weekly/Phase1_Week01-04_Languages.md) + 5 recall: ① `Dispatchers.Main.immediate` ② `stateIn` parameters ③ `supervisorScope` vs `coroutineScope` ④ `@JvmInline` ⑤ `channelFlow` vs `callbackFlow`
- [ ] 🔒 **PHASE-1 GATE** — closed-book 20-Q re-quiz across W1–W4 (10 Java, 10 Kotlin); below 14/20 → W5 Saturday re-runs the misses
- [ ] 🎤 STAR #4 — [AndroidBehavioralQuestions.md](../../behavioral_questions/AndroidBehavioralQuestions.md) (2nd half) — 90 s
- [ ] 🏗️ SD drill — "same API, 3 clients: which 3 endpoints would you batch and why" (1 slide)

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
