# 📅 Daily Interview Prep — Study Map

> A structured daily preparation sheet covering **5 core areas** for interview readiness.
> Each section links to the appropriate study file in this repository.

---

## 📋 Quick Navigation

| # | Topic | Focus Area | Study File | Est. Time |
|---|-------|-----------|------------|-----------|
| 1 | 🤖 Android Fundamentals | Basics, Components & Activity Lifecycle | [Link](#1-android-fundamentals) | 45 min |
| 2 | 🟣 Kotlin Basics | Syntax, Null Safety & Functions | [Link](#2-kotlin-basics) | 30 min |
| 3 | 🗣️ HR & Communication | Behavioral / STAR-format Questions | [Link](#3-hr--communication) | 30 min |
| 4 | 🧩 DSA Problem | Kth Largest Element in an Array (Heap) | [Link](#4-dsa-problem) | 45 min |
| 5 | 🏗️ System Design | Design a URL Shortener (TinyURL) | [Link](#5-system-design) | 60 min |

**Total Daily Time: ~3.5 hours**

---

## 1. Android Fundamentals

### What to Study
- Android app components (Activity, Service, Broadcast Receiver, Content Provider)
- Project structure (manifest, res, gradle)
- Activity lifecycle (onCreate → onStart → onResume → onPause → onStop → onDestroy)
- Fragment lifecycle basics

### 📂 Study Files
| File | Description |
|------|-------------|
| [Android Basics & Project Structure](../android_questions/beginner/Basics.md) | Core components, project structure, build system |
| [Activity Lifecycle](../android_questions/beginner/ActivityLifecycle.md) | Full lifecycle, state transitions, configuration changes |
| [Fragment Lifecycle](../android_questions/beginner/FragmentLifecycle.md) | Fragment states, back stack, communication |
| [Intents](../android_questions/beginner/Intents.md) | Explicit/implicit intents, intent filters |
| [UI Layouts](../android_questions/beginner/UILayouts.md) | LinearLayout, ConstraintLayout, RecyclerView |

### ✅ Self-Check Questions
- [ ] Can you draw the Activity lifecycle and explain each callback?
- [ ] What happens when you rotate the device?
- [ ] Difference between `onPause` and `onStop`?
- [ ] What are the 4 app components and their purpose?

---

## 2. Kotlin Basics

### What to Study
- Variables (`val` vs `var`), data types, type inference
- Null safety (`?`, `!!`, `?.`, `?:`)
- Functions, default/named arguments, lambdas
- Control flow (`when`, `for`, ranges)
- String templates and smart casts

### 📂 Study Files
| File | Description |
|------|-------------|
| [Kotlin Basics & Hello World](../kotlin_questions/beginner/Basics.md) | `main` function, semicolons, key characteristics |
| [Variables & Data Types](../kotlin_questions/beginner/VariablesAndDataTypes.md) | `val`/`var`, primitive types, type inference |
| [Null Safety](../kotlin_questions/beginner/NullSafety.md) | Nullable types, `?.`, `?:`, `!!`, safe casts |
| [Functions](../kotlin_questions/beginner/Functions.md) | Default args, named args, lambdas, infix |
| [Control Flow](../kotlin_questions/beginner/ControlFlow.md) | `if`, `when`, `for`, `while`, ranges |
| [String Templates](../kotlin_questions/beginner/StringTemplates.md) | String interpolation, multiline strings |
| [Type Checks & Smart Casts](../kotlin_questions/beginner/TypeChecksAndSmartCasts.md) | `is`, `as`, smart cast behavior |

### ✅ Self-Check Questions
- [ ] Difference between `val` and `var`?
- [ ] How does Kotlin's null safety work? What do `?.`, `?:`, and `!!` do?
- [ ] What is a smart cast and when does it apply?
- [ ] How do you define a lambda in Kotlin?

---

## 3. HR & Communication

### What to Study
- STAR format (Situation, Task, Action, Result)
- Leadership & team transition stories
- Mentoring and conflict resolution
- Career growth and self-reflection
- Technology-specific behavioral questions (Android, Kotlin, Java)

### 📂 Study Files
| File | Description |
|------|-------------|
| [General Behavioral Questions (STAR)](../behavioral_questions/GeneralBehavioralQuestions.md) | Leadership, mentoring, teamwork, career growth |
| [Android Behavioral Questions](../behavioral_questions/AndroidBehavioralQuestions.md) | Android-specific scenarios & decisions |
| [Kotlin Behavioral Questions](../behavioral_questions/KotlinBehavioralQuestions.md) | Kotlin adoption, migration, best practices |
| [Java Behavioral Questions](../behavioral_questions/JavaBehavioralQuestions.md) | Java experience, JVM tuning, design decisions |
| [Compose Behavioral Questions](../behavioral_questions/ComposeBehavioralQuestions.md) | Jetpack Compose migration & architecture |

### ✅ Self-Check Questions
- [ ] Can you tell a story using the STAR format for "a time you led through change"?
- [ ] Describe a conflict you resolved in your team.
- [ ] What is your greatest professional achievement?
- [ ] Why are you looking for a change?

---

## 4. DSA Problem

### Today's Problem: Kth Largest Element in an Array (LeetCode #215)

> **Topic:** Heap / QuickSelect | **Difficulty:** Medium | **Importance:** ⭐⭐⭐⭐⭐

### What to Study
- Min-heap of size K approach — O(N log K)
- QuickSelect (partition-based) approach — O(N) average
- Trade-offs between heap and QuickSelect
- Kotlin implementation

### 📂 Study Files
| File | Description |
|------|-------------|
| [Problem Explanation (MD)](../DSA/heap/kth_largest_element/KthLargestElement.md) | Full walkthrough: problem, 3 methods, complexity |
| [Kotlin Solution (KT)](../DSA/heap/kth_largest_element/KthLargestElement.kt) | Runnable Kotlin code with all approaches |

### ✅ Self-Check Questions
- [ ] Can you explain the min-heap approach and why it's O(N log K)?
- [ ] How does QuickSelect work? What's its average vs worst-case complexity?
- [ ] When would you choose heap over QuickSelect?
- [ ] Can you code the solution from scratch in 15 minutes?

### 🔄 More DSA Problems for Rotation
| Day | Problem | File |
|-----|---------|------|
| Day 2 | Top K Frequent Elements | [Link](../DSA/heap/top_k_frequent_elements/TopKFrequentElements.md) |
| Day 3 | Merge K Sorted Lists | [Link](../DSA/heap/merge_k_sorted_lists/MergeKSortedLists.md) |
| Day 4 | Find Median from Data Stream | [Link](../DSA/heap/find_median_from_data_stream/FindMedianFromDataStream.md) |
| Day 5 | Two Sum (sorted array) | [Link](../DSA/array/two_pointer/two_sum_level_two_with_sorted_array/) |
| Day 6 | Container With Most Water | [Link](../DSA/array/two_pointer/container_with_most_water/) |
| Day 7 | Longest Substring Without Repeating | [Link](../DSA/array/sliding_window/longest_substring_without_repeating/) |

---

## 5. System Design

### Today's Question: Design a URL Shortener (TinyURL)

> **Difficulty:** Beginner | **Key Concepts:** Key generation, caching, redirection, sharding

### What to Study
- Requirements clarification (functional & non-functional)
- Back-of-the-envelope estimation (QPS, storage, bandwidth)
- High-level design (API Gateway → Write/Read services → DB + Cache)
- Deep dive: Key generation strategies (Hash+Base62, KGS, Counter)
- 301 vs 302 redirection (analytics vs performance)
- Caching strategy (Redis, LRU, cache-aside)
- Sharding & bottlenecks

### 📂 Study Files
| File | Description |
|------|-------------|
| [Case Studies (URL Shortener is #1)](../system_design/case_studies/README.md) | Full end-to-end design with 5-step framework |
| [Interview Framework (5-Step)](../system_design/interview/README.md) | How to approach any system design question |
| [System Design Overview](../system_design/README.md) | Folder structure & study guide |
| [Fundamentals](../system_design/fundamentals/README.md) | Scalability, CAP, consistency, idempotency |

### ✅ Self-Check Questions
- [ ] Can you estimate QPS and storage for 100M URLs/month?
- [ ] Explain 3 key generation strategies and their trade-offs?
- [ ] When would you use 301 vs 302 redirect?
- [ ] What are the bottlenecks and how do you mitigate them?

### 🔄 More System Design Questions for Rotation
| Day | Question | Section in Case Studies |
|-----|----------|------------------------|
| Day 2 | Design Twitter / Social Feed | Section 2 |
| Day 3 | Design Chat System (WhatsApp) | Section 3 |
| Day 4 | Design Rate Limiter | Section 4 |
| Day 5 | Design Google Drive | Section 5 |
| Day 6 | Design Notification System | Section 6 |
| Day 7 | Design Ticket Booking (BookMyShow) | Section 8 |

---

## 📊 Daily Tracker

| Date | Android | Kotlin | HR/Comm | DSA | Sys Design | Notes |
|------|---------|--------|---------|-----|------------|-------|
| Day 1 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |
| Day 2 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |
| Day 3 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |
| Day 4 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |
| Day 5 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |
| Day 6 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |
| Day 7 | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | |

> Mark each box with ✅ when complete. Add notes on areas to revisit.

---

## 🎯 Study Tips

1. **Android & Kotlin first** — These are foundational. Review one new sub-topic each day.
2. **DSA daily** — Solve one problem per day. Code it, don't just read it.
3. **System Design** — Practice drawing the diagram on paper. Time yourself (45 min).
4. **HR/Communication** — Prepare 5-7 STAR stories. Practice speaking them out loud.
5. **Rotate topics** — Use the rotation tables above to cycle through different problems/questions.
6. **Review weekly** — Revisit self-check questions from previous days to reinforce retention.

---

## 🔗 Quick Links to All Repositories

| Category | Link |
|----------|------|
| Android Questions | [android_questions/](../android_questions/) |
| Kotlin Questions | [kotlin_questions/](../kotlin_questions/) |
| Behavioral Questions | [behavioral_questions/](../behavioral_questions/) |
| DSA Problems | [DSA/](../DSA/) |
| System Design | [system_design/](../system_design/) |
| Design Patterns | [design_patterns/](../design_patterns/) |
| SOLID Principles | [solid/](../solid/) |
| Jetpack Compose | [jetpack_compose_questions/](../jetpack_compose_questions/) |
| Testing | [testing_questions/](../testing_questions/) |
| CI/CD | [cicd_questions/](../cicd_questions/) |
| Interview Schedule | [interview_schedule/](../interview_schedule/) |
