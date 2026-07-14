# Week 11: Flutter + KMP + Firebase + WebRTC + Testing

> **Duration:** 1 week | **Hours:** 18 hrs | **DSA Problems:** ~10 (mixed review)

---

## 📅 Daily Schedule

| Day | Time | Theory (1.5 hr) | DSA (30 min) |
|-----|------|-----------------|--------------|
| Mon | 2hr | 🟡 [Flutter Basics](../flutter_questions/beginner/Basics.md) + [Dart Basics](../flutter_questions/beginner/DartBasics.md) + [Widgets](../flutter_questions/beginner/Widgets.md) | 2 mixed medium |
| Tue | 2hr | 🟡 [Flutter Layouts](../flutter_questions/beginner/Layouts.md) + [Navigation](../flutter_questions/beginner/Navigation.md) + [State Mgmt](../flutter_questions/beginner/StateManagement.md) | 2 mixed medium |
| Wed | 2hr | 🟡 [Flutter SM Fundamentals](../flutter_questions/state_management/Fundamentals.md) + [Provider](../flutter_questions/state_management/Provider.md) + [BLoC](../flutter_questions/state_management/BLoC.md) | 2 mixed medium |
| Thu | 2hr | 🟡 [Flutter Riverpod](../flutter_questions/state_management/Riverpod.md) + [GetX](../flutter_questions/state_management/GetX.md) + [Comparison](../flutter_questions/state_management/Comparison.md) | 2 mixed hard |
| Fri | 2hr | 🟡 [Flutter Intermediate](../flutter_questions/intermediate/) — Animations, CustomWidgets, HTTP, Firebase, Testing + [Advanced](../flutter_questions/advanced/) — Architecture, CICD, DI, Internals, Performance, PlatformChannels | 2 mixed hard |
| Sat | 4hr | 🟡 [Firebase Beginner](../firebase_questions/beginner/) + [Intermediate](../firebase_questions/intermediate/) + [Advanced](../firebase_questions/advanced/) + [Scenarios](../firebase_questions/scenario_based/) | 2 mixed review |
| Sun | 4hr | 🟡 [WebRTC](../webrtc_questions/) + [Testing](../testing_questions/) + [Flutter Scenarios](../flutter_questions/scenario_based/) + [Flutter SM Best Practices](../flutter_questions/state_management/BestPractices.md) + 🔴 [KMP Basics](../kmp_questions/beginner/Basics.md) + [KMP Architecture](../kmp_questions/intermediate/Architecture.md) | 2 mixed review |

---

## 📖 Topics to Cover

### Flutter Beginner (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Basics](../flutter_questions/beginner/Basics.md) | Flutter architecture, widget tree, hot reload |
| [Dart Basics](../flutter_questions/beginner/DartBasics.md) | Variables, functions, classes, async/await |
| [Layouts](../flutter_questions/beginner/Layouts.md) | Column, Row, Stack, Container, Padding |
| [Navigation](../flutter_questions/beginner/Navigation.md) | Navigator, routes, named routes, deep links |
| [State Management](../flutter_questions/beginner/StateManagement.md) | setState, StatefulWidget, basic concepts |
| [Widgets](../flutter_questions/beginner/Widgets.md) | StatelessWidget, StatefulWidget, build method |

### Flutter Intermediate (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Animations](../flutter_questions/intermediate/Animations.md) | Tween, AnimationController, Hero, implicit/explicit |
| [Custom Widgets](../flutter_questions/intermediate/CustomWidgets.md) | CustomPainter, RenderObject, composition |
| [Firebase Integration](../flutter_questions/intermediate/FirebaseIntegration.md) | Firebase setup, Auth, Firestore, Storage |
| [HTTP Networking](../flutter_questions/intermediate/HTTPNetworking.md) | http package, dio, interceptors, error handling |
| [State Management Advanced](../flutter_questions/intermediate/StateManagementAdvanced.md) | InheritedWidget, ChangeNotifier, streams |
| [Testing](../flutter_questions/intermediate/Testing.md) | Unit, widget, integration tests |

### Flutter Advanced (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Architecture Patterns](../flutter_questions/advanced/ArchitecturePatterns.md) | Clean Architecture, MVVM, Feature-first |
| [CICD](../flutter_questions/advanced/CICD.md) | Codemagic, GitHub Actions, Fastlane |
| [Dependency Injection](../flutter_questions/advanced/DependencyInjection.md) | get_it, injectable, provider |
| [Flutter Internals](../flutter_questions/advanced/FlutterInternals.md) | Element tree, RenderObject, layers |
| [Performance](../flutter_questions/advanced/Performance.md) | DevTools, rebuilds, const widgets, profiling |
| [Platform Channels](../flutter_questions/advanced/PlatformChannels.md) | MethodChannel, EventChannel, FFI |

### Flutter State Management (9 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Fundamentals](../flutter_questions/state_management/Fundamentals.md) | Ephemeral vs app state, unidirectional flow |
| [Provider](../flutter_questions/state_management/Provider.md) | ChangeNotifier, Consumer, Selector |
| [BLoC](../flutter_questions/state_management/BLoC.md) | Events, states, BlocBuilder, BlocListener |
| [Riverpod](../flutter_questions/state_management/Riverpod.md) | Providers, ref.watch, ref.read, code-gen |
| [GetX](../flutter_questions/state_management/GetX.md) | GetXController, Rx, bindings |
| [MobX](../flutter_questions/state_management/MobX.md) | Observables, actions, reactions |
| [Redux](../flutter_questions/state_management/Redux.md) | Store, reducer, middleware |
| [Comparison](../flutter_questions/state_management/Comparison.md) | When to use which |
| [Best Practices](../flutter_questions/state_management/BestPractices.md) | Patterns, anti-patterns |

### Flutter Scenarios (6 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Debugging Scenarios](../flutter_questions/scenario_based/DebuggingScenarios.md) | Rebuild issues, memory leaks |
| [Navigation Scenarios](../flutter_questions/scenario_based/NavigationScenarios.md) | Deep links, nested nav, bottom nav |
| [Performance Scenarios](../flutter_questions/scenario_based/PerformanceScenarios.md) | Jank, rebuilds, memory |
| [State Management Scenarios](../flutter_questions/scenario_based/StateManagementScenarios.md) | Complex state, persistence |
| [UI Scenarios](../flutter_questions/scenario_based/UIScenarios.md) | Custom UI, animations, theming |

### Firebase Beginner (5 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Authentication](../firebase_questions/beginner/Authentication.md) | Email, Google, Apple, phone auth |
| [Crashlytics](../firebase_questions/beginner/Crashlytics.md) | Crash reporting, custom keys, logs |
| [Firestore Basics](../firebase_questions/beginner/FirestoreBasics.md) | Collections, documents, queries |
| [Realtime Database](../firebase_questions/beginner/RealtimeDatabase.md) | JSON tree, listeners, rules |
| [Storage](../firebase_questions/beginner/Storage.md) | Upload, download, metadata |

### Firebase Intermediate (5 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Analytics](../firebase_questions/intermediate/Analytics.md) | Events, user properties, funnels |
| [Cloud Functions](../firebase_questions/intermediate/CloudFunctions.md) | HTTP, callable, triggers |
| [FCM](../firebase_questions/intermediate/FCM.md) | Push notifications, topics, data messages |
| [Remote Config](../firebase_questions/intermediate/RemoteConfig.md) | Feature flags, A/B testing |
| [Security Rules](../firebase_questions/intermediate/SecurityRules.md) | Firestore rules, RTDB rules, storage rules |

### Firebase Advanced (5 files) 🟡
| File | Key Concepts |
|------|-------------|
| [App Check](../firebase_questions/advanced/AppCheck.md) | Attestation, reCAPTCHA, Play Integrity |
| [Architecture](../firebase_questions/advanced/Architecture.md) | Firestore vs RTDB, offline, multi-region |
| [Cost Optimization](../firebase_questions/advanced/CostOptimization.md) | Reads, writes, deletes, storage costs |
| [Performance](../firebase_questions/advanced/Performance.md) | Indexing, batching, caching |
| [Scaling](../firebase_questions/advanced/Scaling.md) | Sharding, fan-out, rate limits |

### Firebase Scenarios (3 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Data Modeling](../firebase_questions/scenario_based/DataModelingScenarios.md) | NoSQL design, denormalization |
| [Performance](../firebase_questions/scenario_based/PerformanceScenarios.md) | Query optimization, caching |
| [Security](../firebase_questions/scenario_based/SecurityScenarios.md) | Rule design, access control |

### KMP (17 files) 🔴
| File | Key Concepts |
|------|-------------|
| [Basics](../kmp_questions/beginner/Basics.md) | KMP overview, commonMain, targets, Kotlin/Native |
| [Project Setup](../kmp_questions/beginner/ProjectSetup.md) | Gradle config, iOS targets, CocoaPods, framework |
| [Common Code](../kmp_questions/beginner/CommonCode.md) | Shared models, business logic, validation |
| [Expect/Actual](../kmp_questions/beginner/ExpectActual.md) | expect/actual mechanism, typealias, annotations |
| [Platform Specific](../kmp_questions/beginner/PlatformSpecific.md) | Android/iOS APIs, Swift interop, threading |
| [Architecture](../kmp_questions/intermediate/Architecture.md) | MVVM, Clean Architecture, shared ViewModels |
| [Networking](../kmp_questions/intermediate/Networking.md) | Ktor, engines, auth, retry, WebSocket |
| [Database](../kmp_questions/intermediate/Database.md) | SQLDelight, Room KMP, migrations, transactions |
| [Dependency Injection](../kmp_questions/intermediate/DependencyInjection.md) | Koin, Kotlin Inject, manual DI |
| [Coroutines](../kmp_questions/intermediate/Coroutines.md) | StateFlow, SharedFlow, Dispatchers, iOS interop |
| [Testing](../kmp_questions/intermediate/Testing.md) | commonTest, fakes, Turbine, MockEngine |
| [Compose Multiplatform](../kmp_questions/advanced/ComposeMultiplatform.md) | Shared UI, navigation, theming, UIKit interop |
| [Performance](../kmp_questions/advanced/Performance.md) | Build optimization, binary size, memory leaks |
| [CICD](../kmp_questions/advanced/CICD.md) | GitHub Actions, macOS runners, XCFramework |
| [Library Development](../kmp_questions/advanced/LibraryDevelopment.md) | Publishing, API design, binary compatibility |
| [Migration](../kmp_questions/advanced/Migration.md) | Android→KMP, Retrofit→Ktor, Room→SQLDelight |
| [KMP Scenarios](../kmp_questions/scenario_based/KMPScenarios.md) | Real-world problems, debugging, architecture |

### WebRTC (4 files) 🟡
| File | Key Concepts |
|------|-------------|
| [WebRTC Basics](../webrtc_questions/WebRTCBasics.md) | Signaling, STUN/TURN, ICE, SDP |
| [Android WebRTC](../webrtc_questions/AndroidWebRTC.md) | PeerConnection, MediaStream, SurfaceTexture |
| [WebRTC Scenarios](../webrtc_questions/WebRTCScenarios.md) | Video call, screen share, group call |
| [README](../webrtc_questions/README.md) | Overview |

### Testing (7 files) 🟡
| File | Key Concepts |
|------|-------------|
| [Unit Testing](../testing_questions/UnitTesting.md) | JUnit, assertions, fakes, stubs |
| [Espresso](../testing_questions/Espresso.md) | UI tests, ViewMatchers, ViewActions |
| [Mockito](../testing_questions/Mockito.md) | Mocking, verification, argument matchers |
| [TDD](../testing_questions/TDD.md) | Red-Green-Refactor, test pyramid |
| [Compose Testing](../testing_questions/ComposeTesting.md) | createComposeRule, semantics, assertions |
| [Testing Scenarios](../testing_questions/TestingScenarios.md) | Real-world testing problems |
| [README](../testing_questions/README.md) | Overview |

---

## 🧮 DSA Problems (Week 11 — Mixed Review)

| Day | Problem | Source | Difficulty |
|-----|---------|--------|-----------|
| Mon | Pick any medium | Any DSA folder | Medium |
| Mon | Pick any medium | Any DSA folder | Medium |
| Tue | Pick any medium | Any DSA folder | Medium |
| Tue | Pick any medium | Any DSA folder | Medium |
| Wed | Pick any hard | Any DSA folder | Hard |
| Wed | Pick any hard | Any DSA folder | Hard |
| Thu | Pick any hard | Any DSA folder | Hard |
| Thu | Pick any hard | Any DSA folder | Hard |
| Fri | Pick any medium | Any DSA folder | Medium |
| Fri | Pick any hard | Any DSA folder | Hard |
| Sat | Pick any 2 (review) | Any DSA folder | Mixed |
| Sun | Pick any 2 (review) | Any DSA folder | Mixed |

---

## 🧠 Key Concepts to Memorize

### Flutter
- Widget tree → Element tree → RenderObject tree
- `StatelessWidget` = immutable, `StatefulWidget` = mutable state
- `const` widgets = don't rebuild
- Provider: `ChangeNotifier` + `Consumer` + `context.watch()`
- BLoC: Events → Bloc → States
- Riverpod: `ref.watch()` (reactive) vs `ref.read()` (one-time)

### Firebase
- **Firestore:** NoSQL, collections → documents, real-time listeners
- **RTDB:** JSON tree, good for real-time sync
- **Security Rules:** `request.auth.uid == resource.data.userId`
- **Cost:** Firestore charges per read/write/delete — batch and cache
- **FCM:** Topics for broadcast, tokens for direct

### WebRTC
- **Signaling:** Exchange SDP/ICE candidates (not part of WebRTC)
- **STUN:** Discover public IP (NAT traversal)
- **TURN:** Relay server (fallback when P2P fails)
- **ICE:** Framework for connecting peers
- **SDP:** Session description (codecs, resolutions)

### Testing
- **Test Pyramid:** 70% unit, 20% integration, 10% E2E
- **TDD:** Red (write failing test) → Green (make it pass) → Refactor
- **Mockito:** `when().thenReturn()`, `verify()`
- **Compose Testing:** `onNodeWithText()`, `assertIsDisplayed()`, `performClick()`

---

## ✅ Self-Assessment Checklist

### Flutter
- [ ] Can explain widget tree vs element tree
- [ ] Can build a screen with Column, Row, Stack
- [ ] Can implement state with Provider and BLoC
- [ ] Can explain when to use Riverpod vs BLoC vs Provider
- [ ] Can implement navigation (named routes, deep links)
- [ ] Can use Platform Channels for native communication

### Firebase
- [ ] Can set up Firebase Auth (email, Google)
- [ ] Can design Firestore data model (collections, documents)
- [ ] Can write Firestore security rules
- [ ] Can send FCM push notifications
- [ ] Can optimize Firestore queries (indexing, batching)
- [ ] Can explain Firestore vs Realtime Database

### WebRTC
- [ ] Can explain signaling, STUN, TURN, ICE
- [ ] Can set up PeerConnection
- [ ] Can implement video call flow
- [ ] Can handle ICE candidates exchange

### Testing
- [ ] Can write unit tests with JUnit/Mockito
- [ ] Can write Espresso UI tests
- [ ] Can write Compose UI tests
- [ ] Can explain TDD cycle
- [ ] Can explain test pyramid

### DSA
- [ ] Solved 10 mixed review problems
- [ ] Maintaining speed on medium problems

---

## 🔗 Next
- [Week 12: Mock Interviews + Behavioral](Week12_Mock_Interviews_Behavioral.md)
- [Back to README](README.md)
