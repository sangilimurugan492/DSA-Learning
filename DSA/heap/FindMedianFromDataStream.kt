package heap

/**
 * https://leetcode.com/problems/find-median-from-data-stream/
 *
 * The median is the middle value in an ordered integer list. If the size is even,
 * the median is the mean of the two middle values.
 * Design a data structure that supports addNum and findMedian in O(log N) time.
 *
 * Example: addNum(1), addNum(2), findMedian() → 1.5
 *          addNum(3), findMedian() → 2.0
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE two-heap problem — must-know design pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * KEY INSIGHT: Use TWO heaps to maintain the median!
 *   - Max-heap (left): stores the SMALLER half of numbers
 *   - Min-heap (right): stores the LARGER half of numbers
 *
 * INVARIANTS:
 *   1. Size of max-heap = size of min-heap (even total) OR
 *      Size of max-heap = size of min-heap + 1 (odd total)
 *   2. All elements in max-heap ≤ all elements in min-heap
 *
 * MEDIAN:
 *   - If sizes equal: median = (max-heap.max + min-heap.min) / 2.0
 *   - If max-heap larger: median = max-heap.max
 *
 * ADD NUM:
 *   1. Add to max-heap first
 *   2. Balance: move max from max-heap to min-heap
 *   3. If min-heap > max-heap: move min from min-heap to max-heap
 *
 * WHY two heaps? The median is the boundary between the two halves.
 * We need O(1) access to the largest of the small half and smallest
 * of the large half. Heaps give exactly this!
 *
 * Connection to other problems:
 *   - Sliding Window Median: same two-heap pattern with lazy deletion
 *   - This is a fundamental design pattern for streaming median problems
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Find Median from Data Stream ===")
    val medianFinder = MedianFinder()
    medianFinder.addNum(1)
    medianFinder.addNum(2)
    println("Median after [1,2]: ${medianFinder.findMedian()}")  // 1.5
    medianFinder.addNum(3)
    println("Median after [1,2,3]: ${medianFinder.findMedian()}")  // 2.0
    medianFinder.addNum(4)
    println("Median after [1,2,3,4]: ${medianFinder.findMedian()}")  // 2.5
}

/**
 * Two-Heap Median Finder
 *
 * addNum:    O(log N) — two heap operations
 * findMedian: O(1) — peek at heap roots
 * Space: O(N)
 *
 * Trace for adding [1, 2, 3, 4]:
 *
 * addNum(1): maxHeap=[1], minHeap=[] → balance → maxHeap=[1], minHeap=[]
 *   Median = 1.0
 *
 * addNum(2): maxHeap=[2,1], minHeap=[] → balance: move 2 to minHeap
 *   maxHeap=[1], minHeap=[2] → Median = (1+2)/2 = 1.5
 *
 * addNum(3): maxHeap=[3,1], minHeap=[2] → balance: move 3 to minHeap
 *   maxHeap=[1], minHeap=[2,3] → minHeap larger → move 2 to maxHeap
 *   maxHeap=[2,1], minHeap=[3] → Median = 2.0
 *
 * addNum(4): maxHeap=[4,2,1], minHeap=[3] → balance: move 4 to minHeap
 *   maxHeap=[2,1], minHeap=[3,4] → Median = (2+3)/2 = 2.5
 */
class MedianFinder {
    // Max-heap for the smaller half (left side)
    private val maxHeap = java.util.PriorityQueue<Int>(reverseOrder())
    // Min-heap for the larger half (right side)
    private val minHeap = java.util.PriorityQueue<Int>()

    fun addNum(num: Int) {
        // Step 1: Add to max-heap
        maxHeap.offer(num)
        // Step 2: Balance — move largest from max-heap to min-heap
        minHeap.offer(maxHeap.poll())
        // Step 3: If min-heap is now larger, move smallest back
        if (minHeap.size > maxHeap.size) {
            maxHeap.offer(minHeap.poll())
        }
    }

    fun findMedian(): Double {
        return if (maxHeap.size == minHeap.size) {
            (maxHeap.peek() + minHeap.peek()) / 2.0
        } else {
            maxHeap.peek().toDouble()
        }
    }
}
