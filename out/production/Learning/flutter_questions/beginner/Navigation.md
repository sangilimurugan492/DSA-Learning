# Navigation

## 📖 Explanation

Flutter navigation is based on a stack of routes. The `Navigator` manages this stack — `push` adds a route on top, `pop` removes the top route. Flutter supports both imperative (`Navigator.push`) and declarative (`go_router`) navigation.

### Navigation Stack
```
Navigator maintains a stack of routes:

push(HomeScreen)     → [HomeScreen]
push(DetailScreen)   → [HomeScreen, DetailScreen]
push(EditScreen)     → [HomeScreen, DetailScreen, EditScreen]
pop()                → [HomeScreen, DetailScreen]
popUntil(HomeScreen) → [HomeScreen]
```

### Navigation Methods
| Method | Stack Effect | Use Case |
|--------|-------------|----------|
| `push` | Add on top | Normal navigation |
| `pushReplacement` | Replace top | Login → Home |
| `pushAndRemoveUntil` | Clear + add | Reset flow |
| `pop` | Remove top | Back button |
| `popUntil` | Remove until | Back to specific screen |

### Named Routes
Routes are defined centrally in `MaterialApp`. Navigate by name with `Navigator.pushNamed()`. Arguments passed via `arguments` parameter, read via `ModalRoute.of(context).settings.arguments`.

### Imperative vs Declarative
| Imperative (Navigator) | Declarative (go_router) |
|------------------------|------------------------|
| `Navigator.push()` | `context.push('/path')` |
| Code-driven | URL-driven |
| Hard to deep link | Deep links built-in |
| No URL structure | URL = route path |
| Web: no URL change | Web: URL changes |

> **Recommendation:** Use `go_router` for new projects — supports deep links, web URLs, nested navigation.

### Bottom Navigation
`BottomNavigationBar` (Material 2) or `NavigationBar` (Material 3) with `IndexedStack` to preserve state of all screens. `IndexedStack` keeps all children in the tree — only the selected one is visible.

### Drawer
A `Drawer` is a slide-in menu attached to `Scaffold`. Use `ListView` with `ListTile`s for menu items. Always `Navigator.pop(context)` to close the drawer before navigating.

### go_router
`go_router` is the official declarative routing package. Supports: path parameters (`/detail/:id`), nested routes, `ShellRoute` (persistent UI like bottom nav), redirects, and deep links.

### ShellRoute
`ShellRoute` keeps shared UI (like bottom navigation) persistent while navigating between routes. Without it, each tab would rebuild the `Scaffold` and lose the nav bar state.

### Deep Links
Deep links open a specific screen from a URL (e.g., `myapp://product/42`). `go_router` handles deep links automatically by mapping URLs to routes. Platform config: Android (AndroidManifest.xml intent-filter), iOS (Info.plist URL schemes).

### Custom Page Transitions
Use `PageRouteBuilder` with `transitionsBuilder` for custom transitions (slide, fade, scale). For platform-specific transitions, use `MaterialPageRoute` (Android) and `CupertinoPageRoute` (iOS).

---

## 🧪 Code Example

```dart
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      initialRoute: '/',
      routes: {
        '/': (context) => const HomeScreen(),
        '/detail': (context) => const DetailScreen(),
      },
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Home')),
      body: Center(
        child: ElevatedButton(
          // Push with constructor params
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) => const DetailScreen(title: 'Product', id: 42),
              ),
            );
          },
          child: const Text('Go to Detail'),
        ),
      ),
    );
  }
}

class DetailScreen extends StatelessWidget {
  final String title;
  final int id;

  const DetailScreen({super.key, required this.title, required this.id});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(title)),
      body: Center(
        child: ElevatedButton(
          // Pop with result
          onPressed: () => Navigator.pop(context, 'Selected Item $id'),
          child: const Text('Return Result'),
        ),
      ),
    );
  }
}

// Named route navigation
// Navigator.pushNamed(context, '/detail', arguments: {'id': 42});

// Push replacement (login → home)
// Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => HomeScreen()));

// Push and remove until (clear stack)
// Navigator.pushAndRemoveUntil(
//   context,
//   MaterialPageRoute(builder: (_) => const HomeScreen()),
//   (route) => false,  // Remove all routes below
// );
```

### Output
```
- HomeScreen with "Go to Detail" button
- Tapping navigates to DetailScreen with title "Product" and id 42
- Tapping "Return Result" pops back with "Selected Item 42"
```

---

## ❓ Interview Questions

1. **How does navigation work in Flutter?**
   - Flutter navigation is stack-based. `Navigator` maintains a stack of routes. `push` adds a new route on top. `pop` removes the top route. `push` with `MaterialPageRoute` creates a platform-standard transition. You can return data by passing it to `pop(context, result)`. The calling screen receives the result via `await Navigator.push(...)`. The stack is: push(Home) → [Home], push(Detail) → [Home, Detail], pop() → [Home].

2. **What is named navigation?**
   - Named routes are defined centrally in `MaterialApp(routes: {...})`. Navigate with `Navigator.pushNamed(context, '/detail')`. Pass arguments via `arguments` parameter: `Navigator.pushNamed(context, '/detail', arguments: {'id': 42})`. Read arguments in the destination: `ModalRoute.of(context)!.settings.arguments as Map`. Use `pushReplacementNamed` to replace the current route (login → home). Use `pushNamedAndRemoveUntil` to clear the stack. Named routes are better for deep linking and medium/large apps.

3. **What is the difference between `push` and `pushReplacement`?**
   - `push` adds a new route on top of the stack: [A] → push(B) → [A, B]. The previous screen is preserved — user can go back. `pushReplacement` replaces the current route: [A] → pushReplacement(B) → [B]. The previous screen is removed — user can't go back. Use `push` for normal navigation. Use `pushReplacement` for flows like login → home (don't go back to login). `pushAndRemoveUntil` pushes a new route and removes routes below until a predicate is met — use for resetting the entire flow.

4. **How do you pass data between screens?**
   - Three approaches: (1) Constructor parameters — most common and type-safe: `DetailScreen(title: 'Product', id: 42)`. (2) Named route arguments — `Navigator.pushNamed(context, '/detail', arguments: {'id': 42})`, read via `ModalRoute.of(context)!.settings.arguments`. (3) Return data — `Navigator.pop(context, 'result')`, received via `await Navigator.push(...)` on the calling screen. For complex data, use a shared state management solution (Provider, Riverpod) instead of passing data through navigation.

5. **What is `BottomNavigationBar` and how do you implement it?**
   - `BottomNavigationBar` (Material 2) or `NavigationBar` (Material 3) provides tab-based navigation at the bottom. Use with `IndexedStack` to preserve state of all screens — `IndexedStack` keeps all children in the tree, only the selected one is visible. Store the current index in a `StatefulWidget`, update on `onTap`/`onDestinationSelected`. Each tab is a separate screen. For nested navigation within tabs, use `go_router`'s `ShellRoute`.

6. **What is `Drawer` and how do you implement it?**
   - `Drawer` is a slide-in menu attached to `Scaffold`'s `drawer` property. It contains a `ListView` with `DrawerHeader` and `ListTile`s for menu items. Always call `Navigator.pop(context)` to close the drawer before navigating — otherwise the drawer stays open over the new screen. Use `Scaffold.of(context).openDrawer()` or the app bar's `leading` hamburger icon to open it. Drawers are good for 5+ navigation items; use `BottomNavigationBar` for 2-5 items.

7. **What is `go_router` and declarative navigation?**
   - `go_router` is Flutter's official declarative routing package. It maps URLs to routes, supporting path parameters (`/detail/:id`), nested routes, `ShellRoute` (persistent UI), redirects, and deep links. Navigate with `context.go('/path')` (replace, no back) or `context.push('/path')` (push, has back). Use `MaterialApp.router(routerConfig: router)`. Unlike imperative `Navigator`, go_router is URL-driven — the URL changes on web, deep links work automatically. Use go_router for new projects.

8. **How do you implement nested navigation with `ShellRoute`?**
   - `ShellRoute` keeps shared UI (like bottom navigation) persistent while navigating between routes within the shell. The `ShellRoute` builder wraps child routes with a shared widget (e.g., `Scaffold` with `bottomNavigationBar`). Routes inside the shell share the persistent UI. Routes outside the shell (like login) don't have the bottom nav. Without `ShellRoute`, each tab would rebuild the `Scaffold` and lose the nav bar state. The shell's `child` is the current route's content — it changes on navigation, but the shell persists.

9. **How do you handle deep links in Flutter?**
   - Deep links open a specific screen from a URL (e.g., `myapp://product/42`). With `go_router`, deep links are handled automatically — URLs map to routes. Platform config: Android — add `<intent-filter>` with `<data android:scheme="myapp">` in AndroidManifest.xml. iOS — add `CFBundleURLSchemes` in Info.plist. For push notifications, store the target route in the notification payload and navigate when the user taps. Use `uni_links` package for raw deep link streams if not using go_router.

10. **How do you implement custom page transitions?**
    - Use `PageRouteBuilder` with `transitionsBuilder` for custom transitions. Slide: `Tween<Offset>(begin: Offset(1, 0), end: Offset.zero)` with `SlideTransition`. Fade: `FadeTransition(opacity: animation)`. Scale: `ScaleTransition` with `Tween<double>(begin: 0, end: 1)`. Set `transitionDuration` for speed. For platform-specific transitions, use `MaterialPageRoute` (Android slide) and `CupertinoPageRoute` (iOS slide). With `go_router`, use `CustomTransitionPage` in `pageBuilder`. Use custom transitions only when you need a unique effect — platform defaults are usually best.

---

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [State Management](StateManagement.md)
- [Navigation Scenarios](../scenario_based/NavigationScenarios.md)
