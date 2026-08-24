# Flutter Basics

## 📖 Explanation

Flutter is Google's UI toolkit for building natively compiled applications for mobile, web, and desktop from a single codebase. It uses Dart as its programming language and renders UI using its own rendering engine (Skia/Impeller), not platform widgets.

### Flutter Architecture
```
┌─────────────────────────────────────┐
│           Your Dart Code            │
├─────────────────────────────────────┤
│         Flutter Framework           │
│  (Widgets, Rendering, Animation,    │
│   Painting, Gestures, Foundation)   │
├─────────────────────────────────────┤
│          Flutter Engine             │
│  (Skia, Text, Dart Runtime)         │
├─────────────────────────────────────┤
│       Platform Embedder             │
│  (Android, iOS, Web, Desktop)       │
├─────────────────────────────────────┤
│    OS (Android/iOS/Web/Desktop)     │
└─────────────────────────────────────┘
```

- **Dart Code** → compiled to native ARM (mobile) or JS/WASM (web)
- **Flutter Engine** → renders UI using Skia/Impeller (not platform widgets)
- **Platform Embedder** → manages event loop, input, platform channels

### Key Characteristics
- **Single codebase**: One codebase for Android, iOS, web, and desktop
- **Hot reload**: Sub-second code changes reflected instantly without losing state
- **Own rendering**: Uses Skia/Impeller engine, not native platform widgets
- **Widget-based**: Everything is a widget — composition over inheritance

### `main()` and `runApp()`
The `main()` function is the entry point of every Dart/Flutter program. `runApp()` attaches the root widget to the screen.

```dart
void main() {
  runApp(const MyApp());  // Attaches widget tree to screen
}

// runApp() does:
// 1. Creates a RenderObject for the root widget
// 2. Attaches it to the screen
// 3. Schedules first frame
```

### Hot Reload vs Hot Restart
| Feature | Hot Reload | Hot Restart |
|---------|-----------|-------------|
| Speed | <1 second | 2-3 seconds |
| State | Preserved | Reset to initial |
| How to trigger | `r` (in terminal) | `R` (capital) |
| Use case | UI tweaks, logic changes | State-related changes |
| App restarts? | ❌ No | ✅ Yes |

### `pubspec.yaml`
The `pubspec.yaml` is the configuration file for a Flutter project — defines dependencies, assets, fonts, and metadata.

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
```

### Widget Tree, Element Tree, Render Tree
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

- **Widget** — immutable configuration (blueprint)
- **Element** — mutable instance, manages state, diffing
- **RenderObject** — measures, paints, handles input

### `const` vs `final` in Dart
| `const` | `final` |
|----------|---------|
| Compile-time constant | Runtime constant |
| Deeply immutable | Reference is immutable |
| Must be known at compile time | Can be assigned at runtime |
| `const [1, 2, 3]` | `final x = DateTime.now()` |
| Better performance (canonicalized) | Slightly slower |

### Build Modes
| Mode | Compilation | Hot Reload | Performance | Use Case |
|------|-------------|------------|-------------|----------|
| Debug | JIT | ✅ Yes | Slow | Development |
| Profile | AOT | ❌ No | Near-release | Performance testing |
| Release | AOT | ❌ No | Fast | Production |

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
      title: 'My App',
      home: Scaffold(
        appBar: AppBar(title: const Text('Home')),
        body: const Center(child: Text('Hello Flutter!')),
      ),
    );
  }
}

// With async initialization
void mainAsync() async {
  WidgetsFlutterBinding.ensureInitialized();  // Required before async
  await Firebase.initializeApp();
  await SharedPreferences.getInstance();
  runApp(const MyApp());
}
```

### Output
```
A running Flutter app with:
- AppBar showing "Home"
- Centered text "Hello Flutter!"
```

---

## ❓ Interview Questions

1. **What is Flutter and how does it work?**
   - Flutter is Google's UI toolkit for building natively compiled applications for mobile, web, and desktop from a single codebase. It uses Dart and its own rendering engine (Skia/Impeller) to draw widgets, rather than using native platform widgets. The architecture consists of: Dart Code → Flutter Framework (widgets, rendering) → Flutter Engine (Skia, text, Dart runtime) → Platform Embedder → OS.

2. **What is the difference between hot reload and hot restart?**
   - Hot reload injects updated source code into the running VM while preserving state — it takes <1 second and is triggered with `r`. Hot restart restarts the app entirely, resetting all state to initial values — it takes 2-3 seconds and is triggered with `R` (capital). Use hot reload for UI tweaks and logic changes; use hot restart when state-related changes cause issues.

3. **What is `pubspec.yaml` and what does it contain?**
   - `pubspec.yaml` is the configuration file for a Flutter project. It contains: project metadata (name, description, version), environment constraints (SDK and Flutter versions), dependencies (packages from pub.dev), dev_dependencies (testing, linting), and Flutter-specific config (assets, fonts, material design flag). Key commands: `flutter pub get` (install), `flutter pub add` (add package), `flutter pub upgrade` (upgrade).

4. **What is `main()` and `runApp()`?**
   - `main()` is the entry point of every Dart/Flutter program. `runApp()` attaches the given widget to the screen by creating a RenderObject for the root widget, attaching it to the screen, and scheduling the first frame. For async initialization (Firebase, SharedPreferences), use `WidgetsFlutterBinding.ensureInitialized()` before any async calls in `main()`.

5. **What is the widget tree vs element tree vs render tree?**
   - **Widget tree** — immutable descriptions/blueprints of the UI. Widgets are cheap and recreated on every rebuild. **Element tree** — mutable instances that manage lifecycle, state, and diffing. Elements are the bridge between widgets and render objects. **Render tree** — RenderObjects that measure, paint, and handle hit-testing. When `setState()` is called: element marks itself dirty → framework schedules rebuild → new widget compared with old → if same type, update reference; if different, unmount old and mount new → RenderObject updated.

6. **What is `BuildContext`?**
   - `BuildContext` is a handle to the position of a widget in the widget tree. It's used to: (1) Find ancestors (Theme, MediaQuery), (2) Navigate (Navigator.of(context)), (3) Show dialogs (showDialog), (4) Access inherited widgets (Provider.of). It represents the element in the element tree corresponding to the widget.

7. **What is the difference between `const` and `final` in Dart?**
   - `const` is a compile-time constant — deeply immutable, the value must be known at compile time, and it's canonicalized (same instance reused). `final` is a runtime constant — the reference is assigned once but can be at runtime. `const` widgets never rebuild (same instance reused); `final` can hold mutable objects (e.g., `final list = [1, 2]; list.add(3)` works). Use `const` wherever possible for performance.

8. **What is the difference between `StatelessWidget`, `StatefulWidget`, and `InheritedWidget`?**
   - `StatelessWidget` — immutable, no internal state, rebuilds when parent rebuilds. Use for static UI. `StatefulWidget` — has mutable state via `State` object, can rebuild independently via `setState()`. Use for interactive UI. `InheritedWidget` — efficiently propagates data down the tree; only widgets that explicitly depend on it rebuild. It's the foundation of Provider, Theme, MediaQuery.

9. **What are Flutter's build modes and when do you use each?**
   - Debug mode uses JIT compilation with hot reload — for development. Profile mode uses AOT without hot reload — for performance testing (near-release performance with profiling). Release mode uses AOT, fully optimized — for production. Always test performance in **profile mode** — debug mode has assertions, debug checks, and JIT overhead that don't reflect real-world performance.

10. **What is the `Key` class and how does `GlobalKey` differ from `ValueKey`?**
    - `Key` uniquely identifies a widget in the tree, used for preserving state across rebuilds. `ValueKey(value)` — identifies by value (most common, fast). `GlobalKey` — accesses state from anywhere in the tree (expensive, use sparingly). Use `ValueKey` for list items with unique IDs. Use `GlobalKey` for form validation and accessing state from parent widgets. Avoid `GlobalKey` for lists — it's expensive and can cause issues.

---

## 🔗 Related Topics
- [Dart Basics](DartBasics.md)
- [Widgets](Widgets.md)
- [Layouts](Layouts.md)
