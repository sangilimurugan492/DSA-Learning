# Week 2 · Java — Collections, Streams, Concurrency, JVM
**Mon Nov 9 – Sun Nov 15** · 13 files / ~4,102 lines · Phase 1 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-1 detail](../weekly/Phase1_Week01-04_Languages.md) · bank [java_questions/](../../java_questions/)

### Mon Nov 9 — Collections & Streams internals (90 min)
- [ ] **Read** `intermediate/` Collections, LambdaAndStreams + `advanced/AndroidWithJava` (~971 l)
- [ ] **Say it** — `HashMap.put` step by step (hash → bucket → treeify at 8) and `ArrayList` vs `LinkedList` cost model (120 s)
- [ ] **Task** — write a mini `LRUCache` (LinkedHashMap vs hand-rolled map+doubly-linked) — compile & run 3 assertions
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Nov 10 — Concurrency, GC, patterns (90 min)
- [ ] **Read** `advanced/` ConcurrencyAdvanced, GarbageCollection, DesignPatterns (~1,012 l)
- [ ] **Say it** — `volatile` vs `synchronized` vs `Atomic*`; which GC would you pick for Android and why (120 s)
- [ ] **Task** — code a thread-safe singleton 2 ways (holder + enum) and say out loud why `double-checked` needs `volatile`
- [ ] **Log** — summary + tracker

### Wed Nov 11 — JVM, reflection, concurrency scenarios (90 min)
- [ ] **Read** `advanced/` JVMInternals, ReflectionAndAnnotations + `scenario_based/ConcurrencyScenarios` (~935 l)
- [ ] **Say it** — class loading, JIT, memory regions; cost of reflection on Android (90 s)
- [ ] **Task** — draw the JVM memory layout + one "how a leak grows" annotation on paper; photograph it into your notes
- [ ] **Log** — summary + tracker

### Thu Nov 12 — Scenario day (90 min)
- [ ] **Read** `scenario_based/` CollectionScenarios, DebuggingScenarios, AndroidJavaScenarios (~1,166 l)
- [ ] **Say it** — for each scenario: symptom → hypothesis → how you prove it (60 s each)
- [ ] **Task** — write your **debug playbook** (5 steps: reproduce → measure → isolate → fix → guard) with the tools for each step
- [ ] **Log** — summary + tracker

### Sat Nov 14 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 1** — 10 Qs (OOP/exceptions/generics)
- [ ] **Task** — finish the *Java concurrency & JVM* one-pager (this is the Phase-1 artifact #1)

### Sun Nov 15 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-2 quiz Qs](../weekly/Phase1_Week01-04_Languages.md) + 5 recall: ① `ConcurrentHashMap` locking strategy ② `countDownLatency`-free way to wait on N tasks ③ weak vs soft reference ④ what `parallelStream` uses under the hood ⑤ OOM types you have actually seen
- [ ] 🎤 STAR #2 — [GeneralBehavioralQuestions.md](../../behavioral_questions/GeneralBehavioralQuestions.md) (2nd half): "disagreement with a teammate" — 90 s
- [ ] 🏗️ SD drill — "design the cache layer of one screen": 3 cache levels, invalidation rule, 2 failure modes

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
