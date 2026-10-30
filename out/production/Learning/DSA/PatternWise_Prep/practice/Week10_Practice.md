# 🧪 Week 10 Practice — Advanced DP (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 10 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Edit Distance — LC 72
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "horse" → "ros" | 3 | Basic |
| 2 | "intention" → "execution" | 5 | Classic |
| 3 | "" → "abc" | 3 | Insert only |

### 2. Longest Common Subsequence — LC 1143
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "abcde", "ace" | 3 | Basic |
| 2 | "abc", "abc" | 3 | Identical |
| 3 | "abc", "def" | 0 | No common |

### 3. Longest Palindromic Subsequence — LC 516
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "bbbab" | 4 | Basic |
| 2 | "cbbd" | 2 | Pair |
| 3 | "a" | 1 | Single |

### 4. Distinct Subsequences — LC 115
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | s="rabbbit", t="rabbit" | 3 | Classic |
| 2 | s="babgbag", t="bag" | 5 | Classic |
| 3 | s="abc", t="abcd" | 0 | Impossible |

### 5. Interleaving String — LC 97
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | s1="aabcc", s2="dbbca", s3="aadbbcbcac" | true | Classic |
| 2 | s1="aabcc", s2="dbbca", s3="aadbbbaccc" | false | Fails |
| 3 | s1="", s2="", s3="" | true | Empty |

### 6. Minimum Path Sum — LC 64
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,3,1],[1,5,1],[4,2,1]] | 7 | Basic |
| 2 | [[1,2,3],[4,5,6]] | 12 | Two rows |
| 3 | [[1]] | 1 | Single cell |

### 7. Unique Paths II — LC 63
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[0,0,0],[0,1,0],[0,0,0]] | 2 | One obstacle |
| 2 | [[0,1],[0,0]] | 1 | Basic |
| 3 | [[1]] | 0 | Start blocked |

### 8. Maximal Square — LC 221
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | ["10100","10110","11110"] wait matrix | 4 | Basic 2×2 |
| 2 | ["0","0"] | 0 | No 1s |
| 3 | ["1"] | 1 | Single |

### 9. Maximal Rectangle — LC 85
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | ["10100","10111","11111","10010"] | 6 | Basic |
| 2 | ["0"] | 0 | Empty |
| 3 | ["1"] | 1 | Single |

### 10. Cherry Pickup II — LC 1463
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[3,1,1],[2,5,1],[1,5,3],[2,1,1]] | 24 | Classic |
| 2 | [[1,0,0,0,0,0,1],[2,0,0,0,0,3,0],[2,0,9,0,0,0,1],[2,0,0,0,0,0,1],[1,0,0,0,0,0,2]] | 15 | Classic |
| 3 | [[1,1],[1,1]] | 4 | Small |

### 11. Burst Balloons — LC 312
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,1,5,8] | 167 | Classic |
| 2 | [1,5] | 10 | Basic |
| 3 | [1] | 1 | Single |

### 12. Palindromic Substrings — LC 647
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "abc" | 3 | Singles |
| 2 | "aaa" | 6 | All pairs |
| 3 | "abba" | 4 | Even palindrome |

### 13. Decode Ways — LC 91
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "12" | 2 | AB or L |
| 2 | "226" | 3 | Classic |
| 3 | "06" | 0 | Leading zero |

### 14. Word Break — LC 139
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "leetcode", ["leet","code"] | true | Basic |
| 2 | "applepenapple", ["apple","pen"] | true | Reuse words |
| 3 | "catsandog", ["cats","dog","sand","and","cat"] | false | Fails |

### 15. Perfect Squares — LC 279
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | 12 | 3 | 4+4+4 |
| 2 | 13 | 2 | 4+9 |
| 3 | 1 | 1 | Single |

### 16. Coin Change II — LC 518
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | amount=5, coins=[1,2,5] | 4 | Classic |
| 2 | amount=3, coins=[2] | 0 | Impossible |
| 3 | amount=10, coins=[10] | 1 | Single |

### 17. Longest Increasing Path — LC 329
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[9,9,4],[6,6,8],[2,1,1]] | 4 | 1→2→6→9 |
| 2 | [[3,4,5],[3,2,6],[2,2,1]] | 4 | Classic |
| 3 | [[1]] | 1 | Single |

### 18. Dungeon Game — LC 174
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[-2,-3,3],[-5,-10,1],[10,30,-5]] | 7 | Classic |
| 2 | [[0]] | 1 | Single |
| 3 | [[100]] | 1 | Healing |

### 19. Largest Divisible Subset — LC 368
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] | [1,2] (or size 2) | Basic |
| 2 | [1,2,4,8] | [1,2,4,8] | Chain |
| 3 | [3] | [3] | Single |

### 20. Number of Longest Increasing Subsequence — LC 673
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,3,5,4,7] | 2 | Two LIS |
| 2 | [2,2,2,2,2] | 5 | All length 1 |
| 3 | [1,2,3] | 1 | One LIS |


---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Case | Size | Validates |
|---|------|------|-----------|
| 1 | Edit Distance: two random strings | 500×500 | O(mn) DP fills fast |
| 2 | LCS: random alphabet strings | 1,000×1,000 | O(mn) with rolling row |
| 3 | Distinct Subsequences: worst case s="aaaa...", t="aaa" | n=5,000 | Long overflow guard |
| 4 | Minimum Path Sum: all-ones grid | 500×500 | O(mn), no recursion |
| 5 | Maximal Square: all-1s matrix | 300×300 | O(mn) DP |
| 6 | Maximal Rectangle: random 0/1 matrix | 200×200 | Histogram per row ≤ O(mn) |
| 7 | Burst Balloons: all balloons equal | n=300 | O(n³) interval DP OK at 300 |
| 8 | Coin Change II: coins [1..100], amount | 10,000 | O(n·amount) |
| 9 | Longest Increasing Path: snake staircase grid | 200×200 | Memoized DFS O(mn) |
| 10 | Num of LIS: strictly increasing run | n=5,000 | O(n²) DP OK |

### 📝 Notes
- Recursive memo solutions: raise JVM stack or convert to iterative if tests #9 fail.
- Any SLOW ⚠️ on #3 → you returned Int where Long is needed (overflow, not slowness).
