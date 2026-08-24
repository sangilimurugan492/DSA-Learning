# Debugging Scenarios

## 📖 Explanation

Debugging is a critical Flutter skill. These scenarios cover common bugs, debugging techniques, and tools to diagnose and fix issues efficiently.

### Debugging Tools
| Tool | Purpose |
|------|---------|
| Flutter DevTools | Performance, memory, network, widget inspector |
| Dart DevTools | CPU profiler, memory profiler |
| `debugPrint()` | Console logging (rate-limited) |
| `assert()` | Runtime assertions (debug mode only) |
| Flutter Inspector | Widget tree, properties, layout |
| `flutter run --verbose` | Detailed logs |
| `flutter logs` | Device logs |
| Breakpoints | IDE debugger (step through code) |

### Common Bug Categories
| Category | Symptom | Tool |
|----------|---------|------|
| Layout overflow | Yellow/black stripes | Flutter Inspector |
| State not updating | UI doesn't change | DevTools, debugPrint |
| Memory leak | Growing memory | DevTools Memory |
| Network error | API call fails | Network tab, try/catch |
| Null reference | `Null check operator` | Stack trace, breakpoints |
| Infinite rebuild | App freezes | debugPrint in build |
| disposed widget | `setState after dispose` | Stack trace |

### Debug vs Release
| Aspect | Debug | Release |
|--------|-------|---------|
| Assertions | Enabled | Disabled |
| Logging | Visible | Stripped |
| Performance | Slow | Fast |
| Error screens | Red error screen | Crash |
| DevTools | Connected | Not connected |

---

## 🧪 Code Example

```dart
// ── Scenario 1: setState() called after dispose() ──
// ❌ Bad — crashes if widget is disposed before future completes
class _MyState extends State<MyWidget> {
  Future<void> _loadData() async {
    final data = await api.fetch();  // Takes 5 seconds
    setState(() => _data = data);    // 💥 Crash if widget was popped
  }
}

// ✅ Good — check mounted before setState
class _MyState extends State<MyWidget> {
  Future<void> _loadData() async {
    final data = await api.fetch();
    if (!mounted) return;  // Widget was disposed
    setState(() => _data = data);
  }
}

// ── Scenario 2: Layout Overflow (Yellow/Black Stripes) ──
// ❌ Bad — Column with unbounded height
Column(
  children: [
    Text('Title'),
    ListView(children: [...]),  // Takes infinite height → overflow
  ],
)

// ✅ Good — wrap in Expanded
Column(
  children: [
    Text('Title'),
    Expanded(child: ListView(children: [...])),  // Bounded
  ],
)

// Debug: Flutter Inspector shows overflow location
// Or: debugPaintSizeEnabled = true; in main()

// ── Scenario 3: Memory Leak from Undisposed Controller ──
// ❌ Bad — controller never disposed
class _MyState extends State<MyWidget> {
  late TextEditingController _controller;
  late AnimationController _animController;
  late StreamSubscription _sub;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
    _animController = AnimationController(
      vsync: this, duration: const Duration(seconds: 1));
    _sub = someStream.listen((data) { ... });
    // User pops screen → controllers leak!
  }
  // Missing dispose()!
}

// ✅ Good — dispose everything
@override
void dispose() {
  _controller.dispose();
  _animController.dispose();
  _sub.cancel();
  super.dispose();
}

// ── Scenario 4: Infinite Rebuild Loop ──
// ❌ Bad — setState in build causes infinite loop
class _MyState extends State<MyWidget> {
  int count = 0;

  @override
  Widget build(BuildContext context) {
    setState(() => count++);  // 💥 Infinite loop!
    return Text('$count');
  }
}

// ✅ Good — use initState or event handlers
@override
void initState() {
  super.initState();
  count = initializeCount();  // One-time setup
}

// ✅ For post-frame callbacks:
@override
void initState() {
  super.initState();
  WidgetsBinding.instance.addPostFrameCallback((_) {
    // Run after first build — safe to use context
    _checkDeepLink();
  });
}

// ── Scenario 5: Network Error Handling ──
// ❌ Bad — no error handling, app crashes on network failure
Future<void> fetchData() async {
  final response = await http.get(Uri.parse(url));
  final data = jsonDecode(response.body);  // 💥 Crashes on error
}

// ✅ Good — try/catch with error state
Future<void> fetchData() async {
  setState(() => _isLoading = true);
  try {
    final response = await http.get(Uri.parse(url));
    if (response.statusCode != 200) {
      throw Exception('Server error: ${response.statusCode}');
    }
    _data = jsonDecode(response.body);
  } on SocketException {
    _error = 'No internet connection';
  } on FormatException {
    _error = 'Invalid response format';
  } catch (e) {
    _error = 'Unexpected error: $e';
    debugPrint('Error fetching data: $e');  // Log for debugging
  } finally {
    if (mounted) setState(() => _isLoading = false);
  }
}

// ── Scenario 6: Using debugPrint Effectively ──
// debugPrint throttles output — better than print() for rapid logs
debugPrint('Building $runtimeType');
debugPrint('State: ${state.toString()}');
debugPrint('Items count: ${items.length}');

// For objects, override toString()
class User {
  final String name;
  final int age;
  @override
  String toString() => 'User(name: $name, age: $age)';
}

// ── Scenario 7: Flutter Inspector ──
// Run: flutter run → open DevTools → Inspector tab
// Features:
// - Select widget on screen → see in tree
// - View widget properties and constraints
// - "Render as Slow Animations" — slow down transitions
// - "Highlight Oversized Images" — find memory-heavy images
// - "Paint Baselines" — check text alignment
// - "Repaint Rainbow" — see what's repainting

// ── Scenario 8: Error Widget Customization ──
void main() {
  // Catch all errors in release mode
  FlutterError.onError = (details) {
    FlutterError.presentError(details);
    // Send to Crashlytics
    FirebaseCrashlytics.instance.recordFlutterError(details);
  };

  // Catch async errors not caught by FlutterError
  PlatformDispatcher.instance.onError = (error, stack) {
    debugPrint('Async error: $error\n$stack');
    FirebaseCrashlytics.instance.recordError(error, stack);
    return true;
  };

  runApp(const MyApp());
}

// Custom error widget (instead of red screen)
MaterialApp(
  builder: (context, widget) {
    ErrorWidget.builder = (details) => Material(
      child: Center(child: Text('Something went wrong\n${details.exception}')),
    );
    return widget!;
  },
)

// ── Scenario 9: Debugging with Breakpoints ──
// In IDE (VS Code / Android Studio):
// 1. Set breakpoint by clicking left of line number
// 2. Run in debug mode (F5 in VS Code)
// 3. Code pauses at breakpoint
// 4. Inspect variables in "Variables" panel
// 5. Step Over (F10), Step Into (F11), Continue (F5)
// 6. Conditional breakpoints: right-click → Edit Breakpoint
//    Condition: i == 50
// 7. Logpoint (VS Code): log without pausing
//    Message: "Processing item {i}"

// ── Scenario 10: Performance Debugging ──
// 1. flutter run --profile  (NOT debug!)
// 2. Open DevTools > Performance
// 3. Record → perform action → Stop
// 4. Look for:
//    - Frames > 16ms (jank)
//    - Long build() calls
//    - Excessive widget rebuilds
// 5. Fix: use const, Selector, RepaintBoundary
// 6. Re-profile to verify fix
```

### Output
```
A Flutter app with comprehensive debugging:
- mounted check to prevent setState after dispose
- Expanded to fix layout overflow
- Proper dispose() to prevent memory leaks
- No setState in build to avoid infinite loops
- Try/catch with error states for network calls
- debugPrint for logging
- Flutter Inspector for visual debugging
- Custom error widget and Crashlytics for production
- Breakpoints and conditional breakpoints in IDE
- Profile mode for performance debugging
```

---

## ❓ Interview Questions

1. **How do you debug a "setState() called after dispose()" error?**
   - This error occurs when an async operation completes after the widget is removed from the tree. Fix: check `mounted` before calling `setState()`: `if (!mounted) return; setState(() => _data = data);`. The `mounted` property is `false` after `dispose()` is called. Common scenario: API call takes 5 seconds, user pops the screen before it completes, then `setState` is called on a disposed widget → crash. Always check `mounted` after any `await` before calling `setState` or accessing `context`. For streams: cancel subscriptions in `dispose()`. For timers: cancel in `dispose()`. For animation controllers: dispose in `dispose()`. This is the #1 most common Flutter crash.

2. **How do you fix a "RenderFlex overflow" error?**
   - The yellow/black stripes appear when a widget's content exceeds its constraints. Common cause: `Column` or `Row` with a child that wants infinite size (like `ListView` or `Column` inside `Column`). Fix: wrap the expanding child in `Expanded` or `Flexible` to give it bounded constraints. For text overflow: use `Text('...', overflow: TextOverflow.ellipsis, maxLines: 1)`. For `Row` overflow: use `Expanded` or `Flexible` on children, or wrap in `SingleChildScrollView(scrollDirection: Axis.horizontal)`. Use Flutter Inspector to see the exact widget causing overflow. Use `debugPaintSizeEnabled = true` in `main()` to visualize widget boundaries. Always test on different screen sizes — overflow may only appear on small screens.

3. **How do you debug memory leaks in Flutter?**
   - Use DevTools Memory tab. (1) Take a snapshot, perform actions (navigate, scroll), take another snapshot. (2) Compare snapshots — growing object counts indicate leaks. (3) Common leak sources: undisposed controllers (`TextEditingController`, `AnimationController`, `ScrollController`), uncanceled stream subscriptions, timers not cancelled, listeners not removed. (4) Fix: always `dispose()` controllers in `dispose()`, `cancel()` subscriptions, `cancel()` timers. (5) Use `AutoDispose` in Riverpod to auto-dispose providers. (6) Check for circular references — use `WeakReference` if needed. (7) Profile in profile mode, not debug. (8) Use DevTools' "GC" button to trigger garbage collection and see if memory drops. Test by repeatedly navigating to/from a screen and checking if memory returns to baseline.

4. **How do you handle errors in a Flutter app?**
   - (1) **Widget errors**: override `ErrorWidget.builder` to show a custom error widget instead of the red screen. (2) **Sync errors**: wrap in `try/catch`. (3) **Async errors**: use `try/catch` with `async/await`. (4) **Uncaught errors**: `FlutterError.onError` for widget errors, `PlatformDispatcher.instance.onError` for async errors outside Flutter's zone. (5) **Production**: send errors to Crashlytics/Sentry: `FirebaseCrashlytics.instance.recordError(error, stack)`. (6) **Network errors**: catch `SocketException` (no internet), `TimeoutException`, `HttpException`. Show user-friendly error messages with retry buttons. (7) **State**: use `error` state in your BLoC/Provider — show error widget in UI. (8) Never silently swallow errors — at minimum `debugPrint` them.

5. **How do you debug an app that freezes/hangs?**
   - A freeze usually means the main thread is blocked. (1) Check for infinite loops: `while(true)`, recursive calls without base case, `setState` in `build()`. (2) Check for heavy synchronous computation on the main thread — move to isolate with `compute()`. (3) Check for deadlocks in async code — `await` on a `Completer` that never completes. (4) Use `debugPrint` before and after suspect code to find where it hangs. (5) Use IDE debugger with breakpoints — pause the running app and inspect the call stack. (6) Check DevTools Performance tab for long-running frames. (7) Check for infinite rebuild loops — `debugPrint('Building $runtimeType')` in `build()` and check if it logs endlessly. Fix: remove `setState` from `build()`, use `initState` or `addPostFrameCallback`.

6. **What is the Flutter Inspector and how do you use it?**
   - Flutter Inspector is a DevTools tool for visual debugging. Open: run `flutter run`, press `D` or open DevTools in browser. Features: (1) **Select Widget Mode** — click any widget on the device to see it in the widget tree. (2) **Widget tree** — navigate the widget hierarchy, see properties and constraints. (3) **Render as Slow Animations** — slow down transitions for debugging. (4) **Highlight Oversized Images** — find images using too much memory. (5) **Paint Baselines** — check text alignment. (6) **Repaint Rainbow** — see which widgets are repainting (for performance debugging). (7) **Debug Paint** — show boundaries, padding, margins. Use Inspector to understand layout issues, find the widget causing overflow, identify unnecessary rebuilds, and inspect widget properties.

7. **How do you debug network/API issues?**
   - (1) Add `try/catch` around network calls and `debugPrint` the error: `catch (e) { debugPrint('API error: $e'); }`. (2) Log the request: `debugPrint('GET $url')` and response: `debugPrint('Response: ${response.statusCode} ${response.body}')`. (3) Use DevTools Network tab to see all network requests, headers, bodies, and response times. (4) Check `response.statusCode` — 200 = success, 401 = unauthorized, 404 = not found, 500 = server error. (5) Catch specific exceptions: `SocketException` (no internet), `TimeoutException` (slow server), `FormatException` (invalid JSON). (6) Use `http` package's `interceptor` or `Dio` interceptor for centralized logging. (7) Test with `curl` or Postman to verify the API works. (8) Check if the device has internet with `connectivity_plus`. (9) Verify the API URL — common mistake is forgetting `http://` or using `localhost` on a real device (use `10.0.2.2` for Android emulator).

8. **How do you use breakpoints in Flutter?**
   - In VS Code / Android Studio: (1) Click left of a line number to set a breakpoint (red dot). (2) Run in debug mode (F5 in VS Code, Debug button in Android Studio). (3) When execution reaches the breakpoint, it pauses. (4) Inspect variables in the "Variables" or "Watch" panel. (5) **Step Over (F10)** — execute current line, move to next. (6) **Step Into (F11)** — enter the function being called. (7) **Step Out (Shift+F11)** — finish current function, return to caller. (8) **Continue (F5)** — resume until next breakpoint. (9) **Conditional breakpoint** — right-click → Edit Breakpoint → set condition (`i == 50`). (10) **Logpoint** (VS Code) — log a message without pausing. Use breakpoints to inspect state at specific points, trace execution flow, and find where variables change unexpectedly.

9. **How do you debug "widget is not updating" issues?**
   - The UI doesn't reflect state changes. Causes: (1) Forgot to call `setState()` after modifying state. (2) Called `setState()` but the widget doesn't depend on the changed state. (3) Using `const` widget that can't rebuild — remove `const` if state changes. (4) Using `context.read<T>()` in `build()` instead of `context.watch<T>()` — read doesn't trigger rebuild. (5) Using `==` comparison that fails — use `Equatable` or `equatable` package for value equality. (6) The provider/bloc is not at the right scope — the widget can't find it. (7) `notifyListeners()` not called after state change. Debug: add `debugPrint('Building $runtimeType')` in `build()` — if it doesn't print, the widget isn't rebuilding. Add `debugPrint('State changed: $state')` before `notifyListeners()`. Use Flutter Inspector to check if the widget is in the tree.

10. **How do you set up Crashlytics for production error tracking?**
    - (1) Add `firebase_crashlytics` to `pubspec.yaml`. (2) Initialize in `main()`: `await Firebase.initializeApp(); FlutterError.onError = FirebaseCrashlytics.instance.recordFlutterFatalError;`. (3) Catch async errors: `PlatformDispatcher.instance.onError = (error, stack) { FirebaseCrashlytics.instance.recordError(error, stack); return true; };`. (4) Log custom data: `FirebaseCrashlytics.instance.setCustomKey('user_id', userId); FirebaseCrashlytics.instance.log('User tapped checkout');`. (5) Manually report: `FirebaseCrashlytics.instance.recordError(error, stack, reason: 'API failure');`. (6) Test: `FirebaseCrashlytics.instance.crash();` to trigger a test crash. (7) View crashes in Firebase Console → Crashlytics. (8) For non-fatal errors, use `recordError` instead of `recordFlutterFatalError`. (9) Set user identifier: `FirebaseCrashlytics.instance.setUserIdentifier(userId);`. (10) Only works in release mode — debug mode uses Flutter's error display.

---

## 🔗 Related Topics
- [Performance Scenarios](PerformanceScenarios.md)
- [Testing](../intermediate/Testing.md)
- [Flutter Internals](../advanced/FlutterInternals.md)
