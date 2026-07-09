# Compose Multiplatform — Interview Questions

## 🔴 Q1: What is Compose Multiplatform?
**Answer:** Compose Multiplatform extends Jetpack Compose to share UI code across Android, iOS, Desktop, and Web. It uses the same Compose compiler and runtime.

```kotlin
// build.gradle.kts
plugins {
    id("org.jetbrains.compose") version "1.5.11"
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm("desktop")
}
```

```kotlin
// commonMain — shared UI
@Composable
fun Greeting(name: String) {
    Text("Hello, $name!")
}
```

---

## 🔴 Q2: What platforms does Compose Multiplatform support?
**Answer:**

| Platform | Status | UI Rendering |
|----------|--------|-------------|
| Android | ✅ Stable | Native (Skia/Android Canvas) |
| Desktop (JVM) | ✅ Stable | Skia |
| iOS | ✅ Stable (1.6+) | Skia (via Kotlin/Native) |
| Web (Wasm) | 🟡 Experimental | Canvas/DOM |

---

## 🔴 Q3: How do you share UI between Android and iOS?
**Answer:**

```kotlin
// commonMain
@Composable
fun App() {
    var count by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}
```

```kotlin
// androidMain
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

// iosMain
fun MainViewController() = ComposeUIViewController { App() }
```

```swift
// Swift
let viewController = MainKt.MainViewController()
window.rootViewController = viewController
```

---

## 🔴 Q4: How do you handle navigation in Compose Multiplatform?
**Answer:** Use **Voyager** or **Navigation Compose** (multiplatform):

```kotlin
// commonMain — using Voyager
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.core.Screen

@Composable
fun App() {
    Navigator(HomeScreen)
}

object HomeScreen : Screen {
    @Composable
    override fun Content() {
        Column {
            Text("Home")
            Button(onClick = { Navigator.current?.push(DetailScreen("123")) }) {
                Text("Go to Detail")
            }
        }
    }
}

data class DetailScreen(val id: String) : Screen {
    @Composable
    override fun Content() {
        Text("Detail: $id")
    }
}
```

---

## 🟡 Q5: How do you handle platform-specific UI in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
@Composable
fun PlatformButton(text: String, onClick: () -> Unit) {
    if (isAndroid()) {
        // Android-specific styling
        Button(onClick, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )) { Text(text) }
    } else {
        // iOS/Desktop styling
        Button(onClick) { Text(text) }
    }
}

// Or use expect/actual
@Composable
expect fun getPlatformInsets(): PaddingValues

// androidMain
@Composable
actual fun getPlatformInsets(): PaddingValues =
    WindowInsets.statusBars.asPaddingValues()

// iosMain
@Composable
actual fun getPlatformInsets(): PaddingValues = PaddingValues(0.dp)
```

---

## 🟡 Q6: How do you handle theming in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
        typography = Typography(),
        content = content
    )
}

@Composable
fun App() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Greeting("World")
        }
    }
}
```

---

## 🟡 Q7: How do you use platform APIs in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
@Composable
expect fun rememberCameraLauncher(onResult: (ByteArray) -> Unit): () -> Unit

// androidMain
@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { /* handle result */ }
    return { launcher.launch(null) }
}

// iosMain
@Composable
actual fun rememberCameraLauncher(onResult: (ByteArray) -> Unit): () -> Unit {
    // Use UIImagePickerController
    return { /* launch camera */ }
}
```

---

## 🟡 Q8: How do you handle resources (images, strings) in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileImage() {
    Image(
        painter = painterResource(Res.drawable.profile),
        contentDescription = "Profile"
    )
}

@Composable
fun Greeting() {
    Text(stringResource(Res.string.hello))
}
```

Resources go in `commonMain/composeResources/`:
```
commonMain/composeResources/
├── drawable/
│   └── profile.xml
└── values/
    └── strings.xml
```

---

## 🟡 Q9: How do you handle state management in Compose Multiplatform?
**Answer:** Same as Android Compose — `remember`, `StateFlow`, `collectAsState`:

```kotlin
// commonMain
@Composable
fun UserScreen(viewModel: UserViewModel) {
    val state by viewModel.uiState.collectAsState()
    
    when (state) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> UserList((state as UiState.Success).data)
        is UiState.Error -> Text((state as UiState.Error).message)
    }
}
```

---

## 🟡 Q10: How do you test Compose Multiplatform UI?
**Answer:**

```kotlin
// commonTest
class GreetingTest {
    @Test
    fun `should display greeting`() {
        composeTestRule.setContent {
            Greeting("World")
        }
        composeTestRule.onNodeWithText("Hello, World!").assertExists()
    }
}
```

Use `org.jetbrains.compose.ui.ui-test-junit4` for multiplatform UI testing.

---

## 🟡 Q11: How do you handle interop with UIKit on iOS?
**Answer:**

```kotlin
// iosMain
import platform.UIKit.UIView
import androidx.compose.ui.viewinterop.UIKitView

@Composable
fun MapView() {
    UIKitView(
        factory = { MKMapView() },
        modifier = Modifier.fillMaxSize()
    )
}
```

---

## 🟡 Q12: How do you handle lifecycle in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
@Composable
fun App() {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> { /* start */ }
                Lifecycle.Event.ON_STOP -> { /* stop */ }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}
```

---

## 🟡 Q13: How do you handle modals/dialogs in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
@Composable
fun App() {
    var showDialog by remember { mutableStateOf(false) }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm") },
            text = { Text("Are you sure?") },
            confirmButton = {
                Button(onClick = { showDialog = false }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("No") }
            }
        )
    }
}
```

---

## 🟡 Q14: How do you handle keyboard/IME in Compose Multiplatform?
**Answer:**

```kotlin
// commonMain
@Composable
fun TextFieldExample() {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    TextField(
        value = text,
        onValueChange = { text = it },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        )
    )
}
```

---

## 🟡 Q15: What are the limitations of Compose Multiplatform?
**Answer:**

| Limitation | Details |
|-----------|---------|
| iOS rendering | Uses Skia, not native UIKit rendering |
| Binary size | Larger than native (includes Skia) |
| Web | Experimental, limited support |
| Platform APIs | Need `expect`/`actual` for native APIs |
| Performance | Slightly slower than native on iOS |
| Accessibility | Limited compared to native |

---

## 📌 Key Takeaways
- Compose Multiplatform shares UI across Android, iOS, Desktop, Web
- Same Compose API — `@Composable`, `remember`, `StateFlow`
- Use `ComposeUIViewController` for iOS entry point
- Voyager or Navigation Compose for navigation
- `UIKitView` for embedding native iOS views
- Resources via `composeResources/` directory

---

[← Intermediate](../intermediate/Testing.md) | [Back to README](../README.md) | [Next: Performance →](Performance.md)
