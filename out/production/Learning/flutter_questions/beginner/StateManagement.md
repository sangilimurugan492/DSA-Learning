# State Management

## Q1: What is state and what are the types of state?

```dart
// State — data that can change over time and affects the UI

// 1. Ephemeral (Local) State — only one widget needs it
class Counter extends StatefulWidget {
  const Counter({super.key});
  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int _count = 0;  // Ephemeral state — only this widget uses it

  @override
  Widget build(BuildContext context) {
    return Text('$_count');
  }
}

// 2. App (Shared) State — multiple widgets need it
// User auth, cart, theme, locale — shared across screens
// Needs state management solution (Provider, Riverpod, BLoC)
```

| Ephemeral State | App State |
|----------------|-----------|
| Single widget | Multiple widgets/screens |
| `setState()` | Provider, Riverpod, BLoC |
| Form validation | User session |
| Tab index | Shopping cart |
| Animation state | Theme/locale |

> **Rule:** Start with `setState()`. Only add a state management solution when state needs to be shared.

---

## Q2: How does `setState()` work?

```dart
class _CounterState extends State<Counter> {
  int _count = 0;

  void _increment() {
    setState(() {
      _count++;  // Modify state inside setState
    });
    // Framework marks this widget as dirty
    // Schedules a rebuild on next frame
    // build() is called again with new _count value
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $_count'),
        ElevatedButton(
          onPressed: _increment,
          child: const Text('Add'),
        ),
      ],
    );
  }
}
```

### setState() Rules
```dart
// ✅ Do:
setState(() {
  _count++;                    // Modify state
  _items.add(newItem);         // Update list
  _name = newName;             // Assign value
});

// ❌ Don't:
// 1. Don't call in build()
@override
Widget build(BuildContext context) {
  setState(() {});  // ❌ Infinite loop!
  return Container();
}

// 2. Don't call after async gap without checking mounted
Future<void> _loadData() async {
  final data = await fetchData();
  // ✅ Check if still in tree
  if (!mounted) return;
  setState(() {
    _data = data;
  });
}

// 3. Don't put async work inside setState
setState(() async {  // ❌ setState is synchronous
  _data = await fetchData();
});
// ✅ Fix:
final data = await fetchData();
setState(() => _data = data);
```

---

## Q3: How do you lift state up?

```dart
// Problem: Two sibling widgets need to share state
// Solution: Move state to common parent

// Parent holds state
class CartScreen extends StatefulWidget {
  const CartScreen({super.key});
  @override
  State<CartScreen> createState() => _CartScreenState();
}

class _CartScreenState extends State<CartScreen> {
  int _itemCount = 0;  // Shared state

  void _addItem() => setState(() => _itemCount++);
  void _removeItem() => setState(() => _itemCount--);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Child 1 — reads state
        CartBadge(count: _itemCount),
        // Child 2 — modifies state via callback
        CartControls(
          onAdd: _addItem,
          onRemove: _removeItem,
        ),
      ],
    );
  }
}

class CartBadge extends StatelessWidget {
  final int count;
  const CartBadge({super.key, required this.count});
  @override
  Widget build(BuildContext context) => Text('Items: $count');
}

class CartControls extends StatelessWidget {
  final VoidCallback onAdd;
  final VoidCallback onRemove;
  const CartControls({super.key, required this.onAdd, required this.onRemove});
  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        ElevatedButton(onPressed: onAdd, child: const Text('+')),
        ElevatedButton(onPressed: onRemove, child: const Text('-')),
      ],
    );
  }
}
```

---

## Q4: What is Provider and how do you use it?

```dart
// Provider — wrapper around InheritedWidget, simpler API

// 1. Define a model (ChangeNotifier)
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;

  void increment() {
    _count++;
    notifyListeners();  // Notify all listeners to rebuild
  }
}

// 2. Provide it at the top of the tree
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CounterModel(),
      child: const MyApp(),
    ),
  );
}

// 3. Consume it in widgets
class CounterWidget extends StatelessWidget {
  const CounterWidget({super.key});

  @override
  Widget build(BuildContext context) {
    // Consumer — rebuilds when model changes
    return Consumer<CounterModel>(
      builder: (context, model, child) {
        return Text('${model.count}');
      },
    );
  }
}

// Or use context.watch (rebuilds on change)
class CounterDisplay extends StatelessWidget {
  const CounterDisplay({super.key});
  @override
  Widget build(BuildContext context) {
    final count = context.watch<CounterModel>().count;  // Rebuilds
    return Text('$count');
  }
}

// Or use context.read (no rebuild, just access)
class IncrementButton extends StatelessWidget {
  const IncrementButton({super.key});
  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () => context.read<CounterModel>().increment(),  // No rebuild
      child: const Text('+'),
    );
  }
}
```

### Provider vs context.watch vs context.read
| Method | Rebuilds? | Use Case |
|--------|-----------|----------|
| `Consumer<T>` | ✅ Yes | Rebuild specific subtree |
| `context.watch<T>()` | ✅ Yes | Read + rebuild on change |
| `context.read<T>()` | ❌ No | Read once (in callbacks) |
| `Selector<T, S>` | ✅ On select | Rebuild only when selected value changes |

---

## Q5: What is the difference between Provider, Riverpod, BLoC, and GetX?

| Feature | Provider | Riverpod | BLoC | GetX |
|---------|----------|----------|------|------|
| Learning curve | Low | Medium | High | Low |
| Boilerplate | Low | Low | High | Very Low |
| Testability | Good | Excellent | Excellent | Medium |
| Compile-safe | ❌ | ✅ | ✅ | ❌ |
| Architecture | Opinionated | Flexible | Strict | Flexible |
| Popularity | High | Growing | High | Medium |

```dart
// Provider — simple, InheritedWidget wrapper
ChangeNotifierProvider(create: (_) => MyModel(), child: MyApp());

// Riverpod — compile-safe, no BuildContext needed
final counterProvider = StateNotifierProvider<CounterNotifier, int>(
  (ref) => CounterNotifier(),
);
// Widget: ref.watch(counterProvider)

// BLoC — streams, events → states
class CounterBloc extends Bloc<CounterEvent, int> {
  CounterBloc() : super(0) {
    on<Increment>((event, emit) => emit(state + 1));
  }
}
// Widget: BlocBuilder<CounterBloc, int>(builder: (ctx, count) => Text('$count'))

// GetX — minimal boilerplate
class CounterController extends GetxController {
  var count = 0.obs;
  void increment() => count.value++;
}
// Widget: Obx(() => Text('${controller.count}'))
```

> **Recommendation:** Start with Provider for small apps. Use Riverpod for new projects (compile-safe, testable). Use BLoC for large teams (strict architecture).

---

## Q6: What is `ChangeNotifier` and `ValueNotifier`?

```dart
// ChangeNotifier — observable model, notifies listeners on change
class UserModel extends ChangeNotifier {
  String _name = '';
  int _age = 0;

  String get name => _name;
  int get age => _age;

  void update(String name, int age) {
    _name = name;
    _age = age;
    notifyListeners();  // Notifies all listeners
  }
}

// ValueNotifier — ChangeNotifier for a single value
class CounterNotifier extends ValueNotifier<int> {
  CounterNotifier() : super(0);

  void increment() => value++;  // Auto-notifies listeners
}

// Usage with ValueListenableBuilder
final counter = CounterNotifier();

ValueListenableBuilder<int>(
  valueListenable: counter,
  builder: (context, value, child) {
    return Text('$value');  // Rebuilds only this widget
  },
)
```

| ChangeNotifier | ValueNotifier |
|----------------|---------------|
| Multiple fields | Single value |
| Manual `notifyListeners()` | Auto-notify on `value =` |
| Used with Provider | Used with ValueListenableBuilder |
| More flexible | Simpler, more efficient |

---

## Q7: When should you use `setState()` vs a state management solution?

```
Use setState() when:
  ✅ State is used by only one widget
  ✅ State doesn't need to survive configuration changes
  ✅ Simple form, toggle, animation
  ✅ Small app, prototype

Use Provider/Riverpod/BLoC when:
  ✅ State shared across multiple screens
  ✅ Auth state, user session
  ✅ Shopping cart, wishlist
  ✅ Theme, locale settings
  ✅ Data from API cached across screens
  ✅ Large app with team
```

```dart
// ✅ setState — local toggle
class ExpandableCard extends StatefulWidget {
  @override
  State<ExpandableCard> createState() => _ExpandableCardState();
}
class _ExpandableCardState extends State<ExpandableCard> {
  bool _expanded = false;  // Only this widget needs it
  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: () => setState(() => _expanded = !_expanded),
      title: Text(_expanded ? 'Expanded' : 'Collapsed'),
    );
  }
}

// ✅ Provider — shared auth state
class AuthModel extends ChangeNotifier {
  User? _user;
  User? get user => _user;
  bool get isLoggedIn => _user != null;

  Future<void> login(String email, String password) async {
    _user = await api.login(email, password);
    notifyListeners();  // All screens rebuild
  }
}
```

---

## Q8: What is `ValueListenableBuilder` and how does it differ from `Consumer`?

```dart
// ValueListenableBuilder — rebuilds only when a ValueNotifier changes
// Most granular rebuild — only the builder subtree updates

class CounterNotifier extends ValueNotifier<int> {
  CounterNotifier() : super(0);
  void increment() => value++;
}

final counter = CounterNotifier();

ValueListenableBuilder<int>(
  valueListenable: counter,
  builder: (context, value, child) {
    // Only this Text rebuilds when counter changes
    return Text('$value');
  },
  // child — static widget that doesn't rebuild
  child: const Icon(Icons.star),  // Passed to builder, never rebuilds
)

// vs Consumer (Provider) — rebuilds entire builder
Consumer<CounterModel>(
  builder: (context, model, child) {
    return Column(
      children: [
        Text('${model.count}'),       // Rebuilds
        const Icon(Icons.star),       // Also rebuilds (unless child)
      ],
    );
  },
)
```

| Feature | `ValueListenableBuilder` | `Consumer` |
|---------|--------------------------|-----------|
| Rebuild scope | Only builder subtree | Entire builder |
| State type | `ValueNotifier<T>` | `ChangeNotifier` |
| Granularity | ✅ Very granular | ⚠️ Broader |
| Boilerplate | Low | Low |
| Best for | Single value | Model with multiple fields |

> **Best Practice:** Use `ValueListenableBuilder` for single-value state (counter, toggle, loading flag). Use `Selector` or `Consumer` for model-based state.

---

## Q9: What is `ListenableBuilder` and when do you use it?

```dart
// ListenableBuilder — rebuilds when any Listenable changes
// More flexible than ValueListenableBuilder (works with any Listenable)

class AnimationController extends ChangeNotifier {
  double _progress = 0;
  double get progress => _progress;

  void tick() {
    _progress += 0.01;
    notifyListeners();
  }
}

ListenableBuilder(
  listenable: animationController,
  builder: (context, child) {
    return LinearProgressIndicator(value: animationController.progress);
  },
)

// Multiple listenables
ListenableBuilder(
  listenable: Listenable.merge([controller1, controller2]),
  builder: (context, child) {
    return Text('${controller1.value} + ${controller2.value}');
  },
)
```

> **Key:** `ListenableBuilder` is the general form — `ValueListenableBuilder` is a specialization for `ValueNotifier`. Use `Listenable.merge` when you need to rebuild on changes from multiple sources.

---

## Q10: How do you handle async state without a state management library?

```dart
// AsyncSnapshot pattern — handle loading/data/error in UI
class UserScreen extends StatefulWidget {
  const UserScreen({super.key});
  @override
  State<UserScreen> createState() => _UserScreenState();
}

class _UserScreenState extends State<UserScreen> {
  Future<User>? _userFuture;

  @override
  void initState() {
    super.initState();
    _userFuture = fetchUser();  // Start fetch on init
  }

  Future<User> fetchUser() async {
    final response = await http.get(Uri.parse('https://api.example.com/user/1'));
    return User.fromJson(jsonDecode(response.body));
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<User>(
      future: _userFuture,
      builder: (context, snapshot) {
        switch (snapshot.connectionState) {
          case ConnectionState.waiting:
            return const CircularProgressIndicator();
          case ConnectionState.done:
            if (snapshot.hasError) {
              return Text('Error: ${snapshot.error}');
            }
            return Text('Hello, ${snapshot.data!.name}');
          default:
            return const SizedBox.shrink();
        }
      },
    );
  }
}

// StreamBuilder — for real-time data
StreamBuilder<List<Message>>(
  stream: chatService.messages,
  builder: (context, snapshot) {
    if (snapshot.hasError) return Text('Error: ${snapshot.error}');
    if (!snapshot.hasData) return const CircularProgressIndicator();
    return ListView.builder(
      itemCount: snapshot.data!.length,
      itemBuilder: (context, index) => MessageTile(message: snapshot.data![index]),
    );
  },
)
```

| Widget | Data Source | Use Case |
|--------|-------------|----------|
| `FutureBuilder` | `Future<T>` | One-time async (API call) |
| `StreamBuilder` | `Stream<T>` | Real-time data (WebSocket, Firestore) |

> **Rule:** `FutureBuilder` is great for simple one-time fetches. For anything more complex (retry, refresh, caching), use a proper state management solution like Riverpod or BLoC.

---

## Q11: What is the difference between `Provider` and `Riverpod`?

```dart
// Provider — the original, uses ProviderScope at root
// Depends on BuildContext to access state

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => CounterModel()),
        Provider<AuthService>(create: (_) => AuthService()),
      ],
      child: const MyApp(),
    ),
  );
}

// Access — needs BuildContext
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final counter = context.watch<CounterModel>();  // Rebuilds on change
    final auth = context.read<AuthService>();  // One-time read
    return Text('${counter.count}');
  }
}

// Riverpod — the evolution, no BuildContext dependency
// Compile-safe, testable, no ProviderNotFoundException

void main() {
  runApp(const ProviderScope(child: MyApp()));
}

// Define providers
final counterProvider = ChangeNotifierProvider((ref) => CounterModel());
final authProvider = Provider<AuthService>((ref) => AuthService());

// Access — no BuildContext needed
class MyWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final counter = ref.watch(counterProvider);  // Rebuilds on change
    final auth = ref.read(authProvider);  // One-time read
    return Text('${counter.count}');
  }
}
```

| Feature | Provider | Riverpod |
|---------|----------|----------|
| Context dependency | Yes (needs BuildContext) | No (uses ref) |
| Compile safety | Runtime errors (ProviderNotFound) | Compile-time safe |
| Testing | Harder (needs context) | Easy (ProviderContainer) |
| Auto-dispose | Manual | `autoDispose` modifier |
| Async support | Limited | First-class (FutureProvider, StreamProvider) |
| Learning curve | Easier | Steeper |

> **Best Practice:** For new projects, use Riverpod. It solves Provider's limitations (context dependency, runtime errors, testing difficulty). For simple apps, Provider is still a solid choice.

---

## Q12: What is `ChangeNotifier` and how does it work with `setState`?

```dart
// ChangeNotifier — observable class that notifies listeners on change
// Alternative to setState for shared state

class CartModel extends ChangeNotifier {
  final List<Item> _items = [];

  List<Item> get items => List.unmodifiable(_items);
  int get totalPrice => _items.fold(0, (sum, item) => sum + item.price);
  int get itemCount => _items.length;

  void addItem(Item item) {
    _items.add(item);
    notifyListeners();  // Notifies all listeners → UI rebuilds
  }

  void removeItem(Item item) {
    _items.remove(item);
    notifyListeners();
  }

  void clear() {
    _items.clear();
    notifyListeners();
  }
}

// Usage with Provider
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CartModel(),
      child: const MyApp(),
    ),
  );
}

// Widget rebuilds when notifyListeners() is called
class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final cart = context.watch<CartModel>();
    return Column(
      children: [
        Text('Items: ${cart.itemCount}'),
        Text('Total: \$${cart.totalPrice}'),
        ElevatedButton(
          onPressed: () => cart.addItem(Item('New', 10)),
          child: const Text('Add'),
        ),
      ],
    );
  }
}
```

| Feature | `setState` | `ChangeNotifier` |
|---------|-----------|-----------------|
| Scope | Single widget | Multiple widgets |
| Notification | Automatic (marks dirty) | Manual (`notifyListeners()`) |
| Use case | Local ephemeral state | Shared app state |
| Disposal | Automatic | Must call `dispose()` |
| Performance | Rebuilds entire widget | Only listeners rebuild |

> **Key:** Call `notifyListeners()` only when state actually changes — not on every method call. Over-calling causes unnecessary rebuilds. Always `dispose()` ChangeNotifier to prevent memory leaks.

---

## Q13: What is the BLoC pattern and when should you use it?

```dart
// BLoC (Business Logic Component) — separates UI from business logic
// Uses Streams: Events → Bloc → States

// 1. Define Events
abstract class CounterEvent {}
class IncrementEvent extends CounterEvent {}
class DecrementEvent extends CounterEvent {}
class ResetEvent extends CounterEvent {}

// 2. Define States
class CounterState {
  final int count;
  const CounterState(this.count);
}

// 3. Create Bloc
class CounterBloc extends Bloc<CounterEvent, CounterState> {
  CounterBloc() : super(const CounterState(0)) {
    on<IncrementEvent>((event, emit) => emit(CounterState(state.count + 1)));
    on<DecrementEvent>((event, emit) => emit(CounterState(state.count - 1)));
    on<ResetEvent>((event, emit) => emit(const CounterState(0)));
  }
}

// 4. Provide Bloc
void main() {
  runApp(
    BlocProvider(
      create: (_) => CounterBloc(),
      child: const MyApp(),
    ),
  );
}

// 5. Consume in UI
class CounterScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocBuilder<CounterBloc, CounterState>(
      builder: (context, state) {
        return Column(
          children: [
            Text('Count: ${state.count}'),
            ElevatedButton(
              onPressed: () => context.read<CounterBloc>().add(IncrementEvent()),
              child: const Text('Increment'),
            ),
          ],
        );
      },
    );
  }
}
```

| BLoC Concept | Description |
|--------------|-------------|
| Event | User intent (button tap, load data) |
| State | UI representation at a point in time |
| Bloc | Transforms Events → States |
| BlocBuilder | Rebuilds UI on state change |
| BlocProvider | Makes bloc available to widget tree |
| BlocListener | Side effects on state change (navigation, snackbar) |

> **When to use BLoC:** Large apps with complex business logic, teams needing strict separation of concerns, or apps requiring testable, predictable state. For simpler apps, Provider or Riverpod is more appropriate. BLoC has more boilerplate but provides the best testability and structure.

---

## Q14: What is `Selector` and how does it improve performance?

```dart
// Selector — rebuilds only when a specific part of state changes
// More granular than context.watch which rebuilds on ANY change

class User {
  final String name;
  final int age;
  final String email;
  const User(this.name, this.age, this.email);
}

class UserModel extends ChangeNotifier {
  User _user = const User('Alice', 30, 'alice@test.com');
  User get user => _user;

  void updateName(String name) {
    _user = User(name, _user.age, _user.email);
    notifyListeners();
  }

  void updateAge(int age) {
    _user = User(_user.name, age, _user.email);
    notifyListeners();
  }
}

// ❌ context.watch — rebuilds on ANY change (name OR age OR email)
class NameWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final user = context.watch<UserModel>().user;  // Rebuilds on age change too!
    return Text(user.name);
  }
}

// ✅ Selector — rebuilds ONLY when name changes
class NameWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Selector<UserModel, String>(
      selector: (_, model) => model.user.name,  // Select only name
      builder: (context, name, child) {
        return Text(name);  // Only rebuilds when name changes
      },
    );
  }
}

// With Riverpod — select() method
class NameWidget extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final name = ref.watch(userProvider.select((user) => user.name));
    return Text(name);  // Only rebuilds when name changes
  }
}
```

> **Best Practice:** Use `Selector` (Provider) or `.select()` (Riverpod) when a model has multiple fields but a widget only needs one. This prevents unnecessary rebuilds when unrelated fields change. For simple models, `context.watch` is fine.

---

## Q15: How do you handle async state with `FutureProvider` and `StreamProvider`?

```dart
// FutureProvider — async one-time data fetch
final weatherProvider = FutureProvider<Weather>((ref) async {
  final api = ref.read(apiProvider);
  return api.getWeather();  // Returns Future<Weather>
});

// Usage
class WeatherScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final weatherAsync = ref.watch(weatherProvider);

    return weatherAsync.when(
      data: (weather) => Text('Temp: ${weather.temp}°C'),
      loading: () => const CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  }
}

// StreamProvider — real-time data
final messagesProvider = StreamProvider<List<Message>>((ref) {
  return ref.read(chatService).messages;  // Returns Stream<List<Message>>
});

// Usage
class ChatScreen extends ConsumerWidget {
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final messagesAsync = ref.watch(messagesProvider);

    return messagesAsync.when(
      data: (messages) => ListView.builder(
        itemCount: messages.length,
        itemBuilder: (context, index) => MessageTile(messages[index]),
      ),
      loading: () => const CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  }
}

// Auto-refresh on dependency change
final userPostsProvider = FutureProvider<List<Post>>((ref) async {
  final userId = ref.watch(currentUserIdProvider);  // Re-fetches when userId changes
  return api.getPosts(userId);
});
```

| Provider Type | Data Source | Use Case |
|---------------|-------------|----------|
| `Provider` | Sync value | Singletons, services |
| `FutureProvider` | `Future<T>` | API calls, one-time fetch |
| `StreamProvider` | `Stream<T>` | WebSocket, Firestore, real-time |
| `ChangeNotifierProvider` | `ChangeNotifier` | Mutable state with listeners |
| `StateNotifierProvider` | `StateNotifier` | Immutable state (Riverpod) |

> **Key:** `FutureProvider` and `StreamProvider` handle loading/error states automatically via `.when()`. They auto-cancel when the widget is disposed. Use `autoDispose` modifier to clean up when no longer listened: `FutureProvider.autoDispose(...)`.

---

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [Navigation](Navigation.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
