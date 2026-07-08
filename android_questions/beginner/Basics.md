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
   - Activity (UI screen), Service (background work), Broadcast Receiver (system events), Content Provider (data sharing).

2. **What is the role of AndroidManifest.xml?**
   - It declares all app components, permissions, hardware requirements, and metadata. The system reads it to understand the app's structure.

3. **What is the difference between `minSdk`, `targetSdk`, and `compileSdk`?**
   - `minSdk` — minimum Android version to run. `targetSdk` — the version the app is tested against (enables latest behaviors). `compileSdk` — the SDK version used to compile.

4. **What is the difference between `dp` and `sp`?**
   - `dp` (density-independent pixel) — for layout sizing. `sp` (scale-independent pixel) — for text, respects user's font size settings.

5. **What is the difference between `implementation` and `api` in Gradle?**
   - `implementation` — dependency is not exposed to other modules (better encapsulation, faster builds). `api` — dependency is exposed transitively to consumers.

---

## 🔗 Related Topics
- [Activity & Lifecycle](ActivityLifecycle.md)
- [UI Layouts & Views](UILayouts.md)
