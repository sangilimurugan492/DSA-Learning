# Flutter Basics

## Q1: What is Flutter and how does it work?

Flutter is Google's UI toolkit for building natively compiled applications for mobile, web, and desktop from a single codebase.

```
Flutter Architecture
┌─────────────────────────────────────┐
│           Your Dart Code            │
├─────────────────────────────────────┤
│         Flutter Framework           │
│  (Widgets, Rendering, Animation,    │
│   Painting, Gestures, Foundation)   │
├─────────────────────────────────────┤
│          Flutter Engine              │
│  (Skia, Text, Dart Runtime)          │
├─────────────────────────────────────┤
│       Platform Embedder              │
│  (Android, iOS, Web, Desktop)        │
├─────────────────────────────────────┤
│    OS (Android/iOS/Web/Desktop)      │
└─────────────────────────────────────┘
```

- **Dart Code** → compiled to native ARM (mobile) or JS/WASM (web)
- **Flutter Engine** → renders UI using Skia/Impeller (not platform widgets)
- **Platform Embedder** → manages event loop, input, platform channels

```dart
// Minimal Flutter app
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'My App',
      home: Scaffold(
        appBar: AppBar(title: const Text('Home')),
        body: const Center(child: Text('Hello Flutter!')),
      ),
    );
  }
}
```

---

## Q2: What is the difference between hot reload and hot restart?

| Feature | Hot Reload | Hot Restart |
|---------|-----------|-------------|
| Speed | <1 second | 2-3 seconds |
| State | Preserved | Reset to initial |
| How to trigger | `r` (in terminal) | `R` (capital) |
| Use case | UI tweaks, logic changes | State-related changes |
| App restarts? | ❌ No | ✅ Yes |

```dart
// Hot Reload preserves state
class CounterPage extends StatefulWidget {
  const CounterPage({super.key});
  @override
  State<CounterPage> createState() => _CounterPageState();
}

class _CounterPageState extends State<CounterPage> {
  int count = 0;  // This is preserved on hot reload

  @override
  Widget build(BuildContext context) {
    return Text('$count');  // Change color → hot reload → count stays
  }
}

// Hot Restart resets state
// count goes back to 0
```

---

## Q3: What is `pubspec.yaml`?

```yaml
name: my_app
description: A Flutter application
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'
  flutter: '>=3.10.0'

dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.5
  http: ^1.1.0
  provider: ^6.0.5

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^2.0.3

flutter:
  uses-material-design: true
  assets:
    - assets/images/
    - assets/config.json
  fonts:
    - family: Roboto
      fonts:
        - asset: assets/fonts/Roboto-Regular.ttf
        - asset: assets/fonts/Roboto-Bold.ttf
          weight: 700
```

### Key Commands
```bash
flutter pub get        # Install dependencies
flutter pub upgrade    # Upgrade to latest compatible
flutter pub add http   # Add a package
flutter pub remove http # Remove a package
flutter pub outdated   # Check for outdated packages
```

---

## Q4: What is `main()` and `runApp()`?

```dart
// main() — entry point of every Dart program
void main() {
  runApp(const MyApp());  // Attaches widget tree to screen
}

// runApp() does:
// 1. Creates a RenderObject for the root widget
// 2. Attaches it to the screen
// 3. Schedules first frame

// With async initialization
void main() async {
  WidgetsFlutterBinding.ensureInitialized();  // Required before async
  await Firebase.initializeApp();
  await SharedPreferences.getInstance();
  runApp(const MyApp());
}
```

---

## Q5: What is the widget tree vs element tree vs render tree?

```
Widget Tree (immutable descriptions)
  MaterialApp
    └── Scaffold
         └── AppBar
         └── Center
              └── Text

Element Tree (mutable, manages lifecycle)
  MaterialAppElement
    └── ScaffoldElement
         └── AppBarElement
         └── CenterElement
              └── TextElement

Render Tree (layout, painting, hit-testing)
  RenderView
    └── RenderBox (Scaffold)
         └── RenderBox (AppBar)
         └── RenderPositionedBox (Center)
              └── RenderParagraph (Text)
```

```dart
// Widget — immutable configuration (blueprint)
// Element — mutable instance, manages state, diffing
// RenderObject — measures, paints, handles input

// When setState() is called:
// 1. Element marks itself dirty
// 2. Framework schedules rebuild
// 3. New widget compared with old (canUpdate)
// 4. If same type → update element's widget reference
// 5. If different type → unmount old, mount new
// 6. RenderObject updated with new layout/paint
```

---

## Q6: What is `BuildContext`?

```dart
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    // context is the position in the widget tree
    // Used to:
    // 1. Find ancestors
    final theme = Theme.of(context);
    final mediaQuery = MediaQuery.of(context);

    // 2. Navigate
    Navigator.of(context).push(...);

    // 3. Show dialogs
    showDialog(context: context, builder: ...);

    // 4. Access inherited widgets
    final provider = Provider.of<MyModel>(context);

    return Text('Hello', style: theme.textTheme.bodyLarge);
  }
}
```

> **Key:** `BuildContext` is a handle to the location of a widget in the tree. It's used to look up inherited widgets, themes, and navigate.

---

## Q7: What is the difference between `const` and `final` in Dart?

```dart
// const — compile-time constant (deeply immutable)
const int x = 42;
const List<int> nums = [1, 2, 3];
const Point p = Point(0, 0);

// final — runtime constant (assigned once)
final int y = DateTime.now().hour;  // Can't be const — runtime value
final List<int> items = [1, 2, 3];
items.add(4);  // ✅ Allowed — final is reference, not deeply immutable

// const constructor
class Point {
  final double x, y;
  const Point(this.x, this.y);  // const constructor
}

// In widgets — const constructors save rebuilds
const Text('Hello');  // Never rebuilds — same instance reused
```

| `const` | `final` |
|----------|---------|
| Compile-time constant | Runtime constant |
| Deeply immutable | Reference is immutable |
| Must be known at compile time | Can be assigned at runtime |
| `const [1, 2, 3]` | `final x = DateTime.now()` |
| Better performance (canonicalized) | Slightly slower |

---

## Q8: What is the difference between `StatelessWidget`, `StatefulWidget`, and `InheritedWidget`?

```dart
// StatelessWidget — immutable, rebuilt when parent rebuilds or input changes
class Greeting extends StatelessWidget {
  final String name;
  const Greeting({super.key, required this.name});

  @override
  Widget build(BuildContext context) => Text('Hello, $name');
}

// StatefulWidget — has mutable state, can rebuild independently via setState()
class Counter extends StatefulWidget {
  const Counter({super.key});
  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  int _count = 0;
  @override
  Widget build(BuildContext context) => Text('$_count');
}

// InheritedWidget — efficiently propagates data down the tree
// Only dependents rebuild when data changes
class ThemeProvider extends InheritedWidget {
  final ThemeData theme;
  const ThemeProvider({super.key, required this.theme, required super.child});

  static ThemeProvider? of(BuildContext context) =>
      context.dependOnInheritedWidgetOfExactType<ThemeProvider>();

  @override
  bool updateShouldNotify(ThemeProvider oldWidget) => theme != oldWidget.theme;
}
```

| Widget Type | State | Rebuild Trigger | Use Case |
|-------------|-------|-----------------|----------|
| `StatelessWidget` | None | Parent rebuilds | Static UI, display |
| `StatefulWidget` | Mutable (`State`) | `setState()` | Interactive UI |
| `InheritedWidget` | Shared | `updateShouldNotify` | Propagate data down tree |

> **Key:** `InheritedWidget` is the foundation of Provider, Theme, MediaQuery — it only rebuilds widgets that explicitly depend on it via `dependOnInheritedWidgetOfExactType`.

---

## Q9: What are Flutter's build modes and when do you use each?

```dart
// Check build mode at compile time
const isDebug = kDebugMode;       // true in debug
const isProfile = kProfileMode;   // true in profile
const isRelease = kReleaseMode;   // true in release

// Use in code
if (kDebugMode) {
  print('Debug-only log');  // Stripped in release
}

// Conditional imports
// import 'debug_utils.dart' if (dart.library.html) 'web_utils.dart';
```

| Mode | Compilation | Hot Reload | Performance | Use Case |
|------|-------------|------------|-------------|----------|
| Debug | JIT | ✅ Yes | Slow | Development |
| Profile | AOT | ❌ No | Near-release | Performance testing |
| Release | AOT | ❌ No | Fast | Production |

```bash
# Run in different modes
flutter run --debug       # Default (JIT, hot reload)
flutter run --profile     # Performance testing (AOT + profiling)
flutter run --release     # Production-like (AOT, optimized)

# Build
flutter build apk --debug
flutter build apk --profile
flutter build apk --release
```

> **Rule:** Always test performance in **profile mode** — debug mode has assertions, debug checks, and JIT overhead that don't reflect real-world performance.

---

## Q10: What is the `Key` class and how does `GlobalKey` differ from `ValueKey`?

```dart
// ValueKey — identifies by value (most common)
ListView.builder(
  itemBuilder: (context, index) => ListItem(
    key: ValueKey(items[index].id),  // Unique by ID
    item: items[index],
  ),
)

// GlobalKey — access state from anywhere (use sparingly)
final _formKey = GlobalKey<FormState>();

Form(
  key: _formKey,
  child: Column(
    children: [
      TextFormField(validator: (v) => v!.isEmpty ? 'Required' : null),
      ElevatedButton(
        onPressed: () {
          if (_formKey.currentState!.validate()) {
            // Form is valid
          }
        },
        child: const Text('Submit'),
      ),
    ],
  ),
)

// GlobalKey for state access across widget tree
final _counterKey = GlobalKey<CounterState>();

Counter(key: _counterKey);

// Access state from anywhere
_counterKey.currentState?.increment();
```

| Key Type | Scope | Use Case | Performance |
|----------|-------|----------|-------------|
| `ValueKey` | Local | List items, reorderable | ✅ Fast |
| `ObjectKey` | Local | Identity by object | ✅ Fast |
| `UniqueKey` | Local | Force new instance | ⚠️ Always rebuilds |
| `GlobalKey` | Global | Access state, form validation | ⚠️ Expensive |
| `PageStorageKey` | Local | Preserve scroll position | ✅ Fast |

> **Warning:** Avoid `GlobalKey` for lists — it's expensive and can cause issues. Use `ValueKey` with unique IDs instead. `GlobalKey` is appropriate for form validation and accessing state from parent widgets.

---

## 🔗 Related Topics
- [Dart Basics](DartBasics.md)
- [Widgets](Widgets.md)
- [Layouts](Layouts.md)
