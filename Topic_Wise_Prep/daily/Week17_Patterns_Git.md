# Week 17 · Design Patterns in Practice + Git & GitHub
**Mon Feb 22 – Sun Feb 28** · 10 files / ~3,696 lines · Phase 6 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-6 detail](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md)

### Mon Feb 22 — Behavioural patterns (90 min)
- [ ] **Read** [behavioral/README.md](../../design_patterns/behavioral/README.md) (~888 l)
- [ ] **Say it** — Observer vs Flow (when a callback interface is still right); Strategy vs `when`; Command for a retry queue (120 s)
- [ ] **Task** — implement Strategy for one branchy function you own (replace a 6-branch `when` with a `Map<Kind, Strategy>`); keep the tests green
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Feb 23 — Concurrency & architectural patterns (90 min)
- [ ] **Read** [concurrency/README.md](../../design_patterns/concurrency/README.md), [architectural/README.md](../../design_patterns/architectural/README.md) (~1,239 l)
- [ ] **Say it** — producer-consumer / actor / thread-per-message mapped to Channel/actor-like `Flow`; MVVM vs MVI vs Clean vs offline-first (120 s)
- [ ] **Task** — rewrite one callback-based API as a `Channel`-backed producer-consumer and one as a `Flow`; state which you'd ship and why
- [ ] **Log** — summary + tracker

### Wed Feb 24 — Git mechanics (90 min)
- [ ] **Read** [GitBasics](../../github_questions/GitBasics.md), [Branching](../../github_questions/Branching.md), [GitHubActions](../../github_questions/GitHubActions.md) (~769 l)
- [ ] **Say it** — rebase vs merge policy for a mobile repo; recovering a detached HEAD; `bisect` over 300 commits (90 s)
- [ ] **Task** — in a scratch repo: create a conflict, resolve via `rebase`, then undo the whole thing with `reflog` (prove you can always go back)
- [ ] **Log** — summary + tracker

### Thu Feb 25 — Workflow, PRs, scenarios (90 min)
- [ ] **Read** [GitWorkflow](../../github_questions/GitWorkflow.md), [PullRequests](../../github_questions/PullRequests.md), [ScenarioBased](../../github_questions/ScenarioBased.md), [README](../../github_questions/README.md) (~800 l)
- [ ] **Say it** — your branch model (main/develop/release/hotfix), what you check as a reviewer, how you keep PRs reviewable (90 s)
- [ ] **Task** — write your 8-point personal review checklist (correctness, tests, naming, scope leak, thread-safety, rollout flag, analytics, rollback)
- [ ] **Log** — summary + tracker

### Sat Feb 27 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 5 + Week 7** — 10 Qs each (Android fundamentals + advanced)
- [ ] **Task** — 5 flashcards: "pattern I would refuse to use and why" (interviewers love this)
- [ ] **Log** — summary + tracker

### Sun Feb 28 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-17 quiz Qs](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) + 5 recall: ① Visitor when-to-refuse ② Flyout/`Flyweight` in lists ③ Template method vs Strategy ④ what `git cherry-pick -x` records ⑤ CODEOWNERS purpose
- [ ] 🎤 STAR #17 — [behavioral_questions/README.md](../../behavioral_questions/README.md) — polish your 2 weakest stories to 90 s
- [ ] 🏗️ SD drill — sketch Observer/Strategy/Repository exactly as they appear in your app's request path

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
