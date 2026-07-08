# Type Checks & Smart Casts

## 📖 Explanation

Kotlin's smart cast is one of its most loved features. The compiler automatically casts variables after type checks, eliminating the need for explicit casts.

### `is` and `!is` Operators
Check if an object is an instance of a type.

```kotlin
if (obj is String) {
    println(obj.length)  // Smart cast: obj is automatically String here
}
```

### Smart Cast
After an `is` check, the compiler automatically casts the variable. No explicit cast needed.

```kotlin
fun demo(x: Any) {
    if (x is String) {
        println(x.length)  // x is smart-cast to String
    }
}
```

### Smart Cast Limitations
Smart cast doesn't work on:
- `var` properties (mutable — could change between check and use)
- Properties with custom getters
- Delegated properties

### Explicit Cast with `as`
```kotlin
val str = obj as String  // Throws ClassCastException if not String
```

### Safe Cast with `as?`
Returns `null` if the cast fails — no exception.

```kotlin
val str: String? = obj as? String  // null if obj is not String
```

### Smart Cast with `when`
```kotlin
when (x) {
    is Int -> println(x + 1)     // x is smart-cast to Int
    is String -> println(x.length) // x is smart-cast to String
    is List<*> -> println(x.size) // x is smart-cast to List
}
```

### Smart Cast with `&&` and `||`
```kotlin
if (obj is String && obj.length > 5) {
    // obj is smart-cast in the second condition
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // is operator + smart cast
    val items: List<Any> = listOf("Kotlin", 42, 3.14, true, listOf(1, 2, 3))

    for (item in items) {
        describe(item)
    }

    // Safe cast
    val obj1: Any = "Hello"
    val obj2: Any = 123

    val str1: String? = obj1 as? String
    val str2: String? = obj2 as? String
    println("\nSafe cast: str1=$str1, str2=$str2")

    // Smart cast in when
    println("\nWhen smart casts:")
    val shapes: List<Any> = listOf(Circle(5.0), Square(4.0), Circle(3.0))
    for (shape in shapes) {
        val area = when (shape) {
            is Circle -> "Circle area = ${Math.PI * shape.radius * shape.radius}"
            is Square -> "Square area = ${shape.side * shape.side}"
            else -> "Unknown shape"
        }
        println("  $area")
    }

    // Smart cast with && (short-circuit)
    val value: Any = "Kotlin"
    if (value is String && value.length > 3) {
        println("\nLong string: $value (length ${value.length})")
    }

    // !is operator
    val data: Any = 42
    if (data !is String) {
        println("\n$data is NOT a String")
    }

    // Smart cast with nullable types
    val name: String? = "Alice"
    if (name != null) {
        println("Name length: ${name.length}")  // Smart cast: String? -> String
    }

    // Explicit cast (unsafe)
    val anyObj: Any = "Test"
    val explicit = anyObj as String
    println("Explicit cast: $explicit")
}

fun describe(obj: Any) {
    if (obj is String) {
        println("String: '$obj' (length ${obj.length})")  // smart cast
    } else if (obj is Int) {
        println("Int: $obj (squared ${obj * obj})")  // smart cast
    } else if (obj is Double) {
        println("Double: $obj (rounded ${Math.round(obj)})")  // smart cast
    } else if (obj is Boolean) {
        println("Boolean: $obj (negated ${!obj})")  // smart cast
    } else if (obj is List<*>) {
        println("List: size=${obj.size}, items=$obj")  // smart cast
    }
}

class Circle(val radius: Double)
class Square(val side: Double)
```

### Output
```
String: 'Kotlin' (length 6)
Int: 42 (squared 1764)
Double: 3.14 (rounded 3)
Boolean: true (negated false)
List: size=3, items=[1, 2, 3]

Safe cast: str1=Hello, str2=null

When smart casts:
  Circle area = 78.53981633974483
  Square area = 16.0
  Circle area = 28.274333882308138

Long string: Kotlin (length 6)

42 is NOT a String
Name length: 5
Explicit cast: Test
```

---

## ❓ Interview Questions

1. **What is smart cast in Kotlin?**
   - After an `is` type check, the compiler automatically treats the variable as the checked type — no explicit cast needed. E.g., after `if (x is String)`, `x.length` works directly.

2. **What is the difference between `as` and `as?`?**
   - `as` is an unsafe cast — throws `ClassCastException` if the cast fails. `as?` is a safe cast — returns `null` if the cast fails.

3. **When does smart cast NOT work?**
   - Smart cast doesn't work on `var` properties (mutable, could change between check and use), properties with custom getters, delegated properties, or properties from other modules that could be overridden.

4. **How does smart cast work with `when`?**
   - In each `is` branch of a `when`, the subject is automatically smart-cast to the matched type. E.g., `is String -> println(x.length)` — `x` is `String` in that branch.

5. **Does smart cast work with null checks?**
   - Yes. After `if (name != null)`, a `String?` is smart-cast to `String` — you can access `name.length` without `?.`.

---

## 🔗 Related Topics
- [Null Safety](NullSafety.md)
- [Control Flow](ControlFlow.md)
