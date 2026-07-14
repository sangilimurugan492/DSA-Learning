# OOP Scenarios

## Scenario 1: Designing a Payment System

### Problem
Design a payment system that supports multiple payment methods (credit card, PayPal, crypto). Each method has different validation and processing logic. New methods may be added later.

```java
// ❌ Bad — if-else chain, hard to extend
class PaymentProcessor {
    public void process(String type, double amount) {
        if (type.equals("CREDIT_CARD")) {
            validateCard();
            chargeCard(amount);
        } else if (type.equals("PAYPAL")) {
            validatePayPal();
            chargePayPal(amount);
        } else if (type.equals("CRYPTO")) {
            validateWallet();
            chargeWallet(amount);
        }
        // Adding new method = modifying this class (violates OCP)
    }
}
```

### Solution: Strategy Pattern with Factory

```java
// ✅ Good — Strategy pattern, open for extension
public interface PaymentStrategy {
    boolean validate();
    void process(double amount);
    String getName();
}

public class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    private final String cvv;

    public CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    @Override
    public boolean validate() {
        return cardNumber.length() == 16 && cvv.length() == 3;
    }

    @Override
    public void process(double amount) {
        System.out.println("Charging $" + amount + " to card " + maskCard());
    }

    @Override
    public String getName() { return "CREDIT_CARD"; }

    private String maskCard() {
        return "****-****-****-" + cardNumber.substring(12);
    }
}

public class PayPalPayment implements PaymentStrategy {
    private final String email;

    public PayPalPayment(String email) { this.email = email; }

    @Override
    public boolean validate() { return email.contains("@"); }

    @Override
    public void process(double amount) {
        System.out.println("Charging $" + amount + " via PayPal: " + email);
    }

    @Override
    public String getName() { return "PAYPAL"; }
}

// Context — uses strategy, doesn't know concrete type
public class PaymentProcessor {
    public void process(PaymentStrategy payment, double amount) {
        if (!payment.validate()) {
            throw new IllegalArgumentException("Invalid " + payment.getName());
        }
        payment.process(amount);
    }
}

// Usage
PaymentProcessor processor = new PaymentProcessor();
processor.process(new CreditCardPayment("1234567890123456", "123"), 99.99);
processor.process(new PayPalPayment("user@example.com"), 49.99);

// Adding crypto = new class, no modification to existing code
public class CryptoPayment implements PaymentStrategy {
    @Override
    public boolean validate() { return walletValid; }
    @Override
    public void process(double amount) { /* crypto logic */ }
    @Override
    public String getName() { return "CRYPTO"; }
}
```

### Key Takeaway
- Strategy pattern makes adding new payment methods trivial
- Each strategy is a separate class — no if-else chains
- Open/Closed Principle: extend without modifying existing code
- Easy to test — mock `PaymentStrategy` in tests

---

## Scenario 2: Designing a Notification System

### Problem
Send notifications via email, SMS, and push. A notification may go through multiple channels (e.g., email + push). The order and channels should be configurable.

```java
// ❌ Bad — tight coupling, hard to add channels
class NotificationService {
    void send(String type, String message) {
        if (type.equals("EMAIL")) sendEmail(message);
        if (type.equals("SMS")) sendSms(message);
        if (type.equals("PUSH")) sendPush(message);
    }
}
```

### Solution: Observer + Chain of Responsibility

```java
// ✅ Good — Channel interface, composable
public interface NotificationChannel {
    void send(String recipient, String message);
}

public class EmailChannel implements NotificationChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Email to " + recipient + ": " + message);
    }
}

public class SmsChannel implements NotificationChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("SMS to " + recipient + ": " + message);
    }
}

public class PushChannel implements NotificationChannel {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Push to " + recipient + ": " + message);
    }
}

// Notification service — composes channels
public class NotificationService {
    private final List<NotificationChannel> channels = new ArrayList<>();

    public void addChannel(NotificationChannel channel) {
        channels.add(channel);
    }

    public void notify(String recipient, String message) {
        for (NotificationChannel channel : channels) {
            try {
                channel.send(recipient, message);
            } catch (Exception e) {
                System.err.println("Failed: " + e.getMessage());
                // Continue to next channel — don't fail all
            }
        }
    }
}

// Usage
NotificationService service = new NotificationService();
service.addChannel(new EmailChannel());
service.addChannel(new PushChannel());
service.notify("user@example.com", "Welcome!");
// Email to user@example.com: Welcome!
// Push to user@example.com: Welcome!
```

### Key Takeaway
- Channels are independent and composable
- Adding a new channel = new class, no modification to service
- Failure in one channel doesn't block others
- Channels can be added/removed at runtime

---

## Scenario 3: Designing a File Parser System

### Problem
Parse different file formats (CSV, JSON, XML). Each parser has common steps (open, read, parse, close) but different parsing logic.

```java
// ❌ Bad — duplicated code across parsers
class CsvParser {
    public List<Map<String, String>> parse(String path) {
        openFile(path);
        // CSV-specific parsing
        closeFile();
        return results;
    }
}
class JsonParser {
    public List<Map<String, String>> parse(String path) {
        openFile(path);  // Duplicated
        // JSON-specific parsing
        closeFile(path);  // Duplicated
    }
}
```

### Solution: Template Method Pattern

```java
// ✅ Good — Template Method defines skeleton, subclasses implement steps
public abstract class FileParser {
    // Template method — final, defines algorithm
    public final List<Map<String, String>> parse(String path) {
        try (BufferedReader reader = openFile(path)) {
            String content = readContent(reader);
            List<Map<String, String>> data = parseContent(content);
            validate(data);
            return data;
        } catch (IOException e) {
            throw new ParseException("Failed to parse: " + path, e);
        }
    }

    // Common steps — implemented in base class
    private BufferedReader openFile(String path) throws IOException {
        return new BufferedReader(new FileReader(path));
    }

    private String readContent(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line).append("\n");
        return sb.toString();
    }

    private void validate(List<Map<String, String>> data) {
        if (data == null || data.isEmpty()) {
            throw new ParseException("No data found");
        }
    }

    // Abstract steps — implemented by subclasses
    protected abstract List<Map<String, String>> parseContent(String content);
}

public class CsvParser extends FileParser {
    @Override
    protected List<Map<String, String>> parseContent(String content) {
        List<Map<String, String>> result = new ArrayList<>();
        String[] lines = content.split("\n");
        String[] headers = lines[0].split(",");
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            Map<String, String> row = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                row.put(headers[j].trim(), values[j].trim());
            }
            result.add(row);
        }
        return result;
    }
}

public class JsonParser extends FileParser {
    @Override
    protected List<Map<String, String>> parseContent(String content) {
        // JSON parsing logic
        return new ArrayList<>();
    }
}

// Usage
FileParser parser = new CsvParser();
List<Map<String, String>> data = parser.parse("data.csv");
```

### Key Takeaway
- Template Method defines the algorithm skeleton in base class
- Common steps (open, read, validate) are shared — no duplication
- Subclasses implement only the varying step (parseContent)
- `final` on template method prevents subclasses from changing the flow

---

## Scenario 4: Designing a Configuration Manager

### Problem
A singleton configuration manager that loads settings from a file, supports different environments (dev, prod), and is thread-safe.

```java
// ❌ Bad — not thread-safe, no environment support
class ConfigManager {
    private static ConfigManager instance;
    private Map<String, String> settings;

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();  // ❌ Race condition
        }
        return instance;
    }
}
```

### Solution: Thread-safe Singleton with Builder

```java
// ✅ Good — Bill Pugh singleton + Builder for configuration
public class ConfigManager {
    private final Map<String, String> settings;
    private final String environment;

    private ConfigManager(Builder builder) {
        this.environment = builder.environment;
        this.settings = Collections.unmodifiableMap(builder.settings);
    }

    // Bill Pugh singleton
    private static class Holder {
        private static ConfigManager INSTANCE;
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    public static void initialize(Builder builder) {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
        Holder.INSTANCE = builder.build();
    }

    public String get(String key) {
        return settings.getOrDefault(key, System.getProperty(key));
    }

    public String getEnvironment() { return environment; }

    // Builder
    public static class Builder {
        private String environment = "dev";
        private final Map<String, String> settings = new HashMap<>();

        public Builder environment(String env) {
            this.environment = env;
            return this;
        }

        public Builder set(String key, String value) {
            settings.put(key, value);
            return this;
        }

        public Builder loadFromFile(String path) {
            try (var props = new java.util.Properties()) {
                props.load(new java.io.FileInputStream(path));
                props.forEach((k, v) -> settings.put(k.toString(), v.toString()));
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
            return this;
        }

        public ConfigManager build() {
            return new ConfigManager(this);
        }
    }
}

// Usage
ConfigManager.initialize(new ConfigManager.Builder()
    .environment("prod")
    .set("timeout", "5000")
    .loadFromFile("/etc/app/config.properties")
);

String timeout = ConfigManager.getInstance().get("timeout");
```

### Key Takeaway
- Bill Pugh singleton is thread-safe without synchronization
- Builder pattern handles complex initialization
- `Collections.unmodifiableMap` makes settings immutable
- `initialize()` prevents re-initialization (fail-fast)
- Environment-specific defaults can be loaded from files

---

## Scenario 5: Designing an Event System

### Problem
Components need to communicate without direct dependencies. A button click should notify multiple listeners (logger, analytics, UI updater) without the button knowing about them.

```java
// ❌ Bad — button knows about all listeners directly
class Button {
    private Logger logger;
    private Analytics analytics;
    private UIUpdater updater;

    void onClick() {
        logger.log("clicked");
        analytics.track("button_click");
        updater.update();
    }
}
```

### Solution: Observer Pattern with Event Bus

```java
// ✅ Good — Event Bus decouples publishers from subscribers
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                  .add(handler);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<Consumer<?>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            for (Consumer<?> handler : handlers) {
                ((Consumer<T>) handler).accept(event);
            }
        }
    }
}

// Event types
public record ButtonClickEvent(String buttonId, long timestamp) {}
public record DataLoadedEvent(List<String> data) {}

// Subscribers — don't know about each other
public class Logger {
    public Logger(EventBus bus) {
        bus.subscribe(ButtonClickEvent.class, e ->
            System.out.println("Log: " + e.buttonId() + " at " + e.timestamp()));
    }
}

public class Analytics {
    public Analytics(EventBus bus) {
        bus.subscribe(ButtonClickEvent.class, e ->
            System.out.println("Track: " + e.buttonId()));
        bus.subscribe(DataLoadedEvent.class, e ->
            System.out.println("Track: loaded " + e.data().size() + " items"));
    }
}

// Publisher — doesn't know subscribers
public class Button {
    private final EventBus bus;
    private final String id;

    public Button(EventBus bus, String id) {
        this.bus = bus;
        this.id = id;
    }

    public void click() {
        bus.publish(new ButtonClickEvent(id, System.currentTimeMillis()));
    }
}

// Usage
EventBus bus = new EventBus();
new Logger(bus);
new Analytics(bus);
Button button = new Button(bus, "submit");
button.click();
// Log: submit at 1234567890
// Track: submit
```

### Key Takeaway
- Event Bus decouples publishers from subscribers completely
- Publishers don't know who listens — add/remove subscribers freely
- `ConcurrentHashMap` + `CopyOnWriteArrayList` for thread safety
- `record` types make events immutable and concise
- Easy to test — subscribe a mock handler, publish event, verify

---

## 🔗 Related Topics
- [OOP Concepts](../intermediate/OOPConcepts.md)
- [Design Patterns](../advanced/DesignPatterns.md)
- [OOP Basics](../beginner/OOPBasics.md)
