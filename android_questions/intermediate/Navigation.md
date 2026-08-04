# Navigation Component

## 📖 Explanation

The Navigation Component is a Jetpack library for managing navigation between screens (fragments). It replaces manual FragmentTransaction management with a visual graph-based approach.

### Key Components
| Component            | Description                                          |
|----------------------|------------------------------------------------------|
| Navigation Graph     | XML resource defining all destinations and actions   |
| NavHostFragment      | Container that swaps fragments                       |
| NavController        | Programmatic control of navigation                   |
| Safe Args            | Type-safe argument passing between destinations      |
| Bottom Navigation    | Integrates with nav graph                            |
| Deep Links           | Direct navigation to specific destinations           |

### Navigation Graph
```xml
<!-- res/navigation/nav_graph.xml -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.app.HomeFragment"
        android:label="Home">
        <action
            android:id="@+id/action_home_to_detail"
            app:destination="@id/detailFragment" />
    </fragment>

    <fragment
        android:id="@+id/detailFragment"
        android:name="com.example.app.DetailFragment"
        android:label="Detail">
        <argument
            android:name="itemId"
            app:argType="integer" />
    </fragment>
</navigation>
```

### NavHost in Layout
```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/navHost"
    android:name="androidx.navigation.fragment.NavHostFragment"
    app:navGraph="@navigation/nav_graph"
    app:defaultNavHost="true" />
```

### NavController
```kotlin
// Navigate to destination
findNavController().navigate(R.id.action_home_to_detail)

// Navigate with arguments
val directions = HomeFragmentDirections.actionHomeToDetail(itemId = 42)
findNavController().navigate(directions)

// Navigate up (back)
findNavController().navigateUp()
```

### Safe Args Plugin
Generates type-safe classes for navigation. No more string keys or manual Bundles.

```groovy
plugins {
    id("androidx.navigation.safeargs.kotlin")
}
```

### Passing Arguments (Safe Args)
```kotlin
// Sender
val action = HomeFragmentDirections.actionHomeToDetail(itemId = 42)
findNavController().navigate(action)

// Receiver
val args: DetailFragmentArgs by navArgs()
val itemId = args.itemId
```

### Bottom Navigation Integration
```kotlin
val navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
val navController = navHost.navController
binding.bottomNav.setupWithNavController(navController)
```

### Deep Links
```xml
<deepLink
    android:id="@+id/deepLink"
    app:uri="myapp://detail/{itemId}" />
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupActionBarWithNavController

// --- Activity with NavHost ---
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHost.navController

        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        return navHost.navController.navigateUp() || super.onSupportNavigateUp()
    }
}

// --- Home Fragment ---
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate with Safe Args
        view.findViewById<Button>(R.id.btnGoToDetail).setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToDetail(itemId = 42)
            findNavController().navigate(action)
        }

        // Navigate to settings
        view.findViewById<Button>(R.id.btnSettings).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }
    }
}

// --- Detail Fragment (receives arguments) ---
class DetailFragment : Fragment() {

    // Safe Args — type-safe arguments
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = args.itemId
        view.findViewById<TextView>(R.id.detailText).text = "Item ID: $itemId"

        // Navigate back
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}

// --- Settings Fragment ---
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
}
```

```xml
<!-- res/navigation/nav_graph.xml -->
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.app.HomeFragment"
        android:label="Home">
        <action
            android:id="@+id/action_home_to_detail"
            app:destination="@id/detailFragment" />
        <action
            android:id="@+id/action_home_to_settings"
            app:destination="@id/settingsFragment" />
    </fragment>

    <fragment
        android:id="@+id/detailFragment"
        android:name="com.example.app.DetailFragment"
        android:label="Detail">
        <argument
            android:name="itemId"
            app:argType="integer"
            android:defaultValue="0" />
    </fragment>

    <fragment
        android:id="@+id/settingsFragment"
        android:name="com.example.app.SettingsFragment"
        android:label="Settings" />
</navigation>
```

---

## ❓ Interview Questions

1. **What is the Navigation Component and what problem does it solve?**
   - It manages fragment navigation via a visual graph. Replaces manual FragmentTransactions, provides type-safe argument passing (Safe Args), handles up/back navigation, and supports deep links.

2. **What is Safe Args and why is it useful?**
   - A Gradle plugin that generates type-safe classes for navigation arguments. No more string keys or manual Bundles — compile-time safety for argument types and names.

3. **What is the difference between `navigateUp()` and `popBackStack()`?**
   - `navigateUp()` goes to the parent destination (respects the nav graph hierarchy). `popBackStack()` simply removes the top fragment from the back stack. `navigateUp` is smarter — it handles the start destination correctly.

4. **How do you handle deep links with Navigation Component?**
   - Add `<deepLink>` to a destination in the nav graph with a URI pattern. The system routes the URI to the correct destination. Use `navController.handleDeepLink(intent)` in the Activity.

5. **How do you integrate Bottom Navigation with Navigation Component?**
   - Use `NavigationUI.setupWithNavController(bottomNav, navController)`. The menu item IDs must match destination IDs in the nav graph for automatic tab switching.

6. **What is the difference between `navigate()` and `popBackStack()` in Navigation Component?**
   - `navigate(direction)` pushes a new destination onto the back stack — the current fragment is paused/stopped and the new one is shown. `popBackStack()` removes the current destination and returns to the previous one. `navigateUp()` is smarter — it pops back if there's a previous destination, or finishes the Activity if at the start destination. Use `navigate()` for forward navigation, `popBackStack()` for explicit back navigation. You can also pop to a specific destination: `navController.popBackStack(R.id.homeFragment, false)` — the `false` means don't pop the home fragment itself. Use `navigate()` with `popUpTo` and `popUpToInclusive` to control the back stack when navigating (e.g., clearing login flow after login).

7. **How do you handle conditional navigation (e.g., redirect to login if not authenticated)?**
   - Use a conditional check before navigating. The recommended approach: (1) Check auth state in the ViewModel or a SessionManager. (2) In the source fragment, check before navigating: `if (isLoggedIn) findNavController().navigate(R.id.action_to_dashboard) else findNavController().navigate(R.id.action_to_login)`. (3) Alternatively, use a `NavigationUI` callback or `OnDestinationChangedListener` to intercept navigation. (4) For global auth checks, use a custom `NavController.OnDestinationChangedListener` in the Activity. (5) For more complex flows, use nested navigation graphs — put the login flow in a nested graph and navigate to it conditionally. Don't put auth logic in the nav graph XML — keep it in Kotlin code where it's testable.

8. **What are nested navigation graphs and when do you use them?**
   - A nested navigation graph is a sub-graph within the main nav graph. It groups related destinations together. Use cases: (1) **Login/Onboarding flow** — group login, register, and forgot password screens. Once complete, pop the entire nested graph. (2) **Checkout flow** — group cart, shipping, payment, and confirmation screens. (3) **Modular features** — each feature module can have its own nav graph included via `<include app:graph="@navigation/feature_nav" />`. Benefits: cleaner main graph, reusable flows, and the ability to pop the entire sub-graph at once: `popUpTo(R.id.login_nested_graph) { inclusive = true }`. Nested graphs have their own start destination. In Compose, use `navigation(startDestination, route) { }` for nested graphs.

9. **How do you pass complex objects between destinations?**
   - Don't pass objects via Safe Args (only primitives, strings, Parcelables supported). Instead: (1) Pass an ID via Safe Args and fetch the object in the destination: `val args by navArgs<DetailArgs>(); repository.getItem(args.itemId)`. (2) Use a shared ViewModel scoped to the Activity: `val sharedViewModel by activityViewModels<SharedViewModel>()` — both fragments access the same data. (3) For one-time passing, use `NavController`'s `SavedStateHandle` — store the object in a shared location keyed by an ID. (4) For Parcelables, Safe Args supports them: `app:argType="com.example.app.User"` with `@Parcelize`. Approach (1) is the most robust — it survives process death because only the ID is in the Bundle.

10. **How do you handle animations and transitions with Navigation Component?**
    - Define animations in the nav graph XML with `<action>` attributes: `app:enterAnim`, `app:exitAnim`, `app:popEnterAnim`, `app:popExitAnim`. Example: `<action app:destination="@id/detailFragment" app:enterAnim="@anim/slide_in_right" app:exitAnim="@anim/slide_out_left" app:popEnterAnim="@anim/slide_in_left" app:popExitAnim="@anim/slide_out_right" />`. For shared element transitions: `FragmentNavigator.Extras` with `sharedElements` map. In Compose, use `composable(route, enterTransition = { ... }, exitTransition = { ... })`. For Material container transform, use `TransitionMode.SHARED_ELEMENT`. You can also set default animations globally. For custom transitions, use `FragmentSharedElementTransition` or `MaterialContainerTransform`.

11. **How do you implement deep links with Navigation Component?**
    - (1) Add `<deepLink app:uri="myapp://detail/{itemId}" />` to a destination in the nav graph. (2) For app links (HTTP), use `app:uri="https://example.com/detail/{itemId}"` with `app:autoVerify="true"`. (3) In the Activity, call `navController.handleDeepLink(intent)` in `onCreate` — this routes the URI to the correct destination. (4) For explicit deep links, use `NavDeepLinkBuilder(context).setDestination(R.id.detailFragment).setArguments(args).createTaskStackBuilder()`. (5) Arguments are automatically parsed from URI placeholders (`{itemId}`). (6) For web-based app links, host `.well-known/assetlinks.json` on your domain. Deep links work with push notifications, emails, and web redirects. Always handle invalid URIs gracefully.

12. **How do you use Navigation Component with Jetpack Compose?**
    - In Compose, use `NavHost(navController, startDestination)` with `composable(route) { Screen() }` instead of XML nav graphs. Example: `NavHost(navController, "home") { composable("home") { HomeScreen(navController) }; composable("detail/{id}") { backStack -> DetailScreen(backStack.arguments?.getString("id")) } }`. Get the controller with `rememberNavController()`. Navigate with `navController.navigate("detail/42")`. Pass arguments via route strings: `navController.navigate("detail/$itemId")`. For type-safe navigation, use Kotlinx Serialization to encode objects in the route. Use `navigation(startDestination, route)` for nested graphs. Integrate bottom nav with `NavigationBar` + `currentBackStackEntryAsState()`. Compose Navigation is more flexible than XML — no Safe Args plugin needed, but less type safety.

---

## 🔗 Related Topics
- [Fragment & Lifecycle](../beginner/FragmentLifecycle.md)
- [Architecture Patterns](ArchitecturePatterns.md)
