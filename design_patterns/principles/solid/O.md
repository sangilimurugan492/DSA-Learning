# O — Open/Closed Principle (OCP)

> **Software entities should be open for extension, but closed for modification.**

---

## What It Really Means

You should be able to **add new behavior** without **changing existing, tested, working code**. When a new requirement arrives, you write a new class — you don't modify an old one.

- **Open for extension**: You can add new behavior.
- **Closed for modification**: You don't change existing source code.

---

## The Problem: The Growing Switch

```kotlin
// BAD: Every new type requires modifying this class
class DiscountCalculator {
    fun calculateDiscount(customer: Customer): Double {
        return when (customer.type) {
            "regular" -> 0.0
            "member" -> 0.10       // 10% off
            "vip" -> 0.20          // 20% off
            "employee" -> 0.30     // 30% off — ADDED LATER (modified the class)
            "wholesale" -> 0.40    // ADDED LATER (modified again)
            "black_friday" -> 0.50 // ADDED LATER (modified again)
            else -> 0.0
        }
    }
}
```

Every time marketing adds a new customer type, you:
1. Modify `DiscountCalculator` — a class that was already tested and working.
2. Risk breaking existing discount calculations.
3. Re-run all existing tests (even for unrelated types).
4. Potentially introduce a merge conflict (another developer is also modifying this class).

---

## The Solution: Polymorphism (Strategy Pattern)

```kotlin
// Abstract the varying behavior into an interface
interface DiscountStrategy {
    fun calculateDiscount(customer: Customer): Double
}

// Each strategy is a separate class — open for extension
class RegularDiscount : DiscountStrategy {
    override fun calculateDiscount(customer: Customer) = 0.0
}

class MemberDiscount : DiscountStrategy {
    override fun calculateDiscount(customer: Customer) = 0.10
}

class VIPDiscount : DiscountStrategy {
    override fun calculateDiscount(customer: Customer) = 0.20
}

// Adding a new discount type = new class, no modification to existing code
class EmployeeDiscount : DiscountStrategy {
    override fun calculateDiscount(customer: Customer) = 0.30
}

class WholesaleDiscount : DiscountStrategy {
    override fun calculateDiscount(customer: Customer) = 0.40
}

// The calculator is closed for modification — it never changes
class DiscountCalculator {
    private val strategies = mutableMapOf<String, DiscountStrategy>()

    fun register(type: String, strategy: DiscountStrategy) {
        strategies[type] = strategy
    }

    fun calculateDiscount(customer: Customer): Double {
        return strategies[customer.type]?.calculateDiscount(customer) ?: 0.0
    }
}

// Usage:
val calculator = DiscountCalculator().apply {
    register("regular", RegularDiscount())
    register("member", MemberDiscount())
    register("vip", VIPDiscount())
    register("employee", EmployeeDiscount())  // added without touching existing code
}

// Adding "black_friday" later:
calculator.register("black_friday", object : DiscountStrategy {
    override fun calculateDiscount(customer: Customer) = 0.50
})
// No existing class was modified!
```

---

## How OCP Is Achieved

### 1. Abstraction (Interfaces / Abstract Classes)
Define an interface for the behavior that varies. New types implement the interface.

### 2. Polymorphism
The client code calls the interface, not concrete types. The correct implementation is resolved at runtime.

### 3. Dependency Injection
The client receives the abstraction (interface) — it doesn't create concrete types. New types can be injected without changing the client.

```
Without OCP:
  Client → when(type) { "A" → ..., "B" → ..., "C" → ... }
  Add "D" → modify the when statement → modify the client

With OCP:
  Client → strategy.calculate()
  Add "D" → new class implementing Strategy → register it → client unchanged
```

---

## How to Detect OCP Violations

### Smell 1: The Growing `when` / `switch`
```kotlin
when (type) {
    "A" -> handleA()
    "B" -> handleB()
    "C" -> handleC()  // added last sprint
    "D" -> handleD()  // added this sprint
    // ... keeps growing
}
```
Every new case = a modification. Replace with a polymorphic dispatch (map of strategies).

### Smell 2: `if-else` Chains Based on Type
```kotlin
if (shape is Circle) {
    area = PI * shape.radius * shape.radius
} else if (shape is Square) {
    area = shape.side * shape.side
} else if (shape is Triangle) {
    area = 0.5 * shape.base * shape.height
}
```
Every new shape = new `else if`. Replace with `shape.area()` (polymorphism).

### Smell 3: Modifying a Class to Add a Feature
If you're adding a new feature and you have to open an existing class file, OCP is likely violated. Ask: "Can I add this as a new class instead?"

### Smell 4: Tests Break for Unrelated Features
If adding a new discount type breaks the test for an existing type, the code is coupled — OCP is violated.

---

## Real-World Example: Notification System

### Before (Bad)
```kotlin
class NotificationService {
    fun send(notification: Notification) {
        when (notification.channel) {
            "email" -> {
                val smtp = SmtpClient("smtp.gmail.com", 587)
                smtp.send(notification.to, notification.subject, notification.body)
            }
            "sms" -> {
                val twilio = TwilioClient("account_sid", "auth_token")
                twilio.sendSms(notification.to, notification.body)
            }
            "push" -> {
                val fcm = FcmClient("server_key")
                fcm.send(notification.to, notification.title, notification.body)
            }
            // Adding "slack" → modify this class
            // Adding "teams" → modify this class again
        }
    }
}
```

### After (Good)
```kotlin
interface NotificationChannel {
    fun send(notification: Notification)
}

class EmailChannel(private val smtp: SmtpClient) : NotificationChannel {
    override fun send(notification: Notification) {
        smtp.send(notification.to, notification.subject, notification.body)
    }
}

class SmsChannel(private val twilio: TwilioClient) : NotificationChannel {
    override fun send(notification: Notification) {
        twilio.sendSms(notification.to, notification.body)
    }
}

class PushChannel(private val fcm: FcmClient) : NotificationChannel {
    override fun send(notification: Notification) {
        fcm.send(notification.to, notification.title, notification.body)
    }
}

// Adding Slack = new class, no modification to existing code
class SlackChannel(private val slack: SlackClient) : NotificationChannel {
    override fun send(notification: Notification) {
        slack.postMessage(notification.to, notification.body)
    }
}

// The service never changes — it just dispatches to channels
class NotificationService(private val channels: Map<String, NotificationChannel>) {
    fun send(notification: Notification) {
        channels[notification.channel]?.send(notification)
            ?: throw IllegalArgumentException("Unknown channel: ${notification.channel}")
    }
}

// Wiring:
val service = NotificationService(mapOf(
    "email" to EmailChannel(smtpClient),
    "sms" to SmsChannel(twilioClient),
    "push" to PushChannel(fcmClient),
    "slack" to SlackChannel(slackClient)  // added without touching NotificationService
))
```

---

## OCP and Design Patterns

| Pattern | How It Achieves OCP |
|---|---|
| **Strategy** | New algorithm = new strategy class |
| **Observer** | New observer = new subscriber class |
| **Decorator** | New behavior = new decorator class |
| **Factory Method** | New product = new factory subclass |
| **Chain of Responsibility** | New handler = new handler class |
| **Template Method** | New step = new subclass |

All of these patterns exist to satisfy OCP. They let you add behavior by adding classes, not by modifying existing ones.

---

## When OCP Is Over-Applied

```kotlin
// BAD: Abstracting something that never changes
interface GreeterStrategy {
    fun greet(name: String): String
}

class EnglishGreeter : GreeterStrategy {
    override fun greet(name: String) = "Hello, $name!"
}

// There will never be a SpanishGreeter or FrenchGreeter.
// This is premature abstraction — YAGNI.

// GOOD: Just write the code
fun greet(name: String) = "Hello, $name!"
```

OCP should be applied when you **expect** variation. If a behavior will never change, don't abstract it. Apply OCP when the third variation arrives (Rule of Three).

---

## The Cost of OCP

OCP adds indirection (interfaces, factories, registries). This has a cost:
- **More classes**: Each variation is a separate class.
- **More indirection**: Code is harder to trace (which implementation is used?).
- **More boilerplate**: Interface + class + registration.

The benefit:
- **No modification of existing code**: New features don't risk old features.
- **Parallel development**: Two developers can add two new types without conflict.
- **Testability**: Each strategy is tested in isolation.

---

## Key Insight

> **OCP is the principle that makes systems maintainable over time. Without it, every new feature touches existing code → regression risk grows → development slows down → the system becomes "legacy." With OCP, new features are additive — you write new code, not modify old code. The test is simple: "When I add a new type, do I open an existing file?" If yes, OCP is violated. The fix: abstract the varying behavior into an interface, and add new types as new classes.**
