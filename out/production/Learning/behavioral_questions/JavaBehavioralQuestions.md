# Java Behavioral Questions — Senior Engineer (STAR Format)

> STAR = **S**ituation, **T**ask, **A**ction, **R**esult
> Each answer is framed from the perspective of a Senior Java Engineer.

---

## Q1: Tell me about a time you had to resolve a production OOM (OutOfMemoryError).

### Situation
Our Java backend service started throwing `OutOfMemoryError: Java heap space` every 2-3 days, requiring a restart. The service processed large CSV files (500MB+) and loaded them into memory for transformation. As file sizes grew, the heap couldn't keep up.

### Task
I needed to eliminate the OOM crashes and ensure the service could handle files up to 2GB without memory issues.

### Action
- **Analyzed heap dumps** — I configured `-XX:+HeapDumpOnOutOfMemoryError` and analyzed the dump with Eclipse MAT. The dump showed 1.2GB was consumed by `ArrayList<String[]>` — the entire CSV was loaded into memory at once.
- **Identified the root cause** — the code used `Files.readAllLines()` which loaded the entire file into a `List<String>`. For a 500MB CSV, this consumed ~1.5GB of heap (strings + list overhead).
- **Rewrote the parser to stream** — I replaced `Files.readAllLines()` with `Files.lines()` which returns a `Stream<String>`. This processed the file line-by-line without loading it all into memory.
- **Used `BufferedReader` with batch processing** — for transformations that needed context (e.g., aggregating by key), I processed in batches of 10,000 lines, aggregating into a `HashMap` and flushing to the database periodically.
- **Set appropriate JVM flags** — I configured `-Xmx2g -Xms1g` and enabled G1GC with `-XX:+UseG1GC` for better large-heap performance.
- **Added memory monitoring** — I added a `MemoryMXBean` check that logged a warning when heap usage exceeded 80%.
- **Wrote a test** that processed a 2GB test file and verified memory usage stayed under 500MB.

### Result
OOM crashes were eliminated — 0 occurrences in 6 months. The service handled 2GB files with peak memory usage of 400MB (down from 1.5GB). Processing time improved by 20% because streaming avoided the initial load delay. The streaming pattern was adopted as the standard for all file processing. The key learning was that `Files.readAllLines()` is a hidden OOM trap for large files — always use streaming.

---

## Q2: Describe a time you had to refactor a legacy Java codebase.

### Situation
We inherited a 10-year-old Java monolith with 200+ classes, no tests, and tight coupling everywhere. Every change risked breaking something. The team was afraid to touch the code, and bugs were accumulating. The business wanted new features but we spent 60% of our time fixing regressions.

### Task
I needed to make the codebase maintainable without stopping feature development or rewriting the entire system.

### Action
- **Introduced a "characterization test" layer** — before refactoring any class, I wrote tests that captured the current behavior (even if buggy). These tests ensured refactoring didn't change behavior. I used `JUnit 5` + `Mockito`.
- **Applied the "strangler fig" pattern** — I built new features in a separate module with clean architecture. The old monolith called the new module via an interface. Over time, the new module grew and the old monolith shrank.
- **Extracted modules gradually** — I identified bounded contexts (user management, billing, reporting) and extracted each into a separate Gradle module. This enforced boundaries at the build level.
- **Replaced inheritance with composition** — the codebase had deep inheritance hierarchies (5+ levels). I replaced them with composition + interfaces, making the code testable.
- **Introduced dependency injection** — I migrated from `new` everywhere to Guice DI, making classes testable in isolation.
- **Added static analysis** — I set up SonarQube with a quality gate and a "technical debt" dashboard. I didn't try to fix all 5,000 violations — I focused on preventing new ones.
- **Ran "fix-it Fridays"** — every Friday, the team spent 2 hours fixing technical debt in the area they were currently working in.

### Result
Over 6 months, test coverage went from 0% to 45%. Regression bugs dropped by 50% — the characterization tests caught breaking changes before they reached production. Three bounded contexts were extracted into separate modules. The team's confidence improved — they were no longer afraid to make changes. The "fix-it Friday" practice was adopted permanently and reduced technical debt by 30% in the first year. The key learning was that you don't need a rewrite — characterization tests + strangler fig + gradual extraction can make any codebase maintainable.

---

## Q3: Tell me about a time you had to handle a concurrency bug in Java.

### Situation
Our Java service had a `ConcurrentModificationException` that occurred intermittently in production. The exception happened in a `HashMap` that was accessed by multiple threads — one reading, one writing. It affected ~0.5% of requests and was impossible to reproduce in testing.

### Task
I needed to fix the concurrency issue and prevent similar issues in the future.

### Action
- **Analyzed the stack trace** — the exception occurred in a shared cache `HashMap<String, User>` that was read by request threads and written by a background refresh thread. `HashMap` is not thread-safe.
- **Identified the root cause** — the cache used `HashMap` instead of `ConcurrentHashMap`. The read and write happened simultaneously, causing the `ConcurrentModificationException`.
- **Replaced `HashMap` with `ConcurrentHashMap`** — this is thread-safe and allows concurrent reads and writes without external synchronization.
- **Audited all shared mutable state** — I searched for `static Map`, `static List`, and instance fields accessed by multiple threads. I found 5 more `HashMap` and `ArrayList` instances used across threads.
- **Replaced with concurrent collections** — `HashMap` → `ConcurrentHashMap`, `ArrayList` → `CopyOnWriteArrayList` (for read-heavy) or `Collections.synchronizedList` (for write-heavy).
- **Used `AtomicInteger` and `AtomicReference`** for shared counters and references instead of `synchronized` blocks.
- **Added a thread-safety test** — I wrote a test that ran 100 threads concurrently reading and writing to the cache for 30 seconds and verified no exceptions.
- **Enabled `-XX:+UseThreadPriorities`** and ran stress tests with `Gatling` to simulate production load.

### Result
The `ConcurrentModificationException` was eliminated — 0 occurrences after the fix. The thread-safety test caught 2 more concurrency issues in subsequent PRs. I created a "Thread Safety Checklist" that was added to code review guidelines: (1) Is this field accessed by multiple threads? (2) Is the collection thread-safe? (3) Are compound operations atomic? The checklist was adopted by 3 other teams. The key learning was that `HashMap` and `ArrayList` are never safe for cross-thread use — always use `ConcurrentHashMap` or `CopyOnWriteArrayList`.

---

## Q4: Tell me about a time you had to improve Java application performance.

### Situation
Our Java API had a P99 latency of 3.5 seconds, well above our SLA of 500ms. The API served a product search endpoint that queried a PostgreSQL database with 10M+ rows. As traffic grew, latency increased linearly.

### Task
I needed to bring P99 latency under 500ms without adding more database servers.

### Action
- **Profiled with async-profiler** — I generated a flame graph and found 70% of request time was in database queries, 20% in JSON serialization, and 10% in business logic.
- **Optimized database queries** — I found N+1 query patterns in 3 places. A product search was fetching products, then for each product, fetching its categories and reviews separately. I replaced N+1 with `JOIN` queries and `IN` clauses.
- **Added database indexes** — I analyzed slow queries with `pg_stat_statements` and added 4 missing indexes on frequently-filtered columns. This alone reduced query time by 60%.
- **Introduced caching** — I added a Caffeine cache for hot products (top 1000). Cache hit rate was 85%, reducing database load significantly.
- **Switched JSON serialization** — I replaced `Jackson` with `Jackson + Afterburner` module (now `jackson-blackbird`) for faster serialization via bytecode generation. This reduced serialization time by 40%.
- **Used connection pooling** — I tuned HikariCP settings: `maximumPoolSize=20`, `minimumIdle=5`, `connectionTimeout=3000`. The default pool was too small for our load.
- **Added pagination** — search results were returning all matches. I added `LIMIT/OFFSET` pagination with a max page size of 50.
- **Set up latency monitoring** — I added Micrometer metrics for each query and a Grafana dashboard for P50/P95/P99 latency.

### Result
P99 latency dropped from 3.5s to 350ms — a 90% improvement. The database CPU usage dropped from 80% to 30%. The Caffeine cache handled 85% of requests, reducing database load. The latency dashboard caught a regression 2 weeks later when a new feature added a slow query. The key learning was that N+1 queries and missing indexes are the most common Java API performance killers — always profile the database first.

---

## Q5: Tell me about a time you had to design a scalable Java system.

### Situation
Our monolithic Java service was handling 1,000 requests/second but couldn't scale beyond that. The database was the bottleneck — all requests hit a single PostgreSQL instance. We needed to handle 10,000 requests/second for an upcoming product launch.

### Task
I needed to design a scalable architecture that could handle 10x traffic without a complete rewrite.

### Action
- **Identified the bottleneck** — the database was at 90% CPU. Read queries accounted for 90% of the load. Write queries were only 10%.
- **Introduced read replicas** — I set up 3 PostgreSQL read replicas and used a routing `DataSource` that sent reads to replicas and writes to the primary. This immediately reduced primary database load by 70%.
- **Added Redis caching** — for hot data (product details, user profiles), I added a Redis cache layer with a 5-minute TTL. Cache hit rate was 80%, further reducing database load.
- **Implemented async processing** — for non-critical operations (analytics, notifications, search indexing), I moved them to a Kafka queue processed by separate workers. This reduced the main request path latency.
- **Used CQRS pattern** — I separated the write model (commands) from the read model (queries). Writes went to PostgreSQL; reads were served from denormalized Elasticsearch indices. This allowed independent scaling of reads and writes.
- **Containerized with Docker** and deployed on Kubernetes with horizontal pod autoscaling (HPA) based on CPU and custom metrics.
- **Added circuit breakers** — I used Resilience4j for circuit breaking, retry, and rate limiting to prevent cascading failures.
- **Load tested** with Gatling — I simulated 15,000 req/s to verify the system could handle 50% above target.

### Result
The system handled 12,000 requests/second at P99 latency of 200ms. Database CPU dropped to 40%. The read replica + Redis + Elasticsearch architecture allowed independent scaling of reads. The async processing via Kafka reduced main-path latency by 30%. The circuit breakers prevented 2 cascading failures in the first 3 months. The key learning was that read/write separation (CQRS) + caching + async is the most effective scaling strategy for read-heavy Java applications.

---

## Q6: Tell me about a time you had to handle a difficult code review.

### Situation
A senior engineer on the team submitted a PR that introduced a custom thread pool with 200 lines of concurrency logic. I reviewed it and found 3 issues: (1) no shutdown hook for the thread pool, (2) `submit()` without exception handling (exceptions would be swallowed), (3) the pool size was hardcoded instead of configurable. The engineer pushed back, saying "it works fine" and "you're being too pedantic."

### Task
I needed to get the issues fixed without damaging the relationship or escalating to management.

### Action
- **Acknowledged their work** — I started the review with "This is a solid implementation. The thread pool design is well-thought-out. I have a few concerns about edge cases."
- **Framed issues as questions, not commands** — instead of "add a shutdown hook," I asked "What happens to the thread pool when the application shuts down? Will the threads be cleaned up?" This led them to discover the issue themselves.
- **Showed concrete impact** — for the exception handling issue, I wrote a 5-line test that submitted a task that threw an exception. The exception was silently swallowed. Seeing the test fail made the issue concrete.
- **Referenced best practices** — I linked to the Java docs for `ExecutorService` and an article about thread pool leaks. This made it about the code, not about me vs them.
- **Offered to pair** — I said "I'm happy to pair on the shutdown hook if you want. I've dealt with this before." This showed I was offering help, not just criticizing.
- **Picked my battles** — I didn't comment on naming or style. I focused on the 3 issues that could cause production problems.

### Result
The engineer fixed all 3 issues. They later told me they appreciated the approach — the test that showed the swallowed exception was the most convincing. They started writing similar tests for their own thread pool code. Our code review relationship improved — they started asking me to review earlier in the process. The key learning was that questions + concrete examples > commands. People resist being told what to do, but they respond to evidence.

---

## Q7: Tell me about a time you had to introduce a new technology to the team.

### Situation
Our Java team was using `java.util.logging` (JUL) for logging. It lacked features like structured logging, async logging, and log routing. We were missing critical logs in production because JUL would block the request thread during I/O. I wanted to migrate to Log4j2 (or SLF4J + Logback).

### Task
I needed to migrate the logging framework without disrupting the team or causing production issues.

### Action
- **Measured the problem** — I profiled a production request and found that JUL logging added 40ms to each request (synchronous file I/O). This was 20% of the total request time.
- **Researched alternatives** — I compared Log4j2, Logback, and SLF4J. Log4j2 had the best async performance (LMAX Disruptor-based).
- **Prototyped the migration** — I migrated one service as a proof-of-concept. I used SLF4J as the facade (so we could swap implementations) and Log4j2 as the implementation. The async logging reduced logging overhead from 40ms to 2ms.
- **Presented the data** — I showed the before/after profiling results in the team meeting. The 20% latency improvement spoke for itself.
- **Created a migration guide** — I documented the 3-step process: (1) Add SLF4J + Log4j2 dependencies, (2) Replace `java.util.logging.Logger` with `org.slf4j.Logger`, (3) Configure `log4j2.xml` with async appenders.
- **Migrated one service at a time** — I didn't do a big-bang migration. Each service was migrated when it was touched for feature work.
- **Added a CI check** — I created a check that flagged new `java.util.logging` usage with a message pointing to the migration guide.

### Result
Over 2 months, all 8 services were migrated to SLF4J + Log4j2. Logging overhead dropped from 40ms to 2ms per request. We gained structured logging (JSON format) which made log searching 10x faster in our log aggregation tool (ELK). The async logging prevented 2 production incidents where disk I/O was slow — JUL would have blocked requests, but Log4j2's async queue absorbed the delay. The key learning was that logging is infrastructure — it should be async, structured, and measured.

---

## Q8: Tell me about a time you had to handle a security vulnerability.

### Situation
Our security team flagged a SQL injection vulnerability in our Java service. A search endpoint was building SQL queries by concatenating user input directly into the query string: `String sql = "SELECT * FROM products WHERE name LIKE '%" + searchTerm + "%'"`. This was a P0 vulnerability.

### Task
I needed to fix the vulnerability immediately, audit the codebase for similar issues, and prevent future occurrences.

### Action
- **Fixed the immediate vulnerability** — I replaced string concatenation with `PreparedStatement` and parameterized queries: `String sql = "SELECT * FROM products WHERE name LIKE ?"` with `stmt.setString(1, "%" + searchTerm + "%")`.
- **Audited the entire codebase** — I searched for `String sql =` and `+` patterns. I found 12 more SQL injection vulnerabilities in different endpoints.
- **Fixed all 12 vulnerabilities** — I replaced each with `PreparedStatement` or `JPA`/`JOOQ` parameterized queries.
- **Introduced a lint rule** — I added a custom PMD rule that flagged SQL string concatenation. This prevented new vulnerabilities from being introduced.
- **Migrated to JPA/JOOQ** — for new code, I mandated using JPA (with `@Query` and named parameters) or JOOQ. These frameworks handle parameterization automatically.
- **Added a security test** — I wrote a test that sent SQL injection payloads (`' OR '1'='1`, `'; DROP TABLE--`) to every endpoint and verified they were handled safely.
- **Wrote a post-mortem** — I documented the vulnerability, the fix, and the preventive measures. I shared it with the entire engineering org.

### Result
All 13 SQL injection vulnerabilities were fixed within 24 hours. The PMD rule caught 2 attempts to introduce SQL concatenation in subsequent PRs. The security test was added to CI and ran on every PR. The migration to JPA/JOOQ for new code eliminated the class of vulnerability entirely. The security team audited our service and gave it a clean bill. The key learning was that string concatenation in SQL should never be allowed — always use parameterized queries, and enforce it with lint rules.

---

## Q9: Tell me about a time you had to balance technical debt with deadlines.

### Situation
Our Java service had a monolithic `OrderService` class with 2,000+ lines and 15 public methods. It handled order creation, payment, inventory, shipping, and notifications. Every change risked breaking something, and the class had 40% test coverage. The business wanted a new feature (gift cards) in 2 weeks.

### Task
I needed to deliver the gift card feature on time while making progress on the `OrderService` refactor.

### Action
- **Scoped the feature** — the gift card feature only needed to hook into order creation and payment. It didn't need to touch shipping or notifications.
- **Extracted `PaymentService`** — I extracted the payment logic from `OrderService` into a separate `PaymentService` class. This took 1 day and was needed for the gift card feature anyway.
- **Implemented the gift card feature** in the new `PaymentService` — it was clean, testable, and didn't touch the rest of `OrderService`. This took 3 days.
- **Wrote tests** — I achieved 90% coverage on `PaymentService`. The rest of `OrderService` remained at 40%, but the new code was fully tested.
- **Filed a tech-debt ticket** for the remaining `OrderService` refactor (extracting `InventoryService`, `ShippingService`, `NotificationService`).
- **Communicated the trade-off** — I told the PM: "The gift card feature is done. I also extracted the payment logic into a separate service, which will make future payment features faster. The rest of the refactor is tracked in TICKET-123."

### Result
The gift card feature shipped on time. The `PaymentService` extraction was a net positive — it made the feature easier to build AND reduced `OrderService` by 300 lines. The tech-debt ticket was addressed in the next sprint, extracting `InventoryService` and `ShippingService`. Over 3 months, `OrderService` was reduced from 2,000 to 600 lines. The key learning was that you can make progress on tech debt while delivering features — extract the piece you need for the feature, and file tickets for the rest.

---

## Q10: Tell me about a time you failed and what you learned.

### Situation
I was tasked with building a custom ORM layer for our Java service. We were using raw JDBC and I wanted to eliminate boilerplate. Instead of using existing solutions (Hibernate, MyBatis, or jOOQ), I decided to build my own using reflection and annotations — `@Table`, `@Column`, `@Id`. I thought it would be simple and give us "full control."

### Task
I needed to build a lightweight ORM that handled CRUD operations, relationships, and transactions.

### Action
- **Built the ORM** — I spent 4 weeks building it with reflection-based field mapping, annotation processing, and a custom `Session` class. It handled CRUD for simple entities.
- **Hit walls on relationships** — `@OneToMany` and `@ManyToOne` required lazy loading, which required proxy classes. I spent a week trying to implement this with `java.lang.reflect.Proxy`.
- **Hit walls on transactions** — managing transaction isolation levels, rollbacks, and connection pooling was more complex than I expected. I had a bug where a rollback in one transaction affected another.
- **Hit walls on performance** — reflection was 10x slower than raw JDBC. I tried caching reflection metadata, but it was still slow.
- **A teammate asked** "Why didn't you use jOOQ?" — I looked at jOOQ and realized it did everything I was trying to do, but better, faster, and with type safety.
- **I had to scrap 4 weeks of work** and migrate to jOOQ. The migration took 1 week.

### Result
jOOQ handled everything my custom ORM struggled with — type-safe queries, relationships, transactions, and connection pooling. I wasted 4 weeks building an inferior version of an existing tool. The lesson was: **never build infrastructure that already exists.** I created a checklist: (1) Is there an existing library? (2) Is it maintained? (3) Does it meet 80% of requirements? (4) Can I contribute to it instead of building a competitor? I shared this checklist with the team and it prevented 3 similar "not invented here" attempts. The key learning was that "full control" is usually an illusion — existing tools are more battle-tested than anything you can build alone.

---

## 🔗 Related Topics
- [Kotlin Behavioral Questions](KotlinBehavioralQuestions.md)
- [Android Behavioral Questions](AndroidBehavioralQuestions.md)
