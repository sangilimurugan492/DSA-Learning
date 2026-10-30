# Week 11 · Flutter — Dart & Widget Fundamentals
**Mon Jan 11 – Sun Jan 17** · 7 files / ~2,526 lines · Phase 4 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-4 detail](../weekly/Phase4_Week11-13_Flutter.md) · bank [flutter_questions/](../../flutter_questions/)

### Mon Jan 11 — Dart + Flutter basics (90 min)
- [ ] **Read** `README` + `beginner/` Basics, DartBasics (~1,604 l)
- [ ] **Say it** — event loop vs isolate, `Future` vs `Stream`, `async*`/`yield`, null-safety rules in Dart (120 s)
- [ ] **Task** — write a Dart snippet that proves backpressure behaviour on a `Stream` (slow listener + `await for`) and explain the output in 3 lines
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Jan 12 — Layouts, navigation, basic state (90 min)
- [ ] **Read** `beginner/` Layouts, Navigation, StateManagement (~688 l)
- [ ] **Say it** — layout protocol (constraints down, sizes up, parent positions) + named routes vs `Navigator 2.0`/GoRouter (120 s)
- [ ] **Task** — build a screen with a `LayoutBuilder` + `ConstrainedBox` responsive layout; then break it with unbounded width and explain the error message
- [ ] **Log** — summary + tracker

### Wed Jan 13 — Widgets deep read (90 min)
- [ ] **Read** `beginner/Widgets` (~234 l) then re-read the two Dart sections you marked ❓
- [ ] **Say it** — Widget vs Element vs RenderObject: who holds state, who is rebuilt, who is reused (120 s)
- [ ] **Task** — draw the three trees for a tiny `Column(Icon, Text)` app and label where `Key` matters (list reordering bug demo if you have Flutter locally)
- [ ] **Log** — summary + tracker

### Thu Jan 14 — 🔧 Build day (90 min, no new files)
- [ ] Build a 3-screen app: named routes + one `StatefulWidget` you then convert to `ConsumerWidget` (Provider)
- [ ] Add a `const` constructor pass over the widget tree and note which rebuilds disappeared
- [ ] **Task** — write the "Flutter vs Compose" comparison (5 rows: state, layout, rebuild model, tooling, perf ceiling)
- [ ] **Log** — summary + tracker

### Sat Jan 16 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 8 + Week 9** — 10 Qs each (Compose fundamentals + state)
- [ ] **Task** — start the *state-management comparison table* (due W13): Provider/Riverpod/BLoC/Redux/GetX/MobX columns

### Sun Jan 17 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-11 quiz Qs](../weekly/Phase4_Week11-13_Flutter.md) + 5 recall: ① `Key` types and when each ② `StatefulWidget` state lives where ③ `mainAxisSize`/`IntrinsicHeight` cost ④ `FutureBuilder` pitfall on rebuild ⑤ what `hot reload` cannot do
- [ ] 🎤 STAR #11 — [GeneralBehavioralQuestions.md](../../behavioral_questions/GeneralBehavioralQuestions.md) (2nd pass — sharpen the Result with numbers) — 90 s
- [ ] 🏗️ SD drill — deep-link → route → data prefetch flow for a mobile app, 1 slide

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
