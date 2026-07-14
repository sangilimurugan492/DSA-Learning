package heap.find_median_from_data_stream

/**
 * Find Median from Data Stream — LeetCode #295
 * https://leetcode.com/problems/find-median-from-data-stream/
 *
 * Problem:
 * -------
 * Design a data structure that supports addNum and findMedian in O(log N) time.
 *
 * Example:  addNum(1), addNum(2), findMedian() → 1.5
 *           addNum(3), findMedian() → 2.0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE two-heap problem — must-know design pattern)
 *
 * Two approaches:
 * 1. Brute Force: O(N log N) addNum — sort on each findMedian
 * 2. Two-Heap: O(log N) addNum, O(1) findMedian — max-heap (small half) + min-heap (large half)
 */

fun main() {
    println("=== Method 1: Brute Force ===")
    val bf = MedianFinderBruteForce()
    bf.addNum(1); bf.addNum(2)
    println("Median after [1,2]: ${bf.findMedian()}")  // 1.5
    bf.addNum(3)
    println("Median after [1,2,3]: ${bf.findMedian()}")  // 2.0

    println("\n=== Method 2: Two-Heap ===")
    val mf = MedianFinder()
    mf.addNum(1); mf.addNum(2)
    println("Median after [1,2]: ${mf.findMedian()}")  // 1.5
    mf.addNum(3)
    println("Median after [1,2,3]: ${mf.findMedian()}")  // 2.0
    mf.addNum(4)
    println("Median after [1,2,3,4]: ${mf.findMedian()}")  // 2.5

    println("\n=== Step-by-step trace ===")
    medianFinderTrace()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: BRUTE FORCE — O(N log N) findMedian
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * BRUTE FORCE — Store all numbers, sort on findMedian.
 *
 * Time Complexity:  addNum O(1), findMedian O(N log N).
 * Space Complexity: O(N).
 */
class MedianFinderBruteForce {
    private val nums = mutableListOf<Int>()

    fun addNum(num: Int) = nums.add(num)

    fun findMedian(): Double {
        nums.sort()
        val n = nums.size
        return if (n % 2 == 1) nums[n / 2].toDouble()
        else (nums[n / 2 - 1] + nums[n / 2]) / 2.0
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: TWO-HEAP — O(log N) addNum, O(1) findMedian
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * TWO-HEAP — Max-heap for smaller half, min-heap for larger half.
 *
 * Core Idea:
 *   - Max-heap (left): stores the SMALLER half of numbers.
 *   - Min-heap (right): stores the LARGER half of numbers.
 *   - Invariant: maxHeap.size == minHeap.size OR maxHeap.size == minHeap.size + 1.
 *   - All elements in max-heap ≤ all elements in min-heap.
 *
 * addNum:
 *   1. Add to max-heap.
 *   2. Move max from max-heap to min-heap (balance).
 *   3. If min-heap > max-heap, move min from min-heap to max-heap.
 *
 * findMedian:
 *   - If sizes equal: (maxHeap.max + minHeap.min) / 2.0
 *   - If max-heap larger: maxHeap.max
 *
 * Time Complexity:  addNum O(log N), findMedian O(1).
 * Space Complexity: O(N).
 */
class MedianFinder {
    private val maxHeap = java.util.PriorityQueue<Int>(reverseOrder())  // smaller half
    private val minHeap = java.util.PriorityQueue<Int>()                 // larger half

    fun addNum(num: Int) {
        maxHeap.offer(num)
        minHeap.offer(maxHeap.poll())
        if (minHeap.size > maxHeap.size) maxHeap.offer(minHeap.poll())
    }

    fun findMedian(): Double {
        return if (maxHeap.size == minHeap.size)
            (maxHeap.peek() + minHeap.peek()) / 2.0
        else
            maxHeap.peek().toDouble()
    }
}

/**
 * Two-heap with step-by-step trace.
 */
fun medianFinderTrace() {
    val maxHeap = java.util.PriorityQueue<Int>(reverseOrder())
    val minHeap = java.util.PriorityQueue<Int>()

    for (num in listOf(1, 2, 3, 4)) {
        maxHeap.offer(num)
        minHeap.offer(maxHeap.poll())
        if (minHeap.size > maxHeap.size) maxHeap.offer(minHeap.poll())

        val median = if (maxHeap.size == minHeap.size)
            (maxHeap.peek() + minHeap.peek()) / 2.0
        else maxHeap.peek().toDouble()

        println("  addNum($num) → maxHeap=$maxHeap, minHeap=$minHeap, median=$median")
    }
}
