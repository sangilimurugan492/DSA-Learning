# Theming

## Q1: How do you use MaterialTheme?

```kotlin
@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            onPrimary = Color.White,
            secondary = Color(0xFF03DAC6),
            background = Color.White,
            surface = Color.White,
            error = Color.Red,
        ),
        typography = Typography(
            headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
            bodyLarge = TextStyle(fontSize = 16.sp),
        ),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(16.dp),
        ),
    ) {
        AppContent()
    }
}

// Access theme values
@Composable
fun AppContent() {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val shapes = MaterialTheme.shapes

    Text(
        "Hello",
        color = colorScheme.onBackground,
        style = typography.headlineMedium,
    )
}
```

---

## Q2: How do you handle dark mode?

```kotlin
@Composable
fun App() {
    val darkMode = isSystemInDarkTheme()  // System setting

    val colorScheme = if (darkMode) {
        darkColorScheme(
            primary = Color(0xFFBB86FC),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EE),
            background = Color.White,
            surface = Color.White,
        )
    }

    MaterialTheme(colorScheme = colorScheme) {
        AppContent()
    }
}

// User-preference-based dark mode
@Composable
fun App(themeViewModel: ThemeViewModel = viewModel()) {
    val isDark by themeViewModel.isDarkMode.collectAsStateWithLifecycle()

    val colorScheme = if (isDark) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        AppContent()
    }
}
```

---

## Q3: How do you use dynamic color (Material You)?

```kotlin
@Composable
fun App() {
    val darkMode = isSystemInDarkTheme()
    val context = LocalContext.current

    // Dynamic color (Android 12+)
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkMode) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkMode -> DarkColors  // Fallback
        else -> LightColors
    }

    MaterialTheme(colorScheme = colorScheme) {
        AppContent()
    }
}
```

---

## Q4: How do you define custom typography?

```kotlin
val AppTypography = Typography(
    displayLarge = TextStyle(fontSize = 57.sp, fontWeight = FontWeight.Normal, lineHeight = 64.sp),
    displayMedium = TextStyle(fontSize = 45.sp, fontWeight = FontWeight.Normal, lineHeight = 52.sp),
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
)

// Custom font family
val AppFontFamily = FontFamily(
    Font(R.font.regular, FontWeight.Normal),
    Font(R.font.medium, FontWeight.Medium),
    Font(R.font.bold, FontWeight.Bold),
)

val AppTypography = Typography(
    bodyLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 16.sp),
    titleLarge = TextStyle(fontFamily = AppFontFamily, fontSize = 22.sp, fontWeight = FontWeight.Bold),
)

// Usage
MaterialTheme(typography = AppTypography) { /* ... */ }
```

---

## Q5: How do you create custom themes?

```kotlin
// Custom color scheme
data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val accent: Color,
    val isDark: Boolean,
)

val LocalAppColors = staticCompositionLocalOf { AppColors(
    primary = Color.Unspecified,
    onPrimary = Color.Unspecified,
    accent = Color.Unspecified,
    isDark = false,
) }

@Composable
fun AppTheme(isDark: Boolean, content: @Composable () -> Unit) {
    val colors = if (isDark) {
        AppColors(primary = Color(0xFFBB86FC), onPrimary = Color.Black, accent = Color(0xFF03DAC6), isDark = true)
    } else {
        AppColors(primary = Color(0xFF6200EE), onPrimary = Color.White, accent = Color(0xFF018786), isDark = false)
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(colorScheme = if (isDark) darkColorScheme() else lightColorScheme()) {
            content()
        }
    }
}

// Access custom colors
@Composable
fun CustomButton() {
    val colors = LocalAppColors.current
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary,
        ),
    ) { Text("Custom") }
}
```

---

## Q6: How do you use CompositionLocal for theming?

```kotlin
// staticCompositionLocalOf — never changes (better performance)
val LocalElevation = staticCompositionLocalOf { 4.dp }

// compositionLocalOf — can change (triggers recomposition)
val LocalTheme = compositionLocalOf { Theme.Light }

@Composable
fun App() {
    CompositionLocalProvider(
        LocalTheme provides Theme.Dark,
        LocalElevation provides 8.dp,
    ) {
        Child()
    }
}

@Composable
fun Child() {
    val theme = LocalTheme.current
    val elevation = LocalElevation.current
    // Use values
}

// Built-in CompositionLocals
val context = LocalContext.current
val configuration = LocalConfiguration.current
val density = LocalDensity.current
val hapticFeedback = LocalHapticFeedback.current
val view = LocalView.current
val lifecycleOwner = LocalLifecycleOwner.current
```

### staticCompositionLocalOf vs compositionLocalOf
| staticCompositionLocalOf | compositionLocalOf |
|--------------------------|---------------------|
| Never changes | Can change |
| No recomposition tracking | Tracks reads |
| Better performance | More flexible |
| For app-wide constants | For dynamic values |

---

## Q7: How do you handle elevation and shapes?

```kotlin
// Elevation — tonal + shadow
Surface(
    tonalElevation = 4.dp,   // Tonal color change (Material 3)
    shadowElevation = 8.dp,   // Drop shadow
    shape = RoundedCornerShape(12.dp),
) {
    Text("Elevated surface")
}

// Shape hierarchy
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// Access shapes
@Composable
fun CustomCard() {
    val shapes = MaterialTheme.shapes
    Surface(shape = shapes.medium) {
        Text("Card")
    }
}

// Elevation overlays (Material 2) vs tonal elevation (Material 3)
// Material 3: tonalElevation changes surface color (no shadow)
// shadowElevation adds actual shadow
```

---

## Q8: How do you implement multi-brand theming?

```kotlin
// Multi-brand — support different brand colors/logos in one app
enum class Brand { Default, Premium, Enterprise }

data class BrandTheme(
    val primary: Color,
    val secondary: Color,
    val logo: Int,
    val font: FontFamily,
)

val LocalBrandTheme = staticCompositionLocalOf { BrandTheme.Default }

object BrandThemes {
    val Default = BrandTheme(
        primary = Color(0xFF6200EE),
        secondary = Color(0xFF03DAC6),
        logo = R.drawable.logo_default,
        font = FontFamily.Default,
    )
    val Premium = BrandTheme(
        primary = Color(0xFFFFD700),
        secondary = Color(0xFFB8860B),
        logo = R.drawable.logo_premium,
        font = FontFamily.Serif,
    )
    val Enterprise = BrandTheme(
        primary = Color(0xFF003366),
        secondary = Color(0xFF0066CC),
        logo = R.drawable.logo_enterprise,
        font = FontFamily.SansSerif,
    )
}

@Composable
fun BrandThemeWrapper(brand: Brand, content: @Composable () -> Unit) {
    val theme = when (brand) {
        Brand.Default -> BrandThemes.Default
        Brand.Premium -> BrandThemes.Premium
        Brand.Enterprise -> BrandThemes.Enterprise
    }

    val colorScheme = when (brand) {
        Brand.Default -> lightColorScheme(primary = theme.primary, secondary = theme.secondary)
        Brand.Premium -> lightColorScheme(primary = theme.primary, secondary = theme.secondary)
        Brand.Enterprise -> darkColorScheme(primary = theme.primary, secondary = theme.secondary)
    }

    CompositionLocalProvider(LocalBrandTheme provides theme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(bodyLarge = TextStyle(fontFamily = theme.font)),
        ) {
            content()
        }
    }
}

// Usage
@Composable
fun App() {
    val brand = getBrandFromConfig()
    BrandThemeWrapper(brand) {
        MainScreen()
    }
}

// Access brand-specific values
@Composable
fun Header() {
    val brand = LocalBrandTheme.current
    Image(painterResource(brand.logo), contentDescription = "Logo")
}
```

> **Key:** Use `CompositionLocal` for brand-specific values that don't fit in `MaterialTheme` (logos, custom fonts). Keep `MaterialTheme` for standard Material colors/typography/shapes.

---

## Q9: How do you create custom component styles?

```kotlin
// Component-specific theming — consistent styling across app

// 1. Define custom component colors
data class ButtonColors(
    val container: Color,
    val content: Color,
    val disabledContainer: Color,
    val disabledContent: Color,
)

val LocalButtonColors = staticCompositionLocalOf {
    ButtonColors(
        container = Color.Unspecified,
        content = Color.Unspecified,
        disabledContainer = Color.Unspecified,
        disabledContent = Color.Unspecified,
    )
}

// 2. Provide in theme
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = ButtonColors(
        container = MaterialTheme.colorScheme.primary,
        content = MaterialTheme.colorScheme.onPrimary,
        disabledContainer = MaterialTheme.colorScheme.surfaceVariant,
        disabledContent = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    CompositionLocalProvider(LocalButtonColors provides colors) {
        content()
    }
}

// 3. Reusable styled component
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val colors = LocalButtonColors.current
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.container,
            contentColor = colors.content,
            disabledContainerColor = colors.disabledContainer,
            disabledContentColor = colors.disabledContent,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(text)
    }
}

// Usage — consistent everywhere
PrimaryButton("Submit", onClick = { /* ... */ })
PrimaryButton("Save", onClick = { /* ... */ }, enabled = false)
```

> **Best Practice:** Create wrapper composables (`PrimaryButton`, `SecondaryButton`, `AppCard`) that apply your app's design system. This ensures consistency and makes rebranding a one-line change.

---

## Q10: How do you handle edge-to-edge and window insets?

```kotlin
// Edge-to-edge — content draws behind system bars
// In Activity: enableEdgeToEdge()

// 1. windowInsetsPadding — push content away from system bars
Column(Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
    Text("Below status bar")
}

// 2. Modifier.statusBarsPadding() — shorthand
Column(Modifier.statusBarsPadding()) {
    Text("Below status bar")
}

// 3. Multiple insets
Column(
    Modifier
        .statusBarsPadding()
        .navigationBarsPadding()
) {
    Text("Safe from status + nav bars")
}

// 4. Scaffold handles insets automatically
Scaffold(
    topBar = { TopAppBar(title = { Text("App") }) },
    contentWindowInsets = WindowInsets.safeDrawing,
) { padding ->
    Column(Modifier.padding(padding)) {
        Text("Properly inset content")
    }
}

// 5. IME (keyboard) insets
Column(Modifier.imePadding()) {
    TextField(value = text, onValueChange = { text = it })
    // Content moves up when keyboard appears
}

// 6. Custom inset handling
Box(Modifier.fillMaxSize()) {
    Text("Full screen")
    // Apply insets only to specific elements
    Text(
        "Status bar text",
        modifier = Modifier
            .align(Alignment.TopCenter)
            .statusBarsPadding(),
    )
}

// 7. Animated IME insets
val imeInsets = WindowInsets.ime
val imeVisible = imeInsets.isVisible
val imeHeight = imeInsets.getBottom(LocalDensity.current)
```

| Inset Type | Modifier | What It Avoids |
|-----------|----------|---------------|
| Status bar | `statusBarsPadding()` | Notch, clock, battery |
| Navigation bar | `navigationBarsPadding()` | Back button, gesture bar |
| IME (keyboard) | `imePadding()` | Keyboard overlap |
| Safe drawing | `safeDrawingPadding()` | All system UI |
| System bars | `systemBarsPadding()` | Status + nav bars |
| Display cutout | `displayCutoutPadding()` | Notches, punch holes |

> **Key:** `Scaffold` handles most insets automatically via its `padding` parameter. For custom layouts, use `WindowInsets` modifiers. Always call `enableEdgeToEdge()` in your Activity for modern edge-to-edge rendering.

---

## 🔗 Related Topics
- [Composables](../beginner/Composables.md)
- [Modifiers](../beginner/Modifiers.md)
- [State Management](StateManagement.md)
