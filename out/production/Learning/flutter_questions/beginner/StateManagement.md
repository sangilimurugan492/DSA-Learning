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

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [Navigation](Navigation.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
