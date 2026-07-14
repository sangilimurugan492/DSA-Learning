# Redux

## Q1: What is Redux?

Redux is a predictable state container using a single store, actions, and reducers.

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  Action   │ ──→ │ Reducer  │ ──→ │  Store   │ ──→ │    UI     │
│ (intent)  │     │ (pure fn) │     │ (state)  │     │ (rebuild) │
└──────────┘     └──────────┘     └──────────┘     └──────────┘
                        ↑                                  │
                        └──────────────────────────────────┘
                              (UI dispatches actions)
```

### Redux Three Principles
1. **Single source of truth** — one store for the entire app
2. **State is read-only** — only actions can change it
3. **Changes are pure functions** — reducers are pure (no side effects)

```dart
// pubspec.yaml: flutter_redux: ^0.10.0
```

---

## Q2: How do you define actions, reducers, and state?

```dart
// 1. State — immutable app state
class AppState {
  final int counter;
  final User? user;
  final List<Todo> todos;
  final bool isLoading;

  const AppState({
    this.counter = 0,
    this.user,
    this.todos = const [],
    this.isLoading = false,
  });

  AppState copyWith({
    int? counter,
    User? user,
    List<Todo>? todos,
    bool? isLoading,
  }) {
    return AppState(
      counter: counter ?? this.counter,
      user: user ?? this.user,
      todos: todos ?? this.todos,
      isLoading: isLoading ?? this.isLoading,
    );
  }
}

// 2. Actions — describe what happened
class IncrementAction {}
class DecrementAction {}
class ResetAction {}

class AddTodoAction {
  final Todo todo;
  AddTodoAction(this.todo);
}

class LoginAction {
  final User user;
  LoginAction(this.user);
}

class LogoutAction {}

class SetLoadingAction {
  final bool isLoading;
  SetLoadingAction(this.isLoading);
}

// 3. Reducer — pure function: (state, action) → new state
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
  if (action is AddTodoAction) {
    return state.copyWith(todos: [...state.todos, action.todo]);
  }
  if (action is LoginAction) {
    return state.copyWith(user: action.user);
  }
  if (action is LogoutAction) {
    return state.copyWith(user: null);
  }
  if (action is SetLoadingAction) {
    return state.copyWith(isLoading: action.isLoading);
  }
  return state;  // Unknown action — return unchanged
}

// Typed reducers (cleaner)
final counterReducer = TypedReducer<int, dynamic>((state, action) {
  if (action is IncrementAction) return state + 1;
  if (action is DecrementAction) return state - 1;
  if (action is ResetAction) return 0;
  return state;
});
```

---

## Q3: How do you set up the store and use it in Flutter?

```dart
// 4. Create store
final store = Store<AppState>(
  appReducer,
  initialState: const AppState(),
);

// 5. Provide store at app root
void main() {
  runApp(
    StoreProvider(
      store: store,
      child: const MyApp(),
    ),
  );
}

// 6. Consume in widgets
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return StoreConnector<AppState, int>(
      converter: (store) => store.state.counter,  // Select state
      builder: (context, count) {
        return Text('$count');  // Rebuilds when counter changes
      },
    );
  }
}

// Dispatch actions
ElevatedButton(
  onPressed: () => StoreProvider.of<AppState>(context).dispatch(IncrementAction()),
  child: const Text('+'),
)

// Or with StoreConnector
StoreConnector<AppState, VoidCallback>(
  converter: (store) => () => store.dispatch(IncrementAction()),
  builder: (context, increment) {
    return ElevatedButton(
      onPressed: increment,
      child: const Text('+'),
    );
  },
)
```

---

## Q4: What is StoreConnector vs StoreBuilder?

```dart
// StoreConnector — select state, rebuild on change
StoreConnector<AppState, int>(
  converter: (store) => store.state.counter,
  distinct: true,  // Only rebuild if selected value changes (== check)
  builder: (context, count) => Text('$count'),
)

// StoreBuilder — access store without converting
StoreBuilder<AppState>(
  builder: (context, store) {
    return Text('${store.state.counter}');
  },
)
// Rebuilds on every state change (less efficient)

// StoreConnector with multiple values
StoreConnector<AppState, ({int count, User? user})>(
  converter: (store) => (
    count: store.state.counter,
    user: store.state.user,
  ),
  builder: (context, data) {
    return Column(
      children: [
        Text('Count: ${data.count}'),
        Text('User: ${data.user?.name ?? 'Guest'}'),
      ],
    );
  },
)
```

| Widget | Rebuilds | Use Case |
|--------|----------|----------|
| `StoreConnector` | On selected change | Most cases |
| `StoreBuilder` | On any state change | When you need full store |

---

## Q5: How do you use middleware for side effects?

```dart
// Middleware — intercepts actions for side effects (API calls, logging)
// (store, action, next) → next(action)

// Logging middleware
void loggingMiddleware(Store<AppState> store, dynamic action, NextDispatcher next) {
  print('Action: $action');
  print('State before: ${store.state}');

  next(action);  // Pass to reducer

  print('State after: ${store.state}');
}

// API middleware (thunk)
void apiMiddleware(Store<AppState> store, dynamic action, NextDispatcher next) {
  if (action is FetchUserAction) {
    store.dispatch(SetLoadingAction(true));
    api.fetchUser(action.userId).then((user) {
      store.dispatch(LoginAction(user));
      store.dispatch(SetLoadingAction(false));
    }).catchError((e) {
      store.dispatch(SetErrorAction(e.toString()));
      store.dispatch(SetLoadingAction(false));
    });
  }
  next(action);
}

// Thunk middleware — for async actions
// pubspec.yaml: redux_thunk: ^0.4.0
final store = Store<AppState>(
  appReducer,
  initialState: const AppState(),
  middleware: [
    thunkMiddleware,
    loggingMiddleware,
    apiMiddleware,
  ],
);

// Thunk action — async function with store access
final fetchUser = (int userId) {
  return (Store<AppState> store) async {
    store.dispatch(SetLoadingAction(true));
    try {
      final user = await api.fetchUser(userId);
      store.dispatch(LoginAction(user));
    } catch (e) {
      store.dispatch(SetErrorAction(e.toString()));
    } finally {
      store.dispatch(SetLoadingAction(false));
    }
  };
};

// Dispatch thunk
store.dispatch(fetchUser(42));
```

---

## Q6: How do you combine reducers?

```dart
// Individual reducers
final counterReducer = TypedReducer<int, dynamic>((state, action) {
  if (action is IncrementAction) return state + 1;
  if (action is DecrementAction) return state - 1;
  return state;
});

final todosReducer = TypedReducer<List<Todo>, dynamic>((state, action) {
  if (action is AddTodoAction) return [...state, action.todo];
  if (action is RemoveTodoAction) {
    return state.where((t) => t.id != action.id).toList();
  }
  return state;
});

final userReducer = TypedReducer<User?, dynamic>((state, action) {
  if (action is LoginAction) return action.user;
  if (action is LogoutAction) return null;
  return state;
});

// Combine into app reducer
AppState appReducer(AppState state, dynamic action) {
  return AppState(
    counter: counterReducer(state.counter, action),
    todos: todosReducer(state.todos, action),
    user: userReducer(state.user, action),
    isLoading: loadingReducer(state.isLoading, action),
  );
}
```

---

## Q7: How do you test Redux?

```dart
void main() {
  group('appReducer', () {
    test('IncrementAction increases counter', () {
      const initial = AppState(counter: 0);
      final state = appReducer(initial, IncrementAction());
      expect(state.counter, 1);
    });

    test('AddTodoAction adds todo', () {
      const initial = AppState(todos: []);
      final todo = Todo(id: '1', title: 'Test');
      final state = appReducer(initial, AddTodoAction(todo));
      expect(state.todos.length, 1);
      expect(state.todos.first.title, 'Test');
    });

    test('LogoutAction clears user', () {
      const initial = AppState(user: User('Alice'));
      final state = appReducer(initial, LogoutAction());
      expect(state.user, isNull);
    });
  });

  group('Store', () {
    test('dispatch updates state', () {
      final store = Store<AppState>(appReducer, initialState: const AppState());
      expect(store.state.counter, 0);

      store.dispatch(IncrementAction());
      expect(store.state.counter, 1);

      store.dispatch(IncrementAction());
      expect(store.state.counter, 2);
    });

    test('state changes trigger listeners', () {
      final store = Store<AppState>(appReducer, initialState: const AppState());
      var stateChanged = false;

      store.onChange.listen((state) {
        stateChanged = true;
      });

      store.dispatch(IncrementAction());
      expect(stateChanged, true);
    });
  });

  group('Middleware', () {
    test('logging middleware calls next', () {
      final store = Store<AppState>(appReducer, initialState: const AppState());
      var nextCalled = false;

      loggingMiddleware(
        store,
        IncrementAction(),
        (action) => nextCalled = true,
      );

      expect(nextCalled, true);
    });
  });
}
```

---

## Q8: What are the pros and cons of Redux?

### Pros
- ✅ Predictable — pure reducers, single store
- ✅ Time-travel debugging — replay actions
- ✅ Excellent testability — reducers are pure functions
- ✅ Mature ecosystem (from React/JS)
- ✅ Great for large teams — strict architecture

### Cons
- ❌ Very high boilerplate (actions, reducers, state, middleware)
- ❌ Not idiomatic Flutter (designed for React)
- ❌ Global store (hard to scope)
- ❌ Async is complex (needs thunk/saga middleware)
- ❌ Less popular in Flutter community

| Feature | Redux | Riverpod | BLoC |
|---------|-------|----------|------|
| Store | Single global | Multiple scoped | Multiple scoped |
| Boilerplate | Very High | Low | High |
| Testing | Excellent | Excellent | Excellent |
| Time travel | ✅ | ❌ | ❌ |
| Flutter native | ❌ | ✅ | ✅ |
| Learning curve | High | Medium | High |

> **Recommendation:** Redux is overkill for most Flutter apps. Use it only if your team has Redux experience from React, or if you need time-travel debugging. For new Flutter projects, prefer Riverpod or BLoC.

---

## 🔗 Related Topics
- [BLoC](BLoC.md)
- [Comparison](Comparison.md)
- [Best Practices](BestPractices.md)
