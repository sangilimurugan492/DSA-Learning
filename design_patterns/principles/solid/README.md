# SOLID Principles

The five principles of object-oriented design that make systems more maintainable, flexible, and scalable. Introduced by Robert C. Martin (Uncle Bob).

Each principle is explained in a separate file with deep examples, violations, and real-world applications.

| File | Principle | Core Idea |
|------|-----------|-----------|
| [S.md](S.md) | Single Responsibility | One reason to change |
| [O.md](O.md) | Open/Closed | Open for extension, closed for modification |
| [L.md](L.md) | Liskov Substitution | Subtypes must honor the parent's contract |
| [I.md](I.md) | Interface Segregation | Don't force unused interfaces |
| [D.md](D.md) | Dependency Inversion | Depend on abstractions, not concretions |

## The Big Picture

```
S ──► A class should do one thing
O ──► You should be able to add behavior without changing existing code
L ──► Subclasses must be true substitutes for their parents
I ──► Keep interfaces small and focused
D ──► High-level policy should not depend on low-level details
```

> **SOLID is not a goal — it's a diagnostic tool. When code is hard to change, SOLID tells you why. When code is easy to change, SOLID is already being followed (even if you didn't name it).**
