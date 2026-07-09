# GitHub Actions for Android

## Q1: What is GitHub Actions?

CI/CD platform built into GitHub. Runs workflows on push, PR, schedule, or manual trigger.

### Key concepts
| Concept | Description |
|---------|-------------|
| Workflow | YAML file in `.github/workflows/` |
| Job | A set of steps running on the same runner |
| Step | Individual command or action |
| Runner | Machine executing the job (ubuntu, macos, windows) |
| Action | Reusable unit (e.g., `actions/checkout`) |
| Secret | Encrypted variable (API keys, keystore) |

---

## Q2: How do you set up a basic Android CI?

```yaml
# .github/workflows/android-ci.yml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3

      - name: Build debug APK
        run: ./gradlew assembleDebug

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Run lint
        run: ./gradlew lintDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug.apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

---

## Q3: How do you sign and release an APK/AAB?

```yaml
name: Release Build

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Decode keystore
        run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > app/release.keystore

      - name: Build signed AAB
        run: ./gradlew bundleRelease
        env:
          KEYSTORE_PATH: release.keystore
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: app-release.aab
          path: app/build/outputs/bundle/release/app-release.aab
```

### Setting up secrets
```bash
# Encode keystore to base64
base64 -i release.keystore -o keystore_base64.txt

# Add to GitHub: Settings → Secrets and variables → Actions
# KEYSTORE_BASE64 — content of keystore_base64.txt
# KEYSTORE_PASSWORD — keystore password
# KEY_ALIAS — key alias
# KEY_PASSWORD — key password
```

### `build.gradle` signing config
```groovy
android {
    signingConfigs {
        release {
            def keystorePath = System.getenv("KEYSTORE_PATH")
            if (keystorePath != null) {
                storeFile file(keystorePath)
                storePassword System.getenv("KEYSTORE_PASSWORD")
                keyAlias System.getenv("KEY_ALIAS")
                keyPassword System.getenv("KEY_PASSWORD")
            }
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

---

## Q4: How do you cache Gradle builds?

```yaml
steps:
  - uses: actions/checkout@v4

  - name: Set up JDK 17
    uses: actions/setup-java@v4
    with:
      java-version: '17'
      distribution: 'temurin'

  - name: Cache Gradle
    uses: actions/cache@v4
    with:
      path: |
        ~/.gradle/caches
        ~/.gradle/wrapper
      key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
      restore-keys: |
        gradle-${{ runner.os }}-

  # OR use the Gradle action (handles caching automatically)
  - name: Setup Gradle
    uses: gradle/actions/setup-gradle@v3
```

### Cache impact
| Without cache | With cache |
|--------------|-----------|
| ~8 min build | ~3 min build |
| Downloads all deps | Reuses cached deps |

---

## Q5: How do you run tests on multiple API levels?

```yaml
name: Instrumented Tests

on: [pull_request]

jobs:
  instrumented-tests:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        api-level: [29, 30, 33, 34]
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Enable KVM
        run: |
          echo 'KERNEL=="kvm", GROUP="kvm", MODE="0777"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
          sudo udevadm control --reload-rules
          sudo udevadm trigger --name-match=kvm

      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: ${{ matrix.api-level }}
          arch: x86_64
          script: ./gradlew connectedAndroidTest
```

---

## Q6: How do you deploy to Play Store?

```yaml
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

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build AAB
        run: ./gradlew bundleRelease

      - name: Deploy to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_SERVICE_ACCOUNT_JSON }}
          packageName: com.example.app
          releaseFiles: app/build/outputs/bundle/release/app-release.aab
          track: production  # or 'internal', 'alpha', 'beta'
          status: completed
          inAppUpdatePriority: 3
```

### Play Store service account setup
1. Google Play Console → Setup → API access
2. Create service account
3. Download JSON key
4. Add as GitHub secret: `PLAY_SERVICE_ACCOUNT_JSON`

---

## Q7: How do you optimize CI for speed?

```yaml
# 1. Run jobs in parallel
jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew testDebugUnitTest

  lint:
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew lintDebug

  build:
    runs-on: ubuntu-latest
    steps:
      - run: ./gradlew assembleDebug

# 2. Skip if only docs changed
on:
  push:
    paths-ignore:
      - '**.md'
      - 'docs/**'

# 3. Use concurrency to cancel old runs
concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true

# 4. Conditional execution
steps:
  - name: Only on main
    if: github.ref == 'refs/heads/main'
    run: ./gradlew assembleRelease
```

### Speed optimization tips
| Tip | Time Saved |
|-----|-----------|
| Gradle cache | 3-5 min |
| Parallel jobs | 2-3 min |
| Cancel old runs | Full run |
| Skip docs-only pushes | Full run |
| `--parallel` flag | 1-2 min |
| Configuration cache | 30-60s |

```bash
# Enable Gradle parallel + configuration cache
./gradlew assembleDebug --parallel --configuration-cache
```

---

## 🔗 Related Topics
- [Git Workflow](GitWorkflow.md)
- [Pull Requests](PullRequests.md)
- [Scenario Based](ScenarioBased.md)
