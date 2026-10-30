# 📅 Week 2 — Sliding Window + Prefix Sum + Kadane's

> **Dates:** Oct 26 (Mon) – Nov 1 (Sun)
> **Pattern Focus:** Sliding Window (fixed & variable size), Prefix Sum, Kadane's Algorithm
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/array/sliding_window/`, `DSA/array/prefix_sum/`, `DSA/array/kadane_algorithm/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week02_Practice.md](../practice/Week02_Practice.md) — sample test cases for every problem + 13 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Oct 26 | #1–#4 (Fixed window + Prefix Sum basics) | 2 hrs |
| Tue | Oct 27 | #5–#8 (Variable window + Kadane's) | 2 hrs |
| Wed | Oct 28 | #9–#13 (Prefix Sum + HashMap combos) | 2 hrs |
| Thu | Oct 29 | #14–#18 (Hard window problems) | 2 hrs |
| Fri | Oct 30 | #19–#30 (Bonus: window / prefix / Kadane extension) | 2 hrs |
| Sat | Oct 31 | Revision: re-solve 4 weakest + Week 1 mixed | 2 hrs |
| Sun | Nov 1 | 🧪 Mock Test 2 + Review | 3 hrs |

---

## 🟢 MONDAY, Oct 26 — Fixed Window + Prefix Sum Basics

### 1. Max Average Subarray (Fixed K) — 🟢
**LeetCode #643** | Solution: `DSA/array/sliding_window/maximum_average_sub_array/MaximumAverageSubArray.kt`

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
**LeetCode #1480** | Solution: `DSA/array/prefix_sum/running_sum1_d_array/RunningSum1DArray.kt`

**Problem:** Return running sum: runningSum[i] = sum(nums[0 until i]).

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
**Solution:** `DSA/array/prefix_sum/prefix_sum_query/PrefixSumQuery.kt`

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
**LeetCode #53** | Solution: `DSA/array/kadane_algorithm/maximum_subarray/MaximumSubArray.kt`

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
**LeetCode #904** | Solution: `DSA/array/sliding_window/fruits_into_baskets/FruitsIntoBaskets.kt`

**Problem:** Pick fruits from trees left to right. You have 2 baskets, each holding one fruit TYPE (unlimited count). Max fruits you can pick.

<details><summary>💡 Hint 1</summary>
Sliding Window — VARIABLE size. Longest subarray with at most 2 distinct values.
</details>

<details><summary>💡 Hint 2</summary>
HashMap counts fruit types in window. When map has > 2 types → shrink from left until 2 remain.
</details>

<details><summary>💡 Hint 3</summary>
1. map = HashMap&lt;fruitType, count&gt;, left = 0, best = 0
2. For right in 0 until n: map[f[right]]++
3. While map has more than 2 fruit types: decrement map[f[left]], remove if 0, left++
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
**LeetCode #1493** | Solution: `DSA/array/sliding_window/longest_sub_array_after_deleting_one/LongestSubArrayAfterDeletingOne.kt`

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
**LeetCode #1052** | Solution: `DSA/array/sliding_window/grumpy_book_store_owner/GrumpyBookStoreOwner.kt`

**Problem:** Owner is grumpy on some minutes. He can be non-grumpy for k consecutive minutes. Maximize satisfied customers.

<details><summary>💡 Hint 1</summary>
Base = customers satisfied when NOT grumpy. Then find the k-minute window that adds the MOST (grumpy minutes' customers).
</details>

<details><summary>💡 Hint 2</summary>
Fixed window of size k over the customer counts for the grumpy minutes. Maximize the extra gain.
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
**LeetCode #152** | Solution: `DSA/array/kadane_algorithm/maximum_product_subarray/MaximumProductSubarray.kt`

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
**LeetCode #918** | Solution: `DSA/array/kadane_algorithm/maximum_sum_circular_subarray/MaximumSumCircularSubarray.kt`

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
**LeetCode #238** | Solution: `DSA/array/prefix_sum/product_of_array_except_self/ProductOfArrayExceptSelf.kt`

**Problem:** Return array where result[i] = product of all elements except nums[i]. No division. O(1) extra space (excluding output).

<details><summary>💡 Hint 1</summary>
Prefix product from the LEFT + suffix product from the RIGHT.
</details>

<details><summary>💡 Hint 2</summary>
result[i] = (product of everything left of i) × (product of everything right of i). Do two passes.
</details>

<details><summary>💡 Hint 3</summary>
1. Pass 1: result[i] = product of nums[0 until i] (running left product)
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
**LeetCode #560** | Solution: `DSA/array/prefix_sum/subarray_sum_equals_k/SubarraySumEqualsK.kt`

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
**LeetCode #525** | Solution: `DSA/array/prefix_sum/contiguous_array/ContiguousArray.kt`

**Problem:** Find the longest contiguous subarray with equal number of 0s and 1s.

<details><summary>💡 Hint 1</summary>
Treat 0 as -1. Then find the longest subarray with sum 0.
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
**Solution:** `DSA/array/prefix_sum/longest_subarray_with_sum_k/LongestSubarrayWithSumK.kt`

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
**LeetCode #3** | Solution: `DSA/array/sliding_window/longest_substring_without_repeating_characters/LongestSubstringWithoutRepeatingCharacters.kt`

**Problem:** Find the length of the longest substring without repeating characters.

<details><summary>💡 Hint 1</summary>
Sliding Window — VARIABLE size with HashSet/HashMap of chars in window.
</details>

<details><summary>💡 Hint 2</summary>
When a duplicate enters → shrink left until the duplicate is removed. Store last index of each char for O(1) jumps.
</details>

<details><summary>💡 Hint 3</summary>
1. lastSeen = HashMap&lt;Char, Int&gt;, left = 0, best = 0
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
**LeetCode #424** | Solution: `DSA/array/sliding_window/longest_repeating_character_replacement/LongestRepeatingCharacterReplacement.kt`

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
**LeetCode #209** | Solution: `DSA/array/sliding_window/minimum_size_subarray_sum/MinimumSizeSubarraySum.kt`

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
**LeetCode #438** | Solution: `DSA/array/sliding_window/find_all_anagrams_in_string/FindAllAnagramsInString.kt`

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
**LeetCode #76** | Solution: `DSA/array/sliding_window/minimum_window_substring/MinimumWindowSubstring.kt`

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

### 19. Max Consecutive Ones III — 🟡
**LeetCode #1004** | Solution: `DSA/array/sliding_window/max_consecutive_ones_iii/MaxConsecutiveOnesIII.kt`

**Problem:** Return the maximum number of consecutive 1s in the array if you can flip at most k zeros.

<details><summary>💡 Hint 1</summary>
Variable Sliding Window — the window may contain AT MOST k zeros.
</details>

<details><summary>💡 Hint 2</summary>
Expand right, counting zeros in the window. While zeroCount > k, shrink from the left.
</details>

<details><summary>💡 Hint 3</summary>
1. zeros = 0; left = 0
2. For right: if nums[right] == 0 → zeros++
3. While zeros > k: if nums[left] == 0 → zeros--; left++
4. best = max(best, right - left + 1)
</details>

```kotlin
fun longestOnes(nums: IntArray, k: Int): Int {
    // TODO: Variable window — track zero count, shrink when zeros > k
    return 0
}
```

---

### 20. Permutation in String — 🟡
**LeetCode #567** | Solution: `DSA/array/sliding_window/permutation_in_string/PermutationInString.kt`

**Problem:** Return true if s2 contains a contiguous substring that is a permutation of s1.

<details><summary>💡 Hint 1</summary>
Fixed-size window of length s1.length with frequency comparison (same idea as Find All Anagrams).
</details>

<details><summary>💡 Hint 2</summary>
Build count arrays for s1 and the first window of s2. Slide: add the new char, remove the leaving char.
</details>

<details><summary>💡 Hint 3</summary>
1. need[26] from s1; win[26] from first window of s2
2. For i in s1.length until s2.length: if win == need → true; win[s2[i]]++; win[s2[i - s1.length]]--
3. Return win == need
</details>

```kotlin
fun checkInclusion(s1: String, s2: String): Boolean {
    // TODO: Fixed window of len(s1), compare frequency arrays
    return false
}
```

---

### 21. Best Sightseeing Pair — 🟡
**LeetCode #1014** | Solution: `DSA/array/kadane_algorithm/best_sightseeing_pair/BestSightseeingPair.kt`

**Problem:** Find the maximum of values[i] + values[j] + i − j over all pairs i < j.

<details><summary>💡 Hint 1</summary>
Rearrange the score: (values[i] + i) + (values[j] − j). The two parts can be maximized independently.
</details>

<details><summary>💡 Hint 2</summary>
Scan j left to right, maintaining bestLeft = max(values[i] + i) seen so far. Score for j = bestLeft + values[j] − j.
</details>

<details><summary>💡 Hint 3</summary>
1. bestLeft = values[0] + 0; ans = 0
2. For j in 1 until n: ans = max(ans, bestLeft + values[j] − j); bestLeft = max(bestLeft, values[j] + j)
3. Return ans — single pass, O(1) space (Kadane-style prefix tracking)
</details>

```kotlin
fun maxScoreSightseeingPair(values: IntArray): Int {
    // TODO: Track best (values[i] + i) so far in one pass
    return 0
}
```

---

### 22. Binary Subarrays With Sum — 🟡
**LeetCode #930** | Solution: `DSA/array/sliding_window/binary_subarrays_with_sum/BinarySubarraysWithSum.kt`

**Problem:** Count the number of non-empty subarrays whose sum equals `goal` (binary array).

<details><summary>💡 Hint 1</summary>
This is a COUNT, not a length — same shape as Subarray Sum Equals K (#11): prefix sum + frequency map.
</details>

<details><summary>💡 Hint 2</summary>
A subarray (i, j] sums to goal when prefix[j] − prefix[i] == goal. So count how many earlier prefixes equal prefix − goal.
Alternative window form: atMost(goal) − atMost(goal − 1).
</details>

<details><summary>💡 Hint 3</summary>
1. freq = HashMap; freq[0] = 1; prefix = 0; ans = 0
2. For num: prefix += num; ans += freq[prefix - goal] (0 if absent); freq[prefix]++
3. Return ans
</details>

```kotlin
fun numSubarraysWithSum(nums: IntArray, goal: Int): Int {
    // TODO: Prefix sum + frequency map (or atMost(goal) − atMost(goal-1))
    return 0
}
```

---

### 23. Maximum Points You Can Obtain from Cards — 🟡
**LeetCode #1423** | Solution: `DSA/array/sliding_window/maximum_points_from_cards/MaximumPointsYouCanObtainFromCards.kt`

**Problem:** Take exactly k cards from either end of the row; maximise the total points taken.

<details><summary>💡 Hint 1</summary>
Instead of maximising what you TAKE from the two ends, minimise what you LEAVE — the leftover is one contiguous block of n − k cards.
</details>

<details><summary>💡 Hint 2</summary>
Answer = totalSum − minimum fixed-window sum of size (n − k). That is a plain fixed-size sliding window.
</details>

<details><summary>💡 Hint 3</summary>
1. windowSize = n - k; sum = sum of first windowSize cards; minMid = sum
2. Slide the window across the array, tracking minMid
3. Return total - minMid (edge case: k == n → return total)
</details>

```kotlin
fun maxScore(cardPoints: IntArray, k: Int): Int {
    // TODO: total − min sum of a contiguous window of size n − k
    return 0
}
```

---

### 24. Maximum Erasure Value — 🟡
**LeetCode #1695** | Solution: `DSA/array/sliding_window/maximum_erasure_value/MaximumErasureValue.kt`

**Problem:** Return the maximum sum of a subarray containing only distinct elements.

<details><summary>💡 Hint 1</summary>
Variable sliding window with an all-distinct invariant — but here you maximise the window SUM, not its length.
</details>

<details><summary>💡 Hint 2</summary>
Keep a HashSet of the window plus a running windowSum. When a duplicate arrives, shrink from the left (subtracting elements) until the duplicate is gone.
</details>

<details><summary>💡 Hint 3</summary>
1. left = 0; sum = 0; best = 0; seen = HashSet
2. For right: while nums[right] in seen → sum -= nums[left]; seen.remove(nums[left]); left++
3. seen.add(nums[right]); sum += nums[right]; best = max(best, sum)
4. Return best
</details>

```kotlin
fun maximumErasureValue(nums: IntArray): Int {
    // TODO: Distinct-element window — track the window SUM
    return 0
}
```

---

### 25. Sliding Window Maximum — 🔴
**LeetCode #239** | Solution: `DSA/array/sliding_window/sliding_window_maximum/SlidingWindowMaximum.kt`

**Problem:** For every window of size k moving left to right, return the maximum value in that window.

<details><summary>💡 Hint 1</summary>
Re-scanning each window costs O(N·k). Keep only the elements that could still become a future window's maximum — every smaller element behind a newer bigger one is useless.
</details>

<details><summary>💡 Hint 2</summary>
Monotonic DECREASING deque of INDICES. The front is always the current window's maximum; the back is where new candidates enter.
</details>

<details><summary>💡 Hint 3</summary>
1. deque of indices
2. For i: while deque not empty && nums[deque.last] <= nums[i] → remove last; add i
3. Remove the front while deque.first <= i - k (fell out of the window)
4. Once i >= k - 1 → result[i - k + 1] = nums[deque.first]
</details>

```kotlin
fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
    // TODO: Monotonic decreasing deque of indices — front = window max
    return intArrayOf()
}
```

---

### 26. Find Pivot Index — 🟢
**LeetCode #724** | Solution: `DSA/array/prefix_sum/find_pivot_index/FindPivotIndex.kt`

**Problem:** Return the leftmost index where the sum of elements strictly to the left equals the sum strictly to the right (−1 if none).

<details><summary>💡 Hint 1</summary>
Prefix Sum — compare a running LEFT sum against the total instead of recomputing both sides.
</details>

<details><summary>💡 Hint 2</summary>
rightSum(i) = total − leftSum − nums[i]. Pivot when leftSum == rightSum(i).
</details>

<details><summary>💡 Hint 3</summary>
1. total = nums.sum(); left = 0
2. For i: if (left == total - left - nums[i]) return i; left += nums[i]
3. Return -1 — O(N) time, O(1) space
</details>

```kotlin
fun pivotIndex(nums: IntArray): Int {
    // TODO: total vs running left sum — O(1) extra space
    return -1
}
```

---

### 27. Range Sum Query 2D — Immutable — 🟡
**LeetCode #304** | Solution: `DSA/array/prefix_sum/range_sum_query_2d/NumMatrix.kt`

**Problem:** Preprocess a matrix so the sum of any sub-rectangle (row1, col1)–(row2, col2) can be answered in O(1).

<details><summary>💡 Hint 1</summary>
2D Prefix Sum — prefix[i][j] = sum of the rectangle (0,0)…(i−1, j−1), with a zero row/column as padding.
</details>

<details><summary>💡 Hint 2</summary>
Build with inclusion–exclusion: prefix[i+1][j+1] = m[i][j] + prefix[i][j+1] + prefix[i+1][j] − prefix[i][j]. The query uses the same inclusion–exclusion on four corners.
</details>

<details><summary>💡 Hint 3</summary>
1. prefix = (m+1) × (n+1) array
2. Build as above (one pass over the matrix)
3. sumRegion(r1, c1, r2, c2) = prefix[r2+1][c2+1] − prefix[r1][c2+1] − prefix[r2+1][c1] + prefix[r1][c1]
</details>

```kotlin
class NumMatrix(matrix: Array<IntArray>) {
    // TODO: Build the 2D prefix matrix; answer each query in O(1)
    fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int = 0
}
```

---

### 28. Continuous Subarray Sum — 🟡
**LeetCode #523** | Solution: `DSA/array/prefix_sum/continuous_subarray_sum/ContinuousSubarraySum.kt`

**Problem:** Return true if the array has a subarray of length ≥ 2 whose sum is a multiple of k.

<details><summary>💡 Hint 1</summary>
Prefix sums modulo k — a subarray (i, j] is a multiple of k exactly when prefix[j] % k == prefix[i] % k.
</details>

<details><summary>💡 Hint 2</summary>
Store the FIRST index at which each remainder appeared. If the same remainder shows up again at j and j − firstIndex ≥ 2 → true. Never overwrite an earlier index.
</details>

<details><summary>💡 Hint 3</summary>
1. first = HashMap; first[0] = -1; prefixMod = 0
2. For j: prefixMod = ((prefixMod + nums[j]) % k + k) % k (keeps it non-negative)
3. If remainder seen at i → return true when j - i >= 2; otherwise if not seen → first[remainder] = j
4. Return false
</details>

```kotlin
fun checkSubarraySum(nums: IntArray, k: Int): Boolean {
    // TODO: prefix sum mod k + first-occurrence index map
    return false
}
```

---

### 29. Maximum Absolute Sum of Any Subarray — 🟡
**LeetCode #1749** | Solution: `DSA/array/kadane_algorithm/maximum_absolute_sum/MaximumAbsoluteSumOfAnySubarray.kt`

**Problem:** Return the maximum absolute value of the sum of any (possibly empty) subarray.

<details><summary>💡 Hint 1</summary>
|sum| is maximised either by the LARGEST positive subarray sum or by the MOST NEGATIVE one — so run Kadane's twice.
</details>

<details><summary>💡 Hint 2</summary>
Track `maxEnding` (classic Kadane) and `minEnding` (Kadane on the mirrored array) in the SAME pass; answer = max(maxEnding seen, −minEnding seen).
</details>

<details><summary>💡 Hint 3</summary>
1. maxEnd = 0; minEnd = 0; best = 0
2. For x: maxEnd = max(x, maxEnd + x); minEnd = min(x, minEnd + x)
3. best = max(best, max(maxEnd, -minEnd))
4. Return best — O(N), O(1) space
</details>

```kotlin
fun maxAbsoluteSum(nums: IntArray): Int {
    // TODO: Kadane for max AND min in one pass
    return 0
}
```

---

### 30. Maximum Length of Subarray With Positive Product — 🟡
**LeetCode #1567** | Solution: `DSA/array/kadane_algorithm/max_length_positive_product/MaximumLengthOfSubarrayWithPositiveProduct.kt`

**Problem:** Return the maximum length of a subarray whose product is positive (zeros break every subarray).

<details><summary>💡 Hint 1</summary>
Only the SIGN of each element matters. A zero-free segment has a positive product iff it contains an even number of negatives.
</details>

<details><summary>💡 Hint 2</summary>
Kadane-style state tracking: keep the longest subarray ending here with POSITIVE product (`pos`) and with NEGATIVE product (`neg`); a zero resets both to 0.
</details>

<details><summary>💡 Hint 3</summary>
1. pos = 0; neg = 0; best = 0
2. For x: if x == 0 → pos = 0; neg = 0
   if x > 0 → pos++; neg = if (neg > 0) neg + 1 else 0
   if x < 0 → newPos = if (neg > 0) neg + 1 else 0; neg = pos + 1; pos = newPos
3. best = max(best, pos) after each step; return best
</details>

```kotlin
fun getMaxLen(nums: IntArray): Int {
    // TODO: Kadane-style sign tracking (pos/neg lengths since last zero)
    return 0
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
- [ ] Fri: bonus problems #19–#30 (12 available — do at least 6 if short on time)
- [ ] Sat: Revision + Week 1 mixed practice
- [ ] Sun: Mock Test 2 completed & logged
- [ ] Progress Tracker updated
