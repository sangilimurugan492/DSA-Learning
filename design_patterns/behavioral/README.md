# Behavioral Patterns

Behavioral patterns deal with **communication and assignment of responsibilities** between objects. They define how objects interact and how responsibilities are distributed.

---

## 1. Strategy

### Intent
Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from the clients that use it.

### Problem It Solves
You have multiple ways to do something (sort, navigate, discount, pay). Without Strategy, you use `if/else` or `when` statements that grow with every new option — violating Open/Closed Principle.

### Structure
```kotlin
// Strategy interface
interface PricingStrategy {
    fun calculatePrice(basePrice: Double): Double
}

// Concrete strategies
class RegularPricing : PricingStrategy {
    override fun calculatePrice(basePrice: Double) = basePrice
}

class DiscountPricing(private val discountPercent: Double) : PricingStrategy {
    override fun calculatePrice(basePrice: Double) = basePrice * (1 - discountPercent / 100)
}

class ClearancePricing : PricingStrategy {
    override fun calculatePrice(basePrice: Double) = basePrice * 0.3  // 70% off
}

// Context: uses the strategy
class ShoppingCart(private val pricingStrategy: PricingStrategy) {
    private val items = mutableListOf<Double>()

    fun addItem(price: Double) = items.add(price)

    fun checkout(): Double {
        val subtotal = items.sum()
        return pricingStrategy.calculatePrice(subtotal)
    }
}

// Usage: strategy is swappable
val cart1 = ShoppingCart(RegularPricing())
val cart2 = ShoppingCart(DiscountPricing(20.0))  // 20% off
val cart3 = ShoppingCart(ClearancePricing())       // 70% off

// Change strategy at runtime:
var cart = ShoppingCart(RegularPricing())
cart = ShoppingCart(DiscountPricing(50.0))  // switch to 50% off
```

### Strategy vs State

| Strategy | State |
|---|---|
| Client chooses the algorithm | Object's state determines behavior |
| Algorithms are independent | State transitions are connected |
| "How to do it" | "What to do based on where I am" |
| Client controls switching | Object self-transitions |

### When to Use
- Multiple algorithms for the same task.
- You want to avoid large `when/switch` statements.
- You need to switch algorithms at runtime.

### Key Insight
> **Strategy is the most used behavioral pattern. It's the direct application of "encapsulate what varies." Every time you see a `when` statement that keeps growing, replace it with a Strategy. The pattern is everywhere: `Comparator`, `PricingStrategy`, `RoutingStrategy`, `PaymentStrategy`.**

---

## 2. Observer

### Intent
Define a one-to-many dependency so that when one object changes state, all its dependents are notified and updated automatically.

### Problem It Solves
One object (the subject) changes, and multiple objects (observers) need to react — without the subject knowing the concrete types of the observers.

### Structure
```kotlin
// Observer interface
interface Observer<T> {
    fun update(data: T)
}

// Subject (observable)
class Subject<T> {
    private val observers = mutableListOf<Observer<T>>()

    fun subscribe(observer: Observer<T>) = observers.add(observer)
    fun unsubscribe(observer: Observer<T>) = observers.remove(observer)

    fun notifyObservers(data: T) {
        observers.forEach { it.update(data) }
    }
}

// Concrete subject
class NewsAgency : Subject<String>() {
    fun publishNews(headline: String) {
        println("📰 Breaking: $headline")
        notifyObservers(headline)
    }
}

// Concrete observers
class EmailSubscriber(private val email: String) : Observer<String> {
    override fun update(data: String) = println("📧 Email to $email: $data")
}

class SMSSubscriber(private val phone: String) : Observer<String> {
    override fun update(data: String) = println("📱 SMS to $phone: $data")
}

class SocialMediaPublisher : Observer<String> {
    override fun update(data: String) = println("🐦 Tweet: $data")
}

// Usage:
val agency = NewsAgency()
agency.subscribe(EmailSubscriber("alice@example.com"))
agency.subscribe(SMSSubscriber("+1234567890"))
agency.subscribe(SocialMediaPublisher())

agency.publishNews("Kotlin 2.0 released!")
// 📰 Breaking: Kotlin 2.0 released!
// 📧 Email to alice@example.com: Kotlin 2.0 released!
// 📱 SMS to +1234567890: Kotlin 2.0 released!
// 🐦 Tweet: Kotlin 2.0 released!
```

### Kotlin Flow/Channels (Modern Observer)
```kotlin
// Modern Kotlin uses Flow/StateFlow for reactive streams
class NewsAgency {
    private val _news = MutableSharedFlow<String>()
    val news: SharedFlow<String> = _news.asSharedFlow()

    suspend fun publishNews(headline: String) {
        _news.emit(headline)
    }
}

// Observers collect the flow
val agency = NewsAgency()
agency.news.collect { headline ->
    println("Received: $headline")
}
```

### Push vs Pull Model

| Push | Pull |
|---|---|
| Subject sends full data to observers | Subject sends "changed" signal; observer queries |
| Observer is passive | Observer is active |
| More coupling (subject knows what observer needs) | Less coupling, but observer must query |

### When to Use
- Event-driven systems (UI events, domain events).
- Pub/sub messaging.
- MVC: Model notifies View of changes.
- Reactive programming (RxJava, Flow, LiveData).

### Key Insight
> **Observer is the pattern behind every event system: DOM events, Android LiveData, RxJava, Kafka consumers, Spring ApplicationEvents. The subject doesn't know who's listening — it just broadcasts. This decoupling is powerful: add new observers without touching the subject. But beware: uncontrolled observers create hidden dependencies and debugging nightmares ("who reacted to this event?").**

---

## 3. Command

### Intent
Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.

### Problem It Solves
You want to: queue operations, log them, undo/redo them, or parameterize them — but method calls are ephemeral. You can't queue a method call. Encapsulate it as an object, and you can do all of this.

### Structure
```kotlin
// Command interface
interface Command {
    fun execute()
    fun undo()
}

// Receiver (does the actual work)
class Light {
    var isOn = false
        private set

    fun turnOn() { isOn = true; println("💡 Light is ON") }
    fun turnOff() { isOn = false; println("⚫ Light is OFF") }
}

// Concrete commands
class TurnOnLightCommand(private val light: Light) : Command {
    override fun execute() = light.turnOn()
    override fun undo() = light.turnOff()
}

class TurnOffLightCommand(private val light: Light) : Command {
    override fun execute() = light.turnOff()
    override fun undo() = light.turnOn()
}

// Invoker (triggers commands, supports undo)
class RemoteControl {
    private val history = ArrayDeque<Command>()

    fun executeCommand(command: Command) {
        command.execute()
        history.addLast(command)
    }

    fun undo() {
        val command = history.removeLastOrNull() ?: return
        command.undo()
    }
}

// Usage:
val light = Light()
val remote = RemoteControl()

remote.executeCommand(TurnOnLightCommand(light))   // 💡 Light is ON
remote.executeCommand(TurnOffLightCommand(light))  // ⚫ Light is OFF
remote.undo()  // 💡 Light is ON (undoes the last command)
remote.undo()  // ⚫ Light is OFF (undoes the first command)
```

### Advanced: Macro Commands
```kotlin
class MacroCommand(private val commands: List<Command>) : Command {
    override fun execute() = commands.forEach { it.execute() }
    override fun undo() = commands.reversed().forEach { it.undo() }
}

// Usage: one button press does multiple things
val goodnight = MacroCommand(listOf(
    TurnOffLightCommand(light),
    TurnOffLightCommand(light2),
    LockDoorCommand(door),
    SetAlarmCommand(alarm)
))
remote.executeCommand(goodnight)  // turns off all lights, locks door, sets alarm
```

### When to Use
- Undo/redo functionality.
- Job queues (serialize commands, process later).
- Macro recording (record a sequence of actions).
- Transaction management (each command is a transaction step).

### Key Insight
> **Command turns a method call into an object. This unlocks: queuing (put it on a queue), logging (serialize and persist), undo (store inverse), and composition (macro commands). It's the pattern behind editor undo/redo, job schedulers, and CQRS command handlers. The trade-off: more classes for each operation.**

---

## 4. State

### Intent
Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

### Problem It Solves
An object has different behavior depending on its state (e.g., a media player: playing, paused, stopped). Without State, you have `if (state == PLAYING)` checks scattered everywhere. With State, each state is a separate class.

### Structure
```kotlin
// State interface
interface MediaPlayerState {
    fun play(player: MediaPlayer): MediaPlayerState
    fun pause(player: MediaPlayer): MediaPlayerState
    fun stop(player: MediaPlayer): MediaPlayerState
}

// Concrete states
class PlayingState : MediaPlayerState {
    override fun play(player: MediaPlayer) = this  // already playing
    override fun pause(player: MediaPlayer): MediaPlayerState {
        println("⏸ Pausing playback")
        return PausedState()
    }
    override fun stop(player: MediaPlayer): MediaPlayerState {
        println("⏹ Stopping playback")
        return StoppedState()
    }
}

class PausedState : MediaPlayerState {
    override fun play(player: MediaPlayer): MediaPlayerState {
        println("▶ Resuming playback")
        return PlayingState()
    }
    override fun pause(player: MediaPlayer) = this  // already paused
    override fun stop(player: MediaPlayer): MediaPlayerState {
        println("⏹ Stopping from pause")
        return StoppedState()
    }
}

class StoppedState : MediaPlayerState {
    override fun play(player: MediaPlayer): MediaPlayerState {
        println("▶ Starting playback")
        return PlayingState()
    }
    override fun pause(player: MediaPlayer) = this  // can't pause when stopped
    override fun stop(player: MediaPlayer) = this   // already stopped
}

// Context
class MediaPlayer {
    private var state: MediaPlayerState = StoppedState()

    fun play() { state = state.play(this) }
    fun pause() { state = state.pause(this) }
    fun stop() { state = state.stop(this) }
}

// Usage:
val player = MediaPlayer()
player.play()   // ▶ Starting playback
player.pause()  // ⏸ Pausing playback
player.play()   // ▶ Resuming playback
player.stop()   // ⏹ Stopping playback
player.stop()   // (no effect — already stopped)
```

### State vs Strategy

| State | Strategy |
|---|---|
| Object changes its own state | Client sets the strategy |
| State transitions are internal | Strategy is external |
| States know about each other | Strategies don't know about each other |
| "I behave differently because I changed" | "I was given a different tool" |

### When to Use
- Object behavior depends on state, and behavior changes at runtime.
- State transitions have complex rules.
- You have many `if (state == X)` checks scattered in methods.

### Key Insight
> **State is Strategy with self-transitions. Each state encapsulates the behavior for that state AND knows which state to transition to. This eliminates all the `if/when` state checks — they're replaced by polymorphism. The pattern is behind every state machine: TCP connections, media players, order lifecycles, UI navigation.**

---

## 5. Template Method

### Intent
Define the skeleton of an algorithm in a base class, but let subclasses override specific steps without changing the algorithm's structure.

### Problem It Solves
You have an algorithm with a fixed structure (step 1 → step 2 → step 3), but some steps vary. You want to share the structure while allowing customization of individual steps.

### Structure
```kotlin
// Abstract class defines the algorithm skeleton
abstract class DataProcessor {
    // Template method: defines the algorithm structure (final — can't be overridden)
    fun process() {
        val data = readData()
        val processed = transformData(data)
        validateData(processed)
        saveData(processed)
    }

    // Steps that can be overridden
    abstract fun readData(): String
    abstract fun transformData(data: String): String

    // Hook: optional override (has default implementation)
    open fun validateData(data: String) {
        require(data.isNotEmpty()) { "Data cannot be empty" }
    }

    // Common step (shared by all subclasses)
    private fun saveData(data: String) {
        println("💾 Saving: $data")
    }
}

// Concrete implementations
class CSVProcessor : DataProcessor() {
    override fun readData() = "name,age\nAlice,30"
    override fun transformData(data: String) = data.uppercase()
}

class JSONProcessor : DataProcessor() {
    override fun readData() = """{"name": "Alice", "age": 30}"""
    override fun transformData(data: String) = data.replace(" ", "")
    override fun validateData(data: String) {
        require(data.contains("{") && data.contains("}")) { "Invalid JSON" }
    }
}

// Usage:
CSVProcessor().process()
// 💾 Saving: NAME,AGE\nALICE,30

JSONProcessor().process()
// 💾 Saving: {"name":"Alice","age":30}
```

### Hook Methods
- A hook is a method with a **default (often empty) implementation** that subclasses can optionally override.
- Unlike abstract methods, hooks are optional — subclasses don't have to implement them.
- Example: `open fun beforeSave() {}` — subclasses can add pre-save logic, or do nothing.

### When to Use
- You have an algorithm with a fixed structure but varying steps.
- You want to share code across similar algorithms.
- You want to enforce the algorithm structure (prevent subclasses from changing the order).

### When NOT to Use
- When the algorithm structure varies — Strategy is better.
- When you have few common steps — composition is simpler.

### Key Insight
> **Template Method is "inheritance for algorithms." The parent defines the skeleton; the child fills in the blanks. It's the pattern behind `Activity.onCreate()`, `TestNG.setUp()`, and Spring's `JdbcTemplate`. The trade-off: it uses inheritance (rigid). If the algorithm structure changes, all subclasses are affected. Prefer Strategy when flexibility is more important than structure.**

---

## 6. Iterator

### Intent
Provide a way to access the elements of an aggregate object sequentially without exposing its underlying representation.

### Problem It Solves
You have a collection (list, tree, graph, stream) and want to traverse it without knowing its internal structure. The iterator abstracts the traversal.

### Structure
```kotlin
// Iterator interface
interface Iterator<T> {
    fun hasNext(): Boolean
    fun next(): T
}

// Aggregate interface
interface Iterable<T> {
    fun iterator(): Iterator<T>
}

// Concrete aggregate: a custom list
class MyList<T>(private val items: List<T>) : Iterable<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var index = 0

        override fun hasNext() = index < items.size

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return items[index++]
        }
    }
}

// Usage:
val list = MyList(listOf("A", "B", "C"))
val iterator = list.iterator()
while (iterator.hasNext()) {
    println(iterator.next())  // A, B, C
}

// Kotlin: for-loop uses iterator automatically
for (item in list) {
    println(item)  // A, B, C
}
```

### Special Iterators

#### Reverse Iterator
```kotlin
class ReverseList<T>(private val items: List<T>) : Iterable<T> {
    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var index = items.size - 1

        override fun hasNext() = index >= 0
        override fun next() = items[index--]
    }
}
```

#### Lazy/Stream Iterator (Infinite)
```kotlin
class FibonacciIterator : Iterator<Long> {
    private var current = 0L
    private var next = 1L

    override fun hasNext() = true  // infinite

    override fun next(): Long {
        val result = current
        current = next
        next = result + next
        return result
    }
}

// Usage: take first 10 Fibonacci numbers
val fib = FibonacciIterator()
repeat(10) { println(fib.next()) }
// 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
```

### When to Use
- You want to traverse a collection without exposing its internals.
- You have multiple traversal strategies (in-order, pre-order, BFS, DFS).
- You want lazy evaluation (stream/infinite sequence).

### Key Insight
> **Iterator is so fundamental that most languages build it in: Kotlin's `for` loop, Java's enhanced for-loop, Python's `for x in`. The pattern separates traversal from the data structure. A tree can have pre-order, in-order, post-order, and BFS iterators — all without the client knowing the tree's internal structure. Kotlin's `Sequence` and Java's `Stream` are lazy iterators with functional operations.**

---

## 7. Mediator

### Intent
Define an object that encapsulates how a set of objects interact. Mediator promotes loose coupling by keeping objects from referring to each other explicitly.

### Problem It Solves
Objects communicate directly with each other → tight coupling → spaghetti. The mediator centralizes communication: objects talk to the mediator, the mediator routes messages.

### Structure
```kotlin
// Mediator interface
interface ChatMediator {
    fun sendMessage(message: String, user: User)
    fun addUser(user: User)
}

// Colleague (objects that communicate through mediator)
class User(val name: String, private val mediator: ChatMediator) {
    fun send(message: String) {
        println("$name sends: $message")
        mediator.sendMessage(message, this)
    }

    fun receive(message: String) {
        println("$name receives: $message")
    }
}

// Concrete mediator
class ChatRoom : ChatMediator {
    private val users = mutableListOf<User>()

    override fun addUser(user: User) = users.add(user)

    override fun sendMessage(message: String, user: User) {
        // Mediator decides who receives the message
        users.filter { it != user }.forEach { it.receive(message) }
    }
}

// Usage:
val room = ChatRoom()
val alice = User("Alice", room)
val bob = User("Bob", room)
val charlie = User("Charlie", room)

room.addUser(alice)
room.addUser(bob)
room.addUser(charlie)

alice.send("Hi everyone!")
// Alice sends: Hi everyone!
// Bob receives: Hi everyone!
// Charlie receives: Hi everyone!
```

### Without Mediator (The Problem)
```
Without mediator: each User needs a reference to every other User.
  Alice → Bob, Charlie
  Bob → Alice, Charlie
  Charlie → Alice, Bob
  N users → N*(N-1) references. Adding a user requires updating all others.
```

### With Mediator
```
With mediator: each User only knows the mediator.
  Alice → Mediator
  Bob → Mediator
  Charlie → Mediator
  Adding a user: just register with the mediator. Others don't change.
```

### When to Use
- Many objects communicate in complex ways.
- You want to decouple objects from each other.
- You want to centralize communication logic (routing, filtering, logging).

### Key Insight
> **Mediator is the pattern behind every chat system, air traffic control, and event bus. It replaces N×N communication with N×1. The trade-off: the mediator can become a "God Object" — it knows about all colleagues and all routing rules. Keep it focused on routing, not business logic.**

---

## 8. Chain of Responsibility

### Intent
Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until one handles it.

### Problem It Solves
A request may be handled by different handlers depending on conditions (auth, logging, validation, business logic). You don't want the sender to know which handler will process it.

### Structure
```kotlin
// Handler interface
abstract class Handler(private val next: Handler? = null) {
    fun handle(request: Request): Response {
        if (canHandle(request)) {
            return process(request)
        }
        return next?.handle(request) ?: Response("Unhandled: ${request.type}")
    }

    protected abstract fun canHandle(request: Request): Boolean
    protected abstract fun process(request: Request): Response
}

// Concrete handlers
class AuthHandler(next: Handler? = null) : Handler(next) {
    override fun canHandle(request: Request) = request.type == "AUTH"
    override fun process(request: Request) = Response("Authenticated: ${request.userId}")
}

class LoggingHandler(next: Handler? = null) : Handler(next) {
    override fun canHandle(request: Request) = request.type == "LOG"
    override fun process(request: Request) = Response("Logged: ${request.message}")
}

class ErrorHandler(next: Handler? = null) : Handler(next) {
    override fun canHandle(request: Request) = request.type == "ERROR"
    override fun process(request: Request) = Response("Error handled: ${request.message}")
}

// Usage: build the chain
val chain = AuthHandler(LoggingHandler(ErrorHandler()))

chain.handle(Request("AUTH", userId = "alice"))   // "Authenticated: alice"
chain.handle(Request("LOG", message = "test"))     // "Logged: test"
chain.handle(Request("ERROR", message = "crash")) // "Error handled: crash"
```

### Kotlin Functional Chain (Middleware)
```kotlin
// Kotlin: chain as function composition (like Ktor/Express middleware)
class Pipeline {
    private val interceptors = mutableListOf<(Request, () -> Response) -> Response>()

    fun addInterceptor(interceptor: (Request, () -> Response) -> Response) =
        apply { interceptors.add(interceptor) }

    fun execute(request: Request): Response {
        val final: () -> Response = { Response("Default response") }
        return interceptors.foldRight(final) { interceptor, next ->
            { interceptor(request, next) }
        }()
    }
}

// Usage:
val pipeline = Pipeline()
    .addInterceptor { req, next ->
        println("Auth: checking ${req.userId}")
        next()
    }
    .addInterceptor { req, next ->
        println("Logging: ${req.type}")
        next()
    }
    .addInterceptor { req, next ->
        Response("Handled: ${req.type}")
    }

pipeline.execute(Request("AUTH", userId = "alice"))
// Auth: checking alice
// Logging: AUTH
// → Response("Handled: AUTH")
```

### When to Use
- Multiple handlers may process a request (middleware pipeline).
- You want to decouple sender from receiver.
- The order of handlers matters.

### Key Insight
> **Chain of Responsibility is the pattern behind HTTP middleware (Express, Ktor, Spring Filter), exception handling (try-catch chain), and event bubbling (DOM). The request flows through the chain; each handler decides: handle it or pass it along. The sender doesn't know (or care) which handler processes it.**

---

## 9. Visitor

### Intent
Represent an operation to be performed on the elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements.

### Problem It Solves
You have a stable object structure (e.g., a tree of shapes: Circle, Square, Triangle) but frequently add new operations (export to JSON, export to XML, calculate area, render). Without Visitor, every new operation requires modifying every shape class. With Visitor, you add a new Visitor — no shape class changes.

### Structure
```kotlin
// Element interface
interface Shape {
    fun accept(visitor: ShapeVisitor): String
}

// Concrete elements
class Circle(val radius: Double) : Shape {
    override fun accept(visitor: ShapeVisitor) = visitor.visitCircle(this)
}

class Square(val side: Double) : Shape {
    override fun accept(visitor: ShapeVisitor) = visitor.visitSquare(this)
}

class Rectangle(val width: Double, val height: Double) : Shape {
    override fun accept(visitor: ShapeVisitor) = visitor.visitRectangle(this)
}

// Visitor interface
interface ShapeVisitor {
    fun visitCircle(circle: Circle): String
    fun visitSquare(square: Square): String
    fun visitRectangle(rect: Rectangle): String
}

// Concrete visitors: each is a new operation
class AreaCalculator : ShapeVisitor {
    override fun visitCircle(c: Circle) = "Circle area: ${Math.PI * c.radius * c.radius}"
    override fun visitSquare(s: Square) = "Square area: ${s.side * s.side}"
    override fun visitRectangle(r: Rectangle) = "Rectangle area: ${r.width * r.height}"
}

class JSONExporter : ShapeVisitor {
    override fun visitCircle(c: Circle) = """{"type": "circle", "radius": ${c.radius}}"""
    override fun visitSquare(s: Square) = """{"type": "square", "side": ${s.side}}"""
    override fun visitRectangle(r: Rectangle) = """{"type": "rect", "w": ${r.width}, "h": ${r.height}}"""
}

// Usage:
val shapes: List<Shape> = listOf(Circle(5.0), Square(4.0), Rectangle(3.0, 6.0))

// New operation = new visitor, no shape class changes
val areaCalc = AreaCalculator()
val jsonExporter = JSONExporter()

shapes.forEach { println(it.accept(areaCalc)) }
// Circle area: 78.54
// Square area: 16.0
// Rectangle area: 18.0

shapes.forEach { println(it.accept(jsonExporter)) }
// {"type": "circle", "radius": 5.0}
// {"type": "square", "side": 4.0}
// {"type": "rect", "w": 3.0, "h": 6.0}
```

### Double Dispatch
Visitor uses **double dispatch**:
1. First dispatch: `shape.accept(visitor)` → calls the right method on the shape (polymorphism).
2. Second dispatch: `visitor.visitCircle(this)` → calls the right method on the visitor (overloading).

### When to Use
- Object structure is stable (rarely add new element types).
- Operations change frequently (often add new operations).
- You want to keep operations separate from the object structure.

### When NOT to Use
- You frequently add new element types → you must update every visitor.
- The object structure is simple — a `when` statement is simpler.

### Key Insight
> **Visitor is the "opposite" of Strategy. Strategy adds new algorithms to one class. Visitor adds new operations to a structure of classes. Use Visitor when the structure is stable but operations vary. Use Strategy when the algorithm varies but the structure is stable. Visitor is the pattern behind compilers (AST visitors), serializers, and code generators.**

---

## 10. Memento

### Intent
Without violating encapsulation, capture and externalize an object's internal state so that the object can be restored to this state later.

### Problem It Solves
You want to save an object's state (for undo/redo, checkpoint, snapshot) but you don't want to expose its private fields. The memento captures the state opaquely — only the originator can read it.

### Structure
```kotlin
// Memento: stores state (immutable)
data class EditorMemento(val content: String, val cursorPosition: Int)

// Originator: creates and restores mementos
class TextEditor {
    var content: String = ""
        private set
    var cursorPosition: Int = 0
        private set

    fun type(text: String) {
        content += text
        cursorPosition = content.length
    }

    fun moveCursor(position: Int) {
        cursorPosition = position
    }

    fun save(): EditorMemento = EditorMemento(content, cursorPosition)

    fun restore(memento: EditorMemento) {
        content = memento.content
        cursorPosition = memento.cursorPosition
    }
}

// Caretaker: manages mementos (undo/redo stack)
class History {
    private val undoStack = ArrayDeque<EditorMemento>()
    private val redoStack = ArrayDeque<EditorMemento>()

    fun save(state: EditorMemento) {
        undoStack.addLast(state)
        redoStack.clear()  // new action invalidates redo history
    }

    fun undo(): EditorMemento? {
        if (undoStack.isEmpty()) return null
        val memento = undoStack.removeLast()
        redoStack.addLast(memento)
        return memento
    }

    fun redo(): EditorMemento? {
        if (redoStack.isEmpty()) return null
        return redoStack.removeLast()
    }
}

// Usage:
val editor = TextEditor()
val history = History()

history.save(editor.save())
editor.type("Hello ")
history.save(editor.save())
editor.type("World")
println(editor.content)  // "Hello World"

// Undo
editor.restore(history.undo()!!)
println(editor.content)  // "Hello "

// Redo
editor.restore(history.redo()!!)
println(editor.content)  // "Hello World"
```

### When to Use
- Undo/redo functionality.
- Checkpoint/snapshot (game saves, transaction rollback).
- State recovery after failure.

### Key Insight
> **Memento is the pattern behind undo/redo in editors, transaction rollback in databases, and game save states. The key: the memento is opaque — the caretaker holds it but can't read it. Only the originator can restore from it. This preserves encapsulation while enabling state capture.**

---

## Summary: When to Use Which Behavioral Pattern

| Pattern | When to Use | Key Benefit |
|---|---|---|
| **Strategy** | Multiple algorithms for same task | Swap algorithms at runtime |
| **Observer** | One-to-many notification | Decouple subject from observers |
| **Command** | Queue, log, undo operations | Encapsulate method calls |
| **State** | Behavior depends on state | Eliminate state conditionals |
| **Template Method** | Fixed algorithm, varying steps | Share structure, customize steps |
| **Iterator** | Traverse collection | Hide internal structure |
| **Mediator** | Many objects communicate | Centralize communication |
| **Chain of Responsibility** | Pipeline of handlers | Decouple sender from handler |
| **Visitor** | Stable structure, varying operations | Add operations without modifying classes |
| **Memento** | Save/restore state | Undo/redo, snapshots |
