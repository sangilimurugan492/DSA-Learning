# Animations

## 📖 Explanation

Animations in Flutter bring UI to life — smooth transitions, movement, and visual feedback. Flutter provides two main approaches: **implicit animations** (easy, framework handles it) and **explicit animations** (full control with AnimationController).

### Implicit vs Explicit Animations
| Feature | Implicit | Explicit |
|---------|----------|----------|
| Setup | Just change a value | AnimationController + Tween |
| Control | Framework animates | You control start/stop/reverse |
| Complexity | Low | Medium-High |
| Reusable | `AnimatedXxx` widgets | `Animation` + `AnimatedBuilder` |
| Best for | Simple transitions | Complex, multi-step animations |

### Common Implicit Animation Widgets
| Widget | Animates |
|--------|----------|
| `AnimatedContainer` | color, size, decoration, padding |
| `AnimatedOpacity` | opacity (fade in/out) |
| `AnimatedPositioned` | position in Stack |
| `AnimatedSwitcher` | swapping between widgets |
| `AnimatedDefaultTextStyle` | text style |
| `AnimatedAlign` | alignment |
| `AnimatedCrossFade` | cross-fade between two widgets |

### AnimationController
- `vsync` — `TickerProvider` (use `SingleTickerProviderStateMixin`)
- `duration` — total animation time
- `.forward()` — play forward
- `.reverse()` — play backward
- `.repeat()` — loop continuously
- `.stop()` — pause
- `.value` — current value (0.0 to 1.0)
- `.dispose()` — must dispose to stop ticker

### Curves
| Curve | Effect |
|-------|--------|
| `Curves.linear` | Constant speed |
| `Curves.easeIn` | Starts slow, speeds up |
| `Curves.easeOut` | Starts fast, slows down |
| `Curves.easeInOut` | Slow at both ends |
| `Curves.bounceOut` | Bounces at end |
| `Curves.elasticOut` | Spring/elastic |

### Tween
`Tween<double>(begin: 0, end: 1)` maps animation progress (0.0–1.0) to a range. Also `ColorTween`, `IntTween`, `RectTween`, `SizeTween`. Use `.animate(curvedAnimation)` to chain.

### Animation Best Practices
- Always `dispose()` AnimationController to prevent memory leaks
- Use `SingleTickerProviderStateMixin` for 1 controller, `TickerProviderStateMixin` for multiple
- Use `const` where possible, `AnimatedBuilder` to limit rebuilds
- Prefer implicit animations for simple cases — less code, fewer bugs
- Use `Hero` for shared element transitions between screens

---

## 🧪 Code Example

```dart
// ── Implicit Animation ──
class AnimatedBox extends StatefulWidget {
  const AnimatedBox({super.key});
  @override
  State<AnimatedBox> createState() => _AnimatedBoxState();
}

class _AnimatedBoxState extends State<AnimatedBox> {
  bool _expanded = false;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => setState(() => _expanded = !_expanded),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeInOut,
        width: _expanded ? 200 : 100,
        height: _expanded ? 200 : 100,
        color: _expanded ? Colors.blue : Colors.red,
        child: const Center(child: Text('Tap')),
      ),
    );
  }
}

// ── Explicit Animation ──
class SpinningBox extends StatefulWidget {
  const SpinningBox({super.key});
  @override
  State<SpinningBox> createState() => _SpinningBoxState();
}

class _SpinningBoxState extends State<SpinningBox>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat();  // Loop forever

    _animation = CurvedAnimation(
      parent: _controller,
      curve: Curves.easeInOut,
    );
  }

  @override
  void dispose() {
    _controller.dispose();  // Must dispose!
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return RotationTransition(
      turns: _animation,
      child: Container(
        width: 100, height: 100,
        color: Colors.blue,
        child: const Center(child: Text('Spin')),
      ),
    );
  }
}

// ── Tween + AnimatedBuilder ──
class FadeSlideIn extends StatefulWidget {
  const FadeSlideIn({super.key});
  @override
  State<FadeSlideIn> createState() => _FadeSlideInState();
}

class _FadeSlideInState extends State<FadeSlideIn>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;
  late Animation<Offset> _slideAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 500),
    );

    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0)
        .animate(CurvedAnimation(parent: _controller, curve: Curves.easeIn));

    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.5),  // Start below
      end: Offset.zero,             // End at position
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeOut));

    _controller.forward();  // Start animation
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return FadeTransition(
          opacity: _fadeAnimation,
          child: SlideTransition(
            position: _slideAnimation,
            child: child,
          ),
        );
      },
      child: const Card(
        child: ListTile(title: Text('Animated item')),
      ),
    );
  }
}

// ── Hero Animation ──
// Screen 1
Hero(
  tag: 'product-image',
  child: Image.network(product.imageUrl),
)

// Screen 2
Hero(
  tag: 'product-image',  // Same tag
  child: Image.network(product.imageUrl),
)
// Flutter animates the image flying from screen 1 to screen 2
```

### Output
```
A Flutter app with animations:
- AnimatedContainer for implicit size/color transitions
- AnimationController + RotationTransition for spinning
- Tween + FadeTransition + SlideTransition for fade-slide-in
- Hero for shared element transitions between screens
```

---

## ❓ Interview Questions

1. **What are implicit animations?**
   - Implicit animations are `AnimatedXxx` widgets that animate automatically when a property changes. You just change the value (e.g., `width: _expanded ? 200 : 100`) and Flutter handles the transition over the specified `duration` with a `curve`. Examples: `AnimatedContainer` (color, size, decoration), `AnimatedOpacity` (fade), `AnimatedPositioned` (position in Stack), `AnimatedSwitcher` (swap widgets), `AnimatedDefaultTextStyle` (text style). No `AnimationController` needed — simpler, less code, fewer bugs. Best for simple, one-off transitions. The framework creates and manages the controller internally.

2. **What are explicit animations?**
   - Explicit animations use `AnimationController` + `Animation` + `AnimatedBuilder` (or `XxxTransition` widgets) for full control. You create an `AnimationController` with `vsync` and `duration`, create `Animation`s with `Tween` + `CurvedAnimation`, and control playback with `.forward()`, `.reverse()`, `.repeat()`, `.stop()`. Use `AnimatedBuilder(animation: _controller, builder: ...)` to rebuild only the animated widget. Use `SingleTickerProviderStateMixin` (1 controller) or `TickerProviderStateMixin` (multiple). Always `dispose()` the controller. Best for complex, multi-step, or continuously running animations.

3. **What is an AnimationController?**
   - `AnimationController` is a special `Animation<double>` that controls the animation's progress from 0.0 to 1.0 over a `duration`. It requires a `vsync` (TickerProvider) to sync with the screen refresh rate. Methods: `.forward()` (play), `.reverse()` (play backward), `.repeat()` (loop), `.stop()`, `.reset()`. Properties: `.value` (current progress 0.0–1.0), `.duration`, `.status` (completed/forward/reverse/dismissed). Must be `dispose()`d to stop the ticker and prevent memory leaks. Use `SingleTickerProviderStateMixin` for one controller, `TickerProviderStateMixin` for multiple.

4. **What is a Tween and how do you use it?**
   - A `Tween<T>(begin: T, end: T)` maps the animation's progress (0.0–1.0) to a value range. `Tween<double>(begin: 0.0, end: 1.0)` for opacity, `Tween<Offset>(begin: Offset(0, 1), end: Offset.zero)` for slide, `Tween<Color>(begin: Colors.red, end: Colors.blue)` for color. Apply with `.animate(animation)` to get an `Animation<T>`. Combine with `CurvedAnimation` for easing: `Tween(begin: 0, end: 100).animate(CurvedAnimation(parent: controller, curve: Curves.easeInOut))`. For types without a `Tween`, create a custom `Tween` subclass with `lerp()`.

5. **What is the difference between AnimatedBuilder and AnimatedTransition widgets?**
   - `AnimatedBuilder` is a generic builder that rebuilds on animation tick — you provide the `animation` and a `builder` function that returns the widget. It's flexible but requires manual widget construction. `XxxTransition` widgets (e.g., `FadeTransition`, `SlideTransition`, `RotationTransition`, `ScaleTransition`) are pre-built widgets that take an `Animation` and handle the rendering for you. They're simpler for common transitions. Use `AnimatedBuilder` for custom animations (size, color, gradient). Use `XxxTransition` for standard fade/slide/rotate/scale. Both rebuild only the animated subtree, not the entire widget tree.

6. **What is a Hero animation?**
   - `Hero` creates a shared element transition between two screens. Wrap a widget with `Hero(tag: 'unique-tag', child: ...)` on both screens using the same `tag`. When navigating, Flutter animates the widget flying from the first screen's position to the second screen's position, scaling and transforming smoothly. Use for image transitions (list → detail), icon morphing, or any element that appears on both screens. The `tag` must be unique per route. `flightShuttleBuilder` can customize the in-flight widget. Keep the child widget the same on both screens for best results.

7. **How do you choose between implicit and explicit animations?**
   - Use **implicit animations** (`AnimatedContainer`, `AnimatedOpacity`, etc.) when: the animation is triggered by a state change, it's a simple property transition, and you don't need fine-grained control. Less code, no controller management. Use **explicit animations** (`AnimationController` + `AnimatedBuilder`) when: you need to control playback (pause, reverse, repeat), the animation involves multiple properties with different timing, you need custom curves or sequences, or it runs continuously. Rule: start with implicit, switch to explicit when you need more control.

8. **What are Curves and how do they work?**
   - `Curve` defines the rate of change during an animation — the easing function. `Curves.linear` (constant speed), `Curves.easeIn` (slow start), `Curves.easeOut` (slow end), `Curves.easeInOut` (slow at both ends), `Curves.bounceOut` (bounces), `Curves.elasticOut` (spring). Apply with `CurvedAnimation(parent: controller, curve: Curves.easeInOut)` or pass `curve:` to implicit animation widgets. Curves make animations feel natural — real-world objects don't start/stop instantly. Always use easing curves — linear animation looks robotic. Custom curves can be created with `Cubic(a, b, c, d)`.

9. **How do you create staggered animations?**
   - Staggered animations run multiple animations with different start times, creating a cascade effect. Use `AnimationController` with multiple `Tween`s and `Interval`s: `Tween(begin: 0, end: 1).animate(CurvedAnimation(parent: controller, curve: const Interval(0.0, 0.5, curve: Curves.easeIn)))` — this animation runs during the first half. Another with `Interval(0.5, 1.0)` runs during the second half. Use `AnimatedBuilder` to apply all animations to different parts of the UI. Staggered animations are great for onboarding screens, list item entrances, and multi-element reveals.

10. **How do you optimize animations for performance?**
    - (1) Always `dispose()` AnimationController — leaked tickers drain battery. (2) Use `AnimatedBuilder` with `child` parameter — the `child` is built once and not rebuilt on each tick. (3) Use `RepaintBoundary` around animated content to isolate repaints. (4) Prefer `Transform` over changing layout properties — transforms are GPU-accelerated (no relayout). (5) Avoid animating `Opacity` in lists — use `AnimatedOpacity` or `FadeTransition` which uses the compositor. (6) Use `const` for non-animated children. (7) Profile in `--profile` mode — check the Performance overlay for dropped frames.

---

## 🔗 Related Topics
- [Custom Widgets](CustomWidgets.md)
- [Performance](../advanced/Performance.md)
- [UI Scenarios](../scenario_based/UIScenarios.md)
