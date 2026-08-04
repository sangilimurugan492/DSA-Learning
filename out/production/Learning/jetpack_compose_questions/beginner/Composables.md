# Composables

## Q1: What are the basic composables in Compose?

```kotlin
// Text — display text
Text("Hello, World!", style = MaterialTheme.typography.headlineMedium)

// Button — clickable button
Button(onClick = { /* action */ }) {
    Text("Click Me")
}

// Image — display image
Image(painter = painterResource(R.drawable.logo), contentDescription = "Logo")

// Icon — vector icon
Icon(Icons.Default.Home, contentDescription = "Home")

// Box — stack children (like FrameLayout)
Box {
    Text("Background")
    Text("Foreground")
}

// Column — vertical list (like LinearLayout vertical)
Column {
    Text("First")
    Text("Second")
}

// Row — horizontal list (like LinearLayout horizontal)
Row {
    Text("Left")
    Text("Right")
}
```

---

## Q2: How do you use Text composable?

```kotlin
// Basic
Text("Hello")

// With style
Text(
    text = "Hello, $name!",
    color = Color.Blue,
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    fontStyle = FontStyle.Italic,
    fontFamily = FontFamily.Serif,
    letterSpacing = 1.sp,
    textDecoration = TextDecoration.Underline,
    textAlign = TextAlign.Center,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
)

// Annotated string — mixed styles
Text(buildAnnotatedString {
    append("Hello, ")
    withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
        append("World!")
    }
})

// Clickable text
Text(
    text = "Click here",
    modifier = Modifier.clickable { /* handle click */ },
    color = Color.Blue,
    textDecoration = TextDecoration.Underline,
)
```

---

## Q3: How do you use Button composables?

```kotlin
// Button
Button(onClick = { /* action */ }) {
    Text("Submit")
}

// OutlinedButton
OutlinedButton(onClick = { /* action */ }) {
    Text("Cancel")
}

// TextButton
TextButton(onClick = { /* action */ }) {
    Text("Skip")
}

// FloatingActionButton
FloatingActionButton(onClick = { /* action */ }) {
    Icon(Icons.Default.Add, contentDescription = "Add")
}

// IconButton
IconButton(onClick = { /* action */ }) {
    Icon(Icons.Default.Favorite, contentDescription = "Favorite")
}

// Button with icon + text
Button(onClick = { /* action */ }) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("Send")
    }
}

// Disabled button
Button(onClick = {}, enabled = false) { Text("Disabled") }
```

---

## Q4: How do you use Image and Icon?

```kotlin
// Image from resource
Image(
    painter = painterResource(R.drawable.profile),
    contentDescription = "Profile photo",
    modifier = Modifier.size(100.dp).clip(CircleShape),
    contentScale = ContentScale.Crop,
)

// Image from URL (with Coil)
AsyncImage(
    model = "https://example.com/photo.jpg",
    contentDescription = "Network photo",
    modifier = Modifier.size(100.dp),
)

// Icon
Icon(
    imageVector = Icons.Default.Home,
    contentDescription = "Home",
    tint = MaterialTheme.colorScheme.primary,
    modifier = Modifier.size(24.dp),
)

// Icon from drawable
Icon(
    painter = painterResource(R.drawable.ic_custom),
    contentDescription = "Custom icon",
)

// contentDescription = null for decorative icons
Icon(Icons.Default.Search, contentDescription = null)  // Decorative
```

---

## Q5: How do you use Box, Column, and Row?

```kotlin
// Column — vertical arrangement
Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Text("First")
    Text("Second")
    Text("Third")
}

// Row — horizontal arrangement
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text("Left")
    Text("Right")
}

// Box — stacking (z-order)
Box(
    modifier = Modifier.size(200.dp),
    contentAlignment = Alignment.Center,
) {
    Image(painter = painterResource(R.drawable.bg), contentDescription = null)
    Text("Overlay", color = Color.White)
}

// Box with alignment per child
Box {
    Text("Top", modifier = Modifier.align(Alignment.TopStart))
    Text("Bottom", modifier = Modifier.align(Alignment.BottomEnd))
}
```

### Arrangement Options
| Column (vertical) | Row (horizontal) |
|-------------------|------------------|
| `Arrangement.Top` | `Arrangement.Start` |
| `Arrangement.Center` | `Arrangement.Center` |
| `Arrangement.Bottom` | `Arrangement.End` |
| `Arrangement.SpaceEvenly` | `Arrangement.SpaceEvenly` |
| `Arrangement.SpaceBetween` | `Arrangement.SpaceBetween` |
| `Arrangement.SpaceAround` | `Arrangement.SpaceAround` |
| `Arrangement.spacedBy(8.dp)` | `Arrangement.spacedBy(8.dp)` |

---

## Q6: How do you use Spacer and Divider?

```kotlin
// Spacer — add space between elements
Column {
    Text("First")
    Spacer(modifier = Modifier.height(16.dp))
    Text("Second")
}

Row {
    Text("Left")
    Spacer(modifier = Modifier.width(8.dp))
    Text("Right")
}

// Spacer with weight (fills remaining space)
Row {
    Text("Left")
    Spacer(modifier = Modifier.weight(1f))  // Takes all remaining space
    Text("Right")
}

// HorizontalDivider (Material 3)
Column {
    Text("Section 1")
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
    Text("Section 2")
}
```

---

## Q7: How do you handle user input (TextField)?

```kotlin
// Basic TextField
@Composable
fun NameInput() {
    var name by remember { mutableStateOf("") }

    TextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Name") },
        placeholder = { Text("Enter your name") },
        singleLine = true,
    )
}

// OutlinedTextField
OutlinedTextField(
    value = email,
    onValueChange = { email = it },
    label = { Text("Email") },
    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
    trailingIcon = { Icon(Icons.Default.Clear, contentDescription = "Clear") },
    isError = !isValid,
    supportingText = { Text("Enter valid email") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
)

// Password field
var password by remember { mutableStateOf("") }
var visible by remember { mutableStateOf(false) }

OutlinedTextField(
    value = password,
    onValueChange = { password = it },
    label = { Text("Password") },
    visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
    trailingIcon = {
        IconButton(onClick = { visible = !visible }) {
            Icon(
                if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "Toggle password",
            )
        }
    },
)
```

---

## Q8: How do you use `LazyColumn` and `LazyRow` for lists?

```kotlin
// LazyColumn — vertical scrolling list (only composes visible items)
@Composable
fun SimpleList() {
    LazyColumn {
        items(100) { index ->
            Text("Item $index", modifier = Modifier.padding(8.dp))
        }
    }
}

// With data list + key (efficient recomposition)
@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users, key = { it.id }) { user ->
            UserItem(user)
        }
    }
}

// itemsIndexed — need index
LazyColumn {
    itemsIndexed(users) { index, user ->
        Text("$index: ${user.name}")
    }
}

// LazyRow — horizontal
LazyRow {
    items(photos) { photo ->
        AsyncImage(model = photo.url, contentDescription = null)
    }
}

// With header and footer
LazyColumn {
    item { Text("Header") }
    items(data) { item -> TextItem(item) }
    item { Text("Footer") }
}

// Sticky header
LazyColumn {
    stickyHeader { Text("Sticky", modifier = Modifier.background(Color.Yellow)) }
    items(100) { Text("Item $it") }
}
```

> **Performance:** Always use `key` for list items — it helps Compose track items across recompositions and avoid unnecessary work. Without keys, reordering items causes full recomposition of affected items.

---

## Q9: How do you use `Scaffold` and `Snackbar`?

```kotlin
@Composable
fun MainScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My App") },
                navigationIcon = { Icon(Icons.Default.Menu, contentDescription = "Menu") },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Item added!")
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Text("Content")
        }
    }
}

// Snackbar with action
scope.launch {
    val result = snackbarHostState.showSnackbar(
        message = "Item deleted",
        actionLabel = "Undo",
        duration = SnackbarDuration.Short,
    )
    if (result == SnackbarResult.ActionPerformed) {
        // Undo deletion
    }
}
```

> **Key:** `Scaffold` implements basic Material Design layout structure. It provides slots for `topBar`, `bottomBar`, `floatingActionButton`, `snackbarHost`, and `drawer`. The `padding` parameter gives you the correct insets for content.

---

## Q10: How do you handle gestures in Compose?

```kotlin
// Click
Box(Modifier.clickable { /* handle click */ }) { Text("Click me") }

// Long press + double click
Box(
    Modifier.combinedClickable(
        onClick = { /* single click */ },
        onLongClick = { /* long click */ },
        onDoubleClick = { /* double click */ },
    )
) { Text("Tap me") }

// Swipe/drag
Box(
    Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragEnd = { /* gesture ended */ },
        ) { change, dragAmount ->
            // dragAmount — pixels dragged horizontally
        }
    }
) { Text("Swipe me") }

// Transform (pinch-to-zoom, rotate)
var scale by remember { mutableStateOf(1f) }
var rotation by remember { mutableStateOf(0f) }

Box(
    Modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, touchRotation ->
            scale *= zoom
            rotation += touchRotation
        }
    }.graphicsLayer {
        scaleX = scale
        scaleY = scale
        rotationZ = rotation
    }
) { Text("Pinch me") }

// Scrollable
Box(
    Modifier.scrollable(
        state = rememberScrollState(),
        orientation = Orientation.Vertical,
    )
) { Text("Scroll me") }

// Swipe to dismiss
val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { it == SwipeToDismissBoxValue.EndToStart },
)
SwipeToDismissBox(
    state = dismissState,
    backgroundContent = { /* red background */ },
) { Text("Swipe to delete") }
```

| Gesture API | Use Case |
|-------------|----------|
| `clickable` | Simple click |
| `combinedClickable` | Click + long press + double click |
| `detectHorizontalDragGestures` | Swipe, drag |
| `detectTransformGestures` | Pinch zoom, rotate |
| `scrollable` | Custom scroll |
| `SwipeToDismissBox` | Swipe to delete |

> **Tip:** Use `pointerInput` with a key to reset gesture detection when the key changes. For simple clicks, always prefer `Modifier.clickable` over `pointerInput` — it handles accessibility, ripple, and focus automatically.

---

## Q11: What is `derivedStateOf` and when should you use it?

```kotlin
// derivedStateOf — converts multiple states into one state
// Only recomposes when the DERIVED result changes, not on every source change

@Composable
fun TodoList(items: List<Todo>) {
    var searchText by remember { mutableStateOf("") }

    // ❌ Without derivedStateOf — recomposes on every keystroke
    val filteredItems = items.filter { it.title.contains(searchText) }

    // ✅ With derivedStateOf — only recomposes when filtered list changes
    val filteredItems by remember {
        derivedStateOf {
            items.filter { it.title.contains(searchText) }
        }
    }

    LazyColumn {
        items(filteredItems) { item ->
            Text(item.title)
        }
    }
}

// Use case: scroll-to-load-more
@Composable
fun InfiniteList() {
    val listState = rememberLazyListState()

    // Only true when near the end — derivedStateOf prevents recomposition on every scroll
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) loadMore()
    }
}
```

> **Key:** Use `derivedStateOf` when a computed value depends on multiple states but only changes occasionally. It prevents unnecessary recompositions. Common use cases: filtering, sorting, scroll-based UI changes, and conditional UI state.

---

## Q12: How do you use `CompositionLocal` for providing values?

```kotlin
// CompositionLocal — pass data deeply through the tree without explicit parameters

// 1. Define a CompositionLocal with a default value
val LocalThemeColor = staticCompositionLocalOf { Color.Black }

// 2. Provide a value higher in the tree
@Composable
fun App() {
    CompositionLocalProvider(LocalThemeColor provides Color.Blue) {
        Screen()
    }
}

// 3. Consume it anywhere below
@Composable
fun CustomText() {
    val color = LocalThemeColor.current  // Gets Blue
    Text("Hello", color = color)
}

// dynamicCompositionLocalOf — recomposes when value changes
val LocalCurrentUser = compositionLocalOf<User?> { null }

@Composable
fun App(user: User?) {
    CompositionLocalProvider(LocalCurrentUser provides user) {
        ProfileScreen()
    }
}

@Composable
fun ProfileScreen() {
    val user = LocalCurrentUser.current
    Text("Welcome, ${user?.name ?: "Guest"}")
}
```

| Type | When to Use | Performance |
|------|-------------|-------------|
| `staticCompositionLocalOf` | Value never changes after init | Better (skips tracking) |
| `compositionLocalOf` | Value can change at runtime | Slightly worse (tracks reads) |

> **Best Practice:** Use `CompositionLocal` sparingly — only for truly cross-cutting concerns like theme, locale, or current user. Overuse makes code hard to trace. Prefer explicit parameters for most data flow.

---

## Q13: What is `produceState` and how do you use it for async data?

```kotlin
// produceState — convert non-Compose state (Flow, LiveData, callbacks) into Compose state

// Load data from API
@Composable
fun loadUser(userId: String): State<Result<User>> {
    return produceState(initialValue = Result.Loading, userId) {
        // This runs in a coroutine scope
        val result = api.fetchUser(userId)
        value = Result.Success(result)  // Update state
    }
}

// Usage
@Composable
fun UserProfile(userId: String) {
    val userState by loadUser(userId)
    when (val state = userState) {
        is Result.Loading -> CircularProgressIndicator()
        is Result.Success -> Text("Hello, ${state.data.name}")
        is Result.Error -> Text("Error: ${state.error}")
    }
}

// Collect Flow as State
@Composable
fun FlowExample(viewModel: MyViewModel) {
    // collectAsState — shorthand for produceState with Flow
    val items by viewModel.items.collectAsState()
    LazyColumn {
        items(items) { Text(it.name) }
    }
}

// produceState with cleanup
@Composable
fun observeLocation(): State<Location?> {
    return produceState<Location?>(initialValue = null) {
        val callback = object : LocationCallback {
            override fun onLocation(loc: Location) { value = loc }
        }
        locationClient.requestUpdates(callback)
        awaitDispose {  // Cleanup when composable leaves composition
            locationClient.removeUpdates(callback)
        }
    }
}
```

> **Key:** `produceState` is the bridge between imperative APIs (callbacks, listeners) and Compose's declarative state. It automatically cancels the coroutine when the composable leaves. For Flows, use `collectAsState()` which is built on top of `produceState`.

---

## Q14: How do you create a custom `Modifier` extension?

```kotlin
// Custom modifier — reusable styling and behavior

// 1. Simple modifier extension (composable)
fun Modifier.roundedBorder(
    radius: Dp = 8.dp,
    color: Color = Color.Gray,
    width: Dp = 1.dp,
): Modifier = this.then(
    Modifier.border(width, color, RoundedCornerShape(radius))
)

// Usage
Box(
    modifier = Modifier
        .size(100.dp)
        .roundedBorder(radius = 12.dp, color = Color.Blue)
)

// 2. Custom modifier with state (needs Modifier.Node API or factory)
fun Modifier.pressAnimation(): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f)

    this
        .graphicsLayer { scaleX = scale; scaleY = scale }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { pressed = true; awaitRelease(); pressed = false }
            )
        }
}

// 3. Modifier with layout — custom measurement
fun Modifier.circleCrop(): Modifier = this.clip(CircleShape)

// 4. Chaining modifiers
fun Modifier.cardStyle(
    padding: Dp = 16.dp,
    elevation: Dp = 4.dp,
): Modifier = this
    .padding(padding)
    .shadow(elevation, RoundedCornerShape(12.dp))
    .background(MaterialTheme.colorScheme.surface)
    .clip(RoundedCornerShape(12.dp))

// Usage
Column(
    modifier = Modifier
        .fillMaxWidth()
        .cardStyle(padding = 20.dp)
) {
    Text("Card content")
}
```

> **Best Practice:** Extract common modifier chains into extensions. Use `composed {}` when you need state or animations inside the modifier. For production code, prefer the `Modifier.Node` API (Compose 1.5+) for better performance — it avoids recomposition.

---

## Q15: What is `key` and how does it affect recomposition?

```kotlin
// key — explicitly identify a composable in a list
// Helps Compose track items across recomposition

// ❌ Without key — reordering causes full recomposition of affected items
LazyColumn {
    items(users) { user ->
        UserItem(user)  // No key — Compose can't track
    }
}

// ✅ With key — reordering only recomposes changed items
LazyColumn {
    items(users, key = { it.id }) { user ->
        UserItem(user)  // Keyed by ID
    }
}

// key() in loops — identify iterations
@Composable
fun FormSections(sections: List<Section>) {
    Column {
        sections.forEach { section ->
            key(section.id) {
                SectionItem(section)  // State preserved across reordering
            }
        }
    }
}

// Key with multiple values
key(user.id, isActive) {
    // Recomposes when either user.id or isActive changes
    UserProfile(user)
}
```

| Scenario | Without Key | With Key |
|----------|-------------|---------|
| List reorder | Full recomposition | Only moved items |
| List delete | All items below shift | Only removed item |
| List insert | All items below shift | Only new item |
| State preservation | Lost on reorder | Preserved |

> **Important:** Always provide `key` in `items()` for lists that can change. Use a stable, unique identifier (like database ID). Avoid using index as key — it defeats the purpose. Without keys, Compose uses positional memoization, which breaks when order changes.

---

## 🔗 Related Topics
- [Basics](Basics.md)
- [Layouts](Layouts.md)
- [Modifiers](Modifiers.md)
