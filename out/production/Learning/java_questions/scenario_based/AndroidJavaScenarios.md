# Android Java Scenarios

## Scenario 1: AsyncTask Memory Leak

### Problem
An AsyncTask holds an Activity reference. When the Activity is destroyed, the task is still running, leaking the Activity.

```java
// ❌ Bad — AsyncTask leaks Activity
public class BadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownloadTask().execute("https://example.com/file");
    }

    // Non-static inner class holds Activity reference
    class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return downloadFile(urls[0]);  // Slow
        }
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);  // Activity leaked if destroyed
        }
    }
}
```

### Solution: Static inner class + WeakReference

```java
// ✅ Good — static inner class with WeakReference
public class GoodActivity extends AppCompatActivity {
    private DownloadTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = new DownloadTask(textView);
        task.execute("https://example.com/file");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) task.cancel(true);  // Cancel on destroy
    }

    // Static — no implicit Activity reference
    private static class DownloadTask extends AsyncTask<String, Void, String> {
        private final WeakReference<TextView> textViewRef;

        DownloadTask(TextView textView) {
            this.textViewRef = new WeakReference<>(textView);
        }

        @Override
        protected String doInBackground(String... urls) {
            return downloadFile(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView tv = textViewRef.get();
            if (tv != null) {  // Check if still alive
                tv.setText(result);
            }
        }
    }
}
```

### Key Takeaway
- Non-static inner classes hold implicit reference to outer Activity
- Make inner classes `static` and use `WeakReference` for UI components
- Cancel tasks in `onDestroy()`
- Check if the view is still alive before updating
- Best: migrate to ExecutorService + Handler or RxJava

---

## Scenario 2: Network Call on Main Thread

### Problem
The app crashes with `NetworkOnMainThreadException` when making a Retrofit call directly in an onClick handler.

```java
// ❌ Bad — network call on main thread
button.setOnClickListener(v -> {
    // ❌ Crashes — network I/O on UI thread
    Response<User> response = api.getUser(1).execute();
    textView.setText(response.body().getName());
});
```

### Solution: Background thread + Handler

```java
// ✅ Good — ExecutorService + Handler
button.setOnClickListener(v -> {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    executor.execute(() -> {
        try {
            Response<User> response = api.getUser(1).execute();
            User user = response.body();

            // Update UI on main thread
            handler.post(() -> {
                if (user != null) {
                    textView.setText(user.getName());
                } else {
                    textView.setText("Error");
                }
            });
        } catch (IOException e) {
            handler.post(() -> textView.setText("Network error"));
        }
    });
});

// ✅ Better — Retrofit with async callback
api.getUser(1).enqueue(new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if (response.isSuccessful() && response.body() != null) {
            textView.setText(response.body().getName());
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        textView.setText("Error: " + t.getMessage());
    }
});
```

### Key Takeaway
- Never do network I/O on the main thread — Android throws `NetworkOnMainThreadException`
- Use `ExecutorService` for background work, `Handler.post()` for UI updates
- Retrofit's `enqueue()` handles threading automatically
- Always handle failure cases (network error, null body)

---

## Scenario 3: Activity Recreated on Rotation — Data Lost

### Problem
User rotates the screen. The Activity is recreated, and all loaded data is lost — the app makes another API call.

```java
// ❌ Bad — data in Activity, lost on rotation
public class BadActivity extends AppCompatActivity {
    private List<User> users;  // Lost on rotation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUsers();  // Called again on every rotation
    }
}
```

### Solution: ViewModel (or retain Fragment for Java)

```java
// ✅ Good — ViewModel survives configuration changes
public class UserViewModel extends ViewModel {
    private List<User> users;
    private final UserRepository repository = new UserRepository();

    public void loadUsers(MutableLiveData<List<User>> liveData) {
        if (users != null) {
            liveData.setValue(users);  // Return cached
            return;
        }
        repository.getUsers(new Callback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                users = result;  // Cache in ViewModel
                liveData.setValue(result);
            }
            @Override
            public void onError(String error) {
                liveData.setValue(null);
            }
        });
    }
}

public class GoodActivity extends AppCompatActivity {
    private UserViewModel viewModel;
    private final MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        usersLiveData.observe(this, users -> {
            if (users != null) adapter.setUsers(users);
        });

        viewModel.loadUsers(usersLiveData);  // Only loads once — cached in ViewModel
    }
}
```

### Key Takeaway
- ViewModel survives configuration changes (rotation)
- Data is cached in ViewModel — no re-fetch on rotation
- `LiveData.observe()` auto-updates UI when data changes
- `ViewModelProvider(this)` scopes ViewModel to Activity lifecycle

---

## Scenario 4: Handler Memory Leak

### Problem
A Handler with a delayed message holds an Activity reference. The Activity is destroyed but the Handler's message is still in the queue.

```java
// ❌ Bad — Handler leaks Activity
public class BadActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            updateUI();  // Holds Activity reference
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.postDelayed(() -> updateUI(), 60000);  // 60s delay
    }
    // If destroyed before 60s → Activity leaked
}
```

### Solution: Static Handler + removeCallbacks

```java
// ✅ Good — static Handler with WeakReference + removeCallbacks
public class GoodActivity extends AppCompatActivity {
    private static class SafeHandler extends Handler {
        private final WeakReference<GoodActivity> activityRef;

        SafeHandler(GoodActivity activity) {
            super(Looper.getMainLooper());
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GoodActivity activity = activityRef.get();
            if (activity != null && !activity.isFinishing()) {
                activity.updateUI();
            }
        }
    }

    private SafeHandler handler;
    private final Runnable updateRunnable = this::updateUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new SafeHandler(this);
        handler.postDelayed(updateRunnable, 60000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);  // ✅ Remove pending messages
    }

    private void updateUI() {
        // Update UI
    }
}
```

### Key Takeaway
- Non-static Handler holds implicit reference to Activity
- Make Handler `static` + `WeakReference` to Activity
- Always `removeCallbacks()` in `onDestroy()`
- Check `isFinishing()` before updating UI
- Consider using `View.post()` or `LiveData` instead of Handler

---

## Scenario 5: SharedPreferences on Main Thread

### Problem
The app freezes briefly when saving to SharedPreferences — `commit()` is synchronous on the main thread.

```java
// ❌ Bad — commit() blocks main thread
button.setOnClickListener(v -> {
    SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
    prefs.edit()
        .putString("token", newToken)
        .commit();  // ❌ Synchronous — blocks UI
});
```

### Solution: apply() (async) or background thread

```java
// ✅ Good — apply() is asynchronous
button.setOnClickListener(v -> {
    SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
    prefs.edit()
        .putString("token", newToken)
        .apply();  // ✅ Async — doesn't block UI
});

// ✅ For bulk writes — use background thread
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.execute(() -> {
    SharedPreferences prefs = getSharedPreferences("cache", MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    for (Map.Entry<String, String> entry : largeMap.entrySet()) {
        editor.putString(entry.getKey(), entry.getValue());
    }
    editor.apply();  // Single async write
});
```

| Method | Thread | Blocks UI | Reliability |
|--------|--------|-----------|------------|
| `commit()` | Calling thread | ✅ Yes | Synchronous — returns success |
| `apply()` | Background | ❌ No | Async — no return value |

### Key Takeaway
- `commit()` is synchronous — blocks the calling thread
- `apply()` is asynchronous — writes to disk in background
- Use `apply()` for most cases — `commit()` only when you need confirmation
- For bulk writes, batch them in a single `apply()` call
- SharedPreferences is not thread-safe for cross-process — use DataStore or ContentProvider

---

## 🔗 Related Topics
- [Android with Java](../advanced/AndroidWithJava.md)
- [Concurrency](../intermediate/Concurrency.md)
- [Android Questions](../../android_questions/README.md)
