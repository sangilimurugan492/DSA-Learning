# Riverpod

## Q1: What is Riverpod and how is it different from Provider?

Riverpod is a compile-safe, testable state management solution — an evolution of Provider.

```dart
// pubspec.yaml: flutter_riverpod: ^2.4.0

// Provider (old) — runtime error if type not found
final model = context.read<MyModel>();  // Could throw at runtime

// Riverpod (new) — compile-safe
final model = ref.read(myModelProvider);  // Type-safe, no runtime errors
```

| Feature | Provider | Riverpod |
|---------|----------|----------|
| Compile-safe | ❌ | ✅ |
| Needs BuildContext | ✅ | ❌ |
| Testable | Manual | Easy (ProviderContainer) |
| Scoped state | Hard | Easy (ProviderScope) |
| Auto-dispose | Auto | Explicit (autoDispose) |
| DevTools | Basic | Good |

---

## Q2: What are the types of providers in Riverpod?

```dart
// 1. Provider — read-only value (computed, cached)
final greetingProvider = Provider<String>((ref) {
  return 'Hello, World!';
});

// 2. StateProvider — simple mutable state
final counterProvider = StateProvider<int>((ref) => 0);
// Modify: ref.read(counterProvider.notifier).state++
// Read: ref.watch(counterProvider)

// 3. StateNotifierProvider — complex state with notifier (legacy)
class TodoNotifier extends StateNotifier<List<Todo>> {
  TodoNotifier() : super([]);
  void add(Todo todo) => state = [...state, todo];
  void toggle(String id) => state = [
    for (final t in state)
      if (t.id == id) t.copyWith(done: !t.done) else t,
  ];
}
final todoProvider = StateNotifierProvider<TodoNotifier, List<Todo>>((ref) {
  return TodoNotifier();
});

// 4. NotifierProvider — modern (Riverpod 2.0+)
class CounterNotifier extends Notifier<int> {
  @override
  int build() => 0;  // Initial state
  void increment() => state++;
  void decrement() => state--;
}
final counterProvider = NotifierProvider<CounterNotifier, int>(CounterNotifier.new);

// 5. FutureProvider — async data (loading/data/error)
final userProvider = FutureProvider<User>((ref) async {
  return api.fetchUser();
});

// 6. StreamProvider — stream data
final authProvider = StreamProvider<User?>((ref) {
  return FirebaseAuth.instance.authStateChanges();
});

// 7. ChangeNotifierProvider — for existing ChangeNotifier models
final themeProvider = ChangeNotifierProvider<ThemeModel>((ref) => ThemeModel());
```

### Provider Selection Guide
```
Need read-only value?        → Provider
Need simple mutable state?   → StateProvider
Need complex state logic?    → NotifierProvider
Need async data?             → FutureProvider
Need real-time stream?      → StreamProvider
Have existing ChangeNotifier? → ChangeNotifierProvider
```

---

## Q3: How do you consume providers?

```dart
// ConsumerWidget — for StatelessWidget
class CounterScreen extends ConsumerWidget {
  const CounterScreen({super.key});
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final count = ref.watch(counterProvider);  // Rebuilds on change
    return Text('$count');
  }
}

// ConsumerStatefulWidget — for StatefulWidget
class TodoScreen extends ConsumerStatefulWidget {
  const TodoScreen({super.key});
  @override
  ConsumerState<TodoScreen> createState() => _TodoScreenState();
}

class _TodoScreenState extends ConsumerState<TodoScreen> {
  @override
  Widget build(BuildContext context) {
    final todos = ref.watch(todoProvider);  // List<Todo>
    final notifier = ref.read(todoProvider.notifier);  // TodoNotifier

    return ListView.builder(
      itemCount: todos.length,
      itemBuilder: (_, i) => ListTile(
        title: Text(todos[i].title),
        trailing: Checkbox(
          value: todos[i].done,
          onChanged: (_) => notifier.toggle(todos[i].id),
        ),
      ),
    );
  }
}

// ref.watch — rebuilds on change (in build only)
final count = ref.watch(counterProvider);

// ref.read — read once, no rebuild (in callbacks, initState)
ref.read(counterProvider.notifier).increment();

// ref.listen — side effect on change (not rebuild)
ref.listen<int>(counterProvider, (previous, next) {
  if (next > 10) ScaffoldMessenger.of(context).showSnackBar(
    const SnackBar(content: Text('Limit reached!')),
  );
});

// Consumer — rebuild only specific subtree
Consumer(
  builder: (context, ref, child) {
    final count = ref.watch(counterProvider);
    return Text('$count');
  },
)
```

### watch vs read vs listen
| Method | Rebuilds? | Where | Use Case |
|--------|-----------|-------|----------|
| `ref.watch` | ✅ Yes | `build()` | React to state changes |
| `ref.read` | ❌ No | Callbacks | One-time access |
| `ref.listen` | ❌ No | `build()`/`initState` | Side effects (snackbar, navigation) |

---

## Q4: How do you handle async state with AsyncValue?

```dart
// FutureProvider returns AsyncValue<T>
final userProvider = FutureProvider<User>((ref) async {
  return api.fetchUser();
});

// Consume with when() — handles loading/data/error
class UserScreen extends ConsumerWidget {
  const UserScreen({super.key});
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final userAsync = ref.watch(userProvider);

    return userAsync.when(
      data: (user) => Text('Hello, ${user.name}'),
      loading: () => const CircularProgressIndicator(),
      error: (error, stack) => Text('Error: $error'),
    );
  }
}

// maybeWhen — skip some states
userAsync.maybeWhen(
  data: (user) => Text(user.name),
  orElse: () => const CircularProgressIndicator(),
);

// AsyncValue.guard — for manual async
class UserNotifier extends AsyncNotifier<User> {
  @override
  Future<User> build() => api.fetchUser();

  Future<void> refresh() async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() => api.fetchUser());
  }
}

// AsyncValue.data / AsyncValue.error
final asyncValue = AsyncValue.data(User('Alice'));
final loading = const AsyncValue.loading();
```

---

## Q5: How do you use autoDispose and family?

```dart
// autoDispose — provider is disposed when no longer listened to
final userProvider = FutureProvider.autoDispose<User>((ref) async {
  // Cancel previous request if provider is re-created
  ref.onDispose(() => print('User provider disposed'));
  return api.fetchUser();
});

// Keep alive for a duration
final userProvider = FutureProvider.autoDispose<User>((ref) async {
  ref.keepAlive();  // Don't auto-dispose
  return api.fetchUser();
});

// family — parameterized provider (create per argument)
final userByIdProvider = FutureProvider.autoDispose.family<User, int>((ref, id) async {
  return api.fetchUser(id);
});

// Usage
final user = ref.watch(userByIdProvider(42));  // User with id=42
final user2 = ref.watch(userByIdProvider(99));  // User with id=99

// Real example: paginated list
final productsProvider = FutureProvider.autoDispose.family<List<Product>, int>((ref, page) async {
  return api.fetchProducts(page: page);
});

// Usage
final page1 = ref.watch(productsProvider(1));
final page2 = ref.watch(productsProvider(2));
```

---

## Q6: How do you use Notifier and AsyncNotifier?

```dart
// Notifier — synchronous state (Riverpod 2.0+)
class CartNotifier extends Notifier<List<CartItem>> {
  @override
  List<CartItem> build() => [];  // Initial state

  void add(Product product) {
    state = [...state, CartItem(product: product)];
  }

  void remove(String id) {
    state = state.where((item) => item.id != id).toList();
  }

  void clear() => state = [];

  double get total => state.fold(0, (sum, item) => sum + item.price);
}

final cartProvider = NotifierProvider<CartNotifier, List<CartItem>>(CartNotifier.new);

// AsyncNotifier — async state
class ProductNotifier extends AsyncNotifier<List<Product>> {
  @override
  Future<List<Product>> build() => api.fetchProducts();

  Future<void> refresh() async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() => api.fetchProducts());
  }

  Future<void> addProduct(Product product) async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async {
      await api.addProduct(product);
      return [...state.value!, product];
    });
  }
}

final productProvider =
    AsyncNotifierProvider<ProductNotifier, List<Product>>(ProductNotifier.new);

// Usage
class ProductScreen extends ConsumerWidget {
  const ProductScreen({super.key});
  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final productsAsync = ref.watch(productProvider);
    final notifier = ref.read(productProvider.notifier);

    return productsAsync.when(
      data: (products) => ListView.builder(
        itemCount: products.length,
        itemBuilder: (_, i) => ListTile(title: Text(products[i].name)),
      ),
      loading: () => const CircularProgressIndicator(),
      error: (e, _) => Text('Error: $e'),
    );
  }
}
```

---

## Q7: How do you test Riverpod?

```dart
void main() {
  group('CounterNotifier', () {
    test('starts at 0', () {
      final container = ProviderContainer();
      addTearDown(container.dispose);

      expect(container.read(counterProvider), 0);
    });

    test('increment increases count', () {
      final container = ProviderContainer();
      addTearDown(container.dispose);

      container.read(counterProvider.notifier).increment();
      expect(container.read(counterProvider), 1);
    });
  });

  group('TodoNotifier', () {
    test('add todo', () {
      final container = ProviderContainer();
      addTearDown(container.dispose);

      final notifier = container.read(todoProvider.notifier);
      notifier.add(Todo(id: '1', title: 'Test'));

      expect(container.read(todoProvider), hasLength(1));
    });

    test('toggle todo', () {
      final container = ProviderContainer();
      final notifier = container.read(todoProvider.notifier);

      notifier.add(Todo(id: '1', title: 'Test', done: false));
      notifier.toggle('1');

      expect(container.read(todoProvider).first.done, true);
    });
  });

  group('With overrides', () {
    test('mock API', () async {
      final container = ProviderContainer(
        overrides: [
          apiClientProvider.overrideWithValue(MockApiClient()),
        ],
      );
      addTearDown(container.dispose);

      final user = await container.read(userProvider.future);
      expect(user.name, 'Mock User');
    });
  });

  group('Widget tests', () {
    testWidgets('Counter screen', (tester) async {
      await tester.pumpWidget(
        ProviderScope(
          child: const MaterialApp(home: CounterScreen()),
        ),
      );

      expect(find.text('0'), findsOneWidget);

      await tester.tap(find.text('+'));
      await tester.pump();

      expect(find.text('1'), findsOneWidget);
    });

    testWidgets('With override', (tester) async {
      await tester.pumpWidget(
        ProviderScope(
          overrides: [
            counterProvider.overrideWith(() => MockCounterNotifier()),
          ],
          child: const MaterialApp(home: CounterScreen()),
        ),
      );

      expect(find.text('42'), findsOneWidget);  // Mock returns 42
    });
  });
}
```

---

## Q8: How do you use ProviderScope for scoping?

```dart
// Root scope — app-wide providers
void main() {
  runApp(
    ProviderScope(
      child: const MyApp(),
    ),
  );
}

// Override for testing
ProviderScope(
  overrides: [
    apiClientProvider.overrideWithValue(MockApiClient()),
  ],
  child: const TestApp(),
)

// Nested scope — feature-level overrides
ProviderScope(
  overrides: [
    cartProvider.overrideWith(() => GuestCartNotifier()),
  ],
  child: const GuestCartScreen(),
)

// Scoped providers are isolated
// GuestCartScreen has its own cartProvider instance
// Other screens use the root cartProvider
```

---

## 🔗 Related Topics
- [Provider](Provider.md)
- [BLoC](BLoC.md)
- [Best Practices](BestPractices.md)
