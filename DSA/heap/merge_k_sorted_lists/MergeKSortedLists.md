# Merge K Sorted Lists — Detailed Explanation

> **LeetCode #23** | [Problem Link](https://leetcode.com/problems/merge-k-sorted-lists/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Hard — THE heap/merge problem)  
> **Topic:** Heap, Linked List, Divide & Conquer

---

## 📋 Problem Statement

Given an array of k sorted linked-lists, merge all into one sorted linked-list.

### Example

`lists = [[1,4,5],[1,3,4],[2,6]]` → `1→1→2→3→4→4→5→6`

---

## 🧩 Method 1: Min-Heap — O(N log K)

### Core Idea

Push all list heads into a min-heap. Pop the minimum, add to result, push its next node. Repeat until heap is empty.

### Key Insight

> We process N nodes total, each heap operation is O(log K). The heap never has more than K elements at once. This is optimal for comparison-based merging.

### Dry Run — `lists = [[1,4,5],[1,3,4],[2,6]]`

| Step | Pop | Result | Push next | Heap |
|:----:|:---:|:------:|:---------:|:----:|
| init | — | — | — | [1,1,2] |
| 1 | 1 (list1) | 1 | 4 | [1,2,4] |
| 2 | 1 (list2) | 1→1 | 3 | [2,3,4] |
| 3 | 2 (list3) | 1→1→2 | 6 | [3,4,6] |
| 4 | 3 (list2) | 1→1→2→3 | 4 | [4,4,6] |
| 5 | 4 (list1) | 1→1→2→3→4 | 5 | [4,5,6] |
| 6 | 4 (list2) | 1→1→2→3→4→4 | — | [5,6] |
| 7 | 5 (list1) | 1→1→2→3→4→4→5 | — | [6] |
| 8 | 6 (list3) | 1→1→2→3→4→4→5→6 | — | [] |

✅ **Result: 1→1→2→3→4→4→5→6**

### Code

```kotlin
fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    if (lists.isEmpty()) return null
    val minHeap = PriorityQueue<ListNode>(compareBy { it.`val` })
    for (list in lists) if (list != null) minHeap.offer(list)

    val dummy = ListNode(0)
    var tail = dummy
    while (minHeap.isNotEmpty()) {
        val smallest = minHeap.poll()
        tail.next = smallest; tail = tail.next!!
        if (smallest.next != null) minHeap.offer(smallest.next)
    }
    return dummy.next
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log K) | N nodes, each O(log K) heap op |
| **Space** | O(K) | Heap stores at most K nodes |

---

## 🧩 Method 2: Divide & Conquer — O(N log K)

### Core Idea

Pair up lists and merge each pair. Repeat until one list remains. Same complexity, no heap overhead.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log K) | log K rounds, each O(N) |
| **Space** | O(log K) | Recursion stack |

---

## 📊 Comparison Table

| Aspect | Min-Heap | Divide & Conquer |
|--------|----------|-------------------|
| **Time** | O(N log K) | O(N log K) |
| **Space** | O(K) | O(log K) |
| **Simplicity** | Simpler | More code |
| **Interview preference** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **K-way merge pattern:** Use a min-heap to efficiently find the minimum across K sorted sources.
2. **Dummy head:** Use a dummy node for easy linked-list construction.
3. **O(N log K) is optimal:** We must look at all N nodes and compare across K lists.
4. **Pattern:** K-way merge — extends to Kth Smallest in Sorted Matrix, Smallest Range Covering Elements.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Merge K Sorted Lists | [#23](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard |
| Merge Two Sorted Lists | [#21](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy |
| Kth Smallest in Sorted Matrix | [#378](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) | Medium |
| Smallest Range Covering Elements | [#632](https://leetcode.com/problems/smallest-range-covering-elements-from-k-lists/) | Hard |
