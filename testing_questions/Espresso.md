# Espresso (UI Testing)

## Q1: What is Espresso?

Espresso is Google's UI testing framework for Android. It tests the app as a real user would — launching activities, clicking buttons, typing text.

### Setup
```gradle
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
androidTestImplementation 'androidx.test:runner:1.5.2'
androidTestImplementation 'androidx.test:rules:1.5.0'
```

### Espresso basics
```kotlin
@Test
fun `login flow test`() {
    // Find view → perform action → check result
    onView(withId(R.id.emailField))
        .perform(typeText("user@test.com"))

    onView(withId(R.id.passwordField))
        .perform(typeText("password"))

    onView(withId(R.id.loginButton))
        .perform(click())

    onView(withId(R.id.welcomeText))
        .check(matches(isDisplayed()))
}
```

### Espresso flow
```
onView(ViewMatcher) → .perform(ViewAction) → .check(ViewAssertion)
```

---

## Q2: What are ViewMatchers?

ViewMatchers find views in the view hierarchy.

```kotlin
// By ID
onView(withId(R.id.loginButton))

// By text
onView(withText("Login"))
onView(withText(R.string.login))

// By hint
onView(withHint("Enter email"))

// By content description
onView(withContentDescription("Back"))

// By visibility
onView(isDisplayed())
onView(isCompletelyDisplayed())

// By class
onView(instanceOf(EditText::class.java))

// Combine matchers
onView(allOf(
    withId(R.id.button),
    isDisplayed(),
    isEnabled()
))

// Any of
onView(anyOf(
    withText("Login"),
    withText("Sign In")
))

// With parent
onView(withParent(withId(R.id.container)))

// Has sibling
onView(hasSibling(withText("Alice")))
```

### Common matchers
| Matcher | Description |
|---------|-------------|
| `withId(id)` | By resource ID |
| `withText(text)` | By displayed text |
| `withHint(text)` | By hint text |
| `isDisplayed()` | Visible on screen |
| `isEnabled()` | Enabled state |
| `isChecked()` | Checkbox checked |
| `hasFocus()` | View has focus |
| `withContentDescription(desc)` | By accessibility text |

---

## Q3: What are ViewActions?

ViewActions simulate user interactions.

```kotlin
// Click
onView(withId(R.id.button)).perform(click())
onView(withId(R.id.item)).perform(longClick())

// Type text
onView(withId(R.id.editText)).perform(typeText("Hello"))
onView(withId(R.id.editText)).perform(replaceText("Hello"))

// Clear text
onView(withId(R.id.editText)).perform(clearText())

// Scroll
onView(withId(R.id.scrollView)).perform(scrollTo())
onView(withId(R.id.recyclerView)).perform(scrollToPosition(5))

// Swipe
onView(withId(R.id.viewPager)).perform(swipeLeft())
onView(withId(R.id.recyclerView)).perform(swipeUp())

// Close keyboard
onView(withId(R.id.editText)).perform(closeSoftKeyboard())

// Multiple actions
onView(withId(R.id.editText))
    .perform(clearText(), typeText("Hello"), closeSoftKeyboard())

// Press back
pressBack()
pressBackUnconditionally()
```

### Common actions
| Action | Description |
|--------|-------------|
| `click()` | Tap on view |
| `typeText(text)` | Type into field |
| `replaceText(text)` | Replace field content |
| `clearText()` | Clear field |
| `scrollTo()` | Scroll to view |
| `swipeLeft()` | Swipe left |
| `swipeRight()` | Swipe right |
| `closeSoftKeyboard()` | Hide keyboard |
| `doubleClick()` | Double tap |

---

## Q4: What are ViewAssertions?

ViewAssertions verify the state of a view.

```kotlin
// Visibility
onView(withId(R.id.textView)).check(matches(isDisplayed()))
onView(withId(R.id.textView)).check(matches(not(isDisplayed())))
onView(withId(R.id.textView)).check(matches(withEffectiveVisibility(Visibility.GONE)))

// Text
onView(withId(R.id.textView)).check(matches(withText("Hello")))
onView(withId(R.id.textView)).check(matches(not(withText("Error"))))

// Enabled
onView(withId(R.id.button)).check(matches(isEnabled()))
onView(withId(R.id.button)).check(matches(not(isEnabled())))

// Checked
onView(withId(R.id.checkbox)).check(matches(isChecked()))
onView(withId(R.id.checkbox)).check(matches(isNotChecked()))

// Count (for RecyclerView items)
onView(withId(R.id.recyclerView))
    .check(matches(hasMinimumChildCount(3)))

// Does not exist
onView(withId(R.id.errorView)).check(doesNotExist())
```

### Common assertions
| Assertion | Description |
|-----------|-------------|
| `matches(isDisplayed())` | View is visible |
| `matches(withText(text))` | View has text |
| `matches(isEnabled())` | View is enabled |
| `matches(isChecked())` | View is checked |
| `doesNotExist()` | View not in hierarchy |
| `matches(hasDescendant(...))` | View has child matching |

---

## Q5: How do you test RecyclerView?

```kotlin
// Use RecyclerViewActions
import androidx.recyclerview.espresso.contrib.RecyclerViewActions

@Test
fun `scroll to item and click`() {
    // Scroll to position
    onView(withId(R.id.recyclerView))
        .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(10))

    // Click on item at position
    onView(withId(R.id.recyclerView))
        .perform(
            RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                5, click()
            )
        )

    // Click on item with text
    onView(withId(R.id.recyclerView))
        .perform(
            RecyclerViewActions.actionOnItem<ViewHolder>(
                hasDescendant(withText("Alice")),
                click()
            )
        )
}

@Test
fun `verify item at position has text`() {
    // Check item at position 0
    onView(RecyclerViewMatcher.withRecyclerView(R.id.recyclerView)
        .atPosition(0))
        .check(matches(hasDescendant(withText("Alice"))))
}
```

### RecyclerView matcher helper
```kotlin
class RecyclerViewMatcher(private val recyclerViewId: Int) {
    fun atPosition(position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("at position $position")
            }
            override fun matchesSafely(view: View): Boolean {
                val recyclerView = view.rootView
                    .findViewById<RecyclerView>(recyclerViewId)
                val childAt = recyclerView
                    .findViewHolderForAdapterPosition(position)?.itemView
                return childAt == view
            }
        }
    }
    companion object {
        fun withRecyclerView(id: Int) = RecyclerViewMatcher(id)
    }
}
```

---

## Q6: How do you test Intents?

```gradle
androidTestImplementation 'androidx.test.espresso:espresso-intents:3.5.1'
```

```kotlin
@get:Rule
val intentsRule: IntentsTestRule = IntentsTestRule(MainActivity::class.java)

@Test
fun `clicking login opens HomeActivity`() {
    // Stub all external intents
    intending(not(isInternal()))
        .respondWith(Instrumentation.ActivityResult(0, null))

    onView(withId(R.id.loginButton)).perform(click())

    // Verify intent was sent
    intended(allOf(
        hasComponent(HomeActivity::class.java.name),
        hasExtra("user_id", "123")
    ))
}

@Test
fun `clicking share opens external app`() {
    // Stub the external intent
    val result = Instrumentation.ActivityResult(Activity.RESULT_OK, null)
    intending(toPackage("com.example.shareapp")).respondWith(result)

    onView(withId(R.id.shareButton)).perform(click())

    // Verify intent was sent to external app
    intended(toPackage("com.example.shareapp"))
}

@After
fun tearDown() {
    Intents.release()
}
```

---

## Q7: How do you use IdlingResource?

IdlingResource tells Espresso to wait for async operations to finish.

```kotlin
// Problem: Espresso doesn't wait for async operations
// Solution: IdlingResource

class NetworkIdlingResource : IdlingResource {
    @Volatile private var isIdle = true
    private var callback: IdlingResource.ResourceCallback? = null

    fun setIdle(isIdle: Boolean) {
        this.isIdle = isIdle
        if (isIdle) callback?.onTransitionToIdle()
    }

    override fun getName(): String = "NetworkIdlingResource"

    override fun isIdleNow(): Boolean = isIdle

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }
}

// In test
@Test
fun `wait for network then verify`() {
    val idlingResource = NetworkIdlingResource()
    IdlingRegistry.getInstance().register(idlingResource)

    idlingResource.setIdle(false)  // Start loading

    onView(withId(R.id.refreshButton)).perform(click())

    // Espresso waits until isIdleNow() returns true
    idlingResource.setIdle(true)  // Loading done

    onView(withId(R.id.content))
        .check(matches(isDisplayed()))

    IdlingRegistry.getInstance().unregister(idlingResource)
}
```

### When to use IdlingResource
| Scenario | Need IdlingResource? |
|----------|-------------------|
| Network call | ✅ Yes |
| Database query (async) | ✅ Yes |
| Animation | ✅ Yes |
| Click button | ❌ No (synchronous) |
| Type text | ❌ No (synchronous) |

---

## Q8: How do you test Activity scenarios?

```kotlin
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.*

@Test
fun `launch activity and verify UI`() {
    val scenario = launchActivity<MainActivity>()

    onView(withId(R.id.title))
        .check(matches(withText("Welcome")))

    scenario.close()
}

@Test
fun `test activity state changes`() {
    val scenario = launchActivity<MainActivity>()

    // Simulate configuration change (rotation)
    scenario.recreate()

    // Verify state is preserved
    onView(withId(R.id.editText))
        .check(matches(withText("Typed text")))
}

@Test
fun `test activity result`() {
    val scenario = launchActivity<FormActivity>()

    onView(withId(R.id.inputField))
        .perform(typeText("Hello"))
    onView(withId(R.id.submitButton))
        .perform(click())

    val result = scenario.result
    assertEquals(Activity.RESULT_OK, result.resultCode)
    assertEquals("Hello", result.resultData.getStringExtra("input"))
}
```

---

## Q9: How do you test Dialogs?

```kotlin
@Test
fun `alert dialog is shown and button clicked`() {
    onView(withId(R.id.deleteButton)).perform(click())

    // Dialog is a child of window
    onView(withText("Delete?"))
        .check(matches(isDisplayed()))

    onView(withText("Confirm"))
        .perform(click())

    // Verify item was deleted
    onView(withId(R.id.emptyView))
        .check(matches(isDisplayed()))
}

@Test
fun `cancel dialog`() {
    onView(withId(R.id.deleteButton)).perform(click())

    onView(withText("Cancel"))
        .perform(click())

    // Verify dialog is gone
    onView(withText("Delete?")).check(doesNotExist())
}

@Test
fun `toast message is shown`() {
    // Toasts are in a different window
    onView(withText("Saved!"))
        .inRoot(withDecorView(not(`is`(activityTestRule.activity.window.decorView))))
        .check(matches(isDisplayed()))
}
```

---

## Q10: How do you test with ActivityScenarioRule?

```kotlin
@get:Rule
val activityRule = ActivityScenarioRule(MainActivity::class.java)

@Test
fun `test with activity rule`() {
    // Activity is launched automatically
    onView(withId(R.id.welcomeText))
        .check(matches(isDisplayed()))
}

// With intent extras
@get:Rule
val activityRule = ActivityScenarioRule<DetailActivity>(
    Intent(context, DetailActivity::class.java).apply {
        putExtra("item_id", "123")
    }
)

@Test
fun `detail shows correct item`() {
    onView(withId(R.id.titleText))
        .check(matches(withText("Item 123")))
}
```

### ActivityScenarioRule vs IntentsTestRule
| Rule | Purpose |
|------|---------|
| `ActivityScenarioRule` | Launch activity, test UI |
| `IntentsTestRule` | Launch activity + track intents |
| `GrantPermissionRule` | Grant permissions before test |

---

## Q11: How do you test permissions?

```kotlin
@get:Rule
val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
)

@Test
fun `camera opens with permissions granted`() {
    onView(withId(R.id.cameraButton)).perform(click())
    onView(withId(R.id.cameraPreview)).check(matches(isDisplayed()))
}

// Test permission denial
@get:Rule
val denyPermissionRule: GrantPermissionRule = GrantPermissionRule.grant()

@Test
fun `show rationale when permission denied`() {
    onView(withId(R.id.cameraButton)).perform(click())
    onView(withText("Camera permission required"))
        .check(matches(isDisplayed()))
}
```

---

## Q12: How do you test WebView?

```kotlin
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms
import androidx.test.espresso.web.webdriver.Locator

@get:Rule
val activityRule = ActivityScenarioRule(WebViewActivity::class.java)

@Test
fun `webview loads and interacts`() {
    Web.onWebView()
        .withElement(
            DriverAtoms.findElement(
                Locator.ID, "username"
            )
        )
        .perform(DriverAtoms.webKeys("Alice"))
        .withElement(
            DriverAtoms.findElement(
                Locator.ID, "submit"
            )
        )
        .perform(DriverAtoms.webClick())
        .withElement(
            DriverAtoms.findElement(
                Locator.ID, "welcome"
            )
        )
        .check(
            DriverAtoms.webMatches(
                DriverAtoms.getText(),
                containsString("Welcome Alice")
            )
        )
}
```

---

## Q13: How do you test Fragment navigation?

```kotlin
@get:Rule
val fragmentRule = createRule<LoginFragment>(R.id.container)

@Test
fun `login navigates to home fragment`() {
    launchFragmentInContainer<LoginFragment>(themeResId = R.style.AppTheme)

    onView(withId(R.id.emailField))
        .perform(typeText("user@test.com"))
    onView(withId(R.id.passwordField))
        .perform(typeText("password"))
    onView(withId(R.id.loginButton))
        .perform(click())

    // Verify HomeFragment is shown
    onView(withId(R.id.homeTitle))
        .check(matches(isDisplayed()))
}

// Test with NavController mock
@Test
fun `verify navigation to home`() {
    val navController = TestNavController()
    val scenario = launchFragmentInContainer<LoginFragment>()

    scenario.onFragment { fragment ->
        Navigation.setViewNavController(fragment.requireView(), navController)
    }

    onView(withId(R.id.loginButton)).perform(click())

    assertEquals(R.id.homeFragment, navController.currentDestination?.id)
}
```

---

## Q14: How do you test data binding?

```kotlin
@Test
fun `data binding displays user data`() {
    val scenario = launchActivity<UserActivity>(
        Intent(context, UserActivity::class.java).apply {
            putExtra("user_id", "123")
        }
    )

    // Verify data bound to views
    onView(withId(R.id.nameText))
        .check(matches(withText("Alice")))
    onView(withId(R.id.emailText))
        .check(matches(withText("alice@test.com")))
}

@Test
fun `two way binding updates viewmodel`() {
    onView(withId(R.id.nameInput))
        .perform(replaceText("Bob"))

    scenario.onFragment { fragment ->
        assertEquals("Bob", fragment.viewModel.name.value)
    }
}
```

---

## Q15: How do you handle flaky tests?

### Common causes of flaky tests
| Cause | Solution |
|-------|---------|
| Async operations | Use IdlingResource |
| Animations | Disable animations in test |
| Timing issues | Use `Espresso.onIdle()` |
| Shared state | Reset in `@Before` |
| Network calls | Mock with MockWebServer |

### Disable animations for tests
```kotlin
// In test: disable animations
@get:Rule
val animationDisableRule = AnimationDisablerRule()

// Or via settings on emulator:
// Settings → Developer Options →
//   Window animation scale: 0
//   Transition animation scale: 0
//   Animator duration scale: 0
```

### Retry flaky tests
```gradle
android {
    testOptions {
        execution 'ANDROIDX_TEST_ORCHESTRATOR'
    }
}
```

### Best practices for stable tests
- ✅ Use IdlingResource for async
- ✅ Disable animations
- ✅ Mock network calls
- ✅ Use isolated test data
- ✅ Clean state in `@Before`
- ❌ Don't use `Thread.sleep()`
- ❌ Don't depend on external services
- ❌ Don't share state between tests

---

## 🔗 Related Topics
- [Unit Testing](UnitTesting.md)
- [Mockito](Mockito.md)
- [Compose Testing](ComposeTesting.md)
