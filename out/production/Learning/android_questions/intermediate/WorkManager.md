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

6. **What is the difference between `CoroutineWorker`, `Worker`, and `RxWorker`?**
    - `Worker` — runs on a background thread (from `Executor`) provided by WorkManager. Override `doWork()` which returns `Result`. The work is blocking — you must manage threading yourself. `CoroutineWorker` — extends `Worker` and provides a `suspend fun doWork()`. Runs on `Dispatchers.Default` by default. Supports coroutine cancellation naturally. **Recommended for Kotlin projects.** You can use `withContext(Dispatchers.IO)` for blocking I/O. `RxWorker` — for projects using RxJava. Returns `Single<Result>`. Use `CoroutineWorker` for new Kotlin code. `ListenableWorker` — the base class for custom threading models (e.g., if you need a specific Executor). All workers extend `ListenableWorker`. `CoroutineWorker` is the best choice for coroutine-based apps.

7. **How do you ensure unique work with `enqueueUniqueWork`?**
    - `enqueueUniqueWork(uniqueWorkName, existingWorkPolicy, request)` ensures only one work with that name runs at a time. `ExistingWorkPolicy` options: (1) `REPLACE` — cancel the existing work and start the new one. (2) `KEEP` — keep the existing work, discard the new request. (3) `APPEND` — append the new work after the existing one finishes (creates a chain). Use unique work for: sync operations (don't want multiple syncs running), upload tasks, or any task that should only have one instance. Example: `WorkManager.getInstance(context).enqueueUniqueWork("daily_sync", ExistingWorkPolicy.KEEP, syncRequest)`. For periodic work, use `enqueueUniquePeriodicWork` with `ExistingPeriodicWorkPolicy.KEEP` or `REPLACE`. Always use unique work names that describe the task (e.g., "upload_profile_photo").

8. **How do you observe and communicate work progress?**
    - Use `WorkInfo` and `LiveData`/`Flow` to observe work status. `WorkManager.getWorkInfoByIdFlow(id)` returns a `Flow<WorkInfo>` with states: ENQUEUED, RUNNING, SUCCEEDED, FAILED, CANCELLED. For progress updates, call `setProgress(workDataOf("progress" to 50))` inside the Worker, then read it from `WorkInfo.progress` in the observer. Example: In Worker: `setProgress(workDataOf("progress" to 50)); ...; setProgress(workDataOf("progress" to 100))`. In UI: `workInfo.progress.getInt("progress", 0)`. Observe by tag: `getWorkInfosByTagFlow("upload")`. Observe by name: `getWorkInfosForUniqueWorkFlow("upload_task")`. Use this for progress bars during uploads/syncs. `WorkInfo` also contains `outputData` which is available after the work completes.

9. **How do you handle foreground services with WorkManager?**
    - For long-running work (e.g., uploads, downloads) that the user should be aware of, use `setForeground()` inside `CoroutineWorker`. This promotes the Worker to a foreground service with a notification. Steps: (1) Create a notification (Android 14+ requires foreground service type). (2) Call `setForeground(ForegroundInfo(notificationId, notification, foregroundServiceType))`. (3) The Worker now runs as a foreground service — the system won't kill it easily. (4) Call `setForeground()` again to update the notification. Note: On Android 14+, you must declare the `foregroundServiceType` in the manifest and match it with the `ForegroundInfo`. The Worker is automatically demoted to a background service if `setForeground` is not called within 10 seconds. Use `ExpeditedWorkRequest` (Android 12+) for urgent work that needs foreground-like priority.

10. **What is `ExpeditedWorkRequest` and when do you use it?**
    - `ExpeditedWorkRequest` (Android 12+) is for urgent work that needs to execute immediately. Unlike regular work (which may be deferred), expedited work runs as soon as possible as a foreground service. It bypasses some background execution limits. Use for: (1) User-initiated uploads/downloads. (2) Time-sensitive notifications. (3) Critical syncs. Limitations: (1) The system may throttle the number of expedited jobs per app per day. (2) Must call `setForeground()` within 10 seconds. (3) Requires `FOREGROUND_SERVICE` permission. Create with: `OneTimeWorkRequestBuilder<MyWorker>().setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).build()`. `OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST` means if the expedited quota is exhausted, it falls back to a regular work request. `OutOfQuotaPolicy.DROP_WORK_REQUEST` discards it.

11. **How does WorkManager persist work across device reboots?**
    - WorkManager stores work requests in a Room database (internal). When the device reboots, WorkManager receives the `BOOT_COMPLETED` broadcast and re-enqueues all pending work. This is why WorkManager guarantees execution — it's persisted to disk. The work state (ENQUEUED, RUNNING) is also persisted. If a Worker was RUNNING when the device was killed, WorkManager re-runs it after reboot. Constraints are re-evaluated after reboot — the work only runs when constraints are met. The internal database is managed by WorkManager — you don't need to configure anything. Note: `PeriodicWorkRequest` is also re-scheduled after reboot. This is the key differentiator from coroutines/Services which are lost on process death or reboot.

12. **When should you NOT use WorkManager?**
    - (1) **Immediate async** — use coroutines with `viewModelScope` or `lifecycleScope`. WorkManager has overhead (database persistence, scheduling). (2) **Exact time alarms** — use `AlarmManager` for alarm-clock precision. WorkManager's timing is approximate. (3) **Push from server** — use Firebase Cloud Messaging (FCM) for server-triggered actions. (4) **Foreground user interaction** — use a foreground Service with a notification for music playback, ongoing calls. (5) **Real-time data** — use WebSockets or polling with coroutines. WorkManager is for **deferrable** and **guaranteed** work — tasks that can wait but must complete eventually. If the task must run at an exact time, or must respond instantly to server events, use alternatives. WorkManager's sweet spot: sync, upload, cleanup, analytics, and scheduled tasks with constraints.

---

## 🔗 Related Topics
- [ViewModel & LiveData](ViewModelLiveData.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
