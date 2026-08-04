# Longest Subarray with Sum K — Detailed Explanation

> **GeeksforGeeks** | [Problem Link](https://www.geeksforgeeks.org/problems/longest-sub-array-with-sum-k/0809)
> **FAANG Importance:** ⭐⭐⭐⭐⭐ (Prefix sum + HashMap pattern — must know)
> **Topic:** Prefix Sum, HashMap, Sliding Window

---

## 📋 Problem Statement

Given an array of integers and an integer K, find the length of the longest subarray whose sum equals K.

### Examples

```
Input: nums = [1,2,3,1,1,1,1], K = 3  → Output: 3  ([1,1,1])
Input: nums = [-1,1,1], K = 1          → Output: 3  ([-1,1,1])
Input: nums = [1,2,3], K = 6           → Output: 3  ([1,2,3])
```

---

## 🧩 Method 1: Prefix Sum + HashMap — O(N)

### Core Idea

Maintain a running prefix sum. Store the **first** occurrence of each prefix sum in a HashMap. If `prefixSum - K` exists in the map, the subarray from that index+1 to current index has sum K.

### Key Insight

> If `prefixSum[i] - prefixSum[j] = K`, then the subarray from `j+1` to `i` has sum K. So we look for `prefixSum - K` in the map. Store only the **first** occurrence to maximize length.

### Dry Run — `nums = [1,2,3,1,1,1,1], K = 3`

```
prefixSumIndex = {0: -1}
prefixSum = 0, maxLen = 0

i=0: prefixSum=1, look for 1-3=-2 (not found), store {0:-1, 1:0}
i=1: prefixSum=3, look for 3-3=0 (found at -1), len=1-(-1)=2, maxLen=2, store {0:-1, 1:0, 3:1}
i=2: prefixSum=6, look for 6-3=3 (found at 1), len=2-1=1, maxLen=2, store {0:-1,1:0,3:1,6:2}
i=3: prefixSum=7, look for 7-3=4 (not found), store {0:-1,1:0,3:1,6:2,7:3}
i=4: prefixSum=8, look for 8-3=5 (not found), store {0:-1,1:0,3:1,6:2,7:3,8:4}
i=5: prefixSum=9, look for 9-3=6 (found at 2), len=5-2=3, maxLen=3 ✅
i=6: prefixSum=10, look for 10-3=7 (found at 3), len=6-3=3, maxLen=3

Result: 3 ✅
```

### Code

```kotlin
fun longestSubarrayWithSumK(nums: IntArray, k: Int): Int {
    val prefixSumIndex = HashMap<Int, Int>()
    prefixSumIndex[0] = -1  // Empty prefix at index -1
    var prefixSum = 0
    var maxLen = 0

    for (i in nums.indices) {
        prefixSum += nums[i]
        if (prefixSum - k in prefixSumIndex) {
            maxLen = maxOf(maxLen, i - prefixSumIndex[prefixSum - k]!!)
        }
        if (prefixSum !in prefixSumIndex) {
            prefixSumIndex[prefixSum] = i  // Store first occurrence only
        }
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Single pass |
| **Space** | O(N) | HashMap storage |

---

## 🧩 Method 2: Sliding Window (Positive only) — O(N)

### Core Idea

Expand right pointer, shrink left when sum > K. Only works for non-negative numbers.

### Key Insight

> With non-negative numbers, adding elements only increases the sum. So we can shrink from the left when sum exceeds K. This doesn't work with negatives because shrinking might increase the sum.

### Code

```kotlin
fun longestSubarrayWithSumKPositive(nums: IntArray, k: Int): Int {
    var left = 0
    var sum = 0
    var maxLen = 0

    for (right in nums.indices) {
        sum += nums[right]
        while (sum > k && left <= right) {
            sum -= nums[left]
            left++
        }
        if (sum == k) {
            maxLen = maxOf(maxLen, right - left + 1)
        }
    }
    return maxLen
}
```

### Complexity

| Metric | Value | Reason |
|--------|-------|--------|
| **Time** | O(N) | Each element visited at most twice |
| **Space** | O(1) | Two pointers |

---

## 📊 Method Comparison

| Method | Time | Space | Negative Numbers? |
|--------|------|-------|-------------------|
| Prefix Sum + HashMap | O(N) | O(N) | ✅ Yes |
| Sliding Window | O(N) | O(1) | ❌ No (positive only) |
| Brute Force | O(N²) | O(1) | ✅ Yes |

> **Interview Tip:** Always ask: "Can the array contain negative numbers?" If yes → prefix sum + HashMap. If no → sliding window (O(1) space). The prefix sum approach is more general. Store only the **first** occurrence of each prefix sum to get the **longest** subarray.
