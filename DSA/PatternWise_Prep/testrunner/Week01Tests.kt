package testrunner

import array.two_pointer_inplace.next_permutation.reverseP
import array.two_pointer_inplace.next_permutation.swap
import kotlin.math.abs

/**
 * Week01Tests.kt — Week 1: Two Pointer + Two Pointer In-Place
 * Test class + solution stubs. Implement the stubs at the bottom as you solve.
 */

class Week01Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 1 — Two Pointer ━━━━━━━")
        twoSumSorted(); validPalindrome(); isSubsequence(); moveZeroes(); mergeSortedArray()
        removeElement(); intersectII(); threeSum(); threeSumClosest(); fourSum()
        containerWithMostWater(); sortColors(); backspaceCompare(); twoSumLessThanK(); kSumPairs()
        boatsToSavePeople(); trappingRainWater(); removeDuplicatesI(); removeDuplicatesII()
        replaceGreatestRight(); sortedSquares(); rotateArray(); nextPermutation()
        // Complexity validation (Part B):
//        trappingRainWaterBig(); threeSumBig()
    }

    fun twoSumSorted() = TestRunner.check("W1 #1 Two Sum II (Sorted)", block = { twoSumSorted(intArrayOf(2, 7, 11, 15), 9) }, verify = { it.contentEquals(intArrayOf(1, 2)) })
    fun validPalindrome() = TestRunner.check("W1 #2 Valid Palindrome", block = { isPalindrome("A man, a plan, a canal: Panama") }, verify = { it })
    fun isSubsequence() = TestRunner.check("W1 #3 Is Subsequence", block = { isSubsequence("abc", "ahbgdc") }, verify = { it })
    fun moveZeroes() = TestRunner.check("W1 #4 Move Zeroes", block = { val a = intArrayOf(0, 1, 0, 3, 12); moveZeroes(a); a.toList() }, verify = { it == listOf(1, 3, 12, 0, 0) })
    fun mergeSortedArray() = TestRunner.check("W1 #5 Merge Sorted Array", block = { val a = intArrayOf(1,2,3,0,0,0); merge(a, 3, intArrayOf(2,5,6), 3); a.toList() }, verify = { it == listOf(1,2,2,3,5,6) })
    fun removeElement() = TestRunner.check("W1 #6 Remove Element", block = { removeElement(intArrayOf(3, 2, 2, 3), 3) }, verify = { it == 2 })
    fun intersectII() = TestRunner.check("W1 #7 Intersection II", block = { intersect(intArrayOf(1, 2, 2, 1), intArrayOf(2, 2)).sorted() }, verify = { it == listOf(2, 2) })
    fun threeSum() = TestRunner.check("W1 #8 Three Sum", block = { threeSum(intArrayOf(-1, 0, 1, 2, -1, -4)).map { it.sorted() }.sortedBy { it.joinToString() } }, verify = { it == listOf(listOf(-1, -1, 2), listOf(-1, 0, 1)) })
    fun threeSumClosest() = TestRunner.check("W1 #9 Three Sum Closest", block = { threeSumClosest(intArrayOf(-1, 2, 1, -4), 1) }, verify = { it == 2 })
    fun fourSum() = TestRunner.check("W1 #10 Four Sum", block = { fourSum(intArrayOf(1, 0, -1, 0, -2, 2), 0).map { it.sorted() }.sortedBy { it.joinToString() } }, verify = { it.size == 3 })
    fun containerWithMostWater() = TestRunner.check("W1 #11 Container With Most Water", block = { maxArea(intArrayOf(1, 8, 6, 2, 5, 4, 8, 3, 7)) }, verify = { it == 49 })
    fun sortColors() = TestRunner.check("W1 #12 Sort Colors", block = { val a = intArrayOf(2, 0, 2, 1, 1, 0); sortColors(a); a.toList() }, verify = { it == listOf(0, 0, 1, 1, 2, 2) })
    fun backspaceCompare() = TestRunner.check("W1 #13 Backspace Compare", block = { backspaceCompare("ab##", "c#d#") }, verify = { it })
    fun twoSumLessThanK() = TestRunner.check("W1 #14 Two Sum < K", block = { twoSumLessThanK(intArrayOf(34, 23, 1, 24, 75, 33, 54, 8), 60) }, verify = { it == 58 })
    fun kSumPairs() = TestRunner.check("W1 #15 K-Sum Pairs", block = { maxOperations(intArrayOf(1, 2, 3, 4), 5) }, verify = { it == 2 })
    fun boatsToSavePeople() = TestRunner.check("W1 #16 Boats to Save", block = { numRescueBoats(intArrayOf(3, 2, 2, 1), 3) }, verify = { it == 3 })
    fun trappingRainWater() = TestRunner.check("W1 #17 Trapping Rain Water", block = { trap(intArrayOf(0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1)) }, verify = { it == 6 })
    fun removeDuplicatesI() = TestRunner.check("W1 #18 Remove Duplicates I", block = { val a = intArrayOf(1, 1, 2); val k = removeDuplicatesI(a); a.copyOf(k).toList() }, verify = { it == listOf(1, 2) })
    fun removeDuplicatesII() = TestRunner.check("W1 #19 Remove Duplicates II", block = { val a = intArrayOf(1, 1, 1, 2, 2, 3); val k = removeDuplicatesII(a); a.copyOf(k).toList() }, verify = { it == listOf(1, 1, 2, 2, 3) })
    fun replaceGreatestRight() = TestRunner.check("W1 #20 Replace Greatest Right", block = { replaceElements(intArrayOf(17, 18, 5, 4, 6, 1)).toList() }, verify = { it == listOf(18,6,6,6,1,-1) })
    fun sortedSquares() = TestRunner.check("W1 #21 Sorted Squares", block = { sortedSquares(intArrayOf(-4, -1, 0, 3, 10)).toList() }, verify = { it == listOf(0, 1, 9, 16, 100) })
    fun rotateArray() = TestRunner.check("W1 #22 Rotate Array", block = { val a = intArrayOf(1, 2, 3, 4, 5, 6, 7); rotate(a, 3); a.toList() }, verify = { it == listOf(5, 6, 7, 1, 2, 3, 4) })
    fun nextPermutation() = TestRunner.check("W1 #23 Next Permutation", block = { val a = intArrayOf(3, 2, 1); nextPermutation(a); a.toList() }, verify = { it == listOf(1, 2, 3) })

    // Part B — complexity validation
    fun trappingRainWaterBig() = TestRunner.check("W1-B Trapping Rain Water [3,0,3]×100k", budgetMs = 100, block = { trap(IntArray(100_000) { if (it % 3 == 1) 0 else 3 }) }, verify = { it == 100_002 })
    fun threeSumBig() = TestRunner.check("W1-B Three Sum all-zeros 3k", budgetMs = 500, block = { threeSum(IntArray(3000)) }, verify = { it == listOf(listOf(0, 0, 0)) })
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
            if (j > 0 && nums[j - 1] == nums[j]) continue

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
    while (left < right) {
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
