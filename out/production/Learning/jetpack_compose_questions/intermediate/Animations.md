# Animations

## Q1: How do you use animate*AsState?

```kotlin
// animateColorAsState
val color by animateColorAsState(
    targetValue = if (isEnabled) Color.Green else Color.Gray,
    animationSpec = tween(durationMillis = 300),
    label = "color",
)
Box(Modifier.background(color).size(100.dp))

// animateFloatAsState
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.9f else 1f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "scale",
)
Box(Modifier.graphicsLayer { scaleX = scale; scaleY = scale })

// animateDpAsState
val cornerRadius by animateDpAsState(
    targetValue = if (isExpanded) 32.dp else 8.dp,
    label = "corner",
)
Box(Modifier.clip(RoundedCornerShape(cornerRadius)))

// animateIntAsState
val count by animateIntAsState(targetValue = target, label = "count")
Text("$count")

// animateBoundsAsState (for shared element transitions)
val bounds by animateBoundsAsState(targetValue = rect, label = "bounds")
```

---

## Q2: How do you use AnimatedVisibility?

```kotlin
// AnimatedVisibility — fade in/out
var visible by remember { mutableStateOf(true) }

AnimatedVisibility(visible = visible) {
    Text("Hello, I'm animated!")
}

// Custom enter/exit
AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + slideInVertically { it },
    exit = fadeOut() + slideOutVertically { -it },
) {
    Card { Text("Animated card") }
}

// With expand/shrink
AnimatedVisibility(
    visible = visible,
    enter = expandVertically(),
    exit = shrinkVertically(),
) {
    Column { Text("Expandable content") }
}

// AnimatedContent — animate between different content
AnimatedContent(targetState = count, label = "count") { target ->
    Text("$target")
}

// Custom transition for AnimatedContent
AnimatedContent(
    targetState = page,
    transitionSpec = {
        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
    },
    label = "page",
) { page ->
    when (page) {
        0 -> Screen1()
        1 -> Screen2()
    }
}
```

---

## Q3: How do you use updateTransition?

```kotlin
// updateTransition — multiple animations from one state change
enum class BoxState { Collapsed, Expanded }

@Composable
fun AnimatingBox() {
    var state by remember { mutableStateOf(BoxState.Collapsed) }
    val transition = updateTransition(targetState = state, label = "box")

    val color by transition.animateColor(label = "color") { s ->
        when (s) {
            BoxState.Collapsed -> Color.Gray
            BoxState.Expanded -> Color.Blue
        }
    }

    val size by transition.animateDp(label = "size") { s ->
        when (s) {
            BoxState.Collapsed -> 64.dp
            BoxState.Expanded -> 128.dp
        }
    }

    val cornerRadius by transition.animateDp(label = "corner") { s ->
        when (s) {
            BoxState.Collapsed -> 8.dp
            BoxState.Expanded -> 32.dp
        }
    }

    Box(
        Modifier
            .background(color, RoundedCornerShape(cornerRadius))
            .size(size)
            .clickable {
                state = if (state == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
            }
    )
}
```

---

## Q4: How do you use rememberInfiniteTransition?

```kotlin
// rememberInfiniteTransition — continuous looping animation
@Composable
fun PulsingDot() {
    val transition = rememberInfiniteTransition(label = "pulse")

    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    val alpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha",
    )

    Box(
        Modifier
            .size(50.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(Color.Red, CircleShape)
    )
}
```

---

## Q5: How do you use Animatable for programmatic animations?

```kotlin
@Composable
fun ProgrammaticAnimation() {
    val color = remember { Animatable(Color.Gray) }
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .size(100.dp)
            .background(color.value)
            .clickable {
                scope.launch {
                    // Animate to red
                    color.animateTo(Color.Red, animationSpec = tween(500))
                    // Then animate to blue
                    color.animateTo(Color.Blue, animationSpec = tween(500))
                    // Snap back to gray
                    color.snapTo(Color.Gray)
                }
            }
    )
}

// Keyframe animation
scope.launch {
    color.animateTo(
        targetValue = Color.Green,
        animationSpec = keyframes {
            durationMillis = 1000
            Color.Red at 0
            Color.Yellow at 500
            Color.Green at 1000
        },
    )
}
```

---

## Q6: How do you use animationSpec?

```kotlin
// tween — linear interpolation over duration
animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)

// spring — physics-based
animationSpec = spring(
    dampingRatio = Spring.DampingRatioHighBouncy,  // 0.2f
    stiffness = Spring.StiffnessLow,  // 200f
)

// keyframes — custom values at specific times
animationSpec = keyframes {
    durationMillis = 1000
    0f at 0 with FastOutSlowInEasing
    0.5f at 500 with LinearEasing
    1f at 1000
}

// repeatable — repeat N times
animationSpec = repeatable(
    iterations = 3,
    animation = tween(500),
    repeatMode = RepeatMode.Restart,
)

// infiniteRepeatable — loop forever
animationSpec = infiniteRepeatable(
    animation = tween(1000),
    repeatMode = RepeatMode.Reverse,
)

// Easing options
FastOutSlowInEasing       // Default, smooth
LinearEasing              // Constant speed
LinearOutSlowInEasing     // Decelerate
FastOutLinearInEasing     // Accelerate
```

---

## Q7: How do you create gesture-driven animations?

```kotlin
@Composable
fun SwipeToDismiss() {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .background(Color.White)
            .size(200.dp, 100.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            // Animate back to center or dismiss
                            if (offsetX.value > 300f) {
                                offsetX.animateTo(1000f)
                            } else {
                                offsetX.animateTo(0f, spring())
                            }
                        }
                    },
                ) { _, dragAmount ->
                    scope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount)
                    }
                }
            }
    ) {
        Text("Swipe me")
    }
}

// Modifier.draggable with anchoredDraggable
val anchoredDraggableState = remember {
    AnchoredDraggableState(
        initialValue = DragAnchor.Start,
        anchors = DraggableAnchors {
            DragAnchor.Start at 0f
            DragAnchor.End at 300f
        },
        positionalThreshold = { it * 0.5f },
        velocityThreshold = { 125f },
        animationSpec = tween(),
    )
}
```

---

## 🔗 Related Topics
- [State Management](StateManagement.md)
- [Lists](Lists.md)
- [UI Scenarios](../scenario_based/UIScenarios.md)
