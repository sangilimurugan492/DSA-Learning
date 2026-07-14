# Flutter Behavioral Questions — Senior Mobile Engineer (STAR Format)

> STAR = **S**ituation, **T**ask, **A**ction, **R**esult
> Each answer is framed from the perspective of a Senior Flutter Engineer.

---

## Q1: Tell me about a time you had to optimize Flutter app performance.

### Situation
Our Flutter app had jank issues on the product listing screen — a `ListView.builder` with 500+ items, each containing an image, text, and a favorite button. The Flutter DevTools timeline showed 25ms+ per frame on mid-range Android devices, well above the 16ms budget for 60 FPS.

### Task
I needed to bring frame times under 16ms without sacrificing the visual quality of the list.

### Action
- **Profiled with Flutter DevTools** — the timeline showed that 60% of frame time was spent in image decoding and 30% in widget rebuilds. Only 10% was actual layout/paint.
- **Identified unnecessary rebuilds** — the `ListView.builder` was rebuilding all visible items when the favorite count changed, even though only one item's state changed. I wrapped each item in a `const` constructor where possible and extracted the favorite button into a separate `StatefulWidget` with its own `State`.
- **Used `const` constructors** aggressively — I marked all static widgets as `const` to skip rebuilds entirely.
- **Implemented `AutomaticKeepAliveClientMixin`** on list items to preserve state across scroll, preventing re-fetching of images.
- **Used `cached_network_image`** with proper `cacheWidth` and `cacheHeight` to decode images at display size instead of full resolution.
- **Added `RepaintBoundary`** around each list item to isolate repaints.
- **Used `ListView.builder` with `itemExtent`** to avoid layout calculations for each item.
- **Wrote a performance test** using `flutter test` with `benchmarkWidgets` to measure frame times in CI.

### Result
Frame times dropped from 25ms to 9ms on mid-range devices. The performance test in CI caught a regression when a teammate added a non-const widget to the list item. The `RepaintBoundary` + `const` pattern was adopted as the team's standard for all list-based screens. Image loading time dropped by 70% due to `cacheWidth`/`cacheHeight` optimization.

---

## Q2: Describe a time you had to migrate from Provider to Riverpod.

### Situation
Our app used `Provider` for state management across 25+ screens. As the app grew, we faced issues: `Provider` was hard to test (required `ProviderScope` setup), had implicit dependencies (context-based lookups), and couldn't handle async state cleanly. The team wanted to adopt `Riverpod` for its testability and compile-time safety.

### Task
I needed to migrate from `Provider` to `Riverpod` without breaking existing features or blocking new development.

### Action
- **Audited all Provider usages** — I categorized them into: simple state (ChangeNotifier), async state (FutureProvider equivalent), and global state. This gave us a migration scope.
- **Migrated one screen as a pilot** — the settings screen. I documented the before/after patterns: `ChangeNotifierProvider` → `NotifierProvider`, `Consumer` → `ConsumerWidget`, `context.read()` → `ref.read()`.
- **Ran both side-by-side** — I wrapped the app in `ProviderScope` and migrated screens one at a time. `Provider` and `Riverpod` coexisted during the transition.
- **Created a migration cheat sheet** with the 5 most common patterns mapped from Provider to Riverpod.
- **Used the "boy scout rule"** — screens were migrated when touched for feature work. No dedicated refactor sprint.
- **Added a lint rule** that flagged new `Provider` usage with a message pointing to the Riverpod migration guide.
- **Paired with each engineer** on their first Riverpod migration.

### Result
Over 3 months, all 25 screens were migrated to Riverpod. Test coverage for state management increased from 40% to 80% because Riverpod providers could be tested without a widget tree. No runtime errors were introduced during migration — Riverpod's compile-time safety caught missing dependencies that `Provider` would have only caught at runtime. The migration cheat sheet was adopted by 2 other Flutter teams in the company.

---

## Q3: Tell me about a time you had to handle a complex state management challenge.

### Situation
We were building a cart and checkout flow with real-time price calculation, promo code validation, inventory checks, and multi-address shipping. The state was complex: cart items, quantities, selected address, shipping method, promo code, and tax — all interdependent. A change in any field triggered recalculation of totals, tax, and shipping.

### Task
I needed to design a state management solution that handled all dependencies, was testable, and didn't cause unnecessary rebuilds.

### Action
- **Mapped the state dependencies** — I drew a dependency graph: cart items → subtotal → tax → total; address → shipping options → shipping cost → total; promo code → discount → total. This made the relationships explicit.
- **Chose Riverpod with `AsyncNotifier`** — each piece of state was a provider. Derived state (subtotal, total, tax) used `ref.watch` to react to changes.
- **Used `AsyncValue` for async operations** — promo code validation and inventory checks returned `AsyncValue`, so the UI could show loading/error/data states cleanly.
- **Batched state updates** — when a user changed quantity, I updated the cart and triggered recalculation in a single `Future` to avoid intermediate UI states.
- **Created a `CheckoutState` freezed class** for the overall state, with `copyWith` for immutable updates.
- **Wrote comprehensive tests** — I tested each provider in isolation and the integration of all providers together.
- **Used `select` to minimize rebuilds** — the UI only rebuilt the widgets that depended on the changed state, not the entire checkout screen.

### Result
The checkout flow was delivered in 4 weeks with zero state-related bugs in production. The provider-based architecture made it easy to add a new feature (gift wrapping) in 2 days — just a new provider that depended on the cart provider. Test coverage for the checkout flow was 90%. The dependency graph diagram was added to the team wiki and became our standard approach for complex state design.

---

## Q4: Tell me about a time you had to integrate a native module into Flutter.

### Situation
We needed to integrate a native Bluetooth SDK into our Flutter app. The SDK was only available as native Android (Java) and iOS (Swift) libraries — there was no Flutter package. The SDK handled BLE communication with a custom hardware device.

### Task
I needed to create a Flutter plugin that wrapped the native SDKs and exposed a unified Dart API, handling platform differences and async communication.

### Action
- **Researched existing approaches** — I evaluated `MethodChannel`, `EventChannel`, and `FFI`. `MethodChannel` was the best fit because the SDK used callbacks and events, not just synchronous calls.
- **Designed the Dart API** — I defined the interface in Dart first: `connect()`, `disconnect()`, `sendData()`, and a stream for incoming data. This ensured the API was idiomatic Dart, not a leak of the native API.
- **Implemented the Android side** — I created a `MethodChannel` handler in Kotlin that called the native SDK. For async events (incoming BLE data), I used an `EventChannel` that streamed data from the SDK's callback.
- **Implemented the iOS side** — I created the equivalent in Swift, handling the iOS-specific BLE delegate pattern.
- **Handled platform differences** — Android required location permissions for BLE scanning, iOS didn't. I handled this in the plugin with platform checks and exposed a `requestPermissions()` method.
- **Wrote integration tests** — I used `integration_test` to verify the plugin worked on both platforms with a mock BLE device.
- **Published as a private package** — I structured the plugin as a separate package so it could be reused by other apps.

### Result
The plugin was delivered in 3 weeks and worked on both Android and iOS. The Dart API was clean — consumers didn't know or care about the native implementation. The `EventChannel` for incoming data was the key decision — it provided a natural `Stream` API in Dart that integrated with Riverpod's `StreamProvider`. The plugin was reused by a second app in the company, saving 3 weeks of duplicate work. I wrote a "Flutter Plugin Development Guide" that was adopted by the team for future native integrations.

---

## Q5: Describe a time you had to deal with a difficult bug in production.

### Situation
Users on iOS reported that the app crashed when opening a specific screen — but only on devices with iOS 15.x. The crash log showed `EXC_BAD_ACCESS` in the Flutter engine's text rendering pipeline. The crash affected ~5% of iOS users and was not reproducible on simulators.

### Task
I needed to find and fix the crash without being able to reproduce it locally, and ship a fix within 48 hours.

### Action
- **Analyzed crash logs** in Firebase Crashlytics. The stack trace pointed to `SkParagraph::layout` — Flutter's text layout engine. The crash only happened on iOS 15.x with specific text content.
- **Correlated crash data** — I noticed that all crashes happened on screens with `RichText` widgets using custom `TextStyle` with `fontFeatures`. I cross-referenced with the iOS 15 release notes and found that Apple changed the CoreText API in iOS 15.
- **Reproduced the crash** — I borrowed an iPhone with iOS 15.4 and created a minimal reproduction: a `RichText` with `FontFeature.stylisticAlternates()` and a custom font. It crashed immediately.
- **Identified the root cause** — the custom font file was missing a glyph table that iOS 15's CoreText required. The font worked on Android (Skia handled it) but iOS 15 was stricter.
- **Fixed the crash** — I regenerated the font file with FontForge to include the missing glyph table. I also added a `try/catch` on the platform channel for font loading as a safety net.
- **Added a regression test** — I created a golden test that rendered the `RichText` with the custom font and font features.
- **Shipped a hotfix** within 24 hours.

### Result
Crashes dropped to 0 within 48 hours of the hotfix. The golden test caught a similar issue 2 months later when a designer added a new custom font. I wrote a post-mortem documenting the iOS 15 CoreText change and shared it with the Flutter community — it turned out other apps had the same issue. The key learning was that platform-specific rendering differences can cause crashes that are hard to reproduce, and custom fonts need to be validated on all target platforms.

---

## Q6: Tell me about a time you had to make a trade-off between code quality and deadline.

### Situation
We were 1 week from releasing a major update that included a new navigation pattern (GoRouter) and a redesigned home screen. The GoRouter migration was 80% done, but deep linking wasn't working correctly — the router was losing state on deep link navigation. Fixing it properly would take 3-4 days, but the release was in 5 days.

### Task
I needed to decide whether to fix the deep linking properly, ship a workaround, or revert to the old navigation.

### Action
- **Assessed the impact** — deep linking was used by 8% of users (marketing campaigns and push notifications). Not having it would hurt those campaigns but wouldn't break the app.
- **Prototyped a workaround** — I added a redirect in the old `Navigator` that handled deep links by navigating to the correct screen manually. It was 20 lines of code and took 2 hours. It wasn't elegant but it worked.
- **Made the call**: ship the workaround for the release, file a tech-debt ticket for the proper GoRouter deep linking fix.
- **Added a `// TODO: TECH-DEBT` comment** with the ticket number and the ideal implementation.
- **Communicated to the team** — I explained the trade-off in the standup: "We're shipping a workaround for deep linking. It works but it's not maintainable. I've filed a ticket to fix it properly next sprint."
- **Wrote the proper fix** in the next sprint — it took 3 days, as estimated. The workaround was removed.

### Result
The release shipped on time with working deep linking (via workaround). The proper fix was implemented in the next sprint and the tech-debt ticket was closed within 2 weeks. No users noticed the difference between the workaround and the proper implementation. The product team appreciated the transparency. The key learning was that "good enough on time" is often better than "perfect late" — as long as you have a plan to fix it.

---

## Q7: Tell me about a time you drove the adoption of a new practice or tool.

### Situation
Our Flutter team had no consistent approach to testing. Some engineers wrote widget tests, some wrote only unit tests, and some wrote no tests at all. The test coverage was 25% and regressions were reaching production regularly.

### Task
I wanted to establish a testing culture and bring coverage to 70%+ without mandating it top-down.

### Action
- **Started with myself** — I added tests to every PR I submitted and included test coverage in the PR description. I didn't ask others to do it; I just modeled the behavior.
- **Created a testing guide** — I wrote a one-page guide with examples of: unit test for a Riverpod provider, widget test for a screen, integration test for a flow. Each example was copy-pasteable.
- **Made it easy** — I created test templates in the IDE and added a `make test` script that ran tests with coverage reporting.
- **Showed the impact** — after 2 weeks, I shared in the team meeting: "PRs with tests have 80% fewer regression bugs than PRs without (measured by Crashlytics)." The data spoke for itself.
- **Added a CI check** — not a gate, just a comment on the PR showing coverage delta. This made coverage visible without forcing it.
- **Paired with engineers** who were less comfortable with testing — I sat with them for 30 minutes to write their first widget test.
- **Celebrated wins** — when a test caught a regression in CI, I highlighted it in the team channel.

### Result
Within 2 months, test coverage went from 25% to 68%. Every engineer was writing tests. The CI coverage comment became a point of pride — engineers started competing for higher coverage. Regression bugs reaching production dropped by 60%. The testing guide was adopted by 2 other Flutter teams. The key learning was that modeling + making it easy + showing data > mandates.

---

## Q8: Tell me about a time you had to handle app size optimization.

### Situation
Our Flutter app's APK size had grown to 28MB, and the iOS IPA was 35MB. The product team was concerned about install conversion rates, especially in emerging markets where data is expensive. Google Play's console showed we were in the top 30% for app size in our category.

### Task
I needed to reduce app size by at least 30% without removing features.

### Action
- **Analyzed size breakdown** — I used `flutter build apk --analyze-size` and the iOS app size report. The breakdown: Flutter engine (6MB), Dart code (4MB), native libraries (8MB), assets (10MB).
- **Reduced assets** — I audited all images and found 40% were unused (leftover from old features). I removed them and converted remaining PNGs to WebP, saving 4MB.
- **Used `--split-per-abi`** for Android — instead of a universal APK, I split by ABI (arm64-v8a, armeabi-v7a, x86_64). This reduced each APK by ~6MB. Google Play's AAB delivery handled this automatically.
- **Enabled R8/ProGuard** — I enabled `minifyEnabled` and `shrinkResources` in the Android build, saving 2MB.
- **Deferred font loading** — I had 3 custom fonts (2.5MB each) bundled in the app. I moved 2 to deferred loading with `deferredComponents`, downloading them on first use.
- **Removed unused dependencies** — I ran `dart pub deps` and found 4 packages that were imported but not used. Removing them saved 1.5MB.
- **Used `--tree-shake-icons`** — I ensured `--tree-shake-icons` was enabled (it is by default in release mode) to remove unused icon fonts.

### Result
APK size dropped from 28MB to 17MB (39% reduction). IPA dropped from 35MB to 24MB (31% reduction). Install conversion rate in emerging markets increased by 8% (measured by Play Store console). The asset audit process was added to our release checklist. The `--split-per-abi` and R8 configurations were adopted as the default build settings for all Flutter apps in the company.

---

## Q9: Tell me about a time you had to handle a disagreement with your tech lead.

### Situation
Our tech lead wanted to use `Bloc` for state management in a new feature. I advocated for `Riverpod` because it was simpler, had better testability, and the team was already familiar with it. The tech lead felt `Bloc` was more "industry standard" and better for complex flows.

### Task
I needed to express my technical opinion without being confrontational, while respecting the tech lead's authority and experience.

### Action
- **Acknowledged their perspective** — I said "I understand Bloc is a solid choice and is widely used. I want to share why I think Riverpod might be a better fit for this specific feature."
- **Prepared a comparison** — I created a side-by-side comparison of Bloc vs Riverpod for the specific feature (a 3-step form with async validation). I focused on: lines of code, testability, and team familiarity.
- **Built a prototype in both** — I implemented the first step of the form in both Bloc and Riverpod. The Riverpod version was 40% less code and didn't require a separate event class hierarchy.
- **Presented the data, not opinions** — I showed the comparison and prototype to the tech lead. I said "Here's the data. I'll go with whatever you decide."
- **Raised a valid concern** — I pointed out that the team had no Bloc experience, so we'd need 1-2 weeks of ramp-up. This was a factual concern, not an opinion.
- **Accepted the decision** — the tech lead chose Riverpod after seeing the prototype. But I made it clear I would have been fine with Bloc if they'd chosen it.

### Result
We used Riverpod and the feature was delivered on time. The tech lead later told me they appreciated the prototype-driven approach — it made the decision objective rather than subjective. Our relationship improved because I showed respect while still advocating for my position. The key learning was that data + prototypes resolve disagreements better than arguments, and "I'll support whatever you decide" builds trust.

---

## Q10: Tell me about a time you failed and what you learned.

### Situation
I was tasked with building a custom Flutter package for charts — a combination of line charts, bar charts, and pie charts with animations. Instead of using the existing `fl_chart` package, I decided to build from scratch using `CustomPainter` because I wanted "full control" and thought the existing package was too restrictive.

### Task
I needed to deliver a charting library that supported 5 chart types, animations, tooltips, and theming.

### Action
- **Built from scratch** — I spent 3 weeks building a custom charting library with `CustomPainter`. The line chart and bar chart worked, but animations were janky, tooltips were misaligned on different screen sizes, and I hadn't handled RTL.
- **Hit a wall on pie charts** — the math for animated pie chart segments with gaps was significantly more complex than I expected. I spent 4 days on it and it still had rendering bugs.
- **A teammate suggested `fl_chart`** in code review — I looked at it and realized it handled everything I was struggling with, plus it was battle-tested by thousands of apps.
- **I had to scrap 3 weeks of work** and integrate `fl_chart` instead. The integration took 2 days and handled all 5 chart types with smooth animations.

### Result
I wasted 3 weeks building something that already existed in a better form. The lesson was: **always evaluate existing packages before building custom.** I created a checklist for myself: (1) Is there a pub.dev package? (2) Is it maintained? (3) Does it meet 80% of requirements? (4) Can I extend it rather than build from scratch? I shared this checklist with the team and it saved multiple engineers from the same mistake. I also learned that "full control" is often an excuse for "not invented here" syndrome — `fl_chart` was extensible enough for our needs through its builder API.

---

## 🔗 Related Topics
- [Android Behavioral Questions](AndroidBehavioralQuestions.md)
- [Compose Behavioral Questions](ComposeBehavioralQuestions.md)
