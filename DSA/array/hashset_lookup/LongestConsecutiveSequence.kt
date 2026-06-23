package array.hashset_lookup

/**
 * https://leetcode.com/problems/longest-consecutive-sequence/
 *
 * Given an unsorted array of integers, find the length of the longest consecutive
 * elements sequence. Algorithm must run in O(N) time.
 *
 * Example 1:
 * Input: nums = [100,4,200,1,3,2] → Output: 4 (sequence [1,2,3,4])
 * Example 2:
 * Input: nums = [0,3,7,2,5,8,4,6,0,1] → Output: 9 (sequence [0,1,2,3,4,5,6,7,8])
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 20 most asked)
 */

fun main() {
    println(longestConsecutiveBruteForce(intArrayOf(100, 4, 200, 1, 3, 2)))
    println(longestConsecutiveBruteForce(intArrayOf(0, 3, 7, 2, 5, 8, 4, 6, 0, 1)))
    println("---")
    println(longestConsecutiveSort(intArrayOf(100, 4, 200, 1, 3, 2)))
    println(longestConsecutiveSort(intArrayOf(0, 3, 7, 2, 5, 8, 4, 6, 0, 1)))
    println("---")
    println(longestConsecutiveHashSet(intArrayOf(100, 4, 200, 1, 3, 2)))
    println(longestConsecutiveHashSet(intArrayOf(0, 3, 7, 2, 5, 8, 4, 6, 0, 1)))
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N³) — for each num, check if num+1, num+2... exist (each check is O(N))
 * Space Complexity: O(1)
 *
 * For each number, count how many consecutive numbers follow it.
 * Very slow: nested loop × linear search.
 */
fun longestConsecutiveBruteForce(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    var maxLen = 1

    for (num in nums) {
        var current = num
        var len = 1
        while (nums.contains(current + 1)) {  // O(N) search each time!
            current++
            len++
        }
        maxLen = maxOf(maxLen, len)
    }
    return maxLen
}

/**
 * BETTER — Sort first
 * Time Complexity: O(N log N) — sort + single pass
 * Space Complexity: O(1) or O(N)
 *
 * Sort, then scan for consecutive sequences. Skip duplicates.
 */
fun longestConsecutiveSort(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    nums.sort()
    var maxLen = 1
    var currentLen = 1

    for (i in 1 until nums.size) {
        if (nums[i] == nums[i - 1]) continue  // skip duplicates
        if (nums[i] == nums[i - 1] + 1) {
            currentLen++
        } else {
            currentLen = 1
        }
        maxLen = maxOf(maxLen, currentLen)
    }
    return maxLen
}

/**
 * OPTIMAL — HashSet
 * Time Complexity: O(N) — each number visited at most twice
 * Space Complexity: O(N) — HashSet
 *
 * Key insight: Only START counting from numbers that are the BEGINNING of a sequence.
 * A number is a sequence start if (num - 1) is NOT in the set.
 * This ensures we count each sequence exactly once.
 *
 * Trace for [100,4,200,1,3,2]:
 * set = {100,4,200,1,3,2}
 * 100: 99 not in set → count 100,101... → len=1
 *   4: 3 in set → skip (not a start)
 * 200: 199 not in set → count 200,201... → len=1
 *   1: 0 not in set → count 1,2,3,4,5... → len=4 ✅
 *   3: 2 in set → skip
 *   2: 1 in set → skip
 * Result: 4 ✅
 */
fun longestConsecutiveHashSet(nums: IntArray): Int {
    if (nums.isEmpty()) return 0
    val set = nums.toSet()
    var maxLen = 0

    for (num in set) {
        if (num - 1 !in set) {  // Only start from sequence beginning
            var current = num
            var len = 1
            while (current + 1 in set) {
                current++
                len++
            }
            maxLen = maxOf(maxLen, len)
        }
    }
    return maxLen
}
