# Kth Largest Element in an Array — Detailed Explanation

> **LeetCode #215** | [Problem Link](https://leetcode.com/problems/kth-largest-element-in-an-array/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (THE heap problem — QuickSelect is a must-know)  
> **Topic:** Heap, QuickSelect, Partitioning

---

## 📋 Problem Statement

Given an integer array nums and an integer k, return the kth largest element in sorted order.

### Examples

| nums | k | Output |
|------|---|--------|
| `[3,2,1,5,6,4]` | 2 | 5 |
| `[3,2,3,1,2,4,5,5,6]` | 4 | 4 |

---

## 🧩 Method 1: Min-Heap of Size K — O(N log K)

### Core Idea

Keep only K elements in a min-heap. The root is the Kth largest (smallest among top K). When heap size > K, pop the minimum.

### Key Insight

> We only need the Kth largest, not all elements sorted. Keep a min-heap of the K largest elements seen so far. The root = Kth largest.

### Dry Run — `nums = [3,2,1,5,6,4], k = 2`

| num | Action | Heap |
|:---:|:------:|:----:|
| 3 | push 3 | [3] |
| 2 | push 2 | [2,3] (size=2=k) |
| 1 | push 1, pop min | [2,3] |
| 5 | push 5, pop min | [3,5] |
| 6 | push 6, pop min | [5,6] |
| 4 | push 4, pop min | [5,6] |

✅ **Result: 5** (root of heap)

### Code

```kotlin
fun findKthLargestHeap(nums: IntArray, k: Int): Int {
    val minHeap = java.util.PriorityQueue<Int>()
    for (num in nums) {
        minHeap.offer(num)
        if (minHeap.size > k) minHeap.poll()
    }
    return minHeap.peek()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log K) | N insertions, each O(log K) |
| **Space** | O(K) | Heap size |

---

## 🧩 Method 2: QuickSelect — O(N) average

### Core Idea

Like QuickSort but only recurse into ONE partition. Looking for element at index (N-K) in sorted order.

### Key Insight

> QuickSelect partitions around a pivot. If pivot lands at target index (N-K), we found the Kth largest. Otherwise, recurse into the side that contains the target — halving the search space each time.

### Code

```kotlin
fun findKthLargestQuickSelect(nums: IntArray, k: Int): Int {
    val targetIndex = nums.size - k
    return quickSelect(nums, 0, nums.size - 1, targetIndex)
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) avg, O(N²) worst | Each step halves search space |
| **Space** | O(1) | In-place |

---

## 📊 Comparison Table

| Aspect | Min-Heap | QuickSelect |
|--------|----------|-------------|
| **Time** | O(N log K) | O(N) avg |
| **Space** | O(K) | O(1) |
| **Deterministic?** | Yes | No (randomized) |
| **Interview preference** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Min-heap of size K:** Root = Kth largest. Pop when size > K to discard elements that can't be in top K.
2. **QuickSelect:** QuickSort but only recurse into ONE side. O(N) average.
3. **Target index:** Kth largest = element at index (N-K) in sorted order.
4. **Randomized pivot:** Prevents O(N²) worst case on sorted input.
5. **Pattern:** "Find top K" — extends to Top K Frequent, Kth Smallest, Median of Two Arrays.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Kth Largest Element | [#215](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium |
| Top K Frequent Elements | [#347](https://leetcode.com/problems/top-k-frequent-elements/) | Medium |
| Kth Smallest in BST | [#230](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium |
| Median of Two Sorted Arrays | [#4](https://leetcode.com/problems/median-of-two-sorted-arrays/) | Hard |
