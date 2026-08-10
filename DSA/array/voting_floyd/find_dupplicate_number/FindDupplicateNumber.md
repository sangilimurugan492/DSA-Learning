# Find the Duplicate Number — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/find-the-duplicate-number/  
> **Topic:** Array, Floyd's Cycle Detection (Tortoise and Hare)  
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given an array of integers `nums` containing `n + 1` integers where each integer is in the
range `[1, n]` inclusive. There is only **one repeated number** in `nums`. Return this
repeated number.

**Constraints:**
- You must solve the problem **without modifying the array**.
- You must use only **constant extra space** (O(1)).

### Constraints

- `1 <= n <= 10^5`
- `nums.length == n + 1`
- `1 <= nums[i] <= n`
- There is only one repeated number (but it could repeat more than once).

### Examples

**Example 1:**

```
Input:  nums = [1, 3, 4, 2, 2]
Output: 2
```

**Example 2:**

```
Input:  nums = [3, 1, 3, 4, 2]
Output: 3
```

---

## 🧩 Method 1: Brute Force — Compare All Pairs

### Core Idea

For each element, compare it with every other element. If a match is found, return it.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — compare every pair |
| **Space** | O(1) |

---

## 🧩 Method 2: Better — Sort and Check Adjacent

### Core Idea

Sort the array, then check adjacent elements. If two adjacent elements are equal, that's
the duplicate.

> ⚠️ This **modifies the array**, which violates the problem's constraint. Included for
> comparison.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N log N) — dominated by sorting |
| **Space** | O(1) or O(N) — depending on sort implementation |

---

## 🧩 Method 3: Optimal — Floyd's Cycle Detection (Tortoise and Hare)

### Core Idea

**Treat the array as a linked list** where index `i` "points to" `nums[i]`. Since values
are in `[1, n]` and there are `n+1` elements, this creates a linked list with a **cycle**.

**Why is there a cycle?** Two different indices point to the same value (the duplicate),
creating a cycle. The **entrance to the cycle** is the duplicate number.

Floyd's algorithm finds the cycle entrance in two phases:

1. **Phase 1 — Find meeting point**: Use two pointers (slow = 1 step, fast = 2 steps).
   They will meet inside the cycle.
2. **Phase 2 — Find cycle entrance**: Reset one pointer to the start. Move both at the
   same speed (1 step). Where they meet is the cycle entrance — the duplicate.

### Step-by-step Walkthrough (Example 1)

```
nums = [1, 3, 4, 2, 2]

Linked list representation (index → nums[index]):
  0 → 1 → 3 → 2 → 4 → 2 → 4 → 2 → ...  (cycle: 2 ↔ 4)
```

**Phase 1 — Find meeting point:**

| Step | slow (1 step) | fast (2 steps) |
|------|---------------|-----------------|
| Init | nums[0] = 1   | nums[nums[0]] = nums[1] = 3 |
| 1    | nums[1] = 3   | nums[nums[3]] = nums[2] = 4 |
| 2    | nums[3] = 2   | nums[nums[4]] = nums[2] = 4 |
| 3    | nums[2] = 4   | nums[nums[4]] = nums[2] = 4 → meet at 4! |

**Phase 2 — Find cycle entrance:**

Reset `slow` to start. Both move 1 step at a time.

| Step | slow | fast |
|------|------|------|
| Init | nums[0] = 1 | 4 (meeting point) |
| 1    | nums[1] = 3 | nums[4] = 2 |
| 2    | nums[3] = 2 | nums[2] = 4 |
| 3    | nums[2] = 4 | nums[4] = 2 |
| ...  | ... | ... |

> With the code variant `slow = nums[0]`, both pointers converge. The key insight is
> that the distance from start to cycle entrance equals the distance from meeting point
> to cycle entrance (modulo cycle length). So moving both at the same speed from their
> respective starting points makes them meet at the entrance.

**The duplicate is 2** ✅

### Why does this work?

- The array defines a function `f(x) = nums[x]` mapping indices to values.
- Since all values are in `[1, n]` and there are `n+1` elements, the mapping creates a
  cycle (pigeonhole principle guarantees a repeat).
- The cycle entrance is the duplicate because two different indices map to it.
- Floyd's algorithm finds the cycle entrance in O(N) time and O(1) space — without
  modifying the array.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N) — both phases are O(N) |
| **Space** | O(1) — only two pointers |

### Comparison of Methods

| Method | Time | Space | Modifies Array? | Meets Constraints? |
|--------|------|-------|------------------|---------------------|
| Brute Force | O(N²) | O(1) | No | ✅ (but slow) |
| Sort + Adjacent | O(N log N) | O(1) | **Yes** | ❌ |
| Floyd's | O(N) | O(1) | No | ✅ |

---

## 🔑 Key Takeaways

1. **Array as linked list**: When values are valid indices, `nums[i]` defines a pointer,
   turning the array into a linked list.
2. **Floyd's cycle detection** finds cycles in O(N) time, O(1) space — no modification
   needed.
3. The **cycle entrance** is the duplicate because two indices "point to" it.
4. Two phases: Phase 1 finds the meeting point (inside cycle), Phase 2 finds the entrance.
5. This is a classic FAANG interview problem — knowing the linked-list interpretation is key.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Find the Duplicate Number | [link](https://leetcode.com/problems/find-the-duplicate-number/) | Medium |
| Linked List Cycle II | [link](https://leetcode.com/problems/linked-list-cycle-ii/) | Medium |
| Missing Number | [link](https://leetcode.com/problems/missing-number/) | Easy |
| Find All Duplicates in an Array | [link](https://leetcode.com/problems/find-all-duplicates-in-an-array/) | Medium |
