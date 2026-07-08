# Lifecycle & Memory Leak Scenarios

## Scenario 1: Memory Leak with AsyncTask

### Problem
An AsyncTask holds an Activity reference. When the Activity is destroyed, the task is still running, leaking the Activity.

```kotlin
// ❌ Bad — AsyncTask leaks Activity
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DownloadTask().execute("https://example.com/file")
    }

    inner class DownloadTask : AsyncTask<String, Int, String>() {
        override fun doInBackground(vararg params: String): String {
            Thread.sleep(5000)
            return "Done"
        }

        override fun onPostExecute(result: String) {
            // Holds reference to MyActivity — leak!
            findViewById<TextView>(R.id.text).text = result
        }
    }
}
```

### Solution: Use Coroutines with lifecycleScope

```kotlin
// ✅ Good — Coroutines auto-cancel on destroy
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // lifecycleScope auto-cancels in onDestroy
        lifecycleScope.launch {
            val result = downloadFile("https://example.com/file")
            findViewById<TextView>(R.id.text).text = result
        }
    }

    private suspend fun downloadFile(url: String): String =
        withContext(Dispatchers.IO) {
            delay(5000)
            "Done"
        }
}
```

### Key Takeaway
- `lifecycleScope` cancels coroutines when the lifecycle is destroyed
- No inner class holding Activity reference
- Coroutines are the modern replacement for AsyncTask
- Use `repeatOnLifecycle(STARTED)` for Flow collection

---

## Scenario 2: ViewModel Surviving Configuration Change

### Problem
User rotates the screen and all loaded data is lost — the Activity is recreated.

```kotlin
// ❌ Bad — data lost on rotation
class BadActivity : AppCompatActivity() {
    var users: List<User> = emptyList()  // Lost on rotation!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()  // Reloads every rotation
    }

    private fun loadData() {
        lifecycleScope.launch {
            users = repository.getUsers()  // Network call on every rotation
        }
    }
}
```

### Solution: Use ViewModel

```kotlin
// ✅ Good — ViewModel survives rotation
class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init { loadUsers() }

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repo.getUsers()
        }
    }
}

class GoodActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Collect — only loads once, survives rotation
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    adapter.submitList(users)
                }
            }
        }
    }
}
```

### Key Takeaway
- ViewModel survives configuration changes (rotation)
- Data is loaded once, not on every rotation
- `repeatOnLifecycle(STARTED)` pauses collection when in background
- Use `SavedStateHandle` for surviving process death

---

## Scenario 3: Fragment Not Receiving Result

### Problem
Fragment A starts Fragment B. When B returns, A doesn't receive the result.

```kotlin
// ❌ Bad — old approach, fragile
class FragmentA : Fragment() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Fragile — magic numbers, easy to break
        }
    }
}
```

### Solution: Fragment Result API

```kotlin
// ✅ Good — Fragment Result API
class FragmentA : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Register listener BEFORE navigation
        parentFragmentManager.setFragmentResultListener("requestKey", viewLifecycleOwner) { _, bundle ->
            val result = bundle.getString("result")
            textView.text = result
        }

        button.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.container, FragmentB())
                addToBackStack(null)
            }
        }
    }
}

class FragmentB : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        button.setOnClickListener {
            val result = Bundle().apply { putString("result", "Hello from B") }
            parentFragmentManager.setFragmentResult("requestKey", result)
            parentFragmentManager.popBackStack()
        }
    }
}
```

### Key Takeaway
- `setFragmentResultListener` must be registered before navigation
- Use `viewLifecycleOwner` — not `this` (Fragment lifecycle is longer)
- `setFragmentResult` sends result to all registered listeners
- Type-safe, no magic request codes

---

## Scenario 4: Multiple Observers Causing Duplicate Updates

### Problem
LiveData observer is set in `onCreate`, but after rotation it fires again, causing duplicate network calls.

```kotlin
// ❌ Bad — observer triggers reload on rotation
class BadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.users.observe(this) { users ->
            adapter.submitList(users)
            viewModel.loadUsers()  // ❌ Triggers reload on every rotation!
        }
    }
}
```

### Solution: Separate observation from action

```kotlin
// ✅ Good — observe once, load in init
class UserViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        loadUsers()  // Load once when ViewModel is created
    }

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repository.getUsers()
        }
    }
}

class GoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Observe — just update UI, don't trigger reload
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    adapter.submitList(users)  // Just update UI
                }
            }
        }

        // Manual refresh button
        refreshButton.setOnClickListener {
            viewModel.loadUsers()  // Explicit user action
        }
    }
}
```

### Key Takeaway
- Don't trigger data loads inside observers
- Load data in `ViewModel.init` or on explicit user action
- Observers should only update UI, not trigger side effects
- `StateFlow` only emits when value changes (no duplicate emissions)

---

## Scenario 5: Leak via Singleton Holding Context

### Problem
A singleton holds an Activity context, preventing it from being garbage collected.

```kotlin
// ❌ Bad — singleton holds Activity context
object ImageLoader {
    private var context: Context? = null

    fun init(context: Context) {
        this.context = context  // If Activity context → leak!
    }

    fun load(url: String) {
        context?.let { /* load image */ }
    }
}

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ImageLoader.init(this)  // ❌ Passes Activity context
    }
}
```

### Solution: Use Application context

```kotlin
// ✅ Good — use Application context
object ImageLoader {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext  // Application context — no leak
    }

    fun load(url: String) {
        // appContext is safe — lives as long as the app
    }
}

class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ImageLoader.init(applicationContext)  // ✅ Application context
        // Or: ImageLoader.init(this.applicationContext)
    }
}

// ✅ Even better — initialize in Application class
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ImageLoader.init(this)  // `this` IS the Application context
    }
}
```

### Key Takeaway
- Singletons outlive Activities — never hold Activity context in them
- Use `context.applicationContext` for long-lived references
- Initialize in `Application.onCreate()` for singletons
- Activity context = short-lived, Application context = app-lifetime

---

## 🔗 Related Topics
- [Activity & Lifecycle](../beginner/ActivityLifecycle.md)
- [ViewModel & LiveData](../intermediate/ViewModelLiveData.md)
- [Performance Optimization](../advanced/Performance.md)
