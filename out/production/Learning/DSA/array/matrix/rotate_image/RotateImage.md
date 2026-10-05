# RotateImage — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/rotate-image/  
> **Topic:** Array / Matrix  
> **Difficulty:** Medium  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Asked at Amazon, Apple, Microsoft)

---

## 📋 Problem Statement

You are given an `n x n` 2D matrix representing an image. Rotate the image by **90 degrees clockwise** **in-place**.

### Examples

**Example 1:**
```
Input:  matrix = [[1,2,3],
                  [4,5,6],
                  [7,8,9]]
Output: [[7,4,1],
         [8,5,2],
         [9,6,3]]
```

**Example 2:**
```
Input:  matrix = [[5, 1, 9, 11],
                  [2, 4, 8, 10],
                  [13,3, 6, 7],
                  [15,14,12,16]]
Output: [[15,13,2, 5],
         [14,3, 4, 1],
         [12,6, 8, 9],
         [16,7, 10,11]]
```

---

## 🧩 Method 1: Brute Force (Auxiliary Matrix)

### Core Idea

Create a new `n x n` matrix. For every cell `(i, j)` in the original matrix, place its value at position `(j, n-1-i)` in the new matrix. Then copy the new matrix back into the original.

**Mapping formula:**
```
rotated[j][n - 1 - i] = matrix[i][j]
```

### Visual Walkthrough (3×3)

```
Original:          Rotated:
[1,2,3]            [7,4,1]
[4,5,6]    →       [8,5,2]
[7,8,9]            [9,6,3]

(0,0)=1 → (0,2)    (0,1)=2 → (1,2)
(0,2)=3 → (2,2)    (1,0)=4 → (0,1)
(2,0)=7 → (0,0)    (2,2)=9 → (2,0)
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) |
| **Space** | O(N²) — requires a full auxiliary matrix |

### Drawback

Uses O(N²) extra space, which violates the in-place requirement. Good as a starting point to understand the rotation mapping before optimizing.

> **Implementation:** `rotateBruteForce()` in `RotateImage.kt`

---

## 🧩 Method 2: Optimal — Transpose + Reverse Each Row

### Core Idea

A 90° clockwise rotation can be decomposed into two simpler in-place operations:

1. **Transpose** the matrix (swap `matrix[i][j]` with `matrix[j][i]`)
2. **Reverse each row**

### Visual Walkthrough (3×3)

**Step 1 — Transpose:**
```
[1,2,3]      [1,4,7]
[4,5,6]  →   [2,5,8]
[7,8,9]      [3,6,9]
```

**Step 2 — Reverse each row:**
```
[1,4,7]      [7,4,1]
[2,5,8]  →   [8,5,2]
[3,6,9]      [9,6,3]
```

### Why It Works

- **Transpose** mirrors along the main diagonal (converts rows to columns).
- **Reversing each row** flips horizontally, which combined with the transpose produces a 90° clockwise rotation.

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — every element visited twice (transpose + reverse) |
| **Space** | O(1) — all swaps in-place |

> **Implementation:** `rotateTransposeReverse()` in `RotateImage.kt`

---

## 🧩 Method 3: Optimal — Layer-by-Layer (4-Way Swap)

### Core Idea

Process the matrix in concentric layers (outer ring → inner ring). For each layer, rotate groups of 4 elements in a single cycle:

```
top-left → top-right → bottom-right → bottom-left → top-left
```

For a position `(i, j)` in a layer of size `n`:
```
(i, j) → (j, n-1-i) → (n-1-i, n-1-j) → (n-1-j, i) → (i, j)
```

### Visual Walkthrough (3×3)

```
Layer 0 (outer ring):
  1 → 3 → 9 → 7 → 1    (4-way swap of corners)
  2 → 6 → 8 → 4 → 2    (4-way swap of edges)

Layer 1 (center): 5 stays (n/2 = 1 layer only)

Result:
[7,4,1]
[8,5,2]
[9,6,3]
```

### Complexity

| Metric | Value |
|--------|-------|
| **Time** | O(N²) — every element rotated exactly once |
| **Space** | O(1) — only a single `temp` variable per 4-way swap |

> **Implementation:** `rotateLayerByLayer()` in `RotateImage.kt`

---

## 📊 Comparison of Approaches

| Approach | Time | Space | In-Place | Notes |
|----------|------|-------|----------|-------|
| Brute Force (Auxiliary Matrix) | O(N²) | O(N²) | ❌ | Simplest to understand |
| Transpose + Reverse Rows | O(N²) | O(1) | ✅ | Clean, easy to remember |
| Layer-by-Layer (4-way swap) | O(N²) | O(1) | ✅ | Single pass, classic interview solution |

---

## 🔑 Key Takeaways

1. **90° clockwise = Transpose + Reverse each row** — the easiest pattern to remember.
2. **90° counter-clockwise = Transpose + Reverse each column** (or Reverse rows + Transpose).
3. **Layer-by-layer** approach rotates each element exactly once — most efficient in practice.
4. All optimal approaches are **O(N²) time** and **O(1) space** — you cannot avoid visiting every cell.
5. The brute force mapping `rotated[j][n-1-i] = matrix[i][j]` is the foundation for understanding all approaches.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Rotate Image | [https://leetcode.com/problems/rotate-image/](https://leetcode.com/problems/rotate-image/) | Medium |
| Spiral Matrix | [https://leetcode.com/problems/spiral-matrix/](https://leetcode.com/problems/spiral-matrix/) | Medium |
| Spiral Matrix II | [https://leetcode.com/problems/spiral-matrix-ii/](https://leetcode.com/problems/spiral-matrix-ii/) | Medium |
| Set Matrix Zeroes | [https://leetcode.com/problems/set-matrix-zeroes/](https://leetcode.com/problems/set-matrix-zeroes/) | Medium |
| Matrix Diagonal Sum | [https://leetcode.com/problems/matrix-diagonal-sum/](https://leetcode.com/problems/matrix-diagonal-sum/) | Easy |
