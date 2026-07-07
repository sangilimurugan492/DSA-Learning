# Estimation

Back-of-the-envelope capacity estimation is a critical architect skill. You must be able to quickly estimate storage, throughput, bandwidth, and server count before designing a system. These estimates guide your architectural decisions.

---

## 1. Why Estimation Matters

### The Purpose
- **Validate feasibility**: Can this fit on one machine, or do I need to shard?
- **Guide architecture**: If you need 500K QPS, a single DB won't work — you need caching + read replicas + sharding.
- **Cost projection**: How many servers? How much storage? What's the cloud bill?
- **Identify bottlenecks early**: If bandwidth is 50 Gbps, that's a network bottleneck — you need a CDN.

### The Mindset
> **Estimation is not about being exact. It's about being within an order of magnitude. If you estimate 100 servers and need 150, that's fine. If you estimate 10 and need 10,000, you missed something fundamental.**

---

## 2. Numbers Every Architect Must Memorize

### Powers of Two

| Power | Value | Name | Human Readable |
|-------|-------|------|----------------|
| 2^10 | 1,024 | KB | Thousand |
| 2^20 | 1,048,576 | MB | Million |
| 2^30 | 1,073,741,824 | GB | Billion |
| 2^40 | ~1.1 × 10^12 | TB | Trillion |
| 2^50 | ~1.1 × 10^15 | PB | Quadrillion |
| 2^60 | ~1.15 × 10^18 | EB | Quintillion |

**Rule of thumb**: 1 KB ≈ 10^3, 1 MB ≈ 10^6, 1 GB ≈ 10^9, 1 TB ≈ 10^12, 1 PB ≈ 10^15.

### Latency Numbers Every Programmer Should Know

| Operation | Latency | Comparison |
|---|---|---|
| L1 cache reference | 0.5 ns | |
| Branch mispredict | 5 ns | |
| L2 cache reference | 7 ns | 14x L1 |
| Mutex lock/unlock | 25 ns | |
| Main memory reference | 100 ns | 20x L2 |
| Compress 1 KB with Zippy | 3,000 ns | 3 µs |
| Send 1 KB over 1 Gbps network | 10,000 ns | 10 µs |
| SSD random read (4 KB) | 150,000 ns | 150 µs |
| Read 1 MB sequentially from memory | 250,000 ns | 250 µs |
| Round trip within datacenter | 500,000 ns | 500 µs |
| Read 1 MB sequentially from SSD | 1,000,000 ns | 1 ms |
| HDD seek | 10,000,000 ns | 10 ms |
| Read 1 MB sequentially from HDD | 20,000,000 ns | 20 ms |
| Send packet CA→Netherlands→CA | 150,000,000 ns | 150 ms |

### Key Takeaways from Latency Numbers
1. **Memory is 100,000x faster than disk.** Always cache hot data in memory.
2. **SSD is 100x faster than HDD for random reads.** Use SSDs for databases.
3. **Datacenter round trip is 500 µs.** A single API call that hits 5 services sequentially = 2.5 ms just in network.
4. **Cross-continent is 150 ms.** This is why CDNs and multi-region deployment matter.
5. **1 Gbps network = 10 µs per KB.** A 1 MB response takes 10 ms to transfer.

### Throughput Numbers

| Component | Approx Throughput |
|---|---|
| Single web server | 1,000 - 5,000 RPS |
| Single app server | 500 - 2,000 RPS |
| Single DB (reads) | 5,000 - 10,000 QPS |
| Single DB (writes) | 1,000 - 3,000 QPS |
| Redis (single node) | 100,000 QPS |
| Kafka (single partition) | 10,000 msgs/sec |
| Kafka (cluster) | millions msgs/sec |
| Load balancer | 100,000+ RPS |

---

## 3. The Estimation Framework

### Step-by-Step Process

```
1. Define the scale (users, requests, data)
2. Estimate QPS (queries per second)
3. Estimate storage (per day, per year, retention)
4. Estimate bandwidth (network throughput)
5. Estimate memory (cache size)
6. Estimate server count
7. Estimate cost (optional but impressive)
```

### Conversions to Remember
- 1 day = 86,400 seconds ≈ **100,000 seconds** (for easy mental math)
- 1 month = 30 days = 2.6 million seconds ≈ **2.5 million seconds**
- 1 year = 365 days = 31.5 million seconds ≈ **30 million seconds**
- Peak traffic ≈ **2-3x average** traffic
- Read:Write ratio varies: social media 100:1, chat 1:1, logging 1:1000

---

## 4. Worked Example: Twitter Estimation

### Assumptions
- 300 million MAU, 150 million DAU.
- Average user posts 0.5 tweets/day.
- Average user views timeline 10 times/day.
- Tweet size: 500 bytes (text) + 1 MB (media, 20% of tweets).
- Retention: 5 years.

### QPS Estimation

**Write QPS (tweets):**
```
150M DAU × 0.5 tweets/day = 75M tweets/day
75M / 86400 sec = 868 tweets/sec (avg)
Peak = 868 × 3 = ~2,600 tweets/sec
```

**Read QPS (timeline views):**
```
150M DAU × 10 views/day = 1.5B views/day
1.5B / 86400 sec = 17,361 views/sec (avg)
Peak = 17,361 × 3 = ~52,000 views/sec
```

**Read:Write ratio**: 17,361 / 868 ≈ **20:1** (read-heavy → needs caching + read replicas).

### Storage Estimation

**Text storage:**
```
75M tweets/day × 500 bytes = 37.5 GB/day
37.5 GB × 365 × 5 years = 68 TB (text only)
```

**Media storage:**
```
75M tweets/day × 20% have media × 1 MB = 15 GB/day
15 GB × 365 × 5 years = 27 TB (media)
```

Wait — that seems low. Let me recalculate:
```
75M × 0.2 = 15M media tweets/day
15M × 1 MB = 15,000 GB = 15 TB/day
15 TB × 365 × 5 = 27,375 TB ≈ 27 PB (media)
```

**Total storage (5 years)**: 68 TB (text) + 27 PB (media) ≈ **27 PB**.

### Bandwidth Estimation

**Write bandwidth:**
```
868 tweets/sec × (0.8 × 500B + 0.2 × 1MB) = 868 × 200KB ≈ 174 MB/sec ≈ 1.4 Gbps
```

**Read bandwidth:**
```
17,361 views/sec × 200KB (avg response) = 3.4 GB/sec ≈ 27 Gbps
```

**Total bandwidth**: ~29 Gbps → needs multiple network connections + CDN for media.

### Cache Estimation

**How much cache do we need?**
- Timeline cache: 150M DAU × top 1000 tweets × 500 bytes = 75 TB.
- But tweets have media — cache only tweet IDs + text: 150M × 1000 × 100 bytes = 15 TB.
- **Redis cluster**: 15 TB / 256 GB per node = ~60 Redis nodes.

### Server Estimation

**App servers (timeline reads):**
```
17,361 reads/sec / 2,000 RPS per server = 9 servers (avg)
9 × 3 (peak factor) = 27 servers
Add 30% headroom = ~35 app servers
```

**App servers (tweet writes):**
```
868 writes/sec / 1,000 RPS per server = 1 server (avg)
3 × 3 (peak) = 3 servers + headroom = ~5 write servers
```

**Total app servers**: ~40 servers.

**Database servers:**
- 27 PB media → S3 (object storage, not DB).
- 68 TB text → shard across 10 DB servers (6.8 TB each) with read replicas.
- 10 primary + 20 read replicas = 30 DB servers.

### Cost Estimation (Rough)

| Component | Count | Unit Cost/month | Total/month |
|---|---|---|---|
| App servers | 40 | $500 | $20,000 |
| DB servers | 30 | $2,000 | $60,000 |
| Redis nodes | 60 | $1,000 | $60,000 |
| S3 storage | 27 PB | $23/TB | $621,000 |
| Bandwidth | 29 Gbps | ~$5,000/Gbps | $145,000 |
| **Total** | | | **~$906,000/month** |

> **S3 storage dominates the cost.** This is why deduplication, compression, and tiered storage (move old media to Glacier) are critical.

---

## 5. Worked Example: URL Shortener Estimation

### Assumptions
- 100M new URLs/month.
- 10:1 read:write ratio → 1B redirections/month.
- URL record: 500 bytes.
- Retention: 10 years.

### QPS
```
Write: 100M / 30 / 86400 = 39 writes/sec (avg), ~120/sec (peak)
Read: 1B / 30 / 86400 = 386 reads/sec (avg), ~1,200/sec (peak)
```

### Storage
```
100M/month × 12 × 10 years = 12B URLs
12B × 500 bytes = 6 TB
```

### Cache
```
Hot URLs (top 20% that get 80% of traffic):
20% of 12B = 2.4B URLs × 500 bytes = 1.2 TB
But only recent URLs are hot → cache last 1 month = 100M × 500B = 50 GB
50 GB / 256 GB per Redis node = 1 Redis node (with replicas = 3)
```

### Server Count
```
Read: 1,200/sec / 5,000 RPS = 1 server (with replicas = 3)
Write: 120/sec / 1,000 RPS = 1 server (with replicas = 3)
DB: 6 TB fits on 1 machine (with replicas = 3)
Total: ~9 servers
```

> **This is a small system.** The URL shortener is deceptively simple — it doesn't need many servers. The challenge is in the key generation and caching strategy, not raw scale.

---

## 6. Worked Example: Chat System Estimation

### Assumptions
- 500M DAU.
- 50 messages/user/day = 25B messages/day.
- Message: 200 bytes (text) + 1 MB (media, 10% of messages).
- Retention: 30 days.

### QPS
```
Write: 25B / 86400 = 290K messages/sec (avg), ~870K/sec (peak)
Read: ~2x writes = 580K reads/sec (avg), ~1.7M/sec (peak)
```

### Storage
```
Text: 25B × 0.9 × 200B = 4.5 TB/day
Media: 25B × 0.1 × 1MB = 2.5 TB/day
Total: ~7 TB/day
30 days: 210 TB
```

### WebSocket Connections
```
500M concurrent connections
Each server handles 50K connections
500M / 50K = 10,000 connection servers
```

### Bandwidth
```
290K msgs/sec × 200 bytes = 58 MB/sec (text)
290K × 0.1 × 1MB = 29 GB/sec (media) → 232 Gbps
```

> **Media bandwidth is the bottleneck.** Solution: media goes through CDN/S3, not through the chat servers. Chat servers only handle text + metadata.

---

## 7. Common Estimation Patterns

### Rule of 80/20 (Pareto Principle)
- 80% of traffic comes from 20% of data.
- **Cache the 20%**: 20% of 100M URLs = 20M URLs × 500 bytes = 10 GB → fits in one Redis node.
- This is why caching is so effective — you don't need to cache everything.

### Peak Factor
- Peak traffic is typically **2-3x average**.
- For shopping (Black Friday): **10-50x average**.
- Always design for peak, not average.

### Read:Write Ratios by System Type

| System Type | Read:Write | Implication |
|---|---|---|
| Social media feed | 100:1 | Heavy caching, read replicas |
| Chat | 1:1 | Balanced, WebSocket |
| Logging/Analytics | 1:1000 | Write-optimized, columnar DB |
| E-commerce product | 10:1 | Cache product catalog |
| Banking | 1:1 | Strong consistency, no cache |

### Server Sizing Rules of Thumb
- **Web server**: 2-4 CPU, 4-8 GB RAM → 1,000-5,000 RPS.
- **App server**: 4-8 CPU, 8-16 GB RAM → 500-2,000 RPS.
- **DB server**: 16-32 CPU, 64-128 GB RAM, SSD → 5,000-10,000 QPS (read), 1,000-3,000 (write).
- **Cache server**: 8 CPU, 256 GB RAM → 100,000 QPS.
- **Always add 30-50% headroom** for unexpected traffic.

### Storage Sizing Rules of Thumb
- **SSD**: 1-5 TB per server. Fast random reads.
- **HDD**: 10-50 TB per server. Cheap, slow. For cold storage.
- **Object storage (S3)**: Unlimited. $0.023/GB/month. For media, backups, logs.
- **Always plan for 2-3x growth** in the first year.

---

## 8. Estimation Cheat Sheet

### Quick Mental Math
```
Seconds in a day:     86400 ≈ 100K
Seconds in a month:   2.6M  ≈ 2.5M
Seconds in a year:    31.5M ≈ 30M

1 KB = 10^3 bytes
1 MB = 10^6 bytes
1 GB = 10^9 bytes
1 TB = 10^12 bytes
1 PB = 10^15 bytes

1 Gbps = 125 MB/sec (divide by 8)
1 MB over 1 Gbps = 10 ms
1 MB over 10 Gbps = 1 ms
```

### Server Count Formula
```
servers = (QPS × peak_factor × headroom) / RPS_per_server

Example:
  QPS = 10,000
  Peak factor = 3
  Headroom = 1.5 (50%)
  RPS per server = 2,000

  servers = (10,000 × 3 × 1.5) / 2,000 = 22.5 → 23 servers
```

### Storage Formula
```
storage = (records_per_day × record_size × retention_days) / compression_ratio

Example:
  100M records/day × 1 KB × 365 days × 5 years / 3 (compression) = 61 TB
```

### Cache Size Formula
```
cache_size = hot_data_percentage × total_data

Example:
  100M users × 1 KB profile × 20% (hot) = 2 GB cache
```

---

## 9. Common Mistakes in Estimation

### 1. Forgetting Peak Factor
- Designing for average traffic → system dies during peak.
- **Always multiply by 2-3x for peak.**

### 2. Ignoring Media
- Text is small. Media is huge.
- A tweet is 500 bytes. A photo is 1 MB. A video is 50 MB.
- **Always estimate media separately.**

### 3. Forgetting Replication and Redundancy
- You need 3x storage for replication (3 replicas).
- You need 2x servers for HA (active + standby).
- **Multiply your estimates by replication factor.**

### 4. Not Accounting for Growth
- System grows 2-3x per year.
- Design for 2x current scale, plan for 5x.
- **Storage is cheap to add. Server architecture is hard to change.**

### 5. Confusing Bandwidth Units
- 1 Gbps = 125 MB/sec (not 1 GB/sec).
- Bits vs bytes: network is in bits (Gbps), storage is in bytes (GB).
- **Always clarify: is it Gb (gigabits) or GB (gigabytes)?**

### Key Insight
> **Estimation is the architect's superpower. It lets you reject bad designs before building them. "Can we handle this on one DB?" → do the math. "Do we need a CDN?" → calculate the bandwidth. "Is Redis enough?" → estimate the cache hit ratio. The architect who can estimate can make decisions; the one who can't is guessing.**
