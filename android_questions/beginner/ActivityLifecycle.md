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
   - `onCreate` → `onStart` → `onResume` → `onPause` → `onStop` → `onDestroy`. `onRestart` is called when returning from stopped state.

2. **What is the difference between `onPause` and `onStop`?**
   - `onPause` is called when the activity loses focus but may still be partially visible (e.g., a dialog appears). `onStop` is called when the activity is no longer visible at all.

3. **How do you handle screen rotation without losing data?**
   - Use `onSaveInstanceState()` to save transient data, or use `ViewModel` which survives configuration changes. For persistent data, use a database or SharedPreferences.

4. **What are launch modes and when would you use `singleTop`?**
   - `standard` (default), `singleTop` (reuse if on top), `singleTask` (one instance), `singleInstance` (own task). Use `singleTop` for notification activities to avoid stacking duplicates.

5. **When is `onDestroy` called and is it always called?**
   - `onDestroy` is called when the activity is finishing or being destroyed by the system. It's NOT guaranteed to be called if the system kills the process to reclaim memory.

---

## 🔗 Related Topics
- [Fragment & Lifecycle](FragmentLifecycle.md)
- [Android Basics](Basics.md)
