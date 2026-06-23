package array.greedy

/**
 * https://leetcode.com/problems/wiggle-subsequence/
 *
 * A wiggle sequence alternates between increasing and decreasing.
 * Given an integer array nums, return the length of the longest wiggle subsequence.
 *
 * Example 1:
 *
 * Input: nums = [1,7,4,9,2,5]
 * Output: 6
 * Explanation: 1 < 7 > 4 < 9 > 2 < 5 (entire sequence is wiggle)
 *
 * Example 2:
 *
 * Input: nums = [1,17,5,10,13,15,10,5,16,8]
 * Output: 7
 * Explanation: 1 < 17 > 5 < 13 > 5 < 16 > 8
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon)
 *
 * Key Insight: Count the number of peaks and valleys. Every time the diff changes
 * sign (positive ↔ negative), we have a new wiggle. Just count the sign changes.
 * Up-down-up-down pattern → count alternating direction changes.
 */
fun main() {
    println(wiggleMaxLength(intArrayOf(1, 7, 4, 9, 2, 5)))
    println(wiggleMaxLength(intArrayOf(1, 17, 5, 10, 13, 15, 10, 5, 16, 8)))
    println(wiggleMaxLength(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: Greedy — count peaks and valleys
 *
 * Track previous difference direction. When current diff has opposite sign,
 * increment count and update direction. Skip equal neighbors (diff = 0).
 *
 * Trace for [1,17,5,10,13,15,10,5,16,8]:
 * 1→17: diff=+16 (up)   → count=2, prevDir=up
 * 17→5: diff=-12 (down) → count=3, prevDir=down
 * 5→10: diff=+5 (up)    → count=4, prevDir=up
 * 10→13: diff=+3 (up)   → same dir, skip
 * 13→15: diff=+2 (up)   → same dir, skip
 * 15→10: diff=-5 (down) → count=5, prevDir=down
 * 10→5: diff=-5 (down)  → same dir, skip
 * 5→16: diff=+11 (up)   → count=6, prevDir=up
 * 16→8: diff=-8 (down)  → count=7, prevDir=down
 * Result: 7 ✅
 */
fun wiggleMaxLength(nums: IntArray): Int {
    if (nums.size < 2) return nums.size

    var up = 1   // Length of wiggle ending with UP
    var down = 1 // Length of wiggle ending with DOWN

    for (i in 1 until nums.size) {
        when {
            nums[i] > nums[i - 1] -> up = down + 1
            nums[i] < nums[i - 1] -> down = up + 1
        }
    }

    return maxOf(up, down)
}
