# Phase 7 (Weeks 21–24) — Mobile System Design + Mocks

> Bank: [system_design/](../../system_design/) (~18,300 lines) — read [STUDY_ROADMAP.md](../../system_design/STUDY_ROADMAP.md) first, it states the intended order.
> **Method for every worked example:** read only the prompt, design it yourself on paper for 25 min, *then* open the written answer and diff. Reading the answer first is the #1 way to lose this round.
> Phase deliverable: your own **6-step answer template** (base: [00_Interview_Answer_Template.md](../../system_design/mobile_interview_playbook/00_Interview_Answer_Template.md)) + 4 designs you can deliver end-to-end in 40 minutes.

---

## Week 21 — Fundamentals, Components, Patterns, Estimation · Mar 22 – Mar 28 · 11 files / 4,508 lines

| Day | Files (in [system_design/](../../system_design/)) | ~Lines |
|-----|---------------------------------------------------|--------|
| Mon | `README` · `STUDY_ROADMAP` · `fundamentals/README` · `estimation/README` | 1,154 |
| Tue | `components/README` · `patterns/README` · `interview/README` | 1,465 |
| Wed | `case_studies/README` · `mobile/README` | 1,062 |
| Thu | `mobile/Mobile_System_Design_Interview_Guide` · `mobile/Mobile_System_Design_Cheat_Sheet` | 827 |
| Sat | 10 hardest of W21 + 10 from W6 + W7 (the client-side answers you already own) | — |
| Sun | 🧪 quiz · 🎤 STAR #21 · 🏗️ 10 back-of-envelope estimates aloud (DAU → QPS → storage → bandwidth) | — |

**Quiz:** ① Latency vs throughput vs availability; CAP in practice for a mobile feed. ② Estimation drill: 5 M DAU chat — messages/day, storage/year, peak QPS, push fan-out. ③ CDN, LB, API gateway, cache layers (memory/disk/CDN/DB) — where each sits in one mobile request. ④ Queues + event-driven ingestion, idempotency keys, at-least-once vs exactly-once. ⑤ Mobile constraints you must always raise: offline, flaky network, battery, release lag, version fragmentation (old clients live for months).

---

## Week 22 — Worked Examples I + Answer Template · Mar 29 – Apr 4 · 10 files / 3,489 lines

| Day | Files | ~Lines |
|-----|-------|--------|
| Mon | [example_chat_app_design](../../system_design/mobile/example_chat_app_design.md) · [example_ecommerce_app_design](../../system_design/mobile/example_ecommerce_app_design.md) | 820 |
| Tue | [example_food_delivery_app_design](../../system_design/mobile/example_food_delivery_app_design.md) · [example_ride_sharing_app_design](../../system_design/mobile/example_ride_sharing_app_design.md) | 813 |
| Wed | [example_social_media_feed_design](../../system_design/mobile/example_social_media_feed_design.md) · [playbook/README](../../system_design/mobile_interview_playbook/README.md) · [00_Answer_Template](../../system_design/mobile_interview_playbook/00_Interview_Answer_Template.md) | 906 |
| Thu | [playbook 01 Ecommerce](../../system_design/mobile_interview_playbook/01_Ecommerce_App_Answer.md) · [02 Chat](../../system_design/mobile_interview_playbook/02_Chat_App_Answer.md) · [03 Food Delivery](../../system_design/mobile_interview_playbook/03_Food_Delivery_App_Answer.md) | 950 |
| Sat | Re-deliver **chat** aloud in 40 min, recorded, then diff against your notes | — |
| Sun | 🧪 quiz · 🎤 STAR #22 · 🏗️ personalise the 6-step template (write once, reuse forever) | — |

**Name the hard part of each design out loud:** chat (ordering, delivery receipts, presence fan-out, offline queue) · ecommerce (catalog cache, cart consistency, inventory race, checkout idempotency) · food delivery (order state machine, ETA, location sampling) · ride sharing (geo index, matching, surge, safety) · feed (fan-out-on-write vs -on-read, ranking, caching, cold start).

---

## Week 23 — Worked Examples II + Playbook · Apr 5 – Apr 11 · 10 files / 4,138 lines

| Day | Files | ~Lines |
|-----|-------|--------|
| Mon | [example_video_streaming_app_design](../../system_design/mobile/example_video_streaming_app_design.md) · [example_payment_app_design](../../system_design/mobile/example_payment_app_design.md) | 1,024 |
| Tue | [example_notification_system_design](../../system_design/mobile/example_notification_system_design.md) · [example_offline_first_app_design](../../system_design/mobile/example_offline_first_app_design.md) | 1,154 |
| Wed | [example_job_search_app_design](../../system_design/mobile/example_job_search_app_design.md) · [playbook 04 Ride Sharing](../../system_design/mobile_interview_playbook/04_Ride_Sharing_App_Answer.md) · [05 Social Feed](../../system_design/mobile_interview_playbook/05_Social_Media_Feed_Answer.md) | 1,081 |
| Thu | [playbook 06 Video](../../system_design/mobile_interview_playbook/06_Video_Streaming_App_Answer.md) · [07 Payment](../../system_design/mobile_interview_playbook/07_Payment_App_Answer.md) · [08 Notification](../../system_design/mobile_interview_playbook/08_Notification_System_Answer.md) | 879 |
| Sat | Re-deliver **offline-first** or **notifications** aloud in 40 min — the two most-asked mobile designs | — |
| Sun | 🧪 quiz · 🎤 STAR #23 · 🏗️ build your design-checklist card: auth, offline, sync, retries, idempotency, observability | — |

**Focus:** video (adaptive bitrate, DRM, CDN, pre-fetch, resume) · payments (idempotency, tokenisation, 3-DS, reconciliation, double-charge prevention — tie back to Week 19 security) · notifications (FCM limits, topic vs device token, dedup, ordering, rate limits, delivery metrics) · offline-first (source of truth, conflict resolution LWW vs CRDT vs server-wins, sync windows, DB migrations on old clients).

---

## Week 24 — Playbook III + 🏁 Grand Mock & Grand Revision · Apr 12 – Apr 18 · 6 files / 3,068 lines

| Day | Files (in [mobile_interview_playbook/](../../system_design/mobile_interview_playbook/)) | ~Lines |
|-----|------------------------------------------------------------------------------------------|--------|
| Mon | [09_Job_Search_App_Answer](../../system_design/mobile_interview_playbook/09_Job_Search_App_Answer.md) · [10_Offline_First_App_Answer](../../system_design/mobile_interview_playbook/10_Offline_First_App_Answer.md) | 659 |
| Tue | [11_Udemy_Like_App_Features](../../system_design/mobile_interview_playbook/11_Udemy_Like_App_Features.md) · [12_Udemy_Like_App_Design_Answer](../../system_design/mobile_interview_playbook/12_Udemy_Like_App_Design_Answer.md) | 800 |
| Wed | [13_Udemy_Like_App_Architecture](../../system_design/mobile_interview_playbook/13_Udemy_Like_App_Architecture.md) | 666 |
| Thu | [Mobile_Payment_Implementation_Topics](../../system_design/mobile/payment_implementation/Mobile_Payment_Implementation_Topics.md) — *payments roles: add Flows + Code Examples (3,082 l)* | 943 |
| Sat | 🏁 **GRAND MOCK** — 45-min design on a prompt you have NOT rehearsed + 30 random questions sampled from every `scenario_based/` index | — |
| Sun | 🧪 score the mock · 🎤 4-story rapid-fire STAR · 🏗️ write the 1-page "my 6 steps + my numbers" summary | — |

**Interview-ready self-check (all boxes must be yes):**
- [ ] Any of the 10 worked designs delivered in 40 min, no notes
- [ ] "Kotlin vs Java", "Compose vs Views", "Flutter vs native", "KMP yes/no" — 2 minutes each, trade-offs + numbers
- [ ] 8+ STAR stories at 90 seconds each with measurable results
- [ ] One deep incident explained symptom → root cause → fix → prevention (Week 15 file)
- [ ] 5 production metrics you watch (crash-free, ANR, p95 latency, cold start, battery) and where you read them

