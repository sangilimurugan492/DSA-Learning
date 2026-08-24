# State Management

## 📖 Explanation

State is data that can change over time and affects the UI. State management is how you create, store, retrieve, and update this data. Flutter offers multiple approaches — from simple `setState()` to full architectures like BLoC.

### Types of State
| Ephemeral (Local) State | App (Shared) State |
|------------------------|---------------------|
| Single widget | Multiple widgets/screens |
| `setState()` | Provider, Riverpod, BLoC |
| Form validation | User session |
| Tab index | Shopping cart |
| Animation state | Theme/locale |

> **Rule:** Start with `setState()`. Only add a state management solution when state needs to be shared.

### `setState()`
`setState()` marks a `StatefulWidget` as dirty, triggering a rebuild. It's synchronous — don't put async work inside it. Always check `mounted` after async gaps before calling `setState()`.

### setState() Rules
- ✅ Do: Modify state inside `setState(() { _count++; })`
- ❌ Don't: Call in `build()` (infinite loop)
- ❌ Don't: Call after async gap without checking `mounted`
- ❌ Don't: Put async work inside `setState` (it's synchronous)

### Lifting State Up
When two sibling widgets need to share state, move the state to their common parent. The parent holds the state and passes it down via constructor, and passes callbacks down for modifications.

### Provider
Provider is a wrapper around `InheritedWidget` with a simpler API. Use `ChangeNotifierProvider` at the top of the tree, `Consumer` or `context.watch` to rebuild on change, and `context.read` for one-time access.

| Method | Rebuilds? | Use Case |
|--------|-----------|----------|
| `Consumer<T>` | ✅ Yes | Rebuild specific subtree |
| `context.watch<T>()` | ✅ Yes | Read + rebuild on change |
| `context.read<T>()` | ❌ No | Read once (in callbacks) |
| `Selector<T, S>` | ✅ On select | Rebuild only when selected value changes |

### State Management Comparison
| Feature | Provider | Riverpod | BLoC | GetX |
|---------|----------|----------|------|------|
| Learning curve | Low | Medium | High | Low |
| Boilerplate | Low | Low | High | Very Low |
| Testability | Good | Excellent | Excellent | Medium |
| Compile-safe | ❌ | ✅ | ✅ | ❌ |
| Architecture | Opinionated | Flexible | Strict | Flexible |

> **Recommendation:** Start with Provider for small apps. Use Riverpod for new projects (compile-safe, testable). Use BLoC for large teams (strict architecture).

### ChangeNotifier vs ValueNotifier
| ChangeNotifier | ValueNotifier |
|----------------|---------------|
| Multiple fields | Single value |
| Manual `notifyListeners()` | Auto-notify on `value =` |
| Used with Provider | Used with `ValueListenableBuilder` |
| More flexible | Simpler, more efficient |

### When to use `setState()` vs a state management solution
- Use `setState()` when: state is used by only one widget, simple form/toggle/animation, small app/prototype.
- Use Provider/Riverpod/BLoC when: state shared across multiple screens, auth/session, shopping cart, theme/locale, data from API cached across screens, large app with team.

### ValueListenableBuilder vs Consumer
| Feature | `ValueListenableBuilder` | `Consumer` |
|---------|--------------------------|-----------|
| Rebuild scope | Only builder subtree | Entire builder |
| State type | `ValueNotifier<T>` | `ChangeNotifier` |
| Granularity | ✅ Very granular | ⚠️ Broader |
| Best for | Single value | Model with multiple fields |

### ListenableBuilder
`ListenableBuilder` rebuilds when any `Listenable` changes — more flexible than `ValueListenableBuilder`. Use `Listenable.merge` to rebuild on changes from multiple sources.

### Async State (FutureBuilder / StreamBuilder)
| Widget | Data Source | Use Case |
|--------|-------------|----------|
| `FutureBuilder` | `Future<T>` | One-time async (API call) |
| `StreamBuilder` | `Stream<T>` | Real-time data (WebSocket, Firestore) |

### Provider vs Riverpod
| Feature | Provider | Riverpod |
|---------|----------|----------|
| Context dependency | Yes (needs BuildContext) | No (uses ref) |
| Compile safety | Runtime errors (ProviderNotFound) | Compile-time safe |
| Testing | Harder (needs context) | Easy (ProviderContainer) |
| Auto-dispose | Manual | `autoDispose` modifier |
| Async support | Limited | First-class (FutureProvider, StreamProvider) |

### BLoC Pattern
BLoC (Business Logic Component) separates UI from business logic using streams: Events → Bloc → States.

| BLoC Concept | Description |
|--------------|-------------|
| Event | User intent (button tap, load data) |
| State | UI representation at a point in time |
| Bloc | Transforms Events → States |
| BlocBuilder | Rebuilds UI on state change |
| BlocProvider | Makes bloc available to widget tree |
| BlocListener | Side effects on state change (navigation, snackbar) |

### Selector
`Selector` (Provider) or `.select()` (Riverpod) rebuilds only when a specific part of state changes — more granular than `context.watch` which rebuilds on ANY change.

### Riverpod Provider Types
| Provider Type | Data Source | Use Case |
|---------------|-------------|----------|
| `Provider` | Sync value | Singletons, services |
| `FutureProvider` | `Future<T>` | API calls, one-time fetch |
| `StreamProvider` | `Stream<T>` | WebSocket, Firestore, real-time |
| `ChangeNotifierProvider` | `ChangeNotifier` | Mutable state with listeners |
| `StateNotifierProvider` | `StateNotifier` | Immutable state (Riverpod) |

---

## 🧪 Code Example

```dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CounterModel(),
      child: const MyApp(),
    ),
  );
}

// Model — extends ChangeNotifier
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;

  void increment() {
    _count++;
    notifyListeners();  // Notify all listeners to rebuild
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('State Management Demo')),
        body: const CounterScreen(),
      ),
    );
  }
}

// Consumer — rebuilds when model changes
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Consumer — rebuilds only this subtree
        Consumer<CounterModel>(
          builder: (context, model, child) {
            return Text('Count: ${model.count}',
                style: const TextStyle(fontSize: 32));
          },
        ),

        // context.read — no rebuild, just access
        ElevatedButton(
          onPressed: () => context.read<CounterModel>().increment(),
          child: const Text('Increment'),
        ),
      ],
    );
  }
}

// Alternative: setState (local state only)
class LocalCounter extends StatefulWidget {
  const LocalCounter({super.key});
  @override
  State<LocalCounter> createState() => _LocalCounterState();
}

class _LocalCounterState extends State<LocalCounter> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Local: $_count'),
        ElevatedButton(
          onPressed: () => setState(() => _count++),
          child: const Text('Add'),
        ),
      ],
    );
  }
}
```

### Output
```
A running Flutter app with:
- Text "Count: 0" (updates on button press via Provider)
- ElevatedButton labeled "Increment"
- Tapping increments the count and rebuilds only the Consumer subtree
```

---

## ❓ Interview Questions

1. **What is state and what are the types of state?**
   - State is data that can change over time and affects the UI. Two types: **Ephemeral (local) state** — only one widget needs it (form validation, tab index, animation). Managed with `setState()`. **App (shared) state** — multiple widgets/screens need it (user auth, cart, theme). Managed with Provider, Riverpod, or BLoC. Rule: start with `setState()`, add a state management solution only when state needs to be shared across widgets.

2. **How does `setState()` work?**
   - `setState()` marks the `State` object as dirty, scheduling a rebuild on the next frame. The `build()` method is called again with the updated state. Rules: (1) Don't call in `build()` — causes infinite loop. (2) Don't call after async gap without checking `mounted` — the widget may have been removed from the tree. (3) Don't put async work inside `setState` — it's synchronous. Correct pattern: `final data = await fetchData(); if (!mounted) return; setState(() => _data = data);`.

3. **How do you lift state up?**
   - When two sibling widgets need to share state, move the state to their common parent. The parent holds the state and passes it down: data via constructor, modifications via callbacks. Example: `CartScreen` holds `_itemCount`, passes it to `CartBadge(count: _itemCount)` and passes callbacks `CartControls(onAdd: _addItem, onRemove: _removeItem)`. This is the simplest form of state sharing — no external library needed. For deeper trees or more complex sharing, use Provider or Riverpod.

4. **What is `InheritedWidget` and how does it relate to state management?**
   - `InheritedWidget` is Flutter's built-in mechanism for sharing data down the widget tree without prop drilling. When an `InheritedWidget` changes, all descendant widgets that called `dependOnInheritedWidgetOfExactType` rebuild. `Theme` and `MediaQuery` are `InheritedWidget`s built into Flutter. You create one by extending `InheritedWidget`, wrapping your data, and calling `updateShouldNotify()` to determine if descendants should rebuild. To access: `context.dependOnInheritedWidgetOfExactType<MyInheritedWidget>()`. `InheritedWidget` is low-level and verbose — Provider, Riverpod, and BLoC are all built on top of it with better APIs. You rarely use `InheritedWidget` directly in modern Flutter — use Provider instead. See the [State Management Deep Dive](../state_management/Fundamentals.md) for more details.

5. **What is the difference between Provider, Riverpod, BLoC, and GetX?**
   - **Provider** — low learning curve, wraps InheritedWidget, depends on BuildContext, runtime errors (ProviderNotFound). Good for small apps. **Riverpod** — compile-safe, no BuildContext dependency, excellent testability, first-class async support (FutureProvider, StreamProvider). Best for new projects. **BLoC** — strict architecture using streams (Events → States), high boilerplate, excellent testability. Best for large teams. **GetX** — minimal boilerplate, low learning curve, but not compile-safe and less testable. Good for rapid prototyping.

6. **What is `ChangeNotifier` and `ValueNotifier`?**
   - `ChangeNotifier` is an observable class that notifies listeners on change — call `notifyListeners()` after modifying state. Used with Provider for models with multiple fields. `ValueNotifier<T>` is a `ChangeNotifier` for a single value — it auto-notifies when `value` is set. Used with `ValueListenableBuilder` for fine-grained rebuilds. `ChangeNotifier` requires manual `notifyListeners()`; `ValueNotifier` auto-notifies. Always `dispose()` both to prevent memory leaks.

7. **When should you use `setState()` vs a state management solution?**
   - Use `setState()` when: state is used by only one widget, simple form/toggle/animation, small app/prototype, state doesn't need to survive configuration changes. Use Provider/Riverpod/BLoC when: state is shared across multiple screens, auth/session state, shopping cart, theme/locale, data from API cached across screens, large app with team. Rule: start simple with `setState()`, add complexity only when needed.

8. **What is `ValueListenableBuilder` and how does it differ from `Consumer`?**
   - `ValueListenableBuilder` rebuilds only its builder subtree when a `ValueNotifier` changes — most granular rebuild. The widget can be a `StatelessWidget` — state lives in `ValueNotifier`. `Consumer` (Provider) rebuilds the entire builder function — broader scope. Use `ValueListenableBuilder` for single-value state (counter, toggle, loading flag). Use `Consumer` for model-based state with multiple fields. `ValueListenableBuilder` also accepts a `child` parameter for static widgets that don't rebuild.

9. **What is `ListenableBuilder` and when do you use it?**
   - `ListenableBuilder` rebuilds when any `Listenable` changes — it's the general form of `ValueListenableBuilder` (which is specialized for `ValueNotifier`). Use it with `AnimationController`, `ChangeNotifier`, or any `Listenable`. For multiple sources, use `Listenable.merge([controller1, controller2])` to rebuild when any of them changes. Use `ListenableBuilder` when you need to listen to a `Listenable` that isn't a `ValueNotifier`.

10. **How do you handle async state without a state management library?**
    - Use `FutureBuilder<T>` for one-time async operations (API calls) — it takes a `Future<T>` and provides `AsyncSnapshot` with `connectionState`, `hasError`, and `data`. Use `StreamBuilder<T>` for real-time data (WebSocket, Firestore) — it takes a `Stream<T>` and provides snapshots as values arrive. Handle states: `ConnectionState.waiting` (show spinner), `ConnectionState.done` (check `hasError` or show data). For anything more complex (retry, refresh, caching), use Riverpod's `FutureProvider` or BLoC.

11. **What is the difference between `BuildContext` and `ref` in state management?**
    - `BuildContext` is Flutter's built-in way to access the widget tree — Provider uses `context.watch<T>()` and `context.read<T>()` which require a `BuildContext`. This means you can only access providers inside `build()` or callbacks that have `context`. Riverpod replaces `BuildContext` with `ref` (a `WidgetRef` in `ConsumerWidget` or `Ref` in providers) — `ref.watch()` and `ref.read()` work anywhere in the widget, not just `build()`. `ref` also enables compile-time safety (no `ProviderNotFoundException`). The key advantage of `ref`: providers can depend on each other without `BuildContext`, making the architecture more modular and testable.

12. **What is `Consumer` and how does it limit rebuilds compared to `context.watch`?**
    - `Consumer<T>` wraps a subtree and only rebuilds that subtree when the provider changes. `context.watch<T>()` rebuilds the entire `build()` method of the widget that calls it. Example: if a screen has a header, a list, and a bottom bar, and only the bottom bar needs the cart state, wrapping just the bottom bar in `Consumer<CartModel>` means the header and list don't rebuild. `Consumer` also accepts a `child` parameter for static widgets that never need to rebuild. `context.watch` is simpler but less efficient — it rebuilds everything in the `build()` method.

13. **What is the difference between ephemeral state and app state?**
    - **Ephemeral (local) state** is state that only one widget needs — e.g., text input focus, current tab, animation progress, expand/collapse toggle. Manage with `setState()` inside a `StatefulWidget`. It doesn't need to survive screen transitions or be shared. **App (shared) state** is state that multiple widgets/screens need — e.g., user session, shopping cart, theme, locale, cached API data. Manage with Provider, Riverpod, or BLoC. It needs to survive navigation and be accessible from anywhere. Rule: start with `setState()` for ephemeral state. Only add a state management solution when state needs to be shared. Don't over-engineer — a simple form doesn't need BLoC.

14. **What is `MultiProvider` and why do you need it?**
    - `MultiProvider` provides multiple providers at once without nesting: `MultiProvider(providers: [ChangeNotifierProvider(create: (_) => CartModel()), ChangeNotifierProvider(create: (_) => UserModel())], child: MyApp())`. Without `MultiProvider`, you'd need nested providers: `ChangeNotifierProvider(create: (_) => CartModel(), child: ChangeNotifierProvider(create: (_) => UserModel(), child: MyApp()))` — deeply nested and hard to read. `MultiProvider` flattens this. It also supports `MultiProvider(providers: [...])` with `ProxyProvider` for providers that depend on other providers. Always provide at the top of the tree (above `MaterialApp`) for app-wide state, or at the feature root for feature-scoped state.

15. **How do you dispose state when a widget is removed from the tree?**
    - In `StatefulWidget`: override `dispose()` and clean up controllers, subscriptions, and listeners: `@override void dispose() { _controller.dispose(); _subscription.cancel(); super.dispose(); }`. With Provider: `ChangeNotifierProvider` automatically calls `dispose()` on the model when the provider is removed from the tree — but only if `create` was used (not `value`). With Riverpod: use `autoDispose` modifier — `final provider = Provider.autoDispose((ref) => ...)` — the provider is automatically disposed when no widget is listening. With BLoC: `BlocProvider` automatically closes the bloc when removed. For `StreamSubscription`, always cancel in `dispose()`. For `AnimationController`, always call `dispose()`. Failing to dispose causes memory leaks — the object stays in memory after the widget is gone.

---

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [Navigation](Navigation.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
