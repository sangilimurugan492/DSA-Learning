# Week 23 · System Design — Case Studies II (delivery reps)
**Mon Apr 5 – Sun Apr 11** · 11 files / ~4,138 lines · Phase 7 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-7 detail](../weekly/Phase7_Week21-24_System_Design.md)

### Mon Apr 5 — Video streaming + Payments (90 min)
- [ ] **Read** [example_video_streaming_app_design](../../system_design/mobile/example_video_streaming_app_design.md), [example_payment_app_design](../../system_design/mobile/example_payment_app_design.md) (~1,024 l)
- [ ] **Say it** — reproduce each in 4 min: ingest → transcode → CDN; ledger, idempotency, reconciliation, exactly-once claims (120 s)
- [ ] **Task** — write the payment **idempotency + double-spend prevention** answer in exactly 6 lines (the one answer you must never fumble)
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Apr 6 — Notifications + Offline-first (90 min)
- [ ] **Read** [example_notification_system_design](../../system_design/mobile/example_notification_system_design.md), [example_offline_first_app_design](../../system_design/mobile/example_offline_first_app_design.md) (~1,154 l)
- [ ] **Say it** — these are your two strongest mobile-native designs — deliver each in 4 min with numbers
- [ ] **Task** — write the sync conflict matrix: last-write-wins / CRDT merge / server-authoritative — one feature each
- [ ] **Log** — summary + tracker

### Wed Apr 7 — Job board + model answers 4–5 (90 min)
- [ ] **Read** [example_job_search_app_design](../../system_design/mobile/example_job_search_app_design.md), [04_Ride_Sharing_App_Answer](../../system_design/mobile_interview_playbook/04_Ride_Sharing_App_Answer.md), [05_Social_Media_Feed_Answer](../../system_design/mobile_interview_playbook/05_Social_Media_Feed_Answer.md) (~1,081 l)
- [ ] **Say it** — search/filter/index answer (what an index is, why it costs write throughput) in 90 s
- [ ] **Task** — for each model answer: 5-min paper section only (requirements, estimates, API/interface contract)
- [ ] **Log** — summary + tracker

### Thu Apr 8 — Model answers 6–8 (90 min)
- [ ] **Read** [06_Video_Streaming_App_Answer](../../system_design/mobile_interview_playbook/06_Video_Streaming_App_Answer.md), [07_Payment_App_Answer](../../system_design/mobile_interview_playbook/07_Payment_App_Answer.md), [08_Notification_System_Answer](../../system_design/mobile_interview_playbook/08_Notification_System_Answer.md) (~879 l)
- [ ] **Say it** — for each: the failure mode interviewers probe (buffering/DRM, double charge, notification storm + dedupe) and your mitigation (90 s each)
- [ ] **Task** — steal one phrasing from each written answer into your own template; mark which numbers you'd quote unprompted
- [ ] **Log** — summary + tracker

### Sat Apr 10 — Revision & re-delivery (60 min)
- [ ] 🔁 Re-deliver **offline-first sync** OR **notification system** in 40 min, recorded → score vs Week-21 baseline
- [ ] Spaced revision: **Week 16 + Week 19** — 10 Qs each (patterns + security)
- [ ] **Task** — finalise your **personal design checklist card** (1 page you can review in a cab before an interview)
- [ ] **Log** — score delta + tracker

### Sun Apr 11 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-23 quiz Qs](../weekly/Phase7_Week21-24_System_Design.md) + 5 recall: ① CDN cache keys & signed URLs ② why a ledger beats a balance column ③ retry topics vs dedupe keys ④ LWW vs CRDT decision rule ⑤ what you'd cut from a design at the 35-min mark
- [ ] 🎤 STAR #23 — any bank: "mentoring / raising the team bar" — 90 s
- [ ] 🏗️ SD drill — 15-min: "design our app's analytics pipeline" — your own product, end to end

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
