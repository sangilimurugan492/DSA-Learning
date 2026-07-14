# Custom Widgets

## Q1: How do you create reusable custom widgets?

```dart
// Custom button widget — encapsulates styling and behavior
class PrimaryButton extends StatelessWidget {
  final String label;
  final VoidCallback onPressed;
  final bool isLoading;
  final IconData? icon;

  const PrimaryButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.isLoading = false,
    this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: isLoading ? null : onPressed,
      style: ElevatedButton.styleFrom(
        minimumSize: const Size(double.infinity, 50),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
      child: isLoading
          ? const SizedBox(
              width: 20, height: 20,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                if (icon != null) ...[
                  Icon(icon),
                  const SizedBox(width: 8),
                ],
                Text(label),
              ],
            ),
    );
  }
}

// Usage
const PrimaryButton(
  label: 'Submit',
  onPressed: handleSubmit,
  icon: Icons.send,
)
```

---

## Q2: How do you use `CustomPaint` and `CustomPainter`?

```dart
// CustomPainter — draw custom shapes on canvas
class CirclePainter extends CustomPainter {
  final Color color;
  const CirclePainter({this.color = Colors.blue});

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = color
      ..style = PaintingStyle.fill;

    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;

    canvas.drawCircle(center, radius, paint);
  }

  @override
  bool shouldRepaint(covariant CirclePainter oldDelegate) {
    return oldDelegate.color != color;  // Repaint only if color changes
  }
}

// Usage
CustomPaint(
  size: const Size(100, 100),
  painter: const CirclePainter(color: Colors.red),
)
```

### Drawing a progress bar
```dart
class ProgressBarPainter extends CustomPainter {
  final double progress;  // 0.0 to 1.0
  final Color color;

  const ProgressBarPainter({required this.progress, this.color = Colors.blue});

  @override
  void paint(Canvas canvas, Size size) {
    // Background
    final bgPaint = Paint()..color = Colors.grey.shade300;
    final rRect = RRect.fromRectAndRadius(
      Rect.fromLTWH(0, 0, size.width, size.height),
      const Radius.circular(8),
    );
    canvas.drawRRect(rRect, bgPaint);

    // Progress
    final progressPaint = Paint()..color = color;
    final progressRect = RRect.fromRectAndRadius(
      Rect.fromLTWH(0, 0, size.width * progress, size.height),
      const Radius.circular(8),
    );
    canvas.drawRRect(progressRect, progressPaint);
  }

  @override
  bool shouldRepaint(covariant ProgressBarPainter old) => old.progress != progress;
}

// Usage
CustomPaint(
  size: const Size(200, 12),
  painter: ProgressBarPainter(progress: 0.7),
)
```

---

## Q3: How do you create a custom form field?

```dart
class CustomTextField extends StatelessWidget {
  final String label;
  final String? hint;
  final TextEditingController? controller;
  final String? Function(String?)? validator;
  final void Function(String)? onChanged;
  final bool obscureText;
  final Widget? suffixIcon;

  const CustomTextField({
    super.key,
    required this.label,
    this.hint,
    this.controller,
    this.validator,
    this.onChanged,
    this.obscureText = false,
    this.suffixIcon,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: TextFormField(
        controller: controller,
        validator: validator,
        onChanged: onChanged,
        obscureText: obscureText,
        decoration: InputDecoration(
          labelText: label,
          hintText: hint,
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
          suffixIcon: suffixIcon,
        ),
      ),
    );
  }
}

// Usage in a form
Form(
  key: _formKey,
  child: Column(
    children: [
      CustomTextField(
        label: 'Email',
        validator: (v) => v!.contains('@') ? null : 'Invalid email',
      ),
      CustomTextField(
        label: 'Password',
        obscureText: true,
        suffixIcon: IconButton(
          icon: const Icon(Icons.visibility),
          onPressed: () {},
        ),
      ),
    ],
  ),
)
```

---

## Q4: How do you create a composite widget?

```dart
// Profile card — composes multiple widgets into one
class ProfileCard extends StatelessWidget {
  final String name;
  final String email;
  final String? avatarUrl;
  final VoidCallback? onTap;

  const ProfileCard({
    super.key,
    required this.name,
    required this.email,
    this.avatarUrl,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              CircleAvatar(
                radius: 30,
                backgroundImage: avatarUrl != null
                    ? NetworkImage(avatarUrl!)
                    : null,
                child: avatarUrl == null
                    ? Text(name.isNotEmpty ? name[0] : '?')
                    : null,
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(name, style: Theme.of(context).textTheme.titleMedium),
                    const SizedBox(height: 4),
                    Text(email, style: Theme.of(context).textTheme.bodySmall),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right, color: Colors.grey),
            ],
          ),
        ),
      ),
    );
  }
}
```

---

## Q5: How do you create a widget with custom gestures?

```dart
// Swipeable card — detect horizontal swipe
class SwipeableCard extends StatefulWidget {
  final Widget child;
  final VoidCallback onSwipeLeft;
  final VoidCallback onSwipeRight;

  const SwipeableCard({
    super.key,
    required this.child,
    required this.onSwipeLeft,
    required this.onSwipeRight,
  });

  @override
  State<SwipeableCard> createState() => _SwipeableCardState();
}

class _SwipeableCardState extends State<SwipeableCard> {
  double _dx = 0;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onHorizontalDragUpdate: (details) {
        setState(() => _dx += details.delta.dx);
      },
      onHorizontalDragEnd: (details) {
        if (_dx < -100) {
          widget.onSwipeLeft();
        } else if (_dx > 100) {
          widget.onSwipeRight();
        }
        setState(() => _dx = 0);
      },
      child: Transform.translate(
        offset: Offset(_dx, 0),
        child: widget.child,
      ),
    );
  }
}

// Long press + double tap
GestureDetector(
  onTap: () => print('Single tap'),
  onDoubleTap: () => print('Double tap'),
  onLongPress: () => print('Long press'),
  child: const Card(child: ListTile(title: Text('Tap me'))),
)

// Dismissible — swipe to dismiss
Dismissible(
  key: ValueKey(item.id),
  background: Container(color: Colors.red),
  onDismissed: (direction) {
    // Remove item
  },
  child: ListTile(title: Text(item.title)),
)
```

---

## Q6: How do you create a custom RenderObject?

```dart
// Custom RenderObject — ultimate control over layout and painting
// Example: A widget that sizes to the largest child

class MaxSizeBox extends RenderBox
    with RenderObjectWithChildMixin<RenderBox> {
  double _maxWidth = double.infinity;

  set maxWidth(double value) {
    if (_maxWidth != value) {
      _maxWidth = value;
      markNeedsLayout();
    }
  }

  @override
  void performLayout() {
    if (child != null) {
      child!.layout(
        BoxConstraints(maxWidth: _maxWidth, maxHeight: constraints.maxHeight),
        parentUsesSize: true,
      );
      size = constraints.constrainDimensions(
        child!.size.width,
        child!.size.height,
      );
    } else {
      size = constraints.smallest;
    }
  }

  @override
  void paint(PaintingContext context, Offset offset) {
    if (child != null) {
      context.paintChild(child!, offset);
    }
  }

  @override
  bool hitTestChildren(BoxHitTestResult result, {required Offset position}) {
    return child?.hitTest(result, position: position) ?? false;
  }
}

// Wrap in a RenderObjectWidget
class MaxSize extends SingleChildRenderObjectWidget {
  final double maxWidth;
  const MaxSize({super.key, required this.maxWidth, super.child});

  @override
  RenderObject createRenderObject(BuildContext context) {
    return MaxSizeBox()..maxWidth = maxWidth;
  }

  @override
  void updateRenderObject(BuildContext context, MaxSizeBox renderObject) {
    renderObject.maxWidth = maxWidth;
  }
}
```

> **Note:** Custom RenderObjects are rarely needed. Use `LayoutBuilder`, `CustomPaint`, or composition first. Only use RenderObject for custom layout algorithms.

---

## Q7: How do you theme custom widgets?

```dart
// Define custom theme data
class ButtonThemeData {
  final double height;
  final double borderRadius;
  final Color color;
  final TextStyle textStyle;

  const ButtonThemeData({
    this.height = 50,
    this.borderRadius = 12,
    this.color = Colors.blue,
    this.textStyle = const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
  });

  static ButtonThemeData of(BuildContext context) {
    return context.dependOnInheritedWidgetOfExactType<_ButtonTheme>()?.data
        ?? const ButtonThemeData();
  }
}

class _ButtonTheme extends InheritedWidget {
  final ButtonThemeData data;
  const _ButtonTheme({required this.data, required super.child});

  @override
  bool updateShouldNotify(_ButtonTheme old) => data != old.data;
}

// Theme provider widget
class ButtonTheme extends StatelessWidget {
  final ButtonThemeData data;
  final Widget child;
  const ButtonTheme({super.key, required this.data, required this.child});

  @override
  Widget build(BuildContext context) {
    return _ButtonTheme(data: data, child: child);
  }
}

// Usage
ButtonTheme(
  data: const ButtonThemeData(height: 60, color: Colors.green),
  child: const PrimaryButton(label: 'Custom', onPressed: {}),
)

// In PrimaryButton, read theme:
@override
Widget build(BuildContext context) {
  final theme = ButtonThemeData.of(context);
  return SizedBox(
    height: theme.height,
    child: ElevatedButton(
      style: ElevatedButton.styleFrom(
        backgroundColor: theme.color,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(theme.borderRadius),
        ),
      ),
      child: Text(label, style: theme.textStyle),
      onPressed: onPressed,
    ),
  );
}
```

---

## 🔗 Related Topics
- [Widgets](../beginner/Widgets.md)
- [Layouts](../beginner/Layouts.md)
- [Animations](Animations.md)
