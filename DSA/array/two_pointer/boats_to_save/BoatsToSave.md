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

## 🧩 Method 1: Brute Force (Sort + Greedy Scan)

### Core Idea

**Sort** the array, then for each unboarded person (lightest first), scan from the **heaviest** unboarded person downward to find a partner that fits within `limit`. If no partner is found, the person goes alone.

This is simpler than recursion: we just iterate and greedily pair each person with the heaviest possible partner.

### Algorithm Steps

1. **Sort** the array in ascending order.
2. Maintain a `boarded` boolean array to track who has been assigned a boat.
3. For each person `i` (from lightest to heaviest):
   - If already boarded, skip.
   - Scan from the heaviest unboarded person `j` downward.
   - If `people[i] + people[j] <= limit`, mark both as boarded (paired).
   - If no partner found, mark only `i` as boarded (alone).
   - Increment boat count.
4. Return total boats.

### Walkthrough (`people = [3,2,2,1]`, `limit = 3`)

```
Sorted: [1, 2, 2, 3]

i=0 (weight 1): scan j=3(3)→1+3=4>3, j=2(2)→1+2=3≤3 → pair (1,2)  boats=1
i=1 (weight 2): scan j=3(3)→2+3=5>3, j=2 already boarded → alone    boats=2
i=2 (weight 2): already boarded → skip
i=3 (weight 3): scan j=2 already boarded → alone                     boats=3

Result: 3 boats ✅
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — for each person, scan remaining unboarded people |
| **Space** | O(N) — boarded array |


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

1. **Brute force (O(N²))** sorts then greedily scans for a partner — simple and intuitive, but slower than optimal.
2. **Sorting + two pointers (O(N log N))** improves on brute force by replacing the inner scan with a single two-pointer pass: the heaviest person either fits with the lightest or must go alone — no better pairing exists for them.
3. Each iteration in the two-pointer approach always represents exactly one boat, making the boat count straightforward.


---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Boats to Save People | [Link](https://leetcode.com/problems/boats-to-save-people/description/) | Medium |
| Assign Cookies | [Link](https://leetcode.com/problems/assign-cookies/) | Easy |
| Bag of Tokens | [Link](https://leetcode.com/problems/bag-of-tokens/) | Medium |
