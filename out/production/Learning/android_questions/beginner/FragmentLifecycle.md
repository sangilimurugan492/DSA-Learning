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
   - Fragments have additional callbacks: `onAttach`, `onCreateView`, `onViewCreated`, `onDestroyView`, `onDetach`. Fragment lifecycle is tied to the host activity but has view-specific states.

2. **What is the difference between `onCreate` and `onCreateView` in a Fragment?**
   - `onCreate` initializes non-view data. `onCreateView` inflates the fragment's layout. View setup should happen in `onCreateView`/`onViewCreated`.

3. **How do you pass data to a Fragment?**
   - Use `arguments` Bundle with a `newInstance()` factory method. Avoid direct setters — fragments may be recreated by the system.

4. **What is `FragmentManager` and `FragmentTransaction`?**
   - `FragmentManager` manages fragments within an activity. `FragmentTransaction` performs operations (add, replace, remove) and supports back stack.

5. **How do Fragments communicate with each other or the Activity?**
   - Modern approach: `setFragmentResult`/`setFragmentResultListener`. Shared `ViewModel`. Or via a shared interface/callback.

---

## 🔗 Related Topics
- [Activity & Lifecycle](ActivityLifecycle.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
