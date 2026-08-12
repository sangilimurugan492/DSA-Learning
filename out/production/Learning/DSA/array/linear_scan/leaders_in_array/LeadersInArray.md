# Leaders in an Array — Detailed Explanation

> **GeeksforGeeks** | https://www.geeksforgeeks.org/dsa/leaders-in-an-array/  
> **Topic:** Array, Linear Scan (Right-to-Left)  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array `arr[]` of positive integers, an element is a **"leader"** if it is **greater
than or equal to all the elements to its right**. The rightmost element is always a leader
(since there are no elements to its right).

Return all leaders in the array in their **original order** (left to right).

### Constraints

- `1 <= arr.size <= 10^5`
- `1 <= arr[i] <= 10^6`

### Examples

**Example 1:**

```
Input:  arr = [16, 17, 4, 3, 5, 2]
Output: [17, 5, 2]

Element  Right-side elements     Leader?
  16     [17, 4, 3, 5, 2]        No  (17 > 16)
  17     [4, 3, 5, 2]            Yes (17 ≥ all)
  4      [3, 5, 2]               No  (5 > 4)
  3      [5, 2]                  No  (5 > 3)
  5      [2]                     Yes (5 ≥ 2)
  2      [] (rightmost)          Yes (always a leader)
```

**Example 2:**

```
Input:  arr = [1, 2, 3, 4, 5, 2]
Output: [5, 2]

Element  Right-side elements     Leader?
  1      [2, 3, 4, 5, 2]        No
  2      [3, 4, 5, 2]           No
  3      [4, 5, 2]              No
  4      [5, 2]                 No
  5      [2]                    Yes (5 ≥ 2)
  2      [] (rightmost)          Yes
```

---

## 🧩 Method 1: Brute Force — Nested Loops

### Core Idea

For each element `arr[i]`, scan all elements to its right (`arr[i+1..n-1]`):
- If any element to the right is **larger** than `arr[i]`, then `arr[i]` is **not a leader**.
- If we reach the end without finding a larger element, `arr[i]` **is a leader**.

### Step-by-step Walkthrough (Example 1)

```
arr = [16, 17, 4, 3, 5, 2]

i=0 (16): scan right [17, 4, 3, 5, 2] → 17 > 16 → break → NOT a leader
i=1 (17): scan right [4, 3, 5, 2]     → none > 17  → reached end → LEADER ✓
i=2 (4):  scan right [3, 5, 2]       → 5 > 4  → break → NOT a leader
i=3 (3):  scan right [5, 2]           → 5 > 3  → break → NOT a leader
i=4 (5):  scan right [2]              → none > 5  → reached end → LEADER ✓
i=5 (2):  scan right []               → reached end → LEADER ✓ (rightmost)

Result = [17, 5, 2]
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — for each of N elements, scan up to N elements to the right |
| **Space** | O(1) — excluding the result array (no extra data structures) |

---

## 🧩 Method 2: Optimal — Right-to-Left Scan with Running Maximum

### Core Idea

The key insight is that an element is a leader if and only if it is **>= the maximum of
all elements to its right**. Instead of scanning all right-side elements for each position,
we can maintain a **running maximum** (`maxRight`) as we scan **from right to left**.

- If `arr[i] >= maxRight`, then `arr[i]` is a leader, and it becomes the new `maxRight`.
- If `arr[i] < maxRight`, then `arr[i]` is not a leader (a larger element exists to its right).

Since we scan right-to-left, leaders are collected in **reverse order**. We reverse the
result at the end to restore left-to-right order.

### Algorithm Steps

1. Initialize `maxRight = arr[n-1]` (rightmost element). Add it to the result (always a leader).
2. Scan from `i = n-2` down to `0`:
   - If `arr[i] >= maxRight` → it's a leader: add to result, update `maxRight = arr[i]`.
   - Otherwise → skip.
3. Reverse the result list to restore original left-to-right order.

### Step-by-step Walkthrough (Example 1)

```
arr = [16, 17, 4, 3, 5, 2]
```

**Initialization:**

```
maxRight = arr[5] = 2  (rightmost element is always a leader)
result = [2]
```

**Scan right to left:**

| i | arr[i] | maxRight (before) | arr[i] >= maxRight? | Leader? | maxRight (after) | result (after) |
|---|--------|-------------------|----------------------|---------|-------------------|-----------------|
| 4 | 5      | 2                 | 5 >= 2 → Yes         | ✅ Yes  | 5                 | [2, 5]          |
| 3 | 3      | 5                 | 3 >= 5 → No          | ❌ No   | 5                 | [2, 5]          |
| 2 | 4      | 5                 | 4 >= 5 → No          | ❌ No   | 5                 | [2, 5]          |
| 1 | 17     | 5                 | 17 >= 5 → Yes        | ✅ Yes  | 17                | [2, 5, 17]      |
| 0 | 16     | 17                | 16 >= 17 → No        | ❌ No   | 17                | [2, 5, 17]      |

**Reverse result:**

```
[2, 5, 17] → reversed → [17, 5, 2]
```

**Result = [17, 5, 2]** ✅

### Why does this work?

- `maxRight` always holds the maximum value among all elements to the right of the current
  position.
- If `arr[i] >= maxRight`, then `arr[i]` is >= every element to its right → it's a leader.
- After processing `arr[i]`, we update `maxRight` so it includes `arr[i]` for the next
  (further left) position.
- The right-to-left direction is essential — it lets us build `maxRight` incrementally in
  a single pass.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — single right-to-left pass + O(N) reverse = O(N) total |
| **Space** | O(1) — only a `maxRight` variable (excluding the result array) |

### Why is this better than brute force?

| Aspect | Brute Force (Nested Loops) | Optimal (Right-to-Left + Running Max) |
|--------|---------------------------|---------------------------------------|
| Time | O(N²) | O(N) |
| Space | O(1) | O(1) |
| Key idea | Scan all right elements for each position | Track running max from right |

---

## 🔑 Key Takeaways

1. **Right-to-left scanning** is a powerful technique when a property depends on "all elements
   to the right" — maintain a running aggregate (here, the maximum).
2. The rightmost element is **always a leader** — this gives us the initial `maxRight`.
3. Using `>=` (not `>`) ensures that duplicate values at the right edge are also leaders.
4. Leaders are collected in reverse order during the scan — don't forget to **reverse** the
   result to restore original order.
5. Time is O(N) with O(1) extra space — optimal for this problem.

---

## 📚 Related Problems

| Problem | Link | Difficulty |
|---------|------|------------|
| Leaders in an Array | [GeeksforGeeks](https://www.geeksforgeeks.org/dsa/leaders-in-an-array/) | Medium |
| Replace Elements with Greatest Element on Right Side | [LeetCode](https://leetcode.com/problems/replace-elements-with-greatest-element-on-right-side/) | Easy |
| Next Greater Element I | [LeetCode](https://leetcode.com/problems/next-greater-element-i/) | Easy |
| Daily Temperatures | [LeetCode](https://leetcode.com/problems/daily-temperatures/) | Medium |
