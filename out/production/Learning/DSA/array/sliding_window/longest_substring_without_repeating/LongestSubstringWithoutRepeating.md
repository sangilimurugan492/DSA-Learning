# Longest Substring Without Repeating Characters — Detailed Explanation

> **LeetCode #3** | [Problem Link](https://leetcode.com/problems/longest-substring-without-repeating-characters/)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic sliding window — must know)
> **Topic:** Sliding Window, HashSet, HashMap, String

---

## 📋 Problem Statement

Given a string `s`, find the length of the longest substring without repeating characters.

### Examples

```
Input: "abcabcbb" → Output: 3  ("abc")
Input: "bbbbb"    → Output: 1  ("b")
Input: "pwwkew"   → Output: 3  ("wke")
```

---

## 🧩 Method 1: Sliding Window + HashSet — O(N)

### Core Idea

Maintain a window `[left, right]` with all unique characters. Expand `right`. If `s[right]` is already in the window, shrink `left` until the duplicate is removed.

### Key Insight

> The window always contains unique characters. When we encounter a duplicate, we shrink from the left until it's removed. Track the max window size.

### Dry Run — `"abcabcbb"`

```
left=0, seen={}, maxLen=0

right=0 'a': not in seen → seen={a}, maxLen=1
right=1 'b': not in seen → seen={a,b}, maxLen=2
right=2 'c': not in seen → seen={a,b,c}, maxLen=3
right=3 'a': 'a' in seen → remove s[0]='a', left=1 → seen={b,c}
                   add 'a' → seen={a,b,c}, maxLen=3
right=4 'b': 'b' in seen → remove s[1]='b', left=2 → seen={a,c}
                   add 'b' → seen={a,b,c}, maxLen=3
right=5 'c': 'c' in seen → remove s[2]='c', left=3 → seen={a,b}
                   add 'c' → seen={a,b,c}, maxLen=3
right=6 'b': 'b' in seen → remove s[3]='a', left=4 → remove s[4]='b', left=5 → seen={c}
                   add 'b' → seen={b,c}, maxLen=3
right=7 'b': 'b' in seen → remove s[5]='c', left=6 → remove s[6]='b', left=7 → seen={}
                   add 'b' → seen={b}, maxLen=3

Result: 3 ✅
```

### Code

```kotlin
fun lengthOfLongestSubstring(s: String): Int {
    val seen = HashSet<Char>()
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        while (s[right] in seen) {
            seen.remove(s[left])
            left++
        }
        seen.add(s[right])
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each char added/removed once |
| **Space** | O(min(N, charset)) | HashSet size |

---

## 🧩 Method 2: Optimized HashMap (Jump Left) — O(N)

### Core Idea

Instead of shrinking one by one, store the last index of each character. On duplicate, jump `left` directly to `lastIndex[char] + 1`.

### Key Insight

> If we've seen `s[right]` before and its last index is >= `left`, we can jump `left` past it in one step — no need to shrink one character at a time.

### Code

```kotlin
fun lengthOfLongestSubstringOptimized(s: String): Int {
    val lastIndex = HashMap<Char, Int>()
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        val ch = s[right]
        if (ch in lastIndex && lastIndex[ch]!! >= left) {
            left = lastIndex[ch]!! + 1  // Jump past the duplicate
        }
        lastIndex[ch] = right
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass, no shrinking loop |
| **Space** | O(min(N, charset)) | HashMap size |

---

## 📊 Method Comparison

| Method | Time | Space | Pros | Cons |
|--------|------|-------|------|------|
| HashSet + Shrink | O(2N) = O(N) | O(charset) | Simple | May visit chars twice |
| HashMap + Jump | O(N) | O(charset) | Faster (no shrink loop) | Slightly more complex |

> **Interview Tip:** Start with the HashSet approach — it's intuitive. Then optimize with the HashMap "jump" approach. The key check is `lastIndex[ch] >= left` — we only jump if the duplicate is inside the current window. This is the foundation for all "longest substring with condition K" problems.
