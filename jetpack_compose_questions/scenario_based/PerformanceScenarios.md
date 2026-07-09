# Performance Scenarios

## Scenario 1: Excessive Recomposition in List

**Problem:** A LazyColumn with 100 items recomposes all items when one item changes.

**Root Cause:** No `key` parameter, unstable parameters, or reading state in parent.

**Solution:**
```kotlin
// ❌ Bad — no key, unstable List parameter
@Composable
fun BadList(items: List<Item>) {
    LazyColumn {
        items(items) { item ->  // No key
            ItemRow(item)  // List<Item> is unstable
        }
    }
}

// ✅ Good — key + stable wrapper
@Immutable
data class ItemList(val items: List<Item>)

@Composable
fun GoodList(itemList: ItemList) {
    LazyColumn {
        items(itemList.items, key = { it.id }, contentType = { "item" }) { item ->
            ItemRow(item)
        }
    }
}

// ✅ Good — item is stateless, only recomposes changed item
@Composable
fun ItemRow(item: Item) {  // Item is stable data class
    Text(item.name)
}
```

---

## Scenario 2: Scroll Causes Full Recomposition

**Problem:** Scrolling a LazyColumn causes the entire screen to recompose.

**Root Cause:** Reading scroll state in parent composable.

**Solution:**
```kotlin
// ❌ Bad — parent reads scroll state
@Composable
fun BadScreen() {
    val listState = rememberLazyListState()
    val index = listState.firstVisibleItemIndex  // Parent recomposes on scroll!

    Column {
        Text("Scroll position: $index")  // This causes full recomposition
        LazyColumn(state = listState) { /* ... */ }
    }
}

// ✅ Good — defer read to child only
@Composable
fun GoodScreen() {
    val listState = rememberLazyListState()

    Column {
        ScrollHeader(listState)  // Only this recomposes
        LazyColumn(state = listState) { /* ... */ }
    }
}

@Composable
fun ScrollHeader(listState: LazyListState) {
    val index by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    Text("Scroll position: $index")  // Only this widget recomposes
}

// ✅ Good — defer to layout phase
@Composable
fun ScrollOffset(listState: LazyListState) {
    Box(Modifier.offset { IntOffset(0, listState.firstVisibleItemScrollOffset) })
    // No recomposition — only relayout
}
```

---

## Scenario 3: Image Loading Causes Jank

**Problem:** Loading large images in a grid causes frame drops.

**Solution:**
```kotlin
// ❌ Bad — loading full resolution
@Composable
fun BadImage(url: String) {
    AsyncImage(model = url, contentDescription = null, modifier = Modifier.size(100.dp))
}

// ✅ Good — resize, cache, placeholder
@Composable
fun OptimizedImage(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(200, 200)  // Resize before decoding
            .crossfade(true)
            .memoryCacheKey(url)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.placeholder),
    )
}

// ✅ Good — use derivedStateOf for scroll-based loading
@Composable
fun LazyGrid(photos: List<Photo>) {
    val gridState = rememberLazyGridState()
    val visibleItems by remember {
        derivedStateOf { gridState.layoutInfo.visibleItemsInfo }
    }

    LazyVerticalGrid(columns = GridCells.Fixed(3), state = gridState) {
        items(photos, key = { it.id }) { photo ->
            OptimizedImage(photo.thumbnailUrl)
        }
    }
}
```

---

## Scenario 4: Unstable Parameters Cause Recomposition

**Problem:** A composable with `List<String>` parameter recomposes even when list content is the same.

**Solution:**
```kotlin
// ❌ Bad — List is unstable
data class ScreenState(
    val items: List<String>,  // Unstable
    val count: Int,
)

@Composable
fun MyScreen(state: ScreenState) {  // ScreenState is unstable
    Column {
        Text("Count: ${state.count}")
        state.items.forEach { Text(it) }
    }
}

// ✅ Good — wrap in @Immutable
@Immutable
data class ScreenState(
    val items: ImmutableList<String>,  // kotlinx.collections.immutable
    val count: Int,
)

// ✅ Good — or use @Immutable wrapper
@Immutable
data class ItemList(val items: List<Item>)

@Composable
fun MyScreen(state: ScreenState) {  // Now stable and skippable
    Column {
        Text("Count: ${state.count}")
        state.items.forEach { Text(it) }
    }
}
```

---

## Scenario 5: Lambda Causes Recomposition

**Problem:** Passing a new lambda every recomposition causes child to recompose.

**Solution:**
```kotlin
// ❌ Bad — new lambda each recomposition
@Composable
fun BadParent(items: List<Item>) {
    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemRow(
                item = item,
                onClick = { handleClick(item.id) },  // New lambda each time!
            )
        }
    }
}

// ✅ Good — use remember for lambda
@Composable
fun GoodParent(items: List<Item>) {
    val onClick: (String) -> Unit = remember { { id -> handleClick(id) } }

    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemRow(item = item, onClick = onClick)
        }
    }
}

// ✅ Good — pass only the ID, let child handle click
@Composable
fun ItemRow(item: Item, onClick: (String) -> Unit) {
    Row(modifier = Modifier.clickable { onClick(item.id) }) {
        Text(item.name)
    }
}
```

---

## Scenario 6: Heavy Computation in Composition

**Problem:** Formatting dates in composable causes jank.

**Solution:**
```kotlin
// ❌ Bad — computation in composition
@Composable
fun BadDateItem(item: Item) {
    val formattedDate = SimpleDateFormat("dd/MM/yyyy").format(item.date)  // Every recomposition!
    Text("${item.name} - $formattedDate")
}

// ✅ Good — precompute in ViewModel
data class ItemUiModel(
    val id: String,
    val name: String,
    val formattedDate: String,  // Pre-formatted
)

// ✅ Good — use remember with key
@Composable
fun GoodDateItem(item: Item) {
    val formattedDate = remember(item.date) {
        SimpleDateFormat("dd/MM/yyyy").format(item.date)
    }
    Text("${item.name} - $formattedDate")
}

// ✅ Good — use derivedStateOf
@Composable
fun DerivedDateItem(item: Item) {
    val formattedDate by remember {
        derivedStateOf { SimpleDateFormat("dd/MM/yyyy").format(item.date) }
    }
    Text("${item.name} - $formattedDate")
}
```

---

## 🔗 Related Topics
- [Performance](../advanced/Performance.md)
- [Lists](../intermediate/Lists.md)
- [Internals](../advanced/Internals.md)
