package testrunner

import kotlin.math.abs

/**
 * Week01Tests.kt — Week 1: Two Pointer + Two Pointer In-Place
 * Test class + solution stubs. Implement the stubs at the bottom as you solve.
 *
 * Test numbering (W1 #1 … #25) matches weeks/Week01.md; the input/expected pairs come
 * from practice/Week01_Practice.md (Part A) — every problem has all 3 cases wired up —
 * and the 10 timed cases (W1-B1 … W1-B10) come from its Part B. Part B is commented out
 * in runAll(); call complexityAll() to time-check the O(N)/O(N log N) solutions.
 */

class Week01Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 1 — Two Pointer ━━━━━━━")
        // Problems #1–#23 — 3 Part-A cases each (practice/Week01_Practice.md)
        twoSumSorted(); validPalindrome(); isSubsequence(); moveZeroes(); mergeSortedArray()
        removeElement(); intersectII(); threeSum(); threeSumClosest(); fourSum()
        containerWithMostWater(); sortColors(); backspaceCompare(); twoSumLessThanK(); kSumPairs()
        boatsToSavePeople(); trappingRainWater(); removeDuplicatesI(); removeDuplicatesII()
        replaceGreatestRight(); sortedSquares(); rotateArray(); nextPermutation()
        // Re-solves: #24 Sort Colors, #25 Remove Element (same cases as #12 / #6)
        sortColorsRe(); removeElementRe()
        // Complexity validation (Part B, cases 1–10) — uncomment to time-check against budgets:
        // complexityAll()
    }

    fun complexityAll() {
        trapBig(); trapIncreasingBig(); threeSumZerosBig(); threeSumConsecutiveBig(); maxAreaBig()
        sortColorsBig(); mergeBig(); sortedSquaresBig(); rotateBig(); nextPermutationBig()
    }

    fun twoSumSorted() {
        TestRunner.check("W1 #1 Two Sum II (Sorted), basic", block = { twoSumSorted(intArrayOf(2, 7, 11, 15), 9) }, verify = { it.contentEquals(intArrayOf(1, 2)) })
        TestRunner.check("W1 #1b Two Sum II (Sorted), even length", block = { twoSumSorted(intArrayOf(2, 3, 4), 6) }, verify = { it.contentEquals(intArrayOf(1, 3)) })
        TestRunner.check("W1 #1c Two Sum II (Sorted), negatives", block = { twoSumSorted(intArrayOf(-1, 0), -1) }, verify = { it.contentEquals(intArrayOf(1, 2)) })
    }
    fun validPalindrome() {
        TestRunner.check("W1 #2 Valid Palindrome, mixed case + punctuation", block = { isPalindrome("A man, a plan, a canal: Panama") }, verify = { it })
        TestRunner.check("W1 #2b Valid Palindrome, near miss", block = { isPalindrome("race a car") }, verify = { !it })
        TestRunner.check("W1 #2c Valid Palindrome, only spaces", block = { isPalindrome(" ") }, verify = { it })
    }
    fun isSubsequence() {
        TestRunner.check("W1 #3 Is Subsequence, basic", block = { isSubsequence("abc", "ahbgdc") }, verify = { it })
        TestRunner.check("W1 #3b Is Subsequence, out of order", block = { isSubsequence("axc", "ahbgdc") }, verify = { !it })
        TestRunner.check("W1 #3c Is Subsequence, empty s", block = { isSubsequence("", "ahbgdc") }, verify = { it })
    }
    fun moveZeroes() {
        TestRunner.check("W1 #4 Move Zeroes, basic", block = { val a = intArrayOf(0, 1, 0, 3, 12); moveZeroes(a); a.toList() }, verify = { it == listOf(1, 3, 12, 0, 0) })
        TestRunner.check("W1 #4b Move Zeroes, single zero", block = { val a = intArrayOf(0); moveZeroes(a); a.toList() }, verify = { it == listOf(0) })
        TestRunner.check("W1 #4c Move Zeroes, leading zeros", block = { val a = intArrayOf(0, 0, 1); moveZeroes(a); a.toList() }, verify = { it == listOf(1, 0, 0) })
    }
    fun mergeSortedArray() {
        TestRunner.check("W1 #5 Merge Sorted Array, basic", block = { val a = intArrayOf(1, 2, 3, 0, 0, 0); merge(a, 3, intArrayOf(2, 5, 6), 3); a.toList() }, verify = { it == listOf(1, 2, 2, 3, 5, 6) })
        TestRunner.check("W1 #5b Merge Sorted Array, empty nums2", block = { val a = intArrayOf(1); merge(a, 1, intArrayOf(), 0); a.toList() }, verify = { it == listOf(1) })
        TestRunner.check("W1 #5c Merge Sorted Array, empty nums1", block = { val a = intArrayOf(0); merge(a, 0, intArrayOf(1), 1); a.toList() }, verify = { it == listOf(1) })
    }
    fun removeElement() {
        TestRunner.check("W1 #6 Remove Element, basic", block = { val a = intArrayOf(3, 2, 2, 3); val k = removeElement(a, 3); k to a.copyOf(k).sorted() }, verify = { it == (2 to listOf(2, 2)) })
        TestRunner.check("W1 #6b Remove Element, scattered", block = { val a = intArrayOf(0, 1, 2, 2, 3, 0, 4, 2); val k = removeElement(a, 2); k to a.copyOf(k).sorted() }, verify = { it == (5 to listOf(0, 0, 1, 3, 4)) })
        TestRunner.check("W1 #6c Remove Element, empty", block = { val a = intArrayOf(); val k = removeElement(a, 0); k to a.copyOf(k).sorted() }, verify = { it == (0 to emptyList<Int>()) })
    }
    fun intersectII() {
        TestRunner.check("W1 #7 Intersection II, duplicates count", block = { intersect(intArrayOf(1, 2, 2, 1), intArrayOf(2, 2)).sorted() }, verify = { it == listOf(2, 2) })
        TestRunner.check("W1 #7b Intersection II, partial overlap (any order)", block = { intersect(intArrayOf(4, 9, 5), intArrayOf(9, 4, 9, 8, 4)).sorted() }, verify = { it == listOf(4, 9) })
        TestRunner.check("W1 #7c Intersection II, empty", block = { intersect(intArrayOf(), intArrayOf(1)).sorted() }, verify = { it == emptyList<Int>() })
    }
    fun threeSum() {
        TestRunner.check("W1 #8 Three Sum, basic", block = { threeSum(intArrayOf(-1, 0, 1, 2, -1, -4)).map { it.sorted() }.toSet() }, verify = { it == setOf(listOf(-1, -1, 2), listOf(-1, 0, 1)) })
        TestRunner.check("W1 #8b Three Sum, all same", block = { threeSum(intArrayOf(0, 0, 0, 0)).map { it.sorted() }.toSet() }, verify = { it == setOf(listOf(0, 0, 0)) })
        TestRunner.check("W1 #8c Three Sum, no triplet", block = { threeSum(intArrayOf(1, 2, 3)).map { it.sorted() }.toSet() }, verify = { it == emptySet<List<Int>>() })
    }
    fun threeSumClosest() {
        TestRunner.check("W1 #9 Three Sum Closest, basic", block = { threeSumClosest(intArrayOf(-1, 2, 1, -4), 1) }, verify = { it == 2 })
        TestRunner.check("W1 #9b Three Sum Closest, far target", block = { threeSumClosest(intArrayOf(0, 0, 0), 1) }, verify = { it == 0 })
        TestRunner.check("W1 #9c Three Sum Closest, extreme target", block = { threeSumClosest(intArrayOf(1, 1, 1, 0), -100) }, verify = { it == 2 })
    }
    fun fourSum() {
        TestRunner.check("W1 #10 Four Sum, basic", block = { fourSum(intArrayOf(1, 0, -1, 0, -2, 2), 0).map { it.sorted() }.toSet() }, verify = { it == setOf(listOf(-2, -1, 1, 2), listOf(-2, 0, 0, 2), listOf(-1, 0, 0, 1)) })
        TestRunner.check("W1 #10b Four Sum, all same", block = { fourSum(intArrayOf(2, 2, 2, 2, 2), 8).map { it.sorted() }.toSet() }, verify = { it == setOf(listOf(2, 2, 2, 2)) })
        TestRunner.check("W1 #10c Four Sum, empty", block = { fourSum(intArrayOf(), 0).map { it.sorted() }.toSet() }, verify = { it == emptySet<List<Int>>() })
    }
    fun containerWithMostWater() {
        TestRunner.check("W1 #11 Container With Most Water, basic", block = { maxArea(intArrayOf(1, 8, 6, 2, 5, 4, 8, 3, 7)) }, verify = { it == 49 })
        TestRunner.check("W1 #11b Container With Most Water, two bars", block = { maxArea(intArrayOf(1, 1)) }, verify = { it == 1 })
        TestRunner.check("W1 #11c Container With Most Water, symmetric", block = { maxArea(intArrayOf(4, 3, 2, 1, 4)) }, verify = { it == 16 })
    }
    fun sortColors() {
        TestRunner.check("W1 #12 Sort Colors, basic", block = { val a = intArrayOf(2, 0, 2, 1, 1, 0); sortColors(a); a.toList() }, verify = { it == listOf(0, 0, 1, 1, 2, 2) })
        TestRunner.check("W1 #12b Sort Colors, single", block = { val a = intArrayOf(2); sortColors(a); a.toList() }, verify = { it == listOf(2) })
        TestRunner.check("W1 #12c Sort Colors, two elems", block = { val a = intArrayOf(1, 0); sortColors(a); a.toList() }, verify = { it == listOf(0, 1) })
    }
    fun backspaceCompare() {
        TestRunner.check("W1 #13 Backspace Compare, both reduce to ac", block = { backspaceCompare("ab#c", "ad#c") }, verify = { it })
        TestRunner.check("W1 #13b Backspace Compare, both empty", block = { backspaceCompare("ab##", "c#d#") }, verify = { it })
        TestRunner.check("W1 #13c Backspace Compare, different results", block = { backspaceCompare("a#c", "b") }, verify = { !it })
    }
    fun twoSumLessThanK() {
        TestRunner.check("W1 #14 Two Sum < K, basic", block = { twoSumLessThanK(intArrayOf(34, 23, 1, 24, 75, 33, 54, 8), 60) }, verify = { it == 58 })
        TestRunner.check("W1 #14b Two Sum < K, no pair under k", block = { twoSumLessThanK(intArrayOf(10, 20, 30), 15) }, verify = { it == -1 })
        TestRunner.check("W1 #14c Two Sum < K, sum equals k (not <)", block = { twoSumLessThanK(intArrayOf(1, 2), 3) }, verify = { it == -1 })
    }
    fun kSumPairs() {
        TestRunner.check("W1 #15 K-Sum Pairs, basic", block = { maxOperations(intArrayOf(1, 2, 3, 4), 5) }, verify = { it == 2 })
        TestRunner.check("W1 #15b K-Sum Pairs, leftover element", block = { maxOperations(intArrayOf(3, 1, 3, 4, 3), 6) }, verify = { it == 1 })
        TestRunner.check("W1 #15c K-Sum Pairs, all same", block = { maxOperations(intArrayOf(1, 1, 1, 1), 2) }, verify = { it == 2 })
    }
    fun boatsToSavePeople() {
        TestRunner.check("W1 #16 Boats to Save, pair fits", block = { numRescueBoats(intArrayOf(1, 2), 3) }, verify = { it == 1 })
        TestRunner.check("W1 #16b Boats to Save, mixed", block = { numRescueBoats(intArrayOf(3, 2, 2, 1), 3) }, verify = { it == 3 })
        TestRunner.check("W1 #16c Boats to Save, pairs + singles", block = { numRescueBoats(intArrayOf(1, 2, 3, 4), 4) }, verify = { it == 3 })
    }
    fun trappingRainWater() {
        TestRunner.check("W1 #17 Trapping Rain Water, classic", block = { trap(intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)) }, verify = { it == 6 })
        TestRunner.check("W1 #17b Trapping Rain Water, deep valley", block = { trap(intArrayOf(4, 2, 0, 3, 2, 5)) }, verify = { it == 9 })
        TestRunner.check("W1 #17c Trapping Rain Water, increasing (no water)", block = { trap(intArrayOf(1, 2, 3, 4)) }, verify = { it == 0 })
    }
    fun removeDuplicatesI() {
        TestRunner.check("W1 #18 Remove Duplicates I, basic", block = { val a = intArrayOf(1, 1, 2); val k = removeDuplicatesI(a); k to a.copyOf(k).toList() }, verify = { it == (2 to listOf(1, 2)) })
        TestRunner.check("W1 #18b Remove Duplicates I, many dups", block = { val a = intArrayOf(0, 0, 1, 1, 1, 2, 2, 3, 3, 4); val k = removeDuplicatesI(a); k to a.copyOf(k).toList() }, verify = { it == (5 to listOf(0, 1, 2, 3, 4)) })
        TestRunner.check("W1 #18c Remove Duplicates I, single", block = { val a = intArrayOf(1); val k = removeDuplicatesI(a); k to a.copyOf(k).toList() }, verify = { it == (1 to listOf(1)) })
    }
    fun removeDuplicatesII() {
        TestRunner.check("W1 #19 Remove Duplicates II, triple run", block = { val a = intArrayOf(1, 1, 1, 2, 2, 3); val k = removeDuplicatesII(a); k to a.copyOf(k).toList() }, verify = { it == (5 to listOf(1, 1, 2, 2, 3)) })
        TestRunner.check("W1 #19b Remove Duplicates II, mixed runs", block = { val a = intArrayOf(0, 0, 1, 1, 1, 1, 2, 3, 3); val k = removeDuplicatesII(a); k to a.copyOf(k).toList() }, verify = { it == (7 to listOf(0, 0, 1, 1, 2, 3, 3)) })
        TestRunner.check("W1 #19c Remove Duplicates II, single", block = { val a = intArrayOf(1); val k = removeDuplicatesII(a); k to a.copyOf(k).toList() }, verify = { it == (1 to listOf(1)) })
    }
    fun replaceGreatestRight() {
        TestRunner.check("W1 #20 Replace Greatest Right, basic", block = { replaceElements(intArrayOf(17, 18, 5, 4, 6, 1)).toList() }, verify = { it == listOf(18, 6, 6, 6, 1, -1) })
        TestRunner.check("W1 #20b Replace Greatest Right, single", block = { replaceElements(intArrayOf(400)).toList() }, verify = { it == listOf(-1) })
        TestRunner.check("W1 #20c Replace Greatest Right, two elems", block = { replaceElements(intArrayOf(2, 1)).toList() }, verify = { it == listOf(1, -1) })
    }
    fun sortedSquares() {
        TestRunner.check("W1 #21 Sorted Squares, negatives", block = { sortedSquares(intArrayOf(-4, -1, 0, 3, 10)).toList() }, verify = { it == listOf(0, 1, 9, 16, 100) })
        TestRunner.check("W1 #21b Sorted Squares, mixed", block = { sortedSquares(intArrayOf(-5, -3, 2, 3)).toList() }, verify = { it == listOf(4, 9, 9, 25) })
        TestRunner.check("W1 #21c Sorted Squares, single", block = { sortedSquares(intArrayOf(0)).toList() }, verify = { it == listOf(0) })
    }
    fun rotateArray() {
        TestRunner.check("W1 #22 Rotate Array, basic", block = { val a = intArrayOf(1, 2, 3, 4, 5, 6, 7); rotate(a, 3); a.toList() }, verify = { it == listOf(5, 6, 7, 1, 2, 3, 4) })
        TestRunner.check("W1 #22b Rotate Array, no rotation", block = { val a = intArrayOf(1); rotate(a, 0); a.toList() }, verify = { it == listOf(1) })
        TestRunner.check("W1 #22c Rotate Array, k > n", block = { val a = intArrayOf(-1); rotate(a, 2); a.toList() }, verify = { it == listOf(-1) })
    }
    fun nextPermutation() {
        TestRunner.check("W1 #23 Next Permutation, basic", block = { val a = intArrayOf(1, 2, 3); nextPermutation(a); a.toList() }, verify = { it == listOf(1, 3, 2) })
        TestRunner.check("W1 #23b Next Permutation, last permutation wraps", block = { val a = intArrayOf(3, 2, 1); nextPermutation(a); a.toList() }, verify = { it == listOf(1, 2, 3) })
        TestRunner.check("W1 #23c Next Permutation, duplicates", block = { val a = intArrayOf(1, 1, 5); nextPermutation(a); a.toList() }, verify = { it == listOf(1, 5, 1) })
    }

    // Re-solves (Week01_Practice.md #24 / #25 — same cases as #12 / #6)
    fun sortColorsRe() {
        TestRunner.check("W1 #24 Sort Colors (re-solve), basic", block = { val a = intArrayOf(2, 0, 2, 1, 1, 0); sortColors(a); a.toList() }, verify = { it == listOf(0, 0, 1, 1, 2, 2) })
        TestRunner.check("W1 #24b Sort Colors (re-solve), single", block = { val a = intArrayOf(2); sortColors(a); a.toList() }, verify = { it == listOf(2) })
        TestRunner.check("W1 #24c Sort Colors (re-solve), two elems", block = { val a = intArrayOf(1, 0); sortColors(a); a.toList() }, verify = { it == listOf(0, 1) })
    }
    fun removeElementRe() {
        TestRunner.check("W1 #25 Remove Element (re-solve), basic", block = { val a = intArrayOf(3, 2, 2, 3); val k = removeElement(a, 3); k to a.copyOf(k).sorted() }, verify = { it == (2 to listOf(2, 2)) })
        TestRunner.check("W1 #25b Remove Element (re-solve), scattered", block = { val a = intArrayOf(0, 1, 2, 2, 3, 0, 4, 2); val k = removeElement(a, 2); k to a.copyOf(k).sorted() }, verify = { it == (5 to listOf(0, 0, 1, 3, 4)) })
        TestRunner.check("W1 #25c Remove Element (re-solve), empty", block = { val a = intArrayOf(); val k = removeElement(a, 0); k to a.copyOf(k).sorted() }, verify = { it == (0 to emptyList<Int>()) })
    }

    // ── Part B — complexity validation (Week01_Practice.md cases 1–10) ─────────────
    // Expectations re-derived; the doc's values were wrong in 4 places:
    //   B1: 99,999 (33,333 valleys × 3 water units each — doc said 100,000, old test 100_002)
    //   B4: 499,500 unique triplets for -1000..999 (doc's 999 only counted the (a,0,-a) form)
    //   B5: heights are divided by 100 — on a raw 1..100k ramp the true max area is
    //       50,000 × 50,000 = 2.5e9, which overflows Int, and the doc's 99,999 is only the
    //       end-to-end area, not the maximum. Scaled ramp → 50,000 × 500 = 25,000,000.
    //   B9: rotating right by k = n - 1 is a shift LEFT by 1 (doc said "shifted right by 1")
    fun trapBig() = TestRunner.check("W1-B1 Trapping Rain Water, [3,0,3]×100k", budgetMs = 100, block = { trap(IntArray(100_000) { if (it % 3 == 1) 0 else 3 }) }, verify = { it == 99_999 })
    fun trapIncreasingBig() = TestRunner.check("W1-B2 Trapping Rain Water, strictly increasing 200k", budgetMs = 100, block = { trap(IntArray(200_000) { it }) }, verify = { it == 0 })
    fun threeSumZerosBig() = TestRunner.check("W1-B3 Three Sum, 3k zeros → one triplet", budgetMs = 500, block = { threeSum(IntArray(3000)).map { it.sorted() }.toSet() }, verify = { it == setOf(listOf(0, 0, 0)) })
    fun threeSumConsecutiveBig() = TestRunner.check("W1-B4 Three Sum, -1000..999 → 499,500 triplets", budgetMs = 1000, block = { threeSum(IntArray(2000) { it - 1000 }) }, verify = { it.size == 499_500 && it.all { t -> t.size == 3 && t.sum() == 0 } })
    fun maxAreaBig() = TestRunner.check("W1-B5 Container With Most Water, decreasing 100k", budgetMs = 100, block = { maxArea(IntArray(100_000) { (100_000 - it) / 100 }) }, verify = { it == 25_000_000 })
    fun sortColorsBig() = TestRunner.check("W1-B6 Sort Colors, 1M random 0/1/2", budgetMs = 300, block = { val rnd = kotlin.random.Random(42); val a = IntArray(1_000_000) { rnd.nextInt(3) }; val sum = a.sum(); sortColors(a); sum to a }, verify = { it.first == it.second.sum() && it.second.size == 1_000_000 && isSorted(it.second) })
    fun mergeBig() = TestRunner.check("W1-B7 Merge Sorted Array, m=n=500k", budgetMs = 300, block = { val n1 = IntArray(1_000_000); for (i in 0 until 500_000) n1[i] = i + 1; merge(n1, 500_000, IntArray(500_000) { 500_001 + it }, 500_000); n1 }, verify = { isSorted(it) && it[0] == 1 && it[999_999] == 1_000_000 })
    fun sortedSquaresBig() = TestRunner.check("W1-B8 Sorted Squares, 500k ascending", budgetMs = 300, block = { sortedSquares(IntArray(500_000) { it / 6 - 41_000 }) }, verify = { isSorted(it) && it.size == 500_000 && it[0] == 0 && it[499_999] == 1_792_082_889 })
    fun rotateBig() = TestRunner.check("W1-B9 Rotate Array, 1M k=999,999 (≡ shift left by 1)", budgetMs = 200, block = { val a = IntArray(1_000_000) { it + 1 }; rotate(a, 999_999); a }, verify = { it[0] == 2 && it[1] == 3 && it[999_999] == 1 })
    fun nextPermutationBig() = TestRunner.check("W1-B10 Next Permutation, descending 500k → ascending", budgetMs = 200, block = { val a = IntArray(500_000) { 500_000 - it }; nextPermutation(a); a }, verify = { it[0] == 1 && isSorted(it) && it[499_999] == 500_000 })
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 1 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

fun twoSumSorted(numbers: IntArray, target: Int): IntArray {
    var left = 0
    var right = numbers.size - 1
    var sum : Int

    while (left < right) {
        sum = numbers[left] + numbers[right]
        when {
            sum == target -> return intArrayOf(left + 1, right + 1)
            sum < target -> left++
            else -> right--
        }
    }
    return intArrayOf(0,0)
}
fun isPalindrome(s: String): Boolean {
    var left = 0
    var right = s.length - 1
    while (left < right) {
        while(left < right && !s[left].isLetterOrDigit()) left++
        while(left < right &&!s[right].isLetterOrDigit()) right--
        if (s[left].lowercaseChar() != s[right].lowercaseChar()) return false
        left++
        right--
    }
    return true
}
fun isSubsequence(s: String, t: String): Boolean {
    if (s.isEmpty()) return true
    if (t.isEmpty()) return false
    if (s.length > t.length) return false

    var sIndex = 0

    for (tIndex in t.indices) {
        if (sIndex < s.length && s[sIndex] == t[tIndex]) {
            sIndex++
        }
    }
    return sIndex == s.length

//    var i = 0
//    var j = 0
//    while (i < s.length && j < t.length) {
//        while (s[i] != t[j]) j++
//        i++
//        j++
//    }
//
//    return i == s.length
}
fun moveZeroes(nums: IntArray): Unit {
//    if (nums.size > 1) {
        var slow = 0
        var temp : Int

        for (fast in nums.indices) {
            if (nums[fast] != 0) {
                temp = nums[fast]
                nums[fast] = nums[slow]
                nums[slow] = temp
//                nums[slow] = nums[fast] + nums[slow]
//                nums[fast] = nums[slow] - nums[fast]
//                nums[slow] = nums[slow] - nums[fast]
                slow++
            }
        }
//    }

//    var insertPos = 0
//    for (i in nums.indices) {
//        if (nums[i] != 0) {
//            nums[insertPos++] = nums[i]
//        }
//    }
//
//    while (insertPos < nums.size) {
//        nums[insertPos++] = 0
//    }
}
fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int): Unit {
    var i = m - 1
    var j = n - 1
    var k = (m + n) - 1

    while (j >= 0) {
        if (i>= 0 && nums1[i] > nums2[j]) {
            nums1[k] = nums1[i]
            i--
        } else {
            nums1[k] = nums2[j]
            j--
        }
        k--
    }
}
fun removeElement(nums: IntArray, `val`: Int): Int {
    var slow = 0
    var temp : Int

    for (fast in nums.indices) {
        if (nums[fast] != `val`) {
            temp = nums[fast]
            nums[fast] = nums[slow]
            nums[slow] = temp
            slow++
        }
    }
    return slow
}
fun intersect(nums1: IntArray, nums2: IntArray): IntArray {
    //[1,2,2,1], [2,2]	[2,2]	Duplicates count
    nums1.sort()
    nums2.sort()

    var p1 = 0
    var p2 = 0
    val result = mutableListOf<Int>()

    while (p1 < nums1.size && p2 < nums2.size) {
        when  {
            (nums1[p1] == nums2[p2]) -> {
                result.add(nums1[p1])
                p1++
                p2++
            }
            nums1[p1] < nums2[p2] -> {
                p1++
            }
            else -> p2++
        }
    }
    return result.toIntArray()
}
fun threeSum(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()

    nums.sort()

    for (i in nums.indices) {

        if (i > 0 && nums[i] == nums[i - 1]) continue
        var left = i + 1
        var right = nums.size - 1

        while (left < right) {

            val sum = nums[left] + nums[right] + nums[i]

            when {
                sum < 0 -> left++
                sum > 0 -> right--
                else -> {
                    result.add(listOf(nums[left], nums[right], nums[i]))
                    left++
                    right--
                    while (left < right && nums[left] == nums[left - 1]) left++
                    while (left < right && nums[right] == nums[right + 1]) right--
                }

            }
        }
    }

    return result
}
fun threeSumClosest(nums: IntArray, target: Int): Int {
    nums.sort()
    var closest = nums[0] + nums[1] + nums[2]

    for (i in 0 until nums.size - 2) {
        var left = i + 1
        var right = nums.size - 1

        while (left < right) {
            val sum = nums[left] + nums[right] + nums[i]
            if(abs(sum - target) < abs(closest - target)) {
                closest = sum
            }
            when {
                sum < target -> left++
                sum > target -> right--
                else -> return sum
            }
        }
    }
    return closest
}
fun fourSum(nums: IntArray, target: Int): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    nums.sort()
    val n = nums.size
    for (i in 0 .. n - 3) {
        if (i > 0 && nums[i] == nums[i - 1]) continue

        for (j in i + 1 until n - 2) {
            if (j > i + 1 && nums[j - 1] == nums[j]) continue

            var left = j + 1
            var right = n - 1

            while (left < right) {
                val sum : Long = nums[i].toLong() + nums[j].toLong() + nums[left].toLong() + nums[right].toLong()

                when {
                    sum < target -> left++
                    sum > target -> right--
                    else -> {
                        result.add(listOf(nums[i], nums[j], nums[left], nums[right]))
                        left++
                        right--
                        while (left < right && nums[left] == nums[left + 1]) left++
                        while (left < right && nums[right] == nums[right - 1]) right--
                    }
                }
            }
        }
    }
    return result
}
fun maxArea(height: IntArray): Int {
    var maxArea = 0
    var left = 0
    var right = height.size - 1
    var minHeight : Int

    while (left < right) {
        minHeight = minOf(height[left], height[right])
        maxArea = maxOf(maxArea, ((right - left) * minHeight))
        if (height[left] < height[right]) left++ else right--
    }
    return maxArea
}
fun sortColors(nums: IntArray): Unit {
    //[2,0,2,1,1,0]	[0,0,1,1,2,2]	Basic
    var left = 0
    var right = nums.size - 1
    var index = 0
    while (index <= right) {
        when {
            nums[index] == 0 -> {
                val temp = nums[left]
                nums[left] = nums[index]
                nums[index] = temp
                left++
                index++
            }
            nums[index] == 2 -> {
                val temp = nums[right]
                nums[right] = nums[index]
                nums[index] = temp
                right--
            } else -> {
                index++
            }
        }
    }
}
fun backspaceCompare(s: String, t: String): Boolean {
    var tp = t.length - 1
    var sp = s.length - 1
    var sHashCount = 0
    var tHashCount = 0
    while (sp >= 0 || tp >= 0) {
        while (sp >= 0) {
            if (s[sp] == '#') {
                sHashCount++
                sp--
            } else if (sHashCount > 0) {
                sHashCount--
                sp--
            } else
                break
        }
        while (tp >= 0) {
            if (s[tp] == '#') {
                tHashCount++
                tp--
            } else if (tHashCount > 0) {
                tHashCount--
                tp--
            } else
                break
        }

        if (sp >= 0 && tp >= 0 && s[sp] != s[tp]) return false
        if ((sp >= 0) !=  (tp >= 0) ) return false
        sp--
        tp --
    }

    return true
}
fun twoSumLessThanK(nums: IntArray, k: Int): Int {
    nums.sort()
    var left = 0
    var right = nums.size - 1
    var maxSum = -1

    while (left < right) {
        var sum = nums[left] + nums[right]

        if (sum < k) {
            maxSum = maxOf(maxSum, sum)
            left++
        } else {
            right--
        }
    }
    return maxSum
}
fun maxOperations(nums: IntArray, k: Int): Int {
    var operations = 0
    var left = 0
    var right = nums.size - 1
    nums.sort()
    while (left < right) {
        val sum = nums[left] + nums[right]
        when {
            sum < k -> left++
            sum > k -> right--
            else -> {
                operations++
                left++
                right--
            }
        }
    }
    return operations
}
fun numRescueBoats(people: IntArray, limit: Int): Int  {
    people.sort()
    var left = 0
    var right = people.size - 1
    var boats = 0
    while (left <= right) {
        if (people[left] + people[right] <= limit) {
            left++
        }
        right--
        boats++
    }
    return boats
}
fun trap(height: IntArray): Int {
    var left = 0
    var right = height.size - 1
    var maxLeft = height[left]
    var maxRight = height[right]
    var totalWater = 0
    while (left < right) {
        if (height[left] < height[right]) {
            left++
            maxLeft = maxOf(maxLeft, height[left])
            totalWater += maxLeft - height[left]
        } else {
            right--
            maxRight = maxOf(maxRight, height[right])
            totalWater += maxRight - height[right]
        }
    }
    return totalWater
}
fun removeDuplicatesI(nums: IntArray): Int {
    var current = 0
    var count = 0

    if (nums.isNotEmpty()) {
        current = nums[0]
        count = 1
    }

    for (i in 1 until nums.size) {
        if (current != nums[i]) {
            nums[count] = nums[i]
            count++
        }
        current = nums[i]
    }
    return count
}
fun removeDuplicatesII(nums: IntArray): Int {
    val size = nums.size
    var currentCount = 0
    var totalCount = 0
    var currentValue = 0
    if (nums.isNotEmpty()) {
        currentCount = 1
        totalCount = 1
        currentValue = nums[0]
    }

    for (i in 1 until size) {
        if (nums[i] != currentValue || (currentValue == nums[i] && currentCount < 2)) {
            nums[totalCount] = nums[i]
            totalCount++
            if (currentValue != nums[i]) {
                currentCount = 0
            }
            currentCount++
        }
        currentValue = nums[i]
    }

    return totalCount
}
fun replaceElements(arr: IntArray): IntArray {
    var lar = -1
    var n = arr.size - 1
    var item : Int
    while (n >= 0) {
        item = arr[n]
        arr[n] = lar
        if (item > lar) {
            lar = item
        }
        n--
    }
    return arr
}
fun sortedSquares(nums: IntArray): IntArray {
    val result = IntArray(nums.size)
    var left = 0
    var right = nums.size - 1
    var i = nums.size - 1
    while (left <= right) {
        val leftS = nums[left] * nums[left]
        val rightS = nums[right] * nums[right]
        if (leftS < rightS) {
            result[i] = rightS
            right--
        } else {
            result[i] = leftS
            left++
        }
        i--
    }
    return result
}
fun rotate(nums: IntArray, k: Int): Unit {
    val n = nums.size
    if (n == 0) return
    val steps = k % n
    reverse(nums, 0, n -1)
    reverse(nums, 0, steps - 1)
    reverse(nums, steps, n - 1)
}
fun reverse(nums: IntArray, left : Int, right : Int) {
    var l = left
    var r = right
    while (l < r) {
        val temp = nums[l]
        nums[l] = nums[r]
        nums[r] = temp
        l++
        r--
    }
}
fun nextPermutation(nums: IntArray): Unit {
    // [1,2,3]	[1,3,2]
    val n = nums.size
    var i = n - 2
    while (i >= 0 && nums[i] >= nums[i + 1]) {
        i--
    }
    if (i >= 0) {
        var j = n - 1
        while (nums[j] <= nums[i]) {
            j--
        }
        swapPoi(nums, i, j)
    }
    reversePoi(nums, i + 1)
}

fun swapPoi(nums: IntArray, i: Int, j: Int) {
    val temp = nums[i]
    nums[i] = nums[j]
    nums[j] = temp
}

fun reversePoi(nums: IntArray, i: Int) {
    var left = i
    var right = nums.size - 1
    while (left < right) {
        val temp = nums[left]
        nums[left] = nums[right]
        nums[right] = temp
        left++
        right--
    }
}
