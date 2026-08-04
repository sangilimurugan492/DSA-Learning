# Valid Anagrams — Detailed Explanation

> **LeetCode #242** | [Problem Link](https://leetcode.com/problems/valid-anagram/)
> **FAANG Importance:** ⭐⭐⭐⭐ (Classic hash map frequency problem)
> **Topic:** Hash Map, String, Sorting

---

## 📋 Problem Statement

Given two strings `s` and `t`, return `true` if `t` is an anagram of `s`, and `false` otherwise. An anagram uses all original characters exactly once.

### Examples

```
Input: s = "anagram", t = "nagaram"  → Output: true
Input: s = "rat", t = "car"          → Output: false
Input: s = "listen", t = "silent"    → Output: true
```

---

## 🧩 Method 1: Sort & Compare — O(N log N)

### Core Idea

Sort both strings. If they're equal → anagrams.

### Code

```kotlin
fun isAnagramSort(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    return s.toCharArray().sorted() == t.toCharArray().sorted()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sorting |
| **Space** | O(N) | Sorted arrays |

---

## 🧩 Method 2: Character Frequency Count — O(N)

### Core Idea

Count character frequencies in `s` (increment) and `t` (decrement). If all counts are 0 → anagrams.

### Key Insight

> If two strings are anagrams, every character appears the same number of times. Increment for `s`, decrement for `t` — if all zeros at the end, they match.

### Dry Run — `s = "anagram", t = "nagaram"`

```
count array (26 zeros):
s: a n a g r a m → a:3, n:1, g:1, r:1, m:1
t: n a g a r a m → a:3, n:1, g:1, r:1, m:1

After increment (s) and decrement (t):
a: 3-3=0, n: 1-1=0, g: 1-1=0, r: 1-1=0, m: 1-1=0
All zeros → true ✅
```

### Code

```kotlin
fun isAnagramCount(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    val count = IntArray(26)
    for (i in s.indices) {
        count[s[i] - 'a']++
        count[t[i] - 'a']--
    }
    return count.all { it == 0 }
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(1) | Fixed 26-element array |

---

## 🧩 Method 3: HashMap (Unicode-safe) — O(N)

### Core Idea

Use a HashMap for character frequencies. Works for any Unicode characters, not just a-z.

### Code

```kotlin
fun isAnagramMap(s: String, t: String): Boolean {
    if (s.length != t.length) return false
    val map = HashMap<Char, Int>()
    for (c in s) map[c] = map.getOrDefault(c, 0) + 1
    for (c in t) {
        val count = map.getOrDefault(c, 0) - 1
        if (count < 0) return false
        map[c] = count
    }
    return true
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(K) | K = unique characters |

---

## 📊 Method Comparison

| Method | Time | Space | When to Use |
|--------|------|-------|-------------|
| Sort | O(N log N) | O(N) | Quick to code |
| Frequency Count | O(N) | O(1) | Lowercase a-z only |
| HashMap | O(N) | O(K) | Unicode, any characters |

> **Interview Tip:** Start with the frequency count approach — it's O(N) time and O(1) space. Mention the sort approach as a simpler alternative. If asked about Unicode, switch to HashMap. Follow-up: Group Anagrams (LeetCode #49) uses the sorted string as a key.
