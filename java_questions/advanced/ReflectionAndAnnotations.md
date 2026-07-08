# Reflection and Annotations

## Q1: What is reflection and how do you use it?

```java
// Reflection — inspect and modify classes at runtime
Class<?> clazz = String.class;

// Get class info
clazz.getName();           // "java.lang.String"
clazz.getSimpleName();    // "String"
clazz.getModifiers();     // Modifier.PUBLIC | Modifier.FINAL

// Get fields
Field[] fields = clazz.getDeclaredFields();
for (Field f : fields) {
    System.out.println(f.getName() + ": " + f.getType());
}

// Get methods
Method[] methods = clazz.getDeclaredMethods();
for (Method m : methods) {
    System.out.println(m.getName() + " - " + m.getReturnType());
}

// Get constructors
Constructor<?>[] constructors = clazz.getConstructors();
```

### Creating objects and invoking methods via reflection
```java
// Create instance
Class<?> clazz = Class.forName("com.example.User");
Constructor<?> constructor = clazz.getConstructor(String.class, int.class);
Object user = constructor.newInstance("Alice", 30);

// Invoke method
Method method = clazz.getMethod("getName");
String name = (String) method.invoke(user);  // "Alice"

// Access private field
Field privateField = clazz.getDeclaredField("secret");
privateField.setAccessible(true);  // Bypass access check
String secret = (String) privateField.get(user);

// Modify private field
privateField.set(user, "new secret");
```

---

## Q2: What are the use cases and risks of reflection?

### Use Cases
```java
// 1. Frameworks (Spring, Hibernate, Jackson)
//    Spring: @Autowired → reflection to inject dependencies
//    Jackson: @JsonProperty → reflection to serialize/deserialize
//    Hibernate: @Entity → reflection to map objects to tables

// 2. Testing (JUnit)
//    @Test → reflection to find and run test methods

// 3. Dependency injection
//    @Inject → reflection to find fields and inject

// 4. Code generation / annotation processing
//    Lombok, MapStruct → compile-time annotation processing

// 5. Dynamic proxies
MyInterface proxy = (MyInterface) Proxy.newProxyInstance(
    loader,
    new Class[]{MyInterface.class},
    (proxyObj, method, args) -> {
        System.out.println("Before: " + method.getName());
        Object result = method.invoke(realObject, args);
        System.out.println("After: " + method.getName());
        return result;
    }
);
```

### Risks
| Risk | Description |
|------|-------------|
| Performance | Reflection is 10-100x slower than direct calls |
| Type safety | No compile-time checks — runtime errors |
| Security | Can bypass access control (private fields) |
| Maintenance | Refactoring breaks reflection code silently |
| Encapsulation | Breaks encapsulation — accesses private members |

```java
// ✅ Prefer alternatives when possible
// Instead of reflection:
// - Use interfaces and polymorphism
// - Use method references
// - Use annotation processors (compile-time)
// - Use MethodHandle (faster than reflection, Java 7+)
```

---

## Q3: What are annotations and how to create custom ones?

```java
// Built-in annotations
@Override       // Method overrides parent
@Deprecated     // Marked as deprecated
@SuppressWarnings("unchecked")  // Suppress warnings
@FunctionalInterface  // Single abstract method

// Custom annotation definition
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime
@Target(ElementType.METHOD)          // Only on methods
public @interface MyAnnotation {
    String value() default "";
    int priority() default 0;
    String[] tags() default {};
}

// Using the annotation
class MyClass {
    @MyAnnotation(value = "process", priority = 1, tags = {"api", "v2"})
    public void process() {
        System.out.println("Processing");
    }
}
```

### Meta-Annotations
| Meta-Annotation | Purpose |
|----------------|---------|
| `@Retention` | When annotation is available: SOURCE, CLASS, RUNTIME |
| `@Target` | Where it can be applied: TYPE, METHOD, FIELD, PARAMETER, etc. |
| `@Inherited` | Subclasses inherit the annotation |
| `@Documented` | Appears in Javadoc |
| `@Repeatable` | Can be applied multiple times |

```java
// Retention policies
@Retention(RetentionPolicy.SOURCE)   // Discarded by compiler (e.g., @Override)
@Retention(RetentionPolicy.CLASS)    // In bytecode, not at runtime (default)
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime via reflection

// Repeatable annotation
@Repeatable(Schedules.class)
@interface Schedule {
    String day();
    String time();
}

@interface Schedules {
    Schedule[] value();
}

class Task {
    @Schedule(day = "MON", time = "09:00")
    @Schedule(day = "WED", time = "14:00")
    public void run() {}
}
```

---

## Q4: How do you process annotations at runtime?

```java
// Custom annotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonField {
    String name();
}

// Class using annotation
public class User {
    @JsonField(name = "user_name")
    private String username;

    @JsonField(name = "user_age")
    private int age;

    public User(String username, int age) {
        this.username = username;
        this.age = age;
    }
}

// Annotation processor — serialize to JSON
public class JsonSerializer {
    public static String serialize(Object obj) throws IllegalAccessException {
        StringBuilder json = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(JsonField.class)) {
                fields[i].setAccessible(true);
                JsonField annotation = fields[i].getAnnotation(JsonField.class);
                String key = annotation.name();
                Object value = fields[i].get(obj);

                json.append(String.format("\"%s\":\"%s\"", key, value));
                if (i < fields.length - 1) json.append(", ");
            }
        }
        json.append("}");
        return json.toString();
    }
}

// Usage
User user = new User("alice", 30);
String json = JsonSerializer.serialize(user);
// {"user_name":"alice", "user_age":"30"}
```

---

## Q5: What is the difference between reflection and annotation processing?

| Reflection | Annotation Processing |
|-----------|----------------------|
| Runtime inspection | Compile-time processing |
| Slower (runtime overhead) | No runtime cost |
| Can access private members | Generates new code |
| `java.lang.reflect` | `javax.annotation.processing` |
| Jackson, Spring (runtime) | Lombok, MapStruct (compile-time) |

```java
// Annotation Processor (compile-time)
@SupportedAnnotationTypes("com.example.JsonField")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class JsonProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(JsonField.class)) {
            // Generate code at compile time
            // No runtime reflection needed
        }
        return true;
    }
}
```

---

## 🔗 Related Topics
- [OOP Concepts](../intermediate/OOPConcepts.md)
- [Design Patterns](DesignPatterns.md)
- [Android with Java](AndroidWithJava.md)
