# Android Basics & Project Structure

## 📖 Explanation

Android is an open-source operating system for mobile devices. Android apps are built using the Android SDK and can be written in Kotlin or Java.

### Android App Components
| Component   | Description                                              |
|------------|----------------------------------------------------------|
| Activity   | A single screen with a UI                               |
| Service    | Background operations (no UI)                           |
| Broadcast Receiver | Responds to system-wide broadcasts           |
| Content Provider | Shares data between apps                        |

### Project Structure
```
app/
├── manifests/
│   └── AndroidManifest.xml      # App metadata, permissions, components
├── java/
│   └── com.example.app/
│       └── MainActivity.kt     # App code
├── res/
│   ├── layout/                  # XML layouts
│   ├── drawable/                # Images, icons
│   ├── mipmap/                  # App launcher icons
│   ├── values/                  # strings.xml, colors.xml, themes
│   └── font/                    # Custom fonts
├── build.gradle (Module)        # Module-level build config
└── build.gradle (Project)       # Project-level build config
```

### AndroidManifest.xml
The manifest declares all app components, permissions, and metadata.

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.app">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.App">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Build System (Gradle)
- **Project-level** `build.gradle` — Configures repositories and plugins for all modules.
- **Module-level** `build.gradle` — Dependencies, SDK versions, build types, product flavors.

### Key Files
| File              | Purpose                              |
|-------------------|--------------------------------------|
| `strings.xml`      | All string resources (i18n)          |
| `colors.xml`       | Color palette                       |
| `themes.xml`       | App theme and styles                 |
| `build.gradle`     | Dependencies and build config        |

---

## 🧪 Code Example

```kotlin
// MainActivity.kt — Entry point of the app
package com.example.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        textView.text = getString(R.string.welcome_message)
    }
}
```

```xml
<!-- activity_main.xml -->
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center"
    android:orientation="vertical">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="24sp" />
</LinearLayout>
```

```xml
<!-- strings.xml -->
<resources>
    <string name="app_name">My App</string>
    <string name="welcome_message">Welcome to Android!</string>
</resources>
```

```groovy
// build.gradle (Module: app)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
}
```

---

## ❓ Interview Questions

1. **What are the four main components of an Android app?**
   - Activity (UI screen), Service (background work), Broadcast Receiver (system events), Content Provider (data sharing). Each component has a unique lifecycle and is registered in the AndroidManifest.xml. Activities and Services are started via Intents, Broadcast Receivers are triggered by system/app events, and Content Providers are accessed via a ContentResolver URI.

2. **What is the role of AndroidManifest.xml?**
   - It declares all app components, permissions, hardware requirements, and metadata. The system reads it to understand the app's structure. Without manifest registration, components won't be recognized by the system. It also defines the app's package name, minimum SDK version, theme, and launcher activity.

3. **What is the difference between `minSdk`, `targetSdk`, and `compileSdk`?**
   - `minSdk` — minimum Android version required to run the app (lower versions won't install). `targetSdk` — the version the app is tested against; it enables latest platform behaviors and security enforcement. `compileSdk` — the SDK version used at compile time (must be ≥ `targetSdk`). Best practice: `compileSdk` = `targetSdk` ≥ `minSdk`.

4. **What is the difference between `dp` and `sp`?**
   - `dp` (density-independent pixel) — for layout sizing, ensures consistent physical size across screens with different pixel densities. `sp` (scale-independent pixel) — for text, respects user's font size accessibility settings. Always use `sp` for text and `dp` for everything else.

5. **What is the difference between `implementation` and `api` in Gradle?**
   - `implementation` — dependency is not exposed to other modules (better encapsulation, faster builds since changes don't trigger recompilation of consumers). `api` — dependency is exposed transitively to consumers, meaning other modules can access it. Use `api` only when consumers need direct access to the dependency.

6. **What is an Intent and what are its types?**
   - An Intent is a messaging object used to request an action from another component. **Explicit Intents** specify the exact target component (e.g., `Intent(this, SecondActivity::class.java)`). **Implicit Intents** declare a general action to perform (e.g., `ACTION_VIEW` with a URL), and the system finds a matching component via Intent Filters.

7. **What is the difference between `applicationId` and `package` name?**
   - `applicationId` (in `build.gradle`) uniquely identifies your app on the Play Store and device. The `package` name (in manifest or `namespace` in Gradle) is used for R class generation and source code organization. They can differ — `applicationId` can change per build variant (e.g., debug vs release), while `namespace` stays constant.

8. **What is the Android Jetpack library and why is it useful?**
   - Android Jetpack is a suite of libraries, tools, and guidance to help developers write high-quality apps. It includes AndroidX libraries (AppCompat, RecyclerView, Navigation, Room, ViewModel, LiveData, WorkManager). Benefits: backward compatibility, reduced boilerplate, consistent architecture, and less platform-specific code.

9. **What is the difference between `val` and `var` in Kotlin, and how does this relate to Android development?**
   - `val` is read-only (assigned once), `var` is mutable. In Android, prefer `val` for views, references, and collections to prevent accidental reassignment. For ViewModels and state, use `val` with `MutableStateFlow`/`MutableLiveData` internally — the reference is immutable but the content is mutable.

10. **What is `Context` in Android and what are its types?**
    - `Context` provides access to application-specific resources and classes. **Application Context** (`applicationContext`) — tied to app lifecycle, use for singletons, database. **Activity Context** — tied to activity lifecycle, use for UI inflation, launching activities. **View Context** — the context of the view. Using the wrong context can cause memory leaks (e.g., holding Activity context in a singleton).

11. **What is the difference between `Activity` and `AppCompatActivity`?**
    - `AppCompatActivity` extends `FragmentActivity` which extends `Activity`. It provides backward compatibility for features like ActionBar, Material Design, and dark mode on older Android versions. Always use `AppCompatActivity` as the base class for activities to ensure consistent behavior across API levels.

12. **What are build types and product flavors in Gradle?**
    - **Build Types** (e.g., `debug`, `release`) define build configuration like signing, minification, debuggability. **Product Flavors** (e.g., `free`, `paid`) define app variants with different features, packages, or resources. Combined, they create build variants (e.g., `freeDebug`, `paidRelease`). This enables multi-environment builds from a single codebase.

13. **What is the `Application` class and when would you override it?**
    - The `Application` class is the base class for maintaining global application state. You override it when you need to: (1) Initialize global libraries (e.g., Firebase, Dagger, Timber) in `onCreate()`. (2) Maintain shared state across all activities. (3) Handle app-level lifecycle callbacks via `ActivityLifecycleCallbacks`. Avoid storing mutable global state in the Application class — use a DI framework or ViewModel instead. The `onCreate()` is called before any activity, service, or receiver is created.

14. **What is an `ADB` (Android Debug Bridge) and what are common commands?**
    - ADB is a command-line tool for communicating with an Android device/emulator. Common commands: `adb devices` (list connected devices), `adb install <apk>` (install an app), `adb uninstall <package>` (uninstall), `adb shell` (open a shell on device), `adb logcat` (view logs), `adb push/pull` (transfer files), `adb shell am start -n com.example/.MainActivity` (launch an activity). ADB runs as a client-server process — the server runs on your machine and communicates with the daemon on the device via USB or TCP.

15. **What is the difference between `Parcelable` and `Serializable`?**
    - `Serializable` is a Java interface that uses reflection — it's slow and creates many temporary objects. `Parcelable` is Android-specific and requires implementing `writeToParcel()` and `createFromParcel()` — the developer explicitly serializes fields, making it 10x+ faster than `Serializable`. Use `@Parcelize` (Kotlin plugin) to auto-generate the implementation. Always prefer `Parcelable` for passing data between activities/fragments via Intents or Bundles.

16. **What is `R.java` / `R` class in Android and how does it work?**
    - The `R` class is auto-generated by the build system. It maps resource references (in `res/`) to integer IDs. When you use `R.string.app_name` or `R.layout.activity_main`, the compiler resolves it to an integer that the Android runtime uses to find the actual resource. In Kotlin, `R` is accessible directly. The `R` class is regenerated on every build, so you never edit it manually. Different modules have their own `R` classes (e.g., `com.example.app.R` vs `com.example.lib.R`).

17. **What is the difference between `wrap_content`, `match_parent`, and `0dp` (in ConstraintLayout)?**
    - `wrap_content` — the view sizes to its content. `match_parent` — the view fills the available space of its parent. `0dp` (with `match_constraint` in ConstraintLayout) — the view expands to fill constraints defined by `app:layout_constraintStart_toEndOf`, etc. In a `LinearLayout` with weights, `0dp` lets weight determine size. Always prefer `0dp` in ConstraintLayout over `match_parent` for proper constraint-based sizing.

---

## 🔗 Related Topics
- [Activity & Lifecycle](ActivityLifecycle.md)
- [UI Layouts & Views](UILayouts.md)
