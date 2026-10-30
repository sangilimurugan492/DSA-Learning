# Phase 1 (Weeks 1–4) — Language Depth: Java + Kotlin

> Banks: [java_questions/](../../java_questions/) · [kotlin_questions/](../../kotlin_questions/)
> Daily protocol: read headings → self-answer aloud → 3-line summary → mark 🔵 known / ❓ unknown in [Progress_Tracker.md](../Progress_Tracker.md).
> Phase deliverable (by end of W4): **2 one-pagers** — *Java concurrency & JVM*, *Kotlin coroutines & Flows* — plus 10 flashcards you re-answer every Saturday.

---

## Week 1 — Java: Core Language, OOP, Generics · Nov 2 – Nov 8 · 12 files / 2,914 lines

| Day | Files (in [java_questions/](../../java_questions/)) | ~Lines |
|-----|-----------------------------------------------------|--------|
| Mon | `README` · `beginner/`: Arrays, Basics, ControlFlow | 596 |
| Tue | `beginner/`: OOPBasics, Strings, VariablesAndDataTypes | 685 |
| Wed | `intermediate/`: OOPConcepts, ExceptionHandling | 594 |
| Thu | `intermediate/`: Generics, Concurrency · `scenario_based/OOPScenarios` | 1,039 |
| Sat | 10 hardest of this week + interview-style "explain X" out loud | — |
| Sun | 🧪 quiz below · 🎤 STAR #1 · 🏗️ sketch one app architecture on paper | — |

**Quiz (closed notes):** ① overload vs override vs hiding a static method — which bind at compile time? ② `equals`/`hashCode` contract — what breaks in `HashMap` if `hashCode` returns a constant? ③ String pool + `intern()`, and why JDK 9 moved `char[]` → `byte[]`. ④ Checked vs unchecked exceptions; what does `try-with-resources` require? ⑤ PECS: what can you read/write through `? extends T` vs `? super T`?

---

## Week 2 — Java: Collections, Streams, Concurrency, JVM · Nov 9 – Nov 15 · 13 files / 4,102 lines

| Day | Files (in [java_questions/](../../java_questions/)) | ~Lines |
|-----|-----------------------------------------------------|--------|
| Mon | `intermediate/`: Collections, LambdaAndStreams · `advanced/AndroidWithJava` | 971 |
| Tue | `advanced/`: ConcurrencyAdvanced, DesignPatterns, GarbageCollection | 1,012 |
| Wed | `advanced/`: JVMInternals, ReflectionAndAnnotations · `scenario_based/ConcurrencyScenarios` | 935 |
| Thu | `scenario_based/`: CollectionScenarios, DebuggingScenarios, AndroidJavaScenarios | 1,166 |
| Sat | `scenario_based/README` (index) + 10 hardest of W1–W2 | 18 |
| Sun | 🧪 quiz · 🎤 STAR #2 · 🏗️ paper design: "design the cache layer of an app" | — |

**Quiz:** ① walk through `HashMap.put` (hash → bucket → treeify at 8) and the load-factor consequence. ② `volatile` vs `synchronized` vs `Atomic*` — memory visibility vs atomicity. ③ `ThreadPoolExecutor` params + rejection policy; why `Executors.newFixedThreadPool` is risky. ④ How do you read a heap dump to find a leaked Activity/Context? ⑤ Why is `parallelStream` a bad default on Android?

---

## Week 3 — Kotlin: Core Language, Null Safety, Idioms · Nov 16 – Nov 22 · 16 files / 3,022 lines

| Day | Files (in [kotlin_questions/](../../kotlin_questions/)) | ~Lines |
|-----|---------------------------------------------------------|--------|
| Mon | `README` · `beginner/`: Arrays, Basics, ControlFlow, ExceptionHandling | 778 |
| Tue | `beginner/`: Functions, NullSafety, StringTemplates, TypeChecksAndSmartCasts | 787 |
| Wed | `beginner/`: VariablesAndDataTypes · `intermediate/`: OOP, DataAndSealedClasses, ScopeFunctions | 712 |
| Thu | `intermediate/`: Extensions · `scenario_based/`: NullSafetyScenarios, OOPScenarios | 745 |
| Sat | 10 hardest of W3 + 10 from W1 | — |
| Sun | 🧪 quiz · 🎤 STAR #3 · 🏗️ paper design: "offline-first reads/writes" | — |

**Quiz:** ① `val` vs `var` + `lateinit` vs `by lazy` — when does each blow up? ② `?.`, `?:`, `!!`, `let` — rewrite a null-check chain idiomatically. ③ Return value of `also`/`apply`/`let`/`run`/`with` — one example each. ④ data class vs sealed class vs value class — pick the right one and say why. ⑤ When does the compiler refuse a smart cast, and what is the fix?

---

## Week 4 — Kotlin: Coroutines, Flows, Advanced · Nov 23 – Nov 29 · 17 files / 4,463 lines

| Day | Files (in [kotlin_questions/](../../kotlin_questions/)) | ~Lines |
|-----|---------------------------------------------------------|--------|
| Mon | `intermediate/`: Collections, LambdasAndHigherOrderFunctions, InlineAndReified, OperatorOverloading · `advanced/Coroutines` | 1,010 |
| Tue | `advanced/`: DSL, DelegatedProperties, Flows, Generics | 941 |
| Wed | `advanced/`: ReflectionAndAnnotations, Serialization, ValueClasses | 1,037 |
| Thu | `scenario_based/`: CollectionScenarios, ConcurrencyScenarios, PerformanceScenarios | 1,037 |
| Sat | `scenario_based/`: RealWorldScenarios, README + 10 hardest of W3–W4 | 438 |
| Sun | 🧪 quiz · 🎤 STAR #4 · 🏗️ **Phase-1 closed-book re-quiz (Java + Kotlin, 20 Qs)** | — |

**Quiz:** ① `suspend` + dispatchers + `CoroutineScope` — what cancels a coroutine and how do you make cleanup safe? ② Flow vs StateFlow vs SharedFlow — buffering/backpressure operators you would name. ③ `inline`/`reified` restrictions and the bytecode cost of getting it wrong. ④ Write a property delegate from scratch (`getValue`/`setValue`) for a prefs-backed field. ⑤ Value-class erasure: what surprises show up in interop/JVM signatures?

**Checkpoint before Phase 2:** both one-pagers written, ≥ 90 % of 🔵/❓ marks recorded, and you can hold a 5-minute "Kotlin vs Java in production" answer without notes.
