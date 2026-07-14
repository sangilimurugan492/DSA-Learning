# Patterns

Common system design patterns and techniques. These are the tools an architect uses to solve scaling, consistency, and reliability problems.

---

## 1. Sharding / Partitioning

### What is Sharding?
Sharding is the process of **splitting a large dataset across multiple database servers** so that each server holds only a subset of the data. This allows horizontal scaling of storage and compute.

### Why Shard?
- **Data too large for one machine**: A single PostgreSQL instance can't hold 10 TB of data efficiently.
- **Write throughput**: A single primary can handle ~5K writes/sec. If you need 50K writes/sec, you need multiple primaries → shard.
- **Memory pressure**: Working set doesn't fit in RAM → disk I/O → slow. Sharding reduces working set per node.

### Shard Key — The Most Critical Decision
The shard key determines **which shard** a record goes to. Choosing the wrong key is the most expensive mistake in sharding.

#### Good Shard Key Properties
1. **High cardinality**: Many distinct values → even distribution.
2. **Even distribution**: No hot shard that gets 80% of traffic.
3. **Monotonicity avoidance**: Don't use auto-increment IDs (all new writes go to the last shard → hot shard).
4. **Query locality**: Queries that access the same shard (avoid cross-shard queries/JOINs).

#### Shard Key Strategies

| Strategy | How | Pros | Cons |
|---|---|---|---|
| **Range-based** | Shard by value ranges (A-M → Shard 1) | Efficient range queries | Hot spots (everyone named "A") |
| **Hash-based** | `hash(key) % N` | Even distribution | No range queries. Re-sharding is hard |
| **Directory-based** | Lookup table maps key → shard | Flexible, easy re-shard | Directory is a SPOF (must be HA) |
| **Geo-based** | Shard by geography (US data → US shard) | Data locality, compliance | Uneven distribution (more US users) |

### Re-sharding Problem
- When you add a new shard, you must redistribute data.
- **Hash-based**: `hash(key) % N` changes to `hash(key) % (N+1)` → almost all data moves. Terrible.
- **Solution: Consistent Hashing** (see below).

### Cross-Shard Queries
- A query that needs data from multiple shards (e.g., "find all users who signed up today").
- **Problem**: Must query all shards, merge results → slow and complex.
- **Solution**: Denormalize or use a secondary index service (Elasticsearch).

### Key Insight
> **Sharding is a one-way door. Once you shard, it's extremely hard to un-shard or change the shard key. Think carefully about your shard key before you shard. A bad shard key will haunt you forever.**

---

## 2. Replication

### What is Replication?
Replication is the process of **copying data from a primary database to one or more replicas** so that reads can be distributed and data is redundant for fault tolerance.

### Why Replicate?
- **Read scaling**: Offload reads to replicas → primary handles only writes.
- **High availability**: If primary dies, promote a replica.
- **Geographic locality**: Replicate data to regions closer to users.
- **Backup/Analytics**: Run heavy analytical queries on replicas, not primary.

### Master-Slave (Primary-Replica) Replication
```
                    ┌── Replica 1 (read)
Primary (write) ────┼── Replica 2 (read)
                    └── Replica 3 (read)
```
- All writes go to primary. Primary replicates to replicas.
- **Synchronous**: Primary waits for replica ACK before returning to client. Safe but slow.
- **Asynchronous**: Primary returns immediately, replicates in background. Fast but may lose data on primary crash.
- **Semi-synchronous**: Primary waits for at least one replica ACK. Balance of safety and speed.

### Master-Master (Multi-Primary) Replication
```
Primary A ←──→ Primary B
```
- Both nodes accept writes. Changes replicate bidirectionally.
- **Pros**: Write scaling. Either primary can handle writes.
- **Cons**: **Write conflicts** — what if both primaries update the same row simultaneously? Conflict resolution is hard (last-write-wins, application-level merge, CRDTs).
- **Use case**: Multi-region write availability (rare). Most systems avoid this.

### Quorum-Based Replication
- With N replicas, a write must be acknowledged by **W** replicas (write quorum).
- A read must contact **R** replicas (read quorum).
- **W + R > N** guarantees that any read overlaps with the last write → strong consistency.
- **W + R ≤ N** → eventual consistency (faster, but may read stale data).

```
Example: N=5, W=3, R=3
- Write: 3 of 5 replicas must confirm → write succeeds.
- Read: 3 of 5 replicas are contacted → at least 1 has the latest data (since W=3, R=3, 3+3=6 > 5).
```

### Replication Lag
- The delay between a write to primary and that write appearing on a replica.
- **Causes**: Network latency, replica load, large transactions.
- **Impact**: Reading from replica may return stale data.
- **Mitigation**: Read-after-write consistency (route to primary for a short window after user's own write).

### Key Insight
> **Replication solves read scaling and availability. It does NOT solve write scaling — all writes still go to one primary. For write scaling, you need sharding. Replication + sharding together solve both.**

---

## 3. Consistent Hashing

### The Problem
With naive hash-based sharding (`hash(key) % N`), adding or removing a shard changes the mapping for **almost all keys** — because N changes. This means massive data movement.

### The Solution: Consistent Hashing
- Imagine a **ring** (hash space from 0 to 2^32 - 1).
- Each server is placed on the ring at position `hash(server_ip)`.
- Each key is placed on the ring at position `hash(key)`.
- A key is assigned to the **first server clockwise** from its position on the ring.

```
       0
      / \
   Server A   Server C
     |         |
   Server B
      \ /
     2^32
```

### What Happens When a Server is Added/Removed?
- **Add Server D**: Only keys between D's position and the previous server's position move to D. All other keys stay put.
- **Remove Server A**: Only keys that were on A move to the next server (B). All other keys stay.
- **Result**: Only `1/N` of keys move, not all keys.

### Virtual Nodes
- Problem: With few servers, distribution is uneven (one server may get 50% of the ring).
- Solution: Each physical server maps to **multiple virtual nodes** (e.g., 150-200) on the ring.
- This smooths out the distribution → each server gets approximately equal share.

### Where It's Used
- **Cassandra**: Partitioning across nodes.
- **DynamoDB**: Partitioning.
- **Memcached/Redis Cluster**: Key distribution.
- **CDN edge routing**: Route to nearest cache.

### Key Insight
> **Consistent hashing is the algorithm that makes distributed caching and NoSQL databases possible. Without it, adding a node to a 100-node cluster would require re-hashing all data. With it, only ~1% of data moves. This is the difference between a system that scales and one that doesn't.**

---

## 4. Bloom Filters

### What is a Bloom Filter?
A Bloom filter is a **space-efficient probabilistic data structure** that tells you whether an element is **possibly in a set** or **definitely not in a set**.

### How It Works
1. Start with a **bit array** of m bits, all set to 0.
2. Use **k independent hash functions**.
3. To **add** an element: hash it with all k functions → set those k bits to 1.
4. To **check** an element: hash with all k functions → if **all** k bits are 1 → "possibly in set." If **any** bit is 0 → "definitely not in set."

```
Bit array: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

Add "apple":
  h1("apple") = 3  → set bit 3
  h2("apple") = 7  → set bit 7
  h3("apple") = 10 → set bit 10

Bit array: [0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0]

Check "apple": bits 3, 7, 10 are all 1 → "possibly in set" ✓
Check "banana": h1=2, h2=5, h3=8 → bit 2 is 0 → "definitely not in set" ✓
```

### Properties
- **False positives**: Possible (bits may be set by other elements). Can be reduced with larger bit array and more hash functions.
- **False negatives**: **Impossible** — if the filter says "not in set," it's definitely not.
- **Cannot delete**: You can't unset bits (other elements may have set them). Use a **Counting Bloom Filter** for deletions.
- **Space**: 10 MB bit array can store 1 million URLs with 1% false positive rate.

### Use Cases
- **Cache penetration**: Check Bloom filter before DB. If "definitely not" → return 404 without hitting DB.
- **Malicious URL detection**: Check if URL is in blocklist.
- **Distributed joins**: Filter rows that definitely won't match before sending across network.
- **Cassandra**: Checks if an SSTable might contain a key before doing disk I/O.

### Key Insight
> **Bloom filters are the "fast no" of system design. When 99% of queries are for non-existent data, a Bloom filter eliminates 99% of DB lookups. It's a tiny data structure with outsized impact.**

---

## 5. Rate Limiting

### What is Rate Limiting?
Rate limiting restricts the number of requests a client can make in a given time window. It protects your system from abuse, DDoS, and resource exhaustion.

### Why Rate Limit?
- **Protect the system**: Prevent a single client from consuming all resources.
- **Fairness**: Ensure no client starves others.
- **Cost control**: API calls cost money (LLM APIs, cloud APIs).
- **Security**: Brute-force protection (login attempts).

### Algorithms

#### Token Bucket
- Bucket holds **N tokens**. Each request consumes 1 token.
- Tokens are refilled at rate **R tokens/sec**.
- If bucket is empty → request is rejected (429 Too Many Requests).
- **Pros**: Allows bursts (up to N tokens available). Smooth average rate.
- **Cons**: Burst can exceed the steady-state rate.
- **Use case**: API rate limiting (Stripe, AWS).

#### Leaky Bucket
- Bucket holds **N requests**. Requests pour in, leak out at rate **R requests/sec**.
- If bucket overflows → request rejected.
- **Pros**: Strict rate (no bursts). Smooths traffic.
- **Cons**: Bursts are not allowed (even if system could handle them).
- **Use case**: When you need a strict, smooth output rate.

#### Sliding Window
- Track requests in a sliding time window (e.g., last 60 seconds).
- Count requests in the window. If > limit → reject.
- **Pros**: Precise — no boundary issues (unlike fixed window).
- **Cons**: More memory (must track timestamps of all requests in window).
- **Optimization**: Use a **sliding window counter** — approximate with a weighted combination of current and previous fixed windows.

#### Fixed Window
- Count requests in fixed time windows (e.g., 12:00-12:01, 12:01-12:02).
- **Problem**: Boundary burst — 2x traffic at the boundary (100 requests at 12:00:59 + 100 at 12:01:01 = 200 in 2 seconds).

### Where to Rate Limit
```
Client → CDN (rate limit) → API Gateway (rate limit) → App (rate limit) → DB
```
- **CDN/Edge**: Block DDoS at the edge (Cloudflare, AWS WAF).
- **API Gateway**: Per-client rate limiting (API keys, JWT).
- **Application**: Per-user, per-resource rate limiting (business logic).

### Distributed Rate Limiting
- If you have multiple API Gateway instances, they must share rate limit state.
- **Solution**: Store counters in Redis. Each gateway increments and checks atomically (using `INCR` + `EXPIRE`).
- **Challenge**: Redis adds latency. For ultra-low latency, use local rate limiting with periodic sync (approximate but fast).

### Key Insight
> **Rate limiting is not just about rejecting requests — it's about protecting your system's availability. A system without rate limiting is one bad client away from a cascading failure. Always rate limit at the edge, before requests reach your application.**

---

## 6. Circuit Breaker

### The Problem: Cascading Failures
- Service A calls Service B. Service B is slow (DB is overloaded).
- Service A waits for Service B → holds connections/threads.
- More requests pile up → Service A runs out of threads → Service A is now down.
- Service C calls Service A → same thing happens → Service C is down.
- **The entire system fails because one service was slow.**

### The Solution: Circuit Breaker
A circuit breaker wraps a remote call and monitors failures. It has three states:

```
         ┌──────────────────────────────────┐
         ▼                                  │
    ┌─────────┐  failures > threshold  ┌─────────┐
    │ CLOSED  │ ──────────────────────▶│  OPEN   │
    │ (normal)│                        │ (reject)│
    └─────────┘                        └────┬────┘
         ▲                                  │
         │  success                         │ timeout
         │                                  ▼
         │                            ┌───────────┐
         └────────────────────────────│ HALF-OPEN │
                                      │ (probe)   │
                                      └───────────┘
```

### States
1. **CLOSED**: Normal operation. Requests flow through. Failures are counted.
2. **OPEN**: Failure threshold exceeded. All requests are **immediately rejected** (return fallback/error). No calls to the failing service. This gives the service time to recover.
3. **HALF-OPEN**: After a timeout, allow **one** request through as a probe. If it succeeds → CLOSE. If it fails → OPEN again.

### Configuration
- **Failure threshold**: e.g., 5 failures in 10 seconds → open.
- **Open timeout**: e.g., 30 seconds before trying half-open.
- **Fallback**: Return cached data, default value, or a graceful degradation.

### Key Insight
> **A circuit breaker is the circuit breaker in your electrical panel — it trips to prevent a fire. In software, it trips to prevent a cascading failure. Without circuit breakers, one slow service can take down your entire system. With them, the failure is contained.**

---

## 7. CQRS & Event Sourcing

### CQRS (Command Query Responsibility Segregation)

#### Traditional (CRUD)
- Same model handles reads and writes. One database, one API.
- Works fine until reads and writes have very different patterns.

#### CQRS
- **Split the model**: One model for writes (commands), another for reads (queries).
- Write side: Optimized for validation, business rules, normalization.
- Read side: Optimized for queries, denormalized, pre-computed.

```
Client → Command → Write Model (DB) → [Event] → Read Model (denormalized DB)
Client → Query → Read Model → Return
```

#### When to Use CQRS
- Read/write ratio is very skewed (100:1).
- Read and write models need different schemas (write = normalized, read = denormalized for UI).
- Different scaling needs (1 write node, 10 read replicas).

#### When NOT to Use CQRS
- Simple CRUD apps. CQRS adds complexity. Don't use it unless you need it.

### Event Sourcing

#### Traditional State Storage
- Store the **current state** of an entity. Each update overwrites the previous state.
- You lose history. You can't answer "what was the balance on Tuesday?"

#### Event Sourcing
- Store **every state-changing event** as an immutable log. The current state is derived by replaying events.
- **Example**: Instead of `balance = 100`, store:
  ```
  Event 1: AccountCreated (balance=0)
  Event 2: Deposited (amount=50)
  Event 3: Withdrawn (amount=20)
  Event 4: Deposited (amount=70)
  → Current balance = 0 + 50 - 20 + 70 = 100
  ```

#### Benefits
- **Full audit log**: Every change is recorded. Never lose history.
- **Time travel**: Reconstruct state at any point in time.
- **Event replay**: If you change your read model, replay all events to rebuild it.
- **Decoupling**: Other services can consume the same events.

#### Challenges
- **Eventual consistency**: Read model lags behind write model.
- **Schema evolution**: Events are immutable, but your schema changes. How to handle old events?
- **Complexity**: Replaying events, snapshotting (don't replay 10 years of events).

### Key Insight
> **CQRS + Event Sourcing is powerful but complex. It's the architecture of systems that need full audit trails, time travel, and independent read/write scaling (banking, insurance, ERP). For most apps, it's overkill. Use it when the business value of event history justifies the complexity.**

---

## 8. Leader Election

### The Problem
In a distributed system with multiple replicas, you often need **exactly one** node to act as the leader (primary). Examples:
- Only the primary accepts writes.
- Only one scheduler runs cron jobs.
- Only one instance processes a queue.

### How Leader Election Works
1. All nodes register with a coordination service (ZooKeeper, etcd, Consul).
2. Nodes compete to create an ephemeral lock node (e.g., `/leader`).
3. The node that successfully creates the lock → becomes leader.
4. Other nodes watch the lock. If the leader dies → the ephemeral node disappears (session timeout) → other nodes compete again.

### Algorithms

#### Raft (used by etcd, Consul)
- **Term-based**: Time is divided into terms. Each term has at most one leader.
- **RequestVote**: Candidates ask peers for votes. Majority wins.
- **AppendEntries**: Leader sends heartbeats. If a follower doesn't receive heartbeats within timeout → it starts a new election.
- **Split vote prevention**: Randomized election timeouts → only one node starts an election first.

#### Paxos (used by Chubby, Spanner)
- More general but harder to understand and implement.
- Multi-Paxos is the practical version (optimizes for stable leader).

#### Bully Algorithm
- Nodes have IDs. The node with the highest ID becomes leader.
- Simple but not fault-tolerant (if the highest-ID node is flapping, leadership keeps changing).

### Split Brain
- **The worst failure**: Two nodes both think they're the leader.
- **Cause**: Network partition — the two sides can't see each other, each elects its own leader.
- **Prevention**: **Quorum** — leader must have majority. If you have 5 nodes, you need 3 to agree. In a 2-3 split, the side with 3 can elect a leader; the side with 2 cannot.

### Key Insight
> **Leader election is fundamental to distributed systems. Without it, you'd have multiple primaries accepting conflicting writes. The key insight is: always require a majority quorum for leadership. A leader without a majority is not a leader — it's a split brain waiting to happen.**

---

## 9. Write-Ahead Log (WAL)

### What is a WAL?
A Write-Ahead Log is a technique where **every change is written to an append-only log BEFORE it's applied to the main data store**.

### Why WAL?
- **Durability**: If the system crashes, the log can be replayed to recover state.
- **Replication**: The log can be shipped to replicas (they apply the same log in order).
- **Atomicity**: A transaction is committed when its log entry is flushed to disk. Even if the data page write hasn't happened, the log guarantees the commit.

### How It Works
```
1. Write request arrives
2. Append change to WAL (on disk, fsync)
3. Apply change to in-memory data structure
4. Return success to client
5. Eventually, flush in-memory changes to main data files (checkpoint)
```

### Recovery
```
1. System restarts after crash
2. Read WAL from last checkpoint
3. Replay each log entry → reconstruct in-memory state
4. System is now consistent
```

### Where It's Used
- **PostgreSQL**: WAL (pg_wal) for crash recovery and replication.
- **MySQL**: Binlog for replication. InnoDB redo log for crash recovery.
- **Kafka**: Each partition is a WAL. Consumers read the log.
- **Redis**: AOF (Append-Only File) is a WAL.
- **etcd/Consul**: Raft log is a WAL.

### Key Insight
> **The WAL is the source of truth, not the data files. The data files are just an optimization — a materialized view of the WAL. This is why databases can recover from crashes: the WAL survived, so the state can be reconstructed.**

---

## 10. Saga Pattern

### The Problem: Distributed Transactions
- In a monolith, a database transaction spans multiple tables atomically (ACID).
- In microservices, an operation may span multiple services, each with its own database. You can't do a 2-phase commit (too slow, blocks resources).
- **Example**: Place order → deduct payment → reserve inventory → ship. If shipping fails, you must undo payment and inventory.

### Saga Pattern
A saga is a sequence of **local transactions**, each in a different service. If any step fails, **compensating transactions** undo the previous steps.

```
1. Order Service: Create Order (PENDING)
2. Payment Service: Charge Card
3. Inventory Service: Reserve Items
4. Shipping Service: Create Shipment

If step 4 fails:
4. Shipping Service: Fail
3. Inventory Service: Release Items (compensate)
2. Payment Service: Refund Card (compensate)
1. Order Service: Mark Order FAILED (compensate)
```

### Coordination Approaches

#### Choreography (Event-Driven)
- Each service publishes events. Other services listen and react.
- No central coordinator. Fully decoupled.
- **Pros**: No SPOF, naturally async.
- **Cons**: Hard to track the full flow. Debugging is difficult ("which event triggered what?").

#### Orchestration (Central Coordinator)
- An orchestrator service tells each service what to do and in what order.
- **Pros**: Clear flow, easy to debug, centralized error handling.
- **Cons**: Orchestrator is a SPOF. Tighter coupling.

### Key Insight
> **In a distributed system, you cannot have ACID transactions across services. The saga pattern is the alternative: eventual consistency with compensating actions. The key is to design compensations carefully — they must be idempotent and handle edge cases (e.g., what if the refund fails?).**

---

## 11. Bulkhead Pattern

### What is a Bulkhead?
Named after ship bulkheads (watertight compartments), this pattern **isolates resources** so that a failure in one part of the system doesn't take down everything.

### How It Works
- Divide your thread pool, connection pool, or resources into **separate pools per service or per tenant**.
- If Service A is slow and consumes all its threads → only Service A is affected. Service B still has its own threads and continues working.

```
Without Bulkhead:
  [Shared Thread Pool: 100 threads]
  Service A is slow → consumes 100 threads → Service B, C, D all blocked

With Bulkhead:
  [Pool A: 30 threads] [Pool B: 30 threads] [Pool C: 40 threads]
  Service A is slow → consumes 30 threads → Service B, C still work fine
```

### Key Insight
> **A shared resource pool is a shared failure domain. Bulkheads prevent one slow dependency from consuming all resources. Combined with circuit breakers, bulkheads make your system resilient to partial failures.**

---

## 12. Retry with Exponential Backoff & Jitter

### The Problem
- A request fails (network blip, service restart). You retry.
- If you retry immediately → the service is still recovering → fails again.
- If all clients retry at the same time → "thundering herd" → service is overwhelmed again.

### Exponential Backoff
- Wait time increases exponentially: 1s, 2s, 4s, 8s, 16s...
- Gives the service time to recover.

### Jitter
- Add **randomness** to the wait time: `wait = base * 2^attempt + random(0, 1000ms)`.
- Prevents all clients from retrying at the exact same moment (thundering herd).

### Retry Best Practices
- **Max retries**: Don't retry forever (3-5 attempts).
- **Retry only on transient failures**: Network timeout → retry. 400 Bad Request → don't retry.
- **Idempotency**: Retrying a non-idempotent operation (e.g., charge card) is dangerous. Use idempotency keys.
- **Circuit breaker integration**: If circuit is open, don't retry — fail fast.

### Key Insight
> **Retries without backoff and jitter are a DDoS attack on your own system. Retries with exponential backoff + jitter are a resilience pattern. The difference is just a few lines of code, but it's the difference between a self-healing system and a self-destroying one.**
