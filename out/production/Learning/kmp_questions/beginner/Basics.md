# KMP Basics — Interview Questions

## 🔴 Q1: What is Kotlin Multiplatform (KMP)?
**Answer:** KMP is a technology that allows you to write shared code in Kotlin that runs on multiple platforms (Android, iOS, JVM, JS, WebAssembly, etc.). You write business logic once in the `commonMain` source set and platform-specific implementations in `androidMain`, `iosMain`, etc.

```kotlin
// commonMain — shared code
expect fun platformName(): String

// androidMain
actual fun platformName(): String = "Android"

// iosMain
actual fun platformName(): String = "iOS"
```

**Key points:**
- Code sharing at the **source level**, not binary level
- Kotlin compiles to platform-specific targets (JVM bytecode, JS, native binaries)
- Interoperable with platform-native languages (Java, Swift, Obj-C)

---

## 🔴 Q2: What is the difference between KMP and Flutter/React Native?
**Answer:**

| Aspect | KMP | Flutter | React Native |
|--------|-----|---------|-------------|
| Language | Kotlin | Dart | JavaScript |
| UI | Native (or Compose Multiplatform) | Custom rendering engine | Native components |
| Code sharing | Business logic only | UI + logic | UI + logic |
| Performance | Native | Near-native (Skia) | Bridge overhead |
| Interop | Full native interop | Platform channels | Bridge |
| Learning curve | Low for Kotlin devs | New language + framework | Low for JS devs |

KMP shares **logic**, not UI (unless using Compose Multiplatform). Flutter/RN share both.

---

## 🔴 Q3: What is `commonMain`?
**Answer:** `commonMain` is the shared source set in a KMP project. Code here is platform-agnostic and compiled to all targets. It can only use:
- Kotlin standard library (common subset)
- KMP-compatible libraries
- `expect` declarations (implemented per platform)

```kotlin
// commonMain/src/Calculator.kt
class Calculator {
    fun add(a: Int, b: Int): Int = a + b
    fun multiply(a: Int, b: Int): Int = a * b
}
```

---

## 🔴 Q4: What are the supported KMP targets?
**Answer:**

| Target | Compile to | Source set |
|--------|-----------|------------|
| Android | JVM bytecode / DEX | `androidMain` |
| iOS | Native binary (framework) | `iosMain` |
| JVM | JVM bytecode | `jvmMain` |
| JS | JavaScript | `jsMain` |
| Wasm | WebAssembly | `wasmMain` |
| macOS | Native binary | `macosX64Main` / `macosArm64Main` |
| Linux | Native binary | `linuxX64Main` |
| watchOS | Native binary | `watchosX64Main` / `watchosArm64Main` |
| tvOS | Native binary | `tvosX64Main` / `tvosArm64Main` |

---

## 🟡 Q5: What is the Kotlin/Native?
**Answer:** Kotlin/Native is the technology that compiles Kotlin code to standalone native binaries (no JVM required). It uses **LLVM** as the backend compiler. This is what enables KMP to target iOS, macOS, Linux, etc.

- Produces `.framework` for iOS (consumable from Swift/Obj-C)
- Produces `.dylib`, `.so`, `.dll` for desktop/server
- No garbage collector — uses automatic reference counting (ARC) on iOS

---

## 🔴 Q6: What is the difference between KMP and Kotlin/Native?
**Answer:**
- **KMP** = the overall technology for sharing code across platforms
- **Kotlin/Native** = the specific compiler backend that compiles Kotlin to native binaries (LLVM)
- KMP uses Kotlin/JVM for Android/JVM targets and Kotlin/Native for iOS/macOS/Linux targets

---

## 🟡 Q7: What is the Kotlin Multiplatform Mobile (KMM)?
**Answer:** KMM is a subset of KMP focused specifically on **mobile platforms** (Android + iOS). It's the most common use case. KMP is the broader technology supporting all targets.

> **Note:** As of 2024, JetBrains rebranded KMM back to just "KMP" (Kotlin Multiplatform) since it now supports more than mobile.

---

## 🔴 Q8: How does KMP handle platform-specific code?
**Answer:** Using `expect`/`actual` mechanism:
- `expect` = declaration in `commonMain` (like an interface)
- `actual` = implementation in platform source sets

```kotlin
// commonMain
expect class DateTimeFormatter() {
    fun format(epoch: Long): String
}

// androidMain
actual class DateTimeFormatter {
    actual fun format(epoch: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date(epoch))
    }
}

// iosMain
actual class DateTimeFormatter {
    actual fun format(epoch: Long): String {
        val formatter = NSDateFormatter()
        formatter.dateFormat = "dd/MM/yyyy"
        return formatter.stringFromDate(NSDate(epoch / 1000))
    }
}
```

---

## 🟡 Q9: What is `kotlin-multiplatform` Gradle plugin?
**Answer:** The `kotlin-multiplatform` plugin is the Gradle plugin that enables KMP in a project. It:
- Configures compilation for each target
- Sets up source sets (`commonMain`, `androidMain`, `iosMain`, etc.)
- Handles dependency resolution across platforms
- Manages `expect`/`actual` compilation

```kotlin
plugins {
    kotlin("multiplatform") version "1.9.22"
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
}
```

---

## 🟡 Q10: Can you use Java libraries in KMP common code?
**Answer:** No. `commonMain` cannot directly use Java libraries because it must compile to all targets (including iOS which has no JVM). You can:
1. Use a KMP-compatible version of the library (e.g., `kotlinx.coroutines` instead of Java threads)
2. Wrap the Java library in an `expect`/`actual` and use it only in `androidMain`/`jvmMain`
3. Use a multiplatform library that provides the same API

---

## 🔴 Q11: What is the `commonMain` source set hierarchy?
**Answer:** KMP supports intermediate source sets for sharing code between subsets of targets:

```
commonMain
├── jvmMain (shared by androidMain, jvmMain)
│   ├── androidMain
│   └── jvmMain
├── nativeMain (shared by all native targets)
│   ├── appleMain (shared by all Apple targets)
│   │   ├── iosMain
│   │   │   ├── iosX64Main
│   │   │   ├── iosArm64Main
│   │   │   └── iosSimulatorArm64Main
│   │   ├── macosMain
│   │   └── watchosMain
│   └── linuxX64Main
└── jsMain
```

---

## 🟡 Q12: What is `@OptIn` in KMP?
**Answer:** `@OptIn` is used to opt into experimental APIs in KMP. Many KMP APIs are marked as `@ExperimentalStdlibApi` or `@ExperimentalMultiplatform`.

```kotlin
@OptIn(ExperimentalStdlibApi::class)
fun process() {
    // Using experimental API
}
```

---

## 🟢 Q13: What is the Kotlin/Native memory model?
**Answer:** The **new Kotlin/Native memory model** (stable since Kotlin 1.9) removed the previous constraints:
- No more frozen object restrictions
- Objects can be freely shared between threads
- Uses garbage collection (not just ARC)
- Enables coroutines to work naturally across threads

```kotlin
// kotlin {
//     kotlin {
//         binaryOption("memoryModel", "experimental")
//     }
// }
```

---

## 🟡 Q14: What is the difference between `commonMain` and `nativeMain`?
**Answer:**
- `commonMain` — code shared across **all** targets (JVM, JS, native)
- `nativeMain` — code shared only across **native** targets (iOS, macOS, Linux)
- `nativeMain` can use native-specific APIs not available in JVM/JS

---

## 🔴 Q15: How do you add dependencies in KMP?
**Answer:** Dependencies are specified per source set:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
            }
        }
        val iosMain by getting {
            dependencies {
                // iOS-specific deps
            }
        }
    }
}
```

---

## 📌 Key Takeaways
- KMP shares **business logic** across platforms at source level
- `commonMain` = platform-agnostic, `expect`/`actual` for platform-specific
- Kotlin/Native compiles to native binaries via LLVM
- New memory model enables free object sharing between threads
- Can't use Java libraries in `commonMain` — use KMP alternatives

---

[← Back to README](../README.md) | [Next: Project Setup →](ProjectSetup.md)
