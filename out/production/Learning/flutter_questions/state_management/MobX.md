# MobX

## 📖 Explanation

MobX is a state management library that uses observables, actions, and reactions. It applies reactive programming principles — the UI automatically reacts to state changes. MobX uses code generation for boilerplate reduction.

### MobX Core Concepts
| Concept | Purpose |
|---------|---------|
| `@observable` | A property that widgets can observe |
| `@action` | A method that modifies observable state |
| `@computed` | A derived value from observables |
| `Observer` | Widget that rebuilds when observed values change |
| `Reaction` | Side effect when observables change |

### MobX Flow
```
Action → modifies @observable → @computed recalculates → Observer rebuilds
```

### MobX vs Other Solutions
| Feature | MobX | Provider | BLoC |
|---------|------|----------|------|
| Reactivity | Auto (observable) | Manual (notifyListeners) | Manual (emit) |
| Boilerplate | Medium (codegen) | Low | High |
| Code generation | ✅ Required | ❌ | ❌ |
| Computed values | ✅ Built-in | Manual | Manual |
| Learning curve | Medium | Low | High |

### When to Use MobX
- You want automatic reactivity (no manual `notifyListeners()`)
- You need computed/derived values
- You prefer OOP-style state management
- You're comfortable with code generation

---

## 🧪 Code Example

```dart
import 'package:mobx/mobx.dart';
part 'counter_store.g.dart';  // Generated code

// ── Store ──
class CounterStore = _CounterStore with _$CounterStore;

abstract class _CounterStore with Store {
  @observable
  int count = 0;

  @observable
  bool isEven = true;

  @computed
  String get status => count > 10 ? 'High' : count > 0 ? 'Medium' : 'Low';

  @action
  void increment() {
    count++;
    isEven = count % 2 == 0;
  }

  @action
  void decrement() {
    count--;
    isEven = count % 2 == 0;
  }

  @action
  void reset() {
    count = 0;
    isEven = true;
  }
}

// ── Provide the store ──
void main() {
  runApp(
    Provider<CounterStore>(
      create: (_) => CounterStore(),
      child: const MyApp(),
    ),
  );
}

// ── Observer widget ──
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.read<CounterStore>();

    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Observer rebuilds when count changes
            Observer(
              builder: (_) => Text(
                'Count: ${store.count}',
                style: const TextStyle(fontSize: 48),
              ),
            ),
            Observer(
              builder: (_) => Text('Status: ${store.status}'),
            ),
            Observer(
              builder: (_) => Text(store.isEven ? 'Even' : 'Odd'),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: store.increment,  // Action modifies observable
        child: const Icon(Icons.add),
      ),
    );
  }
}

// ── Async actions ──
class UserStore = _UserStore with _$UserStore;

abstract class _UserStore with Store {
  @observable
  User? user;

  @observable
  bool isLoading = false;

  @observable
  String? error;

  @computed
  bool get isLoggedIn => user != null;

  @action
  Future<void> login(String email, String password) async {
    isLoading = true;
    error = null;
    try {
      user = await api.login(email, password);
    } catch (e) {
      error = e.toString();
    } finally {
      isLoading = false;
    }
  }

  @action
  void logout() {
    user = null;
  }
}

// ── Reactions (side effects) ──
class ReactionExample extends StatefulWidget {
  const ReactionExample({super.key});
  @override
  State<ReactionExample> createState() => _ReactionExampleState();
}

class _ReactionExampleState extends State<ReactionExample> {
  late final CounterStore store;
  late final ReactionDisposer disposer;

  @override
  void initState() {
    super.initState();
    store = CounterStore();

    // Run when count changes
    disposer = reaction(
      (_) => store.count,
      (count) {
        if (count == 10) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Reached 10!')),
          );
        }
      },
    );
  }

  @override
  void dispose() {
    disposer();  // Clean up reaction
    super.dispose();
  }
}

// Generate code: dart run build_runner build
```

### Output
```
A Flutter app with MobX state management:
- CounterStore with @observable, @action, @computed
- Observer widgets that auto-rebuild on observable changes
- UserStore with async login action and computed isLoggedIn
- Reactions for side effects (snackbar on count == 10)
- Code generation for boilerplate reduction
```

---

## ❓ Interview Questions

1. **What is MobX and how does it work?**
   - MobX is a reactive state management library using observables, actions, and computed values. Annotate state with `@observable`, mutation methods with `@action`, derived values with `@computed`. `Observer` widgets automatically rebuild when observed values change — no manual `notifyListeners()`. Flow: Action modifies `@observable` → `@computed` recalculates → `Observer` rebuilds. MobX uses code generation (`mobx_codegen` + `build_runner`) to generate the implementation. The store class is abstract with a mixin; `part 'store.g.dart'` includes generated code. MobX is great for automatic reactivity and computed values without manual notification.

2. **What is the difference between @observable and @computed?**
   - `@observable` marks a property as trackable — when it changes, all `Observer` widgets watching it rebuild. Example: `@observable int count = 0`. `@computed` marks a getter that derives its value from observables — it's recalculated only when a dependency changes, and cached otherwise. Example: `@computed String get status => count > 10 ? 'High' : 'Low'`. Computed values are reactive — if `count` changes, `status` is recalculated and Observers watching `status` rebuild. Use `@observable` for raw state, `@computed` for derived values (totals, filtered lists, formatted strings). This eliminates manual calculation and keeps state DRY.

3. **What is @action and why use it?**
   - `@action` marks a method that modifies observables. Actions batch changes — all observable modifications within an action are applied atomically, and observers are notified once (not after each change). Without `@action`, each observable modification triggers a separate notification — multiple rebuilds for one logical operation. Example: `@action void increment() { count++; isEven = count % 2 == 0; }` — both changes are batched, Observer rebuilds once. Actions also enable MobX's transaction tracking for debugging. Always use `@action` for methods that modify observables — never modify observables directly from non-action methods.

4. **What is Observer and how does it work?**
   - `Observer` is a widget that tracks which observables are accessed inside its `builder` and rebuilds when any of them change. `Observer(builder: (_) => Text('${store.count}'))` — the builder accesses `store.count`, so the Observer tracks it and rebuilds when `count` changes. Only observables accessed during the build are tracked — observables not accessed don't trigger rebuilds. This is fine-grained reactivity — each Observer tracks exactly what it needs. Wrap only the widget subtree that depends on the observable, not the entire screen. Multiple Observers can watch the same store independently.

5. **How do you handle async operations in MobX?**
   - Mark the async method with `@action`: `@action Future<void> login(String email, String password) async { isLoading = true; try { user = await api.login(email, password); } catch (e) { error = e.toString(); } finally { isLoading = false; } }`. The action's observable modifications (isLoading, user, error) are tracked. However, `await` breaks the action's batching — changes after `await` are in a new batch. Use `ObservableFuture` for tracking async state: `@observable ObservableFuture<User>? userFuture`. Or use `runInAction(() { ... })` to wrap post-await changes in an action: `final user = await api.login(); runInAction(() => this.user = user);`.

6. **What are Reactions in MobX?**
   - Reactions are side effects that run when observed values change. `reaction<T>(predicate, effect)` — runs `effect` when `predicate`'s result changes. `autorun(fn)` — runs immediately and re-runs when any accessed observable changes. `when(predicate, effect)` — runs `effect` once when `predicate` becomes true. Reactions are useful for: logging, analytics, navigation, snackbars, persisting state. Always dispose reactions: `final disposer = reaction(...); disposer();` in `dispose()`. Reactions don't rebuild UI — use `Observer` for UI, reactions for side effects. Reactions are the MobX equivalent of `BlocListener` or `ref.listen`.

7. **How do you test MobX stores?**
   - Create the store, call actions, verify observable values: `test('increment increases count', () { final store = CounterStore(); store.increment(); expect(store.count, 1); })`. For async: `test('login sets user', () async { final store = UserStore(mockApi); await store.login('email', 'pass'); expect(store.isLoggedIn, isTrue); })`. Mock the API with `mocktail`. Test computed values: `expect(store.status, 'High')`. Test reactions: use `when()` reaction and verify the callback fires. MobX stores are pure Dart — no widget testing needed for logic. For widget tests: provide the store via Provider, pump the widget, call actions, verify Observer rebuilds with new values.

8. **What are the pros and cons of MobX?**
   - **Pros**: (1) Automatic reactivity — no manual `notifyListeners()`. (2) Computed values — derived state without manual recalculation. (3) Fine-grained reactivity — only observers watching changed values rebuild. (4) Clean OOP-style stores. (5) Good DevTools integration. **Cons**: (1) Requires code generation — `build_runner` adds build time. (2) Learning curve — observables, actions, reactions, computed. (3) Less popular than Provider/Riverpod/BLoC — smaller community. (4) Code generation can be confusing for beginners. (5) Less strict structure than BLoC — easier to make mistakes. Use MobX when you want automatic reactivity and computed values. For most apps, Provider or Riverpod are simpler choices.

9. **How does MobX compare to Riverpod?**
   - **MobX**: OOP-style stores with `@observable`/`@action`/`@computed`. Code generation required. Automatic reactivity — Observers auto-detect dependencies. Computed values built-in. More magical (less explicit). **Riverpod**: Functional-style providers. No code generation (optional with riverpod_generator). Explicit `ref.watch()` for reactivity. No built-in computed (use `Provider` that depends on others). More explicit and compile-safe. Riverpod is more popular, has better testing, and no code generation requirement. MobX's advantage is automatic reactivity and computed values — less boilerplate for derived state. Riverpod's advantage is compile-safety, explicitness, and no code generation. Choose Riverpod for most apps, MobX if you love reactive programming.

10. **How do you structure MobX stores for a large app?**
    - Create one store per feature: `AuthStore`, `CartStore`, `ProductStore`, `SettingsStore`. Each store manages its own observables, actions, and computed values. Use dependency injection (Provider or get_it) to provide stores. Stores can reference each other via constructor injection. Split large stores into smaller ones — don't create a god store. Use `@computed` for cross-store derived values. Persist stores with `ObservableStream` + SharedPreferences. Keep stores in `domain/` or `stores/` folder, separate from UI. Test each store independently. MobX stores are classes — use OOP best practices (single responsibility, composition over inheritance). Example structure: `stores/auth/auth_store.dart`, `stores/cart/cart_store.dart`.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Comparison](Comparison.md)
- [Best Practices](BestPractices.md)
