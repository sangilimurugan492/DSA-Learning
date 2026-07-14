# Assign Cookies — Detailed Explanation

> **LeetCode #455** | [Problem Link](https://leetcode.com/problems/assign-cookies/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Classic Greedy)  
> **Topic:** Greedy, Two Pointers, Sorting

---

## 📋 Problem Statement

Each child `i` has a greed factor `g[i]` (minimum cookie size to be content). Each cookie `j` has a size `s[j]`. If `s[j] >= g[i]`, child `i` is content. Maximize the number of content children. Each child gets at most one cookie.

### Examples

| g | s | Output |
|---|---|--------|
| `[1,2,3]` | `[1,1]` | 1 |
| `[1,2]` | `[1,2,3]` | 2 |

---

## 🧩 Method 1: Brute Force — O(N × M)

### Core Idea

Sort both. For each child, scan all cookies for the smallest unused one that satisfies greed.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × M) | Nested loops |
| **Space** | O(M) | used array |

---

## 🧩 Method 2: Two Pointers — O(N log N + M log M)

### Core Idea

Sort both. Use two pointers. If cookie satisfies child → move both. If cookie too small → move to next cookie.

### Key Insight

> Assign the **smallest sufficient cookie** to the **least greedy child** first. This greedy choice maximizes content children.

### Dry Run — `g=[1,2,3], s=[1,1]`

| childPtr | cookiePtr | s[cookiePtr] | g[childPtr] | Satisfied? | Action |
|:--------:|:---------:|:------------:|:----------:|:----------:|:------:|
| 0 | 0 | 1 | 1 | 1≥1 ✅ | child++, cookie++ |
| 1 | 1 | 1 | 2 | 1<2 ❌ | cookie++ |

✅ **Result: 1**

### Code

```kotlin
fun findContentChildren(g: IntArray, s: IntArray): Int {
    g.sort(); s.sort()
    var childPointer = 0; var cookiePointer = 0

    while (childPointer < g.size && cookiePointer < s.size) {
        if (s[cookiePointer] >= g[childPointer]) childPointer++
        cookiePointer++
    }
    return childPointer
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N + M log M) | Sorting dominates |
| **Space** | O(1) | Two pointers |

---

## 📊 Comparison Table

| Aspect | Brute Force | Two Pointers |
|--------|-------------|-------------|
| **Time** | O(N × M) | O(N log N + M log M) |
| **Space** | O(M) | O(1) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Greedy choice:** Smallest sufficient cookie → least greedy child. This leaves bigger cookies for greedier children.
2. **Sort both arrays:** Sorting enables the two-pointer approach.
3. **Pattern:** Classic greedy assignment — extends to Task Scheduler, Meeting Rooms.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Assign Cookies | [#455](https://leetcode.com/problems/assign-cookies/) | Easy |
| Meeting Rooms | [#252](https://leetcode.com/problems/meeting-rooms/) | Easy |
| Task Scheduler | [#621](https://leetcode.com/problems/task-scheduler/) | Medium |
