package heap

/**
 * https://leetcode.com/problems/top-k-frequent-elements/
 *
 * Given an integer array nums and an integer k, return the k most frequent elements.
 * You may return the answer in any order.
 *
 * Example 1: nums = [1,1,1,2,2,3], k = 2 → Output: [1,2]
 * Example 2: nums = [1], k = 1 → Output: [1]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 15 most asked — heap + bucket sort pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Key question: "Which elements appear most often?"
 *
 * APPROACH 1: Min-Heap of size K
 *   1. Count frequency of each element using HashMap
 *   2. Maintain a min-heap of size K (ordered by frequency)
 *   3. For each (element, freq): push to heap; if heap size > K, pop min
 *   4. The heap contains the K most frequent elements
 *
 *   WHY min-heap? Because we want to keep the LARGEST frequencies.
 *   A min-heap of size K evicts the smallest frequency when full,
 *   ensuring only the K largest remain.
 *
 * APPROACH 2: Bucket Sort (OPTIMAL — O(N) time!)
 *   1. Count frequency of each element
 *   2. Create buckets where bucket[i] = list of elements with frequency i
 *   3. Traverse buckets from highest frequency down, collect K elements
 *
 *   WHY O(N)? Because frequency ≤ N, so we have at most N buckets.
 *   Traversing from the end gives us the most frequent elements first.
 *
 * Connection to other problems:
 *   - This is the HEAP pattern problem — "find top K" always uses heap or bucket sort
 *   - Similar: Kth Largest Element, Top K Frequent Words
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Top K Frequent Elements ===")
    println("Heap [1,1,1,2,2,3], k=2: ${topKFrequentHeap(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList()}")
    println("Bucket [1,1,1,2,2,3], k=2: ${topKFrequentBucket(intArrayOf(1, 1, 1, 2, 2, 3), 2).toList()}")
    println("---")
    println("Bucket [1], k=1: ${topKFrequentBucket(intArrayOf(1), 1).toList()}")
    println("Bucket [3,0,1,0], k=1: ${topKFrequentBucket(intArrayOf(3, 0, 1, 0), 1).toList()}")
}

/**
 * APPROACH 1: Min-Heap
 * Time Complexity: O(N log K) — N elements, each heap operation is O(log K)
 * Space Complexity: O(N + K) — frequency map + heap
 *
 * Trace for [1,1,1,2,2,3], k=2:
 * freq = {1:3, 2:2, 3:1}
 *
 * Process (1,3): heap = [(1,3)]
 * Process (2,2): heap = [(2,2), (1,3)]  size=2=k
 * Process (3,1): push (3,1) → heap = [(3,1), (1,3), (2,2)] → pop min → heap = [(2,2), (1,3)]
 *
 * Result: [1, 2] ✅
 */
fun topKFrequentHeap(nums: IntArray, k: Int): IntArray {
    // Step 1: Count frequencies
    val freq = HashMap<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    // Step 2: Min-heap of size K (ordered by frequency)
    val minHeap = java.util.PriorityQueue<Pair<Int, Int>>(compareBy { it.second })
    for ((num, count) in freq) {
        minHeap.offer(Pair(num, count))
        if (minHeap.size > k) minHeap.poll()  // evict smallest frequency
    }

    return minHeap.map { it.first }.toIntArray()
}

/**
 * APPROACH 2: Bucket Sort (OPTIMAL)
 * Time Complexity: O(N) — linear!
 * Space Complexity: O(N) — frequency map + buckets
 *
 * Trace for [1,1,1,2,2,3], k=2:
 * freq = {1:3, 2:2, 3:1}
 *
 * buckets:
 *   bucket[0] = []
 *   bucket[1] = [3]     ← 3 appears 1 time
 *   bucket[2] = [2]     ← 2 appears 2 times
 *   bucket[3] = [1]     ← 1 appears 3 times
 *
 * Traverse from end: bucket[3]=[1] → add 1, count=1
 *                     bucket[2]=[2] → add 2, count=2=k → STOP
 *
 * Result: [1, 2] ✅
 */
fun topKFrequentBucket(nums: IntArray, k: Int): IntArray {
    // Step 1: Count frequencies
    val freq = HashMap<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    // Step 2: Bucket sort by frequency
    val n = nums.size
    val buckets = Array(n + 1) { mutableListOf<Int>() }
    for ((num, count) in freq) {
        buckets[count].add(num)
    }

    // Step 3: Collect top K from highest frequency
    val result = mutableListOf<Int>()
    for (i in n downTo 0) {
        for (num in buckets[i]) {
            result.add(num)
            if (result.size == k) return result.toIntArray()
        }
    }
    return result.toIntArray()
}
