# CI/CD Scenarios

## Scenario 1: PR Quality Gate

### Problem
Every PR must pass build, unit tests, lint, and code coverage before merge.

### Solution (GitHub Actions)
```yaml
name: PR Quality Gate

on:
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    timeout-minutes: 15
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Build
        run: ./gradlew assembleDebug

      - name: Unit tests
        run: ./gradlew testDebugUnitTest

      - name: Lint
        run: ./gradlew lintDebug

      - name: Coverage
        run: ./gradlew jacocoTestReport

      - name: Check coverage threshold
        run: |
          COVERAGE=$(./gradlew -q printCoverage)
          if (( $(echo "$COVERAGE < 70" | bc -l) )); then
            echo "Coverage $COVERAGE% is below 70%"
            exit 1
          fi

  # Required status check
  # GitHub → Settings → Branches → Protect main
  # → Require status checks: build
```

### Branch protection
```
GitHub → Settings → Branches → Branch protection rules
✅ Require pull request before merging
✅ Require status checks to pass (build, test, lint)
✅ Require conversation resolution
✅ Require linear history
```

---

## Scenario 2: Auto-Deploy to Firebase on Merge

### Problem
When a PR is merged to `develop`, automatically build and deploy to Firebase App Distribution for QA testing.

### Solution
```yaml
name: Deploy to Firebase

on:
  push:
    branches: [ develop ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Build debug APK
        run: ./gradlew assembleDebug

      - name: Deploy to Firebase
        uses: wzieba/Firebase-Distribution-Github-Action@v1
        with:
          appId: ${{ secrets.FIREBASE_APP_ID }}
          serviceCredentialsFile: ${{ secrets.FIREBASE_CREDENTIALS }}
          groups: qa-team
          file: app/build/outputs/apk/debug/app-debug.apk
          releaseNotes: |
            Build #${{ github.run_number }}
            Commit: ${{ github.event.head_commit.message }}

      - name: Notify Slack
        uses: slackapi/slack-github-action@v1
        with:
          slack-message: |
            📱 New build deployed to Firebase!
            Build #${{ github.run_number }}
            Commit: ${{ github.event.head_commit.message }}
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

---

## Scenario 3: Production Release with Approval

### Problem
Production release requires manual approval, version bump, changelog, and staged rollout.

### Solution
```yaml
name: Production Release

on:
  workflow_dispatch:
    inputs:
      version_name:
        description: 'Version (e.g., 2.1.0)'
        required: true
      rollout_percentage:
        description: 'Rollout % (1, 5, 10, 50, 100)'
        default: '1'

jobs:
  release:
    runs-on: ubuntu-latest
    environment: production  # ← Requires manual approval
    steps:
      - uses: actions/checkout@v4

      - name: Bump version
        run: |
          sed -i "s/versionCode .*/versionCode ${{ github.run_number }}/" app/build.gradle
          sed -i "s/versionName .*/versionName \"${{ inputs.version_name }}\"/" app/build.gradle

      - name: Build AAB
        run: ./gradlew bundleRelease

      - name: Deploy to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SA }}
          packageName: com.example.app
          releaseFiles: app/build/outputs/bundle/release/app-release.aab
          track: production
          rolloutFraction: ${{ inputs.rollout_percentage }}
          status: inProgress
          whatsNewDirectory: distribution/whatsnew

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          tag_name: v${{ inputs.version_name }}
          name: Release ${{ inputs.version_name }}
          body: |
            ## What's New
            - See changelog
            Build: ${{ github.run_number }}
          files: app/build/outputs/bundle/release/*.aab

      - name: Notify team
        uses: slackapi/slack-github-action@v1
        with:
          slack-message: "🚀 v${{ inputs.version_name }} deployed to ${{ inputs.rollout_percentage }}% of users"
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

### Release flow
```
1. Developer triggers workflow manually
2. Environment "production" requires approval
3. Approver reviews and approves
4. Pipeline: bump version → build → deploy → tag → notify
5. Monitor crash rate for 24h
6. Increase rollout: 1% → 5% → 10% → 50% → 100%
```

---

## Scenario 4: Rollback a Bad Release

### Problem
A production release has a critical bug. Need to rollback quickly.

### Solution
```yaml
name: Rollback Release

on:
  workflow_dispatch:
    inputs:
      previous_version:
        description: 'Previous version to rollback to (e.g., 2.0.9)'
        required: true

jobs:
  rollback:
    runs-on: ubuntu-latest
    environment: production
    steps:
      - uses: actions/checkout@v4
        with:
          ref: v${{ inputs.previous_version }}

      - name: Build previous version
        run: ./gradlew bundleRelease

      - name: Deploy previous version
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SA }}
          packageName: com.example.app
          releaseFiles: app/build/outputs/bundle/release/app-release.aab
          track: production
          status: completed
          # This replaces the current production release

      - name: Notify team
        run: |
          echo "⚠️ Rolled back to v${{ inputs.previous_version }}"
```

### Rollback checklist
| Step | Action |
|------|--------|
| 1 | Identify the bad version |
| 2 | Trigger rollback workflow |
| 3 | Build previous good version |
| 4 | Deploy to Play Store |
| 5 | Verify crash rate drops |
| 6 | Notify users (if needed) |
| 7 | Post-mortem on what went wrong |

### Play Console rollback
```
Play Console → Production → Release history
→ Select previous release → Promote to production
```

---

## Scenario 5: Parallel Test Execution

### Problem
Running all tests sequentially takes 30+ minutes. Need to parallelize.

### Solution (GitHub Actions)
```yaml
name: Parallel Tests

on: [ pull_request ]

jobs:
  # Split test classes into groups
  prepare:
    runs-on: ubuntu-latest
    outputs:
      matrix: ${{ steps.split.outputs.matrix }}
    steps:
      - uses: actions/checkout@v4
      - id: split
        run: |
          # Find all test classes and split into 4 groups
          TESTS=$(find app/src/test -name "*Test.kt" | sort)
          echo "matrix=$(echo $TESTS | jq -R -s -c 'split(" ") | [_nwise(length/4 | floor)] | {group: .}' )" >> $GITHUB_OUTPUT

  # Run each group in parallel
  test:
    needs: prepare
    runs-on: ubuntu-latest
    strategy:
      matrix:
        group: [ 1, 2, 3, 4 ]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Run test group ${{ matrix.group }}
        run: |
          ./gradlew testDebugUnitTest \
            --tests "*.group${{ matrix.group }}.*"

  # Merge results
  report:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/download-artifact@v4
        with:
          name: test-results
      - uses: EnricoMi/publish-unit-test-result-action@v2
        with:
          files: '**/build/test-results/**/*.xml'
```

### Jenkins parallel
```groovy
stage('Parallel Tests') {
    parallel {
        stage('Unit Tests') {
            steps { sh './gradlew testDebugUnitTest' }
        }
        stage('Lint') {
            steps { sh './gradlew lintDebug' }
        }
        stage('Detekt') {
            steps { sh './gradlew detekt' }
        }
        stage('Coverage') {
            steps { sh './gradlew jacocoTestReport' }
        }
    }
}
```

### Speed comparison
| Approach | Time |
|----------|------|
| Sequential | ~30 min |
| 4 parallel groups | ~8 min |
| 8 parallel groups | ~4 min |

---

## Scenario 6: Monorepo CI/CD

### Problem
Monorepo with multiple modules. Only build/test changed modules.

### Solution
```yaml
name: Monorepo CI

on:
  pull_request:

jobs:
  # Detect changed modules
  detect-changes:
    runs-on: ubuntu-latest
    outputs:
      modules: ${{ steps.changes.outputs.modules }}
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Detect changed modules
        id: changes
        run: |
          CHANGED=$(git diff --name-only origin/main...HEAD | \
            grep -oP 'modules/\K[^/]+' | sort -u)
          echo "modules=$CHANGED" >> $GITHUB_OUTPUT

  # Build only changed modules
  build:
    needs: detect-changes
    if: ${{ needs.detect-changes.outputs.modules != '' }}
    runs-on: ubuntu-latest
    strategy:
      matrix:
        module: ${{ fromJson(needs.detect-changes.outputs.modules) }}
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Build ${{ matrix.module }}
        run: ./gradlew :${{ matrix.module }}:assembleDebug

      - name: Test ${{ matrix.module }}
        run: ./gradlew :${{ matrix.module }}:testDebugUnitTest
```

### Changed module detection
```
Changed files:
  modules/auth/src/main/Auth.kt
  modules/auth/src/test/AuthTest.kt
  modules/payment/src/main/Payment.kt

→ Detected modules: auth, payment
→ Only build :auth and :payment
```

---

## Scenario 7: Nightly Build with Full Test Suite

### Problem
Run full test suite (including instrumented tests) nightly to catch issues that PR checks miss.

### Solution
```yaml
name: Nightly Full Test

on:
  schedule:
    - cron: '0 2 * * *'  # 2 AM UTC daily
  workflow_dispatch:

jobs:
  unit-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      - run: ./gradlew testDebugUnitTest
      - run: ./gradlew testReleaseUnitTest

  instrumented-tests:
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false
      matrix:
        api-level: [ 29, 30, 33, 34 ]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Run instrumented tests (API ${{ matrix.api-level }})
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: ${{ matrix.api-level }}
          arch: x86_64
          script: ./gradlew connectedAndroidTest
          emulator-options: -no-window -no-audio -no-snapshot

  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle
      - run: ./gradlew detekt lintDebug jacocoTestReport

  notify:
    needs: [ unit-tests, instrumented-tests, quality ]
    if: always()
    runs-on: ubuntu-latest
    steps:
      - name: Notify Slack
        uses: slackapi/slack-github-action@v1
        with:
          slack-message: |
            🌙 Nightly build: ${{ job.status }}
            Unit tests: ${{ needs.unit-tests.result }}
            Instrumented: ${{ needs.instrumented-tests.result }}
            Quality: ${{ needs.quality.result }}
        env:
          SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK }}
```

---

## Scenario 8: Manage Multiple Flavors

### Problem
App has multiple flavors (free, paid, enterprise). Each needs separate build and deploy.

### Solution
```yaml
name: Multi-Flavor Build

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        flavor: [ free, paid, enterprise ]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Build ${{ matrix.flavor }}
        run: ./gradlew assemble${{ matrix.flavor }}Release

      - name: Build AAB ${{ matrix.flavor }}
        run: ./gradlew bundle${{ matrix.flavor }}Release

      - name: Upload artifacts
        uses: actions/upload-artifact@v4
        with:
          name: app-${{ matrix.flavor }}
          path: |
            app/build/outputs/apk/${{ matrix.flavor }}/release/*.apk
            app/build/outputs/bundle/${{ matrix.flavor }}Release/*.aab

  deploy:
    needs: build
    runs-on: ubuntu-latest
    strategy:
      matrix:
        flavor: [ free, paid, enterprise ]
    steps:
      - uses: actions/download-artifact@v4
        with:
          name: app-${{ matrix.flavor }}

      - name: Deploy ${{ matrix.flavor }} to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets[format('PLAY_SA_{0}', matrix.flavor)] }}
          packageName: com.example.app.${{ matrix.flavor }}
          releaseFiles: app-${{ matrix.flavor }}-release.aab
          track: internal
```

### Flavor-specific secrets
| Secret | Flavor |
|--------|--------|
| `PLAY_SA_FREE` | free |
| `PLAY_SA_PAID` | paid |
| `PLAY_SA_ENTERPRISE` | enterprise |

---

## Scenario 9: Cache Invalidation

### Problem
Gradle cache is stale after dependency update. Builds fail because old cache is used.

### Solution
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          # Cache key includes gradle files → changes when deps change
          key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', 'gradle/wrapper/gradle-wrapper.properties', '**/gradle/libs.versions.toml') }}
          restore-keys: |
            gradle-${{ runner.os }}-

      - name: Build
        run: ./gradlew assembleDebug --build-cache

      # Manual cache bust
      - name: Bust cache
        if: github.event.inputs.bust_cache == 'true'
        run: |
          rm -rf ~/.gradle/caches
        # Triggered via workflow_dispatch with bust_cache input
```

### Cache key strategy
| Key component | When it changes |
|---------------|----------------|
| `runner.os` | Different OS |
| `**/*.gradle*` | Gradle file modified |
| `gradle-wrapper.properties` | Gradle version updated |
| `libs.versions.toml` | Dependency version changed |

---

## Scenario 10: Security Scanning in CI

### Problem
Scan dependencies for vulnerabilities and check for hardcoded secrets on every PR.

### Solution
```yaml
name: Security Scan

on: [ pull_request ]

jobs:
  dependency-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Dependency vulnerability scan
        run: ./gradlew dependencyCheckAnalyze

      - name: Upload report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: dependency-report
          path: build/reports/dependency-check-report.html

  secret-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Scan for secrets
        uses: gitleaks/gitleaks-action@v2
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  apk-analysis:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Build APK
        run: ./gradlew assembleDebug

      - name: APK security analysis
        uses: nv95kotov/apk-security-scanner@v1
        with:
          apk-path: app/build/outputs/apk/debug/app-debug.apk
```

### Security checks
| Check | Tool | When |
|-------|------|------|
| Dependency vulnerabilities | OWASP Dependency-Check | Every PR |
| Hardcoded secrets | Gitleaks | Every PR |
| APK analysis | APK Security Scanner | Every PR |
| SAST | SonarQube | Every PR |
| SCA | Dependabot | Weekly |

---

## 🔗 Related Topics
- [Jenkins](Jenkins.md)
- [GitHub Actions](GitHubActions.md)
