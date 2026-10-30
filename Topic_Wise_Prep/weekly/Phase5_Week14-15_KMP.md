# Phase 5 (Weeks 14–15) — Kotlin Multiplatform

> Bank: [kmp_questions/](../../kmp_questions/)
> KMP questions are mostly *"why/when, not how"* — expect trade-off, migration and risk questions more than syntax.
> Phase deliverable: **KMP decision memo** (1 page: when you would/would not choose KMP, shared-layer boundary, risk list) — reuse it in the "tell me about an architecture decision" STAR story.

---

## Week 14 — KMP Foundations & Intermediate · Feb 1 – Feb 7 · 12 files / 3,750 lines

| Day | Files (in [kmp_questions/](../../kmp_questions/)) | ~Lines |
|-----|----------------------------------------------------|--------|
| Mon | `README` · `beginner/`: Basics, CommonCode, ExpectActual | 942 |
| Tue | `beginner/`: PlatformSpecific, ProjectSetup · `intermediate/Architecture` | 961 |
| Wed | `intermediate/`: Coroutines, Database | 782 |
| Thu | `intermediate/`: DependencyInjection, Networking, Testing | 1,065 |
| Sat | 10 hardest of W14 + 10 from W12 (Flutter state mgmt — same question, other stack) | — |
| Sun | 🧪 quiz · 🎤 STAR #14 · 🏗️ sketch shared domain layer + platform UI boundary for an existing app | — |

**Quiz:** ① `expect`/`actual` rules and the moment you know the abstraction is wrong. ② Shared code: what belongs in `commonMain` vs `androidMain`/`iosMain` (and the "leaky abstraction" smell). ③ multiplatform coroutines on iOS — main dispatcher, cold flows, what breaks with `runBlocking`. ④ SQLDelift/Room-KMP, Ktor + serialization, Koin vs Deko/DI choice — what each costs you. ⑤ How you test common code and what still needs an iOS/XCTest harness.

---

## Week 15 — KMP Advanced + Compose Multiplatform · Feb 8 – Feb 14 · 6 files / 2,038 lines · *light week*

| Day | Files (in [kmp_questions/](../../kmp_questions/)) | ~Lines |
|-----|----------------------------------------------------|--------|
| Mon | `advanced/`: CICD, ComposeMultiplatform | 622 |
| Tue | `advanced/`: LibraryDevelopment, Migration | 619 |
| Wed | `advanced/Performance` · `scenario_based/KMPScenarios` | 797 |
| Thu | 🔧 **Catch-up slot** — clear backlog, or write the KMP decision memo | — |
| Sat | Backlog review: every ❓ question from W11–W15 (this is the planned buffer) | — |
| Sun | 🧪 quiz · 🎤 STAR #15 · 🏗️ **Phase-5 closed-book re-quiz (Flutter + KMP)** | — |

**Quiz:** ① Compose Multiplatform: what is stable on iOS today (interop vs Skia/native rendering), memory/perf caveats, and what you would keep native. ② Publishing a KMP library: artifact setup, API surface, binary compatibility, versioning. ③ Migration strategy: strikangler order (domain → data → UI), how you keep both stacks shippable, rollback plan. ④ KMP CI: what you build for iOS on a Mac runner, cache strategy, flaky-simulator mitigation. ⑤ Sell / kill the KMP pitch in 90 seconds to a skeptical Android lead.

**Checkpoint:** decision memo written; you can name 3 real risks of KMP with mitigations (not marketing answers).
