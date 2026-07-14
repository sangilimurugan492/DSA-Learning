# Navigation Scenarios

## Scenario 1: Auth Flow with Conditional Navigation

**Problem:** App should show login if not authenticated, main screen if authenticated. After login, user shouldn't go back to login.

**Solution:**
```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    val startDestination = when (authState) {
        is AuthState.Authenticated -> "main"
        is AuthState.Unauthenticated -> "auth"
    }

    NavHost(navController, startDestination = startDestination) {
        navigation(startDestination = "login", route = "auth") {
            composable("login") {
                LoginScreen(onLogin = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }  // Remove auth from backstack
                    }
                })
            }
            composable("register") { RegisterScreen() }
        }

        navigation(startDestination = "home", route = "main") {
            composable("home") { HomeScreen(navController) }
            composable("detail/{id}") { entry ->
                DetailScreen(entry.arguments?.getString("id") ?: "")
            }
            composable("settings") { SettingsScreen(navController) }
        }
    }
}

// Logout — clear backstack and go to auth
fun logout(navController: NavController) {
    navController.navigate("auth") {
        popUpTo("main") { inclusive = true }
    }
}
```

---

## Scenario 2: Bottom Navigation with State Preservation

**Problem:** Bottom nav tabs should preserve their state when switching tabs.

**Solution:**
```kotlin
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val tabs = listOf(Tab.Home, Tab.Search, Tab.Profile)

    Scaffold(bottomBar = {
        NavigationBar {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            tabs.forEach { tab ->
                NavigationBarItem(
                    selected = currentRoute == tab.route,
                    onClick = {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true  // Save tab state
                            }
                            launchSingleTop = true  // Don't create duplicate
                            restoreState = true  // Restore saved state
                        }
                    },
                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                    label = { Text(tab.label) },
                )
            }
        }
    }) { padding ->
        NavHost(navController, startDestination = Tab.Home.route, modifier = Modifier.padding(padding)) {
            composable(Tab.Home.route) { HomeScreen() }
            composable(Tab.Search.route) { SearchScreen() }
            composable(Tab.Profile.route) { ProfileScreen() }
        }
    }
}
```

---

## Scenario 3: Pass Data Between Screens

**Problem:** Navigate from list to detail, passing item ID. Detail screen fetches data by ID.

**Solution:**
```kotlin
// Type-safe navigation
@Serializable data class Detail(val itemId: String)

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "list") {
        composable("list") {
            ListScreen(onItemClick = { id ->
                navController.navigate(Detail(id))
            })
        }
        composable<Detail> { entry ->
            val args = entry.toRoute<Detail>()
            DetailScreen(itemId = args.itemId)
        }
    }
}

// Detail screen with ViewModel
@Composable
fun DetailScreen(itemId: String, viewModel: DetailViewModel = viewModel()) {
    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    // ...
}

// Pass result back
@Composable
fun ListScreen(navController: NavController) {
    val result = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("result")

    LaunchedEffect(result) {
        result?.let {
            // Handle result from detail screen
        }
    }

    LazyColumn {
        items(items) { item ->
            Text(
                item.name,
                modifier = Modifier.clickable {
                    navController.navigate(Detail(item.id))
                },
            )
        }
    }
}

// In DetailScreen — set result before popping
fun saveResult(navController: NavController, result: String) {
    navController.previousBackStackEntry?.savedStateHandle?.set("result", result)
    navController.popBackStack()
}
```

---

## Scenario 4: Deep Link to Detail Screen

**Problem:** App should open detail screen directly from a URL or notification.

**Solution:**
```kotlin
@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        composable(
            "detail/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "myapp://detail/{itemId}" },
                navDeepLink { uriPattern = "https://myapp.com/items/{itemId}" },
            ),
        ) { entry ->
            val itemId = entry.arguments?.getString("itemId") ?: ""
            DetailScreen(itemId)
        }
    }
}

// AndroidManifest.xml
// <activity android:name=".MainActivity">
//     <intent-filter>
//         <action android:name="android.intent.action.VIEW" />
//         <category android:name="android.intent.category.DEFAULT" />
//         <category android:name="android.intent.category.BROWSABLE" />
//         <data android:scheme="myapp" android:host="detail" />
//     </intent-filter>
//     <intent-filter>
//         <action android:name="android.intent.action.VIEW" />
//         <category android:name="android.intent.category.DEFAULT" />
//         <category android:name="android.intent.category.BROWSABLE" />
//         <data android:scheme="https" android:host="myapp.com" android:pathPrefix="/items" />
//     </intent-filter>
// </activity>

// From notification
val deepLink = "myapp://detail/42"
val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
context.startActivity(intent)
```

---

## Scenario 5: Nested Navigation Graphs

**Problem:** Feature modules should have their own navigation graphs.

**Solution:**
```kotlin
@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "main") {
        // Main feature graph
        navigation(startDestination = "home", route = "main") {
            composable("home") { HomeScreen() }
            composable("search") { SearchScreen() }
            composable("profile") { ProfileScreen() }
        }

        // Checkout feature graph
        navigation(startDestination = "cart", route = "checkout") {
            composable("cart") { CartScreen() }
            composable("shipping") { ShippingScreen() }
            composable("payment") { PaymentScreen() }
            composable("confirmation") { ConfirmationScreen() }
        }

        // Settings feature graph
        navigation(startDestination = "settings_list", route = "settings") {
            composable("settings_list") { SettingsListScreen() }
            composable("settings/{key}") { entry ->
                SettingDetailScreen(entry.arguments?.getString("key") ?: "")
            }
        }
    }
}

// Navigate to feature graph
navController.navigate("checkout")  // Opens cart (start of checkout graph)
navController.navigate("settings")  // Opens settings_list

// Navigate within graph
navController.navigate("shipping")  // Must be in current graph
```

---

## 🔗 Related Topics
- [Navigation](../intermediate/Navigation.md)
- [State Management](../intermediate/StateManagement.md)
- [Architecture](../advanced/Architecture.md)
