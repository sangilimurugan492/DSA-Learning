package heap.kth_largest_element

/**
 * Kth Largest Element in an Array — LeetCode #215
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 *
 * Problem:
 * -------
 * Given an integer array nums and an integer k, return the kth largest element.
 *
 * Example:  nums = [3,2,1,5,6,4], k = 2 → 5
 *           nums = [3,2,3,1,2,4,5,5,6], k = 4 → 4
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE heap problem — QuickSelect is a must-know)
 *
 * Two approaches:
 * 1. Min-Heap of size K: O(N log K) — keep K largest, root = Kth largest
 * 2. QuickSelect: O(N) average — partition like QuickSort, recurse into ONE side
 */

fun main() {
    val nums = intArrayOf(3, 2, 1, 5, 6, 4)
    val k = 2

    println("=== Method 1: Min-Heap ===")
    println("findKthLargest(${nums.toList()}, $k) = ${findKthLargestHeap(nums, k)}")

    println("\n=== Method 2: QuickSelect ===")
    println("findKthLargest(${nums.toList()}, $k) = ${findKthLargestQuickSelect(nums.copyOf(), k)}")

    println("\n=== Step-by-step trace ===")
    findKthLargestTrace(intArrayOf(3, 2, 1, 5, 6, 4), 2)
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 1: MIN-HEAP OF SIZE K — O(N log K)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MIN-HEAP — Keep only K elements. The root is the Kth largest (smallest among top K).
 *
 * Core Idea:
 *   - Push each element into a min-heap.
 *   - If heap size > K, pop the minimum (it can't be in top K).
 *   - At the end, the root = Kth largest.
 *
 * Time Complexity:  O(N log K) — N insertions, each O(log K).
 * Space Complexity: O(K) — heap size.
 */
fun findKthLargestHeap(nums: IntArray, k: Int): Int {
    val minHeap = java.util.PriorityQueue<Int>()
    for (num in nums) {
        minHeap.offer(num)
        if (minHeap.size > k) minHeap.poll()
    }
    return minHeap.peek()
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHOD 2: QUICKSELECT — O(N) average
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * QUICKSELECT — Like QuickSort but only recurse into ONE partition.
 *
 * Core Idea:
 *   - Pick a pivot, partition around it.
 *   - If pivot is at index (N-K), it's the Kth largest → return it.
 *   - If pivot index < (N-K), search right partition.
 *   - If pivot index > (N-K), search left partition.
 *
 * Time Complexity:  O(N) average, O(N²) worst case.
 * Space Complexity: O(1) — in-place.
 */
fun findKthLargestQuickSelect(nums: IntArray, k: Int): Int {
    val targetIndex = nums.size - k
    return quickSelect(nums, 0, nums.size - 1, targetIndex)
}

private fun quickSelect(nums: IntArray, left: Int, right: Int, target: Int): Int {
    if (left == right) return nums[left]

    val pivotIdx = left + (Math.random() * (right - left + 1)).toInt()
    val newPivotIdx = partition(nums, left, right, pivotIdx)

    return when {
        newPivotIdx == target -> nums[newPivotIdx]
        newPivotIdx < target -> quickSelect(nums, newPivotIdx + 1, right, target)
        else -> quickSelect(nums, left, newPivotIdx - 1, target)
    }
}

private fun partition(nums: IntArray, left: Int, right: Int, pivotIdx: Int): Int {
    val pivotVal = nums[pivotIdx]
    nums[pivotIdx] = nums[right]; nums[right] = pivotVal

    var storeIdx = left
    for (i in left until right) {
        if (nums[i] < pivotVal) {
            val temp = nums[storeIdx]; nums[storeIdx] = nums[i]; nums[i] = temp
            storeIdx++
        }
    }
    nums[right] = nums[storeIdx]; nums[storeIdx] = pivotVal
    return storeIdx
}

/**
 * Min-heap with step-by-step trace.
 */
fun findKthLargestTrace(nums: IntArray, k: Int) {
    println("Input: ${nums.toList()}, k=$k")
    val minHeap = java.util.PriorityQueue<Int>()
    for (num in nums) {
        minHeap.offer(num)
        if (minHeap.size > k) minHeap.poll()
        println("  Process $num → heap=$minHeap")
    }
    println("  Result (root): ${minHeap.peek()}")
}
