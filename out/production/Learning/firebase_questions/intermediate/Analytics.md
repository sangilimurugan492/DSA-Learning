# Firebase Analytics

## Q1: What is Google Analytics for Firebase?

Free, unlimited analytics that provides insight into app usage, user behavior, and engagement.

### Key concepts
| Concept | Description |
|---------|-------------|
| Event | User action (login, purchase, screen_view) |
| Parameter | Additional data for an event |
| User Property | User attribute (age, subscription) |
| Audience | Group of users matching criteria |
| Funnel | Sequence of events |
| Conversion | Event that counts as a goal |

---

## Q2: How do you log events?

```kotlin
val analytics = Firebase.analytics

// Log custom event
analytics.logEvent("checkout_started") {
    param("cart_total", 50.0)
    param("item_count", 3)
    param("payment_method", "card")
}

// Log with bundle
val bundle = Bundle().apply {
    putString("item_id", "SKU_123")
    putString("item_name", "Wireless Headphones")
    putDouble("price", 99.99)
    putLong("quantity", 1)
}
analytics.logEvent("view_item", bundle)

// Standard events (recommended)
analytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART) {
    param(FirebaseAnalytics.Param.ITEM_ID, "SKU_123")
    param(FirebaseAnalytics.Param.ITEM_NAME, "Headphones")
    param(FirebaseAnalytics.Param.PRICE, 99.99)
    param(FirebaseAnalytics.Param.QUANTITY, 1)
}

// Screen tracking (automatic with FirebaseAnalytics)
analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
    param(FirebaseAnalytics.Param.SCREEN_NAME, "ProductDetail")
    param(FirebaseAnalytics.Param.SCREEN_CLASS, "ProductDetailActivity")
}

// Purchase event (important for revenue tracking)
analytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
    param(FirebaseAnalytics.Param.TRANSACTION_ID, "order_123")
    param(FirebaseAnalytics.Param.VALUE, 150.0)
    param(FirebaseAnalytics.Param.CURRENCY, "USD")
    param(FirebaseAnalytics.Param.ITEMS, arrayListOf(
        Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, "SKU_123")
            putString(FirebaseAnalytics.Param.ITEM_NAME, "Headphones")
            putDouble(FirebaseAnalytics.Param.PRICE, 99.99)
            putLong(FirebaseAnalytics.Param.QUANTITY, 1)
        }
    ))
}
```

---

## Q3: How do you set user properties?

```kotlin
// Set user property
analytics.setUserProperty("subscription_type", "premium")
analytics.setUserProperty("favorite_category", "electronics")
analytics.setUserProperty("account_age_days", "365")

// Set user ID (for cross-device tracking)
analytics.setUserId("user_12345")

// Set audience membership
analytics.setUserProperty("is_premium", "true")

// Reset user data on sign out
analytics.setUserId(null)
analytics.setUserProperty("subscription_type", null)
```

### Standard user properties
| Property | Description |
|----------|-------------|
| `sign_up_method` | How user signed up |
| `subscription_type` | free/premium |
| `user_age_range` | 18-24, 25-34, etc. |
| `user_country` | Country code |

---

## Q4: How do you create audiences?

Audiences are created in the Firebase Console, not in code.

### Creating an audience
1. Go to Firebase Console → Analytics → Audiences
2. Click "New Audience"
3. Define criteria:
   - Events (e.g., `purchase` in last 7 days)
   - User properties (e.g., `subscription_type = premium`)
   - Demographics (age, country)
4. Name and save

### Example audiences
| Audience | Criteria |
|---------|----------|
| Premium users | `subscription_type = premium` |
| Cart abandoners | `add_to_cart` AND NOT `purchase` within 24h |
| Active users | `session_start` in last 7 days |
| High spenders | `purchase` with `value > 100` in last 30 days |
| New users | `first_open` in last 7 days |

### Using audiences
- Target FCM notifications
- Remotely configure features
- Create A/B tests
- Analyze behavior in dashboard

---

## Q5: How do you build funnels?

```kotlin
// Log each step of the funnel
analytics.logEvent("onboarding_step_1") { param("step", "welcome") }
analytics.logEvent("onboarding_step_2") { param("step", "profile") }
analytics.logEvent("onboarding_step_3") { param("step", "permissions") }
analytics.logEvent("onboarding_complete") { param("step", "done") }

// E-commerce funnel
analytics.logEvent("view_item_list") { param("category", "electronics") }
analytics.logEvent("select_item") { param("item_id", "SKU_123") }
analytics.logEvent("view_item") { param("item_id", "SKU_123") }
analytics.logEvent("add_to_cart") { param("item_id", "SKU_123") }
analytics.logEvent("begin_checkout") { param("cart_total", 99.99) }
analytics.logEvent("purchase") {
    param("transaction_id", "order_123")
    param("value", 99.99)
}
```

### Viewing funnels
- Go to Firebase Console → Analytics → Funnels
- Select events in order
- See drop-off rate at each step
- Filter by audience, device, country

---

## Q6: How do you use Analytics with Remote Config?

```kotlin
// Remote Config can use Analytics conditions
// e.g., Show different config for "premium" users

// 1. Define audience in Analytics
// 2. Create Remote Config condition based on audience
// 3. Set different values for each condition

// In Remote Config:
// Condition: "Audience matches 'Premium users'"
//   → value: "premium_feature_enabled"
// Default value: "premium_feature_disabled"
```

---

## Q7: How do you debug Analytics events?

```kotlin
// Enable debug logging
adb shell setprop debug.firebase.analytics.app com.your.package
adb shell setprop log.tag.FA VERBOSE
adb shell setprop log.tag.FA-SVC VERBOSE
adb logcat -v time -s FA FA-SVC

// View events in real-time in Firebase Console
// Go to Analytics → DebugView
// Events appear within seconds

// Disable analytics collection
Firebase.analytics.setAnalyticsCollectionEnabled(false)

// Enable at runtime (after consent)
Firebase.analytics.setAnalyticsCollectionEnabled(true)
```

### DebugView
- Shows events in real-time
- Shows user properties
- Shows device info
- Useful for verifying events are logged correctly

### Best practices
- Use standard events when possible
- Log events at the right granularity (not too fine)
- Use consistent parameter names
- Set user properties early (on sign-in)
- Test events in DebugView before releasing
- Don't log PII (personally identifiable information)

---

## 🔗 Related Topics
- [Crashlytics](../beginner/Crashlytics.md)
- [Remote Config](RemoteConfig.md)
- [FCM](FCM.md)
