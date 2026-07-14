# Performance & Debugging Scenarios

## Scenario 1: OOM Crash on Large Image Load

### Problem
The app crashes with `OutOfMemoryError` when loading multiple high-resolution images in a RecyclerView.

```kotlin
// ❌ Bad — loads full-size bitmaps into memory
class BadImageLoader {
    fun loadBitmap(url: String): Bitmap {
        // ❌ Original image: 4000x3000 = ~48MB in memory!
        return BitmapFactory.decodeStream(URL(url).openStream())
    }
}

// 10 items in view × 48MB = 480MB → OOM crash
```

### Solution: Downsampling + caching + recycle

```kotlin
// ✅ Good — downsample, cache, and use Glide/Coil
class GoodImageLoader(private val context: Context) {

    // ✅ Method 1: Manual downsampling
    fun loadSampledBitmap(url: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true  // First pass: get dimensions only
        }
        BitmapFactory.decodeStream(URL(url).openStream(), null, options)

        // Calculate sample size
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false  // Second pass: decode with sampling

        return BitmapFactory.decodeStream(URL(url).openStream(), null, options)
    }

    private fun calculateSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var sampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / sampleSize >= reqHeight && halfWidth / sampleSize >= reqWidth) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }
}

// ✅ Method 2: Use Coil (recommended) — handles everything automatically
imageView.load(imageUrl) {
    crossfade(true)
    size(200, 200)                    // Target size — Coil downsamples
    placeholder(R.drawable.placeholder)
    error(R.drawable.error_image)
    memoryCachePolicy(CachePolicy.ENABLED)   // Memory cache
    diskCachePolicy(CachePolicy.ENABLED)     // Disk cache
}

// ✅ Method 3: Use Glide (alternative)
Glide.with(context)
    .load(imageUrl)
    .override(200, 200)               // Target size
    .placeholder(R.drawable.placeholder)
    .into(imageView)
```

### Key Takeaway
- A 4000×3000 image = ~48MB in ARGB_8888 — never load full-size bitmaps
- `inSampleSize` downsamples before decoding — reduces memory dramatically
- **Coil/Glide** handle downsampling, caching, and lifecycle automatically
- Enable both memory and disk caching to avoid re-decoding
- Use `Bitmap.Config.RGB_565` for further memory savings on non-transparent images

---

## Scenario 2: StrictMode Violations — Disk I/O on Main Thread

### Problem
The app occasionally freezes for 1–2 seconds. No crash, but the UI is unresponsive intermittently.

```kotlin
// ❌ Bad — database read on main thread
class BadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ❌ Synchronous Room query on main thread — freezes UI
        val users = db.userDao().getAllUsersSync()
        adapter.submitList(users)
    }
}
```

### Solution: Enable StrictMode + async I/O

```kotlin
// ✅ Step 1: Enable StrictMode in debug builds to catch violations
class DebugApp : Application() {
    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectCustomSlowCalls()
                    .penaltyLog()           // Log to logcat
                    .penaltyDeath()         // Crash in debug
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedSqliteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        }
        super.onCreate()
    }
}

// ✅ Step 2: Make all I/O async
class GoodActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Async via Flow from Room
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    adapter.submitList(users)
                }
            }
        }
    }
}

// ✅ Room DAO — suspend functions are automatically off main thread
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>  // ✅ Reactive Flow — auto-async

    @Query("SELECT * FROM users")
    suspend fun getAllUsersOnce(): List<User>  // ✅ Suspend — runs on Dispatchers.IO

    @Insert
    suspend fun insert(user: User)  // ✅ Suspend — never blocks main thread
}

// ✅ SharedPreferences — also I/O, wrap in withContext
suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
    context.getSharedPreferences("auth", MODE_PRIVATE)
        .edit().putString("token", token).apply()
}
```

### Key Takeaway
- **StrictMode** catches disk I/O, network calls, and leaks on the main thread
- Enable `penaltyDeath()` in debug — fail fast so developers notice
- Use `Flow` or `suspend` functions for Room — they run on a background thread
- SharedPreferences writes are also I/O — wrap in `withContext(Dispatchers.IO)`
- Use `.apply()` (async) instead of `.commit()` (sync) for SharedPreferences

---

## Scenario 3: ANR — Blocked Main Thread

### Problem
The app shows an ANR (Application Not Responding) dialog when the user taps a button that triggers a heavy computation.

```kotlin
// ❌ Bad — CPU-heavy work on main thread
class BadActivity : AppCompatActivity() {
    fun onProcessClick() {
        // ❌ Parses 10,000 JSON objects on main thread — 5+ second block
        val results = parseHugeJson(largeJsonString)
        showResults(results)  // Never reached — ANR first
    }
}
```

### Solution: Move work to background + show progress

```kotlin
// ✅ Good — background computation with progress updates
class ProcessViewModel : ViewModel() {

    private val _state = MutableStateFlow<ProcessState>(ProcessState.Idle)
    val state: StateFlow<ProcessState> = _state

    fun process(json: String) {
        viewModelScope.launch {
            _state.value = ProcessState.Loading(0)

            try {
                // ✅ Heavy work on Default dispatcher (CPU-optimized)
                val results = withContext(Dispatchers.Default) {
                    val parser = JsonParser()
                    val items = parser.parse(json)  // Background

                    val processed = mutableListOf<Result>()
                    items.forEachIndexed { index, item ->
                        processed.add(transform(item))

                        // ✅ Report progress every 10%
                        if (index % (items.size / 10) == 0) {
                            val progress = (index * 100 / items.size)
                            _state.value = ProcessState.Loading(progress)
                        }
                    }
                    processed
                }

                _state.value = ProcessState.Success(results)
            } catch (e: Exception) {
                _state.value = ProcessState.Error(e.message ?: "Processing failed")
            }
        }
    }
}

sealed class ProcessState {
    object Idle : ProcessState()
    data class Loading(val progress: Int) : ProcessState()
    data class Success(val data: List<Result>) : ProcessState()
    data class Error(val message: String) : ProcessState()
}

// ✅ Quick reference — which dispatcher for what?
// Dispatchers.Main     → UI updates, lightweight UI logic
// Dispatchers.Default  → CPU-heavy: parsing, sorting, image processing
// Dispatchers.IO       → I/O: network, database, file reads/writes
// Dispatchers.Unconfined → Testing only (rarely in production)
```

### Key Takeaway
- ANR triggers after **5 seconds** of main thread blocking (10s for broadcast receivers)
- CPU-heavy work → `Dispatchers.Default` (optimized for CPU)
- I/O work → `Dispatchers.IO` (optimized for blocking I/O)
- Report progress via `StateFlow` — UI shows a progress bar
- Break very large operations into chunks with `yield()` to allow cancellation

---

## Scenario 4: Memory Leak Detection with LeakCanary

### Problem
The app's memory usage grows over time. After navigating between screens multiple times, heap dumps show retained destroyed Activities.

```kotlin
// ❌ Bad — Fragment holds a reference to a destroyed Activity
class LeakyFragment : Fragment() {
    private var activityRef: Activity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityRef = context as Activity  // ❌ Holds Activity after detach
    }
}
```

### Solution: LeakCanary + proper lifecycle management

```kotlin
// ✅ Step 1: Add LeakCanary to debug builds
// build.gradle.kts
// dependencies {
//     debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
// }
// → LeakCanary auto-installs, no code needed

// ✅ Step 2: Fix the leak — use weak reference or lifecycle-aware scope
class CleanFragment : Fragment() {
    // ✅ Use viewLifecycleOwner for view-related references
    private val viewModel: MyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Coroutines tied to viewLifecycleOwner — cancelled when view destroyed
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collect { data ->
                binding.textView.text = data
            }
        }
    }

    // ✅ If you must hold a context, use application context
    private lateinit var appContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context.applicationContext  // ✅ App context — no leak
    }
}

// ✅ Step 3: Common leak sources to watch for
// 1. Singleton holding Activity/Fragment context → use applicationContext
// 2. Static reference to View/Activity → clear in onDestroy
// 3. Inner class (non-static) in Activity → use static inner + WeakReference
// 4. Unregistered listeners → unregister in onDestroy/onDestroyView
// 5. Coroutine in GlobalScope → use lifecycleScope/viewModelScope
// 6. Handler with delayed message → removeCallbacks in onDestroy

// ✅ Example: Fixing a handler leak
class CleanActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateUi()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(updateRunnable, 1000)
    }

    override fun onPause() {
        super.onPause()
        // ✅ Remove callbacks — prevents leak
        handler.removeCallbacks(updateRunnable)
    }
}
```

### Key Takeaway
- **LeakCanary** auto-detects leaks in debug builds — zero config needed
- It dumps the heap and shows the reference chain causing the leak
- Use `viewLifecycleOwner` for Fragment view bindings and coroutines
- Use `applicationContext` for long-lived references (singletons, etc.)
- Remove handlers, listeners, and observers in lifecycle destroy methods
- Common leaks: singletons, static fields, inner classes, unregistered listeners

---

## Scenario 5: Debugging a Crash That Only Happens in Production

### Problem
A crash occurs only in release builds or on specific devices. It doesn't reproduce in debug. Crashlytics shows a generic `NullPointerException` with no clear trace.

```kotlin
// ❌ Bad — no error handling, no logging, obfuscated stack traces
class BadRepository(private val api: ApiService) {
    suspend fun getUser(): User {
        val response = api.getUser()  // ❌ No try-catch
        return response.body()!!      // ❌ NPE if body is null (rare in prod)
    }
}
```

### Solution: Crashlytics + defensive coding + logging

```kotlin
// ✅ Step 1: Integrate Firebase Crashlytics
// build.gradle.kts:
// plugins { id("com.google.firebase.crashlytics") }
// dependencies {
//     releaseImplementation("com.google.firebase:firebase-crashlytics-ktx")
// }

// ✅ Step 2: Defensive code with structured error handling
class GoodRepository(private val api: ApiService) {

    suspend fun getUser(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUser()

            if (!response.isSuccessful) {
                // ✅ Log to Crashlytics with context
                FirebaseCrashlytics.getInstance().apply {
                    setCustomKey("http_code", response.code())
                    setCustomKey("api_endpoint", "/user")
                    log("getUser: HTTP ${response.code()}")
                    recordException(IOException("HTTP ${response.code()}"))
                }
                return@withContext Result.failure(IOException("Server error: ${response.code()}"))
            }

            val user = response.body()
                ?: return@withContext Result.failure(IOException("Empty response body"))

            Result.success(user)
        } catch (e: IOException) {
            // ✅ Network error — non-fatal, log with context
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("error_type", "network")
                log("getUser: network error - ${e.message}")
                recordException(e)  // Non-fatal — app doesn't crash
            }
            Result.failure(e)
        } catch (e: Exception) {
            // ✅ Unexpected error — log everything
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("error_type", "unexpected")
                setCustomKey("thread", Thread.currentThread().name)
                log("getUser: unexpected error")
                recordException(e)
            }
            Result.failure(e)
        }
    }
}

// ✅ Step 3: Global exception handler for coroutines
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ✅ Catch uncaught coroutine exceptions
        val handler = CoroutineExceptionHandler { _, throwable ->
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("coroutine_uncaught", throwable::class.java.simpleName)
                log("Uncaught coroutine exception")
                recordException(throwable)
            }
        }
    }
}

// ✅ Step 4: ProGuard rules — keep model classes and stack traces
// proguard-rules.pro:
// -keep class com.example.model.** { *; }
// -keepattributes SourceFile,LineNumberTable
// -renamesourcefileattribute SourceFile

// ✅ Step 5: ViewModel-level error boundary
class UserViewModel(private val repo: GoodRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState<User>>(UiState.Loading)
    val state: StateFlow<UiState<User>> = _state

    fun loadUser() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            repo.getUser()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }
}
```

### Key Takeaway
- **Firebase Crashlytics** captures crashes in production with stack traces
- Use `recordException()` for non-fatal errors — app stays alive, crash is logged
- `setCustomKey()` adds context: HTTP codes, device info, user state
- Always handle `response.body()` being null — `!!` crashes in production
- Keep ProGuard rules for model classes — obfuscated names are unreadable
- Use `CoroutineExceptionHandler` for a global safety net
- `Result<T>` wrapper prevents uncaught exceptions from propagating

---

## 🔗 Related Topics
- [Performance Optimization](../advanced/Performance.md)
- [Testing Strategies](../advanced/Testing.md)
- [Security Best Practices](../advanced/Security.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
