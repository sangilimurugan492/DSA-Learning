# Inline Classes & Value Classes

## 📖 Explanation

Value classes (formerly inline classes) allow wrapping a single value without the runtime overhead of an object allocation. They are erased at compile time to their underlying type, but provide type safety at the API level.

### Declaration
```kotlin
@JvmInline
value class UserId(val value: Long)
```

- Must have exactly **one** `val` property in the primary constructor.
- `@JvmInline` annotation is required.
- Cannot have `init` blocks with side effects (in older versions), but can have computed properties.

### Why Use Value Classes?
- **Type safety**: Prevent mixing up `Long` IDs like `UserId` vs `OrderId`.
- **Zero overhead**: At runtime, the wrapper is erased — no extra allocation.
- **Readability**: APIs become self-documenting.

### Under the Hood
- In most cases, the value class is compiled to the underlying type (e.g., `Long`).
- Boxing occurs only when the value class is used as a nullable type, in generics, or stored in a collection.

### Value Class with Methods
```kotlin
@JvmInline
value class Email(val value: String) {
    fun isValid(): Boolean = value.contains("@")
    val domain: String get() = value.substringAfter("@")
}
```

### Value Class vs Type Alias
| Feature         | `value class`           | `typealias`              |
|-----------------|-------------------------|--------------------------|
| Type safety     | ✅ Distinct type        | ❌ Just an alias         |
| Runtime overhead| None (erased)           | None                     |
| Can have methods| ✅ Yes                  | ❌ No                    |
| Can have properties | ✅ Yes              | ❌ No                    |

### Value Class vs Data Class
| Feature         | `value class`           | `data class`             |
|-----------------|-------------------------|--------------------------|
| Properties      | Exactly 1               | 1 or more                |
| Overhead        | Zero (erased)           | Object allocation        |
| `equals`/`hashCode` | Auto-generated     | Auto-generated           |
| `copy()`        | ❌ No                   | ✅ Yes                   |

---

## 🧪 Code Example

```kotlin
@JvmInline
value class UserId(val value: Long) {
    fun isValid(): Boolean = value > 0
}

@JvmInline
value class OrderId(val value: Long)

@JvmInline
value class Email(val value: String) {
    fun isValid(): Boolean = value.contains("@") && value.contains(".")
    val domain: String get() = value.substringAfter("@")
}

@JvmInline
value class Password(val value: String) {
    fun strength(): String = when {
        value.length < 6 -> "Weak"
        value.length < 10 -> "Medium"
        else -> "Strong"
    }
}

fun main() {
    // Type-safe IDs — can't mix UserId and OrderId
    val userId = UserId(12345L)
    val orderId = OrderId(67890L)

    println("User ID: ${userId.value}")
    println("Is valid: ${userId.isValid()}")

    // Email with computed properties
    val email = Email("alice@example.com")
    println("Email valid: ${email.isValid()}")
    println("Domain: ${email.domain}")

    // Password strength
    val weakPass = Password("123")
    val strongPass = Password("SuperSecure123!")
    println("Weak password strength: ${weakPass.strength()}")
    println("Strong password strength: ${strongPass.strength()}")

    // Function with type-safe parameter
    fun findUser(id: UserId) = "User(${id.value})"
    fun findOrder(id: OrderId) = "Order(${id.value})"

    println(findUser(userId))
    println(findOrder(orderId))
    // findUser(orderId)  // ❌ Compilation error — type mismatch!
}
```

### Output
```
User ID: 12345
Is valid: true
Email valid: true
Domain: example.com
Weak password strength: Weak
Strong password strength: Strong
User(12345)
Order(67890)
```

---

## ❓ Interview Questions

1. **What is a value class and why use it?**
   - A value class wraps a single value with zero runtime overhead (erased at compile time). It provides type safety — e.g., `UserId` and `OrderId` are distinct types even though both wrap `Long`.

2. **What is the difference between `value class` and `typealias`?**
   - `typealias` is just a name alias — no type safety (both resolve to the same type). `value class` creates a distinct type with its own methods and properties.

3. **When does boxing occur for value classes?**
   - Boxing occurs when the value class is used as a nullable type, stored in a generic collection, or used as an interface. In all other cases, it's erased to the underlying type.

4. **Can a value class have multiple properties?**
   - No. It must have exactly one `val` property in the primary constructor. However, it can have computed properties (with getters) and methods.

5. **What is the `@JvmInline` annotation?**
   - It marks the class as an inline (value) class. It's mandatory for value classes. It tells the compiler to erase the wrapper and use the underlying type at runtime.

---

## 🔗 Related Topics
- [Generics](Generics.md)
- [OOP](../intermediate/OOP.md)
