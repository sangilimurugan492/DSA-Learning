# Find Median from Data Stream — Detailed Explanation

> **LeetCode #295** | [Problem Link](https://leetcode.com/problems/find-median-from-data-stream/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (THE two-heap problem — must-know design pattern)  
> **Topic:** Heap, Design

---

## 📋 Problem Statement

Design a data structure that supports `addNum` and `findMedian` in O(log N) time.

### Example

```
addNum(1), addNum(2), findMedian() → 1.5
addNum(3), findMedian() → 2.0
```

---

## 🧩 Method 1: Brute Force — O(N log N) findMedian

### Core Idea

Store all numbers in a list. Sort on each `findMedian` call.

### Complexity

| Operation | Time | Space |
|-----------|------|-------|
| addNum | O(1) | O(N) |
| findMedian | **O(N log N)** | — |

---

## 🧩 Method 2: Two-Heap — O(log N) addNum, O(1) findMedian

### Core Idea

Max-heap stores the **smaller half**, min-heap stores the **larger half**. The median is at the boundary.

### Key Insight

> The median is the boundary between two halves. We need O(1) access to the largest of the small half (max-heap root) and smallest of the large half (min-heap root). Heaps give exactly this!

### Invariants

1. `maxHeap.size == minHeap.size` (even total) OR `maxHeap.size == minHeap.size + 1` (odd total)
2. All elements in max-heap ≤ all elements in min-heap

### addNum Steps

1. Add to max-heap.
2. Move max from max-heap to min-heap (balance).
3. If min-heap > max-heap, move min from min-heap to max-heap.

### Dry Run — adding [1, 2, 3, 4]

| addNum | maxHeap | minHeap | Median |
|:------:|:-------:|:-------:|:------:|
| 1 | [1] | [] | 1.0 |
| 2 | [1] | [2] | 1.5 |
| 3 | [2,1] | [3] | 2.0 |
| 4 | [2,1] | [3,4] | 2.5 |

### Code

```kotlin
class MedianFinder {
    private val maxHeap = PriorityQueue<Int>(reverseOrder())  // smaller half
    private val minHeap = PriorityQueue<Int>()                 // larger half

    fun addNum(num: Int) {
        maxHeap.offer(num)
        minHeap.offer(maxHeap.poll())
        if (minHeap.size > maxHeap.size) maxHeap.offer(minHeap.poll())
    }

    fun findMedian(): Double {
        return if (maxHeap.size == minHeap.size)
            (maxHeap.peek() + minHeap.peek()) / 2.0
        else maxHeap.peek().toDouble()
    }
}
```

### Complexity

| Operation | Time | Space |
|-----------|------|-------|
| addNum | **O(log N)** | O(N) |
| findMedian | **O(1)** | — |

---

## 📊 Comparison Table

| Aspect | Brute Force | Two-Heap |
|--------|-------------|----------|
| **addNum** | O(1) | O(log N) |
| **findMedian** | O(N log N) | O(1) |
| **Space** | O(N) | O(N) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Two-heap pattern:** Max-heap for smaller half, min-heap for larger half. Median at the boundary.
2. **Balancing:** After adding, ensure max-heap size ≥ min-heap size (by at most 1).
3. **O(1) findMedian:** Just peek at heap roots — no sorting needed.
4. **Pattern:** Two-heap — extends to Sliding Window Median, IPO, Find Median from Running Stream.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Find Median from Data Stream | [#295](https://leetcode.com/problems/find-median-from-data-stream/) | Hard |
| Sliding Window Median | [#480](https://leetcode.com/problems/sliding-window-median/) | Hard |
| IPO | [#502](https://leetcode.com/problems/ipo/) | Hard |
| Kth Largest Element | [#215](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium |
