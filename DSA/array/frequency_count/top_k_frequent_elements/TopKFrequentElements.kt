package array.frequency_count.top_k_frequent_elements

/**
 * Top K Frequent Elements — LeetCode #347
 * https://leetcode.com/problems/top-k-frequent-elements/
 *
 * Problem:
 * -------
 * Given an integer array nums and an integer k, return the k most frequent elements.
 *
 * Example 1:  nums = [1,1,1,2,2,3], k = 2 → [1,2]
 * Example 2:  nums = [1], k = 1 → [1]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 15 most asked)
 *
 * Three approaches:
 * 1. Brute Force: O(N²) — for each unique element, scan array to count frequency
 * 2. HashMap + Sort: O(N + U log U) — build freq map, sort by frequency
 * 3. Bucket Sort: O(N) — bucket by frequency, traverse from highest
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    println(topKFrequentBruteForce(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList())
    println(topKFrequentBruteForce(intArrayOf(1), 1).toList())

    println("\n=== Method 2: HashMap + Sort ===")
    println(topKFrequentSort(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList())

    println("\n=== Method 3: Bucket Sort ===")
    println(topKFrequentBucketSort(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList())
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N²)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — For each unique element, scan the entire array to count its frequency.
 * Then sort by frequency and take top k.
 *
 * Time Complexity:  O(N²) — for each of U unique elements, scan N elements.
 *                    Sorting U elements adds O(U log U), dominated by O(N²).
 * Space Complexity: O(U) — store unique elements + frequencies.
 */
fun topKFrequentBruteForce(nums: IntArray, k: Int): IntArray {
    val seen = mutableSetOf<Int>()
    val freqList = mutableListOf<Pair<Int, Int>>() // (element, frequency)

    for (num in nums) {
        if (num in seen) continue
        seen.add(num)
        // Count frequency by scanning the entire array
        var count = 0
        for (other in nums) {
            if (other == num) count++
        }
        freqList.add(num to count)
    }

    return freqList
        .sortedByDescending { it.second }
        .take(k)
        .map { it.first }
        .toIntArray()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: HASHMAP + SORT — O(N + U log U)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * HASHMAP + SORT — Build a frequency map in one pass, then sort entries by frequency.
 *
 * Time Complexity:  O(N + U log U) — N to build map, U log U to sort (U = unique elements).
 * Space Complexity: O(N) — frequency map + result.
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

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 3: BUCKET SORT — O(N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BUCKET SORT — Key insight: frequency can be at most N.
 * Create buckets where bucket[i] = list of numbers with frequency i.
 * Traverse from highest bucket to collect k elements.
 *
 * Trace for [1,1,1,2,2,3], k=2:
 *   freq = {1:3, 2:2, 3:1}
 *   buckets: [ [], [3], [2], [1], [], [], [] ]
 *                      freq=1 freq=2 freq=3
 *   Traverse from end: bucket[3]=[1], bucket[2]=[2] → [1,2] ✅
 *
 * Time Complexity:  O(N) — no sorting needed!
 * Space Complexity: O(N) — frequency map + buckets.
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
