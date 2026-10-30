# Phase 6 (Weeks 16–20) — Patterns, Quality, Delivery, Security, Realtime & Backend

> Banks: [solid/](../../solid/) · [design_patterns/](../../design_patterns/) · [github_questions/](../../github_questions/) · [testing_questions/](../../testing_questions/) · [cicd_questions/](../../cicd_questions/) · [security_questions/](../../security_questions/) · [webrtc_questions/](../../webrtc_questions/) · [firebase_questions/](../../firebase_questions/)
> ⚠️ [design_patterns/principles/solid/](../../design_patterns/principles/solid/) duplicates [solid/](../../solid/) — read `solid/` once (Week 16), skip the copy.
> Phase deliverable: **"how I ship quality" one-pager** (tests + CI gates + security checklist) — the backbone of the hiring-manager round.

---

## Week 16 — SOLID, Design Principles, Creational & Structural · Feb 15 – Feb 21 · 9 files / 3,445 lines

| Day | Files | ~Lines |
|-----|-------|--------|
| Mon | [solid/](../../solid/): D, I | 746 |
| Tue | [solid/](../../solid/): L, O, S, README | 950 |
| Wed | [design_patterns/README](../../design_patterns/README.md) · [principles/README](../../design_patterns/principles/README.md) | 730 |
| Thu | [creational/README](../../design_patterns/creational/README.md) · [structural/README](../../design_patterns/structural/README.md) | 1,047 |
| Sat | 10 hardest of W16 + 10 from W1 + W3 (same OOP questions, deeper answers now) | — |
| Sun | 🧪 quiz · 🎤 STAR #16 · 🏗️ label every decision in your last PR with the principle behind it | — |

**Quiz:** ① LSP with a real counter-example (throwing subclass / read-only collection) and how sealed classes help. ② DIP in Android: who owns the interface, why `@Inject` alone is not DIP. ③ Singleton: thread-safe forms, why `object` is fine but global mutable state is not, DI-provided singleton vs `object`. ④ Builder vs Factory vs Abstract Factory for a `NetworkClient` config. ⑤ Decorator vs Adapter vs Facade — name the classes in your own codebase.

---

## Week 17 — Patterns in Practice + Git & GitHub · Feb 22 – Feb 28 · 10 files / 3,696 lines

| Day | Files | ~Lines |
|-----|-------|--------|
| Mon | [behavioral/README](../../design_patterns/behavioral/README.md) — Observer, Strategy, Command… | 888 |
| Tue | [concurrency/README](../../design_patterns/concurrency/README.md) · [architectural/README](../../design_patterns/architectural/README.md) | 1,239 |
| Wed | [github_questions/](../../github_questions/): GitBasics, Branching, GitHubActions | 769 |
| Thu | [github_questions/](../../github_questions/): GitWorkflow, PullRequests, ScenarioBased, README | 800 |
| Sat | 10 hardest of W17 + 10 from W5 + W7 | — |
| Sun | 🧪 quiz · 🎤 STAR #17 · 🏗️ sketch Observer/Strategy/Repository as they actually appear in your app | — |

**Quiz:** ① Observer vs Flow — when a callback interface is still right. ② Strategy for validation/flags, Command for retry queues, Visitor — when to refuse it. ③ Concurrency patterns (producer-consumer, actor, thread-per-message, lock-free) mapped to coroutine/Flow constructs. ④ `rebase` vs `merge`, recovering a detached HEAD, `bisect` over 300 commits, resolving a 40-file rebase conflict. ⑤ Branch model for mobile (release branches, hotfix, changelog) + what you look for as a reviewer.

---

## Week 18 — Testing & TDD · Mar 1 – Mar 7 · 7 files / 4,668 lines

| Day | Files (in [testing_questions/](../../testing_questions/)) | ~Lines |
|-----|-----------------------------------------------------------|--------|
| Mon | `README` · `TDD` | 927 |
| Tue | `UnitTesting` · `Mockito` | 1,302 |
| Wed | `Espresso` · `ComposeTesting` | 1,289 |
| Thu | `TestingScenarios` | 1,150 |
| Sat | 10 hardest of W18 + 10 from W8 + W10 | — |
| Sun | 🧪 quiz · 🎤 STAR #18 · 🏗️ define the testing pyramid + CI gates for one real module | — |

**Quiz:** ① What you test vs what you don't — and the coverage number you would defend. ② Fake vs stub vs mock vs spy; why over-mocking is a design smell and how you refactor instead. ③ Testing coroutines: `runTest`, `TestDispatcher`, virtual time, Flow collection assertions. ④ Espresso vs Compose semantics vs Robolectric vs instrumented — pick per scenario with runtime cost. ⑤ Flaky-test triage: 5 causes, the fix for each, and how you stop the next one reaching `main`.

---

## Week 19 — CI/CD + App Security · Mar 8 – Mar 14 · 10 files / 4,150 lines

| Day | Files | ~Lines |
|-----|-------|--------|
| Mon | [cicd_questions/](../../cicd_questions/): README, GitHubActions | 729 |
| Tue | [cicd_questions/](../../cicd_questions/): Jenkins, CICDScenarios | 1,450 |
| Wed | [security_questions/](../../security_questions/): README, Keystore, DataEncryption | 777 |
| Thu | [security_questions/](../../security_questions/): SSLPinning, BiometricAuth, SecurityScenarios | 1,194 |
| Sat | 10 hardest of W19 + 10 from W11 + W13 | — |
| Sun | 🧪 quiz · 🎤 STAR #19 · 🏗️ write your mobile security checklist (storage, transport, auth, build) | — |

**Quiz:** ① Pipeline for mobile: signing, flavors, caching, per-PR vs release lanes, artifact promotion, rollback. ② Cutting CI below 15 min on a modular app — what you actually parallelise. ③ Keystore/StrongBox: hardware-backed keys, key attestation, why a hardcoded key over SharedPreferences is theatre. ④ Certificate pinning: trust anchors, pin rotation without bricking users, what a proxy attacker still gets. ⑤ `BiometricPrompt` flow with crypto object + fallback, and token storage/exchange for a banking-grade app.

---

## Week 20 — WebRTC + Firebase · Mar 15 – Mar 21 · 23 files / 5,566 lines · *heaviest week*

| Day | Files | ~Lines |
|-----|-------|--------|
| Mon | [webrtc_questions/](../../webrtc_questions/): README, WebRTCBasics, AndroidWebRTC, WebRTCScenarios | 1,161 |
| Tue | [firebase_questions/](../../firebase_questions/): README + `beginner/` (Authentication, Crashlytics, FirestoreBasics, RealtimeDatabase, Storage) | 1,157 |
| Wed | `intermediate/`: Analytics, CloudFunctions, FCM, RemoteConfig, SecurityRules | 1,210 |
| Thu | `advanced/`: AppCheck, Architecture, CostOptimization, Performance, Scaling | 1,189 |
| Sat | `scenario_based/`: DataModeling, Performance, Security Scenarios + 10 hardest of W14 + W16 | 849 |
| Sun | 🧪 quiz · 🎤 STAR #20 · 🏗️ **Phase-6 closed-book re-quiz + finish the quality one-pager** | — |

**Quiz:** ① Signalling, SDP offer/answer, ICE/STUN/TURN — the setup handshake in 3 minutes. ② Why TURN is unavoidable and what it costs per minute. ③ SFU vs MCU vs mesh, and where FCM helps (and where it is far too slow). ④ Firestore vs Realtime DB vs your own backend: data modelling, index explosion, read/write cost model, offline persistence. ⑤ Security Rules + AppCheck as your real backend boundary; Cloud Functions vs own server; how you'd cut a bill you can't explain.

**Checkpoint before Phase 7:** quality one-pager + security checklist finished — they feed straight into the Week 21–24 answers.

