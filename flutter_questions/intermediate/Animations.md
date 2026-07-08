# Animations

## Q1: What are implicit animations?

```dart
// Implicit animations — Flutter handles the animation automatically
// Just change a value, Flutter animates the transition

// AnimatedContainer — animates color, size, decoration
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

// Other implicit animation widgets
AnimatedOpacity(
  opacity: _visible ? 1.0 : 0.0,
  duration: const Duration(milliseconds: 300),
  child: const Text('Fade'),
)

AnimatedPositioned(
  duration: const Duration(milliseconds: 300),
  top: _top ? 0 : 100,
  left: 0,
  child: const Text('Slide'),
)

AnimatedSwitcher(
  duration: const Duration(milliseconds: 300),
  child: _showA ? const Text('A', key: ValueKey('a')) : const Text('B', key: ValueKey('b')),
  transitionBuilder: (child, animation) => FadeTransition(opacity: animation, child: child),
)

AnimatedDefaultTextStyle(
  duration: const Duration(milliseconds: 200),
  style: _large ? const TextStyle(fontSize: 32) : const TextStyle(fontSize: 16),
  child: const Text('Resize'),
)

TweenAnimationBuilder<double>(
  tween: Tween(begin: 0, end: _target),
  duration: const Duration(seconds: 1),
  builder: (context, value, child) => CircularProgressIndicator(value: value),
)
```

| Implicit Widget | Animates |
|-----------------|----------|
| `AnimatedContainer` | Size, color, decoration, padding |
| `AnimatedOpacity` | Opacity (fade) |
| `AnimatedPositioned` | Position in Stack |
| `AnimatedAlign` | Alignment |
| `AnimatedPadding` | Padding |
| `AnimatedSwitcher` | Widget transitions |
| `TweenAnimationBuilder` | Custom values |

---

## Q2: What are explicit animations?

```dart
// Explicit animations — you control the AnimationController
class SpinningBox extends StatefulWidget {
  const SpinningBox({super.key});
  @override
  State<SpinningBox> createState() => _SpinningBoxState();
}

class _SpinningBoxState extends State<SpinningBox>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat();  // Loop forever
  }

  @override
  void dispose() {
    _controller.dispose();  // Always dispose!
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return RotationTransition(
      turns: _controller,
      child: Container(
        width: 100,
        height: 100,
        color: Colors.blue,
      ),
    );
  }
}
```

### AnimationController + Tween + CurvedAnimation
```dart
class PulseAnimation extends StatefulWidget {
  const PulseAnimation({super.key});
  @override
  State<PulseAnimation> createState() => _PulseAnimationState();
}

class _PulseAnimationState extends State<PulseAnimation>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    // Tween — maps 0..1 to 1.0..1.5
    final tween = Tween<double>(begin: 1.0, end: 1.5);

    // Curve — easing
    final curve = CurvedAnimation(
      parent: _controller,
      curve: Curves.easeInOut,
    );

    _animation = tween.animate(curve);

    _controller.repeat(reverse: true);  // Ping-pong
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScaleTransition(
      scale: _animation,
      child: Container(
        width: 80,
        height: 80,
        color: Colors.red,
      ),
    );
  }
}
```

---

## Q3: What is the Hero animation?

```dart
// Hero — shared element transition between screens
// Screen 1
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});
  @override
  Widget build(BuildContext context) {
    return GridView.builder(
      itemCount: 20,
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(crossAxisCount: 3),
      itemBuilder: (context, index) {
        return Hero(
          tag: 'image-$index',  // Unique tag
          child: Image.network('https://picsum.photos/200?image=$index'),
        );
      },
    );
  }
}

// Screen 2 — same tag, Flutter animates the transition
class DetailScreen extends StatelessWidget {
  final int index;
  const DetailScreen({super.key, required this.index});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Hero(
          tag: 'image-$index',  // Must match!
          child: Image.network('https://picsum.photos/600?image=$index'),
        ),
      ),
    );
  }
}
```

### Hero with custom flightShuttleBuilder
```dart
Hero(
  tag: 'avatar',
  flightShuttleBuilder: (flightContext, animation, direction, fromContext, toContext) {
    return ScaleTransition(
      scale: animation,
      child: const Icon(Icons.person, size: 50),
    );
  },
  child: const CircleAvatar(radius: 20),
)
```

---

## Q4: What are common animation curves?

```dart
// Curves — easing functions for natural motion
Curves.linear           // Constant speed
Curves.easeIn           // Slow start, fast end
Curves.easeOut          // Fast start, slow end
Curves.easeInOut        // Slow start and end (most common)
Curves.bounceIn         // Bounce at start
Curves.bounceOut        // Bounce at end
Curves.elasticIn        // Spring at start
Curves.elasticOut       // Spring at end
Curves.fastOutSlowIn    // Material standard
Curves.decelerate       // Fast start, very slow end
```

```dart
// Custom curve
final customCurve = CurveTransformer(
  (t) => t * t,  // Quadratic ease-in
);

// Interval — run animation in a time window
Animation<double> delayed = Tween(begin: 0.0, end: 1.0).animate(
  CurvedAnimation(
    parent: _controller,
    curve: const Interval(0.5, 1.0, curve: Curves.easeIn),  // Starts at 50%
  ),
);
```

---

## Q5: How do you create staggered animations?

```dart
// Staggered — multiple animations with different timing
class StaggeredAnimation extends StatefulWidget {
  const StaggeredAnimation({super.key});
  @override
  State<StaggeredAnimation> createState() => _StaggeredAnimationState();
}

class _StaggeredAnimationState extends State<StaggeredAnimation>
    with TickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..forward();
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
        return Column(
          children: [
            // Fade in at 0.0 - 0.3
            Opacity(
              opacity: Tween(begin: 0.0, end: 1.0).animate(
                CurvedAnimation(parent: _controller, curve: const Interval(0.0, 0.3)),
              ).value,
              child: const Text('First'),
            ),
            // Slide in at 0.2 - 0.5
            Transform.translate(
              offset: Offset(
                Tween(begin: -100.0, end: 0.0).animate(
                  CurvedAnimation(parent: _controller, curve: const Interval(0.2, 0.5)),
                ).value,
                0,
              ),
              child: const Text('Second'),
            ),
            // Scale at 0.4 - 0.7
            Transform.scale(
              scale: Tween(begin: 0.0, end: 1.0).animate(
                CurvedAnimation(parent: _controller, curve: const Interval(0.4, 0.7)),
              ).value,
              child: const Text('Third'),
            ),
          ],
        );
      },
    );
  }
}
```

---

## Q6: What is `AnimatedBuilder` vs `AnimatedWidget`?

```dart
// AnimatedBuilder — rebuilds only the child, not the whole widget
AnimatedBuilder(
  animation: _controller,
  builder: (context, child) {
    return Transform.rotate(
      angle: _controller.value * 2 * pi,
      child: child,  // Reused — not rebuilt
    );
  },
  child: const FlutterLogo(size: 100),  // Built once
)

// AnimatedWidget — create a reusable animated widget
class SpinningLogo extends AnimatedWidget {
  const SpinningLogo({super.key, required Animation<double> animation})
      : super(listenable: animation);

  @override
  Widget build(BuildContext context) {
    final animation = listenable as Animation<double>;
    return Transform.rotate(
      angle: animation.value * 2 * pi,
      child: const FlutterLogo(size: 100),
    );
  }
}

// Usage
SpinningLogo(animation: _controller)
```

| AnimatedBuilder | AnimatedWidget |
|-----------------|----------------|
| Inline, one-off | Reusable widget |
| `child` param optimized | Rebuilds entire widget |
| More flexible | Cleaner for repeated use |

---

## Q7: What packages help with animations?

```dart
// 1. Lottie — After Effects animations (JSON)
// pubspec.yaml: lottie: ^2.7.0
Lottie.asset('assets/animations/loading.json')

Lottie.network('https://example.com/animation.json')

// With controller
late AnimationController _controller;
Lottie.asset(
  'assets/animation.json',
  controller: _controller,
  onLoaded: (composition) {
    _controller.duration = composition.duration;
    _controller.forward();
  },
)

// 2. Rive — interactive animations
// pubspec.yaml: rive: ^0.12.0
RiveAnimation.asset('assets/animations/button.riv')

// 3. flutter_animate — declarative chain animations
// pubspec.yaml: flutter_animate: ^4.5.0
const Text('Hello')
    .animate()
    .fadeIn(duration: 500.ms)
    .slideY(begin: 0.5, end: 0)
    .then(delay: 200.ms)
    .shimmer(duration: 1000.ms);

// 4. page_transition — page transitions
// pubspec.yaml: page_transition: ^2.1.0
Navigator.push(
  context,
  PageTransition(type: PageTransitionType.fade, child: const DetailScreen()),
);
```

---

## 🔗 Related Topics
- [Widgets](../beginner/Widgets.md)
- [Custom Widgets](CustomWidgets.md)
- [UI Scenarios](../scenario_based/UIScenarios.md)
