# Longest Substring Without Repeating Characters — Detailed Explanation

> **LeetCode #3** | [Problem Link](https://leetcode.com/problems/longest-substring-without-repeating-characters/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (#1 most asked sliding window)  
> **Topic:** String, Sliding Window, HashMap

---

## 📋 Problem Statement

Given a string `s`, find the length of the **longest substring** without repeating characters.

### Examples

| Input | Output | Substring |
|-------|--------|-----------|
| `"abcabcbb"` | `3` | `"abc"` |
| `"bbbbb"` | `1` | `"b"` |
| `"pwwkew"` | `3` | `"wke"` |

### Visual Walkthrough — `"abcabcbb"`

```
Index:  0  1  2  3  4  5  6  7
Char:   a  b  c  a  b  c  b  b

Window [0,2] "abc" → len=3 ✅ (max so far)
  'a' at 3 is duplicate → jump left to 1
Window [1,3] "bca" → len=3
  'b' at 4 is duplicate → jump left to 2
Window [2,4] "cab" → len=3
  'c' at 5 is duplicate → jump left to 3
Window [3,5] "abc" → len=3
  'b' at 6 is duplicate → jump left to 5
Window [5,6] "cb" → len=2
  'b' at 7 is duplicate → jump left to 7
Window [7,7] "b" → len=1

Result: 3 ✅
```

---

## 🧩 Method 1: Brute Force — Check Every Substring

### Core Idea

For each starting index `i`, extend `j` until a duplicate is found. Track the maximum length.

### Code

```kotlin
fun lengthOfLongestSubstringBruteForce(s: String): Int {
    var maxLen = 0
    for (i in s.indices) {
        val seen = mutableSetOf<Char>()
        for (j in i until s.length) {
            if (s[j] in seen) break
            seen.add(s[j])
            maxLen = maxOf(maxLen, j - i + 1)
        }
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Nested loops |
| **Space** | O(N) | Set for seen characters |

---

## 🧩 Method 2: Sliding Window with HashMap (Optimal)

### Core Idea

Maintain a window `[left, right]` with all unique characters. When a duplicate is found, **jump** `left` directly past the last occurrence — no need to shrink one-by-one.

### Key Insight

> When `s[right]` is already in the window, we don't need to shrink `left` one step at a time. We can jump `left` directly to `lastSeen[s[right]] + 1` — skipping all positions that would also contain the duplicate.

### Algorithm — Step by Step

1. **Initialize** `left = 0`, `maxLen = 0`, `lastSeen = empty HashMap`.
2. **For each** `right` from `0` to `n-1`:
   - If `s[right]` is in `lastSeen` AND `lastSeen[s[right]] >= left`:
     - `left = lastSeen[s[right]] + 1` (jump past the duplicate).
   - `lastSeen[s[right]] = right` (update last seen position).
   - `maxLen = max(maxLen, right - left + 1)`.
3. **Return** `maxLen`.

### Dry Run — `"abcabcbb"`

| `right` | `s[right]` | `lastSeen[s[right]]` | In window? | `left` | Window | `maxLen` |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| 0 | a | — | No | 0 | "a" | 1 |
| 1 | b | — | No | 0 | "ab" | 2 |
| 2 | c | — | No | 0 | "abc" | **3** |
| 3 | a | 0 | Yes (0 ≥ 0) | **1** | "bca" | 3 |
| 4 | b | 1 | Yes (1 ≥ 1) | **2** | "cab" | 3 |
| 5 | c | 2 | Yes (2 ≥ 2) | **3** | "abc" | 3 |
| 6 | b | 4 | Yes (4 ≥ 3) | **5** | "cb" | 3 |
| 7 | b | 6 | Yes (6 ≥ 5) | **7** | "b" | 3 |

✅ **Result: `3`**

### Code

```kotlin
fun lengthOfLongestSubstringSlidingWindow(s: String): Int {
    val lastSeen = hashMapOf<Char, Int>()
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        if (s[right] in lastSeen && lastSeen[s[right]]!! >= left) {
            left = lastSeen[s[right]]!! + 1
        }
        lastSeen[s[right]] = right
        maxLen = maxOf(maxLen, right - left + 1)
    }

    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass, each character visited once |
| **Space** | O(N) | HashMap for last seen positions |

### Why It Works

- **HashMap stores last seen index:** When we encounter a duplicate, we know exactly where it was last seen.
- **Jump, don't shrink:** Instead of incrementing `left` one-by-one (O(2N)), we jump directly past the duplicate (O(N)).
- **Check `lastSeen[s[right]] >= left`:** This ensures the duplicate is within the current window. If it was before `left`, it's not relevant.

---

## 📊 Comparison Table

| Aspect | Brute Force | Sliding Window |
|--------|-------------|---------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(N) |
| **Approach** | Check every substring | Jump left past duplicates |
| **Optimality** | ❌ | ✅ Optimal |
| **Interview preference** | ⭐ (starting point) | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Sliding window pattern:** Maintain a window `[left, right]` that satisfies a constraint (all unique chars). Expand `right`, adjust `left` when constraint is violated.
2. **Jump, don't shrink:** Using a HashMap to store last seen positions allows O(1) jumps instead of incremental shrinking.
3. **Check `>= left`:** The duplicate must be within the current window to matter. If it was before `left`, it's already been excluded.
4. **Pattern:** This sliding window with HashMap pattern applies to many problems (Longest Repeating Character Replacement, Minimum Window Substring, etc.).
5. **Edge cases:** Empty string → 0. Single char → 1. All same chars → 1.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Substring Without Repeating | [#3](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium |
| Longest Repeating Character Replacement | [#424](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium |
| Minimum Window Substring | [#76](https://leetcode.com/problems/minimum-window-substring/) | Hard |
