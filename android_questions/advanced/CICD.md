# CI/CD & App Publishing

## 📖 Explanation

CI/CD (Continuous Integration / Continuous Deployment) automates building, testing, and releasing Android apps. It ensures code quality and fast, reliable releases.

### CI/CD Pipeline Stages
```
Code Push → Build → Unit Test → Instrumented Test → Lint → Sign → Deploy
```

### Key Concepts
| Concept            | Description                                          |
|--------------------|------------------------------------------------------|
| CI                  | Auto-build and test on every push/PR               |
| CD                  | Auto-deploy to Play Store / internal track          |
| Build Variants      | Debug, Release, + product flavors                    |
| Signing             | Keystore for release builds                          |
| ProGuard/R8         | Minify, obfuscate, shrink                            |
| App Bundle (AAB)    | Google Play's preferred upload format                |
| Play Console        | Manage releases, tracks, rollout                     |

### Build Variants
```groovy
android {
    buildTypes {
        debug { applicationIdSuffix ".debug" }
        release {
            minifyEnabled true
            signingConfig signingConfigs.release
        }
    }

    productFlavors {
        dev { applicationIdSuffix ".dev" }
        staging { applicationIdSuffix ".staging" }
        production {}
    }
}
// Result: devDebug, devRelease, stagingDebug, stagingRelease, productionDebug, productionRelease
```

### Signing Config
```groovy
android {
    signingConfigs {
        release {
            storeFile file("keystore.jks")
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
}
```

### GitHub Actions Example
```yaml
name: Android CI/CD
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup JDK
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3
      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest
      - name: Run Lint
        run: ./gradlew lintDebug
      - name: Build Debug APK
        run: ./gradlew assembleDebug
      - name: Build Release AAB
        run: ./gradlew bundleRelease
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug.apk
          path: app/build/outputs/apk/debug/*.apk
```

### Play Store Publishing
| Track          | Description                          |
|---------------|--------------------------------------|
| Internal       | Quick internal testing (up to 100)  |
| Closed (Alpha) | Limited testers by email list      |
| Open (Beta)    | Anyone with opt-in link             |
| Production      | All users                            |

### Staged Rollout
```kotlin
// Play Console: Release → Create Release → Staged Rollout
// 10% → 50% → 100% over days
```

### Versioning
```groovy
defaultConfig {
    versionCode 1    // Integer, increments each release
    versionName "1.0.0"  // Display string
}
```

---

## 🧪 Code Example

```groovy
// build.gradle (app) — Full CI/CD ready config
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
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("KEY_ALIAS") ?: ""
            keyPassword = System.getenv("KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    productFlavors {
        create("dev") {
            applicationIdSuffix = ".dev"
            buildConfigField("String", "BASE_URL", "\"https://dev-api.example.com\"")
        }
        create("staging") {
            applicationIdSuffix = ".staging"
            buildConfigField("String", "BASE_URL", "\"https://staging-api.example.com\"")
        }
        create("production") {
            buildConfigField("String", "BASE_URL", "\"https://api.example.com\"")
        }
    }

    bundle {
        language { enableSplit = true }
        density { enableSplit = true }
        abi { enableSplit = true }
    }
}
```

```yaml
# .github/workflows/deploy.yml — Full CD pipeline
name: Deploy to Play Store
on:
  push:
    tags:
      - 'v*'

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Decode Keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/keystore.jks

      - name: Build Release AAB
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
        run: ./gradlew bundleProductionRelease

      - name: Upload to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
          packageName: com.example.app
          releaseFiles: app/build/outputs/bundle/productionRelease/*.aab
          track: internal
          status: completed
```

```kotlin
// Version management utility
object VersionManager {
    fun parseVersionName(version: String): Triple<Int, Int, Int> {
        val parts = version.split(".")
        return Triple(
            parts.getOrNull(0)?.toInt() ?: 0,
            parts.getOrNull(1)?.toInt() ?: 0,
            parts.getOrNull(2)?.toInt() ?: 0
        )
    }

    fun shouldForceUpdate(
        current: String,
        minimum: String
    ): Boolean {
        val (curMajor, curMinor, curPatch) = parseVersionName(current)
        val (minMajor, minMinor, minPatch) = parseVersionName(minimum)

        return if (curMajor < minMajor) true
        else if (curMajor == minMajor && curMinor < minMinor) true
        else if (curMajor == minMajor && curMinor == minMinor && curPatch < minPatch) true
        else false
    }
}
```

---

## ❓ Interview Questions

1. **What is the difference between APK and AAB?**
   - APK is the installable package. AAB (App Bundle) is the upload format for Play Store — Google generates optimized APKs per device (only needed ABIs, densities, languages). AAB reduces download size by ~20%.

2. **What is the difference between `minifyEnabled` and `shrinkResources`?**
   - `minifyEnabled` runs R8/ProGuard — removes unused code and obfuscates. `shrinkResources` removes unused resources (images, strings). Both should be enabled for release builds.

3. **How do you manage different environments (dev, staging, production)?**
   - Use `productFlavors` with different `applicationIdSuffix` and `buildConfigField` for base URLs. Each flavor can be installed side-by-side on the same device.

4. **What is staged rollout on Play Store?**
   - Gradually release to a percentage of users (e.g., 10% → 50% → 100%). If issues are found at 10%, you can halt the rollout. Available on Production track.

5. **How do you securely manage signing keys in CI/CD?**
   - Store the keystore as a base64-encoded secret in GitHub Actions. Decode at build time. Never commit the keystore to version control. Use environment variables for passwords.

6. **What is the difference between ProGuard and R8?**
   - **ProGuard** is a Java class file optimizer and obfuscator. It shrinks, optimizes, and obfuscates code. **R8** is Google's replacement for ProGuard, introduced in Android Gradle Plugin 3.4+. R8 does everything ProGuard does (shrinking, obfuscation, optimization) but also does **desugaring** (converting Java 8+ language features like lambdas, streams, and default methods to bytecode compatible with older Android versions). R8 is faster than ProGuard and is the default in Android. R8 is compatible with ProGuard keep rules — existing `proguard-rules.pro` files work with R8. Key differences: (1) R8 integrates desugaring and shrinking in one step. (2) R8 is maintained by Google, ProGuard by GuardSquare. (3) R8 is enabled by default; ProGuard requires explicit configuration. (4) R8 supports `@Keep` annotation for keeping classes/members. Use R8 (default) — it's the modern standard. Keep rules: `-keep class com.example.Model { *; }` prevents model classes from being obfuscated (important for reflection-based JSON parsing like Gson).

7. **How do you set up a GitHub Actions workflow for Android CI/CD?**
   - Create `.github/workflows/android.yml`. Key steps: (1) **Checkout code**: `uses: actions/checkout@v4`. (2) **Set up JDK**: `uses: actions/setup-java@v4` with `distribution: 'temurin'` and `java-version: '17'`. (3) **Setup Gradle cache**: `uses: gradle/actions/setup-gradle@v3` — caches Gradle dependencies and wrapper, significantly speeding up builds. (4) **Run tests**: `./gradlew test` — unit tests. (5) **Build APK/AAB**: `./gradlew assembleRelease` or `bundleRelease`. (6) **Decode signing key**: `echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/release.keystore`. (7) **Sign the APK**: Use `signingConfig` in `build.gradle` with environment variables for keystore path and passwords. (8) **Upload artifact**: `uses: actions/upload-artifact@v4` with the APK/AAB path. (9) **Distribute**: Upload to Firebase App Distribution, Play Store (via `r0adkll/upload-google-play` action), or internal testing. Trigger on `push` to `main` and `pull_request`. Use matrix builds for multiple API levels if needed.

8. **What is Fastlane and how does it automate Android deployment?**
   - **Fastlane** is an open-source tool that automates building, signing, and deploying mobile apps. For Android, key "lanes" (automation scripts): (1) **`upload_to_play_store`** — uploads AAB/APK to Google Play Console, creates a new release on a track (internal, alpha, beta, production). Supports staged rollout percentages. (2) **`supply`** — manages Play Store listing metadata (title, description, screenshots) from local files. (3) **`slack`** — sends build notifications to Slack. Example `Fastfile`: `lane :deploy do; gradle(task: "bundleRelease"); upload_to_play_store(track: "production", aab: "app/build/outputs/bundle/release/app-release.aab"); slack(message: "Deployed to Play Store!"); end`. Benefits: (1) One command to build + sign + deploy. (2) Reproducible releases. (3) Integrates with CI/CD (GitHub Actions, Jenkins). (4) Manages metadata across languages. Fastlane uses `json_key` for Play Store API authentication — store the key as a CI secret. Use Fastlane to eliminate manual Play Console uploads.

9. **What are Gradle build types and product flavors and how do they interact?**
   - **Build Types** — define how the app is built (debug vs release). `debug` has debugging enabled, no signing config, `minifyEnabled false`. `release` has `minifyEnabled true`, `shrinkResources true`, signing config for release key. You can create custom build types: `staging { initWith debug; applicationIdSuffix ".staging" }`. **Product Flavors** — define variants of the app (free vs paid, dev vs prod). Each flavor can have different `applicationId`, `buildConfigField`, resources, and dependencies. Example: `flavorDimensions += "tier"; productFlavors { free { dimension = "tier" }; paid { dimension = "tier" } }`. **Interaction**: Build types × product flavors = build variants. For 2 flavors × 2 build types = 4 variants: `freeDebug`, `freeRelease`, `paidDebug`, `paidRelease`. Each variant has its own APK, `applicationId` (e.g., `com.app.free.debug`), and can be installed side-by-side. Use source sets (`src/free/java`, `src/paid/java`) for flavor-specific code. `buildConfigField` injects constants: `buildConfigField("String", "BASE_URL", "\"https://dev.api.com\"")` — accessible as `BuildConfig.BASE_URL`.

10. **How do you implement feature flags in Android?**
    - Feature flags allow toggling features without deploying new code. Approaches: (1) **Firebase Remote Config** — server-side flags, fetched at runtime. Supports user segmentation and A/B testing. `FirebaseRemoteConfig.getInstance().fetchAndActivate()`. (2) **Local flags** — `BuildConfig` fields for compile-time flags: `buildConfigField("boolean", "FEATURE_X_ENABLED", "true")`. (3) **SharedPreferences/DataStore** — local runtime flags that can be toggled via debug menu. (4) **Unleash/LaunchDarkly** — enterprise feature flag platforms with real-time updates. (5) **Gradle product flavors** — compile-time feature separation (different APKs). Best practice: (1) Use Firebase Remote Config for server-controlled flags. (2) Cache flag values locally for offline use. (3) Use a wrapper class: `class FeatureFlags(rc: FirebaseRemoteConfig) { fun isNewFeatureEnabled() = rc.getBoolean("new_feature") }`. (4) Gradually roll out: 10% → 50% → 100%. (5) Always have a safe default. Feature flags enable trunk-based development, A/B testing, and dark launches.

11. **What is the difference between internal, closed, and open testing on Google Play?**
    - **Internal Testing** — up to 100 testers, updates available within seconds. Use for quick QA iterations and dogfooding. No review process. **Closed Testing (Alpha/Beta)** — up to 200 testers via email list or Google Group. Requires a release review (1-3 days). Use for beta testing with a controlled group. Testers opt in via a Play Store link. **Open Testing** — anyone can join via a Play Store link. Requires review. The app appears on Play Store with a "(Beta)" badge. Use for wide beta testing before production. **Production** — the live app available to all users. Supports staged rollout (10% → 100%). Best practice: Internal → Closed → Open → Production. Internal for rapid iteration, Closed for trusted beta testers, Open for broad feedback, Production for final release. Each track maintains its own version history. Use different version codes to avoid conflicts.

12. **How do you handle database migrations in CI/CD for Android?**
    - Room database migrations require careful CI/CD handling: (1) **Migration testing** — use `MigrationTestHelper` in instrumented tests. CI should run migration tests on every PR to ensure migrations work correctly. `@RunWith(AndroidJUnit4::class) class MigrationTest { @get:Rule val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java) }`. (2) **Export schemas** — set `room.schemaLocation` in `ksp`/`kapt` options. Commit schema JSON files to version control. CI uses these to validate migrations. (3) **Automated fallback** — in development, use `fallbackToDestructiveMigration()`. In production, always provide explicit `Migration` objects. (4) **Version control** — database version must increment with each schema change. CI should verify version bumps. (5) **Pre-release testing** — before releasing, test the app upgrade path on a device with the previous version's database. (6) **Rollback strategy** — if a migration fails in production, have a rollback plan (downgrade APK, handle `IllegalStateException`). Room doesn't support down migrations — you must handle rollback at the app level.

---

## 🔗 Related Topics
- [Performance Optimization](Performance.md)
- [Testing](Testing.md)
