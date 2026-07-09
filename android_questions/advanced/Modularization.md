# Modularization

## Q1: What is app modularization and why do it?

Modularization is splitting a monolithic app into smaller, independent modules.

### Why modularize?
| Benefit | Description |
|---------|-------------|
| Build speed | Parallel builds, incremental compilation |
| Ownership | Teams own specific modules |
| Reusability | Share modules across apps |
| Build variants | Feature modules can be dynamic |
| Scalability | Add features without bloating |
| Encapsulation | Enforce boundaries between modules |

### Monolith vs Modular
| Aspect | Monolith | Modular |
|--------|----------|---------|
| Build time | Slow (all code) | Fast (changed modules only) |
| Dependencies | Hidden, tangled | Explicit, controlled |
| Team scaling | Merge conflicts | Independent work |
| Reusability | Copy-paste | Module dependency |
| Complexity | Low setup | More Gradle config |

---

## Q2: How do you structure modules?

```
:app                    ← App module (DI, navigation, launcher)
:core:common            ← Shared utilities, constants
:core:ui                ← Shared UI components, theme
:core:data              ← Repository implementations
:core:database          ← Room database
:core:network           ← Retrofit, API interfaces
:core:domain            ← Use cases, models (pure Kotlin)
:core:designsystem      ← Design tokens, Compose theme
:feature:login          ← Login feature
:feature:home           ← Home screen
:feature:settings       ← Settings screen
:feature:profile        ← Profile feature
```

### Module types
| Type | Depends on | Description |
|------|-----------|-------------|
| `:app` | All modules | App entry point, DI graph |
| `:core:*` | Other core modules | Shared infrastructure |
| `:feature:*` | Core modules | Self-contained features |
| `:library:*` | Nothing | Standalone libraries |

---

## Q3: How do you set up module dependencies?

```kotlin
// :feature:home/build.gradle.kts
dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Hilt
    implementation(libs.hilt.navigation.compose)
}
```

### Dependency rules
```
:app → :feature:* → :core:domain → :core:data → :core:network, :core:database
                                → :core:ui → :core:designsystem → :core:common
```

### Key principle
> Features should NOT depend on each other. They communicate through shared core modules or navigation.

---

## Q4: How do you enforce module boundaries?

```kotlin
// Use Kotlin's internal visibility
// core/domain/src/main/kotlin/User.kt
data class User(val id: String, val name: String)

// internal — only visible within module
internal class UserMapper {
    fun map(dto: UserDto): User = User(dto.id, dto.name)
}

// public — visible to dependent modules
class GetUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(id: String): User
}
```

### Boundary enforcement with build.gradle
```kotlin
// :feature:home/build.gradle.kts
android {
    // Prevent accessing other features
    dependencies {
        // ✅ Good — depends on core
        implementation(project(":core:domain"))
        implementation(project(":core:ui"))

        // ❌ Bad — feature depending on feature
        // implementation(project(":feature:profile"))
    }
}
```

### Tools for enforcement
| Tool | Description |
|------|-------------|
| `internal` keyword | Kotlin visibility |
| Dependency rules | Gradle module restrictions |
| [Module Graph Assert](https://github.com/jraska/modules-graph-assert) | Gradle plugin |
| ArchUnit | Architecture tests |

---

## Q5: How do you share navigation between modules?

```kotlin
// :core:navigation — shared navigation contracts
interface FeatureNavigator {
    fun navigateToLogin()
    fun navigateToHome()
    fun navigateToProfile(userId: String)
}

// :app — implements navigation
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navigator = remember(navController) {
        AppNavigator(navController)
    }

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navigator) }
        composable("login") { LoginScreen(navigator) }
        composable("profile/{userId}") { backStack ->
            ProfileScreen(
                userId = backStack.arguments?.getString("userId") ?: "",
                navigator = navigator
            )
        }
    }
}

// :feature:home — uses navigator, doesn't know about other features
@Composable
fun HomeScreen(navigator: FeatureNavigator) {
    Button(onClick = { navigator.navigateToProfile("123") }) {
        Text("View Profile")
    }
}
```

---

## Q6: How do you use dynamic feature modules?

```kotlin
// :app/build.gradle.kts
android {
    dynamicFeatures += setOf(
        ":feature:camera",
        ":feature:ar"
    )
}

// :feature:camera/build.gradle.kts
plugins {
    id("com.android.dynamic-feature")
}

android {
    // No applicationId needed
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:domain"))
}
```

### Dynamic feature benefits
| Benefit | Description |
|---------|-------------|
| Smaller initial APK | Download features on demand |
| On-demand delivery | Install features when needed |
| Instant apps | Use features without installing app |

### When to use dynamic features
- ✅ Large features used rarely (AR camera, video editor)
- ✅ App size > 150MB
- ❌ Don't use for core features always needed

---

## Q7: How do you handle DI with modules?

```kotlin
// :core:data — provides repository
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

// :core:network — provides API
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)
}

// :feature:login — provides ViewModel
@Module
@InstallIn(ViewModelComponent::class)
object LoginModule {
    @Provides
    fun provideLoginViewModel(
        repository: UserRepository
    ): LoginViewModel = LoginViewModel(repository)
}

// :app — aggregates all modules
// Hilt automatically discovers @Module in all modules
// No need to list them — just add dependencies
```

### DI per module
| Module | Provides |
|--------|---------|
| `:core:network` | Retrofit, OkHttp, APIs |
| `:core:database` | Room DB, DAOs |
| `:core:data` | Repositories |
| `:core:domain` | Use cases |
| `:feature:*` | ViewModels |
| `:app` | Aggregates all |

---

## Q8: How do you share resources across modules?

```kotlin
// :core:designsystem — shared theme
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = AppTypography,
        content = content
    )
}

// :core:ui — shared components
@Composable
fun AppButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text)
    }
}

// :feature:home — uses shared components
@Composable
fun HomeScreen() {
    AppTheme {
        AppButton("Click me") { }
    }
}
```

### Resource sharing
| Resource | Where to put |
|----------|-------------|
| Colors, themes | `:core:designsystem` |
| Strings (shared) | `:core:common` |
| Strings (feature) | `:feature:*` |
| Drawables (shared) | `:core:ui` |
| Drawables (feature) | `:feature:*` |

---

## Q9: How do you handle multi-module testing?

```kotlin
// :core:domain — unit tests
class GetUserUseCaseTest {
    @Test
    fun `get user returns user`() = runTest {
        val useCase = GetUserUseCase(fakeRepository)
        val result = useCase("123")
        assertEquals("Alice", result.name)
    }
}

// :feature:home — UI tests
class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `displays welcome message`() {
        composeRule.setContent { HomeScreen() }
        composeRule.onNodeWithText("Welcome").assertIsDisplayed()
    }
}

// :app — integration tests
@RunWith(AndroidJUnit4::class)
class AppIntegrationTest {
    @Test
    fun `login flow works end to end`() {
        // Test across modules
    }
}
```

### Test types per module
| Module | Test type |
|--------|-----------|
| `:core:domain` | Unit tests (pure Kotlin) |
| `:core:data` | Unit tests (mock API) |
| `:core:database` | Instrumented (in-memory Room) |
| `:feature:*` | Compose UI tests |
| `:app` | Integration / E2E |

---

## Q10: What are modularization best practices?

### Do's
- ✅ Start with `:core:common`, `:core:ui`, `:core:domain`
- ✅ Keep features independent (no feature-to-feature deps)
- ✅ Use `internal` visibility to enforce boundaries
- ✅ Share navigation contracts, not implementations
- ✅ Each module has its own `build.gradle.kts`
- ✅ Use version catalogs (`libs.versions.toml`)

### Don'ts
- ❌ Don't over-modularize early (start with 3-4 modules)
- ❌ Don't create circular dependencies
- ❌ Don't put business logic in `:app`
- ❌ Don't share ViewModels across features
- ❌ Don't create a module for one file

### Module size guidelines
| Module size | Action |
|-------------|--------|
| < 10 files | Too small, merge with another |
| 10-50 files | Good |
| 50-100 files | Consider splitting |
| > 100 files | Split into sub-modules |

### Version catalog
```toml
# gradle/libs.versions.toml
[versions]
compose-bom = "2024.02.00"
hilt = "2.51"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
```

---

## 🔗 Related Topics
- [Architecture Patterns](../intermediate/ArchitecturePatterns.md)
- [Dependency Injection](DependencyInjection.md)
- [Performance](Performance.md)
