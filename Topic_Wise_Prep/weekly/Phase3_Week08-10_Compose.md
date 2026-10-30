# Phase 3 (Weeks 8–10) — Jetpack Compose

> Bank: [jetpack_compose_questions/](../../jetpack_compose_questions/)
> Compose is the highest-frequency modern-Android topic: expect recomposition, stability, side-effect and state-hoisting questions in every round.
> Phase deliverable: **recomposition/perf one-pager** + one screen you can whiteboard (list + search + pagination) in Compose pseudocode.

---

## Week 8 — Compose Fundamentals · Dec 21 – Dec 27 · 7 files / 2,872 lines

| Day | Files (in [jetpack_compose_questions/](../../jetpack_compose_questions/)) | ~Lines |
|-----|---------------------------------------------------------------------------|--------|
| Mon | `README` · `beginner/`: Basics, Composables | 1,235 |
| Tue | `beginner/`: Layouts, Modifiers | 643 |
| Wed | `beginner/`: SideEffects, State | 994 |
| Thu | 🔧 **Build day** — rewrite one XML screen of a real app in Compose; no new files | — |
| Sat | 10 hardest of W8 + 10 from W4 (coroutines/Flow — Compose collects them) | — |
| Sun | 🧪 quiz · 🎤 STAR #8 · 🏗️ draw the composition → layout → draw pipeline | — |

**Quiz:** ① What makes a composable *restartable/skippable*, and what breaks skipping? ② `remember` vs `rememberSaveable` vs ViewModel state — what survives rotation/process death? ③ Modifier order: why does `.padding().background()` differ from `.background().padding()`? ④ `LaunchedEffect` vs `DisposableEffect` vs `SideEffect` vs `derivedStateOf` — pick each correctly. ⑤ State hoisting: what goes up, what stays down, and why testability improves.

---

## Week 9 — State, Navigation, Theming · Dec 28 – Jan 3 · 14 files / 4,377 lines

| Day | Files (in [jetpack_compose_questions/](../../jetpack_compose_questions/)) | ~Lines |
|-----|---------------------------------------------------------------------------|--------|
| Mon | `intermediate/`: Animations, Effects, Lists | 917 |
| Tue | `intermediate/`: Navigation, StateManagement, Theming | 1,257 |
| Wed | `state_management/`: BestPractices, Comparison, Flow, Fundamentals | 1,111 |
| Thu | `state_management/`: README, SavedStateHandle, StateHoisting, ViewModel | 1,092 |
| Sat | 10 hardest of W9 + 10 from W5 (Android lifecycle tie-in) | — |
| Sun | 🧪 quiz · 🎤 STAR #9 · 🏗️ sketch unidirectional data flow: UI event → ViewModel → repository → StateFlow → UI | — |

**Quiz:** ① `LazyColumn` keys + `contentType` — what happens with missing keys during insert? ② `collectAsState` vs `collectAsStateWithLifecycle` — why the second one exists. ③ Type-safe navigation (Compose Navigation 2.7+/2.8): route args, deep links, back stack and process death restore. ④ Where does `SavedStateHandle` fit vs a Compose-only `remember`? ⑤ Material 3 theming: dynamic color, typography tokens, and theming a component library.

---

## Week 10 — Advanced Compose + Scenario Bootcamp · Jan 4 – Jan 10 · 12 files / 3,737 lines

| Day | Files (in [jetpack_compose_questions/](../../jetpack_compose_questions/)) | ~Lines |
|-----|---------------------------------------------------------------------------|--------|
| Mon | `advanced/`: Architecture, CustomLayouts, Internals | 1,024 |
| Tue | `advanced/`: Interop, Performance, Testing | 1,208 |
| Wed | `scenario_based/`: DebuggingScenarios, NavigationScenarios, PerformanceScenarios | 845 |
| Thu | `scenario_based/`: README, StateScenarios, UIScenarios | 660 |
| Sat | 10 hardest of W10 + 10 from W7 (Android advanced) | — |
| Sun | 🧪 quiz · 🎤 STAR #10 · 🏗️ **Phase-3 closed-book re-quiz + write perf one-pager** | — |

**Quiz:** ① How do you measure recomposition (Layout Inspector, `CompositionTracer`, macrobenchmark) and name 5 causes of jank with their fixes. ② `Modifier` chaining + custom `Layout` — measure/place API in one paragraph. ③ Compose↔View interop: `ComposeView`, `AndroidView`, `rememberComposeFromView` — pitfalls (focus, nested scroll, insets). ④ Stability: `@Stable` vs `@Immutable`, and why an unwrapped `List<>` parameter kills skipping. ⑤ Compose testing: `createComposeRule`, semantics matchers, and how you test a ViewModel-driven screen without rendering.

**Checkpoint before Phase 4:** perf one-pager done; you can answer "Compose vs Views on a large list screen" in 2 minutes with numbers.
