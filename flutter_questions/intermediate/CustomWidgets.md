# Custom Widgets

## 📖 Explanation

Custom widgets are reusable UI components you build by composing existing widgets or by painting directly to a canvas. They promote code reuse, consistency, and maintainability.

### Three Ways to Create Custom Widgets
| Method | Use Case | Complexity |
|--------|----------|------------|
| Compose existing widgets | Most cases — combine widgets | Low |
| CustomPainter (Canvas) | Custom graphics, charts, shapes | Medium |
| RenderObject (custom) | Full control — layout + paint | High |

### Widget Composition Patterns
- **Extract widget** — Move repeated UI to a separate widget
- **Builder pattern** — Pass a builder function for flexible content
- **Slot pattern** — Define named slots (leading, trailing, child)
- **Generic widget** — `class Card<T> extends ...` for typed data

### CustomPainter
- `paint(Canvas canvas, Size size)` — draw using `Paint`, `Path`, `Rect`, `Offset`
- `shouldRepaint(CustomPainter oldDelegate)` — return true to repaint
- Use `canvas.drawLine`, `canvas.drawRect`, `canvas.drawCircle`, `canvas.drawPath`
- `Paint` — color, strokeWidth, style (fill/stroke), shader

### Slivers
Slivers are building blocks for `CustomScrollView`. They enable custom scroll effects like sticky headers, parallax, and collapsible app bars.
| Sliver | Purpose |
|--------|---------|
| `SliverAppBar` | Collapsible app bar |
| `SliverList` | Linear list |
| `SliverGrid` | Grid layout |
| `SliverToBoxAdapter` | Wrap a regular widget |
| `SliverPersistentHeader` | Sticky headers |

### Custom Widget Best Practices
- Always add `const` constructor when possible
- Use `super.key` for key parameter
- Make widgets as dumb as possible — pass data in, emit events out
- Use `const` for child widgets that don't change
- Add `Semantics` for accessibility
- Keep widgets small and focused — single responsibility

---

## 🧪 Code Example

```dart
// ── Composing existing widgets ──
class ProfileCard extends StatelessWidget {
  final String name;
  final String subtitle;
  final ImageProvider? avatar;
  final VoidCallback? onTap;

  const ProfileCard({
    super.key,
    required this.name,
    required this.subtitle,
    this.avatar,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              CircleAvatar(
                backgroundImage: avatar,
                child: avatar == null ? Text(name[0]) : null,
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(name, style: Theme.of(context).textTheme.titleMedium),
                    Text(subtitle, style: Theme.of(context).textTheme.bodySmall),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right),
            ],
          ),
        ),
      ),
    );
  }
}

// ── CustomPainter — draw a progress ring ──
class ProgressRing extends StatelessWidget {
  final double progress;  // 0.0 to 1.0
  final double size;
  final double strokeWidth;
  final Color color;

  const ProgressRing({
    super.key,
    required this.progress,
    this.size = 100,
    this.strokeWidth = 8,
    this.color = Colors.blue,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: size,
      height: size,
      child: CustomPaint(
        painter: _ProgressRingPainter(
          progress: progress,
          strokeWidth: strokeWidth,
          color: color,
        ),
      ),
    );
  }
}

class _ProgressRingPainter extends CustomPainter {
  final double progress;
  final double strokeWidth;
  final Color color;

  _ProgressRingPainter({
    required this.progress,
    required this.strokeWidth,
    required this.color,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = (size.width - strokeWidth) / 2;

    // Background circle
    final bgPaint = Paint()
      ..color = color.withOpacity(0.2)
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth;
    canvas.drawCircle(center, radius, bgPaint);

    // Progress arc
    final progressPaint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeWidth = strokeWidth
      ..strokeCap = StrokeCap.round;
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -pi / 2,  // Start from top
      2 * pi * progress,  // Arc length
      false,
      progressPaint,
    );
  }

  @override
  bool shouldRepaint(_ProgressRingPainter oldDelegate) {
    return oldDelegate.progress != progress ||
        oldDelegate.color != color;
  }
}

// ── Slivers — collapsible header ──
class CollapsibleHeaderScreen extends StatelessWidget {
  const CollapsibleHeaderScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return CustomScrollView(
      slivers: [
        SliverAppBar(
          expandedHeight: 200,
          pinned: true,
          flexibleSpace: const FlexibleSpaceBar(
            title: Text('Profile'),
            background: ColoredBox(color: Colors.blue),
          ),
        ),
        SliverList(
          delegate: SliverChildBuilderDelegate(
            (context, index) => ListTile(title: Text('Item $index')),
            childCount: 50,
          ),
        ),
      ],
    );
  }
}

// ── Generic custom widget ──
class DataList<T> extends StatelessWidget {
  final List<T> items;
  final Widget Function(T item) itemBuilder;
  final void Function(T item)? onTap;

  const DataList({
    super.key,
    required this.items,
    required this.itemBuilder,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: items.length,
      itemBuilder: (context, index) {
        final item = items[index];
        return InkWell(
          onTap: () => onTap?.call(item),
          child: itemBuilder(item),
        );
      },
    );
  }
}
```

### Output
```
A Flutter app with custom widgets:
- ProfileCard composed from Card + InkWell + Row
- ProgressRing drawn with CustomPainter (canvas.drawArc)
- CollapsibleHeaderScreen using SliverAppBar + SliverList
- Generic DataList<T> for type-safe list rendering
```

---

## ❓ Interview Questions

1. **How do you create a custom widget in Flutter?**
   - Create a class extending `StatelessWidget` or `StatefulWidget`. Add a `const` constructor with `super.key` and parameters for data. Override `build()` to return composed widgets. Example: `class ProfileCard extends StatelessWidget { final String name; const ProfileCard({super.key, required this.name}); @override Widget build(BuildContext context) { return Card(child: Text(name)); } }`. Best practices: use `const` constructor, pass data as parameters, emit events via callbacks (`VoidCallback`, `ValueChanged<T>`), keep widgets small and focused, use `const` for static children.

2. **What is CustomPainter and when do you use it?**
   - `CustomPainter` draws custom graphics directly to a canvas — used for charts, shapes, progress rings, signatures, game graphics. Implement `paint(Canvas, Size)` to draw using `canvas.drawCircle`, `canvas.drawRect`, `canvas.drawPath`, `canvas.drawArc` with `Paint` objects. Implement `shouldRepaint(oldDelegate)` — return `true` when the painting should change (compare properties). Use with `CustomPaint(painter: MyPainter(), size: Size(100, 100))`. For animations, use `AnimatedBuilder` with `RepaintBoundary` and pass the animation to the painter. CustomPainter is GPU-accelerated and efficient for complex graphics.

3. **What is the difference between composing widgets and CustomPainter?**
   - **Composing widgets** — combine existing widgets (Container, Row, Stack). Faster to build, automatically handle layout, hit-testing, accessibility, theming. Best for 90% of UIs. **CustomPainter** — draw directly to canvas. Full control over pixels, can draw anything (shapes, curves, gradients, paths). Required for custom graphics that can't be built from existing widgets (charts, signatures, progress rings, games). Downside: you handle hit-testing manually, no automatic layout, more complex. Rule: always try composing first, use CustomPainter only when you need pixel-level control.

4. **How do you use Slivers in Flutter?**
   - Slivers are building blocks for `CustomScrollView`. Wrap content in `CustomScrollView(slivers: [...])`. Common slivers: `SliverAppBar` (collapsible app bar with `expandedHeight` and `pinned`), `SliverList` (linear list with `SliverChildBuilderDelegate`), `SliverGrid` (grid with `SliverGridDelegate`), `SliverToBoxAdapter` (wrap a single regular widget), `SliverPersistentHeader` (sticky headers with `PinnedHeader`). Slivers enable scroll effects: parallax, sticky headers, collapsible app bars, custom scroll animations. They share a common `ScrollController` and scroll physics. Use slivers when `ListView`/`GridView` aren't enough — when you need mixed scroll content or scroll effects.

5. **How do you make custom widgets reusable?**
   - (1) Use parameters for all dynamic data — never hardcode text, colors, sizes. (2) Use callbacks for events — `VoidCallback`, `ValueChanged<T>`. (3) Use generics for typed data — `class DataList<T>`. (4) Use `const` constructor for performance. (5) Provide sensible defaults — `this.color = Colors.blue`. (6) Use `Theme.of(context)` for colors/styles — respects app theme. (7) Support `key` with `super.key`. (8) Document with `///` doc comments. (9) Keep widgets focused — single responsibility. (10) Extract sub-widgets for readability. A reusable widget should work in any app without modification.

6. **How do you handle hit-testing in CustomPainter?**
   - Override `hitTest(Offset position)` in `CustomPainter` to return `true` if the position is within the painted shape. Check if the point is inside the shape: `bool hitTest(Offset position) { return (position - center).distance <= radius; }` for circles. For complex shapes, use `Path.contains(Offset)`. Wrap `CustomPaint` in `GestureDetector` for tap/pan events. Alternatively, use `CustomPaint(foregroundPainter: ...)` for overlay graphics. For interactive custom widgets, consider `RenderBox` with `handleEvent` for more control. Most cases don't need custom hit-testing — wrap in `GestureDetector` and use the full widget bounds.

7. **How do you animate a CustomPainter?**
   - Pass an `Animation<double>` to the painter and use `AnimatedBuilder(animation: animation, builder: (context, child) { return CustomPaint(painter: MyPainter(progress: animation.value)); })`. In `shouldRepaint`, return `true` (or compare the progress value). Wrap in `RepaintBoundary` to isolate repaints. The painter reads `animation.value` (0.0–1.0) to determine what to draw — e.g., arc length for progress ring, opacity for fade. For smooth animation, use `CurvedAnimation`. The key insight: the painter itself doesn't animate — the `AnimatedBuilder` rebuilds `CustomPaint` with new values on each tick.

8. **What is the difference between SliverList and SliverChildListDelegate vs SliverChildBuilderDelegate?**
   - `SliverChildListDelegate` — takes a pre-built `List<Widget>`. All children are created upfront. Use for small, known lists (e.g., 5 settings items). `SliverChildBuilderDelegate` — takes a builder function `(context, index) => Widget` and `childCount`. Children are built lazily — only visible items are created. Use for large or dynamic lists (e.g., 1000 items from API). This is the sliver equivalent of `ListView(children: [...])` vs `ListView.builder(itemBuilder: ...)`. Always prefer `SliverChildBuilderDelegate` for large lists to avoid building all items at once.

9. **How do you create a custom Sliver?**
   - Use `SliverPersistentHeader(delegate: MyDelegate(), pinned: true)` with a custom `SliverPersistentHeaderDelegate`. Implement `build(context, double shrinkOffset, bool overlapsContent)` — return a widget that adapts to `shrinkOffset` (0 = expanded, max = collapsed). Implement `minExtent` and `maxExtent` getters. For fully custom slivers, extend `RenderSliver` and implement `performLayout()` — this gives complete control over layout and painting but is complex. Most cases use `SliverPersistentHeader` for sticky headers or `SliverToBoxAdapter` to wrap regular widgets. Use `SliverAnimatedList` for animated list insertions/removals.

10. **What are RenderObjects and when do you need them?**
    - `RenderObject` is the lowest-level rendering primitive — handles layout, painting, and hit-testing. Flutter's built-in widgets (Container, Row, Column) create RenderObjects internally. You need custom RenderObjects when: (1) No existing widget does what you need. (2) You need custom layout algorithms (e.g., waterfall flow, custom flow). (3) You need maximum performance (skip widget/element layer). Create by extending `RenderBox` or `RenderSliver`, implement `performLayout()` (measure and position children) and `paint()` (draw). Wrap with `RenderObjectWidget` → `RenderObjectElement`. This is advanced — 99% of apps don't need custom RenderObjects. Use `LayoutBuilder`, `CustomPaint`, or `CustomScrollView` first.

---

## 🔗 Related Topics
- [Animations](Animations.md)
- [Layouts](../beginner/Layouts.md)
- [UI Scenarios](../scenario_based/UIScenarios.md)
