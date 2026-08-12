package array.two_pointer.max_consecutive_ones_iii

/**
 * https://leetcode.com/problems/max-consecutive-ones-iii/
 *
 * Given a binary array nums and an integer k, return the maximum number of
 * consecutive 1's in the array if you can flip at most k 0's.
 *
 * Example 1:
 *
 * Input: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
 * Output: 6
 * Explanation: [1,1,1,0,0,1,1,1,1,1,1] — flip last two 0s
 *
 * Example 2:
 *
 * Input: nums = [0,0,1,1,1,0,0], k = 0
 * Output: 3
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Google, Meta, Amazon — sliding window classic)
 *
 * Two approaches:
 * 1. Brute Force: For each starting index, expand and count zeros
 * 2. Sliding Window: Track zeros in window, shrink when > k
 */
fun main() {
    println("Brute Force:")
    println(longestOnesBF(intArrayOf(1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0), 2))  // 6
    println(longestOnesBF(intArrayOf(0, 0, 1, 1, 1, 0, 0), 0))               // 3
    println(longestOnesBF(intArrayOf(1, 1, 1, 1, 1), 2))                      // 5
    println("Sliding Window:")
    println(longestOnes(intArrayOf(1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0), 2))     // 6
    println(longestOnes(intArrayOf(0, 0, 1, 1, 1, 0, 0), 0))                  // 3
    println(longestOnes(intArrayOf(1, 1, 1, 1, 1), 2))                         // 5
}

/**
 * Brute Force: For each starting index, expand right and count zeros.
 * If zeros exceed k, stop expanding. Track the maximum window size.
 *
 * Step-by-step:
 * 1. For each starting index i (0 to n-1):
 *    a. Set zeroCount = 0.
 *    b. For each ending index j (i to n-1):
 *       - If nums[j] == 0 → zeroCount++.
 *       - If zeroCount > k → break (can't flip more than k zeros).
 *       - Otherwise → window length = j - i + 1, update maxLen.
 * 2. Return maxLen.
 *
 * Walkthrough: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
 *
 *   i=0: expand → zeros: 0,0,0,1,2 → window [1,1,1,0,0] len=5
 *         next zero would make 3 > k → stop
 *   i=1: expand → zeros: 0,0,1,2 → window [1,1,0,0] len=4
 *   i=2: expand → zeros: 0,1,2 → window [1,0,0] len=3
 *   i=3: expand → zeros: 1,2,3 > k → window [0,0] len=2
 *   i=4: expand → zeros: 1,2 → window [0,0,1,1,1,1] len=6 ← max!
 *   ... (remaining windows are smaller)
 *
 * Result: 6 ✅
 *
 * Time Complexity:  O(N²) — nested loops for each starting index
 * Space Complexity: O(1)
 */
fun longestOnesBF(nums: IntArray, k: Int): Int {
    var maxLen = 0

    for (i in nums.indices) {
        var zeroCount = 0
        for (j in i until nums.size) {
            if (nums[j] == 0) zeroCount++
            if (zeroCount > k) break
            maxLen = maxOf(maxLen, j - i + 1)
        }
    }

    return maxLen
}

/**
 * Sliding Window (Optimal): Expand right, count zeros. When zeros > k,
 * shrink from left until zeros ≤ k. Track max window size.
 *
 * Step-by-step:
 * 1. Set left = 0, zeros = 0, maxLen = 0.
 * 2. For each right (0 to n-1):
 *    a. If nums[right] == 0 → zeros++.
 *    b. While zeros > k → shrink: if nums[left] == 0 → zeros--, left++.
 *    c. Update maxLen = max(maxLen, right - left + 1).
 * 3. Return maxLen.
 *
 * Walkthrough: nums = [1,1,1,0,0,0,1,1,1,1,0], k = 2
 *
 *   right=0..2: zeros=0, window=[1,1,1], maxLen=3
 *   right=3..4: zeros=2, window=[1,1,1,0,0], maxLen=5
 *   right=5: zeros=3 > 2 → shrink left past index 3 → zeros=2, window=[0,0,0]
 *   right=6..9: zeros=2, window=[0,0,1,1,1,1], maxLen=6
 *   right=10: zeros=3 > 2 → shrink left past index 5 → zeros=2, maxLen=6
 *
 * Result: 6 ✅
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1)
 */
fun longestOnes(nums: IntArray, k: Int): Int {
    var left = 0
    var zeros = 0
    var maxLen = 0

    for (right in nums.indices) {
        if (nums[right] == 0) zeros++

        while (zeros > k) {
            if (nums[left] == 0) zeros--
            left++
        }

        maxLen = maxOf(maxLen, right - left + 1)
    }

    return maxLen
}
