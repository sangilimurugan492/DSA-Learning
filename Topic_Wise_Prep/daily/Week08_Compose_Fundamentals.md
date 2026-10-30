# Week 8 · Jetpack Compose — Fundamentals
**Mon Dec 21 – Dec 27** · 7 files / ~2,872 lines · Phase 3 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-3 detail](../weekly/Phase3_Week08-10_Compose.md) · bank [jetpack_compose_questions/](../../jetpack_compose_questions/)

### Mon Dec 21 — Composable contract (90 min)
- [ ] **Read** `README` + `beginner/` Basics, Composables (~1,235 l)
- [ ] **Say it** — the composable contract (pure, ordered, skippable, restartable) and what breaks each clause (120 s)
- [ ] **Task** — convert one XML screen to Compose; then deliberately break purity (a `var` outside, a side effect in body) and observe the behaviour change
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Dec 22 — Layout & modifiers (90 min)
- [ ] **Read** `beginner/` Layouts, Modifiers (~643 l)
- [ ] **Say it** — modifier order effects (`padding` vs `background`), `weight`/`Arrangement`/`Alignment` model (90 s)
- [ ] **Task** — build 1 screen with a nested-scroll conflict + its fix (`nestedScroll`), and 1 screen using `ConsumeWindowInsets`-safe padding
- [ ] **Log** — summary + tracker

### Wed Dec 23 — State & side effects (90 min)
- [ ] **Read** `beginner/` State, SideEffects (~994 l)
- [ ] **Say it** — `remember` vs `rememberSaveable` vs ViewModel; `LaunchedEffect` vs `DisposableEffect` vs `derivedStateOf` (120 s)
- [ ] **Task** — write a composable that debounces a search field (150 ms) with `snapshotFlow` + `debounce`; prove with logs that keystrokes don't hit the repo
- [ ] **Log** — summary + tracker

### Thu Dec 24 — 🔧 Build day (90 min, no new files)
- [ ] Build one list screen end-to-end in Compose: `StateFlow` → `LazyColumn` (keys + `contentType`) → pull-to-refresh → error/empty state
- [ ] **Task** — turn on recomposition debugging (Layout Inspector / `CompositionTracer`) and record which nodes recompose when you toggle one state — write the 3-line finding
- [ ] **Log** — summary + tracker

### Sat Dec 26 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 4 + Week 5** — 10 Qs each (coroutines/Flow + lifecycle)
- [ ] **Task** — start *Compose recomposition/performance one-pager* (due W10): 6 causes of unnecessary recomposition + the fix for each

### Sun Dec 27 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-8 quiz Qs](../weekly/Phase3_Week08-10_Compose.md) + 5 recall: ① `StableMarker`/`@Immutable` ② `key` in `LazyColumn` ③ what `Modifier.composed` costs ④ `CompositionLocal` when-to-not ⑤ effect keys semantics
- [ ] 🎤 STAR #8 — [ComposeBehavioralQuestions.md](../../behavioral_questions/ComposeBehavioralQuestions.md) (2nd half) — 90 s
- [ ] 🏗️ SD drill — draw composition → layout → draw pipeline and mark where each of your 6 perf fixes acts

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
