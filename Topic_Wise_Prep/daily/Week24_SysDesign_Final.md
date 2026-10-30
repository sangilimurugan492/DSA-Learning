# Week 24 · System Design — Payment Deep Dive, Playbook Completion · 🏁 FINAL WEEK
**Mon Apr 12 – Sun Apr 18** · 6 files / ~3,068 lines · Phase 7 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-7 detail](../weekly/Phase7_Week21-24_System_Design.md)

### Mon Apr 12 — Model answers 9–10 (90 min)
- [ ] **Read** [09_Job_Search_App_Answer](../../system_design/mobile_interview_playbook/09_Job_Search_App_Answer.md), [10_Offline_First_App_Answer](../../system_design/mobile_interview_playbook/10_Offline_First_App_Answer.md) (~659 l)
- [ ] **Say it** — 30-s framing each (search relevance + freshness trade-off; source-of-truth & conflict policy) (90 s)
- [ ] **Task** — write the offline **conflict matrix** once, final version: last-write-wins / server-authoritative / CRDT merge — one feature each, with why
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Apr 13 — Udemy-like platform: features + model answer (90 min)
- [ ] **Read** [11_Udemy_Like_App_Features](../../system_design/mobile_interview_playbook/11_Udemy_Like_App_Features.md), [12_Udemy_Like_App_Design_Answer](../../system_design/mobile_interview_playbook/12_Udemy_Like_App_Design_Answer.md) (~800 l)
- [ ] **Say it** — 30-s framing: the subsystems a course platform needs (catalog, video delivery, search, enrolment/payments, progress, instructor tooling) and which two you'd design live
- [ ] **Task** — 5-min paper section (requirements, estimates, interface). Don't re-read the full design afterwards.
- [ ] **Log** — summary + tracker

### Wed Apr 14 — Udemy-like architecture + playbook wrap (90 min)
- [ ] **Read** [13_Udemy_Like_App_Architecture](../../system_design/mobile_interview_playbook/13_Udemy_Like_App_Architecture.md) (~666 l)
- [ ] **Say it** — 30-s framing of the full architecture, then name the 2 decisions you'd defend hardest (video storage cost vs CDN egress, search freshness vs index cost)
- [ ] **Task** — skim-read the playbook answers you skipped across W22–W24; add 1 line each: "the move I'd steal in an interview"
- [ ] **Log** — summary + tracker

### Thu Apr 15 — Payment implementation topics (90 min)
- [ ] **Read** [Mobile_Payment_Implementation_Topics](../../system_design/mobile/payment_implementation/Mobile_Payment_Implementation_Topics.md) (~943 l) — *optional depth: [Flows](../../system_design/mobile/payment_implementation/Mobile_Payment_Flows.md) + [Code Examples](../../system_design/mobile/payment_implementation/Mobile_Payment_Code_Examples.md) (3,082 l)*
- [ ] **Say it** — PCI scope reduction (tokenisation), 3DS flow, PSD2/Open-Banking consent, what the *client* is responsible for vs the backend (120 s)
- [ ] **Task** — write your "mobile payments in 2 minutes" answer: token → authorise → capture → reconcile, with the retry/idempotency story
- [ ] **Log** — summary + tracker

### Sat Apr 17 — 🏁 GRAND MOCK — the whole 24 weeks (2 h)
- [ ] **Full mock interview, recorded:** 30 min rapid technical across all 7 phases → 45 min system design (unseen prompt, use your template) → 20 min behavioural (4 stories, no prep)
- [ ] Score it 1–5 per axis: fundamentals depth / trade-off reasoning / articulation / design structure / behavioural polish
- [ ] Write the **gap list**: exactly 5 topics below 4/5

### Sun Apr 18 — Close-out (90 min)
- [ ] 🧪 Final 25-Q closed-book mixed quiz (pick from every phase's Sunday quiz) — record the score in [Progress_Tracker](../Progress_Tracker.md)
- [ ] 🎤 Rapid-fire: 10 STAR stories at 90 s each, no pausing
- [ ] 📄 **2-page interview cheat sheet** from your ❓ list (final version) → **Phase-7 artifact #10**
- [ ] 🔁 Book the next cycle: the 5 gap topics from yesterday become Week 25–26 focus; pick 2–3 mock interviews on your calendar

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |

## 🏁 Graduation checklist
- [ ] All 24 weeks green in [Progress_Tracker](../Progress_Tracker.md) (or explicitly re-scheduled)
- [ ] 10 phase artifacts exist as real files
- [ ] Grand-mock score ≥ 4/5 on every axis (else continue with gap-cycle)
- [ ] 15 STAR stories polished to 90 s each
- [ ] Cheat sheet printed / on phone
