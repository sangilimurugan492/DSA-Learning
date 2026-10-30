# 🧪 Week 03 Practice — HashMap + Stack (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 3 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Contains Duplicates — LC 217
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,1] | true | Basic |
| 2 | [1,2,3,4] | false | All unique |
| 3 | [1,1,1,3,3,4,3,2,4,2] | true | Many dups |

### 2. Check if N and 2N Exist — LC 1346
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [10,2,5,3] | true | 10 = 2×5 |
| 2 | [7,1,14,11] | true | 14 = 2×7 |
| 3 | [3,1,7,11] | false | No pair |

### 3. Count Distinct Elements
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,2,3,3] | 3 | Duplicates |
| 2 | [5,5,5] | 1 | All same |
| 3 | [] | 0 | Empty |

### 4. Valid Anagrams — LC 242
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "anagram", "nagaram" | true | Basic |
| 2 | "rat", "car" | false | Different letters |
| 3 | "a", "ab" | false | Different lengths |

### 5. Intersection of Two Arrays — LC 349
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,2,1], [2,2] | [2] | Unique values |
| 2 | [4,9,5], [9,4,9,8,4] | [9,4] (any order) | Partial |
| 3 | [1], [2] | [] | Disjoint |

### 6. Find Difference Between Two Arrays — LC 2215
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3], [2,4,6] | [[1,3],[4,6]] | Basic |
| 2 | [1,2,3,3], [1,1,2,2] | [[3],[]] | Duplicates |
| 3 | [], [] | [[],[]] | Empty |

### 7. Two Sum (Unsorted) — LC 1
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,7,11,15], target=9 | [0,1] | Basic |
| 2 | [3,2,4], target=6 | [1,2] | Not first |
| 3 | [3,3], target=6 | [0,1] | Duplicate pair |

### 8. Valid Parentheses — LC 20
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "()" | true | Basic |
| 2 | "(]" | false | Mismatch |
| 3 | "([)]" | false | Wrong nesting |
| 4 | "]" | false | Closer first |

### 9. Two Sum II (Sorted) — LC 167
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,7,11,15], target=9 | [1,2] | Basic |
| 2 | [2,3,4], target=6 | [1,3] | Even length |
| 3 | [-1,0], target=-1 | [1,2] | Negatives |

### 10. Finding Pairs with Certain Sum — LC 1865
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | nums1=[1,1,2,1,2], nums2=[1,4,2,1,1,4]; count(7) | 8 | Classic |
| 2 | same; count(3) | 0 | No pairs |
| 3 | nums1=[1], nums2=[1]; count(2) | 1 | Minimal |

### 11. Longest Consecutive Sequence — LC 128
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [100,4,200,1,3,2] | 4 | 1,2,3,4 |
| 2 | [0,3,7,2,5,8,4,6,0,1] | 9 | 0..8 |
| 3 | [] | 0 | Empty |

### 12. Valid Sudoku — LC 36
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | Valid classic board | true | Valid |
| 2 | Board with duplicate '5' in row | false | Row dup |
| 3 | Board with duplicate in 3×3 box | false | Box dup |

### 13. Group Anagrams — LC 49
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | ["eat","tea","tan","ate","nat","bat"] | [[eat,tea,ate],[tan,nat],[bat]] | Classic |
| 2 | [""] | [[""]] | Empty string |
| 3 | ["a"] | [["a"]] | Single |

### 14. Count the Number of Pairs — LC 2006
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,2,1], k=1 | 4 | |1-2|=1 pairs |
| 2 | [3,2,1,5,4], k=2 | 3 | Classic |
| 3 | [1,3], k=3 | 0 | No pairs |

### 15. Finding Fair Pairs — LC 2563
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [0,1,7,4,4,5], lower=3, upper=6 | 6 | Classic |
| 2 | [1,7,9,2,5], lower=11, upper=11 | 1 | Exact bounds |
| 3 | [], lower=0, upper=0 | 0 | Empty |

### 16. Top K Frequent Elements — LC 347
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,1,2,2,3], k=2 | [1,2] | Classic |
| 2 | [1], k=1 | [1] | Single |
| 3 | [3,0,1,0], k=1 | [0] | Tie broken by freq |

### 17. Min Stack — LC 155
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | push(-2), push(0), push(-3), getMin, pop, top, getMin | -3, 0, -2 | Classic |
| 2 | push(1), getMin | 1 | Single |
| 3 | push(2), push(2), pop, getMin | 2 | Duplicate min |

### 18. Next Greater Element I — LC 496
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | nums1=[4,1,2], nums2=[1,3,4,2] | [-1,3,-1] | Classic |
| 2 | nums1=[2,4], nums2=[1,2,3,4] | [3,-1] | Found |
| 3 | nums1=[1], nums2=[1] | [-1] | Single |

### 19. Next Greater Element II (Circular) — LC 503
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,1] | [2,-1,2] | Wraparound |
| 2 | [1,2,3,4] | [2,3,4,-1] | Increasing |
| 3 | [5,4,3] | [-1,-1,-1] | Decreasing |

### 20. Daily Temperatures — LC 739
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [73,74,75,71,69,72,76,73] | [1,1,4,2,1,1,0,0] | Classic |
| 2 | [30] | [0] | Single |
| 3 | [90,80,70] | [0,0,0] | Decreasing |

### 21. Online Stock Span — LC 901
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | next(100), next(80), next(60), next(70), next(75), next(85) | 1,1,1,2,3,5 | Classic |
| 2 | next(10) | 1 | First |
| 3 | next(5), next(5) | 1, 2 | Equal prices |

### 22. Evaluate Reverse Polish Notation — LC 150
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | ["2","1","+","3","*"] | 9 | (2+1)×3 |
| 2 | ["4","13","5","/","+"] | 6 | Truncation toward 0 |
| 3 | ["10","6","9","3","+","-11","*","/","*","17","+","5","+"] | 22 | Classic hard |

### 23. Minimum Parentheses to Remove — LC 1249
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "lee(t(c)o)de)" | "lee(t(c)o)de" | Trailing ) |
| 2 | "a)b(c)d" | "ab(c)d" | Middle ) |
| 3 | "))((" | "" | All invalid |

### 24. Car Fleet — LC 853
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | target=12, pos=[10,8,0,5,3], speed=[2,4,1,1,3] | 3 | Classic |
| 2 | target=10, pos=[3], speed=[3] | 1 | Single car |
| 3 | target=100, pos=[0,2,4], speed=[4,2,1] | 1 | All catch up |

### 25. Longest Valid Parentheses — LC 32
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "(()" | 2 | Trailing open |
| 2 | ")()())" | 4 | Classic |
| 3 | "" | 0 | Empty |

### 26. Largest Rectangle in Histogram — LC 84
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,1,5,6,2,3] | 10 | Classic |
| 2 | [2,4] | 4 | Two bars |
| 3 | [6,2,5,4,5,1,6] | 12 | Mixed |

### 27. How Many Numbers Are Smaller Than the Current Number — LC 1365
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [8,1,2,2,3] | [4,0,1,1,3] | Basic |
| 2 | [6,5,4,8] | [2,1,0,3] | Mixed |
| 3 | [7,7,7,7] | [0,0,0,0] | All same |

### 28. Car Fleet II — LC 1776
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | target=10, cars=[[1,2],[5,1]] | [4.00000,-1.00000] | Rear catches front |
| 2 | target=10, cars=[[3,1],[6,1]] | [-1.00000,-1.00000] | Same speed, no catch |
| 3 | target=12, cars=[[1,2],[2,1],[4,3],[7,2]] | [1.00000,-1.00000,-1.00000,-1.00000] | Mixed |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Longest Consecutive Sequence | N=500,000: shuffled 1..500,000 | **500,000** | < 500 ms | Sorting O(N log N) ok; nested-loop O(N²) hangs |
| 2 | Longest Consecutive Sequence | N=500,000 all same value (e.g., 7) | **1** | < 200 ms | Re-counting from every element |
| 3 | Daily Temperatures | N=200,000 strictly decreasing | All **0** | < 200 ms | O(N²) forward scan |
| 4 | Daily Temperatures | N=200,000 strictly increasing | All **1** except last = 0 | < 200 ms | O(N²) forward scan |
| 5 | Next Greater Element II | N=200,000 strictly decreasing | All **-1** | < 300 ms | O(N²) double scan |
| 6 | Largest Rectangle in Histogram | N=200,000 all height 5 | 5 × 200,000 = **1,000,000** | < 300 ms | O(N²) area scan |
| 7 | Largest Rectangle in Histogram | N=200,000: sawtooth 1,2,1,2,... | Compute via brute force on small pattern (see note) | < 300 ms | Stack bugs on equal heights |
| 8 | Group Anagrams | 100,000 copies of "abcdefg" shuffled | 1 group of 100,000 | < 1 s | O(N² × L) pairwise compare |
| 9 | Valid Sudoku | Generate 9×9 valid board, then 10,000 random queries of "is this board valid" | true every time | < 2 s total | Rebuilding sets per query |
| 10 | Evaluate RPN | 500,000 tokens: ["1","2","+"] repeated then one final "+" chain (see note) | Sum of all 1s and 2s | < 500 ms | String parsing overhead / recursion depth |

### 📝 Notes
- **Case 7 (sawtooth):** For pattern [1,2,1,2,...] of length N: best rectangle is 2 × N (height 2 across all even positions? No — height 2 only at even indices). Brute-force a small case (N=6: [1,2,1,2,1,2] → areas: h=1 spans all → 6; h=2 spans indices 1,3,5 → 6) → answer **N** for both heights; verify with N=6 brute force = 6.
- **Case 10 (RPN):** Build tokens as: repeat 250,000× ["1","2","+"] (each pushes 3), then flatten with "+" operators between all stack values. Simpler: 500,000× "1" followed by 499,999× "+" → sum = 500,000. Expected: **500,000**.
- **Case 1:** Shuffle with `IntArray(500_000){it+1}.also{it.shuffle(Random(42))}`.
