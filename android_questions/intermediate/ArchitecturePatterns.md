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

---

## 🔗 Related Topics
- [ViewModel & LiveData](ViewModelLiveData.md)
- [Room Database](RoomDatabase.md)
