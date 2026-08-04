# 📅 Daily DSA Schedule — 3 Patterns/Day, Easy → Hard

> **Format:** 6 days/week × 3 problems/day = 18 problems/week | 13 weeks = ~234 problems
> **Daily Structure:** 3 different patterns, progressing Easy → Medium → Hard
> **Day 7:** Weekly review & spaced repetition
> **Time:** ~2 hrs/day (15 min easy + 25 min medium + 35 min hard + review)

---

## 📑 Table of Contents

| Week | Phase | Patterns | Link |
|------|-------|----------|------|
| 1 | Foundation | Linear Scan, Two Pointer, HashSet | [Week 1](#week-1-foundation--linear-scan--two-pointer--hashset) |
| 2 | Foundation | Prefix Sum, Sliding Window, Complement Search | [Week 2](#week-2-foundation--prefix-sum--sliding-window--complement-search) |
| 3 | Foundation | Set Operations, Frequency Count, Bit Manipulation | [Week 3](#week-3-foundation--set-operations--frequency-count--bit-manipulation) |
| 4 | Core Patterns | Kadane's, Binary Search, Two Pointer In-Place | [Week 4](#week-4-core-patterns--kadanes--binary-search--two-pointer-in-place) |
| 5 | Core Patterns | Greedy (Part 1), Stack, Voting & Floyd's | [Week 5](#week-5-core-patterns--greedy-part-1--stack--voting--floyds) |
| 6 | Core Patterns | Greedy (Part 2), Linked List (Single), Matrix | [Week 6](#week-6-core-patterns--greedy-part-2--linked-list-single--matrix) |
| 7 | Trees & Backtracking | Backtracking, Tree (Binary), BST | [Week 7](#week-7-trees--backtracking--backtracking--tree-binary--bst) |
| 8 | Trees & Backtracking | Tree Traversal, String Parsing, Simulation | [Week 8](#week-8-trees--backtracking--tree-traversal--string-parsing--simulation) |
| 9 | Dynamic Programming | 1D DP, 2D DP, Subsequence DP | [Week 9](#week-9-dynamic-programming--1d-dp--2d-dp--subsequence-dp) |
| 10 | Advanced DS | Heap, Trie, Graph BFS/DFS | [Week 10](#week-10-advanced-ds--heap--trie--graph-bfsdfs) |
| 11 | Advanced DS | Graph Shortest Path, Topological Sort, Union Find | [Week 11](#week-11-advanced-ds--graph-shortest-path--topological-sort--union-find) |
| 12 | Advanced DS | Linked List (Double/Circular), Recursion, Interview Problems | [Week 12](#week-12-advanced-ds--linked-list-doublecircular--recursion--interview-problems) |
| 13 | Mock Interviews | FAANG Top 15, Pattern Mixing, Speed Rounds | [Week 13](#week-13-mock-interviews--faang-top-15--pattern-mixing--speed-rounds) |

---

## Week 1: Foundation — Linear Scan, Two Pointer, HashSet

### Day 1 — Linear Scan + Two Pointer + HashSet

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Linear Scan | Find Smallest & Largest | `array/linear_scan/find_smallest_or_largest/FindSmallestOrLargest.kt` | 🟢 | 15 min |
| 2 | Two Pointer | Valid Palindrome | `array/two_pointer/valid_palindrom/ValidPalindrom.kt` | 🟢 | 15 min |
| 3 | HashSet Lookup | Contains Duplicates | `array/hashset_lookup/contains_duplicates/ContainsDuplicates.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Linear scan tracks running values in one pass. Two pointer converges from ends on sorted data. HashSet gives O(1) "have I seen this?" checks.

---

### Day 2 — Linear Scan + Two Pointer + HashSet

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Linear Scan | Find 2nd Largest & Smallest | `array/linear_scan/find_second_largest_or_smallest_element/FindSecondLargestOrSmallestElement.kt` | 🟢 | 15 min |
| 2 | Two Pointer | Is Subsequence | `array/two_pointer/is_sub_sequence/IsSubSequence.kt` | 🟢 | 15 min |
| 3 | HashSet Lookup | Check if N and 2N Exist | `array/hashset_lookup/check_if_n_double_exits/CheckIfNDoubleExits.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Second largest needs two variables (max, secondMax). Subsequence uses two pointers on different strings. HashSet for paired existence checks.

---

### Day 3 — Linear Scan + Two Pointer + HashSet

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Linear Scan | Max Consecutive Ones | `array/linear_scan/find_maximum_consecutive_ones/FindMaximumConsecutiveOnes.kt` | 🟢 | 15 min |
| 2 | Two Pointer | Move Zeros to End | `array/two_pointer/move_zeros_to_end/MoveZerosToEnd.kt` | 🟢 | 15 min |
| 3 | HashSet Lookup | Count Distinct Elements | `array/hashset_lookup/count_distinct_elements/CountDistinctElements.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Consecutive ones = running count + reset on zero. Move zeros = slow/fast pointer overwrite. Distinct count = HashSet size.

---

### Day 4 — Linear Scan + Two Pointer + HashSet

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Linear Scan | Finding Pivot Index | `array/linear_scan/finding_pivot_index/FindingPivotIndex.kt` | 🟢 | 15 min |
| 2 | Two Pointer | Merge Sorted Array | `array/two_pointer/merge_sorted_array/MergeSortedArray.kt` | 🟢 | 20 min |
| 3 | HashSet Lookup | Longest Consecutive Sequence | `array/hashset_lookup/longest_consecutive_sequence/LongestConsecutiveSequence.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Pivot index = leftSum == rightSum. Merge from the END to avoid overwriting. Consecutive sequence — only start counting from sequence beginnings (num-1 not in set).

---

### Day 5 — Linear Scan + Two Pointer + HashSet

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Linear Scan | Leaders in Array | `array/linear_scan/leaders_in_array/LeadersInArray.kt` | 🟢 | 15 min |
| 2 | Two Pointer | Two Sum (Sorted) | `array/two_pointer/two_sum_level_two_with_sorted_array/TwoSumLevelTwoWithSortedArray.kt` | 🟡 | 20 min |
| 3 | HashSet Lookup | Valid Sudoku | `array/hashset_lookup/valid_sudoku/ValidSudoku.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Leaders = right-to-left scan tracking max. Two Sum sorted = converge from both ends. Sudoku = 3 HashSets (rows, cols, boxes).

---

### Day 6 — Linear Scan + Two Pointer + HashSet

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Linear Scan | First Repeating Element | `array/linear_scan/first_repeating_element/FirstRepeatingElement.kt` | 🟢 | 15 min |
| 2 | Two Pointer | Three Sum | `array/two_pointer/three_sum/ThreeSum.kt` | 🟡 | 30 min |
| 3 | HashSet Lookup | Valid Anagrams | `array/hashset_lookup/valid_anagrams/ValidAnagrams.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Repeating element = HashSet for seen values. 3Sum = sort + fix i + two-pointer on rest, skip duplicates! Anagrams = frequency map comparison.

---

### Day 7 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from this week that were ❌ or 🔄
2. Pattern summary:
   - Linear Scan: Single pass, track running values (min, max, count)
   - Two Pointer: Converge from ends (sorted), slow/fast (in-place)
   - HashSet: O(1) existence checks, "have I seen this?"
3. PRIORITY REVIEW: Three Sum + Longest Consecutive Sequence
```

---

## Week 2: Foundation — Prefix Sum, Sliding Window, Complement Search

### Day 8 — Prefix Sum + Sliding Window + Complement Search

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Prefix Sum | Running Sum of 1D Array | `array/prefix_sum/running_sum1_d_array/RunningSum1DArray.kt` | 🟢 | 15 min |
| 2 | Sliding Window | Max Average Subarray (Fixed K) | `array/sliding_window/maximum_average_sub_array/MaximumAverageSubArray.kt` | 🟢 | 15 min |
| 3 | Complement Search | Two Sum (Unsorted) | `array/complement_search/two_sum_level_one_without_sorted_array/TwoSumLevelOneWithoutSortedArray.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Prefix sum = cumulative running total. Fixed sliding window = add right, remove left when > K. Complement search = HashMap stores target - current.

---

### Day 9 — Prefix Sum + Sliding Window + Complement Search

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Prefix Sum | Prefix Sum Query | `array/prefix_sum/prefix_sum_query/PrefixSumQuery.kt` | 🟢 | 15 min |
| 2 | Sliding Window | Fruits into Baskets | `array/sliding_window/fruits_into_baskets/FruitsIntoBaskets.kt` | 🟡 | 20 min |
| 3 | Complement Search | Two Sum II (Sorted) | `array/complement_search/two_sum_ii/TwoSumII.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Prefix sum enables O(1) range queries. Fruits = variable window with "at most 2 distinct." Two Sum II sorted = two-pointer or binary search.

---

### Day 10 — Prefix Sum + Sliding Window + Complement Search

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Prefix Sum | Product of Array Except Self | `array/prefix_sum/product_of_array_except_self/ProductOfArrayExceptSelf.kt` | 🟡 | 25 min |
| 2 | Sliding Window | Longest Substring Without Repeating | `array/sliding_window/longest_substring_without_repeating_characters/LongestSubstringWithoutRepeatingCharacters.kt` | 🟡 | 25 min |
| 3 | Complement Search | Finding Pairs with Certain Sum | `array/complement_search/finding_pairs_with_certain_sum/FindingPairsWithCertainSum.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Product Except Self = prefix × suffix products. Longest substring = variable window, shrink on duplicate. These are TOP FAANG questions!

---

### Day 11 — Prefix Sum + Sliding Window + Complement Search

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Prefix Sum | Subarray Sum Equals K | `array/prefix_sum/subarray_sum_equals_k/SubarraySumEqualsK.kt` | 🟡 | 25 min |
| 2 | Sliding Window | Longest Repeating Char Replacement | `array/sliding_window/longest_repeating_character_replacement/LongestRepeatingCharacterReplacement.kt` | 🟡 | 25 min |
| 3 | Complement Search | Two Sum Less Than K | `array/two_pointer/two_sum_less_than_k/TwoSumLessThanK.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Subarray Sum K = prefix sum + HashMap (count of prefix sums). Char Replacement: `windowLen - maxFreq ≤ k`. Two Sum Less Than K = sort + two-pointer counting.

---

### Day 12 — Prefix Sum + Sliding Window + Complement Search

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Prefix Sum | Contiguous Array (Equal 0s & 1s) | `array/prefix_sum/contiguous_array/ContiguousArray.kt` | 🟡 | 25 min |
| 2 | Sliding Window | Find All Anagrams in String | `array/sliding_window/find_all_anagrams_in_string/FindAllAnagramsInString.kt` | 🟡 | 25 min |
| 3 | Complement Search | Two Sum Less Than K (review) | `array/two_pointer/two_sum_less_than_k/TwoSumLessThanK.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Contiguous Array = treat 0 as -1, prefix sum + HashMap (first occurrence). Anagrams = frequency map + match counting in fixed window.

---

### Day 13 — Prefix Sum + Sliding Window + Complement Search

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Prefix Sum | Longest Subarray with Sum K | `array/prefix_sum/longest_subarray_with_sum_k/LongestSubarrayWithSumK.kt` | 🟡 | 25 min |
| 2 | Sliding Window | Minimum Window Substring | `array/sliding_window/minimum_window_substring/MinimumWindowSubstring.kt` | 🔴 | 35 min |
| 3 | Complement Search | Finding Fair Pairs | `array/frequency_count/finding_fair_pairs/FindingFairPairs.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Longest subarray with sum K = prefix sum + HashMap (earliest index). Min Window Substring = THE hardest sliding window — expand until formed, shrink to minimize.

---

### Day 14 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 2 that were ❌ or 🔄
2. Pattern summary:
   - Prefix Sum: Precompute → O(1) range queries, HashMap for subarray sums
   - Sliding Window: Fixed (window=K) vs Variable (shrink while invalid/valid)
   - Complement Search: HashMap stores complement (target - current)
3. PRIORITY REVIEW: Product of Array Except Self + Subarray Sum Equals K + Minimum Window Substring
```

---

## Week 3: Foundation — Set Operations, Frequency Count, Bit Manipulation

### Day 15 — Set Operations + Frequency Count + Bit Manipulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Set Operations | Intersection of Two Arrays | `array/set_operations/intersection_of_two_arrays/IntersectionOfTwoArrays.kt` | 🟢 | 15 min |
| 2 | Frequency Count | Count the Number of Pairs | `array/frequency_count/count_the_number_of_pairs/CountTheNumberOfPairs.kt` | 🟡 | 20 min |
| 3 | Bit Manipulation | Single Number | `array/bit_manipulation/single_number/SingleNumber.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Set intersection = retainAll or HashSet. Frequency count = HashMap for occurrences. Single Number = XOR: a^a=0, a^0=a.

---

### Day 16 — Set Operations + Frequency Count + Bit Manipulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Set Operations | Find Difference Between Two Arrays | `array/set_operations/find_the_difference_between_two_array/FindTheDifferenceBetweenTwoArray.kt` | 🟢 | 15 min |
| 2 | Frequency Count | Finding Fair Pairs | `array/frequency_count/finding_fair_pairs/FindingFairPairs.kt` | 🟡 | 25 min |
| 3 | Bit Manipulation | Number of 1 Bits | `array/bit_manipulation/number_of_1_bits/NumberOf1Bits.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Set difference = elements in A not in B. Fair Pairs = sort + two-pointer. Count bits = n & (n-1) trick or shift & mask.

---

### Day 17 — Set Operations + Frequency Count + Bit Manipulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Set Operations | Group Anagrams | `array/set_operations/group_anagrams/GroupAnagrams.kt` | 🟡 | 25 min |
| 2 | Frequency Count | Top K Frequent Elements | `array/frequency_count/top_k_frequent_elements/TopKFrequentElements.kt` | 🟡 | 25 min |
| 3 | Bit Manipulation | Missing Number | `array/bit_manipulation/missing_number/MissingNumber.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Group Anagrams = sorted string as HashMap key. Top K Frequent = bucket sort O(N). Missing Number = XOR all indices and values, or sum formula.

---

### Day 18 — Set Operations + Frequency Count + Bit Manipulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Set Operations | Group Anagrams (review) | `array/set_operations/group_anagrams/GroupAnagrams.kt` | 🟡 | 20 min |
| 2 | Frequency Count | Top K Frequent (review) | `array/frequency_count/top_k_frequent_elements/TopKFrequentElements.kt` | 🟡 | 20 min |
| 3 | Bit Manipulation | Sum of Two Integers | `array/bit_manipulation/sum_of_two_integers/SumOfTwoIntegers.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Sum without + = XOR for sum, AND + shift for carry, loop until no carry. This is how CPUs add!

---

### Day 19 — Set Operations + Frequency Count + Bit Manipulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Set Operations | Valid Anagrams (review) | `array/hashset_lookup/valid_anagrams/ValidAnagrams.kt` | 🟢 | 15 min |
| 2 | Frequency Count | Count the Number of Pairs (review) | `array/frequency_count/count_the_number_of_pairs/CountTheNumberOfPairs.kt` | 🟡 | 20 min |
| 3 | Bit Manipulation | Power of Two | `array/bit_manipulation/power_of_two/PowerOfTwo.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Power of two = exactly one bit set: `n > 0 && (n & (n-1)) == 0`.

---

### Day 20 — Set Operations + Frequency Count + Bit Manipulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Set Operations | Intersection of Two Arrays (review) | `array/set_operations/intersection_of_two_arrays/IntersectionOfTwoArrays.kt` | 🟢 | 10 min |
| 2 | Frequency Count | Finding Fair Pairs (review) | `array/frequency_count/finding_fair_pairs/FindingFairPairs.kt` | 🟡 | 20 min |
| 3 | Bit Manipulation | Counting Bits | `array/bit_manipulation/counting_bits/CountingBits.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Counting bits for all numbers 0..n: `dp[i] = dp[i >> 1] + (i & 1)` — reuse previous results!

---

### Day 21 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 3 that were ❌ or 🔄
2. Pattern summary:
   - Set Operations: Intersection, difference, grouping (sorted key as HashMap key)
   - Frequency Count: HashMap for occurrences, bucket sort for Top K
   - Bit Manipulation: XOR for unique, n&(n-1) for bit counting, DP for counting bits
3. PRIORITY REVIEW: Group Anagrams + Top K Frequent Elements + Single Number
```

---

## Week 4: Core Patterns — Kadane's, Binary Search, Two Pointer In-Place

### Day 22 — Kadane's + Binary Search + Two Pointer In-Place

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Kadane's | Maximum Subarray | `array/kadane_algorithm/maximum_sub_array/MaximumSubArray.kt` | 🟡 | 20 min |
| 2 | Binary Search | Search in Rotated Sorted Array | `array/binary_search/search_in_rotated_sorted_array/SearchInRotatedSortedArray.kt` | 🟡 | 25 min |
| 3 | Two Pointer In-Place | Remove Duplicates from Sorted Array | `array/two_pointer_inplace/remove_duplicate_from_sorted_array/RemoveDuplicateFromSortedArray.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Kadane's: `localMax = max(arr[i], localMax + arr[i])`. Rotated array: one half is always sorted, find which. Remove duplicates = slow/fast pointer overwrite.

---

### Day 23 — Kadane's + Binary Search + Two Pointer In-Place

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Kadane's | Maximum Product Subarray | `array/kadane_algorithm/maximum_product_subarray/MaximumProductSubarray.kt` | 🟡 | 25 min |
| 2 | Binary Search | Find Min in Rotated Sorted Array | `array/binary_search/find_minimum_in_rotated_sorted_array/FindMinimumInRotatedSortedArray.kt` | 🟡 | 25 min |
| 3 | Two Pointer In-Place | Remove Element | `array/two_pointer_inplace/remove_element/RemoveElement.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Product subarray = track BOTH max AND min (negative × negative = positive). Find min in rotated = compare mid with right. Remove element = overwrite in-place.

---

### Day 24 — Kadane's + Binary Search + Two Pointer In-Place

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Kadane's | Maximum Sum Circular Subarray | `array/kadane_algorithm/maximum_sum_circular_subarray/MaximumSumCircularSubarray.kt` | 🟡 | 25 min |
| 2 | Binary Search | Median of Two Sorted Arrays | `array/binary_search/median_of_two_sorted_arrays/MedianOfTwoSortedSubArray.kt` | 🔴 | 40 min |
| 3 | Two Pointer In-Place | Remove Duplicates II (Keep Two) | `array/two_pointer_inplace/remove_duplicatese_two/RemoveDuplicateseTWO.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Circular subarray = `max(normal Kadane, totalSum - minSubarraySum)`. Median of two sorted = binary search on partition, O(log(min(m,n))).

---

### Day 25 — Kadane's + Binary Search + Two Pointer In-Place

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Kadane's | Maximum Subarray (review) | `array/kadane_algorithm/maximum_sub_array/MaximumSubArray.kt` | 🟡 | 15 min |
| 2 | Binary Search | Search in Rotated (review) | `array/binary_search/search_in_rotated_sorted_array/SearchInRotatedSortedArray.kt` | 🟡 | 20 min |
| 3 | Two Pointer In-Place | Sorted Squares | `array/two_pointer_inplace/sorted_squares/SortedSquares.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Sorted squares = two pointers from ends (negatives square to large), merge into result from the back.

---

### Day 26 — Kadane's + Binary Search + Two Pointer In-Place

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Kadane's | Maximum Product Subarray (review) | `array/kadane_algorithm/maximum_product_subarray/MaximumProductSubarray.kt` | 🟡 | 20 min |
| 2 | Binary Search | Find Min in Rotated (review) | `array/binary_search/find_minimum_in_rotated_sorted_array/FindMinimumInRotatedSortedArray.kt` | 🟡 | 20 min |
| 3 | Two Pointer In-Place | Rotate Array | `array/two_pointer_inplace/rotate_array/RotateArray.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Rotate array = reverse all → reverse first k → reverse remaining. The reversal trick!

---

### Day 27 — Kadane's + Binary Search + Two Pointer In-Place

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Kadane's | Maximum Sum Circular (review) | `array/kadane_algorithm/maximum_sum_circular_subarray/MaximumSumCircularSubarray.kt` | 🟡 | 20 min |
| 2 | Binary Search | Median of Two Sorted (review) | `array/binary_search/median_of_two_sorted_arrays/MedianOfTwoSortedSubArray.kt` | 🔴 | 35 min |
| 3 | Two Pointer In-Place | Next Permutation | `array/two_pointer_inplace/next_permutation/NextPermutation.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Next permutation = find pivot (first decreasing from right), swap with next larger, reverse suffix.

---

### Day 28 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 4 that were ❌ or 🔄
2. Pattern summary:
   - Kadane's: Extend or start fresh? Track local max/min + global max
   - Binary Search: Find the sorted half, check if target is in range
   - Two Pointer In-Place: Slow/fast overwrite, reversal tricks
3. PRIORITY REVIEW: Maximum Subarray + Median of Two Sorted Arrays + Next Permutation
```

---

## Week 5: Core Patterns — Greedy (Part 1), Stack, Voting & Floyd's

### Day 29 — Greedy + Stack + Voting & Floyd's

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Best Time to Buy & Sell Stock I | `array/greedy/best_time_to_buy_and_sell_stock_i/BestTimeToBuyAndSellStockI.kt` | 🟢 | 15 min |
| 2 | Stack | Valid Parentheses | `stack/valid_parentheses/ValidParentheses.kt` | 🟢 | 15 min |
| 3 | Voting & Floyd's | Majority Element (> n/2) | `array/voting_floyd/majority_of_elements/MajorityOfElements.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Stock I = track minPrice, maxProfit. Valid parens = push opening, pop on closing. Boyer-Moore = candidate + count, majority always survives.

---

### Day 30 — Greedy + Stack + Voting & Floyd's

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Best Time to Buy & Sell Stock II | `array/greedy/best_time_to_buy_and_sell_stock_ii/BestTimeToBuyAndSellStockII.kt` | 🟡 | 20 min |
| 2 | Stack | Evaluate Reverse Polish Notation | `stack/evaluate_reverse_polish_notation/EvaluateReversePolishNotation.kt` | 🟡 | 20 min |
| 3 | Voting & Floyd's | Majority Element II (> n/3) | `array/voting_floyd/majority_element_ii/MajorityElementII.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Stock II = sum all positive daily differences. RPN = push operands, pop on operator. Majority II = 2 candidates with extended Boyer-Moore.

---

### Day 31 — Greedy + Stack + Voting & Floyd's

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Jump Game | `array/greedy/jump_game/JumpGame.kt` | 🟡 | 20 min |
| 2 | Stack | Min Stack | `stack/min_stack/MinStack.kt` | 🟡 | 20 min |
| 3 | Voting & Floyd's | Find Duplicate Number | `array/voting_floyd/find_dupplicate_number/FindDupplicateNumber.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Jump Game = track farthest reachable index. Min Stack = auxiliary stack for min. Find Duplicate = Floyd's cycle detection on array (treat as LL).

---

### Day 32 — Greedy + Stack + Voting & Floyd's

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Jump Game II | `array/greedy/jump_game_ii/JumpGameII.kt` | 🟡 | 25 min |
| 2 | Stack | Daily Temperatures | `stack/daily_temperatures/DailyTemperatures.kt` | 🟡 | 25 min |
| 3 | Voting & Floyd's | Missing Number in Array | `array/voting_floyd/missing_number_in_array/MissingNumberInArray.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Jump Game II = BFS-like level counting (greedy). Daily Temperatures = monotonic decreasing stack. Missing Number = XOR all or sum formula.

---

### Day 33 — Greedy + Stack + Voting & Floyd's

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Assign Cookies | `array/greedy/assign_cookies/AssignCookies.kt` | 🟢 | 15 min |
| 2 | Stack | Next Greater Element I | `stack/next_greater_element_i/NextGreaterElementI.kt` | 🟡 | 25 min |
| 3 | Voting & Floyd's | Find Duplicate (review) | `array/voting_floyd/find_dupplicate_number/FindDupplicateNumber.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Assign Cookies = sort both, greedy match. Next Greater = monotonic stack, while top < current, pop and set answer.

---

### Day 34 — Greedy + Stack + Voting & Floyd's

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Lemonade Change | `array/greedy/lemonade_change/LemonadeChange.kt` | 🟢 | 15 min |
| 2 | Stack | Next Greater Element II (Circular) | `stack/next_greater_element_ii/NextGreaterElementII.kt` | 🟡 | 25 min |
| 3 | Voting & Floyd's | Majority Element (review) | `array/voting_floyd/majority_of_elements/MajorityOfElements.kt` | 🟢 | 10 min |

🔑 **Key Insight:** Lemonade = track 5s and 10s for change. Next Greater II = iterate 2× array length (simulate circular with modulo).

---

### Day 35 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 5 that were ❌ or 🔄
2. Pattern summary:
   - Greedy: Sort + scan, locally optimal → globally optimal
   - Stack: LIFO for matching, monotonic for "next greater/smaller"
   - Voting & Floyd's: Boyer-Moore for majority, cycle detection for duplicates
3. PRIORITY REVIEW: Jump Game + Daily Temperatures + Find Duplicate Number
```

---

## Week 6: Core Patterns — Greedy (Part 2), Linked List (Single), Matrix

### Day 36 — Greedy + Linked List + Matrix

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Merge Intervals | `array/greedy/merge_intervals/MergeIntervals.kt` | 🟡 | 20 min |
| 2 | Linked List (Single) | Reverse Linked List | `linked_list/single/reverse_linked_list/ReverseLinkedList.kt` | 🟢 | 15 min |
| 3 | Matrix | Set Matrix Zeroes | `array/matrix/set_matrix_zeroes/SetMatrixZeroes.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Merge Intervals = sort by start, merge if overlap. Reverse LL = prev, curr, next triplet. Set Zeroes = use first row/col as markers.

---

### Day 37 — Greedy + Linked List + Matrix

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Insert Interval | `array/greedy/insert_interval/InsertInterval.kt` | 🟡 | 25 min |
| 2 | Linked List (Single) | Linked List Cycle | `linked_list/single/linked_list_cycle/LinkedListCycle.kt` | 🟢 | 15 min |
| 3 | Matrix | Spiral Matrix | `array/matrix/spiral_matrix/SpiralMatrix.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Insert Interval = add non-overlapping, merge overlapping, add rest. Cycle = fast/slow pointer. Spiral = four boundaries (top/bottom/left/right).

---

### Day 38 — Greedy + Linked List + Matrix

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Meeting Rooms | `array/greedy/meeting_rooms/MeetingRooms.kt` | 🟡 | 25 min |
| 2 | Linked List (Single) | Middle of Linked List | `linked_list/single/middle_of_linked_list/MiddleOfLinkedList.kt` | 🟢 | 15 min |
| 3 | Matrix | Rotate Image | `array/matrix/rotate_image/RotateImage.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Meeting Rooms = sort by start, check overlaps. Middle = fast/slow pointer. Rotate Image = transpose + reverse rows.

---

### Day 39 — Greedy + Linked List + Matrix

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Non-Overlapping Intervals | `array/greedy/non_overlapping_intervals/NonOverlappingIntervals.kt` | 🟡 | 25 min |
| 2 | Linked List (Single) | Merge Two Sorted Lists | `linked_list/single/merge_two_sorted_lists/MergeTwoSortedLists.kt` | 🟢 | 15 min |
| 3 | Matrix | Game of Life | `array/matrix/game_of_life/GameOfLife.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Non-overlapping = sort by END, pick earliest finish. Merge sorted = dummy head + compare. Game of Life = intermediate states (2=dead→live, 3=live→dead).

---

### Day 40 — Greedy + Linked List + Matrix

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Gas Station | `array/greedy/gas_station/GasStation.kt` | 🟡 | 30 min |
| 2 | Linked List (Single) | Remove Nth from End | `linked_list/single/remove_nth_node_from_last/RemoveNthNodeFromLast.kt` | 🟡 | 20 min |
| 3 | Matrix | Spiral Matrix (review) | `array/matrix/spiral_matrix/SpiralMatrix.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Gas Station = if total surplus ≥ 0, solution exists; skip ALL stations when tank < 0. Remove Nth = fast pointer N ahead, then slow follows.

---

### Day 41 — Greedy + Linked List + Matrix

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Greedy | Candy | `array/greedy/candy/Candy.kt` | 🔴 | 35 min |
| 2 | Linked List (Single) | Palindrome Linked List | `linked_list/single/palindrome_linked_list/PalindromeLinkedList.kt` | 🟡 | 25 min |
| 3 | Matrix | Rotate Image (review) | `array/matrix/rotate_image/RotateImage.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Candy = two-pass L→R then R→L, take max. Palindrome LL = find mid + reverse 2nd half + compare.

---

### Day 42 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 6 that were ❌ or 🔄
2. Pattern summary:
   - Greedy: Sort + scan, intervals (merge/insert/non-overlap), locally optimal
   - Linked List: Pointer manipulation, fast/slow, dummy head
   - Matrix: In-place tricks (markers, transpose+reverse, spiral boundaries)
3. PRIORITY REVIEW: Merge Intervals + Gas Station + Candy
```

---

## Week 7: Trees & Backtracking — Backtracking, Tree (Binary), BST

### Day 43 — Backtracking + Binary Tree + BST

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Backtracking | Subsets | `BackTracking/subsets/Subsets.kt` | 🟡 | 25 min |
| 2 | Binary Tree | Maximum Depth of Binary Tree | `tree/binary_tree/MaximumDepthOfBinaryTree.kt` | 🟢 | 15 min |
| 3 | BST | Search in BST | `tree/bst/SearchInBST.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Subsets = include/exclude each element → 2^N. Max depth = 1 + max(left, right). BST search = go left if < root, right if > root.

---

### Day 44 — Backtracking + Binary Tree + BST

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Backtracking | Combination Sum | `BackTracking/combination_sum/CombinationSum.kt` | 🟡 | 25 min |
| 2 | Binary Tree | Same Tree | `tree/binary_tree/SameTree.kt` | 🟢 | 15 min |
| 3 | BST | Validate BST | `tree/bst/ValidateBST.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Combination Sum = stay at same index to allow reuse. Same tree = recursive structure comparison. Validate BST = min/max range check.

---

### Day 45 — Backtracking + Binary Tree + BST

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Backtracking | Permutations | `BackTracking/permutations/Permutations.kt` | 🟡 | 25 min |
| 2 | Binary Tree | Balanced Binary Tree | `tree/binary_tree/BalancedBinaryTree.kt` | 🟢 | 15 min |
| 3 | BST | Insert into BST | `tree/bst/InsertIntoBST.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Permutations = order matters, use used[] array, try ALL positions. Balanced = check height diff ≤ 1 at each node. Insert = traverse to correct leaf position.

---

### Day 46 — Backtracking + Binary Tree + BST

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Backtracking | Word Search | `BackTracking/word_search/WordSearch.kt` | 🟡 | 30 min |
| 2 | Binary Tree | Diameter of Binary Tree | `tree/binary_tree/DiameterOfBinaryTree.kt` | 🟡 | 20 min |
| 3 | BST | Delete Node in BST | `tree/bst/DeleteNodeInBST.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Word Search = grid DFS + mark visited with '#', restore after backtracking. Diameter = max(leftHeight + rightHeight) across all nodes. Delete = find inorder successor.

---

### Day 47 — Backtracking + Binary Tree + BST

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Backtracking | N-Queens | `BackTracking/n_queens/NQueens.kt` | 🔴 | 40 min |
| 2 | Binary Tree | Subtree of Another Tree | `tree/binary_tree/SubtreeOfAnotherTree.kt` | 🟢 | 20 min |
| 3 | BST | Kth Smallest Element in BST | `tree/bst/KthSmallestElementInBST.kt` | 🟡 | 25 min |

🔑 **Key Insight:** N-Queens = place row by row, check column + diagonals. Subtree = check if same tree at each node. Kth smallest = inorder traversal (sorted order).

---

### Day 48 — Backtracking + Binary Tree + BST

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Backtracking | Palindrome Partitioning | `BackTracking/palindrome_partitioning/PalindromePartitioning.kt` | 🟡 | 30 min |
| 2 | Binary Tree | Lowest Common Ancestor | `tree/binary_tree/LowestCommonAncestor.kt` | 🟡 | 25 min |
| 3 | BST | Validate BST (review) | `tree/bst/ValidateBST.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Palindrome Partitioning = try all cuts, check palindrome. LCA = if current is p or q, return it; recurse left and right. BST validate = min/max range.

---

### Day 49 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 7 that were ❌ or 🔄
2. Pattern summary:
   - Backtracking: CHOOSE → EXPLORE → UNDO (draw recursion tree first!)
   - Binary Tree: "What can I learn from left and right subtrees?"
   - BST: left < root < right, inorder = sorted, min/max range for validation
3. PRIORITY REVIEW: Subsets + Permutations + N-Queens
```

---

## Week 8: Trees & Backtracking — Tree Traversal, String Parsing, Simulation

### Day 50 — Tree Traversal + String Parsing + Simulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Tree Traversal | Tree Traversals (In/Pre/Post) | `tree/traversal/TreeTraversals.kt` | 🟢 | 15 min |
| 2 | String Parsing | String to Integer (atoi) | `array/string_parsing/string_to_integer/StringToInteger.kt` | 🟡 | 25 min |
| 3 | Simulation | Add Binary | `simulation/add_binary/AddBinary.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Tree traversals = DFS recursive (left-root-right, root-left-right, left-right-root). atoi = handle sign, overflow, whitespace. Add Binary = carry-based addition.

---

### Day 51 — Tree Traversal + String Parsing + Simulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Tree Traversal | Binary Tree Level Order Traversal | `tree/traversal/BinaryTreeLevelOrderTraversal.kt` | 🟡 | 20 min |
| 2 | String Parsing | Integer to Roman | `array/string_parsing/integer_to_roman/IntegerToRoman.kt` | 🟡 | 25 min |
| 3 | Simulation | Add Strings | `simulation/add_string/AddString.kt` | 🟢 | 15 min |

🔑 **Key Insight:** Level order = BFS with queue. Integer to Roman = greedy subtract from largest values. Add Strings = digit-by-digit with carry.

---

### Day 52 — Tree Traversal + String Parsing + Simulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Tree Traversal | Binary Tree Right Side View | `tree/traversal/BinaryTreeRightSideView.kt` | 🟡 | 25 min |
| 2 | String Parsing | Longest Palindromic String | `array/string_parsing/longest_palidrom_string/LongestPalidromString.kt` | 🟡 | 30 min |
| 3 | Simulation | Fizz Buzz | `simulation/fizz_buzz/FizzBuzz.kt` | 🟢 | 10 min |

🔑 **Key Insight:** Right side view = BFS, take last node at each level. Longest Palindromic = expand around center (odd + even length). FizzBuzz = modulo checks.

---

### Day 53 — Tree Traversal + String Parsing + Simulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Tree Traversal | Level Order (review) | `tree/traversal/BinaryTreeLevelOrderTraversal.kt` | 🟡 | 15 min |
| 2 | String Parsing | ZigZag Conversion | `array/string_parsing/zig_zag_conversion_string/ZigZagConversionString.kt` | 🟡 | 25 min |
| 3 | Simulation | Add Digits (Digital Root) | `simulation/add_digits/AddDigits.kt` | 🟢 | 15 min |

🔑 **Key Insight:** ZigZag = simulate rows going up and down. Add Digits = digital root: `1 + (n-1) % 9`.

---

### Day 54 — Tree Traversal + String Parsing + Simulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Tree Traversal | Right Side View (review) | `tree/traversal/BinaryTreeRightSideView.kt` | 🟡 | 20 min |
| 2 | String Parsing | String to Integer (review) | `array/string_parsing/string_to_integer/StringToInteger.kt` | 🟡 | 20 min |
| 3 | Simulation | Spiral Matrix | `simulation/spiral_matrix/SpiralMatrix.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Spiral = four boundaries shrinking inward. Simulation = directly follow the described process step by step.

---

### Day 55 — Tree Traversal + String Parsing + Simulation

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Tree Traversal | Invert Binary Tree | `tree/binary_tree/invert_binary_tree/InvertBinaryTree.kt` | 🟢 | 15 min |
| 2 | String Parsing | Longest Palindromic (review) | `array/string_parsing/longest_palidrom_string/LongestPalidromString.kt` | 🟡 | 25 min |
| 3 | Simulation | Text Justification | `simulation/text_justification/TextJustification.kt` | 🔴 | 35 min |

🔑 **Key Insight:** Invert tree = swap left/right, recurse. Text Justification = greedy line packing, handle last line and spacing edge cases.

---

### Day 56 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 8 that were ❌ or 🔄
2. Pattern summary:
   - Tree Traversal: DFS (recursive) for in/pre/post, BFS (queue) for level-order
   - String Parsing: Handle overflow, edge cases, convert between representations
   - Simulation: Directly simulate the described process step by step
3. PRIORITY REVIEW: Level Order Traversal + Longest Palindromic + Text Justification
```

---

## Week 9: Dynamic Programming — 1D DP, 2D DP, Subsequence DP

### Day 57 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | Climbing Stairs | `dp/one_d/ClimbingStairs.kt` | 🟢 | 15 min |
| 2 | 2D DP | Unique Paths | `dp/two_d/UniquePaths.kt` | 🟡 | 20 min |
| 3 | Subsequence DP | Longest Increasing Subsequence | `dp/subsequence/LongestIncreasingSubsequence.kt` | 🟡 | 30 min |

🔑 **Key Insight:** Climbing Stairs = Fibonacci: `ways(n) = ways(n-1) + ways(n-2)`. Unique Paths = `dp[r][c] = dp[r-1][c] + dp[r][c-1]`. LIS = O(N log N) patience sorting with binary search.

---

### Day 58 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | Min Cost Climbing Stairs | `dp/one_d/MinCostClimbingStairs.kt` | 🟢 | 15 min |
| 2 | 2D DP | Minimum Path Sum | `dp/two_d/MinimumPathSum.kt` | 🟡 | 20 min |
| 3 | Subsequence DP | Partition Equal Subset Sum | `dp/subsequence/PartitionEqualSubsetSum.kt` | 🟡 | 30 min |

🔑 **Key Insight:** Min Cost = `dp[i] = cost[i] + min(dp[i-1], dp[i-2])`. Min Path Sum = `dp[r][c] += min(dp[r-1][c], dp[r][c-1])`. Partition = 0/1 knapsack, iterate target in REVERSE.

---

### Day 59 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | House Robber | `dp/one_d/HouseRobber.kt` | 🟡 | 20 min |
| 2 | 2D DP | Longest Common Subsequence | `dp/two_d/LongestCommonSubsequence.kt` | 🟡 | 25 min |
| 3 | Subsequence DP | Target Sum | `dp/subsequence/TargetSum.kt` | 🟡 | 30 min |

🔑 **Key Insight:** House Robber = `dp[i] = max(dp[i-1], dp[i-2] + nums[i])` (ROB or SKIP). LCS = match: `1+dp[i-1][j-1]`, no match: `max(dp[i-1][j], dp[i][j-1])`. Target Sum = count subsets summing to (target+total)/2.

---

### Day 60 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | House Robber II (Circular) | `dp/one_d/HouseRobberII.kt` | 🟡 | 25 min |
| 2 | 2D DP | Edit Distance | `dp/two_d/EditDistance.kt` | 🟡 | 30 min |
| 3 | Subsequence DP | LIS (review) | `dp/subsequence/LongestIncreasingSubsequence.kt` | 🟡 | 20 min |

🔑 **Key Insight:** House Robber II = circular! Break into two linear: `max(rob[0..n-2], rob[1..n-1])`. Edit Distance = 3 operations: `1 + min(insert, delete, replace)`.

---

### Day 61 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | Coin Change (Min Coins) | `dp/one_d/CoinChange.kt` | 🟡 | 25 min |
| 2 | 2D DP | Longest Palindromic Subsequence | `dp/two_d/LongestPalindromicSubsequence.kt` | 🟡 | 25 min |
| 3 | Subsequence DP | Partition Equal Subset (review) | `dp/subsequence/PartitionEqualSubsetSum.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Coin Change = `dp[a] = min(dp[a-coin] + 1)` for each coin. LPS(s) = LCS(s, s.reversed()).

---

### Day 62 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | Coin Change II (Combinations) | `dp/one_d/CoinChangeII.kt` | 🟡 | 25 min |
| 2 | 2D DP | Edit Distance (review) | `dp/two_d/EditDistance.kt` | 🟡 | 25 min |
| 3 | Subsequence DP | Target Sum (review) | `dp/subsequence/TargetSum.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Coin Change II = `dp[a] += dp[a-coin]`. CRITICAL: coins OUTER loop, amounts INNER loop → counts COMBINATIONS (not permutations).

---

### Day 63 — 1D DP + 2D DP + Subsequence DP

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | 1D DP | Decode Ways | `dp/one_d/DecodeWays.kt` | 🟡 | 25 min |
| 2 | 2D DP | LCS (review) | `dp/two_d/LongestCommonSubsequence.kt` | 🟡 | 20 min |
| 3 | Subsequence DP | LIS (review) | `dp/subsequence/LongestIncreasingSubsequence.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Decode Ways = Climbing Stairs with CONSTRAINTS (valid 1-digit, valid 2-digit). `dp[i] = dp[i-1] (if valid 1-digit) + dp[i-2] (if valid 2-digit)`.

---

### Day 64 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 9 that were ❌ or 🔄
2. DP Problem-Solving Checklist:
   □ What is the state? (dp[i] or dp[i][j])
   □ What is the recurrence?
   □ What are the base cases?
   □ What is the answer?
   □ Can I space-optimize?
3. PRIORITY REVIEW: House Robber + Coin Change + LCS + Edit Distance
```

---

## Week 10: Advanced DS — Heap, Trie, Graph BFS/DFS

### Day 65 — Heap + Trie + Graph BFS/DFS

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Heap | Top K Frequent Elements | `heap/top_k_frequent_elements/TopKFrequentElements.kt` | 🟡 | 25 min |
| 2 | Trie | Implement Trie (Prefix Tree) | `trie/implement_trie/ImplementTrie.kt` | 🟡 | 25 min |
| 3 | Graph BFS/DFS | Number of Islands | `graph/bfs_dfs/NumberOfIslands.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Top K = min-heap of size K or bucket sort. Trie = 26 children + isEnd flag, all O(M). Number of Islands = DFS/BFS, mark visited by changing '1' → '2'.

---

### Day 66 — Heap + Trie + Graph BFS/DFS

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Heap | Kth Largest Element | `heap/kth_largest_element/KthLargestElement.kt` | 🟡 | 25 min |
| 2 | Trie | Word Search II | `trie/word_search_ii/WordSearchII.kt` | 🔴 | 40 min |
| 3 | Graph BFS/DFS | Clone Graph | `graph/bfs_dfs/CloneGraph.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Kth Largest = min-heap of size K or QuickSelect O(N). Word Search II = Trie + Backtracking — build trie from words, DFS board once. Clone Graph = HashMap(old→new) + DFS.

---

### Day 67 — Heap + Trie + Graph BFS/DFS

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Heap | Merge K Sorted Lists | `heap/merge_k_sorted_lists/MergeKSortedLists.kt` | 🔴 | 35 min |
| 2 | Trie | Implement Trie (review) | `trie/implement_trie/ImplementTrie.kt` | 🟡 | 20 min |
| 3 | Graph BFS/DFS | Pacific Atlantic Water Flow | `graph/bfs_dfs/PacificAtlanticWaterFlow.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Merge K Sorted = push all heads into min-heap, pop min, push next. O(N log K). Pacific Atlantic = start from oceans, flow inward!

---

### Day 68 — Heap + Trie + Graph BFS/DFS

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Heap | Find Median from Data Stream | `heap/find_median_from_data_stream/FindMedianFromDataStream.kt` | 🔴 | 35 min |
| 2 | Trie | Word Search II (review) | `trie/word_search_ii/WordSearchII.kt` | 🔴 | 30 min |
| 3 | Graph BFS/DFS | Rotting Oranges | `graph/bfs_dfs/rotting_oranges/RottingOranges.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Median = max-heap (smaller half) + min-heap (larger half), balance sizes. Rotting Oranges = multi-source BFS, count fresh remaining.

---

### Day 69 — Heap + Trie + Graph BFS/DFS

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Heap | Kth Largest (review) | `heap/kth_largest_element/KthLargestElement.kt` | 🟡 | 20 min |
| 2 | Trie | Implement Trie (review) | `trie/implement_trie/ImplementTrie.kt` | 🟡 | 15 min |
| 3 | Graph BFS/DFS | Number of Islands (review) | `graph/bfs_dfs/NumberOfIslands.kt` | 🟡 | 20 min |

🔑 **Key Insight:** QuickSelect = like QuickSort but only recurse into one partition. Average O(N), worst O(N²).

---

### Day 70 — Heap + Trie + Graph BFS/DFS

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Heap | Merge K Sorted (review) | `heap/merge_k_sorted_lists/MergeKSortedLists.kt` | 🔴 | 30 min |
| 2 | Trie | Word Search II (review) | `trie/word_search_ii/WordSearchII.kt` | 🔴 | 30 min |
| 3 | Graph BFS/DFS | Clone Graph (review) | `graph/bfs_dfs/CloneGraph.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Merge K review — focus on the heap comparator and edge cases (empty lists). Clone Graph — HashMap prevents revisiting.

---

### Day 71 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 10 that were ❌ or 🔄
2. Pattern summary:
   - Heap: "Top K" → min-heap of size K, "Running median" → two heaps
   - Trie: O(M) prefix operations, build from words + DFS board
   - Graph BFS/DFS: BFS (shortest unweighted), DFS (explore all), mark visited
3. PRIORITY REVIEW: Merge K Sorted Lists + Find Median + Number of Islands
```

---

## Week 11: Advanced DS — Graph Shortest Path, Topological Sort, Union Find

### Day 72 — Shortest Path + Topological Sort + Union Find

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Shortest Path | Dijkstra's Algorithm | `graph/shortest_path/Dijkstra.kt` | 🟡 | 25 min |
| 2 | Topological Sort | Course Schedule | `graph/topological_sort/CourseSchedule.kt` | 🟡 | 25 min |
| 3 | Union Find | Number of Provinces | `graph/union_find/NumberOfProvinces.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Dijkstra = min-heap of (dist, node), process smallest first. Course Schedule = detect cycle (can finish?). Union Find = union(a,b) merges sets, count components.

---

### Day 73 — Shortest Path + Topological Sort + Union Find

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Shortest Path | Word Ladder | `graph/shortest_path/WordLadder.kt` | 🔴 | 35 min |
| 2 | Topological Sort | Course Schedule II | `graph/topological_sort/CourseScheduleII.kt` | 🟡 | 25 min |
| 3 | Union Find | Graph Valid Tree | `graph/union_find/GraphValidTree.kt` | 🟡 | 25 min |

🔑 **Key Insight:** Word Ladder = BFS on implicit graph (words differ by 1 letter). Course Schedule II = return the ordering (Kahn's or DFS + stack). Valid Tree = n-1 edges + connected (no cycle).

---

### Day 74 — Shortest Path + Topological Sort + Union Find

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Shortest Path | Dijkstra (review) | `graph/shortest_path/Dijkstra.kt` | 🟡 | 20 min |
| 2 | Topological Sort | Course Schedule (review) | `graph/topological_sort/CourseSchedule.kt` | 🟡 | 20 min |
| 3 | Union Find | Number of Provinces (review) | `graph/union_find/NumberOfProvinces.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Dijkstra review — focus on the visited set and relaxation step. Union Find with path compression makes find() nearly O(1).

---

### Day 75 — Shortest Path + Topological Sort + Union Find

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Shortest Path | Word Ladder (review) | `graph/shortest_path/WordLadder.kt` | 🔴 | 30 min |
| 2 | Topological Sort | Course Schedule II (review) | `graph/topological_sort/CourseScheduleII.kt` | 🟡 | 20 min |
| 3 | Union Find | Graph Valid Tree (review) | `graph/union_find/GraphValidTree.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Word Ladder optimization = bidirectional BFS (from start and end simultaneously). Course Schedule II = Kahn's algorithm with indegree array.

---

### Day 76 — Shortest Path + Topological Sort + Union Find

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Shortest Path | Dijkstra (review) | `graph/shortest_path/Dijkstra.kt` | 🟡 | 20 min |
| 2 | Topological Sort | Course Schedule (review) | `graph/topological_sort/CourseSchedule.kt` | 🟡 | 20 min |
| 3 | Union Find | Number of Provinces (review) | `graph/union_find/NumberOfProvinces.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Focus on writing clean implementations from memory. These are common interview questions — speed matters!

---

### Day 77 — Shortest Path + Topological Sort + Union Find

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Shortest Path | Word Ladder (review) | `graph/shortest_path/WordLadder.kt` | 🔴 | 30 min |
| 2 | Topological Sort | Course Schedule II (review) | `graph/topological_sort/CourseScheduleII.kt` | 🟡 | 20 min |
| 3 | Union Find | Graph Valid Tree (review) | `graph/union_find/GraphValidTree.kt` | 🟡 | 20 min |

🔑 **Key Insight:** Final review — ensure you can implement all graph algorithms from memory. Focus on edge cases (disconnected, single node, self-loop).

---

### Day 78 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 11 that were ❌ or 🔄
2. Pattern summary:
   - Shortest Path: Dijkstra (weighted, min-heap), BFS (unweighted), bidirectional for optimization
   - Topological Sort: Kahn's (indegree) or DFS + stack, detect cycle first
   - Union Find: union(a,b) + find(a) with path compression, count connected components
3. PRIORITY REVIEW: Dijkstra + Course Schedule + Word Ladder
```

---

## Week 12: Advanced DS — Linked List (Double/Circular), Recursion, Interview Problems

### Day 79 — Doubly Linked List + Circular Linked List + Recursion

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Doubly Linked List | Reverse Doubly Linked List | `linked_list/double/reverse_doubly_linked_list/ReverseDoublyLinkedList.kt` | 🟢 | 15 min |
| 2 | Circular Linked List | Circular Linked List | `linked_list/circular/circular_linked_list/CircularLinkedList.kt` | 🟢 | 15 min |
| 3 | Recursion | Scramble Strings | `recursion/scramble_strings/ScrambleStrings.kt` | 🔴 | 35 min |

🔑 **Key Insight:** Reverse DLL = swap prev and next for each node. Circular LL = tail.next = head. Scramble Strings = recursive partition + swap check.

---

### Day 80 — Doubly Linked List + Circular Linked List + Recursion

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Doubly Linked List | Delete Node in DLL | `linked_list/double/delete_node_in_doubly_linked_list/DeleteNodeInDoublyLinkedList.kt` | 🟢 | 15 min |
| 2 | Circular Linked List | Josephus Problem | `linked_list/circular/josephus_problem/JosephusProblem.kt` | 🟡 | 25 min |
| 3 | Recursion | Scramble Strings (review) | `recursion/scramble_strings/ScrambleStrings.kt` | 🔴 | 30 min |

🔑 **Key Insight:** Delete DLL node = update prev.next and next.prev. Josephus = mathematical recurrence: `J(n,k) = (J(n-1,k) + k) % n`.

---

### Day 81 — Doubly Linked List + Circular Linked List + Recursion

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Doubly Linked List | Insert Node in DLL | `linked_list/double/insert_node_in_doubly_linked_list/InsertNodeInDoublyLinkedList.kt` | 🟢 | 15 min |
| 2 | Circular Linked List | Sorted Insert in Circular LL | `linked_list/circular/sorted_insert_in_circular_linked_list/SortedInsertInCircularLinkedList.kt` | 🟡 | 25 min |
| 3 | Recursion | Scramble Strings (review) | `recursion/scramble_strings/ScrambleStrings.kt` | 🔴 | 30 min |

🔑 **Key Insight:** Insert DLL = update 4 pointers (prev.next, new.prev, new.next, next.prev). Sorted insert in circular = find correct position in cycle.

---

### Day 82 — Doubly Linked List + Circular Linked List + Recursion

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Doubly Linked List | Find Pairs with Given Sum (DLL) | `linked_list/double/find_pairs_with_given_sum/FindPairsWithGivenSum.kt` | 🟡 | 25 min |
| 2 | Circular Linked List | Split Circular Linked List | `linked_list/circular/split_circular_linked_list/SplitCircularLinkedList.kt` | 🟡 | 25 min |
| 3 | Recursion | Scramble Strings (review) | `recursion/scramble_strings/ScrambleStrings.kt` | 🔴 | 30 min |

🔑 **Key Insight:** Find pairs in DLL = two-pointer from head and tail (sorted DLL). Split circular = find mid, break into two cycles.

---

### Day 83 — Doubly Linked List + Circular Linked List + Recursion

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Doubly Linked List | Remove Duplicates from Sorted DLL | `linked_list/double/remove_duplicates_from_sorted_dll/RemoveDuplicatesFromSortedDLL.kt` | 🟢 | 15 min |
| 2 | Circular Linked List | Josephus (review) | `linked_list/circular/josephus_problem/JosephusProblem.kt` | 🟡 | 20 min |
| 3 | Recursion | Scramble Strings (review) | `recursion/scramble_strings/ScrambleStrings.kt` | 🔴 | 30 min |

🔑 **Key Insight:** Remove duplicates DLL = skip nodes with same value. Josephus review — focus on the recurrence and base case.

---

### Day 84 — Doubly Linked List + Circular Linked List + Recursion

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | Doubly Linked List | Flatten Multilevel DLL | `linked_list/double/flattern_double_ll_multi_level/FlatternDoubleLLMultiLevel.kt` | 🟡 | 30 min |
| 2 | Circular Linked List | Split Circular (review) | `linked_list/circular/split_circular_linked_list/SplitCircularLinkedList.kt` | 🟡 | 20 min |
| 3 | Recursion | Scramble Strings (review) | `recursion/scramble_strings/ScrambleStrings.kt` | 🔴 | 30 min |

🔑 **Key Insight:** Flatten DLL = stack or recursion (depth-first), connect child list into parent level.

---

### Day 85 — 🔄 Weekly Review & Spaced Repetition

```
1. Re-solve 3 problems from Week 12 that were ❌ or 🔄
2. Pattern summary:
   - Doubly Linked List: prev + next pointers, O(1) operations at both ends
   - Circular Linked List: tail.next = head, find mid in cycle
   - Recursion: Break problem into subproblems, identify base case
3. PRIORITY REVIEW: Flatten Multilevel DLL + Josephus + Scramble Strings
```

---

## Week 13: Mock Interviews — FAANG Top 15, Pattern Mixing, Speed Rounds

### Day 86 — FAANG Top 5 + Pattern Mixing + Speed

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | FAANG Top 1 | Two Sum | `array/complement_search/two_sum_level_one_without_sorted_array/TwoSumLevelOneWithoutSortedArray.kt` | 🟢 | 15 min |
| 2 | Pattern Mixing | Minimum Window Substring | `array/sliding_window/minimum_window_substring/MinimumWindowSubstring.kt` | 🔴 | 35 min |
| 3 | Speed Round | Solve 3 easy problems in 30 min | (pick any 3 easy) | 🟢 | 30 min |

🔑 **Key Insight:** Two Sum = HashMap for complement. Min Window = sliding window + HashMap. Speed = build confidence and velocity.

---

### Day 87 — FAANG Top 6-10 + Pattern Mixing + Speed

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | FAANG Top 6 | Climbing Stairs | `dp/one_d/ClimbingStairs.kt` | 🟢 | 10 min |
| 2 | Pattern Mixing | Word Search II | `trie/word_search_ii/WordSearchII.kt` | 🔴 | 40 min |
| 3 | Speed Round | Solve 3 easy problems in 30 min | (pick any 3 easy) | 🟢 | 30 min |

🔑 **Key Insight:** Climbing Stairs = Fibonacci. Word Search II = Trie + Backtracking combined. Speed = pattern recognition under pressure.

---

### Day 88 — FAANG Top 11-15 + Pattern Mixing + Speed

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | FAANG Top 11 | Subsets | `BackTracking/subsets/Subsets.kt` | 🟡 | 25 min |
| 2 | Pattern Mixing | Merge K Sorted Lists | `heap/merge_k_sorted_lists/MergeKSortedLists.kt` | 🔴 | 35 min |
| 3 | Speed Round | Solve 3 medium problems in 45 min | (pick any 3 medium) | 🟡 | 45 min |

🔑 **Key Insight:** Subsets = include/exclude backtracking. Merge K = Heap + Linked List. Speed medium = focus on clean implementation.

---

### Day 89 — FAANG Top + Pattern Mixing + Hard Challenge

| # | Pattern | Problem | File | Difficulty | Time |
|---|---------|---------|------|-----------|------|
| 1 | FAANG Top 3 | Maximum Subarray | `array/kadane_algorithm/maximum_sub_array/MaximumSubArray.kt` | 🟡 | 15 min |
| 2 | Pattern Mixing | Find Median from Data Stream | `heap/find_median_from_data_stream/FindMedianFromDataStream.kt` | 🔴 | 35 min |
| 3 | Hard Challenge | Median of Two Sorted Arrays | `array/binary_search/median_of_two_sorted_arrays/MedianOfTwoSortedSubArray.kt` | 🔴 | 40 min |

🔑 **Key Insight:** Kadane's = extend or start fresh. Median Stream = two heaps. Median of two sorted = binary search on partition.

---

### Day 90 — Full Mock Interview #1 + #2

```
MOCK INTERVIEW #1 (45 min):
  Easy (15 min): Valid Parentheses / Reverse Linked List / Two Sum
  Medium (30 min): 3Sum / Merge Intervals / Number of Islands

MOCK INTERVIEW #2 (45 min):
  Easy (15 min): Best Time to Buy & Sell Stock / Contains Duplicate / Climbing Stairs
  Medium/Hard (30 min): Trapping Rain Water / Word Break / LRU Cache

Rules: No solutions, talk out loud, write clean code, analyze complexity
```

---

### Day 91 — Full Mock Interview #3 + Final Review

```
MOCK INTERVIEW #3 (45 min):
  Easy (15 min): Maximum Subarray / Valid Parentheses / Middle of Linked List
  Medium/Hard (30 min): Edit Distance / Find Median from Data Stream / Minimum Window Substring

FINAL REVIEW:
  - List all patterns you struggled with
  - Re-solve 3 weakest problems
  - Write down the KEY INSIGHT for each pattern in one sentence
```

---

### Day 92 — 🔄 Final Review & Ongoing Practice Plan

```
PATTERN RECOGNITION QUICK FIRE:
For each keyword, write down the pattern and one example problem:

"Find a pair..."           → Complement Search / Two Pointer → Two Sum
"Longest subarray/substring" → Sliding Window / Prefix Sum → Longest Substring Without Repeating
"Valid parentheses..."     → Stack → Valid Parentheses
"Next greater element"     → Monotonic Stack → Daily Temperatures
"Detect cycle"             → Fast/Slow Pointer → Linked List Cycle
"All combinations"         → Backtracking → Subsets
"Optimal scheduling"       → Greedy → Merge Intervals
"Range queries"            → Prefix Sum → Subarray Sum Equals K
"Count ways / max / min"   → Dynamic Programming → Climbing Stairs
"Compare two strings"      → 2D DP → LCS / Edit Distance
"Can we select subset?"    → 0/1 Knapsack DP → Partition Equal Subset Sum
"Top K elements"           → Heap → Top K Frequent Elements
"Prefix matching"          → Trie → Implement Trie
"Connected components"     → Union Find / DFS → Number of Islands
"Course prerequisites"     → Topological Sort → Course Schedule

ONGOING PRACTICE (after 13 weeks):
  DAILY (30 min): 1 Easy (warmup) + 1 Medium (keep sharp)
  WEEKLY (2 hrs): 1 Full mock interview + review weak problems
  BI-WEEKLY (3 hrs): 1 Hard problem + review a full topic
```

---

## 📊 Summary Statistics

| Metric | Value |
|--------|-------|
| Total Weeks | 13 |
| Total Days | 91 (6 practice + 1 review per week) |
| Problems Covered | ~234 |
| Patterns Covered | 20+ |
| Daily Time | ~2 hours |
| Problems/Day | 3 (Easy → Medium → Hard) |

---

## 💡 Tips for Success

1. **Consistency > Intensity** — 2 hours daily beats 14 hours on weekends
2. **3 Patterns/Day** — Builds cross-pattern recognition, prevents tunnel vision
3. **Easy → Hard** — Warmup builds confidence, hard problem pushes limits
4. **Spaced Repetition** — Re-solve problems on Day 3, Day 7, Day 14 after first solve
5. **Pattern Recognition** — After 50+ problems, patterns become automatic
6. **Don't Memorize, Understand** — Understanding the pattern lets you re-derive solutions
7. **Talk Out Loud** — Practice explaining your approach as you code
8. **Time Yourself** — 15 min Easy, 25 min Medium, 35 min Hard

---

## 🔗 Quick Links

| Resource | Link |
|----------|------|
| Interview Schedule README | [README.md](README.md) |
| DSA Pattern Index | [../DSA/PatternWiseProblems.md](../DSA/PatternWiseProblems.md) |
| DSA Study Guide | [../DSA/study.md](../DSA/study.md) |
| Quick Reference | [Quick_Reference.md](Quick_Reference.md) |
