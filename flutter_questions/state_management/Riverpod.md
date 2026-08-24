# Riverpod

## 📖 Explanation

Riverpod is a modern state management package by the same author as Provider. It's compile-safe, doesn't depend on `BuildContext`, and provides excellent testability. It's the recommended choice for medium to large Flutter apps.

### Riverpod vs Provider
| Feature | Provider | Riverpod |
|---------|----------|----------|
| Compile-safe | ❌ | ✅ |
| Needs BuildContext | ✅ | ❌ |
| Auto-dispose | Manual | Built-in |
| Scoped | Manual | ProviderScope |
| Testing | MultiProvider | Override |
| Async handling | Manual | AsyncValue |

### Provider Types
| Provider | State Type | Use Case |
|----------|-----------|----------|
| `Provider` | Read-only | Computed values, services |
| `StateProvider` | Mutable simple | Counter, toggle, dropdown |
| `FutureProvider` | AsyncValue<T> | API call (one-time) |
| `StreamProvider` | AsyncValue<T> | Firestore, WebSocket |
| `NotifierProvider` | Notifier<T> | Complex state (Riverpod 2.0+) |
| `AsyncNotifierProvider` | AsyncNotifier<T> | Complex async state |

### AsyncValue
Riverpod wraps async data in `AsyncValue<T>` with three states:
- `AsyncValue.data(T)` — data loaded
- `AsyncValue.loading()` — loading
- `AsyncValue.error(error, stack)` — error

Use `.when(data:, loading:, error:)` or `.maybeWhen()` to handle all states.

### ref.watch vs ref.read
| Method | Rebuilds | Use In |
|--------|----------|--------|
| `ref.watch(provider)` | ✅ Yes | build() |
| `ref.read(provider)` | ❌ No | Callbacks, event handlers |
| `ref.listen(provider, callback)` | Side effect | Snackbars, navigation |

### ProviderScope
`ProviderScope` is the root of all Riverpod providers. Wrap `MaterialApp` with `ProviderScope(child: MyApp())`. Use nested `ProviderScope(overrides: [...])` for scoped providers (feature-level isolation).

---

## 🧪 Code Example

```dart
import 'package:flutter_riverpod/flutter_riverpod.dart';

// ── Provider (read-only) ──
final apiClientProvider = Provider<ApiClient>((ref) {
  return ApiClient();
});

// ── StateProvider (simple mutable) ──
final counterProvider = StateProvider<int>((ref) => 0);

// ── FutureProvider (async) ──
final userProvider = FutureProvider<User>((ref) async {
  final api = ref.watch(apiClientProvider);
  return api.fetchUser();
});

// ── StreamProvider (real-time) ──
final messagesProvider = StreamProvider<List<Message>>((ref) {
  return ref.watch(apiClientProvider).messageStream();
});

// ── NotifierProvider (complex state — Riverpod 2.0+) ──
final cartProvider = NotifierProvider<CartNotifier, CartState>(() {
  return CartNotifier();
});

class CartNotifier extends Notifier<CartState> {
  @override
  CartState build() => CartState.initial();

  void addItem(Item item) {
    state = state.copyWith(items: [...state.items, item]);
  }

  void removeItem(Item item) {
    state = state.copyWith(
      items: state.items.where((i) => i != item).toList(),
    );
  }

  void clear() {
    state = CartState.initial();
  }
}

class CartState {
  final List<Item> items;
  const CartState({required this.items});
  const CartState.initial() : items = const [];

  double get totalPrice =>
      items.fold(0.0, (sum, item) => sum + item.price);

  CartState copyWith({List<Item>? items}) =>
      CartState(items: items ?? this.items);
}

// ── AsyncNotifierProvider (complex async) ──
final productProvider =
    AsyncNotifierProvider<ProductNotifier, List<Product>>(() {
  return ProductNotifier();
});

class ProductNotifier extends AsyncNotifier<List<Product>> {
  @override
  Future<List<Product>> build() async {
    return _fetchProducts();
  }

  Future<List<Product>> _fetchProducts() async {
    final api = ref.watch(apiClientProvider);
    return api.getProducts();
  }

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _fetchProducts());
  }
}

// ── ConsumerWidget (read providers) ──
class CartScreen extends ConsumerWidget {
  const CartScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final cart = ref.watch(cartProvider);

    return Scaffold(
      appBar: AppBar(title: Text('Cart (${cart.items.length})')),
      body: ListView.builder(
        itemCount: cart.items.length,
        itemBuilder: (_, i) => ListTile(
          title: Text(cart.items[i].name),
          trailing: IconButton(
            icon: const Icon(Icons.remove),
            onPressed: () =>
                ref.read(cartProvider.notifier).removeItem(cart.items[i]),
          ),
        ),
      ),
      bottomNavigationBar: Padding(
        padding: const EdgeInsets.all(16),
        child: Text('Total: \$${cart.totalPrice}'),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => ref.read(cartProvider.notifier).addItem(
              const Item(name: 'New', price: 9.99),
            ),
        child: const Icon(Icons.add),
      ),
    );
  }
}

// ── AsyncValue handling ──
class UserScreen extends ConsumerWidget {
  const UserScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userProvider);

    return userAsync.when(
      data: (user) => Text('Hello, ${user.name}'),
      loading: () => const CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  }
}

// ── ref.listen (side effects) ──
class CheckoutScreen extends ConsumerWidget {
  const CheckoutScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    ref.listen<CartState>(cartProvider, (previous, next) {
      if (next.items.isEmpty && previous?.items.isNotEmpty == true) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Cart cleared!')),
        );
      }
    });

    return const SizedBox();
  }
}

// ── AutoDispose (cleanup when no listeners) ──
final autoCompleteProvider =
    AutoDisposeFutureProvider.family<List<String>, String>((ref, query) async {
  // Auto-disposes when the widget is removed
  return api.search(query);
});

// ── Setup ──
void main() {
  runApp(const ProviderScope(child: MyApp()));
}
```

### Output
```
A Flutter app with Riverpod state management:
- Provider/StateProvider for simple state
- NotifierProvider for complex cart state
- FutureProvider/StreamProvider for async data
- AsyncNotifierProvider for complex async
- AsyncValue.when for loading/data/error handling
- ref.listen for side effects (snackbars, navigation)
- AutoDispose for automatic cleanup
```

---

## ❓ Interview Questions

1. **What is Riverpod and how does it differ from Provider?**
   - Riverpod is Provider's successor by the same author (Remi Rousselet). Key differences: (1) Compile-safe — no `ProviderNotFoundException` at runtime. (2) No `BuildContext` needed — providers are accessed via `ref`, usable outside widget tree. (3) Providers are global top-level variables, not widget-tree-dependent. (4) Auto-dispose — providers can auto-dispose when no one listens. (5) `AsyncValue` for built-in async handling (loading/data/error). (6) Better testability — override providers without `MultiProvider`. (7) Scoped with `ProviderScope` instead of nesting providers. Riverpod is more structured and safer but has a steeper learning curve than Provider.

2. **What are the different provider types in Riverpod?**
   - `Provider<T>` — read-only value or service. `StateProvider<T>` — simple mutable state (counter, toggle). `FutureProvider<T>` — async data, returns `AsyncValue<T>`. `StreamProvider<T>` — stream data, returns `AsyncValue<T>`. `NotifierProvider<Notifier<T>, T>` — complex state with methods (Riverpod 2.0+). `AsyncNotifierProvider<AsyncNotifier<T>, T>` — complex async state. `ChangeNotifierProvider` — for existing ChangeNotifier models. Use `Provider` for services, `StateProvider` for simple state, `NotifierProvider` for complex state with business logic, `FutureProvider`/`StreamProvider` for async data.

3. **What is `ref.watch` vs `ref.read`?**
   - `ref.watch(provider)` — registers the widget as a listener, rebuilds when the provider's value changes. Use in `build()` where the UI depends on the state. `ref.read(provider)` — accesses the current value without listening — no rebuild. Use in event handlers (onPressed, onTap) where you just need to call a method or read a value once. `ref.listen(provider, (prev, next) { ... })` — listen for side effects without rebuilding — use for snackbars, navigation, logging. Rule: watch in build, read in callbacks, listen for side effects. Never use `ref.watch()` in callbacks or `ref.read()` expecting rebuilds.

4. **What is AsyncValue and how do you handle it?**
   - `AsyncValue<T>` wraps async data with three states: `AsyncValue.data(T)`, `AsyncValue.loading()`, `AsyncValue.error(error, stack)`. Use `.when(data: (d) => ..., loading: () => ..., error: (e, s) => ...)` to handle all states. Use `.maybeWhen()` for optional handling. Use `.whenData((d) => ...)` to transform data while keeping loading/error states. `AsyncValue.guard(() async { ... })` catches exceptions and wraps them in `AsyncValue.error`. Use `ref.refresh(provider)` to re-execute. `AsyncValue` eliminates the need for separate loading/error/data variables — one value handles all states declaratively.

5. **What is NotifierProvider and how is it different from StateProvider?**
   - `StateProvider<T>` is for simple mutable state — `final counterProvider = StateProvider<int>((ref) => 0)`. Modify with `ref.read(counterProvider.notifier).state++`. Good for counters, toggles, dropdowns. `NotifierProvider<Notifier<T>, T>` is for complex state with business logic — `final cartProvider = NotifierProvider<CartNotifier, CartState>(() => CartNotifier())`. The `Notifier` class has methods (`addItem`, `removeItem`) that modify `state`. Good for carts, forms, multi-step flows. NotifierProvider is more structured — state changes go through methods, not direct `state` manipulation. Use StateProvider for simple values, NotifierProvider for complex state with logic.

6. **What is AutoDispose and when do you use it?**
   - `AutoDispose` providers automatically dispose when no widget is listening. `final productProvider = AutoDisposeFutureProvider(...)`. When the widget watching it is removed from the tree, the provider's state is disposed — frees memory and cancels pending futures/streams. Use for: search results, detail screens, paginated data — anything that shouldn't persist when the user navigates away. Use `.family` with AutoDispose for parameterized providers: `AutoDisposeFutureProvider.family<T, Param>`. Without AutoDispose, providers live for the app's lifetime — can cause memory leaks. Use non-auto-dispose for app-level state (auth, theme).

7. **How do you test Riverpod providers?**
   - Use `ProviderContainer` with overrides: `final container = ProviderContainer(overrides: [apiClientProvider.overrideWithValue(MockApiClient())])`. Access provider: `container.read(provider)`. For async: `await container.read(futureProvider.future)`. Dispose: `container.dispose()`. For widget tests: `tester.pumpWidget(ProviderScope(overrides: [...], child: MyApp()))`. Use `overrideWith` for NotifierProvider: `cartProvider.overrideWith(() => MockCartNotifier())`. Test state transitions by reading before and after actions. Riverpod's testability is a key advantage over Provider — no need for `MultiProvider` wrappers.

8. **What is `ref.listen` and when do you use it?**
   - `ref.listen(provider, (previous, next) { ... })` listens to provider changes for side effects without rebuilding the widget. Use for: showing snackbars, navigating, showing dialogs, logging analytics. Example: `ref.listen<CartState>(cartProvider, (prev, next) { if (next.items.isEmpty) ScaffoldMessenger.of(context).showSnackBar(...); })`. Unlike `ref.watch`, the callback receives both `previous` and `next` values. Place `ref.listen` in `build()` — it's set up once and cleaned up automatically. For one-time effects, check `prev != next` in the callback. `ref.listen` is the Riverpod equivalent of `BlocListener` in BLoC.

9. **How do you use `.family` for parameterized providers?**
   - `.family` creates a provider for each parameter value. `final productProvider = FutureProvider.family<Product, int>((ref, id) async { return api.getProduct(id); })`. Watch with `ref.watch(productProvider(42))` — each `id` gets its own cached provider instance. Use for: detail screens (product by ID), filtered lists (by category), search (by query). Combine with AutoDispose: `AutoDisposeFutureProvider.family<T, Param>` to dispose when the widget is unmounted. The parameter must implement `==` and `hashCode` (use a class with `equatable` or a record). Families enable efficient caching — fetching product 42 twice returns the cached result.

10. **What are the advantages of Riverpod over BLoC?**
    - (1) Less boilerplate — no events/states classes, just providers and notifiers. (2) Compile-safe — type errors caught at compile time. (3) Built-in async handling — `AsyncValue` handles loading/error/data. (4) Auto-dispose — automatic lifecycle management. (5) No `BuildContext` needed — providers accessible anywhere. (6) Simpler testing — `ProviderContainer` with overrides. (7) Incremental adoption — can use alongside Provider. BLoC advantages: (1) Better for very complex state machines. (2) Event-driven architecture is more explicit. (3) `bloc_test` for state transition testing. (4) Better for teams that want strict structure. Choose Riverpod for medium-large apps, BLoC for very large/enterprise apps with complex state machines.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [BLoC](BLoC.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
