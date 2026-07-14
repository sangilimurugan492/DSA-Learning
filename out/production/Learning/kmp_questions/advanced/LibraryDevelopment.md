# KMP Library Development — Interview Questions

## 🔴 Q1: How do you structure a KMP library?
**Answer:**

```
my-library/
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── commonMain/
│   │   └── kotlin/
│   ├── androidMain/
│   │   └── kotlin/
│   ├── iosMain/
│   │   └── kotlin/
│   └── jvmMain/
│       └── kotlin/
└── README.md
```

```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform") version "1.9.22"
    `maven-publish`
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
    
    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val iosMain by creating { dependsOn(commonMain) }
    }
}
```

---

## 🔴 Q2: How do you publish a KMP library?
**Answer:**

```kotlin
// build.gradle.kts
publishing {
    publications.withType<MavenPublication> {
        groupId = "com.example"
        artifactId = "my-library"
        version = "1.0.0"
        
        pom {
            name.set("My KMP Library")
            description.set("A multiplatform library")
            url.set("https://github.com/user/my-library")
            licenses {
                license { name.set("MIT") }
            }
        }
    }
    repositories {
        mavenCentral()
        mavenLocal()
    }
}
```

Publish: `./gradlew publishAllPublicationsToMavenCentralRepository`

---

## 🟡 Q3: How do you consume a KMP library?
**Answer:**

```kotlin
// In app's build.gradle.kts
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.example:my-library:1.0.0")
            }
        }
    }
}
```

Gradle automatically resolves the correct artifact for each target (`.aar` for Android, `.klib` for iOS, `.jar` for JVM).

---

## 🔴 Q4: How do you handle API surface design in KMP?
**Answer:**

```kotlin
// commonMain — public API
class MyLibrary {
    fun initialize(config: LibraryConfig) { ... }
    fun execute(request: Request): Response { ... }
}

// Keep public API minimal
// Use internal for implementation details
internal class InternalProcessor { ... }

// Use sealed classes for public types
sealed class LibraryResult {
    data class Success(val data: String) : LibraryResult()
    data class Error(val code: Int) : LibraryResult()
}
```

---

## 🟡 Q5: How do you handle backward compatibility in KMP libraries?
**Answer:**

```kotlin
// Use @Deprecated for removed APIs
@Deprecated("Use newMethod() instead", ReplaceWith("newMethod()"))
fun oldMethod() { ... }

fun newMethod() { ... }

// Versioning: SemVer
// MAJOR.MINOR.PATCH
// 1.0.0 → 1.0.1 (patch: bug fix)
// 1.0.0 → 1.1.0 (minor: new feature, backward compatible)
// 1.0.0 → 2.0.0 (major: breaking change)
```

---

## 🟡 Q6: How do you document a KMP library?
**Answer:**

```kotlin
/**
 * Fetches user data from the API.
 *
 * @param id The unique user identifier.
 * @return [User] if found, null otherwise.
 * @throws NetworkException if the request fails.
 * @sample com.example.samples.UserSamples.basicUsage
 */
suspend fun getUser(id: String): User?
```

```markdown
<!-- README.md -->
# My Library

## Installation
implementation("com.example:my-library:1.0.0")

## Usage
val lib = MyLibrary()
val result = lib.execute(Request("data"))
```

---

## 🟡 Q7: How do you handle platform-specific APIs in a library?
**Answer:**

```kotlin
// commonMain — public interface
interface Storage {
    fun put(key: String, value: String)
    fun get(key: String): String?
}

// commonMain — default implementation
class DefaultStorage : Storage {
    private val map = mutableMapOf<String, String>()
    override fun put(key: String, value: String) { map[key] = value }
    override fun get(key: String): String? = map[key]
}

// androidMain — platform implementation
class SharedPreferencesStorage(context: Context) : Storage {
    private val prefs = context.getSharedPreferences("lib", MODE_PRIVATE)
    override fun put(key: String, value: String) = prefs.edit().putString(key, value).apply()
    override fun get(key: String): String? = prefs.getString(key, null)
}
```

---

## 🟡 Q8: How do you test a KMP library?
**Answer:**

```kotlin
// commonTest — shared tests
class StorageTest {
    @Test
    fun `should store and retrieve`() {
        val storage = DefaultStorage()
        storage.put("key", "value")
        assertEquals("value", storage.get("key"))
    }
}

// androidUnitTest — Android-specific
class SharedPreferencesStorageTest {
    @Test
    fun `should use SharedPreferences`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = SharedPreferencesStorage(context)
        storage.put("key", "value")
        assertEquals("value", storage.get("key"))
    }
}
```

---

## 🟡 Q9: How do you handle binary compatibility?
**Answer:** Use **Binary Compatibility Validator**:

```kotlin
// build.gradle.kts
plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.14.0"
}

apiValidation {
    ignoredPackages.add("com.example.internal")
    ignoredProjects.add("examples")
}
```

Run: `./gradlew apiDump` → generates `.api` files. CI checks: `./gradlew apiCheck`.

---

## 🟡 Q10: How do you support Compose Multiplatform in a library?
**Answer:**

```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.5.11"
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()
}

// commonMain
@Composable
fun MyLibraryComponent(text: String) {
    Text(text)
}
```

---

## 🟡 Q11: How do you handle Gradle version catalogs in a library?
**Answer:**

```toml
# gradle/libs.versions.toml
[versions]
kotlin = "1.9.22"
coroutines = "1.7.3"

[libraries]
coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
```

```kotlin
// build.gradle.kts
dependencies {
    commonMainImplementation(libs.coroutines.core)
}
```

---

## 🟡 Q12: How do you distribute a KMP library via XCFramework?
**Answer:**

```kotlin
kotlin {
    iosX64 {
        binaries.framework {
            baseName = "MyLibrary"
            isStatic = true
        }
    }
    
    // Or XCFramework for universal distribution
    xcf {
        list("MyLibrary")
    }
}
```

```bash
./gradlew assembleXCFramework
# Output: build/XCFrameworks/MyLibrary.xcframework
```

---

## 📌 Key Takeaways
- Structure: `commonMain` + platform source sets
- Publish with `maven-publish` plugin to Maven Central
- Use Binary Compatibility Validator for API stability
- Keep public API minimal, use `internal` for implementation
- SemVer for versioning, `@Deprecated` for migrations
- XCFramework for iOS distribution

---

[← CI/CD](CICD.md) | [Back to README](../README.md) | [Next: Migration →](Migration.md)
