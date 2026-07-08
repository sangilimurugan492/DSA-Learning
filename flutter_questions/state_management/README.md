# State Management

Complete guide to state management in Flutter — from `setState` to advanced solutions.

## Topics

| File | Description |
|------|-------------|
| [Fundamentals](Fundamentals.md) | What is state, types, setState, lifting state, InheritedWidget |
| [Provider](Provider.md) | ChangeNotifier, Consumer, Selector, MultiProvider, ProxyProvider |
| [Riverpod](Riverpod.md) | Providers, AsyncValue, Notifiers, autoDispose, family, testing |
| [BLoC](BLoC.md) | Cubit, BLoC, Events/States, BlocProvider, BlocBuilder, testing |
| [GetX](GetX.md) | GetxController, Obx, GetX routing, dependency injection |
| [MobX](MobX.md) | Observables, Actions, Reactions, Store, code generation |
| [Redux](Redux.md) | Store, Actions, Reducers, Middleware, flutter_redux |
| [Comparison](Comparison.md) | Choosing the right solution, decision matrix, migration |
| [Best Practices](BestPractices.md) | Anti-patterns, performance, testing, architecture |

## Quick Decision Guide

```
App Size         → Recommended
──────────────────────────────────────
Small/Prototype  → setState + Provider
Medium           → Riverpod or Provider
Large/Team       → BLoC or Riverpod
Enterprise       → BLoC (strict architecture)
```

## Related
- [Beginner - State Management](../beginner/StateManagement.md)
- [Intermediate - State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [Scenarios - State Management](../scenario_based/StateManagementScenarios.md)
