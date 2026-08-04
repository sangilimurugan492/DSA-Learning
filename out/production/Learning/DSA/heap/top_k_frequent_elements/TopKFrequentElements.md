# Top K Frequent Elements — Detailed Explanation

> **LeetCode #347** | [Problem Link](https://leetcode.com/problems/top-k-frequent-elements/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic Heap + HashMap)  
> **Topic:** Heap, HashMap, Sorting

---

## 📋 Problem Statement

Given an integer array and an integer k, return the k most frequent elements.

### Examples

| nums | k | Output |
|------|:-:|:------:|
| `[1,1,1,2,2,3]` | 2 | `[1,2]` |
| `[1]` | 1 | `[1]` |

---

## 🧩 Method 1: Sort by Frequency — O(N log N)

### Core Idea

Build frequency map. Sort entries by frequency (desc). Take top k.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log N) | Sorting dominates |
| **Space** | O(N) | Frequency map |

---

## 🧩 Method 2: Min-Heap — O(N log K)

### Core Idea

Count frequencies. Maintain a min-heap of size k (by frequency). Push entries; if heap size > k, pop the least frequent.

### Key Insight

> Min-heap of size k keeps the TOP k elements. The smallest frequency is at the root. When heap exceeds k, evict the root (least frequent among the top k).

### Dry Run — `nums = [1,1,1,2,2,3], k = 2`

Frequencies: `{1:3, 2:2, 3:1}`

| Entry | Action | Heap | Size > k? |
|:-----:|:------:|:----:|:---------:|
| (1, 3) | push | [(1,3)] | No |
| (2, 2) | push | [(2,2), (1,3)] | No |
| (3, 1) | push | [(3,1), (1,3), (2,2)] | Yes → evict (3,1) |

Final heap: `[(2,2), (1,3)]` → **Result: [1, 2]** ✅

### Code

```kotlin
fun topKFrequentHeap(nums: IntArray, k: Int): IntArray {
    val freq = HashMap<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1

    val heap = java.util.PriorityQueue<Pair<Int, Int>> { a, b -> a.second - b.second }
    for ((num, count) in freq) {
        heap.offer(num to count)
        if (heap.size > k) heap.poll()
    }
    return heap.map { it.first }.toIntArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N log K) | N entries, each heap op is O(log K) |
| **Space** | O(N) | Frequency map + heap of size k |

---

## 📊 Comparison Table

| Aspect | Sort by Frequency | Min-Heap |
|--------|-------------------|----------|
| **Time** | O(N log N) | O(N log K) |
| **Space** | O(N) | O(N) |
| **Best when** | k ≈ N | k << N |
| **Interview preference** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Min-heap of size k:** Keeps top k elements. Root = smallest (least frequent).
2. **Evict on overflow:** When heap size > k → poll root (evict least frequent).
3. **Why min-heap not max-heap:** We want to quickly remove the LEAST frequent, not the most.
4. **Pattern:** Top K with heap — extends to Kth Largest, K Closest Points, Top K Frequent Words.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Top K Frequent Elements | [#347](https://leetcode.com/problems/top-k-frequent-elements/) | Medium |
| Kth Largest Element | [#215](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium |
| Top K Frequent Words | [#692](https://leetcode.com/problems/top-k-frequent-words/) | Medium |
| K Closest Points to Origin | [#973](https://leetcode.com/problems/k-closest-points-to-origin/) | Medium |
