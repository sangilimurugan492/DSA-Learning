# Week 18 · Testing & TDD
**Mon Mar 1 – Sun Mar 7** · 7 files / ~4,668 lines · Phase 6 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-6 detail](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) · bank [testing_questions/](../../testing_questions/)

### Mon Mar 1 — TDD & the why (90 min)
- [ ] **Read** [README](../../testing_questions/README.md) + [TDD](../../testing_questions/TDD.md) (~927 l)
- [ ] **Say it** — what you test vs don't, and the coverage number you'd defend; red-green-refactor on a real bug (120 s)
- [ ] **Task** — take one bug you fixed recently and write the test that would have caught it *before* the fix; verify it fails, then passes
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Mar 2 — Unit tests & mocking (90 min)
- [ ] **Read** [UnitTesting](../../testing_questions/UnitTesting.md), [Mockito](../../testing_questions/Mockito.md) (~1,302 l)
- [ ] **Say it** — fake vs stub vs mock vs spy; why over-mocking is a design smell (90 s)
- [ ] **Task** — replace one mock-heavy test with a hand-written fake; count how many `verify()` calls disappeared
- [ ] **Log** — summary + tracker

### Wed Mar 3 — UI testing (90 min)
- [ ] **Read** [Espresso](../../testing_questions/Espresso.md), [ComposeTesting](../../testing_questions/ComposeTesting.md) (~1,289 l)
- [ ] **Say it** — Espresso vs Compose semantics vs Robolectric vs instrumented, per scenario, with runtime cost (120 s)
- [ ] **Task** — write one Compose semantics test using `onNodeWithText`/`performTextInput` and one Espresso idling-resource case; run both
- [ ] **Log** — summary + tracker

### Thu Mar 4 — Testing scenarios (90 min)
- [ ] **Read** [TestingScenarios](../../testing_questions/TestingScenarios.md) (~1,150 l)
- [ ] **Say it** — each scenario: what layer you'd test at and why (unit vs UI vs integration) (60 s each)
- [ ] **Task** — write the **flaky-test triage card**: 5 causes (timing, shared state, animation, clock, network) + the fix + the guard
- [ ] **Log** — summary + tracker

### Sat Mar 6 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 8 + Week 10** — 10 Qs each (Compose fundamentals + advanced)
- [ ] **Task** — define the testing pyramid + CI gates for one real module of yours (how many tests per layer, what blocks merge)
- [ ] **Log** — summary + tracker

### Sun Mar 7 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-18 quiz Qs](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) + 5 recall: ① `runTest` virtual time ② `TestDispatcher` vs `UnconfinedTestDispatcher` ③ snapshot/golden tests ④ test doubles for `Flow` ⑤ what you do with a test that only fails on CI
- [ ] 🎤 STAR #18 — pick any bank: "a quality/process improvement you drove" — 90 s
- [ ] 🏗️ SD drill — add an "observability & test seams" slide to your architecture one-pager (logs, metrics, traces, feature flags)

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
