# Performance Optimization

## 📖 Explanation

Performance optimization ensures smooth UX (60fps), low memory usage, and fast startup. Key areas: rendering, memory, startup, and network.

### Key Metrics
| Metric              | Target          | Tool                          |
|---------------------|-----------------|-------------------------------|
| Frame rate          | 60fps (16ms/frame) | GPU Profiler, Systrace     |
| App startup         | < 1.5s (cold)   | Macrobenchmark, Logcat        |
| Memory usage        | No leaks        | LeakCanary, Memory Profiler   |
| ANR (App Not Responding) | < 5s on main thread | StrictMode             |
| APK size            | Minimize        | R8, resource shrinking        |

### Common Performance Issues

#### 1. Jank (Dropped Frames)
- Heavy work on main thread
- Complex view hierarchies
- Overdraw (drawing same pixel multiple times)

#### 2. Memory Leaks
- Static references to Activity/Context
- Unregistered listeners
- Inner classes holding outer reference
- Handler/Runnable posted to main thread

#### 3. Slow Startup
- Heavy work in `Application.onCreate()`
- Synchronous initialization
- Too many content providers

#### 4. Large APK
- Unoptimized images
- Unused resources
- Multiple ABIs

### Optimization Techniques

| Area          | Technique                                      |
|---------------|------------------------------------------------|
| Rendering     | Flatten layout, use ConstraintLayout, avoid overdraw |
| Memory        | Use LeakCanary, weak references, clear disposables |
| Startup       | Lazy init, App Startup library, background init |
| Images        | WebP, vector drawables, Glide/Coil for loading  |
| Network       | Gzip, caching, pagination, prefetch             |
| Database      | Indexes, batch operations, avoid N+1 queries    |
| APK Size      | R8/ProGuard, resource shrinking, app bundles    |
| Coroutines    | Use Dispatchers.IO for blocking, Default for CPU |

### Profiling Tools
| Tool              | Purpose                              |
|-------------------|--------------------------------------|
| Android Profiler  | CPU, memory, network, energy         |
| LeakCanary        | Memory leak detection (runtime)      |
| Macrobenchmark    | Startup and frame timing tests       |
| Layout Inspector  | View hierarchy inspection            |
| GPU Profiler      | Rendering performance                |
| Battery Historian | Battery usage analysis               |

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity

// --- Application with performance setup ---
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Enable StrictMode in debug
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
        }

        // Lazy initialization — don't block startup
        // Use App Startup library for deferred init
    }
}

// --- Lazy initialization ---
class LazyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Only init critical components here
        // Defer analytics, crash reporting to background
        initCriticalOnly()

        // Schedule non-critical init
        thread {
            initAnalytics()
            initCrashReporting()
            initRemoteConfig()
        }
    }
}

// --- Efficient RecyclerView ---
class EfficientAdapter : ListAdapter<Item, ItemViewHolder>(DIFF) {

    // Use payloads for partial updates (avoid full rebind)
    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains("LIKE_UPDATE")) {
            holder.updateLikeCount(getItem(position))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    // Set fixed size to avoid layout calculations
    // recyclerView.setHasFixedSize(true)

    // Use multiple view types efficiently
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.Header -> TYPE_HEADER
            is Item.Content -> TYPE_CONTENT
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(a: Item, b: Item) = a.id == b.id
            override fun areContentsTheSame(a: Item, b: Item) = a == b
        }
    }
}

// --- Memory leak prevention ---
class SafeActivity : AppCompatActivity() {

    private var handler: Handler? = null

    // Use static inner class (no outer reference)
    private class SafeHandler(looper: Looper) : Handler(looper) {
        // Use WeakReference to activity if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use weak reference for callbacks
        val callback = object : Callback {
            // WeakReference to activity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear all references
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
}

// --- Efficient image loading with Coil ---
// build.gradle: implementation("io.coil-kt:coil:2.5.0")
class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageView = ImageView(this)
        imageView.load("https://example.com/image.jpg") {
            crossfade(true)
            placeholder(R.drawable.placeholder)
            error(R.drawable.error)
            // Downsample to target size
            size(720, 1280)
        }
    }
}

// --- Coroutines: correct dispatcher usage ---
class PerformantViewModel : ViewModel() {

    fun loadData() {
        viewModelScope.launch {
            // IO for blocking operations (DB, network, file)
            val data = withContext(Dispatchers.IO) {
                fetchDataFromDisk()
            }

            // Default for CPU-intensive work
            val processed = withContext(Dispatchers.Default) {
                data.map { heavyTransform(it) }
            }

            // Main for UI updates
            _uiState.value = processed
        }
    }
}
```

```groovy
// build.gradle — R8 and shrinking
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
        }
    }
}

dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")
}
```

### Baseline Profiles (Startup Optimization)
```groovy
// Generate baseline-prof.txt for faster startup
dependencies {
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
}
```

---

## ❓ Interview Questions

1. **What causes jank (dropped frames) in Android and how do you fix it?**
   - Jank is caused by frame rendering taking >16ms. Causes: heavy work on main thread, complex layouts, overdraw. Fix: move work to background, flatten layouts, remove background where overlapping, use `withContext(Dispatchers.IO)`.

2. **What are common causes of memory leaks in Android?**
   - Static references to Activity/Context, unregistered listeners, inner classes (non-static) holding outer reference, Handlers with delayed messages, and RxJava/Flow subscriptions not disposed in `onDestroy`.

3. **How do you optimize app startup time?**
   - Lazy initialize non-critical components, use App Startup library, defer analytics/crash reporting to background, avoid synchronous I/O in `Application.onCreate()`, use Baseline Profiles.

4. **What is LeakCanary and how does it work?**
   - LeakCanary detects memory leaks at runtime. It watches destroyed activities/fragments, checks if they're GC'd, and if not, dumps the heap and analyzes the reference chain to find the leak.

5. **How do you reduce APK size?**
   - Enable R8/ProGuard (minify + shrink), use App Bundles (AAB) for per-device APKs, convert PNGs to WebP/vector drawables, remove unused resources, use resource shrinking, and split by ABI.

6. **What is overdraw and how do you reduce it?**
   - Overdraw occurs when the same pixel is drawn multiple times per frame — the GPU draws a background, then another view draws over it. Excessive overdraw wastes GPU cycles and causes jank. Detect with **GPU Overdraw** in Developer Options — colors indicate overdraw levels: blue (1x), green (2x), pink (3x), red (4x+). To reduce: (1) Remove unnecessary backgrounds — if a parent and child both have backgrounds, the parent's is overdrawn. (2) Remove the window background: `getWindow().setBackgroundDrawable(null)` or use a theme without a background. (3) Use `Canvas.clipRect()` in custom views to limit drawing areas. (4) Flatten view hierarchies — use ConstraintLayout instead of nested layouts with backgrounds. (5) Use `View.LAYER_TYPE_NONE` for views that don't need hardware acceleration. Target: no more than 2x overdraw on most pixels.

7. **What are Baseline Profiles and how do they improve startup time?**
   - Baseline Profiles are pre-compiled code paths stored in the APK that tell Android's ART (Ahead-of-Time compiler) which methods to compile to machine code on install. Without profiles, ART uses JIT (Just-in-Time) compilation, which is slower for the first few runs. Baseline Profiles: (1) Are generated by running Macrobenchmark tests on a real device with `BaselineProfileRule`. (2) Contain a list of classes and methods to pre-compile. (3) Reduce startup time by 20-40%. (4) Improve frame rendering for critical user journeys. Steps to add: (1) Add `androidx.profileinstaller:profileinstaller`. (2) Create a `BaselineProfile` module with Macrobenchmark. (3) Run the benchmark to generate `baseline-prof.txt`. (4) Include the profile in the app module. (5) Use `androidx.benchmark:macro-junit4` for testing. Google Play automatically uses Baseline Profiles for cloud compilation.

8. **How do you identify and fix memory leaks in Android?**
   - **Identify**: (1) **LeakCanary** (debug builds) — automatically watches destroyed Activities/Fragments and reports leaks with a reference chain. (2) **Memory Profiler** in Android Studio — take heap dumps, find retained objects. (3) **Memory Profiler's Leak Detection** — highlights suspected leaks. **Common leaks**: (1) Static reference to Activity/Context. (2) Inner (non-static) class holding outer Activity reference — fix: make it `static` or use a separate class. (3) `Handler` with delayed messages — fix: `removeCallbacksAndMessages(null)` in `onDestroy()`. (4) Unregistered listeners — fix: unregister in `onDestroy()`. (5) RxJava/Flow subscriptions not disposed — fix: `CompositeDisposable` or `lifecycleScope`. (6) Singleton holding Activity Context — fix: use `applicationContext`. (7) View references in ViewModels — fix: ViewModels must never reference Views. (8) Static `Bitmap`/`Drawable` — fix: clear in `onDestroy()`.

9. **What is StrictMode and how does it help with performance?**
   - `StrictMode` is a development tool that detects accidental disk/network operations on the main thread and other violations. Configure in `Application.onCreate()` (debug only): `StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().detectCustomSlowCalls().penaltyLog().penaltyDeath().build())`. Also: `StrictMode.setVmPolicy(VmPolicy.Builder().detectActivityLeaks().detectLeakedClosableObjects().detectLeakedSqlLiteObjects().penaltyLog().build())`. `penaltyLog()` logs violations to Logcat. `penaltyDeath()` crashes the app (use sparingly). `penaltyFlashScreen()` flashes the screen. StrictMode catches: disk I/O on main thread (SQLite, file access), network on main thread, resource leaks (unclosed cursors, connections), and Activity leaks. Always disable in release builds (`if (BuildConfig.DEBUG)`).

10. **How do you optimize RecyclerView for smooth scrolling?**
    - (1) Use `ListAdapter` with DiffUtil — efficient item updates, no full `notifyDataSetChanged()`. (2) Set `setHasFixedSize(true)` if the RecyclerView's size doesn't depend on content. (3) Use `setHasStableIds(true)` with `getItemId()` — improves animations and scroll position restoration. (4) Avoid heavy work in `onBindViewHolder` — precompute data, use Glide/Coil for images. (5) Use `recyclerView.setItemViewCacheSize(20)` for lists that scroll back and forth. (6) Share `RecycledViewPool` for nested RecyclerViews. (7) Use `prefetch` — `layoutManager.isItemPrefetchEnabled = true` (default on). (8) Use `DiffUtil` payloads for partial updates: `onBindViewHolder(holder, position, payloads)`. (9) Avoid `wrap_content` for RecyclerView height — use `match_parent` or fixed height. (10) Use `ConcatAdapter` instead of multi-view-type adapters for better separation. (11) Profile with `RecyclerView` item animation off if not needed: `recyclerView.itemAnimator = null`.

11. **What is the difference between `Dispatchers.IO`, `Dispatchers.Default`, and `Dispatchers.Main`?**
    - `Dispatchers.Main` — runs on the main/UI thread. Use for UI updates, lifecycle operations, and lightweight work. All Android UI operations must be on this thread. Backed by a single thread. `Dispatchers.IO` — optimized for blocking I/O operations (file, network, database). Backed by a pool of 64 threads (configurable). Use for `Retrofit` calls, Room queries (Room handles this automatically), file reads/writes. Never do CPU-intensive work here — use `Default` instead. `Dispatchers.Default` — optimized for CPU-intensive work (parsing, sorting, calculations, image processing). Backed by a pool of threads equal to the CPU core count. Use for heavy computation. `Dispatchers.Unconfined` — runs on the calling thread, then resumes wherever it was suspended. Rarely used. Rule of thumb: UI → Main, I/O → IO, CPU → Default. Switch with `withContext(Dispatchers.IO) { ... }`.

12. **How do you profile and optimize app startup time?**
    - **Cold start** (process not in memory): target < 1.5s. **Warm start** (process alive, Activity recreated): target < 0.5s. **Tools**: (1) `adb shell am start -W -n package/.MainActivity` — shows `TotalTime`, `WaitTime`, `Displayed` time. (2) **CPU Profiler** in Android Studio — see which methods run during startup. (3) **Macrobenchmark** — automated startup measurement. (4) Logcat with `ActivityManager: Displayed`. **Optimizations**: (1) Move non-critical initialization out of `Application.onCreate()` — use the App Startup library or `androidx.startup`. (2) Defer analytics, crash reporting, and remote config to a background thread. (3) Use `androidx.startup.Initializer` for lazy initialization. (4) Reduce the number of Content Providers initialized at startup (each `ContentProvider`'s `onCreate` runs before `Application.onCreate`). (5) Use Baseline Profiles. (6) Lazy-initialize ViewModels — don't fetch data in `init {}`, fetch when the screen is visible. (7) Use `android:windowBackground` for instant splash screen. (8) Avoid heavy dependency injection setup at startup — use lazy initialization.

---

## 🔗 Related Topics
- [RecyclerView & Adapter Patterns](../beginner/RecyclerView.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
