# Debugging Scenarios

## Scenario 1: setState() Called After Dispose

### Problem
The app crashes with `setState() called after dispose()` when navigating away from a screen during an async operation.

```dart
// ❌ Bad — setState after widget is disposed
class _MyScreenState extends State<MyScreen> {
  String _data = '';

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final data = await api.fetchData();  // User navigates back during this
    setState(() => _data = data);  // ❌ Crash! Widget already disposed
  }
}
```

### Solution: Check mounted before setState

```dart
// ✅ Good 1 — check mounted
Future<void> _loadData() async {
  final data = await api.fetchData();
  if (!mounted) return;  // Widget no longer in tree
  setState(() => _data = data);
}

// ✅ Good 2 — cancel the async operation
class _MyScreenState extends State<MyScreen> {
  StreamSubscription? _subscription;

  @override
  void initState() {
    super.initState();
    _subscription = api.dataStream.listen((data) {
      if (mounted) setState(() => _data = data);
    });
  }

  @override
  void dispose() {
    _subscription?.cancel();  // Cancel before dispose
    super.dispose();
  }
}

// ✅ Good 3 — use a flag
class _MyScreenState extends State<MyScreen> {
  bool _disposed = false;

  @override
  void dispose() {
    _disposed = true;
    super.dispose();
  }

  Future<void> _loadData() async {
    final data = await api.fetchData();
    if (_disposed) return;
    setState(() => _data = data);
  }
}

// ✅ Good 4 — use Completer for cancellable futures
class _MyScreenState extends State<MyScreen> {
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _timer = Timer(const Duration(seconds: 2), () {
      if (mounted) setState(() => _data = 'Loaded');
    });
  }

  @override
  void dispose() {
    _timer?.cancel();  // Cancel timer
    super.dispose();
  }
}
```

### Key Takeaway
- Always check `mounted` before `setState()` after any `await`
- Cancel streams, timers, and subscriptions in `dispose()`
- The error happens because async operations complete after the widget is gone
- `mounted` is `false` after `dispose()` — check it to avoid crashes

---

## Scenario 2: RenderFlex Overflow

### Problem
A yellow-black striped error appears: `RenderFlex overflowed by X pixels on the right`.

```dart
// ❌ Bad — Row children don't fit
Row(
  children: [
    const Icon(Icons.person),
    const SizedBox(width: 8),
    Text('Alice Anderson with a very long name'),  // Overflows!
    const Icon(Icons.chevron_right),
  ],
)
// Text takes all available width → Row overflows
```

### Solution: Flexible/Expanded + FittedBox

```dart
// ✅ Good 1 — Expanded wraps Text to fit available space
Row(
  children: [
    const Icon(Icons.person),
    const SizedBox(width: 8),
    Expanded(  // Takes remaining space
      child: Text(
        'Alice Anderson with a very long name',
        overflow: TextOverflow.ellipsis,  // Adds ...
        maxLines: 1,
      ),
    ),
    const Icon(Icons.chevron_right),
  ],
)

// ✅ Good 2 — Flexible (doesn't force fill)
Row(
  children: [
    const Icon(Icons.person),
    const SizedBox(width: 8),
    Flexible(
      child: Text(
        'Alice Anderson',
        overflow: TextOverflow.ellipsis,
        maxLines: 1,
      ),
    ),
    const Icon(Icons.chevron_right),
  ],
)

// ✅ Good 3 — FittedBox scales down
Row(
  children: [
    const FittedBox(
      child: Text('Long text that needs to fit'),
    ),
  ],
)

// ✅ Good 4 — Wrap for dynamic content
Wrap(
  spacing: 8,
  children: [
    Chip(label: Text('Tag 1')),
    Chip(label: Text('Tag 2')),
    Chip(label: Text('Tag 3')),
  ],
)

// ✅ Good 5 — SingleChildScrollView for horizontal
SingleChildScrollView(
  scrollDirection: Axis.horizontal,
  child: Row(
    children: [
      const Text('Long content...'),
      const Text('More content...'),
    ],
  ),
)
```

### Common Overflow Causes
```
Row overflow:
  → Text without Expanded/Flexible
  → Fixed-width children that don't fit
  → Fix: Expanded(child: Text(..., overflow: ellipsis))

Column overflow:
  → Column in ScrollView without shrinkWrap
  → Fix: shrinkWrap: true or use ListView

ListView in Column:
  → ListView has unbounded height
  → Fix: Expanded(child: ListView(...))
```

### Key Takeaway
- `Expanded` gives child all remaining space (tight)
- `Flexible` gives child up to remaining space (loose)
- `Text` with `overflow: TextOverflow.ellipsis` + `maxLines: 1` truncates
- `FittedBox` scales content to fit
- `Wrap` flows children to next line
- `SingleChildScrollView` for scrollable content

---

## Scenario 3: Memory Leak from Controllers

### Problem
The app's memory usage grows over time. Profiling shows leaked `AnimationController` and `TextEditingController` instances.

```dart
// ❌ Bad — controllers never disposed
class _MyScreenState extends State<MyScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _animController;
  late TextEditingController _textController;
  late ScrollController _scrollController;
  late FocusNode _focusNode;

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 1),
    )..repeat();
    _textController = TextEditingController();
    _scrollController = ScrollController();
    _focusNode = FocusNode();
  }

  // ❌ No dispose() — all controllers leak!
}
```

### Solution: Dispose all controllers

```dart
// ✅ Good — dispose everything in dispose()
class _MyScreenState extends State<MyScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _animController;
  late TextEditingController _textController;
  late ScrollController _scrollController;
  late FocusNode _focusNode;
  StreamSubscription? _subscription;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 1),
    )..repeat();
    _textController = TextEditingController();
    _scrollController = ScrollController();
    _focusNode = FocusNode();

    _subscription = someStream.listen((event) { ... });
    _timer = Timer(const Duration(seconds: 10), () { ... });
  }

  @override
  void dispose() {
    _animController.dispose();    // Stop animation
    _textController.dispose();    // Clean up text controller
    _scrollController.dispose();  // Clean up scroll controller
    _focusNode.dispose();         // Clean up focus node
    _subscription?.cancel();      // Cancel stream subscription
    _timer?.cancel();             // Cancel timer
    super.dispose();
  }
}
```

### What to Dispose
| Resource | Dispose Method | Leak if not disposed |
|----------|---------------|---------------------|
| `AnimationController` | `.dispose()` | ✅ Ticker keeps running |
| `TextEditingController` | `.dispose()` | ✅ Listeners retained |
| `ScrollController` | `.dispose()` | ✅ Listeners retained |
| `FocusNode` | `.dispose()` | ✅ Listeners retained |
| `StreamSubscription` | `.cancel()` | ✅ Stream stays open |
| `Timer` | `.cancel()` | ✅ Callback fires on dead widget |
| `PageController` | `.dispose()` | ✅ Listeners retained |
| `TabController` | `.dispose()` | ✅ Listeners retained |

### Key Takeaway
- Every controller created in `initState()` must be disposed in `dispose()`
- Cancel all `StreamSubscription` and `Timer` in `dispose()`
- Use DevTools Memory tab to find leaked objects
- Leaked controllers keep listeners alive → memory grows
- `super.dispose()` must be called last in `dispose()`

---

## Scenario 4: Blank Screen / Widget Not Rendering

### Problem
The screen is blank — no content appears, no error in console.

```dart
// ❌ Bad 1 — missing Material ancestor
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Text('Hello');  // No Material/Scaffold → no text direction
    // Error: "No Material widget found"
  }
}

// ❌ Bad 2 — unbounded constraints
Column(
  children: [
    ListView(  // ListView has unbounded height in Column
      children: [Text('A'), Text('B')],
    )
  ],
)
// Error: "Vertical viewport was given unbounded height"

// ❌ Bad 3 — Future returns null
FutureBuilder(
  future: fetchData(),  // Returns null
  builder: (_, snap) {
    return Text(snap.data);  // snap.data is null → blank
  },
)

// ❌ Bad 4 — Container with no size and no child
Container(color: Colors.red)  // Zero size — invisible
```

### Solution: Debug step by step

```dart
// ✅ Good 1 — wrap in Material/Scaffold
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(child: Text('Hello')),  // Has Material ancestor
    );
  }
}

// ✅ Good 2 — give ListView bounded constraints
Column(
  children: [
    Expanded(  // Give ListView bounded height
      child: ListView(
        children: [Text('A'), Text('B')],
      ),
    ),
  ],
)

// ✅ Good 3 — handle null in FutureBuilder
FutureBuilder(
  future: fetchData(),
  builder: (_, snap) {
    if (snap.connectionState != ConnectionState.done) {
      return const CircularProgressIndicator();
    }
    if (snap.hasError) return Text('Error: ${snap.error}');
    if (!snap.hasData || snap.data == null) {
      return const Text('No data');
    }
    return Text(snap.data!);
  },
)

// ✅ Good 4 — give Container a size
Container(
  width: 100,
  height: 100,
  color: Colors.red,
)

// Debug tools
// 1. Flutter Inspector — inspect widget tree
// 2. debugPaintSizeEnabled = true;  — show boundaries
// 3. print('Building $widget');  — check if build is called
// 4. FlutterError.onError = (details) => print(details);  — catch errors
```

### Debugging Checklist
```
1. Check console for errors (red text)
2. Use Flutter Inspector to see widget tree
3. Is there a Material/Scaffold ancestor?
4. Are constraints bounded? (ListView in Column needs Expanded)
5. Is the Future returning data? (check connectionState)
6. Is the widget actually in the tree? (Inspector)
7. Is opacity 0 or visibility false?
8. Is the widget behind another widget? (Stack order)
```

### Key Takeaway
- Blank screen = missing ancestor (Material), unbounded constraints, or null data
- `Scaffold` provides Material, directionality, and media query
- `ListView` in `Column` needs `Expanded` (bounded height)
- `FutureBuilder` must handle loading, error, and null states
- Use Flutter Inspector to verify widget is in the tree

---

## Scenario 5: "setState() or markNeedsBuild() called during build"

### Problem
The app crashes with `setState() or markNeedsBuild() called during build`.

```dart
// ❌ Bad — calling setState during build
class _MyScreenState extends State<MyScreen> {
  @override
  Widget build(BuildContext context) {
    setState(() => _count++);  // ❌ Can't call during build!
    return Text('$_count');
  }
}

// ❌ Bad — calling provider methods during build
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    context.read<MyModel>().updateData();  // ❌ Triggers rebuild during build
    return Text('data');
  }
}

// ❌ Bad — showing dialog during build
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    showDialog(context: context, builder: (_) => AlertDialog());  // ❌
    return Container();
  }
}
```

### Solution: Defer to after build

```dart
// ✅ Good 1 — use addPostFrameCallback
class _MyScreenState extends State<MyScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      // Runs after the current build frame
      setState(() => _count++);
      showDialog(context: context, builder: (_) => AlertDialog());
    });
  }

  @override
  Widget build(BuildContext context) {
    return Text('$_count');
  }
}

// ✅ Good 2 — use didChangeDependencies for context-dependent init
@override
void didChangeDependencies() {
  super.didChangeDependencies();
  // Safe to use context here
  final theme = Theme.of(context);
  if (_initialTheme != theme) {
    _initialTheme = theme;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _updateTheme();
    });
  }
}

// ✅ Good 3 — use initState for one-time setup (no context)
@override
void initState() {
  super.initState();
  _loadData();  // Start async — setState happens after build
}

Future<void> _loadData() async {
  final data = await api.fetch();
  if (mounted) setState(() => _data = data);
}

// ✅ Good 4 — use LayoutBuilder for layout-dependent logic
LayoutBuilder(
  builder: (context, constraints) {
    if (constraints.maxWidth > 600) {
      return WideLayout();
    }
    return NarrowLayout();
  },
)
```

### When to Use What
| Need | Use |
|------|-----|
| One-time init (no context) | `initState()` |
| Context-dependent init | `didChangeDependencies()` |
| After first frame | `addPostFrameCallback()` |
| Layout-dependent | `LayoutBuilder` |
| React to widget update | `didUpdateWidget()` |

### Key Takeaway
- Never call `setState()` inside `build()` — causes infinite rebuild loop
- Never show dialogs/snackbars during `build()` — use `addPostFrameCallback`
- `initState()` — one-time setup, no `context` access (no `Theme.of(context)`)
- `didChangeDependencies()` — safe for context access, called after `initState`
- `addPostFrameCallback` — defer to after the current frame completes

---

## 🔗 Related Topics
- [Widgets](../beginner/Widgets.md)
- [Flutter Internals](../advanced/FlutterInternals.md)
- [Performance](../advanced/Performance.md)
