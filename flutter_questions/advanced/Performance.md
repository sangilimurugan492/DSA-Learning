# Performance

## Q1: How do you profile a Flutter app?

```bash
# Run in profile mode (not debug — debug is slow)
flutter run --profile

# Open DevTools
flutter pub global activate devtools
dart devtools

# Or press 'D' in terminal while app is running
```

### DevTools Tabs
| Tab | Purpose |
|-----|---------|
| Flutter Inspector | Widget tree, rebuild detection |
| Performance | Frame rendering, GPU/CPU timeline |
| CPU Profiler | Function call time, flame chart |
| Memory | Heap snapshots, memory leaks |
| Network | HTTP requests timeline |

### Key Metrics
```
60 FPS = 16ms per frame (16.67ms budget)
120 FPS = 8ms per frame

Frame budget:
  - Build phase: ~8ms
  - Layout/paint: ~4ms
  - GPU rasterize: ~4ms

If a frame takes >16ms → jank (dropped frame)
```

---

## Q2: How do you avoid unnecessary rebuilds?

```dart
// ❌ Bad — entire Column rebuilds when counter changes
class BadCounter extends StatefulWidget {
  @override
  State<BadCounter> createState() => _BadCounterState();
}

class _BadCounterState extends State<BadCounter> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const ExpensiveWidget(),  // Rebuilds unnecessarily!
        Text('$_count'),
        ElevatedButton(
          onPressed: () => setState(() => _count++),
          child: const Text('Add'),
        ),
      ],
    );
  }
}

// ✅ Good 1 — const constructor (never rebuilds)
const ExpensiveWidget()  // Add const

// ✅ Good 2 — extract to separate widget
class GoodCounter extends StatefulWidget {
  @override
  State<GoodCounter> createState() => _GoodCounterState();
}

class _GoodCounterState extends State<GoodCounter> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const ExpensiveWidget(),  // const — no rebuild
        _CountDisplay(count: _count),  // Only this rebuilds
        ElevatedButton(
          onPressed: () => setState(() => _count++),
          child: const Text('Add'),
        ),
      ],
    );
  }
}

class _CountDisplay extends StatelessWidget {
  final int count;
  const _CountDisplay({required this.count});
  @override
  Widget build(BuildContext context) => Text('$count');
}

// ✅ Good 3 — use Consumer/Selector to limit rebuild scope
Consumer<CounterModel>(
  builder: (context, model, child) {
    return Text('${model.count}');  // Only this rebuilds
  },
)

// ✅ Good 4 — Selector for specific field
Selector<UserModel, String>(
  selector: (_, model) => model.name,  // Only rebuilds when name changes
  builder: (context, name, child) => Text(name),
)
```

### Rebuild Detection
```dart
// In DevTools → Flutter Inspector → "Track widget rebuilds"
// Widgets that rebuild are highlighted

// Or use debugPrint
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    debugPrint('MyWidget building...');  // Check if too frequent
    return Container();
  }
}
```

---

## Q3: How do you optimize lists?

```dart
// ❌ Bad — ListView builds all items at once
ListView(
  children: items.map((item) => ItemWidget(item: item)).toList(),
)
// 1000 items → 1000 widgets built → memory + jank

// ✅ Good 1 — ListView.builder (lazy, only visible items)
ListView.builder(
  itemCount: items.length,
  itemBuilder: (context, index) => ItemWidget(item: items[index]),
)

// ✅ Good 2 — const items (if static)
ListView(
  children: const [
    ItemWidget(item: Item('A')),
    ItemWidget(item: Item('B')),
  ],
)

// ✅ Good 3 — add keys for reordering
ListView.builder(
  itemCount: items.length,
  itemBuilder: (context, index) => ItemWidget(
    key: ValueKey(items[index].id),  // Preserves state on reorder
    item: items[index],
  ),
)

// ✅ Good 4 — cacheExtent for smoother scrolling
ListView.builder(
  cacheExtent: 500,  // Build 500px ahead of viewport
  itemCount: items.length,
  itemBuilder: (context, index) => ItemWidget(item: items[index]),
)

// ✅ Good 5 — ListView.separated for dividers
ListView.separated(
  itemCount: items.length,
  separatorBuilder: (_, __) => const Divider(),
  itemBuilder: (context, index) => ItemWidget(item: items[index]),
)
```

---

## Q4: How do you optimize images?

```dart
// ❌ Bad — full resolution image
Image.network('https://example.com/huge_4k_photo.jpg')
// Downloads 10MB, decodes to 50MB in memory → jank + OOM

// ✅ Good 1 — cached_network_image (caching + resizing)
// pubspec.yaml: cached_network_image: ^3.3.0
CachedNetworkImage(
  imageUrl: 'https://example.com/photo.jpg',
  memCacheWidth: 300,  // Decode at 300px width → saves memory
  placeholder: (_, __) => const CircularProgressIndicator(),
  errorWidget: (_, __, ___) => const Icon(Icons.error),
)

// ✅ Good 2 — precache images
Future<void> _preloadImages() async {
  await precacheImage(
    NetworkImage('https://example.com/hero.jpg'),
    context,
  );
}

// ✅ Good 3 — use appropriate format
// PNG for transparency, WebP for photos, SVG for icons
Image.asset('assets/logo.webp')  // Smaller than PNG

// ✅ Good 4 — resize for thumbnails
Image(
  image: ResizeImage(
    NetworkImage('https://example.com/photo.jpg'),
    width: 100,  // Decode at 100px
  ),
)

// ✅ Good 5 — use ListView.builder with images
// Don't load all images at once
```

### Image Memory
```
Image resolution vs memory:
  100x100 px   → ~40 KB
  500x500 px   → ~1 MB
  1000x1000 px → ~4 MB
  4000x4000 px → ~64 MB  ← OOM risk!

Always specify memCacheWidth/memCacheHeight
```

---

## Q5: How do you handle heavy computations?

```dart
// ❌ Bad — heavy work on UI thread → jank
void processData() {
  final result = hugeList.map((e) => complexCalculation(e)).toList();
  // Blocks UI thread → dropped frames
}

// ✅ Good 1 — compute() (isolates)
Future<List<Result>> processData() async {
  return compute(_processInIsolate, hugeList);
}

List<Result> _processInIsolate(List<Data> data) {
  return data.map((e) => complexCalculation(e)).toList();
}

// ✅ Good 2 — Isolate.run (Dart 2.19+)
final result = await Isolate.run(() {
  return hugeList.map(complexCalculation).toList();
});

// ✅ Good 3 — Isolate.spawn for long-running
void startWorker() async {
  final receivePort = ReceivePort();
  await Isolate.spawn(_workerEntry, receivePort.sendPort);

  receivePort.listen((message) {
    print('Result: $message');
  });
}

void _workerEntry(SendPort sendPort) {
  // Runs in separate isolate
  final result = heavyComputation();
  sendPort.send(result);
}

// ✅ Good 4 — chunk work into frames
Future<void> processInChunks(List<Data> items) async {
  const chunkSize = 50;
  for (var i = 0; i < items.length; i += chunkSize) {
    final end = (i + chunkSize).clamp(0, items.length);
    processChunk(items.sublist(i, end));
    await Future.delayed(Duration.zero);  // Yield to UI
  }
}
```

---

## Q6: How do you reduce app size?

```bash
# 1. Build with --split-per-abi (smaller per-arch APKs)
flutter build apk --split-per-abi
# arm64-v8a: ~15MB (modern devices)
# armeabi-v7a: ~12MB (older devices)
# x86_64: ~15MB (emulators)

# 2. Build App Bundle (recommended for Play Store)
flutter build appbundle
# Play Store delivers only needed ABI/resources

# 3. Tree-shake icons
flutter build apk --tree-shake-icons

# 4. Obfuscate (smaller + secure)
flutter build apk --obfuscate --split-debug-info=./symbols
```

### Reduce Asset Size
```yaml
# pubspec.yaml — only include needed assets
flutter:
  assets:
    - assets/images/  # Entire folder

# ✅ Better — list specific files
  assets:
    - assets/images/logo.png
    - assets/images/hero.webp
```

| Optimization | Size Reduction |
|-------------|----------------|
| `--split-per-abi` | ~40% (per APK) |
| App Bundle | ~30% (Play Store) |
| WebP images | ~30% vs PNG |
| `--obfuscate` | ~5% |
| Remove unused deps | Varies |

---

## Q7: How do you use the Performance overlay?

```dart
// Enable performance overlay
MaterialApp(
  showPerformanceOverlay: true,  // Shows FPS + GPU graph
  home: MyApp(),
)

// Or in code
// Check if in profile mode
if (kProfileMode) {
  // Show debug overlays
}
```

### Reading the Overlay
```
┌─────────────────────┐
│  GPU   │   UI       │  ← Two graphs
│ ▓▓▓▓▓ │ ▓▓▓▓▓▓▓▓▓  │
│  4ms   │   12ms      │  ← Time per frame
└─────────────────────┘

Green  = < 16ms (good, 60fps)
Yellow = 16-32ms (jank)
Red    = > 32ms (severe jank)

GPU graph = rasterization time (painting, compositing)
UI graph  = build + layout time (widget tree)
```

### Common Fixes
```
UI graph high (build/layout slow):
  → Reduce rebuilds (const, Selector)
  → Simplify widget tree
  → Use ListView.builder

GPU graph high (paint/raster slow):
  → Reduce shadows, gradients, clip
  → Use RepaintBoundary
  → Optimize images (memCacheWidth)
  → Avoid opacity in lists
```

---

## 🔗 Related Topics
- [Flutter Internals](FlutterInternals.md)
- [Custom Widgets](../intermediate/CustomWidgets.md)
- [Performance Scenarios](../scenario_based/PerformanceScenarios.md)
