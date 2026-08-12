package array.two_pointer.k_sum_pairs

/**
 * https://leetcode.com/problems/k-sum-pairs/
 *
 * Given an array of integers nums and an integer k, return the maximum number of
 * operations you can perform where each operation picks two numbers whose sum equals k
 * and removes them from the array.
 *
 * Example 1:
 *
 * Input: nums = [1,2,3,4], k = 5
 * Output: 2
 * Explanation: (1,4) and (2,3) → 2 operations
 *
 * Example 2:
 *
 * Input: nums = [3,1,3,4,3], k = 6
 * Output: 1
 * Explanation: (3,3) → only one pair, remaining [1,4,3] can't form sum=6
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Amazon, Google)
 *
 * Two approaches:
 * 1. Brute Force: Nested loops — for each element, scan remaining for a partner
 * 2. Sort + Two Pointer: Pair smallest with largest
 */
fun main() {
    println("Brute Force:")
    println(maxOperationsBF(intArrayOf(1, 2, 3, 4), 5))       // 2
    println(maxOperationsBF(intArrayOf(3, 1, 3, 4, 3), 6))   // 1
    println("Sort + Two Pointer:")
    println(maxOperations(intArrayOf(1, 2, 3, 4), 5))         // 2
    println(maxOperations(intArrayOf(3, 1, 3, 4, 3), 6))      // 1
}

/**
 * Brute Force (Nested Loops): For each unpaired element, scan the rest of
 * the array for a partner that sums to k. Mark both as used when found.
 *
 * Step-by-step:
 * 1. Create a `used` boolean array to track which elements are already paired.
 * 2. For each element nums[i] (if not already used):
 *    a. Scan forward through nums[j] (j > i, not used).
 *    b. If nums[i] + nums[j] == k → pair found! Mark both as used, increment count, break.
 *    c. If no partner found, skip (nums[i] goes unpaired).
 * 3. Return total pairs found.
 *
 * Walkthrough: nums = [1,2,3,4], k = 5
 *
 *   i=0, nums[0]=1: scan j=1(2)→3, j=2(3)→4, j=3(4)→1+4=5 ✅ pair! used=[T,F,F,T], count=1
 *   i=1, nums[1]=2: not used, scan j=2(3)→2+3=5 ✅ pair! used=[T,T,T,T], count=2
 *   i=2, nums[2]=3: already used → skip
 *   i=3, nums[3]=4: already used → skip
 *
 * Result: 2 ✅
 *
 * Walkthrough: nums = [3,1,3,4,3], k = 6
 *
 *   i=0, nums[0]=3: scan j=1(1)→4, j=2(3)→6 ✅ pair! used=[T,F,T,F,F], count=1
 *   i=1, nums[1]=1: not used, scan j=3(4)→5, j=4(3)→4 → no pair found
 *   i=2, nums[2]=3: already used → skip
 *   i=3, nums[3]=4: not used, scan j=4(3)→7 → no pair found
 *   i=4, nums[4]=3: not used → no more elements to scan
 *
 * Result: 1 ✅
 *
 * Time Complexity:  O(N²) — nested loops
 * Space Complexity: O(N)  — used array
 */
fun maxOperationsBF(nums: IntArray, k: Int): Int {
    val used = BooleanArray(nums.size)
    var operations = 0

    for (i in nums.indices) {
        if (used[i]) continue // Already paired

        // Scan forward for a partner
        for (j in i + 1 until nums.size) {
            if (!used[j] && nums[i] + nums[j] == k) {
                used[i] = true
                used[j] = true
                operations++
                break // Move to next i (each element pairs at most once)
            }
        }
    }

    return operations
}

/**
 * Sort + Two Pointer (Optimal): Sort the array, then pair from both ends.
 *
 * Step-by-step:
 * 1. Sort the array.
 * 2. Set left = 0, right = last index.
 * 3. While left < right:
 *    a. sum = nums[left] + nums[right]
 *    b. If sum == k → pair found! operations++, left++, right--
 *    c. If sum < k → need bigger → left++ (move to larger element)
 *    d. If sum > k → need smaller → right-- (move to smaller element)
 * 4. Return operations.
 *
 * Walkthrough: nums = [3,1,3,4,3], k = 6
 *
 *   Sorted: [1,3,3,3,4]
 *   left=0, right=4: 1+4=5 < 6 → left++
 *   left=1, right=4: 3+4=7 > 6 → right--
 *   left=1, right=3: 3+3=6 == 6 → pair! ops=1, left=2, right=2
 *   left=2, right=2: left >= right → stop
 *
 * Result: 1 ✅
 *
 * Time Complexity:  O(N log N) — dominated by sorting
 * Space Complexity: O(1)       — in-place sort
 */
fun maxOperations(nums: IntArray, k: Int): Int {
    nums.sort()
    var left = 0
    var right = nums.size - 1
    var operations = 0

    while (left < right) {
        val sum = nums[left] + nums[right]
        when {
            sum == k -> {
                operations++
                left++
                right--
            }
            sum < k -> left++
            else -> right--
        }
    }

    return operations
}
