package DSA.PatternWise_Prep.mocktest

/**
 * Week05Mock.kt — MOCK TEST Week 5: Tree + Heap + Trie
 * 5 problems · suggested time 110 min. Uses bundled MockTreeNode/mockTree helpers.
 */

fun main(args: Array<String>) {
    mockInit(args)
    println("\n━━━━━━━ WEEK 5 MOCK — Tree + Heap + Trie (5 problems · 110 min) ━━━━━━━")
    mockCheck("M5 #1 Max Depth (10 min)", block = { w5mMaxDepth(mockTree(3, 9, 20, null, null, 15, 7)) }, verify = { it == 3 })
    mockCheck("M5 #2 Diameter (20 min)", block = { w5mDiameter(mockTree(1, 2, 3, 4, 5)) }, verify = { it == 3 })
    mockCheck("M5 #3 Validate BST (20 min)", block = { w5mIsValidBST(mockTree(5, 1, 4, null, null, 3, 6)) }, verify = { !it })
    mockCheck("M5 #4 Right Side View (20 min)", block = { w5mRightView(mockTree(1, 2, 3, null, 5, null, 4)) }, verify = { it == listOf(1, 3, 4) })
    mockCheck("M5 #5 Kth Largest (25 min)", block = { w5mKthLargest(intArrayOf(3, 2, 1, 5, 6, 4), 2) }, verify = { it == 5 })
    mockSummary()
}

// ═══════════════════════════════════════════════════════════
// 👇 SOLUTIONS — implement as you solve (leave TODO() to SKIP) 👇
// ═══════════════════════════════════════════════════════════

fun w5mMaxDepth(root: MockTreeNode?): Int = TODO()
fun w5mDiameter(root: MockTreeNode?): Int = TODO()
fun w5mIsValidBST(root: MockTreeNode?): Boolean = TODO()
fun w5mRightView(root: MockTreeNode?): List<Int> = TODO()
fun w5mKthLargest(nums: IntArray, k: Int): Int = TODO()
