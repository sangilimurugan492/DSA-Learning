# D — Dependency Inversion Principle (DIP)

> **1. High-level modules should not depend on low-level modules. Both should depend on abstractions.**
> **2. Abstractions should not depend on details. Details should depend on abstractions.**

---

## What It Really Means

The **business logic** (high-level) should not depend on the **database, email service, or external API** (low-level). Both should depend on an **interface** (abstraction).

- **High-level module**: Contains business rules and policies (e.g., `OrderService`).
- **Low-level module**: Contains infrastructure and details (e.g., `PostgresRepository`, `SmtpEmailSender`).
- **Abstraction**: An interface defined by the high-level module (e.g., `OrderRepository`, `EmailSender`).

The dependency is **inverted**: instead of the high-level depending on the low-level, the low-level depends on the high-level's abstraction.

```
Without DIP:
  OrderService → PostgresRepository (high depends on low)

With DIP:
  OrderService → OrderRepository (interface, owned by high-level)
  PostgresRepository → OrderRepository (low-level implements the interface)
  
  The arrow is inverted: low-level now depends on high-level's abstraction.
```

---

## The Problem: Hardcoded Dependencies

```kotlin
// BAD: High-level depends directly on low-level (concrete classes)
class OrderService {
    private val paymentGateway = StripePaymentGateway()  // hardcoded
    private val emailSender = SmtpEmailSender()             // hardcoded
    private val repo = PostgresOrderRepository()            // hardcoded

    fun placeOrder(order: Order) {
        repo.save(order)
        paymentGateway.charge(order.total)
        emailSender.send(order.customerEmail, "Order confirmed")
    }
}
```

### Why This Is Bad
1. **Can't swap implementations**: Switch from Stripe to PayPal? You must modify `OrderService`.
2. **Can't test**: Want to test `OrderService` without hitting Stripe's API? You can't — it's hardcoded.
3. **Violation of OCP**: Every new payment gateway requires modifying `OrderService`.
4. **Violation of SRP**: `OrderService` now knows about Stripe, SMTP, and PostgreSQL — it has 3+ reasons to change.
5. **Tight coupling**: If `StripePaymentGateway`'s constructor changes, `OrderService` must change.

---

## The Solution: Depend on Abstractions

```kotlin
// 1. Define abstractions (interfaces) — owned by the high-level module
interface PaymentGateway {
    fun charge(amount: Double): PaymentResult
}

interface EmailSender {
    fun send(to: String, subject: String, body: String)
}

interface OrderRepository {
    fun save(order: Order)
    fun findById(id: String): Order?
}

// 2. High-level module depends on abstractions — NOT concrete classes
class OrderService(
    private val paymentGateway: PaymentGateway,    // abstraction
    private val emailSender: EmailSender,          // abstraction
    private val repo: OrderRepository              // abstraction
) {
    fun placeOrder(order: Order) {
        repo.save(order)
        val result = paymentGateway.charge(order.total)
        if (result.isSuccess) {
            emailSender.send(order.customerEmail, "Order confirmed", "Your order #${order.id} is confirmed.")
        }
    }
}

// 3. Low-level modules implement the abstractions — they depend on the interface
class StripePaymentGateway(private val apiKey: String) : PaymentGateway {
    override fun charge(amount: Double): PaymentResult {
        // Stripe-specific API call
        return PaymentResult.Success("stripe_txn_123")
    }
}

class PayPalPaymentGateway(private val clientId: String, private val secret: String) : PaymentGateway {
    override fun charge(amount: Double): PaymentResult {
        // PayPal-specific API call
        return PaymentResult.Success("paypal_txn_456")
    }
}

class SmtpEmailSender(private val host: String, private val port: Int) : EmailSender {
    override fun send(to: String, subject: String, body: String) {
        // SMTP-specific implementation
    }
}

class PostgresOrderRepository(private val db: Database) : OrderRepository {
    override fun save(order: Order) {
        db.execute("INSERT INTO orders ...", order)
    }

    override fun findById(id: String): Order? {
        return db.query("SELECT * FROM orders WHERE id = ?", id)
    }
}
```

### Wiring (Composition Root)
```kotlin
// At the application entry point, wire everything together
fun main() {
    // Create low-level implementations
    val paymentGateway = StripePaymentGateway("sk_test_123")
    val emailSender = SmtpEmailSender("smtp.gmail.com", 587)
    val repo = PostgresOrderRepository(database)

    // Inject into high-level module
    val orderService = OrderService(paymentGateway, emailSender, repo)

    // Use it
    orderService.placeOrder(Order("1", "alice@example.com", 99.99))
}

// Want to switch to PayPal? Change ONE line:
val paymentGateway = PayPalPaymentGateway("client_id", "secret")
// OrderService doesn't change. Its tests don't change. Its callers don't change.
```

---

## Why It's Called "Inversion"

### Normal Dependency Direction (Without DIP)
```
High-level → Low-level
  OrderService → StripePaymentGateway (direct dependency)
  The high-level module "knows about" the low-level module.
```

### Inverted Dependency Direction (With DIP)
```
High-level ← Abstraction ← Low-level
  OrderService → PaymentGateway (interface)
  StripePaymentGateway → PaymentGateway (implements interface)

  The low-level module now depends on the high-level's abstraction.
  The dependency arrow is INVERTED.
```

The high-level module **owns** the interface. It says "I need something that can `charge()`. I don't care how." The low-level module adapts to that interface.

---

## How to Detect DIP Violations

### Smell 1: `new` Keyword Inside Business Logic
```kotlin
class OrderService {
    private val repo = PostgresOrderRepository()  // 💥 `new` inside business logic
    private val gateway = StripePaymentGateway()  // 💥 direct dependency
}
```
If you see `new` creating infrastructure objects inside business logic, DIP is violated. Inject them instead.

### Smell 2: Importing Infrastructure in Domain Layer
```kotlin
// BAD: Domain layer imports PostgreSQL driver
import org.postgresql.Driver

class OrderService {
    fun placeOrder(order: Order) {
        val conn = DriverManager.getConnection("jdbc:postgresql://...")  // 💥
    }
}
```
The domain layer should never import database drivers, HTTP clients, or email libraries.

### Smell 3: Can't Unit Test Without External Resources
```kotlin
// If you can't test OrderService without a running PostgreSQL or Stripe API,
// DIP is violated. The service is tightly coupled to concrete implementations.
```

### Smell 4: Changing Infrastructure Requires Changing Business Logic
If switching from Redis to Memcached requires modifying `CacheService`, DIP is violated. It should only require writing a new `Cache` implementation.

---

## Real-World Example: Refactoring a Notification System

### Before (Bad)
```kotlin
// BAD: Everything is hardcoded — can't test, can't swap
class OrderProcessor {
    private val db = MySQLDatabase("localhost", 3306, "shop", "root", "password")
    private val emailClient = JavaMailClient("smtp.gmail.com", 587)
    private val smsClient = TwilioClient("AC123", "token")
    private val logger = FileLogger("/var/log/app.log")

    fun process(order: Order) {
        db.save(order)
        emailClient.send(order.email, "Confirmed", "...")
        if (order.phone != null) {
            smsClient.send(order.phone, "Order confirmed")
        }
        logger.info("Processed order ${order.id}")
    }
}

// Testing this is impossible without MySQL, SMTP, Twilio, and a filesystem.
// Switching to PostgreSQL requires modifying OrderProcessor.
// Switching to SendGrid requires modifying OrderProcessor.
```

### After (Good)
```kotlin
// Abstractions — owned by the high-level module
interface OrderStore {
    fun save(order: Order)
}

interface Notifier {
    fun notify(recipient: String, subject: String, body: String)
}

interface Logger {
    fun info(message: String)
}

// High-level module: depends only on abstractions
class OrderProcessor(
    private val store: OrderStore,
    private val emailNotifier: Notifier,
    private val smsNotifier: Notifier?,
    private val logger: Logger
) {
    fun process(order: Order) {
        store.save(order)
        emailNotifier.notify(order.email, "Confirmed", "Your order is confirmed.")
        smsNotifier?.notify(order.phone!!, "Order confirmed")
        logger.info("Processed order ${order.id}")
    }
}

// Low-level implementations: depend on the abstractions
class MySQLOrderStore(private val db: MySQLDatabase) : OrderStore {
    override fun save(order: Order) = db.save(order)
}

class PostgresOrderStore(private val db: PostgresDatabase) : OrderStore {
    override fun save(order: Order) = db.save(order)
}

class EmailNotifier(private val client: JavaMailClient) : Notifier {
    override fun notify(recipient: String, subject: String, body: String) {
        client.send(recipient, subject, body)
    }
}

class SmsNotifier(private val client: TwilioClient) : Notifier {
    override fun notify(recipient: String, subject: String, body: String) {
        client.send(recipient, body)
    }
}

class FileLogger(private val path: String) : Logger {
    override fun info(message: String) { /* write to file */ }
}

// Testing: use fake implementations — no external resources needed
class InMemoryOrderStore : OrderStore {
    val orders = mutableListOf<Order>()
    override fun save(order: Order) = orders.add(order)
}

class FakeNotifier : Notifier {
    val sent = mutableListOf<Triple<String, String, String>>()
    override fun notify(recipient: String, subject: String, body: String) {
        sent.add(Triple(recipient, subject, body))
    }
}

class FakeLogger : Logger {
    val messages = mutableListOf<String>()
    override fun info(message: String) = messages.add(message)
}

// Test:
val store = InMemoryOrderStore()
val emailNotifier = FakeNotifier()
val smsNotifier = FakeNotifier()
val logger = FakeLogger()

val processor = OrderProcessor(store, emailNotifier, smsNotifier, logger)
processor.process(Order("1", "alice@example.com", "1234567890", 99.99))

assert(store.orders.size == 1)
assert(emailNotifier.sent.size == 1)
assert(smsNotifier.sent.size == 1)
assert(logger.messages.contains("Processed order 1"))
// No MySQL, no SMTP, no Twilio needed. Pure unit test. Fast. Reliable.
```

---

## Dependency Injection (DI) vs Dependency Inversion (DIP)

These are related but different:

| Dependency Inversion (DIP) | Dependency Injection (DI) |
|---|---|
| A **principle**: depend on abstractions | A **technique**: provide dependencies from outside |
| "What" to depend on (interfaces) | "How" to provide dependencies (constructor, setter, field) |
| The goal | The mechanism to achieve the goal |

```kotlin
// DIP: OrderService depends on OrderRepository (interface), not PostgresRepository
// DI: OrderRepository is injected via constructor (not created inside)

class OrderService(
    private val repo: OrderRepository  // DIP (interface) + DI (constructor injection)
)

// DI without DIP (less ideal):
class OrderService(
    private val repo: PostgresOrderRepository  // DI (injected) but no DIP (concrete class)
)
```

### Types of DI

```kotlin
// 1. Constructor injection (preferred — immutable, testable)
class OrderService(private val repo: OrderRepository) { ... }

// 2. Setter injection (flexible but mutable)
class OrderService {
    lateinit var repo: OrderRepository
    fun setRepository(repo: OrderRepository) { this.repo = repo }
}

// 3. Field injection (framework-managed, e.g., Spring @Autowired)
class OrderService {
    @Autowired
    private lateinit var repo: OrderRepository  // not recommended — hides dependencies
}

// 4. Method injection
class OrderService {
    fun process(order: Order, repo: OrderRepository) { ... }
}
```

**Constructor injection is preferred** because:
- Dependencies are explicit (you can see them in the constructor).
- The object is immutable after construction.
- The object can't be in an invalid state (missing dependency).

---

## DIP and Frameworks (Spring, Dagger, Koin)

DI frameworks automate the wiring:

```kotlin
// Spring (Java/Kotlin):
@Service
class OrderService(
    @Autowired private val repo: OrderRepository,  // Spring injects the implementation
    @Autowired private val gateway: PaymentGateway
)

@Repository
class PostgresOrderRepository : OrderRepository { ... }  // Spring auto-wires this

// Koin (Kotlin):
val appModule = module {
    single<OrderRepository> { PostgresOrderRepository(get()) }
    single<PaymentGateway> { StripePaymentGateway("sk_test_123") }
    single { OrderService(get(), get()) }
}

// Dagger (Android):
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindOrderRepository(impl: PostgresOrderRepository): OrderRepository

    @Binds
    abstract fun bindPaymentGateway(impl: StripePaymentGateway): PaymentGateway
}
```

The framework creates the objects and injects them. The business logic still depends on interfaces — the framework handles the wiring.

---

## The Composition Root

The **composition root** is the single place in the application where dependencies are wired together. It's the only place that knows about concrete implementations.

```
┌─────────────────────────────────────────────┐
│  Composition Root (main() / Application)   │
│  ┌─────────────┐  ┌──────────────────────┐ │
│  │ Knows about │  │ Creates:             │ │
│  │ everything  │  │  StripePaymentGateway│ │
│  │             │  │  PostgresRepository   │ │
│  │ Wires:      │  │  SmtpEmailSender     │ │
│  │  OrderService(  │                      │ │
│  │    Stripe,   │  │ Injects into:        │ │
│  │    Postgres,│  │  OrderService        │ │
│  │    Smtp     │  │  UserController      │ │
│  │  )          │  │                      │ │
│  └─────────────┘  └──────────────────────┘ │
└─────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│  Business Logic (depends only on interfaces) │
│  OrderService → OrderRepository (interface)  │
│  OrderService → PaymentGateway (interface)   │
│  No knowledge of Stripe, Postgres, SMTP      │
└─────────────────────────────────────────────┘
```

The composition root is the **only** place that imports concrete classes. Everything else depends on interfaces.

---

## Key Insight

> **DIP is the principle that makes systems testable and swappable. Without it, business logic is welded to infrastructure — you can't test without a database, and you can't swap Stripe for PayPal. With DIP, the business logic defines what it needs (interfaces), and infrastructure adapts to those interfaces. The dependency is inverted: infrastructure depends on business logic, not the other way around. This is why DI frameworks (Spring, Dagger, Koin) exist — to automate the wiring at the composition root. The test: "Can I unit test my business logic without any external resource (DB, network, file system)?" If yes, DIP is being followed. If no, you have hardcoded dependencies that need to be inverted.**
