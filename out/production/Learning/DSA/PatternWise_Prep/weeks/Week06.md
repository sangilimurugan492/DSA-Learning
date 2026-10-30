# 📅 Week 6 — Greedy + Binary Search + Bit Manipulation

> **Dates:** Nov 23 (Mon) – Nov 29 (Sun)
> **Pattern Focus:** Greedy (intervals, jumps, stocks), Binary Search (rotated arrays), Bit Manipulation (XOR, bit counting)
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/array/greedy/`, `DSA/array/binary_search/`, `DSA/array/bit_manipulation/`, `DSA/array/voting_floyd/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week06_Practice.md](../practice/Week06_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Mon | Nov 23 | #1–#7 (Easy greedy + bit) | 2 hrs |
| Tue | Nov 24 | #8–#14 (Medium greedy: intervals) | 2 hrs |
| Wed | Nov 25 | #15–#20 (Greedy + binary search) | 2 hrs |
| Thu | Nov 26 | #21–#28 (Bit manipulation + voting) | 2 hrs |
| Sat | Nov 28 | Revision: weakest 4 + Weeks 1–5 mixed | 2 hrs |
| Sun | Nov 29 | 🧪 Mock Test 6 + Review | 3 hrs |

---

## 🟢 MONDAY, Nov 23 — Easy Greedy + Bit

### 1. Best Time to Buy & Sell Stock I — 🟢
**LeetCode #121** | Solution: `DSA/array/greedy/BestTimeToBuyAndSellStockI.kt`

**Problem:** One buy + one sell — max profit?

<details><summary>💡 Hint 1</summary>
Track the minimum price so far; profit = today - minSoFar.
</details>

<details><summary>💡 Hint 2</summary>
Single pass: minPrice = min(minPrice, price); best = max(best, price - minPrice).
</details>

<details><summary>💡 Hint 3</summary>
1. minPrice = MAX, best = 0
2. For price: best = max(best, price - minPrice); minPrice = min(minPrice, price)
</details>

```kotlin
fun maxProfit(prices: IntArray): Int {
    // TODO: Track min so far, best profit
    return 0
}
```

---

### 2. Assign Cookies — 🟢
**LeetCode #455** | Solution: `DSA/array/greedy/AssignCookies.kt`

**Problem:** Each child wants a cookie of size >= g[i]. Maximize content children.

<details><summary>💡 Hint 1</summary>
Sort both; two pointers — give the smallest sufficient cookie to the least greedy child.
</details>

<details><summary>💡 Hint 2</summary>
If cookie >= child's greed → child++, cookie++. Else cookie++ (cookie too small for anyone remaining).
</details>

<details><summary>💡 Hint 3</summary>
Sort g and s. i = 0 (child), j = 0 (cookie). While both in range: if s[j] >= g[i] → i++; j++. Return i.
</details>

```kotlin
fun findContentChildren(g: IntArray, s: IntArray): Int {
    // TODO: Sort + two pointers
    return 0
}
```

---

### 3. Lemonade Change — 🟢
**LeetCode #860** | Solution: `DSA/array/greedy/LemonadeChange.kt`

**Problem:** Each customer pays $5, $10, or $20 for a $5 lemonade. Can you give change to everyone?

<details><summary>💡 Hint 1</summary>
Track counts of $5 and $10 bills.
</details>

<details><summary>💡 Hint 2</summary>
$20 change: prefer $10 + $5 over three $5s (greedy — keep $5s flexible).
</details>

<details><summary>💡 Hint 3</summary>
1. fives = 0, tens = 0
2. $5 → fives++. $10 → need fives > 0; fives--, tens++
3. $20 → if tens > 0 && fives > 0 → tens--, fives--; else if fives >= 3 → fives -= 3; else false
</details>

```kotlin
fun lemonadeChange(bills: IntArray): Boolean {
    // TODO: Track fives/tens, greedy change for $20
    return false
}
```

---

### 4. Single Number — 🟢
**LeetCode #136** | Solution: `DSA/array/bit_manipulation/single_number/SingleNumber.kt`

**Problem:** Every element appears twice except one. Find it.

<details><summary>💡 Hint 1</summary>
XOR: a ^ a = 0, a ^ 0 = a.
</details>

<details><summary>💡 Hint 2</summary>
XOR everything — pairs cancel, the single number remains.
</details>

<details><summary>💡 Hint 3</summary>
var result = 0; for (num) result = result xor num; return result.
</details>

```kotlin
fun singleNumber(nums: IntArray): Int {
    // TODO: XOR all
    return 0
}
```

---

### 5. Number of 1 Bits — 🟢
**LeetCode #191** | Solution: `DSA/array/bit_manipulation/number_of_1_bits/NumberOf1Bits.kt`

**Problem:** Count set bits (Hamming weight).

<details><summary>💡 Hint 1</summary>
n & (n-1) removes the lowest set bit.
</details>

<details><summary>💡 Hint 2</summary>
Repeat until n == 0, counting iterations.
</details>

<details><summary>💡 Hint 3</summary>
while (n != 0) { n = n and (n - 1); count++ }
</details>

```kotlin
fun hammingWeight(n: Int): Int {
    // TODO: n & (n-1) trick
    return 0
}
```

---

### 6. Missing Number — 🟢
**LeetCode #268** | Solution: `DSA/array/bit_manipulation/missing_number/MissingNumber.kt`

**Problem:** nums contains n distinct numbers from [0, n]. Find the missing one.

<details><summary>💡 Hint 1</summary>
XOR all indices 0..n with all values — everything cancels except the missing number.
</details>

<details><summary>💡 Hint 2</summary>
(Or: expected sum n(n+1)/2 - actual sum.)
</details>

<details><summary>💡 Hint 3</summary>
var result = nums.size; for (i in 0 until n) result = result xor i xor nums[i]; return result.
</details>

```kotlin
fun missingNumber(nums: IntArray): Int {
    // TODO: XOR indices and values
    return 0
}
```

---

### 7. Power of Two — 🟢
**LeetCode #231** | Solution: `DSA/array/bit_manipulation/power_of_two/PowerOfTwo.kt`

**Problem:** Is n an exact power of two?

<details><summary>💡 Hint 1</summary>
A power of two has exactly ONE set bit.
</details>

<details><summary>💡 Hint 2</summary>
n > 0 && (n & (n-1)) == 0.
</details>

<details><summary>💡 Hint 3</summary>
One-line check.
</details>

```kotlin
fun isPowerOfTwo(n: Int): Boolean {
    // TODO: Single set bit check
    return false
}
```

---

## 🟡 TUESDAY, Nov 24 — Medium Greedy: Intervals & Jumps

### 8. Best Time to Buy & Sell Stock II — 🟡
**LeetCode #122** | Solution: `DSA/array/greedy/BestTimeToBuyAndSellStockII.kt`

**Problem:** Multiple transactions allowed — max profit?

<details><summary>💡 Hint 1</summary>
Sum all positive consecutive differences.
</details>

<details><summary>💡 Hint 2</summary>
If prices[i+1] > prices[i] → add the difference to profit.
</details>

<details><summary>💡 Hint 3</summary>
For i in 1 until n: if (prices[i] > prices[i-1]) profit += prices[i] - prices[i-1].
</details>

```kotlin
fun maxProfit(prices: IntArray): Int {
    // TODO: Sum positive daily gains
    return 0
}
```

---

### 9. Jump Game — 🟡
**LeetCode #55** | Solution: `DSA/array/greedy/JumpGame.kt`

**Problem:** Can you reach the last index? nums[i] = max jump length.

<details><summary>💡 Hint 1</summary>
Track the farthest reachable index as you scan.
</details>

<details><summary>💡 Hint 2</summary>
If current index i > farthest → stuck → false. Else farthest = max(farthest, i + nums[i]).
</details>

<details><summary>💡 Hint 3</summary>
1. farthest = 0
2. For i: if i > farthest → false; farthest = max(farthest, i + nums[i])
3. Return true
</details>

```kotlin
fun canJump(nums: IntArray): Boolean {
    // TODO: Track farthest reachable
    return false
}
```

---

### 10. Jump Game II — 🟡
**LeetCode #45** | Solution: `DSA/array/greedy/JumpGameII.kt`

**Problem:** Min jumps to reach the last index (guaranteed reachable).

<details><summary>💡 Hint 1</summary>
BFS-like levels: currentEnd = end of current jump's reach; farthest = best of next jump.
</details>

<details><summary>💡 Hint 2</summary>
When i reaches currentEnd → jumps++, currentEnd = farthest.
</details>

<details><summary>💡 Hint 3</summary>
1. jumps = 0, currentEnd = 0, farthest = 0
2. For i in 0 until n-1: farthest = max(farthest, i + nums[i])
3. If i == currentEnd → jumps++, currentEnd = farthest
</details>

```kotlin
fun jump(nums: IntArray): Int {
    // TODO: Level-based greedy
    return 0
}
```

---

### 11. Gas Station — 🟡
**LeetCode #134** | Solution: `DSA/array/greedy/GasStation.kt`

**Problem:** Circular route of gas stations. Return the unique starting station that completes the loop, or -1.

<details><summary>💡 Hint 1</summary>
If total gas < total cost → impossible. Otherwise a unique answer exists.
</details>

<details><summary>💡 Hint 2</summary>
Greedy: if running out of gas at station i, no station between the current start and i can be the answer → restart from i+1.
</details>

<details><summary>💡 Hint 3</summary>
1. total = 0, tank = 0, start = 0
2. For i: diff = gas[i] - cost[i]; total += diff; tank += diff
3. If tank < 0 → start = i + 1; tank = 0
4. Return if (total >= 0) start else -1
</details>

```kotlin
fun canCompleteCircuit(gas: IntArray, cost: IntArray): Int {
    // TODO: Total check + restart on empty tank
    return -1
}
```

---

### 12. Merge Intervals — 🟡
**LeetCode #56** | Solution: `DSA/array/greedy/MergeIntervals.kt`

**Problem:** Merge overlapping intervals.

<details><summary>💡 Hint 1</summary>
Sort by start. Merge into the last output interval.
</details>

<details><summary>💡 Hint 2</summary>
If current.start <= last.end → last.end = max(last.end, current.end). Else append current.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort by start
2. For each interval: if result empty or start > result.last.end → add; else merge (extend end)
</details>

```kotlin
fun merge(intervals: Array<IntArray>): Array<IntArray> {
    // TODO: Sort, merge into result
    return arrayOf()
}
```

---

### 13. Insert Interval — 🟡
**LeetCode #57** | Solution: `DSA/array/greedy/InsertInterval.kt`

**Problem:** Insert a new interval into sorted, non-overlapping intervals; merge if needed.

<details><summary>💡 Hint 1</summary>
Three phases: intervals entirely before (add as-is), overlapping (merge into new), entirely after (add as-is).
</details>

<details><summary>💡 Hint 2</summary>
Overlap test: interval.start <= new.end && interval.end >= new.start → merge: new.start = min, new.end = max.
</details>

<details><summary>💡 Hint 3</summary>
1. Add all intervals with end < new.start
2. While overlap → merge into new
3. Add new, then all remaining intervals
</details>

```kotlin
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    // TODO: Before / merge / after phases
    return arrayOf()
}
```

---

### 14. Meeting Rooms — 🟡
**Solution:** `DSA/array/greedy/MeetingRooms.kt`

**Problem:** Can one person attend all meetings (no overlaps)?

<details><summary>💡 Hint 1</summary>
Sort by start; check adjacent pairs.
</details>

<details><summary>💡 Hint 2</summary>
If intervals[i].start < intervals[i-1].end → overlap → false.
</details>

<details><summary>💡 Hint 3</summary>
Sort by start time; single loop comparing adjacent.
</details>

```kotlin
fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
    // TODO: Sort, adjacent overlap check
    return false
}
```

---

## 🟡 WEDNESDAY, Nov 25 — Greedy + Binary Search

### 15. Non-Overlapping Intervals — 🟡
**LeetCode #435** | Solution: `DSA/array/greedy/NonOverlappingIntervals.kt`

**Problem:** Min intervals to remove so none overlap.

<details><summary>💡 Hint 1</summary>
Equivalent to keeping the max non-overlapping set. Sort by END time.
</details>

<details><summary>💡 Hint 2</summary>
Greedy: keep an interval if its start >= last kept end; else remove it (count++).
</details>

<details><summary>💡 Hint 3</summary>
1. Sort by end
2. For each interval: if start >= lastEnd → keep (lastEnd = end); else removals++
</details>

```kotlin
fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
    // TODO: Sort by end, count removals
    return 0
}
```

---

### 16. Min Arrows to Burst Balloons — 🟡
**LeetCode #452** | Solution: `DSA/array/greedy/MinimumArrowsToBurstBalloons.kt`

**Problem:** Balloons are intervals on an x-axis; one arrow at x bursts all balloons covering x. Min arrows?

<details><summary>💡 Hint 1</summary>
Sort by start; track the current arrow position (rightmost shared point).
</details>

<details><summary>💡 Hint 2</summary>
If next balloon's start > current arrow position → new arrow; else arrow = min(arrow, balloon.end).
</details>

<details><summary>💡 Hint 3</summary>
1. Sort by start. arrows = 1, arrowPos = first.end
2. For each: if start > arrowPos → arrows++, arrowPos = end; else arrowPos = min(arrowPos, end)
</details>

```kotlin
fun findMinArrowShots(points: Array<IntArray>): Int {
    // TODO: Sort, track shared arrow position
    return 0
}
```

---

### 17. Task Scheduler — 🟡
**LeetCode #621** | Solution: `DSA/array/greedy/TaskScheduler.kt`

**Problem:** Tasks A-Z with cooldown n between same tasks. Min intervals (idle allowed)?

<details><summary>💡 Hint 1</summary>
Frame the schedule around the MOST frequent task.
</details>

<details><summary>💡 Hint 2</summary>
Formula: max(tasks.size, (maxFreq - 1) × (n + 1) + countOfMaxFreqTasks).
</details>

<details><summary>💡 Hint 3</summary>
1. Count frequencies; maxFreq = highest, count = how many tasks have maxFreq
2. Return max(total, (maxFreq - 1) * (n + 1) + count)
</details>

```kotlin
fun leastInterval(tasks: CharArray, n: Int): Int {
    // TODO: Max frequency framing formula
    return 0
}
```

---

### 18. Partition Labels — 🟡
**LeetCode #763** | Solution: `DSA/array/greedy/PartitionLabels.kt`

**Problem:** Split the string into as many parts as possible so each letter appears in at most one part.

<details><summary>💡 Hint 1</summary>
Precompute the LAST index of each character.
</details>

<details><summary>💡 Hint 2</summary>
Scan: extend the current partition's end to max(lastIndex of chars seen). When i == end → cut.
</details>

<details><summary>💡 Hint 3</summary>
1. last = map of char → last index
2. end = 0, start = 0
3. For i: end = max(end, last[s[i]]); if i == end → record (i - start + 1); start = i + 1
</details>

```kotlin
fun partitionLabels(s: String): List<Int> {
    // TODO: Last index map, cut when i == end
    return listOf()
}
```

---

### 19. Search in Rotated Sorted Array — 🟡
**LeetCode #33** | Solution: `DSA/array/binary_search/SearchInRotatedSortedArray.kt`

**Problem:** Search in a rotated sorted array in O(log n).

<details><summary>💡 Hint 1</summary>
Modified binary search — one half is ALWAYS sorted.
</details>

<details><summary>💡 Hint 2</summary>
If nums[left..mid] sorted → target in that range? Search left half, else right half. Symmetric for the other case.
</details>

<details><summary>💡 Hint 3</summary>
1. left = 0, right = n-1
2. mid: if nums[mid] == target → done
3. If nums[left] <= nums[mid] (left half sorted): if target in [nums[left], nums[mid]) → right = mid-1 else left = mid+1
4. Else: if target in (nums[mid], nums[right]] → left = mid+1 else right = mid-1
</details>

```kotlin
fun search(nums: IntArray, target: Int): Int {
    // TODO: Identify sorted half, narrow search
    return -1
}
```

---

### 20. Find Min in Rotated Sorted Array — 🟡
**LeetCode #153** | Solution: `DSA/array/binary_search/FindMinimumInRotatedSortedArray.kt`

**Problem:** Find the minimum in a rotated sorted array (unique elements).

<details><summary>💡 Hint 1</summary>
Binary search comparing mid with nums[right].
</details>

<details><summary>💡 Hint 2</summary>
If nums[mid] > nums[right] → min is in the right half (left = mid+1). Else → min is at mid or left (right = mid).
</details>

<details><summary>💡 Hint 3</summary>
While left < right: mid = (left + right) / 2; if nums[mid] > nums[right] → left = mid + 1 else right = mid. Return nums[left].
</details>

```kotlin
fun findMin(nums: IntArray): Int {
    // TODO: Compare mid with right
    return 0
}
```

---

## 🟡 THURSDAY, Nov 26 — Bit Manipulation + Voting

### 21. Single Number II — 🟡
**LeetCode #137** | Solution: `DSA/array/bit_manipulation/single_number_ii/SingleNumberII.kt`

**Problem:** Every element appears three times except one. Find it.

<details><summary>💡 Hint 1</summary>
Count set bits at each of 32 positions.
</details>

<details><summary>💡 Hint 2</summary>
If count % 3 != 0 → that bit is set in the answer.
</details>

<details><summary>💡 Hint 3</summary>
1. For each bit position: count how many numbers have it set
2. If count % 3 != 0 → result |= (1 shl bit)
</details>

```kotlin
fun singleNumber(nums: IntArray): Int {
    // TODO: Bit counting mod 3
    return 0
}
```

---

### 22. Sum of Two Integers — 🟡
**LeetCode #371** | Solution: `DSA/array/bit_manipulation/sum_of_two_integers/SumOfTwoIntegers.kt`

**Problem:** Add two integers without + or -.

<details><summary>💡 Hint 1</summary>
Half-adder: XOR = sum without carry; AND << 1 = carry.
</details>

<details><summary>💡 Hint 2</summary>
Repeat until carry is 0.
</details>

<details><summary>💡 Hint 3</summary>
while (b != 0) { val carry = (a and b) shl 1; a = a xor b; b = carry }; return a.
</details>

```kotlin
fun getSum(a: Int, b: Int): Int {
    // TODO: XOR + carry loop
    return 0
}
```

---

### 23. Counting Bits — 🟡
**LeetCode #338** | Solution: `DSA/array/bit_manipulation/counting_bits/CountingBits.kt`

**Problem:** For every i in [0, n], count set bits of i.

<details><summary>💡 Hint 1</summary>
DP: bits[i] = bits[i >> 1] + (i & 1).
</details>

<details><summary>💡 Hint 2</summary>
i >> 1 is i without its last bit; (i & 1) is the last bit.
</details>

<details><summary>💡 Hint 3</summary>
bits[0] = 0; for i in 1..n: bits[i] = bits[i shr 1] + (i and 1).
</details>

```kotlin
fun countBits(n: Int): IntArray {
    // TODO: DP on shifted index
    return intArrayOf()
}
```

---

### 24. Queue Reconstruction by Height — 🟡
**LeetCode #406** | Solution: `DSA/array/greedy/QueueReconstructionByHeight.kt`

**Problem:** People as (h, k): k = people in front with height >= h. Reconstruct the queue.

<details><summary>💡 Hint 1</summary>
Sort by height DESC, then k ASC. Insert each person at index k.
</details>

<details><summary>💡 Hint 2</summary>
Taller people first — inserting a shorter person later doesn't disturb taller people's k counts.
</details>

<details><summary>💡 Hint 3</summary>
1. Sort: (h desc, k asc)
2. For each person → insert at index k into the result list
</details>

```kotlin
fun reconstructQueue(people: Array<IntArray>): Array<IntArray> {
    // TODO: Sort desc, insert at k
    return arrayOf()
}
```

---

### 25. Wiggle Subsequence — 🟡
**LeetCode #376** | Solution: `DSA/array/greedy/WiggleSubsequence.kt`

**Problem:** Longest subsequence where differences alternate signs.

<details><summary>💡 Hint 1</summary>
Count the number of direction changes (peaks and valleys).
</details>

<details><summary>💡 Hint 2</summary>
Track up/down states; increment length when the direction flips.
</details>

<details><summary>💡 Hint 3</summary>
1. up = 1, down = 1
2. For i: if nums[i] > nums[i-1] → up = down + 1; if < → down = up + 1
3. Return max(up, down)
</details>

```kotlin
fun wiggleMaxLength(nums: IntArray): Int {
    // TODO: Track alternating direction changes
    return 0
}
```

---

### 26. Candy — 🔴
**LeetCode #135** | Solution: `DSA/array/greedy/Candy.kt`

**Problem:** Each child gets at least 1 candy; children with higher ratings get more than ADJACENT neighbors. Min candies?

<details><summary>💡 Hint 1</summary>
Two passes: left-to-right, then right-to-left.
</details>

<details><summary>💡 Hint 2</summary>
Left pass: if rating[i] > rating[i-1] → candies[i] = candies[i-1] + 1. Right pass: symmetric, take max.
</details>

<details><summary>💡 Hint 3</summary>
1. candies = IntArray(n) { 1 }
2. Left pass for rising ratings; right pass for falling (candies[i] = max(candies[i], candies[i+1] + 1))
3. Sum
</details>

```kotlin
fun candy(ratings: IntArray): Int {
    // TODO: Two directional passes
    return 0
}
```

---

### 27. Median of Two Sorted Arrays — 🔴
**LeetCode #4** | Solution: `DSA/array/binary_search/MedianOfTwoSortedSubArray.kt`

**Problem:** Find the median of two sorted arrays in O(log(m+n)).

<details><summary>💡 Hint 1</summary>
Binary search the partition of the SMALLER array.
</details>

<details><summary>💡 Hint 2</summary>
Partition so that maxLeftA <= minRightB and maxLeftB <= minRightA. Median = boundary values.
</details>

<details><summary>💡 Hint 3</summary>
1. Binary search i in [0, m] (smaller array); j = (m + n + 1) / 2 - i
2. Check partition validity; adjust lo/hi
3. Even total → avg of max(lefts) and min(rights); odd → max(lefts)
</details>

```kotlin
fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    // TODO: Partition binary search
    return 0.0
}
```

---

### 28. Majority Element I & II — 🟢/🟡
**LeetCode #169/229** | Solutions: `DSA/array/voting_floyd/MajorityOfElements.kt`, `MajorityElementII.kt`

**Problem:** I: element appearing > n/2 times. II: elements appearing > n/3 times.

<details><summary>💡 Hint 1</summary>
Boyer-Moore Voting — candidate + count.
</details>

<details><summary>💡 Hint 2</summary>
I: one candidate. II: at most 2 candidates can exceed n/3 — track two (candidate1, candidate2) and verify at the end.
</details>

<details><summary>💡 Hint 3</summary>
II: for each num — match candidate1 → count1++; match candidate2 → count2++; count1 == 0 → new candidate1; count2 == 0 → new candidate2; else both counts--. Verify final counts > n/3.
</details>

```kotlin
fun majorityElement(nums: IntArray): List<Int> {
    // TODO: Two candidates, verify
    return listOf()
}
```

---

## 🟢 SATURDAY, Nov 28 — Revision + Mixed Practice

1. Re-solve the 3–4 hardest problems this week (from scratch, no hints)
2. Mixed practice from Weeks 1–5:
   - Three Sum (Week 1)
   - Subarray Sum Equals K (Week 2)
   - Daily Temperatures (Week 3)
   - Reverse in K-Group (Week 4)
   - Validate BST (Week 5)
3. Update [Progress_Tracker.md](../Progress_Tracker.md)

---

## 🧪 SUNDAY, Nov 29 — Mock Test 6 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Single Number | Bit XOR | 🟢 | 15 min |
| 2 | Merge Intervals | Greedy Intervals | 🟡 | 25 min |
| 3 | Search in Rotated Sorted Array | Binary Search | 🟡 | 25 min |
| 4 | Jump Game II | Greedy Levels | 🟡 | 25 min |
| 5 | Median of Two Sorted Arrays | Hard Binary Search | 🔴 | 35 min |

**Part 2 — Review (1 hr):** Score, log in [Mock_Test_Log.md](../Mock_Test_Log.md), re-solve failures, update tracker.

---

## ✅ Week 6 Completion Checklist

- [ ] Mon: 7 easy greedy/bit problems
- [ ] Tue: 7 medium greedy (intervals/jumps)
- [ ] Wed: 6 greedy + binary search problems
- [ ] Thu: 8 bit manipulation + voting problems
- [ ] Sat: Revision + Weeks 1–5 mixed practice
- [ ] Sun: Mock Test 6 completed & logged
- [ ] Progress Tracker updated
