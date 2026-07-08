# Background Task Scenarios

## Scenario 1: Upload That Survives App Kill

### Problem
User uploads a photo. If they close the app, the upload should still complete.

```kotlin
// ❌ Bad — coroutine dies when app is killed
class BadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            api.uploadPhoto(file)  // Cancelled when app dies
        }
    }
}
```

### Solution: WorkManager

```kotlin
// ✅ Good — WorkManager survives app kill and device reboot
class UploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val filePath = inputData.getString("filePath") ?: return Result.failure()
            val file = File(filePath)
            api.uploadPhoto(file)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry()
            else Result.failure()
        }
    }
}

// Enqueue
val request = OneTimeWorkRequestBuilder<UploadWorker>()
    .setInputData(workDataOf("filePath" to file.absolutePath))
    .setConstraints(
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    )
    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
    .addTag("upload")
    .build()

WorkManager.getInstance(context).enqueue(request)
```

### Key Takeaway
- `lifecycleScope` dies with the Activity/app
- WorkManager persists work to disk — survives app kill and reboot
- Set constraints (network, charging) for efficient scheduling
- Use `Result.retry()` for transient failures with backoff

---

## Scenario 2: Periodic Sync Every 15 Minutes

### Problem
Sync user data every 15 minutes when on Wi-Fi.

```kotlin
// ✅ Solution: PeriodicWorkRequest
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val localData = dao.getAllUsers()
            val remoteData = api.getUsers()
            // Merge and save
            dao.replaceAll(remoteData)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Enqueue unique periodic work
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNMETERED)  // Wi-Fi only
    .setRequiresBatteryNotLow(true)
    .build()

val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
    15, TimeUnit.MINUTES  // Minimum is 15 minutes
).setConstraints(constraints).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "daily_sync",
    ExistingPeriodicWorkPolicy.KEEP,  // Don't replace if already running
    syncRequest
)
```

### Key Takeaway
- Minimum periodic interval is 15 minutes
- `enqueueUniquePeriodicWork` prevents duplicates
- `KEEP` policy: if work exists, don't replace it
- `REPLACE` policy: cancel existing and start fresh

---

## Scenario 3: Foreground Service for Music Player

### Problem
Music should keep playing when the app goes to background.

```kotlin
// ✅ Solution: Foreground Service with notification
class MusicService : Service() {

    private val binder = MusicBinder()
    private var isPlaying = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Playing music")
        startForeground(NOTIFICATION_ID, notification)
        // Start playing
        isPlaying = true
        return START_STICKY  // Restart if killed
    }

    private fun createNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_music)
            .setOngoing(true)
            .build()
    }

    inner class MusicBinder : Binder() {
        fun getService() = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "music_channel"
    }
}

// Start service
val intent = Intent(context, MusicService::class.java)
ContextCompat.startForegroundService(context, intent)

// Android 14+ requires foreground service type
// <service android:name=".MusicService" android:foregroundServiceType="mediaPlayback" />
```

### Key Takeaway
- Foreground service shows a persistent notification
- `START_STICKY` restarts the service if killed
- Android 14+ requires `foregroundServiceType` in manifest
- Must call `startForeground()` within 5 seconds of starting

---

## Scenario 4: Chained Background Work

### Problem
Download → Process → Upload, in sequence. Each step depends on the previous.

```kotlin
// ✅ Solution: Chained WorkManager
class DownloadWorker : CoroutineWorker() {
    override suspend fun doWork(): Result {
        val file = downloadFile()
        return Result.success(workDataOf("filePath" to file.absolutePath))
    }
}

class ProcessWorker : CoroutineWorker() {
    override suspend fun doWork(): Result {
        val inputPath = inputData.getString("filePath")!!
        val processed = processImage(File(inputPath))
        return Result.success(workDataOf("filePath" to processed.absolutePath))
    }
}

class UploadWorker : CoroutineWorker() {
    override suspend fun doWork(): Result {
        val path = inputData.getString("filePath")!!
        api.upload(File(path))
        return Result.success()
    }
}

// Chain: download → process → upload
val download = OneTimeWorkRequestBuilder<DownloadWorker>().build()
val process = OneTimeWorkRequestBuilder<ProcessWorker>().build()
val upload = OneTimeWorkRequestBuilder<UploadWorker>().build()

WorkManager.getInstance(context)
    .beginWith(download)
    .then(process)
    .then(upload)
    .enqueue()

// Observe entire chain
WorkManager.getInstance(context)
    .getWorkInfoByIdLiveData(upload.id)
    .observe(this) { info ->
        if (info?.state == WorkInfo.State.SUCCEEDED) {
            showToast("Upload complete!")
        }
    }
```

### Key Takeaway
- `beginWith().then().then()` chains work sequentially
- Output data from one worker is input to the next
- If any step fails, the chain stops
- Use `then(listOf(workA, workB))` for parallel steps

---

## Scenario 5: One-Shot Immediate Background Task

### Problem
Save data to server when user clicks a button. Don't block the UI, but need it to complete.

```kotlin
// ✅ Solution 1: Coroutine for immediate, non-critical work
class SaveViewModel(private val repo: Repository) : ViewModel() {
    fun save(data: String) {
        viewModelScope.launch {
            try {
                repo.saveToServer(data)
                _uiState.value = UiState.Success("Saved!")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }
}

// ✅ Solution 2: WorkManager for guaranteed delivery
fun saveWithGuarantee(data: String) {
    val request = OneTimeWorkRequestBuilder<SaveWorker>()
        .setInputData(workDataOf("data" to data))
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueue(request)
}

// ✅ Solution 3: Foreground service for long-running visible work
fun startLongTask() {
    val intent = Intent(context, LongTaskService::class.java)
    ContextCompat.startForegroundService(context, intent)
}
```

### Decision Matrix
| Requirement                     | Use                    |
|---------------------------------|------------------------|
| Immediate, can be cancelled     | Coroutines             |
| Must survive app kill            | WorkManager            |
| User-visible long-running       | Foreground Service     |
| Exact time (alarm)              | AlarmManager           |
| Server push                     | FCM / Push             |

---

## 🔗 Related Topics
- [WorkManager & Background Tasks](../intermediate/WorkManager.md)
- [Coroutines Deep Dive](../../kotlin_questions/advanced/Coroutines.md)
