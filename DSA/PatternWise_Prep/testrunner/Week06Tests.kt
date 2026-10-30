package testrunner

/**
 * Week06Tests.kt — Week 6: Greedy + Binary Search + Bit Manipulation (28 problems)
 *
 * Part A — 28 problems × 3 cases from practice/Week06_Practice.md (runAll).
 * Part B — 10 timed stress cases (complexityAll, not called by the default main()).
 * Solution stubs sit at the bottom of the file; they throw TODO() so each case is
 * reported as [SKIP ⏭️] until you implement it, then it runs for real.
 *
 * TestRunner.check() non-null-asserts the block result, so a test never returns a
 * nullable directly. Names differ from the week doc only where the doc reuses one
 * name twice: maxProfitII (#8) and singleNumberII (#21); searchRotated = doc's `search`.
 */

class Week06Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 6 — Greedy + Binary Search + Bit ━━━━━━━")
        maxProfitI(); findContentChildren(); lemonadeChange(); singleNumberXor()
        hammingWeight(); missingNumber(); powerOfTwo(); maxProfitII(); canJump()
        jump(); canCompleteCircuit(); mergeIntervals(); insertInterval()
        canAttendMeetings(); eraseOverlapIntervals(); findMinArrowShots()
        leastInterval(); partitionLabels(); searchRotated(); findMinRotated()
        singleNumberII(); getSum(); countBits(); reconstructQueue()
        wiggleMaxLength(); candy(); medianTwoSorted(); majorityElement()
        // Complexity validation (Part B) — run with: complexityAll()
        // canJumpBig(); jumpBig(); gasStationBig(); mergeIntervalsBig(); searchRotatedBig()
        // findMinRotatedBig(); medianTwoArraysBig(); taskSchedulerBig(); candyBig(); countBitsBig()
    }

    // ── Part A — practice-sheet cases ───────────────────────────────────────

    // 1) Best Time to Buy & Sell Stock I: min price so far, then max diff.
    fun maxProfitI() {
        TestRunner.check("W6 #1a Stock I classic", block = { maxProfit(intArrayOf(7, 1, 5, 3, 6, 4)) }, verify = { it == 5 })
        TestRunner.check("W6 #1b Stock I decreasing", block = { maxProfit(intArrayOf(7, 6, 4, 3, 1)) }, verify = { it == 0 })
        TestRunner.check("W6 #1c Stock I buy later", block = { maxProfit(intArrayOf(2, 4, 1)) }, verify = { it == 2 })
    }

    // 2) Assign Cookies: sort both, give the smallest fitting cookie first.
    fun findContentChildren() {
        TestRunner.check("W6 #2a Cookies classic", block = { findContentChildren(intArrayOf(1, 2, 3), intArrayOf(1, 1)) }, verify = { it == 1 })
        TestRunner.check("W6 #2b Cookies enough", block = { findContentChildren(intArrayOf(1, 2), intArrayOf(1, 2, 3)) }, verify = { it == 2 })
        TestRunner.check("W6 #2c Cookies no children", block = { findContentChildren(intArrayOf(), intArrayOf(1)) }, verify = { it == 0 })
    }

    // 3) Lemonade Change: keep $5/$10 counts, hand back the big bill first.
    fun lemonadeChange() {
        TestRunner.check("W6 #3a Lemonade classic", block = { lemonadeChange(intArrayOf(5, 5, 5, 10, 20)) }, verify = { it })
        TestRunner.check("W6 #3b Lemonade cannot make 15", block = { lemonadeChange(intArrayOf(5, 5, 10, 10, 20)) }, verify = { !it })
        TestRunner.check("W6 #3c Lemonade no fives", block = { lemonadeChange(intArrayOf(10, 10)) }, verify = { !it })
    }

    // 4) Single Number: XOR everything, pairs cancel out.
    fun singleNumberXor() {
        TestRunner.check("W6 #4a Single number classic", block = { singleNumber(intArrayOf(2, 2, 1)) }, verify = { it == 1 })
        TestRunner.check("W6 #4b Single number middle", block = { singleNumber(intArrayOf(4, 1, 2, 1, 2)) }, verify = { it == 4 })
        TestRunner.check("W6 #4c Single number alone", block = { singleNumber(intArrayOf(1)) }, verify = { it == 1 })
    }

    // 5) Number of 1 Bits: n & (n - 1) drops the lowest set bit.
    fun hammingWeight() {
        TestRunner.check("W6 #5a Hamming 1011", block = { hammingWeight(0b1011) }, verify = { it == 3 })
        TestRunner.check("W6 #5b Hamming single bit", block = { hammingWeight(128) }, verify = { it == 1 })
        TestRunner.check("W6 #5c Hamming zero", block = { hammingWeight(0) }, verify = { it == 0 })
    }

    // 6) Missing Number: sum 0..n minus the array, or XOR indices with values.
    fun missingNumber() {
        TestRunner.check("W6 #6a Missing classic", block = { missingNumber(intArrayOf(3, 0, 1)) }, verify = { it == 2 })
        TestRunner.check("W6 #6b Missing at end", block = { missingNumber(intArrayOf(0, 1)) }, verify = { it == 2 })
        TestRunner.check("W6 #6c Missing at start", block = { missingNumber(intArrayOf(1)) }, verify = { it == 0 })
    }

    // 7) Power of Two: n > 0 && (n & (n - 1)) == 0.
    fun powerOfTwo() {
        TestRunner.check("W6 #7a Power of two 2^0", block = { isPowerOfTwo(1) }, verify = { it })
        TestRunner.check("W6 #7b Power of two 2^4", block = { isPowerOfTwo(16) }, verify = { it })
        TestRunner.check("W6 #7c Power of two rejects 0/-16/218",
            block = { listOf(isPowerOfTwo(0), isPowerOfTwo(-16), isPowerOfTwo(218)) },
            verify = { it == listOf(false, false, false) })
    }

    // 8) Best Time to Buy & Sell Stock II: sum every positive daily diff.
    fun maxProfitII() {
        TestRunner.check("W6 #8a Stock II classic", block = { maxProfitII(intArrayOf(7, 1, 5, 3, 6, 4)) }, verify = { it == 7 })
        TestRunner.check("W6 #8b Stock II increasing", block = { maxProfitII(intArrayOf(1, 2, 3, 4, 5)) }, verify = { it == 4 })
        TestRunner.check("W6 #8c Stock II decreasing", block = { maxProfitII(intArrayOf(7, 6, 4, 3, 1)) }, verify = { it == 0 })
    }

    // 9) Jump Game: farthest reachable index, walking left to right.
    fun canJump() {
        TestRunner.check("W6 #9a Jump classic", block = { canJump(intArrayOf(2, 3, 1, 1, 4)) }, verify = { it })
        TestRunner.check("W6 #9b Jump stuck at zero", block = { canJump(intArrayOf(3, 2, 1, 0, 4)) }, verify = { !it })
        TestRunner.check("W6 #9c Jump single index", block = { canJump(intArrayOf(0)) }, verify = { it })
    }

    // 10) Jump Game II: BFS layers — jump only when the current layer ends.
    fun jump() {
        TestRunner.check("W6 #10a Jump II classic", block = { jump(intArrayOf(2, 3, 1, 1, 4)) }, verify = { it == 2 })
        TestRunner.check("W6 #10b Jump II alt path", block = { jump(intArrayOf(2, 3, 0, 1, 4)) }, verify = { it == 2 })
        TestRunner.check("W6 #10c Jump II already there", block = { jump(intArrayOf(1)) }, verify = { it == 0 })
    }

    // 11) Gas Station: if total gas >= total cost the start is where the running
    //     deficit last reset.
    fun canCompleteCircuit() {
        TestRunner.check("W6 #11a Gas classic", block = { canCompleteCircuit(intArrayOf(1, 2, 3, 4, 5), intArrayOf(3, 4, 5, 1, 2)) }, verify = { it == 3 })
        TestRunner.check("W6 #11b Gas impossible", block = { canCompleteCircuit(intArrayOf(2, 3, 4), intArrayOf(3, 4, 3)) }, verify = { it == -1 })
        TestRunner.check("W6 #11c Gas single station", block = { canCompleteCircuit(intArrayOf(5), intArrayOf(4)) }, verify = { it == 0 })
    }

    // 12) Merge Intervals: sort by start, extend the last kept interval.
    fun mergeIntervals() {
        TestRunner.check("W6 #12a Merge classic", block = { w6Merge(arrayOf(intArrayOf(1, 3), intArrayOf(2, 6), intArrayOf(8, 10), intArrayOf(15, 18))).map { it.toList() } }, verify = { it == listOf(listOf(1, 6), listOf(8, 10), listOf(15, 18)) })
        TestRunner.check("W6 #12b Merge touching", block = { w6Merge(arrayOf(intArrayOf(1, 4), intArrayOf(4, 5))).map { it.toList() } }, verify = { it == listOf(listOf(1, 5)) })
        TestRunner.check("W6 #12c Merge contained", block = { w6Merge(arrayOf(intArrayOf(1, 4), intArrayOf(2, 3))).map { it.toList() } }, verify = { it == listOf(listOf(1, 4)) })
    }

    // 13) Insert Interval: emit what ends before newInterval, merge the overlap run,
    //     then emit the tail.
    fun insertInterval() {
        TestRunner.check("W6 #13a Insert overlap", block = { insert(arrayOf(intArrayOf(1, 3), intArrayOf(6, 9)), intArrayOf(2, 5)).map { it.toList() } }, verify = { it == listOf(listOf(1, 5), listOf(6, 9)) })
        TestRunner.check("W6 #13b Insert multi-merge",
            block = { insert(arrayOf(intArrayOf(1, 2), intArrayOf(3, 5), intArrayOf(6, 7), intArrayOf(8, 10), intArrayOf(12, 16)), intArrayOf(4, 8)).map { it.toList() } },
            verify = { it == listOf(listOf(1, 2), listOf(3, 10), listOf(12, 16)) })
        TestRunner.check("W6 #13c Insert into empty", block = { insert(arrayOf(), intArrayOf(5, 7)).map { it.toList() } }, verify = { it == listOf(listOf(5, 7)) })
    }

    // 14) Meeting Rooms: sort by start, any start < previous end is a conflict.
    fun canAttendMeetings() {
        TestRunner.check("W6 #14a Meetings overlap", block = { canAttendMeetings(arrayOf(intArrayOf(0, 30), intArrayOf(5, 10), intArrayOf(15, 20))) }, verify = { !it })
        TestRunner.check("W6 #14b Meetings no overlap", block = { canAttendMeetings(arrayOf(intArrayOf(7, 10), intArrayOf(2, 4))) }, verify = { it })
        TestRunner.check("W6 #14c Meetings empty", block = { canAttendMeetings(arrayOf()) }, verify = { it })
    }

    // 15) Non-Overlapping Intervals: sort by end, keep the earliest finishing.
    fun eraseOverlapIntervals() {
        TestRunner.check("W6 #15a Erase classic", block = { eraseOverlapIntervals(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(1, 3))) }, verify = { it == 1 })
        TestRunner.check("W6 #15b Erase all equal", block = { eraseOverlapIntervals(arrayOf(intArrayOf(1, 2), intArrayOf(1, 2), intArrayOf(1, 2))) }, verify = { it == 2 })
        TestRunner.check("W6 #15c Erase touching ok", block = { eraseOverlapIntervals(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3))) }, verify = { it == 0 })
    }

    // 16) Min Arrows to Burst Balloons: sort by end, one arrow at each new end.
    fun findMinArrowShots() {
        TestRunner.check("W6 #16a Arrows classic", block = { findMinArrowShots(arrayOf(intArrayOf(10, 16), intArrayOf(2, 8), intArrayOf(1, 6), intArrayOf(7, 12))) }, verify = { it == 2 })
        TestRunner.check("W6 #16b Arrows disjoint", block = { findMinArrowShots(arrayOf(intArrayOf(1, 2), intArrayOf(3, 4), intArrayOf(5, 6), intArrayOf(7, 8))) }, verify = { it == 4 })
        TestRunner.check("W6 #16c Arrows touching", block = { findMinArrowShots(arrayOf(intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 4), intArrayOf(4, 5))) }, verify = { it == 2 })
    }

    // 17) Task Scheduler: max(size, (maxFreq - 1) * (n + 1) + countMax).
    fun leastInterval() {
        TestRunner.check("W6 #17a Scheduler classic", block = { leastInterval("AAABBB".toCharArray(), 2) }, verify = { it == 8 })
        TestRunner.check("W6 #17b Scheduler no cooldown", block = { leastInterval("AAABBB".toCharArray(), 0) }, verify = { it == 6 })
        TestRunner.check("W6 #17c Scheduler idle fill", block = { leastInterval("AAAAAABCDEFG".toCharArray(), 2) }, verify = { it == 16 })
    }

    // 18) Partition Labels: last-occurrence map, extend the cut as you scan.
    fun partitionLabels() {
        TestRunner.check("W6 #18a Partition classic", block = { partitionLabels("ababcbacadefegdehijhklij") }, verify = { it == listOf(9, 7, 8) })
        TestRunner.check("W6 #18b Partition single piece", block = { partitionLabels("eccbbbbdec") }, verify = { it == listOf(10) })
        TestRunner.check("W6 #18c Partition single char", block = { partitionLabels("a") }, verify = { it == listOf(1) })
    }

    // 19) Search in Rotated Sorted Array: one half is always sorted — check it.
    fun searchRotated() {
        TestRunner.check("W6 #19a Rotated search classic", block = { searchRotated(intArrayOf(4, 5, 6, 7, 0, 1, 2), 0) }, verify = { it == 4 })
        TestRunner.check("W6 #19b Rotated search absent", block = { searchRotated(intArrayOf(4, 5, 6, 7, 0, 1, 2), 3) }, verify = { it == -1 })
        TestRunner.check("W6 #19c Rotated search single", block = { searchRotated(intArrayOf(1), 0) }, verify = { it == -1 })
    }

    // 20) Find Min in Rotated Sorted Array: binary search against the right end.
    fun findMinRotated() {
        TestRunner.check("W6 #20a Rotated min classic", block = { findMin(intArrayOf(3, 4, 5, 1, 2)) }, verify = { it == 1 })
        TestRunner.check("W6 #20b Rotated min rotated", block = { findMin(intArrayOf(4, 5, 6, 7, 0, 1, 2)) }, verify = { it == 0 })
        TestRunner.check("W6 #20c Rotated min unrotated", block = { findMin(intArrayOf(11, 13, 15, 17)) }, verify = { it == 11 })
    }

    // 21) Single Number II: count each bit mod 3 (or the ones/twos state machine).
    fun singleNumberII() {
        TestRunner.check("W6 #21a Single II classic", block = { singleNumberII(intArrayOf(2, 2, 3, 2)) }, verify = { it == 3 })
        TestRunner.check("W6 #21b Single II larger", block = { singleNumberII(intArrayOf(0, 1, 0, 1, 0, 1, 99)) }, verify = { it == 99 })
        TestRunner.check("W6 #21c Single II negative-free", block = { singleNumberII(intArrayOf(7, 7, 7, 8)) }, verify = { it == 8 })
    }

    // 22) Sum of Two Integers: carry = (a and b) shl 1, repeat with XOR, no +/-.
    fun getSum() {
        TestRunner.check("W6 #22a Bit sum basic", block = { getSum(1, 2) }, verify = { it == 3 })
        TestRunner.check("W6 #22b Bit sum with carry", block = { getSum(2, 3) }, verify = { it == 5 })
        TestRunner.check("W6 #22c Bit sum cancelling", block = { getSum(-1, 1) }, verify = { it == 0 })
    }

    // 23) Counting Bits: bits[i] = bits[i shr 1] + (i and 1).
    fun countBits() {
        TestRunner.check("W6 #23a Bits n=2", block = { countBits(2).toList() }, verify = { it == listOf(0, 1, 1) })
        TestRunner.check("W6 #23b Bits n=5", block = { countBits(5).toList() }, verify = { it == listOf(0, 1, 1, 2, 1, 2) })
        TestRunner.check("W6 #23c Bits n=0", block = { countBits(0).toList() }, verify = { it == listOf(0) })
    }

    // 24) Queue Reconstruction: sort tall-first (k asc), insert each at index k.
    fun reconstructQueue() {
        TestRunner.check("W6 #24a Queue classic",
            block = { reconstructQueue(arrayOf(intArrayOf(7, 0), intArrayOf(4, 4), intArrayOf(7, 1), intArrayOf(5, 0), intArrayOf(6, 1), intArrayOf(5, 2))).map { it.toList() } },
            verify = { it == listOf(listOf(5, 0), listOf(7, 0), listOf(5, 2), listOf(6, 1), listOf(4, 4), listOf(7, 1)) })
        TestRunner.check("W6 #24b Queue single", block = { reconstructQueue(arrayOf(intArrayOf(6, 0))).map { it.toList() } }, verify = { it == listOf(listOf(6, 0)) })
        TestRunner.check("W6 #24c Queue empty", block = { reconstructQueue(arrayOf()).map { it.toList() } }, verify = { it == emptyList<List<Int>>() })
    }

    // 25) Wiggle Subsequence: count sign changes of consecutive diffs (+ peaks).
    fun wiggleMaxLength() {
        TestRunner.check("W6 #25a Wiggle full", block = { wiggleMaxLength(intArrayOf(1, 7, 4, 9, 2, 5)) }, verify = { it == 6 })
        TestRunner.check("W6 #25b Wiggle classic", block = { wiggleMaxLength(intArrayOf(1, 17, 5, 10, 13, 15, 10, 5, 16, 8)) }, verify = { it == 7 })
        TestRunner.check("W6 #25c Wiggle monotonic", block = { wiggleMaxLength(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)) }, verify = { it == 2 })
    }

    // 26) Candy: left-to-right pass, right-to-left pass, take the max per child.
    //     The answer for large inputs overflows Int, so it returns Long.
    fun candy() {
        TestRunner.check("W6 #26a Candy classic", block = { candy(intArrayOf(1, 0, 2)) }, verify = { it == 5L })
        TestRunner.check("W6 #26b Candy plateau", block = { candy(intArrayOf(1, 2, 2)) }, verify = { it == 4L })
        TestRunner.check("W6 #26c Candy valley", block = { candy(intArrayOf(1, 3, 2, 2, 1)) }, verify = { it == 7L })
    }

    // 27) Median of Two Sorted Arrays: partition the shorter array, O(log min(m,n)).
    fun medianTwoSorted() {
        TestRunner.check("W6 #27a Median odd total", block = { findMedianSortedArrays(intArrayOf(1, 3), intArrayOf(2)) }, verify = { it == 2.0 })
        TestRunner.check("W6 #27b Median even total", block = { findMedianSortedArrays(intArrayOf(1, 2), intArrayOf(3, 4)) }, verify = { it == 2.5 })
        TestRunner.check("W6 #27c Median one empty", block = { findMedianSortedArrays(intArrayOf(), intArrayOf(1)) }, verify = { it == 1.0 })
    }

    // 28) Majority Element II: generalized Boyer-Moore with two candidates.
    fun majorityElement() {
        TestRunner.check("W6 #28a Majority classic", block = { majorityElement(intArrayOf(3, 2, 3)).sorted() }, verify = { it == listOf(3) })
        TestRunner.check("W6 #28b Majority single", block = { majorityElement(intArrayOf(1)).sorted() }, verify = { it == listOf(1) })
        TestRunner.check("W6 #28c Majority two winners", block = { majorityElement(intArrayOf(1, 1, 1, 3, 3, 2, 2, 2)).sorted() }, verify = { it == listOf(1, 2) })
    }

    // ── Part B — complexity validation (complexityAll) ──────────────────────
    // Same problems, data at a scale that punishes the wrong complexity: the expected
    // answers are hand-derived, the budgets assume the linear / O(log n) approach.

    // 1) O(n) greedy reach scan over 100k indices.
    fun canJumpBig() = TestRunner.check("W6-B1 Jump Game 100k all ones", budgetMs = 100,
        block = { canJump(IntArray(100_000) { 1 }) }, verify = { it })

    // 2) Same input for Jump Game II: one step per index.
    fun jumpBig() = TestRunner.check("W6-B2 Jump Game II 100k all ones", budgetMs = 100,
        block = { jump(IntArray(100_000) { 1 }) }, verify = { it == 99_999 })

    // 3) O(n) single pass; restarting from every station is O(n^2) here.
    fun gasStationBig() = TestRunner.check("W6-B3 Gas Station 100k stations", budgetMs = 100,
        block = { canCompleteCircuit(IntArray(100_000) { 2 }, IntArray(100_000) { 1 }) }, verify = { it == 0 })

    // 4) A 500k-long overlap chain collapses to one interval; sorting must do the work.
    fun mergeIntervalsBig() = TestRunner.check("W6-B4 Merge Intervals 500k chained", budgetMs = 500,
        block = { w6Merge(Array(500_000) { intArrayOf(it, it + 1) }) },
        verify = { it.size == 1 && it[0][0] == 0 && it[0][1] == 500_000 })

    // 5) 500 lookups over a rotated 1..1,000,000 — O(log n) per query, not O(n).
    fun searchRotatedBig() = TestRunner.check("W6-B5 Rotated search 1M x 500 targets", budgetMs = 500,
        block = {
            val n = 1_000_000
            val rotated = IntArray(n) { (it + n / 2) % n + 1 }   // [500001..1000000, 1..500000]
            val targets = List(500) { 1 + (it * 2003) % n }
            targets.all { v -> searchRotated(rotated, v) == if (v > n / 2) v - n / 2 - 1 else v + n / 2 - 1 }
        }, verify = { it })

    // 6) Minimum sits at the very last index — the hardest rotation for a binary
    //    search (and the practice sheet's "expected 1,000,000" is impossible for a
    //    permutation of 1..1,000,000: the smallest value is always 1).
    fun findMinRotatedBig() = TestRunner.check("W6-B6 Rotated min 1M", budgetMs = 100,
        block = { findMin(IntArray(1_000_000) { if (it == 999_999) 1 else it + 2 }) }, verify = { it == 1 })

    // 7) Two 500k arrays partitioned in O(log min(m,n)); merging would allocate 1M.
    fun medianTwoArraysBig() = TestRunner.check("W6-B7 Median of two 500k arrays", budgetMs = 200,
        block = { findMedianSortedArrays(IntArray(500_000) { it * 2 }, IntArray(500_000) { it * 2 + 1 }) },
        verify = { it == 499_999.5 })

    // 8) Formula, not simulation: (50000 - 1) * (100 + 1) + 1 idle-filled slots.
    fun taskSchedulerBig() = TestRunner.check("W6-B8 Task Scheduler 100k tasks", budgetMs = 200,
        block = { leastInterval(CharArray(100_000) { if (it < 50_000) 'A' else 'B' + (it - 50_000) % 25 }, 100) },
        verify = { it == 5_049_900 })

    // 9) Strictly increasing ratings: each child needs i + 1 candies, so the answer
    //    1+2+...+100,000 needs a Long.
    fun candyBig() = TestRunner.check("W6-B9 Candy increasing 100k", budgetMs = 200,
        block = { candy(IntArray(100_000) { it + 1 }) }, verify = { it == 5_000_050_000L })

    // 10) O(n) DP over 10M values, spot-checked against Integer.bitCount.
    fun countBitsBig() = TestRunner.check("W6-B10 Counting Bits 10M", budgetMs = 300,
        block = {
            val bits = countBits(10_000_000)
            listOf(0, 1, 1023, 1024, 9_999_999).all { bits[it] == Integer.bitCount(it) } && bits.size == 10_000_001
        }, verify = { it })

    fun complexityAll() {
        println("\n━━━━━━━ WEEK 6 Part B — complexity validation ━━━━━━━")
        canJumpBig(); jumpBig(); gasStationBig(); mergeIntervalsBig(); searchRotatedBig()
        findMinRotatedBig(); medianTwoArraysBig(); taskSchedulerBig(); candyBig(); countBitsBig()
    }
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 6 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════
//
// Each stub throws TODO() so every case above reports [SKIP ⏭️] instead of a wrong
// answer. Replace the TODO() with your implementation and the case runs for real.

// ── Greedy ──────────────────────────────────────────────────────────────────
fun maxProfit(prices: IntArray): Int = TODO("W6 #1 — min price so far, max diff after it")
fun findContentChildren(g: IntArray, s: IntArray): Int = TODO("W6 #2 — sort both, match smallest cookie")
fun lemonadeChange(bills: IntArray): Boolean = TODO("W6 #3 — count $5/$10, give $10 back first")
fun maxProfitII(prices: IntArray): Int = TODO("W6 #8 — sum every positive daily diff")            // second maxProfit in the week doc
fun canJump(nums: IntArray): Boolean = TODO("W6 #9 — greedy farthest reach")
fun jump(nums: IntArray): Int = TODO("W6 #10 — greedy BFS layers, jump at layer end")
fun canCompleteCircuit(gas: IntArray, cost: IntArray): Int = TODO("W6 #11 — running deficit, reset start")
fun w6Merge(intervals: Array<IntArray>): Array<IntArray> = TODO("W6 #12 — sort by start, extend last")
fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> = TODO("W6 #13 — before / merge run / after")
fun canAttendMeetings(intervals: Array<IntArray>): Boolean = TODO("W6 #14 — sort by start, check overlaps")
fun eraseOverlapIntervals(intervals: Array<IntArray>): Int = TODO("W6 #15 — sort by end, keep earliest finish")
fun findMinArrowShots(points: Array<IntArray>): Int = TODO("W6 #16 — sort by end, arrow at each new end")
fun leastInterval(tasks: CharArray, n: Int): Int = TODO("W6 #17 — (maxFreq-1)*(n+1)+countMax, floored at size")
fun partitionLabels(s: String): List<Int> = TODO("W6 #18 — last-index map, extend the cut")
fun reconstructQueue(people: Array<IntArray>): Array<IntArray> = TODO("W6 #24 — tall first, insert at index k")
fun wiggleMaxLength(nums: IntArray): Int = TODO("W6 #25 — count sign changes of diffs")
fun candy(ratings: IntArray): Long = TODO("W6 #26 — left and right passes, take max (Long!)")

// ── Binary search ───────────────────────────────────────────────────────────
fun searchRotated(nums: IntArray, target: Int): Int = TODO("W6 #19 — one half is sorted, check it")  // `search` in the week doc
fun findMin(nums: IntArray): Int = TODO("W6 #20 — compare mid with the right end")
fun findMedianSortedArrays(nums1: IntArray, nums2: IntArray): Double = TODO("W6 #27 — partition the shorter array")

// ── Bit manipulation ────────────────────────────────────────────────────────
fun singleNumber(nums: IntArray): Int = TODO("W6 #4 — XOR everything")
fun hammingWeight(n: Int): Int = TODO("W6 #5 — n & (n - 1) drops the lowest set bit")
fun missingNumber(nums: IntArray): Int = TODO("W6 #6 — sum 0..n minus the array")
fun isPowerOfTwo(n: Int): Boolean = TODO("W6 #7 — n > 0 && (n & (n - 1)) == 0")
fun singleNumberII(nums: IntArray): Int = TODO("W6 #21 — bit counts mod 3")                          // second singleNumber in the week doc
fun getSum(a: Int, b: Int): Int = TODO("W6 #22 — XOR plus carried shifts")
fun countBits(n: Int): IntArray = TODO("W6 #23 — bits[i] = bits[i shr 1] + (i and 1)")

// ── Boyer-Moore ─────────────────────────────────────────────────────────────
fun majorityElement(nums: IntArray): List<Int> = TODO("W6 #28 — two candidates, then verify counts")
