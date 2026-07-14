# UI/UX Scenarios

## Scenario 1: Responsive Layout (Mobile + Tablet)

### Problem
The app needs to show a single-column list on phones and a two-column grid on tablets.

```dart
// ❌ Bad — fixed column count
GridView.count(
  crossAxisCount: 2,  // Always 2 — too cramped on phones
  children: items,
)
```

### Solution: LayoutBuilder + breakpoints

```dart
// ✅ Good — responsive based on available width
class ProductGrid extends StatelessWidget {
  final List<Product> products;
  const ProductGrid({super.key, required this.products});

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        // Determine columns based on width
      int columns;
      if (constraints.maxWidth > 1200) {
        columns = 4;  // Desktop
      } else if (constraints.maxWidth > 800) {
        columns = 3;  // Tablet landscape
      } else if (constraints.maxWidth > 600) {
        columns = 2;  // Tablet portrait
      } else {
        columns = 1;  // Phone
      }

      final itemWidth = constraints.maxWidth / columns;

      return GridView.builder(
        gridDelegate: SliverGridDelegateWithMaxCrossAxisExtent(
          maxCrossAxisExtent: itemWidth,
          childAspectRatio: 0.75,
          crossAxisSpacing: 8,
          mainAxisSpacing: 8,
        ),
        itemCount: products.length,
        itemBuilder: (context, index) => ProductCard(
          product: products[index],
        ),
      );
    },
    );
  }
}

// ✅ Good — master-detail layout on tablet
class AdaptiveScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        if (constraints.maxWidth > 600) {
          // Tablet — side by side
          return Row(
            children: [
              SizedBox(width: 300, child: MasterList()),
              const VerticalDivider(width: 1),
              const Expanded(child: DetailPanel()),
            ],
          );
        }
        // Phone — single panel
        return const MasterList();
      },
    );
  }
}
```

### Key Takeaway
- `LayoutBuilder` gives parent constraints — use for responsive layouts
- Breakpoints: 600 (tablet), 800 (tablet landscape), 1200 (desktop)
- `SliverGridDelegateWithMaxCrossAxisExtent` auto-calculates columns
- Master-detail pattern: side-by-side on tablet, stacked on phone

---

## Scenario 2: Custom Bottom Sheet with Animation

### Problem
Create a bottom sheet that slides up with a drag handle and can be dismissed by swiping down.

```dart
// ✅ Good — showModalBottomSheet with custom shape
void showCustomSheet(BuildContext context) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true,  // Full height support
    backgroundColor: Colors.transparent,
    builder: (context) => const CustomSheet(),
  );
}

class CustomSheet extends StatelessWidget {
  const CustomSheet({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Drag handle
          Container(
            margin: const EdgeInsets.symmetric(vertical: 8),
            width: 40,
            height: 4,
            decoration: BoxDecoration(
              color: Colors.grey[300],
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          // Content
          Padding(
            padding: EdgeInsets.fromLTRB(
              16, 8, 16,
              16 + MediaQuery.of(context).viewInsets.bottom,  // Keyboard
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text('Options', style: TextStyle(fontSize: 20)),
                const SizedBox(height: 16),
                ListTile(
                  leading: const Icon(Icons.edit),
                  title: const Text('Edit'),
                  onTap: () => Navigator.pop(context, 'edit'),
                ),
                ListTile(
                  leading: const Icon(Icons.delete),
                  title: const Text('Delete'),
                  onTap: () => Navigator.pop(context, 'delete'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// ✅ Good — DraggableScrollableSheet for flexible height
showModalBottomSheet(
  context: context,
  isScrollControlled: true,
  builder: (_) => DraggableScrollableSheet(
    initialChildSize: 0.4,  // 40% of screen
    minChildSize: 0.2,     // Min when dragged down
    maxChildSize: 0.9,     // Max when dragged up
    expand: false,
    builder: (context, scrollController) {
      return Container(
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: ListView.builder(
          controller: scrollController,
          itemCount: 50,
          itemBuilder: (_, i) => ListTile(title: Text('Item $i')),
        ),
      );
    },
  ),
);
```

### Key Takeaway
- `showModalBottomSheet` with `isScrollControlled: true` for full-height sheets
- `DraggableScrollableSheet` for flexible, draggable bottom sheets
- Add `MediaQuery.of(context).viewInsets.bottom` for keyboard padding
- `backgroundColor: Colors.transparent` + custom `BoxDecoration` for rounded corners
- Drag handle is a simple `Container` with rounded decoration

---

## Scenario 3: Shimmer Loading Effect

### Problem
Show a shimmer/skeleton loading animation while data is being fetched.

```dart
// ✅ Good — shimmer effect with AnimatedBuilder
class ShimmerEffect extends StatefulWidget {
  final Widget child;
  const ShimmerEffect({super.key, required this.child});

  @override
  State<ShimmerEffect> createState() => _ShimmerEffectState();
}

class _ShimmerEffectState extends State<ShimmerEffect>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..repeat();
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
        return ShaderMask(
          shaderCallback: (bounds) {
            return LinearGradient(
              begin: Alignment(-1 + _controller.value * 2, 0),
              end: Alignment(_controller.value * 2, 0),
              colors: [
                Colors.grey[300]!,
                Colors.grey[100]!,
                Colors.grey[300]!,
              ],
            ).createShader(bounds);
          },
          child: child,
        );
      },
      child: widget.child,
    );
  }
}

// Skeleton card
class SkeletonCard extends StatelessWidget {
  const SkeletonCard({super.key});

  @override
  Widget build(BuildContext context) {
    return const ShimmerEffect(
      child: Card(
        child: ListTile(
          leading: CircleAvatar(backgroundColor: Colors.white),
          title: SizedBox(
            height: 16,
            child: ColoredBox(color: Colors.white),
          ),
          subtitle: SizedBox(
            height: 12,
            child: ColoredBox(color: Colors.white),
          ),
        ),
      ),
    );
  }
}

// Usage in screen
class ProductScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<Product>>(
      future: fetchProducts(),
      builder: (context, snapshot) {
        if (snapshot.connectionState != ConnectionState.done) {
          return ListView.builder(
            itemCount: 5,
            itemBuilder: (_, __) => const SkeletonCard(),
          );
        }
        return ListView.builder(
          itemCount: snapshot.data!.length,
          itemBuilder: (_, i) => ProductCard(product: snapshot.data![i]),
        );
      },
    );
  }
}

// Or use shimmer package
// pubspec.yaml: shimmer: ^3.0.0
Shimmer.fromColors(
  baseColor: Colors.grey[300]!,
  highlightColor: Colors.grey[100]!,
  child: const SkeletonCard(),
)
```

### Key Takeaway
- Shimmer = `ShaderMask` with animated `LinearGradient`
- `AnimationController..repeat()` for continuous animation
- Show skeleton cards (same layout as real cards) during loading
- `shimmer` package is simpler if you don't need custom animation
- Always dispose `AnimationController` to prevent memory leaks

---

## Scenario 4: Custom Dismissible List with Actions

### Problem
A list where swiping left reveals delete, swiping right reveals archive.

```dart
// ✅ Good — Dismissible with confirmDismiss
class SwipeableList extends StatelessWidget {
  final List<Item> items;
  const SwipeableList({super.key, required this.items});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: items.length,
      itemBuilder: (context, index) {
        final item = items[index];
        return Dismissible(
          key: ValueKey(item.id),
          background: Container(
            color: Colors.green,
            alignment: Alignment.centerLeft,
            padding: const EdgeInsets.only(left: 20),
            child: const Icon(Icons.archive, color: Colors.white),
          ),
          secondaryBackground: Container(
            color: Colors.red,
            alignment: Alignment.centerRight,
            padding: const EdgeInsets.only(right: 20),
            child: const Icon(Icons.delete, color: Colors.white),
          ),
          confirmDismiss: (direction) async {
            if (direction == DismissDirection.startToEnd) {
              // Swipe right → archive
              return await _showConfirmDialog(context, 'Archive?');
            } else {
              // Swipe left → delete
              return await _showConfirmDialog(context, 'Delete?');
            }
          },
          onDismissed: (direction) {
            if (direction == DismissDirection.startToEnd) {
              _archiveItem(item);
            } else {
              _deleteItem(item);
            }
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(direction == DismissDirection.startToEnd
                    ? 'Archived'
                    : 'Deleted'),
                action: SnackBarAction(
                  label: 'Undo',
                  onPressed: () => _restoreItem(item, index),
                ),
              ),
            );
          },
          child: ListTile(
            title: Text(item.title),
            subtitle: Text(item.subtitle),
          ),
        );
      },
    );
  }

  Future<bool> _showConfirmDialog(BuildContext context, String action) async {
    return await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(action),
        content: Text('Are you sure?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Confirm'),
          ),
        ],
      ),
    ) ?? false;
  }
}
```

### Key Takeaway
- `Dismissible` with `background` (swipe right) and `secondaryBackground` (swipe left)
- `confirmDismiss` — return false to cancel, true to proceed
- `onDismissed` — perform the action based on direction
- Always provide an Undo action via `SnackBarAction`
- Use `ValueKey` with unique ID for proper animation

---

## Scenario 5: Custom Pull-to-Refresh

### Problem
Implement pull-to-refresh with a custom indicator and loading state.

```dart
// ✅ Good 1 — RefreshIndicator (built-in)
class RefreshableList extends StatefulWidget {
  @override
  State<RefreshableList> createState() => _RefreshableListState();
}

class _RefreshableListState extends State<RefreshableList> {
  List<Item> _items = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      _items = await api.fetchItems();
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: _loadData,
      color: Colors.blue,
      backgroundColor: Colors.white,
      displacement: 20,
      child: _isLoading && _items.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
              itemCount: _items.length,
              itemBuilder: (_, i) => ListTile(title: Text(_items[i].title)),
            ),
    );
  }
}

// ✅ Good 2 — Custom refresh with Liquid Pull to Refresh
// pubspec.yaml: liquid_pull_to_refresh: ^4.0.0
LiquidPullToRefresh(
  onRefresh: _loadData,
  color: Colors.blue,
  backgroundColor: Colors.white,
  animSpeedFactor: 2,
  child: ListView.builder(
    itemCount: _items.length,
    itemBuilder: (_, i) => ListTile(title: Text(_items[i].title)),
  ),
)

// ✅ Good 3 — Custom sliver refresh
CustomScrollView(
  slivers: [
    CupertinoSliverRefreshControl(
      onRefresh: () async {
        await _loadData();
      },
    ),
    SliverList(
      delegate: SliverChildBuilderDelegate(
        (_, i) => ListTile(title: Text(_items[i].title)),
        childCount: _items.length,
      ),
    ),
  ],
)
```

### Key Takeaway
- `RefreshIndicator` — Material design pull-to-refresh
- `CupertinoSliverRefreshControl` — iOS-style pull-to-refresh (in slivers)
- `LiquidPullToRefresh` — custom animated indicator
- `onRefresh` must return `Future<void>` — completes when refresh is done
- Show loading indicator only on first load, not on refresh

---

## 🔗 Related Topics
- [Layouts](../beginner/Layouts.md)
- [Animations](../intermediate/Animations.md)
- [Custom Widgets](../intermediate/CustomWidgets.md)
