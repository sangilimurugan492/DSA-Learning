# Permutation in String — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/permutation-in-string/  
> **Topic:** Array / Sliding Window  
> **Difficulty:** Medium  
> **FAANG Importance:** ⭐⭐⭐⭐⭐

---

## 📋 Problem Statement

Given two strings `s1` and `s2`, return `true` if `s2` contains a permutation of `s1`, or `false` otherwise.

In other words, return `true` if one of `s1`'s permutations is a substring of `s2`.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `s1 = "ab"`, `s2 = "eidbaooo"` | `true` | `"ba"` is a permutation of `"ab"` and appears at index 3 |
| `s1 = "ab"`, `s2 = "eidboaoo"` | `false` | No substring of `s2` is a permutation of `s1` |

---

## 🧠 Intuition

A **permutation** of `s1` is any string with the **exact same character frequencies** as `s1`. So the problem reduces to:

> Does `s2` contain any substring of length `len(s1)` whose character frequency matches `s1`'s frequency?

This is a classic **fixed-size sliding window** problem:
1. Build a frequency count of `s1`.
2. Slide a window of size `len(s1)` across `s2`.
3. At each position, compare the window's frequency count with `s1`'s frequency count.
4. If they match → return `true`.

---

## 🧩 Method 1: Brute Force

### Core Idea

Check **every substring** of `s2` with length equal to `s1`. Sort each substring and compare with the sorted `s1`. If they match, it's a permutation.

### Code

```kotlin
fun checkInclusionBruteForce(s1: String, s2: String): Boolean {
    if (s2.length < s1.length) return false
    val sortedS1 = s1.toCharArray().sorted().joinToString("")

    for (i in 0..s2.length - s1.length) {
        val sub = s2.substring(i, i + s1.length).toCharArray().sorted().joinToString("")
        if (sub == sortedS1) return true
    }
    return false
}
```

### Dry Run: `s1 = "ab"`, `s2 = "eidbaooo"`

```
sortedS1 = "ab"
Window size = 2

i=0: substring = "ei" → sorted = "ei" ≠ "ab" ❌
i=1: substring = "id" → sorted = "di" ≠ "ab" ❌
i=2: substring = "db" → sorted = "bd" ≠ "ab" ❌
i=3: substring = "ba" → sorted = "ab" == "ab" ✅ → return true!
```

### Dry Run: `s1 = "ab"`, `s2 = "eidboaoo"`

```
sortedS1 = "ab"
Window size = 2

i=0: substring = "ei" → sorted = "ei" ≠ "ab" ❌
i=1: substring = "id" → sorted = "di" ≠ "ab" ❌
i=2: substring = "db" → sorted = "bd" ≠ "ab" ❌
i=3: substring = "bo" → sorted = "bo" ≠ "ab" ❌
i=4: substring = "oa" → sorted = "ao" ≠ "ab" ❌
i=5: substring = "ao" → sorted = "ao" ≠ "ab" ❌
i=6: substring = "oo" → sorted = "oo" ≠ "ab" ❌

No match → return false
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N × M log M) — N = len(s2), M = len(s1); sorting each substring |
| **Space** | O(M) — for sorted strings |

---

## 🧩 Method 2: Optimal — Sliding Window with Frequency Array

### Core Idea

Use a **fixed-size sliding window** of length `len(s1)`. Maintain a frequency count of the current window in `s2`. As the window slides one character at a time:
- **Add** the new character entering the window (on the right).
- **Remove** the character leaving the window (on the left).
- **Compare** the window's frequency array with `s1`'s frequency array.

This avoids re-sorting or re-counting from scratch — each step is O(1) update + O(26) comparison.

### Code

```kotlin
fun checkInclusionSlidingWindow(s1: String, s2: String): Boolean {
    if (s2.length < s1.length) return false
    val s1Count = IntArray(26)
    val s2Count = IntArray(26)

    // Build frequency count of s1
    for (c in s1) s1Count[c - 'a']++

    for (i in s2.indices) {
        // Add current character to window
        s2Count[s2[i] - 'a']++
        // Remove character that's now out of the window
        if (i >= s1.length) s2Count[s2[i - s1.length] - 'a']--
        // Check if window matches s1's frequency
        if (s2Count.contentEquals(s1Count)) return true
    }
    return false
}
```

### How the Window Works

```
s1 = "ab" (length = 2)

s2 = "e i d b a o o o"
      0 1 2 3 4 5 6 7

Window slides across s2:
  i=0: [e]         → window not full yet (size 1 < 2)
  i=1: [e i]       → window full (size 2), compare
  i=2: [i d]       → slide: remove 'e', add 'd'
  i=3: [d b]       → slide: remove 'i', add 'b'
  i=4: [b a]       → slide: remove 'd', add 'a' → MATCH! ✅
```

### Dry Run: `s1 = "ab"`, `s2 = "eidbaooo"`

```
s1Count = [a:1, b:1, rest:0]
s2Count = [all 0]
s1.length = 2

i=0, char='e':
  s2Count[e]++ → s2Count = [e:1]
  i >= 2? No → don't remove
  s2Count == s1Count? [e:1] vs [a:1,b:1] ❌

i=1, char='i':
  s2Count[i]++ → s2Count = [e:1, i:1]
  i >= 2? No → don't remove
  s2Count == s1Count? [e:1,i:1] vs [a:1,b:1] ❌

i=2, char='d':
  s2Count[d]++ → s2Count = [e:1, i:1, d:1]
  i >= 2? Yes → remove s2[2-2]=s2[0]='e' → s2Count[e]-- → s2Count = [i:1, d:1]
  s2Count == s1Count? [i:1,d:1] vs [a:1,b:1] ❌

i=3, char='b':
  s2Count[b]++ → s2Count = [i:1, d:1, b:1]
  i >= 2? Yes → remove s2[3-2]=s2[1]='i' → s2Count[i]-- → s2Count = [d:1, b:1]
  s2Count == s1Count? [d:1,b:1] vs [a:1,b:1] ❌

i=4, char='a':
  s2Count[a]++ → s2Count = [d:1, b:1, a:1]
  i >= 2? Yes → remove s2[4-2]=s2[2]='d' → s2Count[d]-- → s2Count = [b:1, a:1]
  s2Count == s1Count? [a:1,b:1] vs [a:1,b:1] ✅ → return true!
```

### Dry Run: `s1 = "ab"`, `s2 = "eidboaoo"`

```
s1Count = [a:1, b:1]
s1.length = 2

i=0, char='e':  s2Count=[e:1]                    → i<2, no remove → ❌
i=1, char='i':  s2Count=[e:1,i:1]                → i<2, no remove → ❌
i=2, char='d':  add 'd', remove 'e' → [i:1,d:1]  → ❌
i=3, char='b':  add 'b', remove 'i' → [d:1,b:1]  → ❌
i=4, char='o':  add 'o', remove 'd' → [b:1,o:1]  → ❌
i=5, char='a':  add 'a', remove 'b' → [o:1,a:1]  → ❌
i=6, char='o':  add 'o', remove 'o' → [o:1,a:1]  → ❌
i=7, char='o':  add 'o', remove 'a' → [o:2]      → ❌

No match → return false
```

---

## ⏱ Complexity Analysis

| Metric | Brute Force | Sliding Window |
|--------|-------------|----------------|
| **Time** | O(N × M log M) | O(N × 26) = O(N) |
| **Space** | O(M) | O(1) (two arrays of size 26) |

- **Sliding Window is O(N):** Each character is added once and removed once. The `contentEquals` comparison is O(26) = O(1).
- **Space is O(1):** Two fixed-size `IntArray(26)` — one for `s1`, one for the sliding window.

---

## 🔑 Key Takeaways

1. **Permutation = same character frequencies.** The problem is really about frequency matching.
2. **Fixed-size sliding window** — the window size is always `len(s1)`.
3. **Add on the right, remove on the left** — O(1) updates per step.
4. The `if (i >= s1.length)` guard ensures we only start removing **after** the window is full.
5. This is the **same pattern** as "Find All Anagrams in a String" (LeetCode 438).

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Permutation in String | [Link](https://leetcode.com/problems/permutation-in-string/) | Medium |
| Find All Anagrams in a String | [Link](https://leetcode.com/problems/find-all-anagrams-in-a-string/) | Medium |
| Minimum Window Substring | [Link](https://leetcode.com/problems/minimum-window-substring/) | Hard |
| Longest Substring Without Repeating Characters | [Link](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium |
