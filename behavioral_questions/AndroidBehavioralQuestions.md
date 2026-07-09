# Android Behavioral Questions — Senior Mobile Engineer (STAR Format)

> STAR = **S**ituation, **T**ask, **A**ction, **R**esult
> Each answer is framed from the perspective of a Senior Android Engineer.

---

## Q1: Tell me about a time you had to reduce app startup time.

### Situation
Our Android app had a cold start time of 4.2 seconds on mid-range devices, measured via Android Vitals. The product team flagged it because our bounce rate on first launch was 18%. The app used a monolithic `Application.onCreate()` that initialized 15+ SDKs synchronously.

### Task
I needed to bring cold start time under 2 seconds without breaking any SDK initialization or causing race conditions.

### Action
- **Profiled startup** using the `macrobenchmark` library and `Debug.startMethodTracing`. I identified that 3.1s was spent in `Application.onCreate()` — Analytics, Crashlytics, Firebase, and a legacy push SDK were all initializing synchronously.
- **Categorized SDKs** by priority: (1) Must be ready before UI (Crashlytics), (2) Needed within first screen (Analytics), (3) Can be deferred (Push notifications, feature flags).
- **Implemented the App Startup library** (`androidx.startup`) for critical SDKs and used `Initializer` for ordered dependencies.
- **Moved non-critical SDKs to a background coroutine** launched via `Dispatchers.IO` after `onCreate()` returned, with a 500ms delay.
- **Used `WorkManager` for deferred initialization** of push notifications and feature flags — they initialized when the app was idle.
- **Removed an unused legacy ad SDK** that was adding 400ms to startup.
- **Added a startup benchmark** to CI to catch regressions.

### Result
Cold start time dropped from 4.2s to 1.6s on Pixel 4a — a 62% improvement. First-launch bounce rate dropped from 18% to 9%. The startup benchmark in CI caught two regressions in the following months before they reached production. The SDK initialization strategy was documented and adopted by two other Android teams.

---

## Q2: Describe a time you had to handle a memory leak in production.

### Situation
We started receiving ANR reports after a major release. LeakCanary showed a memory leak in our `MapActivity` — a `GoogleMap` instance was holding a reference to the Activity through a `OnCameraIdleListener`. The leak caused OOM crashes on 3% of sessions on low-memory devices.

### Task
I needed to fix the leak, ship a hotfix, and prevent similar leaks in the future.

### Action
- **Reproduced the leak** by navigating in and out of `MapActivity` 10 times and confirming the retained objects in LeakCanary.
- **Identified the root cause**: The `GoogleMap`'s `OnCameraIdleListener` was set in `onMapReady` but never removed. When the Activity was destroyed, the Google Maps internal `MapView` held the listener, which held the Activity.
- **Fixed the leak**: Removed the listener in `onDestroy()` by calling `googleMap.setOnCameraIdleListener(null)` and `mapView.onDestroy()`.
- **Audited all listeners**: I searched the codebase for `setOn*Listener` calls and found 3 more potential leaks in other Activities.
- **Added LeakCanary to debug builds** (it was only in QA builds) and created a CI check that runs a UI test that navigates through all Activities and checks for leaks.
- **Shipped a hotfix** within 24 hours.

### Result
OOM crashes dropped to 0 within 48 hours of the hotfix. The CI leak detection test caught 2 more leaks in subsequent PRs before they reached QA. I wrote a "Listener Cleanup Checklist" that was added to our code review guidelines. The key learning was that all `setOn*Listener` calls need a corresponding `setOn*Listener(null)` in cleanup.

---

## Q3: Tell me about a time you had to migrate from MVP to MVVM.

### Situation
Our app used MVP (Model-View-Presenter) with 30+ screens. Presenters were tightly coupled to Views via interfaces, making them hard to test and prone to lifecycle bugs. Presenters survived configuration changes via `onRetainCustomNonConfigurationInstance()`, which was deprecated and fragile.

### Task
I needed to migrate the app to MVVM with ViewModel and LiveData without stopping feature development or rewriting all screens at once.

### Action
- **Created a migration plan** that prioritized screens by bug frequency and complexity. Screens with the most lifecycle bugs were migrated first.
- **Defined the target architecture**: `ViewModel` + `LiveData`/`StateFlow` + `ViewBinding` (later Compose). Each screen had a single `UiState` data class.
- **Migrated one screen as a pilot** — the profile screen, which was medium complexity. I documented the migration steps and common pitfalls.
- **Used the "strangler fig" pattern** — new features were built with MVVM, existing screens were migrated when touched. No "big bang" rewrite.
- **Created a `BaseViewModel`** with common error handling and loading state to reduce boilerplate.
- **Wrote a migration guide** with before/after code examples for each common pattern (data loading, form submission, navigation).
- **Paired with each engineer** on their first MVVM migration to ensure consistency.

### Result
Over 5 months, 80% of screens were migrated to MVVM. Lifecycle-related bugs dropped by 70% (measured by crash reports). Unit test coverage for ViewModels reached 85%, compared to 30% for Presenters (which required mocking View interfaces). The migration guide was adopted by 3 other teams. No feature development was blocked during the migration.

---

## Q4: Describe a time you had to deal with a difficult stakeholder.

### Situation
The design team delivered a spec for a custom animation-heavy onboarding flow that required Lottie animations, parallax scrolling, and dynamic text sizing. The timeline was 2 weeks. I estimated 4 weeks because the animations required custom `MotionLayout` configurations and the dynamic text sizing needed to handle 15 languages. The design lead pushed back, saying "it's just animations."

### Task
I needed to manage the stakeholder's expectations without damaging the relationship or compromising on quality.

### Action
- **Scheduled a 30-minute demo** with the design lead. I showed a prototype of the simplest animation (1 day of work) and the most complex one (3 days of work) to make the effort tangible.
- **Broke down the estimate** — I showed a spreadsheet with each animation, its complexity, and estimated time. This made the 4-week estimate concrete rather than abstract.
- **Proposed a phased approach**: Phase 1 (2 weeks) — core flow with basic animations. Phase 2 (2 weeks) — polish, parallax, and edge cases. This allowed the feature to launch on time with a follow-up release for polish.
- **Acknowledged their perspective** — I said "I understand this looks simple from the design side. The complexity is in handling 15 languages, 5 screen sizes, and edge cases like RTL."
- **Invited them to pair** — I asked the design lead to sit with me for 30 minutes while I implemented one animation, so they could see the complexity firsthand.

### Result
The design lead agreed to the phased approach after seeing the breakdown. Phase 1 launched on time. Phase 2 shipped 2 weeks later. The design lead later told me the demo was the most helpful part — seeing the actual implementation changed their understanding. They started consulting engineering estimates earlier in the design process for future features. The key learning was that showing is more effective than telling.

---

## Q5: Tell me about a time you improved app quality or reduced crashes.

### Situation
Our app had a crash-free session rate of 97.2%, below Google Play's recommended 99.9%. The top 3 crashes were: (1) `NetworkOnMainThreadException` in a legacy sync module, (2) `IndexOutOfBoundsException` in a custom `RecyclerView.Adapter`, and (3) `NullPointerException` in a Fragment transaction after process death.

### Task
I needed to bring the crash-free rate above 99.5% within one release cycle (2 weeks).

### Action
- **Triage by impact**: I ranked crashes by session impact (users affected × frequency) using Firebase Crashlytics. The top 3 accounted for 78% of all crashes.
- **Fix 1 — NetworkOnMainThread**: Moved the sync logic to `Dispatchers.IO` using a `CoroutineScope`. Added `StrictMode` thread policy in debug builds to catch future violations.
- **Fix 2 — IndexOutOfBoundsException**: The adapter was modifying the list without calling `notifyItem*` methods. I wrapped the list in `ListUpdateCallback` and used `DiffUtil` for diffing. Added `@VisibleForTesting` annotations and a unit test for edge cases (empty list, single item, concurrent modification).
- **Fix 3 — NPE after process death**: The Fragment was accessing `arguments` after process death without null checks. I added `SavedStateHandle` to the ViewModel and null-checked all `arguments` access. Added a test that simulated process death.
- **Added preventive measures**: Enabled R8 full mode, added `@NonNull` annotations to all public APIs, and created a lint baseline to catch new issues.
- **Set up crash rate alerts** in Crashlytics to notify the team if any new crash exceeded 0.1% of sessions.

### Result
Crash-free session rate went from 97.2% to 99.6% in one release. The top 3 crashes were eliminated. The crash rate alert caught 2 new crashes in the following release within hours of rollout. `StrictMode` in debug builds caught 4 additional `NetworkOnMainThread` violations before they reached production. The DiffUtil pattern was adopted as the standard for all `RecyclerView` adapters.

---

## Q6: Tell me about a time you had to make a critical architectural decision.

### Situation
We were building a new real-time messaging feature. The team was split: one group wanted to use WebSocket with a custom protocol, another wanted to use Server-Sent Events (SSE), and a third wanted Firebase Cloud Messaging (FCM) for push + REST polling for message history. The decision would affect the app for years.

### Task
I needed to facilitate a decision that was technically sound, performant, and maintainable, while ensuring team buy-in.

### Action
- **Defined evaluation criteria**: latency, battery impact, complexity, offline support, scalability, and team familiarity.
- **Built a comparison matrix** with each option scored against the criteria. I researched battery impact using `Battery Historian` for a prototype of each approach.
- **Prototyped all three** in 2 days — a minimal chat screen with each transport. This revealed that WebSocket had the lowest latency but highest battery drain, SSE was simple but didn't support bidirectional communication, and FCM + REST was the simplest but had 2-5 second latency.
- **Proposed a hybrid**: WebSocket for foreground real-time messages, FCM for background notifications, REST for message history and pagination. This gave us the best of all three.
- **Documented the decision** in an ADR (Architecture Decision Record) with the evaluation matrix, prototype results, and rationale.
- **Presented to the team** — I showed the prototypes, the matrix, and the recommendation. I asked for concerns rather than votes, which surfaced a valid concern about WebSocket reconnection logic that I addressed in the plan.

### Result
The team unanimously agreed on the hybrid approach. The messaging feature was delivered in 6 weeks with sub-200ms foreground latency. The ADR was referenced 3 times in the following year when new engineers questioned the architecture. The WebSocket reconnection logic, which the team flagged during the review, was the most complex part but was delivered on time because we planned for it. The key learning was that prototypes + criteria > opinions.

---

## Q7: Tell me about a time you mentored a team member who was underperforming.

### Situation
A mid-level engineer on our team was consistently missing sprint commitments by 30-40%. Their code quality was good, but they were spending too much time on over-engineering — building abstractions for features that didn't need them, and adding "future-proofing" code that wasn't requested. This was affecting the team's velocity.

### Task
I needed to help them understand the difference between good engineering and over-engineering, without crushing their enthusiasm or making them feel micromanaged.

### Action
- **Had a 1-on-1 conversation** — I started by asking them to walk me through their approach to a recent task. I listened first and understood their reasoning: they wanted to "do it right" and avoid future rework.
- **Acknowledged their intent** — I said "I appreciate that you care about code quality. Let's talk about how to balance that with delivery speed."
- **Introduced the YAGNI principle** (You Aren't Gonna Need It) with concrete examples from their PRs. I showed them 3 cases where abstractions they built were never used by other features.
- **Set a "scope budget"** — for each task, we agreed on a time box. If the implementation exceeded the time box, they'd come to me before adding more. This gave them a framework for deciding "is this worth it?"
- **Paired on scoping** — for 2 sprints, I helped them break down tasks into smaller pieces before implementation. This made the scope concrete and reduced the temptation to add "just one more abstraction."
- **Gave positive reinforcement** — when they delivered a feature on time with clean, simple code, I specifically praised the simplicity in the PR review.

### Result
Within 3 sprints, their delivery was back on track — they met 90%+ of commitments. Their code quality actually improved because they were spending time on the right things instead of speculative abstractions. They later told me the "scope budget" was the most helpful tool — it gave them permission to not over-engineer. The key learning for me was that over-engineering often comes from a good place (caring about quality) and the fix is giving people a framework for scope, not telling them to "just write less code."

---

## Q8: Describe a time you had to handle a production incident under pressure.

### Situation
On a Friday at 5 PM, our monitoring dashboard showed a spike in API errors — 40% of API calls were returning 500. The app was showing error screens for all users. We had just released a minor update that changed the API base URL configuration.

### Task
I needed to restore service for all users as quickly as possible, then investigate the root cause.

### Action
- **Activated the incident response** — I created an incident channel, paged the backend on-call, and notified the engineering manager. I took the role of incident commander.
- **Assessed severity** — 40% of users affected, no data loss, no security issue. Severity: P1.
- **First decision — rollback**: I checked if the release was the cause. The release notes showed a change to the API base URL from `api.example.com` to `api-v2.example.com`. The new endpoint was returning 500. I made the call to roll back the release immediately rather than debug.
- **Rolled back** via Play Store internal test track (staged rollout at 10%). The rollback took 20 minutes. Error rate dropped to 0.
- **Investigated root cause** — the backend team confirmed that `api-v2.example.com` was a new endpoint that wasn't fully deployed. The mobile release was shipped before the backend was ready. The coordination between mobile and backend had broken down.
- **Wrote a post-mortem** — I documented the timeline, root cause (lack of deployment coordination), and action items: (1) Add a feature flag for API base URL changes, (2) Add a backend health check before switching endpoints, (3) Require backend sign-off before mobile release.
- **Followed up on action items** — I tracked all 3 items to completion in the next sprint.

### Result
Service was restored in 25 minutes. No user data was lost. The feature flag for API base URL was implemented and has been used for every endpoint change since. The post-mortem was shared with the entire engineering org. The backend sign-off process was adopted across all mobile-backend coordinated releases. No similar incidents have occurred since. The key learning was that rollback should always be the first option for release-related incidents — debug after restoring service.

---

## Q9: Tell me about a time you had to balance technical debt with feature delivery.

### Situation
Our `BaseActivity` class had grown to 800+ lines with 15+ responsibilities: analytics, permissions, deep links, theme management, crash reporting, and more. Every new Activity extended it, and changes to `BaseActivity` risked breaking all screens. The team avoided touching it, and bugs were accumulating.

### Task
I needed to refactor `BaseActivity` without stopping feature development or risking regressions across all 30+ screens.

### Action
- **Audited `BaseActivity`** and identified 6 distinct responsibilities that could be extracted.
- **Extracted responsibilities into delegates** — `AnalyticsDelegate`, `PermissionDelegate`, `DeepLinkDelegate`, etc. Each delegate was a standalone class that Activities could opt into.
- **Used the "strangler fig" pattern** — new Activities used delegates directly instead of extending `BaseActivity`. Existing Activities were migrated when touched for feature work.
- **Created a `BaseActivityV2`** that used the delegates internally, so existing Activities could migrate by changing their parent class with minimal code changes.
- **Added a CI check** that flagged new code added to `BaseActivity` — any new logic had to go into a delegate.
- **Migrated 5 high-churn Activities** as part of regular feature work to validate the pattern.

### Result
Over 4 months, `BaseActivity` was reduced from 800 to 150 lines. 20 out of 30 Activities were migrated to the delegate pattern. No regressions were introduced — the delegates had unit tests, while `BaseActivity` had none. New Activities were simpler to create — engineers picked the delegates they needed instead of inheriting everything. The delegate pattern was adopted by 2 other teams for their base classes.

---

## Q10: Tell me about a time you failed and what you learned.

### Situation
I was tasked with implementing a custom offline sync mechanism for our app. I designed a complex system with conflict resolution, delta sync, and a local SQLite database with 12 tables. I spent 3 weeks building it without showing progress to the product team.

### Task
I needed to deliver an offline-first experience so users could use the app without internet and sync when reconnected.

### Action
- **Built the full system** — conflict resolution, delta sync, 12-table schema, a sync manager, and a queue for pending operations. The code was well-structured and tested.
- **Demoed to the product team** in week 3. They were confused — they expected a simple "cache last seen data" feature, not a full offline-first architecture. The 12-table schema was overkill for their use case (read-only data with no user-generated content).
- **Realized the miscommunication** — I had assumed "offline support" meant "full offline-first with conflict resolution." The product team meant "show last cached data when offline."
- **Scrapped 80% of the work** — I kept the local cache and removed the conflict resolution, delta sync, and operation queue. The simplified version took 2 days.
- **Owned the mistake** — I explained to my manager what happened, why, and what I'd do differently. I didn't blame the product team for unclear requirements.

### Result
The simplified offline cache shipped in 2 days and met all product requirements. I wasted 3 weeks of work. The lesson was painful but transformative: **always validate scope with a prototype or demo before building the full system.** I started doing "scope confirmation meetings" where I showed a 1-page design doc before implementation. I also learned to ask "what does offline mean to you?" instead of assuming. My manager appreciated that I owned the mistake and proposed a process change. The scope confirmation practice was adopted by the team and prevented at least 2 similar miscommunications in the following months.

---

## 🔗 Related Topics
- [Compose Behavioral Questions](ComposeBehavioralQuestions.md)
- [Kotlin Behavioral Questions](KotlinBehavioralQuestions.md)
