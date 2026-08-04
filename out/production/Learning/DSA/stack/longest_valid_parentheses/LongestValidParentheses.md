# Longest Valid Parentheses — Detailed Explanation

> **LeetCode #32** | [Problem Link](https://leetcode.com/problems/longest-valid-parentheses/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Hard stack problem)  
> **Topic:** Stack, String

---

## 📋 Problem Statement

Given a string of `(` and `)`, find the length of the longest valid (well-formed) parentheses substring.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `"(()"` | 2 | `"()"` |
| `")()())"` | 4 | `"()()"` |
| `""` | 0 | Empty |

---

## 🧩 Method 1: Brute Force — O(N²)

### Core Idea

Check every substring. For each, count `(` as +1 and `)` as -1. If count reaches 0, it's valid. If count < 0, invalid.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N²) | Check all substrings |
| **Space** | O(1) | — |

---

## 🧩 Method 2: Stack with Index — O(N)

### Core Idea

Push indices of `(` onto stack. On `)`, pop and calculate length. Push -1 initially as base.

### Key Insight

> Stack stores the index of the **last unmatched character**. When we pop a `(` and the stack isn't empty, the distance from current index to the new top gives the length of a valid substring.

### Dry Run — `s = ")()())"`

| i | char | Action | Stack | maxLength |
|:-:|:----:|:------:|:-----:|:---------:|
| — | init | push -1 | [-1] | 0 |
| 0 | `)` | pop -1, empty → push 0 | [0] | 0 |
| 1 | `(` | push 1 | [0,1] | 0 |
| 2 | `)` | pop 1, length=2-0=2 | [0] | 2 |
| 3 | `(` | push 3 | [0,3] | 2 |
| 4 | `)` | pop 3, length=4-0=4 | [0] | 4 |
| 5 | `)` | pop 0, empty → push 5 | [5] | 4 |

✅ **Result: 4**

### Code

```kotlin
fun longestValidParentheses(s: String): Int {
    val stack = java.util.Stack<Int>()
    stack.push(-1)
    var maxLength = 0

    for (i in s.indices) {
        if (s[i] == '(') {
            stack.push(i)
        } else {
            stack.pop()
            if (stack.isEmpty()) stack.push(i)
            else maxLength = maxOf(maxLength, i - stack.peek())
        }
    }
    return maxLength
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | Stack |

---

## 📊 Comparison Table

| Aspect | Brute Force | Stack with Index |
|--------|-------------|------------------|
| **Time** | O(N²) | O(N) |
| **Space** | O(1) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Push -1 initially:** Serves as the base index for length calculation.
2. **On unmatched `)`:** Push its index as the new boundary (last unmatched position).
3. **On matched `)`:** Length = `i - stack.peek()` (distance from last unmatched to current).
4. **Pattern:** Stack with indices — extends to Valid Parentheses, Minimum Remove to Make Valid.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Longest Valid Parentheses | [#32](https://leetcode.com/problems/longest-valid-parentheses/) | Hard |
| Valid Parentheses | [#20](https://leetcode.com/problems/valid-parentheses/) | Easy |
| Minimum Remove to Make Valid | [#1249](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | Medium |
| Generate Parentheses | [#22](https://leetcode.com/problems/generate-parentheses/) | Medium |
