# 📅 Week 10 — Advanced DP: 2D / Strings / Grids

> **Dates:** Dec 21 (Mon) – Dec 27 (Sun)
> **Pattern Focus:** 2D DP on strings (LCS / Edit Distance family), grid DP, interval/partition DP, counting DP
> **Reminder:** Try each problem 20–35 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/dp/` only AFTER solving.

> 🧪 **Practice & Test Cases:** [Week10_Practice.md](../practice/Week10_Practice.md) — sample cases + complexity validation (use [TestRunner.kt](../testrunner/TestRunner.kt)).

## 🗓️ Day Plan

| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Dec 21 | #1–#5 (2D String DP) | 2 hrs |
| Tue | Dec 22 | #6–#10 (Grid DP) | 2 hrs |
| Wed | Dec 23 | #11–#15 (Interval / Partition DP) | 2 hrs |
| Thu | Dec 24 | #16–#20 (Counting / Subsequence DP) | 2 hrs |
| Fri | Dec 25 | 🛑 REST DAY | — |
| Sat | Dec 26 | Revision: re-solve 2 weakest + mixed practice | 2 hrs |
| Sun | Dec 27 | 🧪 Mock Test 10 + Review | 3 hrs |

---

## 🔴 MONDAY, Dec 21 — 2D String DP

### 1. Edit Distance — 🔴
**LeetCode #72** | Solution (check later): `DSA/dp/two_d/`

**Problem:** Min operations (insert/delete/replace) to convert word1 → word2.

<details><summary>💡 Hint 1 (click to reveal)</summary>
dp[i][j] = edit distance between first i chars of word1 and first j of word2.
</details>

<details><summary>💡 Hint 2</summary>
Equal chars → dp[i-1][j-1]. Else 1 + min(insert, delete, replace).
</details>

<details><summary>💡 Hint 3 (steps)</summary>
1. Base dp[i][0]=i, dp[0][j]=j. 2. Fill with recurrence. 3. Return dp[m][n]
</details>

```kotlin
fun minDistance(word1: String, word2: String): Int {
    // TODO: 2D DP, three ops
    return 0
}
```

---

### 2. Longest Common Subsequence — 🟡
**LeetCode #1143** | Solution: `DSA/dp/subsequence/`

**Problem:** Length of the longest subsequence common to both strings.

<details><summary>💡 Hint 1</summary>
dp[i][j] = LCS of the two prefixes.
</details>

<details><summary>💡 Hint 2</summary>
Match → 1 + dp[i-1][j-1]; else max(dp[i-1][j], dp[i][j-1]).
</details>

<details><summary>💡 Hint 3</summary>
Base row/col = 0; fill; answer dp[m][n].
</details>

```kotlin
fun longestCommonSubsequence(text1: String, text2: String): Int {
    // TODO: 2D DP match vs skip
    return 0
}
```

---

### 3. Longest Palindromic Subsequence — 🟡
**LeetCode #516** | Solution: `DSA/dp/subsequence/`

**Problem:** Length of the longest palindromic subsequence of s.

<details><summary>💡 Hint 1</summary>
LPS(s) = LCS(s, s.reversed()) — or direct interval DP.
</details>

<details><summary>💡 Hint 2</summary>
dp[i][j] over s[i..j]: match → 2 + dp[i+1][j-1]; else max(drop either end).
</details>

<details><summary>💡 Hint 3</summary>
1. dp[i][i]=1. 2. i from n−1 down, j from i+1 up. 3. dp[0][n−1]
</details>

```kotlin
fun longestPalindromeSubseq(s: String): Int {
    // TODO: Interval DP on bounds
    return 0
}
```

---

### 4. Distinct Subsequences — 🔴
**LeetCode #115** | Solution: `DSA/dp/subsequence/`

**Problem:** Count distinct subsequences of s equal to t.

<details><summary>💡 Hint 1</summary>
dp[i][j] = ways to build t[j..] from s[i..].
</details>

<details><summary>💡 Hint 2</summary>
Match → use it (dp[i+1][j+1]) + skip it (dp[i+1][j]); mismatch → skip only.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[i][n]=1. 2. Fill backwards with Long to dodge overflow. 3. dp[0][0]
</details>

```kotlin
fun numDistinct(s: String, t: String): Int {
    // TODO: Count-match DP, Long overflow guard
    return 0
}
```


---

### 5. Interleaving String — 🟡
**LeetCode #97** | Solution: `DSA/dp/two_d/`

**Problem:** Is s3 formed by interleaving s1 and s2?

<details><summary>💡 Hint 1</summary>
dp[i][j] = can s3[i+j..] be built from s1[i..] and s2[j..].
</details>

<details><summary>💡 Hint 2</summary>
dp[i][j] = (s1[i]==s3[i+j] && dp[i+1][j]) || (s2[j]==s3[i+j] && dp[i][j+1]).
</details>

<details><summary>💡 Hint 3</summary>
1. Lengths must add up. 2. Fill backwards from dp[m][n]=true.
</details>

```kotlin
fun isInterleave(s1: String, s2: String, s3: String): Boolean {
    // TODO: 2D reachability DP
    return false
}
```

---

## 🟡 TUESDAY, Dec 22 — Grid DP

### 6. Minimum Path Sum — 🟡
**LeetCode #64** | Solution: `DSA/dp/two_d/`

**Problem:** Right/down-only path top-left → bottom-right with min sum.

<details><summary>💡 Hint 1</summary>
dp[r][c] = grid[r][c] + min(top, left).
</details>

<details><summary>💡 Hint 2</summary>
First row/col accumulate — only one way in. In-place works.
</details>

<details><summary>💡 Hint 3</summary>
1. Fill first row/col. 2. Interior with min(top,left). 3. dp[m−1][n−1]
</details>

```kotlin
fun minPathSum(grid: Array<IntArray>): Int {
    // TODO: In-place grid DP
    return 0
}
```

---

### 7. Unique Paths II — 🟡
**LeetCode #63** | Solution: `DSA/dp/two_d/`

**Problem:** Count right/down paths avoiding obstacles (1s).

<details><summary>💡 Hint 1</summary>
Obstacle cells contribute 0 ways.
</details>

<details><summary>💡 Hint 2</summary>
grid==1 → dp=0; else dp = above + left.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0][0] = 1 − grid[0][0]. 2. Row-major scan, zero obstacles. 3. dp[m−1][n−1]
</details>

```kotlin
fun uniquePathsWithObstacles(obstacleGrid: Array<IntArray>): Int {
    // TODO: Obstacles zero out DP
    return 0
}
```

---

### 8. Maximal Square — 🟡
**LeetCode #221** | Solution: `DSA/dp/two_d/`

**Problem:** Largest square of 1s; return its area.

<details><summary>💡 Hint 1</summary>
dp[r][c] = side of largest square with bottom-right at (r,c).
</details>

<details><summary>💡 Hint 2</summary>
1s → dp = 1 + min(up, left, up-left).
</details>

<details><summary>💡 Hint 3</summary>
Track maxSide while filling; return maxSide².
</details>

```kotlin
fun maximalSquare(matrix: Array<CharArray>): Int {
    // TODO: min of 3 neighbors + 1
    return 0
}
```


---

### 9. Maximal Rectangle — 🔴
**LeetCode #85** | Solution: `DSA/dp/two_d/`

**Problem:** Largest rectangle of 1s in a binary matrix.

<details><summary>💡 Hint 1</summary>
Row-by-row histograms + Largest Rectangle in Histogram (stack).
</details>

<details><summary>💡 Hint 2</summary>
heights[c] = consecutive 1s upward per column; run histogram algorithm per row.
</details>

<details><summary>💡 Hint 3</summary>
1. heights[c] = grid==1 ? heights[c]+1 : 0. 2. largestRectangleArea(heights) each row → max
</details>

```kotlin
fun maximalRectangle(matrix: Array<CharArray>): Int {
    // TODO: per-row histogram + monotonic stack
    return 0
}
```

---

### 10. Cherry Pickup II — 🔴
**LeetCode #1463** | Solution: `DSA/dp/two_d/`

**Problem:** Two robots descend row by row (3 moves each); max cherries collected together.

<details><summary>💡 Hint 1</summary>
DP on (row, col1, col2) — move both robots simultaneously.
</details>

<details><summary>💡 Hint 2</summary>
Gain = cherries at (r,c1) + (r,c2) (once if c1==c2) + best of 3×3 next-move combos.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[r][c1][c2] bottom-up from last row. 2. For c1−1..c1+1 × c2−1..c2+1 take max
</details>

```kotlin
fun cherryPickup(grid: Array<IntArray>): Int {
    // TODO: DP(row, col1, col2)
    return 0
}
```

---

## 🟡 WEDNESDAY, Dec 23 — Interval / Partition DP

### 11. Burst Balloons — 🔴
**LeetCode #312** | Solution: `DSA/dp/`

**Problem:** Burst all balloons; coins = nums[left]×nums[i]×nums[right]. Maximize total.

<details><summary>💡 Hint 1</summary>
Think REVERSE: pick the LAST balloon to burst in a range.
</details>

<details><summary>💡 Hint 2</summary>
dp[l][r] = max coins bursting all in (l, r) open interval. Pick k last: dp[l][r] = max(dp[l][k] + dp[k][r] + nums[l]×nums[k]×nums[r]).
</details>

<details><summary>💡 Hint 3</summary>
1. Pad nums with 1s at both ends. 2. Interval length 2..n. 3. dp[0][n+1]
</details>

```kotlin
fun maxCoins(nums: IntArray): Int {
    // TODO: Interval DP, pick last
    return 0
}
```

---

### 12. Palindromic Substrings — 🟡
**LeetCode #647** | Solution: `DSA/dp/`

**Problem:** Count all palindromic substrings.

<details><summary>💡 Hint 1</summary>
dp[i][j] = is s[i..j] a palindrome — or expand-around-center (O(1) space).
</details>

<details><summary>💡 Hint 2</summary>
s[i]==s[j] && (j−i<2 || dp[i+1][j−1]) → dp[i][j]=true, count++.
</details>

<details><summary>💡 Hint 3</summary>
1. i from n−1 down, j from i up. 2. 2n−1 centers alternative
</details>

```kotlin
fun countSubstrings(s: String): Int {
    // TODO: DP or expand around 2n-1 centers
    return 0
}
```


---

### 13. Decode Ways — 🟡
**LeetCode #91** | Solution: `DSA/dp/one_d/`

**Problem:** 'A'-'Z' = 1-26. Count ways to decode a digit string.

<details><summary>💡 Hint 1</summary>
dp[i] = ways to decode prefix of length i.
</details>

<details><summary>💡 Hint 2</summary>
Single digit (≠'0') adds dp[i-1]; two-digit 10-26 adds dp[i-2].
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0]=1, dp[1]= (s[0]≠'0') ? 1 : 0. 2. Scan: add both contributions. 3. dp[n]
</details>

```kotlin
fun numDecodings(s: String): Int {
    // TODO: 1D DP with two-step lookback
    return 0
}
```

---

### 14. Word Break — 🟡
**LeetCode #139** | Solution: `DSA/dp/one_d/`

**Problem:** Can s be segmented into dictionary words?

<details><summary>💡 Hint 1</summary>
dp[i] = s[0..i) is segmentable.
</details>

<details><summary>💡 Hint 2</summary>
dp[i] = any j < i with dp[j] && s[j..i) in dict.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0]=true. 2. For i: for j in 0 until i: if dp[j] && s.substring(j,i) in set → true. 3. dp[n]
</details>

```kotlin
fun wordBreak(s: String, wordDict: List<String>): Boolean {
    // TODO: DP + HashSet lookup
    return false
}
```

---

### 15. Perfect Squares — 🟡
**LeetCode #279** | Solution: `DSA/dp/one_d/`

**Problem:** Least number of perfect squares summing to n.

<details><summary>💡 Hint 1</summary>
Unbounded knapsack: coins = 1,4,9,...  ≤ n.
</details>

<details><summary>💡 Hint 2</summary>
dp[i] = min(dp[i − sq]) + 1 over squares sq ≤ i.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0]=0. 2. For i in 1..n: for sq while sq≤i: dp[i]=min(dp[i], dp[i−sq]+1). 3. dp[n]
</details>

```kotlin
fun numSquares(n: Int): Int {
    // TODO: Coin-change style DP
    return 0
}
```


---

## 🟡 THURSDAY, Dec 24 — Counting / Subsequence DP

### 16. Coin Change II — 🟡
**LeetCode #518** | Solution: `DSA/dp/one_d/`

**Problem:** Number of combinations of coins that sum to amount.

<details><summary>💡 Hint 1</summary>
Loop COINS outer, amount inner → combinations (not permutations).
</details>

<details><summary>💡 Hint 2</summary>
dp[a] += dp[a − coin] for a from coin..amount.
</details>

<details><summary>💡 Hint 3</summary>
1. dp[0]=1. 2. For each coin: for a in coin..amount: dp[a]+=dp[a−coin]. 3. dp[amount]
</details>

```kotlin
fun change(amount: Int, coins: IntArray): Int {
    // TODO: coins outer, amount inner
    return 0
}
```

---

### 17. Longest Increasing Path in a Matrix — 🔴
**LeetCode #329** | Solution: `DSA/dp/`

**Problem:** Length of the longest strictly increasing path (4-direction moves).

<details><summary>💡 Hint 1</summary>
DFS + memo: dp[r][c] = 1 + max(dp of strictly greater neighbors).
</details>

<details><summary>💡 Hint 2</summary>
Memo makes it O(mn) — each cell computed once.
</details>

<details><summary>💡 Hint 3</summary>
1. For each cell: DFS 4 dirs where grid[n] > grid[cur]. 2. Cache dp. 3. Track global max
</details>

```kotlin
fun longestIncreasingPath(matrix: Array<IntArray>): Int {
    // TODO: DFS + memo
    return 0
}
```

---

### 18. Dungeon Game — 🔴
**LeetCode #174** | Solution: `DSA/dp/two_d/`

**Problem:** Minimum initial health to reach bottom-right (grid can drain or heal), never ≤ 0.

<details><summary>💡 Hint 1</summary>
Work BACKWARDS: dp[r][c] = min health needed BEFORE entering (r,c).
</details>

<details><summary>💡 Hint 2</summary>
dp[r][c] = max(1, min(dp right, dp down) − grid[r][c]).
</details>

<details><summary>💡 Hint 3</summary>
1. dp[m−1][n−1] = max(1, 1 − grid). 2. Fill from bottom-right. 3. dp[0][0]
</details>

```kotlin
fun calculateMinimumHP(dungeon: Array<IntArray>): Int {
    // TODO: Backwards DP
    return 0
}
```

---

### 19. Largest Divisible Subset — 🟡
**LeetCode #368** | Solution: `DSA/dp/subsequence/`

**Problem:** Largest subset where every pair (a,b) has a % b == 0.

<details><summary>💡 Hint 1</summary>
Sort; it becomes an "increasing subsequence"-style DP — divisibility is transitive.
</details>

<details><summary>💡 Hint 2</summary>
dp[i] = best chain ending at i via nums[j] % nums[i-1] == 0; track parent for reconstruction.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort ascending. 2. dp[i]=1, parent[i]=−1; for j<i: nums[i]%nums[j]==0 && dp[j]+1>dp[i] → update. 3. Reconstruct max chain
</details>

```kotlin
fun largestDivisibleSubset(nums: IntArray): List<Int> {
    // TODO: Sort + LIS-style DP + parent rebuild
    return listOf()
}
```

---

### 20. Number of Longest Increasing Subsequence — 🟡
**LeetCode #673** | Solution: `DSA/dp/subsequence/`

**Problem:** Count how many LIS exist.

<details><summary>💡 Hint 1</summary>
Two arrays: length[i] and count[i] (number of LIS ending at i).
</details>

<details><summary>💡 Hint 2</summary>
Longer chain → length/count replace. Equal chain → count += count[j].
</details>

<details><summary>💡 Hint 3</summary>
1. For j<i with nums[j]<nums[i]: if length[j]+1>length[i] → update both; elif == → count[i]+=count[j]. 2. Sum counts where length == max
</details>

```kotlin
fun findNumberOfLIS(nums: IntArray): Int {
    // TODO: length[] + count[] DP
    return 0
}
```

---

## 🧪 SUNDAY, Dec 27 — Mock Test 10 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Difficulty | Time Limit |
|---|---------|-----------|-----------|
| 1 | Minimum Path Sum | 🟡 | 15 min |
| 2 | Longest Common Subsequence | 🟡 | 20 min |
| 3 | Word Break | 🟡 | 20 min |
| 4 | Longest Increasing Path in a Matrix | 🔴 | 30 min |
| 5 | Edit Distance | 🔴 | 30 min |

**Part 2 — Review (1 hr):**
1. Score yourself: 🧠 = 2 pts, 💡 = 1 pt, 📖 = 0 pts (max 10)
2. Log results in [Mock_Test_Log.md](../Mock_Test_Log.md)
3. Re-solve failed problems from scratch
4. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## ✅ Week 10 Completion Checklist

- [ ] Mon: 5 2D-string-DP problems solved
- [ ] Tue: 5 grid-DP problems solved
- [ ] Wed: 5 interval/partition-DP problems solved
- [ ] Thu: 5 counting/subsequence-DP problems solved
- [ ] Sat: 2 weakest re-solved + mixed practice
- [ ] Sun: Mock Test 10 completed & logged
- [ ] Progress Tracker updated
