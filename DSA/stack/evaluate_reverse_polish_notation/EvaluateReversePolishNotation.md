# Evaluate Reverse Polish Notation — Detailed Explanation

> **LeetCode #150** | [Problem Link](https://leetcode.com/problems/evaluate-reverse-polish-notation/)  
> **FAANG Importance:** ⭐⭐⭐⭐ (Classic stack evaluation)  
> **Topic:** Stack

---

## 📋 Problem Statement

Evaluate the value of an arithmetic expression in Reverse Polish Notation (postfix).

### Examples

| tokens | Output | Infix |
|--------|--------|-------|
| `["2","1","+","3","*"]` | 9 | (2+1)*3 |
| `["4","13","5","/","+"]` | 6 | 4+(13/5) |

---

## 🧩 Method: Stack — O(N)

### Core Idea

Push numbers. On operator, pop two operands, compute, push result.

### Key Insight

> RPN is postfix notation — operands come before operators. When we see an operator, the two most recent numbers are the operands. Stack's LIFO gives us exactly this.

### Dry Run — `["2","1","+","3","*"]`

| Token | Action | Stack |
|:-----:|:------:|:-----:|
| "2" | push 2 | [2] |
| "1" | push 1 | [2, 1] |
| "+" | pop 1, pop 2, push 2+1=3 | [3] |
| "3" | push 3 | [3, 3] |
| "*" | pop 3, pop 3, push 3*3=9 | [9] |

✅ **Result: 9**

### Code

```kotlin
fun evalRPM(tokens: Array<String>): Int {
    val stack = ArrayDeque<Int>()
    for (token in tokens) {
        when (token) {
            "+" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a + b) }
            "-" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a - b) }
            "*" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a * b) }
            "/" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a / b) }
            else -> stack.addLast(token.toInt())
        }
    }
    return stack.last()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | Stack |

---

## 🔑 Key Takeaways

1. **Postfix notation:** Operands before operators — no parentheses needed.
2. **Stack model:** Push operands, pop on operator → LIFO gives correct order.
3. **Order matters:** For `-` and `/`, first pop = right operand, second pop = left.
4. **Pattern:** Stack evaluation — extends to Basic Calculator, Valid Parentheses.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Evaluate RPN | [#150](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | Medium |
| Basic Calculator | [#224](https://leetcode.com/problems/basic-calculator/) | Hard |
| Basic Calculator II | [#227](https://leetcode.com/problems/basic-calculator-ii/) | Medium |
| Valid Parentheses | [#20](https://leetcode.com/problems/valid-parentheses/) | Easy |
