# UI Scenarios

## 📖 Explanation

Real-world UI scenarios in Flutter — building responsive layouts, custom widgets, forms, animations, and handling different screen sizes and orientations.

### Common UI Scenarios
| Scenario | Key Widgets |
|----------|-------------|
| Responsive layout | `LayoutBuilder`, `MediaQuery`, `Flexible` |
| Forms with validation | `Form`, `TextFormField`, `GlobalKey` |
| Custom widgets | `CustomPaint`, `CustomClipper`, `Tween` |
| Lists with pull-to-refresh | `RefreshIndicator`, `ListView.builder` |
| Bottom sheets & dialogs | `showModalBottomSheet`, `showDialog` |
| Dark mode | `ThemeData`, `ThemeMode` |
| Adaptive design | `Platform.isIOS`, `Cupertino` widgets |

### Responsive Design Breakpoints
| Width | Device | Layout |
|-------|--------|--------|
| < 600px | Phone | Single column |
| 600-900px | Tablet | Two column |
| > 900px | Desktop/Tablet | Three column |

### Material vs Cupertino
| Aspect | Material | Cupertino |
|--------|----------|-----------|
| Platform | Android | iOS |
| Package | `flutter/material.dart` | `flutter/cupertino.dart` |
| Style | Elevation, FAB | Flat, blur, large titles |
| Example | `AppBar`, `ElevatedButton` | `CupertinoNavigationBar`, `CupertinoButton` |

---

## 🧪 Code Example

```dart
// ── Scenario 1: Responsive Layout ──
class ResponsiveLayout extends StatelessWidget {
  const ResponsiveLayout({super.key});

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (_, constraints) {
        if (constraints.maxWidth > 900) {
          return const ThreeColumnLayout();  // Desktop/Tablet
        } else if (constraints.maxWidth > 600) {
          return const TwoColumnLayout();     // Tablet
        }
        return const SingleColumnLayout();    // Phone
      },
    );
  }
}

// Adaptive grid
class AdaptiveGrid extends StatelessWidget {
  const AdaptiveGrid({super.key});

  @override
  Widget build(BuildContext context) {
    return GridView.builder(
      gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
        maxCrossAxisExtent: 200,  // Auto-calculates columns
        crossAxisSpacing: 8,
        mainAxisSpacing: 8,
        childAspectRatio: 0.75,
      ),
      itemCount: 50,
      itemBuilder: (_, i) => Card(child: Center(child: Text('Item $i'))),
    );
  }
}

// ── Scenario 2: Dark Mode Toggle ──
class ThemeModel extends ChangeNotifier {
  ThemeMode _mode = ThemeMode.system;
  ThemeMode get mode => _mode;

  void toggle() {
    _mode = _mode == ThemeMode.dark ? ThemeMode.light : ThemeMode.dark;
    notifyListeners();
  }
}

// In MaterialApp
Consumer<ThemeModel>(
  builder: (_, theme, __) => MaterialApp(
    theme: ThemeData(brightness: Brightness.light, useMaterial3: true),
    darkTheme: ThemeData(brightness: Brightness.dark, useMaterial3: true),
    themeMode: theme.mode,
    home: const HomeScreen(),
  ),
)

// ── Scenario 3: Pull-to-Refresh List ──
class RefreshableList extends StatelessWidget {
  const RefreshableList({super.key});

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: () async {
        await context.read<ProductBloc>().refresh();
      },
      child: ListView.builder(
        itemCount: products.length,
        itemBuilder: (_, i) => ListTile(
          title: Text(products[i].name),
          subtitle: Text('\$${products[i].price}'),
        ),
      ),
    );
  }
}

// ── Scenario 4: Bottom Sheet ──
void showFilterSheet(BuildContext context) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true,  // Full height
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
    ),
    builder: (_) => Padding(
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom,
      ),
      child: const FilterOptions(),
    ),
  );
}

// ── Scenario 5: Custom Widget with CustomPaint ──
class CircularProgressBar extends StatelessWidget {
  final double progress;  // 0.0 to 1.0
  const CircularProgressBar({super.key, required this.progress});

  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      size: const Size(100, 100),
      painter: _CirclePainter(progress),
    );
  }
}

class _CirclePainter extends CustomPainter {
  final double progress;
  _CirclePainter(this.progress);

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;

    // Background circle
    canvas.drawCircle(center, radius,
      Paint()..color = Colors.grey.shade300..style = PaintingStyle.stroke..strokeWidth = 8);

    // Progress arc
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -pi / 2,  // Start from top
      2 * pi * progress,
      false,
      Paint()
        ..color = Colors.blue
        ..style = PaintingStyle.stroke
        ..strokeWidth = 8
        ..strokeCap = StrokeCap.round,
    );
  }

  @override
  bool shouldRepaint(_CirclePainter old) => old.progress != progress;
}

// ── Scenario 6: Form with Validation ──
class RegistrationForm extends StatefulWidget {
  const RegistrationForm({super.key});
  @override State<RegistrationForm> createState() => _RegistrationFormState();
}
class _RegistrationFormState extends State<RegistrationForm> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();
  bool _acceptTerms = false;

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  void _submit() {
    if (!_formKey.currentState!.validate()) return;
    if (!_acceptTerms) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please accept terms')));
      return;
    }
    // Submit registration
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(children: [
        TextFormField(
          controller: _nameController,
          decoration: const InputDecoration(labelText: 'Full Name'),
          validator: (v) => v!.isEmpty ? 'Name required' : null,
        ),
        TextFormField(
          controller: _emailController,
          decoration: const InputDecoration(labelText: 'Email'),
          validator: (v) => v!.contains('@') ? null : 'Invalid email',
          keyboardType: TextInputType.emailAddress,
        ),
        TextFormField(
          controller: _phoneController,
          decoration: const InputDecoration(labelText: 'Phone'),
          validator: (v) => v!.length >= 10 ? null : 'Invalid phone',
          keyboardType: TextInputType.phone,
        ),
        CheckboxListTile(
          value: _acceptTerms,
          onChanged: (v) => setState(() => _acceptTerms = v!),
          title: const Text('Accept Terms & Conditions'),
        ),
        ElevatedButton(onPressed: _submit, child: const Text('Register')),
      ]),
    );
  }
}

// ── Scenario 7: Adaptive Platform UI ──
class AdaptiveButton extends StatelessWidget {
  final VoidCallback onPressed;
  final Widget child;
  const AdaptiveButton({super.key, required this.onPressed, required this.child});

  @override
  Widget build(BuildContext context) {
    if (Platform.isIOS) {
      return CupertinoButton(onPressed: onPressed, child: child);
    }
    return ElevatedButton(onPressed: onPressed, child: child);
  }
}

// ── Scenario 8: Sliver App Bar (Collapsible Header) ──
CustomScrollView(
  slivers: [
    SliverAppBar(
      expandedHeight: 200,
      pinned: true,
      flexibleSpace: FlexibleSpaceBar(
        title: const Text('Products'),
        background: Image.network('url', fit: BoxFit.cover),
      ),
    ),
    SliverList(
      delegate: SliverChildBuilderDelegate(
        (_, i) => ListTile(title: Text('Product $i')),
        childCount: 50,
      ),
    ),
  ],
)
```

### Output
```
A Flutter app with comprehensive UI scenarios:
- Responsive layout (1/2/3 columns based on width)
- Dark mode toggle with ThemeModel
- Pull-to-refresh with RefreshIndicator
- Modal bottom sheet with rounded corners
- Custom circular progress with CustomPaint
- Multi-field registration form with validation
- Adaptive button (Cupertino on iOS, Material on Android)
- Collapsible SliverAppBar with image header
```

---

## ❓ Interview Questions

1. **How do you make a Flutter app responsive?**
   - Use `LayoutBuilder` to check available width and switch layouts: `if (constraints.maxWidth > 900) return WideLayout(); else return NarrowLayout()`. Use `MediaQuery.of(context).size` for screen dimensions. Use `Flexible`/`Expanded` for proportional sizing. Use `GridView` with `SliverGridDelegateWithMaxCrossAxisExtent` for auto-adjusting grids. Use `OrientationBuilder` for portrait/landscape. Avoid hardcoded pixel values — use `MediaQuery`, `EdgeInsets`, and relative sizing. Test on different screen sizes with the device preview in DevTools. For tablets/desktop, consider `NavigationRail` instead of `BottomNavigationBar`. For split views, use `Row` with `Flexible` on phone and `Row` on tablet. The `flutter_adaptive_scaffold` package provides adaptive layouts out of the box.

2. **How do you implement dark mode in Flutter?**
   - Define `theme` (light) and `darkTheme` (dark) in `MaterialApp`. Set `themeMode` to `ThemeMode.system` (follows system), `ThemeMode.light`, or `ThemeMode.dark`. Store the user's preference in `SharedPreferences`. For dynamic switching: create a `ThemeModel extends ChangeNotifier` with `ThemeMode` property. Provide at root. `MaterialApp` uses `Consumer<ThemeModel>` to read `themeMode`. Toggle: `themeMode = themeMode == ThemeMode.dark ? ThemeMode.light : ThemeMode.dark; notifyListeners()`. For colors, use `Theme.of(context).colorScheme.primary` instead of hardcoded colors — it adapts to light/dark automatically. Test with `ThemeMode.dark` in widget tests. Persist the choice so it survives app restarts.

3. **How do you build a custom widget with CustomPaint?**
   - Extend `CustomPainter` and override `paint(Canvas, Size)` and `shouldRepaint(oldDelegate)`. In `paint()`, use `Canvas` methods: `drawCircle`, `drawRect`, `drawPath`, `drawArc`, `drawLine`. Create `Paint` objects with color, style (fill/stroke), strokeWidth. Use `Path` for complex shapes: `moveTo`, `lineTo`, `cubicTo`, `close`. Return `true` from `shouldRepaint` when the data changes. Display with `CustomPaint(size: Size(100, 100), painter: MyPainter(data))`. For animations, use `AnimatedBuilder` with `CustomPaint` and pass the animation value to the painter. CustomPaint is for drawing — it doesn't handle touch. For touch, wrap in `GestureDetector` and use hit testing. Keep `paint()` efficient — it runs on every repaint.

4. **How do you create a form with validation?**
   - Use `Form` widget with `GlobalKey<FormState>`. Wrap fields in `TextFormField` with `validator: (value) { return value!.isEmpty ? 'Required' : null; }`. On submit: `if (_formKey.currentState!.validate()) { /* valid */ }`. For real-time validation: `autovalidateMode: AutovalidateMode.onUserInteraction`. Use `TextEditingController` to read/write values. Always `dispose()` controllers. For complex forms: use a BLoC/Riverpod to manage form state and emit `FormValid`/`FormInvalid` states. Use `InputDecoration` for labels, hints, icons, error text. For custom validation (async): show a loading indicator and validate in the BLoC. Use `FocusNode` to manage focus between fields. Test validation by entering invalid data and verifying error text appears.

5. **How do you implement pull-to-refresh?**
   - Use `RefreshIndicator(onRefresh: () async { await refreshData(); }, child: ListView.builder(...))`. The `onRefresh` callback must return a `Future` — `RefreshIndicator` shows the spinner until the future completes. Call your data refresh method (BLoC, Provider, API). The list rebuilds with new data after refresh. Make sure the `ListView` is scrollable — `RefreshIndicator` needs a scrollable child. For BLoC: `onRefresh: () => context.read<ProductBloc>().add(RefreshEvent())`. For error handling: if refresh fails, show a snackbar. For pull-to-refresh + pagination: handle both `RefreshIndicator` (pull down → reset to page 0) and scroll listener (scroll to bottom → load more). Test by pulling down in widget tests with `tester.fling()` and `tester.pump()`.

6. **How do you handle different screen sizes and orientations?**
   - Use `MediaQuery.of(context).size` for dimensions, `MediaQuery.of(context).orientation` for portrait/landscape. Use `LayoutBuilder` for constraint-based layouts. Use `OrientationBuilder(builder: (_, orientation) { return orientation == Orientation.portrait ? PortraitLayout() : LandscapeLayout(); })`. Use `Flexible` and `Expanded` for proportional layouts. Use `SliverGridDelegateWithMaxCrossAxisExtent` for auto-adjusting grids. Avoid `SizedBox(width: 400)` — use `MediaQuery.sizeOf(context).width * 0.8`. For tablets: use `NavigationRail`, split views, and multi-column layouts. For foldable devices: use `DisplayFeature` to avoid placing widgets on the hinge. Test with `flutter run -d <tablet>` or device preview in DevTools. Handle safe areas with `SafeArea` for notches.

7. **How do you show a bottom sheet or dialog?**
   - Bottom sheet: `showModalBottomSheet(context: context, isScrollControlled: true, builder: (_) => MySheet())`. Use `isScrollControlled: true` for full-height sheets. Handle keyboard with `Padding(padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom))`. Custom shape: `shape: RoundedRectangleBorder(borderRadius: BorderRadius.vertical(top: Radius.circular(20)))`. Dialog: `showDialog(context: context, builder: (_) => AlertDialog(title: Text('Title'), actions: [TextButton(onPressed: () => Navigator.pop(context), child: Text('OK'))])`. For custom dialogs: use `Dialog` widget. Return data: `Navigator.pop(context, result)`. Dismiss on tap outside: `barrierDismissible: true`. For persistent bottom sheets: `showBottomSheet` (not modal). Always check `context.mounted` after async operations before showing.

8. **How do you build adaptive UI for iOS and Android?**
   - Use `Platform.isIOS` / `Platform.isAndroid` to switch between `Cupertino` and `Material` widgets. Create adaptive wrappers: `class AdaptiveButton { Widget build() => Platform.isIOS ? CupertinoButton(...) : ElevatedButton(...) }`. Use `Theme.of(context).platform` for testing (set to `TargetPlatform.iOS` in tests). For navigation: use `CupertinoPageRoute` on iOS (slide transition) and `MaterialPageRoute` on Android. For full adaptive design: use the `flutter_platform_widgets` package which provides `PlatformWidget`, `PlatformScaffold`, `PlatformAppBar`. Cupertino widgets: `CupertinoNavigationBar`, `CupertinoButton`, `CupertinoTextField`, `CupertinoSwitch`, `CupertinoActionSheet`. Material widgets: `AppBar`, `ElevatedButton`, `TextField`, `Switch`, `BottomSheet`. Always test on both platforms.

9. **How do you implement a collapsible app bar?**
   - Use `CustomScrollView` with `SliverAppBar`. Set `expandedHeight: 200`, `pinned: true` (stays visible when collapsed), `flexibleSpace: FlexibleSpaceBar(title: Text('Title'), background: Image.network('url', fit: BoxFit.cover))`. The app bar collapses as you scroll up and expands when scrolling down. Use `floating: true` for the app bar to appear immediately on scroll down (without reaching the top). Use `snap: true` (requires `floating: true`) for the app bar to snap into view. For parallax effect: use `StretchMode` in `FlexibleSpaceBar`. Place content below in `SliverList` or `SliverGrid`. For a persistent header that doesn't collapse: use `SliverPersistentHeader`. For tabs below the app bar: use `SliverAppBar` with `bottom: TabBar(...)`.

10. **How do you create a reusable custom widget?**
    - Create a `StatelessWidget` or `StatefulWidget` with parameters: `class MyCard extends StatelessWidget { final String title; final String subtitle; final VoidCallback? onTap; const MyCard({super.key, required this.title, this.subtitle, this.onTap}); }`. Use `const` constructor. Make optional parameters nullable or provide defaults. Use `Key` for proper widget identity. Use `Semantics` for accessibility. Provide a `dark` variant or use `Theme.of(context)` for colors. Document with `///` dartdoc. Make it composable — accept `child` or `children`. For complex widgets: use `StatefulWidget` with controllers. For animations: use `AnimatedWidget` or `TweenAnimationBuilder`. Test with different parameter values. Publish as a package if reusable across projects. Keep the API simple — expose only what's needed.

---

## 🔗 Related Topics
- [Custom Widgets](../intermediate/CustomWidgets.md)
- [Animations](../intermediate/Animations.md)
- [Navigation Scenarios](NavigationScenarios.md)
