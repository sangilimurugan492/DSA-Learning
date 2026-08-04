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

6. **What is the difference between `map`, `switchMap`, and `flatMapLatest` with LiveData/Flow?**
   - `map` transforms the emitted value synchronously — `liveData.map { it.size }` returns a new LiveData. Use for simple transformations. `switchMap` transforms the value to another LiveData and switches to it — `liveData.switchMap { id -> repository.getUser(id) }`. Each new emission cancels the previous inner LiveData. Use when the transformation involves an async operation. `flatMapLatest` (Flow) is the Flow equivalent of `switchMap` — it collects the latest inner Flow and cancels the previous. Use `map` for sync transforms, `switchMap`/`flatMapLatest` when the transform returns another observable. Example: a search query LiveData that triggers a network call — use `switchMap` so typing a new query cancels the previous search.

7. **What is `viewModelScope` and how does it manage coroutines?**
   - `viewModelScope` is a `CoroutineScope` tied to the ViewModel's lifecycle. It uses `Dispatchers.Main.immediate` by default. When the ViewModel is cleared (`onCleared()`), the scope is cancelled — all coroutines launched in it are automatically cancelled, preventing leaks. Use it for all coroutine launches in the ViewModel: `viewModelScope.launch { val data = repository.fetch() }`. You don't need to manually cancel — structured concurrency handles it. For custom dispatchers in tests, use `MainDispatcherRule` to set a `TestDispatcher`. Never use `GlobalScope` in a ViewModel — it's not tied to any lifecycle and causes leaks. For foreground operations, use `viewModelScope`. For background work that survives process death, use WorkManager.

8. **What is `StateFlow` and how is it different from `MutableStateFlow`?**
   - `StateFlow` is a read-only hot flow that always has a value and conflate emissions (only the latest value matters). `MutableStateFlow` is the writable version — you set `.value` to update. The pattern: expose `StateFlow` (read-only) to the UI, keep `MutableStateFlow` (private) in the ViewModel. `val _state = MutableStateFlow(UiState()); val state: StateFlow<UiState> = _state.asStateFlow()`. StateFlow requires an initial value (unlike LiveData). It's always active (hot) — collectors get the current value immediately on collection. StateFlow conflate — if you set `.value` rapidly, only the latest is emitted to new collectors. Use `StateFlow` for UI state. Use `SharedFlow` for one-time events (navigation, snackbar). Unlike LiveData, StateFlow is not lifecycle-aware by default — use `repeatOnLifecycle(STARTED) { collect }`.

9. **What is `repeatOnLifecycle` and why is it needed with Flow?**
   - `repeatOnLifecycle(state) { }` automatically starts and stops collecting a Flow based on the lifecycle. Without it, collecting a Flow in `onCreate` would keep collecting even when the Activity is in the background — wasting resources and potentially crashing. `repeatOnLifecycle(STARTED) { viewModel.state.collect { ... } }` starts collecting when the lifecycle reaches STARTED and cancels when it drops below STARTED. When the lifecycle comes back to STARTED, it re-collects. This prevents crashes (updating UI when not visible) and saves battery. LiveData does this automatically — Flow requires `repeatOnLifecycle`. Use `collectAsStateWithLifecycle()` in Compose for the same behavior. Always use `repeatOnLifecycle` when collecting Flows in Activities/Fragments.

10. **What is `SharedFlow` and when do you use it over `StateFlow`?**
    - `SharedFlow` is a hot flow for broadcasting events to multiple collectors. Unlike `StateFlow`: (1) No initial value required. (2) Can replay multiple previous values (`replay = N`). (3) Doesn't conflate — all emissions are delivered. Use `SharedFlow` for **one-time events** like navigation, showing a Snackbar, or showing a Toast — events that shouldn't be re-emitted on configuration change. Use `StateFlow` for **state** — data that represents the current UI state and should survive rotation. Pattern: `val _events = MutableSharedFlow<UiEvent>(); val events = _events.asSharedFlow()`. Emit: `_events.tryEmit(ShowSnackbar("Saved!"))`. Collect with `repeatOnLifecycle`. `replay = 0` (default) means new collectors don't receive past events — perfect for navigation.

11. **How do you handle process death and restore ViewModel state?**
    - `SavedStateHandle` allows storing small amounts of data that survive process death. Unlike regular ViewModel data (lost on process kill), `SavedStateHandle` data is saved to a Bundle. Usage: `class MyViewModel(state: SavedStateHandle) : ViewModel() { val name = state.getLiveData("name", "") }`. Store primitives, strings, Parcelable objects. Don't store large objects (1MB Bundle limit). For complex data, store an ID in `SavedStateHandle` and fetch the full object from Room/DataStore. `SavedStateHandle` also works with `StateFlow`: `val state = state.getStateFlow("key", defaultValue)`. This is the modern replacement for `onSaveInstanceState`. Hilt automatically injects `SavedStateHandle` into `@HiltViewModel`.

12. **What are common ViewModel mistakes and how to avoid them?**
    - (1) **Holding View references** — ViewModel must never reference Views, Activities, or Contexts. This causes memory leaks. Fix: expose state via Flow/LiveData. (2) **Doing UI logic in ViewModel** — like showing Toast or navigating. Fix: emit events via SharedFlow, handle in the View. (3) **Using `GlobalScope`** — leaks coroutines. Fix: use `viewModelScope`. (4) **Exposing MutableStateFlow** — allows the UI to modify state directly. Fix: expose read-only `StateFlow`. (5) **Not handling loading/error states** — UI freezes on network errors. Fix: use sealed class `UiState<T>` with Loading/Success/Error. (6) **Storing too much in ViewModel** — it's for UI state, not a data cache. Fix: use Repository/Room for caching. (7) **Not testing ViewModels** — they're pure logic, easy to unit test. Fix: test with `runTest` and mock dependencies.

---

## 🔗 Related Topics
- [Architecture Patterns](ArchitecturePatterns.md)
- [Fragment & Lifecycle](../beginner/FragmentLifecycle.md)
