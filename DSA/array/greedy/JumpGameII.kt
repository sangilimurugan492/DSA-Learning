package array.greedy

/**
 * https://leetcode.com/problems/jump-game-ii/
 *
 * You are given a 0-indexed array of integers nums of length n. Initially you are positioned at nums[0].
 * Each element nums[i] represents the maximum length of a forward jump from index i.
 * Return the minimum number of jumps to reach nums[n - 1].
 *
 * Example 1:
 *
 * Input: nums = [2,3,1,1,4]
 * Output: 2
 * Explanation: Jump from index 0 to 1 (1 jump), then from index 1 to 4 (1 jump). Total = 2.
 *
 * Example 2:
 *
 * Input: nums = [2,3,0,1,4]
 * Output: 2
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Amazon, Meta)
 *
 * Key Insight: Use BFS-like greedy. Track the current "level" (jump range) and the farthest
 * reachable index. When we reach the end of current level, jump to the next level.
 */
fun main() {
    println(jumpGameII(intArrayOf(2, 3, 1, 1, 4)))
    println(jumpGameII(intArrayOf(2, 3, 0, 1, 4)))
    println(jumpGameII(intArrayOf(1, 1, 1, 1)))
}

/**
 * Time Complexity O(N)
 * Space Complexity O(1)
 *
 * Approach: BFS-like greedy
 *
 * Think of it like BFS levels:
 * - Level 0: index 0 (can reach indices 1..2)
 * - Level 1: indices 1..2 (can reach indices 2..4)
 * - Level 2: reached the end!
 *
 * Track: currentEnd (end of current level), farthest (farthest reachable in next level)
 * When i == currentEnd → we've exhausted this level, jump to next level
 *
 * Trace for [2,3,1,1,4]:
 * i=0: farthest=max(0,0+2)=2, i==currentEnd(0) → jumps=1, currentEnd=2
 * i=1: farthest=max(2,1+3)=4, i!=currentEnd
 * i=2: farthest=max(4,2+1)=4, i==currentEnd(2) → jumps=2, currentEnd=4
 * i=3: farthest=max(4,3+1)=4, i!=currentEnd
 * (loop ends before i=4, but currentEnd=4 ≥ last index)
 * Result: 2 ✅
 */
fun jumpGameII(nums: IntArray): Int {
    if (nums.size <= 1) return 0

    var jumps = 0
    var currentEnd = 0
    var farthest = 0

    for (i in 0 until nums.size - 1) {
        farthest = maxOf(farthest, i + nums[i])

        if (i == currentEnd) {
            jumps++
            currentEnd = farthest
            if (currentEnd >= nums.size - 1) break
        }
    }

    return jumps
}
