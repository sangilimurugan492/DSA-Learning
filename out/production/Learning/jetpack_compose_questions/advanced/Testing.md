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

## Q8: How do you test navigation in Compose?

```kotlin
// Test Navigation Compose with TestNavHostController
class NavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        composeRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            AppNavHost(navController = navController)
        }
    }

    @Test
    fun `start destination is home`() {
        composeRule.onNodeWithText("Home Screen").assertIsDisplayed()
    }

    @Test
    fun `navigate to detail on click`() {
        composeRule.onNodeWithText("Go to Detail").performClick()
        composeRule.onNodeWithText("Detail Screen").assertIsDisplayed()
    }

    @Test
    fun `verify current route after navigation`() {
        composeRule.onNodeWithText("Go to Detail").performClick()
        assertThat(navController.currentDestination?.route).isEqualTo("detail")
    }

    @Test
    fun `back button pops back stack`() {
        composeRule.onNodeWithText("Go to Detail").performClick()
        composeRule.onNodeWithText("Detail Screen").assertIsDisplayed()

        // Simulate back press
        composeRule.onNodeWithContentDescription("Back").performClick()

        composeRule.onNodeWithText("Home Screen").assertIsDisplayed()
        assertThat(navController.currentDestination?.route).isEqualTo("home")
    }
}

// AppNavHost for testing
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            Column {
                Text("Home Screen")
                Button(onClick = { navController.navigate("detail") }) {
                    Text("Go to Detail")
                }
            }
        }
        composable("detail") {
            Column {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Detail Screen")
            }
        }
    }
}
```

> **Key:** Use `TestNavHostController` to verify navigation state in tests. Assert on both UI (text displayed) and navigation state (current route) for complete coverage.

---

## Q9: How do you test with screenshot testing (Paparazzi)?

```kotlin
// Paparazzi — screenshot testing without emulator/device
// build.gradle: appTestImplementation 'app.cash.paparazzi:app.cash.paparazzi:1.3.0'

class ScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar",
    )

    @Test
    fun `home screen light mode`() {
        paparazzi.snapshot {
            AppTheme(darkTheme = false) {
                HomeScreen()
            }
        }
    }

    @Test
    fun `home screen dark mode`() {
        paparazzi.snapshot {
            AppTheme(darkTheme = true) {
                HomeScreen()
            }
        }
    }

    @Test
    fun `loading state`() {
        paparazzi.snapshot {
            AppTheme {
                LoadingScreen()
            }
        }
    }

    @Test
    fun `error state`() {
        paparazzi.snapshot {
            AppTheme {
                ErrorScreen(message = "Network error", onRetry = {})
            }
        }
    }

    @Test
    fun `different screen sizes`() {
        // Test with different device configs
        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_5)
        paparazzi.snapshot { HomeScreen() }

        paparazzi.unsafeUpdateConfig(deviceConfig = DeviceConfig.PIXEL_TABLET)
        paparazzi.snapshot { HomeScreen() }
    }
}

// Shot (alternative) — uses connected device
class ShotTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `matches screenshot`() {
        composeRule.setContent {
            AppTheme { HomeScreen() }
        }
        compareScreenshot(composeRule, name = "home_screen")
    }
}
```

| Tool | Needs Device? | CI Friendly | Speed |
|------|--------------|-------------|-------|
| Paparazzi | ❌ No | ✅ Yes | ✅ Fast |
| Shot | ✅ Yes | ⚠️ Needs emulator | ⚠️ Slower |
| Roborazzi | ❌ No | ✅ Yes | ✅ Fast |

> **Best Practice:** Use screenshot tests for visual regression — catch unintended UI changes automatically. Test key states (loading, error, empty, content) and both light/dark themes. Run in CI to prevent visual regressions.

---

## Q10: How do you test performance and recomposition?

```kotlin
// Compose has built-in recomposition tracking
// build.gradle: debugImplementation 'androidx.compose.runtime:runtime-tracing:1.0.0-beta01'

class RecompositionTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `counter only recomposes Text not Button`() {
        var recompositions = 0

        composeRule.setContent {
            var count by remember { mutableStateOf(0) }

            // Track recompositions
            RecomposeTracker {
                recompositions++
            }

            Column {
                Text("$count", modifier = Modifier.testTag("count"))
                Button(onClick = { count++ }, modifier = Modifier.testTag("button")) {
                    Text("Increment")
                }
            }
        }

        // Initial composition
        assertThat(recompositions).isEqualTo(1)

        // Click button — only Text should recompose
        composeRule.onNodeWithTag("button").performClick()

        // Verify count updated
        composeRule.onNodeWithTag("count").assertTextEquals("1")
    }

    @Test
    fun `stable composable skips recomposition`() {
        data class User(val name: String)  // Stable (val properties)

        var recompositions by remember { mutableStateOf(0) }

        composeRule.setContent {
            val user = remember { User("Alice") }
            UserCard(user = user, onRecompose = { recompositions++ })
        }

        // Trigger recomposition
        composeRule.onNodeWithTag("refresh").performClick()

        // UserCard should NOT recompose — input is stable
        assertThat(recompositions).isEqualTo(0)
    }

    @Test
    fun `verify no infinite recomposition`() {
        composeRule.setContent {
            var state by remember { mutableStateOf(0) }
            // If this causes infinite recomposition, test will timeout
            LaunchedEffect(Unit) { state++ }
            Text("$state")
        }
        composeRule.waitForIdle()
        // If we get here, no infinite loop
    }
}

// Helper to track recompositions
@Composable
fun RecomposeTracker(onRecompose: () -> Unit) {
    SideEffect { onRecompose() }
}

// Performance annotations
@Composable
fun OptimizedList(items: List<Item>) {
    // @Immutable — tells compiler this type is stable
    // @Stable — tells compiler this type is stable (but may change)
    LazyColumn {
        items(items, key = { it.id }) { item ->
            // Only recomposes when item content changes
            ItemRow(item)
        }
    }
}
```

| Annotation | Meaning | Use Case |
|-----------|---------|----------|
| `@Immutable` | All fields are `val`, never changes | Data classes with only `val` |
| `@Stable` | Can change but changes are observable | Classes with `mutableStateOf` fields |
| `@Composable` | Function is a composable | UI functions |

> **Key:** Use `@Immutable` or `@Stable` annotations to help the Compose compiler skip unnecessary recompositions. A data class with all `val` properties is automatically stable, but classes with `List<T>` or `Map<K,V>` parameters need `@Immutable` since those interfaces aren't stable.

---

## 🔗 Related Topics
- [Architecture](Architecture.md)
- [State Management](../intermediate/StateManagement.md)
- [Effects](../intermediate/Effects.md)
