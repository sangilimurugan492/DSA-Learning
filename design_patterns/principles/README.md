# Principles

Software design principles are the foundation. Every design pattern exists to satisfy these principles. Master these first, and patterns become obvious applications rather than memorized rules.

---

## 1. SOLID Principles

SOLID is an acronym for five principles that make software more maintainable, flexible, and scalable. They were introduced by Robert C. Martin (Uncle Bob).

### S — Single Responsibility Principle (SRP)

#### Definition
> A class should have one, and only one, reason to change.

#### What It Means
Every class/module should be responsible for **one** thing. If a class has multiple responsibilities, a change in one responsibility affects the others — creating unintended side effects.

#### The Problem
```kotlin
// BAD: This class does everything — it's a "God Object"
class UserManager {
    fun createUser(data: UserData): User { ... }      // responsibility 1: business logic
    fun saveToDatabase(user: User) { ... }              // responsibility 2: persistence
    fun sendWelcomeEmail(user: User) { ... }            // responsibility 3: notification
    fun generateUserReport(user: User): Report { ... }   // responsibility 4: reporting
}
```
If the email template changes, you modify `UserManager`. If the DB schema changes, you modify `UserManager`. Every change risks breaking unrelated functionality.

#### The Solution
```kotlin
// GOOD: Each class has one responsibility
class UserService(private val repo: UserRepository, private val notifier: Notifier) {
    fun createUser(data: UserData): User {
        val user = User(data)
        repo.save(user)
        notifier.notify(user, WelcomeEmail())
        return user
    }
}

class UserRepository {
    fun save(user: User) { /* DB logic */ }
    fun findById(id: String): User? { /* DB logic */ }
}

class EmailNotifier {
    fun notify(user: User, email: Email) { /* email logic */ }
}

class ReportGenerator {
    fun generate(user: User): Report { /* reporting logic */ }
}
```

#### How to Identify Violations
- A class name contains "And" or "Manager" or "Helper" → likely doing too much.
- You're changing a class for unrelated reasons (UI change + DB change + business rule change).
- A class has more than ~200 lines → review its responsibilities.

#### Key Insight
> **SRP is not about "one method per class." It's about one reason to change. A `UserRepository` can have `save`, `findById`, `delete` — they all change for the same reason (persistence). That's one responsibility.**

---

### O — Open/Closed Principle (OCP)

#### Definition
> Software entities should be open for extension, but closed for modification.

#### What It Means
You should be able to **add new behavior** without **modifying existing code**. When you add a new payment method, you shouldn't have to change the existing `PaymentProcessor` class.

#### The Problem
```kotlin
// BAD: Every new payment type requires modifying this class
class PaymentProcessor {
    fun process(payment: Payment) {
        when (payment.type) {
            "credit_card" -> processCreditCard(payment)
            "paypal" -> processPayPal(payment)
            "google_pay" -> processGooglePay(payment)  // added later — modified the class
            "apple_pay" -> processApplePay(payment)     // added later — modified again
        }
    }
}
```
Every new payment method requires modifying `PaymentProcessor`. This risks breaking existing payment types.

#### The Solution
```kotlin
// GOOD: Open for extension (new PaymentStrategy), closed for modification
interface PaymentStrategy {
    fun process(payment: Payment): Result
}

class CreditCardPayment : PaymentStrategy {
    override fun process(payment: Payment) = Result.Success
}

class PayPalPayment : PaymentStrategy {
    override fun process(payment: Payment) = Result.Success
}

class GooglePayPayment : PaymentStrategy {
    override fun process(payment: Payment) = Result.Success
}

class PaymentProcessor {
    private val strategies = mutableMapOf<String, PaymentStrategy>()

    fun register(type: String, strategy: PaymentStrategy) {
        strategies[type] = strategy
    }

    fun process(payment: Payment): Result {
        val strategy = strategies[payment.type]
            ?: return Result.Error("Unknown payment type")
        return strategy.process(payment)
    }
}
```
Now adding `ApplePayPayment` requires **zero changes** to `PaymentProcessor`. Just register the new strategy.

#### Key Insight
> **OCP is achieved through polymorphism (interfaces/abstract classes). When you see a `when` or `switch` statement that keeps growing, it's an OCP violation — replace it with a strategy/registry.**

---

### L — Liskov Substitution Principle (LSP)

#### Definition
> Subtypes must be substitutable for their base types without altering the correctness of the program.

#### What It Means
If `B` is a subtype of `A`, then anywhere `A` is expected, `B` should work without surprises. The subclass must honor the **contract** of the parent class — same behavior, same expectations, same invariants.

#### The Problem (The Classic Square-Rectangle)
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

// This breaks:
fun test(rect: Rectangle) {
    rect.width = 5
    rect.height = 4
    assert(rect.area() == 20) // FAILS for Square! area = 16
}
```
A `Square` is-a `Rectangle` mathematically, but NOT behaviorally. `Square` violates `Rectangle`'s invariant that width and height are independent.

#### The Solution
Don't force an inheritance relationship that doesn't hold behaviorally. Use a common interface or composition.

```kotlin
// GOOD: Use a common interface
interface Shape {
    fun area(): Int
}

class Rectangle(val width: Int, val height: Int) : Shape {
    override fun area() = width * height
}

class Square(val side: Int) : Shape {
    override fun area() = side * side
}
```

#### LSP Violation Signs
- Subclass throws `NotImplementedException` for a parent method.
- Subclass weakens a precondition (parent accepts null, child doesn't).
- Subclass strengthens a postcondition (parent returns nullable, child returns non-null).
- Subclass changes behavior in a way callers don't expect.

#### Key Insight
> **"Is-a" is not about taxonomy — it's about behavior. A penguin is-a bird, but if your `Bird` class has `fly()`, then `Penguin` violates LSP. Don't inherit just because it sounds right. Inherit only if the subtype truly honors the parent's contract.**

---

### I — Interface Segregation Principle (ISP)

#### Definition
> Clients should not be forced to depend on interfaces they do not use.

#### What It Means
Don't create "fat" interfaces with methods that not all implementers need. Split large interfaces into smaller, focused ones.

#### The Problem
```kotlin
// BAD: Fat interface — not all machines can do everything
interface Machine {
    fun print(document: Document)
    fun scan(document: Document)
    fun fax(document: Document)
}

// A simple printer is forced to implement scan and fax
class SimplePrinter : Machine {
    override fun print(document: Document) { /* print */ }
    override fun scan(document: Document) { throw NotImplementedError() }  // can't scan!
    override fun fax(document: Document) { throw NotImplementedError() }   // can't fax!
}
```

#### The Solution
```kotlin
// GOOD: Segregated interfaces
interface Printer {
    fun print(document: Document)
}

interface Scanner {
    fun scan(document: Document)
}

interface Fax {
    fun fax(document: Document)
}

class SimplePrinter : Printer {
    override fun print(document: Document) { /* print */ }
}

class MultiFunctionMachine : Printer, Scanner, Fax {
    override fun print(document: Document) { /* print */ }
    override fun scan(document: Document) { /* scan */ }
    override fun fax(document: Document) { /* fax */ }
}
```

#### Key Insight
> **ISP is SRP for interfaces. If an interface has methods that some implementers don't need, split it. A class should never be forced to implement a method it can't fulfill.**

---

### D — Dependency Inversion Principle (DIP)

#### Definition
> 1. High-level modules should not depend on low-level modules. Both should depend on abstractions.
> 2. Abstractions should not depend on details. Details should depend on abstractions.

#### What It Means
The business logic (high-level) should not depend on the database or email service (low-level). Both should depend on an interface. This allows swapping implementations without touching business logic.

#### The Problem
```kotlin
// BAD: High-level depends on low-level (concrete class)
class OrderService {
    private val paymentGateway = StripePaymentGateway()  // hardcoded dependency
    private val emailService = SmtpEmailService()          // hardcoded dependency

    fun placeOrder(order: Order) {
        paymentGateway.charge(order.total)
        emailService.send(order.customerEmail, "Order confirmed")
    }
}
```
If you want to switch from Stripe to PayPal, or from SMTP to SendGrid, you must modify `OrderService`.

#### The Solution
```kotlin
// GOOD: Both depend on abstractions
interface PaymentGateway {
    fun charge(amount: Double): Result
}

interface EmailService {
    fun send(to: String, body: String)
}

class OrderService(
    private val paymentGateway: PaymentGateway,    // depends on abstraction
    private val emailService: EmailService          // depends on abstraction
) {
    fun placeOrder(order: Order) {
        paymentGateway.charge(order.total)
        emailService.send(order.customerEmail, "Order confirmed")
    }
}

// Low-level depends on the same abstraction
class StripePaymentGateway : PaymentGateway {
    override fun charge(amount: Double) = Result.Success
}

class PayPalPaymentGateway : PaymentGateway {
    override fun charge(amount: Double) = Result.Success
}

// Wiring (at the composition root):
val orderService = OrderService(
    paymentGateway = StripePaymentGateway(),
    emailService = SmtpEmailService()
)
```

#### Key Insight
> **DIP is the principle behind dependency injection (DI). The high-level module defines the interface it needs; the low-level module implements it. This inverts the dependency — the low-level now depends on the high-level's abstraction. This is why DI frameworks (Spring, Dagger, Koin) exist.**

---

## 2. DRY (Don't Repeat Yourself)

### Definition
> Every piece of knowledge must have a single, unambiguous, authoritative representation within a system.

### What It Means
Don't duplicate logic. If the same business rule appears in 3 places, a change requires updating all 3. Miss one → bug.

### The Problem
```kotlin
// BAD: Tax calculation duplicated
class InvoiceService {
    fun calculateTotal(order: Order): Double {
        val subtotal = order.items.sumOf { it.price }
        val tax = subtotal * 0.18  // tax logic here
        return subtotal + tax
    }
}

class ReportService {
    fun generateReport(order: Order): Report {
        val subtotal = order.items.sumOf { it.price }
        val tax = subtotal * 0.18  // SAME tax logic here
        return Report(subtotal, tax, subtotal + tax)
    }
}
```
If the tax rate changes from 18% to 20%, you must update both. Miss one → incorrect invoices or reports.

### The Solution
```kotlin
// GOOD: Single source of truth
class TaxCalculator {
    private val taxRate = 0.18

    fun calculate(subtotal: Double): Double = subtotal * taxRate
}

class InvoiceService(private val taxCalculator: TaxCalculator) {
    fun calculateTotal(order: Order): Double {
        val subtotal = order.items.sumOf { it.price }
        return subtotal + taxCalculator.calculate(subtotal)
    }
}
```

### DRY Is Not Just About Code
- **Knowledge duplication**: Business rules duplicated across services.
- **Data duplication**: Same data stored in multiple places (denormalization is sometimes intentional, but must be managed).
- **Representation duplication**: Same concept modeled differently in different layers.

### When DRY Goes Wrong (Over-DRY)
```kotlin
// BAD: Forced abstraction that's hard to understand
fun <T, R> processTransformValidate(
    input: T, validator: (T) -> Boolean,
    transformer: (T) -> R, processor: (R) -> Unit
): Boolean {
    if (!validator(input)) return false
    val transformed = transformer(input)
    processor(transformed)
    return true
}
```
This is "too DRY" — it abstracts unrelated code into a generic function that's harder to understand than the duplication would be.

### Key Insight
> **DRY is about knowledge, not code. Two identical-looking code blocks may represent different knowledge (e.g., tax calculation in two countries that happen to have the same rate). Duplicating them is correct — they may diverge. DRY only when the knowledge is the same.**

---

## 3. KISS (Keep It Simple, Stupid)

### Definition
> Simplicity is a key goal in design. The simplest solution that works is usually the best.

### What It Means
Don't add complexity unless it's justified. Every abstraction, pattern, and layer adds cognitive load. The more moving parts, the more bugs.

### The Problem
```kotlin
// BAD: Over-engineered for a simple task
interface StringFormatter {
    fun format(input: String): String
}

class UpperCaseFormatter : StringFormatter {
    override fun format(input: String) = input.uppercase()
}

class FormatterFactory {
    fun create(type: String): StringFormatter = when (type) {
        "upper" -> UpperCaseFormatter()
        else -> throw IllegalArgumentException()
    }
}

class FormatterService(private val factory: FormatterFactory) {
    fun process(text: String): String {
        val formatter = factory.create("upper")
        return formatter.format(text)
    }
}

// Usage:
val service = FormatterService(FormatterFactory())
val result = service.process("hello") // "HELLO"
```

### The Solution
```kotlin
// GOOD: Simple and direct
val result = "hello".uppercase() // "HELLO"
```

### When Simplicity Is Not Enough
KISS doesn't mean "never use patterns." It means:
- Start simple. Add complexity only when the simple solution breaks.
- A 5-line function is better than a 5-class hierarchy if it does the same thing.
- Don't build a framework for a problem that occurs once.

### Key Insight
> **"There are two ways of constructing a software design: one way is to make it so simple that there are obviously no deficiencies, and the other way is to make it so complicated that there are no obvious deficiencies." — C.A.R. Hoare. Choose the first way.**

---

## 4. YAGNI (You Aren't Gonna Need It)

### Definition
> Never implement functionality until you actually need it.

### What It Means
Don't build for hypothetical future requirements. Most predicted future needs never materialize, and the code you wrote "just in case" becomes dead weight that must be maintained.

### The Problem
```kotlin
// BAD: Building for a future that may never come
class User {
    val id: String
    val name: String
    val email: String
    val phone: String?          // not used yet
    val address: String?        // not used yet
    val preferences: Map<String, Any>?  // not used yet
    val metadata: JSONObject?    // not used yet
    val legacyId: String?        // not used yet
    val temporaryFlag: Boolean = false  // not used yet
}
```
You added 6 fields "for future use." They're never used. They add confusion, null checks, and maintenance burden.

### The Solution
```kotlin
// GOOD: Only what you need now
class User(
    val id: String,
    val name: String,
    val email: String
)
// Add fields when the requirement actually arrives.
```

### YAGNI vs. Forward-Thinking
YAGNI doesn't mean "never think about the future." It means:
- **Design for change** (use interfaces, DI) but don't **implement for hypothetical change**.
- Make it easy to add a field later, but don't add it now.
- Build the architecture to be extensible, but don't build the extension.

### Key Insight
> **"The best code is no code. The second best code is code that's easy to delete." Every line of code you write is a line you must maintain, test, and debug. If you don't need it now, don't write it. You can always add it later — but you can't un-write complexity.**

---

## 5. Composition Over Inheritance

### Definition
> Favor composing objects from smaller parts over building deep inheritance hierarchies.

### The Problem with Inheritance
```kotlin
// BAD: Deep inheritance hierarchy — rigid and fragile
open class Animal {
    open fun move() = "moving"
}

open class Bird : Animal() {
    override fun move() = "flying"
    fun layEgg() = "laying egg"
}

open class Penguin : Bird() {
    override fun move() = "swimming"  // can't fly!
    // But I inherited from Bird which says "flying"...
    // Now I'm overriding everything. Why am I a Bird?
}

open class FlyingFish : Animal() {
    override fun move() = "swimming and flying"
    // Is it a fish? A bird? Both? Neither?
}
```

Inheritance problems:
1. **Rigid**: You're locked into the parent's interface. Can't change it without breaking all subclasses.
2. **Fragile**: Change in parent breaks subclasses (the "fragile base class problem").
3. **Explosion**: To support all combinations (fly + swim + walk), you need a class for each combination.
4. **Single inheritance**: Most languages allow only one parent. What if a `Bat` is both a `Mammal` and a `FlyingThing`?

### The Solution: Composition
```kotlin
// GOOD: Compose behaviors
interface MoveBehavior {
    fun move(): String
}

class Flying : MoveBehavior {
    override fun move() = "flying"
}

class Swimming : MoveBehavior {
    override fun move() = "swimming"
}

class Walking : MoveBehavior {
    override fun move() = "walking"
}

class Animal(private val moveBehavior: MoveBehavior) {
    fun move() = moveBehavior.move()
}

// Now any animal can have any behavior — no hierarchy needed
val eagle = Animal(Flying())
val penguin = Animal(Swimming())
val bat = Animal(Flying())  // bat is a mammal that flies — no problem
```

### When Inheritance Is OK
- True "is-a" relationships that are stable: `ArrayList` is-a `List`.
- Template Method pattern (parent defines algorithm skeleton, child fills in steps).
- When the hierarchy is shallow (1-2 levels) and unlikely to change.

### Key Insight
> **"Inheritance is white-box reuse (you see the parent's internals). Composition is black-box reuse (you use the object's interface). Composition is more flexible: you can change behavior at runtime by swapping a component. With inheritance, you're stuck with your parent forever.**

---

## 6. Law of Demeter (Principle of Least Knowledge)

### Definition
> An object should only talk to its immediate friends, not friends of friends.

### What It Means
A method `M` of object `O` should only call methods of:
1. `O` itself
2. Parameters passed to `M`
3. Objects created within `M`
4. Direct components of `O`

### The Problem (Train Wreck)
```kotlin
// BAD: Reaching through multiple objects
val city = order.customer.address.city.name  // train wreck
val discount = order.customer.account.loyaltyProgram.discount
```
This code knows about `Customer`, `Address`, `City`, `Account`, `LoyaltyProgram`, and `Discount`. If any of these change, this code breaks. It's tightly coupled to the entire object graph.

### The Solution
```kotlin
// GOOD: Ask for what you need directly
val city = order.getShippingCity()
val discount = order.getLoyaltyDiscount()

class Order(val customer: Customer) {
    fun getShippingCity(): String = customer.getShippingCity()
    fun getLoyaltyDiscount(): Double = customer.getLoyaltyDiscount()
}

class Customer(val address: Address, val account: Account) {
    fun getShippingCity(): String = address.cityName()
    fun getLoyaltyDiscount(): Double = account.loyaltyDiscount()
}
```

### When It's OK to Violate
- **Data structures (DTOs)**: `data class User(val name: String, val address: Address)` — accessing `user.address.city` is fine for a data structure. The law applies to **behavior**, not data.
- **Fluent APIs / Builders**: `builder.setA(1).setB(2).setC(3)` — this is method chaining, not Demeter violation.

### Key Insight
> **The Law of Demeter reduces coupling. If you only talk to your direct friends, you don't know about their internal structure. When their internals change, you don't break. The trade-off: you may write more wrapper methods. That's the cost of decoupling.**

---

## 7. Fail Fast

### Definition
> Errors should be detected and reported as early as possible, not swallowed or deferred.

### The Problem
```kotlin
// BAD: Silent failure — error is swallowed
fun parseAge(input: String): Int {
    return try {
        input.toInt()
    } catch (e: NumberFormatException) {
        0  // silently return 0 — caller has no idea parsing failed
    }
}

// BAD: Late failure — error surfaces far from the cause
fun processOrder(order: Order) {
    val items = order.items  // items is null, but we don't check
    val total = items.sumOf { it.price }  // NullPointerException here
    // The real problem was upstream — order was created without items
    // But the error surfaces here, far from the cause
}
```

### The Solution
```kotlin
// GOOD: Fail immediately with a clear message
fun parseAge(input: String): Int {
    return input.toIntOrNull()
        ?: throw IllegalArgumentException("Invalid age: '$input' is not a number")
}

// GOOD: Validate at the boundary
class Order private constructor(
    val id: String,
    val items: List<Item>
) {
    companion object {
        fun create(id: String, items: List<Item>): Order {
            require(id.isNotBlank()) { "Order ID cannot be blank" }
            require(items.isNotEmpty()) { "Order must have at least one item" }
            return Order(id, items)
        }
    }
}
```

### Key Insight
> **A bug detected at compile time costs $1. At runtime in development, $10. In QA, $100. In production, $1000. Fail fast = detect early = cheaper to fix. Never swallow exceptions silently. Never return null when you can throw. Never defer validation.**

---

## 8. Encapsulate What Varies

### Definition
> Identify the aspects of your code that vary and separate them from what stays the same.

### What It Means
This is the core principle behind most design patterns. If something changes frequently (payment methods, sorting algorithms, notification channels), extract it into its own abstraction. The stable parts depend on the abstraction, not the varying details.

### Example
```kotlin
// What varies: the sorting algorithm
// What stays the same: the need to sort

// Extract the varying part:
interface SortStrategy<T> {
    fun sort(items: MutableList<T>, comparator: Comparator<T>)
}

class QuickSort<T> : SortStrategy<T> {
    override fun sort(items: MutableList<T>, comparator: Comparator<T>) { /* ... */ }
}

class MergeSort<T> : SortStrategy<T> {
    override fun sort(items: MutableList<T>, comparator: Comparator<T>) { /* ... */ }
}

// The stable part depends on the abstraction:
class SortedList<T>(private val strategy: SortStrategy<T>) {
    private val items = mutableListOf<T>()

    fun add(item: T) {
        items.add(item)
        strategy.sort(items, naturalOrder())
    }
}
```

### Key Insight
> **This is the DNA of all patterns. Strategy pattern? Encapsulate the varying algorithm. Factory? Encapsulate the varying object creation. Observer? Encapsulate the varying notification mechanism. If you understand "encapsulate what varies," you understand the motivation behind every GoF pattern.**
