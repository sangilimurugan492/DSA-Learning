package array.frequency_count.top_k_frequent_elements

/**
 * https://leetcode.com/problems/top-k-frequent-elements/
 *
 * Given an integer array nums and integer k, return the k most frequent elements.
 *
 * Example 1:
 * Input: nums = [1,1,1,2,2,3], k = 2 → Output: [1,2]
 * Example 2:
 * Input: nums = [1], k = 1 → Output: [1]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 15 most asked)
 */

fun main() {
    println(topKFrequentBruteForce(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList())
    println(topKFrequentBruteForce(intArrayOf(1), 1).toList())
    println("---")
    println(topKFrequentSort(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList())
    println("---")
    println(topKFrequentBucketSort(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList())
}

/**
 * BRUTE FORCE
 * Time Complexity: O(N²) — count frequency for each unique element
 * Space Complexity: O(N) — frequency map + result
 *
 * For each unique element, count its frequency by scanning the array.
 * Then find the k elements with highest frequency.
 */
fun topKFrequentBruteForce(nums: IntArray, k: Int): IntArray {
    val freq = hashMapOf<Int, Int>()
    for (num in nums) {
        freq[num] = freq.getOrDefault(num, 0) + 1
    }
    // Sort by frequency descending, take k
    return freq.entries
        .sortedByDescending { it.value }
        .take(k)
        .map { it.key }
        .toIntArray()
}

/**
 * BETTER — HashMap + Sort
 * Time Complexity: O(N + U log U) where U = unique elements
 * Space Complexity: O(N)
 *
 * Build frequency map, sort entries by frequency, take top k.
 */
fun topKFrequentSort(nums: IntArray, k: Int): IntArray {
    val freq = hashMapOf<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    return freq.entries
        .sortedByDescending { it.value }
        .take(k)
        .map { it.key }
        .toIntArray()
}

/**
 * OPTIMAL — Bucket Sort
 * Time Complexity: O(N) — no sorting needed!
 * Space Complexity: O(N)
 *
 * Key insight: Frequency can be at most N. Create buckets where
 * bucket[i] = list of numbers with frequency i.
 * Traverse from highest bucket to collect k elements.
 *
 * Trace for [1,1,1,2,2,3], k=2:
 * freq = {1:3, 2:2, 3:1}
 * buckets: [ [], [3], [2], [1], [], [], [] ]
 *                    freq=1 freq=2 freq=3
 * Traverse from end: bucket[3]=[1], bucket[2]=[2] → [1,2] ✅
 */
fun topKFrequentBucketSort(nums: IntArray, k: Int): IntArray {
    val freq = hashMapOf<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    // Bucket sort: index = frequency, value = list of numbers
    val buckets = Array(nums.size + 1) { mutableListOf<Int>() }
    for ((num, count) in freq) {
        buckets[count].add(num)
    }

    val result = mutableListOf<Int>()
    for (i in buckets.size - 1 downTo 0) {
        for (num in buckets[i]) {
            result.add(num)
            if (result.size == k) return result.toIntArray()
        }
    }
    return result.toIntArray()
}
