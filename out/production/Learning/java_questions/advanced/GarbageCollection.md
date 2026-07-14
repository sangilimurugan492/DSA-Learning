# Garbage Collection

## Q1: How does garbage collection work in Java?

```
Heap Layout (Generational)
┌─────────────────────────────────────────┐
│              Young Generation           │
│  ┌──────────┐  ┌────┐  ┌────┐          │
│  │   Eden   │  │ S0 │  │ S1 │          │
│  └──────────┘  └────┘  └────┘          │
├─────────────────────────────────────────┤
│           Old Generation (Tenured)       │
│                                         │
├─────────────────────────────────────────┤
│              Metaspace (Java 8+)        │
└─────────────────────────────────────────┘
```

### GC Process
```
1. New objects → Eden
2. Eden full → Minor GC
   - Live objects → Survivor 0 (S0)
   - Eden cleared
3. Eden full again → Minor GC
   - Eden live + S0 live → Survivor 1 (S1)
   - Age incremented
4. Objects surviving N GCs (default 15) → Old Generation
5. Old Generation full → Major GC (Full GC)
```

```java
// Object lifecycle
public class GCDemo {
    public void method() {
        // Object created in Eden
        String s1 = new String("hello");

        // s1 is reachable — not eligible for GC

        s1 = null;  // Now eligible for GC (no references)

        // Object still referenced — not eligible
        String s2 = new String("world");
        List<String> list = new ArrayList<>();
        list.add(s2);  // list holds reference — s2 not eligible
    }
    // After method returns: s1, s2, list all eligible (if no external refs)
}
```

---

## Q2: What are GC roots?

```
GC Roots (starting points for reachability):
1. Local variables in active stack frames
2. Active Java threads
3. Static fields of loaded classes
4. JNI references (native code)
5. Synchronization monitors (locked objects)
6. System class loader

Mark-and-Sweep:
1. Mark: Start from GC roots, traverse object graph, mark reachable
2. Sweep: Delete unmarked objects
3. Compact (optional): Move surviving objects together (reduce fragmentation)
```

```java
public class GCRootsExample {
    private static List<String> cache = new ArrayList<>();  // GC root (static)

    public void process() {
        String local = "temp";  // GC root (local variable)

        cache.add(local);  // local is reachable via cache (static)
        // Even after method ends, "temp" survives — cache holds it
    }
}
```

---

## Q3: What are the types of garbage collectors?

| Collector | Algorithm | Pause Time | Throughput | Use Case |
|-----------|-----------|------------|------------|----------|
| **Serial GC** | Single-threaded mark-sweep | Long | Low | Small apps, single CPU |
| **Parallel GC** | Multi-threaded mark-sweep | Medium | High | Batch processing |
| **CMS (Concurrent Mark Sweep)** | Concurrent mark + sweep | Short | Medium | Low latency (deprecated Java 9, removed Java 14) |
| **G1 GC** | Region-based, concurrent | Predictable | High | Server apps (default Java 9+) |
| **ZGC** | Concurrent, region-based | <1ms | High | Ultra-low latency (Java 15+) |
| **Shenandoah** | Concurrent, compacting | <10ms | High | Low latency (OpenJDK) |

```bash
# Select GC
-XX:+UseSerialGC        # Serial
-XX:+UseParallelGC      # Parallel (throughput)
-XX:+UseG1GC            # G1 (default Java 9+)
-XX:+UseZGC             # ZGC (low latency)
-XX:+UseShenandoahGC    # Shenandoah
```

### G1 GC Internals
```
Heap divided into regions (1MB-32MB each)
- Eden regions, Survivor regions, Old regions, Humongous regions
- GC picks regions with most garbage first (Garbage First)
- Predictable pause: -XX:MaxGCPauseMillis=200
- Concurrent marking (doesn't stop app)
- Evacuation pause: copy live objects to empty regions
```

---

## Q4: How do you request garbage collection?

```java
// Suggest GC (not guaranteed)
System.gc();  // JVM may or may not run GC
Runtime.getRuntime().gc();  // Same thing

// Better: let JVM decide
// Don't call System.gc() in production — it causes full GC pauses

// Check memory usage
Runtime rt = Runtime.getRuntime();
long total = rt.totalMemory();   // Total heap allocated
long free = rt.freeMemory();     // Free memory
long used = total - free;        // Used memory
long max = rt.maxMemory();        // Maximum heap (-Xmx)

// Memory MXBean
MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
System.out.println("Used: " + heapUsage.getUsed());
System.out.println("Max: " + heapUsage.getMax());
```

---

## Q5: What are common memory leaks in Java?

```java
// 1. Static collection holding objects forever
class Cache {
    private static final Map<String, byte[]> cache = new HashMap<>();

    public void addToCache(String key, byte[] data) {
        cache.put(key, data);  // ❌ Never removed — leak
    }
}
// Fix: Use WeakHashMap or LRU cache with eviction

// 2. Unclosed resources
class FileProcessor {
    public void process() {
        try {
            FileInputStream fis = new FileInputStream("data.txt");
            // ❌ If exception before close, fis leaks
            fis.read();
            fis.close();
        } catch (IOException e) { }
    }
}
// Fix: try-with-resources

// 3. ThreadLocal not removed
class RequestContext {
    private static final ThreadLocal<User> userHolder = new ThreadLocal<>();

    public void handle(User user) {
        userHolder.set(user);
        process();
        // ❌ userHolder.remove() not called
        // In thread pool, user persists across requests
    }
}
// Fix: Always remove() in finally

// 4. Listener/callback not unregistered
class EventBus {
    private List<Listener> listeners = new ArrayList<>();

    public void register(Listener l) { listeners.add(l); }
    // ❌ No unregister method — listeners accumulate
}
// Fix: Provide unregister method, use WeakReference

// 5. Inner class holding outer reference
class Outer {
    private byte[] bigData = new byte[1024 * 1024];

    class Inner {  // Non-static inner class holds Outer.this
        // Even if Inner is used, Outer can't be GC'd
    }
}
// Fix: Make inner class static
```

### Detecting Memory Leaks
```bash
# 1. Heap dump on OOM
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heapdump.hprof

# 2. Analyze with tools
#    - VisualVM (free, bundled with JDK)
#    - Eclipse MAT (Memory Analyzer Tool)
#    - JProfiler (commercial)
#    - IntelliJ Profiler

# 3. JFR (Java Flight Recorder)
#    java -XX:StartFlightRecording=duration=60s,filename=recording.jfr MyApp

# 4. JConsole / JMX monitoring
#    jconsole  # Connect to running JVM
```

---

## Q6: What are WeakReference, SoftReference, and PhantomReference?

```java
// Strong reference — never GC'd
String strong = new String("hello");  // Strong — not eligible

// WeakReference — GC'd on next cycle (if no strong refs)
WeakReference<String> weak = new WeakReference<>(new String("weak"));
// GC may collect at any time
weak.get();  // May return null after GC

// SoftReference — GC'd only when memory is low
SoftReference<byte[]> soft = new SoftReference<>(new byte[1024 * 1024]);
// Survives GC until JVM needs memory
soft.get();  // Returns data until OOM is near

// PhantomReference — enqueued after finalization, get() always null
PhantomReference<String> phantom = new PhantomReference<>(new String("phantom"), new ReferenceQueue<>());
phantom.get();  // Always returns null — used for cleanup tracking
```

| Type | GC Behavior | Use Case |
|------|------------|----------|
| Strong | Never collected | Normal references |
| Soft | Collected on memory pressure | Memory-sensitive cache |
| Weak | Collected on next GC | WeakHashMap, listener tracking |
| Phantom | Collected, enqueued | Pre-mortem cleanup actions |

```java
// Practical: WeakHashMap — keys are weak references
Map<Object, String> metadata = new WeakHashMap<>();
Object key = new Object();
metadata.put(key, "data");
key = null;  // Key eligible for GC
// On next GC, entry removed from map automatically

// Practical: SoftReference cache
Map<String, SoftReference<Image>> imageCache = new HashMap<>();
public Image getImage(String path) {
    SoftReference<Image> ref = imageCache.get(path);
    if (ref != null) {
        Image img = ref.get();
        if (img != null) return img;  // Cache hit
    }
    Image img = loadImage(path);
    imageCache.put(path, new SoftReference<>(img));
    return img;
}
```

---

## 🔗 Related Topics
- [JVM Internals](JVMInternals.md)
- [Concurrency Advanced](ConcurrencyAdvanced.md)
- [Debugging Scenarios](../scenario_based/DebuggingScenarios.md)
