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

6. **When does boxing occur for value classes and how does it affect performance?**
    - Boxing occurs when a value class is used in contexts where the compiler can't guarantee the underlying type: (1) **Nullable value class** — `val id: UserId? = null` — the compiler needs to distinguish between null and the value, so it boxes. (2) **Generic collections** — `List<UserId>` — generics use type erasure, so the compiler boxes to `Object`. (3) **As an interface** — if the value class implements an interface, it's boxed when used as the interface type. (4) **Varargs** — `vararg items: UserId` — arrays require boxing. (5) **Arrays** — `Array<UserId>` — always boxed. In all other cases (direct use, function parameters, return types, local variables), the value class is erased to the underlying type — zero overhead. Performance: unboxed usage is as fast as primitives. Boxed usage creates a wrapper object — same as `Integer` vs `int`. To minimize boxing: avoid nullable value classes, avoid putting value classes in generic collections, and avoid using them as interface instances. Despite boxing in some cases, value classes still provide type safety benefits.

7. **How do value classes compare to `typealias` and when should you use each?**
    - **Value class** (`@JvmInline value class UserId(val value: Long)`): creates a **distinct type** — `UserId` and `OrderId` are different types even though both wrap `Long`. The compiler prevents mixing them: `fun findUser(id: UserId)` — can't pass an `OrderId`. Zero runtime overhead (erased to `Long`). Use when: you need type safety to prevent mixing similar primitive types. **Typealias** (`typealias UserId = Long`): just a **name alias** — `UserId` and `Long` are the same type. No type safety — you can pass `Long` where `UserId` is expected. Zero overhead and zero type safety. Use when: you want readability but don't need type safety. Example: `typealias ClickHandler = (View) -> Unit` — just a readability alias. **Rule**: use value classes for type-safe IDs (UserId, OrderId, EmailAddress), typealias for readability (function types, complex generic types). Value classes have a slight compile-time cost but provide runtime type safety.

8. **Can value classes have methods and computed properties?**
    - Yes! Value classes can have methods, computed properties, and implement interfaces. Methods: `@JvmInline value class Email(val value: String) { fun isValid(): Boolean = value.contains("@"); val domain: String get() = value.substringAfter("@"); val isGmail: Boolean get() = domain == "gmail.com" }`. Usage: `val email = Email("user@gmail.com"); email.isValid()` → true; `email.domain` → "gmail.com". The methods are compiled to static functions that take the underlying type — no object allocation. **Limitations**: (1) Only one `val` property in the primary constructor. (2) Cannot have `var` properties or backing fields (except the single constructor property). (3) Cannot be `open`, `abstract`, `sealed`, or `inner`. (4) Can implement interfaces (but boxing occurs when used as the interface type). (5) Can have `init` blocks. (6) Can have companion objects. (7) Can have secondary constructors (must delegate to primary). Use value classes to add domain logic to primitive types — `EmailAddress`, `Url`, `Percentage`, `Temperature`.

9. **How do value classes work with equals and hashCode?**
    - Value classes auto-generate `equals` and `hashCode` based on the underlying property. Two value class instances are equal if their underlying values are equal: `UserId(1) == UserId(1)` → true. `UserId(1) == UserId(2)` → false. `UserId(1) == OrderId(1)` → compile error (different types). The generated `equals` compares the underlying values: `fun equals(other: Any?): Boolean = other is UserId && value == other.value`. `hashCode` is `value.hashCode()`. This means value classes work correctly in `Set` and as `Map` keys — `setOf(UserId(1))` deduplicates based on the underlying value. When boxed (in collections), the boxed object's `equals`/`hashCode` delegates to the underlying value. You can override `equals`/`hashCode` manually if needed, but it's rarely necessary — the auto-generated ones are correct. Always use `==` (which calls `equals`) for comparison, not `===` (referential equality — undefined for boxed instances).

10. **How do value classes interoperate with Java?**
    - Value classes are erased to their underlying type in the bytecode. From Java's perspective: (1) A value class `@JvmInline value class UserId(val value: Long)` appears as a class with a static `box-impl` and `unbox-impl` method. (2) Java code sees the underlying type in method signatures: `fun findUser(id: UserId)` appears as `findUser(long id)` in Java. (3) When boxed (generics, nullable), Java sees the wrapper class. (4) Java code can create instances via the constructor: `new UserId(42L)`. (5) Java code can access the underlying value: `userId.getValue()` or `userId.unbox-impl()`. (6) The `@JvmInline` annotation is required — it tells the JVM to inline the value. Potential issues: (1) Overloaded methods with value class and underlying type — `fun foo(id: UserId)` and `fun foo(id: Long)` have the same JVM signature — compile error. (2) Reflection from Java doesn't see Kotlin-specific info. Best practice: test Java interop when using value classes across module boundaries.

11. **What are the limitations and gotchas of value classes?**
    - (1) **Only one property** — `@JvmInline value class UserId(val value: Long, val name: String)` is a compile error. Only one `val` in the primary constructor. (2) **No inheritance** — value classes cannot be `open`, `abstract`, or `sealed`. They can implement interfaces but not extend classes. (3) **Boxing in generics** — `List<UserId>` boxes every element. Use primitive arrays or accept the boxing cost. (4) **No `when` exhaustiveness** — unlike sealed classes, value classes don't get `when` exhaustiveness checks. (5) **`equals`/`hashCode` can be slow** — for large underlying values. (6) **Serialization** — need custom serializers (Moshi, Kotlinx Serialization support value classes). (7) **JVM overloading** — `fun foo(id: UserId)` and `fun foo(id: Long)` have the same JVM signature. Use `@JvmName` to fix. (8) **No late initialization** — cannot use `lateinit var` (no `var` allowed). (9) **Cannot be inner** — value classes cannot be inner classes. (10) **`toString` includes the value** — `UserId(42).toString()` → "UserId(value=42)". May leak sensitive data — override `toString` if needed.

12. **How do you serialize value classes with JSON libraries?**
    - (1) **Kotlinx Serialization** — fully supports value classes: `@Serializable @JvmInline value class UserId(val value: Long)`. Serializes to the underlying value: `{"userId": 42}` not `{"userId": {"value": 42}}`. Use `@SerialName` for custom field names. (2) **Moshi** — supports value classes with codegen: `@JsonClass(generateAdapter = true) @JvmInline value class Email(val value: String)`. Moshi serializes/deserializes the underlying value directly. (3) **Gson** — doesn't natively support value classes well. Gson uses reflection and may create invalid instances. Workaround: use a custom `TypeAdapter` or `JsonConverter`. (4) **Manual serialization** — `fun UserId.toJson() = value.toString(); fun String.toUserId() = UserId(this.toLong())`. Best practice: use Kotlinx Serialization (best support, compile-time safe) or Moshi with codegen. Avoid Gson with value classes — it bypasses constructors and may create invalid state. Always test serialization round-trips: `val json = Json.encodeToString(id); val parsed = Json.decodeFromString<UserId>(json); assertEquals(id, parsed)`.

---

## 🔗 Related Topics
- [Generics](Generics.md)
- [OOP](../intermediate/OOP.md)
