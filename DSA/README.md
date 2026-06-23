# 📚 DSA Learning Repository — Complete Guide

A comprehensive Kotlin-based Data Structures & Algorithms learning repository, organized by **technique/pattern** with progressive problem solutions (Brute Force → Optimal).

---

## 📑 Table of Contents

1. [Array (Pattern-Based)](#1-array-pattern-based)
   - [1.1 Linear Scan](#11-linear-scan)
   - [1.2 Prefix Sum](#12-prefix-sum)
   - [1.3 Kadane's Algorithm](#13-kadanes-algorithm)
   - [1.4 Two Pointer In-Place](#14-two-pointer-in-place)
   - [1.5 Greedy](#15-greedy)
   - [1.6 Voting & Floyd's Algorithm](#16-voting--floyds-algorithm)
   - [1.7 Matrix](#17-matrix)
   - [1.8 String Parsing](#18-string-parsing)
   - [1.9 Binary Search](#19-binary-search)
   - [1.10 HashSet Lookup](#110-hashset-lookup)
   - [1.11 Complement Search](#111-complement-search)
   - [1.12 Set Operations](#112-set-operations)
   - [1.13 Frequency Count](#113-frequency-count)
   - [1.14 Bit Manipulation](#114-bit-manipulation)
   - [1.15 Two Pointer](#115-two-pointer)
   - [1.16 Sliding Window](#116-sliding-window)
2. [Stack](#2-stack)
3. [Linked List](#3-linked-list)
4. [Doubly Linked List](#4-doubly-linked-list)
5. [Sorting](#5-sorting)
6. [Backtracking](#6-backtracking)
7. [Greedy Algorithm](#7-greedy-algorithm)
8. [Simulation](#8-simulation)
9. [Dynamic Programming](#9-dynamic-programming)
   - [9.1 1D DP](#91-1d-dp)
   - [9.2 2D DP](#92-2d-dp)
   - [9.3 Subsequence DP](#93-subsequence-dp)
10. [Heap / Priority Queue](#10-heap--priority-queue)
11. [Trie (Prefix Tree)](#11-trie-prefix-tree)
12. [Quick Reference — Complexity Cheatsheet](#12-quick-reference--complexity-cheatsheet)
13. [Problem-Solving Strategy](#13-problem-solving-strategy)
14. [What's Next?](#14-whats-next)
15. [🗺️ DSA Mastery Roadmap — Where to Start & How to Study](#15-️-dsa-mastery-roadmap--where-to-start--how-to-study)
16. [🧠 Pattern Recognition Master Guide](#16-️-pattern-recognition-master-guide)

---

## 1. Array (Pattern-Based)

> **Why pattern-based?** FAANG interviews test your ability to **recognize patterns**. Grouping problems by the underlying technique helps you build pattern recognition — the #1 skill for cracking coding interviews.

> **Every problem includes Brute Force → Better → Optimal progression** to build optimization intuition.

```
📁 array/
├── 📂 linear_scan/          → Simple O(N) traversal, find min/max/count
├── 📂 prefix_sum/            → Precompute cumulative sums for O(1) range queries
├── 📂 kadane_algorithm/      → Maximum subarray sum/product (local vs global max)
├── 📂 two_pointer_inplace/   → Modify array in-place using slow/fast pointers
├── 📂 greedy/                → Make locally optimal choice at each step
├── 📂 voting_floyd/          → Boyer-Moore Voting, Floyd's Cycle Detection, XOR
├── 📂 matrix/                → 2D array manipulation
├── 📂 string_parsing/        → String-to-number, Roman numerals, palindromes
├── 📂 binary_search/         → Binary search on sorted/partitioned data
├── 📂 hashset_lookup/        → Check existence using HashSet
├── 📂 complement_search/     → Find pairs that satisfy a sum condition
├── 📂 set_operations/        → Intersection, difference, grouping
├── 📂 frequency_count/       → Count occurrences, find top K
├── 📂 bit_manipulation/      → XOR, bit counting tricks
├── 📂 two_pointer/           → Two pointers from ends or different speeds
└── 📂 sliding_window/        → Fixed/variable window sliding across data
```

---

### 1.1 Linear Scan

**Pattern:** Visit each element once. Track running values (min, max, count, etc.).

| File | Problem | Time | Key Technique |
|------|---------|------|---------------|
| `FindSmallestOrLargest.kt` | Find min & max in array | O(N) | Track min/max while scanning |
| `FindSecondLargestOrSmallestElement.kt` | Find 2nd min & max | O(N) | Track top 2 values in single pass |
| `ReverseAnArray.kt` | Reverse array in-place | O(N) | Two-pointer swap from ends |
| `ReverseAString.kt` | Reverse a string | O(N) | Two-pointer swap |
| `FindMaximumConsecutiveOnes.kt` | Max consecutive 1s | O(N) | Reset count on 0, track max |
| `CountFrequencyOfElement.kt` | Count element frequency | O(N) | HashMap for O(1) lookup |
| `FirstRepeatingElement.kt` | First repeating element | O(N) | HashSet, return first duplicate |
| `CountNegativesInMatrix.kt` | Count negatives in 2D grid | O(M+N) | Start from top-right corner |
| `CheckArraySortedOrRotate.kt` | Check if sorted & rotated | O(N) | Count drops (should be ≤ 1) |
| `HeightChecker.kt` | Students out of height order | O(N log N) | Compare with sorted copy |
| `MinimumAbsoluteDifference.kt` | Min absolute diff between any pair | O(N log N) | Sort, check adjacent pairs |
| `NumberOfDistinctAverages.kt` | Count distinct averages | O(N log N) | Sort, two-pointer + HashSet |
| `LeadersInArray.kt` | Elements greater than all to their right | O(N) | Right-to-left scan, track max |
| `FindingPivotIndex.kt` | Index where left sum == right sum | O(N) | Total sum - left sum == right sum |

---

### 1.2 Prefix Sum

**Pattern:** Precompute cumulative sums so any range sum query is O(1). Essential for problems involving repeated range queries or "product except self" patterns.

```
Original:  [2, 4, 1, 3, 5]
Prefix:    [2, 6, 7, 10, 15]

Query: Sum from index 1 to 3 → prefix[3] - prefix[0] = 10 - 2 = 8
```

| File | Problem | Time | Space | FAANG |
|------|---------|------|-------|-------|
| `RunningSum1DArray.kt` | Running sum of 1D array | O(N) | O(1) | ⭐⭐ |
| `PrefixSumQuery.kt` | Range sum query - immutable | O(N) precompute, O(1) query | O(N) | ⭐⭐⭐ |
| `ProductOfArrayExceptSelf.kt` | Product of array except self | O(N) | O(1)* | ⭐⭐⭐⭐⭐ |
| `SubarraySumEqualsK.kt` | Subarray sum equals K | O(N) | O(N) | ⭐⭐⭐⭐⭐ |
| `ContiguousArray.kt` | Longest subarray with equal 0s and 1s | O(N) | O(N) | ⭐⭐⭐⭐ |

> 🔑 **Product of Array Except Self** — One of the MOST asked FAANG questions. Uses prefix + suffix products. The O(1) space solution uses the output array itself for prefix, then a running variable for suffix.

> 🔑 **Subarray Sum Equals K** — Top 10 most asked FAANG question. Uses prefix sum + HashMap. For each index j, count how many previous prefix sums equal `prefix[j] - k`. This is the key pattern for ALL "count subarrays with sum" problems.

---

### 1.3 Kadane's Algorithm

**Pattern:** Find the maximum (or minimum) subarray sum/product. At each index, decide: extend the previous subarray OR start fresh?

```
For SUM:   local_max = max(arr[i], local_max + arr[i])
For PRODUCT: Must track BOTH max AND min (negative × negative = positive!)
```

| File | Problem | Time | Space | FAANG |
|------|---------|------|-------|-------|
| `MaximumSubArray.kt` | Maximum subarray sum | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `MaximumProductSubarray.kt` | Maximum subarray product | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `MaximumSumCircularSubarray.kt` | Max sum circular subarray | O(N) | O(1) | ⭐⭐⭐⭐ |

> 🔑 **Maximum Product Subarray** — Unlike Kadane's for sum, you MUST track both `maxSoFar` and `minSoFar` at each position. Why? Because `minSoFar * negative = positive` could become the new max!

> 🔑 **Maximum Sum Circular Subarray** — Circular max = max(normal Kadane, totalSum - minSubarraySum). The circular case wraps around, meaning the "gap" in the middle is the minimum subarray. Edge case: if all negative, return max element.

---

### 1.4 Two Pointer In-Place

**Pattern:** Modify the array in-place without extra space. Use slow/fast pointers or read/write pointers to overwrite elements.

| File | Problem | Time | Space | Key Technique |
|------|---------|------|-------|---------------|
| `RemoveDuplicateFromSortedArray.kt` | Remove duplicates (keep one) | O(N) | O(1) | Slow/fast pointer |
| `RemoveDuplicateseTWO.kt` | Remove duplicates (keep two) | O(N) | O(1) | Slow/fast with count |
| `RemoveElement.kt` | Remove all occurrences of val | O(N) | O(1) | Read/write pointer |
| `ReplaceGratestElementOnRight.kt` | Replace with greatest on right | O(N) | O(1) | Right-to-left with max tracker |
| `SortedSquares.kt` | Squares of sorted array | O(N) | O(N) | Two pointers from ends (negatives!) |
| `RotateArray.kt` | Rotate array right by k | O(N) | O(1) | 3-step reversal trick |
| `NextPermutation.kt` | Next lexicographic permutation | O(N) | O(1) | Find pivot, swap, reverse suffix |

> 🔑 **Rotate Array** — The reversal trick: reverse all → reverse first k → reverse remaining. This achieves O(1) space!

> 🔑 **Sorted Squares** — Two pointers from both ends because negatives square to large positives. Fill result from the end (largest first).

> 🔑 **Next Permutation** — 3-step: (1) Find rightmost decreasing pair (pivot), (2) Swap pivot with smallest larger element from right, (3) Reverse suffix after pivot. If no pivot exists, reverse entire array.

---

### 1.5 Greedy

**Pattern:** Make the locally optimal choice at each step. Works when the problem has the **greedy-choice property** (local optimum leads to global optimum).

| File | Problem | Time | Space | FAANG | Key Insight |
|------|---------|------|-------|-------|-------------|
| `BestTimeToBuyAndSellStockI.kt` | Max profit (one transaction) | O(N) | O(1) | ⭐⭐⭐⭐ | Track min_price, max_profit |
| `BestTimeToBuyAndSellStockII.kt` | Max profit (unlimited transactions) | O(N) | O(1) | ⭐⭐⭐⭐ | Sum all positive daily differences |
| `JumpGame.kt` | Can you reach the last index? | O(N) | O(1) | ⭐⭐⭐⭐⭐ | Track farthest reachable index |
| `JumpGameII.kt` | Min jumps to reach last index | O(N) | O(1) | ⭐⭐⭐⭐⭐ | BFS-like greedy: track levels & farthest |
| `GasStation.kt` | Find starting gas station | O(N) | O(1) | ⭐⭐⭐⭐⭐ | If tank < 0, skip all stations before |
| `MergeIntervals.kt` | Merge overlapping intervals | O(N log N) | O(N) | ⭐⭐⭐⭐⭐ | Sort by start, merge if overlap |
| `Candy.kt` | Min candies for children ratings | O(N) | O(N) | ⭐⭐⭐⭐⭐ | Two-pass: L→R then R→L, take max |
| `InsertInterval.kt` | Insert and merge new interval | O(N) | O(N) | ⭐⭐⭐⭐⭐ | Three phases: before, merge, after |
| `MeetingRooms.kt` | Meeting Rooms I & II | O(N log N) | O(N) | ⭐⭐⭐⭐⭐ | Sort by start + sweep line / min-heap |

> 🔑 **Jump Game** — Two approaches: (1) Backward: track leftmost "goal" that can reach the end, (2) Forward: track farthest reachable index. If current index > farthest → stuck!

> 🔑 **Gas Station** — If total gas ≥ total cost, solution exists. When tank goes negative at station i, ALL stations from start to i are invalid starting points → skip them.

> 🔑 **Merge Intervals** — One of the MOST asked Meta/Google questions. Sort by start time, then merge greedily.

---

### 1.6 Voting & Floyd's Algorithm

**Pattern:** Clever mathematical tricks that avoid extra space. Boyer-Moore finds majority in O(1) space. Floyd's cycle detection finds duplicates. XOR finds single numbers.

| File | Problem | Time | Space | Key Technique |
|------|---------|------|-------|---------------|
| `MajorityOfElements.kt` | Element appearing > n/2 times | O(N) | O(1) | Boyer-Moore Voting |
| `MajorityElementII.kt` | Elements appearing > n/3 times | O(N) | O(1) | Extended Boyer-Moore (2 candidates) |
| `FindDupplicateNumber.kt` | Find duplicate in [1..n] | O(N) | O(1) | Floyd's Cycle Detection (treat as LL) |
| `MissingNumberInArray.kt` | Find missing number in [0..n] | O(N) | O(1) | XOR: a^a=0, or Sum formula |

> 🔑 **Boyer-Moore Voting** — Maintain a candidate and count. If count==0, adopt new candidate. If element==candidate, count++. Else count--. The majority always survives.

> 🔑 **Floyd's Cycle Detection** — Treat array as linked list (index → value → index...). A duplicate creates a cycle. Use slow/fast pointer to find cycle, then find entry point.

> 🔑 **XOR Trick** — `a ^ a = 0` and `a ^ 0 = a`. XOR all numbers: pairs cancel out, single number remains.

---

### 1.7 Matrix

**Pattern:** 2D array manipulation. Key challenge is handling row/column dependencies without extra space.

| File | Problem | Time | Space | FAANG | Key Insight |
|------|---------|------|-------|-------|-------------|
| `SetMatrixZeroes.kt` | Set row/col to 0 if element is 0 | O(M×N) | O(1) | ⭐⭐⭐⭐ | Use first row/col as markers |
| `SpiralMatrix.kt` | Traverse matrix in spiral order | O(M×N) | O(1) | ⭐⭐⭐⭐ | Four boundaries: top/bottom/left/right |
| `RotateImage.kt` | Rotate matrix 90° clockwise | O(N²) | O(1) | ⭐⭐⭐⭐⭐ | Transpose + Reverse each row |
| `GameOfLife.kt` | Game of Life (in-place) | O(M×N) | O(1) | ⭐⭐⭐⭐ | Intermediate states: 0→1=2, 1→0=3 |

> 🔑 **Set Matrix Zeroes** — Use first row and first column as marker arrays. Track separately if first row/col themselves need zeroing. Process markers first, then zero first row/col last.

> 🔑 **Rotate Image** — Two approaches: (1) Transpose + Reverse each row (easier to remember), (2) Layer-by-layer 4-way swap. Both are O(1) space.

> 🔑 **Game of Life** — In-place trick: use intermediate values (2 = dead→live, 3 = live→dead) to encode both old and new state simultaneously. First pass marks transitions, second pass converts to final values.

---

### 1.8 String Parsing

**Pattern:** Convert between string and number representations. Handle edge cases (overflow, invalid characters, etc.).

| File | Problem | Time | Key Technique |
|------|---------|------|---------------|
| `StringToInteger.kt` | String to integer (atoi) | O(N) | Parse with overflow check |
| `IntegerToRoman.kt` | Integer to Roman numeral | O(1) | Greedy with value-symbol map |
| `LongestPalidromString.kt` | Longest palindromic substring | O(N²) | Expand around center |
| `ZigZagConversionString.kt` | Zigzag conversion | O(N) | Track row direction (down/up) |

---

### 1.9 Binary Search

**Pattern:** Search in O(log N) on sorted/partitioned data. The key is identifying the monotonic condition.

| File | Problem | Time | Key Technique |
|------|---------|------|---------------|
| `MedianOfTwoSortedSubArray.kt` | Median of two sorted arrays | O(log(min(m,n))) | Binary search on partition |
| `SearchInRotatedSortedArray.kt` | Search in rotated sorted array | O(log N) | Find sorted half, check if target in range |
| `FindMinimumInRotatedSortedArray.kt` | Find min in rotated sorted array | O(log N) | Compare mid with right, min is in unsorted half |

> 🔑 **Median of Two Sorted Arrays** — One of the hardest LeetCode problems. Binary search on the smaller array to find the correct partition point where all left elements ≤ all right elements.

> 🔑 **Search in Rotated Sorted Array** — Must-know FAANG problem. At any mid, one half is always sorted. Find which half, check if target lies in that range. If yes, search there; otherwise search the other half.

> 🔑 **Find Minimum in Rotated Sorted Array** — Compare nums[mid] with nums[right]. If mid > right, minimum is in right half. If mid ≤ right, minimum is at mid or in left half. The minimum is always in the unsorted half.

---

### 1.10 HashSet Lookup

**Core Idea:** Use a Hash Map / Hash Set to trade **space for time**. O(1) lookups instead of O(N) scans.

**Pattern:** Use HashSet for O(1) existence checks. "Have I seen this before?"

| File | Problem | Key Technique | FAANG |
|------|---------|---------------|-------|
| `ContainsDuplicates.kt` | Check for duplicates | HashSet: if already seen → duplicate | ⭐⭐⭐ |
| `CheckIfNDoubleExits.kt` | Check if N and 2*N both exist | HashSet: check if 2*arr[i] exists | ⭐⭐⭐ |
| `CountDistinctElements.kt` | Count distinct elements | HashSet.size | ⭐⭐ |
| `LongestConsecutiveSequence.kt` | Longest consecutive sequence | HashSet + only start from sequence beginnings | ⭐⭐⭐⭐⭐ |
| `ValidSudoku.kt` | Validate Sudoku board | HashSet per row/col/3x3 box | ⭐⭐⭐⭐ |

> 🔑 **Longest Consecutive Sequence** — Top 20 most asked. Put all numbers in HashSet. Only start counting from num if (num-1) is NOT in set (it's a sequence start). Count consecutive numbers. Total O(N).

> 🔑 **Valid Sudoku** — Use 9 HashSets for rows, 9 for columns, 9 for 3×3 boxes. Box index = (row/3)*3 + (col/3). If a digit already exists in its row, column, or box → invalid.

### 1.11 Complement Search

**Pattern:** For each element, look up its "complement" (target - current) in a HashMap. The key pattern for ALL Two Sum variants.

| File | Problem | Key Technique | FAANG |
|------|---------|---------------|-------|
| `TwoSumLevelOneWithoutSortedArray.kt` | Two Sum (unsorted) | HashMap: store complement (target - arr[i]) | ⭐⭐⭐⭐⭐ |
| `FindingPairsWithCertainSum.kt` | Find pairs with given sum | HashMap complement search | ⭐⭐⭐ |
| `TwoSumII.kt` | Two Sum (sorted array) | Two pointers from both ends, O(1) space | ⭐⭐⭐⭐ |

> 🔑 **Two Sum** — THE most asked FAANG question. Unsorted → HashMap (O(N) time, O(N) space). Sorted → Two pointers (O(N) time, O(1) space).

### 1.12 Set Operations

**Pattern:** Use set intersection, difference, and grouping operations.

| File | Problem | Key Technique | FAANG |
|------|---------|---------------|-------|
| `IntersectionOfTwoArrays.kt` | Intersection of two arrays | HashSet intersection | ⭐⭐⭐ |
| `FindTheDifferenceBetweenTwoArray.kt` | Elements in A not B and vice versa | Set difference (A-B) ∪ (B-A) | ⭐⭐⭐ |
| `GroupAnagrams.kt` | Group anagrams together | Sorted string as HashMap key | ⭐⭐⭐⭐⭐ |

> 🔑 **Group Anagrams** — Top 10 most asked. Sort each string → use as HashMap key. All anagrams produce the same sorted string. Alternative: use character frequency count as key for O(N×K) instead of O(N×K log K).

### 1.13 Frequency Count

**Pattern:** Count occurrences using HashMap, then process by frequency.

| File | Problem | Key Technique | FAANG |
|------|---------|---------------|-------|
| `CountTheNumberOfPairs.kt` | Count valid pairs | HashMap frequency + combinatorics | ⭐⭐⭐ |
| `FindingFairPairs.kt` | Count fair pairs | Sort + two-pointer (hybrid) | ⭐⭐⭐ |
| `TopKFrequentElements.kt` | Top K frequent elements | Bucket sort by frequency | ⭐⭐⭐⭐⭐ |

> 🔑 **Top K Frequent Elements** — Top 15 most asked. Build frequency map, then bucket sort: bucket[i] = numbers with frequency i. Traverse from highest bucket. O(N) time!

### 1.14 Bit Manipulation

**Pattern:** Use XOR and bit counting to find unique elements in O(1) space.

| File | Problem | Key Technique | FAANG |
|------|---------|---------------|-------|
| `SingleNumber.kt` | Single Number (every other appears twice) | XOR: a ^ a = 0, a ^ 0 = a | ⭐⭐⭐⭐ |
| `SingleNumberII.kt` | Single Number II (every other appears 3×) | Bit counting: count % 3 at each position | ⭐⭐⭐⭐ |

> 🔑 **Single Number** — XOR all numbers. Pairs cancel out (a^a=0), single number remains.

> 🔑 **Single Number II** — Count bits at each of 32 positions. If count % 3 ≠ 0, that bit belongs to the answer. Or use two variables (ones, twos) to track bits seen 1× and 2×.

---

### 1.15 Two Pointer

**Core Idea:** Use two pointers to traverse data, typically from opposite ends or at different speeds. Reduces O(N²) to O(N) or O(N log N).

| File | Problem | Pattern |
|------|---------|---------|
| `TwoSumLevelTwoWithSortedArray.kt` | Two Sum (sorted) | Opposite ends |
| `ThreeSum.kt` | 3Sum | Sort + fix one + two-pointer |
| `FourSum.kt` | 4Sum | Sort + fix two + two-pointer |
| `ThreeSumCloset.kt` | 3Sum Closest | Same as 3Sum, track closest |
| `ContainerWithMostWater.kt` | Container With Most Water | Move shorter line inward |
| `TrappingRainWaterI.kt` | Trapping Rain Water | Two-pointer with maxLeft/maxRight |
| `ValidPalindrom.kt` | Valid Palindrome | Compare from both ends |
| `IsSubSequence.kt` | Is Subsequence | Two pointers on two strings |
| `MoveZerosToEnd.kt` | Move Zeroes | Slow/fast pointer |
| `SortColorsDutchNationalFlag.kt` | Sort Colors | Three-way partition (low/mid/high) |
| `MergeSortedArray.kt` | Merge Sorted Array | Fill from end to avoid overwrite |
| `RemoveElementInAnArray.kt` | Remove Element | Slow/fast pointer |
| `StringCompression.kt` | String Compression | Read/write pointer |
| `BoatsToSave.kt` | Boats to Save People | Sort + lightest+heaviest pairing |
| `BackspaceStringCompare.kt` | Backspace String Compare | Traverse from end |
| `LongestPalindromSubString.kt` | Longest Palindromic Substring | Expand around center |
| `LongestSubStringWithoutRepeatingCharacter.kt` | Longest Substring Without Repeating | Sliding window + set |
| `LongestRepeatingCharcterReplacement.kt` | Longest Repeating Char Replacement | Sliding window + max freq |
| `MinimumSizeSubArraySum.kt` | Minimum Size Subarray Sum | Variable sliding window |
| `SubArrayWithKDifferentInteger.kt` | Subarrays with K Different Integers | exactly(K) = atMost(K) - atMost(K-1) |
| `FindAllAnagramInString.kt` | Find All Anagrams in String | Sliding window + frequency map |
| `PermutationInString.kt` | Permutation in String | Sliding window + frequency match |
| `IntersectionTwoArrayII.kt` | Intersection of Two Arrays II | Sort + two-pointer |
| `MinimumPairRemovaltoSortArrayI.kt` | Min Pair Removal to Sort Array | Greedy + simulation |
| `MaxConsecutiveOnesIII.kt` | Max Consecutive Ones III | Sliding window: at most k zeros | ⭐⭐⭐⭐⭐ |
| `KSumPairs.kt` | K-Sum Pairs | Sort + two pointers, remove pairs | ⭐⭐⭐⭐ |
| `TwoSumLessThanK.kt` | Two Sum Less Than K | Sort + two pointers, track max < k | ⭐⭐⭐⭐ |
| `CountNumberOfNiceSubarrays.kt` | Count Nice Subarrays | exactly(K) = atMost(K) - atMost(K-1) | ⭐⭐⭐⭐⭐ |

> 🔑 **Max Consecutive Ones III** — Sliding window allowing at most k zeros. When zeros > k, shrink from left. The window size at any point = max consecutive 1s (with k flips). This pattern applies to ANY "at most K violations" problem.

> 🔑 **Count Nice Subarrays** — Same atMost pattern as SubArrayWithKDifferentInteger. `exactly(K) = atMost(K) - atMost(K-1)`. The atMost function counts all subarrays with ≤K condition using sliding window. Number of subarrays ending at right = `right - left + 1`.

---

### 1.16 Sliding Window

**Core Idea:** Maintain a "window" that slides across data. **Fixed-size** (window == K) or **Variable-size** (shrink while invalid).

| File | Problem | Window Type |
|------|---------|-------------|
| `MaximumAverageSubArray.kt` | Max average subarray of size K | Fixed window K |
| `FruitsIntoBaskets.kt` | Fruit Into Baskets (2 types max) | Variable: at most 2 distinct |
| `LongestSubArrayAfterDeletingOne.kt` | Longest subarray after deleting 1 | Variable: at most 1 zero |
| `GrumpyBookstoreOwner.kt` | Grumpy Bookstore Owner | Fixed window K | ⭐⭐⭐ |
| `MinimumWindowSubstring.kt` | Minimum Window Substring | Variable: shrink while valid | ⭐⭐⭐⭐⭐ |
| `LongestRepeatingCharacterReplacement.kt` | Longest Repeating Char Replacement | Variable: windowLen - maxFreq ≤ k | ⭐⭐⭐⭐⭐ |
| `LongestSubstringWithoutRepeatingCharacters.kt` | Longest Substring Without Repeating | Variable: shrink on duplicate | ⭐⭐⭐⭐⭐ |
| `FindAllAnagramsInString.kt` | Find All Anagrams in String | Fixed window: frequency match | ⭐⭐⭐⭐⭐ |
| `PermutationInString.kt` | Permutation in String | Fixed window: frequency match | ⭐⭐⭐⭐⭐ |

> 🔑 **Minimum Window Substring** — THE hardest sliding window. Build need map from t, expand right until formed == required, then shrink left to minimize. Template for ALL "minimum window containing X" problems.

> 🔑 **Longest Repeating Character Replacement** — Key formula: `windowLen - maxFreq <= k`. Don't need to update maxFreq when shrinking — smaller maxFreq only makes validity stricter.

> 🔑 **Longest Substring Without Repeating Characters** — #1 most asked sliding window. HashSet (shrink one-by-one) or HashMap (jump left to `lastSeen[char]+1`).

> 🔑 **Find All Anagrams / Permutation in String** — Same pattern! Fixed-size window = len(p). Frequency map + match counting. When all 26 counts match → found.

---

## 2. Stack

**Core Idea:** LIFO. Essential for **matching, nesting, nearest greater/smaller element** patterns.

| File | Problem | Description | Key Technique |
|------|---------|-------------|---------------|
| `ValidParentheses.kt` | Valid Parentheses | Check if brackets are properly matched and nested | Stack push/pop matching |
| `LongestValidParentheses.kt` | Longest Valid Parentheses | Find length of longest valid (well-formed) parentheses substring | Stack stores indices of unmatched |
| `MinimumParentheseToRemove.kt` | Minimum Remove to Make Valid | Find minimum removals to make parentheses string valid | Count unmatched open/close |
| `DailyTemperatures.kt` | Daily Temperatures | For each day, how many days until a warmer temperature? | Monotonic stack (decreasing) |
| `EvaluateReversePolishNotation.kt` | Evaluate RPN | Evaluate postfix expression using stack | Stack-based calculator |
| `LargestRectangleInHistogram.kt` | Largest Rectangle in Histogram | Find largest rectangular area in histogram | Monotonic stack (width × height) |
| `MinStack.kt` | Min Stack | Stack that supports O(1) getMin | Auxiliary stack for min |
| `NextGreaterElementI.kt` | Next Greater Element I | Find next greater element for elements of subset | Monotonic stack |
| `NextGreaterElementII.kt` | Next Greater Element II | Same as I but array is circular | Monotonic stack + double array |
| `OnlineStockSpan.kt` | Online Stock Span | Consecutive days with price ≤ today | Monotonic stack |
| `CarFleet.kt` | Car Fleet | Count car fleets arriving at destination | Sort by position + monotonic stack |

---

## 3. Linked List

**Core Idea:** Nodes connected via pointers. O(1) insert/delete at known position, O(N) access.

| File | Problem | Key Technique |
|------|---------|---------------|
| `ReverseLinkedList.kt` | Reverse Linked List | Iterative: prev, curr, next |
| `ReverseLinkedListII.kt` | Reverse Between Positions | Reverse sub-list with boundaries |
| `ReverseLinkedListKGroup.kt` | Reverse in K-Group | Reverse in groups of K |
| `RemoveNthNodeFromLast.kt` | Remove Nth From End | Fast pointer N ahead, then both move |
| `RemoveDuplicatesFromSortedListII.kt` | Remove Duplicates II | Dummy head + skip all duplicates |
| `ReorderList.kt` | Reorder List | Find mid + reverse 2nd half + merge |
| `SwapNodesInPairs.kt` | Swap Nodes in Pairs | Swap every two adjacent |
| `RotateLinkedList.kt` | Rotate List | Make circular, break at new head |
| `PatiotionList.kt` | Partition List | Two dummy heads (< x and ≥ x) |
| `LinkedListBinaryToInteger.kt` | Binary to Integer | result = result * 2 + node.val |
| `DesignHashSet.kt` | Design HashSet | Array of buckets (separate chaining) |
| `ConvertSortedListToBinarySearchTree.kt` | Sorted List to BST | Find mid → root, recurse |

---

## 4. Doubly Linked List

**Core Idea:** `prev` + `next` pointers enable O(1) insertion/deletion at both ends. Essential for **LRU/LFU** caches.

| File | Problem | Key Technique |
|------|---------|---------------|
| `FlatternDoubleLLMultiLevel.kt` | Flatten Multilevel DLL | Stack or recursion |
| `LeastRecentlyUsedLRU.kt` | LRU Cache | HashMap + Doubly LL (O(1) get/put) |
| `LeastFrequentlyUsedLFU.kt` | LFU Cache | HashMap + Freq map + Doubly LL |

---

## 5. Sorting

**Core Idea:** Sorting enables more efficient algorithms (e.g., two-pointer on sorted arrays).

| Algorithm | Best | Average | Worst | Space | Stable |
|-----------|------|---------|-------|-------|--------|
| Bubble Sort | O(N) | O(N²) | O(N²) | O(1) | ✅ |
| Selection Sort | O(N²) | O(N²) | O(N²) | O(1) | ❌ |
| Insertion Sort | O(N) | O(N²) | O(N²) | O(1) | ✅ |
| Merge Sort | O(N log N) | O(N log N) | O(N log N) | O(N) | ✅ |
| Quick Sort | O(N log N) | O(N log N) | O(N²) | O(log N) | ❌ |
| Heap Sort | O(N log N) | O(N log N) | O(N log N) | O(1) | ❌ |

---

## 6. Backtracking

**Core Idea:** Build candidates incrementally, abandon ("backtrack") when candidate can't lead to valid solution. The universal template:

```kotlin
fun backtrack(current: MutableList<T>, candidates: List<T>) {
    if (isComplete(current)) { result.add(current.toList()); return }
    for (candidate in candidates) {
        if (isValid(current, candidate)) {
            current.add(candidate)         // CHOOSE
            backtrack(current, candidates) // EXPLORE
            current.removeAt(current.lastIndex) // UNDO
        }
    }
}
```

| File | Problem | Time | Space | FAANG | Key Insight |
|------|---------|------|-------|-------|-------------|
| `Subsets.kt` | Subsets | O(N × 2^N) | O(N) | ⭐⭐⭐⭐⭐ | Include/exclude each element → 2^N subsets |
| `CombinationSum.kt` | Combination Sum | O(N^(T/M)) | O(T/M) | ⭐⭐⭐⭐⭐ | Subsets + sum constraint + reuse (stay at index i) |
| `Permutations.kt` | Permutations | O(N × N!) | O(N) | ⭐⭐⭐⭐⭐ | Order matters → try ALL positions, use `used[]` |
| `WordSearch.kt` | Word Search | O(N × 4^L) | O(L) | ⭐⭐⭐⭐⭐ | Grid DFS + mark visited with '#' |
| `LetterAndCombinationPhoneNumber.kt` | Letter Combinations of Phone Number | O(4^N) | O(N) | ⭐⭐⭐⭐ | Map digit to letters, combine |

> 🔑 **Subsets** — THE gateway backtracking problem. For each element: INCLUDE or EXCLUDE. This creates a decision tree with 2^N leaves. Every node in the tree is a valid subset. Also solvable iteratively: start with [[]], for each num, add num to all existing subsets.

> 🔑 **Combination Sum** — Subsets with TWO modifications: (1) CONSTRAINT: only add when sum == target, (2) REUSE: `backtrack(i, ...)` not `backtrack(i+1, ...)` — stay at same index to allow reuse. Sort candidates to enable pruning: if `candidates[i] > remaining`, break. This is the "unbounded knapsack" of backtracking.

> 🔑 **Permutations** — Unlike Subsets, ORDER matters. [1,2] ≠ [2,1]. Key difference: try ALL positions (not from `start`), use `used[]` array to skip already-picked elements. Two approaches: (1) Used array (easier), (2) Swap in-place (O(1) extra space). N! permutations for N elements.

> 🔑 **Word Search** — Grid backtracking. Mark visited by changing `board[r][c]` to '#' (avoids separate visited array). Try all 4 directions. Restore character after backtracking. This pattern applies to ALL grid DFS problems that need to track visited cells.

### Backtracking Pattern Recognition

```
"Find all combinations/subsets..."  → Subsets pattern (start index)
"Find combinations that sum to..."  → Combination Sum (start + constraint)
"Find all arrangements/orderings"   → Permutations (used array, try all)
"Find path in grid/matrix"          → Grid DFS (mark visited, 4 directions)
"Generate valid configurations"     → N-Queens / Sudoku (constraint checking)
```

---

## 7. Greedy Algorithm

**Core Idea:** Make locally optimal choice → globally optimal solution. Works when problem has **greedy-choice property**.

| File | Problem | Key Insight | FAANG |
|------|---------|-------------|-------|
| `AssignCookies.kt` | Assign Cookies | Smallest cookie ≥ child's greed | ⭐⭐⭐ |
| `NonOverlappingIntervals.kt` | Non-overlapping Intervals | Sort by END, pick earliest finish | ⭐⭐⭐⭐⭐ |
| `IntervalGroups.kt` | Interval Grouping | Sort by start, assign greedily | ⭐⭐⭐ |
| `TaskScheduler.kt` | Task Scheduler | Formula: max(total, (maxFreq-1)*(n+1) + countOfMaxFreq) | ⭐⭐⭐⭐⭐ |
| `PartitionLabels.kt` | Partition Labels | Last occurrence map + extend partition greedily | ⭐⭐⭐⭐⭐ |
| `LemonadeChange.kt` | Lemonade Change | Prefer $10+$5 over three $5s (conserve $5s) | ⭐⭐⭐⭐ |
| `MinimumArrowsToBurstBalloons.kt` | Min Arrows to Burst Balloons | Sort by END, shoot at overlap boundaries | ⭐⭐⭐⭐ |
| `QueueReconstructionByHeight.kt` | Queue Reconstruction by Height | Sort tallest first, insert at index k | ⭐⭐⭐⭐⭐ |
| `WiggleSubsequence.kt` | Wiggle Subsequence | Count peaks and valleys (direction changes) | ⭐⭐⭐⭐ |
| `MeetingRooms.kt` | Meeting Rooms I & II | Sweep line: (time, ±1) events; max concurrent = min rooms | ⭐⭐⭐⭐⭐ |

> 🔑 **Task Scheduler** — The most frequent task creates the framework. If 'A' appears 3 times with n=2, you need A _ _ | A _ _ | A. Fill gaps with other tasks. Formula: `max(total, (maxFreq-1)*(n+1) + countOfMaxFreq)`.

> 🔑 **Partition Labels** — Record last occurrence of each character. Extend partition end to `lastIndex[char]`. When `i == end`, close the partition. O(N) time.

> 🔑 **Queue Reconstruction by Height** — Sort tallest first (height DESC, k ASC), then insert each person at index k. Since all taller people are already placed, inserting at k guarantees exactly k taller people are in front.

> 🔑 **Min Arrows to Burst Balloons** — Same pattern as Non-overlapping Intervals. Sort by END, shoot arrow at first balloon's end. Any balloon starting before that is also burst. When a balloon starts after → new arrow needed.

---

## 8. Simulation

**Core Idea:** Directly simulate the described process step by step.

| File | Problem | Key Technique |
|------|---------|---------------|
| `AddBinary.kt` | Add Binary | Simulate binary addition with carry |
| `AddString.kt` | Add Strings | Simulate decimal addition with carry |
| `AddDigits.kt` | Add Digits | Digital root: 1 + (n-1) % 9 |
| `FizzBuzz.kt` | Fizz Buzz | Check divisibility by 3 and 5 |
| `SpiralMatrix.kt` | Spiral Matrix | Four boundaries: top/bottom/left/right |
| `SpiralMatrixII.kt` | Spiral Matrix II | Generate spiral matrix |
| `TextJustification.kt` | Text Justification | Greedy line packing |

---

## 9. Dynamic Programming

> **Why DP?** Dynamic Programming is the **most tested** topic in FAANG interviews. The key insight: **every DP problem is just recursion + memoization**. If you can write a brute force recursion, you can optimize it to DP.

> **Every problem includes Brute Force → Better (Memoization) → Optimal (Tabulation) progression** with detailed step-by-step traces.

```
📁 dp/
├── 📂 one_d/        → Single state variable (Fibonacci, knapsack variants)
├── 📂 two_d/        → Two state variables (grid, string comparison)
└── 📂 subsequence/  → Subsequence/selection problems (LIS, subset sum)
```

### DP Pattern Recognition

```
"Count ways to reach..."        → 1D DP (like Climbing Stairs)
"Max/min of something"          → 1D DP with choice (like House Robber)
"Min coins/steps to reach..."   → 1D DP minimization (like Coin Change)
"Grid paths, unique ways"      → 2D Grid DP (like Unique Paths)
"Compare two strings"           → 2D String DP (like LCS, Edit Distance)
"Longest increasing..."         → Subsequence DP (like LIS)
"Can we partition/select..."    → Subset Sum DP (like Partition Equal Subset)
```

### DP Optimization Ladder

```
1. BRUTE FORCE (Recursion)     → O(2^N) or worse — exponential
2. BETTER (Memoization)         → O(N) or O(N²) — top-down with cache
3. OPTIMAL-1 (Tabulation)       → O(N) or O(N²) — bottom-up, no recursion
4. OPTIMAL-2 (Space-optimized)  → O(1) or O(N) space — use variables, not arrays
```

---

### 9.1 1D DP

**Pattern:** Single state variable. Each subproblem depends on a few previous results. Like Fibonacci but with different operations (sum, max, min).

| File | Problem | Brute Force | Optimal | Space | FAANG |
|------|---------|-------------|---------|-------|-------|
| `ClimbingStairs.kt` | Climbing Stairs | O(2^N) | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `MinCostClimbingStairs.kt` | Min Cost Climbing Stairs | O(2^N) | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `HouseRobber.kt` | House Robber | O(2^N) | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `HouseRobberII.kt` | House Robber II (Circular) | O(2^N) | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `CoinChange.kt` | Coin Change (Min Coins) | O(S^N) | O(S×N) | O(S) | ⭐⭐⭐⭐⭐ |
| `CoinChangeII.kt` | Coin Change II (Combinations) | O(2^N) | O(N×amount) | O(amount) | ⭐⭐⭐⭐⭐ |
| `DecodeWays.kt` | Decode Ways | O(2^N) | O(N) | O(1) | ⭐⭐⭐⭐⭐ |
| `WordBreak.kt` | Word Break | O(2^N) | O(N²×W) | O(N) | ⭐⭐⭐⭐⭐ |

> 🔑 **Climbing Stairs** — THE gateway DP problem. It's literally Fibonacci! `ways(n) = ways(n-1) + ways(n-2)`. Key insight: "To reach step n, where could I have come from?" This question is the foundation of ALL DP — identify what previous states lead to the current state.

> 🔑 **Min Cost Climbing Stairs** — Climbing Stairs' minimization cousin. `dp[i] = min(dp[i-1] + cost[i-1], dp[i-2] + cost[i-2])`. Same structure but MIN instead of SUM. Base cases: dp[0]=0, dp[1]=0 (start for free). The "top" is index n.

> 🔑 **House Robber** — Decision DP: at each house, ROB or SKIP? `dp[i] = max(dp[i-1], dp[i-2] + nums[i])`. Similar to Climbing Stairs but with MAX instead of SUM (optimizing, not counting). Space optimization: only need 2 variables since dp[i] depends on dp[i-1] and dp[i-2].

> 🔑 **House Robber II** — Circular variant! House[0] and house[n-1] are adjacent. KEY INSIGHT: break circle into two linear sub-problems. `max(robLinear[0..n-2], robLinear[1..n-1])`. Since you can't rob both first and last, the optimal solution MUST exclude at least one. This "break circular into two linear" pattern appears in MANY circular problems!

> 🔑 **Coin Change** — Unbounded knapsack (use each coin unlimited times). Minimization DP: `dp[a] = min(dp[a-coin] + 1)` for each coin. Key difference from House Robber: MIN instead of MAX, and we try ALL coins at each step. Initialize dp with "infinity" (amount+1) to represent impossible states.

> 🔑 **Coin Change II** — Same unbounded knapsack but COUNTING combinations instead of minimizing. `dp[a] += dp[a - coin]`. CRITICAL: coins in OUTER loop, amounts in INNER loop → counts COMBINATIONS. Swapped loops → counts PERMUTATIONS. Forward inner iteration = unbounded (each coin unlimited times). This is the complement to Partition Equal Subset Sum's reverse iteration (0/1 knapsack).

> 🔑 **Decode Ways** — Climbing Stairs with CONSTRAINTS! Take 1 digit (must be '1'-'9') or 2 digits (must be 10-26). `dp[i] = (valid1 ? dp[i-1] : 0) + (valid2 ? dp[i-2] : 0)`. Key edge case: '0' alone is invalid, '06' is invalid (leading zero).

> 🔑 **Word Break** — Climbing Stairs GENERALIZED! Instead of fixed step sizes (1,2), step sizes are word lengths that match the string. `dp[i] = true if dp[i-word.length] AND s[i-word.len..i] == word`. This is like BFS on positions — each valid word is an "edge" from position j to j + word.length.

---

### 9.2 2D DP

**Pattern:** Two state variables (typically two strings or grid coordinates). The DP table is a matrix where each cell depends on neighbors.

| File | Problem | Brute Force | Optimal | Space | FAANG |
|------|---------|-------------|---------|-------|-------|
| `UniquePaths.kt` | Unique Paths | O(2^(m+n)) | O(m×n) | O(n) | ⭐⭐⭐⭐⭐ |
| `MinimumPathSum.kt` | Minimum Path Sum | O(2^(m+n)) | O(m×n) | O(n) | ⭐⭐⭐⭐⭐ |
| `LongestCommonSubsequence.kt` | LCS | O(2^(m+n)) | O(m×n) | O(m×n) | ⭐⭐⭐⭐⭐ |
| `EditDistance.kt` | Edit Distance | O(3^(m+n)) | O(m×n) | O(m×n) | ⭐⭐⭐⭐⭐ |
| `LongestPalindromicSubsequence.kt` | Longest Palindromic Subsequence | O(2^N) | O(N²) | O(N²) | ⭐⭐⭐⭐⭐ |

> 🔑 **Unique Paths** — 2D version of Climbing Stairs! `dp[r][c] = dp[r-1][c] + dp[r][c-1]`. Space optimization: only need 1 row because each cell depends on the cell above (previous row) and cell to the left (current row). Can also be solved with combinatorics: C(m+n-2, m-1).

> 🔑 **Minimum Path Sum** — Unique Paths' minimization cousin! `dp[r][c] = min(dp[r-1][c], dp[r][c-1]) + grid[r][c]`. Same movement (right/down) but MINIMIZE cost instead of COUNT paths. This is the 2D version of Min Cost Climbing Stairs. Space optimization: same as Unique Paths — only 1 row needed.

> 🔑 **LCS** — THE most important 2D DP. When chars match: `dp[i][j] = 1 + dp[i-1][j-1]` (diagonal). When they don't: `dp[i][j] = max(dp[i-1][j], dp[i][j-1])` (max of up/left). This pattern appears in MANY string DP problems. Read the DP table to reconstruct the actual subsequence.

> 🔑 **Edit Distance** — LCS's more powerful cousin. 3 operations (insert, delete, replace) when chars don't match: `dp[i][j] = 1 + min(dp[i][j-1], dp[i-1][j], dp[i-1][j-1])`. When they match: `dp[i][j] = dp[i-1][j-1]` (free!). Base cases: dp[0][j] = j (j insertions), dp[i][0] = i (i deletions).

> 🔑 **Longest Palindromic Subsequence** — LCS with a twist! LPS(s) = LCS(s, s.reversed()). Direct DP: `dp[i][j]` = LPS of s[i..j]. If s[i]==s[j]: `dp[i][j] = 2 + dp[i+1][j-1]`. If not: `dp[i][j] = max(dp[i+1][j], dp[i][j-1])`. Fill diagonally (shorter substrings first). Base case: dp[i][i] = 1.

---

### 9.3 Subsequence DP

**Pattern:** Find optimal subsequences or check if a subset exists. Combines 1D and 2D thinking. Key variants: LIS (patience sorting) and Subset Sum (0/1 knapsack).

| File | Problem | Brute Force | Optimal | Space | FAANG |
|------|---------|-------------|---------|-------|-------|
| `LongestIncreasingSubsequence.kt` | LIS | O(2^N) | O(N log N) | O(N) | ⭐⭐⭐⭐⭐ |
| `PartitionEqualSubsetSum.kt` | Partition Equal Subset Sum | O(2^N) | O(N×target) | O(target) | ⭐⭐⭐⭐⭐ |
| `TargetSum.kt` | Target Sum | O(2^N) | O(N×target) | O(target) | ⭐⭐⭐⭐⭐ |

> 🔑 **LIS** — Two approaches: (1) O(N²) DP: `dp[i] = 1 + max(dp[j])` for all j < i where nums[j] < nums[i]. (2) O(N log N) Patience Sorting: maintain `tails[]` where tails[i] = smallest tail of any IS of length i+1. For each num, binary search for first tails[i] ≥ num and replace. If num > all tails, extend. The length of tails = LIS length. **NOTE: tails is NOT the actual LIS**, only its length is correct.

> 🔑 **Partition Equal Subset Sum** — 0/1 Knapsack variant (each number used AT MOST once, unlike Coin Change). Reduce to subset sum: can we find a subset summing to total/2? `dp[t] = dp[t] || dp[t - num]`. **CRITICAL**: iterate target in REVERSE to avoid using the same element twice (forward iteration = unbounded knapsack). Space optimization from O(N×target) to O(target) using 1D array with reverse iteration.

> 🔑 **Target Sum** — Partition Equal Subset Sum in disguise! Assign '+' or '-' to each number. Let P = positive subset, N = negative subset. Then sum(P) - sum(N) = target and sum(P) + sum(N) = totalSum. Adding: sum(P) = (target + totalSum) / 2. So the problem reduces to: "How many subsets sum to (target + totalSum) / 2?" — same 0/1 knapsack but COUNTING instead of boolean check. `dp[t] += dp[t - num]` (reverse iteration).

---

## 10. Heap / Priority Queue

> **Why Heap?** Heaps are the go-to data structure for "find top/bottom K" and "merge K sorted sources" problems. They give O(log K) insert/extract-min/max, making them ideal when you only need the extreme elements, not a fully sorted collection.

```
📁 heap/
├── TopKFrequentElements.kt   → Min-heap of size K + Bucket sort
├── KthLargestElement.kt      → Min-heap + QuickSelect
└── MergeKSortedLists.kt      → Min-heap K-way merge
```

| File | Problem | Time | Space | FAANG | Key Technique |
|------|---------|------|-------|-------|---------------|
| `TopKFrequentElements.kt` | Top K Frequent Elements | O(N) bucket / O(N log K) heap | O(N) | ⭐⭐⭐⭐⭐ | Min-heap of size K or Bucket sort |
| `KthLargestElement.kt` | Kth Largest Element | O(N) avg / O(N log K) heap | O(K) | ⭐⭐⭐⭐⭐ | Min-heap of size K or QuickSelect |
| `MergeKSortedLists.kt` | Merge K Sorted Lists | O(N log K) | O(K) | ⭐⭐⭐⭐⭐ | Min-heap K-way merge |
| `FindMedianFromDataStream.kt` | Find Median from Data Stream | O(log N) add / O(1) find | O(N) | ⭐⭐⭐⭐⭐ | Two heaps (max-heap + min-heap) |

> 🔑 **Top K Frequent Elements** — Two approaches: (1) Min-heap of size K: push each (element, freq), pop when size > K. Root = Kth most frequent. (2) Bucket sort (OPTIMAL O(N)): bucket[i] = elements with frequency i. Traverse from highest bucket. Bucket sort is O(N) because frequency ≤ N.

> 🔑 **Kth Largest Element** — Three approaches: (1) Sort: O(N log N) — simple but not optimal. (2) Min-heap of size K: O(N log K) — root = Kth largest. (3) QuickSelect: O(N) average — like QuickSort but only recurse into ONE partition. Randomized pivot makes worst case extremely unlikely. MUST KNOW for FAANG!

> 🔑 **Merge K Sorted Lists** — THE heap problem. Push all list heads into min-heap. Pop minimum, add to result, push its next node. O(N log K) where N = total nodes, K = number of lists. Alternative: Divide & Conquer (pair up lists, merge each pair, repeat) — same O(N log K) but no heap overhead.

> 🔑 **Find Median from Data Stream** — THE two-heap problem! Max-heap stores the SMALLER half, min-heap stores the LARGER half. Invariant: max-heap.size = min-heap.size OR max-heap.size = min-heap.size + 1. Add: push to max-heap → balance to min-heap → if min-heap larger, move back. Median: if equal sizes → average of both roots; if max-heap larger → max-heap root. This two-heap pattern applies to ALL streaming median problems.

### Heap Pattern Recognition

```
"Find top/bottom K..."        → Min/Max-heap of size K
"Kth largest/smallest..."     → Min-heap of size K or QuickSelect
"Merge K sorted sources..."   → Min-heap K-way merge
"Running median..."           → Two heaps (max-heap + min-heap)
"Schedule with priority..."   → Max-heap (priority queue)
```

---

## 11. Trie (Prefix Tree)

> **Why Trie?** Tries provide O(M) search and prefix operations (M = word length), independent of how many words are stored. This is IMPOSSIBLE with HashMap — you can't check "does any word start with prefix X?" without a trie.

```
📁 trie/
├── ImplementTrie.kt    → Insert, Search, StartsWith
└── WordSearchII.kt    → Trie + Backtracking (Hard)
```

| File | Problem | Time | Space | FAANG | Key Technique |
|------|---------|------|-------|-------|---------------|
| `ImplementTrie.kt` | Implement Trie (Prefix Tree) | O(M) per op | O(N×M) | ⭐⭐⭐⭐⭐ | 26-child nodes + isEnd flag |
| `WordSearchII.kt` | Word Search II | O(M×N×4^L) with pruning | O(total chars) | ⭐⭐⭐⭐⭐ | Build trie from words + DFS board |

> 🔑 **Implement Trie** — Each node has 26 children (one per letter) and an isEnd flag. insert: traverse/create nodes, mark last as isEnd. search: traverse nodes, return true only if isEnd. startsWith: traverse nodes, return true if path exists (don't need isEnd). THE foundation for all trie problems.

> 🔑 **Word Search II** — Hard problem combining Trie + Backtracking. Naive: for each word, DFS the board → O(W × M × N × 4^L). Optimal: build trie from ALL words, DFS the board ONCE. The trie acts as a "guide" — we only explore board paths that could lead to a word. When we reach isEnd, we found a word! Set isEnd=false after finding to avoid duplicates. Prune empty trie nodes to reduce search space.

### Trie vs HashMap

```
                    Trie           HashMap
Search word:        O(M)           O(M) avg
Prefix search:      O(M)           ❌ Impossible
Space (shared):     Yes (prefixes)  No (each word separate)
Autocomplete:       Natural fit     Not possible
```

---

## 12. Quick Reference — Complexity Cheatsheet

```
O(1)       → Hash map lookup, array access by index
O(log N)   → Binary search, balanced BST operations
O(√N)      → Primality test
O(N)       → Linear scan, hash map iteration
O(N log N) → Sorting, divide & conquer
O(N²)      → Nested loops, brute force pairs
O(2^N)     → All subsets, backtracking
O(N!)      → All permutations
```

| Pattern | Time | Space |
|---------|------|-------|
| Two Sum (unsorted) | O(N) | O(N) |
| Two Sum (sorted) | O(N) | O(1) |
| Three Sum | O(N²) | O(1)* |
| Sliding Window | O(N) | O(K) or O(N) |
| Stack matching | O(N) | O(N) |
| LRU Cache | O(1) per op | O(capacity) |
| Binary Search | O(log N) | O(1) |
| Merge Sort | O(N log N) | O(N) |
| 1D DP (Fibonacci-like) | O(N) | O(1)–O(N) |
| 2D DP (Grid/String) | O(m×n) | O(m×n) |
| LIS (Patience Sort) | O(N log N) | O(N) |
| Subset Sum (0/1 Knapsack) | O(N×target) | O(target) |

---

## 13. Problem-Solving Strategy

```
1. UNDERSTAND → Read carefully, work through examples
2. IDENTIFY PATTERN:
   "Find a pair..."          → Hashing or Two Pointer
   "Longest subarray..."     → Sliding Window
   "Valid parentheses..."    → Stack
   "Next greater element"    → Monotonic Stack
   "Detect cycle"            → Fast/Slow Pointer
   "All combinations"        → Backtracking
   "Optimal scheduling"      → Greedy
   "Range queries"           → Prefix Sum
   "Count ways / max / min"  → Dynamic Programming
   "Compare two strings"     → 2D DP (LCS, Edit Distance)
   "Can we select subset?"   → Subset Sum / 0-1 Knapsack
3. BRUTE FORCE first → understand the problem
4. OPTIMIZE → hash map? sort + two-pointer? sliding window? DP? math trick?
5. VERIFY → edge cases, complexity
```

---

## 14. What's Next?

| Topic | Key Problems | Difficulty |
|-------|-------------|-----------|
| **Graphs (Advanced)** | Bellman-Ford, Prim's, Kruskal's | ⭐⭐⭐ |
| **Intervals** | Meeting Rooms, Employee Free Time | ⭐⭐⭐ |
| **Monotonic Stack** | Next Greater Element, Largest Rectangle in Histogram | ⭐⭐⭐⭐ |
| **More DP** | Matrix Chain Multiplication, Burst Balloons, Regex Matching | ⭐⭐⭐⭐⭐ |
| **Bit Manipulation (Advanced)** | Counting Bits, Reverse Bits, Sum of Two Integers | ⭐⭐⭐ |

---

> 💡 **Pro Tip:** Always solve brute force first, then optimize. Understanding *why* brute force is slow reveals the key insight for the optimal solution.

> 🎯 **FAANG Strategy:** Easy → build pattern recognition → Medium → learn optimization → Hard → combine patterns

---

## 15. 🗺️ DSA Mastery Roadmap — Where to Start & How to Study

> This roadmap is designed to take you from **zero to FAANG-ready** using this repository. Follow the phases in order — each phase builds on the previous one.

### Phase 1: Foundation (Weeks 1-2) — Build Your Toolkit

**Goal:** Master the basic patterns that appear in EVERY interview.

| Order | Topic | Problems to Start With | Why First? |
|-------|-------|----------------------|------------|
| 1 | **Linear Scan** | `FindSmallestOrLargest`, `FindMaximumConsecutiveOnes`, `LeadersInArray` | Simplest pattern — builds confidence |
| 2 | **Two Pointer** | `TwoSumLevelTwoWithSortedArray`, `ValidPalindrom`, `MoveZerosToEnd` | Most common after linear scan |
| 3 | **HashSet Lookup** | `ContainsDuplicates`, `TwoSumLevelOneWithoutSortedArray`, `LongestConsecutiveSequence` | O(1) lookup is a superpower |
| 4 | **Prefix Sum** | `RunningSum1DArray`, `ProductOfArrayExceptSelf`, `SubarraySumEqualsK` | Essential for range queries |
| 5 | **Sliding Window** | `MaximumAverageSubArray`, `LongestSubstringWithoutRepeatingCharacters`, `MinimumWindowSubstring` | Top 5 most tested pattern |

**Study Method:**
1. Read the problem statement
2. Try to solve it yourself for 15-20 minutes
3. Read the Brute Force solution in the file — understand WHY it's slow
4. Read the Optimal solution — understand the KEY INSIGHT that makes it fast
5. Close the file and re-implement from memory
6. Move to the next problem

---

### Phase 2: Core Patterns (Weeks 3-4) — The FAANG Bread & Butter

**Goal:** Master the patterns that solve 70% of FAANG interview questions.

| Order | Topic | Must-Do Problems | Key Insight |
|-------|-------|-----------------|-------------|
| 1 | **Kadane's Algorithm** | `MaximumSubArray`, `MaximumProductSubarray` | Local vs Global max/min |
| 2 | **Binary Search** | `SearchInRotatedSortedArray`, `FindMinimumInRotatedSortedArray` | Find the sorted half |
| 3 | **Stack** | `ValidParentheses`, `DailyTemperatures`, `LargestRectangleInHistogram` | LIFO for matching & nearest greater |
| 4 | **Greedy** | `JumpGame`, `MergeIntervals`, `GasStation`, `MeetingRooms` | Locally optimal → globally optimal |
| 5 | **Linked List** | `ReverseLinkedList`, `RemoveNthNodeFromLast`, `ReorderList` | Pointer manipulation |
| 6 | **Two Pointer Advanced** | `ThreeSum`, `TrappingRainWaterI`, `ContainerWithMostWater` | Sort + converge from ends |

**🎯 FAANG Sweet Spot:** If you only have limited time, master these 6 topics. They cover ~70% of interview questions.

---

### Phase 3: Backtracking & Trees (Weeks 5-6) — Learn to Enumerate

**Goal:** Master recursive thinking — the foundation for DP and tree problems.

| Order | Topic | Must-Do Problems | Progression |
|-------|-------|-----------------|-------------|
| 1 | **Backtracking Basics** | `LetterAndCombinationPhoneNumber` → `Subsets` → `CombinationSum` | Simplest → include/exclude → with constraint |
| 2 | **Backtracking Advanced** | `Permutations` → `WordSearch` | Order matters → grid DFS |
| 3 | **Tree Traversal** | In-order, Pre-order, Post-order | Foundation for all tree problems |
| 4 | **BST** | Search, Insert, Validate BST | Ordered tree properties |
| 5 | **Binary Tree** | Max Depth, Invert, LCA | Recursive tree thinking |

**Study Method for Backtracking:**
1. ALWAYS draw the recursion tree on paper first
2. Identify: what are you CHOOSING? What's the CONSTRAINT? When do you STOP?
3. Write the template: CHOOSE → EXPLORE → UNDO
4. Trace through with a small example (n ≤ 3)

---

### Phase 4: Dynamic Programming (Weeks 7-9) — The King of FAANG

**Goal:** Master DP — the most tested and highest-leverage topic.

| Order | Sub-Topic | Must-Do Problems | What You Learn |
|-------|-----------|-----------------|----------------|
| 1 | **1D DP — Counting** | `ClimbingStairs` → `DecodeWays` | "How many ways?" = SUM of previous states |
| 2 | **1D DP — Optimization** | `HouseRobber` → `HouseRobberII` → `MinCostClimbingStairs` | "Max/Min?" = MAX/MIN of choices |
| 3 | **1D DP — Minimization** | `CoinChange` | "Min steps?" = MIN across all choices |
| 4 | **1D DP — Counting Combos** | `CoinChangeII` → `WordBreak` | "How many combinations?" = SUM with constraints |
| 5 | **2D DP — Grid** | `UniquePaths` → `MinimumPathSum` | Grid movement = 2D version of 1D |
| 6 | **2D DP — Strings** | `LCS` → `EditDistance` → `LongestPalindromicSubsequence` | Compare two strings = 2D table |
| 7 | **Subsequence DP** | `LIS` → `PartitionEqualSubsetSum` → `TargetSum` | Selection + optimization |

**🎯 DP Mastery Method (THE MOST IMPORTANT):**
1. **ALWAYS start with Brute Force recursion** — don't skip to DP!
2. Write the recurrence: "To solve f(n), what smaller problems do I need?"
3. Add memoization (top-down) — just cache the recursion
4. Convert to tabulation (bottom-up) — fill table from base cases
5. Space optimize — which previous states do you ACTUALLY need?
6. **TRACE through the DP table by hand** — this is how you build intuition

**DP Problem-Solving Checklist:**
```
□ What is the state? (What does dp[i] or dp[i][j] represent?)
□ What is the recurrence? (How does dp[i] depend on previous states?)
□ What are the base cases? (dp[0] = ?)
□ What is the answer? (dp[n]? max of all dp[i]?)
□ Can I space-optimize? (Do I need the full array or just last 2 values?)
```

---

### Phase 5: Advanced Data Structures (Weeks 10-11) — Level Up

**Goal:** Master the data structures that solve the hardest problems.

| Order | Topic | Must-Do Problems | Key Pattern |
|-------|-------|-----------------|-------------|
| 1 | **Heap / Priority Queue** | `TopKFrequentElements` → `KthLargestElement` → `MergeKSortedLists` → `FindMedianFromDataStream` | "Top K" → heap of size K |
| 2 | **Trie** | `ImplementTrie` → `WordSearchII` | Prefix operations in O(M) |
| 3 | **Graph BFS/DFS** | `NumberOfIslands` → `CloneGraph` → `PacificAtlanticWaterFlow` | Grid = implicit graph |
| 4 | **Graph Shortest Path** | `Dijkstra` → `WordLadder` | Weighted vs unweighted |
| 5 | **Topological Sort** | `CourseSchedule` → `CourseScheduleII` | DAG + ordering |
| 6 | **Union Find** | `NumberOfProvinces` → `GraphValidTree` | Connected components |

---

### Phase 6: FAANG Mock Interviews (Week 12+) — Put It All Together

**Goal:** Combine patterns to solve unseen problems under time pressure.

**Daily Practice Routine:**
```
1. Solve 2-3 problems per day (1 Easy warmup + 1-2 Medium)
2. Time yourself: 20 min for Easy, 35 min for Medium
3. If stuck for 15+ minutes, read the HINT (not the solution)
4. After solving, read the OPTIMAL solution — could you have done better?
5. Re-solve the same problem 3 days later (spaced repetition)
```

**FAANG Question Frequency (Solve These FIRST):**

| Rank | Problem | Topic | Appears In |
|------|---------|-------|-----------|
| 1 | Two Sum | HashSet | Google, Meta, Amazon |
| 2 | Longest Substring Without Repeating | Sliding Window | Meta, Amazon |
| 3 | Maximum Subarray | Kadane's | Amazon, Microsoft |
| 4 | Merge Intervals | Greedy | Meta, Google |
| 5 | Valid Parentheses | Stack | Amazon, Google |
| 6 | Climbing Stairs | 1D DP | Amazon, Meta |
| 7 | Coin Change | 1D DP | Amazon, Google |
| 8 | Number of Islands | Graph BFS/DFS | Amazon, Meta, Google |
| 9 | LCS / Edit Distance | 2D DP | Google, Microsoft |
| 10 | Top K Frequent Elements | Heap | Meta, Amazon |
| 11 | Subsets / Permutations | Backtracking | Meta, Amazon |
| 12 | Word Break | 1D DP | Amazon, Meta |
| 13 | Trapping Rain Water | Two Pointer | Amazon, Google |
| 14 | 3Sum | Two Pointer | Meta, Amazon |
| 15 | LRU Cache | Doubly Linked List | Amazon, Meta, Google |

---

### 📊 Progress Tracker

Print this out and check off as you master each topic:

```
Phase 1: Foundation
  □ Linear Scan          (6+ problems)
  □ Two Pointer          (8+ problems)
  □ HashSet Lookup       (5 problems)
  □ Prefix Sum           (5 problems)
  □ Sliding Window       (9 problems)

Phase 2: Core Patterns
  □ Kadane's Algorithm    (3 problems)
  □ Binary Search        (3 problems)
  □ Stack                (3+ problems)
  □ Greedy               (8+ problems)
  □ Linked List           (8+ problems)

Phase 3: Backtracking & Trees
  □ Backtracking          (5 problems)
  □ Tree Traversal        (3+ problems)
  □ BST                   (3+ problems)

Phase 4: Dynamic Programming
  □ 1D DP — Counting     (3 problems)
  □ 1D DP — Optimization (3 problems)
  □ 1D DP — Minimization (2 problems)
  □ 2D DP — Grid         (2 problems)
  □ 2D DP — Strings      (3 problems)
  □ Subsequence DP        (3 problems)

Phase 5: Advanced DS
  □ Heap / Priority Queue (4 problems)
  □ Trie                 (2 problems)
  □ Graph BFS/DFS        (3 problems)
  □ Topological Sort      (2 problems)
  □ Union Find            (2 problems)

Phase 6: Mock Interviews
  □ Solve 50+ Medium problems timed
  □ Top 15 FAANG problems (see table above)
  □ 5+ full mock interviews
```

**🎯 The 80/20 Rule:** If you master Sliding Window + Two Pointer + DP + BFS/DFS + Heap, you can solve ~80% of FAANG interview questions. These 5 topics are your highest-leverage investment.

---

## 16. 🧠 Pattern Recognition Master Guide

> **The #1 skill for FAANG interviews is PATTERN RECOGNITION.** When you see a problem, you should instantly know which technique to apply. This guide maps keywords → patterns → problems.

### 🔍 The Ultimate Keyword → Pattern Mapper

Read the problem. Spot the keywords. Apply the pattern.

| 🔑 Keywords in Problem | 🎯 Pattern to Use | 📁 Start Here |
|------------------------|-------------------|---------------|
| "Find a pair that sums to..." | Complement Search / Two Pointer | `TwoSumII`, `TwoSumLevelOne` |
| "Two sorted arrays..." | Two Pointer (merge-like) | `MergeSortedArray`, `MedianOfTwoSortedSubArray` |
| "Container with most water" | Two Pointer (move shorter) | `ContainerWithMostWater` |
| "Trapping rain water" | Two Pointer (maxLeft/maxRight) | `TrappingRainWaterI` |
| "3Sum / 4Sum" | Sort + Fix + Two Pointer | `ThreeSum`, `FourSum` |
| "Longest substring without..." | Sliding Window + HashSet | `LongestSubstringWithoutRepeatingCharacters` |
| "Minimum window containing..." | Sliding Window (shrink while valid) | `MinimumWindowSubstring` |
| "At most K distinct / violations" | Sliding Window (expand/shrink) | `FruitsIntoBaskets`, `MaxConsecutiveOnesIII` |
| "Exactly K subarrays" | atMost(K) - atMost(K-1) | `SubArrayWithKDifferentInteger`, `CountNumberOfNiceSubarrays` |
| "Anagram / permutation of string" | Fixed-size Sliding Window + Freq Map | `FindAllAnagramsInString`, `PermutationInString` |
| "Maximum subarray sum" | Kadane's Algorithm | `MaximumSubArray` |
| "Maximum subarray product" | Kadane's (track min AND max) | `MaximumProductSubarray` |
| "Circular subarray" | Kadane's + minSubarray trick | `MaximumSumCircularSubarray` |
| "Valid parentheses" | Stack | `ValidParentheses` |
| "Next greater element" | Monotonic Stack (decreasing) | `NextGreaterElementI` |
| "Daily temperatures" | Monotonic Stack | `DailyTemperatures` |
| "Largest rectangle in histogram" | Monotonic Stack (width × height) | `LargestRectangleInHistogram` |
| "Reverse linked list" | Iterative: prev/curr/next | `ReverseLinkedList` |
| "Find cycle in linked list" | Fast/Slow Pointer | Floyd's Cycle Detection |
| "Remove Nth from end" | Fast pointer N ahead | `RemoveNthNodeFromLast` |
| "LRU Cache" | HashMap + Doubly Linked List | `LeastRecentlyUsedLRU` |
| "All possible subsets/combinations" | Backtracking (start index) | `Subsets` |
| "Combinations that sum to target" | Backtracking + constraint + reuse | `CombinationSum` |
| "All permutations/arrangements" | Backtracking (used array) | `Permutations` |
| "Find word in grid" | Grid DFS + mark visited | `WordSearch` |
| "Merge overlapping intervals" | Sort by start + greedy merge | `MergeIntervals` |
| "Can attend all meetings?" | Sort + check overlaps | `MeetingRooms` |
| "Min meeting rooms" | Sweep line or Min-Heap | `MeetingRooms` |
| "Jump game / gas station" | Greedy (track farthest/tank) | `JumpGame`, `GasStation` |
| "Count ways to reach step N" | 1D DP — Fibonacci-like | `ClimbingStairs` |
| "Max/min of choices" | 1D DP — Decision | `HouseRobber`, `MinCostClimbingStairs` |
| "Min coins to make amount" | 1D DP — Unbounded Knapsack | `CoinChange` |
| "How many combinations sum to..." | 1D DP — Unbounded Counting | `CoinChangeII` |
| "Decode / encode string" | 1D DP — Constrained Fibonacci | `DecodeWays` |
| "Can word be segmented?" | 1D DP — Generalized Fibonacci | `WordBreak` |
| "Unique paths in grid" | 2D DP — Grid | `UniquePaths` |
| "Min path sum in grid" | 2D DP — Grid Minimization | `MinimumPathSum` |
| "Compare two strings (LCS)" | 2D DP — String | `LongestCommonSubsequence` |
| "Convert string A to B" | 2D DP — String Operations | `EditDistance` |
| "Longest palindromic subsequence" | 2D DP — LCS variant | `LongestPalindromicSubsequence` |
| "Longest increasing subsequence" | Subsequence DP — Patience Sort | `LongestIncreasingSubsequence` |
| "Can we partition equally?" | Subsequence DP — 0/1 Knapsack | `PartitionEqualSubsetSum` |
| "Assign +/- to reach target" | Subsequence DP — 0/1 Counting | `TargetSum` |
| "Top K frequent/largest/smallest" | Heap of size K | `TopKFrequentElements`, `KthLargestElement` |
| "Merge K sorted lists" | Min-heap K-way merge | `MergeKSortedLists` |
| "Running/streaming median" | Two Heaps (max + min) | `FindMedianFromDataStream` |
| "Prefix search / autocomplete" | Trie | `ImplementTrie` |
| "Find words in grid (multiple)" | Trie + Backtracking | `WordSearchII` |
| "Number of islands" | Grid BFS/DFS | `NumberOfIslands` |
| "Course schedule / dependencies" | Topological Sort | `CourseSchedule` |
| "Connected components" | Union Find | `NumberOfProvinces` |
| "Find duplicate in array [1..n]" | Floyd's Cycle Detection | `FindDupplicateNumber` |
| "Single number (others appear 2×)" | XOR | `SingleNumber` |
| "Majority element" | Boyer-Moore Voting | `MajorityOfElements` |
| "Subarray sum equals K" | Prefix Sum + HashMap | `SubarraySumEqualsK` |
| "Product except self" | Prefix + Suffix products | `ProductOfArrayExceptSelf` |
| "Search in rotated sorted array" | Binary Search (find sorted half) | `SearchInRotatedSortedArray` |
| "Find min in rotated array" | Binary Search (compare with right) | `FindMinimumInRotatedSortedArray` |

### 🧩 Pattern Families — How They Connect

Understanding how patterns relate helps you see that most "different" problems are actually the SAME pattern in disguise:

```
FIBONACCI FAMILY (1D DP — Counting):
  ClimbingStairs → DecodeWays → WordBreak
  "How many ways?" = dp[i] = dp[i-1] + dp[i-2] (with constraints)

OPTIMIZATION FAMILY (1D DP — Max/Min):
  HouseRobber → HouseRobberII → MinCostClimbingStairs
  "Max/Min?" = dp[i] = max/min(choice1, choice2)

KNAPSACK FAMILY:
  0/1 Knapsack:    PartitionEqualSubsetSum, TargetSum (reverse iteration)
  Unbounded:       CoinChange (min), CoinChangeII (count) (forward iteration)
  KEY DIFFERENCE:  reverse = each item once, forward = unlimited reuse

GRID FAMILY (2D DP):
  Counting:      UniquePaths (dp[r][c] = dp[r-1][c] + dp[r][c-1])
  Minimization:  MinimumPathSum (dp[r][c] = min(...) + cost)

STRING FAMILY (2D DP):
  LCS → LongestPalindromicSubsequence (match = diagonal, mismatch = max of neighbors)
  EditDistance (match = free diagonal, mismatch = 1 + min of 3 neighbors)

SLIDING WINDOW FAMILY:
  Fixed:    Anagram, Permutation (window = len(pattern))
  Variable: MinWindow (shrink while valid), Longest (shrink while invalid)

SUBSET FAMILY (Backtracking):
  Subsets → CombinationSum → Permutations
  "Choose some" → "Choose some with constraint" → "Choose all (order matters)"
```

### ⚡ The 5-Second Pattern Test

When you read a problem, ask these questions IN ORDER:

```
1. "Is the array sorted?"           → Binary Search or Two Pointer
2. "Do I need to find pairs/sums?"  → HashMap or Two Pointer
3. "Is it about a subarray/substring?" → Sliding Window or Prefix Sum
4. "Do I need all combinations?"    → Backtracking
5. "Is it about optimization (max/min/count ways)?" → Dynamic Programming
6. "Do I need the top/bottom K?"    → Heap
7. "Is it about prefix matching?"   → Trie
8. "Is it a grid/matrix problem?"   → BFS/DFS or Grid DP
9. "Is it about intervals?"         → Sort + Greedy or Sweep Line
10. "Is there a dependency/order?"   → Topological Sort
```

### 🎯 Problem Transformation Cheatsheet

Many problems are just transformations of simpler ones:

| Hard Problem | It's Actually Just... | Transformation |
|-------------|----------------------|----------------|
| House Robber II | House Robber I | Break circle → two linear sub-problems |
| Longest Palindromic Subsequence | LCS | LPS(s) = LCS(s, s.reversed()) |
| Target Sum | Partition Equal Subset Sum | sum(P) = (target + total) / 2 |
| Coin Change II | Coin Change I | Min → Count; min() → sum() |
| Word Break | Climbing Stairs | Fixed steps → variable word lengths |
| Decode Ways | Climbing Stairs | Steps 1-2 with constraints |
| Min Cost Climbing Stairs | Climbing Stairs | Count → Minimize |
| Minimum Path Sum | Unique Paths | Count → Minimize |
| Word Search II | Word Search | Single word → Trie for multiple words |
| Meeting Rooms II | Merge Intervals | Overlaps → concurrent count |
| Number of Islands | Grid DFS | Grid = implicit graph |
| Car Fleet | Monotonic Stack | Time to target → decreasing stack |

---

*Built with ❤️ using Kotlin | Repository: [DSA-Learning](https://github.com/sangilimurugan492/DSA-Learning)*
