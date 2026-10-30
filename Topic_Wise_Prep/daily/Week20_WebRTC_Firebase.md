# Week 20 · WebRTC + Firebase · 🔒 phase gate
**Mon Mar 15 – Sun Mar 21** · 15 files / ~5,566 lines · Phase 6 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-6 detail](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md)

### Mon Mar 15 — WebRTC (90 min)
- [ ] **Read** all 4 files in [webrtc_questions/](../../webrtc_questions/) (~1,161 l)
- [ ] **Say it** — signalling vs ICE vs TURN; why P2P fails on symmetric NATs (120 s)
- [ ] **Task** — write the connect sequence as 8 messages: SDP offer → answer → ICE candidates → connected, and mark who sends each (peer A / peer B / signalling server)
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Mar 16 — Firebase core (90 min)
- [ ] **Read** `firebase_questions/README` + `beginner/` Authentication, Firestore, FCM (~1,157 l)
- [ ] **Say it** — Firestore security rules vs app-side checks; FCM delivery guarantees and what "delivered" doesn't mean (120 s)
- [ ] **Task** — write Firestore rules for one collection (role-based) + one client query that those rules must reject; explain the error you'd see
- [ ] **Log** — summary + tracker

### Wed Mar 17 — Firebase intermediate (90 min)
- [ ] **Read** `firebase_questions/intermediate/` Analytics, CloudFunctions, MLKit, RemoteConfig (~1,210 l)
- [ ] **Say it** — kill-switch design with RemoteConfig (defaults, fetch/expiry, server-evaluated flags, flag lifecycle) (120 s)
- [ ] **Task** — design one feature flag end-to-end: key, variants, targeting, guardrail metric, kill criteria, cleanup date
- [ ] **Log** — summary + tracker

### Thu Mar 18 — Firebase advanced (90 min)
- [ ] **Read** `firebase_questions/advanced/` AdvancedTopics, OfflinePersistence, PerformanceMonitoring, Security (~1,189 l)
- [ ] **Say it** — Firestore offline persistence conflicts & merge policy; what Firebase Performance actually measures (90 s)
- [ ] **Task** — write the "Firebase lock-in mitigation" answer (abstractions you keep, what you'd swap first, migration cost)
- [ ] **Log** — summary + tracker

### Sat Mar 20 — Firebase scenarios + revision (60 min)
- [ ] **Read** `firebase_questions/scenario_based/` DebuggingScenarios, ScalingScenarios (~849 l)
- [ ] Spaced revision: **Week 14 + Week 16** — 10 Qs each (KMP + SOLID)
- [ ] **Task** — finish *quality one-pager* (Phase-6 artifact #8) — your CI/CD + security + review + flags story on a single page
- [ ] **Log** — summary + tracker

### Sun Mar 21 — Quiz + phase gate (90 min)
- [ ] 🧪 Quiz /10: [Week-20 quiz Qs](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) + 5 recall: ① SFU vs MCU ② what `onNewToken` means (and what it doesn't) ③ index-required errors in Firestore ④ Firebase Auth session refresh on client ⑤ what you'd never put in RemoteConfig
- [ ] 🔒 **PHASE-6 GATE** — closed-book 25-Q re-quiz across W16–W20 (patterns, testing, CI/CD, security, WebRTC/Firebase)
- [ ] 🎤 STAR #20 — any bank: "your proudest engineering-quality improvement" — 90 s

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
