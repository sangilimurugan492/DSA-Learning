# Valid Parentheses — Detailed Explanation

> **LeetCode #20** | [Problem Link](https://leetcode.com/problems/valid-parentheses/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Most asked stack problem)  
> **Topic:** Stack, String

---

## 📋 Problem Statement

Given a string containing `()`, `{}`, `[]`, determine if it's valid:
- Open brackets must be closed by the same type.
- Open brackets must be closed in the correct order.
- Every close bracket has a matching open bracket.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `"()"` | true | Simple match |
| `"()[]{}"` | true | All matched |
| `"(]"` | false | Type mismatch |
| `"["` | false | Unclosed |

---

## 🧩 Method 1: Stack with HashMap — O(N)

### Core Idea

Map open→close. Push open brackets. On close bracket, pop and check if it matches.

### Key Insight

> Stack naturally handles nesting — the most recent open bracket must be closed first (LIFO).

### Dry Run — `"()[]{}"`

| char | Action | Stack |
|:----:|:------:|:-----:|
| `(` | push | `(` |
| `)` | pop `(`, map['(']=')' == ')' ✅ | empty |
| `[` | push | `[` |
| `]` | pop `[`, map['[']=']' == ']' ✅ | empty |
| `{` | push | `{` |
| `}` | pop `{`, map['{']='}' == '}' ✅ | empty |

✅ **Result: true** (stack empty)

### Code

```kotlin
fun isValid(s: String): Boolean {
    val map = mapOf('(' to ')', '{' to '}', '[' to ']')
    val stack = java.util.Stack<Char>()

    for (c in s) {
        if (map.containsKey(c)) stack.push(c)
        else if (stack.isEmpty() || map[stack.pop()] != c) return false
    }
    return stack.isEmpty()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | Stack |

---

## 🧩 Method 2: CharArray as Stack — O(N)

### Core Idea

Use a char array with a `top` pointer instead of a Stack object. Avoids overhead.

### Code

```kotlin
fun isValidOP(s: String): Boolean {
    val stack = CharArray(s.length)
    var top = -1

    for (c in s) {
        when (c) {
            '(', '{', '[' -> stack[++top] = c
            ')' -> if (top < 0 || stack[top--] != '(') return false
            '}' -> if (top < 0 || stack[top--] != '{') return false
            ']' -> if (top < 0 || stack[top--] != '[') return false
        }
    }
    return top == -1
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | Char array |

---

## 📊 Comparison Table

| Aspect | Stack + HashMap | CharArray |
|--------|----------------|-----------|
| **Time** | O(N) | O(N) |
| **Space** | O(N) | O(N) |
| **Readability** | High | Medium |
| **Performance** | Good | Slightly faster |
| **Interview preference** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **LIFO for nesting:** Stack naturally handles nested brackets — most recent open must close first.
2. **Empty stack check:** If close bracket arrives and stack is empty → no matching open → invalid.
3. **Final check:** After processing all chars, stack must be empty (no unclosed opens).
4. **Pattern:** Stack for matching — extends to Longest Valid Parentheses, Remove Invalid Parentheses.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Valid Parentheses | [#20](https://leetcode.com/problems/valid-parentheses/) | Easy |
| Longest Valid Parentheses | [#32](https://leetcode.com/problems/longest-valid-parentheses/) | Hard |
| Remove Invalid Parentheses | [#301](https://leetcode.com/problems/remove-invalid-parentheses/) | Hard |
| Minimum Remove to Make Valid | [#1249](https://leetcode.com/problems/minimum-remove-to-make-valid-parentheses/) | Medium |
