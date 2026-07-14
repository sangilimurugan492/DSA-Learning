# Variables & Data Types

## 📖 Explanation

Kotlin has two keywords for declaring variables:
- **`val`** — Read-only (immutable reference). Cannot be reassigned after initialization. Equivalent to Java's `final`.
- **`var`** — Mutable. Can be reassigned.

### Type Inference
Kotlin infers the type from the assigned value. You can also declare types explicitly.

```kotlin
val name = "Alice"        // Inferred as String
var age = 30              // Inferred as Int
val pi: Double = 3.14     // Explicit type
```

### Primitive Data Types
Kotlin does not have primitives in the language — everything is an object. However, the compiler optimizes to primitives where possible.

| Type      | Description                  | Example                  |
|-----------|------------------------------|--------------------------|
| `Byte`    | 8-bit integer                | `127`                    |
| `Short`   | 16-bit integer               | `32767`                  |
| `Int`     | 32-bit integer               | `2147483647`             |
| `Long`    | 64-bit integer               | `9223372036854775807L`   |
| `Float`   | 32-bit floating point        | `3.14f`                  |
| `Double`  | 64-bit floating point         | `3.14159265359`          |
| `Char`    | Single character             | `'A'`                    |
| `Boolean` | True or false                | `true`                   |
| `String`  | Text                         | `"Hello"`                |

### Type Conversion
Kotlin requires **explicit** conversions — no implicit widening like Java.

```kotlin
val i: Int = 10
val l: Long = i.toLong()      // Explicit conversion required
val d: Double = l.toDouble()
```

### `const` vs `val`
- `const val` — Compile-time constant. Must be top-level or in an object. Only `String` or primitive types.
- `val` — Runtime constant. Can be assigned at runtime.

---

## 🧪 Code Example

```kotlin
fun main() {
    // val (immutable)
    val language = "Kotlin"
    // language = "Java"  // ❌ Compilation error

    // var (mutable)
    var version = 1.0
    version = 2.0           // ✅ Allowed
    println("$language $version")

    // All basic types
    val byteVal: Byte = 1
    val intVal: Int = 100
    val longVal: Long = 9999999999L
    val floatVal: Float = 3.14f
    val doubleVal: Double = 3.14159265359
    val charVal: Char = 'K'
    val boolVal: Boolean = true
    val stringVal: String = "Kotlin"

    println("Byte=$byteVal, Int=$intVal, Long=$longVal")
    println("Float=$floatVal, Double=$doubleVal")
    println("Char=$charVal, Boolean=$boolVal, String=$stringVal")

    // Type conversion
    val num = 42
    val numAsLong = num.toLong()
    val numAsString = num.toString()
    println("Converted: Long=$numAsLong, String=$numAsString")

    // String to number
    val parsed = "123".toInt()
    println("Parsed: $parsed")
}
```

### Output
```
Kotlin 2.0
Byte=1, Int=100, Long=9999999999
Float=3.14, Double=3.14159265359
Char=K, Boolean=true, String=Kotlin
Converted: Long=42, String=42
Parsed: 123
```

---

## ❓ Interview Questions

1. **What is the difference between `val` and `var`?**
   - `val` is read-only (cannot be reassigned), `var` is mutable (can be reassigned).

2. **Is `val` truly immutable?**
   - The reference is immutable, but if it points to a mutable object (e.g., `MutableList`), the object's contents can change.

3. **What is the difference between `const val` and `val`?**
   - `const val` is a compile-time constant (top-level or object only, primitives/Strings only). `val` can be assigned at runtime.

4. **Does Kotlin have primitive types?**
   - At the language level, everything is an object. The compiler optimizes to JVM primitives where possible (e.g., `Int` → `int`).

5. **Why does Kotlin require explicit type conversions?**
   - To prevent silent data loss and make conversions intentional. No implicit widening like Java.

---

## 🔗 Related Topics
- [Basics & Hello World](Basics.md)
- [Control Flow](ControlFlow.md)
