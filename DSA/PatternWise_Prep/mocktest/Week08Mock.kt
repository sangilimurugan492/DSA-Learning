package mocktest

/**
 * Week08Mock.kt — MOCK TEST Week 8: Graph + Matrix + String Simulation
 * 5 problems · suggested time 110 min. Re-solve from scratch — no peeking at Week08Tests!
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 8 MOCK — Graph + Matrix + String (5 problems · 110 min) ━━━━━━━")
    mockCheck("M8 #1 Number of Islands (25 min)", block = { w8mNumIslands(arrayOf(charArrayOf('1', '1', '0'), charArrayOf('1', '0', '0'), charArrayOf('0', '0', '1'))) }, verify = { it == 2 })
    mockCheck("M8 #2 Rotting Oranges (25 min)", block = { w8mRotting(arrayOf(intArrayOf(2, 1, 1), intArrayOf(1, 1, 0), intArrayOf(0, 1, 1))) }, verify = { it == 4 })
    mockCheck("M8 #3 Course Schedule (20 min)", block = { w8mCanFinish(2, arrayOf(intArrayOf(1, 0))) }, verify = { it })
    mockCheck("M8 #4 Spiral Matrix (20 min)", block = { w8mSpiral(arrayOf(intArrayOf(1, 2, 3), intArrayOf(4, 5, 6), intArrayOf(7, 8, 9))) }, verify = { it == listOf(1, 2, 3, 6, 9, 8, 7, 4, 5) })
    mockCheck("M8 #5 atoi (20 min)", block = { w8mAtoi("   -42") }, verify = { it == -42 })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w8mNumIslands(grid: Array<CharArray>): Int = TODO()
fun w8mRotting(grid: Array<IntArray>): Int = TODO()
fun w8mCanFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean = TODO()
fun w8mSpiral(matrix: Array<IntArray>): List<Int> = TODO()
fun w8mAtoi(s: String): Int = TODO()
