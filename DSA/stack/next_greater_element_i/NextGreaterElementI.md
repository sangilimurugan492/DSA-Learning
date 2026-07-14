# Next Greater Element I — Detailed Explanation

> **LeetCode #496** | [Problem Link](https://leetcode.com/problems/next-greater-element-i/)  
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Classic Monotonic Stack intro)  
> **Topic:** Monotonic Stack, HashMap

---

## 📋 Problem Statement

Given nums1 (subset of nums2), find next greater element for each nums1[i] in nums2.

### Example

`nums1 = [4,1,2], nums2 = [1,3,4,2]` → `[-1,3,-1]`

---

## 🧩 Method 1: Brute Force — O(N × M)

### Core Idea

For each element in nums1, scan nums2 to find it, then scan forward for next greater.

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N × M) | For each nums1, scan nums2 |
| **Space** | O(N) | Result |

---

## 🧩 Method 2: Monotonic Stack — O(M)

### Core Idea

Build nextGreater map for nums2 using a decreasing stack. Then lookup nums1 values in the map.

### Key Insight

> Process nums2 once with a stack. When a larger element is found, pop all smaller elements and record their next greater. O(1) lookup per nums1 element.

### Dry Run — `nums2 = [1,3,4,2]`

| num | Stack (before) | Action | Map | Stack (after) |
|:---:|:--------------:|:------:|:---:|:-------------:|
| 1 | [] | push 1 | {} | [1] |
| 3 | [1] | 3>1 → pop 1, map[1]=3. push 3 | {1:3} | [3] |
| 4 | [3] | 4>3 → pop 3, map[3]=4. push 4 | {1:3, 3:4} | [4] |
| 2 | [4] | 2<4 → push 2 | {1:3, 3:4} | [4, 2] |

Remaining [4, 2] → no next greater → -1.

Lookup: `nums1=[4,1,2]` → `[-1, 3, -1]` ✅

### Code

```kotlin
fun nextGreaterElementStack(nums1: IntArray, nums2: IntArray): IntArray {
    val nextGreater = hashMapOf<Int, Int>()
    val stack = ArrayDeque<Int>()
    for (num in nums2) {
        while (stack.isNotEmpty() && num > stack.last()) {
            nextGreater[stack.removeLast()] = num
        }
        stack.addLast(num)
    }
    return nums1.map { nextGreater.getOrDefault(it, -1) }.toIntArray()
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(M) | Process nums2 once |
| **Space** | O(M) | Map + stack |

---

## 📊 Comparison Table

| Aspect | Brute Force | Monotonic Stack |
|--------|-------------|-----------------|
| **Time** | O(N × M) | O(M) |
| **Space** | O(N) | O(M) |
| **Interview preference** | ⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🔑 Key Takeaways

1. **Decreasing stack:** Maintains elements waiting for their next greater.
2. **Pop on larger:** When current > stack top → found next greater for popped.
3. **Map lookup:** Precompute all next greaters in O(M), then O(1) per query.
4. **Pattern:** Next greater element — extends to Daily Temperatures, Online Stock Span, Next Greater Element II.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Next Greater Element I | [#496](https://leetcode.com/problems/next-greater-element-i/) | Easy |
| Next Greater Element II | [#503](https://leetcode.com/problems/next-greater-element-ii/) | Medium |
| Daily Temperatures | [#739](https://leetcode.com/problems/daily-temperatures/) | Medium |
| Online Stock Span | [#901](https://leetcode.com/problems/online-stock-span/) | Medium |
