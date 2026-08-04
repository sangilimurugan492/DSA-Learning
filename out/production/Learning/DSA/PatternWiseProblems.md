# 🗂️ DSA Pattern-Wise Problem Index

> Complete scan of the `DSA/` folder — every problem mapped to its pattern.
> **Total Problems: 260+** across **20+ patterns** and **12 data structure categories**.

---

## 📑 Table of Contents

1. [Array Patterns](#1-array-patterns)
   - [1.1 Linear Scan](#11-linear-scan--14-problems)
   - [1.2 Prefix Sum](#12-prefix-sum--6-problems)
   - [1.3 Kadane's Algorithm](#13-kadanes-algorithm--3-problems--1-duplicate)
   - [1.4 Two Pointer](#14-two-pointer--29-problems--1-duplicate)
   - [1.5 Two Pointer In-Place](#15-two-pointer-in-place--8-problems)
   - [1.6 Sliding Window](#16-sliding-window--9-problems--1-duplicate)
   - [1.7 Binary Search](#17-binary-search--3-problems)
   - [1.8 HashSet Lookup](#18-hashset-lookup--6-problems)
   - [1.9 Complement Search](#19-complement-search--3-problems)
   - [1.10 Set Operations](#110-set-operations--3-problems)
   - [1.11 Frequency Count](#111-frequency-count--3-problems)
   - [1.12 Bit Manipulation (Array)](#112-bit-manipulation-array--7-problems--cheatsheet)
   - [1.13 Greedy](#113-greedy--18-problems)
   - [1.14 Voting & Floyd's Algorithm](#114-voting--floyds-algorithm--4-problems)
   - [1.15 Matrix](#115-matrix--4-problems)
   - [1.16 String Parsing](#116-string-parsing--4-problems)
2. [Stack](#2-stack--11-problems)
3. [Linked List](#3-linked-list--40-problems)
4. [Backtracking](#4-backtracking--7-problems)
5. [Dynamic Programming](#5-dynamic-programming--16-problems)
6. [Tree](#6-tree--16-problems)
7. [Heap / Priority Queue](#7-heap--priority-queue--4-problems)
8. [Graph](#8-graph--10-problems)
9. [Trie](#9-trie--2-problems)
10. [Simulation](#10-simulation--7-problems)
11. [Sorting](#11-sorting--1-file)
12. [Interview Problems](#12-interview-problems--1-problem)
13. [Recursion](#13-recursion--1-problem)
14. [Summary Statistics](#14-summary-statistics)

---

## 1. Array Patterns

> Location: `DSA/array/` — organized by technique/pattern with Brute Force → Optimal progression.

---

### 1.1 Linear Scan (14 problems)

> **Pattern:** Visit each element once. Track running values (min, max, count, etc.).
> **Location:** `DSA/array/linear_scan/`

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Find Smallest & Largest | `FindSmallestOrLargest.kt` | 🟢 |
| 2 | Find 2nd Largest & Smallest | `FindSecondLargestOrSmallestElement.kt` | 🟢 |
| 3 | Check Array Sorted or Rotated | `CheckArraySortedOrRotate.kt` | 🟢 |
| 4 | Count Frequency of Element | `CountFrequencyOfElement.kt` | 🟢 |
| 5 | Count Negatives in Matrix | `CountNegativesInMatrix.kt` | 🟢 |
| 6 | Max Consecutive Ones | `FindMaximumConsecutiveOnes.kt` | 🟢 |
| 7 | Find Pivot Index | `FindingPivotIndex.kt` | 🟢 |
| 8 | First Repeating Element | `FirstRepeatingElement.kt` | 🟢 |
| 9 | Height Checker | `HeightChecker.kt` | 🟢 |
| 10 | Leaders in Array | `LeadersInArray.kt` | 🟢 |
| 11 | Minimum Absolute Difference | `MinimumAbsoluteDifference.kt` | 🟢 |
| 12 | Number of Distinct Averages | `NumberOfDistinctAverages.kt` | 🟢 |
| 13 | Reverse a String | `ReverseAString.kt` | 🟢 |
| 14 | Reverse an Array | `ReverseAnArray.kt` | 🟢 |

> ⚠️ **Extra file:** `TestProblem.java` — a Java test file, not a problem solution.

---

### 1.2 Prefix Sum (6 problems)

> **Pattern:** Precompute cumulative sums for O(1) range queries.
> **Location:** `DSA/array/prefix_sum/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Running Sum of 1D Array | `RunningSum1DArray.kt` | 🟢 | ⭐⭐ |
| 2 | Prefix Sum Query | `PrefixSumQuery.kt` | 🟢 | ⭐⭐⭐ |
| 3 | Product of Array Except Self | `ProductOfArrayExceptSelf.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 4 | Subarray Sum Equals K | `SubarraySumEqualsK.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 5 | Contiguous Array (Equal 0s & 1s) | `ContiguousArray.kt` | 🟡 | ⭐⭐⭐⭐ |
| 6 | Longest Subarray with Sum K | `LongestSubarrayWithSumK.kt` | 🟡 | ⭐⭐⭐⭐ |

---

### 1.3 Kadane's Algorithm (3 problems + 1 duplicate)

> **Pattern:** Find max/min subarray sum/product. At each index: extend or start fresh?
> **Location:** `DSA/array/kadane_algorithm/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Maximum Subarray | `MaximumSubArray.kt` / `MaximumSubarray.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Maximum Product Subarray | `MaximumProductSubarray.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Maximum Sum Circular Subarray | `MaximumSumCircularSubarray.kt` | 🟡 | ⭐⭐⭐⭐ |

> ⚠️ **Duplicate:** `maximum_sub_array/` and `maximum_subarray/` both contain `MaximumSubArray.kt` — same problem in two folders.

---

### 1.4 Two Pointer (29 problems + 1 duplicate)

> **Pattern:** Two pointers from opposite ends or different speeds. Reduces O(N²) to O(N).
> **Location:** `DSA/array/two_pointer/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Two Sum (Sorted) | `TwoSumLevelTwoWithSortedArray.kt` | 🟡 | ⭐⭐⭐⭐ |
| 2 | Three Sum | `ThreeSum.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Three Sum Closest | `ThreeSumClosest.kt` / `ThreeSumCloset.kt` | 🟡 | ⭐⭐⭐⭐ |
| 4 | Four Sum | `FourSum.kt` | 🟡 | ⭐⭐⭐⭐ |
| 5 | Container With Most Water | `ContainerWithMostWater.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 6 | Trapping Rain Water | `TrappingRainWaterI.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 7 | Trapping Rain Water (Variant) | `TrappingRainWater.kt` | 🔴 | ⭐⭐⭐⭐ |
| 8 | Valid Palindrome | `ValidPalindrom.kt` | 🟢 | ⭐⭐⭐ |
| 9 | Is Subsequence | `IsSubSequence.kt` | 🟢 | ⭐⭐⭐ |
| 10 | Move Zeros to End | `MoveZerosToEnd.kt` | 🟢 | ⭐⭐⭐ |
| 11 | Sort Colors (Dutch Flag) | `SortColorsDutchNationalFlag.kt` | 🟡 | ⭐⭐⭐⭐ |
| 12 | Merge Sorted Array | `MergeSortedArray.kt` | 🟢 | ⭐⭐⭐⭐ |
| 13 | Merge Intervals | `MergeIntervals.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 14 | Remove Element in Array | `RemoveElementInAnArray.kt` | 🟢 | ⭐⭐⭐ |
| 15 | String Compression | `StringCompression.kt` | 🟡 | ⭐⭐⭐⭐ |
| 16 | Boats to Save People | `BoatsToSave.kt` | 🟡 | ⭐⭐⭐⭐ |
| 17 | Backspace String Compare | `BackspaceStringCompare.kt` | 🟡 | ⭐⭐⭐ |
| 18 | Longest Palindromic Substring | `LongestPalindromSubString.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 19 | Longest Substring Without Repeating | `LongestSubStringWithoutRepeatingCharacter.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 20 | Longest Repeating Char Replacement | `LongestRepeatingCharcterReplacement.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 21 | Min Size Subarray Sum | `MinimumSizeSubArraySum.kt` | 🟡 | ⭐⭐⭐⭐ |
| 22 | Subarrays with K Different Integers | `SubArrayWithKDifferentInteger.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 23 | Find All Anagrams in String | `FindAllAnagramInString.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 24 | Permutation in String | `PermutationInString.kt` | 🟡 | ⭐⭐⭐⭐ |
| 25 | Intersection of Two Arrays II | `IntersectionTwoArrayII.kt` | 🟢 | ⭐⭐⭐ |
| 26 | Min Pair Removal to Sort Array | `MinimumPairRemovaltoSortArrayI.kt` | 🟡 | ⭐⭐⭐ |
| 27 | Max Consecutive Ones III | `MaxConsecutiveOnesIII.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 28 | K-Sum Pairs | `KSumPairs.kt` | 🟡 | ⭐⭐⭐⭐ |
| 29 | Two Sum Less Than K | `TwoSumLessThanK.kt` | 🟡 | ⭐⭐⭐⭐ |
| 30 | Count Number of Nice Subarrays | `CountNumberOfNiceSubarrays.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

> ⚠️ **Duplicate:** `three_sum_closest/` and `three_sum_closet/` both contain the same problem — `ThreeSumClosest.kt` vs `ThreeSumCloset.kt` (typo variant).

---

### 1.5 Two Pointer In-Place (8 problems)

> **Pattern:** Modify array in-place using slow/fast or read/write pointers. O(1) space.
> **Location:** `DSA/array/two_pointer_inplace/`

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Remove Duplicates from Sorted Array | `RemoveDuplicateFromSortedArray.kt` | 🟢 |
| 2 | Remove Duplicates II (Keep Two) | `RemoveDuplicateseTWO.kt` | 🟡 |
| 3 | Remove Element | `RemoveElement.kt` | 🟢 |
| 4 | Replace with Greatest on Right | `ReplaceGratestElementOnRight.kt` | 🟡 |
| 5 | Sorted Squares | `SortedSquares.kt` | 🟡 |
| 6 | Rotate Array | `RotateArray.kt` | 🟡 |
| 7 | Next Permutation | `NextPermutation.kt` | 🟡 |
| 8 | Sort Colors | `SortColors.kt` | 🟡 |

---

### 1.6 Sliding Window (9 problems + 1 duplicate)

> **Pattern:** Maintain a window that slides across data. Fixed-size or Variable-size.
> **Location:** `DSA/array/sliding_window/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Max Average Subarray (Fixed K) | `MaximumAverageSubArray.kt` | 🟢 | ⭐⭐⭐ |
| 2 | Fruits into Baskets | `FruitsIntoBaskets.kt` | 🟡 | ⭐⭐⭐⭐ |
| 3 | Longest Subarray After Deleting One | `LongestSubArrayAfterDeletingOne.kt` | 🟡 | ⭐⭐⭐ |
| 4 | Grumpy Bookstore Owner | `GrumpyBookStoreOwner.kt` | 🟡 | ⭐⭐⭐ |
| 5 | Minimum Window Substring | `MinimumWindowSubstring.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 6 | Longest Repeating Char Replacement | `LongestRepeatingCharacterReplacement.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 7 | Longest Substring Without Repeating | `LongestSubstringWithoutRepeatingCharacters.kt` / `LongestSubstringWithoutRepeating.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 8 | Find All Anagrams in String | `FindAllAnagramsInString.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 9 | Permutation in String | `PermutationInString.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

> ⚠️ **Duplicate:** `longest_substring_without_repeating/` and `longest_substring_without_repeating_characters/` both contain the same problem.

---

### 1.7 Binary Search (3 problems)

> **Pattern:** Search in O(log N) on sorted/partitioned data. Identify the monotonic condition.
> **Location:** `DSA/array/binary_search/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Median of Two Sorted Arrays | `MedianOfTwoSortedSubArray.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 2 | Search in Rotated Sorted Array | `SearchInRotatedSortedArray.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Find Min in Rotated Sorted Array | `FindMinimumInRotatedSortedArray.kt` | 🟡 | ⭐⭐⭐⭐ |

---

### 1.8 HashSet Lookup (6 problems)

> **Pattern:** Use HashSet for O(1) existence checks. "Have I seen this before?"
> **Location:** `DSA/array/hashset_lookup/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Contains Duplicates | `ContainsDuplicates.kt` | 🟢 | ⭐⭐⭐ |
| 2 | Check if N and 2N Exist | `CheckIfNDoubleExits.kt` | 🟢 | ⭐⭐⭐ |
| 3 | Count Distinct Elements | `CountDistinctElements.kt` | 🟢 | ⭐⭐ |
| 4 | Longest Consecutive Sequence | `LongestConsecutiveSequence.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 5 | Valid Sudoku | `ValidSudoku.kt` | 🟡 | ⭐⭐⭐⭐ |
| 6 | Valid Anagrams | `ValidAnagrams.kt` | 🟢 | ⭐⭐⭐⭐ |

---

### 1.9 Complement Search (3 problems)

> **Pattern:** For each element, look up its complement (target - current) in a HashMap.
> **Location:** `DSA/array/complement_search/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Two Sum (Unsorted) | `TwoSumLevelOneWithoutSortedArray.kt` | 🟢 | ⭐⭐⭐⭐⭐ |
| 2 | Two Sum II (Sorted) | `TwoSumII.kt` | 🟡 | ⭐⭐⭐⭐ |
| 3 | Finding Pairs with Certain Sum | `FindingPairsWithCertainSum.kt` | 🟡 | ⭐⭐⭐ |

---

### 1.10 Set Operations (3 problems)

> **Pattern:** Set intersection, difference, and grouping operations.
> **Location:** `DSA/array/set_operations/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Intersection of Two Arrays | `IntersectionOfTwoArrays.kt` | 🟢 | ⭐⭐⭐ |
| 2 | Find Difference Between Two Arrays | `FindTheDifferenceBetweenTwoArray.kt` | 🟢 | ⭐⭐⭐ |
| 3 | Group Anagrams | `GroupAnagrams.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

---

### 1.11 Frequency Count (3 problems)

> **Pattern:** Count occurrences using HashMap, then process by frequency.
> **Location:** `DSA/array/frequency_count/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Count the Number of Pairs | `CountTheNumberOfPairs.kt` | 🟡 | ⭐⭐⭐ |
| 2 | Finding Fair Pairs | `FindingFairPairs.kt` | 🟡 | ⭐⭐⭐ |
| 3 | Top K Frequent Elements | `TopKFrequentElements.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

---

### 1.12 Bit Manipulation (Array) (7 problems + cheatsheet)

> **Pattern:** Use XOR and bit counting to find unique elements in O(1) space.
> **Location:** `DSA/array/bit_manipulation/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Single Number | `SingleNumber.kt` | 🟢 | ⭐⭐⭐⭐ |
| 2 | Single Number II | `SingleNumberII.kt` | 🟡 | ⭐⭐⭐⭐ |
| 3 | Number of 1 Bits | `NumberOf1Bits.kt` | 🟢 | ⭐⭐⭐ |
| 4 | Missing Number | `MissingNumber.kt` | 🟢 | ⭐⭐⭐⭐ |
| 5 | Sum of Two Integers | `SumOfTwoIntegers.kt` | 🟡 | ⭐⭐⭐⭐ |
| 6 | Power of Two | `PowerOfTwo.kt` | 🟢 | ⭐⭐⭐ |
| 7 | Counting Bits | `CountingBits.kt` | 🟡 | ⭐⭐⭐⭐ |
| — | **Bit Manipulation Cheatsheet** | `BitManipulationCheatsheet.md` | — | — |

---

### 1.13 Greedy (18 problems)

> **Pattern:** Make locally optimal choice at each step → globally optimal solution.
> **Location:** `DSA/array/greedy/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Best Time to Buy & Sell Stock I | `BestTimeToBuyAndSellStockI.kt` | 🟢 | ⭐⭐⭐⭐ |
| 2 | Best Time to Buy & Sell Stock II | `BestTimeToBuyAndSellStockII.kt` | 🟡 | ⭐⭐⭐⭐ |
| 3 | Jump Game | `JumpGame.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 4 | Jump Game II | `JumpGameII.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 5 | Gas Station | `GasStation.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 6 | Merge Intervals | `MergeIntervals.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 7 | Insert Interval | `InsertInterval.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 8 | Meeting Rooms | `MeetingRooms.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 9 | Non-Overlapping Intervals | `NonOverlappingIntervals.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 10 | Candy | `Candy.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 11 | Assign Cookies | `AssignCookies.kt` | 🟢 | ⭐⭐⭐ |
| 12 | Lemonade Change | `LemonadeChange.kt` | 🟢 | ⭐⭐⭐⭐ |
| 13 | Min Arrows to Burst Balloons | `MinimumArrowsToBurstBalloons.kt` | 🟡 | ⭐⭐⭐⭐ |
| 14 | Queue Reconstruction by Height | `QueueReconstructionByHeight.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 15 | Wiggle Subsequence | `WiggleSubsequence.kt` | 🟡 | ⭐⭐⭐⭐ |
| 16 | Task Scheduler | `TaskScheduler.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 17 | Partition Labels | `PartitionLabels.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 18 | Interval Groups | `IntervalGroups.kt` | 🟡 | ⭐⭐⭐ |

---

### 1.14 Voting & Floyd's Algorithm (4 problems)

> **Pattern:** Boyer-Moore Voting, Floyd's Cycle Detection, XOR tricks — O(1) space.
> **Location:** `DSA/array/voting_floyd/`

| # | Problem | File | Difficulty | Key Technique |
|---|---------|------|-----------|---------------|
| 1 | Majority Element (> n/2) | `MajorityOfElements.kt` | 🟢 | Boyer-Moore Voting |
| 2 | Majority Element II (> n/3) | `MajorityElementII.kt` | 🟡 | Extended Boyer-Moore (2 candidates) |
| 3 | Find Duplicate Number | `FindDupplicateNumber.kt` | 🟡 | Floyd's Cycle Detection |
| 4 | Missing Number in Array | `MissingNumberInArray.kt` | 🟢 | XOR or Sum formula |

---

### 1.15 Matrix (4 problems)

> **Pattern:** 2D array manipulation with in-place tricks.
> **Location:** `DSA/array/matrix/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Set Matrix Zeroes | `SetMatrixZeroes.kt` | 🟡 | ⭐⭐⭐⭐ |
| 2 | Spiral Matrix | `SpiralMatrix.kt` | 🟡 | ⭐⭐⭐⭐ |
| 3 | Rotate Image | `RotateImage.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 4 | Game of Life | `GameOfLife.kt` | 🟡 | ⭐⭐⭐⭐ |

---

### 1.16 String Parsing (4 problems)

> **Pattern:** Convert between string and number representations. Handle overflow & edge cases.
> **Location:** `DSA/array/string_parsing/`

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | String to Integer (atoi) | `StringToInteger.kt` | 🟡 |
| 2 | Integer to Roman | `IntegerToRoman.kt` | 🟡 |
| 3 | Longest Palindromic String | `LongestPalidromString.kt` | 🟡 |
| 4 | ZigZag Conversion | `ZigZagConversionString.kt` | 🟡 |

---

## 2. Stack (11 problems)

> **Core Idea:** LIFO. Essential for matching, nesting, nearest greater/smaller element patterns.
> **Location:** `DSA/stack/`

| # | Problem | File | Difficulty | Key Technique |
|---|---------|------|-----------|---------------|
| 1 | Valid Parentheses | `ValidParentheses.kt` | 🟢 | Stack push/pop matching |
| 2 | Longest Valid Parentheses | `LongestValidParentheses.kt` | 🔴 | Stack stores indices of unmatched |
| 3 | Minimum Parentheses to Remove | `MinimumParentheseToRemove.kt` | 🟡 | Count unmatched open/close |
| 4 | Daily Temperatures | `DailyTemperatures.kt` | 🟡 | Monotonic stack (decreasing) |
| 5 | Evaluate RPN | `EvaluateReversePolishNotation.kt` | 🟡 | Stack-based calculator |
| 6 | Largest Rectangle in Histogram | `LargestRectangleInHistogram.kt` | 🔴 | Monotonic stack (width × height) |
| 7 | Min Stack | `MinStack.kt` | 🟡 | Auxiliary stack for min |
| 8 | Next Greater Element I | `NextGreaterElementI.kt` | 🟡 | Monotonic stack |
| 9 | Next Greater Element II | `NextGreaterElementII.kt` | 🟡 | Monotonic stack + circular |
| 10 | Online Stock Span | `OnlineStockSpan.kt` | 🟡 | Monotonic stack |
| 11 | Car Fleet | `CarFleet.kt` | 🟡 | Sort + monotonic stack |

---

## 3. Linked List (40 problems)

> **Core Idea:** Nodes connected via pointers. O(1) insert/delete, O(N) access.
> **Location:** `DSA/linked_list/`

### 3.1 Single Linked List (25 problems + 2 base nodes)

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Reverse Linked List | `ReverseLinkedList.kt` | 🟢 |
| 2 | Reverse Linked List II | `ReverseLinkedListII.kt` | 🟡 |
| 3 | Reverse in K-Group | `ReverseLinkedListKGroup.kt` | 🔴 |
| 4 | Remove Nth from End | `RemoveNthNodeFromLast.kt` | 🟡 |
| 5 | Remove Duplicates from Sorted | `RemoveDuplicatesFromSortedList.kt` | 🟢 |
| 6 | Remove Duplicates II | `RemoveDuplicatesFromSortedListII.kt` | 🟡 |
| 7 | Reorder List | `ReorderList.kt` | 🟡 |
| 8 | Swap Nodes in Pairs | `SwapNodesInPairs.kt` | 🟡 |
| 9 | Rotate Linked List | `RotateLinkedList.kt` | 🟡 |
| 10 | Partition List | `PatiotionList.kt` | 🟡 |
| 11 | Linked List Cycle | `LinkedListCycle.kt` | 🟢 |
| 12 | Linked List Cycle II | `LinkedListCycleII.kt` | 🟡 |
| 13 | Middle of Linked List | `MiddleOfLinkedList.kt` | 🟢 |
| 14 | Palindrome Linked List | `PalindromeLinkedList.kt` | 🟡 |
| 15 | Merge Two Sorted Lists | `MergeTwoSortedLists.kt` | 🟢 |
| 16 | Merge K Sorted Lists | `MergeKSortedLists.kt` | 🔴 |
| 17 | Add Two Numbers | `AddTwoNumbers.kt` | 🟡 |
| 18 | Copy List with Random Pointer | `CopyListWithRandomPointer.kt` | 🟡 |
| 19 | Convert Sorted List to BST | `ConvertSortedListToBinarySearchTree.kt` | 🟡 |
| 20 | Sort List | `SortList.kt` | 🔴 |
| 21 | Linked List Binary to Integer | `LinkedListBinaryToInteger.kt` | 🟢 |
| 22 | Intersection of Two Linked Lists | `IntersectionOfTwoLinkedLists.kt` | 🟢 |
| 23 | Delete Node in Linked List | `DeleteNodeInLinkedList.kt` | 🟢 |
| 24 | Design Hash Set | `DesignHashSet.kt` | 🟡 |
| 25 | My Linked List (Design) | `MyLinkedList.kt` | 🟡 |
| — | ListNode (Base Node) | `ListNode.kt` | — |
| — | Node (Base Node) | `Node.kt` | — |

### 3.2 Doubly Linked List (8 problems + 1 base node)

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | LRU Cache | `LeastRecentlyUsedLRU.kt` | 🟡 |
| 2 | LFU Cache | `LeastFrequentlyUsedLFU.kt` | 🔴 |
| 3 | Flatten Multilevel DLL | `FlatternDoubleLLMultiLevel.kt` | 🟡 |
| 4 | Reverse Doubly Linked List | `ReverseDoublyLinkedList.kt` | 🟢 |
| 5 | Delete Node in DLL | `DeleteNodeInDoublyLinkedList.kt` | 🟢 |
| 6 | Insert Node in DLL | `InsertNodeInDoublyLinkedList.kt` | 🟢 |
| 7 | Find Pairs with Given Sum | `FindPairsWithGivenSum.kt` | 🟡 |
| 8 | Remove Duplicates from Sorted DLL | `RemoveDuplicatesFromSortedDLL.kt` | 🟢 |
| — | DListNode (Base Node) | `DListNode.kt` | — |

### 3.3 Circular Linked List (4 problems)

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Circular Linked List | `CircularLinkedList.kt` | 🟢 |
| 2 | Josephus Problem | `JosephusProblem.kt` | 🟡 |
| 3 | Sorted Insert in Circular LL | `SortedInsertInCircularLinkedList.kt` | 🟡 |
| 4 | Split Circular Linked List | `SplitCircularLinkedList.kt` | 🟡 |

---

## 4. Backtracking (7 problems)

> **Core Idea:** Build candidates incrementally, backtrack when invalid. CHOOSE → EXPLORE → UNDO.
> **Location:** `DSA/BackTracking/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Subsets | `Subsets.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Combination Sum | `CombinationSum.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Permutations | `Permutations.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 4 | Word Search | `WordSearch.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 5 | N-Queens | `NQueens.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 6 | Palindrome Partitioning | `PalindromePartitioning.kt` | 🟡 | ⭐⭐⭐⭐ |
| 7 | Sudoku Solver | `SudokuSolver.kt` | 🔴 | ⭐⭐⭐⭐⭐ |

---

## 5. Dynamic Programming (16 problems)

> **Core Idea:** Recursion + memoization. Every DP problem = brute force recursion optimized with caching.
> **Location:** `DSA/dp/`

### 5.1 1D DP (8 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Climbing Stairs | `ClimbingStairs.kt` | 🟢 | ⭐⭐⭐⭐⭐ |
| 2 | Min Cost Climbing Stairs | `MinCostClimbingStairs.kt` | 🟢 | ⭐⭐⭐⭐⭐ |
| 3 | House Robber | `HouseRobber.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 4 | House Robber II (Circular) | `HouseRobberII.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 5 | Coin Change (Min Coins) | `CoinChange.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 6 | Coin Change II (Combinations) | `CoinChangeII.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 7 | Decode Ways | `DecodeWays.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 8 | Word Break | `WordBreak.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

> ⚠️ **Note:** Some .kt files exist both as loose files in `dp/one_d/` and inside subfolders (e.g., `climbing_stairs/`, `coin_change/`, `decode_ways/`, `house_robber/`, `word_break/`). The subfolder versions include `.md` explanations.

### 5.2 2D DP (5 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Unique Paths | `UniquePaths.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Minimum Path Sum | `MinimumPathSum.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Longest Common Subsequence | `LongestCommonSubsequence.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 4 | Edit Distance | `EditDistance.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 5 | Longest Palindromic Subsequence | `LongestPalindromicSubsequence.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

> ⚠️ **Note:** Some .kt files exist both as loose files in `dp/two_d/` and inside subfolders (e.g., `edit_distance/`, `longest_common_subsequence/`, `unique_paths/`). The subfolder versions include `.md` explanations.

### 5.3 Subsequence DP (3 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Longest Increasing Subsequence | `LongestIncreasingSubsequence.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Partition Equal Subset Sum | `PartitionEqualSubsetSum.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Target Sum | `TargetSum.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

> ⚠️ **Note:** Some .kt files exist both as loose files in `dp/subsequence/` and inside subfolders (e.g., `longest_increasing_subsequence/`, `partition_equal_subset_sum/`). The subfolder versions include `.md` explanations.

---

## 6. Tree (16 problems)

> **Core Idea:** Recursive tree thinking — "What can I learn from left and right subtrees?"
> **Location:** `DSA/tree/`

### 6.1 Binary Tree (8 problems)

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Maximum Depth of Binary Tree | `MaximumDepthOfBinaryTree.kt` | 🟢 |
| 2 | Same Tree | `SameTree.kt` | 🟢 |
| 3 | Balanced Binary Tree | `BalancedBinaryTree.kt` | 🟢 |
| 4 | Diameter of Binary Tree | `DiameterOfBinaryTree.kt` | 🟡 |
| 5 | Lowest Common Ancestor | `LowestCommonAncestor.kt` | 🟡 |
| 6 | Subtree of Another Tree | `SubtreeOfAnotherTree.kt` | 🟢 |
| 7 | Invert Binary Tree | `InvertBinaryTree.kt` | 🟢 |
| 8 | Symmetric Tree | `SymmetricTree.kt` | 🟢 |

### 6.2 BST (5 problems)

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Search in BST | `SearchInBST.kt` | 🟢 |
| 2 | Insert into BST | `InsertIntoBST.kt` | 🟡 |
| 3 | Delete Node in BST | `DeleteNodeInBST.kt` | 🟡 |
| 4 | Kth Smallest Element in BST | `KthSmallestElementInBST.kt` | 🟡 |
| 5 | Validate BST | `ValidateBST.kt` | 🟡 |

### 6.3 Tree Traversal (3 problems)

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Tree Traversals (In/Pre/Post) | `TreeTraversals.kt` | 🟢 |
| 2 | Binary Tree Level Order Traversal | `BinaryTreeLevelOrderTraversal.kt` | 🟡 |
| 3 | Binary Tree Right Side View | `BinaryTreeRightSideView.kt` | 🟡 |

> **Base file:** `TreeNode.kt` — TreeNode class definition.

---

## 7. Heap / Priority Queue (4 problems)

> **Core Idea:** O(log K) insert/extract — ideal for "top/bottom K" and "merge K sorted" problems.
> **Location:** `DSA/heap/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Top K Frequent Elements | `TopKFrequentElements.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Kth Largest Element | `KthLargestElement.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 3 | Merge K Sorted Lists | `MergeKSortedLists.kt` | 🔴 | ⭐⭐⭐⭐⭐ |
| 4 | Find Median from Data Stream | `FindMedianFromDataStream.kt` | 🔴 | ⭐⭐⭐⭐⭐ |

---

## 8. Graph (10 problems)

> **Core Idea:** Grid = implicit graph. BFS for shortest path (unweighted), DFS for connectivity.
> **Location:** `DSA/graph/`

### 8.1 BFS / DFS (4 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Number of Islands | `NumberOfIslands.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Clone Graph | `CloneGraph.kt` | 🟡 | ⭐⭐⭐⭐ |
| 3 | Pacific Atlantic Water Flow | `PacificAtlanticWaterFlow.kt` | 🟡 | ⭐⭐⭐⭐ |
| 4 | Rotting Oranges | `RottingOranges.kt` | 🟡 | ⭐⭐⭐⭐⭐ |

### 8.2 Shortest Path (2 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Dijkstra's Algorithm | `Dijkstra.kt` | 🟡 | ⭐⭐⭐⭐ |
| 2 | Word Ladder | `WordLadder.kt` | 🔴 | ⭐⭐⭐⭐⭐ |

### 8.3 Topological Sort (2 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Course Schedule | `CourseSchedule.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Course Schedule II | `CourseScheduleII.kt` | 🟡 | ⭐⭐⭐⭐ |

### 8.4 Union Find (2 problems)

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Number of Provinces | `NumberOfProvinces.kt` | 🟡 | ⭐⭐⭐⭐ |
| 2 | Graph Valid Tree | `GraphValidTree.kt` | 🟡 | ⭐⭐⭐ |

> **Base file:** `Graph.kt` — Graph implementation with adjacency list.

---

## 9. Trie (2 problems)

> **Core Idea:** O(M) search and prefix operations independent of stored word count.
> **Location:** `DSA/trie/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Implement Trie (Prefix Tree) | `ImplementTrie.kt` | 🟡 | ⭐⭐⭐⭐⭐ |
| 2 | Word Search II | `WordSearchII.kt` | 🔴 | ⭐⭐⭐⭐⭐ |

---

## 10. Simulation (7 problems)

> **Core Idea:** Directly simulate the described process step by step.
> **Location:** `DSA/simulation/`

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Add Binary | `AddBinary.kt` | 🟢 |
| 2 | Add Strings | `AddString.kt` | 🟢 |
| 3 | Add Digits (Digital Root) | `AddDigits.kt` | 🟢 |
| 4 | Fizz Buzz | `FizzBuzz.kt` | 🟢 |
| 5 | Spiral Matrix | `SpiralMatrix.kt` | 🟡 |
| 6 | Spiral Matrix II | `SpiralMatrixII.kt` | 🟡 |
| 7 | Text Justification | `TextJustification.kt` | 🔴 |

---

## 11. Sorting (1 file)

> **Location:** `DSA/sorting/`

| # | File | Description |
|---|------|-------------|
| 1 | `sortingTest.kt` | Sorting algorithm implementations & tests |

---

## 12. Interview Problems (1 problem)

> **Location:** `DSA/interview_problem/`

| # | Problem | File | Difficulty |
|---|---------|------|-----------|
| 1 | Plus One | `PlusOne.kt` | 🟢 |

---

## 13. Recursion (1 problem)

> **Location:** `DSA/recursion/`

| # | Problem | File | Difficulty | FAANG |
|---|---------|------|-----------|-------|
| 1 | Scramble Strings | `ScrambleStrings.kt` | 🔴 | ⭐⭐⭐⭐ |

---

## 14. Summary Statistics

### By Pattern (Total Problems)

| Pattern | Count | Location |
|---------|-------|----------|
| Two Pointer | 29 (+1 dup) | `array/two_pointer/` |
| Linear Scan | 14 | `array/linear_scan/` |
| Greedy | 18 | `array/greedy/` |
| Linked List (Single) | 25 (+2 base) | `linked_list/single/` |
| Linked List (Double) | 8 (+1 base) | `linked_list/double/` |
| Linked List (Circular) | 4 | `linked_list/circular/` |
| Sliding Window | 9 (+1 dup) | `array/sliding_window/` |
| Two Pointer In-Place | 8 | `array/two_pointer_inplace/` |
| Backtracking | 7 | `BackTracking/` |
| Stack | 11 | `stack/` |
| Dynamic Programming (1D) | 8 | `dp/one_d/` |
| Dynamic Programming (2D) | 5 | `dp/two_d/` |
| Dynamic Programming (Subsequence) | 3 | `dp/subsequence/` |
| Tree | 16 | `tree/` |
| Bit Manipulation (Array) | 7 + cheatsheet | `array/bit_manipulation/` |
| HashSet Lookup | 6 | `array/hashset_lookup/` |
| Prefix Sum | 6 | `array/prefix_sum/` |
| Binary Search | 3 | `array/binary_search/` |
| Complement Search | 3 | `array/complement_search/` |
| Set Operations | 3 | `array/set_operations/` |
| Frequency Count | 3 | `array/frequency_count/` |
| Voting & Floyd's | 4 | `array/voting_floyd/` |
| Matrix | 4 | `array/matrix/` |
| String Parsing | 4 | `array/string_parsing/` |
| Kadane's Algorithm | 3 (+1 dup) | `array/kadane_algorithm/` |
| Graph | 10 | `graph/` |
| Heap | 4 | `heap/` |
| Trie | 2 | `trie/` |
| Simulation | 7 | `simulation/` |
| Recursion | 1 | `recursion/` |
| Interview Problems | 1 | `interview_problem/` |
| **TOTAL** | **~260** | |

### By Difficulty

| Difficulty | Count |
|-----------|-------|
| 🟢 Easy | ~85 |
| 🟡 Medium | ~140 |
| 🔴 Hard | ~40 |

### By FAANG Frequency (Top 15 Most Asked)

| Rank | Problem | Pattern |
|------|---------|---------|
| 1 | Two Sum | Complement Search / HashSet |
| 2 | Longest Substring Without Repeating | Sliding Window |
| 3 | Maximum Subarray | Kadane's |
| 4 | Merge Intervals | Greedy |
| 5 | Valid Parentheses | Stack |
| 6 | Climbing Stairs | 1D DP |
| 7 | Coin Change | 1D DP |
| 8 | Number of Islands | Graph BFS/DFS |
| 9 | LCS / Edit Distance | 2D DP |
| 10 | Top K Frequent Elements | Heap |
| 11 | Subsets / Permutations | Backtracking |
| 12 | Word Break | 1D DP |
| 13 | Trapping Rain Water | Two Pointer |
| 14 | 3Sum | Two Pointer |
| 15 | LRU Cache | Doubly Linked List |

---

### 📁 Folder Structure Overview

```
DSA/
├── array/                    → 120+ problems across 16 sub-patterns
│   ├── binary_search/        → 3 problems
│   ├── bit_manipulation/     → 7 problems + cheatsheet
│   ├── complement_search/    → 3 problems
│   ├── frequency_count/      → 3 problems
│   ├── greedy/               → 18 problems
│   ├── hashset_lookup/       → 6 problems
│   ├── kadane_algorithm/     → 3 problems (+1 duplicate)
│   ├── linear_scan/          → 14 problems
│   ├── matrix/               → 4 problems
│   ├── prefix_sum/           → 6 problems
│   ├── set_operations/       → 3 problems
│   ├── sliding_window/       → 9 problems (+1 duplicate)
│   ├── string_parsing/       → 4 problems
│   ├── two_pointer/          → 29 problems (+1 duplicate)
│   ├── two_pointer_inplace/  → 8 problems
│   └── voting_floyd/         → 4 problems
├── BackTracking/             → 7 problems
├── dp/                       → 16 problems
│   ├── one_d/                → 8 problems
│   ├── two_d/                → 5 problems
│   └── subsequence/          → 3 problems
├── graph/                    → 10 problems
│   ├── bfs_dfs/              → 4 problems
│   ├── shortest_path/        → 2 problems
│   ├── topological_sort/     → 2 problems
│   └── union_find/           → 2 problems
├── heap/                     → 4 problems
├── interview_problem/        → 1 problem
├── linked_list/              → 40 problems (+3 base nodes)
│   ├── circular/             → 4 problems
│   ├── double/               → 8 problems (+1 base node)
│   └── single/               → 25 problems (+2 base nodes)
├── recursion/                → 1 problem
├── simulation/               → 7 problems
├── sorting/                  → 1 file
├── stack/                    → 11 problems
├── tree/                     → 16 problems
│   ├── binary_tree/          → 8 problems
│   ├── bst/                  → 5 problems
│   └── traversal/            → 3 problems
├── trie/                     → 2 problems
├── PatternWiseProblems.md    → This file (complete pattern index)
├── README.md                 → Complete guide with roadmap
└── study.md                  → 12-week daily study plan
```

---

> 💡 **Notes:**
> - Each problem folder contains a `.kt` (solution) and most include a `.md` (detailed explanation).
> - Some problems have duplicate folders (e.g., `maximum_sub_array/` vs `maximum_subarray/` in Kadane's, `three_sum_closest/` vs `three_sum_closet/` in Two Pointer, `longest_substring_without_repeating/` vs `longest_substring_without_repeating_characters/` in Sliding Window). These are marked with ⚠️ in their respective sections.
> - The `dp/` folder has some problems stored both as loose `.kt` files and inside subfolders — the subfolder versions include `.md` explanations.
> - Base node files (`ListNode.kt`, `Node.kt`, `DListNode.kt`, `TreeNode.kt`, `Graph.kt`) are utility/data structure definitions, not problem solutions.
