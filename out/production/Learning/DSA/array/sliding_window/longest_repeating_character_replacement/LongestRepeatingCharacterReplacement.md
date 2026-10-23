# Longest Repeating Character Replacement — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/longest-repeating-character-replacement/  
> **Topic:** Array / Sliding Window  
> **Difficulty:** Medium  
> **FAANG Importance:** ⭐⭐⭐⭐⭐

---

## 📋 Problem Statement

You are given a string `s` and an integer `k`. You can choose any character of the string and change it to **any other uppercase English character**. You can perform this operation **at most `k` times**.

Return the length of the **longest substring** containing the same letter you can get after performing the above operations.

### Examples

| Input | k | Output | Explanation |
|-------|---|--------|-------------|
| `s = "AABABBA"` | 1 | 4 | Replace the one 'A' in the middle with 'B' → `"AABA"` or replace the 'B' with 'A' → `"ABBA"` |
| `s = "ABAB"` | 2 | 4 | Replace the two 'A's with 'B' (or vice versa) → entire string `"ABAB"` → `"BBBB"` or `"AAAA"` |

---

## 🧠 Intuition

The key insight is:

> In any window, the number of replacements needed = `windowLength - countOfMostFrequentChar`

If this value is `≤ k`, the window is **valid** — we can replace all the "minority" characters to match the majority character. If it exceeds `k`, the window is **invalid** and we must shrink it.

### The Magic Formula

```
replacementsNeeded = windowLength - maxFreq
if replacementsNeeded <= k  →  window is VALID
```

---

## 🧩 Method 1: Brute Force

### Core Idea

Check **every possible substring**. For each substring, track character frequencies and the max frequency. If `substringLength - maxFreq ≤ k`, it's a valid candidate.

### Code

```kotlin
fun characterReplacementBruteForce(s: String, k: Int): Int {
    var maxLen = 0
    for (i in s.indices) {
        val count = IntArray(26)
        var maxFreq = 0
        for (j in i until s.length) {
            count[s[j] - 'A']++
            maxFreq = maxOf(maxFreq, count[s[j] - 'A'])
            val windowLen = j - i + 1
            if (windowLen - maxFreq <= k) {
                maxLen = maxOf(maxLen, windowLen)
            }
        }
    }
    return maxLen
}
```

### Dry Run: `s = "ABAB"`, `k = 2`

```
i=0:  j=0 → "A"      count[A]=1, maxFreq=1, windowLen=1,  1-1=0 ≤ 2 ✅ maxLen=1
      j=1 → "AB"     count[A]=1,B=1, maxFreq=1, windowLen=2, 2-1=1 ≤ 2 ✅ maxLen=2
      j=2 → "ABA"    count[A]=2,B=1, maxFreq=2, windowLen=3, 3-2=1 ≤ 2 ✅ maxLen=3
      j=3 → "ABAB"   count[A]=2,B=2, maxFreq=2, windowLen=4, 4-2=2 ≤ 2 ✅ maxLen=4

i=1:  j=1 → "B"      count[B]=1, maxFreq=1, windowLen=1, 1-1=0 ≤ 2 ✅ maxLen=4
      j=2 → "BA"     count[B]=1,A=1, maxFreq=1, windowLen=2, 2-1=1 ≤ 2 ✅ maxLen=4
      j=3 → "BAB"    count[B]=2,A=1, maxFreq=2, windowLen=3, 3-2=1 ≤ 2 ✅ maxLen=4

i=2:  j=2 → "A"      maxFreq=1, windowLen=1, 0 ≤ 2 ✅ maxLen=4
      j=3 → "AB"     maxFreq=1, windowLen=2, 1 ≤ 2 ✅ maxLen=4

i=3:  j=3 → "B"      maxFreq=1, windowLen=1, 0 ≤ 2 ✅ maxLen=4

Result: 4
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(1) (fixed array of 26) |

---

## 🧩 Method 2: Optimal — Sliding Window

### Core Idea

Use a **sliding window** with two pointers (`left` and `right`). Expand `right` to grow the window. Track the **max frequency** of any character in the current window. If the window becomes invalid (`windowLen - maxFreq > k`), shrink from the `left`.

**Key optimization:** We **don't decrease `maxFreq`** when shrinking. Why? Because a smaller `maxFreq` only makes the validity check **stricter** (harder to satisfy). The window will never grow beyond the best `maxLen` we've already found unless we encounter a **new** character with an even higher frequency. So keeping `maxFreq` as-is is safe and still correct.

### Code

```kotlin
fun characterReplacementSlidingWindow(s: String, k: Int): Int {
    val count = IntArray(26)
    var maxFreq = 0
    var left = 0
    var maxLen = 0

    for (right in s.indices) {
        count[s[right] - 'A']++
        maxFreq = maxOf(maxFreq, count[s[right] - 'A'])

        // Shrink window if invalid
        while ((right - left + 1) - maxFreq > k) {
            count[s[left] - 'A']--
            left++
        }
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
```

### Step-by-step Walkthrough

```
Initialize: count=[0×26], maxFreq=0, left=0, maxLen=0
```

### Dry Run: `s = "AABABBA"`, `k = 1`

```
Step 1: right=0, char='A'
  count[A]++  → count[A]=1
  maxFreq = max(0, 1) = 1
  windowLen = 0-0+1 = 1
  1 - 1 = 0 ≤ 1? ✅ no shrink
  maxLen = max(0, 1) = 1
  Window: "A"          [A:1]  maxFreq=1

Step 2: right=1, char='A'
  count[A]++  → count[A]=2
  maxFreq = max(1, 2) = 2
  windowLen = 1-0+1 = 2
  2 - 2 = 0 ≤ 1? ✅ no shrink
  maxLen = max(1, 2) = 2
  Window: "AA"         [A:2]  maxFreq=2

Step 3: right=2, char='B'
  count[B]++  → count[B]=1
  maxFreq = max(2, 1) = 2
  windowLen = 2-0+1 = 3
  3 - 2 = 1 ≤ 1? ✅ no shrink
  maxLen = max(2, 3) = 3
  Window: "AAB"        [A:2, B:1]  maxFreq=2

Step 4: right=3, char='A'
  count[A]++  → count[A]=3
  maxFreq = max(2, 3) = 3
  windowLen = 3-0+1 = 4
  4 - 3 = 1 ≤ 1? ✅ no shrink
  maxLen = max(3, 4) = 4
  Window: "AABA"       [A:3, B:1]  maxFreq=3

Step 5: right=4, char='B'
  count[B]++  → count[B]=2
  maxFreq = max(3, 2) = 3
  windowLen = 4-0+1 = 5
  5 - 3 = 2 ≤ 1? ❌ SHRINK!
    → count[s[left]='A']-- → count[A]=2, left=1
    → windowLen = 4-1+1 = 4
    → 4 - 3 = 1 ≤ 1? ✅ stop shrinking
  maxLen = max(4, 4) = 4
  Window: "ABAB"       [A:2, B:2]  maxFreq=3 (stale, but OK!)

Step 6: right=5, char='B'
  count[B]++  → count[B]=3
  maxFreq = max(3, 3) = 3
  windowLen = 5-1+1 = 5
  5 - 3 = 2 ≤ 1? ❌ SHRINK!
    → count[s[left]='B']-- → count[B]=2, left=2
    → windowLen = 5-2+1 = 4
    → 4 - 3 = 1 ≤ 1? ✅ stop shrinking
  maxLen = max(4, 4) = 4
  Window: "BABB"       [A:1, B:3]  maxFreq=3

Step 7: right=6, char='A'
  count[A]++  → count[A]=2
  maxFreq = max(3, 2) = 3
  windowLen = 6-2+1 = 5
  5 - 3 = 2 ≤ 1? ❌ SHRINK!
    → count[s[left]='A']-- → count[A]=1, left=3
    → windowLen = 6-3+1 = 4
    → 4 - 3 = 1 ≤ 1? ✅ stop shrinking
  maxLen = max(4, 4) = 4
  Window: "ABBA"       [A:1, B:3]  maxFreq=3

Result: 4 ✅
```

### Dry Run: `s = "ABAB"`, `k = 2`

```
Step 1: right=0, char='A'
  count[A]=1, maxFreq=1
  windowLen=1, 1-1=0 ≤ 2 ✅
  maxLen=1   Window: "A"

Step 2: right=1, char='B'
  count[B]=1, maxFreq=1
  windowLen=2, 2-1=1 ≤ 2 ✅
  maxLen=2   Window: "AB"

Step 3: right=2, char='A'
  count[A]=2, maxFreq=2
  windowLen=3, 3-2=1 ≤ 2 ✅
  maxLen=3   Window: "ABA"

Step 4: right=3, char='B'
  count[B]=2, maxFreq=2
  windowLen=4, 4-2=2 ≤ 2 ✅
  maxLen=4   Window: "ABAB"

Result: 4 ✅
```

---

## 🔑 Why We Don't Decrease `maxFreq` When Shrinking

This is the **most confusing part** of this problem. Here's the explanation:

1. `maxFreq` tracks the **highest frequency** of any single character we've seen so far.
2. When we shrink the window, the actual max frequency in the window **might** decrease (e.g., if we removed the most frequent character).
3. But we **intentionally don't update it**. Here's why:
   - A **stale (too-high) `maxFreq`** makes `windowLen - maxFreq` **smaller**, which makes the window appear **more valid** than it really is.
   - This means the window might not shrink when it "should" — but that's fine! The window will just **not grow** until a genuinely higher `maxFreq` appears.
   - The result `maxLen` only increases when we find a **genuinely larger valid window**, which requires a **new higher `maxFreq`**.
   - So the answer is still correct — we never overcount.

> **In short:** `maxFreq` being stale only means the window doesn't shrink as aggressively, but `maxLen` is never updated to an invalid value. The window size effectively "ratchets" — it only grows, never shrinks below the best found so far.

---

## ⏱ Complexity Analysis

| Metric | Brute Force | Sliding Window |
|--------|-------------|----------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(1) | O(1) |

- **Sliding Window is O(N):** Each character is visited at most twice (once by `right`, once by `left`).
- **Space is O(1):** The `count` array is always size 26 (uppercase English letters only).

---

## 🔑 Key Takeaways

1. **Core formula:** `windowLen - maxFreq ≤ k` determines window validity.
2. **Sliding window pattern:** Expand `right`, shrink `left` when invalid.
3. **Non-shrinking `maxFreq` trick** is a valid optimization — it avoids re-scanning the window for the true max frequency.
4. This is a classic **variable-size sliding window** problem.
5. The problem can be generalized to lowercase letters by using a larger frequency array or a `HashMap`.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Repeating Character Replacement | [Link](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium |
| Longest Substring Without Repeating Characters | [Link](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium |
| Max Consecutive Ones III | [Link](https://leetcode.com/problems/max-consecutive-ones-iii/) | Medium |
| Longest Substring with At Most K Distinct Characters | [Link](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/) | Medium |
| Longest Repeating Substring | [Link](https://leetcode.com/problems/longest-repeating-substring/) | Medium |
