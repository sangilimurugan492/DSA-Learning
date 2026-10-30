# Week 19 · CI/CD + App Security
**Mon Mar 8 – Sun Mar 14** · 10 files / ~4,150 lines · Phase 6 · [plan](../MASTER_PLAN_24W.md) · [tracker](../Progress_Tracker.md) · [Phase-6 detail](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md)

### Mon Mar 8 — Pipelines that don't waste your day (90 min)
- [ ] **Read** [cicd/README](../../cicd_questions/README.md), [GitHubActions](../../cicd_questions/GitHubActions.md) (~729 l)
- [ ] **Say it** — PR lane vs release lane, signing/secrets, caching, artifact promotion, rollback (120 s)
- [ ] **Task** — write your ideal mobile pipeline as 8 numbered stages with the timeout and the failure owner for each
- [ ] **Log** — 3-line summary + ✅ [tracker](../Progress_Tracker.md)

### Tue Mar 9 — Jenkins & pipeline scenarios (90 min)
- [ ] **Read** [Jenkins](../../cicd_questions/Jenkins.md), [CICDScenarios](../../cicd_questions/CICDScenarios.md) (~1,450 l)
- [ ] **Say it** — how you'd cut a 45-min build to 15 min on a modular app (what you parallelise, what you cache, what you skip) (120 s)
- [ ] **Task** — pick 3 scenarios and write the CI change that prevents them recurring (e.g. signing-expiry alarm, cache invalidation rule)
- [ ] **Log** — summary + tracker

### Wed Mar 10 — Storage & crypto (90 min)
- [ ] **Read** [security/README](../../security_questions/README.md), [Keystore](../../security_questions/Keystore.md), [DataEncryption](../../security_questions/DataEncryption.md) (~777 l)
- [ ] **Say it** — hardware-backed keys, StrongBox, key attestation, why a hardcoded key over SharedPreferences is theatre (120 s)
- [ ] **Task** — write the storage decision table: token / PII / cache / analytics — where it lives, encrypted by what, and what leaks on a rooted device
- [ ] **Log** — summary + tracker

### Thu Mar 11 — Transport & biometrics (90 min)
- [ ] **Read** [SSLPinning](../../security_questions/SSLPinning.md), [BiometricAuth](../../security_questions/BiometricAuth.md), [SecurityScenarios](../../security_questions/SecurityScenarios.md) (~1,194 l)
- [ ] **Say it** — pin rotation without bricking users; `BiometricPrompt` + crypto object + fallback path (120 s)
- [ ] **Task** — produce your **mobile security checklist** (10 lines: storage, transport, auth, root/emulator, build/hardening, logging, WebView, deep links) → Phase-6 artifact #7 input
- [ ] **Log** — summary + tracker

### Sat Mar 13 — Revision (60 min)
- [ ] 10 hardest Qs of this week re-answered aloud
- [ ] Spaced revision: **Week 11 + Week 13** — 10 Qs each (Flutter)
- [ ] **Task** — 4 flashcards: attack → the one control that stops it (MITM, root dump, token replay, insecure deep link)
- [ ] **Log** — summary + tracker

### Sun Mar 14 — Quiz + STAR + SD drill (90 min)
- [ ] 🧪 Quiz /10: [Week-19 quiz Qs](../weekly/Phase6_Week16-20_Patterns_Quality_Backend.md) + 5 recall: ① trust anchors ② what attestation proves (and doesn't) ③ `EncryptedFile`/FileProvider rules ④ proguard/R8 as a security control (limits) ⑤ secret rotation process for a leaked keystore password
- [ ] 🎤 STAR #19 — any bank: "an incident or risk you escalated" — 90 s
- [ ] 🏗️ SD drill — add a security slide to a design: token flow, storage, transport, attestation, fraud signals — 1 slide

## 📝 3-line summaries
| Day | Summary in your own words | 🔵 / 🟡 / ❓ |
|-----|---------------------------|--------------|
| Mon | | |
| Tue | | |
| Wed | | |
| Thu | | |
| Sat/Sun | | |
