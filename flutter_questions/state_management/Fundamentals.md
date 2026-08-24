# State Management Fundamentals

## 📖 Explanation

State is any data that can change over time and affects the UI. State management is how you create, update, and share this data across your app. Choosing the right approach depends on the complexity of your state.

### Two Types of State
| Ephemeral (Local) State | App (Shared) State |
|------------------------|---------------------|
| Single widget needs it | Multiple widgets/screens need it |
| `setState()` is enough | Needs Provider/Riverpod/BLoC |
| Form validation, toggle | Auth, cart, theme, locale |
| Tab index, animation | User session, settings |

### setState() Flow
```
setState(() => count++)
  → Framework marks Element dirty
  → Schedules rebuild on next frame
  → build() called again
  → New widget tree compared to old
  → Only changed widgets repaint
```

### StatelessWidget vs StatefulWidget for State
| StatelessWidget | StatefulWidget |
|----------------|-----------------|
| Immutable, no state | Has mutable State object |
| Rebuilds when parent rebuilds | Can rebuild independently via setState |
| `Text`, `Icon` | `Checkbox`, `TextField`, `Animation` |
| Lighter, faster | Heavier (creates State) |

### State Lifecycle
```
createState() → initState() → didChangeDependencies()
  → build() → setState() → build() → ... → dispose()
```

### When to Move Beyond setState
- State needs to be shared across multiple screens
- State needs to persist across navigation
- Widget tree is too deep for prop drilling
- Multiple widgets need to react to the same state change
- You need testable business logic

### Common State Management Solutions
| Solution | Complexity | Best For |
|----------|------------|----------|
| setState | Low | Local widget state |
| Provider | Low-Medium | Small to medium apps |
| Riverpod | Medium | Medium to large apps |
| BLoC | High | Large, complex apps |
| GetX | Low | Rapid prototyping |

---

## 🧪 Code Example

```dart
// ── Ephemeral state with setState ──
class CounterScreen extends StatefulWidget {
  const CounterScreen({super.key});
  @override
  State<CounterScreen> createState() => _CounterScreenState();
}

class _CounterScreenState extends State<CounterScreen> {
  int _count = 0;  // Local state

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(child: Text('Count: $_count')),
      floatingActionButton: FloatingActionButton(
        onPressed: () => setState(() => _count++),  // Triggers rebuild
        child: const Icon(Icons.add),
      ),
    );
  }
}

// ── App state with Provider ──
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;

  void increment() {
    _count++;
    notifyListeners();  // Notify all listeners to rebuild
  }
}

// Provide at app root
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CounterModel(),
      child: const MyApp(),
    ),
  );
}

// Consume in any widget
class CounterWidget extends StatelessWidget {
  const CounterWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final count = context.watch<CounterModel>().count;  // Rebuilds on change
    return Text('Count: $count');
  }
}

// ── Prop drilling (anti-pattern) ──
// ❌ Bad — passing state through every layer
class Parent extends StatelessWidget {
  final int count;
  const Parent(this.count, {super.key});
  Widget build(context) => MiddleChild(count);  // Passed down
}
class MiddleChild extends StatelessWidget {
  final int count;
  const MiddleChild(this.count, {super.key});
  Widget build(context) => LeafChild(count);  // Passed down again
}
class LeafChild extends StatelessWidget {
  final int count;
  const LeafChild(this.count, {super.key});
  Widget build(context) => Text('$count');  // Finally used
}

// ✅ Good — Provider eliminates prop drilling
class LeafChild extends StatelessWidget {
  Widget build(context) {
    final count = context.watch<CounterModel>().count;  // Direct access
    return Text('$count');
  }
}

// ── Lifting state up ──
class Parent extends StatefulWidget {
  const Parent({super.key});
  @override
  State<Parent> createState() => _ParentState();
}
class _ParentState extends State<Parent> {
  String _text = '';

  @override
  Widget build(BuildContext context) {
    return Column(children: [
      ChildA(onChanged: (v) => setState(() => _text = v)),  // Pass callback
      ChildB(text: _text),  // Pass state down
    ]);
  }
}
```

### Output
```
A Flutter app demonstrating state management fundamentals:
- setState for local (ephemeral) state
- Provider + ChangeNotifier for shared (app) state
- Prop drilling anti-pattern vs Provider solution
- Lifting state up pattern for sibling communication
```

---

## ❓ Interview Questions

1. **What is state in Flutter?**
   - State is any data that can change over time and affects the UI. Examples: counter value, user input text, shopping cart items, loading indicators, auth status, theme mode. There are two types: **ephemeral (local) state** — only one widget needs it (tab index, toggle, form input), managed with `setState()`. **app (shared) state** — multiple widgets/screens need it (auth, cart, theme), managed with Provider, Riverpod, or BLoC. The key distinction: if only one widget cares about the state, use setState. If multiple widgets need to read or change it, use a state management solution.

2. **What is setState() and how does it work?**
   - `setState(() { ... })` marks the widget's State as dirty and schedules a rebuild on the next frame. The callback function modifies the state variables. On the next frame, Flutter calls `build()` again with the updated state, creates a new widget tree, diffs it against the old tree (using `Element.canUpdate`), and updates only what changed. `setState()` is synchronous in terms of marking dirty, but the rebuild happens asynchronously on the next frame. Never call `setState()` after `dispose()` — check `mounted` before calling `setState()` after async operations.

3. **What is the difference between ephemeral and app state?**
   - **Ephemeral (local) state** — state that only a single widget needs. Examples: tab index, text input, toggle, animation progress, form validation. Managed with `setState()` inside a StatefulWidget. No need for external state management. **App (shared) state** — state that multiple widgets or screens need to access. Examples: user auth, shopping cart, theme, locale, app settings. Managed with Provider, Riverpod, BLoC, or similar. The distinction guides your choice: `setState()` for ephemeral, state management packages for shared. Some state starts ephemeral and becomes shared as the app grows.

4. **What is prop drilling and how do you avoid it?**
   - Prop drilling is passing data through multiple widget layers that don't need it, just to reach a deeply nested widget. Example: Parent → MiddleWidget → LeafWidget — the MiddleWidget doesn't need `count` but must receive and pass it. This makes code hard to maintain — adding a parameter means updating every intermediate widget. Avoid it with state management: Provider (`context.watch<T>()`), Riverpod (`ref.watch(provider)`), or BLoC (`BlocBuilder`). The deeply nested widget accesses state directly without intermediate widgets knowing about it. This is the primary reason to use state management packages beyond `setState()`.

5. **When should you move from setState to a state management package?**
   - Move beyond `setState()` when: (1) State needs to be shared across multiple screens. (2) State needs to persist across navigation. (3) The widget tree is too deep for prop drilling. (4) Multiple widgets need to react to the same state change. (5) You need testable business logic separated from UI. (6) State updates are complex (async, debounced, conditional). If a single widget's state never leaves that widget, `setState()` is the right choice. Don't over-engineer — start with `setState()` and add a state management package only when you actually need it.

6. **What is "lifting state up"?**
   - "Lifting state up" is a pattern where shared state is moved to the nearest common ancestor of the widgets that need it. If Widget A and Widget B (siblings) need to share state, move the state to their parent. The parent passes the state down as props and receives updates via callbacks. Example: Parent holds `_text`, passes `_text` to ChildB and `onChanged` callback to ChildA. When ChildA calls `onChanged`, Parent's `setState()` updates `_text`, and ChildB rebuilds with the new value. This is the `setState()` approach to shared state — works for simple cases but becomes unwieldy for deep trees (use Provider instead).

7. **What is the StatefulWidget lifecycle?**
   - `createState()` → creates the State object. `initState()` → called once, initialize state, start listeners/controllers (no `context` access for InheritedWidgets). `didChangeDependencies()` → called after initState and when InheritedWidget changes (safe for `context` access). `build()` → builds the widget tree, called on every rebuild. `didUpdateWidget(oldWidget)` → parent rebuilds with new configuration. `setState()` → marks dirty, triggers `build()`. `deactivate()` → removed from tree (may be reinserted). `dispose()` → called once, clean up controllers, listeners, subscriptions.

8. **What is the difference between StatelessWidget and StatefulWidget for state?**
   - StatelessWidget is immutable — it has no internal state. It rebuilds when its parent rebuilds or when an InheritedWidget it depends on changes. Use for widgets that display data passed via constructor (Text, Icon, Image). StatefulWidget has a mutable State object that persists across rebuilds. It can rebuild independently via `setState()` without parent rebuilding. Use for widgets that manage their own state (Checkbox, TextField, Animation). The State object is created once (createState) and persists — only the widget configuration is recreated on rebuild. This is why controllers should be created in `initState()`, not `build()`.

9. **What is InheritedWidget and how does it relate to state management?**
   - `InheritedWidget` is Flutter's built-in mechanism for sharing data down the widget tree without prop drilling. When an InheritedWidget changes, all descendant widgets that called `dependOnInheritedWidgetOfExactType` rebuild. Provider is built on top of InheritedWidget — `ChangeNotifierProvider` wraps a `ChangeNotifier` in an InheritedWidget, and `context.watch<T>()` calls `dependOnInheritedWidgetOfExactType`. Theme and MediaQuery are InheritedWidgets. InheritedWidget is low-level — Provider, Riverpod, and BLoC provide better APIs on top of it. You rarely use InheritedWidget directly — use Provider instead.

10. **What are the common state management mistakes?**
    - (1) Using `setState()` for shared state — leads to prop drilling. (2) Calling `setState()` after `dispose()` — crashes. Always check `mounted`. (3) Calling `setState()` during `build()` — infinite loop. Use `addPostFrameCallback`. (4) Not disposing controllers/listeners — memory leaks. (5) Putting all state in one giant provider — hard to maintain. Split by feature. (6) Using `context.watch()` in callbacks — use `context.read()` instead. (7) Rebuilding the entire app on every state change — use `Selector` or `Consumer` to limit rebuilds. (8) Not separating business logic from UI — use ViewModels/BLoC.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Riverpod](Riverpod.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
