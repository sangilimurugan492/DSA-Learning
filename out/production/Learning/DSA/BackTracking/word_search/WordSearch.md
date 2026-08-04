# Word Search — Detailed Explanation

> **LeetCode #79** | [Problem Link](https://leetcode.com/problems/word-search/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic Backtracking on Grid)  
> **Topic:** Backtracking, DFS, Matrix

---

## 📋 Problem Statement

Given an m×n grid of characters and a word, return true if the word exists in the grid. The word can be constructed from adjacent cells (horizontally/vertically). A cell may not be used more than once.

### Example

```
board = [["A","B","C","E"],
         ["S","F","C","S"],
         ["A","D","E","E"]]

word = "ABCCED" → true  (A→B→C→C→E→D)
word = "SEE"    → true  (S→E→E)
word = "ABCB"   → false (B already used)
```

---

## 🧩 Method 1: Brute Force DFS — O(N × M × 4^L)

### Core Idea

Try starting from each cell matching `word[0]`. DFS in 4 directions. Track visited cells in a HashSet.

### Code

```kotlin
fun existBruteForce(board: Array<CharArray>, word: String): Boolean {
    val rows = board.size; val cols = board[0].size

    fun dfs(r: Int, c: Int, index: Int, visited: MutableSet<Pair<Int, Int>>): Boolean {
        if (index == word.length) return true
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false
        if (board[r][c] != word[index]) return false
        if (Pair(r, c) in visited) return false

        visited.add(Pair(r, c))
        val found = dfs(r+1, c, index+1, visited) || dfs(r-1, c, index+1, visited) ||
                    dfs(r, c+1, index+1, visited) || dfs(r, c-1, index+1, visited)
        visited.remove(Pair(r, c))
        return found
    }

    for (i in 0 until rows)
        for (j in 0 until cols)
            if (dfs(i, j, 0, mutableSetOf())) return true
    return false
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × M × 4^L) | N×M cells, 4 directions, L = word length |
| **Space** | O(L) | Recursion depth + visited set |

---

## 🧩 Method 2: Backtracking with In-Place Marking — O(N × M × 4^L)

### Core Idea

Same DFS, but mark visited cells **in-place** by temporarily changing the character to `#`. Restore after backtracking. No HashSet needed.

### Key Insight

> In-place marking avoids HashSet overhead. Mark → explore 4 directions → restore. This is the universal grid backtracking pattern.

### Dry Run — `word = "ABCCED"`

```
[0,0]='A' matches word[0] → mark '#'
  [0,1]='B' matches word[1] → mark '#'
    [0,2]='C' matches word[2] → mark '#'
      [1,2]='C' matches word[3] → mark '#'
        [2,2]='E' matches word[4] → mark '#'
          [2,1]='D' matches word[5] → mark '#'
            index=6 == word.length → ✅ FOUND
```

✅ **Result: true**

### Code

```kotlin
fun existBacktrack(board: Array<CharArray>, word: String): Boolean {
    val rows = board.size; val cols = board[0].size

    fun backtrack(r: Int, c: Int, index: Int): Boolean {
        if (index == word.length) return true
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false
        if (board[r][c] != word[index]) return false

        val temp = board[r][c]
        board[r][c] = '#'  // Mark visited.

        val found = backtrack(r+1, c, index+1) || backtrack(r-1, c, index+1) ||
                    backtrack(r, c+1, index+1) || backtrack(r, c-1, index+1)

        board[r][c] = temp  // Restore.
        return found
    }

    for (i in 0 until rows)
        for (j in 0 until cols)
            if (backtrack(i, j, 0)) return true
    return false
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × M × 4^L) | N×M cells, 4 directions, L = word length |
| **Space** | O(L) | Recursion depth only (no visited set) |

---

## 📊 Comparison Table

| Aspect | Brute Force DFS | Backtracking (In-Place) |
|--------|-----------------|-------------------------|
| **Time** | O(N × M × 4^L) | O(N × M × 4^L) |
| **Space** | O(L) + visited set | O(L) only |
| **Visited tracking** | HashSet | In-place char marking |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **In-place marking:** Temporarily change `board[r][c]` to `#` to mark visited. Restore after exploring. No extra space for visited set.
2. **4 directions:** Up, down, left, right. No diagonals.
3. **Base cases:** Out of bounds → false. Char mismatch → false. Index == word.length → true.
4. **Start from every cell:** Try starting DFS from each cell that matches `word[0]`.
5. **Pattern:** Grid backtracking — extends to Number of Islands, Surrounded Regions, Pacific Atlantic Water Flow.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Word Search | [#79](https://leetcode.com/problems/word-search/) | Medium |
| Word Search II | [#212](https://leetcode.com/problems/word-search-ii/) | Hard |
| Number of Islands | [#200](https://leetcode.com/problems/number-of-islands/) | Medium |
| Surrounded Regions | [#130](https://leetcode.com/problems/surrounded-regions/) | Medium |
