# Partition Labels — Detailed Explanation

> **LeetCode #763** | [Problem Link](https://leetcode.com/problems/partition-labels/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)  
> **Topic:** Greedy, String

---

## 📋 Problem Statement

Partition a string into as many parts as possible so each letter appears in at most one part. Return partition sizes.

### Examples

| s | Output | Explanation |
|---|--------|-------------|
| `"ababcbacadefegdehijhklij"` | [9,7,8] | "ababcbaca" \| "defegde" \| "hijhklij" |
| `"eccbbbbdec"` | [10] | All chars in one partition |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

For each partition, keep expanding until all characters in it don't appear later.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | For each char, scan rest of string |
| **Space** | O(1) | — |

---

## 🧩 Method 2: Greedy with Last Index — O(N)

### Core Idea

First pass: record last occurrence of each character. Second pass: extend partition end to max last occurrence. Close when `i == end`.

### Key Insight

> A partition must contain ALL occurrences of every character in it. So the partition end = max(last occurrence of any char seen so far). When current index reaches end → all chars are contained → close partition.

### Dry Run — `"ababcbacadefegdehijhklij"`

Last occurrences: a=8, b=5, c=7, d=14, e=15, f=11, g=13, h=19, i=22, j=23, k=20, l=21

| i | char | lastIndex | end | i==end? | Partition |
|:-:|:----:|:---------:|:---:|:-------:|:---------:|
| 0 | a | 8 | 8 | No | — |
| 1 | b | 5 | 8 | No | — |
| ... | ... | ... | 8 | No | — |
| 8 | a | 8 | 8 | Yes ✅ | [9] |
| 9 | d | 14 | 14 | No | — |
| ... | ... | ... | 15 | No | — |
| 15 | e | 15 | 15 | Yes ✅ | [9,7] |
| 16 | h | 19 | 19 | No | — |
| ... | ... | ... | 23 | No | — |
| 23 | j | 23 | 23 | Yes ✅ | [9,7,8] |

✅ **Result: [9, 7, 8]**

### Code

```kotlin
fun partitionLabels(s: String): List<Int> {
    val lastIndex = IntArray(26)
    for (i in s.indices) lastIndex[s[i] - 'a'] = i

    val result = mutableListOf<Int>()
    var start = 0; var end = 0
    for (i in s.indices) {
        end = maxOf(end, lastIndex[s[i] - 'a'])
        if (i == end) { result.add(end - start + 1); start = i + 1 }
    }
    return result
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Two passes |
| **Space** | O(1) | 26 letters |

---

## 📊 Comparison Table

| Aspect | Brute Force | Greedy with Last Index |
|--------|-------------|------------------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(1) | O(1) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Last occurrence map:** Precompute where each character last appears.
2. **Extend partition:** `end = max(end, lastIndex[char])` — partition must contain all occurrences.
3. **Close condition:** When `i == end`, all characters in the partition are fully contained.
4. **Pattern:** Greedy interval — extends to Merge Strings, String Compression.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Partition Labels | [#763](https://leetcode.com/problems/partition-labels/) | Medium |
| Merge Intervals | [#56](https://leetcode.com/problems/merge-intervals/) | Medium |
| Jump Game | [#55](https://leetcode.com/problems/jump-game/) | Medium |
| String Compression | [#443](https://leetcode.com/problems/string-compression/) | Medium |
