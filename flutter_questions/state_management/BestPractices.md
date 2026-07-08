# Best Practices

## Q1: What are common state management anti-patterns?

### 1. Using setState for shared state
```dart
// ❌ Bad — shared state in setState (prop drilling)
class Parent extends StatefulWidget {
  @override
  State<Parent> createState() => _ParentState();
}
class _ParentState extends State<Parent> {
  String _user = '';  // Shared across 5 screens
  Widget build(BuildContext context) => ChildA(user: _user, onUserChange: (u) => setState(() => _user = u));
}

// ✅ Good — use Provider/Riverpod for shared state
class UserModel extends ChangeNotifier {
  String _user = '';
  String get user => _user;
  void setUser(String u) { _user = u; notifyListeners(); }
}
```

### 2. Calling setState in build()
```dart
// ❌ Bad — infinite rebuild loop
@override
Widget build(BuildContext context) {
  setState(() => _count++);  // Triggers rebuild → build() → setState → ...
  return Text('$_count');
}

// ✅ Good — setState only in event handlers
void _onPressed() {
  setState(() => _count++);
}
```

### 3. Not checking mounted after async
```dart
// ❌ Bad — crash after dispose
Future<void> _load() async {
  final data = await api.fetch();
  setState(() => _data = data);  // Crash if widget disposed
}

// ✅ Good — check mounted
Future<void> _load() async {
  final data = await api.fetch();
  if (!mounted) return;
  setState(() => _data = data);
}
```

### 4. Over-using state management
```dart
// ❌ Bad — BLoC for a simple toggle
class ToggleBloc extends Bloc<ToggleEvent, bool> {
  ToggleBloc() : super(false) {
    on<Toggle>((event, emit) => emit(!state));
  }
}

// ✅ Good — setState for ephemeral state
class _ToggleState extends State<Toggle> {
  bool _isOn = false;
  @override
  Widget build(BuildContext context) => Switch(
    value: _isOn,
    onChanged: (v) => setState(() => _isOn = v),
  );
}
```

### 5. Putting business logic in widgets
```dart
// ❌ Bad — logic in widget
class CartScreen extends StatefulWidget {
  @override
  Widget build(BuildContext context) {
    final total = items.fold(0, (sum, item) => sum + item.price * item.quantity);
    final tax = total * 0.08;
    final shipping = total > 100 ? 0 : 10;
    final grandTotal = total + tax + shipping;
    return Text('$grandTotal');
  }
}

// ✅ Good — logic in model/notifier
class CartModel extends ChangeNotifier {
  double get total => items.fold(0, (sum, item) => sum + item.price * item.quantity);
  double get tax => total * 0.08;
  double get shipping => total > 100 ? 0 : 10;
  double get grandTotal => total + tax + shipping;
}
```

---

## Q2: How do you minimize unnecessary rebuilds?

```dart
// 1. Use const constructors
const Text('Hello')  // Never rebuilds

// 2. Extract widgets to limit rebuild scope
class CounterScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Header(),  // const — never rebuilds
        CounterDisplay(),  // Only this rebuilds
        const Footer(),  // const — never rebuilds
      ],
    );
  }
}

// 3. Use Selector instead of Consumer (Provider)
Selector<CartModel, int>(
  selector: (_, cart) => cart.itemCount,  // Only rebuild when itemCount changes
  builder: (_, count, __) => Text('$count'),
)

// 4. Use select with Riverpod
final count = ref.watch(counterProvider.select((c) => c));

// 5. Use buildWhen with BLoC
BlocBuilder<CartBloc, CartState>(
  buildWhen: (prev, curr) => prev.itemCount != curr.itemCount,
  builder: (_, state) => Text('${state.itemCount}'),
)

// 6. Use child parameter for static widgets
Consumer<CartModel>(
  builder: (context, cart, child) {
    return Column(
      children: [
        child!,  // Static — not rebuilt
        Text('${cart.total}'),
      ],
    );
  },
  child: const ExpensiveWidget(),  // Built once
)
```

### Rebuild Optimization Checklist
```
✅ const constructors for static widgets
✅ Extract stateful logic to separate widgets
✅ Selector/select for specific fields
✅ buildWhen for BLoC
✅ child parameter for static subtrees
✅ Consumer/Observer to limit rebuild scope
✅ DevTools "Track widget rebuilds" to find issues
```

---

## Q3: How do you structure state in a large app?

```
lib/
├── core/
│   └── theme/
├── features/
│   ├── auth/
│   │   ├── data/
│   │   │   ├── auth_repository.dart
│   │   │   └── auth_repository_impl.dart
│   │   ├── domain/
│   │   │   └── entities/
│   │   └── presentation/
│   │       ├── auth_notifier.dart  (or auth_bloc.dart)
│   │       ├── auth_state.dart
│   │       └── screens/
│   │           ├── login_screen.dart
│   │           └── register_screen.dart
│   ├── product/
│   │   ├── data/
│   │   ├── domain/
│   │   └── presentation/
│   │       ├── product_notifier.dart
│   │       └── screens/
│   └── cart/
│       └── presentation/
│           └── cart_notifier.dart
└── main.dart
```

### Principles
1. **Feature-first structure** — group by feature, not by layer
2. **One notifier/bloc per feature** — don't create global mega-state
3. **Repositories for data** — notifiers call repositories, not APIs directly
4. **Separate state from UI** — state classes are pure Dart
5. **Dependency injection** — inject repositories into notifiers/blocs

---

## Q4: How do you handle loading and error states?

```dart
// ❌ Bad — separate booleans
class BadModel extends ChangeNotifier {
  bool isLoading = false;
  bool hasError = false;
  String? error;
  List<Item>? data;
  // Hard to manage all combinations
}

// ✅ Good 1 — sealed state classes
sealed class UiState<T> {}
class Loading<T> extends UiState<T> {}
class Success<T> extends UiState<T> {
  final T data;
  Success(this.data);
}
class Error<T> extends UiState<T> {
  final String message;
  Error(this.message);
}

// Usage
class ProductNotifier extends Notifier<UiState<List<Product>>> {
  @override
  UiState<List<Product>> build() => Loading();

  Future<void> load() async {
    state = Loading();
    try {
      final products = await repository.getProducts();
      state = Success(products);
    } catch (e) {
      state = Error(e.toString());
    }
  }
}

// Widget
switch (state) {
  case Loading(): return CircularProgressIndicator();
  case Success(:final data): return ProductList(products: data);
  case Error(:final message): return ErrorWidget(message: message);
}

// ✅ Good 2 — AsyncValue (Riverpod)
final productProvider = FutureProvider<List<Product>>((ref) async {
  return repository.getProducts();
});

// Auto-handles loading/data/error
ref.watch(productProvider).when(
  data: (products) => ProductList(products: products),
  loading: () => CircularProgressIndicator(),
  error: (e, _) => ErrorWidget(message: e.toString()),
);
```

---

## Q5: How do you test state management?

```dart
// 1. Unit test the model/notifier (no widgets)
test('CartModel add item increases count', () {
  final cart = CartModel();
  expect(cart.itemCount, 0);

  cart.add(Product(id: '1', price: 10));
  expect(cart.itemCount, 1);
  expect(cart.total, 10);
});

// 2. Test with mocked dependencies
test('AuthNotifier login success', () async {
  final mockRepo = MockAuthRepository();
  when(mockRepo.login('email', 'pass'))
      .thenAnswer((_) async => User('Alice'));

  final notifier = AuthNotifier(mockRepo);
  await notifier.login('email', 'pass');

  expect(notifier.state.user?.name, 'Alice');
  expect(notifier.state.isLoggedIn, true);
});

// 3. Widget test with provider override
testWidgets('Counter displays value', (tester) async {
  await tester.pumpWidget(
    ProviderScope(
      overrides: [
        counterProvider.overrideWith(() => MockCounterNotifier()),
      ],
      child: const MaterialApp(home: CounterScreen()),
    ),
  );

  expect(find.text('42'), findsOneWidget);
});

// 4. BLoC test
blocTest<AuthBloc, AuthState>(
  'emits [loading, authenticated] on login',
  build: () => AuthBloc(MockAuthRepository()),
  act: (bloc) => bloc.add(LoginRequested('email', 'pass')),
  expect: () => [isA<AuthLoading>(), isA<Authenticated>()],
);
```

### Testing Checklist
```
✅ Unit test models/notifiers (pure logic)
✅ Mock repositories (don't hit real API)
✅ Test state transitions (initial → loading → success/error)
✅ Test edge cases (empty data, network error)
✅ Widget test with overridden providers
✅ Test side effects (navigation, snackbar)
✅ Integration test for user flows
```

---

## Q6: How do you handle persistence (save state across sessions)?

```dart
// 1. Persist with SharedPreferences
class ThemeNotifier extends Notifier<ThemeMode> {
  @override
  ThemeMode build() {
    _loadTheme();  // Load on init
    return ThemeMode.system;
  }

  Future<void> _loadTheme() async {
    final prefs = await SharedPreferences.getInstance();
    final isDark = prefs.getBool('isDark') ?? false;
    state = isDark ? ThemeMode.dark : ThemeMode.light;
  }

  Future<void> toggle() async {
    state = state == ThemeMode.dark ? ThemeMode.light : ThemeMode.dark;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isDark', state == ThemeMode.dark);
  }
}

// 2. Hydrated BLoC (auto-persist)
// pubspec.yaml: hydrated_bloc: ^9.1.0
class ThemeBloc extends HydratedBloc<ThemeEvent, ThemeMode> {
  ThemeBloc() : super(ThemeMode.system) {
    on<ToggleTheme>((event, emit) {
      emit(state == ThemeMode.dark ? ThemeMode.light : ThemeMode.dark);
    });
  }

  @override
  ThemeMode? fromJson(Map<String, dynamic> json) {
    return ThemeMode.values[json['mode'] as int];
  }

  @override
  Map<String, dynamic>? toJson(ThemeMode state) {
    return {'mode': state.index};
  }
}

// 3. Riverpod with shared preferences
final themeProvider = NotifierProvider<ThemeNotifier, ThemeMode>(ThemeNotifier.new);

// 4. Persist cart with Hive
// pubspec.yaml: hive: ^2.2.0
@HiveType(typeId: 0)
class CartItem extends HiveObject {
  @HiveField(0) final String id;
  @HiveField(1) final double price;
  CartItem({required this.id, required this.price});
}
```

---

## Q7: How do you debug state management?

```dart
// 1. Riverpod — DevTools
// Open DevTools → Provider tab
// See all providers, their state, and dependencies

// 2. BLoC — BlocObserver
class MyBlocObserver extends BlocObserver {
  @override
  void onEvent(Bloc bloc, Object? event) {
    print('Event: $event on ${bloc.runtimeType}');
    super.onEvent(bloc, event);
  }

  @override
  void onChange(BlocBase bloc, Change change) {
    print('State: ${change.currentState} → ${change.nextState}');
    super.onChange(bloc, change);
  }

  @override
  void onError(BlocBase bloc, Object error, StackTrace stackTrace) {
    print('Error: $error');
    super.onError(bloc, error, stackTrace);
  }
}

void main() {
  Bloc.observer = MyBlocObserver();
  runApp(const MyApp());
}

// 3. Provider — debugPrint
class CartModel extends ChangeNotifier {
  void add(Item item) {
    _items.add(item);
    debugPrint('Cart: added ${item.id}, total: ${_items.length}');
    notifyListeners();
  }
}

// 4. Track rebuilds
// DevTools → Flutter Inspector → "Track widget rebuilds"
// Widgets that rebuild frequently are highlighted

// 5. Riverpod — ref.listen for debugging
ref.listen<int>(counterProvider, (previous, next) {
  debugPrint('Counter: $previous → $next');
});
```

---

## Q8: What are the golden rules of state management?

```
1. Start simple — setState first, add complexity only when needed
2. Separate concerns — UI, state, data (repository) in different layers
3. Immutable state — never mutate state directly, always create new state
4. Single source of truth — one state per piece of data
5. Unidirectional flow — action → state → UI (never reverse)
6. Test everything — unit test state logic, widget test UI
7. Dispose resources — cancel streams, timers, dispose controllers
8. Minimize rebuilds — use Selector/select, const, extract widgets
9. Handle all states — loading, success, error, empty
10. Don't over-engineer — use the simplest solution that works
```

```dart
// ✅ The golden checklist
// [ ] State is immutable (copyWith, new list)
// [ ] No business logic in widgets
// [ ] All async operations check mounted
// [ ] All controllers are disposed
// [ ] Loading and error states handled
// [ ] State is testable without widgets
// [ ] Rebuilds are minimized (Selector/select)
// [ ] No global mutable state (use DI)
// [ ] State persists across sessions (if needed)
// [ ] Side effects are in listeners/middleware (not in build)
```

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [Comparison](Comparison.md)
- [Testing](../intermediate/Testing.md)
