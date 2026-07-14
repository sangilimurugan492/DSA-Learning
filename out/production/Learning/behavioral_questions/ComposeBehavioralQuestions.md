# Behavioral Questions — Senior Mobile Engineer (STAR Format)

> STAR = **S**ituation, **T**ask, **A**ction, **R**esult
> Each answer is framed from the perspective of a Senior Mobile Engineer working with Jetpack Compose.

---

## Q1: Tell me about a time you had to migrate a large XML-based app to Jetpack Compose.

### Situation
Our app had 40+ screens built entirely with XML layouts, ViewBinding, and Fragment-based navigation. The UI code was becoming hard to maintain, and the team wanted to adopt Compose for new features. However, a full rewrite was too risky given our 2-week release cycle and 5-person team.

### Task
I was responsible for defining and executing a gradual migration strategy that would allow us to adopt Compose without blocking feature development or destabilizing the app.

### Action
- **Audited the codebase** and categorized screens by complexity and churn rate. I prioritized screens that changed frequently and had simpler state.
- **Set up Compose in the existing project** — added the Compose BOM, compiler, and theme bridge (`MdcTheme`) so Compose screens could reuse our Material 2 color palette.
- **Started with new screens** — all new features were written in Compose using `ComposeView` inside Fragments, so we didn't need to change our Navigation Component setup.
- **Created an `AbstractComposeView` wrapper** for custom views that were used in both XML and Compose, so we could share components during the transition.
- **Wrote an internal migration guide** with patterns for common scenarios: embedding Compose in Fragment, embedding View in Compose (`AndroidView`), and state hoisting patterns.
- **Paired with each engineer** for their first Compose screen to ensure quality and consistency.

### Result
Over 4 months, we migrated 15 screens to Compose with zero regressions. New feature development velocity increased by ~30% because Compose eliminated the XML↔code context switch. The migration guide was adopted by two other Android teams in the company. No crash rate increase was observed in production.

---

## Q2: Describe a time you solved a significant performance problem in Compose.

### Situation
After migrating our product listing screen to Compose with a `LazyColumn` of 200+ items, we noticed frame drops on mid-range devices (Pixel 4a). The Layout Inspector showed recomposition counts of 15+ per item during scroll, and the UI thread was consistently over 20ms per frame.

### Task
As the senior engineer, I needed to identify the root cause and bring the frame time back under 16ms (60 FPS) without sacrificing code readability.

### Action
- **Profiled with Layout Inspector and Compose Compiler reports.** I discovered that our `ProductItem` composable received a `List<Review>` parameter (unstable) and created a new `SimpleDateFormat` instance on every recomposition.
- **Ran the Compose compiler metrics** and confirmed that 12 out of 18 composables were not skippable due to unstable parameters.
- **Wrapped list parameters in `@Immutable` data classes** and switched to `ImmutableList` from `kotlinx.collections.immutable` for collection parameters.
- **Added `key` and `contentType` parameters** to all `items()` calls in the `LazyColumn`.
- **Moved date formatting to the ViewModel** — pre-formatted strings were included in the UI model, eliminating per-frame computation.
- **Deferred scroll-state reads** — moved the "scroll to top" FAB visibility check to a `derivedStateOf` and deferred the offset read to the layout phase using `Modifier.offset { }`.
- **Wrote a `recomposeHighlighter` modifier** for debug builds to catch future regressions during development.

### Result
Recomposition counts dropped from 15+ to 0–1 per item during scroll. Frame times went from 22ms to 8ms on Pixel 4a. The compiler reports showed all item composables as `restartable skippable`. I shared the `recomposeHighlighter` modifier with the team and added a check in our CI pipeline to flag unstable parameters in new composables.

---

## Q3: Tell me about a time you had a disagreement with a teammate about architecture.

### Situation
We were starting a new feature — a multi-step checkout flow. One teammate advocated for a traditional MVVM approach with multiple `LiveData` streams and separate state objects for each step. I advocated for a single MVI-style `UiState` with a sealed `Intent` class, because the steps were tightly coupled and shared state.

### Task
I needed to reach a decision that was technically sound without creating friction, since both approaches were valid and the teammate was experienced.

### Action
- **Scheduled a 30-minute design discussion** rather than debating in PR comments. I prepared a one-page comparison of both approaches for this specific use case.
- **Acknowledged the teammate's concerns** — their approach was simpler for independent screens, and I agreed that MVI can be overkill for simple flows.
- **Built a quick prototype of both approaches** for the first two steps of checkout to demonstrate the trade-offs concretely. The MVI prototype showed that shared state (cart, shipping address) was easier to manage with a single state object, while the MVVM prototype required cross-ViewModel communication that felt bolted-on.
- **Proposed a hybrid** — single `CheckoutUiState` + `Intent` sealed class for the shared checkout flow, but each step's UI used local `remember` state for form fields (not in the global state).
- **Let the team decide** — I presented the prototype and trade-offs, and the team unanimously chose the hybrid approach.

### Result
The checkout feature was delivered on time with zero state-related bugs. The hybrid pattern became our team's standard for multi-step flows. The teammate later used the same pattern in another feature and thanked me for the prototype-driven discussion. The key learning was that prototypes resolve disagreements faster than arguments.

---

## Q4: Describe a time you mentored a junior engineer on Compose.

### Situation
A junior engineer on our team was struggling with Compose. They were writing composables with state at the top level, passing ViewModels deep into child composables, and creating infinite recomposition loops. Their PRs were taking 3–4 rounds of review.

### Task
I needed to help them understand Compose fundamentals and bring their PR quality to a level where reviews could be completed in one round, without making them feel micromanaged.

### Action
- **Paired for 1 hour twice a week** for 3 weeks. Instead of reviewing their code asynchronously, I sat with them and we wrote composables together.
- **Created a one-page "Compose Cheat Sheet"** with the 5 most common mistakes: state writes in composition, unstable parameters, missing keys, passing ViewModels to children, and not using `collectAsStateWithLifecycle`.
- **Introduced the state hoisting pattern** with a concrete exercise — I asked them to convert a stateful `Counter` composable into a stateless `CounterCore` + stateful wrapper. This made the concept click.
- **Set up Compose compiler reports** on their machine so they could see stability issues themselves before asking for review.
- **Gradually reduced pairing frequency** — from twice a week to once a week to on-demand, as their confidence grew.
- **Delegated a small feature** to them that required state hoisting, `LaunchedEffect`, and `LazyColumn` with keys, so they could apply the patterns independently.

### Result
After 4 weeks, their PRs went from 3–4 review rounds to 1–2. They independently built a search screen with debounce, `snapshotFlow`, and `derivedStateOf` — and it passed review with only minor comments. They later presented a 15-minute Compose fundamentals talk to the team. The cheat sheet I created was adopted by the entire team and is now part of our onboarding documentation.

---

## Q5: Tell me about a time you had to make a trade-off between speed and quality.

### Situation
We were 2 weeks from a major release that included a new dashboard screen built in Compose. The product team added a requirement for animated transitions between dashboard tabs at the last minute. The animations were complex — shared element transitions between tabs with dynamic content.

### Task
I needed to deliver the animated transitions before the release deadline without introducing performance issues or technical debt that would haunt us later.

### Action
- **Assessed the options**: (1) Use Compose's `SharedTransitionLayout` (experimental at the time), (2) use `AnimatedContent` with custom transitions, or (3) ship without animations and add them in a follow-up.
- **Prototyped option 2** in half a day — `AnimatedContent` with `slideInHorizontally`/`slideOutHorizontally` + `fadeIn`/`fadeOut`. It looked 80% as good as the shared element approach but was stable and performant.
- **Made the call**: ship option 2 for the release, and file a tech-debt ticket for option 1 when the API stabilizes.
- **Added a `// TODO: TECH-DEBT` comment** with the ticket number and a brief explanation of what the ideal implementation would look like.
- **Communicated the trade-off** to the product team — they agreed that 80% quality on time was better than 100% quality late.
- **Wrote a follow-up plan** in the tech-debt ticket with the exact API calls needed for the shared element approach.

### Result
The release shipped on time with smooth tab transitions. No performance issues were reported. Two months later, when `SharedTransitionLayout` became stable, we implemented the ideal solution in 2 days using the plan I had written. The product team appreciated the transparency and the quick delivery. The tech-debt ticket was resolved within the same quarter, so the debt didn't accumulate.

---

## Q6: Describe a time you had to debug a critical production issue related to Compose.

### Situation
In production, we received crash reports from a specific screen — `IllegalStateException: CompositionLocal LocalLifecycleOwner not present`. The crash affected ~2% of users on older devices (Android 8–9) and was not reproducible on any of our test devices.

### Task
I needed to identify the root cause, ship a fix, and prevent similar issues, all within 24 hours since the crash was affecting active users.

### Action
- **Analyzed the crash trace** in Firebase Crashlytics. The stack trace pointed to a `ComposeView` inside a `RecyclerView` ViewHolder — we had embedded a Compose `ComposeView` in a legacy XML-based list for a hybrid screen during migration.
- **Identified the root cause**: On older devices, the `RecyclerView` was recycling ViewHolders before the `ViewTreeLifecycleOwner` was set on the `ComposeView`. The `ComposeView` tried to access `LocalLifecycleOwner` during composition, but it wasn't available yet.
- **Wrote a fix**: Added a `DisposableEffect` that checks for `ViewTreeLifecycleOwner` before composing content, and set `setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)` on the `ComposeView`.
- **Added a guard**: Wrapped the `ComposeView.setContent` call in a check for `viewTreeLifecycleOwner != null`.
- **Tested on emulators** with Android 8 and 9 API levels — crash was no longer reproducible.
- **Added a regression test** using `createAndroidComposeRule` that simulates ViewHolder recycling.
- **Shipped a hotfix** within 8 hours of the first crash report.

### Result
Crash rate dropped to 0 within 24 hours of the hotfix rollout. I wrote a post-mortem documenting the issue and shared it with the company's Android guild. We added a lint check that flags `ComposeView` usage without `ViewCompositionStrategy`. The regression test was added to our CI pipeline. No similar crashes have been reported since.

---

## Q7: Tell me about a time you drove the adoption of a new technology or practice.

### Situation
Our team was using `collectAsState` for all Flow collection in Compose. I had read about `collectAsStateWithLifecycle` and understood that it saves battery by stopping collection when the app is backgrounded. However, the team was hesitant to change because "it works fine" and they didn't see the battery impact.

### Task
I wanted to migrate the team to `collectAsStateWithLifecycle` without forcing it top-down — I wanted the team to understand the "why" and adopt it willingly.

### Action
- **Measured the impact**: I wrote a benchmark using the Jetpack Macrobenchmark library that compared battery consumption of a screen with `collectAsState` vs `collectAsStateWithLifecycle` during 10 minutes of background/foreground cycling. The result: `collectAsStateWithLifecycle` reduced CPU usage by 18% in background.
- **Presented the data** in our weekly Android guild meeting — I showed the benchmark results and the code diff (literally a one-line change per screen).
- **Created a PR** that migrated one screen as a proof-of-concept, with before/after benchmark numbers in the PR description.
- **Made it trivial to adopt**: I added the `lifecycle-runtime-compose` dependency to our base module and created a Detekt rule that flagged `collectAsState` usage with a message pointing to the migration guide.
- **Didn't mandate it** — I let the team see the data and make their own decision. Within 2 weeks, all engineers started using `collectAsStateWithLifecycle` in new code.

### Result
Within a month, all 25+ screens were migrated. Our app's background battery consumption dropped by ~12% (measured by internal telemetry). The Detekt rule prevented regressions. The benchmark approach was adopted by the team — engineers now benchmark before/after for performance-related changes. The key learning was that data + making it easy > mandates.

---

## Q8: Describe a time you had to balance technical debt with feature delivery.

### Situation
Our Compose codebase had grown organically over a year. We had inconsistent state management patterns — some screens used `remember`, some used `ViewModel` with `mutableStateOf`, some used `StateFlow`, and one screen used `LiveData` with `observeAsState`. This made onboarding harder and code reviews inconsistent.

### Task
I needed to standardize state management across the app without stopping feature development for a "big refactor."

### Action
- **Documented the current state** — I created a spreadsheet listing every screen, its state mechanism, and its complexity. This made the problem visible.
- **Defined the standard** — I wrote a one-page "State Management Guide" (which became the basis for our `state_management/` docs): `remember` for local state, `ViewModel + StateFlow` for screen state, `SavedStateHandle` for process-death survival, `Channel` for one-time events.
- **Applied the "boy scout rule"** — every time an engineer touched a screen for a feature, they migrated that screen to the standard pattern. I tracked progress on the spreadsheet.
- **Created reusable templates** — a `ScreenTemplate.kt` with the standard ViewModel + UiState + Intent structure that engineers could copy for new screens.
- **Set a deadline** — all new screens must use the standard. Existing screens must be migrated when touched. No "big bang" refactor.
- **Reviewed migrations** — I personally reviewed the first migration PR from each engineer to ensure consistency.

### Result
Over 3 months, 80% of screens were migrated to the standard pattern as part of regular feature work. No dedicated refactor sprint was needed. Code review discussions about state management dropped to near zero because the standard was clear. New engineers onboarded faster — they only needed to learn one pattern. The State Management Guide is now part of our team wiki and has been referenced by 3 other teams in the company.

---

## Q9: Tell me about a time you failed and what you learned.

### Situation
Early in our Compose adoption, I designed a custom `FlowRow` layout using the `Layout` composable for a tag/chip input screen. I was excited about building custom layouts and didn't consider using the existing `FlowRow` from the Accompanist library (now in Compose Foundation).

### Task
I needed to deliver a tag input UI where chips wrap to the next line when they don't fit.

### Action
- **Built a custom `FlowRow`** from scratch using `Layout` — measuring children, tracking positions, wrapping lines. It took 2 days.
- **It worked in development** but had edge cases: RTL layout was broken, vertical alignment was off for chips of different heights, and it didn't work with `animateItem` for add/remove animations.
- **Spent 3 more days fixing edge cases** — RTL support, alignment, animation compatibility. The code grew to 150+ lines of layout logic that was hard to maintain.
- **A teammate pointed out** in code review that Accompanist's `FlowRow` (later moved to Compose Foundation) already handled all these cases and was battle-tested.
- **I replaced my custom implementation** with the library version in 30 minutes. It handled RTL, alignment, and animations out of the box.

### Result
I wasted 5 days building something that already existed. The lesson was humbling but valuable: **always check for existing solutions before building custom.** I created a checklist for myself before building custom layouts or components: (1) Is there a Compose Foundation/API? (2) Is there an Accompanist/library equivalent? (3) Is the custom implementation justified by a clear gap? I shared this checklist with the team and it saved multiple engineers from the same mistake. The experience also taught me to do a quick library search before any "I'll build it myself" decision.

---

## Q10: Tell me about a time you had to deliver bad news to a stakeholder.

### Situation
We were 1 week into a 3-week sprint to build a complex settings screen in Compose with search, filtering, and nested preference categories. During a technical spike, I discovered that our navigation library (Navigation Compose 2.7) had a bug with nested navigation graphs and `type-safe` routes — arguments were being lost on back navigation. This was a core requirement for the settings screen.

### Task
I needed to inform the product manager that the feature would be delayed, and propose a realistic path forward.

### Action
- **Gathered facts first** — I reproduced the bug, confirmed it was a known issue in the library's issue tracker, and checked if a fix was upcoming. The fix was scheduled for the next library release (4–6 weeks out).
- **Prepared alternatives** before the meeting: (1) Use string-based routes instead of type-safe routes (workaround, minor tech debt), (2) delay the feature by 1 week, (3) descope the nested navigation and use a flat list.
- **Scheduled a 15-minute sync** with the PM. I led with the impact: "The settings screen will need an extra 2 days, and here's why."
- **Explained the technical issue in non-technical terms** — "The navigation library has a known bug that loses data when going back from a sub-screen. We can work around it, but it adds 2 days."
- **Presented the alternatives** with trade-offs and my recommendation (option 1 — string-based routes as a workaround, with a tech-debt ticket to migrate when the fix ships).
- **Didn't over-promise** — I gave a realistic estimate of 2 extra days, not an optimistic one.

### Result
The PM appreciated the early heads-up and the prepared alternatives. They chose option 1 (string-based routes workaround). The feature shipped 2 days late, well within the sprint buffer. The tech-debt ticket was resolved 5 weeks later when the library fix shipped — the migration took half a day. The PM later told me they appreciated that I came with solutions, not just problems. I learned that delivering bad news early, with options and a recommendation, builds trust with stakeholders.

---

## 🔗 Related Topics
- [Architecture](../advanced/Architecture.md)
- [Performance](../advanced/Performance.md)
- [State Management Best Practices](../state_management/BestPractices.md)

