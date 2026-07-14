# Comparison

## Q1: Full comparison table of all state management solutions

| Feature | setState | Provider | Riverpod | BLoC | GetX | MobX | Redux |
|---------|----------|----------|----------|------|------|------|-------|
| Learning curve | Very Low | Low | Medium | High | Low | Medium | High |
| Boilerplate | None | Low | Low | High | Very Low | Medium | Very High |
| Compile-safe | ✅ | ❌ | ✅ | ✅ | ❌ | ✅ | ✅ |
| Testability | Low | Good | Excellent | Excellent | Medium | Good | Excellent |
| Needs BuildContext | ✅ | ✅ | ❌ | ✅ | ❌ | ✅ | ✅ |
| Scoped state | ❌ | Hard | Easy | Easy | Hard | Hard | ❌ (global) |
| Code generation | ❌ | ❌ | Optional | ❌ | ❌ | Required | ❌ |
| DevTools | Basic | Basic | Good | Excellent | Basic | Good | Excellent |
| Community | All | Large | Growing | Large | Medium | Small | Small |
| Flutter team recommended | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |

---

## Q2: When should you use each solution?

```
App Size / Complexity     → Recommended Solution
─────────────────────────────────────────────────────
Prototype / Demo          → setState
Small app (1-5 screens)   → setState + Provider
Medium app (5-20 screens) → Provider or Riverpod
Large app (20+ screens)   → Riverpod or BLoC
Enterprise (team of 10+)  → BLoC (strict architecture)
```

### Decision Tree
```
Is state shared across screens?
├── No → setState (ephemeral state)
└── Yes → Do you need compile-time safety?
    ├── No → Provider (simple, well-known)
    └── Yes → Do you prefer event-driven architecture?
        ├── Yes → BLoC (events → states, great for complex flows)
        └── No → Riverpod (providers, flexible, modern)
```

---

## Q3: How do you migrate from Provider to Riverpod?

```dart
// Provider (before)
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;
  void increment() { _count++; notifyListeners(); }
}

// Provide
ChangeNotifierProvider(
  create: (_) => CounterModel(),
  child: MyApp(),
)

// Consume
final count = context.watch<CounterModel>().count;
context.read<CounterModel>().increment();

// ────────────────────────────────────────

// Riverpod (after)
class CounterNotifier extends Notifier<int> {
  @override
  int build() => 0;
  void increment() => state++;
}

final counterProvider = NotifierProvider<CounterNotifier, int>(CounterNotifier.new);

// Provide
ProviderScope(child: MyApp())

// Consume
final count = ref.watch(counterProvider);
ref.read(counterProvider.notifier).increment();
```

### Migration Steps
1. Add `flutter_riverpod` to pubspec.yaml
2. Wrap app in `ProviderScope` (replaces `MultiProvider`)
3. Convert `ChangeNotifier` models to `Notifier` classes
4. Convert `ChangeNotifierProvider` to `NotifierProvider`
5. Replace `context.watch` → `ref.watch`
6. Replace `context.read` → `ref.read`
7. Replace `Consumer` → `Consumer` (same name, different package)
8. Replace `Selector` → `ref.watch` with `select`

---

## Q4: How do you migrate from BLoC to Riverpod?

```dart
// BLoC (before)
abstract class CounterEvent {}
class Increment extends CounterEvent {}

class CounterBloc extends Bloc<CounterEvent, int> {
  CounterBloc() : super(0) {
    on<Increment>((event, emit) => emit(state + 1));
  }
}

// Provide
BlocProvider(create: (_) => CounterBloc(), child: CounterScreen())

// Consume
BlocBuilder<CounterBloc, int>(
  builder: (context, count) => Text('$count'),
)
context.read<CounterBloc>().add(Increment());

// ────────────────────────────────────────

// Riverpod (after)
class CounterNotifier extends Notifier<int> {
  @override
  int build() => 0;
  void increment() => state++;
}

final counterProvider = NotifierProvider<CounterNotifier, int>(CounterNotifier.new);

// Consume
final count = ref.watch(counterProvider);
ref.read(counterProvider.notifier).increment();
```

### Key Differences
| BLoC | Riverpod |
|------|----------|
| Events (classes) | Function calls |
| States (classes) | State value (any type) |
| `BlocBuilder` | `ref.watch` |
| `context.read<Bloc>().add(Event)` | `ref.read(provider.notifier).method()` |
| `bloc_test` | Standard `test` |
| More boilerplate | Less boilerplate |

---

## Q5: Performance comparison

```
Rebuild efficiency (best to worst):
1. Selector/Select — only rebuilds when specific value changes
2. Consumer/Observer — rebuilds specific subtree
3. context.watch/ref.watch — rebuilds entire build()
4. setState — rebuilds entire State widget

Memory overhead (best to worst):
1. setState — zero overhead
2. ValueNotifier — minimal
3. Provider/Riverpod — low
4. BLoC — medium (streams)
5. Redux — medium (single large store)
6. MobX — medium (code generation)
7. GetX — low (but global state)
```

### Rebuild Scope Comparison
```dart
// setState — rebuilds entire State widget
setState(() => _count++);
// → entire build() re-runs

// Provider Consumer — rebuilds only builder
Consumer<CounterModel>(
  builder: (context, model, child) => Text('${model.count}'),
  child: ExpensiveWidget(),  // Not rebuilt
)

// Provider Selector — rebuilds only when selected value changes
Selector<CartModel, int>(
  selector: (_, cart) => cart.itemCount,  // Only if itemCount changes
  builder: (_, count, __) => Text('$count'),
)

// Riverpod — same as Consumer/Selector
final count = ref.watch(counterProvider.select((c) => c.count));

// BLoC BlocBuilder with buildWhen
BlocBuilder<CounterBloc, int>(
  buildWhen: (prev, curr) => prev != curr,
  builder: (_, count) => Text('$count'),
)
```

---

## Q6: Code comparison — same feature in all solutions

### Counter app in 6 solutions

```dart
// 1. setState
class CounterScreen extends StatefulWidget {
  @override
  State<CounterScreen> createState() => _CounterScreenState();
}
class _CounterScreenState extends State<CounterScreen> {
  int _count = 0;
  @override
  Widget build(BuildContext context) => Text('${_count++}');
}

// 2. Provider
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;
  void increment() { _count++; notifyListeners(); }
}
// Widget: context.watch<CounterModel>().count

// 3. Riverpod
class CounterNotifier extends Notifier<int> {
  @override int build() => 0;
  void increment() => state++;
}
final counterProvider = NotifierProvider<CounterNotifier, int>(CounterNotifier.new);
// Widget: ref.watch(counterProvider)

// 4. BLoC (Cubit)
class CounterCubit extends Cubit<int> {
  CounterCubit() : super(0);
  void increment() => emit(state + 1);
}
// Widget: BlocBuilder<CounterCubit, int>(builder: (_, count) => Text('$count'))

// 5. GetX
class CounterController extends GetxController {
  var count = 0.obs;
  void increment() => count.value++;
}
// Widget: Obx(() => Text('${controller.count.value}'))

// 6. MobX
class CounterStore = _CounterStore with _$CounterStore;
abstract class _CounterStore with Store {
  @observable int count = 0;
  @action void increment() => count++;
}
// Widget: Observer(builder: (_) => Text('${store.count}'))
```

### Lines of code for counter
| Solution | Lines | Files |
|----------|-------|-------|
| setState | 8 | 1 |
| GetX | 6 | 1 |
| Riverpod | 7 | 1 |
| Provider | 10 | 2 |
| MobX | 8 | 2 (+ codegen) |
| BLoC | 12 | 2 |
| Redux | 25 | 3 |

---

## Q7: Community and ecosystem comparison

| Solution | Pub Likes | GitHub Stars | Trending | Job Posts |
|----------|-----------|-------------|----------|-----------|
| Provider | 6.5K+ | 5.8K | Stable | High |
| Riverpod | 3K+ | 5.5K | ↑ Growing | High |
| BLoC | 6K+ | 11K | Stable | High |
| GetX | 4.5K+ | 9.5K | ↓ Declining | Medium |
| MobX | 1.5K+ | 2.3K | Stable | Low |
| Redux | 0.5K+ | 1.6K | ↓ Declining | Low |

### 2024+ Recommendation
```
For new projects:
  → Riverpod (compile-safe, modern, growing)
  → BLoC (if team prefers event-driven, strict architecture)

For existing projects:
  → Provider (if already using, no need to migrate)
  → BLoC (if already using, well-established)

Avoid for new projects:
  → GetX (anti-patterns, not recommended by Flutter team)
  → Redux (not idiomatic Flutter, high boilerplate)
  → MobX (code generation overhead, smaller community)
```

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [Best Practices](BestPractices.md)
- [Provider](Provider.md) | [Riverpod](Riverpod.md) | [BLoC](BLoC.md)
