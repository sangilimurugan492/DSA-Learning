# State Management Fundamentals

## Q1: What is state?

State is any data that can change over time and affects the UI.

```dart
// Examples of state:
int counter = 0;           // Counter value
String username = '';      // User input
List<Item> cart = [];      // Shopping cart
bool isLoading = false;    // Loading indicator
User? currentUser;         // Auth state
ThemeMode theme = ThemeMode.system;  // Theme
```

### Two Types of State

| Ephemeral (Local) State | App (Shared) State |
|------------------------|---------------------|
| Single widget needs it | Multiple widgets/screens need it |
| `setState()` is enough | Needs Provider/Riverpod/BLoC |
| Form validation, toggle | Auth, cart, theme, locale |
| Tab index, animation | User session, settings |

```dart
// Ephemeral — only this widget cares
class _ToggleState extends State<Toggle> {
  bool _expanded = false;  // Local state

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: _expanded,
      onChanged: (v) => setState(() => _expanded = v),
    );
  }
}

// App state — shared across screens
class AuthModel extends ChangeNotifier {
  User? _user;  // Shared state
  bool get isLoggedIn => _user != null;
}
```

---

## Q2: How does setState() work internally?

```dart
class _CounterState extends State<Counter> {
  int _count = 0;

  void _increment() {
    setState(() {
      _count++;  // 1. Modify state
    });
    // 2. Framework marks this Element as dirty
    // 3. Schedules rebuild on next frame
    // 4. build() is called again
    // 5. New widget tree is diffed against old
    // 6. Only changed widgets are repainted
  }

  @override
  Widget build(BuildContext context) {
    return Text('$_count');  // Rebuilt with new value
  }
}
```

### setState() Flow
```
setState(() => _count++)
  ↓
Element.markNeedsBuild()  ← Marks element dirty
  ↓
Scheduler schedules frame  ← Next vsync
  ↓
Element.rebuild()  ← Calls build()
  ↓
New Widget tree created
  ↓
Diff old vs new (canUpdate)
  ↓
RenderObject.update()  ← Only changed parts
  ↓
Repaint
```

### setState() Rules
```dart
// ✅ Do:
setState(() {
  _count++;
  _items.add(item);
  _name = newName;
});

// ❌ Don't call in build():
@override
Widget build(BuildContext context) {
  setState(() {});  // Infinite loop!
  return Container();
}

// ❌ Don't call after async without mounted check:
Future<void> _load() async {
  final data = await fetchData();
  if (!mounted) return;  // Widget might be disposed
  setState(() => _data = data);
}

// ❌ Don't put async work inside setState:
setState(() async {  // setState is synchronous
  _data = await fetchData();
});
// ✅ Fix:
final data = await fetchData();
setState(() => _data = data);
```

---

## Q3: What is "lifting state up"?

When two sibling widgets need the same state, move it to their common parent.

```dart
// Problem: Siblings need shared state
//    Parent
//    /    \
//  A       B    ← A and B need the same data

// Solution: State lives in parent, passed down via constructor + callbacks
class Parent extends StatefulWidget {
  @override
  State<Parent> createState() => _ParentState();
}

class _ParentState extends State<Parent> {
  String _text = '';

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        WidgetA(
          text: _text,           // Pass state down
          onChanged: (v) => setState(() => _text = v),  // Callback up
        ),
        WidgetB(text: _text),    // Read state
      ],
    );
  }
}

class WidgetA extends StatelessWidget {
  final String text;
  final ValueChanged<String> onChanged;
  const WidgetA({super.key, required this.text, required this.onChanged});

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: TextEditingController(text: text),
      onChanged: onChanged,  // Notify parent
    );
  }
}

class WidgetB extends StatelessWidget {
  final String text;
  const WidgetB({super.key, required this.text});

  @override
  Widget build(BuildContext context) => Text('You typed: $text');
}
```

> **Problem with lifting:** For deeply nested widgets, you get "prop drilling" — passing data through many layers. This is where Provider/Riverpod/BLoC help.

---

## Q4: What is InheritedWidget?

`InheritedWidget` is Flutter's built-in mechanism for sharing state down the tree without prop drilling.

```dart
// 1. Create InheritedWidget
class CounterInherited extends InheritedWidget {
  final int count;
  final VoidCallback onIncrement;

  const CounterInherited({
    super.key,
    required this.count,
    required this.onIncrement,
    required super.child,
  });

  // Fast lookup — O(1)
  static CounterInherited of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<CounterInherited>()!;
  }

  @override
  bool updateShouldNotify(CounterInherited oldWidget) {
    return count != oldWidget.count;  // Rebuild dependents only if changed
  }
}

// 2. Provide at top of tree
class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return CounterInherited(
      count: 42,
      onIncrement: () => print('increment'),
      child: const MaterialApp(home: HomeScreen()),
    );
  }
}

// 3. Consume anywhere in subtree
class DeepWidget extends StatelessWidget {
  const DeepWidget({super.key});
  @override
  Widget build(BuildContext context) {
    final inherited = CounterInherited.of(context);
    return Text('${inherited.count}');  // No prop drilling!
  }
}
```

### InheritedWidget vs Provider
| InheritedWidget | Provider |
|-----------------|----------|
| Manual boilerplate | Less boilerplate |
| No built-in state mutation | ChangeNotifier handles updates |
| `updateShouldNotify` manual | Automatic |
| No `context.read` | `context.read` + `context.watch` |
| Raw Flutter | Package (wrapper) |

> Provider is just a wrapper around InheritedWidget with better API.

---

## Q5: What is the difference between declarative and imperative UI?

```
Imperative (Android/iOS traditional):
  1. Find view: button = findViewById(R.id.button)
  2. Update: button.setText("Clicked")
  3. Framework updates the view

Declarative (Flutter):
  1. State changes: _count++
  2. Framework calls build()
  3. build() returns NEW widget tree
  4. Framework diffs old vs new
  5. Only changed parts update
```

```dart
// Declarative — UI is a function of state
@override
Widget build(BuildContext context) {
  // This function describes WHAT the UI looks like for current state
  // Not HOW to change it
  return Text('Count: $_count');
}

// You never say "update the text"
// You change state → build() returns new description → framework updates
```

### Why Declarative?
- **No bugs from missed updates** — UI always matches state
- **Easier to reason about** — UI = f(state)
- **No `findViewById` or `setText`** — just change state and rebuild
- **Framework handles diffing** — only updates what changed

---

## Q6: What is unidirectional data flow?

```
User Action → State Change → UI Rebuild → (wait for next action)

    ┌──────────┐
    │   User    │
    │  Action   │
    └────┬──────┘
         ↓
    ┌──────────┐
    │   State   │
    │  Change   │
    └────┬──────┘
         ↓
    ┌──────────┐
    │    UI     │
    │  Rebuild  │
    └────┬──────┘
         │
         └──→ (user sees new UI, takes next action)
```

```dart
// setState — simplest unidirectional flow
void _increment() {
  setState(() {
    _count++;  // State change
  });
  // → build() called → UI rebuilds
}

// BLoC — explicit unidirectional
// Event → BLoC → State → UI
context.read<CounterBloc>().add(Increment());
// Bloc processes event → emits new state → UI rebuilds

// Riverpod — unidirectional
ref.read(counterProvider.notifier).increment();
// Notifier updates state → UI rebuilds
```

### Why Unidirectional?
- **Predictable** — data flows one way, easier to trace
- **Debuggable** — state changes are traceable
- **No cascading updates** — no widget directly modifies another widget
- **Testable** — test state changes in isolation

---

## Q7: What is the difference between Ephemeral and App state?

```
Ephemeral State                    App State
─────────────                      ─────────
Only one widget needs it           Multiple widgets need it
setState() is enough               Needs Provider/Riverpod/BLoC
Lost when widget is disposed       Persists across screens
No external package needed         External state management

Examples:                          Examples:
- Form input                       - User auth session
- Tab index                        - Shopping cart
- Expand/collapse toggle           - Theme/dark mode
- Animation progress               - Locale/language
- Bottom sheet open/close          - API cached data
```

```dart
// Ephemeral — setState is correct
class ExpandableTile extends StatefulWidget {
  @override
  State<ExpandableTile> createState() => _ExpandableTileState();
}

class _ExpandableTileState extends State<ExpandableTile> {
  bool _expanded = false;  // Only this widget cares

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: () => setState(() => _expanded = !_expanded),
      title: Text(_expanded ? 'Hide' : 'Show'),
    );
  }
}

// App state — needs Provider
class CartModel extends ChangeNotifier {
  final List<Item> _items = [];  // Multiple screens need this
  List<Item> get items => List.unmodifiable(_items);

  void add(Item item) {
    _items.add(item);
    notifyListeners();  // All screens rebuild
  }
}
```

> **Rule of thumb:** Start with `setState()`. Only add a state management solution when state needs to be shared across widgets/screens.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Riverpod](Riverpod.md)
- [Comparison](Comparison.md)
