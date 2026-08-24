# State Management Advanced

## 📖 Explanation

Advanced state management goes beyond `setState` — using patterns and packages for scalable, testable, and maintainable state management. The main contenders are **Provider**, **Riverpod**, **BLoC**, and **GetX**.

### State Management Comparison
| Feature | Provider | Riverpod | BLoC | GetX |
|---------|----------|----------|------|------|
| Learning curve | Low | Medium | High | Low |
| Boilerplate | Low | Medium | High | Low |
| Testability | Good | Excellent | Excellent | Fair |
| Compile-safe | ❌ | ✅ | ✅ | ❌ |
| Reactive | ChangeNotifier | Providers | Streams | Rx |
| Best for | Small-Medium | Medium-Large | Large | Rapid dev |

### Provider Concepts
- `ChangeNotifier` — holds state, calls `notifyListeners()` on change
- `ChangeNotifierProvider` — provides the model to the tree
- `context.watch<T>()` / `Consumer<T>` — rebuild on change
- `context.read<T>()` — access without rebuild
- `Selector<T, S>` — rebuild only when selected field changes
- `MultiProvider` — provide multiple models

### Riverpod Concepts
- `Provider` — read-only value
- `StateProvider` — mutable state (counter, toggle)
- `FutureProvider` — async data (API call)
- `StreamProvider` — stream data (Firestore)
- `StateNotifierProvider` — complex state with StateNotifier
- `NotifierProvider` (Riverpod 2.0+) — modern replacement
- `ref.watch()` — rebuild on change, `ref.read()` — access without rebuild

### BLoC Concepts
- **Event** — user action or trigger (immutable)
- **State** — UI state (immutable)
- **Bloc** — transforms Events → States via `EventTransformer`
- `BlocBuilder` — rebuild on state change
- `BlocListener` — side effect on state change (navigation, snackbar)
- `BlocProvider` — provides Bloc to the tree

### State Types
| State Type | Example | Management |
|-----------|---------|------------|
| Ephemeral | Text input, animation | setState |
| Local | Form, tab index | StatefulWidget |
| App | Theme, locale | Provider/Riverpod |
| Shared | Cart, user auth | Provider/Riverpod/BLoC |

### When to Use What
```
Simple widget state     → setState
Shared across screens   → Provider/Riverpod
Complex app state       → BLoC
Rapid prototyping       → GetX
```

---

## 🧪 Code Example

```dart
// ── Provider with ChangeNotifier ──
class CartModel extends ChangeNotifier {
  final List<Item> _items = [];
  List<Item> get items => List.unmodifiable(_items);

  double get totalPrice =>
      _items.fold(0, (sum, item) => sum + item.price);

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

// Provide
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CartModel()),
        ChangeNotifierProvider(create: (_) => UserModel()),
      ],
      child: const MyApp(),
    ),
  );
}

// Consume — rebuild on change
Consumer<CartModel>(
  builder: (context, cart, child) {
    return Text('Total: \$${cart.totalPrice}');
  },
)

// Selector — rebuild only when totalPrice changes
Selector<CartModel, double>(
  selector: (_, cart) => cart.totalPrice,
  builder: (context, total, child) {
    return Text('Total: \$$total');
  },
)

// Read without rebuild
onPressed: () => context.read<CartModel>().add(item),

// ── Riverpod ──
final cartProvider = NotifierProvider<CartNotifier, List<Item>>(() {
  return CartNotifier();
});

class CartNotifier extends Notifier<List<Item>> {
  @override
  List<Item> build() => [];

  void add(Item item) => state = [...state, item];
  void remove(Item item) => state = state.where((i) => i != item).toList();
  void clear() => state = [];

  double get totalPrice =>
      state.fold(0, (sum, item) => sum + item.price);
}

// Async data
final userProvider = FutureProvider<User>((ref) async {
  return api.fetchUser();
});

// Stream data
final messagesProvider = StreamProvider<List<Message>>((ref) {
  return firestore.collection('messages').snapshots().map(...);
});

// Consumer
class CartScreen extends ConsumerWidget {
  const CartScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final items = ref.watch(cartProvider);
    final total = ref.watch(cartProvider.notifier).totalPrice;

    return Column(
      children: [
        Expanded(
          child: ListView.builder(
            itemCount: items.length,
            itemBuilder: (_, i) => ListTile(title: Text(items[i].name)),
          ),
        ),
        Text('Total: \$$total'),
        ElevatedButton(
          onPressed: () => ref.read(cartProvider.notifier).clear(),
          child: const Text('Clear'),
        ),
      ],
    );
  }
}

// ── BLoC ──
// Events
abstract class CartEvent {}
class AddItem extends CartEvent { final Item item; AddItem(this.item); }
class RemoveItem extends CartEvent { final Item item; RemoveItem(this.item); }
class ClearCart extends CartEvent {}

// States
abstract class CartState {}
class CartInitial extends CartState {}
class CartUpdated extends CartState {
  final List<Item> items;
  CartUpdated(this.items);
  double get totalPrice => items.fold(0, (s, i) => s + i.price);
}

// Bloc
class CartBloc extends Bloc<CartEvent, CartState> {
  final List<Item> _items = [];

  CartBloc() : super(CartInitial()) {
    on<AddItem>((event, emit) {
      _items.add(event.item);
      emit(CartUpdated(List.unmodifiable(_items)));
    });
    on<RemoveItem>((event, emit) {
      _items.remove(event.item);
      emit(CartUpdated(List.unmodifiable(_items)));
    });
    on<ClearCart>((event, emit) {
      _items.clear();
      emit(CartUpdated([]));
    });
  }
}

// Usage
BlocProvider(
  create: (_) => CartBloc(),
  child: BlocBuilder<CartBloc, CartState>(
    builder: (context, state) {
      if (state is CartUpdated) {
        return Text('Total: \$${state.totalPrice}');
      }
      return const Text('Cart empty');
    },
  ),
)

// Side effects with BlocListener
BlocListener<CartBloc, CartState>(
  listener: (context, state) {
    if (state is CartUpdated && state.items.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Cart cleared!')),
      );
    }
  },
  child: ...,
)
```

### Output
```
A Flutter app with advanced state management:
- Provider: ChangeNotifier + Consumer + Selector for selective rebuilds
- Riverpod: NotifierProvider for cart, FutureProvider for async, StreamProvider for real-time
- BLoC: Events → States with BlocBuilder for UI and BlocListener for side effects
- MultiProvider for multiple state providers at app root
```

---

## ❓ Interview Questions

1. **How do you structure a ChangeNotifier model for a feature with multiple related state fields?**
   - Group related fields in a single `ChangeNotifier` model rather than creating one model per field. Example: `class CartModel extends ChangeNotifier { List<Item> _items = []; bool _isLoading = false; String? _error; List<Item> get items => _items; bool get isLoading => _isLoading; String? get error => _error; }`. Call `notifyListeners()` once after a batch of changes — not after each field. Use `Selector` to limit rebuilds to widgets that depend on specific fields. For unrelated features (auth, cart, settings), create separate models and provide them with `MultiProvider`. Keep models focused on a single feature domain — don't put auth, cart, and settings in one giant model.

2. **How do you migrate an app from Provider to Riverpod?**
   - Migration is incremental: (1) Add Riverpod (`ProviderScope` at root) alongside existing Provider — both work simultaneously. (2) Convert one feature at a time — create a Riverpod provider that replaces the `ChangeNotifier`. (3) Replace `ChangeNotifierProvider` with `NotifierProvider` or `Provider`. (4) Replace `context.watch<T>()` with `ref.watch(provider)` — convert widgets to `ConsumerWidget`. (5) Replace `context.read<T>()` with `ref.read(provider)`. (6) Use `ref.listen()` for side effects (replacing `BlocListener`-like patterns). (7) Remove the Provider dependency once all features are migrated. Keep models as plain Dart classes — Riverpod doesn't require `ChangeNotifier`. Test each feature after migration. The key benefit: no more `ProviderNotFoundException` runtime errors.

3. **How do you handle side effects (navigation, snackbar, dialog) in BLoC without coupling UI to BLoC?**
   - Use `BlocListener` for one-time side effects — it doesn't rebuild the UI, it just runs a callback when state changes. Define a state field for side effects: `class ShowSnackbar extends CartState { final String message; }`. In `BlocListener`: `if (state is ShowSnackbar) { ScaffoldMessenger.of(context).showSnackBar(...); }`. Alternatively, use a `Stream` or `controller` exposed by the BLoC for events that shouldn't be part of the state. In Riverpod: use `ref.listen(provider, (prev, next) { if (next is ShowSnackbar) showSnack(context, next.message); })`. Never put `BuildContext` or `Navigator` inside the BLoC — keep it pure. The BLoC emits states; the UI interprets them.

4. **What is the difference between `context.watch` and `context.read`?**
   - `context.watch<T>()` registers the widget as a listener to the provider — the widget rebuilds whenever `notifyListeners()` is called. Use in `build()` where the UI depends on the state. `context.read<T>()` accesses the provider without registering as a listener — no rebuild. Use in event handlers (onPressed, onTap) where you just need to call a method. In Riverpod: `ref.watch(provider)` (rebuild) vs `ref.read(provider)` (no rebuild). In BLoC: `BlocBuilder` (rebuild) vs `context.read<Bloc>()` (dispatch event, no rebuild). Rule: watch in build, read in callbacks.

5. **What is Selector and why use it?**
   - `Selector<Model, T>` (Provider) or `.select()` (Riverpod) rebuilds the widget only when a specific field of the model changes, not on every `notifyListeners()`. Example: `Selector<CartModel, double>(selector: (_, cart) => cart.totalPrice, builder: (_, total, __) => Text('\$$total'))` — rebuilds only when `totalPrice` changes, not when items list changes. This prevents unnecessary rebuilds — if you have a cart count badge and a total price display, changing the cart only rebuilds the affected widget. Always use `Selector` over `Consumer` when you only need a specific field. The selector function must return a value that implements `==`.

6. **How do you handle async state with Riverpod?**
   - Use `FutureProvider` for one-time async data: `final userProvider = FutureProvider<User>((ref) async => api.fetchUser())`. The UI uses `ref.watch(userProvider)` which returns `AsyncValue<User>` — use `.when(data: ..., loading: ..., error: ...)`. Use `StreamProvider` for continuous data: `final messagesProvider = StreamProvider<List<Message>>((ref) => firestore.snapshots())`. Use `AsyncValue.guard(() async { ... })` for error handling. Use `ref.refresh(provider)` to re-fetch. Use `AutoDisposeFutureProvider` to auto-dispose when no widget is listening. For complex async state, use `AsyncNotifier` (Riverpod 2.0+).

7. **How do you test BLoC?**
   - Use `bloc_test` package. Test that events produce expected states: `blocTest<CartBloc, CartState>('AddItem adds item', build: () => CartBloc(), act: (bloc) => bloc.add(AddItem(item)), expect: () => [CartUpdated([item])])`. Mock dependencies with `mockito` — inject mock repository into the BLoC. Test events: dispatch events, verify states. Test side effects: verify navigation, snackbars using `BlocListener`. For Provider: use `ProviderScope(overrides: [provider.overrideWithValue(mock)])`. For Riverpod: `ProviderContainer(overrides: [provider.overrideWithValue(mock)])`. Test that the BLoC doesn't emit unexpected states — use `expect: []` for events that shouldn't change state.

8. **How do you manage state across multiple screens?**
   - Provide the state at a high level in the widget tree (above `MaterialApp` or at the route level). With Provider: `MultiProvider(providers: [...], child: MaterialApp(...))`. With Riverpod: wrap with `ProviderScope` at app root. With BLoC: `BlocProvider(create: (_) => CartBloc(), child: MaterialApp(...))` — use `BlocProvider.value` for existing instances. The state is accessible from any screen via `context.watch<T>()` (Provider), `ref.watch(provider)` (Riverpod), or `BlocProvider.of<T>(context)` (BLoC). For feature-scoped state, provide at the feature's root widget and it will be disposed when the feature is popped.

9. **How do you handle state persistence across app restarts?**
   - Persist state to `SharedPreferences` (simple key-value), `Hive` (NoSQL), or `SQLite` (relational). Pattern: (1) On app start, load saved state from storage and initialize the model/provider with it. (2) On every state change, save to storage. For Provider: `cartModel.addListener(() { prefs.setString('cart', jsonEncode(cartModel.items)); })`. For Riverpod: use a `ProviderObserver` that saves state on change. For BLoC: use a `BlocObserver` or listen to the bloc's stream and persist. For large state, debounce saves (e.g., save 500ms after last change) to avoid excessive I/O. Use `hydrated_bloc` package for automatic state persistence in BLoC. Use `hydrated` (Riverpod) or serialize in `Notifier.build()`. Always handle deserialization errors — corrupted data shouldn't crash the app.

10. **How do you debug state management issues (state not updating, unexpected rebuilds)?**
    - (1) **State not updating**: Check if `notifyListeners()` (Provider), `state =` (Riverpod), or `emit()` (BLoC) is called. Add `debugPrint` before and after. Verify the widget uses `watch`/`Consumer`/`BlocBuilder`, not `read`. (2) **Unexpected rebuilds**: Use Flutter DevTools "Track Widget Builds" to see which widgets rebuild. Add `debugPrint('Building $runtimeType')` in `build()`. Fix with `Selector`, `const`, or `Consumer` with `child` parameter. (3) **Provider not found**: Verify `ProviderScope`/`MultiProvider` is above the widget accessing it. (4) **State leaks**: Check that controllers and subscriptions are disposed in `dispose()`. (5) **Race conditions**: Use `AsyncValue.guard()` (Riverpod) or `emit` sequentially (BLoC). (6) Use Flutter Inspector to inspect the widget tree and verify provider placement.

---

## 🔗 Related Topics
- [State Management (Beginner)](../beginner/StateManagement.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
- [State Management Scenarios](../scenario_based/StateManagementScenarios.md)
