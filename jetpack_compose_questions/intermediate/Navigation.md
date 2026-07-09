# Navigation

## Q1: How do you set up Navigation in Compose?

```kotlin
// build.gradle: implementation "androidx.navigation:navigation-compose:2.7.7"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("detail") { DetailScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Text("Home")
        Button(onClick = { navController.navigate("detail") }) {
            Text("Go to Detail")
        }
    }
}
```

---

## Q2: How do you pass arguments between screens?

```kotlin
// 1. Define route with argument
NavHost(navController, startDestination = "home") {
    composable(
        "detail/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.StringType }),
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getString("userId") ?: ""
        DetailScreen(userId)
    }
}

// 2. Navigate with argument
navController.navigate("detail/42")

// 3. Optional arguments
composable(
    "profile?userId={userId}",
    arguments = listOf(navArgument("userId") {
        type = NavType.StringType
        defaultValue = "default"
        nullable = true
    }),
) { entry ->
    val userId = entry.arguments?.getString("userId")
    ProfileScreen(userId)
}

// Navigate with optional arg
navController.navigate("profile?userId=42")
```

---

## Q3: How do you handle navigation with type-safe routes?

```kotlin
// Type-safe navigation (Navigation 2.8+)
@Serializable
object Home

@Serializable
data class Detail(val userId: String)

@Serializable
object Profile

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Home) {
        composable<Home> { HomeScreen(navController) }
        composable<Detail> { entry ->
            val args = entry.toRoute<Detail>()
            DetailScreen(args.userId)
        }
        composable<Profile> { ProfileScreen(navController) }
    }
}

// Navigate
navController.navigate(Detail(userId = "42"))
navController.navigate(Home)
```

---

## Q4: How do you handle bottom navigation?

```kotlin
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(padding)) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
```

---

## Q5: How do you handle nested navigation?

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "auth") {
        // Auth graph (nested)
        navigation(startDestination = "login", route = "auth") {
            composable("login") { LoginScreen(onLogin = { navController.navigate("main") }) }
            composable("register") { RegisterScreen() }
            composable("forgot") { ForgotPasswordScreen() }
        }

        // Main graph (nested)
        navigation(startDestination = "home", route = "main") {
            composable("home") { HomeScreen() }
            composable("detail/{id}") { DetailScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}

// Navigate to nested graph
navController.navigate("auth")  // Goes to auth graph → login
navController.navigate("main")  // Goes to main graph → home
```

---

## Q6: How do you handle deep links?

```kotlin
NavHost(navController, startDestination = "home") {
    composable(
        "detail/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.StringType }),
        deepLinks = listOf(navDeepLink {
            uriPattern = "https://myapp.com/detail/{userId}"
            action = Intent.ACTION_VIEW
        }),
    ) { entry ->
        val userId = entry.arguments?.getString("userId")
        DetailScreen(userId)
    }
}

// AndroidManifest.xml
// <activity>
//     <intent-filter>
//         <action android:name="android.intent.action.VIEW" />
//         <category android:name="android.intent.category.DEFAULT" />
//         <category android:name="android.intent.category.BROWSABLE" />
//         <data android:scheme="https" android:host="myapp.com" />
//     </intent-filter>
// </activity>

// Test deep link
adb shell am start -W -a android.intent.action.VIEW -d "https://myapp.com/detail/42" com.myapp
```

---

## Q7: How do you handle navigation with ViewModel scoping?

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        // ViewModel scoped to route
        composable("cart") {
            val viewModel: CartViewModel = hiltViewModel()
            CartScreen(viewModel)
        }

        // ViewModel scoped to parent navigation graph
        navigation(startDestination = "step1", route = "checkout") {
            composable("step1") { entry ->
                // Shared across checkout graph
                val viewModel: CheckoutViewModel = hiltViewModel(entry.destination.parent!!)
                CheckoutStep1(viewModel)
            }
            composable("step2") { entry ->
                val viewModel: CheckoutViewModel = hiltViewModel(entry.destination.parent!!)
                CheckoutStep2(viewModel)
            }
        }
    }
}
```

### Navigation Best Practices
```
✅ Use type-safe routes (Serializable objects)
✅ Pop up to start destination for bottom nav
✅ Use saveState/restoreState for bottom nav
✅ Scope ViewModels to routes or graphs
✅ Handle deep links for external entry
✅ Use nested navigation for feature flows
✅ Pass minimal data via arguments (use IDs, fetch data)
```

---

## 🔗 Related Topics
- [State Management](StateManagement.md)
- [Lists](Lists.md)
- [Navigation Scenarios](../scenario_based/NavigationScenarios.md)
