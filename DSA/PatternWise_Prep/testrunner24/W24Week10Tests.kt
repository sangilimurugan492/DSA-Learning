package testrunner24

import testrunner.Week05Tests

/**
 * W24Week10Tests.kt — 24-Week Plan, Week 10: Tree — Fundamentals & BST
 * 12 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week05Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week10Tests {
    private val src = Week05Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 10 — Tree — Fundamentals & BST ━━━━━━━")
        src.maxDepth() // 24W #1 Maximum Depth of Binary Tree (old §1)
        src.isSameTree() // 24W #2 Same Tree (old §2)
        src.isBalanced() // 24W #3 Balanced Binary Tree (old §3)
        src.isSubtree() // 24W #4 Subtree of Another Tree (old §4)
        src.invertTree() // 24W #5 Invert Binary Tree (old §5)
        src.isSymmetric() // 24W #6 Symmetric Tree (old §6)
        src.searchBST() // 24W #7 Search in BST (old §7)
        src.traversals() // 24W #8 Tree Traversals (In/Pre/Post) (old §8)
        src.diameterOfBinaryTree() // 24W #9 Diameter of Binary Tree (old §9)
        src.lowestCommonAncestor() // 24W #10 Lowest Common Ancestor (old §10)
        src.insertIntoBST() // 24W #11 Insert into BST (old §11)
        src.deleteNodeBST() // 24W #12 Delete Node in BST (old §12)
    }
}
