# KMP CI/CD — Interview Questions

## 🔴 Q1: How do you set up CI/CD for a KMP project?
**Answer:**

```yaml
# .github/workflows/ci.yml
name: KMP CI
on: [push, pull_request]

jobs:
  test:
    runs-on: macos-latest  # macOS for iOS targets
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Run common tests
        run: ./gradlew :shared:allTests
      - name: Run Android tests
        run: ./gradlew :shared:testDebugUnitTest
      - name: Run iOS tests
        run: ./gradlew :shared:iosSimulatorArm64Test
```

---

## 🔴 Q2: Why use macOS runners for KMP CI?
**Answer:** iOS targets require macOS for compilation (Xcode, LLVM). Android and JVM tests can run on Linux, but iOS tests need macOS runners.

```yaml
jobs:
  android-test:
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew :shared:testDebugUnitTest
  
  ios-test:
    runs-on: macos-latest  # Required for iOS
    steps:
      - run: ./gradlew :shared:iosSimulatorArm64Test
```

---

## 🟡 Q3: How do you build iOS framework in CI?
**Answer:**

```yaml
- name: Build iOS framework
  run: ./gradlew :shared:assembleXCFramework

- name: Upload framework
  uses: actions/upload-artifact@v4
  with:
    name: shared-framework
    path: shared/build/XCFrameworks/**/*.xcframework
```

---

## 🟡 Q4: How do you publish a KMP library to Maven Central?
**Answer:**

```yaml
- name: Publish to Maven Central
  run: ./gradlew publishAllPublicationsToMavenCentralRepository
  env:
    ORG_GRADLE_PROJECT_signingKeyId: ${{ secrets.SIGNING_KEY_ID }}
    ORG_GRADLE_PROJECT_signingKey: ${{ secrets.SIGNING_KEY }}
    ORG_GRADLE_PROJECT_signingPassword: ${{ secrets.SIGNING_PASSWORD }}
    ORG_GRADLE_PROJECT_mavenCentralUsername: ${{ secrets.MAVEN_CENTRAL_USERNAME }}
    ORG_GRADLE_PROJECT_mavenCentralPassword: ${{ secrets.MAVEN_CENTRAL_PASSWORD }}
```

---

## 🟡 Q5: How do you cache Gradle in CI?
**Answer:**

```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
      ~/.konan  # Kotlin/Native cache
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
```

---

## 🟡 Q6: How do you set up Fastlane for KMP iOS deployment?
**Answer:**

```ruby
# Fastfile
lane :beta do
  build_app(
    workspace: "iosApp/iosApp.xcworkspace",
    scheme: "iosApp",
    export_method: "ad-hoc"
  )
  upload_to_testflight
end
```

```yaml
- name: Deploy to TestFlight
  run: |
    cd iosApp
    fastlane beta
```

---

## 🟡 Q7: How do you handle versioning in KMP?
**Answer:**

```kotlin
// build.gradle.kts
version = "1.2.0"

// Or from gradle.properties
version = project.findProperty("library.version") ?: "1.0.0"
```

```yaml
- name: Tag release
  run: |
    git tag v${{ steps.version.outputs.version }}
    git push origin v${{ steps.version.outputs.version }}
```

---

## 🟡 Q8: How do you run Detekt/ktlint in CI?
**Answer:**

```yaml
- name: Run Detekt
  run: ./gradlew detekt

- name: Run ktlint
  run: ./gradlew ktlintCheck

- name: Upload reports
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: code-reports
    path: |
      **/build/reports/detekt/
      **/build/reports/ktlint/
```

---

## 🟡 Q9: How do you build Android APK/AAB in CI?
**Answer:**

```yaml
- name: Build Android AAB
  run: ./gradlew :androidApp:bundleRelease

- name: Sign AAB
  uses: r0adkll/sign-android-release@v1
  with:
    releaseDirectory: androidApp/build/outputs/bundle/release
    signingKeyBase64: ${{ secrets.SIGNING_KEY }}
    alias: ${{ secrets.KEY_ALIAS }}
    keyStorePassword: ${{ secrets.KEYSTORE_PASSWORD }}
    keyPassword: ${{ secrets.KEY_PASSWORD }}

- name: Upload to Play Store
  uses: r0adkll/upload-google-play@v1
  with:
    serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT }}
    packageName: com.example.app
    releaseFiles: androidApp/build/outputs/bundle/release/app-release.aab
    track: internal
```

---

## 🟡 Q10: How do you set up a release pipeline for both platforms?
**Answer:**

```yaml
jobs:
  release:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run all tests
        run: ./gradlew allTests
      - name: Build Android
        run: ./gradlew :androidApp:assembleRelease :androidApp:bundleRelease
      - name: Build iOS
        run: ./gradlew :shared:assembleXCFramework
      - name: Deploy Android
        run: fastlane android deploy
      - name: Deploy iOS
        run: fastlane ios beta
```

---

## 📌 Key Takeaways
- Use `macos-latest` runner for iOS compilation
- Cache `~/.konan` for Kotlin/Native builds
- `allTests` runs tests on all platforms
- `assembleXCFramework` builds iOS framework
- Publish to Maven Central with signing secrets
- Fastlane for App Store / Play Store deployment

---

[← Performance](Performance.md) | [Back to README](../README.md) | [Next: Library Development →](LibraryDevelopment.md)
