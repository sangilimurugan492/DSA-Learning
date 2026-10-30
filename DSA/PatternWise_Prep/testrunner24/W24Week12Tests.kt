package testrunner24

import testrunner.Week06Tests

/**
 * W24Week12Tests.kt — 24-Week Plan, Week 12: Greedy — Fundamentals + Bit Manipulation Easy
 * 11 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week06Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week12Tests {
    private val src = Week06Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 12 — Greedy — Fundamentals + Bit Manipulation Easy ━━━━━━━")
        src.maxProfitI() // 24W #1 Best Time to Buy & Sell Stock I (old §1)
        src.findContentChildren() // 24W #2 Assign Cookies (old §2)
        src.lemonadeChange() // 24W #3 Lemonade Change (old §3)
        src.maxProfitII() // 24W #4 Best Time to Buy & Sell Stock II (old §8)
        src.canJump() // 24W #5 Jump Game (old §9)
        src.jump() // 24W #6 Jump Game II (old §10)
        src.canCompleteCircuit() // 24W #7 Gas Station (old §11)
        src.singleNumberXor() // 24W #8 Single Number (old §4)
        src.hammingWeight() // 24W #9 Number of 1 Bits (old §5)
        src.missingNumber() // 24W #10 Missing Number (old §6)
        src.powerOfTwo() // 24W #11 Power of Two (old §7)
    }
}
