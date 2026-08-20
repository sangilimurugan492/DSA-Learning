# 📚 System Design Study Roadmap

> **Complete analysis of all system design topics in this project + a structured 12-week study plan.**
>
> This roadmap analyzes every topic across the `system_design/` folder and organizes them into a logical learning path — from beginner to interview-ready.

---

## 📊 Project Content Analysis

### What's in This Project

| Folder | Topics Covered | Document Size | Maturity |
|--------|---------------|---------------|----------|
| `fundamentals/` | 10 core concepts (scalability, CAP, consistency, idempotency, etc.) | ~280 lines | ✅ Complete |
| `components/` | 10 building blocks (LB, caching, MQ, DB, API GW, CDN, etc.) | ~430 lines | ✅ Complete |
| `patterns/` | 12 distributed patterns (sharding, consistent hashing, circuit breaker, saga, etc.) | ~513 lines | ✅ Complete |
| `case_studies/` | 10 end-to-end designs (URL shortener, Twitter, WhatsApp, etc.) | ~984 lines | ✅ Complete |
| `estimation/` | Capacity estimation, latency numbers, 3 worked examples | ~400 lines | ✅ Complete |
| `interview/` | 5-step framework, 25 questions, mock walkthrough, 8-week plan | ~519 lines | ✅ Complete |
| `mobile/` | Mobile system design guide, cheat sheet, 10 example designs | ~691 lines (guide) | ✅ Complete |
| `mobile_interview_playbook/` | 14-step template + 13 structured answers | 14 documents | ✅ Complete |

### Topic Inventory (65+ Topics)

```
FUNDAMENTALS (10):
  1. Scalability (Vertical vs Horizontal)
  2. Availability & Reliability
  3. Latency vs Throughput
  4. Consistency Models (Strong, Eventual, Weak, Read-Your-Writes, Causal)
  5. CAP Theorem
  6. PACELC Theorem
  7. Stateful vs Stateless Systems
  8. Synchronous vs Asynchronous Communication
  9. Idempotency
  10. Backpressure

COMPONENTS (10):
  1. Load Balancers (L4/L7, algorithms, health checks)
  2. Caching (cache-aside, write-through, write-back, eviction, failure modes)
  3. Message Queues (Kafka deep dive, RabbitMQ, SQS, delivery guarantees)
  4. Databases (SQL vs NoSQL, ACID vs BASE, OLTP vs OLAP, sharding, replication)
  5. API Gateways (routing, auth, rate limiting, BFF pattern)
  6. Service Discovery (registry, client-side, server-side, Kubernetes)
  7. Proxy / Reverse Proxy
  8. CDN (push vs pull, invalidation)
  9. Connection Pooling
  10. Distributed Tracing & Observability (metrics, logs, traces)

PATTERNS (12):
  1. Sharding / Partitioning
  2. Replication (master-slave, master-master, quorum)
  3. Consistent Hashing
  4. Bloom Filters
  5. Rate Limiting (token bucket, leaky bucket, sliding window)
  6. Circuit Breaker
  7. CQRS & Event Sourcing
  8. Leader Election (Raft, Paxos, split brain)
  9. Write-Ahead Log (WAL)
  10. Saga Pattern (choreography, orchestration)
  11. Bulkhead Pattern
  12. Retry with Exponential Backoff & Jitter

CASE STUDIES (10):
  1. URL Shortener (TinyURL)
  2. Twitter / Social Media Feed
  3. Chat / Messaging System (WhatsApp)
  4. Rate Limiter (distributed)
  5. Distributed File Storage (Google Drive)
  6. Notification System
  7. Web Crawler
  8. Ticket Booking System (BookMyShow)
  9. Key-Value Store (DynamoDB-style)
  10. News Feed Generation (Reddit)

ESTIMATION:
  - Latency numbers (memorize!)
  - Throughput numbers
  - 3 worked examples (Twitter, URL shortener, Chat)
  - Server sizing formulas
  - Common mistakes

MOBILE SYSTEM DESIGN:
  - 14-step mobile interview framework
  - 7 evaluation areas (architecture, networking, data, performance, security, reliability, backend)
  - 10 mobile case study designs
  - 14 structured interview answers (playbook)
  - Udemy-like app feature list + architecture

INTERVIEW PREP:
  - 5-step backend interview framework
  - 25 practice questions (beginner → advanced)
  - Technology decision guide
  - Anti-patterns
  - Mock interview walkthrough
```

---

## 🗺️ The 12-Week Study Roadmap

### Overview

| Phase | Weeks | Focus | What You'll Achieve |
|-------|-------|-------|---------------------|
| **Phase 1: Foundation** | 1–3 | Fundamentals + Estimation | Understand core concepts, can estimate any system |
| **Phase 2: Building Blocks** | 4–5 | Components + Patterns | Know what tools exist and when to use them |
| **Phase 3: Real Systems** | 6–7 | Case Studies | See how everything comes together |
| **Phase 4: Mobile Design** | 8–9 | Mobile System Design | Master mobile-specific architecture |
| **Phase 5: Practice** | 10–11 | Mock Interviews | Apply framework under time pressure |
| **Phase 6: Polish** | 12 | Deep Dives + Weak Spots | Interview-ready confidence |

---

## Phase 1: Foundation (Weeks 1–3)

> **Goal**: Internalize core concepts and numbers. Without these, everything else is memorization without understanding.

### Week 1 — Core Concepts (Part 1)

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Scalability (Vertical vs Horizontal) | `fundamentals/` §1 | Stateless scales horizontally; stateful is hard |
| Tue | Availability & Reliability | `fundamentals/` §2 | Nines table, MTBF/MTTR, redundancy |
| Wed | Latency vs Throughput | `fundamentals/` §3 | Latency = user feels; Throughput = system handles |
| Thu | Stateful vs Stateless | `fundamentals/` §7 | Externalize state → easy scaling |
| Fri | Sync vs Async Communication | `fundamentals/` §8 | Sync for user-facing; Async for background |

**Practice**: Explain each concept to yourself in 2 sentences. If you can't, re-read.

### Week 2 — Core Concepts (Part 2) + Consistency

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Consistency Models | `fundamentals/` §4 | Strong vs Eventual vs Weak vs Read-Your-Writes |
| Tue | CAP Theorem | `fundamentals/` §5 | During partition: choose C or A. CA is unrealistic. |
| Wed | PACELC Theorem | `fundamentals/` §6 | Even without partition: Latency vs Consistency trade-off |
| Thu | Idempotency | `fundamentals/` §9 | Retries are a certainty. Design for idempotency. |
| Fri | Backpressure | `fundamentals/` §10 | Every queue can overflow. Have a strategy. |

**Practice**: 
- Can you explain CAP in your own words?
- Can you give 1 real-world example of each consistency model?

### Week 3 — Estimation (Memorize the Numbers!)

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Latency Numbers | `estimation/` §2 | **MEMORIZE**: Memory 100Kx faster than disk, SSD 100x faster than HDD |
| Tue | Throughput Numbers | `estimation/` §2 | Redis: 100K QPS, DB: 5-10K QPS, Web server: 1-5K RPS |
| Wed | Estimation Framework | `estimation/` §3 | 7 steps: QPS → Storage → Bandwidth → Cache → Servers |
| Thu | Worked Example: Twitter | `estimation/` §4 | Practice the full estimation flow |
| Fri | Worked Example: URL Shortener | `estimation/` §5 | Practice the full estimation flow |

**Practice**:
- [ ] Memorize the latency table (L1, memory, SSD, HDD, network, cross-continent)
- [ ] Do 1 estimation from scratch: "Design a chat system for 500M users"
- [ ] Calculate: QPS, storage, bandwidth, cache, servers

---

## Phase 2: Building Blocks (Weeks 4–5)

> **Goal**: Know what components exist, what they do, and when to use each.

### Week 4 — Components

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Load Balancers | `components/` §1 | L4 (fast, TCP) vs L7 (smart, HTTP). Algorithms. Health checks. |
| Tue | Caching | `components/` §2 | Cache-aside (most common). Eviction (LRU). Failure modes (penetration, breakdown, avalanche). |
| Wed | Message Queues | `components/` §3 | Kafka (log, replay, high throughput) vs RabbitMQ (task queue). Delivery guarantees. |
| Thu | Databases | `components/` §4 | SQL (ACID) vs NoSQL (BASE). When to use which. Scaling: read replicas + sharding. |
| Fri | API Gateway + CDN + Proxy | `components/` §5,7,8 | Gateway = cross-cutting concerns. CDN = cheapest perf win. |

**Practice**:
- [ ] When would you choose Kafka over RabbitMQ?
- [ ] What's the difference between cache-aside and write-through?
- [ ] When would you use NoSQL over SQL?

### Week 5 — Patterns

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Sharding + Replication | `patterns/` §1,2 | Sharding = write scaling. Replication = read scaling. Both needed for full scale. |
| Tue | Consistent Hashing | `patterns/` §3 | Adding a node → only 1/N keys move. Virtual nodes for even distribution. |
| Wed | Rate Limiting + Circuit Breaker | `patterns/` §5,6 | Token bucket for APIs. Circuit breaker prevents cascading failures. |
| Thu | CQRS + Event Sourcing | `patterns/` §7 | Split read/write models. Store events, derive state. Powerful but complex. |
| Fri | Saga + Bulkhead + Retry | `patterns/` §10,11,12 | Saga = distributed transactions. Bulkhead = isolate failures. Retry = backoff + jitter. |

**Bonus (read if time permits)**:
- Bloom Filters (`patterns/` §4) — "fast no" for cache penetration
- Leader Election (`patterns/` §8) — Raft, Paxos, split brain
- Write-Ahead Log (`patterns/` §9) — WAL is source of truth, not data files

**Practice**:
- [ ] Explain consistent hashing with a diagram
- [ ] When do you use circuit breaker vs bulkhead?
- [ ] What's the difference between saga choreography and orchestration?

---

## Phase 3: Real Systems (Weeks 6–7)

> **Goal**: See how concepts and components combine into real architectures.

### Week 6 — Case Studies (Part 1)

| Day | Case Study | Read From | Key Deep Dive Topic |
|-----|-----------|-----------|---------------------|
| Mon | URL Shortener | `case_studies/` §1 | Key generation strategies, 301 vs 302, caching |
| Tue | Twitter / Feed | `case_studies/` §2 | **Fan-out: push vs pull vs hybrid** (most important!) |
| Wed | Chat (WhatsApp) | `case_studies/` §3 | WebSocket management, message ordering, presence |
| Thu | Rate Limiter | `case_studies/` §4 | Sliding window counter, Redis atomic operations |
| Fri | Google Drive | `case_studies/` §5 | Block-level deduplication, delta sync, versioning |

**Practice**:
- [ ] For each case study, try to design it yourself FIRST, then read the solution
- [ ] Draw the architecture diagram from memory
- [ ] Can you estimate the QPS and storage for each?

### Week 7 — Case Studies (Part 2) + Cross-Cutting

| Day | Case Study | Read From | Key Deep Dive Topic |
|-----|-----------|-----------|---------------------|
| Mon | Notification System | `case_studies/` §6 | Event-driven architecture, priority queues, rate limiting |
| Tue | Web Crawler | `case_studies/` §7 | URL frontier, politeness, deduplication |
| Wed | Ticket Booking | `case_studies/` §8 | **Concurrency: Redis seat locks with TTL** |
| Thu | Key-Value Store | `case_studies/` §9 | Consistent hashing, quorum, vector clocks, anti-entropy |
| Fri | News Feed (Reddit) | `case_studies/` §10 | Ranking algorithm, Redis sorted sets, vote processing |

**Practice**:
- [ ] Which 3 case studies map closest to your interview domain?
- [ ] Can you identify the "key insight" for each case study?
- [ ] What patterns from Week 5 are used in each case study?

---

## Phase 4: Mobile System Design (Weeks 8–9)

> **Goal**: Master mobile-specific architecture for Senior/Lead mobile developer interviews.

### Week 8 — Mobile Architecture & Framework

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Mobile vs Backend Interviews | `mobile/` Guide §1 | Mobile = client architecture, not just server scaling |
| Tue | 7 Evaluation Areas | `mobile/` Guide §1 | Architecture, Networking, Data, Performance, Security, Reliability, Backend |
| Wed | 14-Step Framework | `mobile/` Guide §3 | Memorize: Requirements → Scope → Constraints → Architecture → Mobile Arch → Data Flow → Networking → Offline → Performance → Security → Scalability → Failures → Trade-offs → Testing |
| Thu | Mobile Architecture Deep Dive | `mobile/` Guide §5 | Clean Architecture + BLoC. Layers: UI → BLoC → UseCases → Repository → Remote/Local |
| Fri | Data Flow & Caching | `mobile/` Guide §6 | Cache-first vs Network-first vs Stale-while-revalidate |

**Practice**:
- [ ] Memorize the 14-step framework (can you list all 14 from memory?)
- [ ] Draw the Clean Architecture layer diagram from memory

### Week 9 — Mobile Deep Dives

| Day | Topic | Read From | Key Takeaway |
|-----|-------|-----------|--------------|
| Mon | Network Design | `mobile/` Guide §7 | Timeouts, retries, backoff, **idempotency keys** |
| Tue | Offline Support & Sync | `mobile/` Guide §8 | Sync queue, WorkManager, conflict resolution, eventual consistency |
| Wed | Performance | `mobile/` Guide §9 | 5 categories: startup, UI, network, memory, battery |
| Thu | Security | `mobile/` Guide §10 | OAuth, Keystore, SSL pinning, encryption, never store secrets in app |
| Fri | Backend Awareness + LLD | `mobile/` Guide §11,12 | Know purpose of API GW, Redis, Kafka, CDN. HLD → LLD → Code. |

**Practice**:
- [ ] Read the Mobile Cheat Sheet (`mobile/Mobile_System_Design_Cheat_Sheet.md`)
- [ ] Study 2-3 mobile example designs deeply (`mobile/example_*.md`)

---

## Phase 5: Practice & Mock Interviews (Weeks 10–11)

> **Goal**: Apply the framework under time pressure. This is where theory becomes skill.

### Week 10 — Structured Answer Practice

| Day | Practice | Read From | Time |
|-----|----------|-----------|------|
| Mon | E-Commerce App Answer | `playbook/01` | Read + explain aloud (60 min) |
| Tue | Chat App Answer | `playbook/02` | Read + explain aloud (60 min) |
| Wed | Food Delivery App Answer | `playbook/03` | Read + explain aloud (60 min) |
| Thu | Ride-Sharing App Answer | `playbook/04` | Read + explain aloud (60 min) |
| Fri | Social Media Feed Answer | `playbook/05` | Read + explain aloud (60 min) |

### Week 11 — Mock Interviews (Timed!)

| Day | Mock Interview | How |
|-----|----------------|-----|
| Mon | Design a Video Streaming App | Use `playbook/06` as reference. Time yourself 60 min. |
| Tue | Design a Payment Application | Use `playbook/07` as reference. Time yourself 60 min. |
| Wed | Design an Offline-First App | Use `playbook/10` as reference. Time yourself 60 min. |
| Thu | Design a Udemy-Like App | Use `playbook/12` as reference. Time yourself 60 min. |
| Fri | Pick ANY question from `interview/` §4 | **No reference**. Design from scratch in 60 min. |

**Mock Interview Rules**:
- [ ] Time yourself strictly (60 min)
- [ ] Talk out loud (even alone)
- [ ] Draw diagrams
- [ ] Use the 14-step (mobile) or 5-step (backend) framework
- [ ] After: compare with the reference answer, identify gaps

---

## Phase 6: Polish & Deep Dives (Week 12)

> **Goal**: Fill gaps, go deep on weak spots, build confidence.

### Week 12 — Targeted Improvement

| Day | Activity | Resource |
|-----|----------|----------|
| Mon | Re-read your weakest fundamental topic | `fundamentals/` |
| Tue | Re-read your weakest component | `components/` |
| Wed | Re-read your weakest pattern | `patterns/` |
| Thu | Do 1 final mock interview (backend: any case study) | `case_studies/` |
| Fri | Do 1 final mock interview (mobile: any playbook answer) | `playbook/` |
| Sat | Review anti-patterns + interview communication | `interview/` §6,7 |
| Sun | **Rest. You're ready.** 🎯 | — |

---

## 📋 Study Checklist (Track Your Progress)

### Fundamentals
- [ ] Can explain scalability (vertical vs horizontal)
- [ ] Can explain CAP theorem in own words
- [ ] Can explain PACELC and why it's better than CAP
- [ ] Know the difference between strong and eventual consistency
- [ ] Can explain idempotency and why it matters
- [ ] Know the latency numbers (memory, SSD, HDD, network, cross-continent)

### Components
- [ ] Know L4 vs L7 load balancing
- [ ] Can explain cache-aside vs write-through vs write-back
- [ ] Know cache failure modes (penetration, breakdown, avalanche)
- [ ] Can explain Kafka vs RabbitMQ
- [ ] Know when to use SQL vs NoSQL
- [ ] Can explain sharding vs replication
- [ ] Understand API Gateway responsibilities

### Patterns
- [ ] Can explain consistent hashing with a diagram
- [ ] Know rate limiting algorithms (token bucket, sliding window)
- [ ] Can explain circuit breaker states (closed, open, half-open)
- [ ] Understand CQRS and event sourcing
- [ ] Can explain saga pattern (choreography vs orchestration)
- [ ] Know retry best practices (backoff + jitter + idempotency)

### Estimation
- [ ] Can estimate QPS from DAU
- [ ] Can estimate storage from records/day × size × retention
- [ ] Can estimate cache size (hot data × 20%)
- [ ] Can estimate server count (QPS × peak / RPS per server)
- [ ] Memorized: 1 day ≈ 100K sec, 1 month ≈ 2.5M sec

### Case Studies
- [ ] Designed URL shortener (key generation, caching, 301 vs 302)
- [ ] Designed Twitter (fan-out: push vs pull vs hybrid)
- [ ] Designed Chat (WebSocket, presence, message ordering)
- [ ] Designed Google Drive (block deduplication, delta sync)
- [ ] Designed Ticket Booking (Redis seat locks with TTL)
- [ ] Designed Key-Value Store (consistent hashing, quorum, vector clocks)

### Mobile System Design
- [ ] Memorized the 14-step mobile framework
- [ ] Can draw Clean Architecture layers (UI → BLoC → UseCases → Repository → Remote/Local)
- [ ] Know 7 evaluation areas
- [ ] Can explain cache-first vs network-first vs stale-while-revalidate
- [ ] Can explain idempotency keys for POST requests
- [ ] Can explain offline sync (queue, WorkManager, conflict resolution)
- [ ] Know security: OAuth, Keystore, SSL pinning, encryption at rest
- [ ] Can discuss performance in 5 categories (startup, UI, network, memory, battery)
- [ ] Completed 3+ mock mobile design interviews (60 min each)

### Interview Skills
- [ ] Can follow the 5-step backend framework without notes
- [ ] Can follow the 14-step mobile framework without notes
- [ ] Practice "Propose → Justify → Check" communication pattern
- [ ] Never go silent for more than 30 seconds
- [ ] Always draw diagrams
- [ ] Always discuss trade-offs
- [ ] Always discuss failure scenarios

---

## 📚 Recommended External Reading (After Completing This Roadmap)

| Priority | Book / Resource | Why |
|----------|----------------|-----|
| 🔴 Must | "Designing Data-Intensive Applications" by Martin Kleppmann | The bible of system design. Deep, thorough, practical. |
| 🔴 Must | "System Design Interview" by Alex Xu (Vol 1 & 2) | Interview-focused, visual, practical. |
| 🟡 Recommended | "Database Internals" by Alex Petrov | Deep dive into storage engines, replication, consensus. |
| 🟡 Recommended | Google SRE Book (free online) | Reliability, SLAs, error budgets, incident response. |
| 🟢 Bonus | High Scalability blog (highscalability.com) | Real-world architecture breakdowns. |
| 🟢 Bonus | Engineering blogs: Uber, Netflix, Discord, Cloudflare | Real systems at scale. |

---

## 🎯 After 12 Weeks: What You Should Be Able to Do

### Backend System Design
> Take a question like *"Design Twitter for 300M users"* and confidently spend 45-60 minutes discussing:
> Requirements → Estimation → Architecture → Deep Dive → Bottlenecks → Trade-offs

### Mobile System Design
> Take a question like *"Design a mobile e-commerce app for 10M users"* and confidently spend 45-60 minutes discussing:
> Requirements → Architecture → Flutter/BLoC → APIs → Local DB → Offline sync → Caching → Security → Performance → Scalability → Failure handling → Testing → Trade-offs

**That is the level to target for Senior/Lead Engineer global-market interviews.**

---

## 🔄 The Continuous Learning Loop

```
Learn concept (from this roadmap)
       ↓
Design system (apply to a case study)
       ↓
Build feature (in your own project)
       ↓
Document architecture (write it down)
       ↓
Explain verbally (mock interview)
       ↓
Improve (identify gaps, re-study)
       ↓
Repeat ↺
```

> **Turn your GitHub projects into interview prep.** Every feature you build is a system design story.
