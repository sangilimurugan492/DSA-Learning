# Navigation Scenarios

## Scenario 1: Deep Link Handling

### Problem
The app needs to handle deep links like `myapp://product/42` to open the product detail screen directly.

```dart
// ❌ Bad — manual deep link parsing
void main() {
  runApp(const MyApp());
  // No deep link handling — links open the app but don't navigate
}
```

### Solution: go_router with deep links

```dart
// ✅ Good — go_router handles deep links automatically
final router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(path: '/', builder: (_, __) => const HomeScreen()),
    GoRoute(
      path: '/product/:id',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return ProductDetailScreen(productId: id);
      },
    ),
    GoRoute(
      path: '/profile',
      builder: (_, __) => const ProfileScreen(),
    ),
  ],
);

// MaterialApp.router
MaterialApp.router(routerConfig: router);

// Android: android/app/src/main/AndroidManifest.xml
<intent-filter>
  <action android:name="android.intent.action.VIEW" />
  <category android:name="android.intent.category.DEFAULT" />
  <category android:name="android.intent.category.BROWSABLE" />
  <data android:scheme="myapp" android:host="product" />
</intent-filter>

// iOS: ios/Runner/Info.plist
<key>CFBundleURLTypes</key>
<array>
  <dict>
    <key>CFBundleURLSchemes</key>
    <array>
      <string>myapp</string>
    </array>
  </dict>
</array>

// Deep link: myapp://product/42 → go_router matches /product/42
// → ProductDetailScreen(productId: '42')
```

### Key Takeaway
- `go_router` handles deep links automatically — URL path = route path
- Configure intent filter (Android) and URL scheme (iOS)
- `state.pathParameters` gives URL parameters
- Deep links work on web too — URL bar updates on navigation

---

## Scenario 2: Nested Navigation with Bottom Nav

### Problem
Each tab in a bottom navigation bar needs its own navigation stack. Switching tabs should preserve each tab's state.

```dart
// ❌ Bad — single Navigator, tabs lose state
class MainScreen extends StatefulWidget {
  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_index],  // Rebuilt on tab change → loses state
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _index,
        onTap: (i) => setState(() => _index = i),
        items: [...],
      ),
    );
  }
}
```

### Solution: IndexedStack + per-tab Navigator

```dart
// ✅ Good 1 — IndexedStack preserves all tab states
class MainScreen extends StatefulWidget {
  const MainScreen({super.key});
  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _index = 0;

  final _screens = [
    const HomeTab(),
    const SearchTab(),
    const ProfileTab(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _index,
        children: _screens,  // All kept in tree — state preserved
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _index,
        onDestinationSelected: (i) => setState(() => _index = i),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.home), label: 'Home'),
          NavigationDestination(icon: Icon(Icons.search), label: 'Search'),
          NavigationDestination(icon: Icon(Icons.person), label: 'Profile'),
        ],
      ),
    );
  }
}

// ✅ Good 2 — Each tab has its own Navigator
class HomeTab extends StatelessWidget {
  const HomeTab({super.key});

  @override
  Widget build(BuildContext context) {
    return Navigator(
      onGenerateRoute: (settings) {
        switch (settings.name) {
          case '/':
            return MaterialPageRoute(builder: (_) => const HomeScreen());
          case '/details':
            return MaterialPageRoute(builder: (_) => const DetailScreen());
          default:
            return MaterialPageRoute(builder: (_) => const HomeScreen());
        }
      },
    );
  }
}

// ✅ Good 3 — go_router ShellRoute for nested nav
final router = GoRouter(
  initialLocation: '/home',
  routes: [
    ShellRoute(
      builder: (context, state, child) => MainShell(child: child),
      routes: [
        GoRoute(
          path: '/home',
          builder: (_, __) => const HomeScreen(),
          routes: [
            GoRoute(
              path: 'details',  // /home/details
              builder: (_, __) => const DetailScreen(),
            ),
          ],
        ),
        GoRoute(
          path: '/search',
          builder: (_, __) => const SearchScreen(),
        ),
        GoRoute(
          path: '/profile',
          builder: (_, __) => const ProfileScreen(),
        ),
      ],
    ),
  ],
);

class MainShell extends StatelessWidget {
  final Widget child;
  const MainShell({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    final location = GoRouterState.of(context).uri.toString();
    final index = switch (location) {
      String s when s.startsWith('/home') => 0,
      String s when s.startsWith('/search') => 1,
      String s when s.startsWith('/profile') => 2,
      _ => 0,
    };

    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: index,
        onDestinationSelected: (i) {
          switch (i) {
            case 0: context.go('/home');
            case 1: context.go('/search');
            case 2: context.go('/profile');
          }
        },
        destinations: const [...],
      ),
    );
  }
}
```

### Key Takeaway
- `IndexedStack` keeps all tabs in tree — state preserved on tab switch
- Each tab can have its own `Navigator` for independent navigation stacks
- `ShellRoute` in go_router provides a persistent shell (bottom nav) with nested routes
- Tab state (scroll position, form input) is preserved when switching tabs

---

## Scenario 3: Auth Flow with Redirect

### Problem
Unauthenticated users should be redirected to login. After login, redirect to the originally requested page.

```dart
// ✅ Good — go_router redirect
final router = GoRouter(
  initialLocation: '/',
  refreshListenable: authProvider,  // Re-evaluate on auth change
  redirect: (context, state) {
    final isLoggedIn = authProvider.isLoggedIn;
    final goingToLogin = state.matchedLocation == '/login';

    // Not logged in → redirect to login
    if (!isLoggedIn && !goingToLogin) {
      return '/login?redirect=${state.matchedLocation}';
    }

    // Logged in but on login page → redirect to home
    if (isLoggedIn && goingToLogin) {
      return '/';
    }

    return null;  // No redirect
  },
  routes: [
    GoRoute(path: '/', builder: (_, __) => const HomeScreen()),
    GoRoute(path: '/login', builder: (_, __) => const LoginScreen()),
    GoRoute(path: '/profile', builder: (_, __) => const ProfileScreen()),
  ],
);

// After login — redirect to original page
class LoginScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final redirect = GoRouterState.of(context).uri.queryParameters['redirect'];

    return ElevatedButton(
      onPressed: () async {
        await authProvider.login(email, password);
        // redirect to original page or home
        if (redirect != null) {
          context.go(redirect);
        } else {
          context.go('/');
        }
      },
      child: const Text('Login'),
    );
  }
}
```

### Key Takeaway
- `redirect` callback runs on every navigation — check auth state
- `refreshListenable` re-evaluates redirect when auth state changes
- Store original destination in query param `?redirect=/profile`
- After login, redirect to stored destination or home
- No manual navigation — router handles everything

---

## Scenario 4: Preserving Scroll Position on Navigation

### Problem
When navigating from a list to detail and back, the list scroll position resets to top.

```dart
// ❌ Bad — ListView rebuilds from scratch on return
class ProductList extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: 1000,
      itemBuilder: (_, i) => ListTile(
        title: Text('Product $i'),
        onTap: () => Navigator.push(context,
          MaterialPageRoute(builder: (_) => DetailScreen(index: i))),
        ),
      );
  }
}
// Problem: On return, ListView starts from top — user loses position
```

### Solution: PageStorageKey + AutomaticKeepAliveClientMixin

```dart
// ✅ Good 1 — PageStorageKey preserves scroll position
ListView.builder(
  key: const PageStorageKey('product_list'),  // Preserves scroll
  itemCount: 1000,
  itemBuilder: (_, i) => ListTile(
    title: Text('Product $i'),
    onTap: () => Navigator.push(context,
      MaterialPageRoute(builder: (_) => DetailScreen(index: i))),
  ),
)

// ✅ Good 2 — AutomaticKeepAliveClientMixin (for tabbed views)
class ProductList extends StatefulWidget {
  const ProductList({super.key});
  @override
  State<ProductList> createState() => _ProductListState();
}

class _ProductListState extends State<ProductList>
    with AutomaticKeepAliveClientMixin {
  @override
  bool get wantKeepAlive => true;  // Keep state when tab switches

  @override
  Widget build(BuildContext context) {
    super.build(context);  // Required
    return ListView.builder(
      itemCount: 1000,
      itemBuilder: (_, i) => ListTile(title: Text('Product $i')),
    );
  }
}

// ✅ Good 3 — ScrollController with position restoration
class _ProductListState extends State<ProductList> {
  final _controller = ScrollController();

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      controller: _controller,
      itemCount: 1000,
      itemBuilder: (_, i) => ListTile(title: Text('Product $i')),
    );
  }
}

// ✅ Good 4 — go_router preserves state automatically
// When using go_router, the Navigator stack is preserved
// context.push('/product/42') → back returns to same scroll position
```

### Key Takeaway
- `PageStorageKey` preserves scroll position across navigation
- `AutomaticKeepAliveClientMixin` keeps state when used in `IndexedStack`/tabs
- `go_router` with `context.push()` preserves the Navigator stack
- `ScrollController` can manually save/restore scroll offset

---

## Scenario 5: Custom Page Transitions

### Problem
Different screens need different transition animations (fade, slide, scale).

```dart
// ✅ Good 1 — Custom PageRouteBuilder
Navigator.push(
  context,
  PageRouteBuilder(
    pageBuilder: (context, animation, secondaryAnimation) =>
        const DetailScreen(),
    transitionsBuilder: (context, animation, secondaryAnimation, child) {
      // Fade transition
      return FadeTransition(opacity: animation, child: child);
    },
    transitionDuration: const Duration(milliseconds: 300),
  ),
);

// Slide transition
PageRouteBuilder(
  pageBuilder: (_, anim, __) => const DetailScreen(),
  transitionsBuilder: (_, anim, __, child) {
    final offset = Tween<Offset>(
      begin: const Offset(1.0, 0.0),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: anim, curve: Curves.easeInOut));
    return SlideTransition(position: offset, child: child);
  },
)

// Scale transition
PageRouteBuilder(
  pageBuilder: (_, anim, __) => const DetailScreen(),
  transitionsBuilder: (_, anim, __, child) {
    return ScaleTransition(
      scale: Tween(begin: 0.0, end: 1.0).animate(
        CurvedAnimation(parent: anim, curve: Curves.elasticOut),
      ),
      child: child,
    );
  },
)

// ✅ Good 2 — go_router custom transitions
GoRoute(
  path: '/detail',
  pageBuilder: (context, state) => CustomTransitionPage(
    child: const DetailScreen(),
    transitionsBuilder: (_, anim, __, child) {
      return FadeTransition(opacity: anim, child: child);
    },
    transitionDuration: const Duration(milliseconds: 300),
  ),
)

// ✅ Good 3 — Global custom theme
MaterialApp(
  theme: ThemeData(
    pageTransitionsTheme: const PageTransitionsTheme(
      builders: {
        TargetPlatform.android: ZoomPageTransitionsBuilder(),
        TargetPlatform.iOS: CupertinoPageTransitionsBuilder(),
      },
    ),
  ),
)
```

### Key Takeaway
- `PageRouteBuilder` for custom transitions without a full `PageRoute` class
- `transitionsBuilder` gives animation + child — return animated widget
- Common transitions: `FadeTransition`, `SlideTransition`, `ScaleTransition`
- `go_router` uses `CustomTransitionPage` for per-route transitions
- Set global transitions in `ThemeData.pageTransitionsTheme`

---

## 🔗 Related Topics
- [Navigation](../beginner/Navigation.md)
- [State Management Scenarios](StateManagementScenarios.md)
- [Animations](../intermediate/Animations.md)
