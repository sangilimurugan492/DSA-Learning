# Components

Building blocks used in system design. An architect must understand each component's role, trade-offs, and when to use it.

---

## 1. Load Balancers

### What is a Load Balancer?
A load balancer distributes incoming traffic across multiple servers to ensure no single server is overwhelmed. It's the traffic cop of your system.

### Why You Need One
- **Single server can't handle all traffic** — it will run out of CPU, memory, or connections.
- **Fault tolerance** — if one server dies, the LB routes traffic to healthy ones.
- **Zero-downtime deploys** — drain traffic from a server, update it, bring it back.

### Layer 4 (Transport Layer) Load Balancing
- Operates at the **TCP/UDP level** — looks at IP, port, protocol.
- **Does NOT inspect** the HTTP request body, headers, or URL path.
- **Pros**: Very fast (less processing), protocol-agnostic (works for any TCP service).
- **Cons**: Can't make routing decisions based on content (e.g., route `/api/v1` to one service and `/api/v2` to another).
- **Examples**: AWS NLB, HAProxy (L4 mode), IPVS.

### Layer 7 (Application Layer) Load Balancing
- Operates at the **HTTP/HTTPS level** — inspects full request (headers, URL, cookies, body).
- **Can route based on content**: `/api/users` → user-service, `/api/orders` → order-service.
- **Can modify requests**: add/remove headers, rewrite URLs, terminate TLS.
- **Pros**: Content-based routing, TLS termination, rate limiting, authentication.
- **Cons**: More processing overhead (must parse HTTP), slightly higher latency.
- **Examples**: AWS ALB, Nginx, HAProxy (L7 mode), Envoy, Traefik.

### Load Balancing Algorithms

| Algorithm | How it works | Best for |
|---|---|---|
| **Round Robin** | Distributes requests sequentially: A → B → C → A → B → C | Equal-capacity servers |
| **Weighted Round Robin** | Like round robin but servers get weights (powerful server gets more) | Heterogeneous servers |
| **Least Connections** | Sends to the server with fewest active connections | Long-lived connections (WebSocket, DB) |
| **IP Hash** | Hashes client IP → always routes to same server | Sticky sessions without cookies |
| **Random** | Picks a random server | Simple, surprisingly effective at scale |
| **Least Response Time** | Sends to server with lowest avg response time | Latency-sensitive apps |

### Health Checks
- **Active**: LB periodically sends HTTP/TCP probes to each server. If no response → mark unhealthy → stop sending traffic.
- **Passive**: LB watches real request results. If a server returns errors → reduce traffic.
- **Health check types**: HTTP 200 OK, TCP connection success, custom health endpoint (`/health`).

### Session Affinity (Sticky Sessions)
- Some apps need the same client to hit the same server (e.g., in-memory session).
- **How**: LB sets a cookie or hashes the client IP to route consistently.
- **Problem**: If that server dies, the session is lost. **Avoid this by externalizing state.**

### Key Insight
> **A load balancer is the entry point of your system. It should be highly available itself — use redundant LBs with a VIP (virtual IP) and failover. A single LB is a single point of failure.**

---

## 2. Caching

### What is a Cache?
A cache is a **small, fast storage layer** that stores frequently accessed data so future requests are served faster without hitting the slower backing store.

### Why Caching?
- **Latency**: Reading from Redis (~0.25 ms) vs DB (~5-20 ms) — 20-80x faster.
- **Throughput**: Cache can handle 100K+ QPS; a DB might handle 1-5K QPS.
- **Cost**: Offload reads from expensive DB to cheap cache.
- **DB protection**: Reduce DB load so it can handle writes.

### Cache-Aside (Lazy Loading)
```
App → Cache HIT? → Return data
         ↓ MISS
       DB → Write to Cache → Return data
```
- **Pros**: Cache only contains requested data (no wasted space). Cache failure is non-fatal (fall through to DB).
- **Cons**: Cache miss = 2 round trips (cache + DB). Stale data if DB changes but cache isn't updated.
- **Most common pattern**.

### Write-Through
```
App → Write to Cache → Cache writes to DB → Return
```
- **Pros**: Cache is always consistent with DB. No stale reads.
- **Cons**: Higher write latency (must write to both). Cache may store data that's never read (wasted space).

### Write-Back (Write-Behind)
```
App → Write to Cache → Return (async: Cache writes to DB later)
```
- **Pros**: Very fast writes (only one write to cache). Absorbs write bursts.
- **Cons**: **Data loss risk** — if cache crashes before flushing to DB. Complex to implement.
- **Use case**: Write-heavy workloads where some data loss is tolerable (metrics, logs).

### Cache Eviction Policies

| Policy | How it works | Best for |
|---|---|---|
| **LRU** (Least Recently Used) | Evicts the item not accessed for the longest time | General purpose, most common |
| **LFU** (Least Frequently Used) | Evicts the item with fewest accesses | Data with stable popularity |
| **FIFO** (First In First Out) | Evicts oldest item | Simple, time-ordered data |
| **TTL** (Time To Live) | Items expire after a set time | Data that goes stale (news, prices) |
| **Random** | Evicts a random item | Simple, surprisingly effective |

### Cache Problems

#### Cache Penetration (Querying for non-existent data)
- Attacker requests data that doesn't exist in DB or cache → every request hits DB.
- **Solution**: Cache negative results (null) with short TTL. Use a Bloom filter to reject non-existent keys before hitting DB.

#### Cache Breakdown (Hot Key Expiration)
- A very popular cache key expires → thousands of requests hit DB simultaneously.
- **Solution**: Mutex/lock — only one request goes to DB, others wait. Or use "stale-while-revalidate" (serve stale data while refreshing in background).

#### Cache Avalanche (Mass Expiration)
- Many cache keys expire at the same time → DB gets flooded.
- **Solution**: Add random jitter to TTLs so they don't expire simultaneously. Pre-warm cache.

### Multi-Layer Caching
```
Browser Cache → CDN Edge Cache → API Gateway Cache → Application Cache (Redis) → DB
```
Each layer serves requests it can, reducing load on the next layer.

### Key Insight
> **Caching is the single most effective performance optimization in system design. But it introduces the hardest problem: cache invalidation. "There are only two hard things in computer science: cache invalidation and naming things." — Phil Karlton**

---

## 3. Message Queues

### What is a Message Queue?
A message queue is an **asynchronous communication mechanism** where producers send messages to a queue, and consumers process them at their own pace.

### Why Message Queues?
- **Decoupling**: Producer doesn't need to know about consumer. They communicate through the queue.
- **Buffering**: If consumer is slow or down, messages queue up. No data loss.
- **Load leveling**: Smooth out traffic spikes. Queue absorbs the burst; consumer processes at steady rate.
- **Reliability**: Messages persist in the queue until processed. If consumer crashes, message is redelivered.

### Queue vs Pub/Sub

| Pattern | How it works | Example |
|---|---|---|
| **Queue (Point-to-Point)** | Each message is consumed by **exactly one** consumer | Task queue (process order) |
| **Pub/Sub** | Each message is delivered to **all** subscribers | Event notification (new user → email service + analytics service) |

### Kafka vs RabbitMQ vs SQS

| Feature | Kafka | RabbitMQ | SQS |
|---|---|---|---|
| **Model** | Append-only log + consumer groups | Traditional AMQP queue/exchange | Managed queue |
| **Message retention** | Days/weeks (configurable) | Deleted after ack | Deleted after ack |
| **Throughput** | Millions/sec | ~50K/sec | ~3K/sec/batch |
| **Ordering** | Per partition | Per queue | Per FIFO queue |
| **Replay** | Yes (rewind offset) | No | No |
| **Best for** | Event streaming, log aggregation | Task queue, RPC | Simple decoupling |

### Kafka Deep Dive
- **Topics**: Named streams of messages (like a category).
- **Partitions**: A topic is split into partitions for parallelism. Each partition is an ordered, append-only log.
- **Consumer Groups**: Each consumer in a group reads from different partitions → horizontal scaling.
- **Offset**: Consumer's position in a partition. Stored in Kafka, not on the consumer.
- **Replication**: Each partition has replicas. One is the leader (handles reads/writes), others are followers.

```
Topic: "orders" (3 partitions)

Partition 0: [msg1, msg2, msg3, msg4, ...]
Partition 1: [msg5, msg6, msg7, msg8, ...]
Partition 2: [msg9, msg10, msg11, ...]

Consumer Group "processors":
  Consumer A ← Partition 0
  Consumer B ← Partition 1
  Consumer C ← Partition 2
```

### At-Least-Once vs At-Most-Once vs Exactly-Once

| Guarantee | Behavior | Complexity |
|---|---|---|
| **At-most-once** | Message may be lost (send and forget) | Simple |
| **At-least-once** | Message may be duplicated (ack after process) | Medium — consumer must be idempotent |
| **Exactly-once** | Message delivered exactly once | Hard — requires transactional producer + consumer |

### Key Insight
> **Message queues are the shock absorbers of distributed systems. They decouple producers from consumers, absorb traffic spikes, and enable async processing. But they add latency and complexity — use them when you need decoupling or buffering, not for every service-to-service call.**

---

## 4. Databases

### Relational (SQL) Databases
- **Model**: Tables, rows, columns, relationships, foreign keys.
- **Schema**: Rigid, pre-defined. Must migrate to change.
- **ACID Transactions**: Atomic, Consistent, Isolated, Durable.
- **Query**: SQL — powerful, declarative, supports JOINs.
- **Scaling**: Vertical (easier). Horizontal is hard (sharding is complex).
- **Examples**: PostgreSQL, MySQL, Oracle, SQL Server.
- **Use when**: Data is relational, you need transactions, complex queries, strong consistency.

### NoSQL Databases

| Type | Description | Examples | Use case |
|---|---|---|---|
| **Key-Value** | Simple key→value store, O(1) lookups | Redis, DynamoDB, Riak | Session store, cache, user profiles |
| **Document** | Store JSON/BSON documents, flexible schema | MongoDB, CouchDB | Content management, catalogs, user profiles |
| **Column-Family** | Column-oriented, optimized for wide rows | Cassandra, HBase, Bigtable | Time-series, IoT, write-heavy workloads |
| **Graph** | Nodes + edges, optimized for relationships | Neo4j, JanusGraph | Social networks, recommendation engines |

### ACID vs BASE

| ACID (SQL) | BASE (NoSQL) |
|---|---|
| **A**tomic — all or nothing | **B**asically Available — system is available |
| **C**onsistent — always valid state | **S**oft state — state may change without input |
| **I**solated — concurrent txns don't interfere | **E**ventual consistency — converges over time |
| **D**urable — committed writes survive crashes | |

### OLTP vs OLAP

| OLTP (Online Transaction Processing) | OLAP (Online Analytical Processing) |
|---|---|
| Handles day-to-day transactions | Handles complex analytical queries |
| Many short read/write operations | Few long, complex read queries |
| Current operational data | Historical aggregated data |
| Normalized schema | Denormalized (star/snowflake schema) |
| Examples: PostgreSQL, MySQL | Examples: BigQuery, Snowflake, Redshift |

### Database Scaling

#### Read Replicas
- All writes go to **primary**. Reads go to **replicas**.
- **Pros**: Scales reads. Primary is offloaded.
- **Cons**: Replication lag (replica may be slightly behind). Writes still limited to one primary.

#### Sharding (Horizontal Partitioning)
- Split data across multiple databases by a **shard key**.
- **Shard key choice is critical**: bad key = uneven distribution (hot shards) or cross-shard queries.
- **Common strategies**:
  - **Range-based**: Shard 1 = users A-M, Shard 2 = users N-Z. Problem: hot spots (if many users start with 'A').
  - **Hash-based**: `hash(user_id) % N_shards`. Even distribution. Problem: can't range query.
  - **Directory-based**: A lookup service maps keys to shards. Flexible. Problem: the directory is a SPOF.

#### Partitioning vs Sharding
- **Partitioning**: Splitting a table within a **single database** (for manageability).
- **Sharding**: Splitting data across **multiple database servers** (for scale).

### Key Insight
> **Your database choice is the most consequential architectural decision. It's extremely hard to change later. Start with what you know (usually PostgreSQL) and only go NoSQL when you have a clear reason: massive scale, flexible schema, or specific access patterns. Don't use NoSQL just because it's trendy.**

---

## 5. API Gateways

### What is an API Gateway?
An API Gateway is the **single entry point** for all client requests. It sits between clients and your backend services.

### Responsibilities

| Responsibility | Description |
|---|---|
| **Routing** | Route requests to the correct backend service |
| **Authentication** | Verify JWT/API keys before forwarding |
| **Rate Limiting** | Throttle abusive clients |
| **TLS Termination** | Decrypt HTTPS, forward HTTP internally |
| **Request/Response Transformation** | Modify headers, aggregate responses from multiple services |
| **Caching** | Cache responses to reduce backend load |
| **Logging & Monitoring** | Central place for access logs, metrics |
| **Circuit Breaking** | Stop calling failing services, return fallback |

### API Gateway vs Load Balancer
- **Load Balancer**: Distributes traffic across instances of the **same** service. L4/L7.
- **API Gateway**: Routes to **different** services, adds cross-cutting concerns (auth, rate limiting). Always L7.
- In practice, an API Gateway often includes load balancing functionality.

### BFF (Backend for Frontend) Pattern
- Instead of one API Gateway for all clients, have **one per client type** (web, mobile, IoT).
- Each BFF is tailored to its client's needs (different response shapes, different endpoints).
- **Pros**: No over-fetching/under-fetching. Client-specific optimizations.
- **Cons**: More code to maintain.

### Key Insight
> **An API Gateway centralizes cross-cutting concerns so your services don't have to. But don't put business logic in it — it should be a thin layer. If your gateway has complex business rules, you've created a distributed monolith.**

---

## 6. Service Discovery

### The Problem
In a microservices architecture with auto-scaling, services come and go. IP addresses change constantly. How does Service A find Service B?

### Service Registry
- A central registry (e.g., Consul, Eureka, etcd) where services register themselves on startup and deregister on shutdown.
- Clients query the registry to find available instances.

### Client-Side Discovery
```
Service A → Query Registry → Get list of Service B instances → Pick one → Call directly
```
- **Pros**: No intermediary hop. Client has full control of load balancing strategy.
- **Cons**: Client must implement discovery logic. Language-specific libraries needed.

### Server-Side Discovery
```
Service A → Call Load Balancer/Router → Router queries Registry → Router forwards to Service B
```
- **Pros**: Client is simple (just call a URL). Language-agnostic.
- **Cons**: Extra hop through the router. Router is another component to manage.

### In Kubernetes
- Each Service gets a stable virtual IP (ClusterIP) and DNS name.
- KubeDNS resolves the name → ClusterIP.
- kube-proxy on each node load-balances to actual pods.
- Services don't need to know about service discovery — it's built in.

### Key Insight
> **Service discovery is infrastructure, not business logic. Use a managed solution (Kubernetes, Consul, AWS Cloud Map) rather than building your own. The complexity of maintaining a registry, health checking, and failover is significant.**

---

## 7. Proxy / Reverse Proxy

### Forward Proxy (Proxy)
- Sits between the **client and the internet**.
- Client → Proxy → Internet → Server.
- **Purpose**: Anonymity, caching, filtering, access control.
- **The client knows it's using a proxy.**
- **Example**: Corporate proxy, VPN, Tor.

### Reverse Proxy
- Sits between the **internet and your servers**.
- Client → Internet → Reverse Proxy → Server.
- **Purpose**: Load balancing, TLS termination, caching, security, routing.
- **The client does NOT know about the proxy** — thinks it's talking to the server directly.
- **Examples**: Nginx, HAProxy, AWS ALB, Cloudflare.

### CDN (Content Delivery Network) as a Reverse Proxy
- A CDN is a globally distributed network of reverse proxy servers (edge nodes).
- Static content (images, CSS, JS) is cached at edge nodes close to users.
- **How it works**:
  1. User in India requests `image.png`.
  2. DNS resolves to the nearest CDN edge (Singapore).
  3. Edge node has the image cached → returns immediately.
  4. If not cached → fetches from origin server → caches → returns.

### Key Insight
> **A reverse proxy is one of the most versatile components in your architecture. Nginx can be a reverse proxy, load balancer, cache, TLS terminator, and API gateway — all in one. Start simple with a reverse proxy in front of your servers; it solves many problems before they arise.**

---

## 8. CDN (Content Delivery Network)

### What is a CDN?
A geographically distributed network of servers that caches content close to users, reducing latency and origin load.

### Why CDN?
- **Latency**: User in India → edge in Mumbai (~5 ms) vs origin in US (~200 ms).
- **Origin protection**: 70-90% of requests served from cache → origin handles only 10-30%.
- **DDoS mitigation**: CDN absorbs traffic spikes before they reach origin.

### Push vs Pull CDN

| Type | How content gets to edge | Best for |
|---|---|---|
| **Push** | Origin pushes content to edge nodes on update | Small, known content (CSS, JS, images) |
| **Pull** | Edge fetches from origin on first request, then caches | Large, dynamic content, long-tail content |

### Cache Invalidation
- **TTL-based**: Content expires after N seconds/minutes.
- **Purge**: Origin tells edge to remove specific content (API call to CDN provider).
- **Versioned URLs**: `/app.v2.css` instead of `/app.css` — new version = new URL = no stale cache.

### Key Insight
> **A CDN is the cheapest performance win in system design. If you have users in multiple geographies and you're not using a CDN, you're leaving 100-300 ms of latency on the table for every request. Cloudflare's free tier has no excuse.**

---

## 9. Connection Pooling

### The Problem
- Opening a TCP connection + DB handshake = 20-50 ms per connection.
- If you open a new connection per request, at 1000 RPS you spend 20-50 seconds just on connections.
- Databases have a **max connection limit** (e.g., PostgreSQL default = 100). Exceed it → connection refused.

### The Solution: Connection Pool
- Maintain a pool of N open connections. Reuse them across requests.
- **How it works**:
  1. App starts → opens N connections to DB → keeps them open.
  2. Request comes in → borrow a connection from pool → execute query → return to pool.
  3. No connection setup/teardown per request.

### Pool Sizing
- Too small: Requests wait for a free connection → latency spike.
- Too large: DB gets overwhelmed → context switching overhead.
- **Rule of thumb**: `pool_size = (core_count × 2) + effective_spindle_count` (HikariCP formula).

### Key Insight
> **Connection pooling is not optional — it's mandatory for any production system. But the pool size must be tuned. A pool of 100 connections to a 4-core DB server will actually be SLOWER than a pool of 8, because the DB spends all its time context-switching between connections.**

---

## 10. Distributed Tracing & Observability

### The Three Pillars of Observability

| Pillar | What it answers | Example |
|---|---|---|
| **Metrics** | "Is something wrong?" (aggregate) | CPU at 90%, error rate 5% |
| **Logs** | "What happened?" (discrete events) | "User 123 checkout failed: payment declined" |
| **Traces** | "Where is it slow?" (request journey) | Request took 500 ms: 400 ms in DB, 80 ms in cache |

### Distributed Tracing
- In a microservices architecture, one user request may touch 5-10 services.
- A **trace ID** is propagated through all services via headers.
- Each service records a **span** (start time, end time, operation name).
- The trace is assembled into a timeline showing where time was spent.

```
Trace: a1b2c3d4
├─ API Gateway (0ms - 5ms)
├─ Auth Service (5ms - 15ms)
├─ Order Service (15ms - 200ms)
│   ├─ DB Query (20ms - 180ms)  ← BOTTLENECK
│   └─ Cache Lookup (180ms - 190ms)
└─ Notification Service (200ms - 210ms)
```

### Key Insight
> **You can't fix what you can't see. In a distributed system, observability is not a nice-to-have — it's a requirement. Invest in metrics, logs, and traces from day one. Retrofitting observability into a running system is 10x harder.**
