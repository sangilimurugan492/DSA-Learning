package array.linear_scan.number_of_distinct_averages

/**
 * https://leetcode.com/problems/number-of-distinct-averages/
 *
 * You are given an integer array nums of even length.
 *
 * You must perform the following algorithm on nums:
 *   1. Let min and max be the minimum and maximum values in nums respectively.
 *   2. Remove both min and max from nums.
 *   3. Add (min + max) / 2 to a set of averages.
 *   4. Repeat until nums is empty.
 *
 * Return the number of **distinct** averages computed.
 *
 * Note: The average is computed as (min + max) / 2.0 (floating-point division).
 *
 * Constraints:
 *   1 <= nums.length <= 100  (always even)
 *   0 <= nums[i] <= 100
 *
 * Example 1:
 *   Input:  nums = [4, 1, 4, 0, 3, 5]
 *   Output: 2
 *   Explanation:
 *     Step 1: min=0, max=5 → avg=2.5 → set={2.5}, nums=[4,1,4,3]
 *     Step 2: min=1, max=4 → avg=2.5 → set={2.5}, nums=[4,3]
 *     Step 3: min=3, max=4 → avg=3.5 → set={2.5, 3.5}
 *     Distinct averages = 2
 *
 * Example 2:
 *   Input:  nums = [1, 100]
 *   Output: 1
 *   Explanation:
 *     Step 1: min=1, max=100 → avg=50.5 → set={50.5}
 *     Distinct averages = 1
 */
fun main() {
    println(distinctAveragesBF(intArrayOf(4, 1, 4, 0, 3, 5))) // 2
    println(distinctAveragesOP(intArrayOf(4, 1, 4, 0, 3, 5))) // 2
    println(distinctAveragesOP(intArrayOf(1, 100)))            // 1
}

/**
 * Brute Force — Repeatedly Find Min and Max
 *
 * For each of N/2 iterations:
 *   1. Scan the array to find the current min and max.
 *   2. Remove them (mark as visited or null them out).
 *   3. Compute their average and add to a set.
 *
 * This simulates the algorithm literally — no sorting, just repeated linear scans.
 *
 * Time Complexity:  O(N²) — N/2 iterations × O(N) scan per iteration
 * Space Complexity: O(N) — for the working copy + set of averages
 */
fun distinctAveragesBF(nums: IntArray): Int {
    // Work on a mutable list so we can remove elements.
    val list = nums.toMutableList()
    val averages = mutableSetOf<Double>()

    while (list.isNotEmpty()) {
        // Find min and max in the current list.
        var min = list[0]
        var max = list[0]
        for (num in list) {
            if (num < min) min = num
            if (num > max) max = num
        }

        // Compute average and add to set.
        averages.add((min + max) / 2.0)

        // Remove one occurrence of min and one of max.
        list.removeAt(list.indexOf(min))
        list.removeAt(list.indexOf(max))
    }

    return averages.size
}

/**
 * Optimal — Sort + Two Pointers
 *
 * Key insight: After sorting, the min is always at the left end and the max is always
 * at the right end. So we can sort once and use two pointers (left, right) moving inward
 * to pair up min and max values. No need to repeatedly scan or remove elements.
 *
 * Steps:
 * 1. Sort the array.
 * 2. Use two pointers: left = 0, right = n-1.
 * 3. At each step, compute the average of nums[left] and nums[right], add to a set.
 *    Move left++ and right--.
 * 4. Repeat until left >= right.
 * 5. Return the size of the set (number of distinct averages).
 *
 * Trace for nums = [4, 1, 4, 0, 3, 5]:
 *
 *   Step 1 — Sort:
 *     sorted = [0, 1, 3, 4, 4, 5]
 *
 *   Step 2 — Two-pointer pairs:
 *     left=0, right=5: avg = (0+5)/2 = 2.5 → set = {2.5}
 *     left=1, right=4: avg = (1+4)/2 = 2.5 → set = {2.5}
 *     left=2, right=3: avg = (3+4)/2 = 3.5 → set = {2.5, 3.5}
 *
 *   Step 3 — left=3 >= right=2 → stop.
 *   Distinct averages = 2 ✅
 *
 * Note on precision: Since values are integers [0, 100], the average (a+b)/2 either
 * yields an integer (e.g., 2.0) or a half-integer (e.g., 2.5). These are exactly
 * representable in Double, so there are no floating-point precision issues.
 * Alternatively, we could store (a+b) as an integer to avoid doubles entirely.
 *
 * Time Complexity:  O(N log N) — dominated by sorting; O(N) for the two-pointer pass
 * Space Complexity: O(N)       — for the set of averages (at most N/2 entries)
 */
fun distinctAveragesOP(nums: IntArray): Int {
    // Step 1: Sort so min is at the left, max is at the right.
    nums.sort()

    // Step 2: Use two pointers to pair min with max.
    val averages = mutableSetOf<Double>()
    var left = 0
    var right = nums.size - 1

    while (left < right) {
        val avg = (nums[left] + nums[right]) / 2.0
        averages.add(avg)
        left++
        right--
    }

    return averages.size
}
