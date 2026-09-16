r# 📝 DSA Quick Revision Notes

> **Purpose:** Quick scan before interviews. One glance per problem = recall the pattern + core logic.
> **Last Updated:** 16 Sep 2026 | **Problems Covered:** 72 (Week 1–2: 36 | Week 3–4: 36)

---

## 1. Complement Search

### Two Sum (Unsorted)
**LeetCode #1** | Pattern: Complement Search | 🟢 Easy

**Key Insight:** For each number, check if `target - num` was already seen. HashMap stores `value → index`.

**Approach:**
- Brute Force: O(N²) — check every pair
- Optimal: O(N) — one pass, HashMap lookup for complement

**Code (Optimal):**
```kotlin
fun twoSum(nums: IntArray, target: Int): IntArray {
    val seen = hashMapOf<Int, Int>()  // value → index
    for (i in nums.indices) {
        val complement = target - nums[i]
        if (complement in seen) return intArrayOf(seen[complement]!!, i)
        seen[nums[i]] = i
    }
    return intArrayOf()
}
```
**Time:** O(N) | **Space:** O(N)

---

### Finding Pairs with Certain Sum
**LeetCode #1865** | Pattern: Complement Search | 🟡 Medium

**Key Insight:** Maintain a frequency map of `nums2`. For `count(tot)`, for each `num1[i]`, look up `tot - num1[i]` in the map. On `add(index, val)`, update the map (decrement old value, increment new value).

**Approach:**
- Build `nums2Map`: frequency count of nums2 values
- `count(tot)`: for each num in nums1, add `nums2Map[tot - num]`
- `add(index, val)`: decrement old nums2[index] count, update nums2[index], increment new count

**Code (Core Logic):**
```kotlin
class FindSumPairs(val nums1: IntArray, val nums2: IntArray) {
    val nums2Map = mutableMapOf<Int, Int>()
    init { nums2.forEach { nums2Map[it] = nums2Map.getOrDefault(it, 0) + 1 } }

    fun add(index: Int, value: Int) {
        nums2Map[nums2[index]] = nums2Map[nums2[index]]!! - 1
        nums2[index] += value
        nums2Map[nums2[index]] = nums2Map.getOrDefault(nums2[index], 0) + 1
    }

    fun count(tot: Int): Int {
        var count = 0
        for (num in nums1) count += nums2Map.getOrDefault(tot - num, 0)
        return count
    }
}
```
**Time:** O(M + N) init, O(M) per count | **Space:** O(M + N)

---

## 2. Two Pointer

### Two Sum II (Sorted Array)
**LeetCode #167** | Pattern: Two Pointer | 🟡 Medium

**Key Insight:** Array is sorted → converge from both ends. Sum too small → move left. Sum too big → move right.

**Approach:**
- Brute Force: O(N²) — check every pair
- Optimal: O(N) — two pointers from left (0) and right (n-1)

**Code (Optimal):**
```kotlin
fun twoSumSorted(numbers: IntArray, target: Int): IntArray {
    var left = 0
    var right = numbers.size - 1
    while (left < right) {
        val sum = numbers[left] + numbers[right]
        when {
            sum > target -> right--
            sum < target -> left++
            else -> return intArrayOf(left + 1, right + 1)  // 1-indexed
        }
    }
    return intArrayOf()
}
```
**Time:** O(N) | **Space:** O(1)

---

### Three Sum
**LeetCode #15** | Pattern: Two Pointer | 🟡 Medium

**Key Insight:** Sort + fix first element + two-pointer for remaining two. Skip duplicates at ALL levels (first element, left, right) to avoid duplicate triplets.

**Approach:**
- Brute Force: O(N³) — three nested loops with Set for dedup
- Sort + Two Pointer: O(N²) — sort, fix i, two-pointer on rest, skip duplicates

**Code (Optimal):**
```kotlin
fun threeSum(nums: IntArray): List<List<Int>> {
    val result = mutableListOf<List<Int>>()
    nums.sort()
    for (i in nums.indices) {
        if (i > 0 && nums[i] == nums[i - 1]) continue  // skip duplicate first
        var left = i + 1; var right = nums.size - 1
        while (left < right) {
            val sum = nums[i] + nums[left] + nums[right]
            when {
                sum < 0 -> left++
                sum > 0 -> right--
                else -> {
                    result.add(listOf(nums[i], nums[left], nums[right]))
                    left++; right--
                    while (left < right && nums[left] == nums[left - 1]) left++      // skip dup
                    while (left < right && nums[right] == nums[right + 1]) right--   // skip dup
                }
            }
        }
    }
    return result
}
```
**Time:** O(N²) | **Space:** O(1) (ignoring output)

---

## 3. Greedy

### Best Time to Buy & Sell Stock I
**LeetCode #121** | Pattern: Greedy | 🟢 Easy

**Key Insight:** Track `minPrice` so far. At each day, profit = `price[i] - minPrice`. Update `maxProfit`.

**Approach:**
- Brute Force: O(N²) — try every buy/sell pair
- Optimal: O(N) — one pass, track min price + max profit

**Code (Optimal):**
```kotlin
fun maxProfit(prices: IntArray): Int {
    var maxProfit = 0
    var minPrice = prices[0]
    for (i in 1 until prices.size) {
        val profit = prices[i] - minPrice
        if (prices[i] < minPrice) minPrice = prices[i]
        if (profit > maxProfit) maxProfit = profit
    }
    return maxProfit
}
```
**Time:** O(N) | **Space:** O(1)

---

### Best Time to Buy & Sell Stock II
**LeetCode #122** | Pattern: Greedy | 🟡 Medium

**Key Insight:** Unlimited transactions → sum ALL positive daily differences. A continuous rise = sum of daily rises.

**Approach:**
- Peak-Valley: O(N) — find local minima to buy, local maxima to sell
- Sum Positive Diffs: O(N) — if `prices[i+1] > prices[i]`, add the difference

**Code (Optimal):**
```kotlin
fun maxProfit(prices: IntArray): Int {
    var profit = 0
    for (i in 0 until prices.size - 1) {
        if (prices[i + 1] > prices[i]) profit += prices[i + 1] - prices[i]
    }
    return profit
}
```
**Time:** O(N) | **Space:** O(1)

---

### Jump Game
**LeetCode #55** | Pattern: Greedy | 🟡 Medium

**Key Insight:** Track `farthest` reachable index. If `i > farthest` → can't reach. If `farthest >= last index` → true.

**Approach:**
- DP: O(N²) — dp[i] = can reach index i
- Greedy: O(N) — track farthest reachable index

**Code (Optimal):**
```kotlin
fun canJump(nums: IntArray): Boolean {
    var farthest = 0
    for (i in nums.indices) {
        if (i > farthest) return false
        farthest = maxOf(farthest, i + nums[i])
        if (farthest >= nums.size - 1) return true
    }
    return true
}
```
**Time:** O(N) | **Space:** O(1)

---

### Gas Station
**LeetCode #134** | Pattern: Greedy | 🟡 Medium

**Key Insight:** If `totalGas >= totalCost`, solution exists. If `currentTank < 0` at station i, skip ALL stations from start to i — start fresh at `i+1`.

**Approach:**
- Brute Force: O(N²) — try each station as start, simulate circuit
- Greedy: O(N) — one pass, track totalTank + currentTank + startStation

**Code (Optimal):**
```kotlin
fun canCompleteCircuit(gas: IntArray, cost: IntArray): Int {
    var totalTank = 0; var currentTank = 0; var startStation = 0
    for (i in gas.indices) {
        totalTank += gas[i] - cost[i]
        currentTank += gas[i] - cost[i]
        if (currentTank < 0) { startStation = i + 1; currentTank = 0 }
    }
    return if (totalTank >= 0) startStation else -1
}
```
**Time:** O(N) | **Space:** O(1)

---

### Assign Cookies
**LeetCode #455** | Pattern: Greedy | 🟢 Easy

**Key Insight:** Sort both arrays. Assign smallest sufficient cookie to least greedy child first → maximizes content children.

**Approach:**
- Brute Force: O(N × M) — for each child, scan cookies
- Two Pointers: O(N log N + M log M) — sort both, greedy match

**Code (Optimal):**
```kotlin
fun findContentChildren(g: IntArray, s: IntArray): Int {
    g.sort(); s.sort()
    var child = 0; var cookie = 0
    while (child < g.size && cookie < s.size) {
        if (s[cookie] >= g[child]) child++
        cookie++
    }
    return child
}
```
**Time:** O(N log N + M log M) | **Space:** O(1)

---

### Candy
**LeetCode #135** | Pattern: Greedy (Two-Pass) | 🔴 Hard

**Key Insight:** Two passes — L→R ensures higher rating than left neighbor gets more, R→L ensures higher than right neighbor. Take max of both passes.

**Approach:**
- Brute Force: O(N²) — repeatedly fix violations until stable
- Two-Pass Greedy: O(N) — L→R pass, then R→L pass, take max

**Code (Optimal):**
```kotlin
fun candy(ratings: IntArray): Int {
    val n = ratings.size
    val candies = IntArray(n) { 1 }
    // Pass 1: Left → Right
    for (i in 1 until n) {
        if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1
    }
    // Pass 2: Right → Left
    for (i in n - 2 downTo 0) {
        if (ratings[i] > ratings[i + 1])
            candies[i] = maxOf(candies[i], candies[i + 1] + 1)
    }
    return candies.sum()
}
```
**Time:** O(N) | **Space:** O(N)

---

### Insert Interval
**LeetCode #57** | Pattern: Greedy (Intervals) | 🟡 Medium

**Key Insight:** Three phases — (1) add all intervals ending before newInterval, (2) merge all overlapping intervals, (3) add remaining.

**Approach:**
- Brute Force: O(N log N) — add newInterval, sort all, merge
- Three-Phase: O(N) — single pass, no sorting needed (already sorted)

**Code (Optimal):**
```kotlin
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
    val result = mutableListOf<IntArray>()
    var i = 0; val n = intervals.size
    // Phase 1: Add before
    while (i < n && intervals[i][1] < newInterval[0]) { result.add(intervals[i]); i++ }
    // Phase 2: Merge overlapping
    while (i < n && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = minOf(newInterval[0], intervals[i][0])
        newInterval[1] = maxOf(newInterval[1], intervals[i][1])
        i++
    }
    result.add(newInterval)
    // Phase 3: Add remaining
    while (i < n) { result.add(intervals[i]); i++ }
    return result.toTypedArray()
}
```
**Time:** O(N) | **Space:** O(N)

---

### Divide Intervals Into Minimum Number of Groups
**LeetCode #2406** | Pattern: Greedy (Intervals) | 🟡 Medium

**Key Insight:** Same as Meeting Rooms II. Max overlap at any time = min groups needed. Use sweep line (counting) or min-heap.

**Approach:**
- Sweep Line: O(N + maxTime) — count starts/ends at each time, track max overlap
- Min-Heap: O(N log N) — sort by start, heap tracks end times, reuse ended groups

**Code (Min-Heap):**
```kotlin
fun minGroupsHeap(intervals: Array<IntArray>): Int {
    intervals.sortBy { it[0] }
    val minHeap = java.util.PriorityQueue<Int>()
    for (interval in intervals) {
        if (minHeap.isNotEmpty() && minHeap.peek() < interval[0]) minHeap.poll()
        minHeap.offer(interval[1])
    }
    return minHeap.size
}
```
**Time:** O(N log N) | **Space:** O(N)

---

## 4. Frequency Count

### Count the Number of Pairs
**LeetCode #2006** | Pattern: Frequency Count | 🟡 Medium

**Key Insight:** For each element, check if `num + k` or `num - k` exists in the frequency map. Count += frequency of the complement.

**Approach:**
- Brute Force: O(N²) — check every pair
- HashMap: O(N) — one pass, look up `num + k` and `num - k` in map

**Code (Optimal):**
```kotlin
fun countNumberOfPairs(nums: IntArray, k: Int): Int {
    var count = 0
    val numMap = mutableMapOf<Int, Int>()
    for (num in nums) {
        count += numMap.getOrDefault(num + k, 0)
        count += numMap.getOrDefault(num - k, 0)
        numMap[num] = numMap.getOrDefault(num, 0) + 1
    }
    return count
}
```
**Time:** O(N) | **Space:** O(N)

---

### Finding Fair Pairs
**LeetCode #2563** | Pattern: Frequency Count / Two Pointer | 🟡 Medium

**Key Insight:** Sort + two pointers. Count pairs with sum ≤ upper, subtract pairs with sum < lower. `countLessEqual(target)` uses two pointers: if `nums[left] + nums[right] <= target`, all pairs (left, left+1..right) are valid.

**Approach:**
- Brute Force: O(N²) — check all pairs
- Sort + Two Pointers: O(N log N) — `countLessEqual(upper) - countLessEqual(lower - 1)`

**Code (Optimal):**
```kotlin
fun findingFairPairs(nums: IntArray, lower: Int, upper: Int): Long {
    nums.sort()
    return countLessEqual(nums, upper) - countLessEqual(nums, lower - 1)
}

private fun countLessEqual(nums: IntArray, target: Int): Long {
    var count = 0L; var left = 0; var right = nums.size - 1
    while (left < right) {
        if (nums[left] + nums[right] <= target) {
            count += (right - left); left++
        } else { right-- }
    }
    return count
}
```
**Time:** O(N log N) | **Space:** O(1)

---

### Top K Frequent Elements
**LeetCode #347** | Pattern: Frequency Count / Bucket Sort | 🟡 Medium

**Key Insight:** Bucket sort — index = frequency, value = list of numbers. Traverse from highest bucket. No sorting needed!

**Approach:**
- Brute Force: O(N²) — count frequency for each unique element
- HashMap + Sort: O(N + U log U) — build freq map, sort by frequency
- Bucket Sort: O(N) — bucket by frequency, traverse from highest

**Code (Optimal):**
```kotlin
fun topKFrequent(nums: IntArray, k: Int): IntArray {
    val freq = hashMapOf<Int, Int>()
    for (num in nums) freq[num] = freq.getOrDefault(num, 0) + 1
    val buckets = Array(nums.size + 1) { mutableListOf<Int>() }
    for ((num, count) in freq) buckets[count].add(num)
    val result = mutableListOf<Int>()
    for (i in buckets.size - 1 downTo 0) {
        for (num in buckets[i]) {
            result.add(num)
            if (result.size == k) return result.toIntArray()
        }
    }
    return result.toIntArray()
}
```
**Time:** O(N) | **Space:** O(N)

---

## 5. Binary Search

### Search in Rotated Sorted Array
**LeetCode #33** | Pattern: Binary Search | 🟡 Medium

**Key Insight:** At any mid, one half is ALWAYS sorted. Find which half is sorted, check if target is in it. Eliminate half each iteration.

**Approach:**
- Linear Scan: O(N) — scan entire array
- Binary Search: O(log N) — find sorted half, check if target is in range

**Code (Optimal):**
```kotlin
fun searchRotated(nums: IntArray, target: Int): Int {
    var left = 0; var right = nums.size - 1
    while (left <= right) {
        val mid = left + (right - left) / 2
        if (nums[mid] == target) return mid
        if (nums[left] <= nums[mid]) {  // Left half sorted
            if (target >= nums[left] && target < nums[mid]) right = mid - 1
            else left = mid + 1
        } else {  // Right half sorted
            if (target > nums[mid] && target <= nums[right]) left = mid + 1
            else right = mid - 1
        }
    }
    return -1
}
```
**Time:** O(log N) | **Space:** O(1)

---

### Find Minimum in Rotated Sorted Array
**LeetCode #153** | Pattern: Binary Search | 🟡 Medium

**Key Insight:** Compare `nums[mid]` with `nums[right]`. If `nums[mid] > nums[right]` → minimum is in right half. Else → minimum is at mid or in left half.

**Approach:**
- Linear Scan: O(N) — scan for minimum
- Binary Search: O(log N) — compare mid with right to find unsorted half

**Code (Optimal):**
```kotlin
fun findMin(nums: IntArray): Int {
    var left = 0; var right = nums.size - 1
    while (left < right) {
        val mid = left + (right - left) / 2
        if (nums[mid] > nums[right]) left = mid + 1  // min in right half
        else right = mid  // min at mid or in left half
    }
    return nums[left]
}
```
**Time:** O(log N) | **Space:** O(1)

---

### Median of Two Sorted Arrays
**LeetCode #4** | Pattern: Binary Search (Partition) | 🔴 Hard

**Key Insight:** Binary search on the smaller array to find the correct partition. `partitionY = (m + n + 1) / 2 - partitionX`. Valid when `maxLeftX ≤ minRightY && maxLeftY ≤ minRightX`.

**Approach:**
- Merge + Sort: O((m+n) log(m+n)) — combine, sort, find middle
- Binary Search Partition: O(log(min(m,n))) — partition both arrays so left halves = right halves

**Code (Optimal):**
```kotlin
fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double {
    val (a, b) = if (nums1.size <= nums2.size) nums1 to nums2 else nums2 to nums1
    val m = a.size; val n = b.size
    var low = 0; var high = m
    while (low <= high) {
        val px = (low + high) / 2
        val py = (m + n + 1) / 2 - px
        val maxLeftX = if (px == 0) Int.MIN_VALUE else a[px - 1]
        val minRightX = if (px == m) Int.MAX_VALUE else a[px]
        val maxLeftY = if (py == 0) Int.MIN_VALUE else b[py - 1]
        val minRightY = if (py == n) Int.MAX_VALUE else b[py]
        if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
            return if ((m + n) % 2 == 0)
                (maxOf(maxLeftX, maxLeftY) + minOf(minRightX, minRightY)) / 2.0
            else maxOf(maxLeftX, maxLeftY).toDouble()
        } else if (maxLeftX > minRightY) high = px - 1
        else low = px + 1
    }
    throw IllegalArgumentException("Not sorted")
}
```
**Time:** O(log(min(m,n))) | **Space:** O(1)

---

## 6. Bit Manipulation

### Single Number
**LeetCode #136** | Pattern: Bit Manipulation (XOR) | 🟢 Easy

**Key Insight:** XOR all numbers. `a ^ a = 0`, `a ^ 0 = a`. Pairs cancel out, leaving the single number.

**Approach:**
- Brute Force: O(N²) — count occurrences for each element
- HashSet: O(N) — add on first seen, remove on second, last remaining is answer
- XOR: O(N) O(1) — XOR all, pairs cancel

**Code (Optimal):**
```kotlin
fun singleNumber(nums: IntArray): Int {
    var result = 0
    for (num in nums) result = result xor num
    return result
}
```
**Time:** O(N) | **Space:** O(1)

---

### Single Number II
**LeetCode #137** | Pattern: Bit Manipulation | 🟡 Medium

**Key Insight:** Every element appears 3 times except one. Use two variables `ones` and `twos` to track bits appearing 1 and 2 times (mod 3). After 3 appearances, bits reset. The single number stays in `ones`.

**Approach:**
- Bit Counting: O(32N) — for each of 32 bits, count set bits. If `count % 3 != 0`, that bit is in the answer
- Two Variables: O(N) O(1) — `ones = (ones ^ num) & ~twos`, `twos = (twos ^ num) & ~ones`

**Code (Optimal):**
```kotlin
fun singleNumberII(nums: IntArray): Int {
    var ones = 0; var twos = 0
    for (num in nums) {
        ones = (ones xor num) and twos.inv()
        twos = (twos xor num) and ones.inv()
    }
    return ones
}
```
**Time:** O(N) | **Space:** O(1)

---

## 7. Linear Scan

### Find Smallest & Largest
**Pattern: Linear Scan | 🟢 Easy**

**Key Insight:** Single pass, track `smallest` and `largest` variables. Update at each element.

**Code:**
```kotlin
fun findSmallestOrLargest(value: Array<Int>) {
    var smallest = value[0]; var largest = value[0]
    for (i in 1 until value.size) {
        if (value[i] < smallest) smallest = value[i]
        if (value[i] > largest) largest = value[i]
    }
}
```
**Time:** O(N) | **Space:** O(1)

---

### Plus One
**LeetCode #66** | Pattern: Linear Scan | 🟢 Easy

**Key Insight:** Start from the rightmost digit, add carry=1. If digit becomes 10, set to 0 and carry continues. If carry remains after all digits, prepend 1.

**Approach:**
- String Conversion: O(N) — convert to BigInteger, add 1, convert back
- Digit-by-Digit: O(N) — right to left with carry, early return when carry=0

**Code (Optimal):**
```kotlin
fun plusOne(digits: IntArray): IntArray {
    var carry = 1
    for (i in digits.lastIndex downTo 0) {
        val sum = digits[i] + carry
        digits[i] = sum % 10
        carry = sum / 10
        if (carry == 0) return digits
    }
    val result = IntArray(digits.size + 1)
    result[0] = 1  // carry, rest are 0
    return result
}
```
**Time:** O(N) | **Space:** O(N) worst case (all 9s)

---

## 8. Kadane's Algorithm

### Maximum Subarray
**LeetCode #53** | Pattern: Kadane's Algorithm | 🟡 Medium

**Key Insight:** At each index, decide: extend previous subarray or start fresh? `localMax = max(nums[i], localMax + nums[i])`. If running sum is negative, it hurts future subarrays → start fresh.

**Approach:**
- Brute Force: O(N²) — try every subarray
- Kadane's: O(N) — one pass, track localMax + globalMax

**Code (Optimal):**
```kotlin
fun maxSubArray(nums: IntArray): Int {
    var localMax = nums[0]; var globalMax = nums[0]
    for (i in 1 until nums.size) {
        localMax = maxOf(nums[i], localMax + nums[i])
        globalMax = maxOf(globalMax, localMax)
    }
    return globalMax
}
```
**Time:** O(N) | **Space:** O(1)

---

## 9. HashSet Lookup

### Contains Duplicates
**LeetCode #217** | Pattern: HashSet Lookup | 🟢 Easy

**Key Insight:** Add each element to HashSet. If already exists → duplicate found.

**Approach:**
- Brute Force: O(N²) — check every pair
- Sort: O(N log N) — check adjacent elements after sort
- HashSet: O(N) — one pass, O(1) lookup

**Code (Optimal):**
```kotlin
fun containsDuplicate(nums: IntArray): Boolean {
    val seen = mutableSetOf<Int>()
    for (num in nums) {
        if (num in seen) return true
        seen.add(num)
    }
    return false
}
```
**Time:** O(N) | **Space:** O(N)

---

### Longest Consecutive Sequence
**LeetCode #128** | Pattern: HashSet Lookup | 🟡 Medium

**Key Insight:** Only START counting from sequence beginnings — a number is a start if `num - 1` is NOT in the set. This ensures each sequence is counted exactly once in O(N).

**Approach:**
- Brute Force: O(N³) — for each num, linear search for num+1, num+2...
- Sort: O(N log N) — sort, scan for consecutive, skip duplicates
- HashSet: O(N) — only count from sequence starts (num-1 not in set)

**Code (Optimal):**
```kotlin
fun longestConsecutive(nums: IntArray): Int {
    val set = nums.toSet()
    var maxLen = 0
    for (num in set) {
        if (num - 1 !in set) {  // only start from sequence beginning
            var current = num; var len = 1
            while (current + 1 in set) { current++; len++ }
            maxLen = maxOf(maxLen, len)
        }
    }
    return maxLen
}
```
**Time:** O(N) | **Space:** O(N)

---

## 10. Set Operations

### Intersection of Two Arrays
**LeetCode #349** | Pattern: Set Operations | 🟢 Easy

**Key Insight:** Add all nums1 to a HashSet. For each num in nums2, if it's in the set, add to result and remove from set (to ensure uniqueness).

**Approach:**
- Brute Force: O(N × M) — nested loops with result set
- HashSet: O(N + M) — add nums1 to set, check nums2 against set

**Code (Optimal):**
```kotlin
fun intersection(nums1: IntArray, nums2: IntArray): IntArray {
    val set = hashSetOf<Int>()
    for (n in nums1) set.add(n)
    val result = mutableListOf<Int>()
    for (num in nums2) {
        if (set.remove(num)) result.add(num)  // remove ensures uniqueness
    }
    return result.toIntArray()
}
```
**Time:** O(N + M) | **Space:** O(N)

---

## 11. String Parsing

### String to Integer (atoi)
**LeetCode #8** | Pattern: String Parsing | 🟡 Medium

**Key Insight:** Trim whitespace, handle optional sign, parse digits until non-digit. Clamp to Int range on overflow.

**Approach:**
- Trim → check sign → parse digits → handle overflow

**Code:**
```kotlin
fun myAtoi(s: String): Int {
    val trimmed = s.trim()
    val resultBuilder = StringBuilder()
    for (i in trimmed.indices) {
        if (Character.isDigit(trimmed[i]) || (trimmed[i] == '-' && i == 0) || (trimmed[i] == '+' && i == 0)) {
            resultBuilder.append(trimmed[i])
        } else break
    }
    val str = resultBuilder.toString()
    if (str.isEmpty() || str == "-" || str == "+") return 0
    return str.toLong().coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
}
```
**Time:** O(N) | **Space:** O(N)

---

## 12. Prefix Sum

### Product of Array Except Self
**LeetCode #238** | Pattern: Prefix Sum | 🟡 Medium

**Key Insight:** `result[i] = prefixProduct[i] × suffixProduct[i]`. Two passes: L→R builds prefix products, R→L multiplies by running suffix. No division needed!

**Approach:**
- Brute Force: O(N²) — for each element, multiply all others
- Division: O(N) — total product / each element (fails with zeros!)
- Prefix + Suffix: O(N) — two passes, O(1) extra space

**Code (Optimal):**
```kotlin
fun productExceptSelf(nums: IntArray): IntArray {
    val n = nums.size
    val result = IntArray(n)
    result[0] = 1
    for (i in 1 until n) result[i] = result[i - 1] * nums[i - 1]  // prefix
    var suffix = 1
    for (i in n - 1 downTo 0) {
        result[i] *= suffix  // multiply by running suffix
        suffix *= nums[i]
    }
    return result
}
```
**Time:** O(N) | **Space:** O(1) (output doesn't count)

---

### Subarray Sum Equals K
**LeetCode #560** | Pattern: Prefix Sum + HashMap | 🟡 Medium

**Key Insight:** If `prefix[j] - prefix[i] = k`, then subarray `[i+1..j]` sums to k. For each j, count how many previous prefix sums equal `prefix[j] - k`. HashMap stores `prefixSum → count`.

**Approach:**
- Brute Force: O(N²) — check every subarray
- Prefix Sum + HashMap: O(N) — one pass, count prefix sums seen so far

**Code (Optimal):**
```kotlin
fun subarraySum(nums: IntArray, k: Int): Int {
    val prefixCount = hashMapOf(0 to 1)  // prefix 0 occurs once
    var count = 0; var prefixSum = 0
    for (num in nums) {
        prefixSum += num
        count += prefixCount.getOrDefault(prefixSum - k, 0)
        prefixCount[prefixSum] = prefixCount.getOrDefault(prefixSum, 0) + 1
    }
    return count
}
```
**Time:** O(N) | **Space:** O(N)

---

## 13. Sliding Window

### Longest Substring Without Repeating Characters
**LeetCode #3** | Pattern: Sliding Window (Variable) | 🟡 Medium

**Key Insight:** Variable window with HashMap storing `char → lastSeenIndex`. When duplicate found AND its last index ≥ left, jump `left` to `lastSeen[char] + 1` (don't shrink one-by-one).

**Approach:**
- Brute Force: O(N²) — check every substring with a set
- Sliding Window: O(N) — HashMap + jump left pointer on duplicate

**Code (Optimal):**
```kotlin
fun lengthOfLongestSubstring(s: String): Int {
    val lastSeen = hashMapOf<Char, Int>()
    var left = 0; var maxLen = 0
    for (right in s.indices) {
        if (s[right] in lastSeen && lastSeen[s[right]]!! >= left) {
            left = lastSeen[s[right]]!! + 1  // jump past duplicate
        }
        lastSeen[s[right]] = right
        maxLen = maxOf(maxLen, right - left + 1)
    }
    return maxLen
}
```
**Time:** O(N) | **Space:** O(min(N, alphabet))

---

### Max Average Subarray
**LeetCode #643** | Pattern: Sliding Window (Fixed K) | 🟢 Easy

**Key Insight:** Fixed window of size k. Add new element on right, remove old element on left. Track max sum.

**Approach:**
- Brute Force: O(N × K) — calculate sum for every window
- Sliding Window: O(N) — maintain running sum, add right, remove left

**Code (Optimal):**
```kotlin
fun findMaxAverage(nums: IntArray, k: Int): Double {
    var sum = 0.0
    for (i in 0 until k) sum += nums[i]
    var maxAvg = sum / k
    for (i in k until nums.size) {
        sum += nums[i] - nums[i - k]  // add new, remove old
        maxAvg = maxOf(maxAvg, sum / k)
    }
    return maxAvg
}
```
**Time:** O(N) | **Space:** O(1)

---

### Minimum Window Substring
**LeetCode #76** | Pattern: Sliding Window (Variable) | 🔴 Hard

**Key Insight:** Expand right until window is valid (has all chars of t), then shrink left to minimize. Track `formed` (chars meeting required count) vs `required` (unique chars in t). THE hardest sliding window.

**Approach:**
- Brute Force: O(N² × M) — check every substring
- Sliding Window: O(N) — expand right, shrink left when valid, track min

**Code (Optimal):**
```kotlin
fun minWindow(s: String, t: String): String {
    if (s.length < t.length) return ""
    val need = t.groupingBy { it }.eachCount()
    val have = mutableMapOf<Char, Int>()
    var formed = 0; val required = need.size
    var left = 0; var minLen = Int.MAX_VALUE; var start = 0
    for (right in s.indices) {
        val ch = s[right]
        have[ch] = have.getOrDefault(ch, 0) + 1
        if (ch in need && have[ch] == need[ch]) formed++
        while (formed == required) {  // window is valid, try to shrink
            if (right - left + 1 < minLen) { minLen = right - left + 1; start = left }
            val leftChar = s[left]
            have[leftChar] = have[leftChar]!! - 1
            if (leftChar in need && have[leftChar]!! < need[leftChar]!!) formed--
            left++
        }
    }
    return if (minLen == Int.MAX_VALUE) "" else s.substring(start, start + minLen)
}
```
**Time:** O(N) | **Space:** O(K) where K = unique chars in t

---

## 14. Matrix

### Set Matrix Zeroes
**LeetCode #73** | Pattern: Matrix (In-Place) | 🟡 Medium

**Key Insight:** Use first row and first column as markers. Two extra booleans track if first row/col themselves need zeroing. Mark → Set → Zero first row/col last.

**Approach:**
- Extra Space: O(M + N) — separate row/col marker arrays
- In-Place: O(1) — use first row/col as markers + 2 booleans

**Code (Optimal):**
```kotlin
fun setZeroes(matrix: Array<IntArray>) {
    val rows = matrix.size; val cols = matrix[0].size
    var firstRowZero = false; var firstColZero = false
    for (j in 0 until cols) if (matrix[0][j] == 0) firstRowZero = true
    for (i in 0 until rows) if (matrix[i][0] == 0) firstColZero = true
    // Mark using first row/col
    for (i in 1 until rows) for (j in 1 until cols) {
        if (matrix[i][j] == 0) { matrix[i][0] = 0; matrix[0][j] = 0 }
    }
    // Set zeros based on markers
    for (i in 1 until rows) for (j in 1 until cols) {
        if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0
    }
    if (firstRowZero) for (j in 0 until cols) matrix[0][j] = 0
    if (firstColZero) for (i in 0 until rows) matrix[i][0] = 0
}
```
**Time:** O(M × N) | **Space:** O(1)

---

## 15. Two Pointer In-Place

### Remove Duplicates from Sorted Array
**LeetCode #26** | Pattern: Two Pointer In-Place | 🟢 Easy

**Key Insight:** Slow pointer tracks position for next unique element. Fast pointer scans ahead. When a new unique value is found, write it at slow pointer position.

**Approach:**
- Extra Array: O(N) — store unique values in new array
- Set: O(N log N) — add to set, return size
- Two Pointer: O(N) — slow/fast overwrite in-place

**Code (Optimal):**
```kotlin
fun removeDuplicates(nums: Array<Int>): Int {
    if (nums.isEmpty()) return 0
    var count = 1  // first element is always unique
    for (i in 1 until nums.size) {
        if (nums[i] != nums[count - 1]) {
            nums[count] = nums[i]
            count++
        }
    }
    return count
}
```
**Time:** O(N) | **Space:** O(1)

---

## 16. Voting & Floyd's

### Find Duplicate Number
**LeetCode #287** | Pattern: Floyd's Cycle Detection | 🟡 Medium

**Key Insight:** Treat array as linked list (index → nums[index]). Values in [1,n] with n+1 elements → cycle exists. Floyd's: Phase 1 finds meeting point (slow/fast), Phase 2 finds cycle entrance (the duplicate).

**Approach:**
- Brute Force: O(N²) — compare all pairs
- Sort: O(N log N) — check adjacent (modifies array!)
- Floyd's: O(N) O(1) — cycle detection, no modification

**Code (Optimal):**
```kotlin
fun findDuplicate(nums: IntArray): Int {
    var slow = nums[0]; var fast = nums[0]
    // Phase 1: Find meeting point
    do { slow = nums[slow]; fast = nums[nums[fast]] } while (slow != fast)
    // Phase 2: Find cycle entrance
    slow = nums[0]
    while (slow != fast) { slow = nums[slow]; fast = nums[fast] }
    return slow
}
```
**Time:** O(N) | **Space:** O(1)

---

## 17. Stack — Basic

### Valid Parentheses
**LeetCode #20** | Pattern: Stack (HashMap) | 🟢 Easy

**Key Insight:** Map open→close brackets. Push open brackets. On close, pop and check match. Stack must be empty at end.

**Code (Optimal):**
```kotlin
fun isValid(s: String): Boolean {
    val map = mapOf('(' to ')', '{' to '}', '[' to ']')
    val stack = java.util.Stack<Char>()
    for (c in s) {
        if (c in map) stack.push(c)
        else if (stack.isEmpty() || map[stack.pop()] != c) return false
    }
    return stack.isEmpty()
}
```
**Time:** O(N) | **Space:** O(N)

---

### Min Stack
**LeetCode #155** | Pattern: Stack (Auxiliary) | 🟡 Medium

**Key Insight:** Store (value, currentMin) pairs. Each entry remembers the minimum at the time it was pushed.

**Code (Optimal):**
```kotlin
class MinStack {
    val stack = ArrayDeque<Pair<Int, Int>>()
    fun push(v: Int) { stack.addLast(v to if (stack.isEmpty()) v else minOf(v, stack.last().second)) }
    fun pop() { stack.removeLast() }
    fun top() = stack.last().first
    fun getMin() = stack.last().second
}
```
**Time:** O(1) all ops | **Space:** O(N)

---

### Evaluate Reverse Polish Notation
**LeetCode #150** | Pattern: Stack (Postfix) | 🟡 Medium

**Key Insight:** Push numbers. On operator, pop two operands (b first, then a), compute `a op b`, push result.

**Code (Optimal):**
```kotlin
fun evalRPN(tokens: Array<String>): Int {
    val stack = ArrayDeque<Int>()
    for (t in tokens) {
        if (t in listOf("+", "-", "*", "/")) {
            val b = stack.removeLast(); val a = stack.removeLast()
            stack.addLast(when (t) { "+" -> a + b; "-" -> a - b; "*" -> a * b; else -> a / b })
        } else stack.addLast(t.toInt())
    }
    return stack.last()
}
```
**Time:** O(N) | **Space:** O(N)

---

## 18. Monotonic Stack

### Daily Temperatures
**LeetCode #739** | Pattern: Monotonic Decreasing Stack (indices) | 🟡 Medium

**Key Insight:** Stack stores indices waiting for warmer day. When current temp > stack top's temp → pop, result = current - popped index.

**Code (Optimal):**
```kotlin
fun dailyTemperatures(temps: IntArray): IntArray {
    val result = IntArray(temps.size)
    val stack = ArrayDeque<Int>()
    for (i in temps.indices) {
        while (stack.isNotEmpty() && temps[i] > temps[stack.last()]) {
            val prev = stack.removeLast(); result[prev] = i - prev
        }
        stack.addLast(i)
    }
    return result
}
```
**Time:** O(N) | **Space:** O(N)

---

### Next Greater Element I
**LeetCode #496** | Pattern: Monotonic Stack + HashMap | 🟢 Easy

**Key Insight:** Process nums2 once with stack → build map (element → next greater). Lookup nums1 values in map.

**Code (Optimal):**
```kotlin
fun nextGreaterElement(nums1: IntArray, nums2: IntArray): IntArray {
    val nextGreater = hashMapOf<Int, Int>()
    val stack = ArrayDeque<Int>()
    for (num in nums2) {
        while (stack.isNotEmpty() && num > stack.last()) nextGreater[stack.removeLast()] = num
        stack.addLast(num)
    }
    return nums1.map { nextGreater.getOrDefault(it, -1) }.toIntArray()
}
```
**Time:** O(N) | **Space:** O(N)

---

### Next Greater Element II
**LeetCode #503** | Pattern: Monotonic Stack (Circular) | 🟡 Medium

**Key Insight:** Iterate 2×N using modulo. Only push indices during first pass (i < N).

**Code (Optimal):**
```kotlin
fun nextGreaterElements(nums: IntArray): IntArray {
    val n = nums.size; val result = IntArray(n) { -1 }
    val stack = ArrayDeque<Int>()
    for (i in 0 until 2 * n) {
        val idx = i % n
        while (stack.isNotEmpty() && nums[idx] > nums[stack.last()]) result[stack.removeLast()] = nums[idx]
        if (i < n) stack.addLast(idx)
    }
    return result
}
```
**Time:** O(N) | **Space:** O(N)

---

### Online Stock Span
**LeetCode #901** | Pattern: Monotonic Stack (price, span pairs) | 🟡 Medium

**Key Insight:** When new price ≥ stack top's price → pop and accumulate span. Each popped element's span is absorbed.

**Code (Optimal):**
```kotlin
class StockSpanner {
    val stack = ArrayDeque<Pair<Int, Int>>()  // (price, span)
    fun next(price: Int): Int {
        var span = 1
        while (stack.isNotEmpty() && stack.last().first <= price) span += stack.removeLast().second
        stack.addLast(price to span)
        return span
    }
}
```
**Time:** O(1) amortized | **Space:** O(N)

---

## 19. Stack — Parentheses

### Minimum Remove to Make Valid Parentheses
**LeetCode #1249** | Pattern: Stack with Index Marking | 🟡 Medium

**Key Insight:** Push '(' indices. On ')': if stack not empty → pop (matched), else mark for removal. Remaining '(' in stack → mark for removal.

**Code (Optimal):**
```kotlin
fun minRemoveToMakeValid(s: String): String {
    val stack = ArrayDeque<Int>(); val toRemove = mutableSetOf<Int>()
    for (i in s.indices) {
        when (s[i]) {
            '(' -> stack.addLast(i)
            ')' -> if (stack.isNotEmpty()) stack.removeLast() else toRemove.add(i)
        }
    }
    toRemove.addAll(stack)
    return s.filterIndexed { i, _ -> i !in toRemove }
}
```
**Time:** O(N) | **Space:** O(N)

---

### Longest Valid Parentheses
**LeetCode #32** | Pattern: Stack with Index (boundary) | 🔴 Hard

**Key Insight:** Push -1 as base. On '(' push index. On ')' pop. If stack empty → push current index (new boundary). Else length = i - stack.top().

**Code (Optimal):**
```kotlin
fun longestValidParentheses(s: String): Int {
    val stack = java.util.Stack<Int>(); stack.push(-1); var maxLen = 0
    for (i in s.indices) {
        if (s[i] == '(') stack.push(i)
        else { stack.pop(); if (stack.isEmpty()) stack.push(i) else maxLen = maxOf(maxLen, i - stack.peek()) }
    }
    return maxLen
}
```
**Time:** O(N) | **Space:** O(N)

---

## 20. Stack — Advanced

### Largest Rectangle in Histogram
**LeetCode #84** | Pattern: Monotonic Increasing Stack | 🔴 Hard

**Key Insight:** When a smaller height is found, pop and calculate area. Width = current_index - stack.top - 1 (or current_index if empty). Add sentinel 0 at end to flush.

**Code (Optimal):**
```kotlin
fun largestRectangleArea(heights: IntArray): Int {
    val stack = ArrayDeque<Int>(); var maxArea = 0
    val ext = heights + intArrayOf(0)  // sentinel
    for (i in ext.indices) {
        while (stack.isNotEmpty() && ext[i] < ext[stack.last()]) {
            val h = ext[stack.removeLast()]
            val w = if (stack.isEmpty()) i else i - stack.last() - 1
            maxArea = maxOf(maxArea, h * w)
        }
        stack.addLast(i)
    }
    return maxArea
}
```
**Time:** O(N) | **Space:** O(N)

---

### Car Fleet
**LeetCode #853** | Pattern: Monotonic Stack (Sort by position DESC) | 🟡 Medium

**Key Insight:** Time to target = (target - position) / speed. If current time ≤ top → merges. If > top → new fleet.

**Code (Optimal):**
```kotlin
fun carFleet(target: Int, position: IntArray, speed: IntArray): Int {
    val cars = position.indices.map { Pair(position[it], speed[it]) }.sortedByDescending { it.first }
    val stack = ArrayDeque<Double>()
    for ((pos, spd) in cars) {
        val time = (target - pos).toDouble() / spd
        if (stack.isEmpty() || time > stack.last()) stack.addLast(time)
    }
    return stack.size
}
```
**Time:** O(N log N) | **Space:** O(N)

---

## 21. Simulation — Basic

### Add Binary
**LeetCode #67** | Pattern: Simulation (Carry) | 🟢 Easy

**Key Insight:** Traverse right to left. sum = bit_a + bit_b + carry. result bit = sum % 2, carry = sum / 2. Reverse at end.

**Code (Optimal):**
```kotlin
fun addBinary(a: String, b: String): String {
    val sb = StringBuilder(); var i = a.length - 1; var j = b.length - 1; var carry = 0
    while (i >= 0 || j >= 0 || carry == 1) {
        val sum = carry + (if (i >= 0) a[i--] - '0' else 0) + (if (j >= 0) b[j--] - '0' else 0)
        sb.append(sum % 2); carry = sum / 2
    }
    return sb.reverse().toString()
}
```
**Time:** O(max(N, M)) | **Space:** O(max(N, M))

---

### Add Strings
**LeetCode #415** | Pattern: Simulation (Carry) | 🟢 Easy

**Key Insight:** Same as Add Binary but base 10. sum = d1 + d2 + carry. result digit = sum % 10, carry = sum / 10.

**Code (Optimal):**
```kotlin
fun addStrings(num1: String, num2: String): String {
    val sb = StringBuilder(); var i = num1.length - 1; var j = num2.length - 1; var carry = 0
    while (i >= 0 || j >= 0 || carry > 0) {
        val sum = carry + (if (i >= 0) num1[i--] - '0' else 0) + (if (j >= 0) num2[j--] - '0' else 0)
        sb.append(sum % 10); carry = sum / 10
    }
    return sb.reverse().toString()
}
```
**Time:** O(max(N, M)) | **Space:** O(max(N, M))

---

### FizzBuzz
**LeetCode #412** | Pattern: Simulation (Modulo) | 🟢 Easy

**Key Insight:** Check divisible by 3 AND 5 first (FizzBuzz), then 3 (Fizz), then 5 (Buzz), else the number.

**Code (Optimal):**
```kotlin
fun fizzBuzz(n: Int): List<String> {
    return (1..n).map { i ->
        when { i % 15 == 0 -> "FizzBuzz"; i % 3 == 0 -> "Fizz"; i % 5 == 0 -> "Buzz"; else -> "$i" }
    }
}
```
**Time:** O(N) | **Space:** O(N)

---

### Add Digits
**LeetCode #258** | Pattern: Simulation (Digital Root) | 🟢 Easy

**Key Insight:** O(1) trick: if num == 0 → 0, if num % 9 == 0 → 9, else num % 9.

**Code (Optimal):**
```kotlin
fun addDigits(num: Int): Int = when { num == 0 -> 0; num % 9 == 0 -> 9; else -> num % 9 }
```
**Time:** O(1) | **Space:** O(1)

---

## 22. Simulation — Matrix Traversal

### Spiral Matrix
**LeetCode #54** | Pattern: Simulation (Four Boundaries) | 🟡 Medium

**Key Insight:** Use top, bottom, left, right boundaries. Traverse: right → down → left → up. Shrink boundaries after each direction.

**Code (Optimal):**
```kotlin
fun spiralOrder(matrix: Array<IntArray>): List<Int> {
    val result = mutableListOf<Int>()
    var top = 0; var bottom = matrix.size - 1; var left = 0; var right = matrix[0].size - 1
    while (top <= bottom && left <= right) {
        for (j in left..right) result.add(matrix[top][j]); top++
        for (i in top..bottom) result.add(matrix[i][right]); right--
        if (top <= bottom) { for (j in right downTo left) result.add(matrix[bottom][j]); bottom-- }
        if (left <= right) { for (i in bottom downTo top) result.add(matrix[i][left]); left++ }
    }
    return result
}
```
**Time:** O(M × N) | **Space:** O(1)

---

### Spiral Matrix II
**LeetCode #59** | Pattern: Simulation (Four Boundaries, Fill) | 🟡 Medium

**Key Insight:** Same as Spiral Matrix but fill instead of read. Counter from 1 to n².

**Code (Optimal):**
```kotlin
fun generateMatrix(n: Int): Array<IntArray> {
    val result = Array(n) { IntArray(n) }
    var top = 0; var bottom = n - 1; var left = 0; var right = n - 1; var count = 1
    while (top <= bottom && left <= right) {
        for (j in left..right) result[top][j] = count++; top++
        for (i in top..bottom) result[i][right] = count++; right--
        if (top <= bottom) { for (j in right downTo left) result[bottom][j] = count++; bottom-- }
        if (left <= right) { for (i in bottom downTo top) result[i][left] = count++; left++ }
    }
    return result
}
```
**Time:** O(N²) | **Space:** O(N²)

---

### Text Justification
**LeetCode #68** | Pattern: Simulation (Greedy Line Packing) | 🔴 Hard

**Key Insight:** Greedily pack words per line. Last line or single word → left justify. Otherwise distribute spaces evenly (extra spaces go left first).

**Code (Optimal):**
```kotlin
fun fullJustify(words: Array<String>, maxWidth: Int): List<String> {
    val result = mutableListOf<String>(); var i = 0
    while (i < words.size) {
        var lineLen = words[i].length; var j = i + 1
        while (j < words.size && lineLen + 1 + words[j].length <= maxWidth) { lineLen += 1 + words[j].length; j++ }
        val line = StringBuilder(); val count = j - i - 1
        if (j == words.size || count == 0) {
            for (k in i until j) { line.append(words[k]); if (k < j - 1) line.append(' ') }
            while (line.length < maxWidth) line.append(' ')
        } else {
            val spaces = (maxWidth - lineLen + count) / count
            val extra = (maxWidth - lineLen + count) % count
            for (k in i until j - 1) {
                line.append(words[k]); repeat(spaces + if (k - i < extra) 1 else 0) { line.append(' ') }
            }
            line.append(words[j - 1])
        }
        result.add(line.toString()); i = j
    }
    return result
}
```
**Time:** O(N × maxWidth) | **Space:** O(N)

---

## 23. Linked List — Basic

### Reverse Linked List
**LeetCode #206** | Pattern: Iterative Three-Pointer | 🟢 Easy

**Key Insight:** For each node: save next, point current.next to prev, move prev and current forward.

**Code (Optimal):**
```kotlin
fun reverseList(head: ListNode?): ListNode? {
    var prev: ListNode? = null; var curr = head
    while (curr != null) { val next = curr.next; curr.next = prev; prev = curr; curr = next }
    return prev
}
```
**Time:** O(N) | **Space:** O(1)

---

### Linked List Cycle
**LeetCode #141** | Pattern: Floyd's Tortoise & Hare | 🟢 Easy

**Key Insight:** Slow moves 1 step, fast moves 2 steps. If they meet → cycle. If fast reaches null → no cycle.

**Code (Optimal):**
```kotlin
fun hasCycle(head: ListNode?): Boolean {
    var slow = head; var fast = head
    while (fast != null && fast.next != null) { slow = slow?.next; fast = fast.next?.next; if (slow == fast) return true }
    return false
}
```
**Time:** O(N) | **Space:** O(1)

---

### Merge Two Sorted Lists
**LeetCode #21** | Pattern: Dummy Head + Two Pointer | 🟢 Easy

**Key Insight:** Use dummy node. Compare values, attach smaller to tail. Advance the chosen list. Append remaining.

**Code (Optimal):**
```kotlin
fun mergeTwoLists(a: ListNode?, b: ListNode?): ListNode? {
    val dummy = ListNode(0); var tail = dummy
    var l1 = a; var l2 = b
    while (l1 != null && l2 != null) {
        if (l1.`val` <= l2.`val`) { tail.next = l1; l1 = l1.next } else { tail.next = l2; l2 = l2.next }
        tail = tail.next!!
    }
    tail.next = l1 ?: l2
    return dummy.next
}
```
**Time:** O(N + M) | **Space:** O(1)

---

### Remove Nth Node from End
**LeetCode #19** | Pattern: Two Pointer (Gap of N) | 🟡 Medium

**Key Insight:** Use dummy to handle head removal. Move fast N+1 ahead. Then move both until fast reaches end. slow.next = slow.next.next.

**Code (Optimal):**
```kotlin
fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    val dummy = ListNode(0).apply { next = head }
    var fast: ListNode? = dummy; var slow: ListNode? = dummy
    repeat(n + 1) { fast = fast?.next }
    while (fast != null) { fast = fast?.next; slow = slow?.next }
    slow?.next = slow?.next?.next
    return dummy.next
}
```
**Time:** O(N) | **Space:** O(1)

---

### Middle of Linked List
**LeetCode #876** | Pattern: Fast/Slow Pointer | 🟢 Easy

**Key Insight:** Fast moves 2 steps, slow moves 1. When fast reaches end, slow is at middle.

**Code (Optimal):**
```kotlin
fun middleNode(head: ListNode?): ListNode? {
    var slow = head; var fast = head
    while (fast != null && fast.next != null) { slow = slow?.next; fast = fast.next?.next }
    return slow
}
```
**Time:** O(N) | **Space:** O(1)

---

### Palindrome Linked List
**LeetCode #234** | Pattern: Fast/Slow + Reverse Second Half | 🟡 Medium

**Key Insight:** Find middle, reverse second half, compare first half with reversed second half.

**Code (Optimal):**
```kotlin
fun isPalindrome(head: ListNode?): Boolean {
    var slow = head; var fast = head
    while (fast?.next != null) { slow = slow?.next; fast = fast.next?.next }
    var prev: ListNode? = null; var curr = slow
    while (curr != null) { val next = curr.next; curr.next = prev; prev = curr; curr = next }
    var left = head; var right = prev
    while (right != null) { if (left?.`val` != right.`val`) return false; left = left?.next; right = right.next }
    return true
}
```
**Time:** O(N) | **Space:** O(1)

---

## 24. HashSet Lookup (Review)

### Valid Sudoku
**LeetCode #36** | Pattern: HashSet Lookup (3 sets: rows, cols, boxes) | 🟡 Medium

**Key Insight:** Box index = (row / 3) * 3 + (col / 3). Check if digit already in row, col, or box set.

**Code (Optimal):**
```kotlin
fun isValidSudoku(board: Array<CharArray>): Boolean {
    val rows = Array(9) { mutableSetOf<Char>() }
    val cols = Array(9) { mutableSetOf<Char>() }
    val boxes = Array(9) { mutableSetOf<Char>() }
    for (i in 0 until 9) for (j in 0 until 9) {
        val c = board[i][j]; if (c == '.') continue
        val box = (i / 3) * 3 + j / 3
        if (c in rows[i] || c in cols[j] || c in boxes[box]) return false
        rows[i].add(c); cols[j].add(c); boxes[box].add(c)
    }
    return true
}
```
**Time:** O(81) | **Space:** O(81)

---

## 25. Two Pointer (Review + New)

### Valid Palindrome
**LeetCode #125** | Pattern: Two Pointer (skip non-alphanumeric) | 🟢 Easy

**Key Insight:** Compare from both ends, skipping non-alphanumeric characters. Lowercase both before comparing.

**Code (Optimal):**
```kotlin
fun isPalindrome(s: String): Boolean {
    var l = 0; var r = s.length - 1
    while (l < r) {
        while (l < r && !s[l].isLetterOrDigit()) l++
        while (l < r && !s[r].isLetterOrDigit()) r--
        if (s[l].lowercaseChar() != s[r].lowercaseChar()) return false
        l++; r--
    }
    return true
}
```
**Time:** O(N) | **Space:** O(1)

---

### Is Subsequence
**LeetCode #392** | Pattern: Two Pointer (single pass) | 🟢 Easy

**Key Insight:** Iterate through t. If s[sIndex] == t[tIndex] → sIndex++. If sIndex == s.length → true.

**Code (Optimal):**
```kotlin
fun isSubsequence(s: String, t: String): Boolean {
    var si = 0
    for (c in t) if (si < s.length && s[si] == c) si++
    return si == s.length
}
```
**Time:** O(T) | **Space:** O(1)

---

## 26. Greedy — Intervals (Review + New)

### Merge Intervals
**LeetCode #56** | Pattern: Greedy (Sort by Start + Single Pass) | 🟡 Medium

**Key Insight:** Sort by start. If current start ≤ last merged end → merge (extend end). Else → add new interval.

**Code (Optimal):**
```kotlin
fun merge(intervals: Array<IntArray>): Array<IntArray> {
    intervals.sortBy { it[0] }
    val result = mutableListOf(intervals[0])
    for (i in 1 until intervals.size) {
        if (intervals[i][0] <= result.last()[1]) result.last()[1] = maxOf(result.last()[1], intervals[i][1])
        else result.add(intervals[i])
    }
    return result.toTypedArray()
}
```
**Time:** O(N log N) | **Space:** O(N)

---

### Meeting Rooms
**LeetCode #252** | Pattern: Greedy (Sort by Start) | 🟡 Medium

**Key Insight:** Sort by start time. If any meeting starts before the previous one ends → conflict.

**Code (Optimal):**
```kotlin
fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
    intervals.sortBy { it[0] }
    for (i in 1 until intervals.size) if (intervals[i][0] < intervals[i - 1][1]) return false
    return true
}
```
**Time:** O(N log N) | **Space:** O(1)

---

### Non-Overlapping Intervals
**LeetCode #435** | Pattern: Greedy (Sort by End Time) | 🟡 Medium

**Key Insight:** Sort by end. If current start < lastEnd → overlap → remove (count++). Else → update lastEnd.

**Code (Optimal):**
```kotlin
fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
    intervals.sortBy { it[1] }
    var removals = 0; var lastEnd = intervals[0][1]
    for (i in 1 until intervals.size) {
        if (intervals[i][0] < lastEnd) removals++ else lastEnd = intervals[i][1]
    }
    return removals
}
```
**Time:** O(N log N) | **Space:** O(1)

---

## 27. Matrix (Review + New)

### Rotate Image
**LeetCode #48** | Pattern: Matrix (Transpose + Reverse Each Row) | 🟡 Medium

**Key Insight:** 90° clockwise = transpose (swap [i][j] with [j][i]) then reverse each row.

**Code (Optimal):**
```kotlin
fun rotate(matrix: Array<IntArray>) {
    val n = matrix.size
    for (i in 0 until n) for (j in i + 1 until n) { val tmp = matrix[i][j]; matrix[i][j] = matrix[j][i]; matrix[j][i] = tmp }
    for (row in matrix) row.reverse()
}
```
**Time:** O(N²) | **Space:** O(1)

---

### Game of Life
**LeetCode #289** | Pattern: Matrix (In-Place with Intermediate States) | 🟡 Medium

**Key Insight:** Use intermediate values: 0→1 = 2 (dead to live), 1→0 = 3 (live to dead). Read original state as 1 or 3.

**Code (Optimal):**
```kotlin
fun gameOfLife(board: Array<IntArray>) {
    val m = board.size; val n = board[0].size
    val dirs = arrayOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
    for (i in 0 until m) for (j in 0 until n) {
        var live = 0
        for ((dr, dc) in dirs) { val r = i + dr; val c = j + dc; if (r in 0 until m && c in 0 until n && board[r][c] in listOf(1, 3)) live++ }
        if (board[i][j] == 1 && (live < 2 || live > 3)) board[i][j] = 3
        else if (board[i][j] == 0 && live == 3) board[i][j] = 2
    }
    for (i in 0 until m) for (j in 0 until n) { if (board[i][j] == 2) board[i][j] = 1; else if (board[i][j] == 3) board[i][j] = 0 }
}
```
**Time:** O(M × N) | **Space:** O(1)

---

## 📊 Pattern Quick Reference

| Pattern | Core Idea | When to Use |
|---------|-----------|-------------|
| **Complement Search** | HashMap stores `target - current` | Pair sum problems, unsorted array |
| **Two Pointer** | Converge from both ends | Sorted array, pair/triplet sum |
| **Greedy** | Locally optimal → globally optimal | Intervals, jumps, buy/sell |
| **Frequency Count** | HashMap for occurrences, bucket sort | Count pairs, top K frequent |
| **Binary Search** | Find sorted half, eliminate half | Rotated arrays, partition problems |
| **Bit Manipulation** | XOR for unique, `n & (n-1)` for bits | Single number, power of two |
| **Linear Scan** | Single pass, track running values | Min/max, plus one, consecutive |
| **Kadane's** | Extend or start fresh? Track local/global max | Max subarray sum |
| **HashSet Lookup** | O(1) "have I seen this?" | Duplicates, existence checks |
| **Set Operations** | Intersection, difference, grouping | Array intersection, anagrams |
| **String Parsing** | Handle overflow, edge cases | atoi, roman numerals |
| **Prefix Sum** | Precompute cumulative sum | Range queries, subarray sums |
| **Sliding Window** | Expand right, shrink left | Substring/subarray with constraint |
| **Matrix** | In-place tricks (markers, transpose) | Set zeroes, rotate, spiral |
| **Two Pointer In-Place** | Slow/fast overwrite | Remove duplicates, move zeros |
| **Voting & Floyd's** | Boyer-Moore majority, cycle detection | Find duplicate, majority element |
| **Stack — Basic** | Push/pop, HashMap for matching | Valid parentheses, min stack, RPN |
| **Monotonic Stack** | Stack stores indices, pop on condition | Daily temps, next greater, stock span |
| **Stack — Parentheses** | Index marking, boundary tracking | Min remove, longest valid parens |
| **Stack — Advanced** | Monotonic increasing, sort + stack | Largest rectangle, car fleet |
| **Simulation — Basic** | Digit-by-digit with carry | Add binary, add strings, FizzBuzz |
| **Simulation — Matrix** | Four boundaries, greedy packing | Spiral matrix, text justification |
| **Linked List — Basic** | Three-pointer, fast/slow, dummy head | Reverse, cycle, merge, remove Nth |
| **Greedy — Intervals** | Sort by start/end, single pass merge | Merge intervals, meeting rooms, non-overlap |

---

## 🔁 Spaced Repetition Schedule

| Review Date | Problems to Re-solve |
|-------------|---------------------|
| **Tomorrow** | Subarray Sum K + Longest Substring + Gas Station + Median Two Sorted |
| **+3 days** | Two Sum + Two Sum II + Finding Pairs + Product Except Self |
| **+1 week** | Search Rotated + Find Min Rotated + Find Duplicate + Candy |
| **+1 week (Week 3-4)** | Valid Parentheses + Reverse Linked List + Merge Intervals + Spiral Matrix |
| **+2 weeks** | All 72 problems (timed: 5 min easy, 10 min medium, 15 min hard) |

---

## 🏷️ Difficulty Summary

| Difficulty | Count | Problems |
|------------|-------|----------|
| 🟢 Easy | 22 | Two Sum, Stock I, Assign Cookies, Single Number, Find Smallest/Largest, Plus One, Contains Duplicates, Intersection, Max Average, Remove Duplicates, Valid Parentheses, Next Greater I, Add Binary, Add Strings, FizzBuzz, Add Digits, Reverse Linked List, Linked List Cycle, Merge Two Sorted Lists, Middle of Linked List, Valid Palindrome, Is Subsequence |
| 🟡 Medium | 38 | Two Sum II, Finding Pairs, Stock II, Jump Game, Gas Station, Insert Interval, Interval Groups, Count Pairs, Fair Pairs, Top K Frequent, Search Rotated, Find Min Rotated, Single Number II, Max Subarray, String to Integer, Product Except Self, Subarray Sum K, Longest Substring, Set Matrix Zeroes, Find Duplicate, Three Sum, Longest Consecutive, Min Stack, Evaluate RPN, Daily Temps, Next Greater II, Online Stock Span, Min Remove Parens, Car Fleet, Spiral Matrix, Spiral Matrix II, Remove Nth, Palindrome Linked List, Valid Sudoku, Merge Intervals, Meeting Rooms, Non-Overlapping, Rotate Image, Game of Life |
| 🔴 Hard | 6 | Candy, Median Two Sorted, Minimum Window Substring, Longest Valid Parens, Largest Rectangle, Text Justification |

---

*Back to [DSA Study Guide](study.md) | [Daily DSA Schedule](../interview_schedule/Daily_DSA_Schedule.md) | [Pattern Index](PatternWiseProblems.md)*
