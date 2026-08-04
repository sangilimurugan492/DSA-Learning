# Layouts

## Q1: How do Row and Column work?

```dart
// Row — horizontal layout (main axis = horizontal)
Row(
  mainAxisAlignment: MainAxisAlignment.spaceEvenly,  // Main axis
  crossAxisAlignment: CrossAxisAlignment.center,      // Cross axis
  children: [
    const Icon(Icons.star),
    const Icon(Icons.star),
    const Icon(Icons.star),
  ],
)

// Column — vertical layout (main axis = vertical)
Column(
  mainAxisAlignment: MainAxisAlignment.center,
  crossAxisAlignment: CrossAxisAlignment.stretch,  // Stretch across cross axis
  children: [
    const Text('Title', style: TextStyle(fontSize: 24)),
    const Text('Subtitle'),
    ElevatedButton(onPressed: () {}, child: const Text('Action')),
  ],
)
```

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

---

## Q2: What is Flex, Expanded, and Flexible?

```dart
// Expanded — fills available space (fit: tight)
Row(
  children: [
    const Text('Label'),
    Expanded(
      flex: 1,  // Takes 1 part of available space
      child: Container(color: Colors.red, height: 50),
    ),
    Expanded(
      flex: 2,  // Takes 2 parts — twice as wide
      child: Container(color: Colors.blue, height: 50),
    ),
  ],
)

// Flexible — can be tight or loose
Row(
  children: [
    Flexible(
      flex: 1,
      fit: FlexFit.tight,  // Tight = fill space (like Expanded)
      child: Container(color: Colors.red),
    ),
    Flexible(
      flex: 1,
      fit: FlexFit.loose,  // Loose = natural size, up to available
      child: const Text('Flexible'),
    ),
  ],
)

// Spacer — shorthand for Expanded with empty child
Row(
  children: [
    const Text('Left'),
    const Spacer(),  // Expanded(child: SizedBox.shrink())
    const Text('Right'),
  ],
)
```

| Widget | Fit | Behavior |
|--------|-----|----------|
| `Expanded` | tight | Fills all available space |
| `Flexible(fit: tight)` | tight | Same as Expanded |
| `Flexible(fit: loose)` | loose | Natural size, max = available |
| `Spacer()` | tight | Empty expanded (gap) |

---

## Q3: What is Stack and how do you position children?

```dart
// Stack — children overlap (last child on top)
Stack(
  alignment: Alignment.center,  // Default alignment for non-positioned
  children: [
    // Bottom layer
    Container(
      width: 200,
      height: 200,
      color: Colors.blue,
    ),

    // Positioned — absolute position within stack
    Positioned(
      top: 10,
      right: 10,
      child: Container(
        width: 40,
        height: 40,
        color: Colors.red,
      ),
    ),

    // Centered (non-positioned — uses Stack alignment)
    const Text('Overlay', style: TextStyle(color: Colors.white)),

    // Bottom bar
    Positioned(
      left: 0,
      right: 0,
      bottom: 0,
      child: Container(
        height: 50,
        color: Colors.black54,
        child: const Center(child: Text('Bottom Bar')),
      ),
    ),
  ],
)
```

### IndexedStack — show only one child at a time
```dart
IndexedStack(
  index: _currentIndex,  // 0, 1, 2...
  children: [
    Page1(),
    Page2(),
    Page3(),
  ],
)
// Only Page at index is visible — others are kept in tree (state preserved)
```

---

## Q4: How does the constraint system work in Flutter?

```
Flutter Layout = "Constraints go down. Sizes go up."

Parent passes constraints (minWidth, maxWidth, minHeight, maxHeight)
  ↓
Child sizes itself within constraints
  ↓
Child returns its size to parent
  ↓
Parent positions child
```

```dart
// Common constraint scenarios:

// 1. Tight constraints — exact size
SizedBox(
  width: 100,
  height: 50,
  child: Container(color: Colors.red),
)

// 2. Loose constraints — range
ConstrainedBox(
  constraints: const BoxConstraints(
    minWidth: 50,
    maxWidth: 200,
    minHeight: 30,
    maxHeight: 100,
  ),
  child: Container(color: Colors.blue),
)

// 3. Unbounded constraints — infinite (in scroll views)
ListView(
  children: [
    // Each item gets unbounded height → must size itself
    Container(height: 100, color: Colors.red),
  ],
)

// 4. Expand to parent
Container(
  color: Colors.green,
  child: const Expanded(child: SizedBox.shrink()),
)
```

### Common Layout Errors
```
// ❌ "RenderFlex overflowed by X pixels"
Row(
  children: [
    Container(width: 200, color: Colors.red),  // Too wide
    Container(width: 200, color: Colors.blue), // Can't fit
  ],
)
// Fix: Wrap in Expanded or Flexible, or use SingleChildScrollView

// ❌ "RenderBox was not laid out"
// Missing material ancestor, or constraints issue

// ❌ "Vertical viewport was given unbounded height"
ListView(
  shrinkWrap: true,  // Fix: shrinkWrap or give bounded parent
  physics: const NeverScrollableScrollPhysics(),
  children: [...],
)
```

---

## Q5: What are common layout widgets?

```dart
// Padding
Padding(
  padding: const EdgeInsets.all(16),
  child: const Text('Padded'),
)

// EdgeInsets variations
const EdgeInsets.all(8)                      // All sides
const EdgeInsets.symmetric(horizontal: 8, vertical: 4)  // Symmetric
const EdgeInsets.only(left: 8, top: 4)        // Specific sides
const EdgeInsets.fromLTRB(8, 4, 8, 4)        // L, T, R, B

// Center
const Center(child: Text('Centered'))

// Align
const Align(
  alignment: Alignment.topLeft,
  child: Text('Top-left'),
)

// SizedBox — fixed size or gap
const SizedBox(height: 16)  // Gap
const SizedBox(width: 100, height: 50, child: Text('Fixed'))

// AspectRatio
AspectRatio(
  aspectRatio: 16 / 9,
  child: Container(color: Colors.red),
)

// FractionallySizedBox — percentage of parent
FractionallySizedBox(
  widthFactor: 0.8,  // 80% of parent width
  child: Container(color: Colors.blue),
)

// FittedBox — scale child to fit
const FittedBox(
  fit: BoxFit.scaleDown,
  child: Text('Long text that needs to fit'),
)

// Wrap — wraps children to next line
Wrap(
  spacing: 8,       // Horizontal gap
  runSpacing: 4,     // Vertical gap between lines
  children: [
    Chip(label: Text('Tag 1')),
    Chip(label: Text('Tag 2')),
    Chip(label: Text('Tag 3')),
  ],
)

// SafeArea — avoid notches and system UI
SafeArea(
  child: const Text('Safe from notches'),
)

// LayoutBuilder — get parent constraints
LayoutBuilder(
  builder: (context, constraints) {
    if (constraints.maxWidth > 600) {
      return WideLayout();
    }
    return NarrowLayout();
  },
)
```

---

## Q6: How do you create responsive layouts?

```dart
// 1. MediaQuery — screen size
Widget build(BuildContext context) {
  final size = MediaQuery.of(context).size;
  final width = size.width;
  final isTablet = width > 600;
  final isLandscape = width > size.height;

  return GridView.count(
    crossAxisCount: isTablet ? 3 : 2,
    children: items,
  );
}

// 2. LayoutBuilder — parent constraints
LayoutBuilder(
  builder: (context, constraints) {
    final columns = constraints.maxWidth > 600 ? 3 : 1;
    return GridView.count(
      crossAxisCount: columns,
      children: items,
    );
  },
)

// 3. OrientationBuilder
OrientationBuilder(
  builder: (context, orientation) {
    return GridView.count(
      crossAxisCount: orientation == Orientation.portrait ? 2 : 4,
      children: items,
    );
  },
)

// 4. Breakpoint utility class
class Breakpoints {
  static const double mobile = 480;
  static const double tablet = 768;
  static const double desktop = 1024;
}

ResponsiveWidget(
  mobile: MobileLayout(),
  tablet: TabletLayout(),
  desktop: DesktopLayout(),
)
```

---

## Q7: What is the difference between ListView and SingleChildScrollView?

```dart
// ListView — lazy loading, only builds visible items
ListView.builder(
  itemCount: 10000,
  itemBuilder: (context, index) => ListTile(
    title: Text('Item $index'),
  ),
)
// Only ~10 items built at a time — efficient for large lists

// SingleChildScrollView — renders ALL children at once
SingleChildScrollView(
  child: Column(
    children: [
      for (int i = 0; i < 10000; i++)
        ListTile(title: Text('Item $i')),
    ],
  ),
)
// ❌ Builds all 10000 items — memory and performance issues

// ListView (without builder) — also renders all children
ListView(
  children: [
    ListTile(title: Text('Item 1')),
    ListTile(title: Text('Item 2')),
  ],
)
// Fine for small lists, bad for large ones

// ✅ Use .builder for large/dynamic lists
// ✅ Use SingleChildScrollView for forms, small content
// ✅ Use CustomScrollView + Slivers for complex scroll layouts
```

| Widget | Builds | Use Case |
|--------|--------|----------|
| `ListView.builder` | Only visible | Large/dynamic lists |
| `ListView` | All children | Small lists (<20 items) |
| `SingleChildScrollView` | All children | Forms, mixed content |
| `CustomScrollView` | Only visible slivers | Complex scroll layouts |

---

## Q8: What is `ClipRRect`, `ClipOval`, and `ClipPath`?

```dart
// ClipRRect — clip to rounded rectangle
ClipRRect(
  borderRadius: BorderRadius.circular(16),
  child: Image.network('https://example.com/photo.jpg'),
)

// ClipOval — clip to oval/circle
ClipOval(
  child: SizedBox(
    width: 100,
    height: 100,
    child: Image.network('https://example.com/avatar.jpg', fit: BoxFit.cover),
  ),
)

// ClipPath — clip to custom path
ClipPath(
  clipper: MyCustomClipper(),
  child: Container(
    width: 200,
    height: 100,
    color: Colors.blue,
  ),
)

class MyCustomClipper extends CustomClipper<Path> {
  @override
  Path getClip(Size size) {
    final path = Path();
    path.moveTo(0, 0);
    path.lineTo(size.width, 0);
    path.lineTo(size.width / 2, size.height);
    path.close();
    return path;
  }

  @override
  bool shouldReclip(covariant CustomClipper<Path> oldClipper) => false;
}
```

> **Performance:** Clipping is expensive — it uses `saveLayer` internally. For simple rounded corners, prefer `Container` with `BoxDecoration(borderRadius:)` instead of `ClipRRect` — it's cheaper.

---

## Q9: What is `FittedBox` and how does it differ from `FractionallySizedBox`?

```dart
// FittedBox — scales child to fit within bounds
const FittedBox(
  fit: BoxFit.scaleDown,  // Scale down only (never up)
  child: Text('Long text that might overflow'),
)

// BoxFit options:
// fill       — stretch to fill (distorts)
// contain    — fit entirely (letterbox)
// cover      — fill, crop overflow
// fitWidth   — fit width, crop height
// fitHeight  — fit height, crop width
// scaleDown  — contain, but never scale up
// none       — no scaling (overflow)

// FractionallySizedBox — size as percentage of parent
FractionallySizedBox(
  widthFactor: 0.8,   // 80% of parent width
  heightFactor: 0.5,  // 50% of parent height
  child: Container(color: Colors.blue),
)

// AspectRatio — fixed width:height ratio
AspectRatio(
  aspectRatio: 16 / 9,
  child: Container(color: Colors.red),
)
```

| Widget | Purpose | Scales Content? |
|--------|---------|-----------------|
| `FittedBox` | Fit child into bounds | ✅ Yes |
| `FractionallySizedBox` | Size as % of parent | ❌ No |
| `AspectRatio` | Fixed ratio | ❌ No |
| `SizedBox` | Fixed size | ❌ No |

> **Use `FittedBox`** when text or content might overflow and you want it to shrink-to-fit. Use `FractionallySizedBox` for responsive percentage-based layouts.

---

## Q10: What is `SliverFillRemaining` and `SliverToBoxAdapter`?

```dart
// SliverToBoxAdapter — wrap a non-sliver widget in a sliver
CustomScrollView(
  slivers: [
    SliverToBoxAdapter(child: const Text('Header')),  // Non-sliver → sliver
    SliverList(delegate: SliverChildBuilderDelegate(
      (context, index) => ListTile(title: Text('Item $index')),
      childCount: 20,
    )),
    SliverToBoxAdapter(child: const Text('Footer')),
  ],
)

// SliverFillRemaining — fill remaining space in CustomScrollView
CustomScrollView(
  slivers: [
    SliverAppBar(title: const Text('App Bar')),
    SliverList(delegate: SliverChildBuilderDelegate(
      (context, index) => ListTile(title: Text('Item $index')),
      childCount: 5,
    )),
    // Fills remaining space — useful for loading spinners at bottom
    SliverFillRemaining(
      hasScrollBody: false,  // Don't scroll the child
      child: const Center(child: CircularProgressIndicator()),
    ),
  ],
)

// SliverFillViewport — full-screen pages
SliverFillViewport(
  delegate: SliverChildBuilderDelegate(
    (context, index) => Container(
      color: Colors.primaries[index],
      child: Center(child: Text('Page $index')),
    ),
    childCount: 5,
  ),
)
```

> **Key:** `SliverToBoxAdapter` is the bridge between regular widgets and slivers. Use it when you need to place a single non-sliver widget (like `Text`, `Container`, `Image`) inside a `CustomScrollView`.

---

## 🔗 Related Topics
- [Widgets](Widgets.md)
- [Basics](Basics.md)
- [State Management](StateManagement.md)
