# State Management Scenarios

## 📖 Explanation

Real-world state management scenarios you'll encounter in Flutter interviews and production apps. Each scenario presents a problem, solution approach, and tradeoffs.

### Common State Management Scenarios
| Scenario | Best Solution |
|----------|--------------|
| Shopping cart across screens | Provider / Riverpod |
| Form state with validation | BLoC / Riverpod |
| Auth state across app | Provider / Riverpod at root |
| Real-time data (chat) | StreamProvider / StreamProvider |
| Offline sync | BLoC + local cache |
| Pagination | Riverpod + AsyncNotifier |

### Scenario Pattern
```
Problem → Identify state type → Choose solution → Implement → Handle edge cases
```

### Key Decision Factors
- **Scope**: Single widget vs. multiple screens vs. app-wide
- **Complexity**: Simple counter vs. multi-step form vs. real-time sync
- **Persistence**: In-memory vs. SharedPreferences vs. database
- **Testing**: How testable does it need to be?
- **Team size**: Solo vs. large team

---

## 🧪 Code Example

```dart
// ── Scenario 1: Shopping Cart Across Multiple Screens ──
// Problem: Cart needs to be accessible from product list, detail, and cart screens

// Solution: Provider at app root
class CartModel extends ChangeNotifier {
  final List<CartItem> _items = [];
  List<CartItem> get items => List.unmodifiable(_items);
  double get totalPrice => _items.fold(0, (s, i) => s + i.price);
  int get itemCount => _items.length;

  void add(Product product) {
    final existing = _items.indexWhere((i) => i.product.id == product.id);
    if (existing != -1) {
      _items[existing] = _items[existing].copyWith(
        quantity: _items[existing].quantity + 1);
    } else {
      _items.add(CartItem(product: product, quantity: 1));
    }
    notifyListeners();
  }

  void remove(String productId) {
    _items.removeWhere((i) => i.product.id == productId);
    notifyListeners();
  }

  void updateQuantity(String productId, int qty) {
    if (qty <= 0) { remove(productId); return; }
    final index = _items.indexWhere((i) => i.product.id == productId);
    if (index != -1) {
      _items[index] = _items[index].copyWith(quantity: qty);
      notifyListeners();
    }
  }
}

// Provide at root — accessible everywhere
void main() => runApp(
  ChangeNotifierProvider(create: (_) => CartModel(), child: MyApp()),
);

// Product List Screen — add to cart
class ProductListScreen extends StatelessWidget {
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: products.length,
      itemBuilder: (_, i) {
        return ListTile(
          title: Text(products[i].name),
          trailing: IconButton(
            icon: const Icon(Icons.add_shopping_cart),
            onPressed: () {
              context.read<CartModel>().add(products[i]);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('${products[i].name} added')));
            },
          ),
        );
      },
    );
  }
}

// Cart Screen — view and modify
class CartScreen extends StatelessWidget {
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Consumer<CartModel>(
        builder: (_, cart, __) => Text('Cart (${cart.itemCount})'))),
      body: Consumer<CartModel>(
        builder: (_, cart, __) => ListView.builder(
          itemCount: cart.items.length,
          itemBuilder: (_, i) => ListTile(
            title: Text(cart.items[i].product.name),
            subtitle: Text('Qty: ${cart.items[i].quantity}'),
            trailing: Text('\$${cart.items[i].totalPrice}'),
          ),
        ),
      ),
      bottomNavigationBar: Consumer<CartModel>(
        builder: (_, cart, __) => Padding(
          padding: const EdgeInsets.all(16),
          child: Text('Total: \$${cart.totalPrice}',
            style: const TextStyle(fontSize: 20))),
      ),
    );
  }
}

// ── Scenario 2: Form Validation with Real-time Feedback ──
class LoginForm extends StatefulWidget {
  const LoginForm({super.key});
  @override State<LoginForm> createState() => _LoginFormState();
}
class _LoginFormState extends State<LoginForm> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);
    try {
      await auth.signIn(_emailController.text, _passwordController.text);
      Navigator.pushReplacementNamed(context, '/home');
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Login failed: $e')));
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(children: [
        TextFormField(
          controller: _emailController,
          validator: (v) => v!.isEmpty ? 'Email required' :
            !v.contains('@') ? 'Invalid email' : null,
          decoration: const InputDecoration(labelText: 'Email'),
        ),
        TextFormField(
          controller: _passwordController,
          obscureText: true,
          validator: (v) => v!.length < 6 ? 'Min 6 characters' : null,
          decoration: const InputDecoration(labelText: 'Password'),
        ),
        ElevatedButton(
          onPressed: _isLoading ? null : _submit,
          child: _isLoading
            ? const CircularProgressIndicator()
            : const Text('Login'),
        ),
      ]),
    );
  }
}

// ── Scenario 3: Real-time Chat with Firestore ──
class ChatScreen extends StatelessWidget {
  const ChatScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<QuerySnapshot>(
      stream: FirebaseFirestore.instance
        .collection('chats/chatId/messages')
        .orderBy('timestamp', descending: true)
        .limit(50)
        .snapshots(),
      builder: (_, snapshot) {
        if (!snapshot.hasData) return const CircularProgressIndicator();
        final messages = snapshot.data!.docs;
        return ListView.builder(
          reverse: true,
          itemCount: messages.length,
          itemBuilder: (_, i) {
            final msg = messages[i].data() as Map<String, dynamic>;
            return ListTile(
              title: Text(msg['text']),
              subtitle: Text(msg['senderName']),
            );
          },
        );
      },
    );
  }
}

// ── Scenario 4: Auth State Across App ──
class AuthModel extends ChangeNotifier {
  User? _user;
  User? get user => _user;
  bool get isLoggedIn => _user != null;

  AuthModel() {
    FirebaseAuth.instance.authStateChanges().listen((user) {
      _user = user;
      notifyListeners();
    });
  }

  Future<void> signIn(String email, String password) async {
    await FirebaseAuth.instance.signInWithEmailAndPassword(
      email: email, password: password);
  }

  Future<void> signOut() async {
    await FirebaseAuth.instance.signOut();
  }
}

// App routes based on auth state
class AuthWrapper extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<AuthModel>(
      builder: (_, auth, __) {
        if (auth.isLoggedIn) return const HomeScreen();
        return const LoginScreen();
      },
    );
  }
}

// ── Scenario 5: Pagination with Riverpod ──
final paginatedProvider =
  AsyncNotifierProvider<PaginationNotifier, PaginationState>(() {
    return PaginationNotifier();
  });

class PaginationState {
  final List<Item> items;
  final bool hasMore;
  final int page;
  const PaginationState({this.items = const [], this.hasMore = true, this.page = 0});
  PaginationState copyWith({List<Item>? items, bool? hasMore, int? page}) =>
    PaginationState(items: items ?? this.items, hasMore: hasMore ?? this.hasMore, page: page ?? this.page);
}

class PaginationNotifier extends AsyncNotifier<PaginationState> {
  @override
  Future<PaginationState> build() async {
    return _loadPage(0);
  }

  Future<PaginationState> _loadPage(int page) async {
    final newItems = await api.getItems(page: page);
    return PaginationState(
      items: [...state.value!.items, ...newItems],
      hasMore: newItems.length == 20,
      page: page,
    );
  }

  Future<void> loadMore() async {
    if (!state.value!.hasMore) return;
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _loadPage(state.value!.page + 1));
  }
}
```

### Output
```
Flutter app with real-world state management scenarios:
- Shopping cart shared across screens via Provider
- Form validation with real-time feedback and loading state
- Real-time chat with Firestore StreamBuilder
- Auth state wrapper for conditional routing
- Pagination with Riverpod AsyncNotifier
```

---

## ❓ Interview Questions

1. **How would you implement a shopping cart shared across multiple screens?**
   - Use Provider with `ChangeNotifier` at the app root. Create `CartModel` with `_items` list, `add()`, `remove()`, `updateQuantity()`, and `totalPrice` getter. Call `notifyListeners()` after each change. Provide with `ChangeNotifierProvider(create: (_) => CartModel())` above `MaterialApp`. Any screen accesses the cart via `context.watch<CartModel>()` (rebuild on change) or `context.read<CartModel>()` (access in callbacks). Product list adds items, cart screen displays/modify items, checkout screen reads total. Use `Consumer` or `Selector` to limit rebuilds. For quantity updates, find existing item by product ID and increment or add new. This eliminates prop drilling — all screens access the same cart instance.

2. **How would you handle form state with validation in Flutter?**
   - Use `Form` widget with `GlobalKey<FormState>` and `TextFormField` validators. Each `TextFormField` has a `validator: (value) { return value!.isEmpty ? 'Required' : null; }` that returns null if valid or an error string. On submit, call `_formKey.currentState!.validate()` — returns false if any field is invalid, and Flutter auto-shows error text. For real-time validation, use `onChanged` callback or `autovalidateMode: AutovalidateMode.onUserInteraction`. For complex forms, use a BLoC or Riverpod to manage form state: emit `FormValid`/`FormInvalid` states. Handle loading state with a `_isLoading` flag. Always `dispose()` controllers. Use `TextEditingController` for initial values and reading text.

3. **How would you implement real-time chat with Firestore?**
   - Use `StreamBuilder<QuerySnapshot>` with Firestore `snapshots()`. Stream: `FirebaseFirestore.instance.collection('chats/$chatId/messages').orderBy('timestamp', descending: true).limit(50).snapshots()`. The `StreamBuilder` auto-rebuilds when new messages arrive. Use `reverse: true` on `ListView.builder` to show latest at the bottom. For sending: `collection.add({'text': text, 'senderId': uid, 'timestamp': FieldValue.serverTimestamp()})`. Handle loading (`!snapshot.hasData`) and error (`snapshot.hasError`) states. For pagination, use `startAfterDocument(lastDoc)` with a "load more" button. For offline, Firestore has built-in cache. For typing indicators, use a `presence` collection with real-time updates. Use `ScrollController` to auto-scroll to the latest message.

4. **How would you manage authentication state across the app?**
   - Create an `AuthModel extends ChangeNotifier` that wraps `FirebaseAuth.instance.authStateChanges()` — a `Stream<User?>`. In the constructor, listen to the stream and call `notifyListeners()` on change. Provide at app root with `ChangeNotifierProvider`. Create an `AuthWrapper` that uses `Consumer<AuthModel>` to show `LoginScreen` if `!isLoggedIn` or `HomeScreen` if `isLoggedIn`. This automatically navigates on login/logout — no manual `Navigator.push` needed. For token persistence, FirebaseAuth handles this automatically. For custom auth, store token in `flutter_secure_storage` and check on app start. For BLoC: use `AuthBloc` with `AuthStateChanged` event. For Riverpod: `StreamProvider<User?>` wrapping `authStateChanges()`.

5. **How would you implement pagination for a large list?**
   - Use Riverpod `AsyncNotifierProvider` or BLoC. State: `PaginationState { items, hasMore, page, isLoading }`. Initial load: fetch first page (20 items). On scroll near bottom: call `loadMore()` — fetch next page, append to existing items, update `hasMore` (false if fewer than page size returned). Use `ScrollController` with listener: `if (controller.position.pixels >= controller.position.maxScrollExtent * 0.8) loadMore()`. Show a loading indicator at the bottom while fetching. Handle errors with retry button. For pull-to-refresh: `RefreshIndicator` that resets to page 0. For optimization: use `ListView.builder` (only renders visible items) and cache images. For Firestore: use `startAfterDocument(lastDoc)` for cursor-based pagination — more efficient than offset.

6. **How would you handle offline data synchronization?**
   - Use a local cache (Hive, SQLite, SharedPreferences) + remote API. Pattern: (1) Read from local cache first (instant UI). (2) Fetch from API in background. (3) Update local cache and UI with fresh data. (4) Queue writes when offline — store in a `pendingChanges` list. (5) When online, sync pending changes to server. Use `connectivity_plus` to detect online/offline. For Firestore: built-in offline persistence handles this automatically. For REST API: implement a `SyncManager` that queues operations and retries when online. Show a "You're offline" banner. Use `conflictResolution` strategy (last-write-wins or merge). For BLoC: emit `OfflineState` vs `OnlineState`. Test with airplane mode.

7. **How would you manage theme and locale state?**
   - Create `SettingsModel extends ChangeNotifier` with `ThemeMode themeMode` and `Locale locale`. Persist to `SharedPreferences` — save on change, load on app start. Provide at root. `MaterialApp` reads from `Consumer<SettingsModel>`: `theme: ThemeData(brightness: settings.themeMode == ThemeMode.dark ? Brightness.dark : Brightness.light)` and `locale: settings.locale`. For dark mode: use `themeMode` (system, light, dark). For locale: use `locale` + `supportedLocales`. For Riverpod: `StateProvider<ThemeMode>` + `Provider` for SharedPreferences. For BLoC: `SettingsBloc` with `ChangeTheme` and `ChangeLocale` events. Always persist user preference — don't reset on restart. Use `shared_preferences` for simple storage, Hive for structured data.

8. **How would you handle a multi-step form/wizard?**
   - Use a `PageView` with `PageController` for swipeable steps, or a `Stepper` widget. State: `WizardState { currentStep, formData, isCompleted }`. Use BLoC or Riverpod: `WizardBloc` with events `NextStep`, `PreviousStep`, `UpdateField(key, value)`, `Submit`. Validate each step before allowing `NextStep` — if invalid, show errors and prevent navigation. Store form data in a `Map<String, dynamic>` — accumulate across steps. On final step, submit all data. Show progress indicator (step 2 of 4). Allow going back without losing data. On submit success: navigate to success screen and clear wizard state. On error: stay on current step with error message. Use `AutoDispose` (Riverpod) to clean up when the wizard is exited.

9. **How would you share state between a parent and deeply nested child?**
   - Use Provider or Riverpod instead of prop drilling. Provider: `ChangeNotifierProvider(create: (_) => MyModel(), child: ParentWidget())`. Deeply nested child accesses via `context.watch<MyModel>()` — no intermediate widgets need the data. For Riverpod: `ProviderScope` at root, `ref.watch(provider)` in child. For one-off cases, use `InheritedWidget` directly (but Provider is easier). For callbacks from child to parent: use `context.read<MyModel>().someMethod()` or a callback provided via Provider. Avoid passing data through 3+ layers — if a widget doesn't use the data, it shouldn't receive it. This is the primary reason to use state management packages — they eliminate prop drilling.

10. **How would you test a complex state management scenario?**
    - Test business logic separately from UI. For Provider: create the model, call methods, verify state: `test('add item', () { final cart = CartModel(); cart.add(product); expect(cart.itemCount, 1); })`. For BLoC: `blocTest<CartBloc, CartState>('add item', build: () => CartBloc(mockRepo), act: (b) => b.add(AddItem(product)), expect: () => [CartLoaded([item])])`. For Riverpod: `ProviderContainer(overrides: [apiProvider.overrideWithValue(mockApi)])`. Mock all external dependencies (API, Firestore, SharedPreferences). Test edge cases: empty state, error state, concurrent operations. For widget tests: provide the model/bloc, pump the widget, interact (tap, enter text), verify UI. Test side effects (navigation, snackbars) with mock observers. Aim for 80%+ coverage on business logic.

---

## 🔗 Related Topics
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [State Management Best Practices](../state_management/BestPractices.md)
- [Performance Scenarios](PerformanceScenarios.md)
