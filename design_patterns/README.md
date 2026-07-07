# Design Patterns

A comprehensive, architect-level guide to software design patterns. Each pattern is explained with: intent, problem it solves, structure, code example (Kotlin), when to use, when NOT to use, and real-world analogies.

## Structure

| Folder | Description |
|--------|-------------|
| [`principles/`](principles/README.md) | SOLID, DRY, KISS, YAGNI, composition over inheritance, law of Demeter |
| [`creational/`](creational/README.md) | Singleton, Factory Method, Abstract Factory, Builder, Prototype, Object Pool |
| [`structural/`](structural/README.md) | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| [`behavioral/`](behavioral/README.md) | Strategy, Observer, Command, State, Template Method, Iterator, Mediator, Chain of Responsibility, Visitor, Memento, Responsibility |
| [`concurrency/`](concurrency/README.md) | Thread Pool, Producer-Consumer, Read-Write Lock, Future/Promise, Monitor, Barrier |
| [`architectural/`](architectural/README.md) | MVC, MVP, MVVM, Layered, Microservices, CQRS, Event-Driven, Hexagonal, Clean Architecture |

## How to Use This Guide

1. **Start with `principles/`** — SOLID is the foundation. Every pattern exists to satisfy these principles.
2. **Learn `creational/` first** — these are the simplest and most commonly used.
3. **Then `structural/`** — these teach you how to compose objects.
4. **Then `behavioral/`** — these teach you how objects communicate.
5. **Then `concurrency/` and `architectural/`** — advanced patterns for real-world systems.

## Quick Reference

### The Golden Rule
> **Patterns are not goals. They are tools. Use a pattern when the problem demands it, not because it sounds clever. A pattern applied to the wrong problem creates complexity, not elegance.**
