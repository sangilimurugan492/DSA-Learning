# CI/CD

## 📖 Explanation

CI/CD (Continuous Integration / Continuous Deployment) automates building, testing, and deploying Flutter apps. CI runs tests and checks on every push, while CD builds and distributes the app to testers or app stores.

### CI/CD Pipeline
```
CI (Continuous Integration)              CD (Continuous Delivery/Deployment)
┌──────────────────────┐                  ┌──────────────────────┐
│ Push to Git          │                  │ Build artifacts       │
│   ↓                  │                  │   ↓                  │
│ Run tests            │                  │ Sign APK/IPA         │
│   ↓                  │                  │   ↓                  │
│ Analyze code         │                  │ Upload to store      │
│   ↓                  │                  │   ↓                  │
│ Check formatting     │                  │ Distribute to testers│
│   ↓                  │                  │   ↓                  │
│ Build verification   │                  │ Release to production │
└──────────────────────┘                  └──────────────────────┘
```

### CI/CD Tools for Flutter
| Tool | Type | Use Case |
|------|------|----------|
| GitHub Actions | CI/CD | Free for public repos, GitHub integration |
| Codemagic | CI/CD | Flutter-first, no setup for mobile |
| Fastlane | CD | Automate store deployment |
| Bitrise | CI/CD | Mobile-focused CI/CD |
| Firebase App Distribution | CD | Distribute to testers |

### Versioning
```yaml
# pubspec.yaml
version: 1.2.3+45  # version_name+version_code
# 1.2.3 = version name (shown to users)
# 45 = version code (integer, increments each release)
```

### Code Quality Checks
- `dart format --set-exit-if-changed .` — formatting
- `flutter analyze` — static analysis
- `flutter test --coverage` — tests with coverage
- `flutter pub outdated` — dependency check

### Signing & Secrets
- **Android**: keystore file + `key.properties` (store in CI secrets as base64)
- **iOS**: App Store Connect API key + signing certificates
- Never commit secrets — use CI/CD secret management

### App Size Optimization
| Optimization | Size Reduction |
|-------------|----------------|
| `--split-per-abi` | ~40% (per APK) |
| App Bundle | ~30% (Play Store) |
| WebP images | ~30% vs PNG |
| `--obfuscate` | ~5% |

---

## 🧪 Code Example

```yaml
# .github/workflows/flutter-ci.yml
name: Flutter CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.19.0'
          channel: stable
          cache: true

      - name: Install dependencies
        run: flutter pub get

      - name: Check formatting
        run: dart format --set-exit-if-changed .

      - name: Analyze code
        run: flutter analyze

      - name: Run tests
        run: flutter test --coverage

      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          file: coverage/lcov.info

  build-android:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.19.0'

      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          distribution: temurin
          java-version: '17'

      - name: Build APK
        run: flutter build apk --release --split-per-abi

      - name: Build App Bundle
        run: flutter build appbundle --release

      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: android-build
          path: |
            build/app/outputs/flutter-apk/*.apk
            build/app/outputs/bundle/release/*.aab

  build-ios:
    needs: test
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4
      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.19.0'

      - name: Build iOS
        run: flutter build ios --release --no-codesign

      - name: Upload artifacts
        uses: actions/upload-artifact@v3
        with:
          name: ios-build
          path: build/ios/iphoneos/Runner.app
```

### Fastlane for Deployment
```ruby
# android/fastlane/Fastfile
platform :android do
  desc "Deploy to Play Store"
  lane :deploy do
    upload_to_play_store(
      track: 'internal',
      aab: '../build/app/outputs/bundle/release/app-release.aab',
      skip_upload_metadata: true,
      skip_upload_images: true,
      skip_upload_screenshots: true
    )
  end
end
```

### Output
```
A CI/CD pipeline that:
- Runs formatting, analysis, and tests on every push/PR
- Builds Android APK + AAB and iOS app on passing tests
- Uploads build artifacts for download
- Distributes to Play Store / TestFlight via Fastlane
```

---

## ❓ Interview Questions

1. **What is CI/CD for Flutter?**
   - CI (Continuous Integration) automatically runs tests, code analysis, and formatting checks on every push/PR. CD (Continuous Delivery/Deployment) builds the app, signs it, and distributes to testers or app stores. For Flutter, CI includes: `flutter pub get`, `dart format --set-exit-if-changed .`, `flutter analyze`, `flutter test --coverage`. CD includes: `flutter build apk/appbundle --release` (Android), `flutter build ios --release` (iOS), signing, and upload to Play Store / App Store. Tools: GitHub Actions, Codemagic, Bitrise. Always run in profile/release mode for accurate performance, never debug mode.

2. **How do you set up GitHub Actions for Flutter?**
   - Create `.github/workflows/flutter-ci.yml`. Trigger on push/PR to main. Use `subosito/flutter-action@v2` to install Flutter. Steps: (1) `flutter pub get` — install deps. (2) `dart format --set-exit-if-changed .` — check formatting. (3) `flutter analyze` — static analysis. (4) `flutter test --coverage` — run tests. (5) Build APK/AAB for Android with `actions/setup-java`. (6) Build iOS on `macos-latest`. (7) Upload artifacts with `actions/upload-artifact`. Use `needs: test` to build only after tests pass. Cache Flutter SDK with `cache: true`.

3. **How do you set up Codemagic CI/CD?**
   - Codemagic is Flutter-first — no manual Flutter setup needed. Create `codemagic.yaml` with workflow config: instance_type (mac_mini_m2), environment (flutter: stable, android_signing, ios_signing), scripts (pub get, test, build), artifacts (aab, ipa), and publishing (email, google_play, app_store_connect). Codemagic handles signing automatically with configured credentials. It's simpler than GitHub Actions for mobile because it provides macOS instances and handles code signing natively. Use it when you need easy iOS builds without managing certificates.

4. **How do you use Fastlane for deployment?**
   - Fastlane automates store deployment. For Android: `fastlane deploy` calls `upload_to_play_store` with the AAB file, track (internal/production), and skip flags for metadata/images/screenshots. For iOS: `fastlane beta` builds the app and uploads to TestFlight, `fastlane release` uploads to App Store. Configure `Appfile` with package name / bundle ID. Store credentials in environment variables or Fastlane match for iOS signing. Run from terminal: `cd android && fastlane deploy` or `cd ios && fastlane beta`. Fastlane is the standard tool for automating Play Store and App Store uploads.

5. **How do you manage signing and secrets?**
   - Android: generate keystore with `keytool`, store credentials in `key.properties` (never commit), reference in `build.gradle` signingConfigs. In CI, store keystore as base64 in GitHub Secrets, decode in workflow, create `key.properties` from secrets. iOS: use App Store Connect API key, store in CI secrets. Never commit secrets to git — use `.gitignore` for `key.properties` and keystore files. In GitHub Actions: `secrets.ANDROID_KEYSTORE_BASE64`, `secrets.ANDROID_KEY_PASSWORD`, etc. Use `echo "${{ secrets.X }}" | base64 --decode > file` to decode. For Codemagic, configure signing in the dashboard.

6. **How do you implement versioning?**
   - Flutter uses `pubspec.yaml`: `version: 1.2.3+45` where `1.2.3` is version name (user-visible) and `45` is version code (integer, must increment). Automate version bumping with a Python script that reads pubspec.yaml, increments patch + version code, and commits. Trigger on PR merge to main. For Android, version code must always increase. For iOS, build number must increase. Use semantic versioning: major.minor.patch. Tag releases with `git tag v1.2.3`. Some teams use `flutter_version` package or Fastlane to manage versions across platforms.

7. **How do you distribute to testers?**
   - Android: Firebase App Distribution — upload APK/AAB with `wzieba/Firebase-Distribution-Github-Action`, specify tester groups. iOS: TestFlight via Fastlane `upload_to_testflight` or App Store Connect API. Alternatively, use Codemagic email distribution or Firebase for both platforms. For internal testing: Android APK can be shared directly (enable unknown sources). iOS requires TestFlight or Ad Hoc distribution (limited to 100 devices). Firebase App Distribution is free and supports both platforms. Crashlytics integration helps track tester crashes.

8. **How do you set up code quality checks?**
   - Create a `quality.yml` workflow that runs on PRs: (1) `dart format --set-exit-if-changed .` — formatting. (2) `flutter analyze` — static analysis with `analysis_options.yaml` (enable strict-casts, strict-raw-types, lints like prefer_const_constructors, avoid_print, require_trailing_commas). (3) `flutter test --coverage` — check coverage threshold (fail if <80%). (4) Check for TODOs — `grep -r "TODO" lib/` and fail if found. (5) `flutter pub outdated` — check for outdated dependencies. (6) Custom lint rules with `custom_lint`. Block PRs from merging if quality checks fail using GitHub branch protection rules.

9. **How do you optimize app size for release?**
   - Build with `--split-per-abi` for per-architecture APKs (~40% smaller per APK). Build App Bundle (AAB) for Play Store — Play delivers only needed ABI/resources (~30% smaller). Use `--tree-shake-icons` to remove unused icons. Use `--obfuscate --split-debug-info=./symbols` for smaller + obfuscated binary (~5% smaller). Use WebP instead of PNG (~30% smaller). List specific asset files instead of folders. Remove unused dependencies. Enable R8/ProGuard on Android. Result: a 20MB debug APK can become ~8MB release AAB. Always measure with `flutter build apk --analyze-size`.

10. **What is the analysis_options.yaml and how do you configure it?**
    - `analysis_options.yaml` configures Dart analyzer and linter rules. Include `package:flutter_lints/flutter.yaml` as base. Enable strict mode: `analyzer: language: strict-casts: true, strict-raw-types: true`. Enable lints: `prefer_const_constructors: true`, `avoid_print: true`, `require_trailing_commas: true`, `use_key_in_widget_constructors: true`, `prefer_final_locals: true`. Exclude generated files: `analyzer: exclude: ["**/*.g.dart", "**/*.freezed.dart"]`. This enforces code quality at compile time — the CI pipeline runs `flutter analyze` which fails on any violation. Customize rules based on team preferences.

---

## 🔗 Related Topics
- [Testing](../intermediate/Testing.md)
- [Performance](Performance.md)
- [Architecture Patterns](ArchitecturePatterns.md)
