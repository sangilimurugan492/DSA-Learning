# Flutter Internals

## 📖 Explanation

Flutter's internals consist of the rendering pipeline, the widget/element/render tree, the Dart event loop, the engine, and compilation modes. Understanding these helps with performance optimization and debugging.

### Rendering Pipeline
```
User Input / setState()
        ↓
   Animation Frame (vsync)
        ↓
┌─────────────────────────────────────────┐
│  1. Build Phase    → Widget → Element   │
│  2. Layout Phase   → Element → Render   │
│  3. Paint Phase    → Render → Layer     │
│  4. Composite Phase → Layer → Frame     │
│  5. Rasterize (GPU) → Frame → Screen   │
└─────────────────────────────────────────┘
```

### Widget vs Element vs RenderObject
| Layer | Role | Mutable | Lifecycle |
|-------|------|---------|-----------|
| Widget | Configuration (blueprint) | ❌ Immutable | Recreated on rebuild |
| Element | Instance, manages tree position | ✅ Mutable | Persists across rebuilds |
| RenderObject | Layout, paint, hit-testing | ✅ Mutable | Persists, updated in place |

### Dart Event Loop
Dart is single-threaded with an event loop. Microtasks run before events.
```
1. Run all microtasks (highest priority)
2. Run one event
3. Repeat
```

### Skia vs Impeller
| Feature | Skia | Impeller |
|---------|------|----------|
| Rendering | CPU + GPU | GPU-first |
| Shaders | Runtime compile | Pre-compiled |
| Jank | First-frame jank | No shader jank |
| Platform | All | iOS, Android |
| Status | Legacy | Default (3.27+) |

### Compilation Modes
| Mode | Compilation | Hot Reload | Performance | Use Case |
|------|-------------|------------|-------------|----------|
| Debug | JIT | ✅ Yes | Slow | Development |
| Profile | AOT | ❌ No | Near-release | Performance testing |
| Release | AOT | ❌ No | Fast | Production |

### Flutter Engine Architecture
```
┌─────────────────────────────┐
│        Dart Code (App)       │
├─────────────────────────────┤
│    Flutter Framework (Dart)  │
│  Widgets · Rendering · Anim  │
├─────────────────────────────┤
│   Flutter Engine (C++)        │
│  Dart VM · Skia/Impeller · Text│
├─────────────────────────────┤
│   Platform Embedder           │
│  Android · iOS · Web · Desktop│
├─────────────────────────────┤
│        OS / Browser           │
└─────────────────────────────┘
```

### RepaintBoundary
`RepaintBoundary` creates a separate layer — the child is painted once and cached. If the child doesn't change, no repaint is needed. Use for complex items in scrolling lists and static content inside animated parents.

---

## 🧪 Code Example

```dart
// ── setState() flow ──
setState(() => count++);
//   → Element marked dirty
//   → Framework schedules rebuild
//   → build() returns new Widget tree
//   → Element compares old vs new Widget (canUpdate: same type + key)
//   → If same type: update Element's widget ref
//   → RenderObject.update() with new properties
//   → Relayout + repaint

// ── Element.canUpdate (diffing) ──
static bool canUpdate(Widget oldWidget, Widget newWidget) {
  return oldWidget.runtimeType == newWidget.runtimeType
      && oldWidget.key == newWidget.key;
}
// Same type + same key → update in place
// Different type or different key → unmount old, mount new

// ── Dart event loop order ──
print('1');                          // 1. Sync
Future(() => print('2'));           // 3. Event queue
scheduleMicrotask(() => print('3')); // 2. Microtask queue
print('4');                          // 1. Sync
// Output: 1, 4, 3, 2

// ── RepaintBoundary ──
ListView.builder(
  itemBuilder: (context, index) => RepaintBoundary(
    child: ComplexItem(item: items[index]),  // Painted once, cached
  ),
)

// ── Enable Impeller (Android) ──
// android/app/src/main/AndroidManifest.xml
// <meta-data
//   android:name="io.flutter.embedding.android.EnableImpeller"
//   android:value="true" />

// ── Build modes ──
// flutter run --debug       # JIT, hot reload
// flutter run --profile      # AOT + profiling
// flutter run --release      # AOT, optimized
```

### Output
```
Understanding of Flutter internals:
- Rendering pipeline: Build → Layout → Paint → Composite → Rasterize
- Widget/Element/RenderObject separation
- Dart event loop: microtasks before events
- Impeller vs Skia rendering
- Debug/Profile/Release compilation modes
```

---

## ❓ Interview Questions

1. **What is the Flutter rendering pipeline?**
   - The rendering pipeline has 5 phases: (1) Build — Widget tree → Element tree (create/update Elements). (2) Layout — Element tree → RenderObject tree (measure sizes, position children). (3) Paint — RenderObject tree → Layer tree (draw commands). (4) Composite — Layer tree → composited frame (merge layers, transform). (5) Rasterize — Skia/Impeller → screen pixels (GPU). Triggered by `setState()` or user input. The pipeline runs at vsync (60fps = 16ms budget, 120fps = 8ms). If any phase exceeds the budget, you get jank (dropped frames).

2. **What is the difference between Widget, Element, and RenderObject?**
   - **Widget** — immutable configuration/blueprint. Cheap, recreated on every rebuild. E.g., `const Text('Hello')`. **Element** — mutable instance that manages lifecycle and tree position. Persists across rebuilds. Bridges Widget and RenderObject. When widget changes, `Element.update(newWidget)` is called. **RenderObject** — does the actual work: measures size, paints to canvas, handles hit-testing. Persists and is updated in place. Example: `setState()` → Element marked dirty → build() returns new Widget → Element compares old vs new → if same type, update ref → `RenderObject.update()` → relayout + repaint.

3. **What is `Element.canUpdate` and how does diffing work?**
   - `canUpdate(oldWidget, newWidget)` returns true if `runtimeType` and `key` both match. If true: the Element stays and updates its widget reference — efficient. If false: old Element is unmounted and new Element is mounted — more expensive. Example 1: `Text('A')` → `Text('B')` — same type, no key → canUpdate = true → update in place. Example 2: `Text('A')` → `Icon(Icons.star)` — different type → canUpdate = false → unmount + mount. Example 3: `Item(key: ValueKey('1'))` → `Item(key: ValueKey('2'))` — same type, different key → canUpdate = false → unmount + mount. Keys are essential for list reordering to preserve state.

4. **What is the Dart event loop?**
   - Dart is single-threaded with an event loop. It processes: (1) Microtask queue — highest priority, runs all microtasks before next event. (2) Event queue — I/O, timers, user input, Futures. The loop: run all microtasks → run one event → repeat. `Future(() => ...)` goes to event queue. `scheduleMicrotask(() => ...)` goes to microtask queue. Order: sync code → microtasks → events. Isolates provide true parallelism — each has its own event loop and memory heap (no shared state). Communication between isolates uses SendPort/ReceivePort.

5. **What is Skia vs Impeller?**
   - **Skia** (Flutter < 3.10): 2D graphics library, CPU-based rendering, shader compilation at runtime (causes first-frame jank). **Impeller** (Flutter 3.10+): new rendering engine, GPU-based rendering (Metal on iOS, Vulkan on Android), pre-compiled shaders (no jank), better performance. Impeller is default on iOS since Flutter 3.10 and on Android since Flutter 3.27. Enable on Android: `AndroidManifest.xml` meta-data `io.flutter.embedding.android.EnableImpeller = true`. Impeller eliminates shader compilation jank that Skia had on first animation. Check with `flutter run --verbose` → look for "Using Impeller".

6. **What is `RepaintBoundary` and how does it work?**
   - `RepaintBoundary` creates a separate layer — the child is painted once and cached as a bitmap. If the child doesn't change, the cached layer is reused without repainting. Without it: parent dirty → all children repaint. With it: parent dirty → check if child changed → if not dirty, reuse cached layer (no repaint). Use for: complex items in scrolling lists, static content inside animated parents, heavy CustomPaint. Don't use for: simple widgets (overhead > benefit), frequently changing content (cache invalidated constantly), small widgets (memory overhead). `ListView.builder` already wraps items in RepaintBoundary.

7. **What is the Flutter engine architecture?**
   - Flutter has 4 layers: (1) Dart Code — your Flutter app. (2) Flutter Framework (Dart) — Widgets, Rendering, Animation, Painting, Gestures, Foundation. (3) Flutter Engine (C++) — Dart VM, Skia/Impeller (2D graphics), Text Shaper (fonts, BiDi). (4) Platform Embedder — Android (Java/Kotlin), iOS (Swift), Web (JS/WASM), Desktop. The engine manages the surface, event loop, and platform channels. Key: Flutter doesn't use native platform widgets — it renders everything itself using Skia/Impeller. This is why Flutter looks identical on all platforms. The platform embedder manages lifecycle, input, and surface creation.

8. **What are Flutter's compilation modes?**
   - **Debug mode**: JIT (Just-In-Time) compilation — Dart VM interprets + compiles hot. Enables hot reload. Slow performance, large binary. For development. **Profile mode**: AOT (Ahead-Of-Time) like release but keeps debugging/profiling info. Near-release performance. For performance testing. **Release mode**: AOT — Dart compiled to native ARM machine code. Tree-shaking removes unused code. Fast performance, smaller binary. For production. Always test performance in **profile mode** — debug has assertions and JIT overhead that don't reflect real-world performance. Commands: `flutter run --debug`, `flutter run --profile`, `flutter run --release`, `flutter build apk --release`.

9. **What is tree shaking in Flutter?**
   - Tree shaking is the process of removing unused code from the final binary during AOT compilation. The compiler analyzes the dependency graph and removes functions, classes, and libraries that are never called. This significantly reduces app size in release mode. Flutter also tree-shakes icon fonts — only icons actually used in the app are included in the binary (use `--tree-shake-icons`). Tree shaking only works in release/profile mode (AOT), not in debug mode (JIT). Unused dependencies in pubspec.yaml are not tree-shaken — remove them manually.

10. **What is the difference between JIT and AOT in Flutter?**
    - **JIT (Just-In-Time)**: Dart VM compiles code at runtime. Enables hot reload (inject updated source into running VM). Used in debug mode. Slower execution, larger memory footprint. **AOT (Ahead-Of-Time)**: Dart code is compiled to native machine code before the app runs. Used in release/profile mode. Faster execution, smaller memory footprint, tree-shaking enabled. No hot reload. JIT is for development (fast iteration with hot reload). AOT is for production (best performance). The Dart VM in debug mode also does "hot reload" by comparing old and new source and patching the running code — this is only possible with JIT.

---

## 🔗 Related Topics
- [Performance](Performance.md)
- [Platform Channels](PlatformChannels.md)
- [Basics](../beginner/Basics.md)
