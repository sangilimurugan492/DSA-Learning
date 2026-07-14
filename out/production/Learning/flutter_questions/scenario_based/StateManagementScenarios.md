# State Management Scenarios

## Scenario 1: Shopping Cart Across Multiple Screens

### Problem
A shopping cart needs to be accessible from product list, product detail, and cart screens. How do you manage this state?

```dart
// ❌ Bad — passing state through constructors (prop drilling)
class ProductList extends StatelessWidget {
  final List<CartItem> cart;  // Passed from parent
  final Function(CartItem) onAdd;  // Callback to update

  Widget build(BuildContext context) {
    return ListView(
      children: products.map((p) => ProductCard(
        product: p,
        cart: cart,  // Passed down again
        onAdd: onAdd,  // Callback passed down
      )).toList(),
    );
  }
}
// Problem: Every screen needs cart + callback → unmaintainable
```

### Solution: Provider with ChangeNotifier

```dart
// ✅ Good — Provider at app root
class CartModel extends ChangeNotifier {
  final List<CartItem> _items = [];
  List<CartItem> get items => List.unmodifiable(_items);

  double get totalPrice =>
      _items.fold(0, (sum, item) => sum + item.price * item.quantity);

  void add(Product product) {
    final existing = _items.indexWhere((i) => i.productId == product.id);
    if (existing != -1) {
      _items[existing].quantity++;
    } else {
      _items.add(CartItem(productId: product.id, price: product.price));
    }
    notifyListeners();
  }

  void remove(String productId) {
    _items.removeWhere((i) => i.productId == productId);
    notifyListeners();
  }

  void clear() {
    _items.clear();
    notifyListeners();
  }
}

// Provide at root
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => CartModel(),
      child: const MyApp(),
    ),
  );
}

// Product List — reads cart, adds items
class ProductList extends StatelessWidget {
  const ProductList({super.key});
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: products.length,
      itemBuilder: (context, index) {
        final product = products[index];
        return ListTile(
          title: Text(product.name),
          trailing: IconButton(
            icon: const Icon(Icons.add_shopping_cart),
            onPressed: () => context.read<CartModel>().add(product),
          ),
        );
      },
    );
  }
}

// Cart Screen — reads cart items and total
class CartScreen extends StatelessWidget {
  const CartScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return Consumer<CartModel>(
      builder: (context, cart, _) {
        return Column(
          children: [
            Expanded(
              child: ListView.builder(
                itemCount: cart.items.length,
                itemBuilder: (context, index) => CartItemTile(
                  item: cart.items[index],
                  onRemove: () => cart.remove(cart.items[index].productId),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Text('Total: \$${cart.totalPrice.toStringAsFixed(2)}'),
            ),
          ],
        );
      },
    );
  }
}
```

### Key Takeaway
- Use `ChangeNotifier` + `Provider` for shared state across screens
- `context.read<T>()` in callbacks (no rebuild)
- `Consumer<T>` or `context.watch<T>()` where UI needs to rebuild
- No prop drilling — state is available anywhere in the tree

---

## Scenario 2: Authentication Flow with Redirect

### Problem
The app should show login screen when logged out, home screen when logged in. How do you implement this flow?

```dart
// ❌ Bad — checking auth in every screen
class HomeScreen extends StatelessWidget {
  Widget build(BuildContext context) {
    if (authService.isLoggedIn) {  // Check on every screen
      return Content();
    }
    return LoginScreen();  // Redirect manually
  }
}
```

### Solution: StreamBuilder with auth state

```dart
// ✅ Good — reactive auth state
class AuthService extends ChangeNotifier {
  User? _user;
  User? get user => _user;
  bool get isLoggedIn => _user != null;

  Future<void> login(String email, String password) async {
    _user = await api.login(email, password);
    notifyListeners();
  }

  Future<void> logout() async {
    await api.logout();
    _user = null;
    notifyListeners();
  }
}

// Or with stream (Firebase Auth)
Stream<User?> get authState => FirebaseAuth.instance.authStateChanges();

// App root — decide which screen to show
class AuthWrapper extends StatelessWidget {
  const AuthWrapper({super.key});
  @override
  Widget build(BuildContext context) {
    return Consumer<AuthService>(
      builder: (context, auth, _) {
        if (auth.isLoggedIn) {
          return const HomeScreen();
        }
        return const LoginScreen();
      },
    );
  }
}

// With go_router — redirect based on auth
final router = GoRouter(
  initialLocation: '/',
  redirect: (context, state) {
    final isLoggedIn = context.read<AuthService>().isLoggedIn;
    final isLoginRoute = state.matchedLocation == '/login';

    if (!isLoggedIn && !isLoginRoute) return '/login';
    if (isLoggedIn && isLoginRoute) return '/';
    return null;  // No redirect
  },
  routes: [
    GoRoute(path: '/', builder: (_, __) => const HomeScreen()),
    GoRoute(path: '/login', builder: (_, __) => const LoginScreen()),
  ],
);

// Login
ElevatedButton(
  onPressed: () async {
    await context.read<AuthService>().login(email, password);
    // AuthWrapper auto-rebuilds → shows HomeScreen
    // No manual navigation needed
  },
  child: const Text('Login'),
)
```

### Key Takeaway
- Use reactive auth state (ChangeNotifier or Stream)
- AuthWrapper decides which screen to show based on state
- With go_router, use `redirect` for auth guards
- No manual navigation after login — UI reacts to state change

---

## Scenario 3: Form State with Validation

### Problem
A multi-field form needs validation, error messages, and submit state. How do you manage this?

```dart
// ❌ Bad — separate controllers, manual validation
class BadForm extends StatefulWidget {
  @override
  State<BadForm> createState() => _BadFormState();
}

class _BadFormState extends State<BadForm> {
  String _email = '';
  String _password = '';
  String? _emailError;
  String? _passwordError;
  bool _isLoading = false;

  void _submit() {
    setState(() {
      _emailError = _email.contains('@') ? null : 'Invalid email';
      _passwordError = _password.length >= 6 ? null : 'Too short';
      _isLoading = true;
    });
    // ... messy, error-prone
  }
}
```

### Solution: Form + TextFormField + GlobalKey

```dart
// ✅ Good — Form widget with validators
class LoginForm extends StatefulWidget {
  const LoginForm({super.key});
  @override
  State<LoginForm> createState() => _LoginFormState();
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
    if (!_formKey.currentState!.validate()) return;  // Validate all fields

    setState(() => _isLoading = true);
    try {
      await context.read<AuthService>().login(
        _emailController.text,
        _passwordController.text,
      );
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString())),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            controller: _emailController,
            decoration: const InputDecoration(labelText: 'Email'),
            keyboardType: TextInputType.emailAddress,
            validator: (value) {
              if (value == null || value.isEmpty) return 'Required';
              if (!value.contains('@')) return 'Invalid email';
              return null;
            },
          ),
          TextFormField(
            controller: _passwordController,
            decoration: const InputDecoration(labelText: 'Password'),
            obscureText: true,
            validator: (value) {
              if (value == null || value.isEmpty) return 'Required';
              if (value.length < 6) return 'Min 6 characters';
              return null;
            },
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _isLoading ? null : _submit,
            child: _isLoading
                ? const SizedBox(
                    width: 20, height: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : const Text('Login'),
          ),
        ],
      ),
    );
  }
}
```

### Key Takeaway
- Use `Form` + `GlobalKey<FormState>` for form-level validation
- `TextFormField.validator` handles per-field validation
- `_formKey.currentState!.validate()` validates all fields at once
- Always `dispose()` controllers to prevent memory leaks
- Check `mounted` after async operations before `setState`

---

## Scenario 4: Theme Switching (Dark/Light Mode)

### Problem
User can toggle between dark and light theme. The change should apply instantly across the entire app.

```dart
// ❌ Bad — passing theme down through every widget
class App extends StatelessWidget {
  final bool isDark;
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: isDark ? ThemeData.dark() : ThemeData.light(),
      home: HomeScreen(isDark: isDark),  // Passed down
    );
  }
}
```

### Solution: ThemeProvider with ChangeNotifier

```dart
// ✅ Good — centralized theme state
class ThemeProvider extends ChangeNotifier {
  ThemeMode _themeMode = ThemeMode.system;
  ThemeMode get themeMode => _themeMode;

  void toggleTheme() {
    _themeMode = _themeMode == ThemeMode.dark
        ? ThemeMode.light
        : ThemeMode.dark;
    notifyListeners();
  }

  void setTheme(ThemeMode mode) {
    _themeMode = mode;
    notifyListeners();
  }
}

// Provide at root
void main() {
  runApp(
    ChangeNotifierProvider(
      create: (_) => ThemeProvider(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, _) {
        return MaterialApp(
          theme: ThemeData.light(useMaterial3: true),
          darkTheme: ThemeData.dark(useMaterial3: true),
          themeMode: themeProvider.themeMode,  // Auto-switches
          home: const HomeScreen(),
        );
      },
    );
  }
}

// Settings screen — toggle theme
class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final themeProvider = context.watch<ThemeProvider>();
    return SwitchListTile(
      title: const Text('Dark Mode'),
      value: themeProvider.themeMode == ThemeMode.dark,
      onChanged: (_) => context.read<ThemeProvider>().toggleTheme(),
    );
  }
}

// Persist theme with SharedPreferences
class ThemeProvider extends ChangeNotifier {
  ThemeMode _themeMode = ThemeMode.system;

  ThemeProvider() {
    _loadTheme();
  }

  Future<void> _loadTheme() async {
    final prefs = await SharedPreferences.getInstance();
    final isDark = prefs.getBool('isDark') ?? false;
    _themeMode = isDark ? ThemeMode.dark : ThemeMode.light;
    notifyListeners();
  }

  Future<void> toggleTheme() async {
    _themeMode = _themeMode == ThemeMode.dark
        ? ThemeMode.light
        : ThemeMode.dark;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isDark', _themeMode == ThemeMode.dark);
  }
}
```

### Key Takeaway
- Use `ChangeNotifier` for theme state — `MaterialApp.themeMode` reacts to it
- `ThemeMode.system` follows device setting, `.dark`/`.light` forces mode
- Persist user preference with `SharedPreferences`
- `Consumer<ThemeProvider>` wraps `MaterialApp` — only rebuilds MaterialApp on change

---

## Scenario 5: Real-Time Data with Streams

### Problem
A chat app needs to display messages in real-time as they arrive from Firestore.

```dart
// ❌ Bad — polling with FutureBuilder
class ChatScreen extends StatefulWidget {
  @override
  void initState() {
    super.initState();
    _loadMessages();  // Load once
    _timer = Timer.periodic(Duration(seconds: 2), (_) => _loadMessages());  // Poll
  }
  // Problem: 2-second delay, unnecessary network calls, no real-time
}
```

### Solution: StreamBuilder with Firestore snapshots

```dart
// ✅ Good — StreamBuilder for real-time updates
class ChatScreen extends StatelessWidget {
  final String chatId;
  const ChatScreen({super.key, required this.chatId});

  @override
  Widget build(BuildContext context) {
    return StreamBuilder<QuerySnapshot>(
      stream: FirebaseFirestore.instance
          .collection('chats')
          .doc(chatId)
          .collection('messages')
          .orderBy('timestamp', descending: true)
          .limit(50)
          .snapshots(),  // Real-time stream
      builder: (context, snapshot) {
        if (snapshot.hasError) {
          return const Center(child: Text('Something went wrong'));
        }

        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }

        final messages = snapshot.data!.docs
            .map((doc) => Message.fromDoc(doc))
            .toList();

        if (messages.isEmpty) {
          return const Center(child: Text('No messages yet'));
        }

        return ListView.builder(
          reverse: true,  // Start from bottom
          itemCount: messages.length,
          itemBuilder: (context, index) {
            final message = messages[index];
            return MessageBubble(
              message: message,
              isMe: message.senderId == currentUserId,
            );
          },
        );
      },
    );
  }
}

// With Riverpod — more testable
final messagesProvider = StreamProvider.family<List<Message>, String>((ref, chatId) {
  return FirebaseFirestore.instance
      .collection('chats')
      .doc(chatId)
      .collection('messages')
      .orderBy('timestamp', descending: true)
      .snapshots()
      .map((snapshot) => snapshot.docs.map(Message.fromDoc).toList());
});

// Widget
class ChatScreen extends ConsumerWidget {
  const ChatScreen({super.key, required this.chatId});
  final String chatId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final messagesAsync = ref.watch(messagesProvider(chatId));

    return messagesAsync.when(
      data: (messages) => ListView.builder(
        reverse: true,
        itemCount: messages.length,
        itemBuilder: (_, i) => MessageBubble(message: messages[i]),
      ),
      loading: () => const CircularProgressIndicator(),
      error: (e, _) => Text('Error: $e'),
    );
  }
}
```

### Key Takeaway
- Use `StreamBuilder` for real-time data (Firestore, WebSocket, sensors)
- `StreamBuilder` auto-manages subscription and cleanup
- `reverse: true` on ListView for chat UI (starts from bottom)
- With Riverpod, `StreamProvider` is more testable (can override in tests)
- Never poll — streams give instant updates with less network traffic

---

## 🔗 Related Topics
- [State Management](../beginner/StateManagement.md)
- [State Management Advanced](../intermediate/StateManagementAdvanced.md)
- [Firebase Integration](../intermediate/FirebaseIntegration.md)
