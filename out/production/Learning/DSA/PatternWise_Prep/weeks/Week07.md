# 📅 Week 7 — Backtracking + Dynamic Programming

> **Dates:** Nov 30 (Mon) – Dec 6 (Sun)
> **Pattern Focus:** Backtracking (CHOOSE → EXPLORE → UNDO), 1D DP, 2D DP, Subsequence DP
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/BackTracking/`, `DSA/dp/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week07_Practice.md](../practice/Week07_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Nov 30 | #1–#6 (Easy DP + backtracking basics) | 2 hrs |
| Tue | Dec 1 | #7–#12 (1D DP) | 2 hrs |
| Wed | Dec 2 | #13–#17 (2D DP) | 2 hrs |
| Thu | Dec 3 | #18–#21 (Subsequence DP + backtracking) | 2 hrs |
| Sat | Dec 5 | #22–#23 (Hard) + revision | 2 hrs |
| Sun | Dec 6 | 🧪 Mock Test 7 + Review | 3 hrs |

---

## 🟢 MONDAY, Nov 30 — Easy DP + Backtracking Basics

### 1. Climbing Stairs — 🟢
**LeetCode #70** | Solution: `DSA/dp/one_d/ClimbingStairs.kt`

**Problem:** Climb 1 or 2 steps at a time. How many distinct ways to reach the top?

<details><summary>💡 Hint 1</summary>
Fibonacci-style DP: ways(n) = ways(n-1) + ways(n-2).
</details>

<details><summary>💡 Hint 2</summary>
Base: ways(1) = 1, ways(2) = 2. Build up iteratively with two variables.
</details>

<details><summary>💡 Hint 3</summary>
a = 1, b = 2; for i in 3..n: (a, b) = (b, a + b); return (n == 1) ? 1 : b.
</details>

```kotlin
fun climbStairs(n: Int): Int {
    // TODO: Fibonacci iteration
    return 0
}
```

---

### 2. Min Cost Climbing Stairs — 🟢
**LeetCode #746** | Solution: `DSA/dp/one_d/MinCostClimbingStairs.kt`

**Problem:** Start at index 0 or 1; each step costs cost[i]. Min cost to reach the top.

<details><summary>💡 Hint 1</summary>
dp[i] = min cost to stand at step i.
</details>

<details><summary>💡 Hint 2</summary>
dp[i] = cost[i] + min(dp[i-1], dp[i-2]). Answer = min(dp[n-1], dp[n-2]).
</details>

<details><summary>💡 Hint 3</summary>
Two rolling variables; dp0 = cost[0], dp1 = cost[1]; iterate from 2.
</details>

```kotlin
fun minCostClimbingStairs(cost: IntArray): Int {
    // TODO: Rolling min of last two
    return 0
}
```

---

### 3. Subsets — 🟡
**LeetCode #78** | Solution: `DSA/BackTracking/subsets/Subsets.kt`

**Problem:** Return all subsets (power set) of distinct integers.

<details><summary>💡 Hint 1</summary>
Backtracking — CHOOSE → EXPLORE → UNDO, or iterative building.
</details>

<details><summary>💡 Hint 2</summary>
DFS(index): add current path to results; for i in index until n: add nums[i], recurse(i+1), remove nums[i].
</details>

<details><summary>💡 Hint 3</summary>
1. result, path = mutableListOf
2. backtrack(start): result.add(path.toList()); for i in start until n: path.add(nums[i]); backtrack(i+1); path.removeAt(path.lastIndex)
</details>

```kotlin
fun subsets(nums: IntArray): List<List<Int>> {
    // TODO: DFS with path add/remove
    return listOf()
}
```

---

### 4. Combination Sum — 🟡
**LeetCode #39** | Solution: `DSA/BackTracking/combination_sum/CombinationSum.kt`

**Problem:** Find unique combinations summing to target (numbers reusable unlimited times).

<details><summary>💡 Hint 1</summary>
Backtracking — at each index, either use the number again (stay at i) or move on (i+1).
</details>

<details><summary>💡 Hint 2</summary>
Prune: if remaining < 0 → return; if 0 → record. Sort first to prune early.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort candidates
2. backtrack(start, remaining): remaining == 0 → record; remaining < 0 → return
3. For i in start until n: path.add(c[i]); backtrack(i, remaining - c[i]); path.removeLast()
</details>

```kotlin
fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
    // TODO: DFS with reuse (stay at i)
    return listOf()
}
```

---

### 5. Permutations — 🟡
**LeetCode #46** | Solution: `DSA/BackTracking/permutations/Permutations.kt`

**Problem:** Return all permutations of distinct integers.

<details><summary>💡 Hint 1</summary>
Backtracking with a "used" set (or swap-in-place).
</details>

<details><summary>💡 Hint 2</summary>
When path.size == n → record. For each unused num: use it, recurse, un-use it.
</details>

<details><summary>💡 Hint 3</summary>
1. used = BooleanArray(n), path, result
2. backtrack: path.size == n → record
3. For i: if !used[i] → used[i] = true; path.add(nums[i]); backtrack; undo both
</details>

```kotlin
fun permute(nums: IntArray): List<List<Int>> {
    // TODO: Used array + path
    return listOf()
}
```

---

### 6. Word Search — 🟡
**LeetCode #79** | Solution: `DSA/BackTracking/word_search/WordSearch.kt`

**Problem:** Does the word exist as a path of adjacent cells in the grid (no cell reuse)?

<details><summary>💡 Hint 1</summary>
DFS from every cell; mark visited with '#'.
</details>

<details><summary>💡 Hint 2</summary>
dfs(r, c, index): index == word.length → true. Out of bounds or mismatch → false. Mark, recurse 4 dirs, restore.
</details>

<details><summary>💡 Hint 3</summary>
1. For each cell: if dfs(r, c, 0) → true
2. dfs: board[r][c] = '#'; found = any of 4 directions dfs(..., index+1); board[r][c] = word[index]; return found
</details>

```kotlin
fun exist(board: Array<CharArray>, word: String): Boolean {
    // TODO: DFS + mark/restore
    return false
}
```

---

## 🟡 TUESDAY, Dec 1 — 1D DP

### 7. House Robber — 🟡
**LeetCode #198** | Solution: `DSA/dp/one_d/HouseRobber.kt`

**Problem:** Max sum of non-adjacent houses.

<details><summary>💡 Hint 1</summary>
dp[i] = max(dp[i-1], dp[i-2] + nums[i]) — rob this house or skip it.
</details>

<details><summary>💡 Hint 2</summary>
Two rolling variables (prev2, prev1).
</details>

<details><summary>💡 Hint 3</summary>
prev2 = 0, prev1 = 0; for num: current = max(prev1, prev2 + num); prev2 = prev1; prev1 = current; return prev1.
</details>

```kotlin
fun rob(nums: IntArray): Int {
    // TODO: Rob or skip, rolling vars
    return 0
}
```

---

### 8. House Robber II (Circular) — 🟡
**LeetCode #213** | Solution: `DSA/dp/one_d/HouseRobberII.kt`

**Problem:** Houses in a circle (first and last are adjacent).

<details><summary>💡 Hint 1</summary>
Two cases: rob houses [0..n-2] or [1..n-1] (can't have both first and last).
</details>

<details><summary>💡 Hint 2</summary>
Answer = max(robLinear(0..n-2), robLinear(1..n-1)). Reuse House Robber I logic.
</details>

<details><summary>💡 Hint 3</summary>
If n == 1 → nums[0]. Else max of the two linear runs.
</details>

```kotlin
fun rob(nums: IntArray): Int {
    // TODO: Two linear ranges
    return 0
}
```

---

### 9. Coin Change (Min Coins) — 🟡
**LeetCode #322** | Solution: `DSA/dp/one_d/CoinChange.kt`

**Problem:** Min coins to make the amount (unlimited supply). -1 if impossible.

<details><summary>💡 Hint 1</summary>
dp[amount] = min coins. dp[a] = 1 + min(dp[a - coin]) over all coins.
</details>

<details><summary>💡 Hint 2</summary>
Initialize dp = IntArray(amount+1) { MAX }, dp[0] = 0. If dp[a - coin] reachable → update.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0] = 0
2. For a in 1..amount: for coin: if coin <= a && dp[a-coin] != MAX → dp[a] = min(dp[a], dp[a-coin] + 1)
3. Return dp[amount] == MAX ? -1 : dp[amount]
</details>

```kotlin
fun coinChange(coins: IntArray, amount: Int): Int {
    // TODO: Bottom-up over amounts
    return 0
}
```

---

### 10. Coin Change II (Combinations) — 🟡
**LeetCode #518** | Solution: `DSA/dp/one_d/CoinChangeII.kt`

**Problem:** Count combinations (not permutations) making the amount.

<details><summary>💡 Hint 1</summary>
Loop COINS in the OUTER loop to count combinations (avoids order permutations).
</details>

<details><summary>💡 Hint 2</summary>
dp[0] = 1; for coin: for a in coin..amount: dp[a] += dp[a - coin].
</details>

<details><summary>💡 Hint 3</summary>
Outer loop over coins, inner over amounts — order of loops is the key insight.
</details>

```kotlin
fun change(amount: Int, coins: IntArray): Int {
    // TODO: Coins outer, amounts inner
    return 0
}
```

---

### 11. Decode Ways — 🟡
**LeetCode #91** | Solution: `DSA/dp/one_d/DecodeWays.kt`

**Problem:** Digits decode as A=1..Z=26. Count decodings of the string.

<details><summary>💡 Hint 1</summary>
dp[i] = ways to decode first i chars. One-digit and two-digit transitions.
</details>

<details><summary>💡 Hint 2</summary>
One digit valid if s[i-1] != '0'. Two digits valid if 10..26. dp[i] = oneWays + twoWays.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0] = 1, dp[1] = (s[0] != '0') ? 1 : 0
2. For i in 2..n: one = if s[i-1] != '0' then dp[i-1] else 0
3. two = if s.substring(i-2, i) in "10".."26" then dp[i-2] else 0
4. dp[i] = one + two
</details>

```kotlin
fun numDecodings(s: String): Int {
    // TODO: 1-digit and 2-digit transitions
    return 0
}
```

---

### 12. Word Break — 🟡
**LeetCode #139** | Solution: `DSA/dp/one_d/WordBreak.kt`

**Problem:** Can s be segmented into words from the dictionary?

<details><summary>💡 Hint 1</summary>
dp[i] = can the first i chars be segmented?
</details>

<details><summary>💡 Hint 2</summary>
dp[i] = true if any j < i with dp[j] && s.substring(j, i) in wordSet.
</details>

<details><summary>💡 Hint 3</summary>
1. wordSet = words.toSet(); dp[0] = true
2. For i in 1..n: for j in 0 until i: if dp[j] && s.substring(j, i) in wordSet → dp[i] = true; break
</details>

```kotlin
fun wordBreak(s: String, wordDict: List<String>): Boolean {
    // TODO: dp over prefixes
    return false
}
```

---

## 🟡 WEDNESDAY, Dec 2 — 2D DP

### 13. Unique Paths — 🟡
**LeetCode #62** | Solution: `DSA/dp/two_d/UniquePaths.kt`

**Problem:** Count paths in an m×n grid moving only right/down.

<details><summary>💡 Hint 1</summary>
dp[i][j] = dp[i-1][j] + dp[i][j-1].
</details>

<details><summary>💡 Hint 2</summary>
First row and column = 1. Can compress to a 1D rolling array.
</details>

<details><summary>💡 Hint 3</summary>
1. dp = IntArray(n) { 1 }
2. For each row after the first: for j in 1 until n: dp[j] += dp[j-1]
</details>

```kotlin
fun uniquePaths(m: Int, n: Int): Int {
    // TODO: Rolling 1D array
    return 0
}
```

---

### 14. Minimum Path Sum — 🟡
**LeetCode #64** | Solution: `DSA/dp/two_d/MinimumPathSum.kt`

**Problem:** Min path sum from top-left to bottom-right (right/down only).

<details><summary>💡 Hint 1</summary>
dp[i][j] = grid[i][j] + min(dp[i-1][j], dp[i][j-1]).
</details>

<details><summary>💡 Hint 2</summary>
First row/column accumulate. Fill the rest.
</details>

<details><summary>💡 Hint 3</summary>
In-place update of grid or a separate dp table.
</details>

```kotlin
fun minPathSum(grid: Array<IntArray>): Int {
    // TODO: Cell + min of top/left
    return 0
}
```

---

### 15. Longest Common Subsequence — 🟡
**LeetCode #1143** | Solution: `DSA/dp/two_d/LongestCommonSubsequence.kt`

**Problem:** Length of the longest common subsequence of two strings.

<details><summary>💡 Hint 1</summary>
dp[i][j] = LCS of first i chars of s1 and first j of s2.
</details>

<details><summary>💡 Hint 2</summary>
Chars match → 1 + dp[i-1][j-1]. Else max(dp[i-1][j], dp[i][j-1]).
</details>

<details><summary>💡 Hint 3</summary>
2D table (m+1)×(n+1), fill row by row.
</details>

```kotlin
fun longestCommonSubsequence(text1: String, text2: String): Int {
    // TODO: Match → diagonal + 1; else max of skips
    return 0
}
```

---

### 16. Edit Distance — 🟡
**LeetCode #72** | Solution: `DSA/dp/two_d/EditDistance.kt`

**Problem:** Min operations (insert/delete/replace) to convert word1 to word2.

<details><summary>💡 Hint 1</summary>
dp[i][j] = min ops for first i chars of w1 and first j of w2.
</details>

<details><summary>💡 Hint 2</summary>
Match → dp[i-1][j-1]. Else 1 + min(replace: dp[i-1][j-1], delete: dp[i-1][j], insert: dp[i][j-1]).
</details>

<details><summary>💡 Hint 3</summary>
Base: dp[i][0] = i, dp[0][j] = j.
</details>

```kotlin
fun minDistance(word1: String, word2: String): Int {
    // TODO: Match or 1 + min of three ops
    return 0
}
```

---

### 17. Longest Palindromic Subsequence — 🟡
**LeetCode #516** | Solution: `DSA/dp/two_d/LongestPalindromicSubsequence.kt`

**Problem:** Length of the longest palindromic subsequence.

<details><summary>💡 Hint 1</summary>
LCS(s, reverse(s)) — or direct interval DP.
</details>

<details><summary>💡 Hint 2</summary>
dp[i][j] = LPS in s[i..j]. Ends match → 2 + dp[i+1][j-1]. Else max(dp[i+1][j], dp[i][j-1]).
</details>

<details><summary>💡 Hint 3</summary>
Fill by increasing interval length; dp[i][i] = 1.
</details>

```kotlin
fun longestPalindromeSubseq(s: String): Int {
    // TODO: Interval DP or LCS with reverse
    return 0
}
```

---

## 🟡 THURSDAY, Dec 3 — Subsequence DP + Backtracking

### 18. Longest Increasing Subsequence — 🟡
**LeetCode #300** | Solution: `DSA/dp/subsequence/LongestIncreasingSubsequence.kt`

**Problem:** Length of the longest strictly increasing subsequence.

<details><summary>💡 Hint 1</summary>
O(n²) DP: dp[i] = LIS ending at i.
</details>

<details><summary>💡 Hint 2</summary>
dp[i] = 1 + max(dp[j]) over j < i with nums[j] < nums[i]. (O(n log n): patience sorting with binary search.)
</details>

<details><summary>💡 Hint 3</summary>
1. dp = IntArray(n) { 1 }
2. For i: for j < i: if nums[j] < nums[i] → dp[i] = max(dp[i], dp[j] + 1)
3. Return dp.max()
</details>

```kotlin
fun lengthOfLIS(nums: IntArray): Int {
    // TODO: dp[i] = best ending at i
    return 0
}
```

---

### 19. Partition Equal Subset Sum — 🟡
**LeetCode #416** | Solution: `DSA/dp/subsequence/PartitionEqualSubsetSum.kt`

**Problem:** Can the array be split into two subsets with equal sums?

<details><summary>💡 Hint 1</summary>
Total odd → false. Else: subset-sum to total/2.
</details>

<details><summary>💡 Hint 2</summary>
dp[x] = can sum x be formed. For each num, iterate sums DOWNWARD.
</details>

<details><summary>💡 Hint 3</summary>
1. target = sum / 2; dp = BooleanArray(target + 1); dp[0] = true
2. For num: for x in target downTo num: dp[x] = dp[x] || dp[x - num]
3. Return dp[target]
</details>

```kotlin
fun canPartition(nums: IntArray): Boolean {
    // TODO: Subset sum to total/2, downward loop
    return false
}
```

---

### 20. Target Sum — 🟡
**LeetCode #494** | Solution: `DSA/dp/subsequence/TargetSum.kt`

**Problem:** Assign + or - to each number; count ways to reach target.

<details><summary>💡 Hint 1</summary>
Transform: subset P (positives) and N (negatives): sum(P) - sum(N) = target, sum(P) + sum(N) = total.
</details>

<details><summary>💡 Hint 2</summary>
sum(P) = (total + target) / 2 — count subsets with that sum (subset-sum counting DP).
</details>

<details><summary>💡 Hint 3</summary>
1. If (total + target) odd or < 0 → 0
2. Count subsets summing to (total + target) / 2 with dp[x] += dp[x - num] (downward)
</details>

```kotlin
fun findTargetSumWays(nums: IntArray, target: Int): Int {
    // TODO: Convert to subset-sum counting
    return 0
}
```

---

### 21. Palindrome Partitioning — 🟡
**LeetCode #131** | Solution: `DSA/BackTracking/palindrome_partitioning/PalindromePartitioning.kt`

**Problem:** Split s into substrings where every piece is a palindrome. Return all ways.

<details><summary>💡 Hint 1</summary>
Backtracking over cut positions + a palindrome check helper.
</details>

<details><summary>💡 Hint 2</summary>
backtrack(start): start == n → record. For end in start until n: if s[start..end] is palindrome → add, recurse(end+1), remove.
</details>

<details><summary>💡 Hint 3</summary>
Optional: precompute isPal[i][j] table for O(1) checks.
</details>

```kotlin
fun partition(s: String): List<List<String>> {
    // TODO: Cut at palindromic prefixes
    return listOf()
}
```

---

## 🟢 SATURDAY, Dec 5 — Hard Problems + Revision

### 22. N-Queens — 🔴
**LeetCode #51** | Solution: `DSA/BackTracking/n_queens/NQueens.kt`

**Problem:** Place n queens on n×n board, none attacking each other. Return all solutions.

<details><summary>💡 Hint 1</summary>
Backtracking row by row — one queen per row.
</details>

<details><summary>💡 Hint 2</summary>
Track occupied columns, and diagonals (r+c and r-c identify diagonals).
</details>

<details><summary>💡 Hint 3</summary>
1. cols = HashSet, diag1 = HashSet (r+c), diag2 = HashSet (r-c)
2. backtrack(row): row == n → record board
3. For col: if col, r+c, r-c all free → place, recurse, remove
</details>

```kotlin
fun solveNQueens(n: Int): List<List<String>> {
    // TODO: Row-by-row with col/diag sets
    return listOf()
}
```

---

### 23. Sudoku Solver — 🔴
**LeetCode #37** | Solution: `DSA/BackTracking/sudoku_solver/SudokuSolver.kt`

**Problem:** Solve the 9×9 Sudoku in-place (exactly one solution).

<details><summary>💡 Hint 1</summary>
Backtracking over empty cells with row/col/box constraint sets.
</details>

<details><summary>💡 Hint 2</summary>
For each empty cell, try 1-9; if valid → place, recurse; if recursion fails → undo. True propagates success.
</details>

<details><summary>💡 Hint 3</summary>
1. rows/cols/boxes = Array(9) { HashSet&lt;Char&gt; }
2. Collect empty cells. backtrack(index): index == empties.size → true
3. Try each digit; check sets; place, recurse, on failure remove
</details>

```kotlin
fun solveSudoku(board: Array<CharArray>) {
    // TODO: Constraint sets + backtracking
}
```

---

**Revision (rest of Saturday):** Re-solve the 3–4 hardest problems this week + mixed practice:
- Coin Change (Week 7)
- Longest Common Subsequence (Week 7)
- Merge Intervals (Week 6)
- LRU Cache (Week 4)

---

## 🧪 SUNDAY, Dec 6 — Mock Test 7 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Climbing Stairs | 1D DP | 🟢 | 15 min |
| 2 | Subsets | Backtracking | 🟡 | 20 min |
| 3 | Coin Change | 1D DP | 🟡 | 25 min |
| 4 | Longest Increasing Subsequence | Subsequence DP | 🟡 | 25 min |
| 5 | Word Break / N-Queens | DP / Hard Backtracking | 🟡/🔴 | 35 min |

**Part 2 — Review (1 hr):** Score, log in [Mock_Test_Log.md](../Mock_Test_Log.md), re-solve failures, update tracker.

---

## ✅ Week 7 Completion Checklist

- [ ] Mon: 6 easy DP + backtracking problems
- [ ] Tue: 6 1D DP problems
- [ ] Wed: 5 2D DP problems
- [ ] Thu: 4 subsequence DP + backtracking
- [ ] Sat: 2 hard problems + revision
- [ ] Sun: Mock Test 7 completed & logged
- [ ] Progress Tracker updated
