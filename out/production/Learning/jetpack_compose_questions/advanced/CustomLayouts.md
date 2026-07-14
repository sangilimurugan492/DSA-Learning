# Custom Layouts

## Q1: How do you create a custom layout?

```kotlin
@Composable
fun SimpleColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        // 1. Measure children
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // 2. Calculate size
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.sumOf { it.height }

        // 3. Place children
        layout(width, height) {
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = y)
                y += placeable.height
            }
        }
    }
}
```

---

## Q2: How does the measure phase work?

```
Parent receives constraints (minWidth, maxWidth, minHeight, maxHeight)
  ↓
Parent measures each child with constraints
  ↓
Child returns Placeable (width, height)
  ↓
Parent places children at (x, y) positions
  ↓
layout(width, height) { place() }
```

```kotlin
Layout(content = content) { measurables, constraints ->
    // constraints: Constraints(minWidth, maxWidth, minHeight, maxHeight)

    // Measure with specific constraints
    val placeable = measurable.measure(
        Constraints(
            minWidth = 0,
            maxWidth = constraints.maxWidth,
            minHeight = 0,
            maxHeight = constraints.maxHeight,
        )
    )

    // placeable.width, placeable.height available
    // placeable.placeRelative(x, y)
}
```

---

## Q3: How do you create a custom layout modifier?

```kotlin
// Custom modifier that centers content
fun Modifier.center(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(constraints.maxWidth, constraints.maxHeight) {
        val x = (constraints.maxWidth - placeable.width) / 2
        val y = (constraints.maxHeight - placeable.height) / 2
        placeable.placeRelative(x, y)
    }
}

// Usage
Box(Modifier.size(200.dp).center()) {
    Text("Centered")
}

// Custom padding modifier
fun Modifier.customPadding(all: Dp): Modifier = layout { measurable, constraints ->
    val horizontal = all.roundToPx()
    val vertical = all.roundToPx()

    val placeable = measurable.measure(
        constraints.offset(-horizontal * 2, -vertical * 2)
    )

    layout(placeable.width + horizontal * 2, placeable.height + vertical * 2) {
        placeable.placeRelative(horizontal, vertical)
    }
}
```

---

## Q4: How do you use SubcomposeLayout?

```kotlin
// SubcomposeLayout — measure, then compose children based on measurements
@Composable
fun AdaptiveText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
) {
    SubcomposeLayout(modifier) { constraints ->
        // First, measure text
        val textPlaceable = subcompose("text") {
            Text(text, style = style, maxLines = 1)
        }.first().measure(constraints)

        // Decide if text fits
        val fits = textPlaceable.width <= constraints.maxWidth

        // Compose different content based on measurement
        val content = subcompose("content") {
            if (fits) {
                Text(text, style = style, maxLines = 1)
            } else {
                Text(text, style = style, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }.first().measure(constraints)

        layout(content.width, content.height) {
            content.placeRelative(0, 0)
        }
    }
}
```

### When to use SubcomposeLayout?
- Need to measure before composing
- Conditional composition based on available space
- Complex layouts like LazyColumn (only composes visible items)

### Drawback
- Slower than `Layout` (extra composition pass)
- Use only when necessary

---

## Q5: How do you create a flow layout?

```kotlin
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val hSpacing = horizontalSpacing.roundToPx()
        val vSpacing = verticalSpacing.roundToPx()

        val placeables = measurables.map { it.measure(constraints) }

        var x = 0
        var y = 0
        var lineHeight = 0
        var maxX = 0

        val positions = placeables.map { placeable ->
            if (x + placeable.width > constraints.maxWidth && x > 0) {
                // Wrap to next line
                x = 0
                y += lineHeight + vSpacing
                lineHeight = 0
            }

            val position = IntOffset(x, y)
            x += placeable.width + hSpacing
            lineHeight = maxOf(lineHeight, placeable.height)
            maxX = maxOf(maxX, x - hSpacing)
            position
        }

        val totalHeight = y + lineHeight

        layout(maxX, totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(positions[index])
            }
        }
    }
}

// Usage
FlowRow(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
    repeat(20) { i ->
        Chip(onClick = {}, label = { Text("Chip $i") })
    }
}
```

---

## Q6: How do you use BoxWithConstraints?

```kotlin
@Composable
fun ResponsiveLayout(content: @Composable () -> Unit) {
    BoxWithConstraints {
        // Access constraints as state
        val maxWidth = maxWidth  // Dp
        val maxHeight = maxHeight  // Dp
        val minWidth = minWidth
        val constraints = constraints  // Constraints object

        when {
            maxWidth < 400.dp -> CompactLayout()
            maxWidth < 800.dp -> MediumLayout()
            else -> ExpandedLayout()
        }
    }
}

// Adaptive grid
@Composable
fun AdaptiveGrid(
    content: @Composable () -> Unit,
) {
    BoxWithConstraints {
        val columns = (maxWidth / 120.dp).toInt().coerceAtLeast(1)

        LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
            // items
        }
    }
}
```

---

## Q7: How do you draw custom graphics?

```kotlin
// Canvas — custom drawing
@Composable
fun CustomDrawing() {
    Canvas(modifier = Modifier.size(200.dp)) {
        // Draw circle
        drawCircle(
            color = Color.Red,
            radius = 100f,
            center = Offset(size.width / 2, size.height / 2),
        )

        // Draw line
        drawLine(
            color = Color.Blue,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = 4f,
        )

        // Draw rectangle
        drawRect(
            color = Color.Green,
            topLeft = Offset(50f, 50f),
            size = Size(100f, 100f),
            style = Stroke(width = 2f),
        )

        // Draw arc
        drawArc(
            color = Color.Yellow,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = true,
            rect = Rect(0f, 0f, size.width, size.height),
        )

        // Draw path
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width / 2, size.height)
            close()
        }
        drawPath(path, color = Color.Magenta)

        // Rotate, scale, translate
        rotate(45f) {
            drawRect(color = Color.Cyan, size = size / 2)
        }
    }
}

// Animated canvas
@Composable
fun AnimatedCircle() {
    val transition = rememberInfiniteTransition(label = "circle")
    val scale by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "scale",
    )

    Canvas(Modifier.size(100.dp)) {
        drawCircle(
            color = Color.Red,
            radius = size.minDimension / 2 * scale,
            center = Offset(size.width / 2, size.height / 2),
        )
    }
}
```

---

## 🔗 Related Topics
- [Layouts](../beginner/Layouts.md)
- [Modifiers](../beginner/Modifiers.md)
- [Performance](Performance.md)
