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

---

## 🔗 Related Topics
- [Performance Optimization](Performance.md)
- [Testing](Testing.md)
