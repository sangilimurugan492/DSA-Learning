# 🧪 Week 02 Practice — Sliding Window + Prefix Sum + Kadane's (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 2 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Max Average Subarray (Fixed K) — LC 643
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,12,-5,-6,50,3], k=4 | 12.75 | Basic |
| 2 | [5], k=1 | 5.0 | Single |
| 3 | [0,4,0,3,2], k=1 | 4.0 | k=1 |

### 2. Running Sum of 1D Array — LC 1480
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4] | [1,3,6,10] | Basic |
| 2 | [1,1,1,1,1] | [1,2,3,4,5] | Same values |
| 3 | [3,1,2,10,1] | [3,4,6,16,17] | Mixed |

### 3. Prefix Sum Query
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5], query(1,3) | 9 | Range middle |
| 2 | [1,2,3,4,5], query(0,4) | 15 | Full range |
| 3 | [1,2,3,4,5], query(2,2) | 3 | Single element |

### 4. Maximum Subarray (Kadane's) — LC 53
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [-2,1,-3,4,-1,2,1,-5,4] | 6 | Classic |
| 2 | [1] | 1 | Single |
| 3 | [-3,-1,-2] | -1 | All negative |

### 5. Fruits into Baskets — LC 904
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,1] | 3 | Basic |
| 2 | [0,1,2,2] | 3 | Two types |
| 3 | [3,3,3,1,2,1,1,2,3,3,4] | 5 | Classic |

### 6. Longest Subarray After Deleting One — LC 1493
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,0,1] | 3 | Basic |
| 2 | [0,1,1,1,0,1,1,0,1] | 5 | Two zeros |
| 3 | [1,1,1] | 2 | No zero (must delete) |

### 7. Grumpy Bookstore Owner — LC 1052
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | customers=[1,0,1,2,1,1,7,5], grumpy=[0,1,0,1,0,1,0,1], minutes=3 | 16 | Classic |
| 2 | customers=[1], grumpy=[0], minutes=1 | 1 | Single |
| 3 | customers=[4,2], grumpy=[1,1], minutes=2 | 6 | All grumpy |

### 8. Maximum Product Subarray — LC 152
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,3,-2,4] | 6 | Basic |
| 2 | [-2,0,-1] | 0 | Zero resets |
| 3 | [-2,3,-4] | 24 | Double negative flip |

### 9. Maximum Sum Circular Subarray — LC 918
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,-2,3,-2] | 3 | Non-circular wins |
| 2 | [5,-3,5] | 10 | Circular wins |
| 3 | [-3,-2,-3] | -2 | All negative |

### 10. Product of Array Except Self — LC 238
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4] | [24,12,8,6] | Basic |
| 2 | [-1,1,0,-3,3] | [0,0,9,0,0] | Zero + negatives |
| 3 | [2,3] | [3,2] | Two elems |

### 11. Subarray Sum Equals K — LC 560
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,1], k=2 | 2 | Basic |
| 2 | [1,2,3], k=3 | 2 | Overlapping |
| 3 | [0,0,0], k=0 | 6 | All zeros (n(n+1)/2) |

### 12. Contiguous Array (Equal 0s & 1s) — LC 525
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [0,1] | 2 | Basic |
| 2 | [0,1,0] | 2 | Odd length |
| 3 | [0,1,1,0,1,1,1,0] | 4 | Middle window |

### 13. Longest Subarray with Sum K
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,1,1,1], k=5 | 4 | Tail run of 1s |
| 2 | [-1,2,3,-6,2,2], k=2 | 2 | Negatives |
| 3 | [1], k=0 | 0 | No match |

### 14. Longest Substring Without Repeating — LC 3
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "abcabcbb" | 3 | Basic |
| 2 | "bbbbb" | 1 | All same |
| 3 | "pwwkew" | 3 | Middle window |

### 15. Longest Repeating Character Replacement — LC 424
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "ABAB", k=2 | 4 | Replace all B |
| 2 | "AABABBA", k=1 | 4 | Classic |
| 3 | "AAAA", k=0 | 4 | No change needed |

### 16. Min Size Subarray Sum — LC 209
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | target=7, [2,3,1,2,4,3] | 2 | Basic |
| 2 | target=4, [1,4,4] | 1 | Exact single |
| 3 | target=11, [1,1,1,1,1,1,1,1] | 0 | Impossible |

### 17. Find All Anagrams in String — LC 438
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | s="cbaebabacd", p="abc" | [0,6] | Classic |
| 2 | s="abab", p="ab" | [0,1,2] | Overlapping |
| 3 | s="a", p="ab" | [] | p longer |

### 18. Minimum Window Substring — LC 76
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | s="ADOBECODEBANC", t="ABC" | "BANC" | Classic |
| 2 | s="a", t="a" | "a" | Exact |
| 3 | s="a", t="aa" | "" | Not enough chars |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Maximum Subarray | N=1,000,000: `IntArray(1_000_000) { (it % 7) - 3 }` | Cross-check vs brute force on one period (see note) | < 200 ms | O(N²) hangs |
| 2 | Subarray Sum Equals K | N=100,000 all 1s, k=100 | Subarrays of length 100 = 100,000 − 100 + 1 = **99,901** | < 200 ms | O(N²) hangs |
| 3 | Longest Substring Without Repeating | 500,000-char string cycling "abc...z" | **26** | < 300 ms | O(N²) re-scan |
| 4 | Product of Array Except Self | N=60 all 2s | All = 2^59 (fits Long; Int overflows) | < 50 ms | Division trick fails on zeros |
| 5 | Min Size Subarray Sum | N=500,000: `IntArray(500_000) { 1 }`, target=500,000 | **500,000** (whole array) | < 200 ms | O(N²) prefix recompute |
| 6 | Find All Anagrams | s = 500,000 chars of "ab" repeated, p = "ab" | 499,999 start indices | < 500 ms | Re-sorting window each step |
| 7 | Maximum Product Subarray | N=60: `IntArray(60) { if (it % 2 == 0) 2 else -1 }` | 2^30 (fits Long; verifies min-tracking) | < 50 ms | Forgetting min tracking |
| 8 | Maximum Sum Circular Subarray | N=100,000: alternating [5,-1] pattern | (N/2)×5 − 1 = **249,999** | < 200 ms | O(N²) wraparound scan |
| 9 | Contiguous Array | N=200,000 alternating 0,1 | **200,000** | < 200 ms | O(N²) subarray scan |
| 10 | Longest Repeating Char Replacement | 500,000 chars "abab...", k=250,000 | **500,000** | < 300 ms | Recomputing maxFreq by scanning window |

### 📝 Notes
- **Case 1 (verify expected):** For a periodic array, the max subarray repeats with period 7 — brute-force one window of ~14 elements to find the per-period best, or cross-check your O(N) solution against a trusted O(N log N) implementation on N=10,000.
- **Overflow warning:** Cases 4 & 7 use N=60 so results fit in Long — Int would overflow. Structure (not magnitude) is what's being tested.
- **Case 8 math:** [5,-1,5,-1,...] circular: best is take all 5s and one -1 → 50,000×5 − 1 = 249,999.
