# KMP Project Setup — Interview Questions

## 🔴 Q1: How do you create a new KMP project?
**Answer:** Use the KMP wizard or Android Studio KMP plugin:

1. **KMP Wizard:** [kmp.jetbrains.com](https://kmp.jetbrains.com)
2. **Android Studio:** New Project → Kotlin Multiplatform app
3. **Manual:** Add `kotlin-multiplatform` plugin to `build.gradle.kts`

```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform") version "1.9.22"
    id("com.android.library")
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val iosMain by creating
    }
}
```

---

## 🔴 Q2: What is the typical KMP project structure?
**Answer:**

```
MyApp/
├── settings.gradle.kts
├── build.gradle.kts
├── shared/                    ← KMP module
│   ├── build.gradle.kts
│   ├── src/
│   │   ├── commonMain/
│   │   │   └── kotlin/
│   │   ├── androidMain/
│   │   │   └── kotlin/
│   │   └── iosMain/
│   │       └── kotlin/
├── androidApp/                ← Android app
│   └── build.gradle.kts
└── iosApp/                     ← iOS app
    └── iosApp.xcodeproj
```

---

## 🔴 Q3: How do you configure iOS targets in KMP?
**Answer:**

```kotlin
kotlin {
    // Three iOS targets for universal binary support
    iosX64()              // Intel simulator
    iosArm64()            // Physical device
    iosSimulatorArm64()   // Apple Silicon simulator
    
    // Or use the shortcut (Kotlin 1.9.20+)
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    // Group into a single iosMain source set
    sourceSets {
        val iosMain by creating {
            dependsOn(commonMain.get())
        }
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}
```

---

## 🟡 Q4: What is the `cocoapods` plugin in KMP?
**Answer:** The `co.cocoapods` plugin integrates KMP with CocoaPods for iOS dependency management:

```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

kotlin {
    cocoapods {
        summary = "Shared module for iOS"
        homepage = "https://example.com"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        
        pod("Alamofire") {
            version = "5.6.0"
        }
    }
}
```

This generates a `.podspec` and makes the KMP module consumable via CocoaPods.

---

## 🔴 Q5: How do you consume a KMP module from iOS?
**Answer:** Two approaches:

**Approach 1: CocoaPods**
```ruby
# Podfile
target 'iosApp' do
  use_frameworks!
  pod 'shared', :path => '../shared'
end
```

**Approach 2: Direct framework embedding**
```kotlin
// build.gradle.kts
kotlin {
    iosX64("ios") {
        binaries {
            framework {
                baseName = "Shared"
                isStatic = true  // or false for dynamic
            }
        }
    }
}
```

Then in Xcode: Add the `.framework` to "Frameworks, Libraries, and Embedded Content".

---

## 🟡 Q6: What is the `androidTarget()` vs `android()` function?
**Answer:**
- `android()` — deprecated since Kotlin 1.9.20
- `androidTarget()` — the replacement, works with the Android Gradle Plugin

```kotlin
kotlin {
    androidTarget()  // ✅ Current
    // android()     // ❌ Deprecated
}
```

---

## 🔴 Q7: How do you configure the Android target in a KMP module?
**Answer:**

```kotlin
kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
}

android {
    namespace = "com.example.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```

---

## 🟡 Q8: What is the `kotlin-android-extensions` vs `kotlin-multiplatform` plugin?
**Answer:**
- `kotlin-android` — for Android-only Kotlin projects
- `kotlin-multiplatform` — for KMP projects, includes Android target support
- In KMP, you use `kotlin("multiplatform")` + `id("com.android.library")` (or `com.android.application`)

---

## 🟡 Q9: How do you set up a KMP module alongside an existing Android app?
**Answer:**

1. Create a new `shared/` module with `kotlin-multiplatform` plugin
2. Configure targets (Android + iOS)
3. Add the shared module as a dependency in the Android app:

```kotlin
// androidApp/build.gradle.kts
dependencies {
    implementation(project(":shared"))
}
```

4. In `settings.gradle.kts`:
```kotlin
include(":androidApp")
include(":shared")
```

---

## 🟢 Q10: What is the `kotlin.mpp.enableCInteropCommonization` flag?
**Answer:** This Gradle property enables sharing of C interop declarations across native targets:

```properties
# gradle.properties
kotlin.mpp.enableCInteropCommonization=true
```

This allows you to define a `cinterop` in one native target and use it in `nativeMain` or other native targets.

---

## 🟡 Q11: How do you publish a KMP library?
**Answer:**

```kotlin
// build.gradle.kts
plugins {
    `maven-publish`
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
}

publishing {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
}
```

The published artifact includes:
- `.jar` for JVM
- `.aar` for Android
- `.framework` for iOS
- `.klib` for Kotlin/Native

---

## 🟡 Q12: What is the `kotlin.mpp.stability` configuration?
**Answer:** Controls the stability level of KMP features:

```properties
# gradle.properties
kotlin.mpp.stability.nowarn=true  # Suppress warnings about experimental features
```

---

## 📌 Key Takeaways
- Use KMP Wizard or Android Studio plugin for project creation
- Three iOS targets needed: `iosX64`, `iosArm64`, `iosSimulatorArm64`
- Consume KMP from iOS via CocoaPods or direct framework
- `androidTarget()` replaces deprecated `android()`
- Publish KMP libraries with `maven-publish` plugin

---

[← Basics](Basics.md) | [Back to README](../README.md) | [Next: Common Code →](CommonCode.md)
