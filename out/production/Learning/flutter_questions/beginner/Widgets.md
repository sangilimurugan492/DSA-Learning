# Widgets

## 📖 Explanation

In Flutter, everything is a widget. Widgets are immutable descriptions of part of the UI. They are composed to build complex UIs — composition over inheritance.

### StatelessWidget vs StatefulWidget
| StatelessWidget | StatefulWidget |
|----------------|-----------------|
| Immutable | Has mutable state |
| No `setState()` | Uses `setState()` to rebuild |
| Rebuilds when parent rebuilds | Can rebuild independently |
| Lighter, faster | Heavier (creates State object) |
| `Text`, `Icon`, `Image` | `Checkbox`, `TextField`, `Animation` |

> **Rule:** Use StatelessWidget by default. Only use StatefulWidget when the widget needs to manage its own mutable state.

### StatefulWidget Lifecycle
```
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

### Common Widget Categories
- **Display**: `Text`, `Icon`, `Image`, `CircleAvatar`
- **Buttons**: `ElevatedButton`, `TextButton`, `OutlinedButton`, `IconButton`, `FloatingActionButton`
- **Input**: `TextField`, `TextFormField`, `Checkbox`, `Switch`, `Slider`, `Radio`
- **Container**: `Container`, `SizedBox`, `Card`
- **List**: `ListView`, `ListView.builder`, `ListView.separated`

### `const` Widgets
`const` widgets are created once and never rebuild — the same instance is reused across rebuilds. All children must also be `const` (deeply const).

> **Best Practice:** Use `const` wherever possible. It prevents unnecessary rebuilds and improves performance significantly.

### Keys
Keys uniquely identify widgets in the tree, used for preserving state across rebuilds.

| Key Type | Use Case |
|----------|----------|
| `ValueKey(value)` | Unique by value (e.g., ID, string) |
| `ObjectKey(object)` | Unique by object identity |
| `UniqueKey()` | Unique per build (rarely needed) |
| `GlobalKey()` | Access state from anywhere (anti-pattern if overused) |
| `PageStorageKey()` | Preserve scroll position |

> **Rule:** Use keys when widgets can be reordered, added, or removed from a list.

### InheritedWidget
InheritedWidget efficiently propagates data down the tree. Only widgets that explicitly depend on it rebuild. Provider, Riverpod, Theme, and MediaQuery use it under the hood.

### Slivers
Slivers are composable scrollable areas used in `CustomScrollView`. Common slivers: `SliverAppBar`, `SliverList`, `SliverGrid`, `SliverFixedExtentList`, `SliverToBoxAdapter`.

### ListView Constructors
| Constructor | Builds | Use Case |
|-------------|--------|----------|
| `ListView()` | All children | Small lists (<20) |
| `ListView.builder()` | Only visible | Large/dynamic lists |
| `ListView.separated()` | Only visible + dividers | Lists with separators |
| `ListView.custom()` | Delegate-controlled | Custom item management |

### Container vs SizedBox
| Feature | `SizedBox` | `Container` |
|---------|-----------|-------------|
| Size | ✅ Fixed size | ✅ Fixed or flexible |
| Padding | ❌ | ✅ |
| Margin | ❌ | ✅ |
| Decoration | ❌ | ✅ (color, border, shadow) |
| Performance | ✅ Lightweight | ⚠️ Heavier |
| Use case | Gaps, fixed size | Complex styling |

> **Best Practice:** Use `SizedBox` for gaps and fixed sizes. Use `Container` only when you need padding, margin, decoration, or transform.

---

## 🧪 Code Example

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Widgets Demo')),
        body: const Counter(),
      ),
    );
  }
}

// StatelessWidget — immutable
class GreetingCard extends StatelessWidget {
  final String name;
  const GreetingCard({super.key, required this.name});

  @override
  Widget build(BuildContext context) {
    return Text('Hello, $name!');
  }
}

// StatefulWidget — has mutable state
class Counter extends StatefulWidget {
  const Counter({super.key});
  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int count = 0;  // Mutable state

  @override
  void initState() {
    super.initState();
    // Called ONCE — initialize state
  }

  @override
  void dispose() {
    // Called ONCE — cleanup
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $count'),
        const GreetingCard(name: 'Flutter'),
        ElevatedButton(
          onPressed: () => setState(() => count++),  // Triggers rebuild
          child: const Text('Increment'),
        ),
      ],
    );
  }
}

// const widgets — never rebuild
const staticWidget = Column(
  children: [
    Text('Static text'),
    Icon(Icons.star),
  ],
);
```

### Output
```
A running Flutter app with:
- AppBar showing "Widgets Demo"
- Text "Count: 0" (updates on button press)
- Text "Hello, Flutter!"
- ElevatedButton labeled "Increment"
```

---

## ❓ Interview Questions

1. **What is the difference between StatelessWidget and StatefulWidget?**
   - StatelessWidget is immutable — it has no internal state and rebuilds only when its parent rebuilds or its inputs change. Use for static UI (Text, Icon, Image). StatefulWidget has mutable state via a `State` object — it can rebuild independently via `setState()`. Use for interactive UI (Checkbox, TextField, Animation). Use StatelessWidget by default; only use StatefulWidget when the widget needs to manage its own mutable state.

2. **What is the widget lifecycle?**
   - StatefulWidget lifecycle: `createState()` → `initState()` (called once, initialize state/controllers) → `didChangeDependencies()` (after initState, InheritedWidget changes) → `build()` (called on every rebuild) → `didUpdateWidget()` (parent rebuilds with new config) → `setState()` (marks dirty, triggers build) → `deactivate()` (removed from tree) → `dispose()` (called once, cleanup). `initState` and `dispose` are called exactly once per State object.

3. **What are the most common Flutter widgets?**
   - Display: `Text`, `Icon`, `Image.network`, `Image.asset`, `CircleAvatar`. Buttons: `ElevatedButton`, `TextButton`, `OutlinedButton`, `IconButton`, `FloatingActionButton`. Input: `TextField`, `TextFormField`, `Checkbox`, `Switch`, `Slider`, `Radio`. Container: `Container` (padding, margin, decoration), `SizedBox` (fixed size/gap), `Card`. List: `ListView.builder` (lazy), `ListView.separated` (with dividers).

4. **What is the difference between `const` widgets and regular widgets?**
   - `const` widgets are created once at compile time — the same instance is reused across all rebuilds, so they never rebuild. Regular widgets create a new instance on every `build()` call. `const` must be deeply const — all children must also be const. Use `const` wherever possible for performance. Example: `const Text('Hello')` never rebuilds; `Text(DateTime.now().toString())` must rebuild because the value changes.

5. **What are keys and when do you need them?**
   - Keys uniquely identify widgets in the tree, used for preserving state across rebuilds. Use keys when widgets can be reordered, added, or removed from a list. Without keys, state may attach to the wrong widget after reordering. `ValueKey(value)` — most common, identifies by value (e.g., item ID). `GlobalKey` — accesses state from anywhere (expensive, use sparingly, good for form validation). Avoid `GlobalKey` for lists.

6. **What is `InheritedWidget`?**
   - InheritedWidget efficiently propagates data down the widget tree. Only widgets that explicitly depend on it (via `dependOnInheritedWidgetOfExactType`) rebuild when the data changes. It's the foundation of Provider, Theme, MediaQuery, and other state management solutions. You rarely use InheritedWidget directly — use Provider or Riverpod instead. `updateShouldNotify` determines whether dependents should rebuild.

7. **What is `Sliver` and how do you use it?**
   - Slivers are composable scrollable areas used inside `CustomScrollView`. Common slivers: `SliverAppBar` (sticky/collapsing app bar), `SliverList` (lazy list), `SliverGrid` (grid), `SliverFixedExtentList` (fixed-height items), `SliverToBoxAdapter` (wrap non-sliver widget), `SliverFillRemaining` (fill remaining space). Slivers enable complex scroll layouts with shared scroll context.

8. **What is `Builder` widget and when should you use it?**
   - `Builder` creates a widget from a `BuildContext` that is a child of the current widget. Use it when you need to access `InheritedWidget`s (Theme, MediaQuery, Provider) that are defined above the current widget but below the root. Common use: accessing `Theme.of(context)` inside `MaterialApp`, or showing a `SnackBar` with `ScaffoldMessenger.of(context)` — the context from the parent widget may be above the `Scaffold`.

9. **What is the difference between `Container` and `SizedBox`?**
   - `SizedBox` is lightweight — it only provides fixed size or a gap. It maps directly to `RenderConstrainedBox`. `Container` is multi-purpose — it supports padding, margin, decoration (color, border, shadow), and transform, but is heavier. Use `SizedBox` for gaps and fixed sizes. Use `Container` only when you need padding, margin, decoration, or transform. `Container` with no decoration/padding/margin is equivalent to `SizedBox` but heavier.

10. **What are `ListView.builder`, `ListView.separated`, and `ListView.custom`?**
    - `ListView.builder` — lazy loading, only builds visible items. Best for large/dynamic lists. `itemExtent` is the biggest performance win (skips layout). `ListView.separated` — like `.builder` but adds separators between items via `separatorBuilder`. `ListView.custom` — full control with `SliverChildDelegate`, supports `findChildIndexCallback` for efficient key-based reordering. `ListView()` (without builder) renders all children — fine for small lists, bad for large ones.

11. **What is the difference between `Navigator.push` and `Navigator.pushNamed`?**
    - `Navigator.push` — direct widget navigation (imperative). You pass a `MaterialPageRoute` with a builder. Arguments are passed directly as constructor params. `Navigator.pushNamed` — named route navigation (declarative). Routes are defined centrally in `MaterialApp`. Arguments passed via `arguments` parameter, read via `ModalRoute.of(context).settings.arguments`. Named routes are better for deep linking and medium/large apps. For complex navigation, use `go_router`.

12. **What is `Hero` animation and how does it work?**
    - `Hero` creates a shared element transition between two screens — the widget "flies" from source to destination. Both screens must have a `Hero` with the same `tag`. When navigating, Flutter finds the matching Hero, creates an overlay, and animates the widget from Screen 1 position to Screen 2 position using `MaterialRectArcTween`. The `tag` must be unique across the entire widget tree — duplicate tags throw an assertion error. Use a unique identifier like `'image-$id'`.

13. **What is `LayoutBuilder` and when should you use it?**
    - `LayoutBuilder` gives you the parent's `BoxConstraints` at build time. Use it to adapt layout based on available space — e.g., 2 columns on tablet, 1 column on phone. It's the responsive design foundation in Flutter. For app-level responsiveness, use `MediaQuery.of(context).size` or `flutter_screenutil`. `LayoutBuilder` is more efficient than `MediaQuery` for widget-level decisions because it only rebuilds when constraints change.

14. **What is `Tween` and how do you create custom animations?**
    - `Tween` defines start and end values for an animation, interpolating between them over a duration. Use with `AnimationController` and `CurvedAnimation`. Types: `Tween<double>` (opacity, scale), `Tween<Offset>` (slide), `ColorTween` (color transitions), `IntTween` (counters). Use `Curves.easeInOut` for natural motion — avoid linear curves (feel robotic). For spring physics, use `SpringSimulation` or `flutter_animate` package. Always dispose `AnimationController` in `dispose()`.

15. **What is `ValueListenableBuilder` and how does it differ from `setState`?**
    - `ValueListenableBuilder` rebuilds only its builder subtree when a `ValueNotifier` changes — more efficient than `setState` which rebuilds the entire widget. With `ValueListenableBuilder`, the widget can be a `StatelessWidget` — state lives in `ValueNotifier`. `setState` rebuilds the entire `State` object's `build()` method. Use `ValueListenableBuilder` for fine-grained updates (single value). Always dispose `ValueNotifier` to prevent memory leaks. For complex state, use Provider or Riverpod.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Layouts](Layouts.md)
- [State Management](StateManagement.md)
