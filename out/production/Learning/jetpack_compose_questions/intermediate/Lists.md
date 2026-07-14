# Lists

## Q1: How do you use LazyColumn and LazyRow?

```kotlin
// LazyColumn — vertical scrolling list
@Composable
fun SimpleList() {
    LazyColumn {
        items(100) { index ->
            Text("Item $index", modifier = Modifier.padding(8.dp))
        }
    }
}

// LazyRow — horizontal scrolling list
@Composable
fun HorizontalList() {
    LazyRow {
        items(50) { index ->
            Text("Item $index", modifier = Modifier.padding(8.dp))
        }
    }
}

// With list of data
@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserItem(user)
        }
    }
}

// With key — efficient recomposition
LazyColumn {
    items(users, key = { it.id }) { user ->
        UserItem(user)
    }
}

// itemsIndexed — need index
LazyColumn {
    itemsIndexed(users) { index, user ->
        Text("$index: ${user.name}")
    }
}
```

---

## Q2: How do you use LazyVerticalGrid?

```kotlin
// LazyVerticalGrid — grid layout
@Composable
fun PhotoGrid(photos: List<Photo>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),  // 3 columns
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoItem(photo)
        }
    }
}

// Adaptive columns — fit screen
LazyVerticalGrid(
    columns = GridCells.Adaptive(120.dp),  // Each item ~120dp
) {
    items(photos) { PhotoItem(it) }
}

// Fixed columns with span
LazyVerticalGrid(columns = GridCells.Fixed(2)) {
    item(span = { GridItemSpan(2) }) {  // Full-width header
        Text("Section Header")
    }
    items(photos) { PhotoItem(it) }
}
```

---

## Q3: How do you add headers, footers, and sticky headers?

```kotlin
LazyColumn {
    // Header
    item {
        Text("Header", style = MaterialTheme.typography.headlineMedium)
    }

    // Items
    items(data) { item ->
        TextItem(item)
    }

    // Footer
    item {
        Text("Footer", style = MaterialTheme.typography.bodySmall)
    }
}

// Sticky header — stays visible while scrolling
LazyColumn {
    stickyHeader {
        Text("Sticky Header", modifier = Modifier.background(Color.Yellow).fillMaxWidth())
    }
    items(100) {
        Text("Item $it")
    }
}

// Multiple sticky headers
LazyColumn {
    sections.forEach { section ->
        stickyHeader { SectionHeader(section.title) }
        items(section.items) { item ->
            TextItem(item)
        }
    }
}
```

---

## Q4: How do you handle list item animations?

```kotlin
// animateItem — animate item placement, add, remove
LazyColumn {
    items(items, key = { it.id }) { item ->
        Row(
            modifier = Modifier.animateItem(
                fadeInSpec = tween(300),
                fadeOutSpec = tween(300),
                placementSpec = spring(stiffness = Spring.StiffnessLow),
            ),
        ) {
            Text(item.name)
            IconButton(onClick = { viewModel.remove(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

// Swipe to dismiss
val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { value ->
        if (value == SwipeToDismissBoxValue.EndToStart) {
            viewModel.remove(item)
            true
        } else false
    },
)

SwipeToDismissBox(
    state = dismissState,
    backgroundContent = { /* red background with delete icon */ },
) {
    ListItem(item)
}
```

---

## Q5: How do you optimize LazyColumn performance?

```kotlin
// 1. Use key — helps Compose track items
LazyColumn {
    items(list, key = { it.id }) { item ->
        Item(item)
    }
}

// 2. contentType — reuse item compositions of same type
LazyColumn {
    items(list, key = { it.id }, contentType = { "item" }) { item ->
        Item(item)
    }
    item(contentType = "header") { Header() }
}

// 3. Avoid heavy computation in item composable
LazyColumn {
    items(list, key = { it.id }) { item ->
        // ❌ Bad — computed every recomposition
        val formatted = SimpleDateFormat("dd/MM/yyyy").format(item.date)

        // ✅ Good — precompute in ViewModel
        val formatted = item.formattedDate
        Text(formatted)
    }
}

// 4. Use derivedStateOf for scroll-based state
val listState = rememberLazyListState()
val showButton by remember {
    derivedStateOf { listState.firstVisibleItemIndex > 0 }
}

// 5. Fixed height for items (avoids measurement)
LazyColumn {
    items(list) { item ->
        Text(item.name, modifier = Modifier.height(48.dp))
    }
}
```

---

## Q6: How do you implement pagination with LazyColumn?

```kotlin
// Using Paging 3 with Compose
// build.gradle: implementation "androidx.paging:paging-compose:3.3.0"

@Composable
fun PagedList(pagingItems: LazyPagingItems<Item>) {
    LazyColumn {
        item {
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator()
            }
        }

        items(pagingItems) { item ->
            item?.let { ItemRow(it) }
        }

        item {
            if (pagingItems.loadState.append is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}

// ViewModel
class ItemViewModel : ViewModel() {
    val items = Pager(PagingConfig(pageSize = 20)) {
        ItemPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}

// Screen
@Composable
fun ItemScreen(viewModel: ItemViewModel = viewModel()) {
    val pagingItems = viewModel.items.collectAsLazyPagingItems()
    PagedList(pagingItems)
}
```

---

## Q7: How do you handle pull-to-refresh?

```kotlin
@Composable
fun RefreshableList() {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                viewModel.refresh()
                isRefreshing = false
            }
        },
    ) {
        LazyColumn {
            items(data) { item ->
                TextItem(item)
            }
        }
    }
}

// With state
val state = rememberPullToRefreshState()

PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = { /* refresh */ },
    state = state,
) {
    LazyColumn { /* items */ }
}
```

### List Best Practices
```
✅ Always use key for items (unique ID)
✅ Use contentType for mixed item types
✅ Precompute data in ViewModel (not in composable)
✅ Use derivedStateOf for scroll-based state
✅ Use animateItem for add/remove/move
✅ Use Paging 3 for large datasets
✅ Avoid 0.dp height items (causes measurement issues)
✅ Use contentPadding for edge spacing
```

---

## 🔗 Related Topics
- [Layouts](../beginner/Layouts.md)
- [Animations](Animations.md)
- [Performance](../advanced/Performance.md)
