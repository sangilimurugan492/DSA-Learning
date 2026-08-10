# Boats to Save People — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/boats-to-save-people/description/  
> **Topic:** Two Pointers / Greedy  
> **Difficulty:** Medium

---

## 📋 Problem Statement

You are given an array `people` where `people[i]` is the weight of the ith person, and an infinite number of boats where each boat can carry a maximum weight of `limit`. Each boat carries **at most two people** at the same time, provided the sum of their weights is at most `limit`.

Return the **minimum number of boats** to carry every given person.

### Examples

| Input | Output | Explanation |
|-------|--------|-------------|
| `people = [1,2]`, `limit = 3` | `1` | 1 boat: (1, 2) |
| `people = [3,2,2,1]`, `limit = 3` | `3` | 3 boats: (1, 2), (2), (3) |
| `people = [3,5,3,4]`, `limit = 5` | `4` | 4 boats: (3), (3), (4), (5) |

---

## 🧩 Method 1: Brute Force (Recursive Exhaustive Search)

### Core Idea

Try **all possible pairings** using recursion. For each unboarded person, we explore two options:
1. **Send them alone** in a boat.
2. **Pair them** with every other unboarded person whose combined weight is within `limit`.

We recursively explore every option and return the minimum boat count.

### Algorithm Steps

1. Maintain a `boarded` boolean array to track who has been assigned a boat.
2. Find the first unboarded person.
3. **Option 1**: Mark them as boarded (alone), recurse with `remaining - 1`.
4. **Option 2**: For each other unboarded person, if their combined weight ≤ `limit`, mark both as boarded, recurse with `remaining - 2`.
5. Take the minimum across all options.
6. **Base case**: `remaining == 0` → all boarded, return 0.

### Walkthrough (`people = [1,2]`, `limit = 3`)

```
firstUnboarded = 0 (weight 1)
  Option 1: person 0 alone → recurse with person 1
    person 1 alone → boats = 2
  Option 2: pair (1, 2) → 1+2=3 <= 3 → recurse, remaining=0 → boats = 1
min(2, 1) = 1
Result: 1 boat
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(n · 2ⁿ) — exponential, each person can be paired or go alone |
| **Space** | O(n) — recursion stack + visited array |

---

## 🧩 Method 2: Two-Pointer Greedy (Optimal)

### Core Idea

The key insight is to **pair the heaviest person with the lightest** whenever possible — this greedy strategy minimizes boats:
1. **Sort** the array so we can access the lightest and heaviest remaining people.
2. Use two pointers: `i` (lightest, left) and `j` (heaviest, right).
3. If `people[i] + people[j] <= limit`, they can share a boat → move both pointers inward.
4. Otherwise, the heaviest person is too heavy to pair → they go alone → move `j` only.
5. Each iteration (whether paired or solo) uses exactly **one boat**.
6. Repeat until all people are assigned (`i > j`).

### Walkthrough (`people = [3,2,2,1]`, `limit = 3`)

```
Sorted: [1, 2, 2, 3]
  i=0, j=3: 1 + 3 = 4 > 3  → 3 goes alone     boats=1, j=2
  i=0, j=2: 1 + 2 = 3 <= 3 → (1, 2) paired    boats=2, i=1, j=1
  i=1, j=1: 2 + 2 = 4 > 3  → 2 goes alone      boats=3, j=0
  i=1 > j=0 → done
Result: 3 boats
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(n log n) — dominated by sorting |
| **Space** | O(log n) — sorting overhead (in-place) |

---

## 🔑 Key Takeaways

1. **Brute force** explores all possible pairings recursively — correct but exponential in time.
2. **Sorting + two pointers** reduces this to O(n log n) by making a greedy choice: the heaviest person either fits with the lightest or must go alone — no better pairing exists for them.
3. Each iteration in the two-pointer approach always represents exactly one boat, making the boat count straightforward.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Boats to Save People | [Link](https://leetcode.com/problems/boats-to-save-people/description/) | Medium |
| Assign Cookies | [Link](https://leetcode.com/problems/assign-cookies/) | Easy |
| Bag of Tokens | [Link](https://leetcode.com/problems/bag-of-tokens/) | Medium |
