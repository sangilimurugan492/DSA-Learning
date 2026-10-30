# Phase 4 (Weeks 11–13) — Flutter & Dart

> Bank: [flutter_questions/](../../flutter_questions/)
> Interview angle: Dart internals + widget/render-object model + **state-management trade-offs** (that is the round everyone asks about).
> Phase deliverable: **state-management comparison table** (Provider/Riverpod/BLoC/Redux/GetX/MobX: boilerplate, testability, side effects, learning curve, when to pick) + a "Flutter internals" one-pager (3 trees, build/layout/paint, isolation model).

---

## Week 11 — Dart & Widget Fundamentals · Jan 11 – Jan 17 · 7 files / 2,526 lines

| Day | Files (in [flutter_questions/](../../flutter_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `README` · `beginner/`: Basics, DartBasics | 1,604 |
| Tue | `beginner/`: Layouts, Navigation, StateManagement | 688 |
| Wed | `beginner/Widgets` | 234 |
| Thu | 🔧 **Build day** — 3-screen app with named routes + one `StatefulWidget` you convert to `ConsumerWidget`; no new files | — |
| Sat | 10 hardest of W11 + 10 from W7 (Android side-by-side: Activity vs widget lifecycle) | — |
| Sun | 🧪 quiz · 🎤 STAR #11 · 🏗️ draw the Widget / Element / RenderObject tree and say who owns state | — |

**Quiz:** ① `StatelessWidget` vs `StatefulWidget` — where does the state actually live (Element)? ② `const` constructors — what do they buy the framework? ③ `setState` vs `InheritedWidget` vs provider — rebuild scope of each. ④ Dart: `Future` vs `Stream`, `async*`/`yield`, `Isolate` vs async (single isolate, event loop). ⑤ `mainAxisSize`, `Expanded`/`Flexible`, `LayoutBuilder` vs `ConstrainedBox` — when the layout refuses to size.

---

## Week 12 — Features + State-Management Deep Dive · Jan 18 – Jan 24 · 16 files / 3,897 lines

| Day | Files (in [flutter_questions/](../../flutter_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `intermediate/`: Animations, CustomWidgets, FirebaseIntegration | 825 |
| Tue | `intermediate/`: HTTPNetworking, StateManagementAdvanced, Testing | 840 |
| Wed | `state_management/`: BLoC, BestPractices, Comparison, Fundamentals | 898 |
| Thu | `state_management/`: GetX, MobX, Provider, README, Redux | 1,070 |
| Sat | `state_management/Riverpod` + 10 hardest of W12 | 264 |
| Sun | 🧪 quiz · 🎤 STAR #12 · 🏗️ sketch BLoC event→state flow for a search-with-debounce screen | — |

**Quiz:** ① BLoC: event vs state, why `mapEventToState`/`on<Event>` must be deterministic, how to test it. ② Riverpod: provider types, `ref.listen` vs `watch`, auto-dispose, why it removes BuildContext coupling. ③ Redux vs BLoC — single source of truth vs streams; middleware/effect equivalence. ④ `dio` interceptor chain: retry, token refresh race, cancel token on route change. ⑤ Flutter testing pyramid: unit vs widget vs integration, `mocktail`/`flutter_test`, golden tests — what you actually automate.

---

## Week 13 — Advanced Flutter + Scenario Bootcamp · Jan 25 – Jan 31 · 13 files / 3,804 lines

| Day | Files (in [flutter_questions/](../../flutter_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `advanced/`: ArchitecturePatterns, CICD, DartAdvanced | 1,508 |
| Tue | `advanced/`: DependencyInjection, FlutterInternals, Performance, PlatformChannels | 769 |
| Wed | `scenario_based/`: DebuggingScenarios, NavigationScenarios, PerformanceScenarios | 837 |
| Thu | `scenario_based/`: README, StateManagementScenarios, UIScenarios | 690 |
| Sat | 10 hardest of W13 + 10 from W11–W12 | — |
| Sun | 🧪 quiz · 🎤 STAR #13 · 🏗️ **Phase-4 closed-book re-quiz + finish comparison table** | — |

**Quiz:** ① Why 60/120 fps drops when you build widgets in `build()` — name the pipeline stage that costs. ② `RepaintBoundary`, `const`, list view builder, `cacheExtent` — which one fixes which profile flame (build/layout/paint/raster). ③ Platform channels: method vs event vs message channels, threading (main vs background isolate), codec choice. ④ Isolate use for JSON/DB heavy work; what stays on the UI isolate anyway. ⑤ Flutter vs Compose vs native — answer in 2 minutes with concrete trade-offs (startup, binary size, hot reload, ecosystem, perf ceiling).

**Checkpoint before Phase 5:** comparison table finished; you have one "Flutter project I shipped/migrated" STAR story with metrics.
