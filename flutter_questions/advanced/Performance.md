# Performance

## 📖 Explanation

Performance optimization in Flutter focuses on achieving smooth 60fps (16ms per frame) or 120fps (8ms per frame). Common issues include unnecessary rebuilds, jank from heavy computations, image memory overflow, and large app size.

### Frame Budget
```
60 FPS = 16ms per frame (16.67ms budget)
120 FPS = 8ms per frame

Frame budget:
  - Build phase: ~8ms
  - Layout/paint: ~4ms
  - GPU rasterize: ~4ms

If a frame takes >16ms → jank (dropped frame)
```

### DevTools Performance Tabs
| Tab | Purpose |
|-----|---------|
| Flutter Inspector | Widget tree, rebuild detection |
| Performance | Frame rendering, GPU/CPU timeline |
| CPU Profiler | Function call time, flame chart |
| Memory | Heap snapshots, memory leaks |
| Network | HTTP requests timeline |

### Performance Overlay
```
┌─────────────────────┐
│  GPU   │   UI       │
│ ▓▓▓▓▓ │ ▓▓▓▓▓▓▓▓▓  │

Green  = < 16ms (good, 60fps)
Yellow = 16-32ms (jank)
Red    = > 32ms (severe jank)

GPU graph = rasterization (painting, compositing)
UI graph  = build + layout (widget tree)
```

### Common Performance Issues
| Issue | Cause | Fix |
|-------|-------|-----|
| Jank on scroll | No ListView.builder, full-res images | Use .builder + memCacheWidth + RepaintBoundary |
| Unnecessary rebuilds | No const, broad Consumer | const widgets, Selector, extract widgets |
| OOM crash | 4K images in memory | memCacheWidth/memCacheHeight, thumbnails |
| Frozen UI | Heavy computation on main thread | compute(), Isolate.run() |
| Slow startup | Blocking main() | Lazy init, splash screen |

### Image Memory
```
100x100 px   → ~40 KB
500x500 px   → ~1 MB
1000x1000 px → ~4 MB
4000x4000 px → ~64 MB  ← OOM risk!

Always specify memCacheWidth/memCacheHeight
```

### Rebuild Optimization
- `const` constructors — never rebuild
- Extract widgets — limit rebuild scope
- `Selector`/`.select()` — rebuild on specific field change
- `Consumer` child parameter — static subtrees not rebuilt
- `RepaintBoundary` — cache painting

---

## 🧪 Code Example

```dart
// ── Avoid unnecessary rebuilds ──

// ❌ Bad — entire Column rebuilds when counter changes
class BadCounter extends StatefulWidget {
  State<BadCounter> createState() => _BadCounterState();
}
class _BadCounterState extends State<BadCounter> {
  int _count = 0;
  @override
  Widget build(BuildContext context) {
    return Column(children: [
      const ExpensiveWidget(),  // Rebuilds unnecessarily!
      Text('$_count'),
      ElevatedButton(
        onPressed: () => setState(() => _count++),
        child: const Text('Add'),
      ),
    ]);
  }
}

// ✅ Good — const + extracted widget
class GoodCounter extends StatelessWidget {
  const GoodCounter({super.key});
  @override
  Widget build(BuildContext context) {
    return const Column(children: [
      ExpensiveWidget(),  // const — no rebuild
      _CountDisplay(),     // Only this rebuilds
    ]);
  }
}

// ── Optimize lists ──
ListView.builder(
  cacheExtent: 500,  // Build 500px ahead
  itemCount: items.length,
  itemBuilder: (context, index) => RepaintBoundary(
    child: ItemWidget(
      key: ValueKey(items[index].id),  // Preserve state on reorder
      item: items[index],
    ),
  ),
)

// ── Optimize images ──
CachedNetworkImage(
  imageUrl: 'https://example.com/photo.jpg',
  memCacheWidth: 300,  // Decode at 300px → saves memory
  placeholder: (_, __) => const CircularProgressIndicator(),
  errorWidget: (_, __, ___) => const Icon(Icons.error),
)

// ── Heavy computation in isolate ──
Future<List<Result>> processData() async {
  return compute(_processInIsolate, hugeList);
}

List<Result> _processInIsolate(List<Data> data) {
  return data.map((e) => complexCalculation(e)).toList();
}

// Or Isolate.run (Dart 2.19+)
final result = await Isolate.run(() {
  return hugeList.map(complexCalculation).toList();
});

// ── Reduce app size ──
// flutter build apk --split-per-abi --obfuscate --split-debug-info=./symbols
// flutter build appbundle --release
```

### Output
```
A performance-optimized Flutter app with:
- const widgets preventing unnecessary rebuilds
- ListView.builder with RepaintBoundary for smooth scrolling
- CachedNetworkImage with memCacheWidth for low memory usage
- Isolates for heavy computation (no UI blocking)
- Split-per-abi + obfuscation for smaller app size
```

---

## ❓ Interview Questions

1. **How do you profile a Flutter app?**
   - Run in profile mode: `flutter run --profile` (debug mode is too slow for profiling). Open DevTools with `dart devtools` or press 'D' in terminal. Use the Performance tab to see frame rendering and GPU/CPU timeline. Use the Flutter Inspector to track widget rebuilds. Key metrics: 60fps = 16ms per frame. If a frame exceeds 16ms, you get jank. The Performance overlay shows two graphs: UI (build + layout) and GPU (rasterize). Green = good (<16ms), yellow = jank (16-32ms), red = severe (>32ms). Never profile in debug mode — JIT and assertions skew results.

2. **How do you avoid unnecessary rebuilds?**
   - Four techniques: (1) `const` constructors — `const Text('Hello')` is created once and never rebuilds. (2) Extract widgets — move stateful logic to a separate widget so only that widget rebuilds. (3) `Selector<Model, T>` (Provider) or `.select()` (Riverpod) — rebuild only when a specific field changes, not on any model change. (4) `Consumer` with `child` parameter — pass static widgets as `child` so they're built once and not rebuilt. Use DevTools "Track widget rebuilds" to find widgets that rebuild too frequently. The most common mistake is putting `const ExpensiveWidget()` inside a StatefulWidget's build — it still rebuilds because the parent rebuilds.

3. **How do you optimize lists?**
   - Use `ListView.builder` (lazy, only builds visible items) instead of `ListView` (builds all items at once). Add `RepaintBoundary` around each item to cache painting — prevents repainting on scroll. Use `cacheExtent: 500` to pre-build items ahead for smoother scrolling. Add `ValueKey` with unique ID for efficient diffing on reorder. Use `itemExtent` if items have fixed height — skips layout calculations. For dividers, use `ListView.separated`. For complex scroll layouts, use `CustomScrollView` with slivers. Avoid `shrinkWrap: true` in nested lists — it forces measuring all children.

4. **How do you optimize images?**
   - Use `cached_network_image` with `memCacheWidth`/`memCacheHeight` to decode images at display size (e.g., `memCacheWidth: 300` decodes at 300px instead of 4000px — 200x memory reduction). Use `ResizeImage` for precise control. Use `precacheImage()` to preload critical images. Request thumbnail URLs from the server instead of downloading 4K images for 100px tiles. Use WebP format (~30% smaller than PNG). Clear image cache on `AppLifecycleState.paused` for large galleries: `PaintingBinding.instance.imageCache.clear()`. A 4K image = ~64MB in memory; 200px = ~320KB — always specify cache dimensions.

5. **How do you handle heavy computations?**
   - Never run heavy work on the UI thread — it blocks rendering and causes jank. Use `compute(function, data)` to run a function in a separate isolate. The function must be top-level or static. Use `Isolate.run(() { ... })` (Dart 2.19+) for simpler API. For long-running work, use `Isolate.spawn()` with `SendPort`/`ReceivePort` for two-way communication. For incremental work without isolates, chunk processing with `Future.delayed(Duration.zero)` to yield to the UI thread between chunks. Rule: any computation >16ms should go to an isolate. Don't use isolates for I/O — Dart's async I/O is already non-blocking.

6. **How do you reduce app size?**
   - Build with `--split-per-abi` for per-architecture APKs (~40% smaller per APK). Build App Bundle (AAB) for Play Store — Play delivers only needed ABI/resources (~30% smaller). Use `--tree-shake-icons` to remove unused Material icons. Use `--obfuscate --split-debug-info=./symbols` for smaller + obfuscated binary (~5% smaller). Use WebP images (~30% smaller than PNG). List specific asset files instead of entire folders. Remove unused dependencies. Enable R8/ProGuard on Android. Result: a 20MB debug APK can become ~8MB release AAB. Measure with `flutter build apk --analyze-size`.

7. **How do you use the Performance overlay?**
   - Enable with `MaterialApp(showPerformanceOverlay: true)`. Two graphs appear: GPU (rasterization time) and UI (build + layout time). Green = <16ms (good, 60fps), yellow = 16-32ms (jank), red = >32ms (severe jank). If UI graph is high: reduce rebuilds (const, Selector), simplify widget tree, use ListView.builder. If GPU graph is high: reduce shadows, gradients, clip, use RepaintBoundary, optimize images (memCacheWidth), avoid opacity in lists. The overlay only shows in debug/profile mode. For detailed analysis, use DevTools Performance tab with timeline.

8. **What is jank and how do you fix it?**
   - Jank is when a frame takes longer than the budget (16ms for 60fps), causing a dropped frame — the user sees stutter. Causes and fixes: (1) Heavy build phase → simplify widget tree, use const, extract widgets. (2) Heavy layout → avoid deep nesting, use itemExtent, avoid shrinkWrap. (3) Heavy painting → reduce shadows/gradients/clip, use RepaintBoundary. (4) Heavy computation on UI thread → use isolates. (5) Large images → memCacheWidth/memCacheHeight. (6) Shader compilation jank → Impeller (pre-compiled shaders). Profile with `flutter run --profile` + DevTools to find the exact cause. First-frame jank is often shader compilation — Impeller fixes this.

9. **What is `const` and why is it important for performance?**
   - `const` creates a compile-time constant — the same instance is reused across all rebuilds, so const widgets never rebuild. `const Text('Hello')` is created once; `Text('Hello')` creates a new instance every build. const must be deeply const — all children must also be const. Flutter's diffing uses `identical()` for const widgets (fast) vs `==` for non-const (slower). Use const wherever possible — it's the single biggest performance win in Flutter. Add `const` to constructors, then use `const` when instantiating. Lint rule `prefer_const_constructors` enforces this. Can't use const with dynamic values: `Text(DateTime.now().toString())` can't be const.

10. **How do you optimize app startup time?**
    - Only do critical initialization in `main()` (<500ms) — show UI first, load data async. Defer Firebase, analytics, config to after first frame. Use a native splash screen (no blank screen during Dart init). Use `FutureBuilder` for screen-level async data. Lazy-load heavy screens — don't build until needed. Pre-compile shaders with `flutter run --cache-sksl`. Use `--release` mode (AOT, not JIT). Minimize main() imports. Profile startup with `flutter run --trace-startup --profile`. The key insight: `runApp()` should be called as early as possible — everything else can happen after the first frame.

---

## 🔗 Related Topics
- [Flutter Internals](FlutterInternals.md)
- [Custom Widgets](../intermediate/CustomWidgets.md)
- [Performance Scenarios](../scenario_based/PerformanceScenarios.md)
