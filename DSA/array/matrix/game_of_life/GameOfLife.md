# Game of Life — Detailed Explanation

> **LeetCode #289** | https://leetcode.com/problems/game-of-life/
> **Topic:** Array, Matrix, In-Place | **Difficulty:** 🟡 Medium
> **FAAG Importance:** ⭐⭐⭐⭐ (Asked at Google, Meta, Amazon)

---

## 📋 Problem Statement

Given an `m x n` board of cells (1 = live, 0 = dead), compute the next state simultaneously (all cells update at once):

| Current State | Live Neighbors | Next State | Rule |
|--------------|---------------|------------|------|
| Live (1) | < 2 | Dead (0) | Underpopulation |
| Live (1) | 2 or 3 | Live (1) | Survival |
| Live (1) | > 3 | Dead (0) | Overpopulation |
| Dead (0) | Exactly 3 | Live (1) | Reproduction |

**Example:**
```
Input:  [[0,1,0],     Output: [[0,0,0],
         [0,0,1],              [1,0,1],
         [1,1,1],              [0,1,1],
         [0,0,0]]              [0,1,0]]
```

---

## 🧩 Method 1: Brute Force (Extra Space)

### Core Idea

Make a **full copy** of the board. Read live neighbor counts from the copy (which preserves the original state), and write the next state directly to the original board.

### Approach

1. Copy the board → `copy[][]`
2. For each cell `(i, j)`: count live neighbors from `copy` (original state)
3. Apply the 4 rules and write the result to `board[i][j]`
4. Since we read from `copy` and write to `board`, there's no interference

### Code

```kotlin
fun gameOfLifeBruteForce(board: Array<IntArray>) {
    val m = board.size
    val n = board[0].size

    // Make a copy of the original board
    val copy = board.map { it.copyOf() }.toTypedArray()

    val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

    for (i in 0 until m) {
        for (j in 0 until n) {
            // Count live neighbors from the COPY (original state)
            var liveNeighbors = 0
            for ((di, dj) in dirs) {
                val ni = i + di
                val nj = j + dj
                if (ni in 0 until m && nj in 0 until n && copy[ni][nj] == 1) {
                    liveNeighbors++
                }
            }

            // Apply rules and write to the ORIGINAL board
            when {
                copy[i][j] == 1 && (liveNeighbors < 2 || liveNeighbors > 3) -> board[i][j] = 0  // Dies
                copy[i][j] == 0 && liveNeighbors == 3 -> board[i][j] = 1  // Becomes live
                // Otherwise, state stays the same
            }
        }
    }
}
```

### Complexity

| Metric | Value | Explanation |
|--------|-------|-------------|
| **Time** | O(M × N) | Each cell visited once, 8 neighbors checked per cell |
| **Space** | O(M × N) | Full copy of the board |

### Pros & Cons

| ✅ Pros | ❌ Cons |
|---------|---------|
| Simple and easy to understand | Uses O(M × N) extra space |
| No risk of reading corrupted state | Not truly in-place |

---

## 🧩 Method 2: Optimal (In-Place with Intermediate States)

### Core Idea

Use **intermediate values** to encode both the original and new state in a single cell. This lets us read the original state while writing the new state — all in-place with O(1) extra space.

### Encoding Scheme

| Transition | Intermediate Value | Meaning |
|-----------|-------------------|---------|
| 0 → 0 | 0 (unchanged) | Was dead, stays dead |
| 1 → 1 | 1 (unchanged) | Was live, stays live |
| 0 → 1 | **2** | Was dead, now live |
| 1 → 0 | **3** | Was live, now dead |

**Key rule for reading original state:**
- Original **live** = value is `1` or `3` (was live, regardless of new state)
- Original **dead** = value is `0` or `2` (was dead, regardless of new state)

### Approach

1. **First pass:** For each cell, count live neighbors (treating `1` and `3` as originally live). Mark transitions with intermediate values (`2` or `3`).
2. **Second pass:** Convert intermediate values back to final state (`2 → 1`, `3 → 0`).

### Code

```kotlin
fun gameOfLife(board: Array<IntArray>) {
    val m = board.size
    val n = board[0].size

    val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)

    // First pass: mark transitions with intermediate values
    for (i in 0 until m) {
        for (j in 0 until n) {
            var liveNeighbors = 0
            for ((di, dj) in dirs) {
                val ni = i + di
                val nj = j + dj
                if (ni in 0 until m && nj in 0 until n) {
                    // Original live = 1 or 3 (was live, now dead)
                    if (board[ni][nj] == 1 || board[ni][nj] == 3) liveNeighbors++
                }
            }

            when {
                board[i][j] == 1 && (liveNeighbors < 2 || liveNeighbors > 3) ->
                    board[i][j] = 3  // Live → Dead
                board[i][j] == 0 && liveNeighbors == 3 ->
                    board[i][j] = 2  // Dead → Live
            }
        }
    }

    // Second pass: convert intermediate values to final
    for (i in 0 until m) {
        for (j in 0 until n) {
            when (board[i][j]) {
                2 -> board[i][j] = 1
                3 -> board[i][j] = 0
            }
        }
    }
}
```

### Complexity

| Metric | Value | Explanation |
|--------|-------|-------------|
| **Time** | O(M × N) | Two passes over the board |
| **Space** | O(1) | No extra board, only a few variables |

### Pros & Cons

| ✅ Pros | ❌ Cons |
|---------|---------|
| True O(1) space — in-place | Slightly more complex logic |
| Two-pass approach is clean | Must remember the encoding scheme |

---

## 📊 Method Comparison

| Aspect | Brute Force | Optimal |
|--------|------------|---------|
| **Time** | O(M × N) | O(M × N) |
| **Space** | O(M × N) | O(1) |
| **Passes** | 1 | 2 |
| **In-Place** | ❌ No | ✅ Yes |
| **Difficulty** | Easy | Medium |

---

## 🔑 Key Takeaways

1. **The challenge** is that all cells must update simultaneously — writing one cell's new state shouldn't affect neighbor counts for other cells.
2. **Brute force** solves this by keeping a separate copy to read from.
3. **Optimal** solves this by encoding both old and new state in a single value using intermediate markers (`2` and `3`).
4. **General principle:** When you need to read original values while writing new ones in-place, consider encoding transitions with intermediate states.
5. The 8-neighbor check pattern (using a `dirs` array) is reusable across all matrix/grid problems.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty | Pattern |
|---------|----------|------------|---------|
| Set Matrix Zeroes | [#73](https://leetcode.com/problems/set-matrix-zeroes/) | 🟡 Medium | Matrix (In-Place Markers) |
| Matrix Reshaping | [#566](https://leetcode.com/problems/reshape-the-matrix/) | 🟢 Easy | Matrix |
| Island Count | [#200](https://leetcode.com/problems/number-of-islands/) | 🟡 Medium | Matrix (DFS/BFS) |
