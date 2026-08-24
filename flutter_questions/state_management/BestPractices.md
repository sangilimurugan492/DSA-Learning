# State Management Best Practices

## 📖 Explanation

Regardless of which state management solution you choose, these best practices ensure scalable, maintainable, and testable Flutter apps.

### Core Principles
| Principle | Description |
|-----------|-------------|
| Single Source of Truth | State lives in one place, not duplicated |
| Immutability | State objects are immutable — create new, don't modify |
| Separation of Concerns | Business logic separate from UI |
| Unidirectional Data Flow | Action → State → UI → Action |
| Testability | State logic should be unit-testable without UI |

### Do's and Don'ts
| ✅ Do | ❌ Don't |
|-------|----------|
| Use `setState` for ephemeral state | Use `setState` for shared state |
| Split state by feature | Create one giant god-state |
| Use immutable state objects | Mutate state directly |
| Dispose controllers/subscriptions | Leave streams/timers open |
| `context.read` in callbacks | `context.watch` in callbacks |
| Use `Selector` for specific fields | Rebuild entire widget tree |
| Test business logic | Only test widgets |

### State Architecture Layers
```
UI (Widgets)
  ↓ dispatches events / calls methods
State Management (Provider/BLoC/Riverpod)
  ↓ calls
Repository (Data access)
  ↓ calls
API / Database / Cache
```

### Common Anti-Patterns
1. **God Object** — one provider/bloc for everything
2. **Prop Drilling** — passing state through every layer
3. **Business Logic in UI** — calculations in `build()`
4. **Not Disposing** — memory leaks from undisposed controllers
5. **Deep Widget Rebuilds** — using `context.watch` too high in tree

### Performance Tips
- Use `Selector` / `BlocSelector` to limit rebuilds
- Use `const` constructors for static widgets
- Use `child` parameter in `Consumer` for static subtrees
- Avoid `context.watch` in deeply nested widgets — use `Consumer`
- Profile with Flutter DevTools to find unnecessary rebuilds

---

## 🧪 Code Example

```dart
// ── 1. Immutable State (Equatable) ──
abstract class CartState extends Equatable {
  const CartState();
  @override List<Object> get props => [];
}

class CartLoaded extends CartState {
  final List<Item> items;
  const CartLoaded(this.items);
  @override List<Object> get props => [items];

  CartLoaded copyWith({List<Item>? items}) =>
    CartLoaded(items ?? this.items);
}

// ── 2. Feature-based state split ──
// ✅ Good — separate stores/blocs per feature
class AuthBloc extends Bloc<AuthEvent, AuthState> { ... }
class CartBloc extends Bloc<CartEvent, CartState> { ... }
class ProductBloc extends Bloc<ProductEvent, ProductState> { ... }

// ❌ Bad — one god-bloc for everything
class AppBloc extends Bloc<AppEvent, AppState> {
  // auth + cart + products + settings + ... — DON'T
}

// ── 3. Repository pattern ──
class CartRepository {
  final ApiClient _api;
  CartRepository(this._api);

  Future<List<Item>> getItems() => _api.getItems();
  Future<void> addItem(Item item) => _api.addItem(item);
}

// BLoC depends on repository, not API directly
class CartBloc extends Bloc<CartEvent, CartState> {
  final CartRepository _repo;
  CartBloc(this._repo) : super(CartInitial()) {
    on<LoadItems>(_onLoadItems);
  }

  Future<void> _onLoadItems(LoadItems event, Emitter<CartState> emit) async {
    emit(CartLoading());
    try {
      final items = await _repo.getItems();  // Repository, not API
      emit(CartLoaded(items));
    } catch (e) {
      emit(CartError(e.toString()));
    }
  }
}

// ── 4. Proper disposal ──
class MyScreen extends StatefulWidget {
  const MyScreen({super.key});
  @override State<MyScreen> createState() => _MyScreenState();
}

class _MyScreenState extends State<MyScreen> {
  late TextEditingController _controller;
  late ScrollController _scrollController;
  StreamSubscription? _subscription;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
    _scrollController = ScrollController();
    _subscription = someStream.listen((data) { ... });
  }

  @override
  void dispose() {
    _controller.dispose();      // ✅ Always dispose
    _scrollController.dispose();
    _subscription?.cancel();     // ✅ Cancel streams
    super.dispose();
  }
}

// ── 5. Selector for performance ──
// ✅ Good — rebuilds only when totalPrice changes
Selector<CartModel, double>(
  selector: (_, cart) => cart.totalPrice,
  builder: (_, total, __) => Text('\$$total'),
)

// ❌ Bad — rebuilds on ANY cart change
Consumer<CartModel>(
  builder: (_, cart, __) => Text('\$${cart.totalPrice}'),
)

// ── 6. Separate business logic from UI ──
// ✅ Good — logic in model/bloc
class DiscountCalculator {
  static double calculate(List<Item> items, String couponCode) {
    final subtotal = items.fold(0.0, (s, i) => s + i.price);
    final discount = couponCode == 'SAVE10' ? 0.1 : 0.0;
    return subtotal * (1 - discount);
  }
}

// UI just displays result
Text('Total: \$${DiscountCalculator.calculate(items, coupon)}')

// ❌ Bad — logic in build()
Text('Total: \$${items.fold(0.0, (s, i) => s + i.price) * (coupon == 'SAVE10' ? 0.9 : 1)}')

// ── 7. Loading/Error states ──
abstract class DataState<T> {}
class DataLoading<T> extends DataState<T> {}
class DataLoaded<T> extends DataState<T> { final T data; DataLoaded(this.data); }
class DataError<T> extends DataState<T> { final String message; DataError(this.message); }

// Always handle all three states in UI
Widget build(BuildContext context) {
  return switch (state) {
    DataLoading() => const CircularProgressIndicator(),
    DataLoaded(data: final d) => ContentView(d),
    DataError(message: final m) => ErrorView(m),
  };
}

// ── 8. Dependency injection for testability ──
// Use get_it or Provider for DI
final getIt = GetIt.instance;
void setupDI() {
  getIt.registerLazySingleton<ApiClient>(() => ApiClient());
  getIt.registerLazySingleton<CartRepository>(() => CartRepository(getIt()));
  getIt.registerFactory<CartBloc>(() => CartBloc(getIt()));
}

// In tests, easily override:
void setupTestDI() {
  getIt.registerSingleton<ApiClient>(MockApiClient());
  getIt.registerSingleton<CartRepository>(CartRepository(getIt()));
}
```

### Output
```
A Flutter app following state management best practices:
- Immutable state with Equatable and copyWith
- Feature-based state split (Auth, Cart, Product BLoCs)
- Repository pattern for data access
- Proper disposal of controllers and subscriptions
- Selector for granular rebuilds
- Business logic separated from UI
- Loading/Error/Data state handling
- Dependency injection for testability
```

---

## ❓ Interview Questions

1. **What are the most important state management best practices?**
   - (1) **Single source of truth** — state lives in one place, not duplicated across widgets. (2) **Immutability** — never modify state in place; create new state objects with `copyWith`. (3) **Separation of concerns** — business logic in models/blocs, UI only renders. (4) **Feature-based splitting** — one provider/bloc per feature, not one giant store. (5) **Proper disposal** — dispose controllers, cancel subscriptions, close blocs. (6) **Use Selector** — rebuild only affected widgets, not the entire tree. (7) **Repository pattern** — abstract data access behind repositories. (8) **Test business logic** — unit test state logic without UI. (9) **Handle all states** — loading, data, error in every async flow. (10) **Dependency injection** — inject dependencies for testability.

2. **Why should state be immutable?**
   - Immutability ensures: (1) **Predictability** — state can only change through defined actions/methods, never modified in place. (2) **Change detection** — compare references (`oldState != newState`) to detect changes efficiently. (3) **No side effects** — no code can accidentally modify shared state. (4) **Testability** — pure functions are easy to test. (5) **Debugging** — you can track every state transition. Use `copyWith()` to create new state: `state.copyWith(count: state.count + 1)`. Use `Equatable` for value equality. Never mutate lists in state — create a new list: `state.copyWith(items: [...state.items, newItem])` instead of `state.items.add(newItem)`.

3. **How do you avoid unnecessary widget rebuilds?**
   - (1) Use `Selector<Model, T>` (Provider) or `BlocSelector` (BLoC) to rebuild only when a specific field changes. (2) Use `const` constructors for static widgets. (3) Use `Consumer`'s `child` parameter for static subtrees. (4) Don't use `context.watch<T>()` high in the widget tree — push it down to the leaf widget that needs the state. (5) Split large widgets into smaller ones so rebuilds are scoped. (6) Use `AutomatedTestWidgetsFlutterBinding` to profile rebuilds. (7) Avoid creating new objects in `build()` — use `const` or cache. (8) Use `ListView.builder` instead of `Column` with many children. Profile with Flutter DevTools' "Track widget rebuilds" to find unnecessary rebuilds.

4. **What is the Repository pattern and why use it?**
   - The Repository pattern abstracts data access behind an interface. UI/Bloc calls `repository.getItems()`, not `apiClient.get('/items')`. Benefits: (1) **Single source of data** — repository decides whether to fetch from API, cache, or database. (2) **Testability** — mock the repository in tests, not the API client. (3) **Swappable backends** — switch from REST to GraphQL without changing UI/Bloc. (4) **Clean separation** — UI doesn't know about API details (endpoints, headers, parsing). (5) **Caching** — repository can cache responses and serve from cache. Example: `class UserRepository { Future<User> getUser(String id) { final cached = cache.get(id); if (cached != null) return cached; return api.fetchUser(id); } }`. Always use repository pattern for data access.

5. **How do you handle loading and error states?**
   - Define state classes for all possible states: `Loading`, `Loaded(data)`, `Error(message)`. In the UI, handle all three with `switch` or `if/else`. For BLoC: emit `LoadingState`, then `LoadedState` or `ErrorState`. For Provider: use `isLoading` flag + `error` string. For Riverpod: `AsyncValue.when(data:, loading:, error:)` handles this automatically. Always show a loading indicator during async operations. Show error messages with retry buttons. Use `try/catch` to catch errors and emit error states. Never leave the UI in an undefined state — always loading, data, or error. Consider adding `Empty` state for no data.

6. **What is dependency injection and why is it important?**
   - Dependency injection (DI) is providing dependencies (API clients, repositories, services) to objects that need them, rather than having objects create their own. Benefits: (1) **Testability** — inject mock dependencies in tests. (2) **Loose coupling** — classes don't know how dependencies are created. (3) **Single instance** — share one API client across all repositories. (4) **Configuration** — change dependencies in one place. Use `get_it` (service locator) or Provider/Riverpod for DI. Example: `final cartBloc = CartBloc(GetIt.instance<CartRepository>())`. In tests: `GetIt.instance.registerSingleton<CartRepository>(MockRepository())`. DI is essential for testable architecture — without it, classes create their own dependencies and can't be tested in isolation.

7. **How do you split state for a large app?**
   - Split state by feature, not by type. Create separate providers/blocs per feature: `AuthBloc`, `CartBloc`, `ProductBloc`, `SettingsBloc`. Each manages its own state independently. Provide at the appropriate scope: app-level state (auth, theme) at app root, feature-level state (cart, checkout) at the feature's root widget. Feature state is disposed when the feature is exited. Communicate between features via events or shared services, not direct references. Use a shared `AppState` only for truly global state (auth, theme, locale). This prevents god-objects and keeps each feature's state manageable. Test each feature's state independently. Example structure: `features/auth/auth_bloc.dart`, `features/cart/cart_bloc.dart`.

8. **What are common state management mistakes?**
   - (1) **Using `setState` for shared state** — leads to prop drilling. (2) **God provider/bloc** — everything in one state, unmaintainable. (3) **Not disposing** — memory leaks from undisposed controllers, streams, blocs. (4) **`context.watch` in callbacks** — does nothing, use `context.read`. (5) **Business logic in `build()`** — calculations, API calls in build method. (6) **Not handling error states** — app crashes on network failure. (7) **Rebuilding entire tree** — using `Consumer` instead of `Selector`. (8) **Mutating state directly** — `state.items.add(item)` instead of `state.copyWith(items: [...])`. (9) **Not testing business logic** — only testing widgets. (10) **Mixing multiple solutions** — Provider + BLoC + GetX in one app.

9. **How do you test state management?**
   - Test business logic separately from UI. For BLoC: `blocTest<Bloc, State>('description', build: () => Bloc(mockRepo), act: (b) => b.add(Event()), expect: () => [ExpectedState()])`. For Provider: create the model, call methods, verify state. For Riverpod: `ProviderContainer(overrides: [provider.overrideWithValue(mock)])`. Mock repositories with `mocktail`. Test all state transitions: initial → loading → loaded, initial → loading → error. Test edge cases: empty data, network failure, concurrent operations. Test side effects: verify navigation, snackbars were triggered. Keep tests fast — mock all external dependencies. Aim for 80%+ coverage on business logic. UI tests verify rendering, not logic.

10. **How do you persist state across app restarts?**
    - Persist only serializable state — not controllers or streams. Options: (1) `SharedPreferences` for simple key-value (theme, locale, auth token). (2) `Hive` or `Isar` for structured data (cart items, user profile). (3) `hydrated_bloc` for automatic BLoC state persistence — override `fromJson`/`toJson`. (4) `sqflite` for complex relational data. Persist on state changes: listen to state and save to storage. Restore on app start: read from storage and initialize state. Don't persist everything — only user preferences, auth, and critical data. For Provider: save `ChangeNotifier` state in `dispose()`, restore in constructor. For Riverpod: use a `Provider` that reads from storage in `build()`. Always handle the case where storage is empty (first launch).

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [Comparison](Comparison.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
