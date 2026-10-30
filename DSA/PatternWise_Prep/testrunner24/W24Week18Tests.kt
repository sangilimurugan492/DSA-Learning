package testrunner24

import testrunner.Week08Tests

/**
 * W24Week18Tests.kt — 24-Week Plan, Week 18: Matrix + Simulation + String Parsing
 * 13 problems. DELEGATES to the original test class — implement each solution
 * stub ONCE in testrunner/Week08Tests.kt, then everything (old + new plan) passes.
 *
 * Run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 * (comment/uncomment weeks in TestRunner24.kt main())
 */
class W24Week18Tests {
    private val src = Week08Tests()
    fun runAll() {
        println("\n━━━━━━━ 24W WEEK 18 — Matrix + Simulation + String Parsing ━━━━━━━")
        src.addBinary() // 24W #1 Add Binary (old §1)
        src.addStrings() // 24W #2 Add Strings (old §2)
        src.addDigits() // 24W #3 Add Digits (Digital Root) (old §3)
        src.fizzBuzz() // 24W #4 Fizz Buzz (old §4)
        src.setZeroes() // 24W #5 Set Matrix Zeroes (old §14)
        src.spiralOrder() // 24W #6 Spiral Matrix (old §15)
        src.rotateMatrix() // 24W #7 Rotate Image (old §16)
        src.gameOfLifeCase() // 24W #8 Game of Life (old §17)
        src.generateMatrix() // 24W #9 Spiral Matrix II (old §21)
        src.myAtoi() // 24W #10 String to Integer (atoi) (old §18)
        src.intToRoman() // 24W #11 Integer to Roman (old §19)
        src.zigzag() // 24W #12 ZigZag Conversion (old §20)
        src.fullJustify() // 24W #13 Text Justification (old §23)
    }
}
