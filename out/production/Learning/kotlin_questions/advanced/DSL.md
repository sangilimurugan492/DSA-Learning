# DSL Building

## 📖 Explanation

A **Domain-Specific Language (DSL)** is a mini-language designed for a specific domain. Kotlin's features — extension functions, lambdas with receivers, and `infix` functions — make it excellent for building type-safe DSLs.

### Key DSL Enablers

| Feature                  | Role in DSL                                    |
|--------------------------|------------------------------------------------|
| Lambda with receiver      | Provides `this` context inside the block       |
| Extension functions      | Add DSL methods to existing types              |
| `infix` functions        | Enable natural-language-like syntax           |
| `@DslMarker`             | Prevents implicit receiver access ambiguity   |
| Operator overloading     | Custom operators for natural syntax            |

### Lambda with Receiver
The core building block. The receiver becomes `this` inside the lambda.

```kotlin
fun buildString(action: StringBuilder.() -> Unit): String {
    val sb = StringBuilder()
    sb.action()  // 'action' runs with 'sb' as 'this'
    return sb.toString()
}

val result = buildString {
    append("Hello")
    append(" World")
}
```

### `@DslMarker`
Prevents accessing outer receivers in nested DSL blocks — avoids ambiguity.

```kotlin
@DslMarker
annotation class HtmlDsl

@HtmlDsl
class Table { fun tr(f: Tr.() -> Unit) { ... } }
```

### `infix` Functions
Enable `a to b` style syntax.

```kotlin
infix fun Int.times(str: String) = str.repeat(this)
3 times "Ab"  // "AbAbAb"
```

### Real-World DSL Examples
- **Gradle** build scripts
- **Kotlinx HTML** — `html { head { ... } body { ... } }`
- **Ktor** routing — `routing { get("/api") { ... } }`
- **Kotlin Test** frameworks — `assertThat(x).isEqualTo(5)`

---

## 🧪 Code Example

```kotlin
@DslMarker
annotation class HtmlDsl

fun main() {
    // --- HTML DSL ---
    val page = html {
        head {
            title("My Page")
        }
        body {
            h1("Welcome!")
            p("This is a paragraph.")
            p("Another paragraph.")
            ul {
                li("Item 1")
                li("Item 2")
                li("Item 3")
            }
        }
    }
    println(page)

    // --- SQL Query DSL ---
    val query = select {
        from("users")
        where { "age" greaterThan 18 }
        orderBy("name")
        limit(10)
    }
    println(query)

    // --- Configuration DSL ---
    val server = server {
        host = "localhost"
        port = 8080
        feature("logging") {
            enabled = true
            level = "DEBUG"
        }
        feature("auth") {
            enabled = true
            level = "STRICT"
        }
    }
    println(server)
}

// --- HTML DSL ---

@HtmlDsl
class Html {
    private var head = ""
    private var body = ""

    fun head(block: Head.() -> Unit) {
        head = Head().apply(block).build()
    }

    fun body(block: Body.() -> Unit) {
        body = Body().apply(block).build()
    }

    fun build(): String = "<html>\n  $head\n  $body\n</html>"
}

@HtmlDsl
class Head {
    private var content = ""
    fun title(text: String) { content = "<title>$text</title>" }
    fun build() = "<head>$content</head>"
}

@HtmlDsl
class Body {
    private val parts = mutableListOf<String>()

    fun h1(text: String) { parts.add("<h1>$text</h1>") }
    fun p(text: String) { parts.add("<p>$text</p>") }
    fun ul(block: Ul.() -> Unit) { parts.add(Ul().apply(block).build()) }

    fun build() = "<body>\n    ${parts.joinToString("\n    ")}\n  </body>"
}

@HtmlDsl
class Ul {
    private val items = mutableListOf<String>()
    fun li(text: String) { items.add("<li>$text</li>") }
    fun build() = "<ul>\n      ${items.joinToString("\n      ")}\n    </ul>"
}

fun html(block: Html.() -> Unit): String = Html().apply(block).build()

// --- SQL Query DSL ---

class WhereClause {
    private val conditions = mutableListOf<String>()
    infix fun String.greaterThan(value: Int) { conditions.add("$this > $value") }
    infix fun String.equalTo(value: Any) { conditions.add("$this = '$value'") }
    fun build() = conditions.joinToString(" AND ")
}

class Select {
    private var table = ""
    private var whereClause = ""
    private var orderColumn = ""
    private var limitValue = 0

    fun from(table: String) { this.table = table }
    fun where(block: WhereClause.() -> Unit) { whereClause = WhereClause().apply(block).build() }
    fun orderBy(column: String) { orderColumn = column }
    fun limit(n: Int) { limitValue = n }

    override fun toString(): String = buildString {
        append("SELECT * FROM $table")
        if (whereClause.isNotEmpty()) append(" WHERE $whereClause")
        if (orderColumn.isNotEmpty()) append(" ORDER BY $orderColumn")
        if (limitValue > 0) append(" LIMIT $limitValue")
    }
}

fun select(block: Select.() -> Unit): Select = Select().apply(block)

// --- Configuration DSL ---

class Feature(val name: String) {
    var enabled: Boolean = false
    var level: String = "INFO"
    override fun toString() = "Feature($name, enabled=$enabled, level=$level)"
}

class ServerConfig {
    var host: String = ""
    var port: Int = 0
    private val features = mutableListOf<Feature>()

    fun feature(name: String, block: Feature.() -> Unit) {
        features.add(Feature(name).apply(block))
    }

    override fun toString() = "Server(host=$host, port=$port, features=$features)"
}

fun server(block: ServerConfig.() -> Unit): ServerConfig = ServerConfig().apply(block)
```

### Output
```
<html>
  <head><title>My Page</title></head>
  <body>
    <h1>Welcome!</h1>
    <p>This is a paragraph.</p>
    <p>Another paragraph.</p>
    <ul>
      <li>Item 1</li>
      <li>Item 2</li>
      <li>Item 3</li>
    </ul>
  </body>
</html>
SELECT * FROM users WHERE age > 18 ORDER BY name LIMIT 10
Server(host=localhost, port=8080, features=[Feature(logging, enabled=true, level=DEBUG), Feature(auth, enabled=true, level=STRICT)])
```

---

## ❓ Interview Questions

1. **What is a DSL in Kotlin and what features enable it?**
   - A DSL is a mini-language for a specific domain. Kotlin enables DSLs via lambdas with receivers, extension functions, `infix` functions, and `@DslMarker`.

2. **What is a lambda with receiver?**
   - A lambda where the receiver object is available as `this` inside the block. E.g., `StringBuilder.() -> Unit`. This is the core mechanism for DSL blocks.

3. **What is `@DslMarker` and why is it needed?**
   - It prevents implicit access to outer receivers in nested DSL blocks. Without it, `this` could ambiguously refer to any enclosing receiver. `@DslMarker` restricts access to only the innermost receiver.

4. **How do `infix` functions help in DSLs?**
   - They allow natural-language syntax without dots or parentheses: `x greaterThan 5` instead of `x.greaterThan(5)`. Makes DSLs read like English.

5. **What are some real-world Kotlin DSL examples?**
   - Gradle build scripts (`build.gradle.kts`), Ktor routing, Kotlinx HTML, Exposed (SQL), and Kotlin Test frameworks.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](../intermediate/LambdasAndHigherOrderFunctions.md)
- [Extensions](../intermediate/Extensions.md)
