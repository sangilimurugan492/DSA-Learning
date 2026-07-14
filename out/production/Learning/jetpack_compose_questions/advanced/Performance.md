# Performance

## Q1: What causes unnecessary recomposition?

```kotlin
// 1. Unstable parameters — List, lambda, data class with mutable fields
@Composable
fun BadExample(items: List<String>) {  // List is unstable
    items.forEach { Text(it) }
}

// 2. Reading state in parent that only child needs
@Composable
fun BadScroll(listState: LazyListState) {
    val index = listState.firstVisibleItemIndex  // Parent reads state
    Column {
        Text("Index: $index")  // Only child needs it
        LazyColumn(state = listState) { /* ... */ }
    }
}

// 3. Creating new objects every recomposition
@Composable
fun BadColors() {
    Text("Hello", color = Color(0xFF0000FF))  // New Color each time
}

// 4. Not using keys in lists
LazyColumn {
    items(list) { item ->  // No key — can't track items
        Text(item.name)
    }
}
```

---

## Q2: What is stability in Compose?

Stability determines if Compose can skip recomposition when inputs are equal.

| Type | Stable? | Examples |
|------|---------|---------|
| Primitives | ✅ | Int, String, Boolean |
| Immutable data classes | ✅ | `data class User(val name: String)` |
| List, Set, Map | ❌ | Mutable interfaces |
| Lambda (new each time) | ❌ | `{ }` created inline |
| Data class with var | ❌ | `data class User(var name: String)` |

```kotlin
// ❌ Unstable — has mutable field
data class User(var name: String, var age: Int)

// ✅ Stable — all val, immutable
data class User(val name: String, val age: Int)

// ❌ Unstable — List interface (could be mutable)
@Composable
fun ListExample(items: List<String>) { /* ... */ }

// ✅ Stable — use @Immutable annotation or kotlin.collections.ImmutableList
@Immutable
data class UserList(val users: List<User>)

@Composable
fun StableExample(users: UserList) { /* ... */ }
```

### @Immutable vs @Stable
| @Immutable | @Stable |
|------------|---------|
| Never changes after creation | Can change but Compose is notified |
| All fields are val | Fields can change via State |
| Strongest guarantee | Weaker but useful |
| Use for truly immutable data | Use for observable state holders |

---

## Q3: How do you make composables skippable?

```kotlin
// ✅ Skippable — all params stable, Compose can skip if inputs unchanged
@Composable
fun UserCard(user: User) {  // User is stable data class
    Text(user.name)
}

// ❌ Not skippable — unstable parameter (List)
@Composable
fun UserList(users: List<User>) {
    users.forEach { Text(it.name) }
}

// ✅ Fix — wrap in stable container
@Immutable
data class UserListState(val users: List<User>)

@Composable
fun UserList(state: UserListState) {
    state.users.forEach { Text(it.name) }
}

// ✅ Use ImmutableList (kotlinx.collections.immutable)
@Composable
fun UserList(users: ImmutableList<User>) {
    users.forEach { Text(it.name) }
}
```

### Checking skippability
```bash
# Add to build.gradle
# composeCompiler { reportsDestination = file("compose_reports") }

# Run: ./gradlew assembleDebug
# Check: app/build/compose_reports/*_composables.txt
# Look for: restartable skippable
```

---

## Q4: How do you defer state reads to minimize recomposition?

```kotlin
// ❌ Bad — reads state in composition, triggers recomposition
@Composable
fun BadScroll(scroll: ScrollState) {
    val offset = scroll.value  // Read in composition
    Box(Modifier.offset(y = offset.dp))  // Recomposes on scroll
}

// ✅ Good — defer read to layout phase (no recomposition)
@Composable
fun GoodScroll(scroll: ScrollState) {
    Box(Modifier.offset { IntOffset(0, scroll.value) })  // Read in layout
    // No recomposition — only relayout
}

// ✅ Good — defer read to draw phase
@Composable
fun GoodColor(scroll: ScrollState) {
    Box(Modifier.drawBehind {
        val color = if (scroll.value > 100) Color.Red else Color.Blue
        drawRect(color)
    })
    // No recomposition or relayout — only redraw
}
```

### Phase deferral
```
Composition (expensive) → Layout (cheaper) → Drawing (cheapest)

Read state as late as possible:
  Modifier.offset { }     → Layout phase (best for scroll)
  Modifier.drawBehind { } → Draw phase (best for color/alpha)
  Direct read              → Composition phase (most expensive)
```

---

## Q5: How do you use keys to optimize recomposition?

```kotlin
// ✅ Key in lists — helps Compose track items
LazyColumn {
    items(users, key = { it.id }) { user ->
        UserCard(user)
    }
}

// ✅ Key around conditional content
@Composable
fun SwitchContent(screen: Screen) {
    when (screen) {
        Screen.Home -> key("home") { HomeContent() }
        Screen.Detail -> key("detail") { DetailContent() }
    }
}

// ✅ Key around items that change position
Row {
    items.forEach { item ->
        key(item.id) {
            ItemCard(item)
        }
    }
}

// ❌ Bad — no key, Compose can't track items
LazyColumn {
    items(users) { user ->
        UserCard(user)
    }
}
```

---

## Q6: How do you profile recomposition?

```kotlin
// 1. Layout Inspector — "Show recomposition counts"
// Android Studio → View → Tool Windows → Layout Inspector
// Enable "Show recomposition counts"

// 2. Modifier.recomposeHighlighter (debug)
@Composable
fun DebugRecomposition() {
    Text("Hello", modifier = Modifier.recomposeHighlighter())
    // Highlights when this composable recomposes
}

// 3. Modifier.recomposeHighlighter in debug builds
inline fun Modifier.recomposeHighlighter(): Modifier = composed {
    if (BuildConfig.DEBUG) {
        Modifier.drawBehind {
            drawRect(color = Color.Red.copy(alpha = 0.3f))
        }
    } else {
        Modifier
    }
}

// 4. Compose Compiler Metrics
// build.gradle:
// composeCompiler { reportsDestination = file("compose_reports") }
// Check: app/compose_reports/*_classes.txt for stability info
```

### Recomposition Checklist
```
✅ Use stable types (val data classes, @Immutable)
✅ Use keys in LazyColumn
✅ Defer state reads (offset {}, drawBehind {})
✅ Use derivedStateOf for computed values
✅ Extract stateless composables
✅ Avoid creating objects in composition
✅ Use remember for expensive computations
✅ Use const modifiers where possible
```

---

## Q7: How do you optimize image loading in Compose?

```kotlin
// Use Coil for image loading
// build.gradle: implementation("io.coil-kt:coil-compose:2.5.0")

@Composable
fun OptimizedImage(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .size(200, 200)  // Resize before loading
            .memoryCacheKey(url)
            .build(),
        contentDescription = "Image",
        modifier = Modifier.size(200.dp),
        contentScale = ContentScale.Crop,
    )
}

// With placeholder and error
AsyncImage(
    model = url,
    contentDescription = null,
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error),
    fallback = painterResource(R.drawable.fallback),
    modifier = Modifier.clip(CircleShape),
)

// SubcomposeAsync — custom loading state
SubcomposeAsyncImage(
    model = url,
    contentDescription = null,
    loading = { CircularProgressIndicator() },
    error = { Text("Error") },
    success = { state ->
        Image(painter = state.painter, contentDescription = null)
    },
)
```

### Image Optimization
```
✅ Resize images before loading (.size())
✅ Use memory cache keys
✅ Set contentScale to avoid unnecessary decoding
✅ Use placeholder for perceived performance
✅ Use contentDescription = null for decorative images
✅ Avoid loading full-resolution images for thumbnails
```

---

## 🔗 Related Topics
- [Lists](../intermediate/Lists.md)
- [Effects](../intermediate/Effects.md)
- [Internals](Internals.md)
