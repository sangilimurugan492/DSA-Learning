# Architecture Patterns (MVC, MVP, MVVM)

## 📖 Explanation

Architecture patterns separate concerns — keeping UI, business logic, and data layers independent. This makes code testable, maintainable, and scalable.

### MVC (Model-View-Controller)
```
View ←→ Controller ←→ Model
```
- **Model**: Data and business logic.
- **View**: XML layouts.
- **Controller**: Activity/Fragment.
- **Problem**: Activity acts as both View and Controller — becomes a "God Object".

### MVP (Model-View-Presenter)
```
View ←→ Presenter ←→ Model
```
- **View**: Activity/Fragment — passive, only updates UI via Presenter.
- **Presenter**: Contains business logic. Talks to View via an interface.
- **Model**: Data layer.
- **Advantage**: View is decoupled from logic — Presenter is testable.
- **Disadvantage**: Presenter holds a reference to View (1:1 coupling).

### MVVM (Model-View-ViewModel)
```
View ←→ ViewModel ←→ Model
```
- **View**: Activity/Fragment — observes ViewModel state.
- **ViewModel**: Holds UI state, survives configuration changes. Does NOT hold a View reference.
- **Model**: Data layer (Repository, API, DB).
- **Advantage**: No direct View reference in ViewModel. Lifecycle-aware via LiveData/StateFlow. Highly testable.
- **Recommended by Google** (Guide to App Architecture).

### Comparison
| Feature         | MVC              | MVP              | MVVM              |
|-----------------|------------------|------------------|-------------------|
| View-Logic coupling | High         | Low (interface)  | None (observation)|
| Testability     | Low              | High             | High              |
| Lifecycle aware | No               | No               | Yes (ViewModel)   |
| Google recommended | No            | No               | Yes               |
| Data binding    | No               | No               | Yes               |

### Clean Architecture (Advanced)
```
UI Layer → Domain Layer → Data Layer
(Presentation)  (Use Cases)  (Repository)
```
- **UI Layer**: Activities, Fragments, ViewModels.
- **Domain Layer**: Use cases (interactors) — pure Kotlin, no Android dependencies.
- **Data Layer**: Repositories, network, database.

---

## 🧪 Code Example (MVVM)

```kotlin
package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Model ---
data class Article(
    val id: Int,
    val title: String,
    val content: String
)

// --- Repository (Data Layer) ---
class ArticleRepository {
    suspend fun getArticles(): List<Article> {
        // Simulate network/database call
        kotlinx.coroutines.delay(500)
        return listOf(
            Article(1, "Kotlin Tips", "Learn Kotlin best practices..."),
            Article(2, "Android Architecture", "MVVM explained..."),
            Article(3, "Coroutines", "Master async programming...")
        )
    }
}

// --- ViewModel ---
class ArticleViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    // UI State
    sealed class UiState {
        object Loading : UiState()
        data class Success(val articles: List<Article>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadArticles()
    }

    fun loadArticles() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val articles = repository.getArticles()
                _uiState.value = UiState.Success(articles)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

// --- View (Activity) ---
class ArticleActivity : AppCompatActivity() {

    private lateinit var viewModel: ArticleViewModel
    private lateinit var adapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        viewModel = ViewModelProvider(
            this,
            ArticleViewModelFactory(ArticleRepository())
        )[ArticleViewModel::class.java]

        adapter = ArticleAdapter()
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@ArticleActivity)
            adapter = this@ArticleActivity.adapter
        }

        // Observe UI state
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showArticles(state.articles)
                    is UiState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun showLoading() { /* Show ProgressBar */ }
    private fun showArticles(articles: List<Article>) { adapter.submitList(articles) }
    private fun showError(message: String) { /* Show error */ }
}

// --- ViewModel Factory ---
class ArticleViewModelFactory(
    private val repository: ArticleRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArticleViewModel(repository) as T
    }
}
```

---

## ❓ Interview Questions

1. **What is the difference between MVC, MVP, and MVVM?**
   - MVC: Activity is both View and Controller (tight coupling). MVP: Presenter holds View via interface (testable but 1:1 coupling). MVVM: ViewModel has no View reference — View observes state via LiveData/Flow (loosely coupled, lifecycle-aware).

2. **Why does Google recommend MVVM?**
   - ViewModel survives configuration changes (no data loss on rotation), has no View reference (prevents memory leaks), is easily testable, and works naturally with LiveData/StateFlow for reactive UI updates.

3. **What is the role of a Repository in Android architecture?**
   - Repository is the single source of truth for data. It abstracts data sources (network, database, cache) from the ViewModel. The ViewModel doesn't know where data comes from.

4. **What is Clean Architecture and how does it differ from MVVM?**
   - Clean Architecture adds a Domain layer (use cases) between UI and Data. MVVM is a presentation pattern; Clean Architecture is a full architectural approach. You can use MVVM in the UI layer of Clean Architecture.

5. **How does ViewModel survive configuration changes?**
   - ViewModel is stored in a `ViewModelStore` retained by the Activity's `NonConfigurationInstances`. When the Activity is recreated, the same ViewModelStore is reused, so the ViewModel instance persists.

6. **What is the Single Source of Truth (SSOT) principle?**
   - SSOT means data should come from one authoritative source — typically the Repository. The Repository decides whether to serve from cache (Room), network, or both. The ViewModel never directly accesses the database or network — it only talks to the Repository. Benefits: (1) Consistent data across screens — multiple ViewModels reading from the same Repository see the same data. (2) Testability — mock the Repository in tests. (3) Caching strategy is centralized — the Repository handles online/offline logic. (4) Reactive updates — when the database changes, all observers are notified via Flow/LiveData. Example: Repository checks Room first, if empty fetches from API, saves to Room, and returns the Flow from Room.

7. **What is the difference between MVP and MVVM in terms of testability?**
   - Both are testable, but MVVM is more so. In MVP, the Presenter holds a reference to the View interface — you must mock the View to test the Presenter. In MVVM, the ViewModel has NO View reference — it exposes state via LiveData/StateFlow. You simply observe the state in tests without mocking any View. This makes ViewModel tests pure unit tests (no Android dependencies). Additionally, ViewModel survives configuration changes, so you can test state preservation naturally. MVP Presenters are recreated with the Activity (unless manually retained), making state preservation harder. MVVM also supports data binding, reducing boilerplate View update code.

8. **What are Use Cases (Interactors) and when should you use them?**
   - Use Cases encapsulate a single business operation (e.g., `GetUserUseCase`, `LoginUseCase`, `SyncDataUseCase`). They sit in the Domain layer of Clean Architecture between ViewModels and Repositories. Benefits: (1) Reusability — multiple ViewModels can use the same Use Case. (2) Testability — test business logic independently of UI. (3) Readability — `loginUseCase(email, password)` clearly communicates intent. (4) Single responsibility — each Use Case does one thing. Implement as a class with `operator fun invoke()` or a `suspend fun execute()`. Use Cases can combine multiple Repositories. Don't create Use Cases for simple CRUD — only for meaningful business logic. Example: `class TransferMoneyUseCase(private val accountRepo, private val notificationRepo) { suspend operator fun invoke(from, to, amount) { ... } }`.

9. **What is the Repository pattern and how does it handle offline support?**
   - The Repository abstracts data sources (network, database, cache) behind a single interface. For offline support: (1) **Cache-first strategy** — read from Room (local DB), return immediately if data exists. (2) **Network-first with cache fallback** — try network, on failure return cached data. (3) **Network-only with cache update** — always fetch from network, cache result for next time. The ViewModel doesn't know where data comes from — it just observes the Repository's Flow. Room acts as the SSOT — when network data arrives, save to Room, and Room's Flow automatically emits the update to the UI. This pattern is called "Offline-First" and is recommended by Google. Use `NetworkBoundResource` (or a custom equivalent) to coordinate network/ cache logic.

10. **What is MVI (Model-View-Intent) and how does it differ from MVVM?**
    - MVI is a unidirectional architecture: **Model** (immutable state) → **View** (renders state) → **Intent** (user actions) → back to Model. The key difference from MVVM: (1) **Single state** — the entire UI is represented by one immutable state object (not multiple LiveData/Flow). (2) **Unidirectional data flow** — state flows in one direction, intents flow back. This makes debugging easier — you can log every state change. (3) **Intent-based actions** — all user actions are modeled as sealed class intents: `sealed class UiIntent { object LoadUsers : UiIntent(); data class DeleteUser(val id: Long) : UiIntent() }`. (4) **State reducer** — a pure function takes current state + intent → new state. Benefits: predictable, testable, no race conditions. Drawbacks: more boilerplate, can over-serialize updates. MVI works well with Compose.

11. **How do you handle dependency injection in MVVM with ViewModels?**
    - Use Hilt for DI. (1) Annotate ViewModel with `@HiltViewModel` and `@Inject constructor`. (2) Hilt generates the `ViewModelProvider.Factory` automatically — no manual factory needed. (3) In Activity/Fragment, use `by viewModels()` to get the ViewModel. (4) For scoped dependencies, use Hilt modules (`@Module`, `@InstallIn`). Example: `@HiltViewModel class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel()`. The Repository, API, and DAO are all injected via Hilt modules. For Navigation Component, use `hiltNavGraphViewModels(R.id.destination)`. Hilt also supports `SavedStateHandle` injection into ViewModels for process-death survival.

12. **What are common anti-patterns in Android architecture?**
    - (1) **God Activity/Fragment** — putting all logic in the Activity. Fix: use MVVM with ViewModels. (2) **Fat ViewModel** — ViewModel contains business logic. Fix: move to Use Cases/Repository. (3) **Leaking Context** — ViewModel holds Activity/Context reference. Fix: ViewModel should never reference Views. (4) **Bypassing Repository** — ViewModel directly calls API or DB. Fix: always go through Repository. (5) **Shared ViewModel misuse** — using Activity-scoped ViewModel for everything instead of proper navigation. (6) **Over-engineering** — adding Clean Architecture layers to a simple CRUD app. Fix: match architecture to complexity. (7) **Tight coupling** — concrete classes instead of interfaces. Fix: depend on abstractions. (8) **No error handling** — not modeling error states in UI state. Fix: use sealed classes for Loading/Success/Error.

---

## 🔗 Related Topics
- [ViewModel & LiveData](ViewModelLiveData.md)
- [Room Database](RoomDatabase.md)
