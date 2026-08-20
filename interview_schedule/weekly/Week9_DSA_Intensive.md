# Week 9: DSA Intensive

> **Duration:** 1 week | **Hours:** 18 hrs | **DSA Problems:** ~30 | **No theory — pure DSA**

---

## 📅 Daily Schedule

| Day | Time | Focus | Problems |
|-----|------|-------|----------|
| Mon | 2hr | DP (1D) — Fibonacci, Climbing Stairs, House Robber | 4 problems |
| Tue | 2hr | DP (1D) — Coin Change, Word Break, Longest Increasing Subsequence | 4 problems |
| Wed | 2hr | DP (2D) — Unique Paths, Minimum Path Sum, Edit Distance | 4 problems |
| Thu | 2hr | DP (Subsequence) — Longest Common Subsequence, Longest Palindromic Subsequence | 4 problems |
| Fri | 2hr | Sorting + Matrix — Merge Sort, Quick Sort, Spiral Matrix, Rotate Image | 4 problems |
| Sat | 4hr | Hard problems — DP + Graph + Tree + Heap combined | 6 problems |
| Sun | 4hr | Timed practice — 2 problems in 45 min each + review | 4 problems |

---

## 📖 DSA Files to Review

### All DSA Folders
| Folder | Link | Topics |
|--------|------|--------|
| `DSA/array/` | [Link](../../DSA/array/) | binary_search, bit_manipulation, complement_search, frequency_count, greedy, hashset_lookup, kadane_algorithm, linear_scan, matrix, prefix_sum, set_operations, sliding_window, string_parsing, two_pointer, two_pointer_inplace, voting_floyd |
| `DSA/BackTracking/` | [Link](../../DSA/BackTracking/) | CombinationSum, Permutations, Subsets, WordSearch, LetterAndCombinationPhoneNumber |
| `DSA/dp/` | [Link](../../DSA/dp/) | one_d, subsequence, two_d |
| `DSA/graph/` | [Link](../../DSA/graph/) | bfs_dfs, shortest_path, topological_sort, union_find |
| `DSA/heap/` | [Link](../../DSA/heap/) | FindMedianFromDataStream, KthLargestElement, MergeKSortedLists, TopKFrequentElements |
| `DSA/linked_list/` | [Link](../../DSA/linked_list/) | circular, double, single |
| `DSA/simulation/` | [Link](../../DSA/simulation/) | AddBinary, AddDigits, AddString, FizzBuzz, SpiralMatrix, SpiralMatrixII, TextJustification |
| `DSA/sorting/` | [Link](../../DSA/sorting/) | sortingTest |
| `DSA/stack/` | [Link](../../DSA/stack/) | CarFleet, DailyTemperatures, EvaluateReversePolishNotation, LargestRectangleInHistogram, LongestValidParentheses, MinimumParentheseToRemove, MinStack, NextGreaterElementI, NextGreaterElementII, OnlineStockSpan, ValidParentheses |
| `DSA/tree/` | [Link](../../DSA/tree/) | binary_tree, bst, traversal, TreeNode |
| `DSA/trie/` | [Link](../../DSA/trie/) | ImplementTrie, WordSearchII |
| `DSA/interview_problem/` | [Link](../../DSA/interview_problem/) | PlusOne |
| DSA Study Guide | [study.md](../../DSA/study.md) | Overall guide |

---

## 🧮 DSA Problem Plan (30 problems)

### Monday: DP 1D (4 problems)
| # | Problem | Pattern | Time Target |
|---|---------|---------|-------------|
| 1 | Climbing Stairs | Fibonacci pattern | 15 min |
| 2 | House Robber | Decision DP | 20 min |
| 3 | Coin Change | Unbounded knapsack | 25 min |
| 4 | Word Break | String DP | 30 min |

### Tuesday: DP 1D continued (4 problems)
| # | Problem | Pattern | Time Target |
|---|---------|---------|-------------|
| 5 | Longest Increasing Subsequence | Binary search + DP | 30 min |
| 6 | Maximum Subarray (Kadane's) | Kadane's algorithm | 20 min |
| 7 | Decode Ways | Counting DP | 25 min |
| 8 | Jump Game II | Greedy/DP | 25 min |

### Wednesday: DP 2D (4 problems)
| # | Problem | Pattern | Time Target |
|---|---------|---------|-------------|
| 9 | Unique Paths | Grid DP | 15 min |
| 10 | Minimum Path Sum | Grid DP | 20 min |
| 11 | Edit Distance | String DP 2D | 35 min |
| 12 | Longest Palindromic Substring | Expand/DP | 30 min |

### Thursday: DP Subsequence (4 problems)
| # | Problem | Pattern | Time Target |
|---|---------|---------|-------------|
| 13 | Longest Common Subsequence | 2D string DP | 25 min |
| 14 | Longest Palindromic Subsequence | 2D string DP | 30 min |
| 15 | Distinct Subsequences | Count DP | 35 min |
| 16 | Interleaving String | 2D boolean DP | 35 min |

### Friday: Sorting + Matrix (4 problems)
| # | Problem | Pattern | Time Target |
|---|---------|---------|-------------|
| 17 | Merge Sort implementation | Divide & conquer | 25 min |
| 18 | Quick Sort implementation | Partition | 25 min |
| 19 | Spiral Matrix (review) | Simulation | 20 min |
| 20 | Rotate Image | Matrix transform | 20 min |

### Saturday: Hard Problems (6 problems)
| # | Problem | Pattern | Time Target |
|---|---------|---------|-------------|
| 21 | Median of Two Sorted Arrays | Binary search | 40 min |
| 22 | Merge K Sorted Lists | Heap | 30 min |
| 23 | Find Median from Data Stream | Heap (two heaps) | 35 min |
| 24 | Largest Rectangle in Histogram | Monotonic stack | 35 min |
| 25 | Word Search II | Trie + backtracking | 40 min |
| 26 | Text Justification | Simulation | 35 min |

### Sunday: Timed Practice (4 problems)
| # | Problem | Time Limit | Focus |
|---|---------|------------|-------|
| 27 | Pick any medium | 25 min | Speed |
| 28 | Pick any medium | 25 min | Speed |
| 29 | Pick any hard | 45 min | Accuracy |
| 30 | Pick any hard | 45 min | Accuracy |

---

## 🧠 Key DSA Patterns to Master

### Dynamic Programming
| Pattern | Template | Example |
|---------|----------|---------|
| 1D DP | `dp[i] = f(dp[i-1], dp[i-2])` | Fibonacci, House Robber |
| 2D Grid | `dp[i][j] = f(dp[i-1][j], dp[i][j-1])` | Unique Paths |
| String DP | `dp[i][j] = f(s1[0..i], s2[0..j])` | LCS, Edit Distance |
| Knapsack | `dp[i][w] = max(dp[i-1][w], dp[i-1][w-wt[i]] + val[i])` | Coin Change |
| Subsequence | `dp[i][j] = if s1[i]==s2[j] then dp[i-1][j-1]+1` | LCS |

### Two Pointers
| Pattern | When | Example |
|---------|------|---------|
| Opposite direction | Sorted array, pair sum | Two Sum II |
| Same direction | In-place modification | Remove Duplicates |
| Fast & slow | Cycle detection | Linked List Cycle |

### Sliding Window
| Pattern | When | Example |
|---------|------|---------|
| Fixed size | Subarray of size K | Max Sum Subarray |
| Variable size | Condition-based | Longest Substring |

### Monotonic Stack
| Pattern | When | Example |
|---------|------|---------|
| Next greater | Find next larger element | Daily Temperatures |
| Largest rectangle | Area problems | Largest Rectangle in Histogram |

### Graph
| Pattern | When | Example |
|---------|------|---------|
| BFS | Shortest path (unweighted) | Word Ladder |
| DFS | Connectivity, cycle detection | Number of Islands |
| Union-Find | Dynamic connectivity | Accounts Merge |
| Topological Sort | Dependency ordering | Course Schedule |

---

## ✅ Self-Assessment Checklist

- [ ] Solved 30 DP/sorting/matrix/hard problems
- [ ] Can identify DP pattern from problem statement
- [ ] Can write DP recurrence relation on paper
- [ ] Can implement merge sort and quick sort from memory
- [ ] Can solve medium DP in <30 min
- [ ] Can solve hard DP in <45 min
- [ ] Comfortable with all DSA patterns above
- [ ] Reviewed all DSA folders in the repo

---

## 🔗 Next
- [Week 10: Design Patterns + System Design](Week10_Design_Patterns_System_Design.md)
- [Back to README](README.md)
