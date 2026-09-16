# ValidSudoku — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/valid-sudoku/  
> **Topic:** Array / HashSet  
> **Difficulty:** Medium  
> **FAANG Importance:** ⭐⭐⭐⭐ (Asked at Google, Amazon, Apple)

---

## 📋 Problem Statement

Determine if a `9 x 9` Sudoku board is valid. Only the filled cells need to be validated according to the following rules:

1. Each **row** must contain the digits `1-9` without repetition
2. Each **column** must contain the digits `1-9` without repetition
3. Each of the nine **3 x 3 sub-boxes** must contain the digits `1-9` without repetition

> **Note:** A valid board does not require the Sudoku to be solvable — only that there are no duplicate digits in any row, column, or box.

### Example

```
Input board:
[5,3,.,.,7,.,.,.,.]
[6,.,.,1,9,5,.,.,.]
[.,9,8,.,.,.,.,6,.]
[8,.,.,.,6,.,.,.,3]
[4,.,.,8,.,3,.,.,1]
[7,.,.,.,2,.,.,.,6]
[.,6,.,.,.,.,2,8,.]
[.,.,.,4,1,9,.,.,5]
[.,.,.,.,8,.,.,7,9]

Output: true
```

---

## 🧩 Method 1: Brute Force (Three Separate Passes)

### Core Idea

Validate the board in **three separate passes**:

1. **Pass 1:** Check each row for duplicates using a HashSet
2. **Pass 2:** Check each column for duplicates using a HashSet
3. **Pass 3:** Check each 3×3 sub-box for duplicates using a HashSet

Each pass creates a fresh HashSet per row/column/box and checks for duplicates.

### Visual Walkthrough

```
Pass 1 (Rows):          Pass 2 (Columns):       Pass 3 (Boxes):
Row 0: {5,3,7} ✓       Col 0: {5,6,8,4,7} ✓    Box 0: {5,3,6,9,8} ✓
Row 1: {6,1,9,5} ✓      Col 1: {3,9} ✓           Box 1: {7,1,9,5,6} ✓
...                     ...                      ...
(81 cells checked)      (81 cells checked)       (81 cells checked)
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — 3 passes × 81 cells = 243 checks (constant for 9×9) |
| **Space** | O(N) — one HashSet of up to 9 elements at a time |

### Drawback

Does 3 passes over the board instead of 1. Simple to understand but not the most efficient.

> **Implementation:** `isValidSudokuBruteForce()` in `ValidSudoku.kt`

---

## 🧩 Method 2: Optimal — Single Pass with HashSets

### Core Idea

Instead of 3 separate passes, do **everything in a single pass**. Maintain:

- **9 HashSets** for rows
- **9 HashSets** for columns
- **9 HashSets** for 3×3 boxes

For each filled cell `(i, j)` with digit `c`:
1. Compute the box index: `boxIndex = (i / 3) * 3 + (j / 3)`
2. Check if `c` already exists in `rows[i]`, `cols[j]`, or `boxes[boxIndex]`
3. If yes → invalid board, return `false`
4. Otherwise, add `c` to all three sets

### Box Index Mapping

```
Box index = (row / 3) * 3 + (col / 3)

  0  1  2
  3  4  5
  6  7  8

For cell (4, 7):
  boxIndex = (4 / 3) * 3 + (7 / 3) = 1 * 3 + 2 = 5
```

### Visual Walkthrough

```
Cell (0,0) = '5': rows[0]={5}, cols[0]={5}, boxes[0]={5}
Cell (0,1) = '3': rows[0]={5,3}, cols[1]={3}, boxes[0]={5,3}
Cell (0,4) = '7': rows[0]={5,3,7}, cols[4]={7}, boxes[1]={7}
...
Cell (1,0) = '6': rows[1]={6}, cols[0]={5,6}, boxes[0]={5,3,6}
Cell (1,3) = '1': rows[1]={6,1}, cols[3]={1}, boxes[1]={7,1}
...
→ No duplicates found → return true
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — single pass over all 81 cells |
| **Space** | O(N²) — 27 HashSets, each holding up to 9 elements |

> **Implementation:** `isValidSudoku()` in `ValidSudoku.kt`

---

## 🧩 Method 3: Optimal — Single Pass with Bitmasks

### Core Idea

Same single-pass logic as Method 2, but replace HashSets with **integer bitmasks** for O(1) space per row/column/box.

Each row, column, and box gets a single `Int` where **bit `d`** represents whether digit `d` has been seen:

- **Check:** `(mask and (1 shl d)) != 0` → digit already seen
- **Mark:** `mask = mask or (1 shl d)`

### Visual Walkthrough

```
Cell (0,0) = '5': digit=5, bit=0b100000
  rows[0]  = 0b100000
  cols[0]  = 0b100000
  boxes[0] = 0b100000

Cell (0,1) = '3': digit=3, bit=0b1000
  rows[0]  = 0b101000
  cols[1]  = 0b001000
  boxes[0] = 0b101000

Cell (1,0) = '6': digit=6, bit=0b1000000
  rows[1]  = 0b1000000
  cols[0]  = 0b1100000   (5 and 6)
  boxes[0] = 0b1101000   (5, 3, 6)
...
→ No bit collisions → return true
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — single pass over all 81 cells |
| **Space** | O(N) — 3 IntArrays of size 9 (27 ints total vs 27 HashSets) |

### Advantage over HashSet

- No hashing overhead — bitwise operations are faster
- Much lower memory — 3 `IntArray(9)` vs 27 `MutableSet<Char>`
- For a fixed 9×9 board, this is effectively O(1) space

> **Implementation:** `isValidSudokuBitmask()` in `ValidSudoku.kt`

---

## 📊 Comparison of Approaches

| Approach | Time | Space | Passes | Notes |
|----------|------|-------|--------|-------|
| Brute Force (3 passes) | O(N²) | O(N) | 3 | Simplest, but redundant traversal |
| HashSet (single pass) | O(N²) | O(N²) | 1 | Clean, idiomatic Kotlin |
| Bitmask (single pass) | O(N²) | O(N) | 1 | Most efficient, no hashing overhead |

> **Note:** For a fixed 9×9 board, all complexities are effectively O(1) since N=9 is constant. The analysis above uses N to show how the approaches scale.

---

## 🔑 Key Takeaways

1. **Box index formula** `(row / 3) * 3 + (col / 3)` is the key insight — it maps any cell to its 3×3 box index (0–8).
2. **Single-pass approach** is optimal — check row, column, and box simultaneously in one traversal.
3. **Bitmasks** are a powerful technique for tracking presence of digits 1–9 using a single integer per row/col/box.
4. The board is **valid** (no duplicates) ≠ **solvable** (can be completed to a solution). This problem only checks validity.
5. For a 9×9 board, all approaches run in constant time — the algorithmic difference matters more for generalization.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Valid Sudoku | [https://leetcode.com/problems/valid-sudoku/](https://leetcode.com/problems/valid-sudoku/) | Medium |
| Sudoku Solver | [https://leetcode.com/problems/sudoku-solver/](https://leetcode.com/problems/sudoku-solver/) | Hard |
| Check if Every Row and Column Contains All Numbers | [https://leetcode.com/problems/check-if-every-row-and-column-contains-all-numbers/](https://leetcode.com/problems/check-if-every-row-and-column-contains-all-numbers/) | Easy |
| Matrix Diagonal Sum | [https://leetcode.com/problems/matrix-diagonal-sum/](https://leetcode.com/problems/matrix-diagonal-sum/) | Easy |
