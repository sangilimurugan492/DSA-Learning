# Layouts

## 📖 Explanation

Flutter's layout system is based on widgets composing other widgets. The core principle is: **"Constraints go down. Sizes go up."** The parent passes constraints (minWidth, maxWidth, minHeight, maxHeight) to the child, the child sizes itself within those constraints, and returns its size to the parent.

### Row and Column
`Row` lays out children horizontally (main axis = horizontal). `Column` lays out children vertically (main axis = vertical). Both use `MainAxisAlignment` (main axis) and `CrossAxisAlignment` (cross axis).

### MainAxisAlignment (main axis)
| Value | Behavior |
|-------|----------|
| `start` | Children at start (default) |
| `center` | Children centered |
| `end` | Children at end |
| `spaceBetween` | Equal space between children |
| `spaceAround` | Space around each child |
| `spaceEvenly` | Equal space between and around |

### CrossAxisAlignment (cross axis)
| Value | Behavior |
|-------|----------|
| `start` | Align to cross-axis start |
| `center` | Align to center (default) |
| `end` | Align to cross-axis end |
| `stretch` | Fill cross-axis |
| `baseline` | Align by text baseline (Row only) |

### Flex, Expanded, and Flexible
| Widget | Fit | Behavior |
|--------|-----|----------|
| `Expanded` | tight | Fills all available space |
| `Flexible(fit: tight)` | tight | Same as Expanded |
| `Flexible(fit: loose)` | loose | Natural size, max = available |
| `Spacer()` | tight | Empty expanded (gap) |

### Stack
`Stack` overlaps children — last child is on top. `Positioned` children use absolute positioning. Non-positioned children use `Stack`'s `alignment`. `IndexedStack` shows only one child at a time (state preserved).

### Constraint System
```
Parent passes constraints (minWidth, maxWidth, minHeight, maxHeight)
  ↓
Child sizes itself within constraints
  ↓
Child returns its size to parent
  ↓
Parent positions child
```

Common constraint types:
- **Tight** — exact size (e.g., `SizedBox`)
- **Loose** — range (e.g., `ConstrainedBox`)
- **Unbounded** — infinite (in scroll views)

### Common Layout Widgets
- **Padding**: `Padding`, `EdgeInsets.all/symmetric/only/fromLTRB`
- **Alignment**: `Center`, `Align`
- **Sizing**: `SizedBox`, `AspectRatio`, `FractionallySizedBox`, `FittedBox`
- **Wrap**: `Wrap` (wraps children to next line)
- **SafeArea**: Avoids notches and system UI
- **LayoutBuilder**: Get parent constraints at build time

### ListView vs SingleChildScrollView
| Widget | Builds | Use Case |
|--------|--------|----------|
| `ListView.builder` | Only visible | Large/dynamic lists |
| `ListView` | All children | Small lists (<20 items) |
| `SingleChildScrollView` | All children | Forms, mixed content |
| `CustomScrollView` | Only visible slivers | Complex scroll layouts |

### Clipping Widgets
| Widget | Shape |
|--------|-------|
| `ClipRRect` | Rounded rectangle |
| `ClipOval` | Oval/circle |
| `ClipPath` | Custom path |

> **Performance:** Clipping is expensive (uses `saveLayer`). For rounded corners, prefer `Container` with `BoxDecoration(borderRadius:)` instead of `ClipRRect`.

### Sizing Widgets
| Widget | Purpose | Scales Content? |
|--------|---------|-----------------|
| `FittedBox` | Fit child into bounds | ✅ Yes |
| `FractionallySizedBox` | Size as % of parent | ❌ No |
| `AspectRatio` | Fixed ratio | ❌ No |
| `SizedBox` | Fixed size | ❌ No |

### Sliver Helpers
- `SliverToBoxAdapter` — wrap a non-sliver widget in a sliver
- `SliverFillRemaining` — fill remaining space in `CustomScrollView`
- `SliverFillViewport` — full-screen pages

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
      home: Scaffold(
        appBar: AppBar(title: const Text('Layouts Demo')),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Row with Expanded
            Row(
              children: [
                const Text('Label'),
                Expanded(
                  flex: 1,
                  child: Container(color: Colors.red, height: 50),
                ),
                Expanded(
                  flex: 2,
                  child: Container(color: Colors.blue, height: 50),
                ),
              ],
            ),

            const SizedBox(height: 16),

            // Stack with Positioned
            Stack(
              alignment: Alignment.center,
              children: [
                Container(width: 200, height: 100, color: Colors.green),
                const Positioned(
                  top: 10,
                  right: 10,
                  child: Icon(Icons.star, color: Colors.white),
                ),
                const Text('Overlay',
                    style: TextStyle(color: Colors.white)),
              ],
            ),

            const SizedBox(height: 16),

            // Wrap
            Wrap(
              spacing: 8,
              runSpacing: 4,
              children: [
                Chip(label: Text('Tag 1')),
                Chip(label: Text('Tag 2')),
                Chip(label: Text('Tag 3')),
              ],
            ),

            const SizedBox(height: 16),

            // Responsive with LayoutBuilder
            LayoutBuilder(
              builder: (context, constraints) {
                final isWide = constraints.maxWidth > 600;
                return Text(isWide ? 'Tablet' : 'Phone');
              },
            ),
          ],
        ),
      ),
    );
  }
}
```

### Output
```
A running Flutter app with:
- Row with red (1/3 width) and blue (2/3 width) containers
- Stack with green background, star icon top-right, "Overlay" text centered
- Wrap with three chips
- LayoutBuilder text showing "Phone" or "Tablet" based on width
```

---

## ❓ Interview Questions

1. **How do Row and Column work?**
   - `Row` lays out children horizontally (main axis = horizontal, cross axis = vertical). `Column` lays out children vertically (main axis = vertical, cross axis = horizontal). `MainAxisAlignment` controls alignment on the main axis (start, center, end, spaceBetween, spaceAround, spaceEvenly). `CrossAxisAlignment` controls alignment on the cross axis (start, center, end, stretch, baseline). Use `Expanded` or `Flexible` to distribute space among children.

2. **What is Flex, Expanded, and Flexible?**
   - `Expanded` fills all available space on the main axis with `fit: tight` — it takes a `flex` factor to distribute space proportionally. `Flexible` can be `tight` (same as Expanded) or `loose` (natural size, up to available space). `Spacer()` is shorthand for `Expanded(child: SizedBox.shrink())` — creates a gap. Use `Expanded` when a child should fill remaining space; use `Flexible(fit: loose)` when a child should be at most the available size but can be smaller.

3. **What is Stack and how do you position children?**
   - `Stack` overlaps children — the last child is on top. `Positioned` children use absolute positioning (top, right, bottom, left). Non-positioned children use `Stack`'s `alignment` property. `IndexedStack` shows only one child at a time while keeping all children in the tree (state preserved). Use `Stack` for overlays, badges, and layered UI. Avoid deeply nested `Stack`s — they're hard to debug.

4. **How does the constraint system work in Flutter?**
   - Flutter layout follows: "Constraints go down. Sizes go up." The parent passes `BoxConstraints` (minWidth, maxWidth, minHeight, maxHeight) to the child. The child sizes itself within those constraints and returns its size. The parent then positions the child. Tight constraints = exact size (SizedBox). Loose constraints = range (ConstrainedBox). Unbounded = infinite (in scroll views). Common errors: "RenderFlex overflowed" (children too wide for Row) — fix with Expanded or SingleChildScrollView. "Vertical viewport was given unbounded height" — fix with shrinkWrap or bounded parent.

5. **What are common layout widgets?**
   - `Padding` (with `EdgeInsets.all/symmetric/only/fromLTRB`), `Center`, `Align`, `SizedBox` (fixed size/gap), `AspectRatio` (fixed ratio), `FractionallySizedBox` (percentage of parent), `FittedBox` (scale to fit), `Wrap` (wraps children to next line), `SafeArea` (avoid notches), `LayoutBuilder` (get parent constraints). Use `SizedBox` for gaps instead of `Container` — it's lighter.

6. **How do you create responsive layouts?**
   - Three approaches: (1) `MediaQuery.of(context).size` — screen size, use for app-level decisions. (2) `LayoutBuilder` — parent constraints, use for widget-level decisions (more efficient, only rebuilds when constraints change). (3) `OrientationBuilder` — portrait/landscape. Define breakpoints: mobile <480, tablet <768, desktop <1024. For complex responsive apps, use `flutter_screenutil` or a `ResponsiveWidget` pattern with mobile/tablet/desktop variants.

7. **What is the difference between ListView and SingleChildScrollView?**
   - `ListView.builder` — lazy loading, only builds visible items. Best for large/dynamic lists. `ListView()` (without builder) — renders all children at once. Fine for small lists (<20), bad for large ones. `SingleChildScrollView` — renders ALL children at once. Use for forms and mixed content, not for large lists. `CustomScrollView` with slivers — only builds visible slivers, best for complex scroll layouts. Always use `.builder` for lists with 20+ items.

8. **What is `ClipRRect`, `ClipOval`, and `ClipPath`?**
   - `ClipRRect` clips to a rounded rectangle (uses `borderRadius`). `ClipOval` clips to an oval/circle. `ClipPath` clips to a custom path via a `CustomClipper`. Clipping is expensive — it uses `saveLayer` internally. For simple rounded corners, prefer `Container` with `BoxDecoration(borderRadius:)` instead of `ClipRRect` — it's cheaper. Use `ClipOval` for circular avatars. Use `ClipPath` for custom shapes (triangles, hexagons).

9. **What is `FittedBox` and how does it differ from `FractionallySizedBox`?**
   - `FittedBox` scales its child to fit within bounds — it actually scales content (text, images). `BoxFit` options: fill (stretch), contain (fit entirely), cover (fill + crop), scaleDown (contain but never up). `FractionallySizedBox` sizes the child as a percentage of the parent — it doesn't scale content, just allocates space. Use `FittedBox` when text/content might overflow and should shrink-to-fit. Use `FractionallySizedBox` for responsive percentage-based layouts. `AspectRatio` maintains a fixed width:height ratio.

10. **What is `SliverFillRemaining` and `SliverToBoxAdapter`?**
    - `SliverToBoxAdapter` wraps a non-sliver widget (like `Text`, `Container`, `Image`) so it can be used inside a `CustomScrollView` alongside other slivers. It's the bridge between regular widgets and slivers. `SliverFillRemaining` fills the remaining space in a `CustomScrollView` — useful for loading spinners at the bottom or "no more items" messages. `SliverFillViewport` creates full-screen pages. Use these when building complex scrollable layouts with `CustomScrollView`.

---

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [Basics](Basics.md)
- [State Management](StateManagement.md)
