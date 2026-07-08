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

---

## 🔗 Related Topics
- [RecyclerView & Adapter Patterns](../beginner/RecyclerView.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
