package heap.top_k_frequent_elements

/**
 * Top K Frequent Elements — LeetCode #347
 * https://leetcode.com/problems/top-k-frequent-elements/
 *
 * Problem:
 * -------
 * Given an integer array and an integer k, return the k most frequent elements.
 *
 * Example:  nums = [1,1,1,2,2,3], k = 2  →  [1,2]
 *           nums = [1], k = 1  →  [1]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic Heap + HashMap)
 *
 * Two approaches:
 * 1. Sort by Frequency: O(N log N) — count, sort, take top k
 * 2. Min-Heap: O(N log K) — count, maintain heap of size k
 */

fun main() {
    val nums = intArrayOf(1, 1, 1, 2, 2, 3)
    val k = 2

    println("=== Method 1: Sort by Frequency ===")
    println("topKFrequent(${nums.toList()}, $k) = ${topKFrequentSort(nums, k).toList()}")

    println("\n=== Method 2: Min-Heap ===")
    println("topKFrequent(${nums.toList()}, $k) = ${topKFrequentHeap(nums, k).toList()}")

    println("\n=== Step-by-step trace ===")
    topKFrequentTrace(nums, k)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: SORT BY FREQUENCY — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SORT — Count frequencies with HashMap. Sort entries by frequency (desc). Take top k.
 *
 * Core Idea:
 *   - Build frequency map.
 *   - Sort entries by frequency in descending order.
 *   - Take first k elements.
 *
 * Time Complexity:  O(N log N) — sorting dominates.
 * Space Complexity: O(N) — frequency map.
 */
fun topKFrequentSort(nums: IntArray, k: Int): IntArray {
    val freq = HashMap<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    return freq.entries
        .sortedByDescending { it.value }
        .take(k)
        .map { it.key }
        .toIntArray()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: MIN-HEAP — O(N log K)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MIN-HEAP — Count frequencies. Maintain a min-heap of size k (by frequency).
 *
 * Core Idea:
 *   - Build frequency map.
 *   - Use a min-heap of size k. Push entries; if heap size > k, pop the least frequent.
 *   - At the end, heap contains the k most frequent elements.
 *
 * Key Insight:
 *   - Min-heap of size k keeps the TOP k elements. The smallest frequency is at the root.
 *   - When heap exceeds k, evict the root (least frequent among the top k).
 *
 * Time Complexity:  O(N log K) — N entries, each heap operation is O(log K).
 * Space Complexity: O(N) — frequency map + heap of size k.
 */
fun topKFrequentHeap(nums: IntArray, k: Int): IntArray {
    val freq = HashMap<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    // Min-heap: compare by frequency (ascending). Root = least frequent.
    val heap = java.util.PriorityQueue<Pair<Int, Int>> { a, b -> a.second - b.second }

    for ((num, count) in freq) {
        heap.offer(num to count)
        if (heap.size > k) heap.poll()  // Evict least frequent.
    }

    return heap.map { it.first }.toIntArray()
}

/**
 * Min-heap with step-by-step trace.
 */
fun topKFrequentTrace(nums: IntArray, k: Int) {
    val freq = HashMap<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1
    println("Input: ${nums.toList()}, k=$k")
    println("Frequencies: $freq")

    val heap = java.util.PriorityQueue<Pair<Int, Int>> { a, b -> a.second - b.second }

    for ((num, count) in freq) {
        heap.offer(num to count)
        println("  Push ($num, freq=$count) → heap=${heap.toList()}")
        if (heap.size > k) {
            val evicted = heap.poll()
            println("  Heap size > $k → evict $evicted → heap=${heap.toList()}")
        }
    }

    println("  Result: ${heap.map { it.first }.toList()}")
}
