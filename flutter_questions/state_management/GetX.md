# GetX

## 📖 Explanation

GetX is an all-in-one Flutter package for state management, navigation, dependency injection, and utilities. It's known for minimal boilerplate and rapid development, but has tradeoffs for large/production apps.

### GetX Features
| Feature | API |
|---------|-----|
| State management | `GetxController` + `Obx` |
| Navigation | `Get.to()`, `Get.back()` |
| Dependency injection | `Get.put()`, `Get.find()` |
| Theming | `Get.changeTheme()` |
| Internationalization | `Get.translations` |
| Dialogs/Snackbars | `Get.snackbar()`, `Get.dialog()` |

### GetX Reactivity
```
Controller (Rx variables)
    ↓ Obx widget detects Rx access
    ↓ Rx variable changes
Obx rebuilds
```

### GetX vs Other Solutions
| Feature | GetX | Provider | BLoC |
|---------|------|----------|------|
| Boilerplate | Minimal | Low | High |
| Learning curve | Low | Low | High |
| Compile-safe | ❌ | ❌ | ✅ |
| Testability | Fair | Good | Excellent |
| DI built-in | ✅ | ❌ (needs get_it) | ❌ |
| Navigation built-in | ✅ | ❌ | ❌ |
| Official recommendation | ❌ | ✅ | ❌ |

### GetX Reactive Types
| Type | Example |
|------|---------|
| `Rx<T>` | `RxInt(0)`, `RxString('')` |
| `.obs` | `int count = 0.obs` |
| `RxList<T>` | `RxList<Item>([])` |
| `RxMap<K,V>` | `RxMap<String, dynamic>({})` |

### When to Use GetX
- Rapid prototyping
- Small to medium apps
- Solo developer who wants speed
- Need navigation + DI + state in one package

### When NOT to Use GetX
- Large/enterprise apps (use BLoC/Riverpod)
- Team of 5+ developers (structure matters)
- High testability required
- Want to learn Flutter fundamentals (GetX hides them)

---

## 🧪 Code Example

```dart
import 'package:get/get.dart';

// ── Controller ──
class CounterController extends GetxController {
  var count = 0.obs;  // Rx variable
  var isLoading = false.obs;

  int get doubled => count.value * 2;  // Computed

  void increment() => count.value++;
  void decrement() => count.value--;
  void reset() => count.value = 0;

  // Async
  Future<void> fetchData() async {
    isLoading.value = true;
    try {
      await Future.delayed(const Duration(seconds: 2));
      count.value = 100;
    } finally {
      isLoading.value = false;
    }
  }

  // Lifecycle
  @override
  void onInit() {
    super.onInit();
    // Called when controller is created
    ever(count, (_) => print('Count changed: $count'));  // Reaction
  }

  @override
  void onClose() {
    // Called when controller is disposed
    super.onClose();
  }
}

// ── Dependency Injection ──
void main() {
  // Register dependencies
  Get.put(ApiClient());
  Get.put(UserRepository(Get.find<ApiClient>()));
  Get.lazyPut<CounterController>(() => CounterController());

  runApp(const GetMaterialApp(
    home: HomeScreen(),
    debugShowCheckedModeBanner: false,
  ));
}

// ── View with Obx ──
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Get.find retrieves the controller
    final controller = Get.find<CounterController>();

    return Scaffold(
      appBar: AppBar(title: const Text('Counter')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Obx rebuilds when any Rx inside changes
            Obx(() => Text(
              'Count: ${controller.count.value}',
              style: const TextStyle(fontSize: 48),
            )),
            Obx(() => Text('Doubled: ${controller.doubled}')),
            Obx(() => controller.isLoading.value
              ? const CircularProgressIndicator()
              : const SizedBox()),
          ],
        ),
      ),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          FloatingActionButton(
            onPressed: controller.increment,
            child: const Icon(Icons.add),
          ),
          const SizedBox(height: 8),
          FloatingActionButton(
            onPressed: () => Get.to(const DetailScreen()),
            child: const Icon(Icons.navigate_next),
          ),
        ],
      ),
    );
  }
}

// ── Navigation (no context needed) ──
class DetailScreen extends StatelessWidget {
  const DetailScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Detail')),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            Get.back(result: 'Data from detail');  // Return data
          },
          child: const Text('Go Back'),
        ),
      ),
    );
  }
}

// Navigate: Get.to(DetailScreen())
// Get.back()
// Get.off(Screen()) — replace (no back)
// Get.offAll(HomeScreen()) — clear stack
// Get.toNamed('/details/123') — named routes

// ── Dialogs & Snackbars (no context needed) ──
Get.snackbar('Title', 'Message', snackPosition: SnackPosition.BOTTOM);
Get.dialog(AlertDialog(title: Text('Dialog')));
Get.bottomSheet(Container(height: 200, child: Text('Bottom sheet')));

// ── GetView (auto-injects controller) ──
class CartScreen extends GetView<CartController> {
  const CartScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // controller is auto-available
    return Obx(() => ListView.builder(
      itemCount: controller.items.length,
      itemBuilder: (_, i) => ListTile(
        title: Text(controller.items[i].name),
      ),
    ));
  }
}

// ── GetBuilder (alternative to Obx — manual update) ──
class CounterWithBuilder extends GetView<CounterController> {
  const CounterWithBuilder({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CounterController>(
      builder: (controller) => Text('${controller.count.value}'),
    );
  }
}
```

### Output
```
A Flutter app with GetX:
- CounterController with Rx variables (.obs)
- Obx for reactive UI rebuilds
- Get.put/Get.find for dependency injection
- Get.to/Get.back for context-free navigation
- Get.snackbar/Get.dialog for context-free UI
- GetView for auto-injected controllers
- ever() for reactions
```

---

## ❓ Interview Questions

1. **What is GetX and how does it work?**
   - GetX is an all-in-one package: state management, navigation, DI, and utilities. State: create a `GetxController` with `Rx` variables (`var count = 0.obs`). UI: wrap widgets in `Obx(() => Text('${controller.count.value}'))` — Obx detects which Rx variables are accessed and rebuilds when they change. DI: `Get.put(Controller())` registers, `Get.find<Controller>()` retrieves. Navigation: `Get.to(Screen())`, `Get.back()` — no `BuildContext` needed. GetX uses global singletons internally — fast and convenient but less testable and structured than BLoC/Riverpod. Best for rapid prototyping and small apps.

2. **What is the difference between Obx and GetBuilder?**
   - `Obx` is reactive — it auto-detects which `.obs` variables are accessed in the builder and rebuilds when any of them change. No need to call `update()`. Just use `controller.count.value` inside `Obx`. `GetBuilder<Controller>(builder: (c) => ...)` is manual — you must call `update()` in the controller to trigger a rebuild. `Obx` is more convenient (automatic), `GetBuilder` is more performant (no stream subscription). Use `Obx` for fine-grained reactivity (individual values), `GetBuilder` for bulk state updates (rebuild entire view on `update()`). Most developers use `Obx` for simplicity.

3. **What are Rx variables in GetX?**
   - `Rx` (Reactive) variables are observable values that notify `Obx` widgets when they change. Create with `.obs`: `var count = 0.obs` (creates `RxInt`), `var name = ''.obs` (creates `RxString`), `var items = <Item>[].obs` (creates `RxList`). Access value with `.value`: `count.value++`. `RxList` and `RxMap` support direct mutation: `items.add(item)` (no `.value` needed). Rx variables also have helpers: `count.value`, `ever(count, callback)`, `debounce()`, `interval()`. Rx variables are the core of GetX's reactivity — they replace `ChangeNotifier` + `notifyListeners()`.

4. **How does GetX dependency injection work?**
   - `Get.put(Controller())` — creates and registers the controller immediately (singleton). `Get.lazyPut(() => Controller())` — creates on first `Get.find()` call (lazy singleton). `Get.find<Controller>()` — retrieves the registered instance. `Get.replace<Controller>(MockController())` — for testing (override). GetX DI is global — registered instances are available anywhere without `BuildContext`. Controllers are automatically disposed when the route they're associated with is removed (if using `GetView` or `Get.put` in a route). For manual lifecycle: `Get.delete<Controller>()`. GetX DI is simpler than `get_it` but uses global state — harder to test in isolation and can lead to hidden dependencies.

5. **What are the advantages of GetX navigation?**
   - GetX navigation doesn't need `BuildContext`: `Get.to(Screen())`, `Get.back()`, `Get.off(Screen())` (replace), `Get.offAll(HomeScreen())` (clear stack). Named routes: `Get.toNamed('/details/123')`, `Get.toNamed('/details', arguments: {'id': 123})`. Get parameters: `Get.parameters['id']`, `Get.arguments`. This means you can navigate from anywhere — controllers, services, models — not just from widget event handlers. Snackbars and dialogs also don't need context: `Get.snackbar(...)`, `Get.dialog(...)`, `Get.bottomSheet(...)`. This is GetX's biggest convenience — no `BuildContext` threading.

6. **What are the criticisms of GetX?**
   - (1) **Global singletons** — `Get.put()`/`Get.find()` use global state, which is an anti-pattern. Hard to test in isolation, hidden dependencies. (2) **Hides Flutter concepts** — GetX navigation, theming, and DI abstract away Flutter fundamentals. Developers may not learn how Navigator, Theme, or InheritedWidget work. (3) **Not compile-safe** — `Get.find<Controller>()` can throw at runtime if not registered. (4) **Less testable** — global state is hard to mock/reset between tests. (5) **Not officially recommended** — Flutter team recommends Provider/Riverpod. (6) **Can lead to spaghetti code** — easy to access any controller from anywhere leads to tight coupling. (7) **Large package** — includes many features you may not use.

7. **How do you test GetX controllers?**
   - Create the controller directly: `test('increment', () { final controller = CounterController(); controller.increment(); expect(controller.count.value, 1); })`. For DI: use `Get.testMode = true` and `Get.put(MockController())`. Mock dependencies: `Get.put<ApiClient>(MockApiClient())`. Test reactions: `ever()` callbacks. For widget tests: `GetMaterialApp(home: MyScreen())` instead of `MaterialApp`. Reset GetX between tests: `Get.reset()`. GetX testing is harder than BLoC/Riverpod because of global state — each test may affect others if not properly reset. Always call `Get.reset()` in `tearDown`. Mocktail works for mocking injected services.

8. **How does GetX compare to Riverpod?**
   - **GetX**: All-in-one (state, nav, DI). Minimal boilerplate. Global singletons. Not compile-safe. Less testable. Rapid development. **Riverpod**: State management + DI only. More boilerplate. No global state (providers are scoped). Compile-safe. Highly testable. Officially recommended. GetX is faster for prototyping — less code, built-in navigation and dialogs. Riverpod is better for production — compile-safe, testable, scoped, no global state. GetX hides Flutter concepts; Riverpod teaches them. For small apps or solo developers who want speed, GetX is fine. For teams, large apps, or production code, Riverpod (or BLoC) is the better choice. The Flutter community generally leans toward Riverpod/BLoC.

9. **What is GetView and GetWidget?**
   - `GetView<T>` is a StatelessWidget that automatically provides `controller` via `Get.find<T>()`. No need to call `Get.find()` in build — just use `controller`. `class MyScreen extends GetView<MyController> { @override Widget build(context) => Text(controller.count.value); }`. `GetWidget<T>` is similar but caches the controller instance — the same controller is reused even if the widget is rebuilt. Use `GetView` for most cases (controller is injected per screen). Use `GetWidget` when you need the controller to persist across widget rebuilds. Both require the controller to be registered with `Get.put()` or `Get.lazyPut()` before the screen is displayed.

10. **When should you use GetX vs other state management?**
    - Use **GetX** when: rapid prototyping, small to medium apps, solo developer who wants speed, need navigation + DI + state in one package. Don't use GetX when: large/enterprise apps (use BLoC/Riverpod), team of 5+ developers (structure matters), high testability required (use BLoC), you want to learn Flutter fundamentals (GetX hides them), you need compile-time safety (use Riverpod). GetX is a valid choice for MVPs and small apps where speed of development is the priority. For production apps with long-term maintenance, Provider, Riverpod, or BLoC are more maintainable. The best state management is the one your team knows and can maintain.

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [Comparison](Comparison.md)
- [Best Practices](BestPractices.md)
