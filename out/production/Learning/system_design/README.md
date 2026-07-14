# System Design

A comprehensive, architect-level collection of system design notes, patterns, and solutions.

## Structure

| Folder | Description |
|--------|-------------|
| [`fundamentals/`](fundamentals/README.md) | Core concepts explained deeply: scalability, availability, CAP/PACELC, consistency models, stateful vs stateless, idempotency, backpressure |
| [`components/`](components/README.md) | Building blocks with trade-offs: load balancers, caching (strategies + failure modes), message queues (Kafka deep dive), databases (SQL vs NoSQL, sharding), API gateways, CDNs, connection pooling, observability |
| [`patterns/`](patterns/README.md) | Distributed systems patterns: sharding, replication, consistent hashing, Bloom filters, rate limiting, circuit breakers, CQRS & event sourcing, leader election, WAL, saga, bulkhead, retry with backoff |
| [`case_studies/`](case_studies/README.md) | 10 end-to-end designs with estimation + deep dive: URL shortener, Twitter, WhatsApp chat, rate limiter, Google Drive, notification system, web crawler, ticket booking, key-value store, news feed |
| [`estimation/`](estimation/README.md) | Back-of-the-envelope estimation: latency numbers, throughput numbers, worked examples (Twitter, URL shortener, chat), formulas, common mistakes |
| [`interview/`](interview/README.md) | Interview framework (5-step), communication strategy, 25 practice questions, technology decision guide, anti-patterns, mock interview walkthrough, 8-week preparation roadmap |

## How to Use This Guide

1. **Start with `fundamentals/`** — understand the core concepts before looking at any architecture.
2. **Read `components/`** — learn what building blocks exist and when to use each.
3. **Study `patterns/`** — these are the tools you'll use to solve scaling and consistency problems.
4. **Work through `case_studies/`** — see how everything comes together in real systems.
5. **Memorize `estimation/`** — the numbers and formulas are essential for interviews and real design.
6. **Practice with `interview/`** — use the framework and checklist for mock interviews.

## Quick Reference

### The Architect's Mindset
> Every decision has a trade-off. Start simple, scale when needed. Design for failure. Know your numbers. Communicate clearly.

### The 5-Step Interview Framework
1. Clarify requirements (5-7 min)
2. Back-of-the-envelope estimation (3-5 min)
3. High-level design (10-12 min)
4. Deep dive (15-20 min)
5. Identify bottlenecks & wrap up (3-5 min)
