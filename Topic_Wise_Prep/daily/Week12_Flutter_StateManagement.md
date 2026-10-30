# Week 12 · Flutter — Features & State-Management Deep Dive
**Mon Jan 18 – Sun Jan 24** · 16 files / ~3,897 lines · Phase 4 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-4 detail](../weekly/Phase4_Week11-13_Flutter.md) · bank [flutter_questions/](../../flutter_questions/)

### Mon Jan 18 — Custom widgets, animations, Firebase (90 min)
- [ ] **Read** `intermediate/` CustomWidgets, Animations, FirebaseIntegration (~825 l)
- [ ] **Say it** — implicit vs explicit animations, `TickerProvider` role; what Firebase adds/removes in a Flutter app (90 s)
- [ ] **Task** — implement one `AnimatedWidget` with an `AnimationController` (dispose correctly) + one `AnimatedContainer` version of the same UI
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Jan 19 — Networking, advanced state, testing (90 min)
- [ ] **Read** `intermediate/` HTTPNetworking, StateManagementAdvanced, Testing (~840 l)
- [ ] **Say it** — `dio` interceptor chain: retry, token-refresh race, cancel-on-route-change (120 s)
- [ ] **Task** — write a refresh-token interceptor that queues concurrent 401s behind a single refresh call (this is a classic interview ask — write it from memory)
- [ ] **Log** — summary + tracker

### Wed Jan 20 — BLoC + fundamentals of state mgmt (90 min)
- [ ] **Read** `state_management/` Fundamentals, BLoC, Comparison, BestPractices (~898 l)
- [ ] **Say it** — event → state mapping, why handlers must be deterministic, how you unit-test a bloc (120 s)
- [ ] **Task** — implement a search-with-debounce BLoC (event `QueryChanged`, debounce 300 ms, states `Loading/Data/Error`) + 2 `bloc_test` cases
- [ ] **Log** — summary + tracker

### Thu Jan 21 — Provider / Redux / GetX / MobX (90 min)
- [ ] **Read** `state_management/` Provider, Redux, GetX, MobX, README (~1,070 l)
- [ ] **Say it** — single source of truth vs streams; middleware vs effect; cost of `Get.find` service-locator style (120 s)
- [ ] **Task** — fill 4 rows of your comparison table (boilerplate / testability / side effects / learning curve / when to pick) **with a one-line justification each**
- [ ] **Log** — summary + tracker

### Sat Jan 23 — Revision (60 min)
- [ ] **Read** `state_management/Riverpod` (~264 l) — provider types, `ref.listen` vs `watch`, auto-dispose
- [ ] 10 hardest Qs of this week + **Week 10 + Week 11** — 10 Qs each
- [ ] **Task** — write the "which state management do I recommend for X" answer 3 ways: startup MVP, large regulated app, migration mid-project
- [ ] **Log** — summary + tracker

### Sun Jan 24 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-12 quiz Qs](../weekly/Phase4_Week11-13_Flutter.md) + 5 recall: ① `context.watch` vs `read` ② `Provider.of/listen` ③ `BlocBuilder` vs `BlocListener` ④ Redux middleware signature ⑤ widget test vs golden test
- [ ] 🎤 STAR #12 — [GeneralBehavioralQuestions.md](../../behavioral_questions/GeneralBehavioralQuestions.md) (2nd pass, part 2) — 90 s
- [ ] 🏗️ SD drill — design the client half of a chat feature in Flutter: streams, offline queue, typing indicator; 2 slides

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
