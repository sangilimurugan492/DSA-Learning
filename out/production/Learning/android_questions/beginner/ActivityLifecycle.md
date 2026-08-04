# Activity & Lifecycle

## 📖 Explanation

An Activity represents a single screen with a UI. The Activity lifecycle is a set of states an activity goes through from creation to destruction.

### Lifecycle States
```
        ┌──────────┐
        │ onCreate  │  ← Activity is created
        └────┬─────┘
             ▼
        ┌──────────┐
        │ onStart   │  ← Activity becomes visible
        └────┬─────┘
             ▼
        ┌──────────┐
        │ onResume  │  ← Activity is interactive (foreground)
        └────┬─────┘
             ▼
        ┌──────────┐
        │ onPause   │  ← Activity loses focus (partially visible)
        └────┬─────┘
             ▼
        ┌──────────┐
        │ onStop    │  ← Activity is no longer visible
        └────┬─────┘
             ▼
        ┌──────────┐
        │ onDestroy │  ← Activity is destroyed
        └──────────┘
```

### Lifecycle Callbacks
| Callback       | When Called                                    | Use Case                              |
|----------------|------------------------------------------------|---------------------------------------|
| `onCreate()`   | Activity is first created                      | Initialize UI, restore state          |
| `onStart()`    | Activity becomes visible                      | Start animations, register listeners  |
| `onResume()`   | Activity is interactive (foreground)          | Start camera, sensors, audio          |
| `onPause()`    | Activity loses focus (another activity is on top) | Pause animations, release sensors   |
| `onStop()`     | Activity is no longer visible                  | Save data, release resources          |
| `onRestart()`  | Activity restarts after being stopped         | Re-initialize                         |
| `onDestroy()`  | Activity is destroyed                         | Clean up all resources                |

### `savedInstanceState`
Used to save and restore transient state across configuration changes (rotation) or process death.

### Configuration Changes
When a configuration change occurs (e.g., rotation), the activity is destroyed and recreated. Use `savedInstanceState` or `ViewModel` to preserve data.

### Launch Modes
| Mode         | Description                                          |
|--------------|------------------------------------------------------|
| `standard`   | New instance every time (default)                    |
| `singleTop`  | Reuses existing if on top of stack                   |
| `singleTask` | Only one instance; clears above it                   |
| `singleInstance` | Only one instance in its own task               |

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_COUNTER = "counter"
    }

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        // Restore state
        counter = savedInstanceState?.getInt(KEY_COUNTER) ?: 0
        Log.d(TAG, "Counter restored: $counter")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume — Activity is interactive")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause — Activity losing focus")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop — Activity no longer visible")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy — Cleaning up")
    }

    // Save state before configuration change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        counter++
        outState.putInt(KEY_COUNTER, counter)
        Log.d(TAG, "onSaveInstanceState — Counter saved: $counter")
    }
}
```

### Logcat Output (on rotation)
```
onCreate
Counter restored: 0
onStart
onResume
onPause
onSaveInstanceState — Counter saved: 1
onStop
onDestroy
onCreate
Counter restored: 1
onStart
onResume
```

---

## ❓ Interview Questions

1. **What is the Activity lifecycle? Name all callbacks in order.**
   - `onCreate` → `onStart` → `onResume` → `onPause` → `onStop` → `onDestroy`. `onRestart` is called when returning from stopped state (between `onStop` and `onStart`). `onCreate` is called once per lifecycle; the others may be called multiple times. The activity is in "created" state after `onCreate`, "started" after `onStart`, "resumed" after `onResume` (interactive), "paused" after `onPause`, "stopped" after `onStop`, and "destroyed" after `onDestroy`.

2. **What is the difference between `onPause` and `onStop`?**
   - `onPause` is called when the activity loses focus but may still be partially visible (e.g., a transparent dialog or a smaller activity appears on top). `onStop` is called when the activity is no longer visible at all. In `onPause`, you should stop heavy operations like animations, camera preview, or sensor updates. In `onStop`, release resources that are not needed when invisible, like broadcast receivers or location updates.

3. **How do you handle screen rotation without losing data?**
   - Three approaches: (1) `onSaveInstanceState()` — saves small transient data to a Bundle, restored in `onCreate`. (2) `ViewModel` — survives configuration changes automatically, ideal for UI-related data. (3) `SavedStateHandle` inside ViewModel — survives process death. For persistent data, use Room database or DataStore. Avoid storing large objects in Bundle (1MB limit).

4. **What are launch modes and when would you use `singleTop`?**
   - `standard` (new instance every time), `singleTop` (reuse if already on top of stack), `singleTask` (one instance, clears above it), `singleInstance` (one instance in its own task). Use `singleTop` for notification activities to avoid stacking duplicates. Use `singleTask` for a "home" or "main" activity that should always be the root. Set via `android:launchMode` in manifest or `Intent.FLAG_ACTIVITY_*` flags.

5. **When is `onDestroy` called and is it always called?**
   - `onDestroy` is called when the activity is finishing (`isFinishing() == true`) or when the system destroys it to reclaim memory. It's NOT guaranteed to be called if the system kills the process directly. Therefore, never rely on `onDestroy` for critical cleanup — use `onStop` or `onPause` instead. You can check `isFinishing()` in `onDestroy` to distinguish between user-initiated finish and system-initiated destroy.

6. **What is the difference between `onSaveInstanceState` and `onRestoreInstanceState`?**
   - `onSaveInstanceState(outState: Bundle)` is called before the activity is destroyed (between `onPause` and `onStop`) to save transient UI state. `onRestoreInstanceState(savedInstanceState: Bundle)` is called after `onStart` only if there's saved state to restore. Alternatively, you can restore state in `onCreate` by checking `savedInstanceState != null`. Use `onSaveInstanceState` for things like scroll position, selected tab, or user input text.

7. **What happens when the user presses the Back button vs the Home button?**
   - **Back button**: `onPause` → `onStop` → `onDestroy` — the activity is finished and removed from the back stack. **Home button**: `onPause` → `onStop` — the activity is stopped but NOT destroyed; it remains in the back stack. When returning, `onRestart` → `onStart` → `onResume`. The system may kill the stopped activity to reclaim memory, requiring state restoration.

8. **What is a Task and a Back Stack in Android?**
   - A **Task** is a collection of activities that users interact with, organized in a **Back Stack**. When a new activity starts, it's pushed onto the stack. Pressing Back pops it. Activities from different apps can be in the same task. You can control back stack behavior with launch modes, task affinities (`android:taskAffinity`), and intent flags like `FLAG_ACTIVITY_NEW_TASK` and `FLAG_ACTIVITY_CLEAR_TOP`.

9. **What is `ViewModel` and how does it survive configuration changes?**
   - `ViewModel` is a lifecycle-aware component that stores and manages UI-related data. It survives configuration changes (rotation, locale change) because it's stored in a `ViewModelStore` retained by the Activity's `NonConfigurationInstances`. When the Activity is recreated, the same `ViewModelStore` (and thus ViewModel) is reused. ViewModel is cleared only when the Activity is permanently destroyed (`onDestroy` with `isFinishing() == true`).

10. **What are configuration changes and how can you handle them?**
    - Configuration changes include screen rotation, keyboard availability, language change, dark/light mode toggle. By default, the Activity is destroyed and recreated. You can handle specific changes yourself by adding `android:configChanges` to the manifest (e.g., `orientation|screenSize`), which calls `onConfigurationChanged()` instead of recreating. However, the recommended approach is to let the system recreate and use `ViewModel`/`onSaveInstanceState` to preserve data.

11. **What is the difference between `finish()` and `finishAffinity()`?**
    - `finish()` closes the current activity and removes it from the back stack. `finishAffinity()` closes the current activity and all parent activities with the same task affinity — effectively clearing the entire task. Use `finishAffinity` when you want to exit the app completely (e.g., after logout). `finishAndRemoveTask()` (API 21+) also removes the task from the recents screen.

12. **What is `onNewIntent` and when is it called?**
    - `onNewIntent(intent)` is called when the activity is already running and a new Intent is delivered to it (instead of creating a new instance). This happens with launch modes `singleTop`, `singleTask`, or `singleInstance` when the activity is already at the top. You override `onNewIntent` to handle the new intent data. The old intent is accessible via `getIntent()` until you call `setIntent(intent)`.

13. **What is `onSaveInstanceState` time limit and what happens if you exceed it?**
    - `onSaveInstanceState` must complete quickly — if it takes longer than ~5 seconds, the system shows an ANR (Application Not Responding) dialog. The Bundle passed has a size limit of ~1MB (Binder transaction buffer). Exceeding this causes a `TransactionTooLargeException` crash. Never store large objects (bitmaps, lists of data) in the Bundle — use ViewModel (for configuration changes) or Room/DataStore (for persistent storage). Only save lightweight UI state: scroll position, selected tab index, user input text, toggle states.

14. **What is the difference between `onPostCreate` and `onPostResume`?**
    - `onPostCreate(savedInstanceState)` is called after `onStart` but before `onResume`. It's primarily used for synchronizing visual state after configuration changes (e.g., restoring drawer state). It's rarely overridden in modern apps. `onPostResume()` is called after `onResume` — the activity is fully interactive at this point. Both are system callbacks that most apps don't need to override. They were more relevant before AppCompat and modern architecture components.

15. **How does the system restore an Activity after process death?**
    - When the system kills your app's process to reclaim memory, it saves the activity's state via `onSaveInstanceState`. When the user returns, the system recreates the activity from scratch. It passes the saved Bundle to `onCreate(savedInstanceState)`. The activity calls `super.onCreate()` which restores the view hierarchy state (text in EditTexts, scroll position of ScrollView). For data not in the Bundle (ViewModels are lost on process death), use `SavedStateHandle` in the ViewModel to persist critical state. The key insight: `ViewModel` survives configuration changes but NOT process death — only `SavedStateHandle` and the Bundle survive both.

---

## 🔗 Related Topics
- [Fragment & Lifecycle](FragmentLifecycle.md)
- [Android Basics](Basics.md)
