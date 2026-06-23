package array.greedy

/**
 * https://leetcode.com/problems/jump-game/
 *
 * Given an array of non-negative integers, determine if you can reach the last index.
 *
 * Example: nums = [2,3,1,1,4] → Output: true | nums = [3,2,1,0,4] → Output: false
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    println(canJumpBruteForce(intArrayOf(2, 3, 1, 1, 4)))
    println(canJumpBruteForce(intArrayOf(3, 2, 1, 0, 4)))
    println("---")
    println(canJumpGreedy(intArrayOf(2, 3, 1, 1, 4)))
    println(canJumpGreedy(intArrayOf(3, 2, 1, 0, 4)))
}

/**
 * BRUTE FORCE — Backtracking
 * Time Complexity: O(2^N) — try every possible jump
 * Space Complexity: O(N) — recursion stack
 *
 * From each position, try all possible jumps (1 to nums[i] steps).
 * Exponential because we explore all paths.
 */
fun canJumpBruteForce(nums: IntArray): Boolean {
    fun canReach(index: Int): Boolean {
        if (index >= nums.size - 1) return true
        val maxJump = nums[index]
        for (jump in 1..maxJump) {
            if (canReach(index + jump)) return true
        }
        return false
    }
    return canReach(0)
}

/**
 * OPTIMAL — Greedy
 * Time Complexity: O(N)
 * Space Complexity: O(1)
 *
 * Track farthest reachable index. If current index > farthest → stuck.
 */
fun canJumpGreedy(nums: IntArray): Boolean {
    var farthest = 0
    for (i in nums.indices) {
        if (i > farthest) return false
        farthest = maxOf(farthest, i + nums[i])
    }
    return true
}
