package heap

/**
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 *
 * Given an integer array nums and an integer k, return the kth largest element
 * in the array. Note that it is the kth largest element in the sorted order,
 * not the kth distinct element.
 *
 * Example 1: nums = [3,2,1,5,6,4], k = 2 → Output: 5
 * Example 2: nums = [3,2,3,1,2,4,5,5,6], k = 4 → Output: 4
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (THE heap problem — QuickSelect is a must-know)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Key question: "What is the Kth largest element?"
 *
 * APPROACH 1: Sort → O(N log N) — simple but not optimal
 * APPROACH 2: Min-Heap of size K → O(N log K)
 * APPROACH 3: QuickSelect → O(N) average (MUST KNOW for FAANG!)
 *
 * WHY Min-Heap of size K?
 *   - We only need the Kth largest, not all elements sorted
 *   - Keep a min-heap of the K largest elements seen so far
 *   - The root of the min-heap = Kth largest (smallest among top K)
 *   - When heap size > K, pop the minimum (it can't be in top K)
 *
 * WHY QuickSelect?
 *   - It's like QuickSort but we only recurse into ONE partition
 *   - Average O(N) because each step reduces the search space by ~half
 *   - Worst case O(N²) but randomized pivot makes this extremely unlikely
 *
 * QuickSelect algorithm:
 *   1. Pick a pivot (random for best average case)
 *   2. Partition: elements < pivot go left, > pivot go right
 *   3. If pivot is at index (N-K), it's the Kth largest → return it
 *   4. If pivot index < (N-K), search right partition
 *   5. If pivot index > (N-K), search left partition
 *
 * Connection to other problems:
 *   - Top K Frequent: same "find top K" pattern
 *   - Median of Two Sorted Arrays: related "find Kth element" pattern
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Kth Largest Element ===")
    println("Sort [3,2,1,5,6,4], k=2: ${findKthLargestSort(intArrayOf(3, 2, 1, 5, 6, 4), 2)}")
    println("Heap [3,2,1,5,6,4], k=2:  ${findKthLargestHeap(intArrayOf(3, 2, 1, 5, 6, 4), 2)}")
    println("QuickSelect [3,2,1,5,6,4], k=2: ${findKthLargestQuickSelect(intArrayOf(3, 2, 1, 5, 6, 4), 2)}")
    println("---")
    println("QuickSelect [3,2,3,1,2,4,5,5,6], k=4: ${findKthLargestQuickSelect(intArrayOf(3, 2, 3, 1, 2, 4, 5, 5, 6), 4)}")
}

/**
 * APPROACH 1: Sort
 * Time Complexity: O(N log N)
 * Space Complexity: O(1) or O(N) depending on sort implementation
 *
 * Sort descending, return element at index k-1.
 * Simple but not optimal — we sort ALL elements when we only need one.
 */
fun findKthLargestSort(nums: IntArray, k: Int): Int {
    return nums.sortedArrayDescending()[k - 1]
}

/**
 * APPROACH 2: Min-Heap of size K
 * Time Complexity: O(N log K) — N insertions, each O(log K)
 * Space Complexity: O(K) — heap size
 *
 * Keep only K elements in the heap. The root is the Kth largest.
 *
 * Trace for [3,2,1,5,6,4], k=2:
 * Process 3: heap = [3]
 * Process 2: heap = [2, 3]  size=2=k
 * Process 1: push 1 → heap = [1, 3, 2] → pop min → heap = [2, 3]
 * Process 5: push 5 → heap = [2, 3, 5] → pop min → heap = [3, 5]
 * Process 6: push 6 → heap = [3, 5, 6] → pop min → heap = [5, 6]
 * Process 4: push 4 → heap = [4, 6, 5] → pop min → heap = [5, 6]
 *
 * Root = 5 ✅
 */
fun findKthLargestHeap(nums: IntArray, k: Int): Int {
    val minHeap = java.util.PriorityQueue<Int>()
    for (num in nums) {
        minHeap.offer(num)
        if (minHeap.size > k) minHeap.poll()
    }
    return minHeap.peek()
}

/**
 * APPROACH 3: QuickSelect (OPTIMAL — O(N) average)
 * Time Complexity: O(N) average, O(N²) worst case
 * Space Complexity: O(1) — in-place partitioning
 *
 * QuickSelect = QuickSort but only recurse into ONE partition.
 * We're looking for the element at index (N-K) in sorted order.
 *
 * Trace for [3,2,1,5,6,4], k=2 (target index = 6-2 = 4):
 * Partition around pivot=4: [3,2,1,4,6,5] → pivot at index 3
 *   3 < 4 → search right half [6,5] for target index 4
 * Partition around pivot=5: [5,6] → pivot at index 4 ✅
 *
 * Result: 5 ✅
 */
fun findKthLargestQuickSelect(nums: IntArray, k: Int): Int {
    val targetIndex = nums.size - k  // index of Kth largest in sorted array
    return quickSelect(nums, 0, nums.size - 1, targetIndex)
}

private fun quickSelect(nums: IntArray, left: Int, right: Int, target: Int): Int {
    if (left == right) return nums[left]

    // Random pivot for best average case
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
    // Move pivot to end
    nums[pivotIdx] = nums[right]
    nums[right] = pivotVal

    var storeIdx = left
    for (i in left until right) {
        if (nums[i] < pivotVal) {
            val temp = nums[storeIdx]
            nums[storeIdx] = nums[i]
            nums[i] = temp
            storeIdx++
        }
    }
    // Move pivot to its final position
    nums[right] = nums[storeIdx]
    nums[storeIdx] = pivotVal
    return storeIdx
}
