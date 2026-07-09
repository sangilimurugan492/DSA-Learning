# Interop

## Q1: How do you embed Compose in an XML layout?

```xml
<!-- res/layout/activity_main.xml -->
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/compose_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            MaterialTheme {
                ComposeContent()
            }
        }
    }
}

// With ViewModel
composeView.setContent {
    MaterialTheme {
        val viewModel: MyViewModel = viewModel()
        MyScreen(viewModel)
    }
}
```

---

## Q2: How do you embed XML Views in Compose?

```kotlin
@Composable
fun AndroidViewInCompose() {
    // Embed any Android View
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                text = "Hello from XML View"
                textSize = 20f
            }
        },
        update = { textView ->
            // Update view when state changes
            textView.text = "Updated: $someState"
        },
    )

    // Embed WebView
    AndroidView(
        factory = { context -> WebView(context) },
        update = { webView ->
            webView.settings.javaScriptEnabled = true
            webView.loadUrl("https://example.com")
        },
    )

    // Embed MapView
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                getMapAsync { googleMap ->
                    googleMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
                }
            }
        },
        update = { mapView ->
            mapView.onResume()
        },
    )
}
```

---

## Q3: How do you migrate from XML to Compose gradually?

```kotlin
// Step 1: Add Compose to existing project
// build.gradle
// buildFeatures { compose = true }
// dependencies { implementation "androidx.compose.ui:ui" ... }

// Step 2: Start with new screens in Compose
class NewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { NewScreen() }
        }
    }
}

// Step 3: Convert individual Views to ComposeView
// Old: <TextView ... />
// New: ComposeView with Text()

// Step 4: Use AbstractComposeView for custom views
class ComposeButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {

    var text by mutableStateOf("Click")
    var onClick by mutableStateOf({})

    @Composable
    override fun Content() {
        Button(onClick = onClick) { Text(text) }
    }
}

// Use in XML
// <com.example.ComposeButton
//     android:id="@+id/button"
//     android:layout_width="wrap_content"
//     android:layout_height="wrap_content" />
```

---

## Q4: How do you use Fragment with Compose?

```kotlin
class ComposeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewDestroyed)
            setContent {
                MaterialTheme {
                    FragmentContent()
                }
            }
        }
    }
}

// Navigation with Fragments
class NavHostFragment : Fragment() {
    override fun onCreateView(...): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "home") {
                    composable("home") { HomeScreen() }
                    composable("detail") { DetailScreen() }
                }
            }
        }
    }
}
```

### ViewCompositionStrategy
| Strategy | When to dispose |
|----------|---------------|
| `DisposeOnViewRemoved` | When ComposeView removed from window |
| `DisposeOnViewDestroyed` | When Fragment view is destroyed |
| `DisposeOnLifecycleDestroyed` | When lifecycle is destroyed |
| `DisposeOnViewTreeLifecycleDestroyed` | When view tree lifecycle ends |

---

## Q5: How do you share theme between XML and Compose?

```kotlin
// Read XML theme colors in Compose
@Composable
fun useXmlColors(): Color {
    val context = LocalContext.current
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
    return Color(typedValue.data)
}

// Use Material attributes
@Composable
fun useMaterialColors(): Color {
    val context = LocalContext.current
    val typedArray = context.obtainStyledAttributes(
        intArrayOf(com.google.android.material.R.attr.colorPrimary),
    )
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return Color(color)
}

// Wrap Compose in Material 2 theme from XML
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MdcTheme {  // Material Design Components theme
                AppContent()
            }
        }
    }
}
```

---

## Q6: How do you handle View bindings in Compose?

```kotlin
@Composable
fun rememberViewTreeLifecycleOwner(): LifecycleOwner? {
    var lifecycleOwner by remember { mutableStateOf<LifecycleOwner?>(null) }
    val view = LocalView.current
    DisposableEffect(view) {
        val observer = ViewTreeLifecycleOwner.get(view)
        lifecycleOwner = observer
        onDispose { }
    }
    return lifecycleOwner
}

// Use findViewById in Compose
@Composable
fun FindViewByIdExample() {
    val context = LocalContext.current
    val activity = context as Activity

    Button(onClick = {
        val textView = activity.findViewById<TextView>(R.id.text_view)
        textView?.text = "Updated from Compose"
    }) {
        Text("Update XML View")
    }
}

// Use ViewBinding in Compose
@Composable
fun ViewBindingExample(binding: ActivityMainBinding) {
    Button(onClick = {
        binding.xmlTextView.text = "Updated"
    }) {
        Text("Update via ViewBinding")
    }
}
```

---

## Q7: How do you handle custom View attributes in Compose?

```kotlin
// Custom View with attributes
class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AbstractComposeView(context, attrs) {

    var buttonText by mutableStateOf("Default")
    var buttonColor by mutableStateOf(Color.Blue)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomButton)
            buttonText = typedArray.getString(R.styleable.CustomButton_text) ?: "Default"
            buttonColor = Color(typedArray.getColor(R.styleable.CustomButton_color, Color.Blue.value.toInt()))
            typedArray.recycle()
        }
    }

    @Composable
    override fun Content() {
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        ) {
            Text(buttonText)
        }
    }
}

// res/values/attrs.xml
// <declare-styleable name="CustomButton">
//     <attr name="text" format="string" />
//     <attr name="color" format="color" />
// </declare-styleable>

// XML usage
// <com.example.CustomButton
//     app:text="Submit"
//     app:color="@color/primary"
//     android:layout_width="wrap_content"
//     android:layout_height="wrap_content" />
```

### Interop Best Practices
```
✅ Use ComposeView to embed Compose in XML
✅ Use AndroidView to embed XML Views in Compose
✅ Use AbstractComposeView for custom Compose Views
✅ Use ViewCompositionStrategy for Fragment lifecycle
✅ Migrate gradually — new screens in Compose first
✅ Share theme with MdcTheme wrapper
✅ Use ViewBinding for complex XML interop
✅ Dispose resources properly in DisposableEffect
```

---

## 🔗 Related Topics
- [Basics](../beginner/Basics.md)
- [Architecture](Architecture.md)
- [Theming](../intermediate/Theming.md)
