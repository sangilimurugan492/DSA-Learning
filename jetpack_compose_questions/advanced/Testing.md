# Testing

## Q1: How do you set up Compose testing?

```kotlin
// build.gradle
// androidTestImplementation "androidx.compose.ui:ui-test-junit4"
// debugImplementation "androidx.compose.ui:ui-test-manifest"

@RunWith(AndroidJUnit4::class)
class CounterTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `counter starts at zero`() {
        composeRule.setContent {
            Counter()
        }
        composeRule.onNodeWithText("0").assertExists()
    }
}
```

---

## Q2: How do you find and assert composables?

```kotlin
class MyScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `find by text`() {
        composeRule.setContent { MyScreen() }

        composeRule.onNodeWithText("Hello").assertExists()
        composeRule.onNodeWithText("Hello").assertIsDisplayed()
        composeRule.onNodeWithText("Submit").assertHasClickAction()
    }

    @Test
    fun `find by content description`() {
        composeRule.setContent { MyScreen() }

        composeRule.onNodeWithContentDescription("Add").assertExists()
    }

    @Test
    fun `find by test tag`() {
        composeRule.setContent {
            Text("Hello", modifier = Modifier.testTag("greeting"))
        }

        composeRule.onNodeWithTag("greeting").assertExists()
    }

    @Test
    fun `find by semantics`() {
        composeRule.setContent { MyScreen() }

        composeRule.onNodeWithContentDescription("Settings").assertExists()
        composeRule.onAllNodesWithText("Item").assertCountEquals(5)
    }

    @Test
    fun `assert no node exists`() {
        composeRule.setContent { MyScreen() }

        composeRule.onNodeWithText("Error").assertDoesNotExist()
    }
}
```

---

## Q3: How do you perform actions in tests?

```kotlin
@Test
fun `click button increments counter`() {
    composeRule.setContent { Counter() }

    composeRule.onNodeWithText("0").assertExists()

    composeRule.onNodeWithText("Increment").performClick()

    composeRule.onNodeWithText("1").assertExists()
}

@Test
fun `type text in TextField`() {
    composeRule.setContent { NameInput() }

    composeRule.onNodeWithText("Name").performTextInput("Alice")
    composeRule.onNodeWithText("Alice").assertExists()
}

@Test
fun `clear and type`() {
    composeRule.setContent { NameInput() }

    composeRule.onNodeWithText("Name").performTextClear()
    composeRule.onNodeWithText("Name").performTextInput("Bob")
}

@Test
fun `scroll to and click`() {
    composeRule.setContent { LongList() }

    composeRule.onNodeWithText("Item 50").performScrollTo().performClick()
}

@Test
fun `perform semantically defined action`() {
    composeRule.setContent { MyScreen() }

    composeRule.onNodeWithTag("toggle").performToggle()
}
```

---

## Q4: How do you test state changes?

```kotlin
@Test
fun `toggle changes text`() {
    composeRule.setContent {
        var expanded by remember { mutableStateOf(false) }
        Column {
            Button(onClick = { expanded = !expanded }) { Text("Toggle") }
            if (expanded) Text("Expanded!")
        }
    }

    composeRule.onNodeWithText("Expanded!").assertDoesNotExist()

    composeRule.onNodeWithText("Toggle").performClick()

    composeRule.onNodeWithText("Expanded!").assertExists()
}

@Test
fun `state hoisting test`() {
    var name = ""
    composeRule.setContent {
        NameInput(name = name, onNameChange = { name = it })
    }

    composeRule.onNodeWithText("Name").performTextInput("Alice")
    assertEquals("Alice", name)
}
```

---

## Q5: How do you test with ViewModel?

```kotlin
// Fake ViewModel
class FakeCounterViewModel : ViewModel() {
    var count = MutableStateFlow(0)
    fun increment() { count.value++ }
}

@Test
fun `viewmodel counter increments`() {
    val viewModel = FakeCounterViewModel()

    composeRule.setContent {
        val count by viewModel.count.collectAsStateWithLifecycle()
        Column {
            Text("$count", modifier = Modifier.testTag("count"))
            Button(onClick = viewModel::increment, modifier = Modifier.testTag("button")) {
                Text("Increment")
            }
        }
    }

    composeRule.onNodeWithTag("count").assertTextEquals("0")

    composeRule.onNodeWithTag("button").performClick()

    composeRule.onNodeWithTag("count").assertTextEquals("1")
}

// With Hilt
@HiltAndroidTest
class UserScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1) val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `displays user`() {
        composeRule.setContent {
            UserScreen(viewModel = hiltViewModel())
        }
        composeRule.onNodeWithText("Alice").assertExists()
    }
}
```

---

## Q6: How do you test animations?

```kotlin
@Test
fun `animated visibility shows content`() {
    composeRule.setContent {
        var visible by remember { mutableStateOf(false) }
        Column {
            Button(onClick = { visible = true }) { Text("Show") }
            AnimatedVisibility(visible) { Text("Content") }
        }
    }

    composeRule.onNodeWithText("Content").assertDoesNotExist()

    composeRule.onNodeWithText("Show").performClick()

    // Wait for animation
    composeRule.waitUntil(5000) {
        composeRule.onAllNodesWithText("Content").fetchSemanticsNodes().isNotEmpty()
    }

    composeRule.onNodeWithText("Content").assertIsDisplayed()
}

// Test with main clock
@Test
fun `animation auto advance`() {
    composeRule.setContent {
        val scale by animateFloatAsState(targetValue = 1f, label = "scale")
        Box(Modifier.graphicsLayer { scaleX = scale; scaleY = scale })
    }

    // Auto-advance animations
    composeRule.mainClock.autoAdvance = true
    composeRule.waitForIdle()
}
```

---

## Q7: How do you test semantics and accessibility?

```kotlin
@Test
fun `button has correct semantics`() {
    composeRule.setContent {
        Button(onClick = {}, modifier = Modifier.testTag("submit")) {
            Text("Submit")
        }
    }

    composeRule.onNodeWithTag("submit")
        .assertHasClickAction()
        .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Role))
}

@Test
fun `content description is set`() {
    composeRule.setContent {
        Icon(Icons.Default.Add, contentDescription = "Add item")
    }

    composeRule.onNodeWithContentDescription("Add item").assertExists()
}

@Test
fun `merged semantics`() {
    composeRule.setContent {
        Row(modifier = Modifier.clickable {} .semantics(mergeDescendants = true) {}) {
            Icon(Icons.Default.Person, contentDescription = null)
            Text("Alice")
        }
    }

    // Merged into one semantics node
    composeRule.onNodeWithContentDescription("Alice").assertExists()
}

// Custom semantics
@Composable
fun RatingBar(rating: Int) {
    Row(
        modifier = Modifier.semantics {
            contentDescription = "Rating: $rating out of 5"
        }
    ) {
        repeat(5) { Icon(Icons.Default.Star, contentDescription = null) }
    }
}
```

### Testing Best Practices
```
✅ Use testTag for reliable finding
✅ Test stateless composables (pass state in)
✅ Use fake/mock ViewModels
✅ Assert on visible output, not implementation
✅ Wait for async with waitUntil
✅ Test accessibility (contentDescription, semantics)
✅ Test user actions (click, type, scroll)
✅ Keep tests independent (no shared state)
```

---

## 🔗 Related Topics
- [Architecture](Architecture.md)
- [State Management](../intermediate/StateManagement.md)
- [Effects](../intermediate/Effects.md)
