# State Management Deep Dive

Comprehensive guide to state management in Jetpack Compose.

## Topics

| File | Description |
|------|-------------|
| [Fundamentals](Fundamentals.md) | Core concepts: mutableStateOf, remember, snapshot system |
| [State Hoisting](StateHoisting.md) | Pattern for stateless composables, unidirectional data flow |
| [ViewModel](ViewModel.md) | ViewModel integration, scoping, lifecycle |
| [Flow](Flow.md) | StateFlow, SharedFlow, collectAsStateWithLifecycle |
| [SavedStateHandle](SavedStateHandle.md) | Process death survival, persistence |
| [Comparison](Comparison.md) | remember vs rememberSaveable vs ViewModel vs SavedStateHandle |
| [Best Practices](BestPractices.md) | Patterns, anti-patterns, checklist |

## State Management Hierarchy

```
remember()              → Simple local state (single composable)
rememberSaveable()      → Local state that survives config change
State Holder            → Complex local state (class with remember)
ViewModel               → Screen-level state (survives config change)
SavedStateHandle        → Screen-level state (survives process death)
Repository + DataStore  → App-level persistent state
```
