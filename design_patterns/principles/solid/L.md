# L — Liskov Substitution Principle (LSP)

> **Subtypes must be substitutable for their base types without altering the correctness of the program.**

*Barbara Liskov, 1987*

---

## What It Really Means

If `B` is a subtype of `A`, then anywhere `A` is expected, `B` should work **without surprises**. The subclass must honor the **contract** of the parent class — same behavior, same expectations, same invariants.

Inheritance is not just about sharing code. It's about **behavioral compatibility**. If a subclass breaks the parent's contract, code that works with the parent will break with the child.

---

## The Problem: The Classic Square-Rectangle

```kotlin
// BAD: Square violates Rectangle's contract
open class Rectangle {
    open var width: Int = 0
    open var height: Int = 0

    fun area() = width * height
}

class Square : Rectangle() {
    override var width: Int = 0
        set(value) { field = value; height = value } // changing width also changes height!

    override var height: Int = 0
        set(value) { field = value; width = value }
}

// This function expects a Rectangle — but Square breaks it
fun test(rect: Rectangle) {
    rect.width = 5
    rect.height = 4
    assert(rect.area() == 20) // FAILS for Square! area = 16
}

test(Square()) // 💥 Assertion fails
```

A `Square` is-a `Rectangle` **mathematically**, but NOT **behaviorally**. `Rectangle`'s contract says "width and height are independent." `Square` violates that invariant. Code that works with `Rectangle` breaks with `Square`.

---

## The Solution: Don't Force Bad Inheritance

```kotlin
// GOOD: Use a common interface — no false "is-a" relationship
interface Shape {
    fun area(): Int
}

class Rectangle(val width: Int, val height: Int) : Shape {
    override fun area() = width * height
}

class Square(val side: Int) : Shape {
    override fun area() = side * side
}

// Now there's no false contract — both just implement Shape
```

---

## The Formal Contract (LSP Rules)

LSP is about **contracts**. A contract has three parts:

### 1. Preconditions (what must be true before calling)
- Subclass **cannot strengthen** preconditions.
- If parent accepts `null`, child must also accept `null`.
- If parent accepts any integer, child can't reject negative numbers.

```kotlin
// BAD: Child strengthens precondition
open class Base {
    fun process(value: Int) { /* accepts any Int */ }
}

class Child : Base() {
    override fun process(value: Int) {
        require(value >= 0) { "Child rejects negatives — parent didn't!" } // 💥
    }
}

// Code that passes -1 to Base.process() works.
// The same code with Child breaks. LSP violated.
```

### 2. Postconditions (what must be true after calling)
- Subclass **cannot weaken** postconditions.
- If parent returns non-null, child must also return non-null.
- If parent guarantees `result >= 0`, child can't return -1.

```kotlin
// BAD: Child weakens postcondition
open class Base {
    open fun find(id: String): User { /* always returns a User */ }
}

class Child : Base() {
    override fun find(id: String): User {
        return if (cache.containsKey(id)) cache[id]!!
        else null!!  // 💥 Parent never returned null, child does
    }
}
```

### 3. Invariants (what must always be true)
- Subclass **must maintain** all invariants of the parent.
- If parent guarantees `count >= 0`, child must also.

---

## LSP Violation Patterns

### Pattern 1: Throwing `NotImplementedException`

```kotlin
// BAD: Subclass can't fulfill the parent's contract
open class Bird {
    fun fly() { /* fly implementation */ }
}

class Penguin : Bird() {
    override fun fly() {
        throw NotImplementedError("Penguins can't fly!") // 💥 LSP violation
    }
}

// Code that calls bird.fly() works for Eagle, crashes for Penguin.
```

**Fix**: Don't put `fly()` in `Bird`. Create a `FlyingBird` subclass.

```kotlin
open class Bird { /* common bird behavior: eat, sleep */ }
open class FlyingBird : Bird() { fun fly() { /* fly */ } }

class Eagle : FlyingBird()
class Penguin : Bird()  // no fly() — penguins don't fly
```

### Pattern 2: Ignoring Input

```kotlin
// BAD: ReadOnlyList silently ignores add() — violates List contract
class ReadOnlyList<T>(private val items: List<T>) : MutableList<T> {
    override fun add(element: T): Boolean {
        return false  // 💥 silently does nothing — caller expects it to work
    }
    // ... other methods throw or silently fail
}

// Code that does list.add(x) and checks size expects size to increase.
// With ReadOnlyList, it doesn't. LSP violated.
```

**Fix**: Don't implement `MutableList`. Implement `List` (read-only) instead.

### Pattern 3: Changing Behavior Unexpectedly

```kotlin
// BAD: Subclass changes the semantics of the parent
open class BaseRepository {
    open fun save(entity: Entity) {
        db.insert(entity)
    }
}

class CachingRepository : BaseRepository() {
    override fun save(entity: Entity) {
        cache.put(entity.id, entity)
        // 💥 Doesn't actually persist to DB! Caller expects save() to persist.
    }
}

// Code that calls repo.save() expects data to be in the DB.
// With CachingRepository, data is only in cache. LSP violated.
```

**Fix**: `CachingRepository` should call `super.save()` or delegate to the real repository.

```kotlin
class CachingRepository(
    private val delegate: BaseRepository,
    private val cache: Cache
) : BaseRepository() {
    override fun save(entity: Entity) {
        delegate.save(entity)  // persist to DB (honors contract)
        cache.put(entity.id, entity)  // also cache
    }
}
```

---

## How to Detect LSP Violations

### Smell 1: `is` Checks Before Calling
```kotlin
// BAD: Code checks the type before calling — LSP is violated
if (bird is Penguin) {
    // don't call fly()
} else {
    bird.fly()
}
```
If you need to check the type before calling a method, the subtype is not substitutable.

### Smell 2: `when` on Type Instead of Polymorphism
```kotlin
// BAD: Type-checking instead of polymorphism
when (animal) {
    is Dog -> animal.bark()
    is Cat -> animal.meow()
    is Fish -> { /* fish can't make sound */ }
}
```
This means `Fish` doesn't truly substitute for `Animal` — it can't `makeSound()`.

### Smell 3: Empty Overrides or Throwing Exceptions
```kotlin
override fun someMethod() {
    // intentionally empty — this subclass doesn't support this
}
// or
override fun someMethod() {
    throw UnsupportedOperationException()
}
```

### Smell 4: Tests for the Parent Fail for the Child
If you have a test suite for `Rectangle` and it fails when you pass a `Square`, LSP is violated.

---

## Real-World Example: The Ostrich Problem

### Before (Bad)
```kotlin
abstract class Account {
    abstract fun deposit(amount: Double)
    abstract fun withdraw(amount: Double): Double

    // Contract: withdraw reduces balance, returns withdrawn amount
    // Invariant: balance >= 0 (can't go negative)
}

class CheckingAccount : Account() {
    var balance: Double = 0.0

    override fun deposit(amount: Double) { balance += amount }
    override fun withdraw(amount: Double): Double {
        if (balance < amount) return 0.0  // insufficient funds
        balance -= amount
        return amount
    }
}

class FixedDepositAccount : Account() {
    var balance: Double = 0.0
    var isMatured: Boolean = false

    override fun deposit(amount: Double) { balance += amount }

    // 💥 LSP violation: withdraw throws if not matured
    // Parent's contract says withdraw returns Double — child throws exception
    override fun withdraw(amount: Double): Double {
        if (!isMatured) throw IllegalStateException("Cannot withdraw before maturity")
        balance -= amount
        return amount
    }
}

// Code that works with Account:
fun transfer(from: Account, to: Account, amount: Double) {
    val withdrawn = from.withdraw(amount)  // 💥 throws for FixedDepositAccount
    to.deposit(withdrawn)
}
```

### After (Good)
```kotlin
// Separate the abstractions — don't force FixedDeposit into Account
interface Depositable {
    fun deposit(amount: Double)
}

interface Withdrawable {
    fun withdraw(amount: Double): Double
}

// CheckingAccount: can deposit and withdraw
class CheckingAccount : Depositable, Withdrawable {
    var balance: Double = 0.0
    override fun deposit(amount: Double) { balance += amount }
    override fun withdraw(amount: Double): Double { /* ... */ }
}

// FixedDepositAccount: can deposit, but NOT withdraw (until maturity)
class FixedDepositAccount : Depositable {
    var balance: Double = 0.0
    override fun deposit(amount: Double) { balance += amount }
    // No withdraw() — it doesn't implement Withdrawable
}

// Transfer only works with Withdrawable accounts
fun transfer(from: Withdrawable, to: Depositable, amount: Double) {
    val withdrawn = from.withdraw(amount)  // type-safe — can't pass FixedDeposit
    to.deposit(withdrawn)
}
```

Now the type system enforces the contract. You can't accidentally call `withdraw()` on a `FixedDepositAccount` — it doesn't have the method.

---

## The "Is-A" Test

Before inheriting, ask:

> **"Does the subtype truly behave like the supertype in every context where the supertype is used?"**

| Question | If No |
|---|---|
| Can I use the child everywhere the parent is used? | Don't inherit — use composition or a different interface |
| Does the child honor all the parent's promises? | Don't inherit |
| Does the child maintain all the parent's invariants? | Don't inherit |

---

## Key Insight

> **LSP is about behavioral subtyping, not taxonomic subtyping. "A penguin is-a bird" is taxonomy. But if your `Bird` class has `fly()`, then `Penguin` is NOT a behavioral subtype — it can't fly. LSP says: if it can't substitute, don't inherit. The cost of LSP violation is subtle: code that works with the parent breaks silently with the child. These bugs are the hardest to find because the type system says "it's fine" but the behavior says "it's not." The fix: prefer composition over inheritance, and use interfaces to define contracts. Only inherit when the subtype truly honors the parent's full contract.**
