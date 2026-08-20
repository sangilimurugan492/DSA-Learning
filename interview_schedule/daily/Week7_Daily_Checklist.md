# 📅 Week 7: Compose Beginner + Intermediate + Flutter State Management
> **Oct 1 (Thu) – Oct 7 (Wed)** | Topics: 28 files | DSA: ~18 problems
> [← Week 6](Week6_Daily_Checklist.md) | [Back to Daily Checklist](../Daily_Checklist.md) | [Week 8 →](Week8_Daily_Checklist.md)

---

## Day 43 — Thursday, 01/10/2026

**🤖 Compose Beginner (45 min):**
- [ ] 🔴 Compose Basics — [Basics.md](../../jetpack_compose_questions/beginner/Basics.md) — *@Composable, remember, MutableState*
- [ ] 🔴 Composables — [Composables.md](../../jetpack_compose_questions/beginner/Composables.md) — *Text, Button, Image, Box, Column, Row*

**🐦 Flutter State Management (45 min):**
- [ ] 🔴 SM Fundamentals — [Fundamentals.md](../../flutter_questions/state_management/Fundamentals.md) — *Ephemeral vs app state, unidirectional flow*
- [ ] 🔴 Provider — [Provider.md](../../flutter_questions/state_management/Provider.md) — *ChangeNotifier, Consumer, Selector*

**🔄 Cross-Platform Insight:**
- Jetpack Compose `@Composable` + `remember` ↔ Flutter `StatefulWidget` + `setState` — both reactive UI frameworks
- Compose `mutableStateOf` ↔ Flutter `ChangeNotifier.notifyListeners()`

**🧮 DSA Practice (30 min):**
- [ ] **[Graph › BFS/DFS | 🟡 Medium]** Number of Islands (25 min) — [NumberOfIslands.kt](../../DSA/graph/bfs_dfs/NumberOfIslands.kt)
- [ ] **[Graph › BFS/DFS | 🟡 Medium]** Clone Graph (25 min) — [CloneGraph.kt](../../DSA/graph/bfs_dfs/CloneGraph.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can build a Compose screen with Column, Row, Box
- [ ] Can use `remember` and `mutableStateOf`
- [ ] Can use Provider with ChangeNotifier in Flutter
- [ ] Solved 2 DSA problems

---

## Day 44 — Friday, 02/10/2026

**🤖 Compose Beginner (45 min):**
- [ ] 🔴 Layouts — [Layouts.md](../../jetpack_compose_questions/beginner/Layouts.md) — *Column, Row, Box, ConstraintLayout, Spacer*
- [ ] 🔴 Modifiers — [Modifiers.md](../../jetpack_compose_questions/beginner/Modifiers.md) — *padding, fillMaxSize, click, background, border*

**🐦 Flutter State Management (45 min):**
- [ ] 🔴 BLoC — [BLoC.md](../../flutter_questions/state_management/BLoC.md) — *Events, states, BlocBuilder, BlocListener*
- [ ] Practice: Build a Flutter app using BLoC pattern

**🔄 Cross-Platform Insight:**
- Compose Modifiers (chain) ↔ Flutter widgets (nesting) — Compose chains modifiers, Flutter nests widgets

**🧮 DSA Practice (30 min):**
- [ ] **[Graph › Shortest Path | 🟡 Medium]** Dijkstra's Algorithm (25 min) — [Dijkstra.kt](../../DSA/graph/shortest_path/Dijkstra.kt)
- [ ] **[Graph › Topological Sort | 🟡 Medium]** Course Schedule (25 min) — [CourseSchedule.kt](../../DSA/graph/topological_sort/CourseSchedule.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can use modifiers (padding, click, background)
- [ ] Can build a BLoC pattern in Flutter
- [ ] Solved 2 DSA problems

---

## Day 45 — Saturday, 03/10/2026 (Weekend — 4-5 hrs)

**🤖 Compose Beginner (1 hr):**
- [ ] 🔴 State — [State.md](../../jetpack_compose_questions/beginner/State.md) — *remember, rememberSaveable, derivedStateOf*
- [ ] 🔴 Side Effects — [SideEffects.md](../../jetpack_compose_questions/beginner/SideEffects.md) — *LaunchedEffect, DisposableEffect, SideEffect*

**🤖 Compose Intermediate (1 hr):**
- [ ] 🔴 Animations — [Animations.md](../../jetpack_compose_questions/intermediate/Animations.md) — *animate*AsState, AnimatedVisibility, transition*
- [ ] 🔴 Effects — [Effects.md](../../jetpack_compose_questions/intermediate/Effects.md) — *LaunchedEffect, rememberCoroutineScope, produceState*
- [ ] 🔴 Lists — [Lists.md](../../jetpack_compose_questions/intermediate/Lists.md) — *LazyColumn, LazyRow, items, key, contentPadding*
- [ ] 🔴 Navigation — [Navigation.md](../../jetpack_compose_questions/intermediate/Navigation.md) — *NavHost, composable, navController, deep links*

**🐦 Flutter State Management (1 hr):**
- [ ] 🔴 Riverpod — [Riverpod.md](../../flutter_questions/state_management/Riverpod.md) — *Providers, ref.watch, ref.read, code-gen*
- [ ] 🔴 GetX — [GetX.md](../../flutter_questions/state_management/GetX.md) — *GetXController, Rx, bindings*
- [ ] Practice: Build a Flutter app using Riverpod

**🧮 DSA Practice (1.5 hr):**
- [ ] **[Graph › Union Find | 🟡 Medium]** Number of Provinces (25 min) — [NumberOfProvinces.kt](../../DSA/graph/union_find/NumberOfProvinces.kt)
- [ ] **[Graph › Union Find | 🟡 Medium]** Graph Valid Tree (25 min) — [GraphValidTree.kt](../../DSA/graph/union_find/GraphValidTree.kt)
- [ ] **[Backtracking › Combination Sum | 🟡 Medium]** Combination Sum (25 min) — [CombinationSum.kt](../../DSA/BackTracking/combination_sum/CombinationSum.kt)
- [ ] **[Backtracking › Permutations | 🟡 Medium]** Permutations (25 min) — [Permutations.kt](../../DSA/BackTracking/permutations/Permutations.kt)

**🎁 EXTRA TOPIC:**
- [ ] 🔄 Compose vs Flutter widgets — Compare Compose `@Composable` vs Flutter `StatelessWidget`

**✅ End-of-Day Self-Check:**
- [ ] Can manage state with remember/rememberSaveable
- [ ] Can use LaunchedEffect for side effects
- [ ] Can build a LazyColumn with multiple item types
- [ ] Can use Riverpod in Flutter
- [ ] Solved 4 DSA problems

---

## Day 46 — Sunday, 04/10/2026 (Weekend — 4-5 hrs)

**🤖 Compose Intermediate (1 hr):**
- [ ] 🔴 State Management — [StateManagement.md](../../jetpack_compose_questions/intermediate/StateManagement.md) — *hoisting, unidirectional data flow, ViewModel*
- [ ] 🔴 Theming — [Theming.md](../../jetpack_compose_questions/intermediate/Theming.md) — *MaterialTheme, colorScheme, typography, shapes*

**🤖 Compose State Management (1 hr):**
- [ ] 🔴 SM Fundamentals — [Fundamentals.md](../../jetpack_compose_questions/state_management/Fundamentals.md) — *Stateful vs stateless, hoisting*
- [ ] 🔴 State Hoisting — [StateHoisting.md](../../jetpack_compose_questions/state_management/StateHoisting.md) — *Pattern, unidirectional data flow*
- [ ] 🔴 ViewModel (SM) — [ViewModel.md](../../jetpack_compose_questions/state_management/ViewModel.md) — *ViewModel + StateFlow + collectAsState*

**🧮 DSA Practice (1.5 hr):**
- [ ] **[Backtracking › Subsets | 🟡 Medium]** Subsets (25 min) — [Subsets.kt](../../DSA/BackTracking/subsets/Subsets.kt)
- [ ] **[Backtracking › Word Search | 🟡 Medium]** Word Search (30 min) — [WordSearch.kt](../../DSA/BackTracking/word_search/WordSearch.kt)
- [ ] **[Backtracking › N-Queens | 🔴 Hard]** N-Queens (40 min) — [NQueens.kt](../../DSA/BackTracking/n_queens/NQueens.kt)
- [ ] **[Backtracking › Palindrome Partitioning | 🟡 Medium]** Palindrome Partitioning (30 min) — [PalindromePartitioning.kt](../../DSA/BackTracking/palindrome_partitioning/PalindromePartitioning.kt)

**🎁 EXTRA TOPIC:**
- [ ] 🔄 Riverpod deep dive — Build a Flutter app with Riverpod + go_router

**✅ End-of-Day Self-Check:**
- [ ] Can implement state hoisting pattern in Compose
- [ ] Can apply MaterialTheme with custom colors/typography
- [ ] Can use ViewModel + StateFlow + collectAsState
- [ ] Solved 4 DSA problems

---

## Day 47 — Monday, 05/10/2026

**🤖 Compose (45 min):**
- [ ] 🔄 Spaced Repetition: Compose Basics + State — write a composable from memory
- [ ] 🔄 Spaced Repetition: Side Effects — write LaunchedEffect code

**🐦 Flutter State Management (45 min):**
- [ ] 🔴 MobX — [MobX.md](../../flutter_questions/state_management/MobX.md) — *Observables, actions, reactions*
- [ ] 🔴 Redux — [Redux.md](../../flutter_questions/state_management/Redux.md) — *Store, reducer, middleware*

**🧮 DSA Practice (30 min):**
- [ ] **[Graph › BFS/DFS | 🟡 Medium]** Pacific Atlantic Water Flow (25 min) — [PacificAtlanticWaterFlow.kt](../../DSA/graph/bfs_dfs/PacificAtlanticWaterFlow.kt)
- [ ] **[Graph › BFS/DFS | 🟡 Medium]** Rotting Oranges (25 min) — [RottingOranges.kt](../../DSA/graph/bfs_dfs/rotting_oranges/RottingOranges.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can write a Compose composable from memory
- [ ] Can explain MobX observables and reactions
- [ ] Can explain Redux store/reducer/middleware
- [ ] Solved 2 DSA problems

---

## Day 48 — Tuesday, 06/10/2026

**🤖 Compose State Management (45 min):**
- [ ] 🔴 SavedStateHandle — [SavedStateHandle.md](../../jetpack_compose_questions/state_management/SavedStateHandle.md) — *Process death survival*
- [ ] 🔴 Flow (SM) — [Flow.md](../../jetpack_compose_questions/state_management/Flow.md) — *collectAsState, collectAsStateWithLifecycle*

**🐦 Flutter State Management (45 min):**
- [ ] 🔴 Comparison — [Comparison.md](../../flutter_questions/state_management/Comparison.md) — *When to use which*
- [ ] 🔴 Best Practices — [BestPractices.md](../../flutter_questions/state_management/BestPractices.md) — *Patterns, anti-patterns*

**🔄 Cross-Platform Insight:**
- Compose `collectAsStateWithLifecycle` ↔ Flutter `StreamBuilder` — both observe streams reactively

**🧮 DSA Practice (30 min):**
- [ ] **[Graph › Shortest Path | 🔴 Hard]** Word Ladder (35 min) — [WordLadder.kt](../../DSA/graph/shortest_path/WordLadder.kt)
- [ ] **[Graph › Topological Sort | 🟡 Medium]** Course Schedule II (25 min) — [CourseScheduleII.kt](../../DSA/graph/topological_sort/CourseScheduleII.kt)

**✅ End-of-Day Self-Check:**
- [ ] Can survive process death with SavedStateHandle
- [ ] Can use collectAsStateWithLifecycle
- [ ] Can explain when to use Provider vs BLoC vs Riverpod vs GetX
- [ ] Solved 2 DSA problems

---

## Day 49 — Wednesday, 07/10/2026 (Weekly Review)

**🔄 Weekly Review (1 hr):**
- [ ] Re-read notes from all Week 7 topics
- [ ] Write summary: "Compose beginner/intermediate + Flutter state management"
- [ ] Identify weak areas

**🧮 DSA Practice (1 hr):**
- [ ] **[Graph › BFS/DFS | 🟡 Medium]** Number of Islands (review) (20 min) — [NumberOfIslands.kt](../../DSA/graph/bfs_dfs/NumberOfIslands.kt)
- [ ] **[Graph › Shortest Path | 🟡 Medium]** Dijkstra (review) (20 min) — [Dijkstra.kt](../../DSA/graph/shortest_path/Dijkstra.kt)
- [ ] **[Graph › Topological Sort | 🟡 Medium]** Course Schedule (review) (20 min) — [CourseSchedule.kt](../../DSA/graph/topological_sort/CourseSchedule.kt)
- [ ] **[Graph › Union Find | 🟡 Medium]** Number of Provinces (review) (20 min) — [NumberOfProvinces.kt](../../DSA/graph/union_find/NumberOfProvinces.kt)

**✅ Week 7 Self-Assessment:**
- [ ] Can build a Compose screen with Column, Row, Box
- [ ] Can use modifiers (padding, click, background)
- [ ] Can manage state with remember/rememberSaveable
- [ ] Can use LaunchedEffect for side effects
- [ ] Can build a LazyColumn with multiple item types
- [ ] Can implement navigation with NavHost
- [ ] Can create animations (animate*AsState, AnimatedVisibility)
- [ ] Can apply MaterialTheme with custom colors/typography
- [ ] Can implement state hoisting pattern
- [ ] Can use ViewModel + StateFlow + collectAsStateWithLifecycle
- [ ] Can survive process death with SavedStateHandle
- [ ] Can differentiate StateFlow vs SharedFlow vs LiveData
- [ ] Can use Provider, BLoC, Riverpod, GetX in Flutter
- [ ] Solved 18 DSA problems this week

---

[← Week 6](Week6_Daily_Checklist.md) | [Back to Daily Checklist](../Daily_Checklist.md) | [Week 8 →](Week8_Daily_Checklist.md)
