# Flutter Internals

## Q1: What is the Flutter rendering pipeline?

```
User Input / setState()
        ↓
   Animation Frame (vsync)
        ↓
┌─────────────────────────────────────────┐
│  1. Build Phase                         │
│     Widget tree → Element tree          │
│     (create/update Elements)            │
├─────────────────────────────────────────┤
│  2. Layout Phase                        │
│     Element tree → RenderObject tree    │
│     (measure sizes, position children)  │
├─────────────────────────────────────────┤
│  3. Paint Phase                         │
│     RenderObject tree → Layer tree      │
│     (draw commands, Skia/Impeller)      │
├─────────────────────────────────────────┤
│  4. Composite Phase                     │
│     Layer tree → composited frame       │
│     (merge layers, transform)           │
├─────────────────────────────────────────┤
│  5. Rasterize (GPU)                     │
│     Skia/Impeller → screen pixels       │
└─────────────────────────────────────────┘
        ↓
     Display on screen
```

```
Widget Tree          Element Tree           RenderObject Tree
(immutable)          (mutable, diff)        (layout, paint, hit-test)

MaterialApp    →   MaterialAppElement  →   RenderView
  Scaffold     →     ScaffoldElement   →   RenderBox
    Column     →       ColumnElement    →   RenderFlex
      Text     →         TextElement    →   RenderParagraph
```

---

## Q2: What is the difference between Widget, Element, and RenderObject?

| Layer | Role | Mutable | Lifecycle |
|-------|------|---------|-----------|
| Widget | Configuration (blueprint) | ❌ Immutable | Recreated on rebuild |
| Element | Instance, manages tree position | ✅ Mutable | Persists across rebuilds |
| RenderObject | Layout, paint, hit-testing | ✅ Mutable | Persists, updated in place |

```dart
// Widget — immutable description
// Recreated every build — cheap (just config)
const Text('Hello')

// Element — bridges Widget and RenderObject
// Persists across rebuilds — manages diffing
// When widget changes: Element.update(newWidget)
// When type changes: unmount old, mount new

// RenderObject — does the actual work
// Measures size, paints to canvas, handles input
// Persists — updated with new layout/paint info

// Example: setState() flow
setState(() => count++);
  → Element marked dirty
  → Framework schedules rebuild
  → build() returns new Widget tree
  → Element compares old vs new Widget
  → If same type: update Element's widget ref
  → RenderObject.update() with new properties
  → Relayout + repaint
```

---

## Q3: What is `Element.canUpdate` and how does diffing work?

```dart
// canUpdate — determines if old widget can be updated to new
static bool canUpdate(Widget oldWidget, Widget newWidget) {
  return oldWidget.runtimeType == newWidget.runtimeType
      && oldWidget.key == newWidget.key;
}

// If canUpdate → true: Element stays, widget ref updated
// If canUpdate → false: Old Element unmounted, new Element mounted

// Example 1: Same type, no key → update in place
// Old: Text('A')  →  New: Text('B')
// canUpdate: true (both Text, no key)
// Result: Element updates, RenderParagraph gets new text

// Example 2: Different type → unmount + mount
// Old: Text('A')  →  New: Icon(Icons.star)
// canUpdate: false (Text != Icon)
// Result: TextElement unmounted, IconElement mounted

// Example 3: Same type, different key → unmount + mount
// Old: Item(key: ValueKey('1'))  →  New: Item(key: ValueKey('2'))
// canUpdate: false (different keys)
// Result: Old Element unmounted, new Element mounted
```

---

## Q4: What is the Dart event loop?

```
Dart Event Loop (single-threaded)

┌─────────────────────────────────┐
│         Event Queue              │  ← I/O, timers, microtasks
│  [event1] [event2] [event3]     │
└──────────┬──────────────────────┘
           ↓
┌─────────────────────────────────┐
│       Microtask Queue            │  ← Highest priority
│  [micro1] [micro2]              │  ← Runs before next event
└──────────┬──────────────────────┘
           ↓
┌─────────────────────────────────┐
│         Event Loop                │
│  while (true):                    │
│    1. Run all microtasks          │
│    2. Run one event               │
│    3. Repeat                      │
└─────────────────────────────────┘
```

```dart
// Event queue — I/O, timers, user input
Future.delayed(Duration(seconds: 1), () => print('event'));

// Microtask queue — runs before next event
scheduleMicrotask(() => print('microtask'));

// Order of execution
print('1');                          // 1. Sync
Future(() => print('2'));           // 3. Event queue
scheduleMicrotask(() => print('3')); // 2. Microtask queue
print('4');                          // 1. Sync

// Output: 1, 4, 3, 2

// Isolates — separate event loops (true parallelism)
// Main isolate runs UI
// Worker isolate runs heavy computation
```

---

## Q5: What is Skia vs Impeller?

```
Skia (Flutter < 3.10):
  - 2D graphics library
  - CPU-based rendering
  - Shader compilation at runtime (jank on first frame)
  - Mature, stable

Impeller (Flutter 3.10+):
  - New rendering engine
  - GPU-based rendering (Metal on iOS, Vulkan on Android)
  - Pre-compiled shaders (no jank)
  - Better performance, especially on iOS
  - Default on iOS since Flutter 3.10
  - Default on Android since Flutter 3.27
```

| Feature | Skia | Impeller |
|---------|------|----------|
| Rendering | CPU + GPU | GPU-first |
| Shaders | Runtime compile | Pre-compiled |
| Jank | First-frame jank | No shader jank |
| Platform | All | iOS, Android |
| Status | Legacy | Default (3.27+) |

```dart
// Enable Impeller (Android, if not default)
// android/app/src/main/AndroidManifest.xml
<meta-data
  android:name="io.flutter.embedding.android.EnableImpeller"
  android:value="true" />

// Check renderer
// flutter run --verbose  → look for "Using Impeller"
```

---

## Q6: What is `RepaintBoundary` and how does it work?

```dart
// RepaintBoundary — creates a separate layer
// Child is painted once, cached as bitmap
// If child doesn't change, no repaint needed

// ❌ Without RepaintBoundary — entire list repaints on scroll
ListView.builder(
  itemBuilder: (context, index) => ComplexItem(item: items[index]),
)

// ✅ With RepaintBoundary — each item painted once, cached
ListView.builder(
  itemBuilder: (context, index) => RepaintBoundary(
    child: ComplexItem(item: items[index]),
  ),
)

// When to use:
// ✅ Complex items in scrolling lists
// ✅ Static content inside animated parent
// ✅ Heavy CustomPaint that doesn't change often

// When NOT to use:
// ❌ Simple widgets (overhead > benefit)
// ❌ Frequently changing content (cache invalidated constantly)
// ❌ Small widgets (memory overhead)
```

### How It Works
```
Without RepaintBoundary:
  Parent dirty → all children repaint

With RepaintBoundary:
  Parent dirty → check if child changed
    → child not dirty → reuse cached layer (no repaint)
    → child dirty → repaint child only, update cache
```

---

## Q7: What is the Flutter engine architecture?

```
┌─────────────────────────────────────────────┐
│                Dart Code                     │
│            (Your Flutter App)                │
├─────────────────────────────────────────────┤
│           Flutter Framework                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐    │
│  │ Widgets  │ │Rendering │ │Animation │    │
│  │          │ │          │ │          │    │
│  │ Material │ │ Layer    │ │ Scheduler│    │
│  │ Cupertino│ │ Tree    │ │          │    │
│  └──────────┘ └──────────┘ └──────────┘    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐    │
│  │ Painting │ │Gestures  │ │Foundation│    │
│  └──────────┘ └──────────┘ └──────────┘    │
├─────────────────────────────────────────────┤
│            Flutter Engine (C++)              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐    │
│  │ Dart VM  │ │  Skia/   │ │  Text    │    │
│  │          │ │ Impeller │ │  Shaper  │    │
│  └──────────┘ └──────────┘ └──────────┘    │
├─────────────────────────────────────────────┤
│           Platform Embedder                  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐    │
│  │ Android  │ │   iOS    │ │  Web     │    │
│  │ (Java/Kt)│ │ (Swift)  │ │ (JS/WASM)│    │
│  └──────────┘ └──────────┘ └──────────┘    │
├─────────────────────────────────────────────┤
│              OS / Browser                    │
└─────────────────────────────────────────────┘
```

### Key Components
| Component | Language | Role |
|-----------|----------|------|
| Dart VM | C++ | Runs Dart code, GC, isolates |
| Skia/Impeller | C++ | 2D graphics rendering |
| Text Shaper | C++ | Text layout, fonts, BiDi |
| Platform Embedder | Java/Kotlin, Swift, JS | Manages surface, input, lifecycle |
| Flutter Framework | Dart | Widgets, rendering, animation |

### Compilation Modes
```
Debug mode:
  - JIT (Just-In-Time) compilation
  - Dart VM interprets + compiles hot
  - Enables hot reload
  - Slow performance

Release mode:
  - AOT (Ahead-Of-Time) compilation
  - Dart → native ARM machine code
  - Tree-shaking (removes unused code)
  - Fast performance, smaller binary

Profile mode:
  - AOT like release
  - Keeps debugging/s profiling info
  - For performance testing
```

```bash
flutter run --debug       # Development (JIT, hot reload)
flutter run --profile      # Performance testing (AOT + profiling)
flutter run --release      # Production (AOT, optimized)
flutter build apk --release  # Build release APK
```

---

## 🔗 Related Topics
- [Performance](Performance.md)
- [Platform Channels](PlatformChannels.md)
- [Basics](../beginner/Basics.md)
