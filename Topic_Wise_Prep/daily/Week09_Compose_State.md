# Week 9 · Compose — State, Navigation, Theming
**Mon Dec 28 – Sun Jan 3** · 14 files / ~4,377 lines · Phase 3 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-3 detail](../weekly/Phase3_Week08-10_Compose.md) · bank [jetpack_compose_questions/](../../jetpack_compose_questions/)

### Mon Dec 28 — Lists, effects, animations (90 min)
- [ ] **Read** `intermediate/` Lists, Effects, Animations (~917 l)
- [ ] **Say it** — `LazyColumn` keys + `contentType`, `derivedStateOf` vs `remember` for computed state (120 s)
- [ ] **Task** — add `key = { it.id }` + `contentType` to a real list, then scroll with Layout Inspector open and count composations before/after
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Dec 29 — Navigation, state mgmt, theming (90 min)
- [ ] **Read** `intermediate/` Navigation, StateManagement, Theming (~1,257 l)
- [ ] **Say it** — type-safe routes, deep links, back stack + process-death restore; Material 3 token strategy (120 s)
- [ ] **Task** — migrate one screen's navigation args to a `@Serializable` route object; add one deep link and test `adb shell am start -a android.intent.action.VIEW`
- [ ] **Log** — summary + tracker

### Wed Dec 30 — State-management core (90 min)
- [ ] **Read** `state_management/` Fundamentals, Flow, Comparison, BestPractices (~1,111 l)
- [ ] **Say it** — `collectAsState` vs `collectAsStateWithLifecycle`, hoisting rules, one-way data flow in Compose (120 s)
- [ ] **Task** — rewrite a screen so the composable takes `value + onEvent` only (no ViewModel inside); state why it is easier to preview & test
- [ ] **Log** — summary + tracker

### Thu Dec 31 — ViewModel & saved state (90 min, light day — New Year)
- [ ] **Read** `state_management/` ViewModel, StateHoisting, SavedStateHandle, README (~1,092 l)
- [ ] **Say it** — where `SavedStateHandle` fits vs `rememberSaveable` (60 s)
- [ ] **Task** — make one screen survive `adb shell am kill` with its scroll position + filter selection intact; write the 3 APIs that made it work
- [ ] **Log** — summary + tracker

### Sat Jan 2 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 5 + Week 6** — 10 Qs each (lifecycle + architecture)
- [ ] **Task** — write the "list + search + pagination" screen in Compose pseudocode on one page (whiteboard rehearsal)

### Sun Jan 3 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-9 quiz Qs](../weekly/Phase3_Week08-10_Compose.md) + 5 recall: ① `rememberSaveable` limits (Parcelable/Saver) ② `NavHost` route collision ③ `CompositionLocalProvider` scoping ④ `Modifier.pointerInput` keys ⑤ `animate*AsState` restart semantics
- [ ] 🎤 STAR #9 — [FlutterBehavioralQuestions.md](../../behavioral_questions/FlutterBehavioralQuestions.md) (1st half) — 90 s
- [ ] 🏗️ SD drill — UI event → VM → repo → DB → UI loop, drawn twice: online happy path vs offline-then-sync

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
