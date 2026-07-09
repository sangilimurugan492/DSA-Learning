# Week 7-8: Jetpack Compose (All Levels) + State Management

> **Duration:** 2 weeks | **Hours:** 36 hrs (18 hrs/week) | **DSA Problems:** ~20

---

## 📅 Daily Schedule

### Week 7: Compose Beginner + Intermediate

| Day | Time | Theory (1.5 hr) | DSA (30 min) |
|-----|------|-----------------|--------------|
| Mon | 2hr | 🔴 [Compose Basics](../jetpack_compose_questions/beginner/Basics.md) + [Composables](../jetpack_compose_questions/beginner/Composables.md) | 2 graph problems |
| Tue | 2hr | 🔴 [Layouts](../jetpack_compose_questions/beginner/Layouts.md) + [Modifiers](../jetpack_compose_questions/beginner/Modifiers.md) | 2 graph problems |
| Wed | 2hr | 🔴 [State](../jetpack_compose_questions/beginner/State.md) + [Side Effects](../jetpack_compose_questions/beginner/SideEffects.md) | 2 graph problems |
| Thu | 2hr | 🔴 [Animations](../jetpack_compose_questions/intermediate/Animations.md) + [Effects](../jetpack_compose_questions/intermediate/Effects.md) | 2 backtracking problems |
| Fri | 2hr | 🔴 [Lists](../jetpack_compose_questions/intermediate/Lists.md) + [Navigation](../jetpack_compose_questions/intermediate/Navigation.md) | 2 backtracking problems |
| Sat | 4hr | 🔴 [State Management](../jetpack_compose_questions/intermediate/StateManagement.md) + [Theming](../jetpack_compose_questions/intermediate/Theming.md) | 4 backtracking problems |
| Sun | 4hr | 🔴 [State Mgmt Fundamentals](../jetpack_compose_questions/state_management/Fundamentals.md) + [State Hoisting](../jetpack_compose_questions/state_management/StateHoisting.md) + Revision | 4 greedy problems |

### Week 8: Compose Advanced + Scenarios + State Management

| Day | Time | Theory (1.5 hr) | DSA (30 min) |
|-----|------|-----------------|--------------|
| Mon | 2hr | 🟡 [Architecture](../jetpack_compose_questions/advanced/Architecture.md) + [Custom Layouts](../jetpack_compose_questions/advanced/CustomLayouts.md) | 2 greedy problems |
| Tue | 2hr | 🟡 [Internals](../jetpack_compose_questions/advanced/Internals.md) + [Interop](../jetpack_compose_questions/advanced/Interop.md) | 2 simulation problems |
| Wed | 2hr | 🟡 [Performance](../jetpack_compose_questions/advanced/Performance.md) + [Testing](../jetpack_compose_questions/advanced/Testing.md) | 2 simulation problems |
| Thu | 2hr | 🟡 [ViewModel (SM)](../jetpack_compose_questions/state_management/ViewModel.md) + [SavedStateHandle](../jetpack_compose_questions/state_management/SavedStateHandle.md) | 2 mixed problems |
| Fri | 2hr | 🟡 [Flow (SM)](../jetpack_compose_questions/state_management/Flow.md) + [Comparison](../jetpack_compose_questions/state_management/Comparison.md) | 2 mixed problems |
| Sat | 4hr | 🟡 [Best Practices (SM)](../jetpack_compose_questions/state_management/BestPractices.md) + [Compose Scenarios](../jetpack_compose_questions/scenario_based/README.md) | 4 mixed problems |
| Sun | 4hr | 🟡 [Compose Scenarios](../jetpack_compose_questions/scenario_based/) — Debugging, Navigation, Performance, State, UI + Revision | 4 mixed problems |

---

## 📖 Topics to Cover

### Compose Beginner (6 files) 🔴
| File | Key Concepts |
|------|-------------|
| [Basics](../jetpack_compose_questions/beginner/Basics.md) | @Composable, remember, MutableState |
| [Composables](../jetpack_compose_questions/beginner/Composables.md) | Text, Button, Image, Box, Column, Row |
| [Layouts](../jetpack_compose_questions/beginner/Layouts.md) | Column, Row, Box, ConstraintLayout, Spacer |
| [Modifiers](../jetpack_compose_questions/beginner/Modifiers.md) | padding, fillMaxSize, click, background, border |
| [State](../jetpack_compose_questions/beginner/State.md) | remember, rememberSaveable, derivedStateOf |
| [Side Effects](../jetpack_compose_questions/beginner/SideEffects.md) | LaunchedEffect, DisposableEffect, SideEffect |

### Compose Intermediate (6 files) 🔴
| File | Key Concepts |
|------|-------------|
| [Animations](../jetpack_compose_questions/intermediate/Animations.md) | animate*AsState, AnimatedVisibility, transition |
| [Effects](../jetpack_compose_questions/intermediate/Effects.md) | LaunchedEffect, rememberCoroutineScope, produceState |
| [Lists](../jetpack_compose_questions/intermediate/Lists.md) | LazyColumn, LazyRow, items, key, contentPadding |
| [Navigation](../jetpack_compose_questions/intermediate/Navigation.md) | NavHost, composable, navController, deep links |
| [State Management](../jetpack_compose_questions/intermediate/StateManagement.md) | hoisting, unidirectional data flow, ViewModel |
| [Theming](../jetpack_compose_questions/intermediate/Theming.md) | MaterialTheme, colorScheme, typography, shapes |

### Compose Advanced (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Architecture](../jetpack_compose_questions/advanced/Architecture.md) | MVI, unidirectional flow, state holders |
| [Custom Layouts](../jetpack_compose_questions/advanced/CustomLayouts.md) | Layout composable, MeasurePolicy, SubcomposeLayout |
| [Internals](../jetpack_compose_questions/advanced/Internals.md) | Slot table, recomposition, applier |
| [Interop](../jetpack_compose_questions/advanced/Interop.md) | AndroidView, ViewInterop, ComposeView |
| [Performance](../jetpack_compose_questions/advanced/Performance.md) | stability, skippable, key, derivedStateOf |
| [Testing](../jetpack_compose_questions/advanced/Testing.md) | createComposeRule, onNodeWithText, assertIsDisplayed |

### Compose Scenarios (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Debugging Scenarios](../jetpack_compose_questions/scenario_based/DebuggingScenarios.md) | Recomposition loops, state loss |
| [Navigation Scenarios](../jetpack_compose_questions/scenario_based/NavigationScenarios.md) | Nested nav, bottom nav, deep links |
| [Performance Scenarios](../jetpack_compose_questions/scenario_based/PerformanceScenarios.md) | Lazy list optimization, stability |
| [State Scenarios](../jetpack_compose_questions/scenario_based/StateScenarios.md) | Complex state, restoration |
| [UI Scenarios](../jetpack_compose_questions/scenario_based/UIScenarios.md) | Custom UI, animations, theming |

### Compose State Management (8 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Fundamentals](../jetpack_compose_questions/state_management/Fundamentals.md) | Stateful vs stateless, hoisting |
| [State Hoisting](../jetpack_compose_questions/state_management/StateHoisting.md) | Pattern, unidirectional data flow |
| [ViewModel](../jetpack_compose_questions/state_management/ViewModel.md) | ViewModel + StateFlow + collectAsState |
| [SavedStateHandle](../jetpack_compose_questions/state_management/SavedStateHandle.md) | Process death survival |
| [Flow](../jetpack_compose_questions/state_management/Flow.md) | collectAsState, collectAsStateWithLifecycle |
| [Comparison](../jetpack_compose_questions/state_management/Comparison.md) | StateFlow vs SharedFlow vs LiveData |
| [Best Practices](../jetpack_compose_questions/state_management/BestPractices.md) | Patterns, anti-patterns |

---

## 🧮 DSA Problems (Week 7-8)

### Week 7: Graphs + Backtracking + Greedy

| Day | Problem | File | Difficulty |
|-----|---------|------|-----------|
| Mon | Graph BFS/DFS | [DSA/graph/bfs_dfs/](../DSA/graph/bfs_dfs/) | Medium |
| Mon | Graph class | [Graph](../DSA/graph/Graph.kt) | Easy |
| Tue | Shortest path | [DSA/graph/shortest_path/](../DSA/graph/shortest_path/) | Medium-Hard |
| Tue | Topological sort | [DSA/graph/topological_sort/](../DSA/graph/topological_sort/) | Medium |
| Wed | Union find | [DSA/graph/union_find/](../DSA/graph/union_find/) | Medium |
| Wed | Graph review | [DSA/graph/](../DSA/graph/) | Medium-Hard |
| Thu | Combination Sum | [CombinationSum](../DSA/BackTracking/CombinationSum.kt) | Medium |
| Thu | Permutations | [Permutations](../DSA/BackTracking/Permutations.kt) | Medium |
| Fri | Subsets | [Subsets](../DSA/BackTracking/Subsets.kt) | Medium |
| Fri | Word Search | [WordSearch](../DSA/BackTracking/WordSearch.kt) | Medium |
| Sat | Letter Combinations | [LetterAndCombinationPhoneNumber](../DSA/BackTracking/LetterAndCombinationPhoneNumber.kt) | Medium |
| Sat | Greedy review | [DSA/array/greedy/](../DSA/array/greedy/) | Easy-Hard |
| Sun | Backtracking review | [DSA/BackTracking/](../DSA/BackTracking/) | Medium |
| Sun | Graph review | [DSA/graph/](../DSA/graph/) | Medium-Hard |

### Week 8: Simulation + Mixed Review

| Day | Problem | File | Difficulty |
|-----|---------|------|-----------|
| Mon | Simulation review | [DSA/simulation/](../DSA/simulation/) | Easy-Hard |
| Mon | Stack review | [DSA/stack/](../DSA/stack/) | Easy-Hard |
| Tue | Array review | [DSA/array/](../DSA/array/) | Easy-Hard |
| Tue | Tree review | [DSA/tree/](../DSA/tree/) | Easy-Hard |
| Wed | Heap review | [DSA/heap/](../DSA/heap/) | Medium-Hard |
| Wed | Trie review | [DSA/trie/](../DSA/trie/) | Medium-Hard |
| Thu | Linked list review | [DSA/linked_list/](../DSA/linked_list/) | Easy-Med |
| Thu | Sorting review | [DSA/sorting/](../DSA/sorting/) | Easy-Med |
| Fri | Mixed (all topics) | — | Easy-Hard |
| Sat | Mixed (all topics) | — | Easy-Hard |
| Sun | Mixed (all topics) | — | Easy-Hard |

---

## 🧠 Key Concepts to Memorize

### Compose Core
- `@Composable` = composable function
- `remember` = survive recomposition, `rememberSaveable` = survive config change
- `mutableStateOf` = observable state
- State hoisting = move state up, pass events down
- Unidirectional data flow: State flows down, events flow up

### Compose Side Effects
- `LaunchedEffect` = run coroutine on composition
- `DisposableEffect` = cleanup on leave
- `rememberCoroutineScope` = scope for coroutines
- `produceState` = convert non-Compose state
- `derivedStateOf` = computed state

### Compose Performance
- `key` = identify items in lists
- `stable` = prevent unnecessary recomposition
- `derivedStateOf` = only recompute when deps change
- `Immutable` = mark class as immutable

### Compose Lists
- `LazyColumn` / `LazyRow` = efficient scrolling
- `items(key = { it.id })` = stable keys
- `contentType` = recycling view types

---

## ✅ Self-Assessment Checklist

### Compose Beginner
- [ ] Can build a screen with Column, Row, Box
- [ ] Can use modifiers (padding, click, background)
- [ ] Can manage state with remember/rememberSaveable
- [ ] Can use LaunchedEffect for side effects

### Compose Intermediate
- [ ] Can build a LazyColumn with multiple item types
- [ ] Can implement navigation with NavHost
- [ ] Can create animations (animate*AsState, AnimatedVisibility)
- [ ] Can apply MaterialTheme with custom colors/typography

### Compose Advanced
- [ ] Can create a custom Layout
- [ ] Can use AndroidView for interop
- [ ] Can optimize recomposition with key/derivedStateOf
- [ ] Can write Compose UI tests

### State Management
- [ ] Can implement state hoisting pattern
- [ ] Can use ViewModel + StateFlow + collectAsStateWithLifecycle
- [ ] Can survive process death with SavedStateHandle
- [ ] Can differentiate StateFlow vs SharedFlow vs LiveData

### DSA
- [ ] Solved 20 graph/backtracking/greedy/simulation problems
- [ ] Comfortable with BFS/DFS traversal
- [ ] Comfortable with backtracking template
- [ ] Can solve graph problems with union-find

---

## 🔗 Next
- [Week 9: DSA Intensive](Week9_DSA_Intensive.md)
- [Back to README](README.md)
