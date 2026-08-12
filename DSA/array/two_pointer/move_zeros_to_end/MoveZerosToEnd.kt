package array.two_pointer.move_zeros_to_end

/**
 * https://leetcode.com/problems/move-zeroes/description/
 *
 * Given an array nums, move all 0s to the end while maintaining the relative
 * order of non-zero elements. Must be done in-place.
 *
 * Example: [0,1,0,3,12] → [1,3,12,0,0]
 *
 * Key Idea: Use a "slow" pointer `k` that tracks where the next non-zero should go.
 * Iterate with `i`, swap non-zero elements to position `k`, then increment `k`.
 */
fun main() {
    println("Move Zeros to End — Brute Force")
    moveZerosToEndBF(intArrayOf(0, 1, 0, 3, 12))
    println("\nMove Zeros to End — Optimal (Two Pointer)")
    moveZerosToEndOP(intArrayOf(0, 1, 0, 3, 12))
}

/**
 * Brute Force: For each 0 found, swap it forward with the next non-zero.
 *
 * Time Complexity:  O(N²)
 * Space Complexity: O(1)
 */
fun moveZerosToEndBF(nums: IntArray) {
    for (i in nums.indices) {
        for (j in i + 1 until nums.size) {
            if (nums[i] == 0) {
                val temp = nums[i]
                nums[i] = nums[j]
                nums[j] = temp
            }
        }
    }
    nums.forEach { print("$it ") }
}

/**
 * Optimal (Two Pointer): `k` tracks the boundary of non-zero elements.
 * Swap each non-zero element to position `k`, then advance `k`.
 *
 * After the loop, all non-zeros are at the front, zeros are at the back.
 *
 * Time Complexity:  O(N) — single pass
 * Space Complexity: O(1) — in-place
 */
fun moveZerosToEndOP(nums: IntArray) {
    var k = 0 // Points to where the next non-zero should go

    for (i in nums.indices) {
        if (nums[i] != 0) {
            // Swap non-zero to the front
            val temp = nums[k]
            nums[k] = nums[i]
            nums[i] = temp
            k++
        }
    }

    nums.forEach { print("$it ") }
}
