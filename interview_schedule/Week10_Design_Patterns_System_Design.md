# Week 10: SOLID + Design Patterns + System Design

> **Duration:** 1 week | **Hours:** 18 hrs | **DSA Problems:** ~10 (mixed review)

---

## 📅 Daily Schedule

| Day | Time | Theory (1.5 hr) | DSA (30 min) |
|-----|------|-----------------|--------------|
| Mon | 2hr | 🔴 [SOLID: S](../solid/S.md) + [O](../solid/O.md) + [L](../solid/L.md) | 2 mixed medium |
| Tue | 2hr | 🔴 [SOLID: I](../solid/I.md) + [D](../solid/D.md) + [SOLID README](../solid/README.md) | 2 mixed medium |
| Wed | 2hr | 🔴 [Creational Patterns](../design_patterns/creational/README.md) + [Structural Patterns](../design_patterns/structural/README.md) | 2 mixed medium |
| Thu | 2hr | 🔴 [Behavioral Patterns](../design_patterns/behavioral/README.md) + [Concurrency Patterns](../design_patterns/concurrency/README.md) | 2 mixed hard |
| Fri | 2hr | 🔴 [Architectural Patterns](../design_patterns/architectural/README.md) + [Principles](../design_patterns/principles/README.md) | 2 mixed hard |
| Sat | 4hr | 🟡 [System Design Fundamentals](../system_design/fundamentals/README.md) + [Components](../system_design/components/README.md) | 2 mixed review |
| Sun | 4hr | 🟡 [System Design Patterns](../system_design/patterns/README.md) + [Case Studies](../system_design/case_studies/README.md) + [Estimation](../system_design/estimation/README.md) + [Interview](../system_design/interview/README.md) | 2 mixed review |

---

## 📖 Topics to Cover

### SOLID Principles (6 files) 🔴
| File | Key Concepts |
|------|-------------|
| [S — Single Responsibility](../solid/S.md) | A class should have one reason to change |
| [O — Open/Closed](../solid/O.md) | Open for extension, closed for modification |
| [L — Liskov Substitution](../solid/L.md) | Subtypes must be substitutable for base types |
| [I — Interface Segregation](../solid/I.md) | Don't force clients to depend on unused interfaces |
| [D — Dependency Inversion](../solid/D.md) | Depend on abstractions, not concretions |
| [README](../solid/README.md) | Overview, examples, anti-patterns |

### Design Patterns (7+ files) 🔴
| File | Key Concepts |
|------|-------------|
| [Creational](../design_patterns/creational/README.md) | Singleton, Factory, Abstract Factory, Builder, Prototype, Object Pool |
| [Structural](../design_patterns/structural/README.md) | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| [Behavioral](../design_patterns/behavioral/README.md) | Strategy, Observer, Command, State, Template Method, Iterator, Mediator, Chain of Responsibility, Visitor, Memento |
| [Concurrency](../design_patterns/concurrency/README.md) | Thread Pool, Producer-Consumer, Read-Write Lock, Future/Promise, Monitor, Barrier |
| [Architectural](../design_patterns/architectural/README.md) | MVC, MVP, MVVM, Layered, Microservices, CQRS, Event-Driven, Hexagonal, Clean Architecture |
| [Principles](../design_patterns/principles/README.md) | DRY, KISS, YAGNI, composition over inheritance, law of Demeter |
| [SOLID (DP)](../design_patterns/principles/solid/README.md) | SOLID within design patterns context |

### System Design (6+ files) 🟡
| File | Key Concepts |
|------|-------------|
| [Fundamentals](../system_design/fundamentals/README.md) | Scalability, availability, CAP/PACELC, consistency, stateful vs stateless, idempotency, backpressure |
| [Components](../system_design/components/README.md) | Load balancers, caching, message queues, databases, API gateways, CDNs, observability |
| [Patterns](../system_design/patterns/README.md) | Sharding, replication, consistent hashing, Bloom filters, rate limiting, circuit breakers, CQRS, saga |
| [Case Studies](../system_design/case_studies/README.md) | URL shortener, Twitter, WhatsApp, Google Drive, notification system, web crawler, ticket booking |
| [Estimation](../system_design/estimation/README.md) | Latency numbers, throughput, worked examples |
| [Interview](../system_design/interview/README.md) | 5-step framework, 25 practice questions, 8-week roadmap |

---

## 🧮 DSA Problems (Week 10 — Mixed Review)

| Day | Problem | Source | Difficulty |
|-----|---------|--------|-----------|
| Mon | Pick any medium (arrays) | [DSA/array/](../DSA/array/) | Medium |
| Mon | Pick any medium (stack) | [DSA/stack/](../DSA/stack/) | Medium |
| Tue | Pick any medium (tree) | [DSA/tree/](../DSA/tree/) | Medium |
| Tue | Pick any medium (graph) | [DSA/graph/](../DSA/graph/) | Medium |
| Wed | Pick any hard (heap) | [DSA/heap/](../DSA/heap/) | Hard |
| Wed | Pick any hard (backtracking) | [DSA/BackTracking/](../DSA/BackTracking/) | Hard |
| Thu | Pick any hard (DP) | [DSA/dp/](../DSA/dp/) | Hard |
| Thu | Pick any hard (trie) | [DSA/trie/](../DSA/trie/) | Hard |
| Fri | Pick any medium (review) | Any folder | Medium |
| Fri | Pick any hard (review) | Any folder | Hard |
| Sat | Pick any 2 (review) | Any folder | Mixed |
| Sun | Pick any 2 (review) | Any folder | Mixed |

---

## 🧠 Key Concepts to Memorize

### SOLID
| Principle | One-liner | Violation Example |
|-----------|-----------|-------------------|
| S | One responsibility | God class doing everything |
| O | Extend, don't modify | if/else for every new type |
| L | Subtypes substitutable | Square extends Rectangle breaks |
| I | Small interfaces | Fat interface with unused methods |
| D | Depend on abstractions | `class Service { val db = MySQLDB() }` |

### Design Patterns Quick Reference
| Category | Patterns | When to Use |
|----------|----------|-------------|
| Creational | Singleton, Factory, Builder, Prototype | Object creation |
| Structural | Adapter, Decorator, Facade, Proxy | Object composition |
| Behavioral | Strategy, Observer, Command, State | Object communication |
| Concurrency | Thread Pool, Producer-Consumer, Future | Multi-threading |
| Architectural | MVVM, Clean, Microservices, CQRS | System structure |

### System Design 5-Step Framework
1. **Clarify requirements** (5-7 min) — functional + non-functional
2. **Estimation** (3-5 min) — traffic, storage, bandwidth
3. **High-level design** (10-12 min) — boxes and arrows
4. **Deep dive** (15-20 min) — data model, API, scaling
5. **Bottlenecks** (3-5 min) — SPOF, scaling, trade-offs

### Key Numbers to Memorize
| Operation | Latency |
|-----------|---------|
| L1 cache | 0.5 ns |
| Main memory | 100 ns |
| SSD read | 100 µs |
| Network (same DC) | 0.5 ms |
| Network (cross-region) | 30-100 ms |
| HDD seek | 10 ms |

---

## ✅ Self-Assessment Checklist

### SOLID
- [ ] Can explain each SOLID principle with example
- [ ] Can identify SOLID violations in code
- [ ] Can refactor code to follow SOLID

### Design Patterns
- [ ] Can name all 23 GoF patterns
- [ ] Can implement Singleton, Factory, Builder, Observer, Strategy in Kotlin
- [ ] Can identify when to use each pattern
- [ ] Can explain MVVM vs MVP vs MVI
- [ ] Can explain Clean Architecture layers

### System Design
- [ ] Can design URL shortener end-to-end
- [ ] Can design a chat application
- [ ] Can explain CAP theorem and PACELC
- [ ] Can do back-of-envelope estimation
- [ ] Can explain sharding, replication, consistent hashing
- [ ] Can explain when to use SQL vs NoSQL
- [ ] Can design rate limiter and circuit breaker

### DSA
- [ ] Solved 10 mixed review problems
- [ ] Can solve medium in <25 min
- [ ] Can solve hard in <40 min

---

## 🔗 Next
- [Week 11: Specialized Topics](Week11_Specialized_Topics.md)
- [Back to README](README.md)
