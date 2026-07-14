# I — Interface Segregation Principle (ISP)

> **Clients should not be forced to depend on interfaces they do not use.**

---

## What It Really Means

Don't create "fat" interfaces with methods that not all implementers need. Split large interfaces into smaller, focused ones. A class should never be forced to implement a method it can't fulfill.

ISP is SRP applied to **interfaces**. Just as a class should have one responsibility, an interface should expose one cohesive set of methods.

---

## The Problem: Fat Interface

```kotlin
// BAD: Fat interface — not all machines can do everything
interface Machine {
    fun print(document: Document)
    fun scan(document: Document)
    fun fax(document: Document)
}

// A simple printer is forced to implement scan and fax
class SimplePrinter : Machine {
    override fun print(document: Document) {
        println("Printing: ${document.content}")
    }

    override fun scan(document: Document) {
        throw NotImplementedError("Simple printer can't scan!") // 💥 forced to implement
    }

    override fun fax(document: Document) {
        throw NotImplementedError("Simple printer can't fax!")  // 💥 forced to implement
    }
}
```

### Why This Is Bad
1. **Forced implementation**: `SimplePrinter` must implement `scan()` and `fax()` even though it can't do them.
2. **LSP violation**: Throwing `NotImplementedException` means `SimplePrinter` is not substitutable for `Machine`.
3. **False advertising**: The interface says "I can print, scan, and fax." `SimplePrinter` claims to be a `Machine` but can't do 2 out of 3 operations.
4. **Coupling**: Clients that only need `print()` are coupled to `scan()` and `fax()` through the fat interface. If `scan()` signature changes, `SimplePrinter` must recompile even though it doesn't scan.

---

## The Solution: Segregate Interfaces

```kotlin
// GOOD: Split into focused interfaces
interface Printer {
    fun print(document: Document)
}

interface Scanner {
    fun scan(document: Document): ScannedImage
}

interface Fax {
    fun fax(document: Document, destination: String)
}

// Simple printer: only implements what it can do
class SimplePrinter : Printer {
    override fun print(document: Document) {
        println("Printing: ${document.content}")
    }
}

// Multi-function machine: implements all three
class MultiFunctionMachine(
    private val printer: Printer,
    private val scanner: Scanner,
    private val fax: Fax
) : Printer, Scanner, Fax {
    override fun print(document: Document) = printer.print(document)
    override fun scan(document: Document) = scanner.scan(document)
    override fun fax(document: Document, destination: String) = fax.fax(document, destination)
}

// Usage:
val simplePrinter: Printer = SimplePrinter()
simplePrinter.print(Document("Hello"))  // ✅ only print

val multiMachine: Printer = MultiFunctionMachine(/* ... */)
multiMachine.print(Document("Hello"))  // ✅ can also be used as Printer
```

Now:
- `SimplePrinter` only depends on `Printer` — no `scan()` or `fax()`.
- Clients that need printing only depend on `Printer`.
- If `Scanner` interface changes, `SimplePrinter` is unaffected.

---

## How to Detect ISP Violations

### Smell 1: `NotImplementedException` or `UnsupportedOperationException`
```kotlin
class MyList : MutableList<T> {
    override fun add(element: T): Boolean {
        throw UnsupportedOperationException("Read-only list") // 💥 ISP violation
    }
}
```
If you're throwing "not supported" in an interface implementation, the interface is too fat.

### Smell 2: Empty Method Implementations
```kotlin
class NoOpCache : Cache {
    override fun put(key: String, value: Any) { /* empty — no-op */ }
    override fun get(key: String): Any? = null  /* empty — no-op */
    override fun clear() { /* empty — no-op */ }
}
```
If methods are empty or return defaults, the interface is too fat. Create a `NoOpCache` that implements a smaller interface, or use the Null Object pattern explicitly.

### Smell 3: Implementers Use Only a Subset of Methods
If a class implements a 10-method interface but only uses 3 methods, the interface is too fat.

### Smell 4: Interface Has More Than ~5-7 Methods
Large interfaces often mix concerns. Review and split.

---

## Real-World Example: The Fat Service Interface

### Before (Bad)
```kotlin
// BAD: One fat interface for everything user-related
interface UserService {
    // Auth operations
    fun login(email: String, password: String): Session
    fun logout(session: Session)
    fun refreshToken(session: Session): Session

    // Profile operations
    fun getProfile(userId: String): UserProfile
    fun updateProfile(userId: String, profile: UserProfile)
    fun uploadAvatar(userId: String, image: ByteArray)

    // Admin operations
    fun deleteUser(userId: String)
    fun banUser(userId: String, reason: String)
    fun listAllUsers(page: Int): List<User>

    // Analytics operations
    fun getUserActivity(userId: String): ActivityLog
    fun getLoginStats(): LoginStats
}

// A mobile app client only needs auth + profile, but is forced to depend on admin + analytics
class MobileAuthClient(private val service: UserService) {
    fun login(email: String, password: String) {
        val session = service.login(email, password)
        // ...
    }
    // This client depends on UserService, which includes deleteUser, banUser, getLoginStats...
    // If deleteUser() signature changes, MobileAuthClient must recompile.
    // MobileAuthClient has no business knowing about admin operations.
}
```

### After (Good)
```kotlin
// GOOD: Split by client need
interface AuthService {
    fun login(email: String, password: String): Session
    fun logout(session: Session)
    fun refreshToken(session: Session): Session
}

interface ProfileService {
    fun getProfile(userId: String): UserProfile
    fun updateProfile(userId: String, profile: UserProfile)
    fun uploadAvatar(userId: String, image: ByteArray)
}

interface UserAdminService {
    fun deleteUser(userId: String)
    fun banUser(userId: String, reason: String)
    fun listAllUsers(page: Int): List<User>
}

interface UserAnalyticsService {
    fun getUserActivity(userId: String): ActivityLog
    fun getLoginStats(): LoginStats
}

// Mobile client: only depends on what it needs
class MobileAuthClient(
    private val authService: AuthService,
    private val profileService: ProfileService
) {
    fun login(email: String, password: String) {
        val session = authService.login(email, password)
        // ...
    }
    // No dependency on UserAdminService or UserAnalyticsService
    // If deleteUser() changes, MobileAuthClient is unaffected
}

// Admin client: depends on admin + analytics
class AdminClient(
    private val adminService: UserAdminService,
    private val analyticsService: UserAnalyticsService
) {
    fun banUser(userId: String, reason: String) {
        adminService.banUser(userId, reason)
    }
}
```

---

## ISP and the Dependency Rule

ISP connects to DIP (Dependency Inversion). When a high-level module depends on an interface, that interface should contain **only the methods the module needs** — nothing more.

```
Without ISP:
  Controller → UserService (15 methods)
  Controller only uses 2 methods, but depends on all 15.
  Change to any of the 13 unused methods → recompile Controller.

With ISP:
  Controller → UserReader (2 methods)
  Controller only depends on what it uses.
  Change to UserAdminService → Controller unaffected.
```

---

## Interface Segregation in Standard Libraries

### Java/Kotlin Collections (Good Example)
```kotlin
// Kotlin collections are well-segregated:
interface Collection<T>          // size, contains, isEmpty, iterator
interface List<T> : Collection<T>  // get, indexOf, subList (read-only)
interface MutableList<T> : List<T>  // add, remove, set (mutable)
interface Set<T> : Collection<T>   // no duplicates (read-only)
interface MutableSet<T> : Set<T>   // add, remove (mutable)
interface Map<K, V>                // keys, values, entries (read-only)
interface MutableMap<K, V> : Map<K, V>  // put, remove (mutable)

// A function that only reads doesn't need mutable methods:
fun printAll(items: List<T>) {  // depends on List, not MutableList
    items.forEach { println(it) }
    // No access to add(), remove() — correctly segregated
}
```

### The Wrong Way
```kotlin
// BAD: If Kotlin only had MutableList (no read-only List)
fun printAll(items: MutableList<T>) {  // forced to depend on mutable methods
    items.forEach { println(it) }
    // Caller must pass a MutableList even if they only want to read
    // printAll() could accidentally call items.clear() — no protection
}
```

---

## When ISP Is Over-Applied

```kotlin
// BAD: Over-segregated — one method per interface
interface UserNameGetter { fun getName(): String }
interface UserEmailGetter { fun getEmail(): String }
interface UserAgeGetter { fun getAge(): Int }

class User : UserNameGetter, UserEmailGetter, UserAgeGetter {
    override fun getName() = "Alice"
    override fun getEmail() = "alice@example.com"
    override fun getAge() = 30
}

// Now every consumer needs 3 interfaces:
fun greet(nameGetter: UserNameGetter, emailGetter: UserEmailGetter) {
    println("Hello, ${nameGetter.getName()}")
    println("Email: ${emailGetter.getEmail()}")
}
```

This is too granular. Group related methods into cohesive interfaces. `UserProfile` with `getName()`, `getEmail()`, `getAge()` is fine — they all change together (profile data).

**Rule of thumb**: Split when some implementers can't fulfill some methods. Don't split when all implementers use all methods.

---

## Key Insight

> **ISP is about respecting clients. A fat interface forces every client to depend on methods they don't use — creating unnecessary coupling. When a method on a fat interface changes, every client recompiles, even those that don't call it. The fix: split interfaces by client need. Each client depends on the smallest possible interface. This is why Kotlin has both `List` (read-only) and `MutableList` — a function that only reads shouldn't be forced to depend on mutation methods. The test: "Does this implementer use every method in the interface?" If not, split the interface.**
