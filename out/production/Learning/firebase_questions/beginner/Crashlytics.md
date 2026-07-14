# Firebase Crashlytics

## Q1: What is Crashlytics?

Lightweight, real-time crash reporter that helps track, prioritize, and fix stability issues.

```kotlin
// Initialize (automatic with Firebase BoM)
// No code needed — just add the dependency

// Force a test crash to verify setup
val crashButton = Button(context)
crashButton.setOnClickListener {
    throw RuntimeException("Test Crash")
}
```

### Key features
- **Real-time crash reporting** — crashes appear in dashboard within minutes
- **Stack traces** — full stack trace with device info
- **Custom keys** — add context to crashes
- **Custom logs** — leave breadcrumbs leading to crash
- **Non-fatal errors** — track caught exceptions
- **User identifiers** — track which users are affected

---

## Q2: How do you add custom keys and logs?

```kotlin
// Set custom keys — appear in crash report
FirebaseCrashlytics.getInstance().setCustomKey("user_id", userId)
FirebaseCrashlytics.getInstance().setCustomKey("subscription", "premium")
FirebaseCrashlytics.getInstance().setCustomKey("cart_items", 3)

// Set user identifier
FirebaseCrashlytics.getInstance().setUserId(userId)

// Custom logs — breadcrumbs leading to crash
FirebaseCrashlytics.getInstance().log("User opened checkout screen")
FirebaseCrashlytics.getInstance().log("Cart total: $${cart.total}")
FirebaseCrashlytics.getInstance().log("Payment method: ${paymentMethod}")

// These logs appear in the crash report in order
```

---

## Q3: How do you log non-fatal exceptions?

```kotlin
// Log caught exceptions (non-fatal)
try {
    val result = api.fetchData()
} catch (e: IOException) {
    FirebaseCrashlytics.getInstance().recordException(e)
    // Shows in dashboard as non-fatal
}

// With custom message
try {
    parseJson(json)
} catch (e: JSONException) {
    FirebaseCrashlytics.getInstance().apply {
        log("Failed to parse JSON: $json")
        setCustomKey("json_length", json.length)
        recordException(e)
    }
}
```

### Fatal vs Non-fatal
| Type | Description | User Impact |
|------|-------------|-------------|
| Fatal | App crashes | App closes |
| Non-fatal | Caught exception | App continues |

---

## Q4: How do you enable/disable Crashlytics?

```kotlin
// Disable in debug builds
FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

// Or via manifest
// <meta-data android:name="firebase_crashlytics_collection_enabled" android:value="false" />

// Enable at runtime (after user consent)
FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true

// Check if collection is enabled
val isEnabled = FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled
```

---

## Q5: How do you use Crashlytics with Coroutines?

```kotlin
// Coroutine exception handler
val crashlyticsExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    FirebaseCrashlytics.getInstance().apply {
        log("Coroutine failed: ${throwable.message}")
        setCustomKey("coroutine_context", "viewModelScope")
        recordException(throwable)
    }
}

viewModelScope.launch(crashlyticsExceptionHandler) {
    val data = repository.fetchData()
    updateUi(data)
}

// In ViewModel
class MyViewModel : ViewModel() {
    private val handler = CoroutineExceptionHandler { _, throwable ->
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    fun loadData() {
        viewModelScope.launch(handler) {
            try {
                val data = repo.fetch()
                _state.value = State.Success(data)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().log("loadData failed")
                FirebaseCrashlytics.getInstance().recordException(e)
                _state.value = State.Error(e.message)
            }
        }
    }
}
```

---

## Q6: How do you read crash reports in the dashboard?

### Dashboard sections
- **Crash Insights** — AI-powered insights and patterns
- **Crash-free users** — % of users who didn't crash
- **Crash-free sessions** — % of sessions without crashes
- **Top crashes** — Most impactful crashes
- **Devices** — Breakdown by device model, OS, etc.
- **App versions** — Which versions have the most crashes

### Key metrics
| Metric | Description | Target |
|--------|-------------|--------|
| Crash-free users | % users without crash | > 99.9% |
| Crash-free sessions | % sessions without crash | > 99.9% |
| Top crash | Most frequent crash | Track trend |
| Affected users | Users impacted by a crash | Minimize |

---

## Q7: How do you integrate Crashlytics with Analytics?

```kotlin
// Crashlytics automatically logs Analytics events as breadcrumbs
// No extra code needed — just ensure both are initialized

// Custom event before crash
Firebase.analytics.logEvent("checkout_started", bundleOf(
    "cart_total" to 50.0,
    "payment_method" to "card"
))
// If crash happens → this event appears in crash report

// Crashlytics → Analytics integration
// Go to Firebase Console → Crashlytics → Settings → Integration
// Enable "Google Analytics" integration
```

### What appears in crash report
- **Custom keys** — set via `setCustomKey()`
- **Custom logs** — set via `log()`
- **Analytics events** — last 6 events before crash
- **Device info** — model, OS, orientation, RAM
- **App info** — version, build, installation UUID
- **Stack trace** — full stack trace

---

## 🔗 Related Topics
- [Analytics](../intermediate/Analytics.md)
- [Cloud Functions](../intermediate/CloudFunctions.md)
- [Performance](../advanced/Performance.md)
