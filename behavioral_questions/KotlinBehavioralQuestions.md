# Kotlin Behavioral Questions — Senior Engineer (STAR Format)

> STAR = **S**ituation, **T**ask, **A**ction, **R**esult
> Each answer is framed from the perspective of a Senior Kotlin Engineer.

---

## Q1: Tell me about a time you migrated a Java codebase to Kotlin.

### Situation
Our Android app was 70% Java and 30% Kotlin. The Java code was harder to maintain — verbose, null-unsafe, and lacked extension functions. New code was written in Kotlin, but the Java code was untouched, creating a fragmented codebase. The team wanted to migrate to 100% Kotlin.

### Task
I needed to migrate 50+ Java files to Kotlin without breaking functionality or blocking feature development.

### Action
- **Prioritized by risk and churn** — I categorized Java files by: (1) frequently changed (migrate first), (2) stable but complex (migrate carefully), (3) stable and simple (migrate last). This ensured we migrated high-impact files first.
- **Used IntelliJ's auto-converter** as a starting point, then manually reviewed and improved each file. Auto-conversion produced working but non-idiomatic Kotlin.
- **Created a "Kotlin-ization" checklist** — after auto-conversion, I checked for: null safety (`?` and `!!`), `val` vs `var`, data classes, sealed classes, extension functions, scope functions, and string templates.
- **Migrated one module at a time** — I started with the data layer (models, repositories), then the domain layer (use cases), then the UI layer. This minimized cross-language boundary issues.
- **Used `@JvmStatic` and `@JvmField`** where Java code still called Kotlin — this allowed gradual migration without breaking interop.
- **Added Detekt** with Kotlin style rules to ensure new Kotlin code was idiomatic.
- **Paired with Java-heavy engineers** who were less comfortable with Kotlin to help them write idiomatic code.

### Result
Over 4 months, all 50+ Java files were migrated to Kotlin. Code size decreased by ~35% (measured by LOC). NullPointerExceptions in the migrated code dropped to 0 (thanks to Kotlin's null safety). The Detekt rules caught 200+ style violations in the first month, which were fixed during migration. No feature development was blocked. The migration checklist was adopted by 2 other teams.

---

## Q2: Describe a time you had to debug a complex coroutine issue.

### Situation
We had a feature that fetched user data from 3 APIs concurrently using `async`/`await`. Intermittently, the feature would hang — the loading spinner would spin forever. It happened on ~2% of sessions and was not reproducible in testing.

### Task
I needed to find and fix the hang without being able to reproduce it reliably.

### Action
- **Analyzed the code** — the feature used `coroutineScope { val a = async { api1() }; val b = async { api2() }; val c = async { api3() }; combine(a.await(), b.await(), c.await()) }`. If any API call hung, the entire scope would hang.
- **Added timeout logging** — I wrapped each `async` call with `withTimeoutOrNull(10_000)` and logged which API was timing out. I deployed this in a debug build to internal testers.
- **Identified the culprit** — API2 was hanging. The API call itself was fine, but the `OkHttp` interceptor was retrying indefinitely on a specific error response (a bug in our retry interceptor).
- **Fixed the root cause** — the retry interceptor had a `while (true)` loop that retried on HTTP 429 (Too Many Requests) without checking the `Retry-After` header. It retried forever. I added a max retry count and `Retry-After` handling.
- **Fixed the coroutine code** — I added `withTimeout` to each `async` call so that one hanging API wouldn't block the others. I also used `supervisorScope` so that one failure didn't cancel the others.
- **Added a test** that simulated a hanging API call and verified the timeout worked.
- **Added coroutine debugging** — I enabled `DebugProbes` in debug builds to dump coroutine states when the app hung.

### Result
The hang was fixed within 24 hours of deploying the debug build. The retry interceptor fix also reduced our API error rate by 5% (the infinite retries were overloading the server). The `withTimeout` + `supervisorScope` pattern was adopted as the standard for concurrent API calls. The `DebugProbes` setup was added to all debug builds and helped debug 3 more coroutine issues in the following months. The key learning was that `async`/`await` without timeout is dangerous — always set a timeout.

---

## Q3: Tell me about a time you had to design a clean architecture in Kotlin.

### Situation
Our app had a "fat ViewModel" problem — ViewModels contained business logic, API calls, database access, and formatting. They were 500+ lines, untestable, and duplicated logic across screens. The team wanted to adopt Clean Architecture but was overwhelmed by the number of layers and abstractions.

### Task
I needed to introduce Clean Architecture in a pragmatic way that improved testability without over-engineering.

### Action
- **Defined 3 layers** — Presentation (ViewModel + UiState), Domain (UseCase + Repository interface), Data (Repository implementation + API + DB). I kept it simple — no mappers for the sake of mappers.
- **Created UseCases** for each business operation — `GetUserUseCase`, `UpdateCartUseCase`, etc. Each UseCase had a single `operator fun invoke()` method, making them readable and testable.
- **Defined Repository interfaces in Domain** and implementations in Data. This allowed mocking in tests without a real database.
- **Used Kotlin's `Result` wrapper** for error handling instead of try/catch — `Result<User>` made error states explicit in the type system.
- **Created a `BaseUseCase`** with coroutine dispatching to reduce boilerplate: `abstract class SuspendUseCase<in P, R> { suspend operator fun invoke(params: P): Result<R> }`.
- **Migrated one feature as a pilot** — the user profile. I documented the before/after and measured: test coverage went from 20% to 85%.
- **Wrote a "Pragmatic Clean Architecture" guide** — I explicitly called out what NOT to do: don't create a UseCase for a single-line repository call, don't create separate mappers if the models are identical, don't add layers you don't need.

### Result
Over 3 months, 60% of features were migrated to Clean Architecture. ViewModel size dropped from 500+ lines to 100-150 lines. Unit test coverage for business logic reached 85%. The "Pragmatic Clean Architecture" guide prevented over-engineering — the team adopted the pattern without creating unnecessary abstractions. The guide was shared with 3 other teams and is now the company standard for new features.

---

## Q4: Tell me about a time you had to handle a concurrency issue in Kotlin.

### Situation
We had a feature that synced data from the server to a local Room database. The sync ran on `Dispatchers.IO` and was triggered by a `WorkManager` job. Intermittently, we got `SQLiteException: database is locked` crashes when the sync ran while the user was also writing to the database (e.g., adding a favorite).

### Task
I needed to fix the concurrency issue and ensure the sync and user writes never conflicted.

### Action
- **Identified the root cause** — both the sync job and user writes were using separate Room database instances (created via `Room.databaseBuilder()` in different modules). SQLite only allows one writer at a time, and the two instances were fighting for the write lock.
- **Consolidated to a single Room instance** — I created a singleton `AppDatabase` and used dependency injection (Hilt) to provide it everywhere. This ensured all writes went through the same connection.
- **Used `withTransaction`** for batch writes in the sync — Room's `withTransaction` acquires a single write lock for the entire batch, preventing interleaving with user writes.
- **Implemented a `Mutex`** for critical sections where order mattered — e.g., updating the user's favorites while the sync was updating the same table. I used `kotlinx.coroutines.sync.Mutex` with `withLock`.
- **Added `@Transaction` annotations** to DAO methods that did multiple writes.
- **Wrote a concurrency test** — I used `runTest` with multiple coroutines writing to the database simultaneously and verified no crashes.
- **Set `enableMultiInstanceInvalidation` to false** since we only had one instance.

### Result
The `SQLiteException: database is locked` crash was eliminated — 0 occurrences after the fix. The singleton database pattern was adopted across the app. The `withTransaction` + `Mutex` pattern was documented as the standard for concurrent database access. The concurrency test was added to CI and caught a similar issue 2 months later when a new feature tried to create its own database instance. The key learning was that Room should always be a singleton, and concurrent writes need explicit transaction management.

---

## Q5: Describe a time you had to improve Kotlin code quality across a team.

### Situation
Our Kotlin codebase had inconsistent patterns — some engineers used `lateinit`, some used `lazy`, some used `by Delegates.observable`, and null handling varied from `!!` to `?.let {}` to `?: return`. Code reviews were spending more time on style debates than on logic.

### Task
I needed to standardize Kotlin patterns across the team without creating a 50-page style guide that no one would read.

### Action
- **Set up Detekt** with a focused rule set — I didn't enable every rule. I picked 15 rules that addressed our actual pain points: `MagicNumber`, `TooGenericExceptionCaught`, `UnsafeCall`, `LateinitUsage`, `ReturnCount`, `LongMethod`, `LargeClass`.
- **Created a one-page "Kotlin Patterns" guide** — not a style guide, but a decision tree: "Use `lateinit` when... Use `lazy` when... Use `Delegates.notNull` when..." Each pattern had a ✅ and ❌ example.
- **Configured Detekt in CI** as a non-blocking comment first, then as a gate after 2 weeks of familiarization. This gave the team time to fix existing violations.
- **Created a `detektBaseline`** for existing violations so only new code was flagged.
- **Ran a "Kotlin cleanup sprint"** — for 1 sprint, 20% of each engineer's time was dedicated to fixing Detekt violations. I tracked progress on a dashboard.
- **Banned `!!`** — I added a custom Detekt rule that flagged `!!` usage with a message: "Use `?: return` or `requireNotNull()` instead." This was the single biggest quality improvement.

### Result
Within 2 months, `!!` usage dropped by 90% (from 300+ to 30). NPE-related crashes dropped by 40%. Code review discussions about style dropped to near zero — Detekt handled style, reviewers focused on logic. The one-page patterns guide was adopted by 3 other teams. The key learning was that a focused rule set + a one-page guide > a comprehensive style guide. The `!!` ban was the highest-impact single change.

---

## Q6: Tell me about a time you had to design a Kotlin DSL.

### Situation
We were building a form validation library. The existing API was a builder pattern: `Validator().addRule("email", EmailRule()).addRule("password", MinLengthRule(8)).build()`. It was verbose and hard to read. The team wanted a more expressive, Kotlin-idiomatic API.

### Task
I needed to design a DSL that made form validation declarative and readable.

### Action
- **Studied existing Kotlin DSLs** — I read the source code of `KotlinTest`, `Ktor`'s routing DSL, and `Gradle Kotlin DSL` to understand patterns: receiver types, `@DslMarker`, and lambda-with-receiver.
- **Designed the target API** — I wrote the desired usage first, then worked backwards:
  ```kotlin
  val validator = validator {
      field("email") {
          rule { it.isNotBlank() } message "Email is required"
          rule { it.contains("@") } message "Invalid email"
      }
      field("password") {
          rule { it.length >= 8 } message "Password too short"
      }
  }
  ```
- **Used `@DslMarker`** to prevent scope leakage — without it, `field` could be called inside `field`, which was confusing.
- **Created `ValidatorBuilder` and `FieldBuilder` classes** with `operator fun invoke` or `infix` functions for the `rule`/`message` syntax.
- **Made everything immutable** — the DSL built a data structure, and `validate()` returned a `Result`. No side effects during DSL construction.
- **Added type safety** — I used generics to ensure `field<String>("email")` only accepted string rules, preventing type mismatches at compile time.
- **Wrote comprehensive tests** — I tested the DSL itself (valid syntax, error messages) and the validation logic.
- **Documented with examples** — I created a README with 10 common validation scenarios.

### Result
The DSL was adopted immediately — engineers preferred it over the old builder. Form validation code was 50% shorter and significantly more readable. The `@DslMarker` prevented the most common DSL mistake (nested scope leakage). The DSL was reused in 3 other projects. The key learning was to design the API usage first, then implement — starting from the desired developer experience produces better APIs than starting from the implementation.

---

## Q7: Tell me about a time you had to handle a performance issue in Kotlin code.

### Situation
Our app's JSON parsing was slow — parsing a 500-item list took 800ms on a mid-range device. We were using `Gson`, which uses reflection. The parsing happened on the main thread during screen transitions, causing visible jank.

### Task
I needed to reduce parsing time from 800ms to under 100ms without a massive rewrite.

### Action
- **Profiled the parsing** — I used `Trace` to measure each step. 90% of the time was in `Gson.fromJson()`, specifically in reflection.
- **Evaluated alternatives** — I benchmarked `Gson`, `Moshi` (with codegen), `kotlinx.serialization`, and `Moshi` (with reflection). Results for 500 items: Gson (800ms), Moshi+reflection (600ms), kotlinx.serialization (120ms), Moshi+codegen (80ms).
- **Chose Moshi with codegen** — it was the fastest, and the migration from Gson was straightforward because Moshi has a Gson-compatible API.
- **Migrated incrementally** — I started with the most-parsed models (the 500-item list). I added `@JsonClass(generateAdapter = true)` to data classes, which generated type-safe adapters at compile time.
- **Moved parsing to `Dispatchers.Default`** — even with faster parsing, I moved it off the main thread to avoid jank during transitions.
- **Added a benchmark** to CI to catch parsing regressions.
- **Migrated the remaining models** over 2 sprints using the boy scout rule.

### Result
Parsing time dropped from 800ms to 80ms — a 90% improvement. The screen transition jank was eliminated. The CI benchmark caught a regression when a teammate added a complex nested model without `@JsonClass`. The migration from Gson to Moshi was completed in 2 sprints with zero breaking changes. The key learning was that reflection-based JSON parsing is a common hidden performance bottleneck, and codegen-based parsers (Moshi/kotlinx.serialization) are dramatically faster.

---

## Q8: Tell me about a time you had to handle a disagreement about dependency injection.

### Situation
Our team was split on DI: one group wanted Hilt (Dagger), another wanted Koin, and a third wanted manual DI. The codebase had no DI — dependencies were created with `new` everywhere, making testing impossible. The decision would affect the entire codebase.

### Task
I needed to facilitate a decision that was technically sound and had team buy-in.

### Action
- **Defined evaluation criteria**: compile-time safety, learning curve, build time impact, testability, and community support.
- **Created a comparison matrix** scoring Hilt, Koin, and manual DI against each criterion.
- **Prototyped all three** — I implemented a single feature (user profile) with each approach. This revealed: Hilt had compile-time safety but slow build times; Koin was simple but runtime-only; manual DI was verbose but had zero overhead.
- **Presented the data** — I showed the prototypes and matrix to the team. I shared my recommendation (Hilt) but explicitly asked for concerns.
- **Addressed the build time concern** — the Koin advocates were concerned about Hilt's build time. I measured it: Hilt added 8 seconds to a 60-second build. I argued the compile-time safety was worth 8 seconds.
- **Let the team decide** — after the presentation, the team voted. Hilt won 4-2. The Koin advocates agreed to support the decision.

### Result
We adopted Hilt and migrated the app in 6 weeks. The compile-time safety caught 5 missing bindings that would have been runtime crashes with Koin. The 8-second build time increase was accepted by the team. The prototype-driven decision process was adopted for future architecture decisions. The key learning was that prototypes + criteria resolve DI debates faster than philosophical arguments about "compile-time vs runtime."

---

## Q9: Tell me about a time you had to handle a production crash related to Kotlin.

### Situation
After enabling R8 minification in release builds, we started getting `ClassNotFoundException` crashes for `kotlinx.coroutines.flow.StateFlow` — but only on Android 7 and below. The crash affected 3% of users on older devices.

### Task
 I needed to fix the crash and ship a hotfix within 24 hours.

### Action
- **Analyzed the crash** — the stack trace showed R8 was stripping `StateFlow` implementations because it couldn't find usage references (they were accessed via reflection in a third-party library).
- **Identified the root cause** — R8's full-mode minification was removing `kotlinx.coroutines` classes that were only referenced via reflection. The third-party analytics SDK used reflection to access `StateFlow`.
- **Added ProGuard keep rules** — I added `-keep class kotlinx.coroutines.** { *; }` and `-keep class kotlinx.coroutines.flow.** { *; }` to the ProGuard rules.
- **Audited all third-party SDKs** — I checked each SDK's documentation for ProGuard rules and found 2 more that were missing.
- **Tested on Android 7 emulator** — the crash was no longer reproducible.
- **Added a smoke test** that ran the app on Android 7, 8, 9, 10, 11, 12, and 13 emulators in CI to catch platform-specific issues.
- **Shipped a hotfix** within 12 hours.

### Result
Crashes dropped to 0 within 24 hours. The multi-API smoke test caught 2 more platform-specific issues in subsequent releases. I created a "ProGuard Rules Checklist" for new SDKs — every new SDK had to have its ProGuard rules verified before integration. The key learning was that R8 full mode is aggressive, and all reflection-based usage needs explicit keep rules, especially for Kotlin coroutines and serialization libraries.

---

## Q10: Tell me about a time you failed and what you learned.

### Situation
I designed a generic repository pattern using Kotlin generics that was supposed to handle all data types — `class Repository<T : Any>(private val api: Api<T>, private val dao: Dao<T>)`. I was proud of the abstraction — one class, all data types. I spent 2 weeks building it with complex generic constraints, variance modifiers, and type erasure workarounds.

### Task
I needed to create a reusable repository pattern that would reduce boilerplate across 15+ data types.

### Action
- **Built the generic repository** with `reified` generics, `KClass` type tokens, and reflection-based DAO calls. It worked but was complex.
- **Presented to the team** — they were confused. The generic constraints (`where T : Any, T : Serializable, T : HasId`) were hard to understand. Adding a new data type required satisfying 3 generic constraints.
- **A teammate pointed out** that the 15 data types had different caching strategies, error handling, and pagination. The generic repository forced them into a one-size-fits-all pattern that didn't fit.
- **I had to scrap the generic repository** and instead created a `BaseRepository` with shared logic (error handling, caching) that each repository extended. It was less "clever" but much more practical.
- **Owned the mistake** — I explained to the team that I over-engineered the solution. The generic approach was technically interesting but practically wrong.

### Result
The `BaseRepository` approach was implemented in 3 days and worked for all 15 data types. It was simpler, more flexible, and easier to understand. I wasted 2 weeks on the generic approach. The lesson was: **generics are powerful but dangerous for abstraction.** When different types need different behavior, inheritance + composition is simpler than generics. I created a personal rule: "If a generic constraint has more than 2 bounds, reconsider the design." I shared this learning with the team and it prevented 2 similar over-engineering attempts.

---

## 🔗 Related Topics
- [Android Behavioral Questions](AndroidBehavioralQuestions.md)
- [Java Behavioral Questions](JavaBehavioralQuestions.md)
