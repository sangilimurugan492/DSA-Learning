package array.sorting.rank_transform_of_an_array

/**
 * https://leetcode.com/problems/rank-transform-of-an-array/
 *
 * Given an array of integers arr, replace each element with its rank.
 *
 * The rank represents how large the element is. The rank has the following rules:
 *   - Rank is an integer starting from 1.
 *   - The larger the element, the larger the rank. The maximum element has the
 *     highest rank (i.e., the largest numerical rank).
 *   - If two elements are equal, their rank must be the same.
 *   - Rank should be as small as possible (no gaps in rank values).
 *
 * Constraints:
 *   0 <= arr.length <= 10^5
 *   -10^9 <= arr[i] <= 10^9
 *
 * Example 1:
 *   Input:  arr = [40, 10, 20, 30]
 *   Output: [4, 1, 2, 3]
 *   Explanation: 40 is the largest → rank 4, 10 is the smallest → rank 1,
 *                20 → rank 2, 30 → rank 3.
 *
 * Example 2:
 *   Input:  arr = [100, 100, 100]
 *   Output: [1, 1, 1]
 *   Explanation: All elements are equal → all get rank 1.
 *
 * Example 3:
 *   Input:  arr = [37, 12, 28, 9, 100, 56, 80, 5, 12]
 *   Output: [5, 3, 4, 2, 8, 6, 7, 1, 3]
 *   Explanation:
 *     Sorted unique: [5, 9, 12, 28, 37, 56, 80, 100]
 *     Ranks:          1   2   3   4    5    6   7    8
 *     arr[0]=37  → rank 5
 *     arr[1]=12  → rank 3
 *     arr[2]=28  → rank 4
 *     arr[3]=9   → rank 2
 *     arr[4]=100 → rank 8
 *     arr[5]=56  → rank 6
 *     arr[6]=80  → rank 7
 *     arr[7]=5   → rank 1
 *     arr[8]=12  → rank 3 (same as arr[1])
 */
fun main() {
    println(arrayRankTransform(intArrayOf(40, 10, 20, 30)).toList())                     // [4, 1, 2, 3]
    println(arrayRankTransform(intArrayOf(100, 100, 100)).toList())                     // [1, 1, 1]
    println(arrayRankTransform(intArrayOf(37, 12, 28, 9, 100, 56, 80, 5, 12)).toList()) // [5, 3, 4, 2, 8, 6, 7, 1, 3]
}

/**
 * Optimal — Sort Unique Values + HashMap
 *
 * Key insight: Rank is determined by an element's position among the *unique* sorted
 * values. We don't need a brute-force approach here — sorting + a HashMap lookup is
 * the natural and optimal solution.
 *
 * Steps:
 * 1. Get the **sorted unique** values from arr (using a SortedSet / TreeSet).
 * 2. Build a HashMap: value → rank (1-indexed, based on position in sorted unique list).
 * 3. For each arr[i], look up its rank in O(1).
 *
 * Why SortedSet? It handles both deduplication and sorting in one step. For languages
 * without a built-in sorted set, you can sort a copy and then deduplicate manually.
 *
 * Trace for arr = [40, 10, 20, 30]:
 *
 *   Step 1 — Sorted unique values:
 *     [10, 20, 30, 40]
 *
 *   Step 2 — Build map (value → rank):
 *     10 → 1, 20 → 2, 30 → 3, 40 → 4
 *
 *   Step 3 — Fill result in original order:
 *     arr[0]=40 → rank 4
 *     arr[1]=10 → rank 1
 *     arr[2]=20 → rank 2
 *     arr[3]=30 → rank 3
 *   Result = [4, 1, 2, 3] ✅
 *
 * Time Complexity:  O(N log N) — dominated by sorting (TreeSet insertion is O(log N) per element)
 * Space Complexity: O(N)       — for the sorted set + HashMap + result
 */
fun arrayRankTransform(arr: IntArray): IntArray {
    if (arr.isEmpty()) return IntArray(0)

    // Step 1: Collect sorted unique values.
    // We sort a copy and deduplicate manually (avoids TreeSet overhead in Kotlin).
    val sortedUnique = arr.toSet().sorted() // toSet() deduplicates, sorted() sorts ascending

    // Step 2: Map each unique value to its rank (1-indexed).
    val rankMap = HashMap<Int, Int>()
    for ((index, value) in sortedUnique.withIndex()) {
        rankMap[value] = index + 1 // rank starts at 1
    }

    // Step 3: Build result in original order using O(1) lookups.
    val result = IntArray(arr.size)
    for (i in arr.indices) {
        result[i] = rankMap[arr[i]]!!
    }
    return result
}
