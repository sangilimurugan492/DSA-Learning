# 🎯 Topic-Wise Interview Preparation — 24-Week Master Plan

> **Start Date:** Monday, Nov 2, 2026 · **End Date:** Sunday, Apr 18, 2027 (24 Weeks)
> **Scope:** every question bank in this repo — **274 files / ~88,200 lines** (DSA excluded, it has its own plan)
> **Pace:** ~8 hrs/week (90 min × Mon–Thu, 1 hr Sat, 1.5 hr Sun, Friday 🛑 rest)
> **Runs in parallel with** [DSA 24-Week Pattern Plan](../DSA/PatternWise_Prep/MASTER_PLAN_24W.md) (13 hrs/week, same dates, same Friday rest day)
> ⚠️ **Supersedes** the 12-week date-rotation plan in [Daily_Topic_Prep/MASTER_PLAN.md](../Daily_Topic_Prep/MASTER_PLAN.md). That plan rotated 1 topic/day; this one gives each topic a **whole week (or 2–4)** → depth, complete coverage, no topic-starved.

---

## 📅 Weekly Rhythm (topic track)

| Day | Time | Activity |
|-----|------|----------|
| **Monday** | 90 min | Block 1 — read → self-answer aloud → 3-line summary |
| **Tuesday** | 90 min | Block 2 |
| **Wednesday** | 90 min | Block 3 |
| **Thursday** | 90 min | Block 4 (finishes the week's file list) |
| **Friday** | — | 🛑 REST DAY (shared with DSA plan) |
| **Saturday** | 60 min | Overflow block (if listed) + **spaced revision**: 10 hardest Qs of this week + 10 Qs from the week 4 weeks back |
| **Sunday** | 90 min | 🧪 10-Q self-quiz, closed-notes (30 min) → 🎤 STAR story from [behavioral_questions/](../behavioral_questions/) (30 min) → 🏗️ system-design micro-drill (30 min) |

**Combined day = topic 90 min + DSA 2 hrs ≈ 3.5 hrs/weekday, ~21 hrs/week.**
⚡ **Light mode (~5 hrs/week)** if a week overflows: keep Mon–Thu at 60 min using the **skim-known rule**, do Saturday, and move the Sunday quiz to the next Sunday. Never drop scenario files or the Sunday quiz — they are the interview simulation.

### 🔵 Skim-Known Rule (this is what makes the volume possible)
Covered material is ~88k lines. Reading it linearly is not the goal:
1. Read the **question headings first**, skip the answers.
2. If you can answer aloud in **< 30 seconds** → mark the question 🔵 (known) and move on.
3. Deep-read only ❓ (unknown) / 🟡 (fuzzy) questions and every **scenario_based/** file.
Realistically this cuts each week by 30–40 %. Mark counts in [Progress_Tracker.md](Progress_Tracker.md).

---

## 🧭 7 Phases → 24 Weeks

| Phase | Weeks | Theme | Detail file |
|-------|-------|-------|-------------|
| **1** | 1–4 | Java + Kotlin (language depth) | [Phase1](weekly/Phase1_Week01-04_Languages.md) |
| **2** | 5–7 | Android core, architecture, advanced | [Phase2](weekly/Phase2_Week05-07_Android.md) |
| **3** | 8–10 | Jetpack Compose | [Phase3](weekly/Phase3_Week08-10_Compose.md) |
| **4** | 11–13 | Flutter & Dart | [Phase4](weekly/Phase4_Week11-13_Flutter.md) |
| **5** | 14–15 | Kotlin Multiplatform | [Phase5](weekly/Phase5_Week14-15_KMP.md) |
| **6** | 16–20 | SOLID · Design Patterns · Testing · CI/CD · Security · WebRTC · Firebase | [Phase6](weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) |
| **7** | 21–24 | System Design (mobile) + mocks | [Phase7](weekly/Phase7_Week21-24_System_Design.md) |

---

## 🗂️ 24-Week Topic Map

| Wk | Dates | Topic (files ≈) | Files | ~Lines |
|----|-------|-----------------|-------|--------|
| **1** | Nov 2 – Nov 8 | Java — Core language, OOP, generics, exceptions | 12 | 2,914 |
| **2** | Nov 9 – Nov 15 | Java — Collections, Streams, Concurrency, JVM/GC + scenarios | 13 | 4,102 |
| **3** | Nov 16 – Nov 22 | Kotlin — Core language, null safety, idioms | 16 | 3,022 |
| **4** | Nov 23 – Nov 29 | Kotlin — Coroutines, Flows, advanced + scenarios | 17 | 4,463 |
| **5** | Nov 30 – Dec 6 | Android — Fundamentals (components, UI, permissions) | 10 | 2,583 |
| **6** | Dec 7 – Dec 13 | Android — Architecture & data layer | 11 | 3,780 |
| **7** | Dec 14 – Dec 20 | Android — Advanced (DI, modularization, perf, security) | 11 | 3,629 |
| **8** | Dec 21 – Dec 27 | Compose — Fundamentals (composables, state, modifiers) | 7 | 2,872 |
| **9** | Dec 28 – Jan 3 | Compose — State management, navigation, theming | 14 | 4,377 |
| **10** | Jan 4 – Jan 10 | Compose — Advanced + scenario bootcamp | 12 | 3,737 |
| **11** | Jan 11 – Jan 17 | Flutter — Dart & widget fundamentals | 7 | 2,526 |
| **12** | Jan 18 – Jan 24 | Flutter — Features + state-management deep dive | 16 | 3,897 |
| **13** | Jan 25 – Jan 31 | Flutter — Advanced (internals, perf, channels) + scenarios | 13 | 3,804 |
| **14** | Feb 1 – Feb 7 | KMP — expect/actual, architecture, coroutines, DB, DI | 12 | 3,750 |
| **15** | Feb 8 – Feb 14 | KMP — Advanced, Compose Multiplatform *(+ catch-up week)* | 6 | 2,038 |
| **16** | Feb 15 – Feb 21 | SOLID + design principles, creational & structural patterns | 9 | 3,445 |
| **17** | Feb 22 – Feb 28 | Behavioural/concurrency/architectural patterns + Git & GitHub | 10 | 3,696 |
| **18** | Mar 1 – Mar 7 | Testing & TDD (unit, Mockito, Espresso, Compose) | 7 | 4,668 |
| **19** | Mar 8 – Mar 14 | CI/CD + App Security (Keystore, encryption, pinning) | 10 | 4,150 |
| **20** | Mar 15 – Mar 21 | WebRTC + Firebase (heaviest week) | 23 | 5,566 |
| **21** | Mar 22 – Mar 28 | System Design — fundamentals, components, patterns, estimation | 11 | 4,508 |
| **22** | Mar 29 – Apr 4 | System Design — worked examples I + answer template | 10 | 3,489 |
| **23** | Apr 5 – Apr 11 | System Design — worked examples II + playbook | 10 | 4,138 |
| **24** | Apr 12 – Apr 18 | System Design — playbook III + 🏁 Grand Mock & grand revision | 6 | 3,068 |

**Total: 274 files, ~88,250 lines, avg 3,677 lines/week (≈ 5.5 hrs of reading + 2.5 hrs of speaking/rewriting).**

---

## 🔁 Retention System (this is what makes 24 weeks stick)

| Layer | When | What |
|-------|------|------|
| **Same-day** | end of each session | 3-line summary in the week file + 🔵/🟡/❓ mark per question |
| **Saturday** | every Sat | 10 hardest Qs of this week **+ 10 Qs from the week ~4 weeks back** (spaced repetition) |
| **Phase close** | last Sunday of each phase (W4, W7, W10, W13, W15, W20, W24) | closed-book 20-Q re-quiz of the whole phase + write the phase one-pager |
| **Story bank** | every Sun | 1 STAR story, added to your bank (24 stories by W24) |
| **Final** | W24 | 🏁 Grand mock: 1 full mobile system design + 30 random Qs across all banks |

**Saturday revision pairs (fixed, no thinking needed):**
W2←W1 · W3←W1 · W4←W2 · W5←W1+W2 · W6←W2+W3 · W7←W3+W4 · W8←W4+W5 · W9←W5+W6 · W10←W7+W8 · W11←W8+W9 · W12←W10+W11 · W13←W11+W12 · W14←W12+W13 · W15←backlog catch-up · W16←W1+W3 · W17←W5+W7 · W18←W8+W10 · W19←W11+W13 · W20←W14+W16 · W21←W6+W7 · W22←W9+W10 · W23←W12+W13 · W24←everything

---

## 🎤 Sunday STAR Rotation — [behavioral_questions/](../behavioral_questions/)

| Sundays | File to mine | Pass |
|---------|--------------|------|
| W1–W2 | [GeneralBehavioralQuestions.md](../behavioral_questions/GeneralBehavioralQuestions.md) | 1st half / 2nd half |
| W3–W4 | [AndroidBehavioralQuestions.md](../behavioral_questions/AndroidBehavioralQuestions.md) | 1st / 2nd half |
| W5–W6 | [KotlinBehavioralQuestions.md](../behavioral_questions/KotlinBehavioralQuestions.md) + [JavaBehavioralQuestions.md](../behavioral_questions/JavaBehavioralQuestions.md) | one each |
| W7–W8 | [ComposeBehavioralQuestions.md](../behavioral_questions/ComposeBehavioralQuestions.md) | 1st / 2nd half |
| W9–W10 | [FlutterBehavioralQuestions.md](../behavioral_questions/FlutterBehavioralQuestions.md) | 1st / 2nd half |
| W11–W13 | [GeneralBehavioralQuestions.md](../behavioral_questions/GeneralBehavioralQuestions.md) | 2nd pass — sharpen the Results with numbers |
| W14–W16 | [critical_issues_star_report.md](../behavioral_questions/critical_issues_star_report.md) | 3 passes → your "hardest incident" answer |
| W17–W20 | [README.md](../behavioral_questions/README.md) + weakest 4 stories | polish, cut to 90 seconds each |
| W21–W24 | full bank | 1 story/round, mock-panel style |

---

## 🤝 How the two 24-week plans share a day

| Time block | Mon–Thu | Sat | Sun |
|------------|---------|-----|-----|
| Slot 1 | Topic prep 90 min | Topic revision 60 min | Topic quiz 30 min |
| Slot 2 | DSA 2 hrs (2–3 problems) | DSA re-solve 2 hrs | DSA mock 2 hrs + review 1 hr |
| Slot 3 | — | — | STAR 30 min + SD drill 30 min |

If a week collapses, **protect the DSA mock and the topic Sunday quiz** — cut Mon–Thu reading instead (light mode). Do not "borrow" Friday: both plans rest on Friday.

---

## 📌 Coverage Notes (verified against the repo)

* **Scheduled:** 274 of 291 bank files (~88,250 lines). Every beginner/intermediate/advanced/scenario/state_management folder is covered.
* **Deliberately skipped:** [design_patterns/principles/solid/](../design_patterns/principles/solid/) — duplicate of [solid/](../solid/), scheduled once in Week 16.
* **Optional add-on (Week 24, only for fintech/payments roles):** [Mobile_Payment_Flows.md](../system_design/mobile/payment_implementation/Mobile_Payment_Flows.md) (1,460 l) + [Mobile_Payment_Code_Examples.md](../system_design/mobile/payment_implementation/Mobile_Payment_Code_Examples.md) (1,622 l).
* **Not part of this plan:** [DSA/](../DSA/) (own 24-week plan), [interview_schedule/](../interview_schedule/) and [Daily_Topic_Prep/](../Daily_Topic_Prep/) (older 12-week variants, kept for reference).

---

## ✅ Rules of the Plan

1. **Speak, don't re-read.** Every answer out loud, 60–120 seconds, interview tense. Reading ≠ answering.
2. **Scenario files are the interview.** Beginner/intermediate files are warm-ups; never bank time by skipping `scenario_based/`.
3. **One artifact per phase** (one-pager/flashcards) — the artifact is the revision material for W21–W24, not the notes you copy.
4. **Never carry more than one week of debt.** Week 15 (KMP advanced, 2,038 lines) is the deliberately light week → use the spare time to clear backlog.
5. **Track it or you lost it.** Update [Progress_Tracker.md](Progress_Tracker.md) at the end of every session; the weekly % is the only signal that tells you to slow down or speed up.

---

## 📂 Files in This Folder

| File | Purpose |
|------|---------|
| [MASTER_PLAN_24W.md](MASTER_PLAN_24W.md) | This file — map, rhythm, retention system, rules |
| [weekly/Phase1_Week01-04_Languages.md](weekly/Phase1_Week01-04_Languages.md) | Week 1–4 day plan + quizzes |
| [weekly/Phase2_Week05-07_Android.md](weekly/Phase2_Week05-07_Android.md) | Week 5–7 |
| [weekly/Phase3_Week08-10_Compose.md](weekly/Phase3_Week08-10_Compose.md) | Week 8–10 |
| [weekly/Phase4_Week11-13_Flutter.md](weekly/Phase4_Week11-13_Flutter.md) | Week 11–13 |
| [weekly/Phase5_Week14-15_KMP.md](weekly/Phase5_Week14-15_KMP.md) | Week 14–15 |
| [weekly/Phase6_Week16-20_Patterns_Quality_Backend.md](weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) | Week 16–20 |
| [weekly/Phase7_Week21-24_System_Design.md](weekly/Phase7_Week21-24_System_Design.md) | Week 21–24 |
| [Progress_Tracker.md](Progress_Tracker.md) | Session, weekly and phase checkboxes |
| [daily/README.md](daily/README.md) | Index of the 24 daily checklist sheets + how to use them |
| [daily/Week01_Java_Core.md](daily/Week01_Java_Core.md) … [daily/Week24_SysDesign_Final.md](daily/Week24_SysDesign_Final.md) | Per-week daily checklists (Read → Say it → Task → Log, Sat revision, Sun quiz/STAR/SD) |

