# Compose Testing

## Q1: How do you set up Compose testing?

```gradle
androidTestImplementation 'androidx.compose.ui:ui-test-junit4:1.6.0'
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
debugImplementation 'androidx.compose.ui:ui-test-manifest:1.6.0'
```

### Basic test
```kotlin
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `login button is displayed`() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule
            .onNodeWithText("Login")
            .assertIsDisplayed()
    }
}
```

### createComposeRule vs createAndroidComposeRule
| Rule | Needs Activity? | Use Case |
|------|----------------|---------|
| `createComposeRule()` | ❌ No | Pure Compose tests |
| `createAndroidComposeRule<MainActivity>()` | ✅ Yes | Needs Activity context |

---

## Q2: How do you find composables?

```kotlin
@Test
fun `find by text`() {
    composeTestRule.setContent { MyScreen() }

    // By text
    composeTestRule.onNodeWithText("Login").performClick()

    // By text (ignore case)
    composeTestRule.onNodeWithText("login", ignoreCase = true)

    // By content description
    composeTestRule.onNodeWithContentDescription("Back button").performClick()

    // By test tag
    composeTestRule.onNodeWithTag("login_button").performClick()

    // By substring
    composeTestRule.onNodeWithText("Welcome", substring = true)

    // All nodes with text
    composeTestRule.onAllNodesWithText("Item")
}
```

### Using test tags
```kotlin
// Production code
@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.testTag("login_button")  // ← Tag
    ) {
        Text("Login")
    }
}

// Test
composeTestRule.onNodeWithTag("login_button").performClick()
```

### Finder comparison
| Finder | When to use |
|--------|------------|
| `onNodeWithText` | User-visible text |
| `onNodeWithTag` | No text (icons, images) |
| `onNodeWithContentDescription` | Accessibility |
| `onAllNodesWithText` | Multiple matches |

---

## Q3: How do you perform actions?

```kotlin
@Test
fun `perform actions on composables`() {
    composeTestRule.setContent { FormScreen() }

    // Click
    composeTestRule.onNodeWithText("Submit").performClick()

    // Type text
    composeTestRule.onNodeWithTag("email_field")
        .performTextInput("user@test.com")

    // Replace text
    composeTestRule.onNodeWithTag("name_field")
        .performTextReplacement("Alice")

    // Clear text
    composeTestRule.onNodeWithTag("name_field")
        .performTextClear()

    // Scroll to
    composeTestRule.onNodeWithText("Bottom Button")
        .performScrollTo()

    // Click with key event
    composeTestRule.onNodeWithTag("search_field")
        .performKeyPress(KeyEvent(KeyEvent.KEYCODE_ENTER))
}
```

### Common actions
| Action | Description |
|--------|-------------|
| `performClick()` | Tap on composable |
| `performTextInput(text)` | Type into TextField |
| `performTextReplacement(text)` | Replace text |
| `performTextClear()` | Clear text |
| `performScrollTo()` | Scroll to composable |
| `performKeyPress(key)` | Press a key |

---

## Q4: How do you assert composable state?

```kotlin
@Test
fun `assert composable state`() {
    composeTestRule.setContent { MyScreen() }

    // Exists
    composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    composeTestRule.onNodeWithText("Error").assertDoesNotExist()

    // Enabled / disabled
    composeTestRule.onNodeWithText("Submit").assertIsEnabled()
    composeTestRule.onNodeWithText("Submit").assertIsNotEnabled()

    // Selected
    composeTestRule.onNodeWithText("Tab 1").assertIsSelected()
    composeTestRule.onNodeWithText("Tab 2").assertIsNotSelected()

    // Has text
    composeTestRule.onNodeWithTag("title")
        .assertTextEquals("Welcome")

    // Has content description
    composeTestRule.onNodeWithTag("icon")
        .assertContentDescriptionEquals("Settings")

    // Count
    composeTestRule.onAllNodesWithText("Item").assertCountEquals(5)
}
```

### Common assertions
| Assertion | Description |
|-----------|-------------|
| `assertIsDisplayed()` | Visible on screen |
| `assertDoesNotExist()` | Not in tree |
| `assertIsEnabled()` | Enabled state |
| `assertIsSelected()` | Selected state |
| `assertTextEquals(text)` | Has exact text |
| `assertCountEquals(n)` | N matching nodes |

---

## Q5: How do you test state changes?

```kotlin
@Test
fun `clicking button changes text`() {
    composeTestRule.setContent {
        var text by remember { mutableStateOf("Hello") }
        Column {
            Text(text, modifier = Modifier.testTag("text"))
            Button(onClick = { text = "World" }) {
                Text("Click")
            }
        }
    }

    // Initial state
    composeTestRule.onNodeWithTag("text").assertTextEquals("Hello")

    // Click button
    composeTestRule.onNodeWithText("Click").performClick()

    // Verify state changed
    composeTestRule.onNodeWithTag("text").assertTextEquals("World")
}

@Test
fun `toggle switches state`() {
    composeTestRule.setContent {
        var checked by remember { mutableStateOf(false) }
        Switch(checked = checked, onCheckedChange = { checked = it })
    }

    composeTestRule.onNode(SwitchToggleableRole).assertIsOff()
    composeTestRule.onNode(SwitchToggleableRole).performClick()
    composeTestRule.onNode(SwitchToggleableRole).assertIsOn()
}
```

---

## Q6: How do you test LazyColumn / LazyRow?

```kotlin
@Test
fun `lazy column displays all items`() {
    val items = listOf("Item 1", "Item 2", "Item 3")

    composeTestRule.setContent {
        LazyColumn {
            items(items) { item ->
                Text(item, modifier = Modifier.testTag("item_${items.indexOf(item)}"))
            }
        }
    }

    // Verify first item
    composeTestRule.onNodeWithTag("item_0").assertTextEquals("Item 1")

    // Scroll to last item
    composeTestRule.onNodeWithTag("item_2").performScrollTo().assertIsDisplayed()

    // Count visible items
    composeTestRule.onAllNodes(hasTestTag("item_")).assertCountEquals(3)
}
```

### Lazy list testing tips
- Use unique test tags per item
- Use `performScrollTo()` for off-screen items
- Don't assume all items are composed (lazy!)

---

## Q7: How do you test ViewModel with Compose?

```kotlin
class UserViewModel : ViewModel() {
    var state by mutableStateOf<UiState>(UiState.Loading)
        private set

    fun loadUser() {
        state = UiState.Success(User("Alice"))
    }
}

@Test
fun `viewmodel state updates UI`() {
    val viewModel = UserViewModel()

    composeTestRule.setContent {
        when (val state = viewModel.state) {
            is UiState.Loading -> Text("Loading...")
            is UiState.Success -> Text(state.user.name)
            is UiState.Error -> Text("Error")
        }
    }

    // Initially loading
    composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()

    // Load user
    viewModel.loadUser()

    // Verify success state
    composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
}
```

---

## Q8: How do you test navigation in Compose?

```kotlin
@Test
fun `navigating from login to home`() {
    composeTestRule.setContent {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "login") {
            composable("login") {
                LoginScreen(onLogin = { navController.navigate("home") })
            }
            composable("home") {
                Text("Home Screen", modifier = Modifier.testTag("home"))
            }
        }
    }

    // On login screen
    composeTestRule.onNodeWithText("Login").assertIsDisplayed()

    // Click login
    composeTestRule.onNodeWithText("Login").performClick()

    // On home screen
    composeTestRule.onNodeWithTag("home").assertIsDisplayed()
}
```

---

## Q9: How do you test text input and validation?

```kotlin
@Test
fun `email validation shows error`() {
    composeTestRule.setContent { LoginScreen() }

    // Type invalid email
    composeTestRule.onNodeWithTag("email_field")
        .performTextInput("invalid-email")

    // Click login
    composeTestRule.onNodeWithText("Login").performClick()

    // Error message shown
    composeTestRule.onNodeWithText("Invalid email").assertIsDisplayed()
}

@Test
fun `valid email does not show error`() {
    composeTestRule.setContent { LoginScreen() }

    composeTestRule.onNodeWithTag("email_field")
        .performTextInput("user@test.com")
    composeTestRule.onNodeWithText("Login").performClick()

    composeTestRule.onNodeWithText("Invalid email").assertDoesNotExist()
}
```

---

## Q10: How do you test animations?

```kotlin
@Test
fun `animated visibility shows content`() {
    composeTestRule.setContent {
        var visible by remember { mutableStateOf(false) }
        Column {
            Button(onClick = { visible = true }) { Text("Show") }
            AnimatedVisibility(visible) {
                Text("Hidden Text", modifier = Modifier.testTag("hidden"))
            }
        }
    }

    // Initially hidden
    composeTestRule.onNodeWithTag("hidden").assertDoesNotExist()

    // Click show
    composeTestRule.onNodeWithText("Show").performClick()

    // Wait for animation
    composeTestRule.waitForIdle()

    // Now visible
    composeTestRule.onNodeWithTag("hidden").assertIsDisplayed()
}
```

### Animation testing tips
- Use `composeTestRule.waitForIdle()` to wait for animations
- Disable animations for faster tests
- Test final state, not intermediate frames

---

## Q11: How do you test custom semantics?

```kotlin
// Production code — add custom semantics
@Composable
fun RatingBar(rating: Int) {
    Row(
        modifier = Modifier.semantics {
            contentDescription = "Rating: $rating out of 5"
            testTagsAsResourceId = true
        }
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                modifier = Modifier.testTag("star_$index")
            )
        }
    }
}

// Test
@Test
fun `rating bar shows correct stars`() {
    composeTestRule.setContent { RatingBar(rating = 3) }

    // Check content description
    composeTestRule.onNodeWithContentDescription("Rating: 3 out of 5")
        .assertIsDisplayed()

    // Check individual stars
    composeTestRule.onNodeWithTag("star_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star_2").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star_3").assertIsDisplayed()
    composeTestRule.onNodeWithTag("star_4").assertIsDisplayed()
}
```

---

## Q12: How do you test dialogs and bottom sheets?

```kotlin
@Test
fun `dialog is shown and dismissed`() {
    composeTestRule.setContent {
        var showDialog by remember { mutableStateOf(false) }
        Column {
            Button(onClick = { showDialog = true }) { Text("Open") }
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirm") },
                    text = { Text("Delete item?") },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }

    // Open dialog
    composeTestRule.onNodeWithText("Open").performClick()

    // Dialog visible
    composeTestRule.onNodeWithText("Delete item?").assertIsDisplayed()

    // Confirm
    composeTestRule.onNodeWithText("Yes").performClick()

    // Dialog dismissed
    composeTestRule.onNodeWithText("Delete item?").assertDoesNotExist()
}
```

---

## Q13: How do you test with dependencies (DI)?

```kotlin
// Use fake dependencies in tests
class FakeUserRepository : UserRepository {
    var userToReturn = User("Alice")
    var throwException = false

    override suspend fun getUser(id: String): User {
        if (throwException) throw RuntimeException("Error")
        return userToReturn
    }
}

@Test
fun `screen shows user from repository`() {
    val fakeRepo = FakeUserRepository()
    val viewModel = UserViewModel(fakeRepo)

    composeTestRule.setContent {
        UserScreen(viewModel = viewModel)
    }

    composeTestRule.onNodeWithText("Alice").assertIsDisplayed()
}

@Test
fun `screen shows error on failure`() {
    val fakeRepo = FakeUserRepository().apply { throwException = true }
    val viewModel = UserViewModel(fakeRepo)

    composeTestRule.setContent {
        UserScreen(viewModel = viewModel)
    }

    composeTestRule.onNodeWithText("Error").assertIsDisplayed()
}
```

---

## Q14: How do you test gestures (swipe, drag)?

```kotlin
@Test
fun `swipe between tabs`() {
    composeTestRule.setContent {
        val pagerState = rememberPagerState { 3 }
        HorizontalPager(state = pagerState) { page ->
            Text("Page $page", modifier = Modifier.testTag("page_$page"))
        }
    }

    // Page 0 is visible
    composeTestRule.onNodeWithTag("page_0").assertIsDisplayed()

    // Swipe left
    composeTestRule.onRoot().performTouchInput {
        swipeLeft()
    }

    // Page 1 is visible
    composeTestRule.onNodeWithTag("page_1").assertIsDisplayed()
}

@Test
fun `long press shows context menu`() {
    composeTestRule.setContent { ItemList() }

    composeTestRule.onNodeWithTag("item_0")
        .performTouchInput { longClick() }

    composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
}
```

### Gesture actions
| Action | Description |
|--------|-------------|
| `swipeLeft()` | Swipe left |
| `swipeRight()` | Swipe right |
| `swipeUp()` | Swipe up |
| `swipeDown()` | Swipe down |
| `longClick()` | Long press |
| `click()` | Tap |
| `doubleClick()` | Double tap |

---

## Q15: What are Compose testing best practices?

### Do's
- ✅ Use `testTag` for elements without text
- ✅ Test behavior, not implementation
- ✅ Use `waitForIdle()` after animations
- ✅ Inject fake dependencies
- ✅ Test state transitions
- ✅ Use `assertDoesNotExist()` for absence

### Don'ts
- ❌ Don't test Compose internals (layout nodes)
- ❌ Don't use `Thread.sleep()` — use `waitForIdle()`
- ❌ Don't rely on item position in LazyColumn
- ❌ Don't test multiple features in one test
- ❌ Don't use real network in tests

### Test structure
```kotlin
@Test
fun `login with valid credentials navigates to home`() {
    // Arrange — set up content
    composeTestRule.setContent {
        LoginScreen(onLoginSuccess = { /* track */ })
    }

    // Act — perform user actions
    composeTestRule.onNodeWithTag("email_field")
        .performTextInput("user@test.com")
    composeTestRule.onNodeWithTag("password_field")
        .performTextInput("password")
    composeTestRule.onNodeWithText("Login").performClick()

    // Assert — verify result
    composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
}
```

### Compose vs Espresso
| Feature | Compose Testing | Espresso |
|---------|----------------|----------|
| View system | Compose | View |
| Finders | `onNodeWithTag` | `onView(withId)` |
| Auto-wait | ✅ Yes | Needs IdlingResource |
| Speed | Fast | Slower |
| Animations | Auto-handled | Need to disable |

---

## 🔗 Related Topics
- [Unit Testing](UnitTesting.md)
- [Espresso](Espresso.md)
- [Testing Scenarios](TestingScenarios.md)
