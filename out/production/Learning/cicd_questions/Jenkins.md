# Jenkins

## Q1: What is Jenkins and why use it for Android CI/CD?

Jenkins is an open-source automation server that enables continuous integration and continuous delivery.

### Why Jenkins for Android?
| Feature | Description |
|---------|-------------|
| Self-hosted | Full control over build environment |
| Plugin ecosystem | 1,800+ plugins (Gradle, Android, Firebase) |
| Pipeline as code | Define CI/CD in `Jenkinsfile` |
| Distributed builds | Multiple agents for parallel jobs |
| Free | No per-minute billing (unlike cloud CI) |

### Jenkins vs GitHub Actions
| Feature | Jenkins | GitHub Actions |
|---------|---------|----------------|
| Hosting | Self-hosted | Cloud (GitHub) |
| Cost | Server cost only | Per-minute billing |
| Setup | Complex | Simple |
| Plugins | 1,800+ | Marketplace actions |
| Control | Full | Limited |
| Maintenance | High | Low |

---

## Q2: How do you set up a Jenkins pipeline?

### Jenkinsfile (Declarative pipeline)
```groovy
pipeline {
    agent any

    environment {
        ANDROID_HOME = '/usr/local/share/android-sdk'
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew assembleDebug'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew testDebugUnitTest'
            }
        }

        stage('Lint') {
            steps {
                sh './gradlew lintDebug'
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/**/*.xml'
            archiveArtifacts '**/build/outputs/apk/**/*.apk'
        }
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
```

### Pipeline types
| Type | Description |
|------|-------------|
| Declarative | Structured, simpler syntax (recommended) |
| Scripted | Full Groovy power, more flexible |
| Multibranch | Auto-discovers branches, creates pipeline per branch |

---

## Q3: How do you configure Android build environment?

### Docker-based agent
```groovy
pipeline {
    agent {
        docker {
            image 'cimg/android:2024.1'
            args '-v /usr/local/share/android-sdk:/usr/local/share/android-sdk'
        }
    }

    environment {
        ANDROID_HOME = '/usr/local/share/android-sdk'
        JAVA_HOME = '/opt/openjdk-17'
    }

    stages {
        stage('Build') {
            steps {
                sh 'java -version'
                sh './gradlew --version'
                sh './gradlew assembleDebug'
            }
        }
    }
}
```

### Required tools on Jenkins agent
| Tool | Purpose |
|------|---------|
| JDK 17 | Compile Kotlin/Java |
| Android SDK | Build Android APK |
| Gradle | Build tool |
| Android NDK (optional) | Native code |
| Docker (optional) | Containerized builds |

---

## Q4: How do you sign and deploy APK/AAB?

### Sign release build
```groovy
pipeline {
    agent any

    environment {
        KEYSTORE = credentials('android-keystore')
        KEYSTORE_PASSWORD = credentials('keystore-password')
        KEY_ALIAS = credentials('key-alias')
        KEY_PASSWORD = credentials('key-password')
    }

    stages {
        stage('Build Release') {
            steps {
                sh """
                    ./gradlew assembleRelease \
                        -Pandroid.injected.signing.store.file=$KEYSTORE \
                        -Pandroid.injected.signing.store.password=$KEYSTORE_PASSWORD \
                        -Pandroid.injected.signing.key.alias=$KEY_ALIAS \
                        -Pandroid.injected.signing.key.password=$KEY_PASSWORD
                """
            }
        }

        stage('Build AAB') {
            steps {
                sh './gradlew bundleRelease'
            }
        }

        stage('Deploy to Firebase') {
            steps {
                sh """
                    ./gradlew appDistributionUploadRelease \
                        --appName="MyApp" \
                        --groups="testers" \
                        --releaseNotes="Build #${BUILD_NUMBER}"
                """
            }
        }
    }
}
```

### Jenkins credentials setup
1. Jenkins → Manage → Credentials → System → Global
2. Add credentials:
   - `android-keystore` (Secret file — upload .jks)
   - `keystore-password` (Secret text)
   - `key-alias` (Secret text)
   - `key-password` (Secret text)
3. Reference in pipeline with `credentials('id')`

---

## Q5: How do you run instrumented tests on Jenkins?

```groovy
pipeline {
    agent any

    stages {
        stage('Unit Tests') {
            steps {
                sh './gradlew testDebugUnitTest'
            }
            post {
                always {
                    junit '**/build/test-results/**/*.xml'
                }
            }
        }

        stage('Instrumented Tests') {
            steps {
                // Start Android emulator
                sh 'emulator -avd test_emulator -no-window -no-audio &'
                sh 'adb wait-for-device'
                sh 'adb shell input keyevent 82'  // Unlock

                // Run tests
                sh './gradlew connectedAndroidTest'

                // Stop emulator
                sh 'adb emu kill'
            }
            post {
                always {
                    junit '**/build/outputs/androidTest-results/**/*.xml'
                }
            }
        }
    }
}
```

### Using Firebase Test Lab (no emulator needed)
```groovy
stage('Firebase Test Lab') {
    steps {
        sh """
            gcloud firebase test android run \
                --type instrumentation \
                --app app/build/outputs/apk/debug/app-debug.apk \
                --test app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk \
                --device model=Pixel2,version=33,locale=en,orientation=portrait \
                --use-orchestrator
        """
    }
}
```

### Test approaches
| Approach | Speed | Cost | Real device? |
|----------|-------|------|-------------|
| Local emulator | Slow | Free | ❌ Emulated |
| Firebase Test Lab | Fast | Paid | ✅ Real |
| BrowserStack | Fast | Paid | ✅ Real |
| Jenkins + emulator | Slow | Free | ❌ Emulated |

---

## Q6: How do you use Jenkins multibranch pipeline?

```groovy
// Multibranch pipeline automatically creates a job per branch
// Configure in Jenkins → New Item → Multibranch Pipeline

pipeline {
    agent any

    stages {
        stage('Build') {
            when {
                branch 'PR-*'
            }
            steps {
                sh './gradlew assembleDebug'
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                sh './gradlew assembleRelease'
                sh 'firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk'
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                sh './gradlew bundleRelease'
                sh 'fastlane supply --aab app/build/outputs/bundle/release/app-release.aab'
            }
        }
    }
}
```

### Branch-based deployment
| Branch | Action |
|--------|--------|
| `feature/*` | Build + unit test |
| `PR-*` | Build + test + lint |
| `develop` | Deploy to Firebase (staging) |
| `main` | Deploy to Play Store (production) |
| `release/*` | Deploy to Play Store (internal track) |

---

## Q7: How do you parallelize builds in Jenkins?

```groovy
pipeline {
    agent any

    stages {
        stage('Parallel Build & Test') {
            parallel {
                stage('Build Debug') {
                    steps {
                        sh './gradlew assembleDebug'
                    }
                }
                stage('Unit Tests') {
                    steps {
                        sh './gradlew testDebugUnitTest'
                    }
                }
                stage('Lint') {
                    steps {
                        sh './gradlew lintDebug'
                    }
                }
                stage('Static Analysis') {
                    steps {
                        sh './gradlew detekt'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying...'
            }
        }
    }
}
```

### Parallel with different agents
```groovy
stage('Test Matrix') {
    parallel {
        stage('API 30') {
            agent { label 'android-api-30' }
            steps { sh './gradlew connectedAndroidTest' }
        }
        stage('API 33') {
            agent { label 'android-api-33' }
            steps { sh './gradlew connectedAndroidTest' }
        }
        stage('API 34') {
            agent { label 'android-api-34' }
            steps { sh './gradlew connectedAndroidTest' }
        }
    }
}
```

---

## Q8: How do you cache Gradle in Jenkins?

```groovy
pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew assembleDebug --build-cache --parallel'
            }
        }
    }

    // Cache Gradle dependencies
    post {
        always {
            // Save cache
            cache(maxCacheSize: 250, caches: [
                [$class: 'ArbitraryFileCache', path: '~/.gradle/caches', maxSize: '200M']
            ])
        }
    }
}
```

### Using Gradle build cache
```properties
# gradle.properties
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.configuration-cache=true
org.gradle.jvmargs=-Xmx4g
```

### Cache strategies
| What to cache | Path | Size |
|--------------|------|------|
| Gradle caches | `~/.gradle/caches` | ~200MB |
| Gradle wrapper | `~/.gradle/wrapper` | ~50MB |
| Android SDK | `$ANDROID_HOME` | ~3GB |
| Build outputs | `build/` | Varies |

---

## Q9: How do you handle Jenkins notifications?

```groovy
pipeline {
    agent any

    post {
        success {
            slackSend(
                channel: '#android-builds',
                color: 'good',
                message: "✅ Build #${BUILD_NUMBER} succeeded: ${env.JOB_NAME}"
            )
        }
        failure {
            slackSend(
                channel: '#android-builds',
                color: 'danger',
                message: "❌ Build #${BUILD_NUMBER} failed: ${env.JOB_NAME}\n${env.BUILD_URL}"
            )
        }
        unstable {
            slackSend(
                channel: '#android-builds',
                color: 'warning',
                message: "⚠️ Build #${BUILD_NUMBER} unstable: ${env.JOB_NAME}"
            )
        }
    }
}
```

### Notification channels
| Channel | Plugin | Use case |
|---------|--------|---------|
| Slack | Slack Notification | Team alerts |
| Email | Email Extension | Detailed reports |
| Microsoft Teams | Office 365 Connector | Enterprise |
| Discord | Discord Notifier | Community projects |

---

## Q10: How do you secure Jenkins?

### Security best practices
```groovy
// 1. Use credentials, never hardcode secrets
environment {
    API_KEY = credentials('api-key')  // ✅ Secure
    // API_KEY = 'sk_live_abc123'    // ❌ Never
}

// 2. Mask secrets in logs
steps {
    sh '''
        set +x  // Don't echo commands
        ./gradlew build -PapiKey=$API_KEY
    '''
}

// 3. Use role-based access
// Jenkins → Manage → Role-Based Strategy
// - Admin: full access
// - Developer: build/deploy to staging
// - Viewer: read-only
```

### Security checklist
| Item | Action |
|------|--------|
| CSRF protection | Enable (default on) |
| Credentials | Use Jenkins credentials store |
| Agent security | Enable agent-to-master security |
| Matrix auth | Role-based access control |
| HTTPS | Use reverse proxy with TLS |
| Audit log | Enable audit logging |
| Plugin updates | Keep plugins updated |
| Backup | Backup JENKINS_HOME regularly |

---

## Q11: How do you set up Jenkins agents for Android?

### Agent types
| Type | Description | Use case |
|------|-------------|---------|
| Permanent agent | Dedicated machine | Heavy builds |
| Docker agent | Container per build | Isolated, reproducible |
| Kubernetes agent | Pod per build | Scalable, cloud |
| EC2 agent | Auto-scale on AWS | On-demand |

### Docker agent setup
```groovy
pipeline {
    agent {
        docker {
            image 'cimg/android:2024.1'
            args '--privileged -v $ANDROID_HOME:/android-sdk'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh './gradlew assembleDebug'
            }
        }
    }
}
```

### Kubernetes agent setup
```groovy
pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: android
    image: cimg/android:2024.1
    command: ['cat']
    tty: true
    resources:
      requests:
        memory: "4Gi"
        cpu: "2"
'''
        }
    }
    stages {
        stage('Build') {
            steps {
                container('android') {
                    sh './gradlew assembleDebug'
                }
            }
        }
    }
}
```

---

## Q12: How do you implement a release pipeline?

```groovy
pipeline {
    agent any

    parameters {
        string(name: 'VERSION_NAME', defaultValue: '1.0.0', description: 'Version name')
        string(name: 'VERSION_CODE', defaultValue: '1', description: 'Version code')
        choice(name: 'TRACK', choices: ['internal', 'alpha', 'beta', 'production'], description: 'Play Store track')
        booleanParam(name: 'ROLLOUT_PERCENTAGE', defaultValue: false, description: 'Enable staged rollout')
    }

    stages {
        stage('Bump Version') {
            steps {
                sh """
                    sed -i 's/versionCode .*/versionCode ${params.VERSION_CODE}/' app/build.gradle
                    sed -i 's/versionName .*/versionName "${params.VERSION_NAME}"/' app/build.gradle
                """
            }
        }

        stage('Build Release') {
            steps {
                sh './gradlew bundleRelease'
                archiveArtifacts 'app/build/outputs/bundle/release/*.aab'
            }
        }

        stage('Deploy to Play Store') {
            steps {
                sh """
                    fastlane supply \
                        --aab app/build/outputs/bundle/release/app-release.aab \
                        --track ${params.TRACK} \
                        --skip_upload_metadata \
                        --skip_upload_images \
                        --skip_upload_screenshots
                """
            }
        }

        stage('Staged Rollout') {
            when {
                expression { params.ROLLOUT_PERCENTAGE }
            }
            steps {
                sh """
                    fastlane supply \
                        --aab app/build/outputs/bundle/release/app-release.aab \
                        --track production \
                        --rollout 0.1
                """
                echo 'Deployed to 10% of users'
            }
        }

        stage('Tag Release') {
            steps {
                sh """
                    git tag v${params.VERSION_NAME}
                    git push origin v${params.VERSION_NAME}
                """
            }
        }
    }
}
```

### Release tracks
| Track | Audience | Use case |
|-------|----------|---------|
| Internal | Up to 100 testers | Quick internal testing |
| Alpha (Closed) | Selected testers | Early access |
| Beta (Open) | Anyone with link | Public beta |
| Production | All users | Full release |

---

## Q13: How do you handle build failures and retries?

```groovy
pipeline {
    agent any

    options {
        retry(3)  // Retry entire pipeline 3 times
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew assembleDebug'
            }
        }

        stage('Flaky Test') {
            steps {
                retry(3) {
                    sh './gradlew testDebugUnitTest --tests "*.FlakyTest"'
                }
            }
        }
    }

    post {
        failure {
            // Collect artifacts for debugging
            archiveArtifacts '**/build/reports/**'
            archiveArtifacts '**/build/outputs/logs/**'

            // Notify team
            slackSend(
                color: 'danger',
                message: "Build failed: ${env.BUILD_URL}\nLast commit: ${env.GIT_COMMIT}"
            )
        }
    }
}
```

---

## Q14: How do you integrate SonarQube analysis?

```groovy
pipeline {
    agent any

    environment {
        SONAR_SCANNER = tool 'SonarQubeScanner'
    }

    stages {
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        $SONAR_SCANNER/bin/sonar-scanner \
                        -Dsonar.projectKey=my-android-app \
                        -Dsonar.sources=app/src/main \
                        -Dsonar.tests=app/src/test \
                        -Dsonar.kotlin.coverage.reportPaths=app/build/reports/jacoco/jacoco.xml \
                        -Dsonar.junit.reportPaths=app/build/test-results
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
```

### Quality gate checks
| Metric | Threshold |
|--------|----------|
| Coverage | > 70% |
| Duplicated lines | < 3% |
| Code smells | < 10 |
| Critical issues | 0 |
| Security rating | A |

---

## Q15: What are Jenkins best practices for Android?

### Do's
- ✅ Use `Jenkinsfile` in repo (pipeline as code)
- ✅ Use Docker agents for reproducible builds
- ✅ Store secrets in Jenkins credentials
- ✅ Cache Gradle dependencies
- ✅ Run unit tests on every push
- ✅ Use parallel stages for speed
- ✅ Archive APK/AAB artifacts
- ✅ Set up quality gates (lint, SonarQube)

### Don'ts
- ❌ Don't hardcode secrets in Jenkinsfile
- ❌ Don't run instrumented tests on every push (slow)
- ❌ Don't use `master` branch for Jenkins (security)
- ❌ Don't install all plugins (only what you need)
- ❌ Don't skip post-build cleanup
- ❌ Don't use `sh "echo $SECRET"` (leaks in logs)

### Pipeline optimization
| Technique | Impact |
|-----------|--------|
| Gradle build cache | 30-50% faster |
| Parallel stages | 40-60% faster |
| Docker layer caching | 20-30% faster |
| Configuration cache | 20-40% faster |
| Incremental builds | 50-80% faster |

---

## 🔗 Related Topics
- [GitHub Actions](GitHubActions.md)
- [CICD Scenarios](CICDScenarios.md)
