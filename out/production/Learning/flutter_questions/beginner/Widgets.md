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

## Q8: What is `Builder` widget and when should you use it?

```dart
// Builder — creates a widget from a BuildContext
// Useful when you need a context that is below the current widget

// ❌ Bad — context is from the parent, theme may not be available yet
class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // This context is ABOVE MaterialApp — Theme.of(context) fails!
    final theme = Theme.of(context);  // ❌ No theme yet
    return MaterialApp(
      home: Text('Hello', style: theme.textTheme.bodyLarge),
    );
  }
}

// ✅ Good — Builder gives a context BELOW MaterialApp
class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Builder(
        builder: (context) {
          // This context is BELOW MaterialApp — theme is available
          final theme = Theme.of(context);  // ✅ Works
          return Text('Hello', style: theme.textTheme.bodyLarge);
        },
      ),
    );
  }
}

// Common use: SnackBar with Scaffold context
Scaffold(
  body: Builder(
    builder: (context) => ElevatedButton(
      onPressed: () => ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Hello!')),
      ),
      child: const Text('Show SnackBar'),
    ),
  ),
)
```

> **Key:** `Builder` provides a `BuildContext` that is a child of the current widget. Use it when you need to access `InheritedWidget`s (Theme, MediaQuery, Provider) that are defined above the current widget but below the root.

---

## Q9: What is the difference between `Container` and `SizedBox`?

```dart
// SizedBox — fixed size or gap (lightweight)
const SizedBox(height: 16)  // Gap
const SizedBox(width: 100, height: 50, child: Text('Fixed'))

// Container — multi-purpose (heavier, only use when needed)
Container(
  width: 100,
  height: 50,
  padding: const EdgeInsets.all(8),
  margin: const EdgeInsets.all(16),
  decoration: BoxDecoration(
    color: Colors.blue,
    borderRadius: BorderRadius.circular(12),
  ),
  child: const Text('Box'),
)

// Container with no decoration/padding/margin = SizedBox (but heavier)
// Container with only color = ColoredBox (but heavier)
```

| Feature | `SizedBox` | `Container` |
|---------|-----------|-------------|
| Size | ✅ Fixed size | ✅ Fixed or flexible |
| Padding | ❌ | ✅ |
| Margin | ❌ | ✅ |
| Decoration | ❌ | ✅ (color, border, shadow) |
| Transform | ❌ | ✅ |
| Performance | ✅ Lightweight | ⚠️ Heavier |
| Use case | Gaps, fixed size | Complex styling |

> **Best Practice:** Use `SizedBox` for gaps and fixed sizes. Use `Container` only when you need padding, margin, decoration, or transform. `SizedBox` is cheaper because it maps directly to `RenderConstrainedBox`.

---

## Q10: What are `ListView.builder`, `ListView.separated`, and `ListView.custom`?

```dart
// ListView.builder — lazy, only builds visible items (most common)
ListView.builder(
  itemCount: 1000,
  itemBuilder: (context, index) => ListTile(
    title: Text('Item $index'),
  ),
)

// ListView.separated — adds separators between items
ListView.separated(
  itemCount: 20,
  separatorBuilder: (context, index) => const Divider(height: 1),
  itemBuilder: (context, index) => ListTile(
    title: Text('Item $index'),
  ),
)

// ListView.custom — full control with SliverChildDelegate
ListView.custom(
  childrenDelegate: SliverChildBuilderDelegate(
    (context, index) => ListTile(title: Text('Item $index')),
    childCount: 100,
    // findChildIndexCallback — for efficient key-based reordering
    findChildIndexCallback: (key) {
      final id = (key as ValueKey).value as String;
      return items.indexWhere((item) => item.id == id);
    },
  ),
)

// Performance tips:
// 1. Use const constructors in items
// 2. Set itemExtent for fixed-height items (skips measurement)
ListView.builder(
  itemExtent: 72,  // Fixed height — faster scrolling
  itemCount: 1000,
  itemBuilder: (context, index) => ListTile(title: Text('Item $index')),
)

// 3. Use prototypeItem for variable-height items (measures once)
ListView.builder(
  prototypeItem: const ListTile(title: Text('Prototype')),
  itemCount: 1000,
  itemBuilder: (context, index) => ListTile(title: Text('Item $index')),
)
```

| Constructor | Builds | Use Case |
|-------------|--------|----------|
| `ListView()` | All children | Small lists (<20) |
| `ListView.builder()` | Only visible | Large/dynamic lists |
| `ListView.separated()` | Only visible + dividers | Lists with separators |
| `ListView.custom()` | Delegate-controlled | Custom item management |

> **Performance:** `itemExtent` is the single biggest performance win for `ListView.builder` — it skips the layout phase for each item since the height is known.

---

## Q11: What is the difference between `Navigator.push` and `Navigator.pushNamed`?

```dart
// Navigator.push — direct widget navigation (imperative)
Navigator.push(
  context,
  MaterialPageRoute(
    builder: (context) => const DetailScreen(),
  ),
);

// Navigator.pushNamed — named route navigation (declarative)
// Requires route definitions in MaterialApp
MaterialApp(
  routes: {
    '/': (context) => const HomeScreen(),
    '/detail': (context) => const DetailScreen(),
    '/settings': (context) => const SettingsScreen(),
  },
);

// Navigate using named route
Navigator.pushNamed(context, '/detail');

// With arguments
Navigator.pushNamed(context, '/detail', arguments: {'id': 42});

// Receiving arguments
class DetailScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
    return Text('Item ${args['id']}');
  }
}
```

| Feature | `Navigator.push` | `Navigator.pushNamed` |
|---------|------------------|---------------------|
| Route definition | Inline | Centralized in MaterialApp |
| Arguments | Direct widget params | `arguments` parameter |
| Deep linking | Harder | Easier (URL → route) |
| Testing | Tightly coupled | Loosely coupled |
| Best for | Small apps | Medium/large apps |

> **Best Practice:** Use named routes for apps with 5+ screens. For complex navigation, use the `go_router` package which supports nested navigation, redirects, and URL-based routing.

---

## Q12: What is `Hero` animation and how does it work?

```dart
// Hero — shared element transition between two screens
// The widget "flies" from source to destination

// Screen 1 — tap to navigate
Hero(
  tag: 'product-${product.id}',  // Unique tag must match
  child: Image.network(product.imageUrl),
)

// Screen 2 — receives the flying widget
Hero(
  tag: 'product-${product.id}',  // Same tag!
  child: Image.network(product.imageUrl),
)
```

### How it works:
1. User taps the image on Screen 1
2. Flutter finds the `Hero` with matching `tag` on Screen 2
3. It creates an overlay that animates the widget from Screen 1 position to Screen 2 position
4. The transition uses `MaterialRectArcTween` (circular arc path)

### Custom flight duration:
```dart
Navigator.push(
  context,
  PageRouteBuilder(
    transitionDuration: const Duration(milliseconds: 500),
    pageBuilder: (context, animation, secondaryAnimation) => const DetailScreen(),
  ),
);
```

> **Tip:** The `tag` must be unique across the entire widget tree. If two Heroes have the same tag, Flutter throws an assertion error. Use a unique identifier like `'image-$id'`.

---

## Q13: What is `LayoutBuilder` and when should you use it?

```dart
// LayoutBuilder — gives you the parent's constraints
// Use when you need to adapt layout based on available space

LayoutBuilder(
  builder: (context, constraints) {
    if (constraints.maxWidth > 600) {
      // Tablet/desktop — 2 columns
      return Row(
        children: [
          Expanded(child: MenuPanel()),
          Expanded(child: ContentPanel()),
        ],
      );
    } else {
      // Phone — 1 column
      return Column(
        children: [
          ContentPanel(),
        ],
      );
    }
  },
)

// Responsive grid — adjust columns based on width
LayoutBuilder(
  builder: (context, constraints) {
    final columns = (constraints.maxWidth / 200).floor().clamp(1, 4);
    return GridView.builder(
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: columns,
        crossAxisSpacing: 8,
        mainAxisSpacing: 8,
      ),
      itemCount: items.length,
      itemBuilder: (context, index) => ItemCard(items[index]),
    );
  },
)
```

> **Key:** `LayoutBuilder` is the responsive design foundation in Flutter. It gives you `BoxConstraints` from the parent, so you can make layout decisions at runtime. For app-level responsiveness, use `MediaQuery.of(context).size` or the `flutter_screenutil` package.

---

## Q14: What is `Tween` and how do you create custom animations?

```dart
// Tween — defines the start and end values for an animation
// Interpolates between two values over a duration

class FadeInWidget extends StatefulWidget {
  const FadeInWidget({super.key});
  @override
  State<FadeInWidget> createState() => _FadeInWidgetState();
}

class _FadeInWidgetState extends State<FadeInWidget>
    with SingleTickerProviderStateMixin {
  late AnimationController controller;
  late Animation<double> fadeAnimation;
  late Animation<Offset> slideAnimation;

  @override
  void initState() {
    super.initState();
    controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    // Fade tween: 0.0 → 1.0
    fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(
      CurvedAnimation(parent: controller, curve: Curves.easeIn),
    );

    // Slide tween: offset(0, 0.1) → offset(0, 0)
    slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.1),
      end: Offset.zero,
    ).animate(
      CurvedAnimation(parent: controller, curve: Curves.easeOut),
    );

    controller.forward();  // Start animation
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: fadeAnimation,
      child: SlideTransition(
        position: slideAnimation,
        child: const Text('Animated!'),
      ),
    );
  }
}

// Color tween
final colorTween = ColorTween(begin: Colors.blue, end: Colors.red)
    .animate(controller);

// Int tween
final intTween = IntTween(begin: 0, end: 100).animate(controller);
```

| Tween Type | Use Case |
|------------|----------|
| `Tween<double>` | Opacity, scale, rotation |
| `Tween<Offset>` | Slide, drag |
| `ColorTween` | Color transitions |
| `IntTween` | Counters, progress |
| `Tween<Rect>` | Size transitions |

> **Tip:** Use `Curves.easeInOut` for natural motion. Avoid linear curves — they feel robotic. For spring physics, use `SpringSimulation` or the `flutter_animate` package for declarative animations.

---

## Q15: What is `ValueListenableBuilder` and how does it differ from `setState`?

```dart
// ValueListenableBuilder — rebuild only when a ValueNotifier changes
// More efficient than setState (which rebuilds the entire widget)

class CounterApp extends StatelessWidget {
  // ValueNotifier holds the state — no StatefulWidget needed!
  final counter = ValueNotifier<int>(0);

  CounterApp({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Only this Text rebuilds when counter changes
        ValueListenableBuilder<int>(
          valueListenable: counter,
          builder: (context, value, child) {
            return Text('Count: $value', style: const TextStyle(fontSize: 32));
          },
        ),
        ElevatedButton(
          onPressed: () => counter.value++,  // No setState needed!
          child: const Text('Increment'),
        ),
      ],
    );
  }
}
```

| Feature | `setState` | `ValueListenableBuilder` |
|---------|-----------|------------------------|
| Rebuild scope | Entire widget | Only the builder |
| State location | Inside State object | In ValueNotifier |
| Widget type | StatefulWidget | StatelessWidget |
| Performance | Rebuilds whole tree | Rebuilds only listener |
| Disposal | Automatic | Must dispose ValueNotifier |
| Best for | Simple local state | Fine-grained updates |

> **Best Practice:** Use `ValueListenableBuilder` when only a small part of the UI needs to rebuild. For complex state, use Provider or Riverpod. Always dispose `ValueNotifier` to prevent memory leaks.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Layouts](Layouts.md)
- [State Management](StateManagement.md)
