# Fundamentals

Core system design concepts every architect must internalize.

---

## 1. Scalability (Vertical vs Horizontal)

### What is Scalability?
Scalability is a system's ability to handle growing amounts of work — more users, more data, more requests — without degrading performance. A scalable system grows its capacity linearly (or near-linearly) as you add resources.

### Vertical Scaling (Scale Up)
- **Definition**: Add more power (CPU, RAM, disk) to an **existing machine**.
- **Analogy**: Upgrading a truck with a bigger engine to carry more load.
- **Pros**:
  - Simple — no code changes needed.
  - No network overhead (everything on one machine).
  - Easy to reason about (single-node consistency).
- **Cons**:
  - **Hard limit** — there's a maximum machine size you can buy.
  - **Single point of failure** — if the machine dies, the whole system dies.
  - Downtime required to upgrade hardware.
- **When to use**: Small systems, databases (until you outgrow a single node), quick wins.

### Horizontal Scaling (Scale Out)
- **Definition**: Add **more machines** (nodes) to the pool, all working together behind a load balancer.
- **Analogy**: Adding more trucks to a fleet instead of buying a bigger truck.
- **Pros**:
  - **No theoretical limit** — keep adding nodes.
  - **Fault tolerance** — if one node dies, others take over.
  - Can scale elastically (auto-scaling groups in AWS).
- **Cons**:
  - **Complexity** — you now have distributed system problems: consistency, partitioning, network failures.
  - Requires stateless services (or externalized state via shared cache/DB).
  - Load balancing, service discovery, and monitoring become essential.
- **When to use**: Web/application servers, microservices, read replicas, sharded databases.

### Key Insight
> **Stateless services scale horizontally trivially. Stateful services (databases) are hard to scale horizontally — that's where sharding, replication, and partitioning come in.**

---

## 2. Availability & Reliability

### Availability
- **Definition**: The percentage of time a system is operational and accessible.
- **Measured in "nines"**:

| Availability | Downtime/year | Downtime/month |
|---|---|---|
| 99% (two 9s) | 3.65 days | 7.3 hours |
| 99.9% (three 9s) | 8.76 hours | 43.8 min |
| 99.99% (four 9s) | 52.6 min | 4.4 min |
| 99.999% (five 9s) | 5.26 min | 26.3 sec |

- **How to achieve high availability**:
  - **Redundancy** — every component has a backup (active-active or active-passive).
  - **Failover** — automatic switch to backup when primary fails.
  - **Multi-AZ / Multi-region deployment** — survive datacenter or region outages.
  - **Health checks** — detect failures quickly and route traffic away.

### Reliability
- **Definition**: The probability that a system performs correctly for a specified time under specified conditions.
- **MTBF (Mean Time Between Failures)**: How long the system runs before failing.
- **MTTR (Mean Time To Recovery)**: How fast the system recovers after a failure.
- **Reliability = MTBF / (MTBF + MTTR)**

### Key Insight
> **Availability is about being "up." Reliability is about being "correct" while up. A system can be available but unreliable (returns errors). A system can be reliable but unavailable (crashes but never returns wrong data).**

---

## 3. Latency vs Throughput

### Latency
- **Definition**: Time taken for a **single request** to travel from source to destination and back.
- **Measured in**: milliseconds (ms) or microseconds (µs).
- **User perception**:
  - < 100 ms: Feels instant.
  - 100 ms – 1 sec: Noticeable delay.
  - > 1 sec: Feels broken.
- **Sources of latency**: Network, DNS resolution, TLS handshake, server processing, DB query, serialization.

### Throughput
- **Definition**: Number of requests (or bytes) processed **per unit of time**.
- **Measured in**: requests/sec (RPS), transactions/sec (TPS), or bits/sec (bps).
- **How to increase**: More servers, better algorithms, batching, async processing.

### The Relationship
- **They are independent but related.**
- You can have high throughput with high latency (batch processing — process 1M records in 10 minutes).
- You can have low latency with low throughput (a single fast server handling 10 RPS).
- **The goal**: Low latency AND high throughput.

### Little's Law (Queueing Theory)
> **L = λ × W**
> - L = average number of items in the system
> - λ = arrival rate (throughput)
> - W = average time in the system (latency)
>
> This means: if you want to handle more requests (higher λ) without increasing queue length (L), you must reduce processing time (W).

### Key Insight
> **Latency is what the user feels. Throughput is what the system handles. Optimize latency for user-facing APIs; optimize throughput for batch/background jobs.**

---

## 4. Consistency Models

### Strong Consistency
- After a write completes, **all subsequent reads** (from any node) return the latest value.
- **How**: Synchronous replication, distributed consensus (Paxos, Raft).
- **Cost**: Higher latency (must wait for all replicas to acknowledge).
- **Use case**: Banking transactions, inventory, anything where losing or seeing stale data is unacceptable.

### Eventual Consistency
- After a write, reads may return **stale data temporarily**, but eventually all replicas converge.
- **How**: Asynchronous replication.
- **Cost**: Lower latency, higher availability, but stale reads.
- **Use case**: Social media feeds, DNS, shopping cart, "likes" count.

### Weak Consistency
- No guarantee that subsequent reads will return the latest write. Best-effort.
- **Use case**: Real-time multiplayer games (voice chat), live video streaming.

### Read-Your-Writes Consistency
- A special guarantee: after a user writes, their own subsequent reads will see that write.
- **How**: Session affinity, sticky routing, or read-after-write synchronization.
- **Use case**: "I posted a comment but don't see it" — this is a read-your-writes violation.

### Causal Consistency
- Preserves cause-and-effect ordering. If event B is caused by event A, all nodes see A before B.
- **Use case**: Comment threads (you see a reply only after the parent comment).

### Key Insight
> **There is no free lunch. Stronger consistency = lower availability + higher latency. The CAP theorem formalizes this trade-off.**

---

## 5. CAP Theorem

### The Three Properties
- **C — Consistency**: All nodes see the same data at the same time.
- **A — Availability**: Every request receives a non-error response (no guarantee it's the latest data).
- **P — Partition Tolerance**: System continues to operate despite network partitions (communication breaks between nodes).

### The Theorem
> In the presence of a network partition (P), you must choose between Consistency (C) and Availability (A). You can have at most two of the three.

### The Three Combinations
| Choice | System Type | Example | Behavior during partition |
|---|---|---|---|
| CP | Consistency + Partition Tolerance | HBase, MongoDB (default) | Refuses writes/reads to stay consistent |
| AP | Availability + Partition Tolerance | Cassandra, DynamoDB | Accepts reads/writes, may return stale data |
| CA | Consistency + Availability | Traditional RDBMS (single node) | Not possible in distributed systems |

### Why CA is not realistic
> Network partitions **will** happen. You can't prevent them. So in practice, every distributed system is either CP or AP. The question is: **when a partition happens, do you refuse service (CP) or risk stale data (AP)?**

### Key Insight
> **CAP is not about "pick two and forget the third." It's about what happens during a network partition. When there's no partition, you can have all three. The theorem only forces a choice when P occurs.**

---

## 6. PACELC Theorem

### The Problem with CAP
CAP only addresses behavior during a **partition**. But what about **normal operation** (no partition)?

### PACELC
> **If Partition (P): choose between Availability (A) and Consistency (C).**
> **Else (E): choose between Latency (L) and Consistency (C).**

### Examples
| System | During Partition | Normal Operation |
|---|---|---|
| Cassandra | AP | EL (low latency, eventual consistency) |
| MongoDB | CP | EL (low latency, eventual on secondaries) |
| Spanner | CP | EC (strong consistency, higher latency) |
| DynamoDB | AP | EL (configurable) |

### Key Insight
> **Even without failures, there's a trade-off: lower latency often means weaker consistency. Strong consistency requires synchronous coordination, which adds latency. PACELC captures this.**

---

## 7. Stateful vs Stateless Systems

### Stateless
- **Definition**: The server does not retain any client state between requests. Each request contains all information needed to process it.
- **Examples**: REST APIs (with JWT tokens), CDN edge servers.
- **Benefits**:
  - **Horizontally scalable** — any server can handle any request.
  - **Easy failover** — if a server dies, another picks up seamlessly.
  - **No session affinity needed** — load balancer can use round-robin.
- **How state is managed**: Externalized to DB, cache (Redis), or client (JWT).

### Stateful
- **Definition**: The server retains client state across requests.
- **Examples**: WebSocket connections, database primary nodes, session-based auth.
- **Challenges**:
  - **Hard to scale** — must route the same client to the same server (sticky sessions).
  - **Failover is complex** — if the server dies, the state is lost (unless replicated).
  - **Load balancing is harder** — can't just round-robin.

### The Architect's Approach
> **Make your application layer stateless. Push state to dedicated stateful systems (databases, caches, message queues) that are designed to handle it. This gives you the best of both worlds: easy scaling + reliable state.**

### Example Architecture
```
Client → Load Balancer → [Stateless App Server 1, Server 2, Server 3]
                                    ↓
                          ┌─────────┼─────────┐
                       Redis      DB Primary   Kafka
                      (cache)    (write)     (events)
                                    ↓
                              DB Replica (read)
```

---

## 8. Synchronous vs Asynchronous Communication

### Synchronous
- Caller **blocks** and waits for a response.
- **Protocols**: HTTP/REST, gRPC (sync mode), JDBC.
- **Pros**: Simple, immediate feedback, easy error handling.
- **Cons**: Tight coupling, cascading failures, caller wastes resources waiting.
- **Use case**: User-facing API calls where a response is needed immediately.

### Asynchronous
- Caller **fires and forgets** (or gets a callback later).
- **Protocols**: Message queues (Kafka, RabbitMQ, SQS), event-driven.
- **Pros**: Decoupling, resilience (consumer can be down), buffering (handle traffic spikes).
- **Cons**: More complex, harder to debug, eventual consistency.
- **Use case**: Order processing, email sending, notifications, ETL pipelines.

### Key Insight
> **Use sync for user-facing, latency-sensitive operations. Use async for background, decoupled, fault-tolerant processing. Most real systems use both.**

---

## 9. Idempotency

### Definition
An operation is **idempotent** if calling it multiple times has the same effect as calling it once.

### Why it matters
- In distributed systems, network failures cause **retries**. If a retry causes a duplicate side effect (e.g., charging a credit card twice), you have a bug.
- **Idempotency keys**: Client sends a unique ID with each request. Server tracks it and returns the cached result if it's a retry.

### Examples
| Operation | Idempotent? |
|---|---|
| `GET /users/123` | Yes |
| `PUT /users/123 {name: "John"}` | Yes |
| `POST /orders` (create new) | No (by default) |
| `DELETE /users/123` | Yes |
| `POST /payments` with idempotency key | Yes (with key) |

### Key Insight
> **Design every API to be idempotent. In a distributed system, retries are not a possibility — they are a certainty.**

---

## 10. Backpressure

### Definition
When a downstream system is slower than the upstream producer, the producer must **slow down** or **buffer**. This is backpressure.

### Without Backpressure
- Producer keeps sending → queue grows → memory exhaustion → OOM crash → system failure.

### With Backpressure
- **Reactive approach**: Producer detects slow consumer and reduces rate.
- **Buffer approach**: Use a bounded queue; when full, apply a strategy (drop, block, error).
- **Examples**: Kafka consumer lag, reactive streams (Project Reactor, RxJava).

### Key Insight
> **Every queue in your system is a buffer that can overflow. Always have a strategy for what happens when it does: drop, block, shed load, or scale out.**
