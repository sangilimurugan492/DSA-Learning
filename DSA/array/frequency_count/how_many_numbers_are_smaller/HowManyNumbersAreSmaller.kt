package array.frequency_count.how_many_numbers_are_smaller

/**
 * https://leetcode.com/problems/how-many-numbers-are-smaller-than-the-current-number/
 *
 * Given the array nums, for each nums[i] find out how many numbers in the array
 * are smaller than it. That is, for each nums[i] you have to count the number of
 * valid j's such that j != i and nums[j] < nums[i].
 *
 * Return the answer in an array.
 *
 * Constraints:
 *   2 <= nums.length <= 500
 *   0 <= nums[i] <= 100
 *
 * Example 1:
 *   Input:  nums = [8, 1, 2, 2, 3]
 *   Output: [4, 0, 1, 1, 3]
 *
 *   Explanation:
 *     For nums[0]=8 there exist four smaller numbers (1, 2, 2, 3).
 *     For nums[1]=1 there is no smaller number.
 *     For nums[2]=2 there exists one smaller number (1).
 *     For nums[3]=2 there exists one smaller number (1).
 *     For nums[4]=3 there exist three smaller numbers (1, 2, 2).
 *
 * Example 2:
 *   Input:  nums = [6, 5, 4, 8]
 *   Output: [2, 1, 0, 3]
 *
 * Example 3:
 *   Input:  nums = [7, 7, 7, 7]
 *   Output: [0, 0, 0, 0]
 */
fun main() {
    println(smallerNumbersThanCurrentBF(intArrayOf(8, 1, 2, 2, 3)).toList()) // [4, 0, 1, 1, 3]
    println(smallerNumbersThanCurrentOP(intArrayOf(8, 1, 2, 2, 3)).toList()) // [4, 0, 1, 1, 3]
    println(smallerNumbersThanCurrentOP(intArrayOf(6, 5, 4, 8)).toList())    // [2, 1, 0, 3]
    println(smallerNumbersThanCurrentOP(intArrayOf(7, 7, 7, 7)).toList())    // [0, 0, 0, 0]
}

/**
 * Brute Force — Nested Loops
 *
 * For each element, scan the entire array and count how many elements are smaller.
 *
 * Time Complexity:  O(N²)  — for each of N elements we scan all N elements
 * Space Complexity: O(N)   — for the result array (excluding input)
 */
fun smallerNumbersThanCurrentBF(nums: IntArray): IntArray {
    val result = IntArray(nums.size)
    for (i in nums.indices) {
        var count = 0
        for (j in nums.indices) {
            if (nums[j] < nums[i]) {
                count++
            }
        }
        result[i] = count
    }
    return result
}

/**
 * Optimal — Counting Sort + Prefix Sum
 *
 * Key insight: nums[i] is in the range [0, 100]. We can count the frequency of each
 * value using a fixed-size array. Then, the number of elements smaller than `v` is
 * simply the sum of frequencies of all values from 0 to v-1 — i.e., a prefix sum.
 *
 * Steps:
 * 1. Build a frequency array `counts` where counts[v] = how many times v appears in nums.
 * 2. Convert `counts` into a prefix sum array where counts[v] = number of elements
 *    strictly smaller than v (sum of counts[0..v-1]).
 *    We do this in-place: for each v, counts[v] += counts[v-1], then the answer for
 *    value v is counts[v-1] (before we added counts[v] to it, counts[v-1] held the
 *    count of all elements < v). A simpler way: after computing prefix sums, the
 *    answer for value v = counts[v-1] if v > 0, else 0.
 * 3. For each nums[i], look up the precomputed answer in O(1).
 *
 * Trace for nums = [8, 1, 2, 2, 3]:
 *
 *   Step 1 — Frequency array (indices 0–8 shown, full size is 101):
 *     counts = [0, 1, 2, 1, 0, 0, 0, 0, 1, ...]
 *                    ↑  ↑  ↑              ↑
 *                   1s 2s 3s             8s
 *
 *   Step 2 — Convert to "smaller count" using a running prefix:
 *     v=0: freq=0, counts[0]=prefix(0), prefix=0
 *     v=1: freq=1, counts[1]=prefix(0), prefix=1   → 0 elements < 1 ✓
 *     v=2: freq=2, counts[2]=prefix(1), prefix=3   → 1 element  < 2 ✓ (the single '1')
 *     v=3: freq=1, counts[3]=prefix(3), prefix=4   → 3 elements < 3 ✓ ('1' and two '2's)
 *     ...
 *     v=8: freq=1, counts[8]=prefix(4), prefix=5   → 4 elements < 8 ✓ ('1','2','2','3')
 *
 *   Step 3 — Look up each nums[i]:
 *     nums[0]=8 → counts[8]=4 → 4 ✅
 *     nums[1]=1 → counts[1]=0 → 0 ✅
 *     nums[2]=2 → counts[2]=1 → 1 ✅
 *     nums[3]=2 → counts[2]=1 → 1 ✅
 *     nums[4]=3 → counts[3]=3 → 3 ✅
 *   Result = [4, 0, 1, 1, 3] ✅
 *
 * Time Complexity:  O(N + K)  where K = 101 (max value + 1). Since K is constant, O(N).
 * Space Complexity: O(K) = O(101) = O(1) — fixed-size frequency array.

 */
fun smallerNumbersThanCurrentOP(nums: IntArray): IntArray {
    // Frequency array: index = value, value = count of occurrences.
    // Size 101 because nums[i] ranges from 0 to 100.
    val counts = IntArray(101)
    for (num in nums) {
        counts[num]++
    }

    // Convert frequency array into "number of elements smaller than v" array.
    // prefix tracks the running total of all elements seen so far (i.e., all
    // elements with value < current index).
    var prefix = 0
    for (v in counts.indices) {
        val freq = counts[v]   // how many times `v` appears
        counts[v] = prefix     // answer for value v = count of elements < v
        prefix += freq         // update prefix to include elements equal to v
    }

    // Build result: for each nums[i], look up the precomputed answer.
    val result = IntArray(nums.size)
    for (i in nums.indices) {
        result[i] = counts[nums[i]]
    }
    return result
}
