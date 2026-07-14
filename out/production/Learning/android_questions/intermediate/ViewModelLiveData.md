# ViewModel & LiveData

## 📖 Explanation

### ViewModel
`ViewModel` stores and manages UI-related data, surviving configuration changes (like rotation). It's lifecycle-aware and doesn't hold View references, preventing memory leaks.

```kotlin
class MyViewModel : ViewModel() {
    val data = MutableLiveData<String>()
}
```

### Key ViewModel Features
- Survives configuration changes (Activity recreation).
- Cleared when the Activity is permanently destroyed (`onCleared()`).
- No View reference — safe from leaks.
- Works with `viewModelScope` for coroutines.

### LiveData
`LiveData` is a lifecycle-aware observable data holder. It only notifies active observers and automatically cleans up when the lifecycle is destroyed.

```kotlin
val name = MutableLiveData<String>()
name.observe(this) { newName ->
    textView.text = newName
}
```

### LiveData vs StateFlow
| Feature         | LiveData              | StateFlow              |
|-----------------|----------------------|------------------------|
| Lifecycle aware | Yes (built-in)       | Needs `repeatOnLifecycle` |
| Initial value    | Not required         | Required               |
| Threading        | Main thread only     | Any dispatcher         |
| Cold/Hot         | Hot                  | Hot                    |
| Kotlin first     | No (Java-based)      | Yes                    |
| Recommended      | Legacy projects      | New Kotlin projects    |

### ViewModel with SavedStateHandle
Persist data across process death.

```kotlin
class MyViewModel(state: SavedStateHandle) : ViewModel() {
    val name = state.getLiveData("name", "")
}
```

### Sharing ViewModel Between Fragments
```kotlin
val viewModel by activityViewModels<SharedViewModel>()
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch

// --- Data Model ---
data class CounterState(
    val count: Int,
    val isEven: Boolean
)

// --- ViewModel with LiveData ---
class CounterViewModel : ViewModel() {

    private val _counter = MutableLiveData(0)
    val counter: LiveData<Int> = _counter

    private val _state = MutableLiveData(CounterState(0, true))
    val state: LiveData<CounterState> = _state

    fun increment() {
        val newCount = (_counter.value ?: 0) + 1
        _counter.value = newCount
        _state.value = CounterState(newCount, newCount % 2 == 0)
    }

    fun decrement() {
        val newCount = (_counter.value ?: 0) - 1
        _counter.value = newCount
        _state.value = CounterState(newCount, newCount % 2 == 0)
    }

    fun reset() {
        _counter.value = 0
        _state.value = CounterState(0, true)
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up resources
    }
}

// --- ViewModel with SavedStateHandle (survives process death) ---
class NoteViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val KEY_NOTE = "note"

    val note: LiveData<String> = state.getLiveData(KEY_NOTE, "")

    fun updateNote(text: String) {
        state[KEY_NOTE] = text
    }
}

// --- ViewModel with Coroutines ---
class UserViewModel(private val repository: UserRepository) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val user: User) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableLiveData<UiState>(UiState.Loading)
    val uiState: LiveData<UiState> = _uiState

    fun loadUser(id: Long) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val user = repository.getUser(id)
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
}

// --- Activity using ViewModel ---
class CounterActivity : AppCompatActivity() {

    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        // Observe LiveData
        viewModel.counter.observe(this) { count ->
            findViewById<TextView>(R.id.countText).text = count.toString()
        }

        viewModel.state.observe(this) { state ->
            findViewById<TextView>(R.id.stateText).text =
                if (state.isEven) "Even" else "Odd"
        }

        findViewById<Button>(R.id.btnIncrement).setOnClickListener {
            viewModel.increment()
        }
        findViewById<Button>(R.id.btnDecrement).setOnClickListener {
            viewModel.decrement()
        }
        findViewById<Button>(R.id.btnReset).setOnClickListener {
            viewModel.reset()
        }
    }
}

// --- Shared ViewModel between Fragments ---
class SharedViewModel : ViewModel() {
    val selectedItem = MutableLiveData<String>()
}

class ListFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    fun onItemSelected(item: String) {
        viewModel.selectedItem.value = item
    }
}

class DetailFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            // Update UI with selected item
        }
    }
}

// --- Stubs ---
data class User(val name: String, val email: String)
class UserRepository {
    suspend fun getUser(id: Long): User {
        kotlinx.coroutines.delay(500)
        return User("Alice", "alice@example.com")
    }
}
```

---

## ❓ Interview Questions

1. **What is ViewModel and why is it needed?**
   - ViewModel stores UI data that survives configuration changes (rotation). Without it, data would be lost on rotation or you'd need `onSaveInstanceState` for everything. It's lifecycle-aware and has no View reference.

2. **What is LiveData and how is it lifecycle-aware?**
   - LiveData is an observable data holder. It only notifies observers when their lifecycle is at least STARTED. It automatically removes observers when the lifecycle is destroyed, preventing memory leaks and crashes.

3. **What is the difference between `LiveData` and `StateFlow`?**
   - LiveData is lifecycle-aware by default but only works on the main thread. StateFlow is Kotlin-first, works on any dispatcher, but requires `repeatOnLifecycle` for lifecycle awareness. StateFlow requires an initial value; LiveData doesn't.

4. **What is `SavedStateHandle` in ViewModel?**
   - It allows persisting data across process death (not just configuration changes). Data is saved to a Bundle. Use for small amounts of data like IDs, scroll positions, or form input.

5. **How do you share a ViewModel between Fragments?**
   - Use `by activityViewModels<T>()` — both fragments get the ViewModel scoped to the host Activity. Changes in one fragment are immediately visible to the other.

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [Fragment & Lifecycle](../beginner/FragmentLifecycle.md)
