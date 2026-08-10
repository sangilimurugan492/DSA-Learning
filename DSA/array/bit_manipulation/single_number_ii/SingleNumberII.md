# Single Number II — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/single-number-ii/  
> **Topic:** Bit Manipulation  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an integer array `nums` where every element appears **three times** except for one,
which appears **exactly once**. Find the single element and return it.

You must implement a solution with **O(n) time** and **O(1) space**.

### Constraints

- `1 <= nums.length <= 3 * 10^4`
- `-2^31 <= nums[i] <= 2^31 - 1`
- Each element appears exactly three times except for one element which appears once.

### Examples

```
Input:  nums = [2, 2, 3, 2]
Output: 3

Input:  nums = [0, 1, 0, 1, 0, 1, 99]
Output: 99
```

---

## 🧩 Method 1: Bit Counting (Intuitive)

### Core Idea

For each of the 32 bit positions, count how many numbers have that bit set. If the count
is not divisible by 3, that bit belongs to the single number.

### Step-by-step Walkthrough (nums = [2, 2, 3, 2])

```
2 = 10, 2 = 10, 3 = 11, 2 = 10

Bit 0 (LSB): appears in 3 (once) → count = 1, 1 % 3 = 1 → set bit 0
Bit 1: appears in 2, 2, 3, 2 (four times) → count = 4, 4 % 3 = 1 → set bit 1
Bits 2-31: count = 0 → not set

Result = binary 11 = 3 ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(32 * N) = O(N) |
| **Space** | O(1) |

---

## 🧩 Method 2: Two Counters (Ones & Twos) — Optimal

### Core Idea

Use two variables to track bit counts **mod 3**:
- `ones` = bits that have appeared **1 time (mod 3)**
- `twos` = bits that have appeared **2 times (mod 3)**

When a bit appears 3 times, it resets from `twos` back to 0 (mod 3 = 0).

**Update rules:**
```
ones = (ones ^ num) & ~twos   → toggle in ones, but only if not already in twos
twos = (twos ^ num) & ~ones   → toggle in twos, but only if not already in ones (after update)
```

After processing all numbers, `ones` holds the single number (appeared once, mod 3 = 1).

### Step-by-step Walkthrough (nums = [2, 2, 3, 2], binary: 2=10, 3=11)

```
ones=0, twos=0

num=2 (10):
  ones = (0^10) & ~00 = 10 & 11 = 10 (2)
  twos = (0^10) & ~10 = 10 & 01 = 00 (0)

num=2 (10):
  ones = (10^10) & ~00 = 00 & 11 = 00 (0)
  twos = (00^10) & ~00 = 10 & 11 = 10 (2)

num=3 (11):
  ones = (00^11) & ~10 = 11 & 01 = 01 (1)
  twos = (10^11) & ~01 = 01 & 10 = 00 (0)

num=2 (10):
  ones = (01^10) & ~00 = 11 & 11 = 11 (3)
  twos = (00^10) & ~11 = 10 & 00 = 00 (0)

Final: ones = 3 ✅ (the single number!)
```

**Why it works:** Bit `1` of 2 appears 3 times → cycles through ones→twos→reset. Bit `0`
of 3 appears once → stays in ones. Bit `1` of 3 appears once → stays in ones. Result: `11`
= 3.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single pass |
| **Space** | O(1) — only two variables |

### Comparison of Methods

| Method | Time | Space | Notes |
|--------|------|-------|-------|
| Bit Counting | O(N) | O(1) | Intuitive, easy to understand |
| Ones & Twos | O(N) | O(1) | Elegant, single pass, harder to derive |

---

## 🔑 Key Takeaways

1. **Modular bit counting**: count each bit position mod 3 — bits with count % 3 = 1 belong
   to the single number.
2. The **ones/twos** technique tracks bit counts mod 3 using only two variables — no array
   needed.
3. `ones = (ones ^ num) & ~twos` and `twos = (twos ^ num) & ~ones` are the key update rules.
4. This generalizes to "appears k times" problems — use k-1 counters.
5. This is a classic Google/Amazon/Meta interview question.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Single Number II | [link](https://leetcode.com/problems/single-number-ii/) | Medium |
| Single Number | [link](https://leetcode.com/problems/single-number/) | Easy |
| Single Number III | [link](https://leetcode.com/problems/single-number-iii/) | Medium |
| Counting Bits | [link](https://leetcode.com/problems/counting-bits/) | Easy |
