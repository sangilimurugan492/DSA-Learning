# 📅 Week 2 — Sliding Window + Prefix Sum + Kadane's

> **Dates:** Oct 26 (Mon) – Nov 1 (Sun)
> **Pattern Focus:** Sliding Window (fixed & variable size), Prefix Sum, Kadane's Algorithm
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/array/sliding_window/`, `DSA/array/prefix_sum/`, `DSA/array/kadane_algorithm/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week02_Practice.md](../practice/Week02_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Oct 26 | #1–#4 (Fixed window + Prefix Sum basics) | 2 hrs |
| Tue | Oct 27 | #5–#8 (Variable window + Kadane's) | 2 hrs |
| Wed | Oct 28 | #9–#13 (Prefix Sum + HashMap combos) | 2 hrs |
| Thu | Oct 29 | #14–#18 (Hard window problems) | 2 hrs |
| Sat | Oct 31 | Revision: re-solve 4 weakest + Week 1 mixed | 2 hrs |
| Sun | Nov 1 | 🧪 Mock Test 2 + Review | 3 hrs |

---

## 🟢 MONDAY, Oct 26 — Fixed Window + Prefix Sum Basics

### 1. Max Average Subarray (Fixed K) — 🟢
**LeetCode #643** | Solution: `DSA/array/sliding_window/MaximumAverageSubArray.kt`

**Problem:** Find the contiguous subarray of length k with the maximum average.

<details><summary>💡 Hint 1</summary>
Sliding Window — FIXED size k.
</details>

<details><summary>💡 Hint 2</summary>
Compute sum of first k. Then slide: add new element, subtract the one leaving the window. Track max sum.
</details>

<details><summary>💡 Hint 3</summary>
1. sum = sum of first k elements
2. For i in k until n: sum += nums[i] - nums[i-k]; maxSum = max(maxSum, sum)
3. Return maxSum / k
</details>

```kotlin
fun findMaxAverage(nums: IntArray, k: Int): Double {
    // TODO: Fixed window, slide and track max sum
    return 0.0
}
```

---

### 2. Running Sum of 1D Array — 🟢
**LeetCode #1480** | Solution: `DSA/array/prefix_sum/RunningSum1DArray.kt`

**Problem:** Return running sum: runningSum[i] = sum(nums[0..i]).

<details><summary>💡 Hint 1</summary>
Prefix Sum — build incrementally.
</details>

<details><summary>💡 Hint 2</summary>
result[i] = result[i-1] + nums[i].
</details>

<details><summary>💡 Hint 3</summary>
result[0] = nums[0]; for i in 1 until n → result[i] = result[i-1] + nums[i].
</details>

```kotlin
fun runningSum(nums: IntArray): IntArray {
    // TODO: Each element = previous running sum + current
    return intArrayOf()
}
```

---

### 3. Prefix Sum Query — 🟢
**Solution:** `DSA/array/prefix_sum/PrefixSumQuery.kt`

**Problem:** Precompute prefix sums so any range sum [l, r] can be answered in O(1).

<details><summary>💡 Hint 1</summary>
prefix[i] = sum of first i elements (prefix[0] = 0).
</details>

<details><summary>💡 Hint 2</summary>
Range sum [l, r] = prefix[r+1] - prefix[l].
</details>

<details><summary>💡 Hint 3</summary>
1. prefix = IntArray(n+1); prefix[i+1] = prefix[i] + nums[i]
2. sum(l, r) = prefix[r+1] - prefix[l]
</details>

```kotlin
class PrefixSum(nums: IntArray) {
    // TODO: Build prefix array, answer range queries in O(1)
    fun rangeSum(l: Int, r: Int): Int = 0
}
```

---

### 4. Maximum Subarray (Kadane's) — 🟡
**LeetCode #53** | Solution: `DSA/array/kadane_algorithm/MaximumSubArray.kt`

**Problem:** Find the contiguous subarray with the largest sum.

<details><summary>💡 Hint 1</summary>
Kadane's Algorithm — at each index: extend the previous subarray or start fresh?
</details>

<details><summary>💡 Hint 2</summary>
current = max(nums[i], current + nums[i]). If current + nums[i] < nums[i] alone → start fresh at nums[i]. Track global max.
</details>

<details><summary>💡 Hint 3</summary>
1. current = nums[0], best = nums[0]
2. For i in 1 until n: current = max(nums[i], current + nums[i]); best = max(best, current)
</details>

```kotlin
fun maxSubArray(nums: IntArray): Int {
    // TODO: Extend or start fresh, track global max
    return 0
}
```

---

## 🟡 TUESDAY, Oct 27 — Variable Window + Kadane's Variants

### 5. Fruits into Baskets — 🟡
**LeetCode #904** | Solution: `DSA/array/sliding_window/FruitsIntoBaskets.kt`

**Problem:** Pick fruits from trees left to right. You have 2 baskets, each holding one fruit TYPE (unlimited count). Max fruits you can pick.

<details><summary>💡 Hint 1</summary>
Sliding Window — VARIABLE size. Longest subarray with at most 2 distinct values.
</details>

<details><summary>💡 Hint 2</summary>
HashMap counts fruit types in window. When map has > 2 types → shrink from left until 2 remain.
</details>

<details><summary>💡 Hint 3</summary>
1. map = HashMap<fruitType, count>, left = 0, best = 0
2. For right in 0 until n: map[f[right]]++
3. While map.size > 2: decrement map[f[left]], remove if 0, left++
4. best = max(best, right - left + 1)
</details>

```kotlin
fun totalFruit(fruits: IntArray): Int {
    // TODO: Variable window, at most 2 distinct types
    return 0
}
```

---

### 6. Longest Subarray After Deleting One — 🟡
**LeetCode #1493** | Solution: `DSA/array/sliding_window/LongestSubArrayAfterDeletingOne.kt`

**Problem:** Return the longest subarray of 1s after deleting exactly one element (must delete one).

<details><summary>💡 Hint 1</summary>
Sliding Window — at most ONE zero in the window.
</details>

<details><summary>💡 Hint 2</summary>
Track zero count. When > 1 → shrink left. Answer = window size - 1 (because you must delete one element).
</details>

<details><summary>💡 Hint 3</summary>
1. zeros = 0, left = 0, best = 0
2. For right: if nums[right] == 0 → zeros++
3. While zeros > 1: if nums[left] == 0 → zeros--; left++
4. best = max(best, right - left) // window size - 1
</details>

```kotlin
fun longestSubarray(nums: IntArray): Int {
    // TODO: Window with at most 1 zero, answer = size - 1
    return 0
}
```

---

### 7. Grumpy Bookstore Owner — 🟡
**LeetCode #1052** | Solution: `DSA/array/sliding_window/GrumpyBookStoreOwner.kt`

**Problem:** Owner is grumpy on some minutes. He can be non-grumpy for k consecutive minutes. Maximize satisfied customers.

<details><summary>💡 Hint 1</summary>
Base = customers satisfied when NOT grumpy. Then find the k-minute window that adds the MOST (grumpy minutes' customers).
</details>

<details><summary>💡 Hint 2</summary>
Fixed window of size k over the grumpy-minutes' customer counts. Maximize the extra gain.
</details>

<details><summary>💡 Hint 3</summary>
1. base = sum of customers[i] where grumpy[i] == 0
2. Fixed-size-k sliding window over gain[i] = grumpy[i] * customers[i]
3. Answer = base + maxWindowGain
</details>

```kotlin
fun maxSatisfied(customers: IntArray, grumpy: IntArray, minutes: Int): Int {
    // TODO: Base + best k-window of grumpy gains
    return 0
}
```

---

### 8. Maximum Product Subarray — 🟡
**LeetCode #152** | Solution: `DSA/array/kadane_algorithm/MaximumProductSubarray.kt`

**Problem:** Find the contiguous subarray with the largest product.

<details><summary>💡 Hint 1</summary>
Kadane's variant — track BOTH max and min products ending at i.
</details>

<details><summary>💡 Hint 2</summary>
A negative number flips max ↔ min. So track maxHere and minHere; a negative num makes maxHere = minHere × num possibly best.
</details>

<details><summary>💡 Hint 3</summary>
1. maxHere = minHere = result = nums[0]
2. For i in 1 until n: candidates = {nums[i], maxHere × nums[i], minHere × nums[i]}
3. maxHere = max(candidates), minHere = min(candidates); result = max(result, maxHere)
</details>

```kotlin
fun maxProduct(nums: IntArray): Int {
    // TODO: Track max AND min products (negatives flip them)
    return 0
}
```

---

## 🟡 WEDNESDAY, Oct 28 — Prefix Sum + HashMap Combos

### 9. Maximum Sum Circular Subarray — 🟡
**LeetCode #918** | Solution: `DSA/array/kadane_algorithm/MaximumSumCircularSubarray.kt`

**Problem:** Find max subarray sum in a CIRCULAR array (can wrap around).

<details><summary>💡 Hint 1</summary>
Two cases: (a) normal Kadane's max, (b) wrapping case = total sum - MIN subarray sum.
</details>

<details><summary>💡 Hint 2</summary>
Run Kadane's twice: once for max subarray, once for min subarray. Wrapping max = total - minSum.
</details>

<details><summary>💡 Hint 3</summary>
1. maxKadane = standard Kadane's
2. minKadane = Kadane's for minimum (flip comparisons)
3. If maxKadane < 0 → return maxKadane (all negative)
4. Return max(maxKadane, total - minKadane)
</details>

```kotlin
fun maxSubarraySumCircular(nums: IntArray): Int {
    // TODO: Max of (Kadane max, total - Kadane min)
    return 0
}
```

---

### 10. Product of Array Except Self — 🟡
**LeetCode #238** | Solution: `DSA/array/prefix_sum/ProductOfArrayExceptSelf.kt`

**Problem:** Return array where result[i] = product of all elements except nums[i]. No division. O(1) extra space (excluding output).

<details><summary>💡 Hint 1</summary>
Prefix product from the LEFT + suffix product from the RIGHT.
</details>

<details><summary>💡 Hint 2</summary>
result[i] = (product of everything left of i) × (product of everything right of i). Do two passes.
</details>

<details><summary>💡 Hint 3</summary>
1. Pass 1: result[i] = product of nums[0..i-1] (running left product)
2. Pass 2 (right to left): result[i] ×= rightProduct; rightProduct ×= nums[i]
</details>

```kotlin
fun productExceptSelf(nums: IntArray): IntArray {
    // TODO: Left products pass, then right products pass
    return intArrayOf()
}
```

---

### 11. Subarray Sum Equals K — 🟡
**LeetCode #560** | Solution: `DSA/array/prefix_sum/SubarraySumEqualsK.kt`

**Problem:** Count subarrays that sum to exactly k.

<details><summary>💡 Hint 1</summary>
Prefix Sum + HashMap: store counts of prefix sums seen.
</details>

<details><summary>💡 Hint 2</summary>
If prefixSum[j] - prefixSum[i] == k, then subarray (i, j] sums to k. So for each j, look up how many earlier prefix sums equal currentSum - k.
</details>

<details><summary>💡 Hint 3</summary>
1. map = {0: 1}, sum = 0, count = 0
2. For each num: sum += num; count += map[sum - k] (default 0); map[sum]++
</details>

```kotlin
fun subarraySum(nums: IntArray, k: Int): Int {
    // TODO: Prefix sum counts in HashMap, lookup sum - k
    return 0
}
```

---

### 12. Contiguous Array (Equal 0s & 1s) — 🟡
**LeetCode #525** | Solution: `DSA/array/prefix_sum/ContiguousArray.kt`

**Problem:** Find the longest contiguous subarray with equal number of 0s and 1s.

<details><summary>💡 Hint 1</summary>
Treat 0 as -1. Then find longest subarray with sum 0.
</details>

<details><summary>💡 Hint 2</summary>
Prefix sum + HashMap storing FIRST index where each sum occurred. Same sum seen again → zero-sum subarray between them.
</details>

<details><summary>💡 Hint 3</summary>
1. map = {0: -1}, sum = 0, best = 0
2. For i: sum += (num == 1 ? 1 : -1)
3. If map contains sum → best = max(best, i - map[sum]); else map[sum] = i
</details>

```kotlin
fun findMaxLength(nums: IntArray): Int {
    // TODO: 0→-1, first-index map, longest zero-sum subarray
    return 0
}
```

---

### 13. Longest Subarray with Sum K — 🟡
**Solution:** `DSA/array/prefix_sum/LongestSubarrayWithSumK.kt`

**Problem:** Find the length of the longest subarray summing to k (may contain negatives).

<details><summary>💡 Hint 1</summary>
Prefix sum + HashMap of first occurrence (same as Contiguous Array idea).
</details>

<details><summary>💡 Hint 2</summary>
For each index: if (sum - k) seen before → length = i - firstIndexOf(sum - k). Store only the FIRST occurrence.
</details>

<details><summary>💡 Hint 3</summary>
1. map = {0: -1}, sum = 0, best = 0
2. For i: sum += nums[i]
3. If map has sum - k → best = max(best, i - map[sum-k])
4. If sum not in map → map[sum] = i
</details>

```kotlin
fun longestSubarrayWithSumK(nums: IntArray, k: Int): Int {
    // TODO: First-occurrence prefix sum map
    return 0
}
```

---

## 🟡 THURSDAY, Oct 29 — Hard Window Problems

### 14. Longest Substring Without Repeating Characters — 🟡
**LeetCode #3** | Solution: `DSA/array/sliding_window/LongestSubstringWithoutRepeatingCharacters.kt`

**Problem:** Find the length of the longest substring without repeating characters.

<details><summary>💡 Hint 1</summary>
Sliding Window — VARIABLE size with HashSet/HashMap of chars in window.
</details>

<details><summary>💡 Hint 2</summary>
When a duplicate enters → shrink left until the duplicate is removed. Store last index of each char for O(1) jumps.
</details>

<details><summary>💡 Hint 3</summary>
1. lastSeen = HashMap<Char, Int>, left = 0, best = 0
2. For right: if lastSeen has char && lastSeen[char] >= left → left = lastSeen[char] + 1
3. lastSeen[char] = right; best = max(best, right - left + 1)
</details>

```kotlin
fun lengthOfLongestSubstring(s: String): Int {
    // TODO: Variable window, jump left past duplicates
    return 0
}
```

---

### 15. Longest Repeating Character Replacement — 🟡
**LeetCode #424** | Solution: `DSA/array/sliding_window/LongestRepeatingCharacterReplacement.kt`

**Problem:** You may replace at most k chars. Find the longest substring of a single repeating letter.

<details><summary>💡 Hint 1</summary>
Sliding Window — valid while (window size - count of most frequent char) <= k.
</details>

<details><summary>💡 Hint 2</summary>
Track counts of each char and max frequency in window. If windowSize - maxFreq > k → shrink left.
</details>

<details><summary>💡 Hint 3</summary>
1. counts = IntArray(26), left = 0, maxFreq = 0, best = 0
2. For right: counts[c]++; maxFreq = max(maxFreq, counts[c])
3. While (right - left + 1) - maxFreq > k → counts[left char]--, left++
4. best = max(best, right - left + 1)
</details>

```kotlin
fun characterReplacement(s: String, k: Int): Int {
    // TODO: Window valid if size - maxFreq <= k
    return 0
}
```

---

### 16. Min Size Subarray Sum — 🟡
**LeetCode #209** | Solution: `DSA/array/two_pointer/MinimumSizeSubArraySum.kt`

**Problem:** Find the minimal length of a subarray with sum >= target (positive numbers).

<details><summary>💡 Hint 1</summary>
Sliding Window — shrink from left while sum >= target.
</details>

<details><summary>💡 Hint 2</summary>
Expand right adding to sum. While sum >= target → record length, subtract nums[left], left++.
</details>

<details><summary>💡 Hint 3</summary>
1. left = 0, sum = 0, best = MAX
2. For right: sum += nums[right]
3. While sum >= target: best = min(best, right - left + 1); sum -= nums[left]; left++
</details>

```kotlin
fun minSubArrayLen(target: Int, nums: IntArray): Int {
    // TODO: Expand right, shrink left while sum >= target
    return 0
}
```

---

### 17. Find All Anagrams in String — 🟡
**LeetCode #438** | Solution: `DSA/array/sliding_window/FindAllAnagramsInString.kt`

**Problem:** Find all start indices of p's anagrams in s.

<details><summary>💡 Hint 1</summary>
Fixed-size window of len(p) with frequency comparison.
</details>

<details><summary>💡 Hint 2</summary>
Maintain count arrays for window and p. Slide: add new char, remove old char. Compare counts (or track a "matches" counter for O(1) compare).
</details>

<details><summary>💡 Hint 3</summary>
1. Build pCount[26] and windowCount[26] for first window
2. Slide: windowCount[s[right]]++, windowCount[s[right - len(p)]]--
3. If windowCount == pCount → record start index
</details>

```kotlin
fun findAnagrams(s: String, p: String): List<Int> {
    // TODO: Fixed window, frequency arrays
    return listOf()
}
```

---

### 18. Minimum Window Substring — 🔴
**LeetCode #76** | Solution: `DSA/array/sliding_window/MinimumWindowSubstring.kt`

**Problem:** Find the smallest substring of s containing all characters of t (with multiplicity).

<details><summary>💡 Hint 1</summary>
Variable Sliding Window + need/have counters.
</details>

<details><summary>💡 Hint 2</summary>
Expand right until window has all needed chars (have == need). Then shrink left while still valid, tracking the smallest window.
</details>

<details><summary>💡 Hint 3</summary>
1. need = counts of t chars; have = 0; required = distinct chars in t
2. For right: add s[right] to window; if its count matches need → have++
3. While have == required: update best; remove s[left]; if count drops below need → have--; left++
</details>

```kotlin
fun minWindow(s: String, t: String): String {
    // TODO: Expand until valid, shrink while valid, track smallest
    return ""
}
```

---

## 🟢 SATURDAY, Oct 31 — Revision + Mixed Practice

1. Re-solve the 3–4 problems you found hardest this week (from scratch, no hints)
2. Mixed practice — 3 problems from Week 1 (Two Pointer):
   - Three Sum (re-solve)
   - Trapping Rain Water (re-solve)
   - Next Permutation (re-solve)
3. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## 🧪 SUNDAY, Nov 1 — Mock Test 2 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Max Average Subarray | Fixed Window | 🟢 | 15 min |
| 2 | Subarray Sum Equals K | Prefix + HashMap | 🟡 | 25 min |
| 3 | Longest Substring Without Repeating | Variable Window | 🟡 | 25 min |
| 4 | Maximum Subarray | Kadane's | 🟡 | 20 min |
| 5 | Minimum Window Substring | Hard Window | 🔴 | 35 min |

**Part 2 — Review (1 hr):** Score, log in [Mock_Test_Log.md](../Mock_Test_Log.md), re-solve failures, update tracker.

---

## ✅ Week 2 Completion Checklist

- [ ] Mon: 4 problems (fixed window + prefix basics)
- [ ] Tue: 4 problems (variable window + Kadane's)
- [ ] Wed: 5 problems (prefix + HashMap combos)
- [ ] Thu: 5 problems (hard windows)
- [ ] Sat: Revision + Week 1 mixed practice
- [ ] Sun: Mock Test 2 completed & logged
- [ ] Progress Tracker updated
