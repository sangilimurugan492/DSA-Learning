# Week 5 · Android — Fundamentals
**Mon Nov 30 – Sun Dec 6** · 10 files / ~2,583 lines · Phase 2 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-2 detail](../weekly/Phase2_Week05-07_Android.md) · bank [android_questions/](../../android_questions/)

### Mon Nov 30 — Components & lifecycle (90 min)
- [ ] **Read** `README` + `beginner/` ActivityLifecycle, Basics, FragmentLifecycle (~714 l)
- [ ] **Say it** — full lifecycle callback order incl. `onSaveInstanceState` timing; what survives process death (120 s)
- [ ] **Task** — instrument one real Activity with lifecycle logs, rotate + kill-without-kill, and write the 6-line log trace you observed
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Dec 1 — Intents, permissions, RecyclerView (90 min)
- [ ] **Read** `beginner/` Intents, Permissions, RecyclerView (~724 l)
- [ ] **Say it** — runtime permission flow incl. permanently-denied handling; DiffUtil with payloads (120 s)
- [ ] **Task** — add `DiffUtil.ItemCallback` + payload binding to one adapter you own; measure the before/after scroll (frames or `Profile GPU Rendering`)
- [ ] **Log** — summary + tracker

### Wed Dec 2 — Layouts + lifecycle scenarios (90 min)
- [ ] **Read** `beginner/UILayouts` + `scenario_based/LifecycleScenarios` (~558 l)
- [ ] **Say it** — ConstraintLayout vs Compose cost; overdraw and flatten depth (90 s)
- [ ] **Task** — take the 3 worst lifecycle scenarios and write the "correct" pattern once each (`repeatOnLifecycle`, `SavedStateHandle`, ViewModel as owner)
- [ ] **Log** — summary + tracker

### Thu Dec 3 — UI scenarios day (90 min)
- [ ] **Read** `scenario_based/UIScenarios` (~587 l)
- [ ] **Say it** — each scenario as symptom → measurement → fix (60 s each)
- [ ] **Task** — start the *Android architecture one-pager* (due W7): your default layer diagram, 5 boxes + arrows + who owns state
- [ ] **Log** — summary + tracker

### Sat Dec 5 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 1 + Week 2** — 10 Qs each (Java OOP + concurrency), because Android interviews drift back to threads
- [ ] **Task** — 5 new flashcards: lifecycle edge cases you got wrong this week

### Sun Dec 6 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-5 quiz Qs](../weekly/Phase2_Week05-07_Android.md) + 5 recall: ① `launchMode` options and use cases ② `startActivityForResult` vs Activity Result API ③ foreground vs system-enforced permissions ④ what `ViewHolder` actually saves ⑤ window insets basics
- [ ] 🎤 STAR #5 — [KotlinBehavioralQuestions.md](../../behavioral_questions/KotlinBehavioralQuestions.md) — "a code-review/quality story" — 90 s
- [ ] 🏗️ SD drill — draw Activity/Fragment lifecycle + process-death restore path on one slide (this slide comes back in W21)

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
