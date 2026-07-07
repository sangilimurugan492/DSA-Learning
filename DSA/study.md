# 📅 DSA Daily Study Guide — Step-by-Step Preparation Plan

> A day-by-day guide to systematically prepare all problems in this repository. Follow this plan to build strong pattern recognition and problem-solving skills for FAANG interviews.

---

## 🗂️ How to Use This Guide

### Daily Study Routine (2-3 Hours/Day)

```
┌─────────────────────────────────────────────────────┐
│  ⏱️  DAILY ROUTINE                                   │
│                                                       │
│  1. 📖 Review (15 min)                               │
│     - Re-solve 1 problem from yesterday from memory   │
│     - Quick review of key pattern/insight             │
│                                                       │
│  2. 🆕 New Problems (90-120 min)                     │
│     - Read problem statement carefully                │
│     - Try to solve for 15-20 min before looking       │
│     - Read Brute Force → understand WHY it's slow     │
│     - Read Optimal → understand the KEY INSIGHT       │
│     - Close file → re-implement from memory           │
│                                                       │
│  3. 📝 Spaced Repetition (15 min)                    │
│     - Mark problems as ✅ / 🔄 / ❌                  │
│     - Revisit ❌ problems in 3 days                   │
│     - Revisit 🔄 problems in 7 days                   │
│                                                       │
│  4. 🧠 Pattern Reflection (10 min)                    │
│     - What pattern did today's problems use?          │
│     - How does it differ from yesterday's pattern?     │
│     - When would I use this pattern in an interview?   │
└─────────────────────────────────────────────────────┘
```

### Problem Difficulty Legend
- 🟢 **Easy** — Build confidence, learn the pattern
- 🟡 **Medium** — FAANG bread & butter
- 🔴 **Hard** — Combine patterns, edge cases

### Progress Tracking
- ⬜ Not started
- 🔄 In progress (saw solution, need to re-solve)
- ✅ Mastered (can solve from memory in < 20 min)

---

## 📊 Overview — 12-Week Plan at a Glance

| Week | Phase | Topics | Problems |
|------|-------|--------|----------|
| 1 | Foundation | Linear Scan, Two Pointer Basics | 14 |
| 2 | Foundation | HashSet, Prefix Sum, Sliding Window | 16 |
| 3 | Core Patterns | Kadane's, Binary Search, Stack | 14 |
| 4 | Core Patterns | Greedy, Linked List | 16 |
| 5 | Core Patterns | Two Pointer Advanced, In-Place | 14 |
| 6 | Backtracking & Trees | Backtracking, Tree Traversal, BST | 13 |
| 7 | Dynamic Programming | 1D DP (Counting, Optimization) | 8 |
| 8 | Dynamic Programming | 1D DP (Minimization), 2D DP | 10 |
| 9 | Dynamic Programming | Subsequence DP, DP Review | 8 |
| 10 | Advanced DS | Heap, Trie, Graph BFS/DFS | 13 |
| 11 | Advanced DS | Graph Shortest Path, Topological Sort, Union Find | 9 |
| 12 | Mock Interviews | Timed Practice, FAANG Top 15 | 15+ |

---

# 🏗️ PHASE 1: FOUNDATION (Weeks 1-2)

> **Goal:** Master the basic patterns that appear in EVERY interview. Build confidence and speed with straightforward problems.

---

## Week 1: Linear Scan & Two Pointer Basics

### Day 1 — Linear Scan: Finding & Tracking
**Topic:** Simple O(N) traversal, track running values

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Find Min & Max in Array | `array/linear_scan/FindSmallestOrLargest.kt` | 🟢 | ⬜ |
| 2 | Find 2nd Min & Max | `array/linear_scan/FindSecondLargestOrSmallestElement.kt` | 🟢 | ⬜ |
| 3 | Max Consecutive Ones | `array/linear_scan/FindMaximumConsecutiveOnes.kt` | 🟢 | ⬜ |
| 4 | Count Element Frequency | `array/linear_scan/CountFrequencyOfElement.kt` | 🟢 | ⬜ |

**🔑 Key Takeaway:** Linear scan is the foundation. Every complex algorithm starts with "can I just scan through once?"

---

### Day 2 — Linear Scan: Variations
**Topic:** More complex single-pass patterns

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | First Repeating Element | `array/linear_scan/FirstRepeatingElement.kt` | 🟢 | ⬜ |
| 2 | Leaders in Array | `array/linear_scan/LeadersInArray.kt` | 🟢 | ⬜ |
| 3 | Finding Pivot Index | `array/linear_scan/FindingPivotIndex.kt` | 🟢 | ⬜ |
| 4 | Check Array Sorted/Rotated | `array/linear_scan/CheckArraySortedOrRotate.kt` | 🟢 | ⬜ |

**🔑 Key Takeaway:** Right-to-left scan (Leaders) and prefix comparison (Pivot Index) are powerful linear scan variations.

---

### Day 3 — Linear Scan: Sorting + Scan
**Topic:** Sort first, then scan for patterns

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Height Checker | `array/linear_scan/HeightChecker.kt` | 🟢 | ⬜ |
| 2 | Min Absolute Difference | `array/linear_scan/MinimumAbsoluteDifference.kt` | 🟢 | ⬜ |
| 3 | Number of Distinct Averages | `array/linear_scan/NumberOfDistinctAverages.kt` | 🟢 | ⬜ |
| 4 | Count Negatives in Matrix | `array/linear_scan/CountNegativesInMatrix.kt` | 🟢 | ⬜ |

**🔑 Key Takeaway:** Sometimes sorting first (O(N log N)) enables a simple O(N) scan. Total O(N log N) is still better than O(N²).

---

### Day 4 — Linear Scan: Reverse & Review
**Topic:** Reversal patterns + review Day 1-3

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Reverse an Array | `array/linear_scan/ReverseAnArray.kt` | 🟢 | ⬜ |
| 2 | Reverse a String | `array/linear_scan/ReverseAString.kt` | 🟢 | ⬜ |
| 3 | 🔄 Re-solve 2 problems from Day 1-3 that were ❌ or 🔄 | — | — | ⬜ |

**🔑 Key Takeaway:** Two-pointer swap from both ends is the standard reversal technique.

---

### Day 5 — Two Pointer: Opposite Ends
**Topic:** Two pointers starting from opposite ends, moving inward

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Valid Palindrome | `array/two_pointer/ValidPalindrom.kt` | 🟢 | ⬜ |
| 2 | Two Sum II (Sorted Array) | `array/two_pointer/TwoSumLevelTwoWithSortedArray.kt` | 🟡 | ⬜ |
| 3 | Is Subsequence | `array/two_pointer/IsSubSequence.kt` | 🟢 | ⬜ |

**🔑 Key Takeaway:** Sorted array + find pair → two pointers from ends. Move the pointer that gets you closer to the target.

---

### Day 6 — Two Pointer: Move & Merge
**Topic:** Slow/fast pointer, merge patterns

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Move Zeros to End | `array/two_pointer/MoveZerosToEnd.kt` | 🟢 | ⬜ |
| 2 | Remove Element in Array | `array/two_pointer/RemoveElementInAnArray.kt` | 🟢 | ⬜ |
| 3 | Merge Sorted Array | `array/two_pointer/MergeSortedArray.kt` | 🟢 | ⬜ |
| 4 | String Compression | `array/two_pointer/StringCompression.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Slow/fast pointer partitions the array. Read/write pointer overwrites in-place.

---

### Day 7 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from this week that were ❌ or 🔄
2. Write down the 3 most important patterns you learned:
   - Linear Scan: Track running values in single pass
   - Two Pointer (Opposite Ends): Sorted array, converge inward
   - Two Pointer (Slow/Fast): Partition array in-place
3. Preview next week's topics: HashSet, Prefix Sum, Sliding Window
```

---

## Week 2: HashSet, Prefix Sum & Sliding Window

### Day 8 — HashSet Lookup: Existence Checks
**Topic:** Use HashSet for O(1) "have I seen this?" checks

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Contains Duplicates | `array/hashset_lookup/ContainsDuplicates.kt` | 🟢 | ⬜ |
| 2 | Count Distinct Elements | `array/hashset_lookup/CountDistinctElements.kt` | 🟢 | ⬜ |
| 3 | Check if N and 2N Exist | `array/hashset_lookup/CheckIfNDoubleExits.kt` | 🟢 | ⬜ |
| 4 | Valid Sudoku | `array/hashset_lookup/ValidSudoku.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** HashSet trades space for time. O(N) space gives O(1) lookups instead of O(N) scans.

---

### Day 9 — HashSet Lookup: Advanced + Complement Search
**Topic:** HashSet for sequence detection + complement (target - current) lookup

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Longest Consecutive Sequence | `array/hashset_lookup/LongestConsecutiveSequence.kt` | 🟡 | ⬜ |
| 2 | Two Sum (Unsorted) | `array/complement_search/TwoSumLevelOneWithoutSortedArray.kt` | 🟢 | ⬜ |
| 3 | Two Sum II (Sorted) | `array/complement_search/TwoSumII.kt` | 🟡 | ⬜ |
| 4 | Find Pairs with Certain Sum | `array/complement_search/FindingPairsWithCertainSum.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Longest Consecutive Sequence — only start counting from sequence beginnings (num-1 not in set). Two Sum — HashMap stores complement (target - arr[i]).

---

### Day 10 — Prefix Sum: Range Queries
**Topic:** Precompute cumulative sums for O(1) range queries

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Running Sum of 1D Array | `array/prefix_sum/RunningSum1DArray.kt` | 🟢 | ⬜ |
| 2 | Prefix Sum Query | `array/prefix_sum/PrefixSumQuery.kt` | 🟢 | ⬜ |
| 3 | Product of Array Except Self | `array/prefix_sum/ProductOfArrayExceptSelf.kt` | 🟡 | ⬜ |
| 4 | Subarray Sum Equals K | `array/prefix_sum/SubarraySumEqualsK.kt` | 🟡 | ⬜ |
| 5 | Contiguous Array | `array/prefix_sum/ContiguousArray.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Product of Array Except Self = prefix + suffix products. Subarray Sum Equals K = prefix sum + HashMap. These are TOP FAANG questions!

---

### Day 11 — Sliding Window: Fixed Size
**Topic:** Window of fixed size K slides across data

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Maximum Average Subarray | `array/sliding_window/MaximumAverageSubArray.kt` | 🟢 | ⬜ |
| 2 | Find All Anagrams in String | `array/sliding_window/FindAllAnagramsInString.kt` | 🟡 | ⬜ |
| 3 | Permutation in String | `array/sliding_window/PermutationInString.kt` | 🟡 | ⬜ |
| 4 | Grumpy Bookstore Owner | `array/sliding_window/GrumpyBookStoreOwner.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Fixed window = add right element, if window > K remove left element. Anagram/Permutation = frequency map + match counting.

---

### Day 12 — Sliding Window: Variable Size (Shrink While Invalid)
**Topic:** Window expands right, shrinks left when condition violated

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Longest Substring Without Repeating | `array/sliding_window/LongestSubstringWithoutRepeatingCharacters.kt` | 🟡 | ⬜ |
| 2 | Fruits into Baskets | `array/sliding_window/FruitsIntoBaskets.kt` | 🟡 | ⬜ |
| 3 | Longest Subarray After Deleting One | `array/sliding_window/LongestSubArrayAfterDeletingOne.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Variable window — expand right, when invalid shrink left. "At most K distinct/invalid" is the key constraint.

---

### Day 13 — Sliding Window: Variable Size (Shrink While Valid)
**Topic:** Window expands right, shrinks left to find MINIMUM valid window

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Minimum Window Substring | `array/sliding_window/MinimumWindowSubstring.kt` | 🔴 | ⬜ |
| 2 | Longest Repeating Character Replacement | `array/sliding_window/LongestRepeatingCharacterReplacement.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Minimum Window Substring = THE hardest sliding window. Build need map, expand until formed == required, then shrink to minimize. Key formula for Character Replacement: `windowLen - maxFreq ≤ k`.

---

### Day 14 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 2 that were ❌ or 🔄
2. Write down the 3 most important patterns:
   - HashSet: O(1) existence check → "Have I seen this before?"
   - Prefix Sum: Precompute → O(1) range queries
   - Sliding Window: Fixed (window=K) vs Variable (shrink while invalid/valid)
3. PRIORITY REVIEW: Product of Array Except Self + Subarray Sum Equals K
   (These are TOP 10 most asked FAANG questions)
```

---

# 🏗️ PHASE 2: CORE PATTERNS (Weeks 3-5)

> **Goal:** Master the patterns that solve 70% of FAANG interview questions.

---

## Week 3: Kadane's, Binary Search & Stack

### Day 15 — Kadane's Algorithm: Maximum Subarray
**Topic:** At each index, decide: extend previous subarray OR start fresh?

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Maximum Subarray | `array/kadane_algorithm/MaximumSubArray.kt` | 🟡 | ⬜ |
| 2 | Maximum Product Subarray | `array/kadane_algorithm/MaximumProductSubarray.kt` | 🟡 | ⬜ |
| 3 | Maximum Sum Circular Subarray | `array/kadane_algorithm/MaximumSumCircularSubarray.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** For SUM: `local_max = max(arr[i], local_max + arr[i])`. For PRODUCT: track BOTH max AND min (negative × negative = positive!). Circular: `max(normal Kadane, totalSum - minSubarraySum)`.

---

### Day 16 — Binary Search: Rotated Sorted Arrays
**Topic:** Binary search on sorted/partitioned data in O(log N)

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Search in Rotated Sorted Array | `array/binary_search/SearchInRotatedSortedArray.kt` | 🟡 | ⬜ |
| 2 | Find Min in Rotated Sorted Array | `array/binary_search/FindMinimumInRotatedSortedArray.kt` | 🟡 | ⬜ |
| 3 | Median of Two Sorted Arrays | `array/binary_search/MedianOfTwoSortedSubArray.kt` | 🔴 | ⬜ |

**🔑 Key Takeaway:** At any mid, one half is always sorted. Find which half, check if target is in range. For min: compare mid with right — min is in the unsorted half.

---

### Day 17 — Stack: Matching & Evaluation
**Topic:** LIFO for matching, nesting, evaluation

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Valid Parentheses | `stack/ValidParentheses.kt` | 🟢 | ⬜ |
| 2 | Evaluate Reverse Polish Notation | `stack/EvaluateReversePolishNotation.kt` | 🟡 | ⬜ |
| 3 | Min Stack | `stack/MinStack.kt` | 🟡 | ⬜ |
| 4 | Minimum Parentheses to Remove | `stack/MinimumParentheseToRemove.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Stack for matching (push opening, pop on closing), evaluation (push operands, pop on operator), and tracking state (auxiliary stack for min).

---

### Day 18 — Stack: Monotonic Stack
**Topic:** Stack that maintains increasing/decreasing order — for "next greater/smaller" problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Daily Temperatures | `stack/DailyTemperatures.kt` | 🟡 | ⬜ |
| 2 | Next Greater Element I | `stack/NextGreaterElementI.kt` | 🟡 | ⬜ |
| 3 | Next Greater Element II (Circular) | `stack/NextGreaterElementII.kt` | 🟡 | ⬜ |
| 4 | Online Stock Span | `stack/OnlineStockSpan.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Monotonic stack (decreasing) — while stack top < current, pop and set answer. Push current. This finds "next greater" in O(N).

---

### Day 19 — Stack: Advanced
**Topic:** Stack for histogram, parentheses, and fleet problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Largest Rectangle in Histogram | `stack/LargestRectangleInHistogram.kt` | 🔴 | ⬜ |
| 2 | Longest Valid Parentheses | `stack/LongestValidParentheses.kt` | 🔴 | ⬜ |
| 3 | Car Fleet | `stack/CarFleet.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Histogram — monotonic stack stores indices, width = current index - stack top after pop. Parentheses — stack stores indices of unmatched, length = current - stack top.

---

### Day 20 — Set Operations, Frequency Count & Bit Manipulation
**Topic:** Set operations, frequency counting, XOR tricks

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Intersection of Two Arrays | `array/set_operations/IntersectionOfTwoArrays.kt` | 🟢 | ⬜ |
| 2 | Find Difference Between Two Arrays | `array/set_operations/FindTheDifferenceBetweenTwoArray.kt` | 🟢 | ⬜ |
| 3 | Group Anagrams | `array/set_operations/GroupAnagrams.kt` | 🟡 | ⬜ |
| 4 | Top K Frequent Elements | `array/frequency_count/TopKFrequentElements.kt` | 🟡 | ⬜ |
| 5 | Single Number | `array/bit_manipulation/SingleNumber.kt` | 🟢 | ⬜ |
| 6 | Single Number II | `array/bit_manipulation/SingleNumberII.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Group Anagrams — sorted string as HashMap key. Top K Frequent — bucket sort O(N). Single Number — XOR: a^a=0, a^0=a.

---

### Day 21 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 3 that were ❌ or 🔄
2. Write down the 3 most important patterns:
   - Kadane's: Extend or start fresh? Track local max/min + global max
   - Binary Search: Find the sorted half, check if target is in range
   - Monotonic Stack: While top < current, pop → next greater found
3. PRIORITY REVIEW: Maximum Subarray + Largest Rectangle in Histogram
```

---

## Week 4: Greedy & Linked List

### Day 22 — Greedy: Buy/Sell Stock & Jump Game
**Topic:** Make locally optimal choice → globally optimal

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Best Time to Buy & Sell Stock I | `array/greedy/BestTimeToBuyAndSellStockI.kt` | 🟢 | ⬜ |
| 2 | Best Time to Buy & Sell Stock II | `array/greedy/BestTimeToBuyAndSellStockII.kt` | 🟡 | ⬜ |
| 3 | Jump Game | `array/greedy/JumpGame.kt` | 🟡 | ⬜ |
| 4 | Jump Game II | `array/greedy/JumpGameII.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Stock I — track min_price, max_profit. Stock II — sum all positive daily differences. Jump Game — track farthest reachable index.

---

### Day 23 — Greedy: Intervals
**Topic:** Sort by start/end, merge or count overlaps

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Merge Intervals | `array/greedy/MergeIntervals.kt` | 🟡 | ⬜ |
| 2 | Insert Interval | `array/greedy/InsertInterval.kt` | 🟡 | ⬜ |
| 3 | Meeting Rooms | `array/greedy/MeetingRooms.kt` | 🟡 | ⬜ |
| 4 | Non-Overlapping Intervals | `array/greedy/NonOverlappingIntervals.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Merge Intervals — sort by start, merge if overlap. Non-overlapping — sort by END, pick earliest finish. Meeting Rooms — sweep line or min-heap.

---

### Day 24 — Greedy: Advanced
**Topic:** More complex greedy strategies

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Gas Station | `array/greedy/GasStation.kt` | 🟡 | ⬜ |
| 2 | Candy | `array/greedy/Candy.kt` | 🔴 | ⬜ |
| 3 | Task Scheduler | `array/greedy/TaskScheduler.kt` | 🟡 | ⬜ |
| 4 | Partition Labels | `array/greedy/PartitionLabels.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Gas Station — if tank < 0, skip ALL stations before. Candy — two-pass L→R then R→L, take max. Task Scheduler — `(maxFreq-1)*(n+1) + countOfMaxFreq`.

---

### Day 25 — Greedy: Remaining Problems
**Topic:** More greedy practice

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Assign Cookies | `array/greedy/AssignCookies.kt` | 🟢 | ⬜ |
| 2 | Lemonade Change | `array/greedy/LemonadeChange.kt` | 🟢 | ⬜ |
| 3 | Min Arrows to Burst Balloons | `array/greedy/MinimumArrowsToBurstBalloons.kt` | 🟡 | ⬜ |
| 4 | Queue Reconstruction by Height | `array/greedy/QueueReconstructionByHeight.kt` | 🟡 | ⬜ |
| 5 | Wiggle Subsequence | `array/greedy/WiggleSubsequence.kt` | 🟡 | ⬜ |
| 6 | Interval Groups | `array/greedy/IntervalGroups.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Arrows = sort by END, shoot at overlap boundaries. Queue Reconstruction = sort tallest first, insert at k. Wiggle = count peaks and valleys.

---

### Day 26 — Linked List: Basics & Reversal
**Topic:** Pointer manipulation, reversal patterns

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Reverse Linked List | `linked_list/single/ReverseLinkedList.kt` | 🟢 | ⬜ |
| 2 | Reverse Linked List II | `linked_list/single/ReverseLinkedListII.kt` | 🟡 | ⬜ |
| 3 | Reverse in K-Group | `linked_list/single/ReverseLinkedListKGroup.kt` | 🔴 | ⬜ |
| 4 | Swap Nodes in Pairs | `linked_list/single/SwapNodesInPairs.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Reversal = prev, curr, next triplet. K-Group = reverse K nodes, recurse on rest. Always use dummy head to simplify edge cases.

---

### Day 27 — Linked List: Fast/Slow Pointer & Cycle
**Topic:** Detect cycles, find middle, remove Nth from end

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Linked List Cycle | `linked_list/single/LinkedListCycle.kt` | 🟢 | ⬜ |
| 2 | Linked List Cycle II | `linked_list/single/LinkedListCycleII.kt` | 🟡 | ⬜ |
| 3 | Middle of Linked List | `linked_list/single/MiddleOfLinkedList.kt` | 🟢 | ⬜ |
| 4 | Remove Nth Node from End | `linked_list/single/RemoveNthNodeFromLast.kt` | 🟡 | ⬜ |
| 5 | Palindrome Linked List | `linked_list/single/PalindromeLinkedList.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Fast/slow pointer — fast moves 2x, when fast reaches end, slow is at middle. Cycle detection — when fast meets slow, reset one to head, move both 1 step → meeting point is cycle start.

---

### Day 28 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 4 that were ❌ or 🔄
2. Write down the 3 most important patterns:
   - Greedy: Sort + scan, locally optimal → globally optimal
   - Linked List Reversal: prev, curr, next + dummy head
   - Fast/Slow Pointer: Cycle detection, find middle, remove Nth from end
3. PRIORITY REVIEW: Merge Intervals + Gas Station + Reverse Linked List
```

---

## Week 5: Two Pointer Advanced & In-Place

### Day 29 — Two Pointer: 3Sum & 4Sum
**Topic:** Sort + fix one/two + two-pointer for remaining

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Three Sum | `array/two_pointer/ThreeSum.kt` | 🟡 | ⬜ |
| 2 | Three Sum Closest | `array/two_pointer/ThreeSumCloset.kt` | 🟡 | ⬜ |
| 3 | Four Sum | `array/two_pointer/FourSum.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** 3Sum = sort + fix i + two-pointer on [i+1, n-1]. Skip duplicates! 4Sum = sort + fix i,j + two-pointer on [j+1, n-1].

---

### Day 30 — Two Pointer: Container & Trapping Water
**Topic:** Area maximization with two pointers

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Container With Most Water | `array/two_pointer/ContainerWithMostWater.kt` | 🟡 | ⬜ |
| 2 | Trapping Rain Water | `array/two_pointer/TrappingRainWaterI.kt` | 🔴 | ⬜ |

**🔑 Key Takeaway:** Container — move shorter line inward (area = min(height) × width). Trapping Water — two-pointer with maxLeft/maxRight, water += min(maxL, maxR) - height[i].

---

### Day 31 — Two Pointer: Pairing & Counting
**Topic:** Sort + two-pointer for pairing, counting problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Boats to Save People | `array/two_pointer/BoatsToSave.kt` | 🟡 | ⬜ |
| 2 | K-Sum Pairs | `array/two_pointer/KSumPairs.kt` | 🟡 | ⬜ |
| 3 | Two Sum Less Than K | `array/two_pointer/TwoSumLessThanK.kt` | 🟡 | ⬜ |
| 4 | Sort Colors (Dutch Flag) | `array/two_pointer/SortColorsDutchNationalFlag.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Boats — sort + lightest+heaviest pairing. Dutch Flag — three-way partition with low/mid/high pointers.

---

### Day 32 — Two Pointer: String & Subarray Patterns
**Topic:** Two-pointer on strings and subarray counting

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Backspace String Compare | `array/two_pointer/BackspaceStringCompare.kt` | 🟡 | ⬜ |
| 2 | Longest Palindromic Substring | `array/two_pointer/LongestPalindromSubString.kt` | 🟡 | ⬜ |
| 3 | Min Size Subarray Sum | `array/two_pointer/MinimumSizeSubArraySum.kt` | 🟡 | ⬜ |
| 4 | Intersection of Two Arrays II | `array/two_pointer/IntersectionTwoArrayII.kt` | 🟢 | ⬜ |

**🔑 Key Takeaway:** Palindrome — expand around center (odd + even length). Min Subarray — variable sliding window, shrink while sum ≥ target.

---

### Day 33 — Two Pointer: Sliding Window Variants
**Topic:** atMost(K) pattern and advanced sliding window

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Max Consecutive Ones III | `array/two_pointer/MaxConsecutiveOnesIII.kt` | 🟡 | ⬜ |
| 2 | Subarrays with K Different Integers | `array/two_pointer/SubArrayWithKDifferentInteger.kt` | 🔴 | ⬜ |
| 3 | Count Number of Nice Subarrays | `array/two_pointer/CountNumberOfNiceSubarrays.kt` | 🟡 | ⬜ |
| 4 | Minimum Pair Removal to Sort | `array/two_pointer/MinimumPairRemovaltoSortArrayI.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** `exactly(K) = atMost(K) - atMost(K-1)`. This is THE pattern for counting subarrays with exactly K something. Number of subarrays ending at right = `right - left + 1`.

---

### Day 34 — Two Pointer In-Place: Modify Array Without Extra Space
**Topic:** Slow/fast pointers, read/write pointers to overwrite in-place

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Remove Duplicates from Sorted Array | `array/two_pointer_inplace/RemoveDuplicateFromSortedArray.kt` | 🟢 | ⬜ |
| 2 | Remove Duplicates II (Keep Two) | `array/two_pointer_inplace/RemoveDuplicateseTWO.kt` | 🟡 | ⬜ |
| 3 | Remove Element | `array/two_pointer_inplace/RemoveElement.kt` | 🟢 | ⬜ |
| 4 | Replace with Greatest on Right | `array/two_pointer_inplace/ReplaceGratestElementOnRight.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Slow pointer = write position, fast pointer = read position. Right-to-left scan with max tracker for "greatest on right."

---

### Day 35 — Two Pointer In-Place: Advanced
**Topic:** Reversal trick, squares, next permutation

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Sorted Squares | `array/two_pointer_inplace/SortedSquares.kt` | 🟡 | ⬜ |
| 2 | Rotate Array | `array/two_pointer_inplace/RotateArray.kt` | 🟡 | ⬜ |
| 3 | Next Permutation | `array/two_pointer_inplace/NextPermutation.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Sorted Squares — two pointers from ends (negatives square to large). Rotate — reverse all → reverse first k → reverse remaining. Next Permutation — find pivot, swap, reverse suffix.

---

# 🏗️ PHASE 3: BACKTRACKING & TREES (Week 6)

> **Goal:** Master recursive thinking — the foundation for DP and tree problems.

---

## Week 6: Backtracking, Trees & Matrix

### Day 36 — Backtracking: Subsets & Combinations
**Topic:** Include/exclude pattern, constraint + reuse

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Subsets | `BackTracking/Subsets.kt` | 🟡 | ⬜ |
| 2 | Combination Sum | `BackTracking/CombinationSum.kt` | 🟡 | ⬜ |
| 3 | Letter Combinations of Phone Number | `BackTracking/LetterAndCombinationPhoneNumber.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Subsets — include/exclude each element → 2^N subsets. Combination Sum — stay at same index to allow reuse. ALWAYS draw the recursion tree first!

---

### Day 37 — Backtracking: Permutations & Grid
**Topic:** Order matters, grid DFS

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Permutations | `BackTracking/Permutations.kt` | 🟡 | ⬜ |
| 2 | Word Search | `BackTracking/WordSearch.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Permutations — order matters, use `used[]` array, try ALL positions. Word Search — grid DFS + mark visited with '#', restore after backtracking.

---

### Day 38 — Tree Traversal & BST
**Topic:** Tree traversal foundations, BST operations

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Tree Traversals (In/Pre/Post) | `tree/traversal/TreeTraversals.kt` | 🟢 | ⬜ |
| 2 | Binary Tree Level Order | `tree/traversal/BinaryTreeLevelOrderTraversal.kt` | 🟡 | ⬜ |
| 3 | Binary Tree Right Side View | `tree/traversal/BinaryTreeRightSideView.kt` | 🟡 | ⬜ |
| 4 | Search in BST | `tree/bst/SearchInBST.kt` | 🟢 | ⬜ |
| 5 | Validate BST | `tree/bst/ValidateBST.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** DFS (recursive) for in/pre/post-order. BFS (queue) for level-order. BST property: left < root < right, validate with min/max range.

---

### Day 39 — Binary Tree & BST Advanced
**Topic:** Recursive tree thinking, BST operations

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Maximum Depth of Binary Tree | `tree/binary_tree/MaximumDepthOfBinaryTree.kt` | 🟢 | ⬜ |
| 2 | Same Tree | `tree/binary_tree/SameTree.kt` | 🟢 | ⬜ |
| 3 | Balanced Binary Tree | `tree/binary_tree/BalancedBinaryTree.kt` | 🟢 | ⬜ |
| 4 | Diameter of Binary Tree | `tree/binary_tree/DiameterOfBinaryTree.kt` | 🟡 | ⬜ |
| 5 | Subtree of Another Tree | `tree/binary_tree/SubtreeOfAnotherTree.kt` | 🟢 | ⬜ |
| 6 | Lowest Common Ancestor | `tree/binary_tree/LowestCommonAncestor.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Most tree problems = "what can I learn from left and right subtrees?" Diameter = max(leftHeight + rightHeight) across all nodes.

---

### Day 40 — BST Operations & Matrix
**Topic:** BST insert/delete, matrix manipulation

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Insert into BST | `tree/bst/InsertIntoBST.kt` | 🟡 | ⬜ |
| 2 | Delete Node in BST | `tree/bst/DeleteNodeInBST.kt` | 🟡 | ⬜ |
| 3 | Kth Smallest Element in BST | `tree/bst/KthSmallestElementInBST.kt` | 🟡 | ⬜ |
| 4 | Set Matrix Zeroes | `array/matrix/SetMatrixZeroes.kt` | 🟡 | ⬜ |
| 5 | Rotate Image | `array/matrix/RotateImage.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** BST delete — find inorder successor (min in right subtree). Matrix — use first row/col as markers (Set Zeroes), transpose + reverse rows (Rotate Image).

---

### Day 41 — Matrix, String Parsing & Simulation
**Topic:** Matrix, string conversion, simulation

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Spiral Matrix | `array/matrix/SpiralMatrix.kt` | 🟡 | ⬜ |
| 2 | Game of Life | `array/matrix/GameOfLife.kt` | 🟡 | ⬜ |
| 3 | String to Integer (atoi) | `array/string_parsing/StringToInteger.kt` | 🟡 | ⬜ |
| 4 | Integer to Roman | `array/string_parsing/IntegerToRoman.kt` | 🟡 | ⬜ |
| 5 | Longest Palindromic String | `array/string_parsing/LongestPalidromString.kt` | 🟡 | ⬜ |
| 6 | ZigZag Conversion | `array/string_parsing/ZigZagConversionString.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Spiral — four boundaries (top/bottom/left/right). Game of Life — intermediate states (2=dead→live, 3=live→dead). String parsing — handle overflow and edge cases.

---

### Day 42 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 6 that were ❌ or 🔄
2. Write down the 3 most important patterns:
   - Backtracking: CHOOSE → EXPLORE → UNDO (draw recursion tree first!)
   - Tree Recursion: "What can I learn from left and right subtrees?"
   - Matrix: Use markers/intermediate states for in-place modification
3. PRIORITY REVIEW: Subsets + Permutations + Validate BST
```

---

# 🏗️ PHASE 4: DYNAMIC PROGRAMMING (Weeks 7-9)

> **Goal:** Master DP — the most tested and highest-leverage topic in FAANG interviews.

---

## Week 7: 1D DP — Counting & Optimization

### Day 43 — 1D DP: Counting Ways (Fibonacci Pattern)
**Topic:** "How many ways?" = SUM of previous states

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Climbing Stairs | `dp/one_d/ClimbingStairs.kt` | 🟢 | ⬜ |
| 2 | Min Cost Climbing Stairs | `dp/one_d/MinCostClimbingStairs.kt` | 🟢 | ⬜ |
| 3 | Decode Ways | `dp/one_d/DecodeWays.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Climbing Stairs = Fibonacci! `ways(n) = ways(n-1) + ways(n-2)`. Decode Ways = Climbing Stairs with CONSTRAINTS (valid 1-digit, valid 2-digit). ALWAYS start with brute force recursion, then memoize, then tabulate.

---

### Day 44 — 1D DP: Optimization (Max/Min Choices)
**Topic:** "Max/Min?" = MAX/MIN of choices at each step

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | House Robber | `dp/one_d/HouseRobber.kt` | 🟡 | ⬜ |
| 2 | House Robber II (Circular) | `dp/one_d/HouseRobberII.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** House Robber — `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` (ROB or SKIP). House Robber II — circular! Break into two linear: `max(rob[0..n-2], rob[1..n-1])`. Space optimize to O(1) with two variables.

---

### Day 45 — 1D DP: Minimization
**Topic:** "Min steps/coins?" = MIN across all choices

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Coin Change (Min Coins) | `dp/one_d/CoinChange.kt` | 🟡 | ⬜ |
| 2 | Coin Change II (Combinations) | `dp/one_d/CoinChangeII.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Coin Change — `dp[a] = min(dp[a-coin] + 1)` for each coin. Initialize with "infinity". Coin Change II — `dp[a] += dp[a - coin]`. CRITICAL: coins OUTER loop, amounts INNER loop → counts COMBINATIONS.

---

### Day 46 — 1D DP: Counting with Constraints
**Topic:** "How many combinations?" with word/string constraints

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Word Break | `dp/one_d/WordBreak.kt` | 🟡 | ⬜ |
| 2 | 🔄 Re-solve Climbing Stairs + House Robber from memory | — | — | ⬜ |

**🔑 Key Takeaway:** Word Break = Climbing Stairs GENERALIZED! Instead of fixed steps (1,2), step sizes = word lengths that match. `dp[i] = true if dp[i-word.length] AND s[i-word.len..i] == word`.

---

### Day 47 — 1D DP: Full Review & Trace
**Topic:** Trace through DP tables by hand — build intuition

```
EXERCISE: For each problem, trace the DP table by hand with a small example:

1. Climbing Stairs: n=5
   dp = [1, 1, 2, 3, 5, 8]

2. House Robber: nums = [2,7,9,3,1]
   dp = [2, 7, 11, 11, 12]

3. Coin Change: coins = [1,3,4], amount = 6
   dp = [0, 1, 2, 1, 1, 2, 2]

4. Decode Ways: s = "226"
   dp = [1, 1, 2, 3]

After tracing, re-implement each from memory.
```

---

### Day 48 — 2D DP: Grid Paths
**Topic:** 2D version of Climbing Stairs — grid movement

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Unique Paths | `dp/two_d/UniquePaths.kt` | 🟡 | ⬜ |
| 2 | Minimum Path Sum | `dp/two_d/MinimumPathSum.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Unique Paths — `dp[r][c] = dp[r-1][c] + dp[r][c-1]` (2D Climbing Stairs). Min Path Sum — same but MINIMIZE cost (2D Min Cost Climbing Stairs). Space optimize to 1 row.

---

### Day 49 — 2D DP: String Comparison
**Topic:** Compare two strings = 2D DP table

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Longest Common Subsequence | `dp/two_d/LongestCommonSubsequence.kt` | 🟡 | ⬜ |
| 2 | Edit Distance | `dp/two_d/EditDistance.kt` | 🟡 | ⬜ |
| 3 | Longest Palindromic Subsequence | `dp/two_d/LongestPalindromicSubsequence.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** LCS — match: `1 + dp[i-1][j-1]`, no match: `max(dp[i-1][j], dp[i][j-1])`. Edit Distance — 3 operations when no match: `1 + min(insert, delete, replace)`. LPS(s) = LCS(s, s.reversed()).

---

## Week 9: Subsequence DP & DP Review

### Day 50 — Subsequence DP: LIS & Subset Sum
**Topic:** Selection + optimization problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Longest Increasing Subsequence | `dp/subsequence/LongestIncreasingSubsequence.kt` | 🟡 | ⬜ |
| 2 | Partition Equal Subset Sum | `dp/subsequence/PartitionEqualSubsetSum.kt` | 🟡 | ⬜ |
| 3 | Target Sum | `dp/subsequence/TargetSum.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** LIS — O(N log N) patience sorting: maintain `tails[]`, binary search for first ≥ num. Partition — 0/1 knapsack: `dp[t] = dp[t] \|\| dp[t-num]`, iterate target in REVERSE. Target Sum = Partition in disguise: count subsets summing to (target+total)/2.

---

### Day 51 — DP Mastery Review
**Topic:** Re-solve all DP problems from memory, focus on the optimization ladder

```
DP OPTIMIZATION LADDER — for each problem, can you write all 4 levels?

1. Brute Force (Recursion)     → O(2^N) exponential
2. Memoization (Top-down)      → O(N) or O(N²) with cache
3. Tabulation (Bottom-up)      → O(N) or O(N²) no recursion
4. Space-optimized              → O(1) or O(N) space

PRACTICE: Pick 3 problems and write ALL 4 levels:
- Climbing Stairs
- House Robber
- Coin Change
```

---

### Day 52 — Linked List: Advanced Operations
**Topic:** Merge, sort, copy, partition

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Merge Two Sorted Lists | `linked_list/single/MergeTwoSortedLists.kt` | 🟢 | ⬜ |
| 2 | Reorder List | `linked_list/single/ReorderList.kt` | 🟡 | ⬜ |
| 3 | Remove Duplicates from Sorted List | `linked_list/single/RemoveDuplicatesFromSortedList.kt` | 🟢 | ⬜ |
| 4 | Remove Duplicates II | `linked_list/single/RemoveDuplicatesFromSortedListII.kt` | 🟡 | ⬜ |
| 5 | Partition List | `linked_list/single/PatiotionList.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Reorder = find mid + reverse 2nd half + merge alternately. Remove Duplicates II = dummy head + skip all duplicates. Partition = two dummy heads (< x and ≥ x).

---

### Day 53 — Linked List: Advanced & Doubly Linked List
**Topic:** Copy with random pointer, rotate, doubly LL

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Copy List with Random Pointer | `linked_list/single/CopyListWithRandomPointer.kt` | 🟡 | ⬜ |
| 2 | Rotate Linked List | `linked_list/single/RotateLinkedList.kt` | 🟡 | ⬜ |
| 3 | Add Two Numbers | `linked_list/single/AddTwoNumbers.kt` | 🟡 | ⬜ |
| 4 | Sort List | `linked_list/single/SortList.kt` | 🟡 | ⬜ |
| 5 | LRU Cache | `linked_list/double/LeastRecentlyUsedLRU.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Copy Random — interleave copies, set random pointers, separate. LRU = HashMap + Doubly LL (O(1) get/put). THE most asked design question!

---

### Day 54 — Doubly Linked List & Remaining
**Topic:** LFU, circular linked list, design

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | LFU Cache | `linked_list/double/LeastFrequentlyUsedLFU.kt` | 🔴 | ⬜ |
| 2 | Flatten Multilevel DLL | `linked_list/double/FlatternDoubleLLMultiLevel.kt` | 🟡 | ⬜ |
| 3 | Design HashSet | `linked_list/single/DesignHashSet.kt` | 🟢 | ⬜ |
| 4 | Sorted Insert in Circular LL | `linked_list/circular/SortedInsertInCircularLinkedList.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** LFU = HashMap + Freq map + Doubly LL. Flatten = stack or recursion (depth-first). Circular LL insert = find correct position in cycle.

---

### Day 55 — Voting/Floyd, Frequency Count & Simulation
**Topic:** Mathematical tricks, simulation problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Majority Element | `array/voting_floyd/MajorityOfElements.kt` | 🟢 | ⬜ |
| 2 | Majority Element II | `array/voting_floyd/MajorityElementII.kt` | 🟡 | ⬜ |
| 3 | Find Duplicate Number | `array/voting_floyd/FindDupplicateNumber.kt` | 🟡 | ⬜ |
| 4 | Missing Number | `array/voting_floyd/MissingNumberInArray.kt` | 🟢 | ⬜ |
| 5 | Add Binary | `simulation/AddBinary.kt` | 🟢 | ⬜ |
| 6 | Text Justification | `simulation/TextJustification.kt` | 🔴 | ⬜ |

**🔑 Key Takeaway:** Boyer-Moore Voting — candidate + count, majority always survives. Floyd's Cycle — treat array as LL, duplicate creates cycle. XOR — a^a=0, a^0=a.

---

### Day 56 — 🔄 Phase 3-4 Review & Spaced Repetition

```
1. Re-solve 5 problems from Phases 3-4 that were ❌ or 🔄
2. Focus on your weakest topics from the past 8 weeks
3. Write down the DP problem-solving checklist:
   □ What is the state? (dp[i] or dp[i][j])
   □ What is the recurrence?
   □ What are the base cases?
   □ What is the answer?
   □ Can I space-optimize?
4. PRIORITY REVIEW: House Robber + Coin Change + LCS + Edit Distance
```

---

# 🏗️ PHASE 5: ADVANCED DATA STRUCTURES (Weeks 10-11)

> **Goal:** Master the data structures that solve the hardest problems.

---

## Week 10: Heap, Trie & Graph BFS/DFS

### Day 57 — Heap: Top K & Kth Element
**Topic:** Min-heap of size K for "top/bottom K" problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Top K Frequent Elements | `heap/TopKFrequentElements.kt` | 🟡 | ⬜ |
| 2 | Kth Largest Element | `heap/KthLargestElement.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Min-heap of size K — push each element, pop when size > K. Root = Kth largest. QuickSelect = O(N) average (like QuickSort but only one partition).

---

### Day 58 — Heap: Merge & Median
**Topic:** K-way merge, two-heap median

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Merge K Sorted Lists | `heap/MergeKSortedLists.kt` | 🔴 | ⬜ |
| 2 | Find Median from Data Stream | `heap/FindMedianFromDataStream.kt` | 🔴 | ⬜ |

**🔑 Key Takeaway:** Merge K Sorted — push all heads into min-heap, pop min, push next. O(N log K). Median — max-heap (smaller half) + min-heap (larger half). Balance sizes.

---

### Day 59 — Trie: Prefix Tree
**Topic:** O(M) search and prefix operations independent of stored count

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Implement Trie | `trie/ImplementTrie.kt` | 🟡 | ⬜ |
| 2 | Word Search II | `trie/WordSearchII.kt` | 🔴 | ⬜ |

**🔑 Key Takeaway:** Trie — 26 children + isEnd flag. Insert/search/startsWith all O(M). Word Search II = Trie + Backtracking — build trie from words, DFS board once.

---

### Day 60 — Graph: BFS/DFS
**Topic:** Grid = implicit graph, explore all reachable nodes

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Number of Islands | `graph/bfs_dfs/NumberOfIslands.kt` | 🟡 | ⬜ |
| 2 | Clone Graph | `graph/bfs_dfs/CloneGraph.kt` | 🟡 | ⬜ |
| 3 | Pacific Atlantic Water Flow | `graph/bfs_dfs/PacificAtlanticWaterFlow.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Number of Islands — DFS/BFS, mark visited by changing '1' → '2'. Clone Graph — HashMap(old→new) + DFS. Pacific Atlantic — start from oceans, flow inward!

---

### Day 61 — Graph: Shortest Path
**Topic:** Weighted (Dijkstra) vs unweighted (BFS) shortest path

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Dijkstra's Algorithm | `graph/shortest_path/Dijkstra.kt` | 🟡 | ⬜ |
| 2 | Word Ladder | `graph/shortest_path/WordLadder.kt` | 🔴 | ⬜ |

**🔑 Key Takeaway:** Dijkstra — min-heap of (dist, node), process smallest first. Word Ladder — BFS on implicit graph (words differ by 1 letter). Bidirectional BFS for optimization.

---

### Day 62 — Graph: Topological Sort & Union Find
**Topic:** DAG ordering, connected components

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Course Schedule | `graph/topological_sort/CourseSchedule.kt` | 🟡 | ⬜ |
| 2 | Course Schedule II | `graph/topological_sort/CourseScheduleII.kt` | 🟡 | ⬜ |
| 3 | Number of Provinces | `graph/union_find/NumberOfProvinces.kt` | 🟡 | ⬜ |
| 4 | Graph Valid Tree | `graph/union_find/GraphValidTree.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Topological Sort — Kahn's algorithm (indegree) or DFS + stack. Union Find — union(a,b) merges sets, find(a) with path compression. Tree = n-1 edges + connected.

---

### Day 63 — Remaining Linked List & Simulation
**Topic:** Catch up on remaining problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Delete Node in LinkedList | `linked_list/single/DeleteNodeInLinkedList.kt` | 🟢 | ⬜ |
| 2 | Linked List Binary to Integer | `linked_list/single/LinkedListBinaryToInteger.kt` | 🟢 | ⬜ |
| 3 | Convert Sorted List to BST | `linked_list/single/ConvertSortedListToBinarySearchTree.kt` | 🟡 | ⬜ |
| 4 | Add Strings | `simulation/AddString.kt` | 🟢 | ⬜ |
| 5 | Spiral Matrix II | `simulation/SpiralMatrixII.kt` | 🟡 | ⬜ |
| 6 | FizzBuzz | `simulation/FizzBuzz.kt` | 🟢 | ⬜ |

**🔑 Key Takeaway:** Sorted List to BST — find mid → root, recurse on left/right halves. Simulation — directly simulate the described process.

---

### Day 64 — Remaining Problems & Doubly Linked List
**Topic:** Catch up on remaining DLL and circular LL problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Reverse Doubly Linked List | `linked_list/double/ReverseDoublyLinkedList.kt` | 🟢 | ⬜ |
| 2 | Delete Node in DLL | `linked_list/double/DeleteNodeInDoublyLinkedList.kt` | 🟢 | ⬜ |
| 3 | Find Pairs with Given Sum (DLL) | `linked_list/double/FindPairsWithGivenSum.kt` | 🟡 | ⬜ |
| 4 | Remove Duplicates from Sorted DLL | `linked_list/double/RemoveDuplicatesFromSortedDLL.kt` | 🟢 | ⬜ |
| 5 | Josephus Problem | `linked_list/circular/JosephusProblem.kt` | 🟡 | ⬜ |
| 6 | Split Circular Linked List | `linked_list/circular/SplitCircularLinkedList.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** DLL — prev + next pointers enable O(1) operations at both ends. Josephus — mathematical recurrence: J(n,k) = (J(n-1,k) + k) % n.

---

### Day 65 — Remaining Frequency Count & Interview Problems
**Topic:** Catch up on remaining problems

| # | Problem | File | Difficulty | Status |
|---|---------|------|-----------|--------|
| 1 | Count the Number of Pairs | `array/frequency_count/CountTheNumberOfPairs.kt` | 🟡 | ⬜ |
| 2 | Finding Fair Pairs | `array/frequency_count/FindingFairPairs.kt` | 🟡 | ⬜ |
| 3 | Plus One | `interview_problem/PlusOne.kt` | 🟢 | ⬜ |
| 4 | Add Digits | `simulation/AddDigits.kt` | 🟢 | ⬜ |
| 5 | My LinkedList | `linked_list/single/MyLinkedList.kt` | 🟡 | ⬜ |

**🔑 Key Takeaway:** Fair Pairs — sort + two-pointer (hybrid). Plus One — handle carry from end. Add Digits — digital root: 1 + (n-1) % 9.

---

### Day 66 — 🔄 Phase 5 Review & Spaced Repetition

```
1. Re-solve 3 problems from Weeks 10-11 that were ❌ or 🔄
2. Write down the 3 most important patterns:
   - Heap: "Top K" → min-heap of size K, "Running median" → two heaps
   - Trie: O(M) prefix operations, build from words + DFS board
   - Graph: BFS (shortest unweighted), DFS (explore all), Union Find (connected)
3. PRIORITY REVIEW: Merge K Sorted Lists + Find Median + Number of Islands
```

---

# 🏗️ PHASE 6: MOCK INTERVIEWS & FAANG PREP (Week 12+)

> **Goal:** Combine patterns to solve unseen problems under time pressure.

---

## Week 12: FAANG Top Problems & Timed Practice

### Day 67 — FAANG Top 5 (Most Asked)
**Topic:** The problems that appear in almost every FAANG interview

| # | Problem | Topic | Time Limit | Status |
|---|---------|------|-----------|--------|
| 1 | Two Sum | HashSet | 15 min | ⬜ |
| 2 | Longest Substring Without Repeating | Sliding Window | 25 min | ⬜ |
| 3 | Maximum Subarray | Kadane's | 15 min | ⬜ |
| 4 | Merge Intervals | Greedy | 20 min | ⬜ |
| 5 | Valid Parentheses | Stack | 10 min | ⬜ |

**🎯 Goal:** Solve each within the time limit WITHOUT looking at solutions. If stuck > 15 min, review the pattern hint, not the full solution.

---

### Day 68 — FAANG Top 6-10
**Topic:** High-frequency FAANG problems

| # | Problem | Topic | Time Limit | Status |
|---|---------|------|-----------|--------|
| 1 | Climbing Stairs | 1D DP | 10 min | ⬜ |
| 2 | Coin Change | 1D DP | 25 min | ⬜ |
| 3 | Number of Islands | Graph BFS/DFS | 25 min | ⬜ |
| 4 | LCS / Edit Distance | 2D DP | 30 min | ⬜ |
| 5 | Top K Frequent Elements | Heap | 20 min | ⬜ |

---

### Day 69 — FAANG Top 11-15
**Topic:** High-frequency FAANG problems

| # | Problem | Topic | Time Limit | Status |
|---|---------|------|-----------|--------|
| 1 | Subsets / Permutations | Backtracking | 25 min | ⬜ |
| 2 | Word Break | 1D DP | 25 min | ⬜ |
| 3 | Trapping Rain Water | Two Pointer | 25 min | ⬜ |
| 4 | 3Sum | Two Pointer | 25 min | ⬜ |
| 5 | LRU Cache | Doubly Linked List | 30 min | ⬜ |

---

### Day 70 — Pattern Mixing: Combine Two Patterns
**Topic:** Problems that require combining multiple techniques

| # | Problem | Patterns Combined | Time Limit | Status |
|---|---------|------|-----------|--------|
| 1 | Minimum Window Substring | Sliding Window + HashMap | 30 min | ⬜ |
| 2 | Word Search II | Trie + Backtracking | 35 min | ⬜ |
| 3 | Merge K Sorted Lists | Heap + Linked List | 30 min | ⬜ |
| 4 | Find Median from Data Stream | Two Heaps + Design | 30 min | ⬜ |

---

### Day 71 — Speed Round: Easy Problems
**Topic:** Solve easy problems fast to build speed and confidence

```
Set a timer for 45 minutes. Solve as many as possible:

1. Find Smallest or Largest
2. Reverse an Array
3. Maximum Consecutive Ones
4. Contains Duplicates
5. Valid Palindrome
6. Move Zeros to End
7. Climbing Stairs
8. Single Number
9. Reverse Linked List
10. Valid Parentheses
11. Maximum Subarray
12. Best Time to Buy & Sell Stock I
13. Linked List Cycle
14. Merge Two Sorted Lists
15. Middle of Linked List

Target: 10+ problems in 45 minutes
```

---

### Day 72 — Speed Round: Medium Problems
**Topic:** Solve medium problems under time pressure

```
Set a timer for 90 minutes. Solve as many as possible:

1. Two Sum (Unsorted)
2. Two Sum II (Sorted)
3. Three Sum
4. Longest Substring Without Repeating
5. Container With Most Water
6. Group Anagrams
7. Product of Array Except Self
8. House Robber
9. Coin Change
10. Decode Ways
11. Number of Islands
12. Top K Frequent Elements
13. Daily Temperatures
14. Merge Intervals
15. Subsets

Target: 8+ problems in 90 minutes
```

---

### Day 73 — Hard Problem Challenge
**Topic:** Tackle the hardest problems in the repo

| # | Problem | Topic | Time Limit | Status |
|---|---------|------|-----------|--------|
| 1 | Median of Two Sorted Arrays | Binary Search | 40 min | ⬜ |
| 2 | Trapping Rain Water | Two Pointer | 30 min | ⬜ |
| 3 | Largest Rectangle in Histogram | Monotonic Stack | 30 min | ⬜ |
| 4 | Longest Valid Parentheses | Stack | 30 min | ⬜ |
| 5 | Candy | Greedy | 25 min | ⬜ |

---

### Day 74 — Full Mock Interview #1
**Topic:** Simulate a real FAANG interview

```
MOCK INTERVIEW FORMAT (45 minutes):

Problem 1 (Easy - 15 min):
  → Two Sum / Valid Parentheses / Reverse Linked List

Problem 2 (Medium - 30 min):
  → 3Sum / Merge Intervals / Number of Islands

Rules:
- No looking at solutions
- Talk through your approach out loud
- Write clean, compilable code
- Analyze time & space complexity
- Consider edge cases
```

---

### Day 75 — Full Mock Interview #2

```
MOCK INTERVIEW FORMAT (45 minutes):

Problem 1 (Easy - 15 min):
  → Best Time to Buy & Sell Stock / Contains Duplicate / Climbing Stairs

Problem 2 (Medium/Hard - 30 min):
  → Trapping Rain Water / Word Break / LRU Cache
```

---

### Day 76 — Full Mock Interview #3

```
MOCK INTERVIEW FORMAT (45 minutes):

Problem 1 (Easy - 15 min):
  → Maximum Subarray / Valid Parentheses / Middle of Linked List

Problem 2 (Medium/Hard - 30 min):
  → Edit Distance / Find Median from Data Stream / Minimum Window Substring
```

---

### Day 77 — Weakness Day
**Topic:** Focus entirely on your weakest topics

```
1. Look at your progress tracking — find all ❌ and 🔄 problems
2. Pick the 5 problems you struggled with most
3. Re-solve each one from memory
4. For each, write down the KEY INSIGHT in one sentence
5. If still stuck, re-read the solution and try again tomorrow
```

---

### Day 78 — Final Comprehensive Review
**Topic:** Review all patterns one more time

```
PATTERN RECOGNITION QUICK FIRE:
For each keyword, write down the pattern and one example problem:

"Find a pair..."           → ___________ → ___________
"Longest subarray..."      → ___________ → ___________
"Valid parentheses..."     → ___________ → ___________
"Next greater element"     → ___________ → ___________
"Detect cycle"             → ___________ → ___________
"All combinations"         → ___________ → ___________
"Optimal scheduling"       → ___________ → ___________
"Range queries"            → ___________ → ___________
"Count ways / max / min"   → ___________ → ___________
"Compare two strings"      → ___________ → ___________
"Can we select subset?"    → ___________ → ___________
"Top K elements"            → ___________ → ___________
"Prefix matching"          → ___________ → ___________
"Connected components"     → ___________ → ___________
"Course prerequisites"     → ___________ → ___________

Check your answers against the Pattern Recognition Guide in README.md!
```

---

## 📈 Ongoing Practice (After Week 12)

### Daily Maintenance Routine

```
After completing the 12-week plan, maintain your skills with:

1. DAILY (30 min):
   - 1 Easy problem (warmup)
   - 1 Medium problem (keep sharp)

2. WEEKLY (2 hours):
   - 1 Full mock interview (45 min)
   - Review ❌/🔄 problems from the week

3. BI-WEEKLY (3 hours):
   - Solve 1 Hard problem (push your limits)
   - Review a full topic from this guide

4. MONTHLY:
   - Complete a full mock interview cycle (Days 67-78)
   - Identify and strengthen weak areas
```

---

## 🏆 Final Tips

1. **Consistency > Intensity** — 2 hours daily beats 14 hours on weekends
2. **Spaced Repetition** — Re-solve problems on Day 3, Day 7, Day 14 after first solve
3. **Pattern Recognition** — After 50+ problems, you'll start seeing patterns automatically
4. **Don't Memorize, Understand** — If you forget a solution, understanding the pattern lets you re-derive it
5. **The 80/20 Rule** — Sliding Window + Two Pointer + DP + BFS/DFS + Heap solve ~80% of FAANG questions
6. **Talk Out Loud** — Practice explaining your approach as you code (essential for interviews)
7. **Time Yourself** — 20 min Easy, 35 min Medium, 45 min Hard — build speed gradually

> 💡 **Remember:** Every FAANG engineer started exactly where you are now. The difference is consistent practice. Follow this guide daily, and you'll be interview-ready in 12 weeks. Good luck! 🚀
