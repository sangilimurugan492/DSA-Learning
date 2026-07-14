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

---

## 🔗 Related Topics
- [Fragment & Lifecycle](../beginner/FragmentLifecycle.md)
- [Architecture Patterns](ArchitecturePatterns.md)
