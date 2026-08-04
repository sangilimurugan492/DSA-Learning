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

6. **What is the difference between `is` and `as` in Kotlin?**
   - `is` (type check) — checks if an object is an instance of a type, returns `Boolean`. Does NOT cast the object: `if (obj is String) { }`. After `is` check, smart cast applies automatically. `as` (unsafe cast) — forcefully casts the object to the type. Throws `ClassCastException` if the cast fails: `val str = obj as String`. `as?` (safe cast) — casts and returns `null` if the cast fails: `val str = obj as? String`. Use `is` for type checking (no risk), `as?` for safe casting, and avoid `as` (unsafe) unless you're certain of the type.

7. **When does smart cast NOT work in Kotlin?**
    - Smart cast doesn't work in these cases: (1) **Mutable `var` properties** — the value could change between the check and use: `var x: Any = "hello"; if (x is String) { x.length }` may not smart-cast because `x` could be reassigned by another thread. (2) **Properties with custom getters** — the getter could return different types on each call. (3) **Properties from other modules** — the property could be overridden in a subclass with a different type. (4) **Delegated properties** — the delegate could change the value. (5) **`lateinit var`** — smart cast doesn't apply because the type is already non-null. Fix: use a local variable: `val x2 = x; if (x2 is String) { x2.length }`. The local `val` is immutable, so smart cast works.

8. **How do you use smart casts with `when` expressions?**
    - In each `is` branch, the subject is automatically smart-cast to the matched type: `when (obj) { is String -> println(obj.length); is Int -> println(obj + 1); is List<*> -> println(obj.size); else -> println("Unknown") }`. Inside the `String` branch, `obj` is `String` — no cast needed. This is especially powerful with sealed classes: `when (state) { is Loading -> showProgressBar(); is Success -> showData(state.data); is Error -> showError(state.message) }`. The compiler knows `state.data` exists in the `Success` branch because of smart cast. `when` smart casts are exhaustive for sealed classes — the compiler enforces all branches are covered.

9. **What is the difference between smart cast and explicit cast?**
    - **Smart cast** — the compiler automatically casts after a type check: `if (x is String) { x.length }` — `x` is automatically `String` inside the block. No code generated for the cast — the compiler just knows the type is narrowed. **Explicit cast** — you manually cast with `as` or `as?`: `val str = x as String; str.length`. If the cast fails, `as` throws `ClassCastException`. `as?` returns `null`. Smart cast is always preferred — it's compile-time safe and free. Use explicit `as?` when you can't use `is` (e.g., in a single expression): `val length = (x as? String)?.length ?: 0`. Never use `as` (unsafe) if you're not 100% certain of the type.

10. **How does smart cast work with inheritance and interfaces?**
    - Smart cast works with any type hierarchy: `open class Animal; class Dog : Animal() { fun bark() = "Woof" }; fun makeSound(a: Animal) { if (a is Dog) { a.bark() } }`. Inside the `if` block, `a` is smart-cast to `Dog`, so `bark()` is available. For interfaces: `interface Clickable { fun click() }; class Button : Clickable { override fun click() {} }; fun handle(c: Clickable) { if (c is Button) { c.click() } }`. Smart cast also works with `when`, `&&`, `||`, and `while` conditions: `if (obj is String && obj.length > 5) { }` — `obj` is `String` in the second condition. Smart cast works transitively: `if (a is Dog && a.parent is Dog) { a.parent.bark() }`.

11. **What is `unsafe` cast (`as`) and when should you avoid it?**
    - `as` is the unsafe cast operator — it throws `ClassCastException` if the object is not the target type: `val str = obj as String` — crashes if `obj` is not a `String`. **Avoid `as` in these cases**: (1) When the type is uncertain — use `as?` instead: `val str = obj as? String` returns `null` on failure. (2) When you need to check before casting — use `is` with smart cast: `if (obj is String) { obj.length }`. (3) In generic code — type erasure makes `as T` unsafe at runtime. **When `as` is acceptable**: (1) After an `is` check (though smart cast makes this unnecessary). (2) When you're 100% certain of the type (e.g., `findViewById<TextView>(R.id.text)`). (3) When you want to crash on type mismatch (fail-fast). Always prefer `as?` over `as` — it's safer and Kotlin-idiomatic.

12. **How do you handle type checking with generics in Kotlin?**
     - Due to type erasure, `is List<String>` doesn't work at runtime — all `List<T>` become `List` (erased). Workarounds: (1) **Check the raw type**: `if (list is List<*>) { }` — checks if it's a `List` but not the element type. (2) **Check elements**: `if (list.all { it is String }) { }` — runtime element check. (3) **`reified` type parameters**: `inline fun <reified T> isListOfType(list: List<*>): Boolean = list.all { it is T }` — makes `T` available at runtime. (4) **`KClass<T>`**: pass the class at runtime: `fun <T> filterType(list: List<*>, clazz: KClass<T>): List<T> = list.filter { clazz.isInstance(it) }.map { clazz.cast(it) }`. (5) **For `Array<T>`**: arrays are reified on the JVM, so `array is Array<String>` works at runtime (unlike `List<String>`). Always use `reified` for type-safe generic operations — it eliminates runtime casts.

13. **What is the `is` operator and how does it differ from `instanceof` in Java?**
    - `is` is Kotlin's type check operator — equivalent to Java's `instanceof` but with a key advantage: **smart cast**. After `if (obj is String)`, the compiler automatically treats `obj` as `String` — no explicit cast needed. In Java, you'd need to cast: `if (obj instanceof String) { String s = (String) obj; s.length(); }`. In Kotlin: `if (obj is String) { obj.length() }`. The negated form is `!is`: `if (obj !is String) return`. `is` also works with nullable types: `null is String` → `false`, `null is String?` → `true`. `is` is a compile-time and runtime check — at runtime it uses `instanceof` on the JVM.

14. **How does smart cast work with `&&` and `||` operators?**
    - Smart cast applies in `&&` (and) conditions after an `is` check: `if (obj is String && obj.length > 5)` — in the second condition, `obj` is already smart-cast to `String`. For `||` (or), smart cast applies in the `else` branch after a `!is` check: `if (obj !is String) return; obj.length` — after the guard, `obj` is `String`. In the condition itself (`||`), smart cast does NOT apply on the right side: `if (obj is String || obj.length > 5)` — `obj.length` is NOT smart-cast because the left side might be false. The compiler analyzes control flow to determine where smart cast is safe.

15. **What is `KClass` and how does it relate to type checking?**
    - `KClass<T>` is Kotlin's representation of a class at runtime — equivalent to Java's `Class<T>`. You obtain it with `String::class` or `"hello"::class`. It provides runtime type information: `isAssignableFrom()`, `isInstance()`, `members` (via reflection), `qualifiedName`, `simpleName`. Use `KClass` for runtime type checking when `reified` isn't available (non-inline functions): `fun <T : Any> isType(value: Any, klass: KClass<T>): Boolean = klass.isInstance(value)`. Get the `KClass` from a generic type parameter: `inline fun <reified T : Any> kClass(): KClass<T> = T::class`. `KClass` requires the `kotlin-reflect` dependency for full reflection features (listing members, functions, properties).

---

## 🔗 Related Topics
- [Null Safety](NullSafety.md)
- [Control Flow](ControlFlow.md)
