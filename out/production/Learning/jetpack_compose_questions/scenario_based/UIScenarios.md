# UI Scenarios

## Scenario 1: Responsive Layout (Phone/Tablet)

**Problem:** App should show single column on phone, two columns on tablet.

**Solution:**
```kotlin
@Composable
fun ResponsiveScreen(items: List<Item>) {
    BoxWithConstraints {
        if (maxWidth < 600.dp) {
            // Phone — single column
            LazyColumn {
                items(items, key = { it.id }) { ItemCard(it) }
            }
        } else {
            // Tablet — master-detail
            Row {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items, key = { it.id }) { ItemCard(it) }
                }
                DetailPane(modifier = Modifier.weight(1.5f))
            }
        }
    }
}

// Using WindowSizeClass
@Composable
fun AdaptiveScreen(windowSize: WindowSizeClass, items: List<Item>) {
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> PhoneLayout(items)
        WindowWidthSizeClass.Medium -> TabletLayout(items)
        WindowWidthSizeClass.Expanded -> DesktopLayout(items)
        else -> PhoneLayout(items)
    }
}
```

---

## Scenario 2: Custom Bottom Sheet

**Problem:** Create a bottom sheet with drag handle, dynamic height, and dismiss.

**Solution:**
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheet(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { DragHandle() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun DragHandle() {
    Box(
        Modifier
            .padding(vertical = 8.dp)
            .width(32.dp)
            .height(4.dp)
            .background(Color.Gray, CircleShape)
    )
}

// Usage
@Composable
fun ProductScreen() {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    if (showSheet) {
        CustomBottomSheet(onDismiss = { showSheet = false }) {
            Text("Product Details")
            Button(onClick = { showSheet = false }) { Text("Close") }
        }
    }
}
```

---

## Scenario 3: Custom Dialog with Form

**Problem:** Show a dialog with a form, validate input, and return result.

**Solution:**
```kotlin
@Composable
fun EditNameDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initialName) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Name") },
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        error = "Name cannot be empty"
                    } else if (name.length < 2) {
                        error = "Name too short"
                    } else {
                        onConfirm(name)
                    }
                },
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

// Usage
@Composable
fun ProfileScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Alice") }

    if (showDialog) {
        EditNameDialog(
            initialName = userName,
            onConfirm = { newName -> userName = newName; showDialog = false },
            onDismiss = { showDialog = false },
        )
    }

    Button(onClick = { showDialog = true }) { Text("Edit Name") }
}
```

---

## Scenario 4: Collapsing Toolbar on Scroll

**Problem:** Toolbar collapses when user scrolls down, expands on scroll up.

**Solution:**
```kotlin
@Composable
fun CollapsingToolbarScreen(title: String, content: List<String>) {
    val scrollState = rememberLazyListState()
    val collapsedFraction by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 1f
            else (scrollState.firstVisibleItemScrollOffset / 300f).coerceIn(0f, 1f)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = collapsedFraction > 0.5f,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) { Text(title) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = collapsedFraction),
                ),
            )
        },
    ) { padding ->
        LazyColumn(state = scrollState, modifier = Modifier.padding(padding)) {
            item {
                Text(
                    title,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(16.dp),
                )
            }
            items(content) { Text(it, modifier = Modifier.padding(16.dp)) }
        }
    }
}
```

---

## Scenario 5: Swipe to Dismiss List Item

**Problem:** Allow swipe-to-delete on list items with undo.

**Solution:**
```kotlin
@Composable
fun SwipeableList(
    items: List<Item>,
    onDismiss: (Item) -> Unit,
) {
    LazyColumn {
        items(items, key = { it.id }) { item ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.EndToStart) {
                        onDismiss(item)
                        true
                    } else false
                },
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .padding(end = 24.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                },
            ) {
                Surface {
                    ListItem(
                        headlineContent = { Text(item.name) },
                        supportingContent = { Text(item.description) },
                    )
                }
            }
        }
    }
}
```

---

## Scenario 6: Custom Animated Button

**Problem:** Create a button with press animation, loading state, and success checkmark.

**Solution:**
```kotlin
@Composable
fun AnimatedSubmitButton(
    isLoading: Boolean,
    isSuccess: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    Button(
        onClick = onClick,
        enabled = !isLoading && !isSuccess,
        interactionSource = interactionSource,
        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
    ) {
        AnimatedContent(targetState = when {
            isLoading -> ButtonState.Loading
            isSuccess -> ButtonState.Success
            else -> ButtonState.Idle
        }, label = "button") { state ->
            when (state) {
                ButtonState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
                ButtonState.Success -> Icon(Icons.Default.Check, contentDescription = "Success")
                ButtonState.Idle -> Text("Submit")
            }
        }
    }
}

enum class ButtonState { Idle, Loading, Success }
```

---

## 🔗 Related Topics
- [Layouts](../beginner/Layouts.md)
- [Animations](../intermediate/Animations.md)
- [Custom Layouts](../advanced/CustomLayouts.md)
