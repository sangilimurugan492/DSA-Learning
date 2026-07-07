# Architectural Patterns

Architectural patterns define the **high-level structure** of a software system. They dictate how major components are organized, how they interact, and where the boundaries are. These patterns operate at a higher level than GoF patterns — they shape the entire application.

---

## 1. Layered Architecture (N-Tier)

### Intent
Organize the system into horizontal layers, each with a specific responsibility. Each layer only communicates with the layer directly below it.

### Structure
```
┌─────────────────────────┐
│  Presentation Layer     │  ← UI, controllers, API endpoints
├─────────────────────────┤
│  Business Logic Layer   │  ← Services, domain rules, orchestration
├─────────────────────────┤
│  Data Access Layer      │  ← Repositories, DAOs, ORM
├─────────────────────────┤
│  Database               │  ← PostgreSQL, MongoDB
└─────────────────────────┘
```

### Example
```kotlin
// Presentation Layer
class UserController(private val userService: UserService) {
    fun getUser(id: String): UserDTO {
        val user = userService.findById(id)
        return user.toDTO()
    }
}

// Business Logic Layer
class UserService(private val repo: UserRepository) {
    fun findById(id: String): User {
        return repo.findById(id) ?: throw NotFoundException("User not found")
    }
}

// Data Access Layer
class UserRepository(private val db: Database) {
    fun findById(id: String): User? {
        return db.query("SELECT * FROM users WHERE id = ?", id)
    }
}
```

### Rules
1. Each layer only depends on the layer directly below.
2. Dependencies flow **downward** — never upward.
3. The presentation layer never touches the database directly.

### When to Use
- Small to medium applications.
- Teams that want simplicity and clear separation.
- CRUD-heavy applications.

### When NOT to Use
- Complex domain logic (use Clean Architecture or Hexagonal).
- Highly scalable systems (layers add latency and coupling).

### Key Insight
> **Layered architecture is the default — most apps start here. The problem: layers are often bypassed (controller calls DB directly) or leak (business logic in controllers). The rule is simple: each layer talks only to the one below. If you find a controller calling the DB, you've broken the layering.**

---

## 2. MVC (Model-View-Controller)

### Intent
Separate the application into three interconnected components: Model (data + logic), View (UI), and Controller (input handling). The controller mediates between model and view.

### Structure
```
    User
      │
      ▼
┌──────────┐     ┌──────────┐     ┌──────────┐
│   View   │◄───►│Controller│◄───►│  Model   │
│ (UI)     │     │ (Logic)  │     │ (Data)   │
└──────────┘     └──────────┘     └──────────┘
```

### Flow
1. User interacts with the **View** (clicks button).
2. **Controller** receives the input, updates the **Model**.
3. **Model** changes state, notifies the **View** (Observer pattern).
4. **View** re-renders based on the new model state.

### Example (Android/Web)
```kotlin
// Model
data class User(val name: String, val email: String)

// View (Android XML / HTML template)
// <TextView android:id="@+id/userName" />
// <TextView android:id="@+id/userEmail" />

// Controller
class UserController {
    private val view: UserView
    private val model: User

    fun onUserLoaded(user: User) {
        view.showName(user.name)
        view.showEmail(user.email)
    }

    fun onUpdateNameButtonClicked(newName: String) {
        // Update model
        // Refresh view
    }
}
```

### MVC Variants

| Pattern | Controller's Role | View-Model Coupling |
|---|---|---|
| **MVC** | Mediator between View and Model | View knows Model directly |
| **MVP** | Presenter updates View via interface | View doesn't know Model |
| **MVVM** | ViewModel exposes state; View binds to it | View binds to ViewModel (data binding) |

### Key Insight
> **MVC is the grandfather of UI architecture. The key insight: separate what the user sees (View) from what the app knows (Model) from how it responds (Controller). The problem with MVC: the View often knows the Model directly, creating tight coupling. MVP and MVVM fix this by making the View completely passive.**

---

## 3. MVVM (Model-View-ViewModel)

### Intent
Separate the UI (View) from the business logic and state (ViewModel) using data binding. The ViewModel exposes state that the View observes automatically.

### Structure
```
┌──────────┐   data binding   ┌──────────────┐     ┌──────────┐
│   View   │◄────────────────►│  ViewModel   │◄───►│  Model   │
│ (XML/UI) │                  │ (State+Logic)│     │ (Data)   │
└──────────┘                  └──────────────┘     └──────────┘
```

### Example (Android with ViewModel + LiveData/StateFlow)
```kotlin
// Model
data class User(val name: String, val email: String)

// ViewModel: holds state, survives configuration changes
class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUser(id: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val user = repo.findById(id)
                _uiState.value = UiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class UiState {
    object Loading : UiState()
    data class Success(val user: User) : UiState()
    data class Error(val message: String) : UiState()
}

// View (Activity/Fragment): observes ViewModel state
class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Data binding: UI automatically updates when state changes
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> showProgressBar()
                    is UiState.Success -> {
                        hideProgressBar()
                        userNameText.text = state.user.name
                        userEmailText.text = state.user.email
                    }
                    is UiState.Error -> showError(state.message)
                }
            }
        }
        viewModel.loadUser("123")
    }
}
```

### MVVM vs MVP

| MVP | MVVM |
|---|---|
| Presenter calls View methods directly | ViewModel exposes state; View binds |
| 1:1 relationship (Presenter per View) | 1:N relationship (one ViewModel, multiple Views) |
| Imperative (presenter tells view what to do) | Declarative (view reacts to state changes) |
| Harder to test (View interface needed) | Easier to test (ViewModel has no View reference) |

### Key Insight
> **MVVM is the standard for modern UI: Android (ViewModel + StateFlow), iOS (SwiftUI + ObservableObject), React (hooks + state). The key insight: the ViewModel has NO reference to the View. It exposes state; the View observes and renders. This makes the ViewModel fully testable — no UI needed. The data binding is the "magic" that connects them.**

---

## 4. Microservices

### Intent
Build a system as a collection of small, independent services, each running in its own process, communicating via lightweight protocols (HTTP, gRPC, messages).

### Structure
```
                    ┌──────────────┐
                    │  API Gateway  │
                    └──────┬───────┘
           ┌────────────────┼────────────────┐
           ▼                ▼                ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │  User    │    │  Order   │    │ Payment  │
    │ Service  │    │ Service  │    │ Service  │
    └────┬─────┘    └────┬─────┘    └────┬─────┘
         │               │               │
    ┌────┴─────┐    ┌────┴─────┐    ┌────┴─────┐
    │ User DB  │    │ Order DB │    │Payment DB│
    └──────────┘    └──────────┘    └──────────┘
```

### Principles
1. **Single responsibility**: Each service owns one business capability.
2. **Independent deployment**: Deploy one service without touching others.
3. **Decentralized data**: Each service has its own database (no shared DB).
4. **Polyglot**: Each service can use a different language/technology.
5. **Failure isolation**: If Order Service crashes, User Service still works.

### Communication Patterns

| Pattern | Protocol | When to Use |
|---|---|---|
| **Synchronous (REST/gRPC)** | HTTP | Need immediate response |
| **Asynchronous (Events)** | Kafka/RabbitMQ | Fire and forget, decoupling |
| **Service Mesh** | Sidecar proxy | Cross-cutting (auth, tracing, retries) |

### Microservices vs Monolith

| Monolith | Microservices |
|---|---|
| Single deployable | Multiple independent deploys |
| Shared database | Database per service |
| Simple to develop | Complex to orchestrate |
| Hard to scale parts | Scale each service independently |
| Single failure domain | Failure isolation |
| Simple to test | Integration testing is hard |

### When to Use Microservices
- Team is large (> 10 developers).
- Different parts have different scale requirements.
- You need independent deployment.
- You need technology diversity.

### When NOT to Use
- Small team (< 5 developers).
- Simple domain.
- You can't afford the operational complexity (CI/CD, monitoring, service discovery).

### Key Insight
> **Microservices solve organizational scaling, not technical scaling. The real benefit: teams work independently. The cost: distributed system complexity (network failures, eventual consistency, distributed transactions). "Don't start with microservices. Start with a monolith, extract services when you feel the pain." — Most engineering leaders.**

---

## 5. Event-Driven Architecture

### Intent
The system reacts to events (state changes) rather than being called directly. Producers emit events; consumers react. Components are decoupled through an event bus.

### Structure
```
┌──────────┐     ┌──────────────┐     ┌──────────┐
│  Order   │────►│  Event Bus   │────►│  Email   │
│ Service  │     │  (Kafka)     │     │ Service  │
└──────────┘     └──────┬───────┘     └──────────┘
                        │
                 ┌──────┴───────┐
                 ▼              ▼
           ┌──────────┐  ┌──────────┐
           │ Analytics │  │ Inventory│
           │ Service  │  │ Service  │
           └──────────┘  └──────────┘
```

### Event Flow
```kotlin
// Producer: Order Service emits an event
class OrderService(private val eventBus: EventBus) {
    fun placeOrder(order: Order) {
        saveOrder(order)
        eventBus.publish(OrderPlacedEvent(order.id, order.userId, order.total))
    }
}

// Consumer: Email Service reacts
class EmailService {
    fun onOrderPlaced(event: OrderPlacedEvent) {
        sendConfirmationEmail(event.userId, event.orderId)
    }
}

// Consumer: Inventory Service reacts
class InventoryService {
    fun onOrderPlaced(event: OrderPlacedEvent) {
        reserveInventory(event.orderId)
    }
}

// Consumer: Analytics Service reacts
class AnalyticsService {
    fun onOrderPlaced(event: OrderPlacedEvent) {
        trackRevenue(event.total)
    }
}
```

### Event-Driven vs Request-Driven

| Request-Driven (REST) | Event-Driven |
|---|---|
| Synchronous: caller waits | Asynchronous: fire and forget |
| Tight coupling: caller knows callee | Loose coupling: producer doesn't know consumers |
| Easy to trace: one call → one response | Hard to trace: one event → N reactions |
| Cascading failures | Failure isolation |
| Lower latency (direct call) | Higher latency (queue + process) |

### When to Use
- Multiple systems need to react to the same event.
- You need decoupling between producers and consumers.
- You need to buffer traffic spikes (queue absorbs bursts).
- You need audit trails (event log = history).

### Key Insight
> **Event-driven architecture is the pattern behind every notification system, every analytics pipeline, and every microservices communication that needs decoupling. The key insight: the producer doesn't know who consumes the event. Add a new consumer (e.g., a new recommendation service) without touching the Order Service. The trade-off: debugging is hard ("which event triggered what?"), and eventual consistency replaces immediate consistency.**

---

## 6. Hexagonal Architecture (Ports and Adapters)

### Intent
Isolate the business logic from external concerns (database, UI, APIs). The application core defines "ports" (interfaces); external systems connect via "adapters" (implementations).

### Structure
```
                    ┌─────────────────────────┐
   ┌──────────┐     │     ┌─────────────┐      │     ┌──────────┐
   │   Web    │────►│     │ Application │      │◄────│ Database │
   │ Adapter  │     │PORT │    Core     │ PORT │     │ Adapter  │
   └──────────┘     │     │ (Domain +   │      │     └──────────┘
                    │     │  Use Cases) │      │
   ┌──────────┐     │     └─────────────┘      │     ┌──────────┐
   │  CLI     │────►│                          │◄────│   REST   │
   │ Adapter  │     │                          │     │ Adapter  │
   └──────────┘     └─────────────────────────┘     └──────────┘
```

### Example
```kotlin
// PORT: interface defined by the application core
interface UserRepository {
    fun findById(id: String): User?
    fun save(user: User)
}

// ADAPTER: implementation for a specific technology (PostgreSQL)
class PostgresUserRepository(private val db: Database) : UserRepository {
    override fun findById(id: String): User? {
        return db.query("SELECT * FROM users WHERE id = ?", id)
    }

    override fun save(user: User) {
        db.execute("INSERT INTO users VALUES (?, ?, ?)", user.id, user.name, user.email)
    }
}

// ADAPTER: implementation for a different technology (in-memory, for testing)
class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<String, User>()

    override fun findById(id: String) = users[id]
    override fun save(user: User) { users[user.id] = user }
}

// APPLICATION CORE: knows nothing about databases, HTTP, or frameworks
class UserService(private val repo: UserRepository) {
    fun registerUser(name: String, email: String): User {
        val user = User(UUID.randomUUID().toString(), name, email)
        repo.save(user)
        return user
    }
}

// Wiring: choose adapters at the composition root
val service = UserService(PostgresUserRepository(database))  // production
val testService = UserService(InMemoryUserRepository())      // testing
```

### Key Insight
> **Hexagonal architecture makes the business logic completely framework-agnostic. The core defines ports (interfaces); the outside provides adapters (implementations). Swap PostgreSQL for MongoDB? Write a new adapter. The core doesn't change. This is the ultimate expression of the Dependency Inversion Principle: the core owns the interfaces; the outside adapts to them.**

---

## 7. Clean Architecture

### Intent
Separate concerns into concentric layers. Dependencies point inward — the innermost layer (domain) knows nothing about outer layers. The domain is pure business logic, free of frameworks, databases, and UI.

### Structure
```
┌───────────────────────────────────────────────┐
│  Frameworks & Drivers (Web, DB, UI)          │  ← Outermost: details
│  ┌───────────────────────────────────────┐   │
│  │  Interface Adapters (Controllers, Gateways)│  ← converters
│  │  ┌───────────────────────────────┐    │   │
│  │  │  Use Cases (Application Services)│   │  ← application logic
│  │  │  ┌───────────────────────┐     │   │   │
│  │  │  │  Entities (Domain)     │     │   │   │  ← innermost: pure business
│  │  │  └───────────────────────┘     │   │   │
│  │  └───────────────────────────────┘    │   │
│  └───────────────────────────────────────┘   │
└───────────────────────────────────────────────┘

Dependency Rule: dependencies point INWARD only.
```

### Example
```kotlin
// ENTITIES (innermost): pure domain, no framework dependencies
data class User(val id: UserId, val name: String, val email: Email)

class Email(value: String) {
    init {
        require(value.contains("@")) { "Invalid email" }
    }
}

// USE CASES: application logic, depends only on entities
class RegisterUserUseCase(private val userRepo: UserRepository, private val notifier: Notifier) {
    fun execute(name: String, email: String): User {
        val user = User(UserId.generate(), name, Email(email))
        userRepo.save(user)
        notifier.sendWelcomeEmail(user)
        return user
    }
}

// INTERFACE ADAPTERS: convert between use cases and external formats
class UserController(private val registerUser: RegisterUserUseCase) {
    fun register(request: RegisterUserRequest): RegisterUserResponse {
        return try {
            val user = registerUser.execute(request.name, request.email)
            RegisterUserResponse.Success(user.id.value)
        } catch (e: Exception) {
            RegisterUserResponse.Error(e.message ?: "Registration failed")
        }
    }
}

// FRAMEWORKS & DRIVERS (outermost): actual implementations
class SpringUserController(...)  // Spring @RestController
class PostgresUserRepository(...) // PostgreSQL implementation
class SmtpNotifier(...)          // SMTP email sender
```

### The Dependency Rule
> **Source code dependencies must point only inward, toward higher-level policies.**
- Entities know nothing about use cases.
- Use cases know nothing about controllers.
- Controllers know nothing about the web framework.
- The database knows nothing about the use cases.

### Clean Architecture vs Hexagonal

| Clean Architecture | Hexagonal |
|---|---|
| Concentric layers (4+) | Ports and adapters (2 sides) |
| More structured (entities, use cases, adapters, frameworks) | Simpler (core + ports + adapters) |
| Stricter separation | More flexible |
| By Uncle Bob | By Alistair Cockburn |

### Key Insight
> **Clean Architecture is the gold standard for complex domain applications. The key insight: the domain (entities + use cases) is pure — it has no dependencies on Spring, PostgreSQL, or HTTP. You can test it in isolation. You can swap the database without touching business logic. You can change the UI without touching use cases. The trade-off: more layers = more boilerplate. Use it for complex domains, not CRUD apps.**

---

## 8. CQRS (Command Query Responsibility Segregation)

### Intent
Separate the write model (commands) from the read model (queries). Optimize each independently.

### Structure
```
         WRITE SIDE                    READ SIDE
    ┌──────────────┐              ┌──────────────┐
    │  Command API  │              │   Query API   │
    │  (POST/PUT)  │              │    (GET)      │
    └──────┬───────┘              └──────┬───────┘
           │                             │
    ┌──────┴───────┐              ┌──────┴───────┐
    │ Write Model  │─── event ───►│  Read Model  │
    │ (normalized  │              │ (denormalized│
    │  DB, ACID)   │              │  view DB)    │
    └──────────────┘              └──────────────┘
```

### Example
```kotlin
// WRITE SIDE: optimized for validation, business rules
class OrderCommandHandler(private val orderRepo: OrderWriteRepository) {
    fun handle(command: PlaceOrderCommand) {
        val order = Order.create(command.userId, command.items)
        orderRepo.save(order)  // normalized: orders table + order_items table
        // Event is published to update read model
    }
}

// READ SIDE: optimized for queries, denormalized
class OrderQueryHandler(private val readRepo: OrderReadRepository) {
    fun getOrderSummary(userId: String): List<OrderSummaryDTO> {
        // Denormalized: one row per order with all info
        return readRepo.findOrderSummariesByUser(userId)
    }

    fun getOrderHistory(userId: String, page: Page): List<OrderHistoryDTO> {
        return readRepo.findOrderHistory(userId, page)
    }
}

// The read model is updated asynchronously via events
class OrderEventHandler(private val readRepo: OrderReadRepository) {
    fun onOrderPlaced(event: OrderPlacedEvent) {
        // Update the denormalized read model
        readRepo.insertOrderSummary(
            OrderSummaryDTO(
                orderId = event.orderId,
                userId = event.userId,
                total = event.total,
                itemCount = event.itemCount,
                placedAt = event.timestamp
            )
        )
    }
}
```

### When to Use
- Read:write ratio is very high (100:1+).
- Read and write need different schemas (write = normalized, read = denormalized).
- Different scale needs (1 write node, 10 read replicas).

### When NOT to Use
- Simple CRUD — CQRS adds complexity.
- Read:write is close to 1:1.
- You can't tolerate eventual consistency between write and read models.

### Key Insight
> **CQRS is the pattern behind event sourcing, materialized views, and read replicas. The key insight: writes and reads have fundamentally different needs. Writes need validation, ACID, normalization. Reads need speed, denormalization, caching. By separating them, you optimize each independently. The trade-off: eventual consistency (the read model lags behind the write model by milliseconds to seconds).**

---

## Summary: When to Use Which Architectural Pattern

| Pattern | When to Use | Key Benefit |
|---|---|---|
| **Layered** | Simple apps, CRUD | Clear separation |
| **MVC** | Web apps with simple UI | Separation of concerns |
| **MVVM** | Modern UI (Android, iOS, React) | Testable ViewModels, data binding |
| **Microservices** | Large teams, independent scaling | Independent deployment |
| **Event-Driven** | Decoupled, reactive systems | Loose coupling, async |
| **Hexagonal** | Framework-agnostic domain | Swap adapters without touching core |
| **Clean Architecture** | Complex domain logic | Domain is pure, fully testable |
| **CQRS** | High read:write ratio | Optimize reads and writes independently |

### The Architect's Decision Framework
```
Small app, simple CRUD?           → Layered Architecture
Web app with UI?                   → MVC or MVVM
Large team, independent services?  → Microservices
Reactive, event-based system?      → Event-Driven
Complex domain, framework-agnostic?→ Clean Architecture or Hexagonal
High read:write, different schemas?→ CQRS
```

### Key Insight
> **Architectural patterns are not mutually exclusive. A microservices system can use CQRS internally. A Clean Architecture app can use MVVM for its UI. Event-driven communication can connect microservices. The architect's job is to choose the right pattern for each concern — not to force one pattern everywhere. Start simple (layered), extract complexity (Clean Architecture) when the domain demands it, and split into microservices when the team outgrows a monolith.**
