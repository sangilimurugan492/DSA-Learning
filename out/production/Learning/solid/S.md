# S — Single Responsibility Principle (SRP)

> **A class should have one, and only one, reason to change.**

---

## What It Really Means

"Reason to change" = **one actor (stakeholder) who would request a change**. If a marketing person and a DBA would both ask you to change the same class, that class has two responsibilities.

SRP is NOT about "one method per class" or "one function per class." It's about **one axis of change**. A `UserRepository` with `save`, `findById`, `delete` is fine — all three change for the same reason (persistence). That's one responsibility.

---

## The Problem: God Object

```kotlin
// BAD: This class has 4 reasons to change
class Employee {
    val name: String
    val salary: Double

    // Reason 1: HR changes how salary is calculated
    fun calculatePay(): Paycheck { ... }

    // Reason 2: IT changes the report format
    fun generateReport(): Report { ... }

    // Reason 3: DBA changes the schema
    fun saveToDatabase() { ... }

    // Reason 4: Security changes the auth rules
    fun authenticate(password: String): Boolean { ... }
}
```

If HR changes the pay formula, you modify `Employee`. If the DBA changes the schema, you modify `Employee`. If security changes auth, you modify `Employee`. **Every change risks breaking unrelated functionality.**

### The Blast Radius
```
HR changes pay formula → touches Employee class → accidentally breaks report generation
DBA changes schema → touches Employee class → accidentally breaks authentication
```

---

## The Solution: Separate by Actor

```kotlin
// Each class changes for one actor only

// Changes when HR changes pay rules
class PayCalculator {
    fun calculatePay(employee: Employee): Paycheck {
        return Paycheck(employee.salary * 1.2)  // simplified
    }
}

// Changes when IT changes report format
class EmployeeReporter {
    fun generateReport(employee: Employee): Report {
        return Report("Name: ${employee.name}, Salary: ${employee.salary}")
    }
}

// Changes when DBA changes schema
class EmployeeRepository(private val db: Database) {
    fun save(employee: Employee) { db.insert(employee) }
    fun findById(id: String): Employee? = db.query(id)
}

// Changes when security changes auth
class EmployeeAuthenticator {
    fun authenticate(employee: Employee, password: String): Boolean {
        return hash(password) == employee.passwordHash
    }
}

// The Employee class itself: just data
data class Employee(val name: String, val salary: Double, val passwordHash: String)
```

Now:
- HR changes pay → modify `PayCalculator` only.
- DBA changes schema → modify `EmployeeRepository` only.
- **No blast radius.**

---

## How to Detect SRP Violations

### Smell 1: "Manager" / "Helper" / "Util" in the Name
```
UserManager, DataHelper, StringUtils, ServiceHelper
```
These names are too vague — they hide multiple responsibilities. If you can't name a class specifically, it's doing too much.

### Smell 2: Class Has Many Dependencies
```kotlin
class OrderService(
    private val db: Database,
    private val emailSender: EmailSender,
    private val paymentGateway: PaymentGateway,
    private val inventorySystem: InventorySystem,
    private val analyticsTracker: AnalyticsTracker,
    private val pdfGenerator: PdfGenerator,
    private val smsSender: SmsSender
)
```
7 dependencies = 7 reasons to change. This class orchestrates too much. Extract: `PaymentProcessor`, `NotificationService`, `InvoiceGenerator`.

### Smell 3: Class Is Too Long
- A class with 500+ lines is likely doing too much.
- Rule of thumb: if you can't describe what a class does in one sentence without "and," it has too many responsibilities.

### Smell 4: Unrelated Tests Fail Together
- If changing the email template breaks the payment test, the class has mixed responsibilities.

### Smell 5: "I just need to change this one thing..."
- If you're afraid to modify a class because "it might break something else," SRP is violated.

---

## Real-World Example: Refactoring a Fat Controller

### Before (Bad)
```kotlin
class UserController {
    fun createUser(req: CreateUserRequest): User {
        // 1. Validate
        if (req.email.isBlank()) throw ValidationException("Email required")
        if (!req.email.contains("@")) throw ValidationException("Invalid email")

        // 2. Check if user exists
        val existing = db.query("SELECT * FROM users WHERE email = ?", req.email)
        if (existing != null) throw ConflictException("User exists")

        // 3. Hash password
        val hashedPassword = BCrypt.hashpw(req.password, BCrypt.gensalt())

        // 4. Create user
        val user = User(UUID.randomUUID().toString(), req.name, req.email, hashedPassword)
        db.insert(user)

        // 5. Send welcome email
        val emailBody = "Welcome ${user.name}!"
        smtpClient.send(user.email, "Welcome", emailBody)

        // 6. Track analytics
        analyticsClient.track("user_created", mapOf("userId" to user.id))

        // 7. Generate response
        return UserResponse(user.id, user.name, user.email)
    }
}
```
This controller has **7 responsibilities**: validation, existence check, password hashing, user creation, email sending, analytics tracking, response mapping.

### After (Good)
```kotlin
// Controller: only handles HTTP concerns
class UserController(
    private val userService: UserService,
    private val validator: UserValidator,
    private val mapper: UserMapper
) {
    fun createUser(req: CreateUserRequest): UserResponse {
        validator.validate(req)
        val user = userService.createUser(req.name, req.email, req.password)
        return mapper.toResponse(user)
    }
}

// Validator: only validates
class UserValidator {
    fun validate(req: CreateUserRequest) {
        require(req.email.isNotBlank()) { "Email required" }
        require(req.email.contains("@")) { "Invalid email" }
        require(req.password.length >= 8) { "Password too short" }
    }
}

// Service: orchestrates business logic
class UserService(
    private val repo: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val notifier: UserNotifier,
    private val analytics: AnalyticsTracker
) {
    fun createUser(name: String, email: String, password: String): User {
        repo.findByEmail(email)?.let { throw ConflictException("User exists") }

        val hashedPassword = passwordHasher.hash(password)
        val user = User(UUID.randomUUID().toString(), name, email, hashedPassword)
        repo.save(user)

        notifier.sendWelcomeEmail(user)
        analytics.track("user_created", mapOf("userId" to user.id))

        return user
    }
}

// Each collaborator has one responsibility:
class PasswordHasher {
    fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())
}

class UserNotifier(private val emailSender: EmailSender) {
    fun sendWelcomeEmail(user: User) {
        emailSender.send(user.email, "Welcome", "Welcome ${user.name}!")
    }
}

class UserRepository(private val db: Database) {
    fun save(user: User) = db.insert(user)
    fun findByEmail(email: String): User? = db.query("SELECT * FROM users WHERE email = ?", email)
}

class UserMapper {
    fun toResponse(user: User) = UserResponse(user.id, user.name, user.email)
}
```

Now each class changes for one reason:
- Email template changes → `UserNotifier`
- Password hashing algorithm changes → `PasswordHasher`
- DB schema changes → `UserRepository`
- Response format changes → `UserMapper`
- Validation rules change → `UserValidator`

---

## Common Misconceptions

### "SRP means one method per class"
**False.** A `PdfExporter` with `exportChart`, `exportTable`, `exportText` is fine — all change for the same reason (PDF export format).

### "SRP means small classes"
**False.** A class can be 300 lines and still have one responsibility. A class can be 30 lines and have three responsibilities.

### "SRP is about functional cohesion"
**Partially true.** SRP is about **change cohesion** — methods that change together should be together. Methods that change for different reasons should be separated.

---

## When SRP Is Over-Applied

```kotlin
// BAD: Over-applied SRP — too many tiny classes
class NameValidator { fun validate(name: String) { require(name.isNotBlank()) } }
class EmailValidator { fun validate(email: String) { require(email.contains("@")) } }
class PasswordValidator { fun validate(pw: String) { require(pw.length >= 8) } }
class AgeValidator { fun validate(age: Int) { require(age >= 0) } }

// Now you need a coordinator to call all validators:
class ValidationCoordinator(
    private val nameValidator: NameValidator,
    private val emailValidator: EmailValidator,
    private val passwordValidator: PasswordValidator,
    private val ageValidator: AgeValidator
) {
    fun validate(req: CreateUserRequest) {
        nameValidator.validate(req.name)
        emailValidator.validate(req.email)
        passwordValidator.validate(req.password)
        ageValidator.validate(req.age)
    }
}
```

This is over-engineered. A single `UserValidator` with all validation methods is simpler and still has one responsibility (validation).

```kotlin
// GOOD: One validator, one responsibility
class UserValidator {
    fun validate(req: CreateUserRequest) {
        validateName(req.name)
        validateEmail(req.email)
        validatePassword(req.password)
        validateAge(req.age)
    }
}
```

---

## Key Insight

> **SRP is the foundation of all other SOLID principles. If a class does one thing, it's easy to extend (OCP), easy to substitute (LSP), easy to interface (ISP), and easy to invert (DIP). If a class does everything, all other principles break. The test is simple: "If I change this class, what else might break?" If the answer is "unrelated things," SRP is violated.**
