# Structural Patterns

Structural patterns deal with **how classes and objects are composed** to form larger structures. They enable collaboration between objects that couldn't otherwise work together due to incompatible interfaces.

---

## 1. Adapter

### Intent
Convert the interface of a class into another interface clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.

### Problem It Solves
You have an existing class that does what you need, but its interface doesn't match what your code expects. You can't modify the existing class (third-party library, legacy code).

### Real-World Analogy
A travel adapter: your US plug (2 flat pins) doesn't fit a European socket (2 round holes). The adapter sits between them — it has a US socket on one side and a European plug on the other.

### Structure
```kotlin
// Target interface (what the client expects)
interface LogTarget {
    fun log(level: String, message: String)
}

// Adaptee (existing class with incompatible interface)
class ThirdPartyLogger {
    fun writeLog(entry: String, severity: Int) {
        println("[$severity] $entry")
    }
}

// Adapter (makes ThirdPartyLogger look like LogTarget)
class ThirdPartyLoggerAdapter(private val logger: ThirdPartyLogger) : LogTarget {
    override fun log(level: String, message: String) {
        val severity = when (level.uppercase()) {
            "ERROR" -> 3
            "WARN" -> 2
            "INFO" -> 1
            else -> 0
        }
        logger.writeLog(message, severity)
    }
}

// Usage:
val logger = ThirdPartyLogger()
val adapter = ThirdPartyLoggerAdapter(logger)
adapter.log("ERROR", "Something went wrong")  // Client uses LogTarget interface
```

### When to Use
- You want to use an existing class but its interface doesn't match.
- You can't modify the existing class (third-party, legacy).
- You want to create a reusable class that cooperates with unrelated classes.

### When NOT to Use
- When you control both sides — just fix the interface directly.
- When the interfaces are very different — the adapter becomes complex and fragile.

### Key Insight
> **Adapter is about making incompatible interfaces compatible. It's a structural wrapper — it doesn't change behavior, just the interface. The adapter is transparent to the client: the client thinks it's talking to a `LogTarget`, but it's actually talking to a `ThirdPartyLogger` through the adapter.**

---

## 2. Bridge

### Intent
Decouple an abstraction from its implementation so that the two can vary independently.

### Problem It Solves
You have two independent dimensions of variation. For example: a Shape (Circle, Square) can be rendered by different renderers (Vector, Raster). Without Bridge, you need 4 classes: `VectorCircle`, `VectorSquare`, `RasterCircle`, `RasterSquare`. Add a Triangle → 6 classes. Add a 3D renderer → 9 classes. This is **Cartesian product explosion**.

### Structure
```kotlin
// Implementation interface (one dimension of variation)
interface Renderer {
    fun renderCircle(radius: Double): String
    fun renderSquare(side: Double): String
}

// Concrete implementations
class VectorRenderer : Renderer {
    override fun renderCircle(radius: Double) = "Drawing vector circle (r=$radius)"
    override fun renderSquare(side: Double) = "Drawing vector square (s=$side)"
}

class RasterRenderer : Renderer {
    override fun renderCircle(radius: Double) = "Drawing raster circle (r=$radius)"
    override fun renderSquare(side: Double) = "Drawing raster square (s=$side)"
}

// Abstraction (other dimension of variation)
abstract class Shape(protected val renderer: Renderer) {
    abstract fun draw(): String
}

// Refined abstractions
class Circle(renderer: Renderer, private val radius: Double) : Shape(renderer) {
    override fun draw() = renderer.renderCircle(radius)
}

class Square(renderer: Renderer, private val side: Double) : Shape(renderer) {
    override fun draw() = renderer.renderSquare(side)
}

// Usage:
val vectorCircle = Circle(VectorRenderer(), 5.0)
val rasterSquare = Square(RasterRenderer(), 10.0)
println(vectorCircle.draw())  // "Drawing vector circle (r=5.0)"
println(rasterSquare.draw())  // "Drawing raster square (s=10.0)"
```

### Without Bridge (The Problem)
```
Shape × Renderer = N × M classes
  VectorCircle, VectorSquare, VectorTriangle
  RasterCircle, RasterSquare, RasterTriangle
  3DCircle, 3DSquare, 3DTriangle
  = 9 classes for 3 shapes × 3 renderers
```

### With Bridge
```
Shape hierarchy: Shape → Circle, Square, Triangle (3 classes)
Renderer hierarchy: Renderer → Vector, Raster, 3D (3 classes)
Total: 6 classes (not 9)
Add a new shape: +1 class (not +3)
Add a new renderer: +1 class (not +3)
```

### When to Use
- You have two independent dimensions of variation.
- You want to avoid a class explosion (Cartesian product).
- You want to switch implementations at runtime.

### Key Insight
> **Bridge separates "what" (abstraction) from "how" (implementation). It's composition over inheritance applied to multi-dimensional variation. The bridge is the reference from the abstraction to the implementation. This is the pattern behind JDBC: `Connection` (abstraction) bridges to `Driver` (implementation) — you can change databases without changing your code.**

---

## 3. Composite

### Intent
Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects **uniformly**.

### Problem It Solves
You have a tree structure (file system, UI components, organization chart). You want to perform operations on both leaf nodes and branches without caring which is which.

### Structure
```kotlin
// Common interface for both leaves and composites
interface FileSystemNode {
    fun getName(): String
    fun getSize(): Long
    fun print(indent: String = "")
}

// Leaf (no children)
class File(private val name: String, private val size: Long) : FileSystemNode {
    override fun getName() = name
    override fun getSize() = size
    override fun print(indent: String) {
        println("$indent📄 $name ($size bytes)")
    }
}

// Composite (has children)
class Directory(private val name: String) : FileSystemNode {
    private val children = mutableListOf<FileSystemNode>()

    fun add(node: FileSystemNode) = apply { children.add(node) }
    fun remove(node: FileSystemNode) = apply { children.remove(node) }

    override fun getName() = name
    override fun getSize() = children.sumOf { it.getSize() }
    override fun print(indent: String) {
        println("$indent📁 $name (${getSize()} bytes)")
        children.forEach { it.print("$indent  ") }
    }
}

// Usage:
val root = Directory("root").apply {
    add(File("readme.txt", 1024))
    add(Directory("src").apply {
        add(File("Main.kt", 4096))
        add(File("Utils.kt", 2048))
    })
    add(File("config.yml", 512))
}

root.print()
// 📁 root (7680 bytes)
//   📄 readme.txt (1024 bytes)
//   📁 src (6144 bytes)
//     📄 Main.kt (4096 bytes)
//     📄 Utils.kt (2048 bytes)
//   📄 config.yml (512 bytes)
```

### When to Use
- You have a tree structure (file system, UI, org chart, menu).
- You want to treat leaf and composite nodes uniformly.
- You want recursive operations on the tree.

### Key Insight
> **Composite is the pattern behind every tree structure in software: DOM (HTML elements), file systems, UI component trees, JSON/XML. The power is: the client doesn't know or care if it's operating on a leaf or a branch. `getSize()` works the same way — a file returns its size, a directory sums its children.**

---

## 4. Decorator

### Intent
Attach additional responsibilities to an object **dynamically** without altering its class. Decorators provide a flexible alternative to subclassing for extending functionality.

### Problem It Solves
You want to add behavior to an object (logging, caching, compression, encryption) without modifying its class or creating a subclass for every combination.

### Structure
```kotlin
// Component interface
interface Coffee {
    fun cost(): Double
    fun description(): String
}

// Concrete component
class SimpleCoffee : Coffee {
    override fun cost() = 2.0
    override fun description() = "Simple coffee"
}

// Decorator (implements same interface, wraps another Coffee)
abstract class CoffeeDecorator(private val coffee: Coffee) : Coffee {
    override fun cost() = coffee.cost()
    override fun description() = coffee.description()
}

// Concrete decorators
class Milk(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun cost() = super.cost() + 0.5
    override fun description() = "${super.description()}, with milk"
}

class Sugar(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun cost() = super.cost() + 0.2
    override fun description() = "${super.description()}, with sugar"
}

class WhippedCream(coffee: Coffee) : CoffeeDecorator(coffee) {
    override fun cost() = super.cost() + 0.7
    override fun description() = "${super.description()}, with whipped cream"
}

// Usage: stack decorators dynamically
val coffee = WhippedCream(Sugar(Milk(SimpleCoffee())))
println(coffee.description()) // "Simple coffee, with milk, with sugar, with whipped cream"
println(coffee.cost())       // 3.4
```

### Decorator vs Inheritance

| Inheritance | Decorator |
|---|---|
| Static (compile-time) | Dynamic (runtime) |
| Class explosion for combinations | Stack decorators freely |
| Can't change at runtime | Add/remove at runtime |
| Knows the concrete class | Transparent to client |

### Real-World Examples
- `InputStream` → `BufferedInputStream` → `GZIPInputStream` (Java I/O)
- HTTP middleware: logging → auth → rate-limit → handler
- RecyclerView adapter: `ItemDecoration` wraps the adapter

### When to Use
- You want to add behavior without modifying existing code (OCP).
- You want to combine behaviors dynamically.
- You want transparent decoration (client doesn't know it's decorated).

### Key Insight
> **Decorator is the pattern behind middleware (Express, Ktor, Spring), Java I/O streams, and Kotlin collection wrappers. The key: the decorator implements the same interface as the wrapped object, so the client can't tell the difference. You can stack decorators infinitely — each adds one responsibility. This is SRP applied to object composition.**

---

## 5. Facade

### Intent
Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use.

### Problem It Solves
A subsystem has many classes with complex interactions. Clients must know about 5 classes and call them in the right order. The facade simplifies this to one call.

### Structure
```kotlin
// Complex subsystem (many classes, complex interactions)
class CPU {
    fun freeze() = println("CPU: Freezing")
    fun jump(address: Long) = println("CPU: Jumping to $address")
    fun execute() = println("CPU: Executing")
}

class Memory {
    fun load(address: Long, data: String) = println("Memory: Loading '$data' to $address")
}

class HardDrive {
    fun read(lba: Long, size: Int): String = "boot_data"
}

// Facade: simple interface to the complex subsystem
class ComputerFacade {
    private val cpu = CPU()
    private val memory = Memory()
    private val hardDrive = HardDrive()

    private val BOOT_ADDRESS = 0x00000000L
    private val BOOT_SECTOR = 0L
    private val SECTOR_SIZE = 512

    fun start() {
        cpu.freeze()
        val data = hardDrive.read(BOOT_SECTOR, SECTOR_SIZE)
        memory.load(BOOT_ADDRESS, data)
        cpu.jump(BOOT_ADDRESS)
        cpu.execute()
    }
}

// Usage: client calls one method instead of 5
val computer = ComputerFacade()
computer.start()
// Without facade, client would need to know about CPU, Memory, HardDrive,
// their methods, and the correct order of calls.
```

### When to Use
- A subsystem is complex and clients need a simple interface.
- You want to layer your architecture (facade per layer).
- You want to decouple clients from subsystem internals.

### Facade vs Adapter

| Facade | Adapter |
|---|---|
| Simplifies a complex subsystem | Makes incompatible interfaces compatible |
| Defines a new interface | Wraps an existing interface |
| Multiple objects behind one | One object behind one |
| Makes things easier | Makes things possible |

### Key Insight
> **Facade doesn't encapsulate the subsystem — it just provides a shortcut. Clients can still use the subsystem directly if they need fine-grained control. The facade is a convenience, not a restriction. It's the pattern behind SDK design: `Stripe.Charge.create()` hides 10 internal classes.**

---

## 6. Flyweight

### Intent
Use sharing to support large numbers of fine-grained objects efficiently.

### Problem It Solves
You need millions of objects that share most of their state. Without sharing, memory explodes. Example: a text editor with 1 million characters — each character object stores font, size, color, position. But font/size/color are shared across many characters. Only position is unique.

### Structure
```kotlin
// Flyweight: shared intrinsic state
data class TreeType(
    val name: String,
    val color: String,
    val texture: String
)

// Flyweight factory: creates and caches shared objects
class TreeFactory {
    private val treeTypes = mutableMapOf<String, TreeType>()

    fun getTreeType(name: String, color: String, texture: String): TreeType {
        val key = "$name-$color-$texture"
        return treeTypes.getOrPut(key) { TreeType(name, color, texture) }
    }
}

// Context: unique extrinsic state
class Tree(
    private val x: Int,
    private val y: Int,
    private val type: TreeType  // shared flyweight
) {
    fun draw() = println("Drawing ${type.name} (${type.color}) at ($x, $y)")
}

// Usage:
val factory = TreeFactory()

// 1 million trees, but only 3 TreeType objects
val trees = (1..1_000_000).map { i ->
    val type = factory.getTreeType("Oak", "green", "oak_texture")
    Tree(i % 1000, i / 1000, type)
}

trees[0].draw()  // "Drawing Oak (green) at (0, 0)"
// Memory: 1M Tree objects (x, y, ref) + 1 TreeType object
// Without flyweight: 1M Tree objects each with full TreeType data
```

### Intrinsic vs Extrinsic State

| Intrinsic (Shared) | Extrinsic (Unique) |
|---|---|
| Stored in the flyweight | Stored in the context |
| Same across all instances | Different per instance |
| `TreeType` (name, color, texture) | `Tree` (x, y position) |

### When to Use
- Large number of similar objects.
- Most state can be shared.
- Memory is a constraint.
- After removing shared state, the remaining state is small.

### Key Insight
> **Flyweight is the pattern behind `Integer.valueOf(-128 to 127)` cache, `String` intern pool, and character formatting in text editors. It's an optimization — don't use it unless you have a proven memory problem. Premature flyweight is just premature optimization.**

---

## 7. Proxy

### Intent
Provide a surrogate or placeholder for another object to control access to it.

### Problem It Solves
You want to add a layer of control between the client and the real object: lazy initialization, access control, caching, logging, remote access.

### Types of Proxies

#### Virtual Proxy (Lazy Loading)
```kotlin
interface Image {
    fun display()
}

class RealImage(private val filename: String) : Image {
    init {
        loadFromDisk()  // expensive — loads immediately
    }

    private fun loadFromDisk() = println("Loading $filename from disk...")

    override fun display() = println("Displaying $filename")
}

class ProxyImage(private val filename: String) : Image {
    private var realImage: RealImage? = null

    override fun display() {
        if (realImage == null) {
            realImage = RealImage(filename)  // lazy load — only when needed
        }
        realImage!!.display()
    }
}

// Usage:
val image = ProxyImage("photo.jpg")  // no loading yet
println("Image created but not loaded")
image.display()  // loads NOW (first access)
image.display()  // already loaded (no reload)
```

#### Protection Proxy (Access Control)
```kotlin
interface Service {
    fun operation(): String
}

class RealService : Service {
    override fun operation() = "Sensitive operation performed"
}

class ProtectionProxy(
    private val service: Service,
    private val userRole: String
) : Service {
    override fun operation(): String {
        if (userRole != "admin") {
            return "Access denied: admin role required"
        }
        return service.operation()
    }
}

// Usage:
val service = RealService()
val adminProxy = ProtectionProxy(service, "admin")
val guestProxy = ProtectionProxy(service, "guest")

println(adminProxy.operation())  // "Sensitive operation performed"
println(guestProxy.operation())  // "Access denied: admin role required"
```

#### Remote Proxy
```kotlin
// Client calls a local proxy that handles network communication
interface UserService {
    fun getUser(id: String): User
}

class RemoteUserServiceProxy(private val endpoint: String) : UserService {
    override fun getUser(id: String): User {
        // Make HTTP call to remote service
        val response = httpClient.get("$endpoint/users/$id")
        return parseUser(response)
    }
}

// Client uses it like a local service — network is hidden
val userService = RemoteUserServiceProxy("https://api.example.com")
val user = userService.getUser("123")  // looks local, but goes over network
```

### Proxy vs Decorator

| Proxy | Decorator |
|---|---|
| Controls access | Adds behavior |
| Client doesn't know about real object | Client doesn't know about wrapped object |
| Same interface, different purpose | Same interface, enhanced behavior |
| Focus: access control | Focus: responsibility addition |

### When to Use
- Lazy initialization (virtual proxy).
- Access control (protection proxy).
- Remote access (remote proxy).
- Caching (cache proxy).
- Logging/metrics (smart proxy).

### Key Insight
> **Proxy is the pattern behind every RPC framework (gRPC stubs, Retrofit interfaces, RMI). The client calls a local object that looks like the real service, but the proxy handles network, serialization, and errors. It's also the pattern behind Spring AOP — `@Transactional`, `@Cacheable`, `@Async` are all proxies injected by Spring.**

---

## Summary: When to Use Which Structural Pattern

| Pattern | When to Use | Key Benefit |
|---|---|---|
| **Adapter** | Incompatible interfaces | Make them work together |
| **Bridge** | Two dimensions of variation | Avoid class explosion |
| **Composite** | Tree structure | Treat leaf and branch uniformly |
| **Decorator** | Add behavior dynamically | Open/Closed Principle |
| **Facade** | Simplify complex subsystem | One entry point |
| **Flyweight** | Many shared objects | Memory efficiency |
| **Proxy** | Control access to object | Lazy, protect, remote |
