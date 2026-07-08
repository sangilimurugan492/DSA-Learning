# Widgets

## Q1: What is the difference between StatelessWidget and StatefulWidget?

```dart
// StatelessWidget — immutable, no internal state
// Rebuilds only when parent rebuilds or input changes
class GreetingCard extends StatelessWidget {
  final String name;
  const GreetingCard({super.key, required this.name});

  @override
  Widget build(BuildContext context) {
    return Text('Hello, $name!');
  }
}

// StatefulWidget — has mutable state, can rebuild independently
class Counter extends StatefulWidget {
  const Counter({super.key});
  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int count = 0;  // Mutable state

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $count'),
        ElevatedButton(
          onPressed: () => setState(() => count++),  // Triggers rebuild
          child: const Text('Increment'),
        ),
      ],
    );
  }
}
```

| StatelessWidget | StatefulWidget |
|----------------|-----------------|
| Immutable | Has mutable state |
| No `setState()` | Uses `setState()` to rebuild |
| Rebuilds when parent rebuilds | Can rebuild independently |
| Lighter, faster | Heavier (creates State object) |
| `Text`, `Icon`, `Image` | `Checkbox`, `TextField`, `Animation` |

> **Rule:** Use StatelessWidget by default. Only use StatefulWidget when the widget needs to manage its own mutable state.

---

## Q2: What is the widget lifecycle?

```
StatefulWidget Lifecycle:

createState()           → Creates State object
  ↓
initState()             → Called once — initialize state, start listeners
  ↓
didChangeDependencies() → Called after initState, InheritedWidget changes
  ↓
build()                 → Builds widget tree (called on every rebuild)
  ↓
didUpdateWidget()       → Parent rebuilds with new configuration
  ↓
setState()              → Marks dirty → triggers build()
  ↓
deactivate()           → Removed from tree (may be reinserted)
  ↓
dispose()              → Called once — cleanup controllers, listeners
```

```dart
class _MyWidgetState extends State<MyWidget> with TickerProviderStateMixin {
  late AnimationController controller;

  @override
  void initState() {
    super.initState();
    // Called ONCE — initialize state, controllers, start listeners
    controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 1),
    )..forward();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Called after initState and when InheritedWidget changes
    final theme = Theme.of(context);
  }

  @override
  void didUpdateWidget(MyWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    // Parent rebuilt with new widget — compare oldWidget vs widget
    if (oldWidget.color != widget.color) {
      // React to property change
    }
  }

  @override
  void dispose() {
    // Called ONCE — cleanup
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
```

---

## Q3: What are the most common Flutter widgets?

```dart
// Display widgets
const Text('Hello')                    // Text
const Icon(Icons.star)                 // Icon
Image.network('url')                   // Image from network
Image.asset('assets/logo.png')         // Image from assets
const CircleAvatar(radius: 20)         // Circular avatar

// Button widgets
ElevatedButton(onPressed: () {}, child: const Text('Elevated'))
TextButton(onPressed: () {}, child: const Text('Text'))
OutlinedButton(onPressed: () {}, child: const Text('Outlined'))
IconButton(onPressed: () {}, icon: const Icon(Icons.menu))
FloatingActionButton(onPressed: () {}, child: const Icon(Icons.add))

// Input widgets
TextField(decoration: InputDecoration(labelText: 'Name'))
TextFormField(validator: (v) => v!.isEmpty ? 'Required' : null)
Checkbox(value: true, onChanged: (v) {})
Switch(value: false, onChanged: (v) {})
Slider(value: 0.5, onChanged: (v) {})
Radio<int>(value: 1, groupValue: 1, onChanged: (v) {})

// Container and decoration
Container(
  width: 200,
  height: 100,
  padding: const EdgeInsets.all(16),
  margin: const EdgeInsets.symmetric(horizontal: 8),
  decoration: BoxDecoration(
    color: Colors.blue,
    borderRadius: BorderRadius.circular(12),
    border: Border.all(color: Colors.grey),
    boxShadow: [const BoxShadow(blurRadius: 4)],
  ),
  child: const Text('Box'),
)

// List widgets
ListView(
  children: [ListTile(title: Text('Item 1')), ListTile(title: Text('Item 2'))],
)

ListView.builder(
  itemCount: 100,
  itemBuilder: (context, index) => ListTile(
    title: Text('Item $index'),
  ),
)

// Card
Card(
  child: ListTile(
    leading: const Icon(Icons.person),
    title: const Text('Alice'),
    subtitle: const Text('alice@test.com'),
    trailing: const Icon(Icons.chevron_right),
    onTap: () {},
  ),
)
```

---

## Q4: What is the difference between `const` widgets and regular widgets?

```dart
// const widget — created once, never rebuilds
const Text('Hello')  // Same instance reused across rebuilds

// Regular widget — new instance on every build
Text(DateTime.now().toString())  // Must rebuild — value changes

// Performance impact
class MyWidget extends StatelessWidget {
  const MyWidget({super.key});  // const constructor

  @override
  Widget build(BuildContext context) {
    return const Column(
      children: [
        Text('Static text'),        // const — never rebuilds
        Icon(Icons.star),           // const — never rebuilds
        Padding(
          padding: EdgeInsets.all(8),
          child: Text('Nested'),    // const — deeply const
        ),
      ],
    );
  }
}

// ⚠️ const must be deeply const — all children must be const too
const Column(
  children: [
    Text('A'),
    Text('B'),
  ],
)
```

> **Best Practice:** Use `const` wherever possible. It prevents unnecessary rebuilds and improves performance significantly.

---

## Q5: What are keys and when do you need them?

```dart
// Key — unique identifier for a widget in the tree
// Used for preserving state across rebuilds

class TodoList extends StatefulWidget {
  const TodoList({super.key});
  @override
  State<TodoList> createState() => _TodoListState();
}

class _TodoListState extends State<TodoList> {
  List<String> todos = ['Task A', 'Task B', 'Task C'];

  @override
  Widget build(BuildContext context) {
    return Column(
      children: todos.map((todo) => TodoItem(
        key: ValueKey(todo),  // ✅ Key preserves state when list reorders
        todo: todo,
      )).toList(),
    );
  }
}

class TodoItem extends StatefulWidget {
  final String todo;
  const TodoItem({super.key, required this.todo});
  @override
  State<TodoItem> createState() => _TodoItemState();
}

class _TodoItemState extends State<TodoItem> {
  bool checked = false;  // State preserved with key

  @override
  Widget build(BuildContext context) {
    return CheckboxListTile(
      value: checked,
      onChanged: (v) => setState(() => checked = v!),
      title: Text(widget.todo),
    );
  }
}
```

### Key Types
| Key Type | Use Case |
|----------|----------|
| `ValueKey(value)` | Unique by value (e.g., ID, string) |
| `ObjectKey(object)` | Unique by object identity |
| `UniqueKey()` | Unique per build (rarely needed) |
| `GlobalKey()` | Access state from anywhere (anti-pattern if overused) |
| `PageStorageKey()` | Preserve scroll position |

> **Rule:** Use keys when widgets can be reordered, added, or removed from a list. Without keys, state may attach to the wrong widget.

---

## Q6: What is `InheritedWidget`?

```dart
// InheritedWidget — efficiently propagate data down the tree
class ThemeProvider extends InheritedWidget {
  final ThemeData theme;
  const ThemeProvider({super.key, required this.theme, required super.child});

  static ThemeProvider? of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<ThemeProvider>();
  }

  @override
  bool updateShouldNotify(ThemeProvider oldWidget) {
    return theme != oldWidget.theme;  // Notify dependents if theme changed
  }
}

// Usage
ThemeProvider(
  theme: myTheme,
  child: MaterialApp(home: MyApp()),
)

// Access anywhere in the tree
final theme = ThemeProvider.of(context)!.theme;
```

> **Note:** You rarely use InheritedWidget directly. Provider, Riverpod, and other state management solutions use it under the hood. `Theme.of(context)` and `MediaQuery.of(context)` are InheritedWidgets.

---

## Q7: What is `Sliver` and how do you use it?

```dart
// Slivers — composable scrollable areas
CustomScrollView(
  slivers: [
    // Sticky app bar that collapses on scroll
    SliverAppBar(
      expandedHeight: 200,
      pinned: true,
      flexibleSpace: const FlexibleSpaceBar(title: Text('Profile')),
    ),

    // Grid
    SliverGrid(
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 3,
      ),
      delegate: SliverChildBuilderDelegate(
        (context, index) => Card(child: Center(child: Text('$index'))),
        childCount: 30,
      ),
    ),

    // List
    SliverList(
      delegate: SliverChildBuilderDelegate(
        (context, index) => ListTile(title: Text('Item $index')),
        childCount: 20,
      ),
    ),

    // Fixed height
    SliverFixedExtentList(
      itemExtent: 80,
      delegate: SliverChildBuilderDelegate(
        (context, index) => ListTile(title: Text('Fixed $index')),
        childCount: 10,
      ),
    ),

    // Padding
    const SliverPadding(
      padding: EdgeInsets.all(16),
      sliver: SliverToBoxAdapter(child: Text('Footer')),
    ),
  ],
)
```

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Layouts](Layouts.md)
- [State Management](StateManagement.md)
