# Layouts

## Q1: How do Column, Row, and Box work?

```kotlin
// Column — vertical, children stacked top to bottom
Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Text("Item 1")
    Text("Item 2")
}

// Row — horizontal, children left to right
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text("Left")
    Text("Right")
}

// Box — children stacked on top of each other (z-order)
Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
    Text("Background")
    Text("Foreground")
}
```

---

## Q2: How does weight work in Column and Row?

```kotlin
// weight — distributes remaining space proportionally
Row(modifier = Modifier.fillMaxWidth()) {
    Text("Small", modifier = Modifier.weight(1f))      // 1/4 space
    Text("Medium", modifier = Modifier.weight(2f))     // 2/4 space
    Text("Large", modifier = Modifier.weight(1f))      // 1/4 space
}

// weight with fill = false (wrap content, not fill)
Row {
    Text("Fixed", modifier = Modifier.weight(1f, fill = false))
    Text("Flexible", modifier = Modifier.weight(1f))
}

// Column weight
Column(modifier = Modifier.fillMaxHeight()) {
    Text("Header")
    Spacer(modifier = Modifier.weight(1f))  // Pushes footer down
    Text("Footer")
}
```

---

## Q3: How do you use ConstraintLayout?

```kotlin
// build.gradle: implementation "androidx.constraintlayout:constraintlayout-compose:1.0.1"

@Composable
fun ConstraintLayoutExample() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (button, text) = createRefsFor("button", "text")

        Button(
            onClick = {},
            modifier = Modifier.constrainAs(button) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
            },
        ) { Text("Button") }

        Text(
            "Text",
            modifier = Modifier.constrainAs(text) {
                top.linkTo(button.bottom, margin = 8.dp)
                start.linkTo(button.start)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            },
        )
    }
}
```

---

## Q4: How do you create a scrollable column?

```kotlin
// verticalScroll — simple scrolling
val scrollState = rememberScrollState()
Column(modifier = Modifier.verticalScroll(scrollState)) {
    repeat(50) { i ->
        Text("Item $i", modifier = Modifier.padding(8.dp))
    }
}

// horizontalScroll
Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
    repeat(20) { Text("Item $it", modifier = Modifier.padding(8.dp)) }
}

// Scroll to position
val scope = rememberCoroutineScope()
Button(onClick = {
    scope.launch { scrollState.animateScrollTo(0) }  // Scroll to top
}) { Text("Top") }
```

---

## Q5: How do you use Card and Surface?

```kotlin
// Card — Material card with elevation
Card(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    onClick = { /* handle click */ },
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Title", style = MaterialTheme.typography.titleLarge)
        Text("Description", style = MaterialTheme.typography.bodyMedium)
    }
}

// Surface — generic surface with color/elevation
Surface(
    modifier = Modifier.padding(8.dp),
    color = MaterialTheme.colorScheme.primary,
    shape = CircleShape,
    tonalElevation = 4.dp,
) {
    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.padding(8.dp))
}
```

---

## Q6: How do you handle responsive layouts?

```kotlin
// WindowSizeClass — adapt to screen size
@Composable
fun ResponsiveScreen(windowSize: WindowSizeClass) {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> PhoneLayout()      // Phone portrait
        WindowWidthSizeClass.Medium -> TabletLayout()      // Tablet portrait
        WindowWidthSizeClass.Expanded -> DesktopLayout()   // Tablet landscape / desktop
        else -> PhoneLayout()
    }
}

// Orientation-based
val configuration = LocalConfiguration.current
if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
    Row { /* side by side */ }
} else {
    Column { /* stacked */ }
}

// BoxWithConstraints — adapt to available space
BoxWithConstraints {
    if (maxWidth < 600.dp) {
        CompactLayout()
    } else {
        WideLayout()
    }
}
```

---

## Q7: How do you use FlowRow and FlowColumn?

```kotlin
// FlowRow — wraps children to next line when out of space
FlowRow(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    maxItemsInEachRow = 3,
) {
    repeat(10) { i ->
        Chip(onClick = {}, label = { Text("Chip $i") })
    }
}

// FlowColumn — wraps vertically
FlowColumn(
    modifier = Modifier.fillMaxHeight(),
    maxItemsInEachColumn = 5,
) {
    items.forEach { item ->
        TextItem(item)
    }
}
```

---

## 🔗 Related Topics
- [Composables](Composables.md)
- [Modifiers](Modifiers.md)
- [State](State.md)
