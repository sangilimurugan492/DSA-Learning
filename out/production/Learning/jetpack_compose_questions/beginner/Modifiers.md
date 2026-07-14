# Modifiers

## Q1: What is a Modifier and how do you use it?

Modifiers decorate or add behavior to composables. They are chained to apply multiple effects.

```kotlin
// Chain modifiers — order matters!
Text(
    "Hello",
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(Color.Blue)
        .clip(RoundedCornerShape(8.dp))
)

// Modifier order matters!
// padding → background: padding is INSIDE background
Modifier.padding(16.dp).background(Color.Blue)

// background → padding: padding is OUTSIDE background
Modifier.background(Color.Blue).padding(16.dp)
```

---

## Q2: What are the most common modifiers?

```kotlin
// Size
Modifier.size(100.dp)                    // Fixed size
Modifier.fillMaxWidth()                  // Fill width
Modifier.fillMaxSize()                   // Fill both
Modifier.width(200.dp)                   // Fixed width
Modifier.height(50.dp)                   // Fixed height
Modifier.weight(1f)                      // Proportional (Row/Column)
Modifier.wrapContentSize()               // Wrap content

// Padding & spacing
Modifier.padding(16.dp)                  // All sides
Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
Modifier.padding(start = 8.dp, top = 4.dp)

// Background & border
Modifier.background(Color.Blue)
Modifier.background(Color.Red, shape = CircleShape)
Modifier.border(1.dp, Color.Gray, RoundedCornerShape(8.dp))

// Shape & clip
Modifier.clip(CircleShape)
Modifier.clip(RoundedCornerShape(12.dp))

// Click & interaction
Modifier.clickable { /* onClick */ }
Modifier.clickable(onClickLabel = "Add") { /* onClick */ }

// Alignment
Modifier.align(Alignment.Center)         // In Box
Modifier.alignBy(Alignment.Start)        // In Row
```

---

## Q3: How does modifier order matter?

```kotlin
// Order matters — modifiers are applied left to right

// 1. padding before background → padding is INSIDE background
Box(Modifier
    .padding(16.dp)      // Space inside
    .background(Color.Red)  // Background fills including padding
)
// Result: Red box with 16dp red padding around content

// 2. background before padding → padding is OUTSIDE background
Box(Modifier
    .background(Color.Red)  // Background only on content
    .padding(16.dp)      // Space outside (transparent)
)
// Result: Red box with 16dp transparent padding around it

// 3. size before padding → size includes padding
Box(Modifier
    .size(100.dp)        // Total 100dp
    .padding(16.dp)      // Content area = 100 - 32 = 68dp
)

// 4. padding before size → size is content area
Box(Modifier
    .padding(16.dp)      // 16dp padding
    .size(100.dp)        // Content = 100dp, total = 132dp
)

// 5. clip before background vs background before clip
Modifier.clip(CircleShape).background(Color.Red)  // Red circle
Modifier.background(Color.Red).clip(CircleShape)  // Red square clipped to circle
```

---

## Q4: How do you use clickable and interaction modifiers?

```kotlin
// Basic click
Modifier.clickable { /* handle click */ }

// Click with label (accessibility)
Modifier.clickable(onClickLabel = "Add to cart") { /* ... */ }

// Click with interaction source (ripple, etc.)
val interactionSource = remember { MutableInteractionSource() }
Modifier.clickable(
    interactionSource = interactionSource,
    indication = ripple(),
) { /* handle click */ }

// Detect pressed state
val isPressed by interactionSource.collectIsPressedAsState()
val scale = if (isPressed) 0.95f else 1.0f
Modifier.graphicsLayer { scaleX = scale; scaleY = scale }

// Toggleable (checkbox-like)
Modifier.toggleable(value = isChecked, onValueChange = { isChecked = it })

// Selectable (radio-like)
Modifier.selectable(selected = isSelected, onClick = { /* select */ })

// Long press
Modifier.combinedClickable(
    onClick = { /* single click */ },
    onLongClick = { /* long click */ },
)
```

---

## Q5: How do you use graphicsLayer and drawBehind?

```kotlin
// graphicsLayer — apply transformations (scale, rotation, alpha, translation)
Modifier.graphicsLayer {
    scaleX = 1.2f
    scaleY = 1.2f
    rotationZ = 45f
    alpha = 0.8f
    translationX = 10f
    translationY = 20f
    shadowElevation = 8f
}

// drawBehind — draw behind content
Modifier.drawBehind {
    drawRect(color = Color.Red)
    drawCircle(color = Color.Blue, radius = 50f)
}

// drawWithContent — draw before and after content
Modifier.drawWithContent {
    drawRect(color = Color.Yellow)  // Behind content
    drawContent()                    // The actual content
    drawRect(color = Color.Transparent, style = Stroke(width = 2.dp.toPx()))  // Border
}

// drawWithCache — cache drawing
Modifier.drawWithCache {
    val gradient = Brush.linearGradient(listOf(Color.Red, Color.Blue))
    onDrawBehind { drawRect(gradient) }
}
```

---

## Q6: How do you create a custom modifier?

```kotlin
// 1. Simple modifier using Modifier extension
fun Modifier.fadeBackground(color: Color, alpha: Float): Modifier =
    this.then(Modifier.background(color.copy(alpha = alpha)))

// 2. Custom modifier using composed
fun Modifier.pressEffect(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { isPressed = true; awaitRelease(); isPressed = false },
            )
        }
}

// 3. Custom layout modifier
fun Modifier.fixedHeight(height: Dp): Modifier = this.then(
    Modifier.height(height)
)

// Usage
Text("Hello", modifier = Modifier
    .fadeBackground(Color.Red, 0.5f)
    .pressEffect()
    .fillMaxWidth()
)
```

---

## Q7: How do you use layout modifiers (offset, wrapContentSize, aspectRatio)?

```kotlin
// offset — move position
Modifier.offset(x = 10.dp, y = 20.dp)  // In composition phase
Modifier.offset { IntOffset(0, scrollState.value) }  // In layout phase (deferred)

// wrapContentSize — align content within available space
Modifier.wrapContentSize(align = Alignment.Center)
Modifier.wrapContentWidth(align = Alignment.CenterHorizontally)
Modifier.wrapContentHeight(align = Alignment.Top)

// aspectRatio — width:height ratio
Modifier.aspectRatio(1f)     // Square (1:1)
Modifier.aspectRatio(16f / 9f)  // Widescreen (16:9)

// requiredSize — override parent constraints
Modifier.requiredSize(100.dp)  // Forces 100dp even if parent is smaller

// zIndex — change draw order
Modifier.zIndex(1f)  // Drawn above siblings with lower zIndex

// windowInsetsPadding — respect system insets
Modifier.windowInsetsPadding(WindowInsets.statusBars)
Modifier.windowInsetsPadding(WindowInsets.navigationBars)
```

---

## 🔗 Related Topics
- [Composables](Composables.md)
- [Layouts](Layouts.md)
- [State](State.md)
