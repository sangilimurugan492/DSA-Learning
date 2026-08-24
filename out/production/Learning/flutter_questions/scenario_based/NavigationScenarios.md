# Navigation Scenarios

## 📖 Explanation

Navigation in Flutter can be simple (`Navigator.push`) or complex (deep linking, nested navigation, conditional routing). These scenarios cover real-world navigation challenges.

### Navigation Types
| Type | Use Case | API |
|------|----------|-----|
| Imperative | `Navigator.push()` / `pop()` | Simple flows |
| Named routes | `Navigator.pushNamed('/details')` | Medium apps |
| go_router | Declarative routing | Large apps |
| Nested navigation | Bottom nav + tabs | Tab-based apps |
| Deep linking | URL → screen | Web + mobile |

### Key Navigation Concepts
- **Push/Pop stack** — screens are pushed onto a stack; pop removes the top
- **Named routes** — pre-defined route names in a routes table
- **Route guards** — check auth before allowing navigation
- **Deep linking** — external URL opens a specific screen
- **Nested navigator** — separate navigation stack per tab
- **Shell route** — shared scaffold (bottom nav) with nested pages

---

## 🧪 Code Example

```dart
// ── Scenario 1: Basic Push/Pop with Results ──
class HomeScreen extends StatelessWidget {
  Widget build(BuildContext context) {
    return ElevatedButton(
      child: const Text('Pick a color'),
      onPressed: () async {
        final result = await Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const ColorPickerScreen()),
        );
        if (result != null && context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Picked: $result')));
        }
      },
    );
  }
}

class ColorPickerScreen extends StatelessWidget {
  const ColorPickerScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Pick Color')),
      body: ListView(children: ['Red', 'Green', 'Blue'].map((color) =>
        ListTile(
          title: Text(color),
          onTap: () => Navigator.pop(context, color),  // Return result
        ),
      ).toList()),
    );
  }
}

// ── Scenario 2: Named Routes ──
MaterialApp(
  initialRoute: '/',
  routes: {
    '/': (_) => const HomeScreen(),
    '/details': (_) => const DetailScreen(),
    '/settings': (_) => const SettingsScreen(),
  },
);

// Navigate
Navigator.pushNamed(context, '/details');
Navigator.pushNamed(context, '/details', arguments: {'id': 123});

// Read arguments
final args = ModalRoute.of(context)!.settings.arguments as Map;
final id = args['id'];

// ── Scenario 3: go_router (Declarative) ──
final router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(path: '/', builder: (_, __) => const HomeScreen()),
    GoRoute(
      path: '/product/:id',
      builder: (_, state) => ProductScreen(
        id: state.pathParameters['id']!,
      ),
    ),
    GoRoute(
      path: '/profile',
      builder: (_, __) => const ProfileScreen(),
      redirect: (context, state) {
        // Route guard — redirect if not logged in
        if (!auth.isLoggedIn) return '/login';
        return null;  // Allow
      },
    ),
    // Nested navigation with shell route
    ShellRoute(
      builder: (_, __, child) => MainShell(child: child),
      routes: [
        GoRoute(path: '/home', builder: (_, __) => const HomeTab()),
        GoRoute(path: '/search', builder: (_, __) => const SearchTab()),
        GoRoute(path: '/profile', builder: (_, __) => const ProfileTab()),
      ],
    ),
  ],
  errorBuilder: (_, __) => const NotFoundScreen(),
);

MaterialApp.router(routerConfig: router);

// Navigate with go_router
context.go('/product/123');       // Replace (no back)
context.push('/product/123');    // Push (with back)

// ── Scenario 4: Bottom Navigation with Nested Navigator ──
class MainScreen extends StatefulWidget {
  const MainScreen({super.key});
  @override State<MainScreen> createState() => _MainScreenState();
}
class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;
  final _screens = [
    const HomeTab(),
    const SearchTab(),
    const ProfileTab(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _screens,  // Preserves state of each tab
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (i) => setState(() => _currentIndex = i),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Search'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),
        ],
      ),
    );
  }
}

// ── Scenario 5: Deep Linking ──
// AndroidManifest.xml: <intent-filter> with deep link config
// iOS: Associated Domains in Info.plist

// Handle incoming deep links
MaterialApp(
  onGenerateRoute: (settings) {
    final uri = Uri.parse(settings.name ?? '/');
    if (uri.pathSegments.length == 2 && uri.pathSegments[0] == 'product') {
      return MaterialPageRoute(
        builder: (_) => ProductScreen(id: uri.pathSegments[1]),
      );
    }
    return MaterialPageRoute(builder: (_) => const NotFoundScreen());
  },
);

// ── Scenario 6: Conditional Navigation (Auth Guard) ──
class AuthGuard extends StatelessWidget {
  final Widget child;
  const AuthGuard({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Consumer<AuthModel>(
      builder: (_, auth, __) {
        if (!auth.isLoggedIn) {
          // Redirect to login — use addPostFrameCallback to avoid
          // navigation during build
          WidgetsBinding.instance.addPostFrameCallback((_) {
            Navigator.pushReplacementNamed(context, '/login');
          });
          return const SizedBox();
        }
        return child;
      },
    );
  }
}

// ── Scenario 7: Custom Page Transitions ──
PageRouteBuilder(
  pageBuilder: (_, animation, __) => const DetailScreen(),
  transitionsBuilder: (_, animation, __, child) {
    return FadeTransition(
      opacity: animation,
      child: SlideTransition(
        position: Tween<Offset>(
          begin: const Offset(0, 0.1),
          end: Offset.zero,
        ).animate(CurvedAnimation(parent: animation, curve: Curves.easeOut)),
        child: child,
      ),
    );
  },
  transitionDuration: const Duration(milliseconds: 300),
)
```

### Output
```
Flutter app with comprehensive navigation:
- Push/pop with results (color picker)
- Named routes with arguments
- go_router declarative routing with path params + guards
- Bottom navigation with IndexedStack (preserves state)
- Deep linking with onGenerateRoute
- Auth guard with conditional redirect
- Custom page transitions (fade + slide)
```

---

## ❓ Interview Questions

1. **How does navigation work in Flutter?**
   - Flutter uses a stack-based navigation model. `Navigator.push(context, route)` pushes a new route onto the stack. `Navigator.pop(context)` removes the top route. The top route is what's visible. `pushReplacement` replaces the current route (no back). `pushAndRemoveUntil` clears the stack to a certain point. Each route is a `Route<T>` where T is the return type. `MaterialPageRoute` provides platform-specific transitions (slide on Android, fade on iOS). For named routes: define `routes: {'/path': (_) => Screen()}` in `MaterialApp`, navigate with `pushNamed`. For complex apps, use `go_router` for declarative routing with path parameters, query parameters, guards, and deep linking.

2. **What is the difference between push and pushReplacement?**
   - `Navigator.push(context, route)` adds a new route on top of the current one — the current screen stays in the stack, so pressing back returns to it. `Navigator.pushReplacement(context, route)` replaces the current route with the new one — the current screen is removed from the stack, so pressing back does NOT return to it. Use `push` for normal navigation (home → detail). Use `pushReplacement` when you don't want the user to go back (login → home, splash → main). `pushAndRemoveUntil(route, (route) => false)` clears the entire stack — use for logout (reset to login). `pushNamedAndRemoveUntil` is the named route version.

3. **How do you pass data between screens?**
   - Three ways: (1) Constructor: `Navigator.push(context, MaterialPageRoute(builder: (_) => DetailScreen(item: item)))`. (2) Named route arguments: `Navigator.pushNamed(context, '/details', arguments: item)`, read with `ModalRoute.of(context)!.settings.arguments`. (3) Return data: `Navigator.pop(context, result)` — the pushing screen receives it via `final result = await Navigator.push(...)`. For complex data, use a shared state management (Provider, Riverpod) so both screens access the same data without passing it. For go_router: `context.push('/product/123')` or `extra` parameter: `context.push('/details', extra: item)`, read with `state.extra`.

4. **How do you implement bottom navigation with state preservation?**
   - Use `IndexedStack` with `BottomNavigationBar`. `IndexedStack` keeps all child widgets alive — only the selected one is visible, but all maintain their state (scroll position, form data). `IndexedStack(index: _currentIndex, children: [HomeTab(), SearchTab(), ProfileTab()])`. On tab tap: `setState(() => _currentIndex = index)`. This preserves each tab's state when switching. Without `IndexedStack`, switching tabs would rebuild and lose state. For nested navigation within each tab (tab has its own back stack), use a separate `Navigator` widget for each tab. With go_router: use `ShellRoute` which provides a shared scaffold (bottom nav) with independent navigation stacks per tab.

5. **What is go_router and why use it?**
   - `go_router` is Flutter's official declarative routing package. Benefits: (1) Declarative — routes defined in a tree, not imperatively pushed. (2) Deep linking — URLs map directly to screens. (3) Path parameters — `/product/:id` extracted automatically. (4) Redirects/guards — `redirect: (context, state) => auth.isLoggedIn ? null : '/login'`. (5) Nested navigation — `ShellRoute` for shared scaffolds. (6) Web support — URL bar updates automatically. (7) Back/forward button support on web. Use `context.go('/path')` for replacement, `context.push('/path')` for stack push. Define routes as a list of `GoRoute` objects. go_router is the recommended routing solution for medium to large Flutter apps.

6. **How do you handle deep linking in Flutter?**
   - Deep linking allows external URLs/links to open specific screens in your app. (1) Add intent filters in `AndroidManifest.xml` (Android) and Associated Domains in `Info.plist` (iOS). (2) Handle the incoming URL in Flutter. With go_router: it handles deep links automatically — `/product/123` opens `ProductScreen(id: '123')`. With manual: use `onGenerateRoute` to parse the URI and return the correct route. For universal links (iOS) and app links (Android), configure the platform-specific files. Test with `adb shell am start -a android.intent.action.VIEW -d "myapp://product/123"` (Android) or `xcrun simctl openurl booted "myapp://product/123"` (iOS). Always validate deep link parameters — don't trust them blindly.

7. **How do you implement an auth guard for protected routes?**
   - With go_router: use `redirect` on protected routes: `GoRoute(path: '/profile', redirect: (context, state) => auth.isLoggedIn ? null : '/login', builder: ...)`. Return `null` to allow, return a path to redirect. For global guard: `GoRouter(redirect: (context, state) { if (!auth.isLoggedIn && state.location != '/login') return '/login'; return null; })`. With manual navigation: wrap protected screens in an `AuthGuard` widget that checks auth in `build()` and redirects with `addPostFrameCallback` if not logged in. For Riverpod: use a `redirect` provider that watches `authProvider`. Always redirect to login when auth expires mid-session — listen to auth state changes and navigate to `/login`.

8. **How do you create custom page transitions?**
   - Use `PageRouteBuilder`: `PageRouteBuilder(pageBuilder: (_, anim, __) => Screen(), transitionsBuilder: (_, anim, __, child) => FadeTransition(opacity: anim, child: child), transitionDuration: Duration(milliseconds: 300))`. Combine transitions: `FadeTransition(opacity: anim, child: SlideTransition(position: Tween(begin: Offset(1, 0), end: Offset.zero).animate(anim), child: child))`. Use `CurvedAnimation(parent: anim, curve: Curves.easeOutCubic)` for smoother animations. For platform-specific: use `MaterialPageRoute` (auto-detects platform). For go_router: use `GoRoute(path: '/', pageBuilder: (_, state) => CustomTransitionPage(child: Screen(), transitionsBuilder: ...))`. Define common transitions as reusable functions for consistency across the app.

9. **How do you handle navigation without BuildContext?**
   - Use a global navigator key: `final navigatorKey = GlobalKey<NavigatorState>()`. Assign in `MaterialApp(navigatorKey: navigatorKey)`. Navigate anywhere: `navigatorKey.currentState!.pushNamed('/details')`. This is useful for navigating from services, BLoCs, or error handlers. Alternatively, use `Get.to()` (GetX) for context-free navigation. With go_router: use `router.go('/path')` — the router instance is global. For Riverpod: create a `Provider<GoRouter>` and access from anywhere. Be careful with global navigation — it bypasses the widget tree's lifecycle. Always check if the navigator is available before navigating. Prefer context-based navigation when possible — it's safer and more testable.

10. **How do you test navigation in Flutter?**
    - Use `NavigatorObserver` to track pushed/popped routes. Create a mock observer: `class MockObserver extends NavigatorObserver { final pushedRoutes = <Route<dynamic>>[]; @override void didPush(route, prev) => pushedRoutes.add(route); }`. In widget test: `tester.pumpWidget(MaterialApp(home: HomeScreen(), navigatorObservers: [observer]))`. Tap a button, then verify: `expect(observer.pushedRoutes.last, isA<MaterialPageRoute>())`. For named routes: `expect(observer.pushedRoutes.last.settings.name, '/details')`. For go_router: use `router.routerDelegate.currentConfiguration` to verify the current route. Test deep links by setting initial location. Test back navigation with `tester.pageBack()` or `Navigator.pop`. Always test that the correct screen appears after navigation.

---

## 🔗 Related Topics
- [State Management Scenarios](StateManagementScenarios.md)
- [UI Scenarios](UIScenarios.md)
- [Architecture Patterns](../advanced/ArchitecturePatterns.md)
