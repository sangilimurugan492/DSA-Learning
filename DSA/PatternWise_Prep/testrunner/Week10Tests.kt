package testrunner

/**
 * Week10Tests.kt — Week 10: Advanced DP (2D / Strings / Grids)
 *
 * Part A — 20 problems × 3 cases from practice/Week10_Practice.md (runAll).
 * Part B — 10 timed stress cases (complexityAll, not called by the default main()).
 * Solution stubs sit at the bottom of the file; they throw TODO() so each case is
 * reported as [SKIP ⏭️] until you implement it, then it runs for real.
 * Names that collide with Week 7 (w10MinDistance, longestCommonSubsequence,
 * longestPalindromeSubseq, minPathSum, numDecodings, wordBreak, change) keep a
 * w10 prefix so both weeks compile together.
 */

class Week10Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 10 — Advanced DP (2D / Strings / Grids) ━━━━━━━")
        editDistance(); editDistance2(); editDistance3(); lcs(); lcs2(); lcs3()
        palinSubseq(); palinSubseq2(); palinSubseq3(); distinctSubseq(); distinctSubseq2(); distinctSubseq3()
        interleave(); interleave2(); interleave3(); minPathSumCase(); minPathSumCase2(); minPathSumCase3()
        uniquePathsII(); uniquePathsII2(); uniquePathsII3(); maxSquare(); maxSquare2(); maxSquare3()
        maxRectangle(); maxRectangle2(); maxRectangle3(); cherryPickupCase(); cherryPickupCase2(); cherryPickupCase3()
        burstBalloons(); burstBalloons2(); burstBalloons3(); palinSubstrings(); palinSubstrings2(); palinSubstrings3()
        decodeWays(); decodeWays2(); decodeWays3(); wordBreakCase(); wordBreakCase2(); wordBreakCase3()
        perfectSquares(); perfectSquares2(); perfectSquares3(); coinChangeII(); coinChangeII2(); coinChangeII3()
        longestIncPath(); longestIncPath2(); longestIncPath3(); dungeon(); dungeon2(); dungeon3()
        divisibleSubset(); divisibleSubset2(); divisibleSubset3(); countLIS(); countLIS2(); countLIS3()
        // Part B runs via TestRunner main with --partb (do NOT write "complexityAll()" here)
    }

    // ── Part A — practice-sheet cases ───────────────────────────────────────

    // 1) Edit Distance: min insert/delete/replace ops to turn word1 into word2.
    fun editDistance() = TestRunner.check("W10 #1 Edit Distance", block = { w10MinDistance("horse", "ros") }, verify = { it == 3 })
    fun editDistance2() = TestRunner.check("W10 #1b Edit Distance long", block = { w10MinDistance("intention", "execution") }, verify = { it == 5 })
    fun editDistance3() = TestRunner.check("W10 #1c Edit Distance empty", block = { w10MinDistance("", "abc") }, verify = { it == 3 })

    // 2) Longest Common Subsequence.
    fun lcs() = TestRunner.check("W10 #2 LCS", block = { w10LCS("abcde", "ace") }, verify = { it == 3 })
    fun lcs2() = TestRunner.check("W10 #2b LCS identical", block = { w10LCS("abc", "abc") }, verify = { it == 3 })
    fun lcs3() = TestRunner.check("W10 #2c LCS disjoint", block = { w10LCS("abc", "def") }, verify = { it == 0 })

    // 3) Longest Palindromic Subsequence.
    fun palinSubseq() = TestRunner.check("W10 #3 Longest Palindromic Subseq", block = { w10LPS("bbbab") }, verify = { it == 4 })
    fun palinSubseq2() = TestRunner.check("W10 #3b Longest Palindromic Subseq", block = { w10LPS("cbbd") }, verify = { it == 2 })
    fun palinSubseq3() = TestRunner.check("W10 #3c Longest Palindromic Subseq single", block = { w10LPS("a") }, verify = { it == 1 })

    // 4) Distinct Subsequences: number of ways t appears as a subsequence of s.
    fun distinctSubseq() = TestRunner.check("W10 #4 Distinct Subsequences", block = { numDistinct("rabbbit", "rabbit") }, verify = { it == 3 })
    fun distinctSubseq2() = TestRunner.check("W10 #4b Distinct Subsequences", block = { numDistinct("babgbag", "bag") }, verify = { it == 5 })
    fun distinctSubseq3() = TestRunner.check("W10 #4c Distinct Subsequences none", block = { numDistinct("abc", "abcd") }, verify = { it == 0 })

    // 5) Interleaving String.
    fun interleave() = TestRunner.check("W10 #5 Interleaving String", block = { isInterleave("aabcc", "dbbca", "aadbbcbcac") }, verify = { it })
    fun interleave2() = TestRunner.check("W10 #5b Interleaving String false", block = { isInterleave("aabcc", "dbbca", "aadbbbaccc") }, verify = { !it })
    fun interleave3() = TestRunner.check("W10 #5c Interleaving String empty", block = { isInterleave("", "", "") }, verify = { it })

    // 6) Minimum Path Sum (grid DP).
    fun minPathSumCase() = TestRunner.check("W10 #6 Min Path Sum", block = { w10MinPathSum(arrayOf(intArrayOf(1,3,1), intArrayOf(1,5,1), intArrayOf(4,2,1))) }, verify = { it == 7 })
    fun minPathSumCase2() = TestRunner.check("W10 #6b Min Path Sum", block = { w10MinPathSum(arrayOf(intArrayOf(1,2,3), intArrayOf(4,5,6))) }, verify = { it == 12 })
    fun minPathSumCase3() = TestRunner.check("W10 #6c Min Path Sum single", block = { w10MinPathSum(arrayOf(intArrayOf(1))) }, verify = { it == 1 })

    // 7) Unique Paths II with obstacles.
    fun uniquePathsII() = TestRunner.check("W10 #7 Unique Paths II", block = { uniquePathsWithObstacles(arrayOf(intArrayOf(0,0,0), intArrayOf(0,1,0), intArrayOf(0,0,0))) }, verify = { it == 2 })
    fun uniquePathsII2() = TestRunner.check("W10 #7b Unique Paths II blocked", block = { uniquePathsWithObstacles(arrayOf(intArrayOf(0,1), intArrayOf(0,0))) }, verify = { it == 1 })
    fun uniquePathsII3() = TestRunner.check("W10 #7c Unique Paths II start blocked", block = { uniquePathsWithObstacles(arrayOf(intArrayOf(1))) }, verify = { it == 0 })

    private fun chars(vararg rows: String) = Array(rows.size) { rows[it].toCharArray() }

    // 8) Maximal Square (largest all-1s square side).
    fun maxSquare() = TestRunner.check("W10 #8 Maximal Square", block = { maximalSquare(chars("10100", "10110", "11110")) }, verify = { it == 4 })
    fun maxSquare2() = TestRunner.check("W10 #8b Maximal Square zeros", block = { maximalSquare(chars("0", "0")) }, verify = { it == 0 })
    fun maxSquare3() = TestRunner.check("W10 #8c Maximal Square single", block = { maximalSquare(chars("1")) }, verify = { it == 1 })

    // 9) Maximal Rectangle (largest all-1s rectangle area).
    fun maxRectangle() = TestRunner.check("W10 #9 Maximal Rectangle", block = { maximalRectangle(chars("10100", "10111", "11111", "10010")) }, verify = { it == 6 })
    fun maxRectangle2() = TestRunner.check("W10 #9b Maximal Rectangle zero", block = { maximalRectangle(chars("0")) }, verify = { it == 0 })
    fun maxRectangle3() = TestRunner.check("W10 #9c Maximal Rectangle single", block = { maximalRectangle(chars("1")) }, verify = { it == 1 })

    // 10) Cherry Pickup II — two robots, best combined cherries.
    fun cherryPickupCase() = TestRunner.check("W10 #10 Cherry Pickup II", block = { cherryPickup(arrayOf(intArrayOf(3,1,1), intArrayOf(2,5,1), intArrayOf(1,5,5), intArrayOf(2,1,1))) }, verify = { it == 24 })
    fun cherryPickupCase2() = TestRunner.check("W10 #10b Cherry Pickup II", block = { cherryPickup(arrayOf(intArrayOf(1,0,0,0,0,0,1), intArrayOf(2,0,0,0,0,3,0), intArrayOf(2,0,9,0,0,0,0), intArrayOf(0,3,0,5,4,0,0), intArrayOf(1,0,2,3,0,0,6))) }, verify = { it == 28 })
    fun cherryPickupCase3() = TestRunner.check("W10 #10c Cherry Pickup II 2x2", block = { cherryPickup(arrayOf(intArrayOf(1,1), intArrayOf(1,1))) }, verify = { it == 4 })

    // 11) Burst Balloons (interval DP).
    fun burstBalloons() = TestRunner.check("W10 #11 Burst Balloons", block = { maxCoins(intArrayOf(3,1,5,8)) }, verify = { it == 167 })
    fun burstBalloons2() = TestRunner.check("W10 #11b Burst Balloons", block = { maxCoins(intArrayOf(1,5)) }, verify = { it == 10 })
    fun burstBalloons3() = TestRunner.check("W10 #11c Burst Balloons single", block = { maxCoins(intArrayOf(1)) }, verify = { it == 1 })

    // 12) Palindromic Substrings (count all substrings that are palindromes).
    fun palinSubstrings() = TestRunner.check("W10 #12 Palindromic Substrings", block = { countSubstrings("abc") }, verify = { it == 3 })
    fun palinSubstrings2() = TestRunner.check("W10 #12b Palindromic Substrings", block = { countSubstrings("aaa") }, verify = { it == 6 })
    fun palinSubstrings3() = TestRunner.check("W10 #12c Palindromic Substrings", block = { countSubstrings("abba") }, verify = { it == 6 })

    // 13) Decode Ways.
    fun decodeWays() = TestRunner.check("W10 #13 Decode Ways", block = { w10NumDecodings("12") }, verify = { it == 2 })
    fun decodeWays2() = TestRunner.check("W10 #13b Decode Ways", block = { w10NumDecodings("226") }, verify = { it == 3 })
    fun decodeWays3() = TestRunner.check("W10 #13c Decode Ways leading zero", block = { w10NumDecodings("06") }, verify = { it == 0 })

    // 14) Word Break.
    fun wordBreakCase() = TestRunner.check("W10 #14 Word Break", block = { w10WordBreak("leetcode", listOf("leet", "code")) }, verify = { it })
    fun wordBreakCase2() = TestRunner.check("W10 #14b Word Break reuse", block = { w10WordBreak("applepenapple", listOf("apple", "pen")) }, verify = { it })
    fun wordBreakCase3() = TestRunner.check("W10 #14c Word Break impossible", block = { w10WordBreak("catsandog", listOf("cats", "dog", "sand", "and", "cat")) }, verify = { !it })

    // 15) Perfect Squares (fewest perfect-square numbers summing to n).
    fun perfectSquares() = TestRunner.check("W10 #15 Perfect Squares", block = { numSquares(12) }, verify = { it == 3 })
    fun perfectSquares2() = TestRunner.check("W10 #15b Perfect Squares", block = { numSquares(13) }, verify = { it == 2 })
    fun perfectSquares3() = TestRunner.check("W10 #15c Perfect Squares one", block = { numSquares(1) }, verify = { it == 1 })

    // 16) Coin Change II (count combinations, coins in any order).
    fun coinChangeII() = TestRunner.check("W10 #16 Coin Change II", block = { w10Change(5, intArrayOf(1, 2, 5)) }, verify = { it == 4 })
    fun coinChangeII2() = TestRunner.check("W10 #16b Coin Change II impossible", block = { w10Change(3, intArrayOf(2)) }, verify = { it == 0 })
    fun coinChangeII3() = TestRunner.check("W10 #16c Coin Change II exact", block = { w10Change(10, intArrayOf(10)) }, verify = { it == 1 })

    // 17) Longest Increasing Path in a Matrix (memoized DFS).
    fun longestIncPath() = TestRunner.check("W10 #17 Longest Increasing Path", block = { longestIncreasingPath(arrayOf(intArrayOf(9,9,4), intArrayOf(6,6,8), intArrayOf(2,1,1))) }, verify = { it == 4 })
    fun longestIncPath2() = TestRunner.check("W10 #17b Longest Increasing Path", block = { longestIncreasingPath(arrayOf(intArrayOf(3,4,5), intArrayOf(3,2,6), intArrayOf(2,2,1))) }, verify = { it == 4 })
    fun longestIncPath3() = TestRunner.check("W10 #17c Longest Increasing Path single", block = { longestIncreasingPath(arrayOf(intArrayOf(1))) }, verify = { it == 1 })

    // 18) Dungeon Game (min initial health, reverse grid DP).
    fun dungeon() = TestRunner.check("W10 #18 Dungeon Game", block = { calculateMinimumHP(arrayOf(intArrayOf(-2,-3,3), intArrayOf(-5,-10,1), intArrayOf(10,30,-5))) }, verify = { it == 7 })
    fun dungeon2() = TestRunner.check("W10 #18b Dungeon Game zero", block = { calculateMinimumHP(arrayOf(intArrayOf(0))) }, verify = { it == 1 })
    fun dungeon3() = TestRunner.check("W10 #18c Dungeon Game positive", block = { calculateMinimumHP(arrayOf(intArrayOf(100))) }, verify = { it == 1 })

    // 19) Largest Divisible Subset (any valid order; check size + divisibility).
    fun divisibleSubset() = TestRunner.check("W10 #19 Largest Divisible Subset", block = { largestDivisibleSubset(intArrayOf(1, 2, 3)) }, verify = { it.size == 2 && it.sorted() == listOf(1, 2) })
    fun divisibleSubset2() = TestRunner.check("W10 #19b Largest Divisible Subset chain", block = { largestDivisibleSubset(intArrayOf(1, 2, 4, 8)) }, verify = { it.sorted() == listOf(1, 2, 4, 8) })
    fun divisibleSubset3() = TestRunner.check("W10 #19c Largest Divisible Subset single", block = { largestDivisibleSubset(intArrayOf(3)) }, verify = { it == listOf(3) })

    // 20) Number of Longest Increasing Subsequences.
    fun countLIS() = TestRunner.check("W10 #20 Number of LIS", block = { findNumberOfLIS(intArrayOf(1, 3, 5, 4, 7)) }, verify = { it == 2 })
    fun countLIS2() = TestRunner.check("W10 #20b Number of LIS all equal", block = { findNumberOfLIS(intArrayOf(2, 2, 2, 2, 2)) }, verify = { it == 5 })
    fun countLIS3() = TestRunner.check("W10 #20c Number of LIS increasing", block = { findNumberOfLIS(intArrayOf(1, 2, 3)) }, verify = { it == 1 })

    // ── Part B — complexity validation (complexityAll) ──────────────────────

    // 1) Edit Distance between two 500-char strings: O(500×500) table.
    //    "a"*250+"b"*250 vs "b"*250+"a"*250 → every char replaced → 500.
    fun editDistanceBig() = TestRunner.check("W10-B1 Edit Distance 500x500", budgetMs = 300,
        block = { w10MinDistance("a".repeat(250) + "b".repeat(250), "b".repeat(250) + "a".repeat(250)) }, verify = { it == 500 })

    // 2) LCS of two 1000-char strings: O(1000×1000) table. Shared a/b counts → 900.
    fun lcsBig() = TestRunner.check("W10-B2 LCS 1000x1000", budgetMs = 500,
        block = { w10LCS("a".repeat(500) + "b".repeat(500), "a".repeat(600) + "b".repeat(400)) }, verify = { it == 900 })

    // 3) Distinct Subsequences worst case: "a"*2000 vs "aaa" → C(2000,3) = 1,331,334,000.
    fun distinctSubseqBig() = TestRunner.check("W10-B3 Distinct Subsequences 2000", budgetMs = 300,
        block = { numDistinct("a".repeat(2000), "aaa") }, verify = { it == 1_331_334_000 })

    // 4) 500×500 grid of 1s: answer is 999 (499 down + 500 right steps).
    fun minPathSumBig() = TestRunner.check("W10-B4 Min Path Sum 500x500", budgetMs = 300,
        block = { w10MinPathSum(Array(500) { IntArray(500) { 1 } }) }, verify = { it == 999 })

    // 5) 300×300 all-1s matrix: largest all-1s square AREA is 300*300 = 90,000.
    fun maxSquareBig() = TestRunner.check("W10-B5 Maximal Square 300x300", budgetMs = 300,
        block = { maximalSquare(Array(300) { CharArray(300) { '1' } }) }, verify = { it == 90_000 })

    // 6) 200×200 all-1s matrix: largest rectangle is the whole grid → 40,000.
    fun maxRectangleBig() = TestRunner.check("W10-B6 Maximal Rectangle 200x200", budgetMs = 300,
        block = { maximalRectangle(Array(200) { CharArray(200) { '1' } }) }, verify = { it == 40_000 })

    // 7) 300 identical balloons: each burst contributes 1 → total 300. O(n^3) table.
    fun burstBalloonsBig() = TestRunner.check("W10-B7 Burst Balloons n=300", budgetMs = 400,
        block = { maxCoins(IntArray(300) { 1 }) }, verify = { it == 300 })

    // 8) Coin Change II, amount 10,000 with coins {1,2,5} → exactly 5,004,001 ways.
    fun coinChangeIIBig() = TestRunner.check("W10-B8 Coin Change II amount 10k", budgetMs = 300,
        block = { w10Change(10_000, intArrayOf(1, 2, 5)) }, verify = { it == 5_004_001 })

    // 9) 200×200 snake staircase: every cell continues the path → 40,000.
    fun longestIncPathBig() = TestRunner.check("W10-B9 Longest Increasing Path 200x200", budgetMs = 400,
        block = {
            val n = 200
            val grid = Array(n) { i -> IntArray(n) { j -> i * n + (if (i % 2 == 0) j else n - 1 - j) + 1 } }
            longestIncreasingPath(grid)
        }, verify = { it == 40_000 })

    // 10) Strictly increasing 5,000 elements → exactly one LIS.
    fun countLISBig() = TestRunner.check("W10-B10 Number of LIS n=5000", budgetMs = 300,
        block = { findNumberOfLIS(IntArray(5_000) { it + 1 }) }, verify = { it == 1 })

    fun complexityAll() {
        println("\n━━━━━━━ WEEK 10 Part B — complexity validation ━━━━━━━")
        editDistanceBig(); lcsBig(); distinctSubseqBig(); minPathSumBig(); maxSquareBig()
        maxRectangleBig(); burstBalloonsBig(); coinChangeIIBig(); longestIncPathBig(); countLISBig()
    }
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 10 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════
//
// Each stub throws TODO() so every case above reports [SKIP ⏭️] instead of a wrong
// answer. Replace the TODO() with your implementation and the case runs for real.
// Week 7 name collisions keep the w10 prefix here as well.

// ── String 2D DP ────────────────────────────────────────────────────────────
fun w10MinDistance(word1: String, word2: String): Int = TODO("W10 #1 — edit distance table")
fun w10LCS(text1: String, text2: String): Int = TODO("W10 #2 — classic LCS table")
fun w10LPS(s: String): Int = TODO("W10 #3 — interval DP over palindromes")
fun numDistinct(s: String, t: String): Int = TODO("W10 #4 — rolling 1D DP over t")
fun isInterleave(s1: String, s2: String, s3: String): Boolean = TODO("W10 #5 — 2D reachability DP")

// ── Grid DP ─────────────────────────────────────────────────────────────────
fun w10MinPathSum(grid: Array<IntArray>): Int = TODO("W10 #6 — in-place row DP")
fun uniquePathsWithObstacles(obstacleGrid: Array<IntArray>): Int = TODO("W10 #7 — paths DP with obstacle guard")
fun maximalSquare(matrix: Array<CharArray>): Int = TODO("W10 #8 — min-of-three-neighbours DP, square the side")
fun maximalRectangle(matrix: Array<CharArray>): Int = TODO("W10 #9 — histogram per row + monotonic stack")
fun cherryPickup(grid: Array<IntArray>): Int = TODO("W10 #10 — two robots per row, 3-way best")

// ── Interval / 1D DP ────────────────────────────────────────────────────────
fun maxCoins(nums: IntArray): Int = TODO("W10 #11 — interval DP, think last balloon burst")
fun countSubstrings(s: String): Int = TODO("W10 #12 — expand around 2n-1 centres")
fun w10NumDecodings(s: String): Int = TODO("W10 #13 — two-step Fibonacci with zero guard")
fun w10WordBreak(s: String, wordDict: List<String>): Boolean = TODO("W10 #14 — boolean DP over prefixes")
fun numSquares(n: Int): Int = TODO("W10 #15 — min over perfect-square predecessors")
fun w10Change(amount: Int, coins: IntArray): Int = TODO("W10 #16 — coin-outer 1D DP counts combinations")
fun longestIncreasingPath(matrix: Array<IntArray>): Int = TODO("W10 #17 — memoized DFS, O(n*m) states")
fun calculateMinimumHP(dungeon: Array<IntArray>): Int = TODO("W10 #18 — reverse DP with max(1, ...)")
fun largestDivisibleSubset(nums: IntArray): List<Int> = TODO("W10 #19 — sort + LIS-style parent rebuild")
fun findNumberOfLIS(nums: IntArray): Int = TODO("W10 #20 — length + count arrays per index")
