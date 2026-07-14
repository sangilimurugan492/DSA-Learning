# CI/CD

## Q1: What is CI/CD for Flutter?

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

### Pipeline Stages
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

---

## Q2: How do you set up Codemagic CI/CD?

```yaml
# codemagic.yaml
workflows:
  flutter-app:
    name: Flutter App
    instance_type: mac_mini_m2
    max_build_duration: 60
    environment:
      flutter: stable
      groups:
        - keystore_credentials
      vars:
        PACKAGE_NAME: com.example.app
      android_signing:
        - keystore_reference
      ios_signing:
        distribution_type: app_store
        bundle_identifier: com.example.app
    scripts:
      - name: Install dependencies
        script: flutter pub get
      - name: Run tests
        script: flutter test
      - name: Build Android
        script: |
          flutter build appbundle --release
      - name: Build iOS
        script: |
          flutter build ipa --release
    artifacts:
      - build/**/outputs/**/*.aab
      - build/ios/ipa/*.ipa
    publishing:
      email:
        recipients:
          - dev@example.com
      google_play:
        credentials: $GCLOUD_SERVICE_ACCOUNT
        track: internal
      app_store_connect:
        api_key: $APP_STORE_CONNECT_API_KEY
        submit_to_testflight: true
```

---

## Q3: How do you use Fastlane for deployment?

```ruby
# android/fastlane/Fastfile
default_platform(:android)

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

# ios/fastlane/Fastfile
default_platform(:ios)

platform :ios do
  desc "Deploy to TestFlight"
  lane :beta do
    build_app(scheme: "Runner")
    upload_to_testflight(
      skip_waiting_for_build_processing: true
    )
  end

  desc "Deploy to App Store"
  lane :release do
    build_app(scheme: "Runner")
    upload_to_app_store(
      skip_screenshots: true,
      skip_metadata: true
    )
  end
end
```

```bash
# Run Fastlane
cd android && fastlane deploy    # Android
cd ios && fastlane beta          # iOS TestFlight
cd ios && fastlane release       # iOS App Store
```

---

## Q4: How do you manage signing and secrets?

```bash
# Android — keystore
# 1. Generate keystore
keytool -genkey -v -keystore upload-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 -alias upload

# 2. android/key.properties
storePassword=********
keyPassword=********
keyAlias=upload
storeFile=../upload-keystore.jks

# 3. android/app/build.gradle
def keystoreProperties = new Properties()
keystoreProperties.load(new FileInputStream(rootProject.file('key.properties')))

android {
  signingConfigs {
    release {
      keyAlias keystoreProperties['keyAlias']
      keyPassword keystoreProperties['keyPassword']
      storeFile file(keystoreProperties['storeFile'])
      storePassword keystoreProperties['storePassword']
    }
  }
  buildTypes {
    release { signingConfig signingConfigs.release }
  }
}
```

```yaml
# GitHub Actions — store secrets in repository secrets
# Settings → Secrets and variables → Actions
# ANDROID_KEYSTORE_BASE64
# ANDROID_KEY_PASSWORD
# ANDROID_STORE_PASSWORD
# APP_STORE_CONNECT_API_KEY

# Use in workflow
- name: Decode keystore
  run: echo "${{ secrets.ANDROID_KEYSTORE_BASE64 }}" | base64 --decode > android/upload-keystore.jks

- name: Create key.properties
  run: |
    echo "storePassword=${{ secrets.ANDROID_STORE_PASSWORD }}" > android/key.properties
    echo "keyPassword=${{ secrets.ANDROID_KEY_PASSWORD }}" >> android/key.properties
    echo "keyAlias=upload" >> android/key.properties
    echo "storeFile=upload-keystore.jks" >> android/key.properties
```

---

## Q5: How do you implement versioning?

```yaml
# pubspec.yaml
version: 1.2.3+45  # version_name+version_code
# 1.2.3 = version name (shown to users)
# 45 = version code (integer, increments each release)
```

```python
# .github/scripts/bump_version.py
import re

with open('pubspec.yaml', 'r') as f:
    content = f.read()

# Bump version
match = re.search(r'version:\s+(\d+)\.(\d+)\.(\d+)\+(\d+)', content)
major, minor, patch, code = match.groups()
new_code = int(code) + 1
new_version = f'{major}.{minor}.{int(patch) + 1}+{new_code}'

content = re.sub(
    r'version:\s+\d+\.\d+\.\d+\+\d+',
    f'version: {new_version}',
    content
)

with open('pubspec.yaml', 'w') as f:
    f.write(content)

print(f'New version: {new_version}')
```

```yaml
# GitHub Actions — auto-bump on merge
- name: Bump version
  run: python .github/scripts/bump_version.py

- name: Commit version
  run: |
    git config user.name "CI Bot"
    git config user.email "ci@example.com"
    git add pubspec.yaml
    git commit -m "chore: bump version"
    git push
```

---

## Q6: How do you distribute to testers?

```yaml
# Firebase App Distribution (Android)
- name: Distribute to Firebase
  uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_ANDROID_APP_ID }}
    serviceCredentialsFile: firebase-credentials.json
    groups: testers
    file: build/app/outputs/flutter-apk/app-release.apk

# TestFlight (iOS) — via Fastlane
- name: Upload to TestFlight
  run: |
    cd ios && fastlane beta

# Codemagic — email distribution
publishing:
  email:
    recipients:
      - tester1@example.com
      - tester2@example.com
```

---

## Q7: How do you set up code quality checks?

```yaml
# .github/workflows/quality.yml
name: Code Quality

on: [pull_request]

jobs:
  quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.19.0'

      - name: Check formatting
        run: dart format --set-exit-if-changed .

      - name: Analyze
        run: flutter analyze

      - name: Custom lint rules
        run: flutter pub run custom_lint

      - name: Run tests with coverage
        run: flutter test --coverage

      - name: Check coverage threshold
        run: |
          COVERAGE=$(grep -o 'SF:.*' coverage/lcov.info | wc -l)
          if [ $COVERAGE -lt 80 ]; then
            echo "Coverage below 80%"
            exit 1
          fi

      - name: Check for TODOs
        run: |
          if grep -r "TODO" lib/; then
            echo "TODOs found — resolve before merge"
            exit 1
          fi

      - name: Dependency check
        run: flutter pub outdated
```

### analysis_options.yaml
```yaml
include: package:flutter_lints/flutter.yaml

linter:
  rules:
    prefer_const_constructors: true
    prefer_const_literals_to_create_immutables: true
    avoid_print: true
    require_trailing_commas: true
    use_key_in_widget_constructors: true
    prefer_final_locals: true
    prefer_final_in_for_each: true

analyzer:
  exclude:
    - "**/*.g.dart"
    - "**/*.freezed.dart"
  errors:
    invalid_annotation_target: ignore
  language:
    strict-casts: true
    strict-raw-types: true
```

---

## 🔗 Related Topics
- [Testing](../intermediate/Testing.md)
- [Performance](Performance.md)
- [Architecture Patterns](ArchitecturePatterns.md)
