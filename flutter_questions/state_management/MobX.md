# MobX

## Q1: What is MobX?

MobX is a state management library that uses observables, actions, and reactions — applying reactive programming principles.

```dart
// pubspec.yaml: mobx: ^2.2.0, flutter_mobx: ^2.2.0
// dev_dependencies: build_runner, mobx_codegen

// MobX Core Concepts:
// Observable — state that can be observed
// Action — modifies observable state
// Reaction — responds to state changes (rebuild UI, side effects)
// Computed — derived state from observables
```

```
┌──────────────┐     ┌──────────┐     ┌──────────────┐
│  Observable   │ ←── │  Action   │     │   Reaction    │
│  (state)      │     │ (mutate)  │     │  (respond)    │
└──────────────┘     └──────────┘     └──────────────┘
       ↓                                    ↑
       └────────── Computed ────────────────┘
                  (derived)
```

---

## Q2: How do you create a MobX store?

```dart
// 1. Define store (counter_store.dart)
import 'package:mobx/mobx.dart';

part 'counter_store.g.dart';  // Generated code

class CounterStore = _CounterStore with _$CounterStore;

abstract class _CounterStore with Store {
  @observable
  int count = 0;

  @action
  void increment() => count++;

  @action
  void decrement() => count--;

  @action
  void reset() => count = 0;

  @computed
  bool get isPositive => count > 0;

  @computed
  String get displayValue => 'Count: $count';
}

// 2. Generate code: dart run build_runner build
// 3. Use store
final counter = CounterStore();
```

---

## Q3: How do you consume MobX in widgets?

```dart
import 'package:flutter_mobx/flutter_mobx.dart';

// Observer — rebuilds when observed values change
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final counter = CounterStore();  // Or inject via Provider/get_it

    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Observer auto-detects which observables are used
            Observer(
              builder: (_) => Text(
                '${counter.count}',
                style: const TextStyle(fontSize: 48),
              ),
            ),
            // Computed values also trigger rebuilds
            Observer(
              builder: (_) => Text(counter.displayValue),
            ),
            Observer(
              builder: (_) => Text(
                counter.isPositive ? 'Positive' : 'Zero or negative',
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: counter.increment,  // Action — no Observer needed
        child: const Icon(Icons.add),
      ),
    );
  }
}

// Observer only rebuilds the builder — not the whole widget
// Multiple Observers = independent rebuilds
```

---

## Q4: How do you use computed values and reactions?

```dart
// Computed — derived state, cached, only recalculates when dependencies change
abstract class _CartStore with Store {
  @observable
  ObservableList<CartItem> items = ObservableList<CartItem>();

  @computed
  int get itemCount => items.length;

  @computed
  double get subtotal =>
      items.fold(0, (sum, item) => sum + item.price * item.quantity);

  @computed
  double get tax => subtotal * 0.08;

  @computed
  double get total => subtotal + tax;

  @computed
  bool get isEmpty => items.isEmpty;

  @action
  void addItem(CartItem item) => items.add(item);

  @action
  void removeItem(String id) =>
      items.removeWhere((item) => item.id == id);

  @action
  void clear() => items.clear();
}

// Reactions — side effects on state change
class _CartStore with Store {
  @observable
  ObservableList<CartItem> items = ObservableList<CartItem>();

  // reaction — runs when specific value changes
  late final ReactionDisposer _disposer;

  _CartStore() {
    _disposer = reaction(
      (_) => items.length,  // Watch itemCount
      (int count) {
        if (count > 10) {
          print('Cart limit reached!');
        }
      },
    );
  }

  // autorun — runs immediately + on any observed change
  void setupAutoRun() {
    autorun((_) {
      print('Cart has ${items.length} items, total: ${total}');
    });
  }

  // when — runs once when condition is true
  void setupWhen() {
    when(
      () => items.length >= 5,
      () => print('5 items in cart!'),
    );
  }

  void dispose() {
    _disposer();  // Always dispose reactions
  }
}
```

### Reaction Types
| Reaction | When | Runs | Use Case |
|----------|------|------|----------|
| `reaction` | Specific value changes | Each time | Side effects on specific change |
| `autorun` | Any observed change | Immediately + on change | Logging, tracking |
| `when` | Condition becomes true | Once | One-time trigger |

---

## Q5: How do you handle async actions in MobX?

```dart
abstract class _UserStore with Store {
  final UserRepository _repository;

  _UserStore(this._repository);

  @observable
  User? user;

  @observable
  bool isLoading = false;

  @observable
  String? error;

  @computed
  bool get isLoggedIn => user != null;

  // Async action
  @action
  Future<void> login(String email, String password) async {
    isLoading = true;
    error = null;
    try {
      user = await _repository.login(email, password);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  Future<void> logout() async {
    await _repository.logout();
    user = null;
  }

  // ObservableFuture — tracks async state
  @observable
  ObservableFuture<List<Product>>? productsFuture;

  @computed
  bool get isLoadingProducts =>
      productsFuture?.status == FutureStatus.pending;

  @action
  Future<void> loadProducts() async {
    productsFuture = ObservableFuture(_repository.getProducts());
    try {
      final products = await productsFuture!;
      // products loaded
    } catch (e) {
      error = e.toString();
    }
  }

  // ObservableStream — tracks stream
  @observable
  ObservableStream<Message>? messageStream;

  @action
  void listenToMessages() {
    messageStream = ObservableStream(_repository.messages);
  }
}
```

---

## Q6: How do you use MobX with Provider for DI?

```dart
// Provide stores via Provider
void main() {
  runApp(
    MultiProvider(
      providers: [
        Provider(create: (_) => UserRepository()),
        Provider<CounterStore>(
          create: (context) => CounterStore(),
        ),
        ProxyProvider<UserRepository, UserStore>(
          update: (_, repo, __) => UserStore(repo),
        ),
        ProxyProvider<UserRepository, CartStore>(
          update: (_, repo, __) => CartStore(repo),
        ),
      ],
      child: const MyApp(),
    ),
  );
}

// Consume in widget
class UserScreen extends StatelessWidget {
  const UserScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final store = context.watch<UserStore>();

    return Observer(
      builder: (_) {
        if (store.isLoading) return const CircularProgressIndicator();
        if (store.error != null) return Text('Error: ${store.error}');
        if (store.user != null) return Text('Hello, ${store.user!.name}');
        return const Text('Not logged in');
      },
    );
  }
}

// Or with get_it
final counter = getIt<CounterStore>();
```

---

## Q7: How do you test MobX stores?

```dart
void main() {
  group('CounterStore', () {
    test('starts at 0', () {
      final store = CounterStore();
      expect(store.count, 0);
    });

    test('increment increases count', () {
      final store = CounterStore();
      store.increment();
      expect(store.count, 1);
    });

    test('isPositive is correct', () {
      final store = CounterStore();
      expect(store.isPositive, false);

      store.increment();
      expect(store.isPositive, true);
    });
  });

  group('CartStore', () {
    test('addItem increases itemCount', () {
      final store = CartStore();
      expect(store.itemCount, 0);

      store.addItem(CartItem(id: '1', price: 10, quantity: 1));
      expect(store.itemCount, 1);
    });

    test('total includes tax', () {
      final store = CartStore();
      store.addItem(CartItem(id: '1', price: 100, quantity: 2));

      expect(store.subtotal, 200);
      expect(store.tax, 16);  // 8% of 200
      expect(store.total, 216);
    });

    test('reaction fires on change', () async {
      final store = CartStore();
      var fired = false;

      store.addItem(CartItem(id: '1', price: 10, quantity: 1));

      final disposer = reaction(
        (_) => store.itemCount,
        (_) => fired = true,
      );

      store.addItem(CartItem(id: '2', price: 20, quantity: 1));
      await Future.delayed(Duration.zero);  // Reactions are async

      expect(fired, true);
      disposer();
    });
  });
}
```

---

## Q8: What are the pros and cons of MobX?

### Pros
- ✅ Reactive programming model (observables, computed)
- ✅ Fine-grained rebuilds (only what changed)
- ✅ Computed values are cached (only recalculate when deps change)
- ✅ Clean separation of concerns
- ✅ Good for complex derived state

### Cons
- ❌ Requires code generation (build_runner)
- ❌ More boilerplate than Riverpod/GetX
- ❌ Learning curve (observables, reactions, computed)
- ❌ Build step slows development
- ❌ Less popular in Flutter community

| Feature | MobX | Riverpod | BLoC |
|---------|------|----------|------|
| Code gen | ✅ Required | ❌ Optional | ❌ None |
| Reactive | ✅ Observables | ✅ Providers | ✅ Streams |
| Computed | ✅ Built-in | Manual | Manual |
| Boilerplate | Medium | Low | High |
| Learning curve | Medium | Medium | High |

> **Recommendation:** MobX is great if you come from a React/MobX background or need complex derived state. For new Flutter projects, Riverpod is more popular.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Riverpod](Riverpod.md)
- [Comparison](Comparison.md)
