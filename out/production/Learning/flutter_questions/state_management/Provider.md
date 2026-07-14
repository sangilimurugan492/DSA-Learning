# Provider

## Q1: What is Provider and how do you set it up?

Provider is a wrapper around `InheritedWidget` that makes state management simpler.

```dart
// pubspec.yaml: provider: ^6.1.0

// 1. Create a model with ChangeNotifier
class CounterModel extends ChangeNotifier {
  int _count = 0;
  int get count => _count;

  void increment() {
    _count++;
    notifyListeners();  // Notify all listeners to rebuild
  }
}

// 2. Provide at app root
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CounterModel(),
      child: const MyApp(),
    ),
  );
}

// 3. Consume in widgets
class CounterScreen extends StatelessWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final count = context.watch<CounterModel>().count;  // Rebuilds on change
    return Column(
      children: [
        Text('$count'),
        ElevatedButton(
          onPressed: () => context.read<CounterModel>().increment(),  // No rebuild
          child: const Text('+'),
        ),
      ],
    );
  }
}
```

---

## Q2: What is the difference between watch, read, and Consumer?

| Method | Rebuilds? | Use In | Use Case |
|--------|-----------|--------|----------|
| `context.watch<T>()` | ✅ Yes | `build()` | Read + rebuild on change |
| `context.read<T>()` | ❌ No | Callbacks | Read once (onPressed) |
| `Consumer<T>` | ✅ Yes | `build()` | Rebuild specific subtree only |
| `Selector<T, S>` | ✅ On select | `build()` | Rebuild only when selected value changes |

```dart
// context.watch — rebuilds entire widget on change
class CounterScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final count = context.watch<CounterModel>().count;  // Rebuilds whole build()
    return Text('$count');
  }
}

// context.read — no rebuild, just access
ElevatedButton(
  onPressed: () {
    context.read<CounterModel>().increment();  // No rebuild for this widget
  },
  child: const Text('+'),
)

// Consumer — rebuilds only the builder subtree
class CartScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Header(),  // Not rebuilt
        Consumer<CartModel>(
          builder: (context, cart, child) {
            return Text('Total: ${cart.total}');  // Only this rebuilds
          },
        ),
        const Footer(),  // Not rebuilt
      ],
    );
  }
}

// Consumer with child optimization
Consumer<CartModel>(
  builder: (context, cart, child) {
    return Column(
      children: [
        child!,  // Static widget — not rebuilt
        Text('Total: ${cart.total}'),
      ],
    );
  },
  child: const ExpensiveWidget(),  // Built once, passed to builder
)
```

---

## Q3: How do you use Selector to minimize rebuilds?

```dart
// Problem: Consumer rebuilds on ANY change to CartModel
Consumer<CartModel>(
  builder: (context, cart, _) {
    return Text('Items: ${cart.itemCount}');  // Rebuilds even if price changes
  },
)

// Solution: Selector rebuilds only when selected value changes
Selector<CartModel, int>(
  selector: (context, cart) => cart.itemCount,  // Select specific value
  builder: (context, itemCount, child) {
    return Text('Items: $itemCount');  // Only rebuilds when itemCount changes
  },
)

// Multiple fields — rebuild when either changes
Selector<CartModel, ({int count, double total})>(
  selector: (context, cart) => (count: cart.itemCount, total: cart.total),
  builder: (context, data, _) {
    return Text('${data.count} items, \$${data.total}');
  },
)

// shouldRebuild — custom comparison
Selector<CartModel, int>(
  selector: (context, cart) => cart.itemCount,
  shouldRebuild: (previous, next) => (next - previous).abs() > 1,  // Only if diff > 1
  builder: (context, count, _) => Text('$count'),
)
```

### Consumer vs Selector
```
CartModel changes (e.g., price updated):
  Consumer<CartModel>     → rebuilds (any change)
  Selector<CartModel, int> → does NOT rebuild (itemCount didn't change)
```

---

## Q4: How do you use MultiProvider?

```dart
// Multiple providers at root
void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthModel()),
        ChangeNotifierProvider(create: (_) => CartModel()),
        ChangeNotifierProvider(create: (_) => ThemeModel()),
        Provider(create: (_) => ApiClient()),  // Plain provider (no notify)
      ],
      child: const MyApp(),
    ),
  );
}

// Consume multiple
class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthModel>();
    final cart = context.watch<CartModel>();

    return Text('${auth.user.name} - ${cart.itemCount} items');
  }
}

// Nested MultiProvider (for feature-level providers)
MultiProvider(
  providers: [
    ChangeNotifierProvider(create: (_) => ProductModel()),
    ChangeNotifierProvider(create: (_) => FilterModel()),
  ],
  child: ProductScreen(),
)
```

---

## Q5: How do you use ProxyProvider and ChangeNotifierProxyProvider?

```dart
// ProxyProvider — a provider that depends on another provider
// Rebuilds when dependency changes

// ApiClient depends on AuthModel (for token)
ProxyProvider<AuthModel, ApiClient>(
  update: (context, auth, previous) => ApiClient(token: auth.token),
)

// ChangeNotifierProxyProvider — same but for ChangeNotifier
// CartModel depends on AuthModel (for user-specific cart)
MultiProvider(
  providers: [
    ChangeNotifierProvider(create: (_) => AuthModel()),
    ChangeNotifierProxyProvider<AuthModel, CartModel>(
      create: (context) => CartModel(),  // Initial
      update: (context, auth, previous) {
        previous?.setUser(auth.user);  // Update when auth changes
        return previous ?? CartModel();
      },
    ),
  ],
  child: const MyApp(),
)

// Real example: ApiClient depends on AuthModel
class ApiClient {
  final String? token;
  ApiClient({this.token});

  Future<Response> get(String path) {
    return dio.get(path, options: Options(headers: {
      if (token != null) 'Authorization': 'Bearer $token',
    }));
  }
}

// AuthModel changes → ApiClient is recreated with new token
ProxyProvider<AuthModel, ApiClient>(
  update: (_, auth, __) => ApiClient(token: auth.token),
)
```

---

## Q6: How do you use ValueNotifier with Provider?

```dart
// ValueNotifier — simpler than ChangeNotifier for single values
class CounterNotifier extends ValueNotifier<int> {
  CounterNotifier() : super(0);
  void increment() => value++;
  void decrement() => value--;
  void reset() => value = 0;
}

// Provide
ValueListenableProvider(
  create: (_) => CounterNotifier(),
  child: const MyApp(),
)

// Consume with ValueListenableBuilder (no Provider needed)
final counter = CounterNotifier();

ValueListenableBuilder<int>(
  valueListenable: counter,
  builder: (context, value, child) {
    return Text('$value');  // Rebuilds only this widget
  },
)

// ChangeNotifier vs ValueNotifier
// ChangeNotifier: multiple fields, manual notifyListeners()
// ValueNotifier: single value, auto-notify on value = X
```

---

## Q7: How do you dispose providers?

```dart
// ChangeNotifierProvider auto-disposes when provider is removed from tree
ChangeNotifierProvider(
  create: (_) => MyModel(),
  child: SomeScreen(),  // MyModel disposed when Screen is removed
)

// Manual disposal for non-ChangeNotifier
Provider(
  create: (_) => MyService(),
  dispose: (_, service) => service.dispose(),
  child: SomeScreen(),
)

// Disposable mixin
class MyModel extends ChangeNotifier with Disposable {
  final StreamSubscription _sub;

  MyModel() : _sub = someStream.listen((_) {});

  @override
  void dispose() {
    _sub.cancel();  // Cancel streams
    super.dispose();  // ChangeNotifier cleanup
  }
}

// Provider is auto-disposed — no manual cleanup needed for ChangeNotifier
// Just make sure your model's dispose() cleans up resources
```

---

## Q8: How do you test Provider?

```dart
void main() {
  group('CounterModel', () {
    test('increment increases count', () {
      final model = CounterModel();
      expect(model.count, 0);

      model.increment();
      expect(model.count, 1);
    });

    test('notifyListeners is called', () {
      final model = CounterModel();
      var notified = false;
      model.addListener(() => notified = true);

      model.increment();
      expect(notified, true);
    });
  });

  group('Widget tests', () {
    testWidgets('Counter displays initial value', (tester) async {
      await tester.pumpWidget(
        ChangeNotifierProvider(
          create: (_) => CounterModel(),
          child: const MaterialApp(home: CounterScreen()),
        ),
      );

      expect(find.text('0'), findsOneWidget);
    });

    testWidgets('Counter increments on tap', (tester) async {
      await tester.pumpWidget(
        ChangeNotifierProvider(
          create: (_) => CounterModel(),
          child: const MaterialApp(home: CounterScreen()),
        ),
      );

      await tester.tap(find.text('+'));
      await tester.pump();

      expect(find.text('1'), findsOneWidget);
    });

    testWidgets('With mock provider', (tester) async {
      await tester.pumpWidget(
        ChangeNotifierProvider(
          create: (_) => MockCartModel().._total = 99.99,
          child: const MaterialApp(home: CartScreen()),
        ),
      );

      expect(find.text('Total: \$99.99'), findsOneWidget);
    });
  });
}
```

---

## 🔗 Related Topics
- [Fundamentals](Fundamentals.md)
- [Riverpod](Riverpod.md)
- [Best Practices](BestPractices.md)
