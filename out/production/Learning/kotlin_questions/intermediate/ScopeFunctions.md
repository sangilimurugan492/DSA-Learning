# Scope Functions

## 📖 Explanation

Kotlin provides five scope functions: `let`, `run`, `with`, `apply`, and `also`. They execute a block of code within the context of an object. The key differences are:

1. **Reference**: `this` (context object) vs `it` (argument)
2. **Return value**: Returns the object itself vs the lambda result

| Function | Object Reference | Return Value   | Typical Use                    |
|----------|-----------------|----------------|--------------------------------|
| `let`    | `it`            | Lambda result  | Null checks, transformations   |
| `run`    | `this`          | Lambda result  | Computation on object          |
| `with`   | `this`          | Lambda result  | Grouping calls on same object  |
| `apply`  | `this`          | Object itself  | Object configuration/setup     |
| `also`   | `it`            | Object itself  | Side effects, chaining         |

### `let`
```kotlin
val result = str?.let { it.uppercase() }
```

### `run`
```kotlin
val result = "Hello".run {
    length  // returns length
}
```

### `with`
```kotlin
val result = with(StringBuilder()) {
    append("A")
    append("B")
    toString()
}
```

### `apply`
```kotlin
val person = Person().apply {
    name = "Alice"
    age = 30
}
```

### `also`
```kotlin
val list = mutableListOf(1, 2, 3).also {
    println("Created: $it")
}
```

---

## 🧪 Code Example

```kotlin
fun main() {
    // let — null check + transform
    val name: String? = "Kotlin"
    val upper = name?.let { it.uppercase() }
    println("let result: $upper")

    // run — compute on object
    val length = "Hello World".run {
        split(" ").size
    }
    println("run result (word count): $length")

    // with — group calls
    val result = with(StringBuilder()) {
        append("Kotlin ")
        append("is ")
        append("awesome!")
        toString()
    }
    println("with result: $result")

    // apply — configure object
    val config = ServerConfig().apply {
        host = "localhost"
        port = 8080
        timeout = 5000
    }
    println("apply result: $config")

    // also — side effect + chain
    val numbers = mutableListOf(1, 2, 3)
        .also { println("Before add: $it") }
        .apply { add(4) }
        .also { println("After add: $it") }
    println("Final list: $numbers")

    // Chaining scope functions
    val processed = "  Hello Kotlin  "
        .let { it.trim() }
        .also { println("Trimmed: '$it'") }
        .run { uppercase() }
        .also { println("Uppercased: $it") }
    println("Processed: '$processed'")
}

data class ServerConfig(
    var host: String = "",
    var port: Int = 0,
    var timeout: Int = 0
)
```

### Output
```
let result: KOTLIN
run result (word count): 2
with result: Kotlin is awesome!
apply result: ServerConfig(host=localhost, port=8080, timeout=5000)
Before add: [1, 2, 3]
After add: [1, 2, 3, 4]
Final list: [1, 2, 3, 4]
Trimmed: 'Hello Kotlin'
Uppercased: HELLO KOTLIN
Processed: 'HELLO KOTLIN'
```

---

## ❓ Interview Questions

1. **What are the five scope functions in Kotlin?**
   - `let`, `run`, `with`, `apply`, and `also`. They execute code in the context of an object.

2. **What is the difference between `apply` and `also`?**
   - `apply` uses `this` and returns the object. `also` uses `it` and returns the object. Use `apply` for configuration, `also` for side effects.

3. **When would you use `let`?**
   - For null checks (`obj?.let { ... }`) and to transform a value while keeping the scope limited.

4. **What is the difference between `run` and `with`?**
   - `run` is an extension function called on the object (`obj.run { }`). `with` takes the object as a parameter (`with(obj) { }`). Both use `this` and return the lambda result.

5. **Which scope function returns the object itself?**
   - `apply` and `also` return the context object. The others return the lambda result.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](LambdasAndHigherOrderFunctions.md)
- [Extensions](Extensions.md)
