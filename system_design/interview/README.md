# Interview

System design interview checklists, templates, and practice questions. This guide prepares you to think and communicate like an architect under pressure.

---

## 1. What Interviewers Are Evaluating

### It's NOT About the "Right" Answer
System design interviews have no single correct answer. The interviewer is evaluating:

| Skill | What They Look For | Red Flag |
|---|---|---|
| **Problem scoping** | Do you ask clarifying questions? | Jumping straight to solution |
| **Structured thinking** | Do you follow a framework? | Random, disorganized approach |
| **Trade-off analysis** | Do you discuss pros/cons of decisions? | Picking a technology without justification |
| **Scalability awareness** | Do you consider scale, bottlenecks, failure modes? | Ignoring failure scenarios |
| **Communication** | Can you explain complex ideas clearly? | Going silent for 10 minutes |
| **Depth** | Can you go deep on at least one component? | Staying at surface level only |
| **Pragmatism** | Do you choose appropriate tech, not over-engineer? | Using Kafka for a 100 QPS system |

### The Hidden Rubric
```
Strong Hire:    Structured, deep, discusses trade-offs, handles edge cases, communicates clearly
Hire:           Structured, covers main points, some depth, decent communication
No Hire:        Unstructured, surface-level, no trade-offs, poor communication
Strong No Hire: Can't design a basic system, no understanding of scale, goes silent
```

---

## 2. The 5-Step Framework (45-Minute Interview)

### Step 1: Clarify Requirements (5-7 minutes)

**Goal**: Understand the problem before solving it. Never start designing without scoping.

#### Ask These Questions
1. **What are the functional requirements?** (What does the system do?)
   - "What features do we need to support?"
   - "Who are the users?"
   - "What are the core use cases?"

2. **What are the non-functional requirements?** (How does the system behave?)
   - "What's the expected scale? (DAU, QPS)"
   - "Is it read-heavy or write-heavy?"
   - "What's the latency requirement?"
   - "Do we need strong consistency or is eventual OK?"
   - "What's the availability target?"

3. **What's the scope?** (What's in and what's out?)
   - "Do we need to design the full system or just the core?"
   - "Do we need authentication, analytics, etc.?"
   - "What clients? (Web, mobile, API)"

#### Example: URL Shortener
```
You: "What's the expected scale?"
Interviewer: "100M new URLs per month, 10B redirections."
You: "So it's read-heavy, 100:1 read:write. What's the latency requirement?"
Interviewer: "Redirection should be under 50ms."
You: "Do URLs expire?"
Interviewer: "Optional. Let's say yes, configurable TTL."
You: "Do we need analytics? Click tracking?"
Interviewer: "Not for now. Focus on core functionality."
```

#### Write Down the Requirements
- Summarize on the whiteboard/screen so the interviewer can see you've understood.
- **Functional**: Shorten URL, redirect, custom alias (optional)
- **Non-functional**: HA, < 50ms redirect, 100:1 read:write, 10-year retention

### Step 2: Back-of-the-Envelope Estimation (3-5 minutes)

**Goal**: Use numbers to guide your architecture. This is where you show you can think at scale.

#### What to Estimate
1. **QPS** (reads and writes separately)
2. **Storage** (per day, per year, retention period)
3. **Bandwidth** (if relevant)
4. **Cache size** (if read-heavy)
5. **Server count** (rough)

#### Example
```
"Let me do a quick estimation to understand the scale:
- 100M writes/month → ~40 writes/sec (avg), ~120/sec (peak)
- 10B reads/month → ~4,000 reads/sec (avg), ~12,000/sec (peak)
- Storage: 100M × 12 × 10 years × 500 bytes = 6 TB
- This tells me: read-heavy (needs caching), small storage (one DB is fine), 
  but we need read replicas for 12K reads/sec."
```

#### Why This Matters
- If you estimate 500K QPS, the interviewer knows you need caching + read replicas + sharding.
- If you estimate 500 QPS, a single server is fine — don't over-engineer.
- **The estimation drives the architecture.**

### Step 3: High-Level Design (10-12 minutes)

**Goal**: Draw the box diagram. Show the main components and data flow.

#### Start Simple
```
Client → Load Balancer → App Server → Database
```

Then add components as needed:
```
Client → CDN → API Gateway → [App Server] → Cache (Redis)
                                        → DB (Primary + Replicas)
```

#### Draw It Out
- Use the whiteboard or drawing tool.
- Label every component.
- Show data flow with arrows.
- Mark which components are read vs write.

#### Discuss API Design
- Define the main API endpoints:
  ```
  POST /api/shorten    { long_url: "..." } → { short_url: "..." }
  GET  /{short_key}    → 301/302 Redirect
  ```

#### Discuss Data Model
- Sketch the main tables:
  ```
  URL Table:
    short_key    VARCHAR(7) PK
    long_url     TEXT
    user_id      BIGINT
    created_at   TIMESTAMP
    expires_at   TIMESTAMP
  ```

### Step 4: Deep Dive (15-20 minutes)

**Goal**: Go deep on the most interesting/challenging part. This is where you earn the "strong hire."

#### How to Choose What to Deep Dive
- The interviewer will often point you: "Let's talk about the key generation."
- If not, pick the most interesting/hard part: caching strategy, sharding, consistency, concurrency.
- **Don't try to deep dive everything** — you'll run out of time. Pick 1-2 areas.

#### Deep Dive Topics by Problem Type

| Problem Type | Deep Dive Area |
|---|---|
| URL Shortener | Key generation, caching, 301 vs 302 |
| Twitter | Fan-out strategy (push vs pull vs hybrid) |
| Chat | WebSocket management, message ordering, presence |
| Rate Limiter | Algorithm choice, distributed rate limiting |
| Google Drive | Block storage, deduplication, sync |
| Ticket Booking | Concurrency control, seat locking |
| Key-Value Store | Consistent hashing, replication, conflict resolution |

#### Present Multiple Options
For each deep dive, present 2-3 approaches, discuss trade-offs, then pick one:

```
"For key generation, I see three approaches:

1. Hash + Base62: Simple, but collision risk.
2. Pre-generated keys (KGS): No collisions, but KGS is a SPOF.
3. Counter + Base62: Simple, but guessable and SPOF.

I'd go with approach 2 (KGS) because it avoids collisions and is 
straightforward to make highly available. The trade-off is maintaining 
the key pool, but that's a simpler problem than handling collisions."
```

#### Discuss Failure Modes
- "What if Redis goes down?" → Fall through to DB (cache-aside).
- "What if the primary DB goes down?" → Promote a replica.
- "What if a shard is overloaded?" → Rebalance or add a shard.

### Step 5: Wrap Up & Identify Bottlenecks (3-5 minutes)

**Goal**: Show you can critically evaluate your own design.

#### Summarize
- "Let me summarize the design: Client → CDN → API Gateway → App → Redis → DB (sharded). 
  Keys are pre-generated by KGS. Reads are cached with 80% hit rate. 
  DB is sharded by short_key hash."

#### Identify Bottlenecks
- "Potential bottlenecks:
  1. KGS is a SPOF → make it HA with multiple instances.
  2. Cache miss storm on hot URL → stale-while-revalidate.
  3. DB write throughput → if we exceed single primary, shard by key."

#### Discuss Future Improvements
- "If we need to scale further:
  1. Multi-region deployment for global latency.
  2. Analytics pipeline (Kafka → ClickHouse) for click tracking.
  3. Custom aliases (requires a different key strategy)."

---

## 3. Communication Strategy

### Think Out Loud
- The interviewer can't see your thoughts. Narrate your reasoning.
- "I'm considering whether to use SQL or NoSQL here. The data is relational (users, URLs, clicks), so I'll start with PostgreSQL. If we need flexible schema later, we can add a document store."

### Use the "Propose → Justify → Check" Pattern
1. **Propose**: "I'll use Redis for caching."
2. **Justify**: "Because the system is read-heavy (100:1), and Redis gives us 100K QPS with sub-millisecond latency."
3. **Check**: "Does that make sense, or would you like me to consider an alternative?"

### Ask for Feedback
- "Does this approach make sense?"
- "Should I go deeper on the caching strategy or the sharding?"
- "Is this the level of detail you're looking for?"

### Manage Time
- 45 minutes goes fast. Don't spend 20 minutes on requirements.
- If you're running low on time, say: "I know we're running low on time. Let me quickly sketch the high-level design and then deep dive on the most interesting part."

### Use Visual Aids
- Draw boxes and arrows. A picture is worth 1000 words.
- Label everything. Don't assume the interviewer knows what "Service A" does.

---

## 4. Common System Design Questions

### Beginner Level
1. Design a URL shortener (TinyURL)
2. Design a pastebin
3. Design a key-value store
4. Design a rate limiter
5. Design a unique ID generator (Snowflake)

### Intermediate Level
6. Design Twitter / social media feed
7. Design a chat system (WhatsApp)
8. Design a notification system
9. Design a web crawler
10. Design an autocomplete / search suggestion system
11. Design a distributed cache
12. Design a news feed (Reddit)
13. Design a parking lot system

### Advanced Level
14. Design Google Drive (distributed file storage)
15. Design YouTube (video streaming)
16. Design a ticket booking system (BookMyShow)
17. Design Google Maps (routing)
18. Design a distributed message queue (Kafka)
19. Design a search engine (Elasticsearch)
20. Design a CDN
21. Design Uber (ride matching)
22. Design Netflix (streaming + recommendations)
23. Design a distributed lock service (ZooKeeper)
24. Design a log aggregation system
25. Design a multi-region database

---

## 5. Technology Decision Guide

### When to Use What

| Need | Technology | Why |
|---|---|---|
| Relational data, ACID | PostgreSQL | Mature, feature-rich, strong consistency |
| Flexible schema, documents | MongoDB | No schema migration, JSON-native |
| Massive write throughput | Cassandra | Write-optimized, linear scalability |
| Key-value cache | Redis | Sub-ms latency, 100K QPS |
| Full-text search | Elasticsearch | Inverted index, fuzzy search |
| Event streaming | Kafka | High throughput, replay, durability |
| Task queue | RabbitMQ / SQS | Routing, retries, dead letter queues |
| Object storage | S3 | Unlimited, cheap, durable |
| Time-series data | InfluxDB / TimescaleDB | Optimized for time-based queries |
| Graph data | Neo4j | Relationship traversal |
| Analytics (OLAP) | BigQuery / ClickHouse | Columnar, fast aggregations |

### Don't Name-Drop Without Justification
```
BAD:   "I'll use Kafka, Redis, Cassandra, and Kubernetes."
GOOD:  "I'll use Redis for caching because we need sub-ms reads at 50K QPS. 
       For the event pipeline, Kafka makes sense because we need replay capability 
       and high throughput. For the database, PostgreSQL is sufficient at this scale 
       — we don't need Cassandra yet."
```

### The "Start Simple" Principle
- Start with the simplest architecture that works.
- Add complexity only when the estimation justifies it.
- "I'll start with a single PostgreSQL instance. Based on the estimation (5K QPS), this is sufficient. If we need to scale reads, I'll add read replicas. If we need to scale writes, I'll shard."

---

## 6. Anti-Patterns: What NOT to Do

### 1. Jumping to Solution Without Scoping
```
BAD:  "So I'll use a load balancer, then app servers, then Kafka, then Cassandra..."
GOOD: "Before I design, let me clarify the requirements. What's the expected scale?"
```

### 2. Over-Engineering
```
BAD:  "I'll use 5 Kafka clusters, 3 Cassandra datacenters, and a custom consensus algorithm."
GOOD: "At 500 QPS, a single PostgreSQL with a read replica is sufficient. 
       I'll add complexity only if the scale demands it."
```

### 3. Name-Dropping Without Justification
```
BAD:  "I'll use Cassandra because it's NoSQL."
GOOD: "I'll use PostgreSQL because the data is relational and we need ACID. 
       If we outgrow a single node, we can shard or move to Cassandra."
```

### 4. Ignoring Failure Modes
```
BAD:  "The app writes to Redis and returns."
GOOD: "The app writes to Redis. If Redis is down, we fall through to the DB 
       (cache-aside). If the DB is down, we return a 503 and queue the write 
       for retry."
```

### 5. Going Silent
- The worst thing you can do is go silent for 5 minutes.
- Even if you're thinking, narrate: "I'm thinking about whether to shard by user_id or by timestamp. User_id gives even distribution but makes range queries hard. Timestamp gives range queries but creates hot shards..."

### 6. Not Drawing
- A system design interview without a diagram is a missed opportunity.
- Draw the architecture. Label components. Show data flow.
- The interviewer wants to see your mental model externalized.

### 7. Ignoring the Interviewer's Hints
- If the interviewer says "What about failure?" → they're hinting you should discuss failure modes.
- If they say "Can we scale this?" → they want you to discuss sharding/replication.
- **Listen and adapt.**

---

## 7. The 5-Minute Checklist (Quick Reference)

Before you start designing, run through this checklist:

```
□ Functional requirements clarified?
□ Non-functional requirements clarified? (scale, latency, consistency)
□ Read:write ratio estimated?
□ QPS estimated?
□ Storage estimated?
□ High-level diagram drawn?
□ API endpoints defined?
□ Data model sketched?
□ Deep dive area chosen?
□ Failure modes discussed?
□ Bottlenecks identified?
□ Trade-offs explained?
```

---

## 8. Mock Interview Walkthrough: Design Twitter (Condensed)

### Minute 0-5: Requirements
```
You: "What's the scale?"
Interviewer: "300M MAU, 150M DAU."
You: "What features? Tweet, follow, timeline, search?"
Interviewer: "Yes, all of those."
You: "Is it read-heavy?"
Interviewer: "Yes, timeline reads are 20x more than tweets."
You: "Consistency requirements?"
Interviewer: "Eventual consistency is fine. A tweet can take a few seconds 
to appear in all timelines."
```

### Minute 5-10: Estimation
```
You: "Let me estimate:
- 75M tweets/day → 870 writes/sec
- 1.5B timeline views/day → 17K reads/sec
- Read:write = 20:1 → heavy caching needed
- Storage: 75M × 500B = 37.5 GB/day text, plus media
- This tells me: need Redis for timeline cache, shard the tweet DB, 
  and use a CDN for media."
```

### Minute 10-25: High-Level Design + Deep Dive
```
You: [Draws diagram: Client → API Gateway → Tweet Service → Tweet DB (sharded)
                                        → Timeline Service → Redis (timeline cache)
                                        → Social Graph Service → Graph DB
                                        → Search Service → Elasticsearch]

You: "The key design decision is timeline generation. I see three approaches:
1. Pull (fan-out on read): Too slow at 17K QPS.
2. Push (fan-out on write): Fast reads, but celebrity problem.
3. Hybrid: Push for normal users, pull for celebrities.

I'll go with hybrid. For users with <100K followers, push tweet to their 
timeline cache. For celebrities, don't push — pull their tweets on read."
```

### Minute 25-35: Deep Dive on Fan-Out
```
You: "Let me go deeper on the fan-out:
- When user A tweets, the Tweet Service writes to DB, then publishes to Kafka.
- Timeline Service consumes the event, looks up A's followers, and pushes 
  the tweet_id to each follower's Redis sorted set (timeline:{user_id}).
- For celebrities, skip the push. When a user opens their timeline, 
  the Timeline Service merges the pre-computed cache with recent celebrity tweets.

Failure modes:
- If Redis is down → fall back to pull model (slower but functional).
- If Kafka is down → tweets are in DB but not fanned out. Process when Kafka recovers.
- If a shard is down → that shard's tweets are temporarily unavailable."
```

### Minute 35-40: Bottlenecks & Wrap-Up
```
You: "Bottlenecks:
1. Celebrity fan-out → solved with hybrid model.
2. Hot tweets → cache aggressively, use stale-while-revalidate.
3. Timeline cache size → keep top 1000 tweets per user in Redis.
4. Search lag → Elasticsearch is eventually consistent (few seconds).

Future improvements:
1. Multi-region deployment for global users.
2. Personalized ranking (ML model for timeline ordering).
3. Media deduplication for storage savings."
```

---

## 9. Key Principles to Remember

### 1. Start Simple, Scale When Needed
> "A system that works is better than a system that's perfectly designed but never built."

### 2. Every Decision Has a Trade-Off
> "There are no solutions, only trade-offs." — Thomas Sowell
- Strong consistency → lower availability + higher latency.
- Caching → faster reads but stale data risk.
- Sharding → write scaling but cross-shard queries are hard.
- Microservices → independent deployment but operational complexity.

### 3. Design for Failure
> "Everything fails all the time." — Werner Vogels
- Every component will fail. Design for it.
- What if the cache dies? What if the DB primary dies? What if a network partition occurs?
- **Redundancy + failover + graceful degradation.**

### 4. Know Your Numbers
> "If you can't estimate, you can't design."
- Memorize latency numbers, throughput numbers, and powers of two.
- Do back-of-the-envelope estimation before designing.
- Let the numbers guide your architecture.

### 5. Communicate, Communicate, Communicate
> "The best design is worthless if you can't explain it."
- Think out loud.
- Draw diagrams.
- Ask for feedback.
- Summarize at the end.

### 6. Depth Over Breadth
> "Know one thing deeply rather than everything shallowly."
- Pick one area and go deep.
- Show you understand the internals, not just the buzzwords.
- "I'll use Redis" is shallow. "I'll use Redis with cache-aside, LRU eviction, and stale-while-revalidate for hot keys" is deep.

### 7. Be Pragmatic
> "Engineering is about making things work within constraints."
- Don't use Kafka for 100 QPS.
- Don't shard a 10 GB database.
- Don't build microservices for a 3-person team.
- **Choose the right tool for the job, not the trendiest one.**

---

## 10. Preparation Roadmap

### Week 1-2: Fundamentals
- Read this entire `system_design` folder.
- Understand every concept in `fundamentals/` and `patterns/`.
- Memorize the numbers in `estimation/`.

### Week 3-4: Case Studies
- Read every case study in `case_studies/`.
- For each, try to design it yourself first, then compare.
- Practice drawing the diagrams on paper or a whiteboard.

### Week 5-6: Mock Interviews
- Do 2-3 mock interviews per week.
- Use the 5-step framework every time.
- Time yourself: 45 minutes per problem.
- Practice explaining out loud, even if alone.

### Week 7-8: Deep Dives
- Pick 3-4 areas and go deep:
  - Caching strategies (cache-aside, write-through, write-back, eviction, invalidation)
  - Database sharding (shard key, re-sharding, cross-shard queries)
  - Message queues (Kafka internals, delivery guarantees, ordering)
  - Consistency models (CAP, PACELC, quorum, vector clocks)
- Read engineering blogs (Uber, Netflix, Discord, Twitter) for real-world examples.

### Recommended Reading
- "Designing Data-Intensive Applications" by Martin Kleppmann (the bible)
- "System Design Interview" by Alex Xu (Volumes 1 & 2)
- "Building Microservices" by Sam Newman
- "Database Internals" by Alex Petrov
- Google SRE Book (free online)
- High Scalability blog (highscalability.com)
- Engineering blogs: Uber Engineering, Netflix TechBlog, Discord Blog, Cloudflare Blog

### Key Insight
> **System design is not memorizing architectures — it's developing the judgment to make good trade-offs under constraints. The best architects don't know the "right" answer; they know how to evaluate options, communicate trade-offs, and choose pragmatically. Practice the framework, internalize the numbers, and learn to think out loud. That's the skill.**
