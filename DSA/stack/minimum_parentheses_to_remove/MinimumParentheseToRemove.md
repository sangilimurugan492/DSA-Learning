# Minimum Remove to Make Valid Parentheses — Detailed Explanation

> **LeetCode #1249** | [Problem Link](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐  
> **Topic:** Stack, String

---

## 📋 Problem Statement

Given a string of `(`, `)` and lowercase characters, remove the minimum number of parentheses to make the string valid. Return any valid string.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `"lee(t(c)o)de)"` | `"lee(t(c)o)de"` | Remove last `)` |
| `"a)b(c)d"` | `"ab(c)d"` | Remove first `)` |
| `"))(("` | `""` | Remove all |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

First pass: remove extra `)` (when no matching `(`). Second pass: remove extra `(` from the right.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | String building with removeRange |
| **Space** | O(N) | StringBuilder |

---

## 🧩 Method 2: Stack with Marking — O(N)

### Core Idea

Use stack to track `(` indices. Mark unmatched `)` during pass, unmatched `(` after pass. Build result excluding marked indices.

### Key Insight

> Unmatched `)` are found **during** the pass (stack empty when `)` arrives). Unmatched `(` are found **after** the pass (still in stack).

### Dry Run — `s = "lee(t(c)o)de)"`

| i | char | Action | Stack | toRemove |
|:-:|:----:|:------:|:-----:|:--------:|
| 3 | `(` | push 3 | [3] | {} |
| 5 | `(` | push 5 | [3,5] | {} |
| 7 | `)` | pop 5 (matched) | [3] | {} |
| 9 | `)` | pop 3 (matched) | [] | {} |
| 12 | `)` | stack empty → mark 12 | [] | {12} |

After: stack empty, no unmatched `(`. Result: remove index 12 → `"lee(t(c)o)de"`

✅ **Result: `"lee(t(c)o)de"`**

### Code

```kotlin
fun minRemoveToMakeValid(s: String): String {
    val stack = ArrayDeque<Int>()
    val toRemove = mutableSetOf<Int>()

    for (i in s.indices) {
        when (s[i]) {
            '(' -> stack.addLast(i)
            ')' -> {
                if (stack.isNotEmpty()) stack.removeLast()
                else toRemove.add(i)
            }
        }
    }
    toRemove.addAll(stack)
    return s.filterIndexed { i, _ -> i !in toRemove }
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass + filter |
| **Space** | O(N) | Stack + set |

---

## 📊 Comparison Table

| Aspect | Brute Force | Stack with Marking |
|--------|-------------|---------------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Two types of unmatched:** Extra `)` (found during pass) and extra `(` (found after pass).
2. **Mark indices, then filter:** Use a set to mark indices for removal, then build result in one pass.
3. **Stack tracks `(` indices:** On `)`, pop if stack not empty (matched). If empty, mark `)` for removal.
4. **Pattern:** Stack for parenthesis matching — extends to Valid Parentheses, Longest Valid Parentheses.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Minimum Remove to Make Valid | [#1249](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | Medium |
| Valid Parentheses | [#20](https://leetcode.com/problems/valid-parentheses/) | Easy |
| Longest Valid Parentheses | [#32](https://leetcode.com/problems/longest-valid-parentheses/) | Hard |
| Remove Invalid Parentheses | [#301](https://leetcode.com/problems/remove-invalid-parentheses/) | Hard |
