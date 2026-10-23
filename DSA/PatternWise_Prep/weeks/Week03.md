# 📅 Week 3 — HashMap/HashSet + Complement + Set Ops + Frequency + Stack

> **Dates:** Nov 2 (Mon) – Nov 8 (Sun)
> **Pattern Focus:** HashSet/HashMap lookups, complement search, set operations, frequency counting, Stack (incl. monotonic stack)
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/array/hashset_lookup/`, `DSA/array/complement_search/`, `DSA/array/set_operations/`, `DSA/array/frequency_count/`, `DSA/stack/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week03_Practice.md](../practice/Week03_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Nov 2 | #1–#8 (Easy HashSet/Set Ops warm-up) | 2 hrs |
| Tue | Nov 3 | #9–#16 (Medium HashMap + Frequency) | 2 hrs |
| Wed | Nov 4 | #17–#22 (Stack basics + monotonic) | 2 hrs |
| Thu | Nov 5 | #23–#26 (Hard stack) | 2 hrs |
| Sat | Nov 7 | Revision: weakest 4 + Week 1–2 mixed | 2 hrs |
| Sun | Nov 8 | 🧪 Mock Test 3 + Review | 3 hrs |

---

## 🟢 MONDAY, Nov 2 — Easy HashSet / Set Operations Warm-up

### 1. Contains Duplicates — 🟢
**LeetCode #217** | Solution: `DSA/array/hashset_lookup/ContainsDuplicates.kt`

**Problem:** Return true if any value appears at least twice.

<details><summary>💡 Hint 1</summary>
HashSet — "have I seen this before?"
</details>

<details><summary>💡 Hint 2</summary>
Add each element to a set; if add() returns false (already present) → true.
</details>

<details><summary>💡 Hint 3</summary>
val seen = HashSet<Int>(); for num: if (!seen.add(num)) return true; return false.
</details>

```kotlin
fun containsDuplicate(nums: IntArray): Boolean {
    // TODO: HashSet add check
    return false
}
```

---

### 2. Check if N and 2N Exist — 🟢
**LeetCode #1346** | Solution: `DSA/array/hashset_lookup/CheckIfNDoubleExits.kt`

**Problem:** Return true if there exist indices i ≠ j with arr[i] == 2 × arr[j].

<details><summary>💡 Hint 1</summary>
HashSet — for each num, check if 2×num OR num/2 (if even) is in the set.
</details>

<details><summary>💡 Hint 2</summary>
Careful with 0: 2×0 == 0, so 0 needs TWO occurrences. Handle separately or count.
</details>

<details><summary>💡 Hint 3</summary>
1. seen = HashSet
2. For num: if seen contains 2*num or (num % 2 == 0 && seen contains num/2) → true
3. Add num to seen
</details>

```kotlin
fun checkIfExist(arr: IntArray): Boolean {
    // TODO: Check 2x and half in set
    return false
}
```

---

### 3. Count Distinct Elements — 🟢
**Solution:** `DSA/array/hashset_lookup/CountDistinctElements.kt`

**Problem:** Count distinct elements in the array.

<details><summary>💡 Hint 1</summary>
Add all to a HashSet; return its size.
</details>

<details><summary>💡 Hint 2</summary>
nums.toSet().size
</details>

<details><summary>💡 Hint 3</summary>
return nums.toSet().size
</details>

```kotlin
fun countDistinct(nums: IntArray): Int {
    // TODO: Set size
    return 0
}
```

---

### 4. Valid Anagrams — 🟢
**LeetCode #242** | Solution: `DSA/array/hashset_lookup/ValidAnagrams.kt`

**Problem:** Return true if t is an anagram of s (same chars, same counts).

<details><summary>💡 Hint 1</summary>
Frequency count — IntArray(26) or HashMap.
</details>

<details><summary>💡 Hint 2</summary>
Increment for s chars, decrement for t chars. All zeros at end → anagram. (Check lengths first.)
</details>

<details><summary>💡 Hint 3</summary>
1. If lengths differ → false
2. counts = IntArray(26)
3. For i: counts[s[i]]++, counts[t[i]]--
4. Return counts.all { it == 0 }
</details>

```kotlin
fun isAnagram(s: String, t: String): Boolean {
    // TODO: Count up for s, down for t
    return false
}
```

---

### 5. Intersection of Two Arrays — 🟢
**LeetCode #349** | Solution: `DSA/array/set_operations/IntersectionOfTwoArrays.kt`

**Problem:** Return unique intersection of two arrays.

<details><summary>💡 Hint 1</summary>
Set intersection.
</details>

<details><summary>💡 Hint 2</summary>
nums1.toSet().intersect(nums2.toSet()) — or iterate one set checking the other.
</details>

<details><summary>💡 Hint 3</summary>
return nums1.toSet().intersect(nums2.toSet()).toIntArray()
</details>

```kotlin
fun intersection(nums1: IntArray, nums2: IntArray): IntArray {
    // TODO: Set intersect
    return intArrayOf()
}
```

---

### 6. Find Difference Between Two Arrays — 🟢
**LeetCode #2215** | Solution: `DSA/array/set_operations/FindTheDifferenceBetweenTwoArray.kt`

**Problem:** Return [nums1-only elements, nums2-only elements] (unique).

<details><summary>💡 Hint 1</summary>
Set difference both ways.
</details>

<details><summary>💡 Hint 2</summary>
set1 - set2 and set2 - set1.
</details>

<details><summary>💡 Hint 3</summary>
1. set1 = nums1.toSet(), set2 = nums2.toSet()
2. answer[0] = (set1 - set2).toList(); answer[1] = (set2 - set1).toList()
</details>

```kotlin
fun findDifference(nums1: IntArray, nums2: IntArray): List<List<Int>> {
    // TODO: Set difference both directions
    return listOf()
}
```

---

### 7. Two Sum (Unsorted) — 🟢
**LeetCode #1** | Solution: `DSA/array/complement_search/TwoSumLevelOneWithoutSortedArray.kt`

**Problem:** Return indices of two numbers summing to target (unsorted array).

<details><summary>💡 Hint 1</summary>
Complement Search — HashMap value → index.
</details>

<details><summary>💡 Hint 2</summary>
For each num, complement = target - num. If complement in map → done. Else store num → index.
</details>

<details><summary>💡 Hint 3</summary>
1. map = HashMap<Int, Int>()
2. For (i, num): if map has target - num → return [map[comp], i]; map[num] = i
</details>

```kotlin
fun twoSum(nums: IntArray, target: Int): IntArray {
    // TODO: HashMap complement lookup
    return intArrayOf()
}
```

---

### 8. Valid Parentheses — 🟢
**LeetCode #20** | Solution: `DSA/stack/ValidParentheses.kt`

**Problem:** Given a string of ()[]{} — is it valid (properly matched & nested)?

<details><summary>💡 Hint 1</summary>
Stack — push openers, pop on closers.
</details>

<details><summary>💡 Hint 2</summary>
Map each closer to its opener. On closer: stack must be non-empty and top must match. End: stack must be empty.
</details>

<details><summary>💡 Hint 3</summary>
1. pairs = {')' to '(', ']' to '[', '}' to '{'}
2. For c: opener → push; closer → if stack empty or pop != pairs[c] → false
3. Return stack.isEmpty()
</details>

```kotlin
fun isValid(s: String): Boolean {
    // TODO: Stack of openers, match closers
    return false
}
```

---

## 🟡 TUESDAY, Nov 3 — Medium HashMap + Frequency

### 9. Two Sum II (Sorted) — 🟡
**LeetCode #167** | Solution: `DSA/array/complement_search/TwoSumII.kt`

**Problem:** (Re-solve from Week 1 — this time also think about the HashMap approach vs two pointers.)

<details><summary>💡 Hint 1</summary>
Two Pointer (opposite ends) is optimal for sorted input.
</details>

<details><summary>💡 Hint 2</summary>
sum > target → right--; sum < target → left++.
</details>

<details><summary>💡 Hint 3</summary>
left = 0, right = n-1; loop while left < right comparing sum to target.
</details>

```kotlin
fun twoSum(numbers: IntArray, target: Int): IntArray {
    // TODO: Re-solve from scratch
    return intArrayOf()
}
```

---

### 10. Finding Pairs with Certain Sum — 🟡
**LeetCode #1865** | Solution: `DSA/array/complement_search/FindingPairsWithCertainSum.kt`

**Problem:** Design: count(nums1[i] + nums2[j] == tot). Count queries repeatedly.

<details><summary>💡 Hint 1</summary>
Precompute frequency map of one array; answer queries with complement lookups.
</details>

<details><summary>💡 Hint 2</summary>
freq2 = counts of nums2 values. count(tot) = sum over nums1[i] of freq2[tot - nums1[i]].
</details>

<details><summary>💡 Hint 3</summary>
1. init: freq2 = HashMap counting nums2
2. count(tot): for x in nums1 → total += freq2.getOrDefault(tot - x, 0)
</details>

```kotlin
class FindSumPairs(nums1: IntArray, nums2: IntArray) {
    // TODO: freq map of nums2, complement count queries
    fun count(tot: Int): Int = 0
}
```

---

### 11. Longest Consecutive Sequence — 🟡
**LeetCode #128** | Solution: `DSA/array/hashset_lookup/LongestConsecutiveSequence.kt`

**Problem:** Find length of the longest run of consecutive integers (e.g., 4,5,6,7) in O(n). Order doesn't matter.

<details><summary>💡 Hint 1</summary>
HashSet — only start counting from the BEGINNING of a sequence.
</details>

<details><summary>💡 Hint 2</summary>
A number x starts a sequence only if x-1 is NOT in the set. From a start, count upward while x+1, x+2... exist.
</details>

<details><summary>💡 Hint 3</summary>
1. set = nums.toSet()
2. For num in set: if num-1 not in set → length = 1; while num+length in set → length++; best = max(best, length)
</details>

```kotlin
fun longestConsecutive(nums: IntArray): Int {
    // TODO: Only start at sequence beginnings, count upward
    return 0
}
```

---

### 12. Valid Sudoku — 🟡
**LeetCode #36** | Solution: `DSA/array/hashset_lookup/ValidSudoku.kt`

**Problem:** Check a 9×9 board is valid: no repeats in any row, column, or 3×3 box.

<details><summary>💡 Hint 1</summary>
Three groups of HashSets: rows, cols, boxes.
</details>

<details><summary>💡 Hint 2</summary>
Box index for (r, c) = (r / 3) * 3 + (c / 3). Check each cell against its row set, col set, box set.
</details>

<details><summary>💡 Hint 3</summary>
1. rows = 9 sets, cols = 9 sets, boxes = 9 sets
2. For each filled cell: if value in rows[r] or cols[c] or boxes[boxIdx] → false; else add to all three
</details>

```kotlin
fun isValidSudoku(board: Array<CharArray>): Boolean {
    // TODO: Row/col/box sets, box = (r/3)*3 + c/3
    return false
}
```

---

### 13. Group Anagrams — 🟡
**LeetCode #49** | Solution: `DSA/array/set_operations/group_anagrams/GroupAnagrams.kt`

**Problem:** Group all anagrams together.

<details><summary>💡 Hint 1</summary>
HashMap — key = canonical form of the word.
</details>

<details><summary>💡 Hint 2</summary>
Sorted string as key: "eat" and "tea" both → "aet". Group words by key.
</details>

<details><summary>💡 Hint 3</summary>
1. map = HashMap<String, MutableList<String>>()
2. For word: key = word.toCharArray().sorted().joinToString(""); map[key].add(word)
3. Return map.values
</details>

```kotlin
fun groupAnagrams(strs: Array<String>): List<List<String>> {
    // TODO: Sorted word as key
    return listOf()
}
```

---

### 14. Count the Number of Pairs — 🟡
**LeetCode #2006** | Solution: `DSA/array/frequency_count/CountTheNumberOfPairs.kt`

**Problem:** Count pairs (i, j) with |nums[i] - nums[j]| == k.

<details><summary>💡 Hint 1</summary>
Frequency map — for each num, pairs with num + k.
</details>

<details><summary>💡 Hint 2</summary>
If k > 0: count += freq[num] × freq[num + k] for each unique num (avoids double counting). If k == 0: count += C(freq, 2).
</details>

<details><summary>💡 Hint 3</summary>
1. Build freq map
2. For (num, f) in freq: if k > 0 → count += f × freq[num+k]; if k == 0 → count += f × (f-1) / 2
</details>

```kotlin
fun countKDifference(nums: IntArray, k: Int): Int {
    // TODO: Freq map, pair with num + k
    return 0
}
```

---

### 15. Finding Fair Pairs — 🟡
**LeetCode #2563** | Solution: `DSA/array/frequency_count/FindingFairPairs.kt`

**Problem:** Count pairs (i < j) with lower <= nums[i] + nums[j] <= upper.

<details><summary>💡 Hint 1</summary>
Sort + Two Pointer (or binary search) for range counting.
</details>

<details><summary>💡 Hint 2</summary>
Count pairs with sum <= X via two pointers. Answer = countPairs(<= upper) - countPairs(<= lower - 1).
</details>

<details><summary>💡 Hint 3</summary>
1. Sort. helper(limit): left = 0, right = n-1; while left < right: if nums[left]+nums[right] <= limit → count += right-left, left++ else right--
2. Return helper(upper) - helper(lower-1)
</details>

```kotlin
fun countFairPairs(nums: IntArray, lower: Int, upper: Int): Long {
    // TODO: Sort, count pairs under limit, subtract
    return 0
}
```

---

### 16. Top K Frequent Elements — 🟡
**LeetCode #347** | Solution: `DSA/array/frequency_count/TopKFrequentElements.kt`

**Problem:** Return the k most frequent elements.

<details><summary>💡 Hint 1</summary>
Frequency map + min-heap of size k (or bucket sort).
</details>

<details><summary>💡 Hint 2</summary>
Count frequencies. Push (freq, num) into a min-heap; when size > k → poll. Remaining = top k.
</details>

<details><summary>💡 Hint 3</summary>
1. freq = HashMap counting
2. heap = PriorityQueue by frequency (min-heap)
3. For each entry: push; if heap.size > k → poll
4. Extract nums from heap
</details>

```kotlin
fun topKFrequent(nums: IntArray, k: Int): IntArray {
    // TODO: Freq map + min-heap of size k
    return intArrayOf()
}
```

---

## 🟡 WEDNESDAY, Nov 4 — Stack Basics + Monotonic Stack

### 17. Min Stack — 🟡
**LeetCode #155** | Solution: `DSA/stack/MinStack.kt`

**Problem:** Design a stack supporting push, pop, top, and getMin — all O(1).

<details><summary>💡 Hint 1</summary>
Auxiliary stack tracking the current minimum.
</details>

<details><summary>💡 Hint 2</summary>
On push x: push min(x, currentMin) onto the min stack too. Both stacks stay in sync.
</details>

<details><summary>💡 Hint 3</summary>
1. Two stacks: main, mins
2. push(x): main.push(x); mins.push(if mins empty x else min(x, mins.peek()))
3. pop(): pop both. getMin(): mins.peek()
</details>

```kotlin
class MinStack() {
    // TODO: Main stack + min stack
    fun push(value: Int) {}
    fun pop() {}
    fun top(): Int = 0
    fun getMin(): Int = 0
}
```

---

### 18. Next Greater Element I — 🟡
**LeetCode #496** | Solution: `DSA/stack/NextGreaterElementI.kt`

**Problem:** For each element of nums1 (subset of nums2), find its next greater element in nums2, or -1.

<details><summary>💡 Hint 1</summary>
Monotonic (decreasing) stack over nums2 + HashMap of answers.
</details>

<details><summary>💡 Hint 2</summary>
Stack holds elements waiting for their next greater. When current > stack top → current is the answer for top; pop and record.
</details>

<details><summary>💡 Hint 3</summary>
1. stack, map = HashMap<Int, Int>()
2. For num in nums2: while stack not empty && num > stack.peek() → map[stack.pop()] = num
3. stack.push(num)
4. For n in nums1 → map[n] ?: -1
</details>

```kotlin
fun nextGreaterElement(nums1: IntArray, nums2: IntArray): IntArray {
    // TODO: Monotonic stack over nums2, map answers
    return intArrayOf()
}
```

---

### 19. Next Greater Element II (Circular) — 🟡
**LeetCode #503** | Solution: `DSA/stack/NextGreaterElementII.kt`

**Problem:** Same as above but nums2 is circular — next greater can wrap around.

<details><summary>💡 Hint 1</summary>
Monotonic stack, iterate the array TWICE (i % n) to handle wrap-around.
</details>

<details><summary>💡 Hint 2</summary>
Store indices in the stack. On second pass, don't push new elements — only resolve.
</details>

<details><summary>💡 Hint 3</summary>
1. result = IntArray(n) { -1 }, stack of indices
2. For i in 0 until 2n: while stack not empty && nums[i % n] > nums[stack.peek()] → result[stack.pop()] = nums[i % n]
3. If i < n → stack.push(i)
</details>

```kotlin
fun nextGreaterElements(nums: IntArray): IntArray {
    // TODO: Two passes with index stack
    return intArrayOf()
}
```

---

### 20. Daily Temperatures — 🟡
**LeetCode #739** | Solution: `DSA/stack/daily_temperatures/DailyTemperatures.kt`

**Problem:** For each day, how many days until a warmer temperature?

<details><summary>💡 Hint 1</summary>
Monotonic (decreasing) stack of indices.
</details>

<details><summary>💡 Hint 2</summary>
When today's temp > temp at stack top → the answer for top = today - top. Pop and record.
</details>

<details><summary>💡 Hint 3</summary>
1. result = IntArray(n), stack of indices
2. For i: while stack not empty && temps[i] > temps[stack.peek()] → result[stack.pop()] = i - top
3. stack.push(i)
</details>

```kotlin
fun dailyTemperatures(temperatures: IntArray): IntArray {
    // TODO: Index stack, resolve on warmer day
    return intArrayOf()
}
```

---

### 21. Online Stock Span — 🟡
**LeetCode #901** | Solution: `DSA/stack/OnlineStockSpan.kt`

**Problem:** Span today = number of consecutive days (including today) with price <= today's price.

<details><summary>💡 Hint 1</summary>
Monotonic stack of (price, span) pairs.
</details>

<details><summary>💡 Hint 2</summary>
Pop all (price, span) with price <= today; accumulate their spans. Today's span = 1 + sum of popped spans.
</details>

<details><summary>💡 Hint 3</summary>
1. stack of (price, span)
2. next(price): span = 1; while stack top price <= price → span += popped span
3. push(price, span); return span
</details>

```kotlin
class StockSpanner() {
    // TODO: Stack of (price, span) pairs
    fun next(price: Int): Int = 0
}
```

---

### 22. Evaluate Reverse Polish Notation — 🟡
**LeetCode #150** | Solution: `DSA/stack/evaluate_reverse_polish_notation/EvaluateReversePolishNotation.kt`

**Problem:** Evaluate a postfix expression: tokens like ["2","1","+","3","*"] → 9.

<details><summary>💡 Hint 1</summary>
Stack — push numbers, on operator pop two, apply, push result.
</details>

<details><summary>💡 Hint 2</summary>
Order matters for - and /: second popped (op) first popped. Use Long to avoid overflow. Kotlin division truncates toward zero (matches requirement).
</details>

<details><summary>💡 Hint 3</summary>
1. For token: number → push
2. Operator → b = pop, a = pop; push(a op b)
</details>

```kotlin
fun evalRPN(tokens: Array<String>): Int {
    // TODO: Number stack, apply operators
    return 0
}
```

---

## 🟡 THURSDAY, Nov 5 — Hard Stack Problems

### 23. Minimum Parentheses to Remove — 🟡
**LeetCode #1249** | Solution: `DSA/stack/MinimumParentheseToRemove.kt`

**Problem:** Make the string valid by removing the minimum number of parentheses; return the result.

<details><summary>💡 Hint 1</summary>
Stack of indices of unmatched '(' + count of unmatched ')'.
</details>

<details><summary>💡 Hint 2</summary>
Scan: '(' → push index. ')' → pop if possible, else mark for removal. At end, all remaining stack indices are removed too.
</details>

<details><summary>💡 Hint 3</summary>
1. stack of indices, remove = HashSet<Int>()
2. For i: '(' → push(i); ')' → if stack empty → remove.add(i) else pop
3. Add remaining stack indices to remove
4. Build result skipping removed indices
</details>

```kotlin
fun minRemoveToMakeValid(s: String): String {
    // TODO: Track unmatched indices, rebuild
    return ""
}
```

---

### 24. Car Fleet — 🟡
**LeetCode #853** | Solution: `DSA/stack/car_fleet/CarFleet.kt`

**Problem:** Cars at positions with speeds drive toward target. A car catches up to a slower car ahead → they form a fleet. Count fleets.

<details><summary>💡 Hint 1</summary>
Sort by position DESCENDING (closest to target first). Compute each car's arrival TIME.
</details>

<details><summary>💡 Hint 2</summary>
Scan from closest-to-target: if a car's time > current fleet's time → new fleet; else it joins the fleet ahead (never passes).
</details>

<details><summary>💡 Hint 3</summary>
1. Sort (position desc). times = (target - pos) / speed
2. fleets = 0, lastTime = 0.0
3. For each time: if time > lastTime → fleets++, lastTime = time
</details>

```kotlin
fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
    // TODO: Sort desc, compare arrival times
    return 0
}
```

---

### 25. Longest Valid Parentheses — 🔴
**LeetCode #32** | Solution: `DSA/stack/longest_valid_parentheses/LongestValidParentheses.kt`

**Problem:** Find the length of the longest valid (well-formed) parentheses substring.

<details><summary>💡 Hint 1</summary>
Stack of indices — with a sentinel base of -1.
</details>

<details><summary>💡 Hint 2</summary>
Push '(' indices. On ')': pop; if stack empty → push current index (new base); else length = i - stack.peek().
</details>

<details><summary>💡 Hint 3</summary>
1. stack = [-1] (base)
2. For i: '(' → push(i)
3. ')' → pop. If empty → push(i) (new base). Else best = max(best, i - stack.peek())
</details>

```kotlin
fun longestValidParentheses(s: String): Int {
    // TODO: Index stack with -1 base
    return 0
}
```

---

### 26. Largest Rectangle in Histogram — 🔴
**LeetCode #84** | Solution: `DSA/stack/largest_rectangle_in_histogram/LargestRectangleInHistogram.kt`

**Problem:** Given bar heights, find the largest rectangular area.

<details><summary>💡 Hint 1</summary>
Monotonic INCREASING stack of indices. For each bar: how far can it extend left and right at its full height?
</details>

<details><summary>💡 Hint 2</summary>
When current bar is SHORTER than stack top → top bar's right boundary is found. Its area = height × (current index - index after new stack top - 1).
</details>

<details><summary>💡 Hint 3</summary>
1. stack of indices (heights increasing)
2. For i in 0..n (use height 0 at end to flush):
3. While stack not empty && h[i] < h[stack.peek()]: height = h[stack.pop()]; width = i - (stack empty ? 0 : stack.peek() + 1); best = max(best, height × width)
4. push(i)
</details>

```kotlin
fun largestRectangleArea(heights: IntArray): Int {
    // TODO: Increasing index stack, flush with sentinel 0
    return 0
}
```

---

## 🟢 SATURDAY, Nov 7 — Revision + Mixed Practice

1. Re-solve the 3–4 hardest problems this week (from scratch, no hints)
2. Mixed practice from Weeks 1–2:
   - Three Sum (Week 1)
   - Subarray Sum Equals K (Week 2)
   - Longest Substring Without Repeating (Week 2)
3. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## 🧪 SUNDAY, Nov 8 — Mock Test 3 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Valid Anagrams | Frequency Count | 🟢 | 15 min |
| 2 | Longest Consecutive Sequence | HashSet | 🟡 | 25 min |
| 3 | Daily Temperatures | Monotonic Stack | 🟡 | 25 min |
| 4 | Group Anagrams | HashMap Grouping | 🟡 | 20 min |
| 5 | Largest Rectangle in Histogram | Hard Stack | 🔴 | 35 min |

**Part 2 — Review (1 hr):** Score, log in [Mock_Test_Log.md](../Mock_Test_Log.md), re-solve failures, update tracker.

---

## ✅ Week 3 Completion Checklist

- [ ] Mon: 8 easy HashSet/Set problems
- [ ] Tue: 8 HashMap/Frequency problems
- [ ] Wed: 6 stack problems
- [ ] Thu: 4 hard stack problems
- [ ] Sat: Revision + Weeks 1–2 mixed practice
- [ ] Sun: Mock Test 3 completed & logged
- [ ] Progress Tracker updated
