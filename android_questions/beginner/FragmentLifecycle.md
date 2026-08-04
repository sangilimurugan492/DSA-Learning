# Fragment & Lifecycle

## 📖 Explanation

A Fragment is a reusable portion of an Activity's UI. Multiple fragments can be combined in a single activity to build a multi-pane UI. Fragments have their own lifecycle, closely tied to the host activity's lifecycle.

### Fragment Lifecycle
```
onAttach → onCreate → onCreateView → onViewCreated →
onActivityCreated → onViewStateRestored → onStart → onResume →
onPause → onStop → onDestroyView → onDestroy → onDetach
```

### Key Lifecycle Callbacks
| Callback              | When Called                              | Use Case                          |
|-----------------------|------------------------------------------|-----------------------------------|
| `onAttach()`          | Fragment is attached to an activity      | Get activity context              |
| `onCreate()`          | Fragment is initialized                 | Initialize data, non-view setup  |
| `onCreateView()`      | Fragment's UI is created                 | Inflate layout                    |
| `onViewCreated()`     | View is created and ready               | Set up views, listeners          |
| `onStart()`           | Fragment becomes visible                | Start animations                 |
| `onResume()`          | Fragment is interactive                  | Resume camera, sensors           |
| `onPause()`          | Fragment loses focus                     | Pause resources                  |
| `onStop()`           | Fragment no longer visible               | Save data                         |
| `onDestroyView()`     | View is destroyed                        | Clean up view references          |
| `onDestroy()`         | Fragment is destroyed                    | Final cleanup                    |
| `onDetach()`          | Fragment detached from activity          | Release activity reference        |

### FragmentManager & Transactions
Fragments are managed by `FragmentManager`. Use `FragmentTransaction` to add, replace, or remove fragments.

```kotlin
supportFragmentManager.commit {
    replace(R.id.fragment_container, MyFragment())
    addToBackStack(null)
}
```

### Fragment Arguments
Pass data to fragments using `arguments` Bundle or `FragmentFactory`.

```kotlin
val fragment = MyFragment().apply {
    arguments = bundleOf("key" to "value")
}
```

### `setFragmentResult` (Modern Communication)
```kotlin
// Sender
setFragmentResult("requestKey", bundleOf("data" to "Hello"))

// Receiver
setFragmentResultListener("requestKey") { _, bundle ->
    val data = bundle.getString("data")
}
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, DetailFragment())
                addToBackStack(null)
            }
        }

        // Listen for fragment result
        supportFragmentManager.setFragmentResultListener("detailResult", this) { _, bundle ->
            val message = bundle.getString("message")
            Log.d("MainActivity", "Received from fragment: $message")
        }
    }
}

class DetailFragment : Fragment() {

    companion object {
        private const val TAG = "DetailFragment"
        private const val ARG_TITLE = "title"

        fun newInstance(title: String) = DetailFragment().apply {
            arguments = Bundle().apply { putString(ARG_TITLE, title) }
        }
    }

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate — title: ${arguments?.getString(ARG_TITLE)}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        val textView = view.findViewById<TextView>(R.id.detailText)
        textView.text = arguments?.getString(ARG_TITLE) ?: "No title"

        // Send result back to activity
        view.findViewById<View>(R.id.sendButton).setOnClickListener {
            setFragmentResult("detailResult", Bundle().apply {
                putString("message", "Hello from Fragment!")
            })
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }
}
```

```xml
<!-- fragment_detail.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center">

    <TextView
        android:id="@+id/detailText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="20sp" />

    <Button
        android:id="@+id/sendButton"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Send Result" />
</LinearLayout>
```

---

## ❓ Interview Questions

1. **What is the difference between Activity and Fragment lifecycle?**
   - Fragments have additional callbacks that Activities don't: `onAttach()` (fragment attached to activity), `onCreateView()` (inflate fragment layout), `onViewCreated()` (view ready for setup), `onDestroyView()` (view being destroyed — clean up view references), and `onDetach()` (fragment detached from activity). The Fragment lifecycle is tied to the host Activity — when the Activity calls `onCreate`, the Fragment's `onCreate` is called too. However, Fragments have a separate view lifecycle from their own lifecycle. The view can be created and destroyed multiple times (e.g., when navigating away and back with `replace` + back stack), while the Fragment instance itself may still be alive. This is why `onDestroyView` exists separately from `onDestroy` — the view hierarchy is destroyed but the Fragment object remains.

2. **What is the difference between `onCreate` and `onCreateView` in a Fragment?**
   - `onCreate(Bundle?)` is called when the Fragment is first created, before any view is inflated. Use it to initialize non-view data: read arguments, set up variables, register observers, or start data loading. Do NOT access views here — they don't exist yet. `onCreateView(LayoutInflater, ViewGroup?, Bundle?)` is called to inflate and return the Fragment's UI layout. This is where you call `inflater.inflate(R.layout.fragment_x, container, false)`. After `onCreateView`, `onViewCreated(view, savedInstanceState)` is called — this is the best place to set up views, click listeners, and adapters because the view hierarchy is ready. The separation exists because Fragments can outlive their views (e.g., when replaced and added to back stack, the view is destroyed in `onDestroyView` but the Fragment instance persists, and `onCreateView` is called again when navigating back).

3. **How do you pass data to a Fragment?**
   - The recommended approach is using the `arguments` Bundle with a companion factory method. This is because the system may recreate the Fragment (e.g., on configuration change or process death), and it uses the no-arg constructor. Direct setters won't survive recreation. Example:
     ```kotlin
     companion object {
         fun newInstance(title: String) = MyFragment().apply {
             arguments = bundleOf("title" to title)
         }
     }
     ```
     Retrieve in `onCreate`: `val title = arguments?.getString("title")`. For more complex scenarios, use `FragmentFactory` (to inject dependencies into the constructor) or Safe Args (Navigation Component) for type-safe argument passing. Never use a custom constructor with parameters — the system can't recreate it.

4. **What is `FragmentManager` and `FragmentTransaction`?**
   - `FragmentManager` is the class responsible for managing Fragments within an Activity. It maintains the back stack, handles fragment lifecycle transitions, and provides methods to find fragments by ID or tag. You access it via `supportFragmentManager` (Activity) or `parentFragmentManager`/`childFragmentManager` (Fragment). `FragmentTransaction` is used to perform atomic operations: `add()`, `replace()`, `remove()`, `hide()`, `show()`, `attach()`, `detach()`. You can group multiple operations and call `commit()` or `commitNow()`. Use `addToBackStack(name)` to allow the user to navigate back with the Back button. `commit()` is asynchronous (schedules on main thread looper), while `commitNow()` is synchronous (executes immediately). Always use `commit()` unless you need immediate execution. Use `commitAllowingStateLoss()` only if you're okay with losing state (e.g., after `onSaveInstanceState`).

5. **How do Fragments communicate with each other or the Activity?**
   - Three main approaches: (1) **`setFragmentResult`/`setFragmentResultListener`** (recommended for one-time data passing) — the sender calls `setFragmentResult("requestKey", bundleOf("data" to value))` and the receiver registers a listener with `setFragmentResultListener("requestKey") { _, bundle -> ... }`. This is lifecycle-safe and decoupled. (2) **Shared ViewModel** (recommended for ongoing communication) — both Fragments access the same ViewModel scoped to the Activity, and communicate via StateFlow/LiveData. This is ideal for sibling fragments that need to share state. (3) **Interface callback** (older approach) — define an interface in the Fragment, implement it in the Activity, and cast `requireActivity()` to the interface. This creates tight coupling and is not recommended for new code. Never communicate directly between two Fragment instances — always go through the Activity or a shared ViewModel.

6. **What is the Fragment back stack and how does it work?**
   - The Fragment back stack is managed by `FragmentManager`. When you call `addToBackStack(name)` on a `FragmentTransaction`, the current state is saved before the transaction is applied. Pressing Back pops the top transaction, reverting to the previous state. Unlike the Activity back stack (which stores Activity instances), the Fragment back stack stores transaction states — it doesn't create new Fragment instances. You can also use `popBackStack()` programmatically. `name` parameter is used with `popBackStack(name, flags)` to pop to a specific saved state. If you don't call `addToBackStack`, the replaced Fragment is destroyed and cannot be restored with Back.

7. **What is the difference between `replace` and `add` in FragmentTransaction?**
   - `add(containerId, fragment)` adds a new Fragment to the container without removing existing ones — multiple fragments can be stacked in the same container. `replace(containerId, fragment)` removes all existing fragments in the container and adds the new one. `replace` = `remove(all existing)` + `add(new)`. Use `replace` when you want a clean swap (most common). Use `add` with `hide`/`show` when you want to keep fragments alive (e.g., tabs with state preservation). Note: `replace` destroys the view of the old fragment (`onDestroyView`), so when you navigate back, `onCreateView` is called again — you need to save/restore view state.

8. **What is `childFragmentManager` and when do you use it?**
   - `childFragmentManager` manages Fragments that are children of the current Fragment (nested fragments). Use it when a Fragment hosts other Fragments (e.g., a ViewPager inside a Fragment, or a master-detail layout within a Fragment). `parentFragmentManager` (or `fragmentManager` in older code) refers to the FragmentManager of the parent Activity or parent Fragment. Using the wrong manager can cause crashes or unexpected behavior. For example, if Fragment A has child Fragment B, you must use `childFragmentManager` to add B, not `parentFragmentManager`.

9. **What is the Fragment view lifecycle and why is it important?**
   - The Fragment view lifecycle is separate from the Fragment lifecycle. It starts at `onCreateView`/`onViewCreated` and ends at `onDestroyView`. This is important because a Fragment can outlive its view — when a Fragment is replaced and added to the back stack, its view is destroyed (`onDestroyView`) but the Fragment instance persists. When navigating back, a new view is created (`onCreateView`). Use `viewLifecycleOwner` (not `this`) when observing LiveData/Flow in `onViewCreated` — this ensures observers are cleaned up when the view is destroyed, preventing memory leaks and duplicate observers. The Fragment lifecycle (`this`) outlives the view lifecycle, so using `this` for view-related observations can cause issues.

10. **What is `FragmentStateAdapter` vs `FragmentPagerAdapter`?**
    - `FragmentPagerAdapter` (deprecated) keeps all fragment instances in memory — good for a small number of static tabs but uses more memory. `FragmentStateAdapter` (recommended, part of ViewPager2) only keeps the current and adjacent fragments in memory, destroying and saving state of others — suitable for large or dynamic lists. `FragmentStateAdapter` is the modern replacement and works with ViewPager2. It saves the Fragment's state (via `saveState`/`restoreState`) when the Fragment is destroyed, so data can be restored when the user navigates back. Always use `FragmentStateAdapter` with ViewPager2 for new code.

11. **How do you handle the Back button in Fragments?**
    - Three approaches: (1) Add the transaction to the back stack with `addToBackStack(null)` — pressing Back automatically pops the transaction. (2) Use `OnBackPressedDispatcher` (modern approach) — register a callback with `requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { /* handle back */ }`. This is lifecycle-aware and allows conditional back handling. (3) Use `OnBackPressedCallback` with `enabled` property to dynamically control whether the callback is active. The old approach of overriding `onBackPressed` in the Activity is deprecated.

12. **What is the `backStackEntryCount` and how is it useful?**
    - `supportFragmentManager.backStackEntryCount` returns the number of Fragment transactions on the back stack. It's useful for: (1) Determining if there are fragments to pop before calling `popBackStack()`. (2) Controlling UI state based on navigation depth (e.g., showing/hiding up arrow). (3) Debugging navigation issues. However, prefer using the Navigation Component for new projects — it handles back stack management automatically and provides type-safe navigation with Safe Args.

13. **What is the difference between `FragmentContainerView` and `FrameLayout` for hosting fragments?**
    - `FragmentContainerView` (introduced in Fragment 1.2.0) is a specialized view for hosting fragments. Benefits over `FrameLayout`: (1) It properly separates the fragment view hierarchy from the activity, preventing `Z-ordering` issues and animation glitches during transitions. (2) It enforces `setHasFixedSize(true)` behavior — fragment views are laid out correctly. (3) It has better `exitTransition` animation support — the exiting fragment's view stays visible during the transition instead of disappearing immediately. (4) It throws an error if you use `replace()` with a fragment that's already added — preventing bugs. Always use `FragmentContainerView` for new code: `<androidx.fragment.app.FragmentContainerView android:id="@+id/container" android:name="com.example.MyFragment" />`.

14. **What is `FragmentResultAPI` and how do you pass data between fragments?**
    - The `FragmentResultAPI` allows passing data between fragments without coupling them via direct references. Instead of calling `fragment.setFragmentResult()`, the receiving fragment registers a listener: `parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { key, bundle -> val result = bundle.getString("data") }`. The sending fragment passes: `parentFragmentManager.setFragmentResult("requestKey", bundleOf("data" to "value"))`. Benefits: (1) Decoupled — sender doesn't need a reference to the receiver. (2) Lifecycle-safe — the listener is tied to the viewLifecycleOwner. (3) Survives configuration changes. This replaces the deprecated `targetFragment`/`setTargetFragment()` pattern.

15. **How do you handle Fragment recreation and state restoration with multiple back stack entries?**
    - In Fragment 1.3.0+, multiple back stack support was added via `FragmentManager.saveBackStack("name")` and `FragmentManager.restoreBackStack("name")`. This lets you save and restore the entire fragment back stack state. For state restoration across process death, fragments automatically save their state via `onSaveInstanceState`. The `SavedStateHandle` in the fragment's ViewModel persists critical state. Use `viewLifecycleOwner` for lifecycle-aware operations to avoid crashes after view recreation. For complex navigation, use Navigation Component with nested graphs instead of manual fragment transactions.

---

## 🔗 Related Topics
- [Activity & Lifecycle](ActivityLifecycle.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
