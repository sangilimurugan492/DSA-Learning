# 🧪 Week 06 Practice — Greedy + Binary Search + Bit Manipulation (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 6 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Best Time to Buy & Sell Stock I — LC 121
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [7,1,5,3,6,4] | 5 | Classic |
| 2 | [7,6,4,3,1] | 0 | Decreasing |
| 3 | [2,4,1] | 2 | Buy later |

### 2. Assign Cookies — LC 455
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | g=[1,2,3], s=[1,1] | 1 | Classic |
| 2 | g=[1,2], s=[1,2,3] | 2 | Enough cookies |
| 3 | g=[], s=[1] | 0 | No children |

### 3. Lemonade Change — LC 860
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [5,5,5,10,20] | true | Classic |
| 2 | [5,5,10,10,20] | false | Can't give $15 |
| 3 | [10,10] | false | No $5s first |

### 4. Single Number — LC 136
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,2,1] | 1 | Classic |
| 2 | [4,1,2,1,2] | 4 | Middle |
| 3 | [1] | 1 | Single |

### 5. Number of 1 Bits — LC 191
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | 11 (0b1011) | 3 | Classic |
| 2 | 128 (0b10000000) | 1 | Single bit |
| 3 | 0 | 0 | Zero |

### 6. Missing Number — LC 268
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,0,1] | 2 | Classic |
| 2 | [0,1] | 2 | At end |
| 3 | [1] | 0 | At start |

### 7. Power of Two — LC 231
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | 1 | true | 2^0 |
| 2 | 16 | true | 2^4 |
| 3 | 0 / -16 / 218 | false, false, false | Edge cases |

### 8. Best Time to Buy & Sell Stock II — LC 122
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [7,1,5,3,6,4] | 7 | Classic |
| 2 | [1,2,3,4,5] | 4 | Increasing |
| 3 | [7,6,4,3,1] | 0 | Decreasing |

### 9. Jump Game — LC 55
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,3,1,1,4] | true | Classic |
| 2 | [3,2,1,0,4] | false | Stuck at 0 |
| 3 | [0] | true | Single index |

### 10. Jump Game II — LC 45
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,3,1,1,4] | 2 | Classic |
| 2 | [2,3,0,1,4] | 2 | Alternative path |
| 3 | [1] | 0 | Already there |

### 11. Gas Station — LC 134
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | gas=[1,2,3,4,5], cost=[3,4,5,1,2] | 3 | Classic |
| 2 | gas=[2,3,4], cost=[3,4,3] | -1 | Impossible |
| 3 | gas=[5], cost=[4] | 0 | Single |

### 12. Merge Intervals — LC 56
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,3],[2,6],[8,10],[15,18]] | [[1,6],[8,10],[15,18]] | Classic |
| 2 | [[1,4],[4,5]] | [[1,5]] | Touching |
| 3 | [[1,4],[2,3]] | [[1,4]] | Contained |

### 13. Insert Interval — LC 57
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,3],[6,9]], new=[2,5] | [[1,5],[6,9]] | Overlap |
| 2 | [[1,2],[3,5],[6,7],[8,10],[12,16]], new=[4,8] | [[1,2],[3,10],[12,16]] | Multi-merge |
| 3 | [], new=[5,7] | [[5,7]] | Empty |

### 14. Meeting Rooms (can attend all?) — LC 252
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[0,30],[5,10],[15,20]] | false | Overlap |
| 2 | [[7,10],[2,4]] | true | No overlap |
| 3 | [] | true | Empty |

### 15. Non-Overlapping Intervals — LC 435
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2],[2,3],[3,4],[1,3]] | 1 | Classic |
| 2 | [[1,2],[1,2],[1,2]] | 2 | All same |
| 3 | [[1,2],[2,3]] | 0 | Touching ok |

### 16. Min Arrows to Burst Balloons — LC 452
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[10,16],[2,8],[1,6],[7,12]] | 2 | Classic |
| 2 | [[1,2],[3,4],[5,6],[7,8]] | 4 | Disjoint |
| 3 | [[1,2],[2,3],[3,4],[4,5]] | 2 | Touching |

### 17. Task Scheduler — LC 621
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [A,A,A,B,B,B], n=2 | 8 | Classic |
| 2 | [A,A,A,B,B,B], n=0 | 6 | No cooldown |
| 3 | [A,A,A,A,A,A,B,C,D,E,F,G], n=2 | 16 | Idle fill |

### 18. Partition Labels — LC 763
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "ababcbacadefegdehijhklij" | [9,7,8] | Classic |
| 2 | "eccbbbbdec" | [10] | One partition |
| 3 | "a" | [1] | Single |

### 19. Search in Rotated Sorted Array — LC 33
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,5,6,7,0,1,2], target=0 | 4 | Classic |
| 2 | [4,5,6,7,0,1,2], target=3 | -1 | Not found |
| 3 | [1], target=0 | -1 | Single |

### 20. Find Min in Rotated Sorted Array — LC 153
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,4,5,1,2] | 1 | Classic |
| 2 | [4,5,6,7,0,1,2] | 0 | Rotated |
| 3 | [11,13,15,17] | 11 | Not rotated |

### 21. Single Number II — LC 137
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,2,3,2] | 3 | Classic |
| 2 | [0,1,0,1,0,1,99] | 99 | Classic |
| 3 | [7,7,7,8] | 8 | Single unique |

### 22. Sum of Two Integers — LC 371
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | a=1, b=2 | 3 | Basic |
| 2 | a=2, b=3 | 5 | Carry |
| 3 | a=-1, b=1 | 0 | Negative + wrap |

### 23. Counting Bits — LC 338
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=2 | [0,1,1] | Basic |
| 2 | n=5 | [0,1,1,2,1,2] | Classic |
| 3 | n=0 | [0] | Zero |

### 24. Queue Reconstruction by Height — LC 406
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[7,0],[4,4],[7,1],[5,0],[6,1],[5,2]] | [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]] | Classic |
| 2 | [[6,0]] | [[6,0]] | Single |
| 3 | [] | [] | Empty |

### 25. Wiggle Subsequence — LC 376
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,7,4,9,2,5] | 6 | Full wiggle |
| 2 | [1,17,5,10,13,15,10,5,16,8] | 7 | Classic |
| 3 | [1,2,3,4,5,6,7,8,9] | 2 | Monotonic |

### 26. Candy — LC 135
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,0,2] | 5 | Classic |
| 2 | [1,2,2] | 4 | Plateau |
| 3 | [1,3,2,2,1] | 7 | Valley |

### 27. Median of Two Sorted Arrays — LC 4
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,3], [2] | 2.0 | Odd total |
| 2 | [1,2], [3,4] | 2.5 | Even total |
| 3 | [], [1] | 1.0 | One empty |

### 28. Majority Element I & II — LC 169/229
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,2,3] | I: 3; II: [3] | Classic |
| 2 | [1] | I: 1; II: [1] | Single |
| 3 | [1,1,1,3,3,2,2,2] | I: 1; II: [1,2] | Two majorities |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Jump Game | N=100,000: `IntArray(100_000) { 1 }` (all 1s) | true (reach last in 99,999 jumps) | < 100 ms | O(N²) BFS/DP |
| 2 | Jump Game II | N=100,000: all 1s | **99,999** | < 100 ms | O(N²) DP |
| 3 | Gas Station | N=100,000: gas all 2, cost all 1 | **0** | < 100 ms | O(N²) restart from every station |
| 4 | Merge Intervals | N=500,000: intervals [i, i+1] for i in 0..499,999 (all overlap) | **1 interval [0, 500,000]** | < 500 ms | Nested overlap scan O(N²) |
| 5 | Search in Rotated Sorted Array | N=1,000,000: rotated at 500,000; search for 500 random targets | Correct index each | < 500 ms total | Linear scan O(N) per query |
| 6 | Find Min in Rotated Sorted Array | N=1,000,000 rotated at 999,999 | **1,000,000** (the min) | < 100 ms | Linear scan |
| 7 | Median of Two Sorted Arrays | Two arrays of 500,000 each (interleaved values) | Correct median | < 200 ms | Merge O(N+M) ok; but O(N×M) or sort hangs |
| 8 | Task Scheduler | N=100,000 tasks: 'A'×50,000 + 26 letters × ~1,900 each, n=100 | Formula: max(total, (maxFreq-1)×(n+1)+countMax) | < 200 ms | Simulation with idle slots |
| 9 | Candy | N=100,000: strictly increasing 1..100,000 | Sum 1..100,000 = **5,000,050,000** (use Long) | < 200 ms | O(N²) neighbor scan |
| 10 | Counting Bits | n=10,000,000 | Verify spot values (e.g., bits[1023]=10, bits[1024]=1) | < 300 ms | Per-number bit counting O(N log N) ok; O(N×32) fine — checks no O(N²) |

### 📝 Notes
- **Case 4:** All intervals [i, i+1] overlap in a chain → single merged interval [0, 500,000].
- **Case 5:** Build `IntArray(1_000_000) { (it + 500_000) % 1_000_000 + 1 }` (rotated 1..1,000,000); binary search must be O(log N) per query.
- **Case 7:** Build nums1 = evens 0..999,998, nums2 = odds 1..999,999 → merged = 0..999,999, median = 499,999.5.
- **Case 8:** maxFreq = 50,000 ('A'), countMax = 1 → (50,000−1)×101+1 = 5,049,900 vs total 100,000 → answer **5,049,900**.
- **Case 9:** Strictly increasing ratings → each child gets i+1 candies → sum = N(N+1)/2 = 5,000,050,000 — **use Long, Int overflows!**
