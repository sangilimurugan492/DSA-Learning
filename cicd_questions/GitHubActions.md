# GitHub Actions

## Q1: What is GitHub Actions?

GitHub Actions is GitHub's built-in CI/CD platform that automates build, test, and deployment directly from your repository.

### Why GitHub Actions for Android?
| Feature | Description |
|---------|-------------|
| Built-in | No separate server needed |
| Free tier | 2,000 min/month for private repos |
| Marketplace | Pre-built actions |
| Matrix builds | Test across API levels |
| Secrets | Encrypted secrets management |
| Artifact storage | Store APK/AAB |

### Basic workflow
```yaml
# .github/workflows/android.yml
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

      - name: Build debug APK
        run: ./gradlew assembleDebug

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug.apk
          path: app/build/outputs/apk/debug/*.apk
```

---

## Q2: How do you structure a workflow?

### Workflow anatomy
```yaml
name: Android CI/CD          # Workflow name

on:                          # Triggers
  push:
    branches: [ main ]
  pull_request:
  workflow_dispatch:         # Manual trigger

jobs:                        # Jobs (run in parallel)
  build:                     # Job name
    runs-on: ubuntu-latest   # Runner
    steps:                   # Steps (run in sequence)
      - uses: actions/checkout@v4
      - run: ./gradlew build
```

### Trigger types
| Trigger | Description |
|---------|-------------|
| `push` | On git push |
| `pull_request` | On PR opened/updated |
| `workflow_dispatch` | Manual trigger |
| `schedule` | Cron schedule |
| `release` | On GitHub release |
| `workflow_call` | Reusable workflow |

### Trigger examples
```yaml
on:
  push:
    branches: [ main, develop ]
    paths: [ 'app/**', 'build.gradle' ]
  pull_request:
    types: [ opened, synchronize, reopened ]
  schedule:
    - cron: '0 2 * * *'  # Daily at 2 AM UTC
  workflow_dispatch:
    inputs:
      track:
        description: 'Deploy track'
        default: 'internal'
```

---

## Q3: How do you cache Gradle in GitHub Actions?

```yaml
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
          cache: gradle  # ← Built-in Gradle cache

      - name: Build
        run: ./gradlew assembleDebug

      - name: Cache Gradle packages
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', 'gradle/wrapper/gradle-wrapper.properties') }}
          restore-keys: |
            gradle-${{ runner.os }}-
```

### Cache impact
| Without cache | With cache | Savings |
|--------------|------------|---------|
| ~8 min build | ~3 min build | ~60% faster |

---

## Q4: How do you run matrix builds?

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      fail-fast: false  # Don't cancel other jobs on failure
      matrix:
        api-level: [ 29, 30, 33, 34 ]
        target: [ google_apis, google_apis_playstore ]
        exclude:
          - api-level: 29
            target: google_apis_playstore

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: ${{ matrix.api-level }}
          target: ${{ matrix.target }}
          arch: x86_64
          script: ./gradlew connectedAndroidTest
```

### Matrix combinations
| API | Target | Job count |
|-----|--------|-----------|
| 29 | google_apis | 1 |
| 30 | google_apis | 1 |
| 30 | google_apis_playstore | 1 |
| 33 | google_apis | 1 |
| 33 | google_apis_playstore | 1 |
| 34 | google_apis | 1 |
| 34 | google_apis_playstore | 1 |
| **Total** | | **7 parallel jobs** |

---

## Q5: How do you manage secrets in GitHub Actions?

### Setting up secrets
1. GitHub repo → Settings → Secrets and variables → Actions
2. New repository secret:
   - `SIGNING_KEYSTORE` (base64 encoded .jks)
   - `SIGNING_KEY_PASSWORD`
   - `SIGNING_KEY_ALIAS`
   - `SIGNING_STORE_PASSWORD`
   - `PLAY_STORE_SERVICE_ACCOUNT` (JSON key)

### Using secrets in workflow
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Decode keystore
        run: |
          echo "${{ secrets.SIGNING_KEYSTORE }}" | base64 -d > app/release.keystore

      - name: Build signed release
        env:
          KEYSTORE_PATH: app/release.keystore
          KEYSTORE_PASSWORD: ${{ secrets.SIGNING_STORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.SIGNING_KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.SIGNING_KEY_PASSWORD }}
        run: |
          ./gradlew assembleRelease \
            -Pandroid.injected.signing.store.file=$KEYSTORE_PATH \
            -Pandroid.injected.signing.store.password=$KEYSTORE_PASSWORD \
            -Pandroid.injected.signing.key.alias=$KEY_ALIAS \
            -Pandroid.injected.signing.key.password=$KEY_PASSWORD

      - name: Clean up keystore
        if: always()
        run: rm -f app/release.keystore
```

### Secret types
| Type | Scope | Use case |
|------|-------|---------|
| Repository secrets | Repo only | Most common |
| Environment secrets | Per environment | Staging vs production |
| Organization secrets | All repos | Shared API keys |
| Encrypted variables | Repo | Non-sensitive config |

---

## Q6: How do you deploy to Play Store?

```yaml
name: Deploy to Play Store

on:
  push:
    tags:
      - 'v*'  # Trigger on version tags

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: production  # Requires manual approval
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Build AAB
        run: ./gradlew bundleRelease

      - name: Deploy to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT }}
          packageName: com.example.app
          releaseFiles: app/build/outputs/bundle/release/app-release.aab
          track: internal
          status: completed
          inAppUpdatePriority: 3
```

### Deploy to Firebase App Distribution
```yaml
- name: Deploy to Firebase
  uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    serviceCredentialsFile: ${{ secrets.FIREBASE_CREDENTIALS }}
    groups: testers
    file: app/build/outputs/apk/release/app-release.apk
```

### Deployment tracks
| Track | Trigger | Audience |
|-------|---------|---------|
| Internal | Push to `develop` | Internal team |
| Alpha | Push to `release/*` | Selected testers |
| Beta | Tag `v*-beta` | Public beta |
| Production | Tag `v*` | All users |

---

## Q7: How do you run code quality checks?

```yaml
name: Code Quality

on: [ pull_request ]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      - name: Run Detekt
        run: ./gradlew detekt

      - name: Run Ktlint
        run: ./gradlew ktlintCheck

      - name: Run Lint
        run: ./gradlew lintDebug

      - name: Run unit tests with coverage
        run: ./gradlew testDebugUnitTest jacocoTestReport

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v4
        with:
          file: app/build/reports/jacoco/jacoco.xml

      - name: Upload reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: quality-reports
          path: |
            app/build/reports/detekt/
            app/build/reports/lint-results-*.html
```

### Quality tools
| Tool | Purpose |
|------|---------|
| Detekt | Static code analysis for Kotlin |
| Ktlint | Code style checking |
| Android Lint | Android-specific checks |
| JaCoCo | Code coverage |
| SonarQube | Comprehensive quality gate |

---

## Q8: How do you create reusable workflows?

### Reusable workflow
```yaml
# .github/workflows/android-build.yml
name: Android Build

on:
  workflow_call:
    inputs:
      build-type:
        required: true
        type: string
      run-tests:
        required: false
        type: boolean
        default: true

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
          cache: gradle

      - name: Build
        run: ./gradlew assemble${{ inputs.build-type }}

      - name: Test
        if: inputs.run-tests
        run: ./gradlew testDebugUnitTest
```

### Calling reusable workflow
```yaml
# .github/workflows/ci.yml
name: CI

on: [ push, pull_request ]

jobs:
  debug-build:
    uses: ./.github/workflows/android-build.yml
    with:
      build-type: Debug
      run-tests: true

  release-build:
    uses: ./.github/workflows/android-build.yml
    with:
      build-type: Release
      run-tests: false
```

---

## Q9: How do you set up environment-based deployments?

```yaml
name: Deploy

on:
  push:
    branches:
      - develop    # → Staging
      - main       # → Production
    tags:
      - 'v*'       # → Production release

jobs:
  deploy-staging:
    if: github.ref == 'refs/heads/develop'
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - uses: actions/checkout@v4
      - name: Build and deploy to Firebase
        run: |
          ./gradlew assembleDebug
          # Deploy to Firebase App Distribution

  deploy-production:
    if: startsWith(github.ref, 'refs/tags/v')
    runs-on: ubuntu-latest
    environment: production  # Requires manual approval
    steps:
      - uses: actions/checkout@v4
      - name: Build and deploy to Play Store
        run: |
          ./gradlew bundleRelease
          # Deploy to Play Store
```

### Environments
| Environment | Protection rules | Use case |
|-------------|----------------|---------|
| `staging` | None | Auto-deploy |
| `production` | Manual approval, branch restriction | Controlled release |
| `testing` | Required reviewers | QA testing |

---

## Q10: How do you handle concurrency and cancel old runs?

```yaml
name: Android CI

on: [ push, pull_request ]

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: true  # Cancel previous runs on same branch

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew assembleDebug
```

### Concurrency groups
| Group | Behavior |
|-------|----------|
| `${{ github.ref }}` | Per-branch |
| `${{ github.event_name }}` | Per-event type |
| `${{ github.head_ref }}` | Per-PR (for PRs) |

---

## Q11: How do you run jobs conditionally?

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Build debug (PR only)
        if: github.event_name == 'pull_request'
        run: ./gradlew assembleDebug

      - name: Build release (main only)
        if: github.ref == 'refs/heads/main'
        run: ./gradlew assembleRelease

      - name: Skip on docs change
        if: |
          !contains(github.event.head_commit.message, '[skip ci]') &&
          !contains(github.event.head_commit.message, '[ci skip]')
        run: ./gradlew build

  deploy:
    needs: build
    if: github.ref == 'refs/heads/main' && success()
    runs-on: ubuntu-latest
    steps:
      - run: echo 'Deploying...'
```

### Condition examples
| Condition | Description |
|-----------|-------------|
| `github.ref == 'refs/heads/main'` | Main branch only |
| `github.event_name == 'pull_request'` | PR only |
| `success()` | Previous job succeeded |
| `failure()` | Previous job failed |
| `always()` | Run regardless of previous status |
| `contains(github.event.head_commit.message, '[deploy]')` | Commit message check |

---

## Q12: How do you use self-hosted runners?

```yaml
jobs:
  build:
    runs-on: self-hosted  # Or labels: [self-hosted, android, macos]
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew assembleDebug
```

### Self-hosted runner setup
```bash
# On your machine
mkdir actions-runner && cd actions-runner
curl -o actions-runner-osx-x64-2.316.0.tar.gz -L \
  https://github.com/actions/runner/releases/download/v2.316.0/actions-runner-osx-x64-2.316.0.tar.gz
tar xzf actions-runner-osx-x64-2.316.0.tar.gz
./config.sh --url https://github.com/owner/repo --token TOKEN
./run.sh
```

### GitHub-hosted vs self-hosted
| Feature | GitHub-hosted | Self-hosted |
|---------|--------------|-------------|
| Setup | Zero | Manual |
| Cost | Free tier, then paid | Your hardware |
| Speed | Standard | Your hardware |
| Android SDK | Pre-installed | Install yourself |
| Emulator | Available | Set up yourself |
| Security | Isolated | Your network |

---

## Q13: How do you create a PR check workflow?

```yaml
name: PR Check

on:
  pull_request:
    types: [ opened, synchronize, reopened ]

jobs:
  # Quick check — runs on every push
  build-and-test:
    runs-on: ubuntu-latest
    timeout-minutes: 15
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Full history for SonarQube

      - name: Set up JDK 17
        uses: actions/setup-java@v4
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

      - name: Comment PR with test results
        if: always()
        uses: EnricoMi/publish-unit-test-result-action@v2
        with:
          files: |
            **/build/test-results/**/*.xml

  # Code coverage — only on main branch PRs
  coverage:
    needs: build-and-test
    if: github.base_ref == 'main'
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: ./gradlew jacocoTestReport
      - uses: codecov/codecov-action@v4
```

---

## Q14: How do you optimize GitHub Actions for speed?

### Optimization techniques
```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      # 1. Shallow clone (faster checkout)
      - uses: actions/checkout@v4
        with:
          fetch-depth: 1

      # 2. Cache Gradle
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      # 3. Parallel build
      - run: ./gradlew assembleDebug testDebugUnitTest --parallel --build-cache

      # 4. Only run changed module tests
      - run: |
          CHANGED=$(git diff --name-only HEAD~1 | grep -oP 'modules/\K[^/]+')
          for module in $CHANGED; do
            ./gradlew :$module:test
          done
```

### Speed comparison
| Technique | Build time | Savings |
|-----------|-----------|---------|
| No cache | ~8 min | — |
| Gradle cache | ~3 min | 62% |
| + Parallel | ~2 min | 75% |
| + Build cache | ~1.5 min | 81% |
| + Shallow clone | ~1.2 min | 85% |

### Other optimizations
| Technique | Impact |
|-----------|--------|
| Use `ubuntu-latest` (fastest) | 20% faster than macOS |
| Limit `fetch-depth` | Faster checkout |
| Use `--build-cache` | Reuse task outputs |
| Skip instrumented tests on PR | 5-10 min saved |
| Use reusable workflows | Less duplication |
| Cancel in-progress runs | Save runner minutes |

---

## Q15: What are GitHub Actions best practices?

### Do's
- ✅ Use `actions/checkout@v4` (latest)
- ✅ Pin action versions (`@v4` not `@main`)
- ✅ Cache Gradle dependencies
- ✅ Use secrets for sensitive data
- ✅ Use environments for production deploys
- ✅ Set `concurrency` to cancel old runs
- ✅ Use reusable workflows
- ✅ Run tests in parallel with matrix

### Don'ts
- ❌ Don't echo secrets in logs
- ❌ Don't use `if: always()` for deploy steps
- ❌ Don't run instrumented tests on every push
- ❌ Don't store keystore in repo
- ❌ Don't use `@main` for actions (use tags)
- ❌ Don't forget to clean up artifacts

### Workflow checklist
- [ ] Triggers defined (push, PR, manual)
- [ ] Gradle cache enabled
- [ ] Secrets configured
- [ ] Artifacts uploaded
- [ ] Notifications set up
- [ ] Concurrency configured
- [ ] Timeout set
- [ ] Environment protection for production

---

## 🔗 Related Topics
- [Jenkins](Jenkins.md)
- [CICD Scenarios](CICDScenarios.md)
