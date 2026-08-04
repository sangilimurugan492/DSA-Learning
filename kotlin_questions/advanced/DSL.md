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

6. **How do you build a type-safe Kotlin DSL from scratch?**
    - Steps: (1) Define a builder class with the DSL configuration: `class HtmlBuilder { var title = ""; fun body(block: BodyBuilder.() -> Unit) { ... } }`. (2) Use a lambda with receiver so `this` is the builder: `fun html(block: HtmlBuilder.() -> Unit): String { val builder = HtmlBuilder(); builder.block(); return builder.build() }`. (3) Use `@DslMarker` to prevent scope ambiguity: `@DslMarker annotation class HtmlDsl; @HtmlDsl class HtmlBuilder { }`. (4) Use `infix` functions for natural syntax: `infix fun String.attr(value: String): Pair<String, String> = this to value`. (5) Support nesting: `html { head { title("Hello") }; body { p("World") } }`. (6) Use `operator fun String.invoke(block: ...) { }` for tag-like syntax. Example: `fun html(block: HtmlBuilder.() -> Unit) = HtmlBuilder().apply(block).build()`. The receiver lambda is key — it allows calling builder methods without `this.` prefix. Type safety comes from Kotlin's type system — you can only call methods that exist on the builder.

7. **What is `@DslMarker` and how does it prevent scope ambiguity?**
    - Without `@DslMarker`, nested DSL blocks can access any enclosing receiver, leading to ambiguity: `html { body { /* `this` could be html or body */ } }`. The compiler would resolve to the innermost receiver, but the user might accidentally call outer receiver methods. `@DslMarker` solves this: define a marker annotation: `@DslMarker annotation class HtmlDsl`. Mark all builder classes with it: `@HtmlDsl class HtmlBuilder; @HtmlDsl class BodyBuilder`. Now, when inside `body { }`, you can only access `BodyBuilder` methods — accessing `HtmlBuilder` methods requires explicit `this@html.method()`. This prevents accidental calls to outer receivers and makes DSLs safer. The marker creates an "implicit receiver" restriction — only the innermost receiver of the same marker is implicitly accessible. All Kotlin DSL libraries (Gradle, Ktor, HTML) use `@DslMarker`.

8. **How do `infix` functions enhance DSL readability?**
    - `infix` allows calling functions without dot/parentheses: `x greaterThan 5` instead of `x.greaterThan(5)`. This makes DSLs read like natural language. Requirements: (1) Must be a member or extension function. (2) Must have exactly one parameter. (3) Cannot have default arguments. Example DSL: `infix fun String.startsWith(prefix: String): Boolean = this.startsWith(prefix)`. In a testing DSL: `assertThat(result) shouldBe 5`. In a query DSL: `where { age greaterThan 18 and name like "A%" }`. `infix` is heavily used in testing frameworks (Kotest, Ktor), Gradle (`implementation "lib"`), and SQL builders (Exposed: `Users.age greater 18`). Combine with extension functions for powerful DSLs: `infix fun <T> T.shouldBe(expected: T) = assertEquals(expected, this)`. Note: `infix` doesn't change behavior — it's purely syntactic sugar for readability.

9. **What is the difference between a lambda with receiver and a regular lambda in DSLs?**
    - **Regular lambda**: `(T) -> R` — receives the object as `it`: `{ it -> it.name }`. In DSLs, this requires explicit `it.` prefix — verbose. **Lambda with receiver**: `T.() -> R` — receives the object as `this`: `{ name }`. Inside the lambda, you can call `T`'s methods without qualification — clean DSL syntax. Example: `fun buildString(block: StringBuilder.() -> Unit): String = StringBuilder().apply(block).toString()`. Usage: `buildString { append("Hello"); append("World") }` — `append` is called on the `StringBuilder` receiver. This is the foundation of all Kotlin DSLs. The `apply`/`run`/`with` scope functions use receiver lambdas. Without receiver lambdas, you'd need: `buildString { it.append("Hello") }` — verbose. The receiver pattern is what makes Kotlin DSLs look like configuration blocks rather than function calls.

10. **How do you handle optional DSL parameters and defaults?**
    - (1) **Default values in builder**: `class Config { var host: String = "localhost"; var port: Int = 8080 }`. Users only set what they need. (2) **Nullable with `?.let`**: `var timeout: Long? = null; timeout?.let { builder.timeout(it) }`. (3) **Builder with `apply`**: `server { host = "api.com"; port = 443; ssl = true }` — only set what you need. (4) **Overloaded DSL functions**: `fun route(path: String, block: Route.() -> Unit)` and `fun route(path: String, method: HttpMethod, block: Route.() -> Unit)` — different parameter sets. (5) **Extension functions for optional config**: `fun Config.timeout(ms: Long) { this.timeout = ms }` — only call if needed. (6) **Sealed classes for constrained options**: `sealed class SslMode; object Enabled : SslMode(); object Disabled : SslMode()`. Best practice: make all properties have defaults so the DSL works with empty blocks: `server { }` should produce a valid default configuration.

11. **How do you test a Kotlin DSL?**
    - Testing a DSL is like testing any builder: (1) **Unit test the builder class directly**: `val builder = HtmlBuilder(); builder.body { p("Hello") }; val html = builder.build(); assertTrue(html.contains("<p>Hello</p>"))`. (2) **Test the DSL function**: `val result = html { body { p("Hello") } }; assertEquals("<html><body><p>Hello</p></body></html>", result)`. (3) **Test edge cases**: empty blocks, nested blocks, invalid configurations. (4) **Test with `@DslMarker`**: verify that outer receivers are not accessible (compile-time test). (5) **Property-based testing**: generate random DSL configurations and verify the output is valid. (6) **Integration test**: if the DSL produces executable output (like Gradle), run it and verify the result. (7) **Snapshot testing**: compare the DSL output against a known-good snapshot. For DSLs that produce strings (HTML, SQL), assert the output string. For DSLs that build objects, assert the object's properties. Always test both the builder class (unit) and the DSL function (integration).

12. **What are advanced DSL patterns in Kotlin?**
    - (1) **`@DslMarker` for scope safety** — prevents implicit access to outer receivers. (2) **Operator `invoke` for tag-like syntax** — `div { }` where `div` is a function that uses `invoke`. (3) **Extension functions as DSL keywords** — `fun Route.get(path: String, block: () -> Unit)` — adds DSL keywords to existing types. (4) **Context receivers** (experimental) — allow a function to require multiple receivers: `context(HttpRequest) fun authenticate()`. (5) **Type-safe builders with generics** — `fun <T> column(name: String): Column<T>` — type-safe columns in SQL DSL. (6) **Inline classes for DSL values** — `@JvmInline value class CssClass(val value: String)` — type-safe CSS classes. (7) **Sealed classes for constrained DSLs** — `sealed class HttpMethod; object GET : HttpMethod()` — only valid HTTP methods. (8) **Recursive DSLs** — tree structures: `fun node(block: Node.() -> Unit)` where `Node` has `fun node(block: Node.() -> Unit)` — builds trees. (9) **DSL with validation** — `init { require(host.isNotEmpty()) { "Host required" } }`.

---

## 🔗 Related Topics
- [Lambdas & Higher-Order Functions](../intermediate/LambdasAndHigherOrderFunctions.md)
- [Extensions](../intermediate/Extensions.md)
