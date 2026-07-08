# WorkManager & Background Tasks

## 📖 Explanation

WorkManager is a Jetpack library for deferrable, guaranteed background work. It persists work across device reboots and handles system constraints (battery, network).

### When to Use WorkManager
| Requirement                          | Use WorkManager? |
|-------------------------------------|-------------------|
| Work must survive process death      | ✅ Yes            |
| Work must survive device reboot      | ✅ Yes            |
| Work has constraints (network, charging) | ✅ Yes        |
| Immediate, one-shot async            | ❌ Use coroutines |
| Exact timing (alarm clock)           | ❌ Use AlarmManager |
| Push from server                     | ❌ Use FCM        |

### Types of Work
| Type           | Description                                    |
|----------------|------------------------------------------------|
| OneTimeWorkRequest | Runs once                               |
| PeriodicWorkRequest | Runs on a repeating interval (min 15 min) |
| Chained Work   | Multiple work items in sequence or parallel    |

### Constraints
```kotlin
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNCONNECTED)
    .setRequiresCharging(true)
    .setRequiresBatteryNotLow(true)
    .setRequiresDeviceIdle(false)
    .build()
```

### Work States
```
ENQUEUED → RUNNING → SUCCEEDED
                  → RETRY → RUNNING
                  → FAILED
                  → CANCELLED
```

### WorkRequest + Worker
- **Worker**: The actual work logic (suspend function).
- **WorkRequest**: Wraps Worker with constraints, backoff, tags.
- **WorkManager**: Enqueues and manages requests.

### Chaining
```kotlin
WorkManager.getInstance(context)
    .beginWith(workA)
    .then(workB)
    .then(listOf(workC, workD))
    .enqueue()
```

### Passing Data
Use `Data` (like a lightweight Bundle) to pass input/output.

```kotlin
val input = workDataOf("key" to "value")
val request = OneTimeWorkRequestBuilder<MyWorker>()
    .setInputData(input)
    .build()
```

---

## 🧪 Code Example

```kotlin
package com.example.app

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import java.util.concurrent.TimeUnit

// --- Worker: Upload file ---
class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val fileName = inputData.getString("fileName") ?: "unknown"
            // Simulate upload
            kotlinx.coroutines.delay(3000)

            // Return output data
            Result.success(workDataOf("result" to "Uploaded $fileName"))
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// --- Worker: Sync data ---
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Simulate sync
        kotlinx.coroutines.delay(2000)
        return Result.success(workDataOf("synced" to true))
    }
}

// --- Activity: Enqueue work ---
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // One-time work with constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(false)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf("fileName" to "photo.jpg"))
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30, TimeUnit.SECONDS
            )
            .addTag("upload")
            .build()

        // Enqueue
        WorkManager.getInstance(this).enqueue(uploadRequest)

        // Observe work status
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(uploadRequest.id)
            .observe(this) { workInfo ->
                when (workInfo?.state) {
                    WorkInfo.State.RUNNING -> println("Uploading...")
                    WorkInfo.State.SUCCEEDED -> {
                        val result = workInfo.outputData.getString("result")
                        println("Upload complete: $result")
                    }
                    WorkInfo.State.FAILED -> println("Upload failed")
                    WorkInfo.State.RETRY -> println("Retrying...")
                    else -> {}
                }
            }

        // Chained work: sync → upload
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
        WorkManager.getInstance(this)
            .beginWith(syncRequest)
            .then(uploadRequest)
            .enqueue()

        // Periodic work (min interval 15 minutes)
        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        // Cancel work by tag
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            WorkManager.getInstance(this).cancelAllWorkByTag("upload")
        }
    }
}
```

```groovy
// build.gradle dependencies
dependencies {
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}
```

### Foreground Service Worker
For long-running work that needs a notification.

```kotlin
class ForegroundWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        setForeground(
            ForegroundInfo(
                NOTIFICATION_ID,
                createNotification("Uploading..."),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        )
        // Do work
        return Result.success()
    }
}
```

---

## ❓ Interview Questions

1. **What is WorkManager and when should you use it?**
   - WorkManager is for deferrable, guaranteed background work that survives process death and device reboots. Use for uploads, syncs, and any work with constraints (network, charging). Don't use for immediate async (use coroutines) or exact-time alarms.

2. **What is the difference between OneTimeWorkRequest and PeriodicWorkRequest?**
   - OneTime runs once. Periodic runs on a repeating interval (minimum 15 minutes). Periodic work cannot have a custom backoff policy and always has the same constraints.

3. **How does WorkManager handle constraints?**
   - You set constraints (network type, charging, idle, battery not low). WorkManager only runs the work when all constraints are met. It defers execution until conditions are satisfied.

4. **How do you chain work in WorkManager?**
   - Use `beginWith(workA).then(workB).then(listOf(workC, workD)).enqueue()`. Work runs in order — B starts after A succeeds. Multiple works in `then()` run in parallel.

5. **What is the difference between `Result.retry()` and `Result.failure()`?**
   - `retry()` tells WorkManager to retry with the backoff policy. `failure()` marks the work as permanently failed (no retry). Use `retry` for transient errors (network), `failure` for permanent errors (invalid input).

---

## 🔗 Related Topics
- [ViewModel & LiveData](ViewModelLiveData.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
