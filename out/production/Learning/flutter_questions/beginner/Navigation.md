# Navigation

## Q1: How does navigation work in Flutter?

```dart
// Basic navigation — push a new route
Navigator.push(
  context,
  MaterialPageRoute(
    builder: (context) => const DetailScreen(),
  ),
);

// Pop back
Navigator.pop(context);

// Push with result
final result = await Navigator.push(
  context,
  MaterialPageRoute(builder: (context) => const SelectionScreen()),
);
// result is returned when the second screen pops

// Return data from second screen
Navigator.pop(context, 'Selected Item');
```

### Navigation Stack
```
Navigator maintains a stack of routes:

push(HomeScreen)     → [HomeScreen]
push(DetailScreen)   → [HomeScreen, DetailScreen]
push(EditScreen)     → [HomeScreen, DetailScreen, EditScreen]
pop()                → [HomeScreen, DetailScreen]
popUntil(HomeScreen) → [HomeScreen]
```

---

## Q2: What is named navigation?

```dart
// Define routes in MaterialApp
MaterialApp(
  initialRoute: '/',
  routes: {
    '/': (context) => const HomeScreen(),
    '/detail': (context) => const DetailScreen(),
    '/settings': (context) => const SettingsScreen(),
  },
)

// Navigate by name
Navigator.pushNamed(context, '/detail');

// With arguments
Navigator.pushNamed(context, '/detail', arguments: {'id': 42});

// Read arguments in destination
class DetailScreen extends StatelessWidget {
  const DetailScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final args = ModalRoute.of(context)!.settings.arguments as Map;
    final id = args['id'];  // 42
    return Text('Detail $id');
  }
}

// Push replacement — replace current route
Navigator.pushReplacementNamed(context, '/home');

// Push and remove until — clear stack
Navigator.pushNamedAndRemoveUntil(
  context,
  '/login',
  (route) => false,  // Remove all routes below
);
```

---

## Q3: What is the difference between `push` and `pushReplacement`?

```dart
// push — add new route on top of stack
// Stack: [A] → push(B) → [A, B]
Navigator.push(context, MaterialPageRoute(builder: (_) => ScreenB()));

// pushReplacement — replace current route with new one
// Stack: [A] → pushReplacement(B) → [B]
Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => ScreenB()));

// pushAndRemoveUntil — push new, remove routes below until predicate
// Stack: [A, B, C] → pushAndRemoveUntil(D, (r) => false) → [D]
Navigator.pushAndRemoveUntil(
  context,
  MaterialPageRoute(builder: (_) => ScreenD()),
  (route) => false,  // Remove all
);

// Common pattern: login → home (don't go back to login)
Navigator.pushAndRemoveUntil(
  context,
  MaterialPageRoute(builder: (_) => const HomeScreen()),
  (route) => false,  // Clear entire stack — no back to login
);
```

| Method | Stack Effect | Use Case |
|--------|-------------|----------|
| `push` | Add on top | Normal navigation |
| `pushReplacement` | Replace top | Login → Home |
| `pushAndRemoveUntil` | Clear + add | Reset flow |
| `pop` | Remove top | Back button |
| `popUntil` | Remove until | Back to specific screen |

---

## Q4: How do you pass data between screens?

```dart
// 1. Constructor parameters (most common)
class DetailScreen extends StatelessWidget {
  final String title;
  final int id;

  const DetailScreen({super.key, required this.title, required this.id});

  @override
  Widget build(BuildContext context) {
    return Scaffold(appBar: AppBar(title: Text(title)));
  }
}

// Navigate
Navigator.push(
  context,
  MaterialPageRoute(
    builder: (_) => DetailScreen(title: 'Product', id: 42),
  ),
);

// 2. Named route with arguments
Navigator.pushNamed(context, '/detail', arguments: {'id': 42});

// 3. Return data
// Screen A:
final result = await Navigator.push(
  context,
  MaterialPageRoute(builder: (_) => const SelectionScreen()),
);
print('Selected: $result');

// Screen B:
ElevatedButton(
  onPressed: () => Navigator.pop(context, 'Option A'),
  child: const Text('Select A'),
);
```

---

## Q5: What is `BottomNavigationBar` and how do you implement it?

```dart
class MainScreen extends StatefulWidget {
  const MainScreen({super.key});
  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;

  final screens = [
    const HomeScreen(),
    const SearchScreen(),
    const ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: screens,  // Preserves state of all screens
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: (index) => setState(() => _currentIndex = index),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Search'),
          BottomNavigationBarItem(icon: Icon(Icons.person), label: 'Profile'),
        ],
      ),
    );
  }
}
```

### NavigationBar (Material 3)
```dart
NavigationBar(
  selectedIndex: _currentIndex,
  onDestinationSelected: (index) => setState(() => _currentIndex = index),
  destinations: const [
    NavigationDestination(icon: Icon(Icons.home), label: 'Home'),
    NavigationDestination(icon: Icon(Icons.search), label: 'Search'),
    NavigationDestination(icon: Icon(Icons.person), label: 'Profile'),
  ],
)
```

---

## Q6: What is `Drawer` and how do you implement it?

```dart
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Home'),
        leading: IconButton(
          icon: const Icon(Icons.menu),
          onPressed: () => Scaffold.of(context).openDrawer(),
        ),
      ),
      drawer: Drawer(
        child: ListView(
          children: [
            const DrawerHeader(
              decoration: BoxDecoration(color: Colors.blue),
              child: Text('Menu', style: TextStyle(color: Colors.white)),
            ),
            ListTile(
              leading: const Icon(Icons.home),
              title: const Text('Home'),
              onTap: () {
                Navigator.pop(context);  // Close drawer first
                Navigator.pushNamed(context, '/home');
              },
            ),
            ListTile(
              leading: const Icon(Icons.settings),
              title: const Text('Settings'),
              onTap: () {
                Navigator.pop(context);
                Navigator.pushNamed(context, '/settings');
              },
            ),
          ],
        ),
      ),
      body: const Center(child: Text('Home')),
    );
  }
}
```

---

## Q7: What is `go_router` and declarative navigation?

```dart
// go_router — official declarative routing package
// pubspec.yaml: go_router: ^12.0.0

final router = GoRouter(
  initialLocation: '/',
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => const HomeScreen(),
    ),
    GoRoute(
      path: '/detail/:id',
      builder: (context, state) {
        final id = state.pathParameters['id']!;
        return DetailScreen(id: id);
      },
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => const ProfileScreen(),
      routes: [
        GoRoute(
          path: 'edit',  // /profile/edit
          builder: (context, state) => const EditProfileScreen(),
        ),
      ],
    ),
    ShellRoute(
      builder: (context, state, child) => MainShell(child: child),
      routes: [
        GoRoute(path: '/tab1', builder: (_, __) => const Tab1()),
        GoRoute(path: '/tab2', builder: (_, __) => const Tab2()),
      ],
    ),
  ],
  errorBuilder: (context, state) => ErrorScreen(error: state.error),
);

// MaterialApp.router
MaterialApp.router(routerConfig: router);

// Navigate
context.go('/');              // Replace — no back
context.push('/detail/42');  // Push — has back
context.replace('/login');   // Replace current

// Deep links work automatically
// URL: /detail/42 → DetailScreen(id: '42')
```

### Imperative vs Declarative
| Imperative (Navigator) | Declarative (go_router) |
|------------------------|------------------------|
| `Navigator.push()` | `context.push('/path')` |
| Code-driven | URL-driven |
| Hard to deep link | Deep links built-in |
| No URL structure | URL = route path |
| Web: no URL change | Web: URL changes |

> **Recommendation:** Use `go_router` for new projects — supports deep links, web URLs, nested navigation.

---

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [State Management](StateManagement.md)
- [Navigation Scenarios](../scenario_based/NavigationScenarios.md)
