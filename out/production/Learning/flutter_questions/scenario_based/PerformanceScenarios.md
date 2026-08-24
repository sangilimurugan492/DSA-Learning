# Performance Scenarios

## 📖 Explanation

Performance optimization is critical for smooth Flutter apps (60 FPS / 120 FPS). These scenarios cover common performance issues and their solutions.

### Performance Metrics
| Metric | Target | Tool |
|--------|--------|------|
| Frame rate | 60 FPS (16ms/frame) | DevTools Performance |
| Build time | < 8ms | DevTools Timeline |
| GPU time | < 8ms | DevTools Performance |
| Memory | No leaks | DevTools Memory |
| App size | < 20MB | `flutter build apk --analyze-size` |
| Startup time | < 2s | `flutter run --trace-startup` |

### Common Performance Issues
| Issue | Symptom | Solution |
|-------|---------|----------|
| Rebuilds too many widgets | Jank on scroll | `Selector`, `const` widgets |
| Heavy build() method | Frame drops | Split widgets, cache computations |
| Large images | Memory spikes | `cacheWidth`, `cached_network_image` |
| Unbounded ListView | Slow scrolling | `ListView.builder` |
| Expensive animations | Jank | `RepaintBoundary`, `Transform` |
| Blocking main thread | Frozen UI | `compute()`, isolates |

### Performance Best Practices
- Use `const` constructors wherever possible
- Use `ListView.builder` / `GridView.builder` for large lists
- Use `Selector` / `Consumer` to limit rebuilds
- Wrap complex widgets in `RepaintBoundary`
- Use `cacheWidth`/`cacheHeight` for images
- Move heavy computation to isolates with `compute()`
- Avoid expensive operations in `build()`
- Use `const` keys for widgets in lists

---

## 🧪 Code Example

```dart
// ── Scenario 1: Optimizing List Rendering ──
// ❌ Bad — builds all 10,000 items at once
ListView(
  children: List.generate(10000, (i) => ListTile(title: Text('Item $i'))),
)

// ✅ Good — only builds visible items
ListView.builder(
  itemCount: 10000,
  itemBuilder: (_, i) => ListTile(title: Text('Item $i')),
)

// ✅ Better — with const and keys
ListView.builder(
  itemCount: 10000,
  itemBuilder: (_, i) => ListTile(
    key: ValueKey(i),
    title: Text('Item $i'),
  ),
)

// ── Scenario 2: Minimizing Widget Rebuilds ──
// ❌ Bad — entire screen rebuilds when count changes
class CounterScreen extends StatelessWidget {
  Widget build(BuildContext context) {
    final count = context.watch<CounterModel>().count;
    return Column(children: [
      Text('Count: $count'),
      const ExpensiveWidget(),  // Rebuilds unnecessarily!
      const StaticHeader(),     // Rebuilds unnecessarily!
    ]);
  }
}

// ✅ Good — only Text rebuilds
class CounterScreen extends StatelessWidget {
  Widget build(BuildContext context) {
    return Column(children: [
      // Only this Consumer rebuilds
      Consumer<CounterModel>(
        builder: (_, model, __) => Text('Count: ${model.count}'),
      ),
      const ExpensiveWidget(),  // Never rebuilds (const)
      const StaticHeader(),     // Never rebuilds (const)
    ]);
  }
}

// ✅ Best — Selector rebuilds only when count changes
Selector<CounterModel, int>(
  selector: (_, model) => model.count,
  builder: (_, count, __) => Text('Count: $count'),
)

// ── Scenario 3: Image Optimization ──
// ❌ Bad — full resolution image loaded into memory
Image.network('https://example.com/huge_image.jpg')

// ✅ Good — resize image to display size
Image.network(
  'https://example.com/huge_image.jpg',
  cacheWidth: (MediaQuery.sizeOf(context).width * 2).toInt(),
)

// ✅ Better — with caching and placeholder
CachedNetworkImage(
  imageUrl: 'https://example.com/image.jpg',
  cacheWidth: 400,
  placeholder: (_, __) => const CircularProgressIndicator(),
  errorWidget: (_, __, ___) => const Icon(Icons.error),
)

// ── Scenario 4: RepaintBoundary for Complex Widgets ──
// Wrap independent complex widgets to isolate repainting
ListView.builder(
  itemCount: 100,
  itemBuilder: (_, i) => RepaintBoundary(  // Isolate each item's painting
    child: ComplexChartWidget(data: data[i]),
  ),
)

// ── Scenario 5: Moving Heavy Computation to Isolates ──
// ❌ Bad — blocks UI thread
String parseHugeJson(String jsonStr) {
  final data = jsonDecode(jsonStr);  // Blocks UI for seconds
  return process(data);
}

// ✅ Good — runs in separate isolate
Future<String> parseHugeJson(String jsonStr) async {
  return await compute(_parseAndProcess, jsonStr);
}

// Top-level function (required for isolates)
String _parseAndProcess(String jsonStr) {
  final data = jsonDecode(jsonStr);
  return process(data);
}

// ── Scenario 6: const Constructors ──
// ❌ Bad — new instance created on every build
class MyWidget extends StatelessWidget {
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(16),  // New instance each build
      child: Text('Hello'),          // New instance each build
    );
  }
}

// ✅ Good — const instances reused
class MyWidget extends StatelessWidget {
  const MyWidget({super.key});  // const constructor
  Widget build(BuildContext context) {
    return const Padding(
      padding: EdgeInsets.all(16),  // Same instance
      child: Text('Hello'),          // Same instance
    );
  }
}

// ── Scenario 7: Avoiding Expensive Operations in build() ──
// ❌ Bad — sorting in build
class ProductList extends StatelessWidget {
  final List<Product> products;
  Widget build(BuildContext context) {
    final sorted = products..sort((a, b) => a.name.compareTo(b.name));
    return ListView(children: sorted.map((p) => ProductCard(p)).toList());
  }
}

// ✅ Good — sort once, not on every build
class ProductList extends StatelessWidget {
  final List<Product> products;
  const ProductList({super.key, required this.products});
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: products.length,
      itemBuilder: (_, i) => ProductCard(products[i]),
    );
  }
}
// Sort in the BLoC/Model before passing to widget

// ── Scenario 8: Profiling ──
// Run in profile mode (not debug!)
// flutter run --profile
// Open DevTools > Performance tab
// Record a session, look for:
// - Frames > 16ms (jank)
// - Widget rebuilds (excessive?)
// - Shader compilation jank

// ── Scenario 9: Deferring Heavy Initialization ──
class MyApp extends StatelessWidget {
  Widget build(BuildContext context) {
    return MaterialApp(
      home: FutureBuilder(
        future: _initialize(),
        builder: (_, snapshot) {
          if (snapshot.connectionState != ConnectionState.done) {
            return const SplashScreen();
          }
          return const HomeScreen();
        },
      ),
    );
  }

  Future<void> _initialize() async {
    await Future.wait([
      _loadFonts(),
      _initFirebase(),
      _preloadImages(),
    ]);
  }
}
```

### Output
```
A Flutter app optimized for performance:
- ListView.builder for efficient list rendering
- Consumer/Selector to minimize widget rebuilds
- cacheWidth for image memory optimization
- RepaintBoundary for complex widget isolation
- compute() for heavy computation in isolates
- const constructors for widget reuse
- Profile mode for performance testing
```

---

## ❓ Interview Questions

1. **Your app has a scrolling list that stutters after 500+ items — how do you diagnose and fix it?**
   - Diagnose: Run `flutter run --profile`, open DevTools Performance tab, scroll the list, look for frames > 16ms. Check if the jank is in build (CPU) or raster (GPU). Likely causes and fixes: (1) Not using `ListView.builder` — switch from `ListView(children: [...])` to `ListView.builder(itemBuilder: ...)` so only visible items are built. (2) Expensive `itemBuilder` — avoid sorting, filtering, or heavy computation inside `build()`. Pre-compute data in the BLoC/model. (3) No `const` on static child widgets — add `const` to prevent rebuilds. (4) Images loading at full resolution — add `cacheWidth`. (5) Missing `RepaintBoundary` — `ListView.builder` adds these automatically, but custom scroll views may not. (6) Large `cacheExtent` — reduce it if too many offscreen items are built. (7) Complex item widgets — split into smaller widgets and use `Selector`. Verify the fix by re-profiling and confirming frames are < 16ms.

2. **Your app's first screen takes 3+ seconds to load — how do you reduce startup time?**
   - Diagnose: `flutter run --trace-startup --profile` to measure. Then: (1) Move non-critical initialization out of `main()` — defer Firebase, analytics, remote config to after the first frame using `WidgetsBinding.instance.addPostFrameCallback`. (2) Show a native splash screen immediately while Dart initializes. (3) Cache API responses locally (Hive/SharedPreferences) so the first screen renders instantly with cached data, then refresh in the background. (4) Reduce font count — each font adds ~500KB. (5) Use `FutureBuilder` to show UI skeleton immediately while data loads. (6) Lazy-load heavy dependencies — don't initialize services until needed. (7) Minimize imports in `main.dart` — each import may trigger initialization. (8) Pre-compile shaders: `flutter run --cache-sksl`. (9) Use deferred components for non-critical features. Target: < 2 seconds on a mid-range device. Always test on real devices.

3. **Your app uses 200MB of memory after browsing a gallery — how do you fix the memory leak?**
   - Diagnose: Open DevTools Memory tab, take snapshots before and after browsing. Look for growing object counts. Common causes: (1) Images not cached at display size — a 4000x3000 image = ~48MB in memory. Fix: `Image.network(url, cacheWidth: 200)`. (2) `ImageProvider` not disposed — call `provider.evict()` or use `precacheImage` judiciously. (3) `ScrollController` not disposed — add `dispose()` in `State.dispose()`. (4) Stream subscriptions not cancelled — cancel in `dispose()`. (5) Animation controllers not disposed. (6) `ListView` with `cacheExtent: 5000` building too many offscreen items. (7) Global caches growing unbounded — clear image cache: `PaintingBinding.instance.imageCache.clear()` on `AppLifecycleState.paused`. (8) Use `CachedNetworkImage` with `memCacheWidth` and set `imageCacheMaximumSizeBytes`. Verify by checking memory returns to baseline after navigating away.

4. **How do you optimize a Flutter app that rebuilds the entire screen on every keystroke in a text field?**
   - The problem: `setState()` or `context.watch()` at a high level rebuilds the entire widget tree on every character typed. Fixes: (1) Move the `TextEditingController` and text field state to a leaf `StatefulWidget` — only that widget rebuilds. (2) Don't call `setState()` in `onChanged` for the whole screen — use a local state or a `ValueNotifier` + `ValueListenableBuilder` that wraps only the text field. (3) Use `Consumer`/`Selector` with a `child` parameter for static subtrees — `Consumer<Model>(builder: (_, model, child) => Column(children: [child!, TextField(...)]), child: ExpensiveWidget())`. (4) Extract the text field into a `StatefulWidget` that manages its own `TextEditingController`. (5) Use Riverpod's `ref.watch(provider.select((s) => s.specificField))` to rebuild only when a specific field changes. (6) Add `const` to all static widgets. Verify with DevTools "Track Widget Builds" — only the text field should rebuild.

5. **How do you handle heavy computation without blocking the UI?**
   - Use `compute()` to run functions in a separate isolate: `final result = await compute(heavyFunction, inputData)`. The function must be top-level or static, and arguments must be serializable. For long-running tasks (parsing large JSON, image processing, data processing), isolates prevent UI freezes. For multiple computations: use `Isolate.run()` (Dart 2.19+). For streaming data: use `Isolate.spawn()` with `ReceivePort`/`SendPort`. For Firebase: use `compute()` to parse Firestore responses. Note: isolates have overhead (~50ms to spawn) — only use for computations > 16ms. For shorter tasks, just use `Future` with `await` — Dart's event loop handles it. Always profile first to confirm the computation is actually the bottleneck.

6. **What is the difference between debug, profile, and release mode?**
   - **Debug mode** (`flutter run`): JIT compilation, assertions enabled, DevTools attached, slow. Use during development. **Profile mode** (`flutter run --profile`): AOT compilation, some debugging, near-release performance. Use for performance testing — it's the only accurate way to measure performance. **Release mode** (`flutter run --release` / `flutter build`): AOT compilation, no debugging, smallest size, fastest. Use for production. Never profile in debug mode — debug overhead (assertions, DevTools) makes it misleadingly slow. Always profile in profile mode. Release mode strips all debug code, uses tree shaking, and applies optimizations. Test performance on a real device, not an emulator — emulators have different performance characteristics.

7. **Your release APK is 45MB — how do you reduce it to under 20MB?**
   - Run `flutter build apk --release --analyze-size` to see the breakdown. Then: (1) Build App Bundle (`.aab`) instead of APK — Play Store delivers only the needed ABI/resources per device (~30% reduction). (2) Use `--split-per-abi` for APKs — each architecture gets its own smaller APK. (3) Use `--obfuscate --split-debug-info=./symbols` — obfuscates code and strips debug symbols (~5% reduction). (4) Compress all images to WebP — a 2MB PNG becomes ~600KB WebP. (5) List specific asset files instead of entire directories in `pubspec.yaml`. (6) Remove unused dependencies — `flutter pub deps` shows what's actually used. (7) Use `--tree-shake-icons` (default in release) to remove unused icon fonts. (8) Use deferred components (Android) to download features on demand. (9) Remove unused locales. (10) Enable R8/ProGuard on Android. Measure again after each step to verify the reduction.

8. **Your app experiences shader compilation jank on first run — how do you fix it?**
   - Shader compilation jank happens when Flutter compiles shaders on the first frame they're used — causing a visible stutter. Fix: (1) **Pre-warm shaders**: Run `flutter run --cache-sksl --profile`, interact with all screens/animations, then save the shader cache with `flutter build --bundle-sksl-path=...`. Ship the pre-compiled shaders with the app. (2) **Impeller** (Flutter 3.10+ on iOS, 3.16+ on Android) — a new rendering backend that pre-compiles shaders at build time, eliminating this issue entirely. Enable: `Manifest` → `<meta-data android:name="io.flutter.embedding.android.EnableImpeller" android:value="true" />`. (3) **Simplify initial animations** — avoid complex custom paint/shader effects on the first screen. (4) **Use `AnimatedSwitcher`/`ImplicitlyAnimatedWidget`** instead of custom shaders. (5) Profile with DevTools to confirm the jank is shader-related (look for "ShaderCompiliation" in the timeline).

9. **How do you identify unnecessary widget rebuilds?**
   - (1) Flutter DevTools: "Track Widget Builds" — highlights which widgets rebuild and how often. (2) Add `debugPrint` in `build()`: `debugPrint('Building $runtimeType')` — if it logs too often, it's rebuilding unnecessarily. (3) Use `RepaintBoundary` with DevTools' "Repaint Rainbow" — areas that repaint frequently flash colors. Fixes: (1) Use `const` constructors — const widgets never rebuild. (2) Use `Selector` instead of `Consumer` — rebuild only when specific fields change. (3) Move `context.watch<T>()` down to the leaf widget that needs the data. (4) Split large widgets into smaller ones so rebuilds are scoped. (5) Use `child` parameter in `Consumer` for static subtrees. (6) Don't create new objects in `build()` — use `const` or cache. (7) Use `Provider`'s `Selector` or Riverpod's `select()` for fine-grained rebuilds.

10. **How do you optimize animations in Flutter?**
    - (1) Use `RepaintBoundary` around animated widgets — isolates the animation's repaint from the rest of the screen. (2) Use `Transform` instead of `Padding`/`SizedBox` for position changes — `Transform` doesn't trigger layout, only paint. (3) Use `AnimatedBuilder` to limit rebuilds to the animated part. (4) Use `Tween` + `AnimationController` for precise control. (5) Avoid animating `Opacity` — use `FadeTransition` or `AnimatedOpacity` (more efficient). (6) Use `Transform.scale` instead of changing `Container` size. (7) Set `filterQuality: FilterQuality.low` for image animations. (8) Use `vsync: this` with `SingleTickerProviderStateMixin` to prevent animations when the widget is off-screen. (9) Use `Curves` for natural motion — avoid linear curves. (10) Profile animations in DevTools — look for frames > 16ms during animation. For complex animations, consider `Lottie` or `Rive` for pre-made, optimized animations.

---

## 🔗 Related Topics
- [Performance](../advanced/Performance.md)
- [Flutter Internals](../advanced/FlutterInternals.md)
- [UI Scenarios](UIScenarios.md)
