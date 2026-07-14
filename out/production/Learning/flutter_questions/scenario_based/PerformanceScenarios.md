# Performance Scenarios

## Scenario 1: Jank on List Scrolling

### Problem
A list of 1000 items with images and text stutters when scrolling. How do you fix it?

```dart
// ❌ Bad — ListView builds all items, no const, full-res images
ListView(
  children: products.map((p) => ListTile(
    leading: Image.network(p.imageUrl),  // Full resolution, no caching
    title: Text(p.name),  // Not const
    subtitle: Text(p.description),  // Rebuilds every time
  )).toList(),
)
// Problems:
// 1. ListView (not .builder) builds all 1000 items at once
// 2. Image.network downloads + decodes full resolution
// 3. No const → every widget rebuilt on scroll
// 4. No RepaintBoundary → entire list repaints
```

### Solution: ListView.builder + cached images + const + RepaintBoundary

```dart
// ✅ Good — optimized list
ListView.builder(
  itemCount: products.length,
  cacheExtent: 500,  // Build ahead for smooth scroll
  itemBuilder: (context, index) {
    final p = products[index];
    return RepaintBoundary(  // Cache each item's painting
      child: ListTile(
        leading: CachedNetworkImage(  // Caching + resizing
          imageUrl: p.imageUrl,
          memCacheWidth: 100,  // Decode at 100px (not 4000px)
          placeholder: (_, __) => const CircularProgressIndicator(),
          errorWidget: (_, __, ___) => const Icon(Icons.error),
        ),
        title: Text(p.name),
        subtitle: Text(p.description),
      ),
    );
  },
)

// Even better — extract to const-able widget
class ProductTile extends StatelessWidget {
  final Product product;
  const ProductTile({super.key, required this.product});

  @override
  Widget build(BuildContext context) {
    return RepaintBoundary(
      child: ListTile(
        leading: CachedNetworkImage(
          imageUrl: product.imageUrl,
          memCacheWidth: 100,
        ),
        title: Text(product.name),
        subtitle: Text(product.description),
      ),
    );
  }
}

ListView.builder(
  itemCount: products.length,
  itemBuilder: (_, i) => ProductTile(
    key: ValueKey(products[i].id),  // Key for efficient diffing
    product: products[i],
  ),
)
```

### Key Takeaway
- Use `ListView.builder` (lazy) not `ListView` (eager) for large lists
- `CachedNetworkImage` with `memCacheWidth` saves memory
- `RepaintBoundary` caches each item's painting — no repaint on scroll
- `const` widgets prevent unnecessary rebuilds
- `cacheExtent: 500` pre-builds items for smoother scrolling

---

## Scenario 2: Excessive Widget Rebuilds

### Problem
Tapping a counter button rebuilds the entire screen including a complex chart that doesn't depend on the counter.

```dart
// ❌ Bad — entire build rebuilds on counter change
class Dashboard extends StatefulWidget {
  @override
  State<Dashboard> createState() => _DashboardState();
}

class _DashboardState extends State<Dashboard> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const ComplexChart(),  // ❌ Rebuilds when count changes!
        Text('Count: $_count'),
        ElevatedButton(
          onPressed: () => setState(() => _count++),
          child: const Text('Add'),
        ),
      ],
    );
  }
}
```

### Solution: Extract widgets + const + Selector

```dart
// ✅ Good 1 — const constructor (if no dynamic data)
const ComplexChart()  // Add const to constructor

// ✅ Good 2 — extract counter to separate widget
class _CounterSection extends StatefulWidget {
  @override
  State<_CounterSection> createState() => _CounterSectionState();
}

class _CounterSectionState extends State<_CounterSection> {
  int _count = 0;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Text('Count: $_count'),
        ElevatedButton(
          onPressed: () => setState(() => _count++),
          child: const Text('Add'),
        ),
      ],
    );
  }
}

class Dashboard extends StatelessWidget {
  const Dashboard({super.key});
  @override
  Widget build(BuildContext context) {
    return const Column(
      children: [
        ComplexChart(),  // const — never rebuilds
        _CounterSection(),  // Only this rebuilds
      ],
    );
  }
}

// ✅ Good 3 — with Provider, use Selector for specific fields
Selector<UserModel, String>(
  selector: (_, model) => model.name,  // Only rebuilds when name changes
  builder: (context, name, child) {
    return Text(name);
  },
)
```

### Key Takeaway
- `const` widgets are created once and never rebuilt
- Extract stateful logic to separate widgets — only that widget rebuilds
- `Selector` rebuilds only when the selected value changes
- Use DevTools "Track widget rebuilds" to find unnecessary rebuilds

---

## Scenario 3: Image Memory Overflow (OOM)

### Problem
A gallery app loads 50 high-resolution images (4K) and crashes with out-of-memory error.

```dart
// ❌ Bad — full resolution images in memory
GridView.builder(
  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3),
  itemCount: images.length,
  itemBuilder: (context, index) {
    return Image.network(images[index].url);  // 4K image → ~64MB each!
  },
)
// 50 images × 64MB = 3.2GB → OOM crash
```

### Solution: Resize images + caching + pagination

```dart
// ✅ Good 1 — memCacheWidth limits decoded size
GridView.builder(
  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3),
  itemCount: images.length,
  itemBuilder: (context, index) {
    return CachedNetworkImage(
      imageUrl: images[index].url,
      memCacheWidth: 200,  // Decode at 200px → ~160KB (not 64MB)
      memCacheHeight: 200,
      fit: BoxFit.cover,
      placeholder: (_, __) => const ColoredBox(color: Colors.grey),
    );
  },
)

// ✅ Good 2 — ResizeImage for precise control
Image(
  image: ResizeImage(
    NetworkImage(images[index].url),
    width: 200,
    height: 200,
  ),
  fit: BoxFit.cover,
)

// ✅ Good 3 — Use thumbnail URLs from server
// Request server to provide thumbnail URLs
CachedNetworkImage(
  imageUrl: images[index].thumbnailUrl,  // Server provides 200px version
)

// ✅ Good 4 — Clear cache when memory is low
class GalleryScreen extends StatefulWidget {
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused) {
      PaintingBinding.instance.imageCache.clear();  // Free memory
    }
  }
}
```

### Image Memory Comparison
```
4K image (3840×2160):
  Raw: 3840 × 2160 × 4 bytes = ~33 MB
  Decoded: ~64 MB (with overhead)

200px thumbnail:
  Raw: 200 × 200 × 4 bytes = ~160 KB
  Decoded: ~320 KB

Savings: 99.5% memory reduction
```

### Key Takeaway
- Always specify `memCacheWidth`/`memCacheHeight` for network images
- 4K image = ~64MB in memory; 200px = ~320KB — 200x reduction
- Use `CachedNetworkImage` for disk + memory caching
- Request thumbnails from server (don't download 4K for a 200px tile)
- Clear image cache on `AppLifecycleState.paused` for large galleries

---

## Scenario 4: Heavy Computation Blocking UI

### Problem
Parsing a large JSON file (10MB) freezes the UI for 3 seconds.

```dart
// ❌ Bad — parsing on UI thread
Future<void> loadData() async {
  final response = await http.get(Uri.parse('https://api.example.com/data'));
  final data = jsonDecode(response.body);  // Blocks UI for 3 seconds!
  final items = (data['items'] as List)
      .map((e) => Item.fromJson(e))
      .toList();  // Also blocks UI
  setState(() => _items = items);
}
```

### Solution: Isolate for heavy work

```dart
// ✅ Good 1 — compute() for one-shot heavy work
Future<void> loadData() async {
  final response = await http.get(Uri.parse('https://api.example.com/data'));
  final items = await compute(_parseItems, response.body);  // Runs in isolate
  if (mounted) setState(() => _items = items);
}

// Top-level function (must be top-level or static)
List<Item> _parseItems(String jsonStr) {
  final data = jsonDecode(jsonStr);
  return (data['items'] as List)
      .map((e) => Item.fromJson(e))
      .toList();
}

// ✅ Good 2 — Isolate.run (Dart 2.19+)
final items = await Isolate.run(() {
  final data = jsonDecode(response.body);
  return (data['items'] as List)
      .map((e) => Item.fromJson(e))
      .toList();
});

// ✅ Good 3 — chunk processing to yield to UI
Future<List<Item>> parseInChunks(String jsonStr) async {
  final data = jsonDecode(jsonStr);
  final rawItems = data['items'] as List;
  final items = <Item>[];
  const chunkSize = 100;

  for (var i = 0; i < rawItems.length; i += chunkSize) {
    final end = (i + chunkSize).clamp(0, rawItems.length);
    items.addAll(
      rawItems.sublist(i, end).map((e) => Item.fromJson(e)),
    );
    await Future.delayed(Duration.zero);  // Yield to UI thread
  }
  return items;
}
```

### Key Takeaway
- `compute()` runs a function in a separate isolate — UI stays smooth
- The function must be top-level or static (not a closure)
- `Isolate.run()` is the modern API (Dart 2.19+)
- For incremental work, chunk + `Future.delayed(Duration.zero)` yields to UI
- Any computation >16ms should go to an isolate

---

## Scenario 5: Slow App Startup

### Problem
The app takes 5 seconds to show the first frame. How do you optimize startup?

```dart
// ❌ Bad — blocking everything in main()
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();        // 1s
  await SharedPreferences.getInstance();  // 0.5s
  await _loadConfig();                    // 1s
  await _setupAnalytics();                // 0.5s
  await _fetchUserData();                 // 2s
  runApp(const MyApp());  // 5s total — blank screen
}
```

### Solution: Lazy initialization + splash screen

```dart
// ✅ Good — show UI first, load async
void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Only critical init in main
  await SharedPreferences.getInstance();

  runApp(const MyApp());  // Show UI immediately

  // Non-critical init after app starts
  _initializeAsync();
}

Future<void> _initializeAsync() async {
  await Firebase.initializeApp();
  await _setupAnalytics();
  // Don't block UI — these run in background
}

// ✅ Good — use FutureBuilder for async-dependent screens
class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: _loadUserData(),  // Load when screen appears
      builder: (context, snapshot) {
        if (snapshot.connectionState != ConnectionState.done) {
          return const LoadingScreen();
        }
        return const Content();
      },
    );
  }
}

// ✅ Good — native splash screen (no blank screen)
// flutter_native_splash package
// flutter_native_splash-create
// Shows immediately while Dart initializes

// ✅ Good — defer route initialization
// Don't build all screens at startup — use lazy loading
GoRoute(
  path: '/heavy',
  builder: (context, state) {
    return FutureBuilder(
      future: HeavyScreen.init(),
      builder: (_, snap) => snap.hasData ? HeavyScreen() : Loading(),
    );
  },
)
```

### Startup Optimization Checklist
```
1. Only critical init in main() (<500ms)
2. Show splash screen immediately
3. Defer Firebase, analytics, config to after first frame
4. Use FutureBuilder for screen-level async data
5. Lazy-load heavy screens (don't build until needed)
6. Pre-compile shaders (flutter run --cache-sksl)
7. Use --release mode (AOT, not JIT)
8. Minimize main() imports
```

### Key Takeaway
- Show UI first, load data async — never block `main()` with non-critical init
- Only Firebase/SharedPreferences are critical — defer the rest
- Use `FutureBuilder` for screen-level async loading
- Native splash screen prevents blank screen during Dart initialization
- Profile startup with `flutter run --trace-startup --profile`

---

## 🔗 Related Topics
- [Performance](../advanced/Performance.md)
- [Flutter Internals](../advanced/FlutterInternals.md)
- [Custom Widgets](../intermediate/CustomWidgets.md)
