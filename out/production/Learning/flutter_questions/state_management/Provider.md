# Provider

## 📖 Explanation

Provider is Flutter's officially recommended state management package for small to medium apps. It's a wrapper around `InheritedWidget` that makes state easy to share, consume, and test.

### Provider Core Concepts
| Concept | Purpose |
|---------|---------|
| `ChangeNotifier` | Holds state, calls `notifyListeners()` on change |
| `ChangeNotifierProvider` | Provides the model to the widget tree |
| `context.watch<T>()` | Rebuild widget when T changes |
| `context.read<T>()` | Access T without rebuild (use in callbacks) |
| `Consumer<T>` | Rebuild only the child subtree |
| `Selector<T, S>` | Rebuild only when a specific field changes |
| `MultiProvider` | Provide multiple models at once |
| `ProxyProvider` | Provide a model that depends on other models |

### Provider Types
| Provider | Creates | Use Case |
|----------|---------|----------|
| `Provider` | Value (immutable) | Static config, constants |
| `ChangeNotifierProvider` | ChangeNotifier | Mutable state (most common) |
| `FutureProvider` | Future result | Async data (API call) |
| `StreamProvider` | Stream result | Real-time data (Firestore) |
| `ValueListenableProvider` | ValueListenable | Lightweight state |
| `ProxyProvider` | Depends on others | Computed providers |

### Consumer vs Selector
| Feature | Consumer | Selector |
|---------|----------|----------|
| Rebuilds on | Any change to T | Only when selected field changes |
| Granularity | Whole model | Single field |
| Performance | Good | Better (fewer rebuilds) |
| Use case | Simple models | Large models with many fields |

### Best Practices
- Use `context.read<T>()` in event handlers (onPressed), `context.watch<T>()` in build
- Use `Selector` over `Consumer` when you need one field
- Use `MultiProvider` at app root for all app-level state
- Split models by feature — don't put everything in one model
- Always call `notifyListeners()` after state changes
- Use `const` for static children in `Consumer`'s `child` parameter

---

## 🧪 Code Example

```dart
// ── Model ──
class CartModel extends ChangeNotifier {
  final List<Item> _items = [];
  List<Item> get items => List.unmodifiable(_items);

  double get totalPrice =>
      _items.fold(0.0, (sum, item) => sum + item.price);

  int get itemCount => _items.length;

  void add(Item item) {
    _items.add(item);
    notifyListeners();
  }

  void remove(Item item) {
    _items.remove(item);
    notifyListeners();
  }

  void clear() {
    _items.clear();
    notifyListeners();
  }
}

// ── Provide at app root ──
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CartModel()),
        ChangeNotifierProvider(create: (_) => UserModel()),
        Provider<ApiClient>(create: (_) => ApiClient()),
      ],
      child: const MyApp(),
    ),
  );
}

// ── watch: rebuild on change ──
class CartTotal extends StatelessWidget {
  const CartTotal({super.key});

  @override
  Widget build(BuildContext context) {
    final total = context.watch<CartModel>().totalPrice;  // Rebuilds
    return Text('\$${total.toStringAsFixed(2)}');
  }
}

// ── read: access without rebuild (use in callbacks) ──
class AddButton extends StatelessWidget {
  const AddButton({super.key});

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        context.read<CartModel>().add(Item(name: 'New', price: 9.99));
      },
      child: const Text('Add Item'),
    );
  }
}

// ── Consumer: rebuild only subtree ──
Consumer<CartModel>(
  builder: (context, cart, child) {
    return Text('Items: ${cart.itemCount}');  // Rebuilds this only
  },
)

// Consumer with child (static widget not rebuilt)
Consumer<CartModel>(
  builder: (context, cart, child) {
    return Column(children: [
      Text('Total: \$${cart.totalPrice}'),
      child!,  // Static widget — not rebuilt
    ]);
  },
  child: const ExpensiveHeader(),  // Built once
)

// ── Selector: rebuild only when totalPrice changes ──
Selector<CartModel, double>(
  selector: (_, cart) => cart.totalPrice,  // Select specific field
  builder: (context, total, child) {
    return Text('Total: \$${total.toStringAsFixed(2)}');
  },
)

// ── FutureProvider ──
FutureProvider<User>(
  create: (_) => api.fetchUser(),
  initialData: User.loading(),
  child: UserWidget(),
)

// ── StreamProvider ──
StreamProvider<List<Message>>(
  create: (_) => firestore.messages.snapshots().map(toList),
  initialData: const [],
  child: MessageList(),
)

// ── ProxyProvider (depends on other providers) ──
MultiProvider(
  providers: [
    Provider<ApiClient>(create: (_) => ApiClient()),
    ProxyProvider<ApiClient, UserRepository>(
      update: (_, api, __) => UserRepository(api),
    ),
    ProxyProvider2<ApiClient, UserRepository, AuthService>(
      update: (_, api, repo, __) => AuthService(api, repo),
    ),
  ],
  child: MyApp(),
)
```

### Output
```
A Flutter app with Provider state management:
- CartModel (ChangeNotifier) for cart state
- MultiProvider at app root for multiple models
- context.watch for rebuilds, context.read for callbacks
- Consumer with child for optimized rebuilds
- Selector for field-level rebuild optimization
- FutureProvider/StreamProvider for async data
- ProxyProvider for dependent providers
```

---

## ❓ Interview Questions

1. **What is Provider and how does it work?**
   - Provider is a state management package built on `InheritedWidget`. Create a model extending `ChangeNotifier` with state and methods. Provide it with `ChangeNotifierProvider(create: (_) => MyModel())`. The model calls `notifyListeners()` when state changes. Widgets consume it with `context.watch<T>()` (rebuilds on change) or `context.read<T>()` (access without rebuild). Under the hood, Provider registers the widget as a listener to the ChangeNotifier. When `notifyListeners()` is called, all listening widgets rebuild. Provider is the official recommendation for small to medium apps — simple, well-documented, and integrates with Flutter DevTools.

2. **What is the difference between `context.watch` and `context.read`?**
   - `context.watch<T>()` registers the widget as a listener — the widget rebuilds whenever `notifyListeners()` is called on T. Use in `build()` where the UI depends on the state. `context.read<T>()` accesses T without registering as a listener — no rebuild. Use in event handlers (onPressed, onTap) where you just need to call a method. Mistake: using `context.watch()` in an `onPressed` callback — it does nothing because it's not in a build method. Mistake: using `context.read()` in `build()` and expecting rebuilds — it won't rebuild. Rule: watch in build, read in callbacks.

3. **What is Consumer and how does it optimize rebuilds?**
   - `Consumer<T>` wraps a subtree and rebuilds only that subtree when T changes, instead of the entire parent widget. `Consumer<CartModel>(builder: (context, cart, child) { return Text('\$${cart.totalPrice}'); })`. The `child` parameter is for static widgets that don't depend on the model — they're built once and passed to the builder, not rebuilt on each change. Without Consumer, the entire parent widget rebuilds when `context.watch<T>()` is called. Consumer limits the rebuild scope to just the builder's return value. Use Consumer when only part of the widget tree depends on the model.

4. **What is Selector and how is it different from Consumer?**
   - `Selector<T, S>` rebuilds only when a specific field (of type S) of the model changes, not on every `notifyListeners()`. `Selector<CartModel, double>(selector: (_, cart) => cart.totalPrice, builder: (_, total, __) => Text('\$$total'))` — rebuilds only when `totalPrice` changes, not when items list changes. Consumer rebuilds on any change to the model. Selector is more granular — better for large models with many fields where only one field is relevant to a widget. The selector function must return a value that implements `==` (for comparison). Use Selector over Consumer when you only need one field.

5. **How do you provide multiple models?**
   - Use `MultiProvider` to nest multiple providers cleanly: `MultiProvider(providers: [ChangeNotifierProvider(create: (_) => CartModel()), ChangeNotifierProvider(create: (_) => UserModel()), Provider<ApiClient>(create: (_) => ApiClient())], child: MyApp())`. Without MultiProvider, you'd nest providers manually: `ChangeNotifierProvider(changeNotifierProvider(Provider(child: MyApp())))`. MultiProvider is cleaner and easier to maintain. Each model is accessed independently via `context.watch<CartModel>()` and `context.watch<UserModel>()`. Split models by feature — don't put all state in one giant model.

6. **What is FutureProvider and StreamProvider?**
   - `FutureProvider` provides the result of a `Future` — the widget rebuilds when the future completes. `FutureProvider<User>(create: (_) => api.fetchUser(), initialData: User.loading(), child: UserWidget())`. Use for one-time async data (API call, shared preferences). `StreamProvider` provides the latest value of a `Stream` — the widget rebuilds on each new event. `StreamProvider<List<Message>>(create: (_) => firestore.snapshots(), initialData: const [], child: MessageList())`. Use for real-time data (Firestore, WebSocket, sensors). Both handle loading and error states via `initialData`. They're the Provider equivalent of `FutureBuilder` and `StreamBuilder`.

7. **What is ProxyProvider?**
   - `ProxyProvider` creates a provider that depends on other providers. `ProxyProvider<ApiClient, UserRepository>(update: (_, api, __) => UserRepository(api))` — creates a `UserRepository` that depends on `ApiClient` from another provider. When `ApiClient` changes, `UserRepository` is recreated. `ProxyProvider2<A, B, C>` depends on two providers, `ProxyProvider3` on three, etc. Use for dependency injection — create repositories that depend on API clients, use cases that depend on repositories. This keeps the dependency graph clean and testable — swap `ApiClient` in tests and `UserRepository` automatically uses the mock.

8. **How do you test widgets that use Provider?**
   - Wrap the test widget with `ChangeNotifierProvider` in the test: `tester.pumpWidget(ChangeNotifierProvider(create: (_) => CartModel(), child: MaterialApp(home: CartScreen())))`. For mocking: create a mock model extending `ChangeNotifier` with test data. Use `MultiProvider` for multiple models. For Provider overrides: `ProviderScope(overrides: [...])`. Test state changes: call methods on the model, then `tester.pump()` to trigger rebuild, then verify the UI. For Riverpod: `ProviderContainer(overrides: [provider.overrideWithValue(mock)])`. Test that `notifyListeners()` is called after state changes by verifying the widget rebuilds with new data.

9. **How do you dispose providers?**
   - `ChangeNotifierProvider` automatically calls `dispose()` on the model when the provider is removed from the tree (e.g., when the screen is popped). The model's `dispose()` method should clean up resources: cancel streams, timers, subscriptions. `ChangeNotifierProvider(create: (_) => MyModel(), dispose: (_, model) => model.cleanup())` for custom disposal. For `FutureProvider` and `StreamProvider`, the provider auto-cancels the future/stream when disposed. If you use `ChangeNotifierProvider.value(value: existingModel)`, the provider does NOT dispose the model — you must manage its lifecycle manually. Always use the `create` constructor for auto-disposal.

10. **What are the limitations of Provider?**
    - (1) Not compile-safe — `ProviderNotFoundException` at runtime if you forget to provide T. (2) Depends on `BuildContext` — can't access providers outside the widget tree (in services, use cases). (3) `notifyListeners()` notifies ALL listeners — can't target specific widgets (use `Selector` to mitigate). (4) No built-in async handling like Riverpod's `AsyncValue`. (5) No auto-dispose by default (need `ChangeNotifierProvider`). (6) Testing requires wrapping in `MultiProvider`. These limitations led to Riverpod — same author, fixes these issues. For small to medium apps, Provider is fine. For large apps with complex state, consider Riverpod or BLoC.

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [Riverpod](Riverpod.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
