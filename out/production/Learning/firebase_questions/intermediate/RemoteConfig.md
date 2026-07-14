# Firebase Remote Config

## Q1: What is Remote Config?

Cloud service that lets you change app behavior and appearance without publishing an app update.

### Use cases
- Feature flags (enable/disable features remotely)
- A/B testing
- Phased rollouts
- Dynamic content (banners, promotions)
- Emergency kill switches

---

## Q2: How do you set up Remote Config?

```kotlin
// Initialize
val remoteConfig = Firebase.remoteConfig

// Set defaults (used before fetch)
val defaults = mapOf(
    "feature_new_ui" to false,
    "max_cart_items" to 10,
    "banner_text" to "Welcome!",
    "discount_percentage" to 0.0
)
remoteConfig.setDefaultsAsync(defaults)

// Configure fetch settings
val settings = remoteConfigSettings {
    minimumFetchIntervalInSeconds = 3600  // 1 hour (12 hours default)
}
remoteConfig.setConfigSettingsAsync(settings)
```

---

## Q3: How do you fetch and activate config?

```kotlin
// Fetch + activate
remoteConfig.fetchAndActivate()
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val activated = task.result
            if (activated) {
                // Config was updated — apply new values
                applyConfig()
            }
        }
    }

// Fetch only (activate later)
remoteConfig.fetch().addOnCompleteListener { task ->
    if (task.isSuccessful) {
        remoteConfig.activate().addOnCompleteListener {
            applyConfig()
        }
    }
}

// Read values
val isNewUiEnabled = remoteConfig.getBoolean("feature_new_ui")
val maxCartItems = remoteConfig.getLong("max_cart_items")
val bannerText = remoteConfig.getString("banner_text")
val discount = remoteConfig.getDouble("discount_percentage")
```

---

## Q4: How do you use conditions?

Conditions are set in the Firebase Console. They allow different values for different users.

### Condition examples
| Condition | Criteria |
|-----------|----------|
| iOS users | Platform = iOS |
| Premium users | Audience = "Premium users" |
| Version 3+ | App version >= 3.0 |
| Country | Country = US, UK, CA |
| Random % | User in random percentile 0-50% |

### Priority
1. Conditions are evaluated top-to-bottom
2. First matching condition wins
3. Default value if no condition matches

### Example setup
```
Parameter: "feature_new_ui"
  Condition 1: "Premium users" → true
  Condition 2: "Random 50%" → true
  Default: false
```

---

## Q5: How do you do A/B testing?

```kotlin
// 1. Create experiment in Firebase Console
// 2. Define variants (A: control, B: new_feature)
// 3. Set target metrics (retention, revenue, engagement)
// 4. Remote Config handles variant assignment automatically

// Read the variant value
val variantValue = remoteConfig.getString("experiment_variant")
// "control" or "treatment"

// Log experiment exposure
Firebase.analytics.logEvent("experiment_exposure") {
    param("experiment_name", "new_checkout_flow")
    param("variant", remoteConfig.getString("checkout_variant"))
}
```

### A/B test setup
1. Go to Firebase Console → A/B Testing
2. Create experiment with Remote Config
3. Define variants and their config values
4. Set targeting (audience, country, etc.)
5. Set goal metric (e.g., 7-day retention)
6. Start experiment
7. Monitor results
8. Apply winning variant

---

## Q6: How do you handle real-time config updates?

```kotlin
// Listen for config updates (requires Realtime Remote Config)
remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
    if (task.isSuccessful && task.result) {
        // Config was updated
        updateUi()
    }
}

// With Coroutines
suspend fun fetchConfig(): Boolean {
    return remoteConfig.fetchAndActivate().await()
}

// Polling strategy (for critical config)
val handler = Handler(Looper.getMainLooper())
val pollRunnable = object : Runnable {
    override fun run() {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful && it.result) applyConfig()
        }
        handler.postDelayed(this, TimeUnit.MINUTES.toMillis(30))
    }
}
handler.post(pollRunnable)
```

### Fetch strategies
| Strategy | Interval | Use Case |
|----------|----------|----------|
| Default | 12 hours | General config |
| Aggressive | 1 hour | Active experiments |
| On-demand | Manual | Critical updates |
| Real-time | Instant | Kill switches |

---

## Q7: How do you use Remote Config with Compose?

```kotlin
@Composable
fun FeatureFlagScreen(remoteConfig: FirebaseRemoteConfig) {
    var isNewUiEnabled by remember { mutableStateOf(remoteConfig.getBoolean("feature_new_ui")) }

    // Fetch config on launch
    LaunchedEffect(Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful && it.result) {
                isNewUiEnabled = remoteConfig.getBoolean("feature_new_ui")
            }
        }
    }

    if (isNewUiEnabled) {
        NewUiScreen()
    } else {
        OldUiScreen()
    }
}

// With ViewModel
class FeatureViewModel(
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchConfig()
    }

    fun fetchConfig() {
        viewModelScope.launch {
            remoteConfig.fetchAndActivate().await()
            _uiState.update {
                it.copy(
                    isNewUiEnabled = remoteConfig.getBoolean("feature_new_ui"),
                    maxItems = remoteConfig.getLong("max_cart_items").toInt()
                )
            }
        }
    }
}
```

### Best practices
- Set sensible defaults
- Don't block UI on fetch — use defaults first
- Cache config locally
- Use conditions for targeted rollouts
- Monitor config changes in analytics
- Test config changes in staging first
- Have a kill switch for every new feature

---

## 🔗 Related Topics
- [Analytics](Analytics.md)
- [FCM](FCM.md)
- [Architecture](../advanced/Architecture.md)
