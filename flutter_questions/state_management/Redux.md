# Redux

## 📖 Explanation

Redux is a predictable state container pattern from the web world, adapted to Flutter via `flutter_redux`. It uses a single store, actions, reducers, and middleware. It's highly structured but has significant boilerplate.

### Redux Flow
```
Action → Reducer → New State → UI Rebuild
  ↑                               ↓
  └─────── User Action ───────────┘
```

### Redux Core Concepts
| Concept | Purpose |
|---------|---------|
| Store | Single source of truth — holds all app state |
| Action | Describes what happened (type + payload) |
| Reducer | Pure function: (state, action) → new state |
| Middleware | Intercept actions for async, logging, side effects |
| StoreConnector | Widget that rebuilds when state changes |

### Redux vs Other Solutions
| Feature | Redux | Provider | BLoC |
|---------|-------|----------|------|
| State location | Single store | Multiple providers | Multiple BLoCs |
| Boilerplate | High | Low | High |
| Predictability | Highest | Medium | High |
| Time travel | ✅ | ❌ | ❌ |
| Learning curve | High | Low | High |
| Popularity in Flutter | Low | High | High |

### Redux Best Practices
- State is immutable — reducers return new state, never modify existing
- Reducers are pure functions — no side effects
- Actions describe what happened, not what to do
- Use middleware for async (redux_thunk, redux_epics)
- Normalize nested data — flat structure with IDs
- One store for the entire app

### When to Use Redux
- You need time-travel debugging
- You need predictable, testable state transitions
- Team is familiar with Redux from web
- App has complex state interactions
- You want a single source of truth

---

## 🧪 Code Example

```dart
import 'package:flutter_redux/flutter_redux.dart';
import 'package:redux/redux.dart';

// ── State ──
class AppState {
  final int counter;
  final bool isLoading;
  final List<String> items;
  final User? user;

  const AppState({
    this.counter = 0,
    this.isLoading = false,
    this.items = const [],
    this.user,
  });

  AppState copyWith({
    int? counter,
    bool? isLoading,
    List<String>? items,
    User? user,
  }) {
    return AppState(
      counter: counter ?? this.counter,
      isLoading: isLoading ?? this.isLoading,
      items: items ?? this.items,
      user: user ?? this.user,
    );
  }
}

// ── Actions ──
class IncrementAction {}
class DecrementAction {}
class ResetAction {}

class FetchItemsAction {}
class FetchItemsSuccessAction {
  final List<String> items;
  FetchItemsSuccessAction(this.items);
}

class LoginAction {
  final String email;
  final String password;
  LoginAction(this.email, this.password);
}
class LoginSuccessAction {
  final User user;
  LoginSuccessAction(this.user);
}

// ── Reducer ──
AppState appReducer(AppState state, dynamic action) {
  if (action is IncrementAction) {
    return state.copyWith(counter: state.counter + 1);
  }
  if (action is DecrementAction) {
    return state.copyWith(counter: state.counter - 1);
  }
  if (action is ResetAction) {
    return state.copyWith(counter: 0);
  }
  if (action is FetchItemsAction) {
    return state.copyWith(isLoading: true);
  }
  if (action is FetchItemsSuccessAction) {
    return state.copyWith(isLoading: false, items: action.items);
  }
  if (action is LoginSuccessAction) {
    return state.copyWith(user: action.user);
  }
  return state;  // No change
}

// ── Middleware (async) ──
void middleware(
  Store<AppState> store,
  dynamic action,
  NextDispatcher next,
) {
  if (action is FetchItemsAction) {
    _fetchItems(store);
  }
  if (action is LoginAction) {
    _login(store, action.email, action.password);
  }
  next(action);  // Pass to reducer
}

Future<void> _fetchItems(Store<AppState> store) async {
  final items = await api.fetchItems();
  store.dispatch(FetchItemsSuccessAction(items));
}

Future<void> _login(Store<AppState> store, String email, String pass) async {
  final user = await api.login(email, pass);
  store.dispatch(LoginSuccessAction(user));
}

// ── Store setup ──
void main() {
  final store = Store<AppState>(
    appReducer,
    initialState: const AppState(),
    middleware: [middleware],
  );

  runApp(MyApp(store: store));
}

class MyApp extends StatelessWidget {
  final Store<AppState> store;
  const MyApp({super.key, required this.store});

  @override
  Widget build(BuildContext context) {
    return StoreProvider(
      store: store,
      child: const MaterialApp(home: CounterScreen()),
    );
  }
}

// ── StoreConnector (like Consumer) ──
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return StoreConnector<AppState, int>(
      converter: (store) => store.state.counter,
      builder: (context, count) {
        return Scaffold(
          body: Center(child: Text('Count: $count')),
          floatingActionButton: Column(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              FloatingActionButton(
                onPressed: () =>
                  StoreProvider.of<AppState>(context).dispatch(IncrementAction()),
                child: const Icon(Icons.add),
              ),
            ],
          ),
        );
      },
    );
  }
}

// ── StoreConnector with ViewModel ──
class CartViewModel {
  final List<String> items;
  final bool isLoading;
  final void Function() onRefresh;

  CartViewModel({
    required this.items,
    required this.isLoading,
    required this.onRefresh,
  });

  static CartViewModel fromStore(Store<AppState> store) {
    return CartViewModel(
      items: store.state.items,
      isLoading: store.state.isLoading,
      onRefresh: () => store.dispatch(FetchItemsAction()),
    );
  }
}

class CartScreen extends StatelessWidget {
  const CartScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return StoreConnector<AppState, CartViewModel>(
      converter: CartViewModel.fromStore,
      builder: (context, vm) {
        if (vm.isLoading) return const CircularProgressIndicator();
        return ListView.builder(
          itemCount: vm.items.length,
          itemBuilder: (_, i) => ListTile(title: Text(vm.items[i])),
        );
      },
    );
  }
}
```

### Output
```
A Flutter app with Redux state management:
- AppState with immutable copyWith
- Actions for all state changes
- appReducer pure function (state, action) → new state
- Middleware for async operations (fetch, login)
- StoreProvider + StoreConnector for reactive UI
- ViewModel pattern for clean StoreConnector
```

---

## ❓ Interview Questions

1. **What is Redux and how does it work?**
   - Redux is a predictable state container with a single store. Flow: Action (describes what happened) → Reducer (pure function: old state + action → new state) → Store (holds new state) → UI (rebuilds via StoreConnector). State is immutable — reducers return new state objects, never modify existing. Actions are plain objects with a type and payload. Middleware intercepts actions for async operations (API calls) and side effects (logging). The single store is the single source of truth — all app state in one place. Redux is highly predictable and testable but has significant boilerplate (actions, reducers, middleware, store setup).

2. **What is a reducer in Redux?**
   - A reducer is a pure function: `(AppState state, action) → AppState`. It takes the current state and an action, returns a new state. Reducers must be pure — no side effects, no async, no modifying the existing state (use `copyWith`). Example: `if (action is IncrementAction) return state.copyWith(counter: state.counter + 1)`. Combine multiple reducers with `combineReducers()`. Reducers are easily testable — given the same state and action, they always return the same result. Never call `store.dispatch()` inside a reducer — that creates infinite loops. Side effects go in middleware, not reducers.

3. **What is middleware in Redux?**
   - Middleware sits between action dispatch and the reducer. It can intercept actions, perform side effects (API calls, logging, analytics), and dispatch new actions. Signature: `void middleware(Store<T> store, dynamic action, NextDispatcher next) { /* logic */ next(action); }`. Call `next(action)` to pass the action to the next middleware or reducer. For async: perform the async operation, then `store.dispatch(SuccessAction(result))`. Use `redux_thunk` for thunks (actions that are functions), `redux_epics` for stream-based async. Middleware is the only place for side effects — reducers must be pure.

4. **What is StoreConnector and how does it work?**
   - `StoreConnector<AppState, T>` is a widget that connects the Redux store to the widget tree. `converter: (store) => store.state.counter` extracts the relevant data from the store. `builder: (context, count) => Text('$count')` rebuilds when the extracted data changes. StoreConnector uses `InheritedWidget` internally — only widgets that depend on changed state rebuild. Use a ViewModel pattern: `converter: MyViewModel.fromStore` — the ViewModel converts store state into a presentation-friendly object with both data and callbacks. StoreConnector is the Redux equivalent of Provider's `Consumer` or BLoC's `BlocBuilder`.

5. **Why is Redux state immutable?**
   - Immutability ensures: (1) Predictability — state can only change through reducers, never modified in place. (2) Time-travel debugging — you can replay any state by re-applying actions. (3) Change detection — compare old and new state references (`oldState != newState`) to detect changes efficiently. (4) No side effects — pure reducers can't modify shared state. (5) Thread safety — immutable objects are inherently thread-safe. Use `copyWith()` to create new state: `state.copyWith(counter: state.counter + 1)`. Never do `state.counter++` — this mutates the existing state and breaks Redux's guarantees. The `equatable` package helps with value equality for state comparison.

6. **How does Redux compare to BLoC?**
   - **Redux**: Single store, actions → reducer → state. Global state. Time-travel debugging. High boilerplate (actions, reducers, middleware). Popular on web, less common in Flutter. **BLoC**: Multiple BLoCs, events → BLoC → states. Scoped state. No time-travel. High boilerplate (events, states, BLoC). Popular in Flutter. Both are predictable and testable. Redux has a single store (simpler mental model but potential bottleneck). BLoC has multiple BLoCs (more modular but more coordination). Redux is better for apps that need time-travel or have complex state interactions. BLoC is better for Flutter apps — more idiomatic, better tooling, more community support. Most Flutter teams choose BLoC over Redux.

7. **What is the ViewModel pattern in Redux?**
   - A ViewModel converts store state into a presentation-friendly object for the widget. `class CartViewModel { final List<String> items; final bool isLoading; final void Function() onRefresh; static CartViewModel fromStore(Store<AppState> store) { return CartViewModel(items: store.state.items, isLoading: store.state.isLoading, onRefresh: () => store.dispatch(FetchItemsAction())); } }`. Use with `StoreConnector(converter: CartViewModel.fromStore, builder: (context, vm) => ...)`. Benefits: (1) Widget doesn't know about the store or actions — clean separation. (2) Pre-computes derived values. (3) Bundles data + callbacks in one object. (4) Easy to test — create ViewModel directly. (5) Reduces StoreConnector boilerplate.

8. **How do you test Redux?**
   - Test reducers: `test('increment', () { final state = AppState(counter: 0); final newState = appReducer(state, IncrementAction()); expect(newState.counter, 1); })`. Test middleware: create a mock store, dispatch actions, verify `next` was called and new actions were dispatched. Test async middleware: use `when(() => api.fetch()).thenAnswer((_) async => items)`, dispatch action, verify success action dispatched. Test StoreConnector: provide a test store with `StoreProvider(store: testStore, child: widget)`. Reducers are the easiest to test — they're pure functions. Middleware is harder — mock the store and verify dispatched actions. Redux's predictability makes it the most testable state management solution.

9. **What are the pros and cons of Redux?**
   - **Pros**: (1) Predictable — all state changes go through reducers. (2) Testable — pure reducers are trivially testable. (3) Time-travel debugging — replay state from action history. (4) Single source of truth — all state in one place. (5) Great DevTools — action log, state diff, time-travel. (6) Well-known pattern from web. **Cons**: (1) High boilerplate — actions, reducers, middleware, store. (2) Single store can become a bottleneck. (3) Async requires middleware (thunks/epics). (4) Less popular in Flutter — smaller community. (5) Steep learning curve. (6) Overkill for small apps. Use Redux for apps that need maximum predictability and debugging, or teams with Redux experience. For most Flutter apps, BLoC or Riverpod are more idiomatic.

10. **When should you use Redux in Flutter?**
    - Use Redux when: (1) You need time-travel debugging. (2) You need maximum predictability and testability. (3) Team has Redux experience from web (React/Redux). (4) App has complex state interactions that benefit from a single store. (5) You want a centralized action log for debugging. Don't use Redux when: (1) App is small — Provider/Riverpod is simpler. (2) You want idiomatic Flutter — BLoC/Riverpod are more popular. (3) You want minimal boilerplate — Redux has the most boilerplate. (4) You need scoped state — Redux's single store is global. For most Flutter apps, Provider (small), Riverpod (medium-large), or BLoC (large) are better choices than Redux. Redux is a valid choice for teams that know it well.

---

## 🔗 Related Topics
- [BLoC](BLoC.md)
- [Comparison](Comparison.md)
- [Best Practices](BestPractices.md)
