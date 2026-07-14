# Android with Java

## Q1: How does Android development with Java differ from Kotlin?

| Feature | Java | Kotlin |
|---------|------|--------|
| Null safety | Manual checks | Built-in (`?`, `!!`) |
| Extension functions | ❌ | ✅ |
| Data classes | Boilerplate getters/setters | `data class` |
| Coroutines | RxJava/AsyncTask (legacy) | Native coroutines |
| Smart casts | `instanceof` + cast | Automatic |
| Default args | Overloading | `fun(name: String = "default")` |
| SAM conversion | Explicit | Implicit (one method interface) |
| Range expressions | `for (int i = 0; ...)` | `for (i in 0..10)` |

```java
// Java — verbose
public class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}

// Kotlin — concise
// data class User(var name: String, var age: Int)
```

---

## Q2: How do you handle background tasks in Android Java?

```java
// 1. AsyncTask (deprecated — don't use in new code)
private static class DownloadTask extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... urls) {
        return downloadFile(urls[0]);  // Background thread
    }
    @Override
    protected void onPostExecute(String result) {
        textView.setText(result);  // Main thread
    }
}
// new DownloadTask().execute("https://example.com/file");

// 2. Thread + Handler (manual)
new Thread(() -> {
    String result = downloadFile(url);  // Background
    runOnUiThread(() -> textView.setText(result));  // Main thread
}).start();

// 3. ExecutorService (recommended for Java)
ExecutorService executor = Executors.newFixedThreadPool(4);
Handler mainHandler = new Handler(Looper.getMainLooper());

executor.execute(() -> {
    String result = downloadFile(url);  // Background
    mainHandler.post(() -> textView.setText(result));  // Main thread
});

// 4. RxJava (reactive)
Disposable disposable = Observable.fromCallable(() -> downloadFile(url))
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(result -> textView.setText(result),
               error -> Log.e("TAG", "Error", error));

// Dispose in onDestroy
@Override
protected void onDestroy() {
    super.onDestroy();
    disposable.dispose();
}
```

---

## Q3: How do you handle Activity lifecycle in Java?

```java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart — visible");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume — interactive");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause — partially visible");
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop — not visible");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy — cleanup");
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    // Handle configuration changes
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("key", "value");
    }
    // In onCreate: savedInstanceState.getString("key")
}
```

---

## Q4: How do you implement MVP/MVVM in Android Java?

```java
// MVP Pattern in Java

// Model
public class User {
    private String name;
    private String email;
    // getters, setters, constructor
}

// View interface — abstracts the Activity
public interface UserView {
    void showUser(User user);
    void showError(String message);
    void showLoading(boolean show);
}

// Presenter — business logic, no Android dependencies
public class UserPresenter {
    private final UserRepository repository;
    private UserView view;

    public UserPresenter(UserRepository repository) {
        this.repository = repository;
    }

    public void attachView(UserView view) { this.view = view; }
    public void detachView() { this.view = null; }

    public void loadUser(int id) {
        if (view != null) view.showLoading(true);
        repository.getUser(id, new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                if (view != null) {
                    view.showLoading(false);
                    view.showUser(user);
                }
            }
            @Override
            public void onError(String error) {
                if (view != null) {
                    view.showLoading(false);
                    view.showError(error);
                }
            }
        });
    }
}

// Activity implements View
public class UserActivity extends AppCompatActivity implements UserView {
    private UserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        presenter = new UserPresenter(new UserRepository());
        presenter.attachView(this);
        presenter.loadUser(1);
    }

    @Override
    public void showUser(User user) {
        nameText.setText(user.getName());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();  // Prevent leaks
        super.onDestroy();
    }
}
```

---

## Q5: How do you handle RecyclerView in Android Java?

```java
// Adapter
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users = new ArrayList<>();
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> newUsers) {
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(
            new DiffUtil.Callback() {
                @Override
                public int getOldListSize() { return users.size(); }
                @Override
                public int getNewListSize() { return newUsers.size(); }
                @Override
                public boolean areItemsTheSame(int o, int n) {
                    return users.get(o).getId() == newUsers.get(n).getId();
                }
                @Override
                public boolean areContentsTheSame(int o, int n) {
                    return users.get(o).equals(newUsers.get(n));
                }
            }
        );
        users = newUsers;
        diff.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameText.setText(user.getName());
        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));
    }

    @Override
    public int getItemCount() { return users.size(); }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
        }
    }
}

// In Activity
UserAdapter adapter = new UserAdapter(user -> {
    // Handle click
});
recyclerView.setAdapter(adapter);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
```

---

## Q6: How do you migrate from Java to Kotlin?

```java
// Step 1: Add Kotlin support to build.gradle
// plugins {
//     id("org.jetbrains.kotlin.android")
// }

// Step 2: Convert Java files to Kotlin
// In Android Studio: Code → Convert Java File to Kotlin File

// Step 3: Mixed project — Java and Kotlin can coexist
// Java can call Kotlin, Kotlin can call Java

// Step 4: Common migration patterns

// Java null handling → Kotlin null safety
// Java:  String name = user.getName();
//        if (name != null) { ... }
// Kotlin: val name: String? = user.name
//         name?.let { ... }

// Java callback → Kotlin coroutine
// Java:
public interface Callback {
    void onSuccess(String result);
    void onError(Exception e);
}
// Kotlin:
// suspend fun fetch(): Result<String>

// Java AsyncTask → Kotlin coroutines
// Java: new AsyncTask<Void, Void, String>() { ... }.execute();
// Kotlin: lifecycleScope.launch { val result = withContext(Dispatchers.IO) { fetch() } }

// Java Singleton → Kotlin object
// Java: private static Instance; static getInstance() { ... }
// Kotlin: object Singleton { ... }

// Java data class → Kotlin data class
// Java: 50 lines (fields, constructor, getters, setters, equals, hashCode)
// Kotlin: data class User(val name: String, val age: Int)
```

### Migration Strategy
1. Start with new code in Kotlin
2. Convert utility classes first (low risk)
3. Convert models/data classes
4. Convert ViewModels/Presenters
5. Convert Activities/Fragments last (high risk)
6. Keep Java interop with `@JvmStatic`, `@JvmField`, `@JvmOverloads`

---

## 🔗 Related Topics
- [OOP Concepts](../intermediate/OOPConcepts.md)
- [Concurrency](../intermediate/Concurrency.md)
- [Android Questions](../../android_questions/README.md)
- [Android Java Scenarios](../scenario_based/AndroidJavaScenarios.md)
