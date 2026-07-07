# Case Studies

End-to-end system design problems and solutions. Each case study follows the architect's framework: requirements → estimation → high-level design → deep dive → bottlenecks.

---

## 1. URL Shortener (TinyURL)

### Problem
Design a service that takes a long URL and returns a short URL. When the short URL is visited, redirect to the long URL.

### Step 1: Requirements

#### Functional
- Given a long URL, return a much shorter URL.
- Given a short URL, redirect to the long URL.
- Short URLs should not be guessable (security).
- Links expire after a configurable time (optional).

#### Non-Functional
- **High availability** — if the service is down, all shortened links break.
- **Low latency** — redirection must be fast (< 50 ms).
- **Read-heavy** — 100:1 read-to-write ratio.
- **Durability** — shortened URLs must not be lost.

### Step 2: Back-of-the-Envelope Estimation

#### Assumptions
- 100 million new URLs per month.
- 10 billion redirections per month (100:1 read:write).
- URLs are retained for 10 years.

#### Write QPS
- 100M / 30 days / 86400 sec ≈ **40 writes/sec** (avg), ~200/sec (peak).

#### Read QPS
- 10B / 30 days / 86400 sec ≈ **4,000 reads/sec** (avg), ~20,000/sec (peak).

#### Storage (10 years)
- 100M URLs/month × 12 × 10 = 12 billion URLs.
- Each URL record: ~500 bytes (long URL + short URL + metadata + user ID + timestamp).
- 12B × 500 bytes = **6 TB** (easily fits on a single machine, but we'll shard for availability).

#### Bandwidth
- Write: 40/sec × 500 bytes = 20 KB/sec (negligible).
- Read: 4,000/sec × 500 bytes = 2 MB/sec (manageable).

### Step 3: High-Level Design

```
Client → API Gateway → [Write Service] → Key Generator → DB (long_url, short_key)
                      → [Read Service] → Cache (Redis) → DB → 301 Redirect
```

### Step 4: Deep Dive

#### Short URL Format
- `https://tiny.url/{7-char-key}`
- 7 characters from `[a-zA-Z0-9]` = 62^7 = 3.5 trillion combinations. Enough for 12 billion URLs.

#### Key Generation Strategies

**Approach 1: Hash + Encoding (MD5 → Base62)**
- `MD5(long_url + user_id)` → 128-bit hash → encode first 7 chars in Base62.
- **Problem**: Hash collisions (must handle with retry).
- **Problem**: Same long URL → same short key (may be desired or not).

**Approach 2: Pre-generated Keys (Key Generation Service)**
- A standalone service generates random 7-char keys and stores them in a pool.
- When a URL needs shortening, the write service picks a key from the pool.
- **Pros**: No collisions, no coordination between app servers.
- **Cons**: Key pool must be maintained. If the KGS dies, new URLs can't be created (use HA + multiple pools).

**Approach 3: Counter-based (Auto-increment → Base62)**
- A global counter increments. Each new URL gets the next counter value, encoded in Base62.
- **Problem**: Single counter is a SPOF. Counter value is guessable (security).
- **Solution**: Use ZooKeeper/etcd for distributed counter. Add randomization.

#### Redirection: 301 vs 302
- **301 (Permanent)**: Browser caches the redirect → subsequent visits don't hit TinyURL. Faster for user, but you lose analytics (can't count clicks).
- **302 (Temporary)**: Browser always hits TinyURL → you can track clicks. Slightly slower (extra round trip).
- **Choice**: Use 302 for analytics. Use 301 for pure performance.

#### Caching
- Hot URLs (recently created, trending) should be in Redis.
- Cache hit ratio target: 80-90%.
- **Cache-aside**: Check Redis → miss → DB → populate cache → return.
- **Eviction**: LRU with TTL (e.g., 24 hours).

#### Data Model
```
URL Table:
  short_key    VARCHAR(7) PRIMARY KEY
  long_url     TEXT NOT NULL
  user_id      BIGINT
  created_at   TIMESTAMP
  expires_at   TIMESTAMP (nullable)
```

#### Sharding
- 6 TB over 10 years → can fit on one machine, but for availability:
- Shard by `short_key` hash → distribute across N database nodes.
- Each shard has read replicas.

### Step 5: Bottlenecks & Trade-offs
- **Single point of failure**: KGS (Key Generation Service) → make it HA with multiple instances + separate key ranges.
- **Cache miss storm**: If a popular URL is evicted from cache → DB gets hammered → use stale-while-revalidate.
- **Abuse**: Malicious URLs → integrate with a URL safety check service (Google Safe Browsing API).

### Key Insight
> **The URL shortener looks simple but teaches: read-heavy systems need caching, key generation is a distributed systems problem, and 301 vs 302 is a business decision (analytics vs performance).**

---

## 2. Twitter / Social Media Feed

### Problem
Design a Twitter-like system where users can post tweets, follow other users, and see a timeline of tweets from people they follow.

### Step 1: Requirements

#### Functional
- Post a tweet (text, image, video).
- Follow / unfollow a user.
- View home timeline (tweets from followed users, reverse chronological).
- View user timeline (tweets from a specific user).
- Search tweets.

#### Non-Functional
- **High availability** — system must not go down.
- **Low latency** — timeline generation < 200 ms.
- **Eventual consistency** — a tweet may take a few seconds to appear in all followers' timelines.
- **Read-heavy** — timeline reads >> tweet writes.

### Step 2: Estimation

#### Assumptions
- 300 million monthly active users (MAU).
- 150 million daily active users (DAU).
- Average user posts 0.5 tweets/day → 75 million tweets/day.
- Average user follows 200 people.
- Average user views timeline 10 times/day → 1.5 billion timeline views/day.

#### QPS
- Write: 75M / 86400 ≈ **870 tweets/sec** (avg), ~5,000/sec (peak).
- Read: 1.5B / 86400 ≈ **17,000 timeline reads/sec** (avg), ~100,000/sec (peak).

#### Storage
- Tweet: 500 bytes (text) + 1 MB (image, 20% of tweets) + 5 MB (video, 10% of tweets).
- 75M tweets/day × (0.7 × 500B + 0.2 × 1MB + 0.1 × 5MB) ≈ 75M × 0.7 MB ≈ **50 GB/day**.
- 10 years: 50 GB × 365 × 10 ≈ **180 TB** (media) + 14 TB (text).

### Step 3: High-Level Design

```
Client → API Gateway → [Tweet Service] → Tweet DB (sharded)
                    → [Timeline Service] → Timeline Cache (Redis)
                    → [User Service] → User DB
                    → [Social Graph Service] → Graph DB (follows)
                    → [Search Service] → Elasticsearch
                    → [Media Service] → S3 / CDN
```

### Step 4: Deep Dive

#### The Core Problem: Timeline Generation

**Approach 1: Pull Model (Fan-out on Read)**
- When user opens timeline: query all followees' recent tweets → merge → sort → return.
- **Problem**: User follows 200 people → 200 DB queries per timeline view → too slow at 17K QPS.
- **Verdict**: Too slow for read-heavy systems.

**Approach 2: Push Model (Fan-out on Write)**
- When user posts a tweet: write the tweet to **all followers' timeline caches** (pre-computed lists in Redis).
- Timeline read: just read the user's pre-computed list from Redis → O(1).
- **Problem**: Celebrity with 50 million followers → 50 million cache writes per tweet → "celebrity problem."
- **Verdict**: Great for normal users. Terrible for celebrities.

**Approach 3: Hybrid Model (What Twitter Actually Uses)**
- **Normal users (< 100K followers)**: Fan-out on write → push tweet to all followers' timeline caches.
- **Celebrities (> 100K followers)**: Don't fan-out. Instead, when a celebrity tweets, store it. When a user opens their timeline, pull the celebrity's recent tweets and merge with the pre-computed cache.
- **Result**: Best of both worlds — fast reads for normal users, no write explosion for celebrities.

#### Data Model
```
Tweet Table (sharded by tweet_id):
  tweet_id     BIGINT PRIMARY KEY
  user_id      BIGINT
  content      TEXT
  media_urls   JSON
  created_at   TIMESTAMP

User Table:
  user_id      BIGINT PRIMARY KEY
  username     VARCHAR
  email        VARCHAR
  created_at   TIMESTAMP

Follow Table (sharded by follower_id):
  follower_id  BIGINT
  followee_id  BIGINT
  created_at   TIMESTAMP
  PRIMARY KEY (follower_id, followee_id)

Timeline Cache (Redis sorted set per user):
  key: timeline:{user_id}
  value: sorted set of (tweet_id, score=created_at)
  max: 1000 most recent tweets
```

#### Sharding
- **Tweets**: Shard by `tweet_id` (hash-based). Even distribution.
- **Follows**: Shard by `follower_id` — so all of a user's followees are on the same shard (fast timeline generation for pull model).
- **Timeline cache**: Redis cluster, sharded by `user_id`.

#### Media Handling
- Images/videos uploaded to S3.
- CDN serves media to users.
- Different sizes/resolutions pre-generated (thumbnail, medium, original).
- Video: HLS streaming (adaptive bitrate).

#### Search
- Tweets indexed in Elasticsearch (inverted index for full-text search).
- Async pipeline: tweet posted → Kafka → Elasticsearch indexer.
- Search is eventually consistent (few seconds lag).

### Step 5: Bottlenecks & Trade-offs
- **Celebrity fan-out**: Solved with hybrid model.
- **Hot tweets**: A viral tweet gets millions of reads → cache aggressively.
- **Eventual consistency**: A tweet may not appear in all followers' timelines instantly — acceptable for social media.
- **Timeline cache size**: Keep only top 1000 tweets per user in Redis. Older tweets fetched from DB on scroll.

### Key Insight
> **The Twitter problem is the canonical "fan-out" problem. The key architectural decision is push vs pull vs hybrid. The hybrid model — push for normal users, pull for celebrities — is a beautiful example of optimizing for the common case while handling the edge case. This pattern appears everywhere: notification systems, news feeds, activity streams.**

---

## 3. Chat / Messaging System (WhatsApp)

### Problem
Design a chat system where users can send 1:1 messages, group messages, and see online status.

### Step 1: Requirements

#### Functional
- 1:1 chat (text, image, video).
- Group chat (up to 200 members).
- Online/offline status (presence).
- Message delivery confirmation (delivered, read).
- Message history (last 30 days).

#### Non-Functional
- **Low latency** — messages must appear in < 500 ms.
- **High availability** — chat must always work.
- **Eventual consistency** — OK if message order is slightly off in group chat.
- **Ordering** — messages within a conversation must be ordered.

### Step 2: Estimation

#### Assumptions
- 500 million DAU.
- Average user sends 50 messages/day → 25 billion messages/day.
- Average group size: 10 members.
- 20% of messages are group messages.

#### QPS
- Write: 25B / 86400 ≈ **290K messages/sec** (avg), ~1M/sec (peak).
- Read: ~2x writes (each message read by recipient) ≈ **580K reads/sec**.

#### Storage
- Message: 200 bytes (text) + 1 MB (media, 10% of messages).
- 25B × (0.9 × 200B + 0.1 × 1MB) ≈ 25B × 0.1 MB ≈ **2.5 TB/day**.
- 30 days retention: 75 TB.

### Step 3: High-Level Design

```
Client (WebSocket) → Chat Gateway → [Message Service] → Message DB (sharded)
                                 → [Presence Service] → Redis (online status)
                                 → [Notification Service] → Push (APNs/FCM)
                                 → [Media Service] → S3 / CDN
```

### Step 4: Deep Dive

#### Connection Management: WebSocket
- Chat requires **real-time, bidirectional** communication. HTTP polling is too slow and wasteful.
- **WebSocket**: Persistent TCP connection, full-duplex. Client and server can push messages at any time.
- **Connection per user**: 500M DAU → 500M open WebSocket connections.
- **Connection servers**: Each server handles ~50K-100K connections. Need 5,000-10,000 servers.
- **Sticky routing**: A user's WebSocket connection is pinned to one server. Messages for that user are routed to that server.

#### Message Flow
```
1. User A sends message to User B
2. Chat Gateway receives message via WebSocket
3. Message Service:
   a. Assign message ID + sequence number
   b. Write to Message DB (sharded by conversation_id)
   c. Publish to Kafka (for notifications, analytics)
4. Chat Gateway looks up User B's connection server (from Presence Service)
5. If User B is online → push message via WebSocket to User B's server → User B
6. If User B is offline → send push notification (APNs/FCM)
7. User B acknowledges → Message Service marks as delivered
```

#### Message Ordering
- Within a conversation, messages must be ordered.
- **Solution**: Each conversation has a monotonically increasing sequence number. The Message Service assigns it.
- **Problem with distributed sequence**: If the Message Service is sharded, who assigns the sequence?
- **Solution**: Use the shard for that conversation as the sequence generator. One conversation → one shard → consistent ordering.

#### Presence (Online/Offline Status)
- When a user connects via WebSocket → mark as online in Redis (`presence:{user_id} = online`).
- When the WebSocket disconnects → mark as offline.
- **Heartbeat**: Client sends a ping every 30 seconds. If no ping for 60 seconds → mark offline.
- **Presence fan-out**: When User A comes online, notify all of A's contacts who are online. This is expensive — use a pub/sub channel per user.

#### Group Chat
- Message is written once to the DB (keyed by `conversation_id`).
- Message is fanned out to all group members' connection servers.
- **Read receipts**: Each member's read status is tracked separately.
- **Optimization**: Don't fan-out to offline members — they'll get the message when they reconnect (pull model).

#### Data Model
```
Message Table (sharded by conversation_id):
  message_id       BIGINT (snowflake ID)
  conversation_id  BIGINT (shard key)
  sender_id        BIGINT
  content          TEXT
  media_url        VARCHAR (nullable)
  sequence_num     BIGINT (per-conversation sequence)
  created_at       TIMESTAMP
  status           ENUM (sent, delivered, read)

Conversation Table:
  conversation_id  BIGINT PRIMARY KEY
  type             ENUM (direct, group)
  created_at       TIMESTAMP

Conversation_Member Table:
  conversation_id  BIGINT
  user_id          BIGINT
  joined_at        TIMESTAMP
  last_read_seq    BIGINT (for unread count)
```

#### Sharding
- Shard by `conversation_id` → all messages in a conversation are on the same shard.
- This enables: ordered reads, efficient pagination, no cross-shard queries for a conversation.

### Step 5: Bottlenecks & Trade-offs
- **500M WebSocket connections**: Massive connection management. Use connection draining, auto-scaling, and graceful reconnection.
- **Presence fan-out**: For users with many contacts, going online triggers many notifications. Batch and throttle.
- **Message delivery guarantees**: At-least-once delivery. Client must deduplicate by `message_id`.
- **Global distribution**: Users in different regions → deploy chat servers in multiple regions. Route by geography.

### Key Insight
> **Chat systems are fundamentally different from request-response systems. They are connection-oriented (WebSocket), stateful (the connection is the state), and push-based (server pushes to client). The hardest part is managing millions of persistent connections efficiently. The key architectural decision is: how do you route a message from sender to receiver when both have persistent connections on different servers?**

---

## 4. Rate Limiter

### Problem
Design a distributed rate limiting service that can be used by multiple APIs to enforce rate limits per client.

### Step 1: Requirements

#### Functional
- Limit requests per client (API key, user ID, IP) per time window.
- Support different limits per API endpoint.
- Return 429 with `Retry-After` header when limit is exceeded.
- Configurable limits (e.g., 1000 req/hour for free tier, 10000 req/hour for pro).

#### Non-Functional
- **Low latency** — rate limit check must add < 1 ms.
- **High availability** — if rate limiter is down, do you fail open (allow) or fail closed (deny)?
- **Eventually consistent** — OK if limit is slightly off in distributed environment.

### Step 2: High-Level Design

```
Client → API Gateway → Rate Limiter Middleware → Backend Service
                         ↓
                      Redis (counters)
```

### Step 3: Deep Dive

#### Algorithm: Sliding Window Counter
- Maintain a counter for the current window and the previous window.
- **Estimated count** = `current_count + previous_count × (1 - elapsed_time_in_current_window / window_size)`.
- If estimated count > limit → reject.
- This is an approximation but very memory-efficient and accurate within ~0.003%.

#### Redis Implementation
```
# Key: rate_limit:{client_id}:{window_start}
# Value: request count

INCR rate_limit:user123:2024010112    # increment counter
EXPIRE rate_limit:user123:2024010112 3600  # expire after 1 hour
# If result > limit → return 429
```

#### Distributed Rate Limiting
- Multiple API Gateway instances share Redis.
- **Atomicity**: Use Redis `INCR` (atomic) + `EXPIRE`.
- **Race condition**: Between `INCR` and `EXPIRE`, the key could expire. Use a Lua script to make it atomic:
  ```lua
  local current = redis.call('INCR', KEYS[1])
  if current == 1 then
      redis.call('EXPIRE', KEYS[1], ARGV[1])
  end
  return current
  ```

#### Fail Open vs Fail Closed
- **Fail open**: If Redis is down, allow all requests. Risk: system gets overwhelmed. But: users aren't blocked.
- **Fail closed**: If Redis is down, reject all requests. Risk: system is unavailable. But: system is protected.
- **Choice**: Most systems **fail open** for user experience, but use a local fallback limiter (in-memory) as a safety net.

### Key Insight
> **Rate limiting is a cross-cutting concern that belongs at the edge (API Gateway / middleware), not in your business logic. The key trade-off is accuracy vs latency: Redis-based is accurate but adds 1 ms; local in-memory is fast but approximate. Use a hybrid: local limiter for fast rejection, Redis for accurate enforcement.**

---

## 5. Distributed File Storage (Google Drive)

### Problem
Design a file storage system where users can upload, download, sync, and share files across devices.

### Step 1: Requirements

#### Functional
- Upload/download files (any type, up to 5 GB).
- Sync files across devices (change on one device → appears on others).
- Share files/folders with other users.
- File versioning (see and restore previous versions).
- Offline support (queue changes, sync when online).

#### Non-Functional
- **Reliability** — data must not be lost (durability is critical).
- **Sync latency** — changes should propagate in < 10 seconds.
- **Bandwidth efficiency** — only sync changed blocks, not entire files.

### Step 2: Estimation

#### Assumptions
- 100 million users.
- Average user stores 10 GB.
- Total storage: 1 EB (exabyte).
- 10 million active users/day.
- Average file size: 1 MB.

### Step 3: High-Level Design

```
Client (Desktop/Mobile/Web) → API Gateway → [Upload Service] → S3 (block storage)
                                          → [Metadata Service] → Metadata DB
                                          → [Sync Service] → Notification → Client
                                          → [Block Service] → S3
                                          → [Share Service] → Metadata DB
```

### Step 4: Deep Dive

#### File Upload (Block-based)
- Don't upload the entire file. Split into **blocks** (e.g., 4 MB each).
- Hash each block. Upload only blocks that have changed.
- **Deduplication**: If two users upload the same file, blocks with the same hash are stored only once.

```
File: "document.pdf" (10 MB)
  Block 1: hash=abc123, 4MB → S3
  Block 2: hash=def456, 4MB → S3
  Block 3: hash=ghi789, 2MB → S3

Metadata: file_id → [block1_hash, block2_hash, block3_hash]
```

#### Sync Flow
```
1. Client modifies file → computes changed blocks → uploads to S3
2. Client updates Metadata Service with new block list
3. Metadata Service publishes event: "file X changed"
4. Sync Service notifies all devices of this user
5. Other devices download only the changed blocks
```

#### Versioning
- Each file version is a list of block hashes.
- Version 1: [block1, block2, block3]
- Version 2: [block1, block4, block3] (only block2 changed to block4)
- **Storage efficient**: unchanged blocks are shared between versions.

#### Data Model
```
File Table:
  file_id        BIGINT PRIMARY KEY
  owner_id       BIGINT
  name           VARCHAR
  type           ENUM (file, folder)
  parent_id      BIGINT (folder hierarchy)
  created_at     TIMESTAMP
  updated_at     TIMESTAMP

File_Version Table:
  version_id     BIGINT PRIMARY KEY
  file_id        BIGINT
  block_hashes   JSON (ordered list of block hashes)
  size           BIGINT
  created_at     TIMESTAMP

Block Table:
  block_hash     VARCHAR PRIMARY KEY
  s3_key         VARCHAR (location in S3)
  size           BIGINT
  ref_count      INT (for garbage collection)

Share Table:
  file_id        BIGINT
  shared_with    BIGINT
  permission     ENUM (view, edit)
```

#### Durability
- S3 provides 11 nines durability (data is replicated across multiple AZs).
- Block-level deduplication means each block is stored once but referenced by many files.
- **Garbage collection**: When a block is no longer referenced by any file version, delete it from S3.

### Step 5: Bottlenecks & Trade-offs
- **Large files**: 5 GB file → 1280 blocks. Upload in parallel for speed. Resume on failure.
- **Conflict resolution**: Two devices edit the same file simultaneously → last-write-wins or application-level merge.
- **Metadata DB**: Must be highly available and consistent. Shard by `user_id`.
- **Notification**: Use long polling or WebSocket to push sync notifications to clients.

### Key Insight
> **The key insight of Google Drive is block-level deduplication. Instead of storing files, store blocks. This gives you: deduplication (same block stored once), delta sync (only changed blocks transferred), and versioning (versions share unchanged blocks). This is how you achieve exabyte-scale storage efficiently.**

---

## 6. Notification System

### Problem
Design a notification system that supports multiple channels (email, SMS, push), different priorities, and rate limiting per user.

### Step 1: Requirements

#### Functional
- Send notifications via email, SMS, and push.
- Support templated notifications (welcome email, order confirmation).
- User preferences (opt-out of certain types).
- Rate limiting (don't spam users — max 10 notifications/day).
- Scheduled notifications (send at a specific time).
- Retry on failure.

#### Non-Functional
- **At-least-once delivery** — a notification should be delivered at least once.
- **Idempotent** — don't send duplicate notifications.
- **Scalable** — handle millions of notifications per day.
- **Priority** — security alerts > transactional > marketing.

### Step 2: High-Level Design

```
[Event Source] → Kafka → [Notification Worker] → [Channel Router]
                                                      ├─ Email → SES/SendGrid
                                                      ├─ SMS → Twilio
                                                      └─ Push → APNs/FCM
                   ↑
              [Template Engine]
                   ↑
              [User Preferences DB]
                   ↑
              [Rate Limiter (Redis)]
```

### Step 3: Deep Dive

#### Event-Driven Architecture
- Notifications are triggered by events from other services (order placed, payment failed, new message).
- Events are published to Kafka. Notification workers consume and process them.
- **Decoupling**: The order service doesn't know about email/SMS/push. It just publishes "order_placed."

#### Priority Queues
- **Critical** (security alerts): Separate queue, immediate processing, no rate limiting.
- **High** (transactional): Processed within seconds.
- **Normal** (social notifications): Processed within minutes.
- **Low** (marketing): Processed in batches, rate limited, can be delayed.

#### Rate Limiting per User
- Before sending, check Redis: `notif_count:{user_id}:{date}` < limit?
- If exceeded → drop or queue for next day.
- Different limits per channel (e.g., max 5 push/day, max 3 SMS/day).

#### Template Engine
- Templates stored in DB or files.
- Variables filled from event data: `Hello {{first_name}}, your order #{{order_id}} has shipped.`
- Multi-language support: template per locale.

#### Retry Strategy
- If SES/Twilio/APNs returns an error → retry with exponential backoff.
- Max 3 retries. After that, move to dead letter queue (DLQ) for manual inspection.
- **Idempotency**: Each notification has a unique ID. If retried, the channel provider deduplicates.

#### Data Model
```
Notification Table:
  notification_id   BIGINT PRIMARY KEY
  user_id            BIGINT
  type               ENUM (email, sms, push)
  template_id        VARCHAR
  payload            JSON (template variables)
  priority           ENUM (critical, high, normal, low)
  status             ENUM (pending, sent, failed, rate_limited)
  created_at         TIMESTAMP
  sent_at            TIMESTAMP

User_Preference Table:
  user_id            BIGINT
  notification_type  VARCHAR
  email_enabled      BOOLEAN
  sms_enabled        BOOLEAN
  push_enabled       BOOLEAN
```

### Key Insight
> **A notification system is the canonical event-driven architecture. The key design decisions are: priority queues (not all notifications are equal), rate limiting per user (don't spam), and idempotent delivery (retries are inevitable). The system is a pure async pipeline: events in, messages out, with no user waiting.**

---

## 7. Web Crawler

### Problem
Design a web crawler that downloads web pages, extracts links, and stores content for indexing.

### Step 1: Requirements

#### Functional
- Crawl the web starting from seed URLs.
- Follow links to discover new pages.
- Respect `robots.txt` (don't crawl disallowed pages).
- Avoid re-crawling the same page too frequently.
- Store page content for search indexing.

#### Non-Functional
- **Politeness** — don't overwhelm any single server (rate limit per domain).
- **Scalability** — crawl billions of pages.
- **Freshness** — re-crawl popular pages more frequently.
- **Efficiency** — don't crawl duplicate or near-duplicate pages.

### Step 2: High-Level Design

```
[Seed URLs] → [URL Frontier (priority queue)] → [Crawler Workers] → [Page Downloader]
                                                                    ↓
                                                              [Content Parser]
                                                                    ↓
                                                    ┌───────────────┼───────────────┐
                                                    ↓               ↓               ↓
                                              [Link Extractor]  [Content Store]  [Indexer]
                                                    ↓
                                              [URL Frontier] (add new URLs)
```

### Step 3: Deep Dive

#### URL Frontier (The Heart of the Crawler)
- A priority queue of URLs to crawl.
- **Priorities**: Homepages and frequently updated pages get higher priority.
- **Politeness**: Per-domain rate limiting. Don't hit the same domain more than once per N seconds.
- **Implementation**: Multiple sub-queues, one per domain. Round-robin across domains. This ensures politeness.

#### Deduplication
- **URL dedup**: Normalize URL (remove fragments, lowercase domain, sort query params) → hash → check Bloom filter / DB.
- **Content dedup**: Hash page content → if same hash exists, skip (it's a duplicate).
- **Near-duplicate**: SimHash or MinHash to detect pages that are 90% similar.

#### Crawl Strategy
- **BFS (Breadth-First)**: Crawl all links at depth 1, then depth 2, etc. Good for coverage.
- **Priority**: Crawl important pages first (high PageRank, frequently updated).
- **Freshness**: Re-crawl based on page's change frequency (news sites: hourly; static pages: monthly).

#### Distributed Crawling
- Multiple crawler workers running in parallel.
- **URL assignment**: Hash the URL to assign to a specific worker (consistent hashing).
- **Coordination**: A central service manages the URL frontier and assigns URLs to workers.

#### Data Model
```
URL Table:
  url_hash        VARCHAR PRIMARY KEY
  url             TEXT
  domain          VARCHAR
  last_crawled    TIMESTAMP
  crawl_frequency ENUM (hourly, daily, weekly, monthly)
  priority        INT

Page Table:
  page_id         BIGINT PRIMARY KEY
  url_hash        VARCHAR
  content_hash    VARCHAR
  title           TEXT
  content         TEXT (or stored in blob storage)
  crawled_at      TIMESTAMP
  status_code     INT
```

### Key Insight
> **The web crawler's core challenge is the URL Frontier — a priority queue that enforces politeness (per-domain rate limiting) while maximizing throughput. The crawler is a distributed BFS over the web graph, with deduplication at every step. The key trade-off is breadth vs depth: do you crawl many pages shallowly, or fewer pages deeply?**

---

## 8. Ticket Booking System (BookMyShow)

### Problem
Design a movie ticket booking system where users can browse movies, select seats, and book tickets.

### Step 1: Requirements

#### Functional
- Browse movies by city, theater, date.
- View available seats for a show.
- Select seats and hold them temporarily.
- Pay and confirm booking.
- Cancel booking (with refund).

#### Non-Functional
- **Consistency** — two users must not book the same seat (no double booking).
- **High availability** — during peak (Friday morning), system must not go down.
- **Low latency** — seat selection must be fast.
- **Fairness** — seats are first-come-first-served.

### Step 2: High-Level Design

```
Client → API Gateway → [Movie Service] → Movie DB (read replica)
                     → [Theater Service] → Theater DB
                     → [Booking Service] → Booking DB + Redis (seat locks)
                     → [Payment Service] → Payment Gateway
                     → [Notification Service]
```

### Step 3: Deep Dive

#### The Core Problem: Seat Reservation (Concurrency)
- Two users try to book seat A1 at the same time. Only one should succeed.
- **Approach 1: Database Lock**: `SELECT ... FOR UPDATE` on the seat row. Pessimistic, blocks other transactions. Simple but doesn't scale.
- **Approach 2: Optimistic Lock**: Version column on seat. Read version → try to update `WHERE version = X`. If 0 rows affected → someone else got it. Retry.
- **Approach 3: Redis Lock (Best)**: When user selects a seat, acquire a Redis lock: `SET seat:{show_id}:{seat_id} {user_id} NX EX 600` (lock for 10 minutes). If lock fails → seat is taken. On payment success → persist to DB. On timeout → lock expires, seat is available again.

#### Seat Hold Flow
```
1. User selects seats → Booking Service acquires Redis locks for each seat
2. Locks held for 10 minutes (configurable)
3. User redirected to payment
4. Payment success → Booking Service writes to DB → releases Redis lock
5. Payment failure / timeout → Redis lock expires → seats available again
6. If user cancels → release locks immediately
```

#### Data Model
```
Show Table:
  show_id        BIGINT PRIMARY KEY
  movie_id       BIGINT
  theater_id     BIGINT
  screen_id      BIGINT
  start_time     TIMESTAMP
  price          DECIMAL

Seat Table:
  seat_id        BIGINT PRIMARY KEY
  screen_id      BIGINT
  row            VARCHAR
  number         INT
  category       ENUM (regular, premium, vip)

Show_Seat Table (status per show):
  show_id        BIGINT
  seat_id        BIGINT
  status         ENUM (available, held, booked)
  held_by        BIGINT (user_id, nullable)
  held_until     TIMESTAMP (nullable)
  PRIMARY KEY (show_id, seat_id)

Booking Table:
  booking_id     BIGINT PRIMARY KEY
  user_id        BIGINT
  show_id        BIGINT
  total_amount   DECIMAL
  status         ENUM (pending, confirmed, cancelled)
  created_at     TIMESTAMP
```

#### Handling Peak Traffic (Friday Morning)
- New movie releases → traffic spike.
- **Read scaling**: Movie listings, theater info → cache aggressively (CDN + Redis).
- **Write scaling**: Booking writes go to a single primary (consistency required). Use Redis for seat holds to offload the DB.
- **Queue**: If DB is overwhelmed, queue bookings and process them in order (accept some latency).

### Key Insight
> **The ticket booking problem is the canonical concurrency problem. The key insight is: don't use the database for temporary seat holds — use Redis with TTL. The database is for confirmed bookings only. This separates the high-frequency, short-lived seat selection from the low-frequency, permanent booking. The Redis lock with TTL is the pattern for any "temporary reservation" system: hotel rooms, flight seats, shopping cart inventory.**

---

## 9. Key-Value Store

### Problem
Design a distributed key-value store (like Redis or DynamoDB) that supports `get(key)` and `put(key, value)` with high availability and partition tolerance.

### Step 1: Requirements

#### Functional
- `put(key, value)` — store a key-value pair.
- `get(key)` — retrieve the value for a key.
- `delete(key)` — remove a key.

#### Non-Functional
- **High availability** — system must be up even if nodes fail.
- **Partition tolerance** — system must work during network partitions.
- **Configurable consistency** — strong or eventual.
- **Scalable** — add nodes without downtime.

### Step 2: High-Level Design

```
Client → Coordinator Node → [Node 1, Node 2, ... Node N]
         (consistent hashing)    (each holds a range of keys)
```

### Step 3: Deep Dive

#### Partitioning: Consistent Hashing
- Keys are distributed across nodes using consistent hashing (see Patterns section).
- Each node is responsible for a range of the hash ring.
- Virtual nodes ensure even distribution.

#### Replication
- Each key is replicated to **N** nodes (e.g., N=3).
- The coordinator node (first node in the ring for that key) is responsible for replicating to the next N-1 nodes.
- If a node goes down, its replicas serve requests.

#### Write Path
```
1. Client sends put(key, value) to any node (coordinator)
2. Coordinator hashes key → finds the N responsible nodes
3. Coordinator sends write to all N nodes
4. When W nodes acknowledge → write is considered successful
5. Coordinator returns success to client
```

#### Read Path
```
1. Client sends get(key) to any node (coordinator)
2. Coordinator hashes key → finds the N responsible nodes
3. Coordinator sends read to all N nodes
4. When R nodes respond → coordinator returns the latest value
5. If values conflict → use vector clocks to resolve
```

#### Consistency Levels (W + R vs N)
- **Strong consistency**: W + R > N (e.g., N=3, W=2, R=2). Read and write quorums overlap.
- **Eventual consistency**: W + R ≤ N (e.g., N=3, W=1, R=1). Fast but may read stale data.

#### Conflict Resolution: Vector Clocks
- Each value has a **vector clock** (version vector) that tracks causality.
- When a client writes, the version is incremented.
- If two clients write concurrently (no causal relationship), both versions are kept (**siblings**).
- The client or application resolves the conflict (e.g., merge, last-write-wins).

```
Vector clock example:
  Client A writes: value="v1", clock={A:1}
  Client B writes: value="v2", clock={B:1}
  → Both are siblings (concurrent writes)
  → Application resolves: merge or pick one
```

#### Hinted Handoff
- If a replica node is down during a write, the coordinator sends the write to a **temporary node** with a hint.
- When the original node comes back, the temporary node forwards the write (handoff).

#### Read Repair
- When a read contacts multiple replicas and finds inconsistent data, the coordinator **repairs** the stale replicas by sending the latest value.
- This is anti-entropy — replicas converge over time.

#### Anti-Entropy (Merkle Trees)
- Periodically, nodes compare their data using **Merkle trees** (hash trees).
- If trees differ, only the differing subtrees are synced (efficient).
- This catches any writes that were missed (e.g., during network partitions).

### Key Insight
> **A distributed key-value store is the foundation of NoSQL. The key concepts are: consistent hashing (partitioning), replication (availability), quorum (consistency tuning), vector clocks (conflict resolution), and anti-entropy (convergence). DynamoDB, Cassandra, and Riak all use this architecture. Understanding this is understanding distributed storage.**

---

## 10. News Feed Generation (Reddit / Hacker News)

### Problem
Design a news aggregation system where users submit links, vote on them, and see a ranked feed.

### Step 1: Requirements

#### Functional
- Submit a link/post.
- Upvote/downvote a post.
- View a ranked feed (hot, new, top).
- Comment on posts.
- Sort comments (best, new, top).

#### Non-Functional
- **Low latency** — feed generation < 200 ms.
- **Eventual consistency** — vote count may lag by a few seconds.
- **Read-heavy** — 100:1 read:write ratio.

### Step 2: High-Level Design

```
Client → API Gateway → [Post Service] → Post DB (sharded)
                     → [Vote Service] → Vote DB + Redis (counters)
                     → [Feed Service] → Feed Cache (Redis sorted set)
                     → [Comment Service] → Comment DB
                     → [Ranking Service] → Redis
```

### Step 3: Deep Dive

#### Ranking Algorithm
- **Hot ranking** (Reddit-style): combines score and recency.
  ```
  score = log10(upvotes - downvotes) + (seconds_since_epoch - 1134028003) / 45000
  ```
  - The log term ensures early votes matter more (diminishing returns).
  - The time term ensures newer posts get a boost.
  - Posts decay over time → feed stays fresh.

#### Vote Processing
- Votes are high-write. Don't write each vote to the DB.
- **Pipeline**: Vote → Kafka → Vote Worker → Redis (increment counter) → Batch write to DB.
- **Redis sorted set**: `ZADD hot_feed {score} {post_id}` → feed is always sorted.
- **Read**: `ZREVRANGE hot_feed 0 99` → top 100 posts, O(log(N)).

#### Data Model
```
Post Table (sharded by post_id):
  post_id        BIGINT PRIMARY KEY
  user_id        BIGINT
  title          TEXT
  url            TEXT
  content        TEXT
  upvotes        INT (denormalized counter)
  downvotes      INT
  created_at     TIMESTAMP

Vote Table (for anti-fraud):
  vote_id        BIGINT PRIMARY KEY
  post_id        BIGINT
  user_id        BIGINT
  vote_type      ENUM (up, down)
  created_at     TIMESTAMP
  UNIQUE (post_id, user_id)

Comment Table (sharded by post_id):
  comment_id     BIGINT PRIMARY KEY
  post_id        BIGINT
  parent_id      BIGINT (nullable, for replies)
  user_id        BIGINT
  content        TEXT
  upvotes        INT
  created_at     TIMESTAMP
```

#### Comment Tree
- Comments form a tree (replies to replies).
- **Storage**: Adjacency list (`parent_id` column).
- **Retrieval**: Recursive query or pre-computed tree (materialized path: `/1/3/7/`).
- **Ranking**: Comments ranked by `score = upvotes - downvotes` (Wilson score interval for statistical confidence).

### Key Insight
> **The news feed problem is about ranking: how do you sort millions of items by relevance and recency? The key insight is pre-computation: don't compute the feed on each request. Maintain a sorted set in Redis, update it on each vote, and read it in O(1). The ranking algorithm (log + time decay) is the secret sauce — it determines what users see and thus what goes viral.**
