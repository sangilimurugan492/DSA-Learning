# Week 22 · System Design — Case Studies I (delivery reps)
**Mon Mar 29 – Sun Apr 4** · 12 files / ~4,400 lines · Phase 7 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-7 detail](../weekly/Phase7_Week21-24_System_Design.md)

### Mon Mar 29 — Chat + E-commerce (90 min)
- [ ] **Read** [example_chat_app_design](../../system_design/mobile/example_chat_app_design.md), [example_ecommerce_app_design](../../system_design/mobile/example_ecommerce_app_design.md) (~820 l)
- [ ] **Say it** — read → close the doc → reproduce the design aloud in 4 min (message fan-out, delivery receipts, presence, inventory race)
- [ ] **Task** — chat: add the mobile angle on top of the doc (FG/BG delivery, FCM + MQTT fallback, read-receipt batching to save battery)
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Mar 30 — Food delivery + Ride sharing (90 min)
- [ ] **Read** [example_food_delivery_app_design](../../system_design/mobile/example_food_delivery_app_design.md), [example_ride_sharing_app_design](../../system_design/mobile/example_ride_sharing_app_design.md) (~813 l)
- [ ] **Say it** — reproduce each in 4 min: matching, geospatial indexing, ETA, order state machine, idempotent payment
- [ ] **Task** — write the **order state machine** once (states, transitions, retry/compensation) and reuse it for both docs — show it's the same shape
- [ ] **Log** — summary + tracker

### Wed Mar 31 — Social feed + playbook setup (90 min)
- [ ] **Read** [example_social_media_feed_design](../../system_design/mobile/example_social_media_feed_design.md), [playbook/README](../../system_design/mobile_interview_playbook/README.md), [00_Interview_Answer_Template](../../system_design/mobile_interview_playbook/00_Interview_Answer_Template.md) (~906 l)
- [ ] **Say it** — fan-out-on-write vs read: which for which follower count (90 s)
- [ ] **Task** — personalise `00_Interview_Answer_Template` (from today's read): write YOUR 5 opening questions + the slide order you always use → **artifact #10**
- [ ] **Log** — summary + tracker

### Thu Apr 1 — Model answers 1–3 (90 min)
- [ ] **Read** [01_Ecommerce_App_Answer](../../system_design/mobile_interview_playbook/01_Ecommerce_App_Answer.md), [02_Chat_App_Answer](../../system_design/mobile_interview_playbook/02_Chat_App_Answer.md), [03_Food_Delivery_App_Answer](../../system_design/mobile_interview_playbook/03_Food_Delivery_App_Answer.md) (~950 l)
- [ ] **Say it** — for each: 30-second framing (the real problem + the one trade-off that decides the design)
- [ ] **Task** — diff these written answers against your own notes; steal 3 phrasings and rewrite your opening 5 minutes once
- [ ] **Log** — summary + tracker

### Sat Apr 3 — Revision & re-delivery (60 min)
- [ ] 🔁 **Re-deliver chat in 40 min** (from Mar 28 baseline) → score against last week's recording (structure, numbers, trade-offs). **Phase-7 artifact #9 evidence.**
- [ ] Spaced revision: **Week 10 + Week 15** — 10 Qs each
- [ ] **Log** — before/after score delta + tracker

### Sun Apr 4 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-22 quiz Qs](../weekly/Phase7_Week21-24_System_Design.md) + 5 recall: ① presence without a heartbeat storm ② inventory oversell prevention ③ geospatial index choice ④ delivery-receipt batching ⑤ what you'd measure first after launch
- [ ] 🎤 STAR #22 — [GeneralBehavioralQuestions.md](../../behavioral_questions/GeneralBehavioralQuestions.md) — "a decision you'd make differently now" — 90 s
- [ ] 🏗️ SD drill — 10-min verbal on a prompt you've never seen (roll a random one from [system_design/interview](../../system_design/interview/README.md))

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
