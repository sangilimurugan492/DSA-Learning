# 🧪 Week 07 Practice — Backtracking + Dynamic Programming (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 7 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Climbing Stairs — LC 70
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=2 | 2 | Basic |
| 2 | n=3 | 3 | Classic |
| 3 | n=45 | 1836311903 | Large (recursion without memo TLEs) |

### 2. Min Cost Climbing Stairs — LC 746
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [10,15,20] | 15 | Classic |
| 2 | [1,100,1,1,1,100,1,1,100,1] | 6 | Classic |
| 3 | [0,0] | 0 | Free |

### 3. Subsets — LC 78
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] | 8 subsets | Basic |
| 2 | [0] | 2 subsets ([], [0]) | Single |
| 3 | [] | 1 (only []) | Empty |

### 4. Combination Sum — LC 39
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,3,6,7], target=7 | [[2,2,3],[7]] | Classic |
| 2 | [2,3,5], target=8 | [[2,2,2,2],[2,3,3],[3,5]] | Classic |
| 3 | [2], target=1 | [] | Impossible |

### 5. Permutations — LC 46
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] | 6 permutations | Basic |
| 2 | [0,1] | 2 permutations | Two |
| 3 | [1] | 1 permutation | Single |

### 6. Word Search — LC 79
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | board=[["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word="ABCCED" | true | Classic |
| 2 | same board, word="SEE" | true | Branching |
| 3 | same board, word="ABCB" | false | No reuse |

### 7. House Robber — LC 198
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,1] | 4 | Classic |
| 2 | [2,7,9,3,1] | 12 | Classic |
| 3 | [2,1,1,2] | 4 | Non-adjacent ends |

### 8. House Robber II (Circular) — LC 213
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,3,2] | 3 | Can't take both ends |
| 2 | [1,2,3,1] | 4 | Classic |
| 3 | [1,2,3] | 3 | Linear wins |

### 9. Coin Change (Min Coins) — LC 322
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,5], amount=11 | 3 | Classic |
| 2 | [2], amount=3 | -1 | Impossible |
| 3 | [1], amount=0 | 0 | Zero amount |

### 10. Coin Change II (Combinations) — LC 518
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,5], amount=5 | 4 | Classic |
| 2 | [2], amount=3 | 0 | Impossible |
| 3 | [10], amount=10 | 1 | Exact |

### 11. Decode Ways — LC 91
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "12" | 2 | AB or L |
| 2 | "226" | 3 | Classic |
| 3 | "06" | 0 | Leading zero invalid |

### 12. Word Break — LC 139
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "leetcode", ["leet","code"] | true | Classic |
| 2 | "applepenapple", ["apple","pen"] | true | Reuse words |
| 3 | "catsandog", ["cats","dog","sand","and","cat"] | false | Can't split |

### 13. Unique Paths — LC 62
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | m=3, n=7 | 28 | Classic |
| 2 | m=3, n=2 | 3 | Small |
| 3 | m=1, n=1 | 1 | Single cell |

### 14. Minimum Path Sum — LC 64
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,3,1],[1,5,1],[4,2,1]] | 7 | Classic |
| 2 | [[1,2,3]] | 6 | Single row |
| 3 | [[5]] | 5 | Single cell |

### 15. Longest Common Subsequence — LC 1143
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "abcde", "ace" | 3 | Classic |
| 2 | "abc", "abc" | 3 | Identical |
| 3 | "abc", "def" | 0 | Disjoint |

### 16. Edit Distance — LC 72
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "horse", "ros" | 3 | Classic |
| 2 | "intention", "execution" | 5 | Classic |
| 3 | "", "abc" | 3 | All inserts |

### 17. Longest Palindromic Subsequence — LC 516
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "bbbab" | 4 | Classic |
| 2 | "cbbd" | 2 | Two b's |
| 3 | "a" | 1 | Single |

### 18. Longest Increasing Subsequence — LC 300
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [10,9,2,5,3,7,101,18] | 4 | Classic |
| 2 | [0,1,0,3,2,3] | 4 | Restart |
| 3 | [7,7,7,7] | 1 | Strictly increasing |

### 19. Partition Equal Subset Sum — LC 416
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,5,11,5] | true | [1,5,5] & [11] |
| 2 | [1,2,3,5] | false | Odd total |
| 3 | [1] | false | Single |

### 20. Target Sum — LC 494
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,1,1,1], target=3 | 5 | Classic |
| 2 | [1], target=1 | 1 | Single |
| 3 | [0,0,1], target=1 | 4 | Zeros double count |

### 21. Palindrome Partitioning — LC 131
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "aab" | [[a,a,b],[aa,b]] | Classic |
| 2 | "a" | [[a]] | Single |
| 3 | "aba" | [[a,b,a],[aba]] | Two cuts |

### 22. N-Queens — LC 51
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=4 | 2 solutions | Classic |
| 2 | n=1 | 1 solution | Single |
| 3 | n=2 | 0 solutions | Impossible |

### 23. Sudoku Solver — LC 37
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | Classic unsolvable-looking board | Solved board (rows/cols/boxes valid) | Classic |
| 2 | Board with one empty cell | Filled correctly | Minimal |
| 3 | Board with 30 empty cells | Solved | Medium |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Climbing Stairs | n=10,000 (result mod 1,000,000,007 if needed) | Fibonacci(10,000) — compute iteratively | < 100 ms | Naive recursion O(2^N) hangs |
| 2 | House Robber | N=1,000,000 random values 1..100 | DP result (cross-check vs N=10,000 brute) | < 200 ms | Recursion without memo |
| 3 | Coin Change | coins=[1,5,6,9,11], amount=10,000 | DP result (verify small case by hand: amount=11 → 2 coins [6,5]... verify) | < 200 ms | Greedy (wrong) / O(2^amount) recursion |
| 4 | Longest Common Subsequence | Two random strings of 5,000 chars each ('a'..'z') | LCS length (cross-check N=500 vs brute) | < 500 ms | O(2^N) recursion; also full O(N×M) table = 25M cells ok |
| 5 | Longest Increasing Subsequence | N=100,000: shuffled 1..100,000 | **100,000** (permutation → whole array) | < 300 ms | O(N²) DP hangs |
| 6 | Partition Equal Subset Sum | N=200: values chosen so total=40,000, subset exists (e.g., 100 pairs of [x, 100−x]... see note) | true | < 500 ms | O(2^N) subset enumeration |
| 7 | Word Break | s = "aaa...a" (1,000 a's), dict = ["a","aa","aaa","aaaa","aaaaa"] | true | < 200 ms | Re-exploring same prefixes O(2^N) |
| 8 | Unique Paths | m=1,000, n=1,000 (result mod 10⁹+7) | C(1998, 999) mod 10⁹+7 | < 500 ms | Recursion; also Int overflow |
| 9 | Target Sum | N=20: values all 1, target=10 | C(20,10) = **184,756** | < 200 ms | O(2^N) enumeration (2^20 ≈ 1M ok but 2^30 hangs) |
| 10 | N-Queens | n=8 | **92** solutions | < 500 ms | Missing pruning (checking all 8^8 placements) |

### 📝 Notes
- **Case 1:** Fibonacci(10,000) has ~2,090 digits — either use BigInteger or compute mod 10⁹+7. The point is iterative O(N) vs recursive O(2^N).
- **Case 3:** Verify small: coins=[1,5,6,9,11], amount=11 → 6+5 = 2 coins. Greedy (take 11) fails if 11 not in coins — test amount=22 with coins=[1,5,6,9,11] where greedy 11+11=2 but optimal is also 2; better test: coins=[1,3,4], amount=6 → greedy 4+1+1=3, optimal 3+3=**2**.
- **Case 6:** Build: 100 pairs where pair i = [i, 200−i]... simpler: 200 values where first 100 sum = last 100 sum (e.g., 100× [1] + 100× [1] → total 200, target 100 → true).
- **Case 8:** C(1998,999) mod 10⁹+7 — compute with Pascal's triangle mod or multiplicative formula. Int overflows around C(34,17).
- **Case 9:** Assign + to 15 ones and − to 5 ones → 15−5=10. Count = C(20,15) = C(20,5) = 15,504... **verify:** +count − (20−+count) = 10 → +count = 15 → C(20,15) = 15,504. (Corrected expected: **15,504**.)
