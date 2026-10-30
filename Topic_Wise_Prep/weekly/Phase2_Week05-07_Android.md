# Phase 2 (Weeks 5–7) — Android Core

> Bank: [android_questions/](../../android_questions/)
> Every "scenario_based" file is **non-skippable** — those are the questions that decide senior-level rounds.
> Phase deliverable: **architecture one-pager** (your default MVVM/Clean answer: layers, data flow, module boundaries) + a 2-minute "how I debugged a janky screen / ANR" story.

---

## Week 5 — Android Fundamentals · Nov 30 – Dec 6 · 10 files / 2,583 lines

| Day | Files (in [android_questions/](../../android_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `README` · `beginner/`: ActivityLifecycle, Basics, FragmentLifecycle | 714 |
| Tue | `beginner/`: Intents, Permissions, RecyclerView | 724 |
| Wed | `beginner/`: UILayouts · `scenario_based/LifecycleScenarios` | 558 |
| Thu | `scenario_based/UIScenarios` | 587 |
| Sat | 10 hardest of W5 + 10 from W2 (Java) | — |
| Sun | 🧪 quiz · 🎤 STAR #5 · 🏗️ draw the Activity/Fragment lifecycle incl. process death | — |

**Quiz:** ① Activity vs Fragment lifecycle callbacks in order, incl. `onSaveInstanceState` timing. ② What survives process death and what must be re-created (ViewModel vs SavedStateHandle vs savedInstanceState)? ③ Explicit vs implicit intents + Android 11+ package visibility. ④ Runtime permission flows incl. "don't ask again" and rationale rules. ⑤ RecyclerView: `DiffUtil` payload, `setHasFixedSize`, why `notifyDataSetChanged` kills performance.

---

## Week 6 — Architecture & Data Layer · Dec 7 – Dec 13 · 11 files / 3,780 lines

| Day | Files (in [android_questions/](../../android_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `intermediate/`: ArchitecturePatterns, DataBinding, DataStore | 985 |
| Tue | `intermediate/`: Navigation, Paging3 | 951 |
| Wed | `intermediate/`: Retrofit, RoomDatabase, ViewModelLiveData | 869 |
| Thu | `intermediate/WorkManager` · `scenario_based/`: NetworkingScenarios, BackgroundTaskScenarios | 975 |
| Sat | 10 hardest of W6 + 10 from W3 (Kotlin) | — |
| Sun | 🧪 quiz · 🎤 STAR #6 · 🏗️ sketch repository → Room → Flow → UI data flow | — |

**Quiz:** ① MVVM vs MVI vs Clean — state ownership, unidirectional flow, when MVI is overkill. ② `StateFlow` vs `LiveData` vs `SharedFlow` in a ViewModel — collection lifecycle & conflation. ③ Room: indexing, `@Transaction`, migrations, and how a bad migration ships a crash. ④ Paging 3: `PagingSource` keys, placeholders, retry, remote-mediation. ⑤ WorkManager: constraints, periodic vs one-time, exact vs expedited, why not a `Service` for deferrable work.

---

## Week 7 — Advanced Android & Hardening · Dec 14 – Dec 20 · 11 files / 3,629 lines

| Day | Files (in [android_questions/](../../android_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `advanced/`: CICD, CustomViews, DependencyInjection | 908 |
| Tue | `advanced/`: Modularization, Performance, PlayIntegrity | 1,158 |
| Wed | `advanced/`: Security, Testing · `scenario_based/ArchitectureScenarios` | 1,067 |
| Thu | `scenario_based/`: DebuggingScenarios, `README` (index/quiz) | 496 |
| Sat | 10 hardest of W7 + 10 from W4 (Kotlin coroutines) | — |
| Sun | 🧪 quiz · 🎤 STAR #7 · 🏗️ **Phase-2 closed-book re-quiz (20 Qs) + write architecture one-pager** | — |

**Quiz:** ① Hilt/Dagger: scopes, components, `@Inject` vs `@Provides` vs module boundaries. ② Modularisation: feature vs layer modules, dependency direction, build-time win, the "leaky abstraction" risk. ③ Startup: how you measure (Macrobenchmark/Perfetto), baseline profiles, deferred init. ④ Memory/jank: StrictMode, LeakCanary root-cause report reading, frame-drop triage. ⑤ Play Integrity vs SafetyNet, why rooted/emulator detection matters, and where attestation belongs in your API contract.

**Checkpoint before Phase 3:** architecture one-pager done, debug story rehearsed to 2 minutes, Compose basics already touched in W8 preview (read `jetpack_compose_questions/README.md`).
