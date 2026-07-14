# Word Break — Detailed Explanation

> **LeetCode #139** | [Problem Link](https://leetcode.com/problems/word-break/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic DP — string segmentation)  
> **Topic:** Dynamic Programming, String

---

## 📋 Problem Statement

Given a string `s` and a dictionary of words, determine if `s` can be segmented into a space-separated sequence of one or more dictionary words.

### Examples

| s | wordDict | Output |
|---|----------|--------|
| `"leetcode"` | `["leet","code"]` | true |
| `"applepenapple"` | `["apple","pen"]` | true |

---

## 🧩 Method 1: Brute Force — Recursion

### Core Idea

Try every possible prefix. If prefix is in dict, recurse on the suffix.

### Code

```kotlin
fun wordBreakBruteForce(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    return canBreak(s, 0, wordSet)
}

private fun canBreak(s: String, start: Int, wordSet: Set<String>): Boolean {
    if (start == s.length) return true
    for (end in start + 1..s.length) {
        if (s.substring(start, end) in wordSet && canBreak(s, end, wordSet)) {
            return true
        }
    }
    return false
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(2^N) | Exponential — overlapping subproblems |
| **Space** | O(N) | Recursion stack |

---

## 🧩 Method 2: Bottom-Up DP (Optimal)

### Core Idea

`dp[i]` = true if `s[0..i)` can be segmented. For each `i`, check all `j < i`: if `dp[j]` AND `s[j..i)` is in dict → `dp[i] = true`.

### Key Insight

> "Can the prefix `s[0..j)` be segmented AND is `s[j..i)` a word?" If both are true, then `s[0..i)` can be segmented.

### Dry Run — `s="leetcode", dict=["leet","code"]`

| i | j | s[j..i) | dp[j] | in dict? | dp[i] |
|:-:|:-:|:-------:|:-----:|:--------:|:-----:|
| 4 | 0 | "leet" | true | ✅ | true |
| 8 | 4 | "code" | true | ✅ | true |

✅ **Result: true**

### Code

```kotlin
fun wordBreakDP(s: String, wordDict: List<String>): Boolean {
    val wordSet = wordDict.toSet()
    val dp = BooleanArray(s.length + 1)
    dp[0] = true

    for (i in 1..s.length) {
        for (j in 0 until i) {
            if (dp[j] && s.substring(j, i) in wordSet) {
                dp[i] = true
                break
            }
        }
    }
    return dp[s.length]
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N² × M) | N = string length, M = substring cost |
| **Space** | O(N) | dp array |

---

## 📊 Comparison Table

| Aspect | Brute Force | Bottom-Up DP |
|--------|-------------|-------------|
| **Time** | O(2^N) | O(N² × M) |
| **Space** | O(N) | O(N) |
| **Approach** | Recursion | dp[i] = any(dp[j] && s[j..i) in dict) |
| **Optimality** | ❌ | ✅ |

---

## 🔑 Key Takeaways

1. **dp[i] = "can s[0..i) be segmented?"** — boolean DP, not count.
2. **Check all split points j:** For each i, try all j < i. If prefix is segmentable AND suffix is a word → dp[i] = true.
3. **HashSet for O(1) lookup:** Convert wordDict to Set for constant-time word lookup.
4. **Pattern:** String segmentation DP — extends to Word Break II, Concatenated Words.
5. **"Can I split here?"** — the key question for all segmentation problems.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Word Break | [#139](https://leetcode.com/problems/word-break/) | Medium |
| Word Break II | [#140](https://leetcode.com/problems/word-break-ii/) | Hard |
| Concatenated Words | [#472](https://leetcode.com/problems/concatenated-words/) | Hard |
