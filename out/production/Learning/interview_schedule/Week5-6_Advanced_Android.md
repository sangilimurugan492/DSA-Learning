# Week 5-6: Advanced Android + Kotlin Advanced + CICD + Security

> **Duration:** 2 weeks | **Hours:** 36 hrs (18 hrs/week) | **DSA Problems:** ~20

---

## 📅 Daily Schedule

### Week 5: Kotlin Advanced + Android Advanced

| Day | Time | Theory (1.5 hr) | DSA (30 min) |
|-----|------|-----------------|--------------|
| Mon | 2hr | 🔴 [Coroutines](../kotlin_questions/advanced/Coroutines.md) + [Flows](../kotlin_questions/advanced/Flows.md) | 2 tree problems |
| Tue | 2hr | 🔴 [Generics](../kotlin_questions/advanced/Generics.md) + [DSL](../kotlin_questions/advanced/DSL.md) | 2 tree problems |
| Wed | 2hr | 🔴 [Delegated Properties](../kotlin_questions/advanced/DelegatedProperties.md) + [Value Classes](../kotlin_questions/advanced/ValueClasses.md) | 2 BST problems |
| Thu | 2hr | 🔴 [Reflection & Annotations](../kotlin_questions/advanced/ReflectionAndAnnotations.md) + [Serialization](../kotlin_questions/advanced/Serialization.md) | 2 BST problems |
| Fri | 2hr | 🔴 [Dependency Injection](../android_questions/advanced/DependencyInjection.md) + [Performance](../android_questions/advanced/Performance.md) | 2 heap problems |
| Sat | 4hr | 🔴 [Testing](../android_questions/advanced/Testing.md) + [Custom Views](../android_questions/advanced/CustomViews.md) | 4 heap problems |
| Sun | 4hr | 🔴 [Modularization](../android_questions/advanced/Modularization.md) + [Security](../android_questions/advanced/Security.md) + Revision | 4 trie problems |

### Week 6: CICD + Security + Android Scenarios

| Day | Time | Theory (1.5 hr) | DSA (30 min) |
|-----|------|-----------------|--------------|
| Mon | 2hr | 🔴 [Play Integrity](../android_questions/advanced/PlayIntegrity.md) + [CICD](../android_questions/advanced/CICD.md) | 2 bit manipulation problems |
| Tue | 2hr | 🟡 [Jenkins](../cicd_questions/Jenkins.md) + [GitHub Actions (CICD)](../cicd_questions/GitHubActions.md) | 2 bit manipulation problems |
| Wed | 2hr | 🟡 [CICD Scenarios](../cicd_questions/CICDScenarios.md) + [Keystore](../security_questions/Keystore.md) | 2 mixed problems |
| Thu | 2hr | 🟡 [Data Encryption](../security_questions/DataEncryption.md) + [SSL Pinning](../security_questions/SSLPinning.md) | 2 mixed problems |
| Fri | 2hr | 🟡 [Biometric Auth](../security_questions/BiometricAuth.md) + [Security Scenarios](../security_questions/SecurityScenarios.md) | 2 mixed problems |
| Sat | 4hr | 🟡 [Android Scenarios](../android_questions/scenario_based/README.md) — Architecture, BackgroundTask, Debugging | 4 mixed problems |
| Sun | 4hr | 🟡 [Android Scenarios](../android_questions/scenario_based/) — Lifecycle, Networking, UI + Revision | 4 mixed problems |

---

## 📖 Topics to Cover

### Kotlin Advanced (8 files) 🔴
| File | Key Concepts |
|------|-------------|
| [Coroutines](../kotlin_questions/advanced/Coroutines.md) | suspend, launch, async, dispatchers, structured concurrency |
| [Flows](../kotlin_questions/advanced/Flows.md) | cold/hot flows, StateFlow, SharedFlow, operators |
| [Generics](../kotlin_questions/advanced/Generics.md) | variance (in/out), star projection, reified |
| [DSL](../kotlin_questions/advanced/DSL.md) | @DslMarker, builder pattern, type-safe builders |
| [Delegated Properties](../kotlin_questions/advanced/DelegatedProperties.md) | by lazy, by observable, custom delegates |
| [Value Classes](../kotlin_questions/advanced/ValueClasses.md) | @JvmInline, zero-allocation wrappers |
| [Reflection & Annotations](../kotlin_questions/advanced/ReflectionAndAnnotations.md) | KClass, KFunction, custom annotations |
| [Serialization](../kotlin_questions/advanced/Serialization.md) | kotlinx.serialization, @Serializable, @SerialName |

### Android Advanced (8 files) 🔴
| File | Key Concepts |
|------|-------------|
| [CICD](../android_questions/advanced/CICD.md) | Pipeline, automation, distribution |
| [Custom Views](../android_questions/advanced/CustomViews.md) | onMeasure, onDraw, Canvas, Paint |
| [Dependency Injection](../android_questions/advanced/DependencyInjection.md) | Hilt, Dagger, @Module, @Inject, scopes |
| [Performance](../android_questions/advanced/Performance.md) | Profiler, memory leaks, ANR, jank |
| [Play Integrity](../android_questions/advanced/PlayIntegrity.md) | App integrity, device attestation |
| [Security](../android_questions/advanced/Security.md) | ProGuard, R8, network security config |
| [Testing](../android_questions/advanced/Testing.md) | Unit, instrumented, UI tests, coverage |
| [Modularization](../android_questions/advanced/Modularization.md) | Module structure, dynamic features, boundaries |

### Android Scenarios (7 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Architecture Scenarios](../android_questions/scenario_based/ArchitectureScenarios.md) | MVVM vs MVI, Clean Architecture decisions |
| [Background Task Scenarios](../android_questions/scenario_based/BackgroundTaskScenarios.md) | WorkManager vs Service vs Coroutine |
| [Debugging Scenarios](../android_questions/scenario_based/DebuggingScenarios.md) | Memory leaks, ANR, crash analysis |
| [Lifecycle Scenarios](../android_questions/scenario_based/LifecycleScenarios.md) | Config changes, process death, multi-window |
| [Networking Scenarios](../android_questions/scenario_based/NetworkingScenarios.md) | Offline, retry, caching, pagination |
| [UI Scenarios](../android_questions/scenario_based/UIScenarios.md) | Complex lists, animations, custom views |

### CICD (4 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Jenkins](../cicd_questions/Jenkins.md) | Jenkinsfile, pipeline, agents, parallel builds |
| [GitHub Actions](../cicd_questions/GitHubActions.md) | Workflows, matrix, secrets, caching |
| [CICD Scenarios](../cicd_questions/CICDScenarios.md) | PR gates, auto-deploy, rollback, security scan |

### Security (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Keystore](../security_questions/Keystore.md) | AndroidKeyStore, key generation, aliases |
| [Data Encryption](../security_questions/DataEncryption.md) | AES, RSA, encryption/decryption |
| [SSL Pinning](../security_questions/SSLPinning.md) | Certificate pinning, network security config |
| [Biometric Auth](../security_questions/BiometricAuth.md) | BiometricPrompt, crypto object, fallback |
| [Security Scenarios](../security_questions/SecurityScenarios.md) | Real-world security problems |

---

## 🧮 DSA Problems (Week 5-6)

### Week 5: Trees + BST + Heaps

| Day | Problem | File | Difficulty |
|-----|---------|------|-----------|
| Mon | Tree traversals | [DSA/tree/traversal/](../DSA/tree/traversal/) | Easy-Med |
| Mon | Binary tree problems | [DSA/tree/binary_tree/](../DSA/tree/binary_tree/) | Medium |
| Tue | BST problems | [DSA/tree/bst/](../DSA/tree/bst/) | Medium |
| Tue | TreeNode class | [TreeNode](../DSA/tree/TreeNode.kt) | Easy |
| Wed | Kth Largest Element | [KthLargestElement](../DSA/heap/KthLargestElement.kt) | Medium |
| Wed | Top K Frequent Elements (heap) | [TopKFrequentElements](../DSA/heap/TopKFrequentElements.kt) | Medium |
| Thu | Merge K Sorted Lists | [MergeKSortedLists](../DSA/heap/MergeKSortedLists.kt) | Hard |
| Thu | Find Median from Data Stream | [FindMedianFromDataStream](../DSA/heap/FindMedianFromDataStream.kt) | Hard |
| Fri | Heap review | [DSA/heap/](../DSA/heap/) | Medium-Hard |
| Sat | Tree + BST review | [DSA/tree/](../DSA/tree/) | Easy-Hard |
| Sat | Heap review | [DSA/heap/](../DSA/heap/) | Medium-Hard |
| Sun | Mixed (tree + heap) | — | Easy-Hard |
| Sun | Mixed (tree + heap) | — | Easy-Hard |

### Week 6: Tries + Bit Manipulation + Mixed

| Day | Problem | File | Difficulty |
|-----|---------|------|-----------|
| Mon | Implement Trie | [ImplementTrie](../DSA/trie/ImplementTrie.kt) | Medium |
| Mon | Word Search II | [WordSearchII](../DSA/trie/WordSearchII.kt) | Hard |
| Tue | Single Number (review) | [SingleNumber](../DSA/array/bit_manipulation/SingleNumber.kt) | Easy |
| Tue | Single Number II (review) | [SingleNumberII](../DSA/array/bit_manipulation/SingleNumberII.kt) | Medium |
| Wed | Bit manipulation review | [DSA/array/bit_manipulation/](../DSA/array/bit_manipulation/) | Easy-Med |
| Wed | Mixed review (trees) | [DSA/tree/](../DSA/tree/) | Easy-Hard |
| Thu | Mixed review (heaps) | [DSA/heap/](../DSA/heap/) | Medium-Hard |
| Thu | Mixed review (tries) | [DSA/trie/](../DSA/trie/) | Medium-Hard |
| Fri | Mixed review (all) | — | Easy-Hard |
| Sat | Mixed review (all) | — | Easy-Hard |
| Sun | Mixed review (all) | — | Easy-Hard |

---

## 🧠 Key Concepts to Memorize

### Coroutines & Flows
- `launch` = fire-and-forget, `async` = returns result
- Dispatchers: IO (network/disk), Main (UI), Default (CPU)
- `StateFlow` = state holder (hot), `SharedFlow` = events (hot)
- Flow operators: `map`, `filter`, `debounce`, `distinctUntilChanged`, `flatMapLatest`
- `cold flow` = one consumer, `hot flow` = multiple consumers

### DI (Hilt)
- `@Module` + `@InstallIn` = provide dependencies
- `@Inject` = request dependency
- Scopes: `@Singleton`, `@ViewModelScoped`, `@ActivityScoped`
- Components: `SingletonComponent`, `ViewModelComponent`, `ActivityComponent`

### Security
- **AndroidKeyStore:** hardware-backed key storage
- **AES** = symmetric (fast), **RSA** = asymmetric (secure)
- **SSL Pinning:** prevent MITM, pin certificate hash
- **BiometricPrompt:** Class 3 (strong) for crypto, Class 2 (weak) for unlock

### CICD
- **Jenkins:** Jenkinsfile, declarative pipeline, agents
- **GitHub Actions:** workflows, matrix builds, secrets, caching
- **Pipeline:** build → test → lint → deploy

---

## ✅ Self-Assessment Checklist

### Kotlin Advanced
- [ ] Can explain coroutine dispatchers and when to use each
- [ ] Can differentiate StateFlow vs SharedFlow
- [ ] Can use Flow operators (map, filter, flatMapLatest)
- [ ] Can create a type-safe DSL
- [ ] Can use `by lazy` and custom delegated properties
- [ ] Can use kotlinx.serialization with @Serializable

### Android Advanced
- [ ] Can set up Hilt with @Module, @InstallIn, @Inject
- [ ] Can identify and fix memory leaks
- [ ] Can create a custom View with onMeasure/onDraw
- [ ] Can write unit tests and instrumented tests
- [ ] Can structure a multi-module app
- [ ] Can configure ProGuard/R8

### CICD
- [ ] Can write a Jenkinsfile pipeline
- [ ] Can write a GitHub Actions workflow
- [ ] Can set up PR quality gates
- [ ] Can configure auto-deploy to Firebase

### Security
- [ ] Can use AndroidKeyStore to generate/store keys
- [ ] Can encrypt/decrypt data with AES
- [ ] Can implement SSL pinning
- [ ] Can implement BiometricPrompt with fallback

### DSA
- [ ] Solved 20 tree/heap/trie/bit problems
- [ ] Comfortable with tree traversals (inorder, preorder, postorder)
- [ ] Comfortable with heap operations
- [ ] Can implement a Trie from scratch

---

## 🔗 Next
- [Week 7-8: Jetpack Compose](Week7-8_Jetpack_Compose.md)
- [Back to README](README.md)
