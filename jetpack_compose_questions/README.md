# Jetpack Compose Interview Questions

Comprehensive interview questions and answers for Jetpack Compose, organized by difficulty level.

## Structure

| Level | Topics |
|-------|--------|
| [Beginner](beginner/) | Basics, Composables, Layouts, State, Modifiers, Side Effects |
| [Intermediate](intermediate/) | State Management, Navigation, Animations, Lists, Theming, Effects |
| [Advanced](advanced/) | Performance, Architecture, Custom Layouts, Testing, Interop, Internals |
| [Scenario-Based](scenario_based/) | Real-world problems with solutions |
| [State Management](state_management/) | Deep dive into all state management solutions |

> 📌 See also: [Behavioral Questions](../behavioral_questions/) — STAR-format behavioral interview questions for Senior Mobile Engineers

## Quick Start

```
Beginner → Intermediate → Advanced → Scenario-Based → State Management
```

## Topics Overview

### Beginner
- [Basics](beginner/Basics.md) — What is Compose, @Composable, recomposition
- [Composables](beginner/Composables.md) — Text, Button, Image, Box, Column, Row
- [Layouts](beginner/Layouts.md) — Column, Row, Box, ConstraintLayout, Spacer
- [State](beginner/State.md) — remember, mutableStateOf, state hoisting
- [Modifiers](beginner/Modifiers.md) — padding, size, background, clip, border
- [Side Effects](beginner/SideEffects.md) — LaunchedEffect, rememberCoroutineScope, DisposableEffect

### Intermediate
- [State Management](intermediate/StateManagement.md) — ViewModel, StateFlow, collectAsState
- [Navigation](intermediate/Navigation.md) — NavHost, NavGraph, deep links, arguments
- [Animations](intermediate/Animations.md) — animate*AsState, AnimatedVisibility, updateTransition
- [Lists](intermediate/Lists.md) — LazyColumn, LazyRow, LazyGrid, keys, contentPadding
- [Theming](intermediate/Theming.md) — MaterialTheme, dark mode, dynamic color, typography
- [Effects](intermediate/Effects.md) — Side effects, produceState, derivedStateOf, snapshotFlow

### Advanced
- [Performance](advanced/Performance.md) — Stability, skippable, recomposition, keys
- [Architecture](advanced/Architecture.md) — MVI, MVI + Compose, state holders
- [Custom Layouts](advanced/CustomLayouts.md) — Layout, SubcomposeLayout, custom measuring
- [Testing](advanced/Testing.md) — createComposeRule, semantics, UI tests
- [Interop](advanced/Interop.md) — Compose in XML, XML in Compose, migration
- [Internals](advanced/Internals.md) — Slot table, Composer, snapshot system, phases

### Scenario-Based
- [State Scenarios](scenario_based/StateScenarios.md) — Form state, cart, auth flow, persistence
- [Performance Scenarios](scenario_based/PerformanceScenarios.md) — Recomposition, lists, images
- [UI Scenarios](scenario_based/UIScenarios.md) — Responsive, custom components, bottom sheets
- [Navigation Scenarios](scenario_based/NavigationScenarios.md) — Nested nav, auth flow, deep links
- [Debugging Scenarios](scenario_based/DebuggingScenarios.md) — Infinite recomposition, state loss

### State Management
- [Fundamentals](state_management/Fundamentals.md) — State, remember, mutableStateOf, hoisting
- [State Hoisting](state_management/StateHoisting.md) — Stateful vs stateless, patterns
- [ViewModel](state_management/ViewModel.md) — ViewModel, SavedStateHandle, lifecycle
- [Flow](state_management/Flow.md) — StateFlow, SharedFlow, collectAsStateWithLifecycle
- [SavedStateHandle](state_management/SavedStateHandle.md) — Process death, restoration
- [Comparison](state_management/Comparison.md) — All solutions compared
- [Best Practices](state_management/BestPractices.md) — Anti-patterns, performance, testing
