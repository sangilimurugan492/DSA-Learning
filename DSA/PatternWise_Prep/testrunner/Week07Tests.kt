package testrunner

/**
 * Week07Tests.kt — Week 7: Backtracking + DP (23 problems)
 *
 * Part A — 23 problems × 3 cases from practice/Week07_Practice.md (runAll).
 * Part B — 10 timed stress cases (complexityAll, not called by the default main()).
 * Solution stubs sit at the bottom of the file; they throw TODO() so each case is
 * reported as [SKIP ⏭️] until you implement it, then it runs for real.
 *
 * `change` is the week doc's name for Coin Change II; `robII` covers House Robber II,
 * which the doc folds into `rob`. TestRunner.check() non-null-asserts the block result,
 * so a test never returns a nullable directly.
 */

class Week07Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 7 — Backtracking + DP ━━━━━━━")
        climbStairs(); minCostClimbing(); subsets(); combinationSum(); permute()
        wordSearch(); houseRobber(); houseRobberII(); coinChange(); coinChangeII()
        decodeWays(); wordBreak(); uniquePaths(); minPathSum(); lcs()
        editDistance(); lps(); lis(); canPartition(); targetSum()
        palindromePartition(); nQueens(); sudoku()
        // Complexity validation (Part B) — run with: complexityAll()
        // climbStairsBig(); houseRobberBig(); coinChangeBig(); lcsBig(); lisBig()
        // canPartitionBig(); wordBreakBig(); uniquePathsBig(); targetSumBig(); nQueensBig()
    }

    // ── Part A — practice-sheet cases ───────────────────────────────────────

    // 1) Climbing Stairs: Fibonacci, two rolling variables.
    fun climbStairs() {
        TestRunner.check("W7 #1a Stairs n=2", block = { climbStairs(2) }, verify = { it == 2 })
        TestRunner.check("W7 #1b Stairs n=3", block = { climbStairs(3) }, verify = { it == 3 })
        TestRunner.check("W7 #1c Stairs n=45", block = { climbStairs(45) }, verify = { it == 1_836_311_903 })
    }

    // 2) Min Cost Climbing Stairs: dp[i] = cost[i] + min(dp[i-1], dp[i-2]).
    fun minCostClimbing() {
        TestRunner.check("W7 #2a Min cost classic", block = { minCostClimbingStairs(intArrayOf(10, 15, 20)) }, verify = { it == 15 })
        TestRunner.check("W7 #2b Min cost ones and traps", block = { minCostClimbingStairs(intArrayOf(1, 100, 1, 1, 1, 100, 1, 1, 100, 1)) }, verify = { it == 6 })
        TestRunner.check("W7 #2c Min cost free steps", block = { minCostClimbingStairs(intArrayOf(0, 0)) }, verify = { it == 0 })
    }

    // 3) Subsets: pick/skip backtracking (or bit masks).
    fun subsets() {
        TestRunner.check("W7 #3a Subsets of 3", block = { subsets(intArrayOf(1, 2, 3)) },
            verify = { it.size == 8 && it.distinct().size == 8 && it.contains(emptyList()) && it.contains(listOf(1, 2, 3)) })
        TestRunner.check("W7 #3b Subsets of one zero", block = { subsets(intArrayOf(0)).sortedBy { it.joinToString() } },
            verify = { it == listOf(emptyList<Int>(), listOf(0)) })
        TestRunner.check("W7 #3c Subsets of none", block = { subsets(intArrayOf()) }, verify = { it == listOf(emptyList<Int>()) })
    }

    // 4) Combination Sum: backtrack with reuse allowed, index never moves backwards.
    fun combinationSum() {
        TestRunner.check("W7 #4a Combos target 7", block = { combinationSum(intArrayOf(2, 3, 6, 7), 7).map { it.sorted() }.sortedBy { it.joinToString() } },
            verify = { it == listOf(listOf(2, 2, 3), listOf(7)) })
        TestRunner.check("W7 #4b Combos target 8", block = { combinationSum(intArrayOf(2, 3, 5), 8).map { it.sorted() }.sortedBy { it.joinToString() } },
            verify = { it == listOf(listOf(2, 2, 2, 2), listOf(2, 3, 3), listOf(3, 5)) })
        TestRunner.check("W7 #4c Combos impossible", block = { combinationSum(intArrayOf(2), 1) }, verify = { it.isEmpty() })
    }

    // 5) Permutations: used-flags (or swap-in-place) backtracking.
    fun permute() {
        TestRunner.check("W7 #5a Permutations of 3", block = { permute(intArrayOf(1, 2, 3)) },
            verify = { it.size == 6 && it.contains(listOf(1, 2, 3)) && it.contains(listOf(3, 2, 1)) })
        TestRunner.check("W7 #5b Permutations of 2", block = { permute(intArrayOf(0, 1)).sortedBy { it.joinToString() } },
            verify = { it == listOf(listOf(0, 1), listOf(1, 0)) })
        TestRunner.check("W7 #5c Permutations of 1", block = { permute(intArrayOf(1)) }, verify = { it == listOf(listOf(1)) })
    }

    // 6) Word Search: DFS with a visited mark, undo the mark on the way out.
    fun wordSearch() {
        TestRunner.check("W7 #6a Word search classic", block = { exist(boardOf("ABCE", "SFCS", "ADEE"), "ABCCED") }, verify = { it })
        TestRunner.check("W7 #6b Word search short", block = { exist(boardOf("ABCE", "SFCS", "ADEE"), "SEE") }, verify = { it })
        TestRunner.check("W7 #6c Word search no reuse", block = { exist(boardOf("ABCE", "SFCS", "ADEE"), "ABCB") }, verify = { !it })
    }

    // 7) House Robber: rob/skip rolling max.
    fun houseRobber() {
        TestRunner.check("W7 #7a Robber classic", block = { rob(intArrayOf(1, 2, 3, 1)) }, verify = { it == 4 })
        TestRunner.check("W7 #7b Robber five houses", block = { rob(intArrayOf(2, 7, 9, 3, 1)) }, verify = { it == 12 })
        TestRunner.check("W7 #7c Robber ties", block = { rob(intArrayOf(2, 1, 1, 2)) }, verify = { it == 4 })
    }

    // 8) House Robber II: max of robbing 0..n-2 and 1..n-1.
    fun houseRobberII() {
        TestRunner.check("W7 #8a Circular three houses", block = { robII(intArrayOf(2, 3, 2)) }, verify = { it == 3 })
        TestRunner.check("W7 #8b Circular four houses", block = { robII(intArrayOf(1, 2, 3, 1)) }, verify = { it == 4 })
        TestRunner.check("W7 #8c Circular skip middle", block = { robII(intArrayOf(1, 2, 3)) }, verify = { it == 3 })
    }

    // 9) Coin Change: bottom-up min coins, amount+1 as "impossible".
    fun coinChange() {
        TestRunner.check("W7 #9a Coins classic", block = { coinChange(intArrayOf(1, 2, 5), 11) }, verify = { it == 3 })
        TestRunner.check("W7 #9b Coins impossible", block = { coinChange(intArrayOf(2), 3) }, verify = { it == -1 })
        TestRunner.check("W7 #9c Coins zero amount", block = { coinChange(intArrayOf(1), 0) }, verify = { it == 0 })
    }

    // 10) Coin Change II: coins in the OUTER loop so order never double-counts.
    fun coinChangeII() {
        TestRunner.check("W7 #10a Combos classic", block = { change(5, intArrayOf(1, 2, 5)) }, verify = { it == 4 })
        TestRunner.check("W7 #10b Combos impossible", block = { change(3, intArrayOf(2)) }, verify = { it == 0 })
        TestRunner.check("W7 #10c Combos exact coin", block = { change(10, intArrayOf(10)) }, verify = { it == 1 })
    }

    // 11) Decode Ways: 1-digit and 2-digit transitions, zeros are traps.
    fun decodeWays() {
        TestRunner.check("W7 #11a Decode 12", block = { numDecodings("12") }, verify = { it == 2 })
        TestRunner.check("W7 #11b Decode 226", block = { numDecodings("226") }, verify = { it == 3 })
        TestRunner.check("W7 #11c Decode leading zero", block = { numDecodings("06") }, verify = { it == 0 })
    }

    // 12) Word Break: dp[i] = any dp[j] with s[j..i] in the dict.
    fun wordBreak() {
        TestRunner.check("W7 #12a Break leetcode", block = { wordBreak("leetcode", listOf("leet", "code")) }, verify = { it })
        TestRunner.check("W7 #12b Break reuse words", block = { wordBreak("applepenapple", listOf("apple", "pen")) }, verify = { it })
        TestRunner.check("W7 #12c Break impossible", block = { wordBreak("catsandog", listOf("cats", "dog", "sand", "and", "cat")) }, verify = { !it })
    }

    // 13) Unique Paths: row DP, dp[j] += dp[j-1].
    fun uniquePaths() {
        TestRunner.check("W7 #13a Paths 3x7", block = { uniquePaths(3, 7) }, verify = { it == 28 })
        TestRunner.check("W7 #13b Paths 3x2", block = { uniquePaths(3, 2) }, verify = { it == 3 })
        TestRunner.check("W7 #13c Paths 1x1", block = { uniquePaths(1, 1) }, verify = { it == 1 })
    }

    // 14) Minimum Path Sum: first row/col are prefix sums, then min of two neighbours.
    fun minPathSum() {
        TestRunner.check("W7 #14a Path sum 3x3", block = { minPathSum(arrayOf(intArrayOf(1, 3, 1), intArrayOf(1, 5, 1), intArrayOf(4, 2, 1))) }, verify = { it == 7 })
        TestRunner.check("W7 #14b Path sum single row", block = { minPathSum(arrayOf(intArrayOf(1, 2, 3))) }, verify = { it == 6 })
        TestRunner.check("W7 #14c Path sum single cell", block = { minPathSum(arrayOf(intArrayOf(5))) }, verify = { it == 5 })
    }

    // 15) Longest Common Subsequence: 2-D table, match extends the diagonal.
    fun lcs() {
        TestRunner.check("W7 #15a LCS classic", block = { longestCommonSubsequence("abcde", "ace") }, verify = { it == 3 })
        TestRunner.check("W7 #15b LCS identical", block = { longestCommonSubsequence("abc", "abc") }, verify = { it == 3 })
        TestRunner.check("W7 #15c LCS disjoint", block = { longestCommonSubsequence("abc", "def") }, verify = { it == 0 })
    }

    // 16) Edit Distance: insert/delete/replace all cost 1.
    fun editDistance() {
        TestRunner.check("W7 #16a Edit horse->ros", block = { minDistance("horse", "ros") }, verify = { it == 3 })
        TestRunner.check("W7 #16b Edit intention->execution", block = { minDistance("intention", "execution") }, verify = { it == 5 })
        TestRunner.check("W7 #16c Edit from empty", block = { minDistance("", "abc") }, verify = { it == 3 })
    }

    // 17) Longest Palindromic Subsequence: interval DP (or LCS with the reverse).
    fun lps() {
        TestRunner.check("W7 #17a LPS bbbab", block = { longestPalindromeSubseq("bbbab") }, verify = { it == 4 })
        TestRunner.check("W7 #17b LPS cbbd", block = { longestPalindromeSubseq("cbbd") }, verify = { it == 2 })
        TestRunner.check("W7 #17c LPS single char", block = { longestPalindromeSubseq("a") }, verify = { it == 1 })
    }

    // 18) Longest Increasing Subsequence: tails array + binary search, O(n log n).
    fun lis() {
        TestRunner.check("W7 #18a LIS classic", block = { lengthOfLIS(intArrayOf(10, 9, 2, 5, 3, 7, 101, 18)) }, verify = { it == 4 })
        TestRunner.check("W7 #18b LIS repeats", block = { lengthOfLIS(intArrayOf(0, 1, 0, 3, 2, 3)) }, verify = { it == 4 })
        TestRunner.check("W7 #18c LIS all equal", block = { lengthOfLIS(intArrayOf(7, 7, 7, 7)) }, verify = { it == 1 })
    }

    // 19) Partition Equal Subset Sum: subset-sum DP up to total/2.
    fun canPartition() {
        TestRunner.check("W7 #19a Partition classic", block = { canPartition(intArrayOf(1, 5, 11, 5)) }, verify = { it })
        TestRunner.check("W7 #19b Partition impossible", block = { canPartition(intArrayOf(1, 2, 3, 5)) }, verify = { !it })
        TestRunner.check("W7 #19c Partition single value", block = { canPartition(intArrayOf(1)) }, verify = { !it })
    }

    // 20) Target Sum: count subsets summing to (sum + target) / 2.
    fun targetSum() {
        TestRunner.check("W7 #20a Target sum classic", block = { findTargetSumWays(intArrayOf(1, 1, 1, 1, 1), 3) }, verify = { it == 5 })
        TestRunner.check("W7 #20b Target sum single", block = { findTargetSumWays(intArrayOf(1), 1) }, verify = { it == 1 })
        TestRunner.check("W7 #20c Target sum with zeros", block = { findTargetSumWays(intArrayOf(0, 0, 1), 1) }, verify = { it == 4 })
    }

    // 21) Palindrome Partitioning: cut only where the prefix is a palindrome.
    fun palindromePartition() {
        TestRunner.check("W7 #21a Pal partitions aab",
            block = { w7Partition("aab").sortedBy { p -> p.joinToString(",") { it.length.toString() } } },
            verify = { it == listOf(listOf("a", "a", "b"), listOf("aa", "b")) })
        TestRunner.check("W7 #21b Pal partitions a", block = { w7Partition("a") }, verify = { it == listOf(listOf("a")) })
        TestRunner.check("W7 #21c Pal partitions aba",
            block = { w7Partition("aba").sortedBy { p -> p.joinToString(",") { it.length.toString() } } },
            verify = { it == listOf(listOf("a", "b", "a"), listOf("aba")) })
    }

    // 22) N-Queens: place one queen per row with col/diagonal sets.
    fun nQueens() {
        TestRunner.check("W7 #22a Queens n=4", block = { solveNQueens(4) }, verify = { it.size == 2 && it.all { b -> b.size == 4 } })
        TestRunner.check("W7 #22b Queens n=1", block = { solveNQueens(1) }, verify = { it == listOf(listOf("Q")) })
        TestRunner.check("W7 #22c Queens n=2", block = { solveNQueens(2) }, verify = { it.isEmpty() })
    }

    // 23) Sudoku Solver: backtracking with row/col/box masks. The puzzles below are
    //     produced by blanking cells out of a complete valid grid, so they always have
    //     at least one solution. A single blank is forced, so that case must reproduce
    //     the source grid; the wider blanks may admit other solutions, so those cases
    //     only require a valid grid that keeps every given digit.
    fun sudoku() {
        TestRunner.check("W7 #23a Sudoku with 30 blanks", block = { sudokuSolvesOut(sudokuSolution) { r, c -> (r + c) % 3 == 0 } }, verify = { it })
        TestRunner.check("W7 #23b Sudoku one empty cell", block = {
            val board = sudokuPuzzle(sudokuSolution) { r, c -> r == 0 && c == 0 }
            solveSudoku(board); sudokuRows(board) == sudokuSolution.toList()
        }, verify = { it })
        TestRunner.check("W7 #23c Sudoku with 18 blanks", block = { sudokuSolvesOut(sudokuSolution) { r, c -> r % 3 == 0 && c % 3 != 0 } }, verify = { it })
    }

    // Rows of a complete valid grid; blanking cells out of them yields a puzzle that
    // is guaranteed to have at least one solution.
    private val sudokuSolution = arrayOf(
        "534678912", "672195348", "198342567", "859761423", "426853791",
        "713924856", "961537284", "287419635", "345286179")

    private fun sudokuPuzzle(rows: Array<String>, blank: (Int, Int) -> Boolean): Array<CharArray> =
        Array(rows.size) { r ->
            rows[r].toCharArray().also { row ->
                (0 until rows[r].length).forEach { c -> if (blank(r, c)) row[c] = '.' }
            }
        }

    private fun sudokuRows(board: Array<CharArray>): List<String> = board.map { it.concatToString() }

    private fun sudokuSolved(board: Array<CharArray>): Boolean {
        val units = (0..8).flatMap { i ->
            listOf(
                (0..8).map { board[i][it] },
                (0..8).map { board[it][i] },
                (0..8).map { k -> board[i / 3 * 3 + k / 3][i % 3 * 3 + k % 3] })
        }
        return units.all { unit -> unit.toSet().size == 9 && unit.all { it in '1'..'9' } }
    }

    private fun sudokuSolvesOut(rows: Array<String>, blank: (Int, Int) -> Boolean): Boolean {
        val board = sudokuPuzzle(rows, blank)
        val givens = board.map { it.concatToString() }
        solveSudoku(board)
        val solved = sudokuRows(board)
        val keptGivens = solved.indices.all { r -> solved[r].zip(givens[r]).none { (cell, given) -> given != '.' && cell != given } }
        return keptGivens && sudokuSolved(board)
    }

    private fun boardOf(vararg rows: String): Array<CharArray> = Array(rows.size) { rows[it].toCharArray() }

    // ── Part B — complexity validation (complexityAll) ──────────────────────
    // Same problems at a scale that punishes the wrong approach. Expected values are
    // hand-derived (or derived in the comment); budgets assume the intended DP /
    // backtracking-with-pruning complexity.

    // 1) 1,000 calls of n=45: an iterative or memoised version reuses work, a plain
    //    recursion needs 2^45 calls per query and never finishes. (The sheet's
    //    n=10,000 variant needs BigInteger or a modulus, which Int cannot express.)
    fun climbStairsBig() = TestRunner.check("W7-B1 Climbing Stairs 45 x 1000 calls", budgetMs = 100,
        block = { (1..1_000).all { climbStairs(45) == 1_836_311_903 } }, verify = { it })

    // 2) 1M houses, values (i * 37) % 100 + 1 — O(n) rolling DP.
    fun houseRobberBig() = TestRunner.check("W7-B2 House Robber 1M houses", budgetMs = 200,
        block = { rob(IntArray(1_000_000) { (it * 37) % 100 + 1 }) }, verify = { it == 30_340_001 })

    // 3) 10,000 / 11 = 909 remainder 1, and no better mix exists: 910 coins.
    //    (Greedy is also wrong on small cases — coins [1,3,4] amount 6 is 2, not 3.)
    fun coinChangeBig() = TestRunner.check("W7-B3 Coin Change amount 10,000", budgetMs = 200,
        block = { coinChange(intArrayOf(1, 5, 6, 9, 11), 10_000) }, verify = { it == 910 })

    // 4) Two 2,000-char alternating strings: LCS of "ab"*n and "ba"*n is 2n - 1, but
    //    only a 2-D table proves it — the 4M-cell table is the point of the case.
    fun lcsBig() = TestRunner.check("W7-B4 LCS 2000 x 2000", budgetMs = 300,
        block = { longestCommonSubsequence("ab".repeat(1_000), "ba".repeat(1_000)) }, verify = { it == 1_999 })

    // 5) Sorted input is the worst case for the O(n^2) DP version; the patience
    //    version walks it once. (A *shuffled* 1..100,000 would have LIS ≈ 632, not
    //    100,000 as the sheet claims, so the ascending array is used instead.)
    fun lisBig() = TestRunner.check("W7-B5 LIS ascending 100k", budgetMs = 300,
        block = { lengthOfLIS(IntArray(100_000) { it + 1 }) }, verify = { it == 100_000 })

    // 6) 1..200 sums to 20,100, so the target is 10,050 — every value up to the total
    //    is reachable with consecutive integers. O(n * target) bitset DP.
    fun canPartitionBig() = TestRunner.check("W7-B6 Partition Equal Subset 200 values", budgetMs = 500,
        block = { canPartition(IntArray(200) { it + 1 }) }, verify = { it })

    // 7) 1,000 a's with words "a".."aaaaa": dp[i] checks 5 splits, so O(n * maxLen).
    fun wordBreakBig() = TestRunner.check("W7-B7 Word Break 1000 a's", budgetMs = 200,
        block = { wordBreak("a".repeat(1_000), listOf("a", "aa", "aaa", "aaaa", "aaaaa")) }, verify = { it })

    // 8) C(32,16) = 601,080,390 — the largest square that still fits in an Int.
    //    Without memoisation this is 6e8 recursive calls; with a row DP it is instant.
    fun uniquePathsBig() = TestRunner.check("W7-B8 Unique Paths 17 x 17", budgetMs = 100,
        block = { uniquePaths(17, 17) }, verify = { it == 601_080_390 })

    // 9) 20 ones, target 10 → 15 plus-signs → C(20,15) = 15,504 (the sheet's first
    //    figure of 184,756 counted C(20,10), which is target 0).
    fun targetSumBig() = TestRunner.check("W7-B9 Target Sum 20 ones", budgetMs = 200,
        block = { findTargetSumWays(IntArray(20) { 1 }, 10) }, verify = { it == 15_504 })

    // 10) The classic count: 92 solutions for an 8x8 board.
    fun nQueensBig() = TestRunner.check("W7-B10 N-Queens n=8", budgetMs = 500,
        block = { solveNQueens(8).size }, verify = { it == 92 })

    fun complexityAll() {
        println("\n━━━━━━━ WEEK 7 Part B — complexity validation ━━━━━━━")
        climbStairsBig(); houseRobberBig(); coinChangeBig(); lcsBig(); lisBig()
        canPartitionBig(); wordBreakBig(); uniquePathsBig(); targetSumBig(); nQueensBig()
    }
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 7 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════
//
// Each stub throws TODO() so every case above reports [SKIP ⏭️] instead of a wrong
// answer. Replace the TODO() with your implementation and the case runs for real.

// ── Backtracking ────────────────────────────────────────────────────────────
fun subsets(nums: IntArray): List<List<Int>> = TODO("W7 #3 — pick/skip, or 1<<n bit masks")
fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> = TODO("W7 #4 — reuse allowed, index never decreases")
fun permute(nums: IntArray): List<List<Int>> = TODO("W7 #5 — used flags, or swap in place")
fun exist(board: Array<CharArray>, word: String): Boolean = TODO("W7 #6 — DFS with a visited mark that is undone")
fun w7Partition(s: String): List<List<String>> = TODO("W7 #21 — cut where the prefix is a palindrome")
fun solveNQueens(n: Int): List<List<String>> = TODO("W7 #22 — one queen per row, col/diag sets")
fun solveSudoku(board: Array<CharArray>) { TODO("W7 #23 — row/col/box candidate masks") }

// ── 1-D DP ──────────────────────────────────────────────────────────────────
fun climbStairs(n: Int): Int = TODO("W7 #1 — two rolling Fibonacci variables")
fun minCostClimbingStairs(cost: IntArray): Int = TODO("W7 #2 — min of the two previous costs")
fun rob(nums: IntArray): Int = TODO("W7 #7 — rob/skip rolling max")
fun robII(nums: IntArray): Int = TODO("W7 #8 — max of robbing 0..n-2 and 1..n-1")          // House Robber II, folded into rob() in the doc
fun coinChange(coins: IntArray, amount: Int): Int = TODO("W7 #9 — bottom-up min coins")
fun change(amount: Int, coins: IntArray): Int = TODO("W7 #10 — 1-D counts, coins in the outer loop")  // Coin Change II
fun numDecodings(s: String): Int = TODO("W7 #11 — 1-digit and 2-digit transitions")
fun wordBreak(s: String, wordDict: List<String>): Boolean = TODO("W7 #12 — dp[i] reachable through a dict word")
fun lengthOfLIS(nums: IntArray): Int = TODO("W7 #18 — patience sorting: tails + binary search")
fun canPartition(nums: IntArray): Boolean = TODO("W7 #19 — subset-sum DP up to total/2")
fun findTargetSumWays(nums: IntArray, target: Int): Int = TODO("W7 #20 — count subsets summing to (sum+target)/2")

// ── Grid DP ─────────────────────────────────────────────────────────────────
fun uniquePaths(m: Int, n: Int): Int = TODO("W7 #13 — one row, dp[j] += dp[j-1]")
fun minPathSum(grid: Array<IntArray>): Int = TODO("W7 #14 — prefix first row/col, then min of two")

// ── String 2-D DP ───────────────────────────────────────────────────────────
fun longestCommonSubsequence(text1: String, text2: String): Int = TODO("W7 #15 — match extends the diagonal")
fun minDistance(word1: String, word2: String): Int = TODO("W7 #16 — insert/delete/replace min + 1")
fun longestPalindromeSubseq(s: String): Int = TODO("W7 #17 — interval DP on (i, j)")
