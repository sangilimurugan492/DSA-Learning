# 🧪 Week 01 Practice — Two Pointer + In-Place (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 1 problem — solve first, then verify.
> **Part B:** 10 complex test cases with large generated inputs to validate your solution's time complexity.
> **Harness:** Use [TestRunner.kt](../testrunner/TestRunner.kt) to run Part B cases with timing.

---

## Part A — Sample Test Cases (Correctness)

### 1. Two Sum (Sorted) — LC 167
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | numbers=[2,7,11,15], target=9 | [1,2] | Basic |
| 2 | numbers=[2,3,4], target=6 | [1,3] | Even length |
| 3 | numbers=[-1,0], target=-1 | [1,2] | Negatives |

### 2. Valid Palindrome — LC 125
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "A man, a plan, a canal: Panama" | true | Mixed case + punctuation |
| 2 | "race a car" | false | Near miss |
| 3 | " " | true | Only spaces |

### 3. Is Subsequence — LC 392
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | s="abc", t="ahbgdc" | true | Basic |
| 2 | s="axc", t="ahbgdc" | false | Out of order |
| 3 | s="", t="ahbgdc" | true | Empty s |

### 4. Move Zeroes to End — LC 283
| # | Input | Expected (array after) | Tests |
|---|-------|----------|-------|
| 1 | [0,1,0,3,12] | [1,3,12,0,0] | Basic |
| 2 | [0] | [0] | Single zero |
| 3 | [0,0,1] | [1,0,0] | Leading zeros |

### 5. Merge Sorted Array — LC 88
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,0,0,0] m=3, [2,5,6] n=3 | [1,2,2,3,5,6] | Basic |
| 2 | [1] m=1, [] n=0 | [1] | Empty nums2 |
| 3 | [0] m=0, [1] n=1 | [1] | Empty nums1 |

### 6. Remove Element — LC 27
| # | Input | Expected (k, first k elems) | Tests |
|---|-------|----------|-------|
| 1 | [3,2,2,3], val=3 | 2, [2,2] | Basic |
| 2 | [0,1,2,2,3,0,4,2], val=2 | 5, [0,1,3,0,4] | Scattered |
| 3 | [], val=0 | 0, [] | Empty |

### 7. Intersection of Two Arrays II — LC 350
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,2,1], [2,2] | [2,2] | Duplicates count |
| 2 | [4,9,5], [9,4,9,8,4] | [4,9] (any order) | Partial overlap |
| 3 | [], [1] | [] | Empty |

### 8. Three Sum — LC 15
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [-1,0,1,2,-1,-4] | [[-1,-1,2],[-1,0,1]] | Basic |
| 2 | [0,0,0,0] | [[0,0,0]] | All same |
| 3 | [1,2,3] | [] | No triplet |

### 9. Three Sum Closest — LC 16
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [-1,2,1,-4], target=1 | 2 | Basic |
| 2 | [0,0,0], target=1 | 0 | Exact far target |
| 3 | [1,1,1,0], target=-100 | 2 | Extreme target |

### 10. Four Sum — LC 18
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,0,-1,0,-2,2], target=0 | [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]] | Basic |
| 2 | [2,2,2,2,2], target=8 | [[2,2,2,2]] | All same |
| 3 | [], target=0 | [] | Empty |

### 11. Container With Most Water — LC 11
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,8,6,2,5,4,8,3,7] | 49 | Basic |
| 2 | [1,1] | 1 | Two bars |
| 3 | [4,3,2,1,4] | 16 | Symmetric |

### 12. Sort Colors (Dutch Flag) — LC 75
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,0,2,1,1,0] | [0,0,1,1,2,2] | Basic |
| 2 | [2] | [2] | Single |
| 3 | [1,0] | [0,1] | Two elems |

### 13. Backspace String Compare — LC 844
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "ab#c", "ad#c" | true | Both reduce to "ac" |
| 2 | "ab##", "c#d#" | true | Both empty |
| 3 | "a#c", "b" | false | Different results |

### 14. Two Sum Less Than K — LC 1099
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [34,23,1,24,75,33,54,8], k=60 | 58 | Basic |
| 2 | [10,20,30], k=15 | -1 | No pair under k |
| 3 | [1,2], k=3 | -1 | Sum equals k (not <) |

### 15. K-Sum Pairs — LC 1679
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4], k=5 | 2 | Basic |
| 2 | [3,1,3,4,3], k=6 | 1 | Leftover element |
| 3 | [1,1,1,1], k=2 | 2 | All same |

### 16. Boats to Save People — LC 881
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2], limit=3 | 1 | Pair fits |
| 2 | [3,2,2,1], limit=3 | 3 | Mixed |
| 3 | [1,2,3,4], limit=4 | 3 | Pairs + singles |

### 17. Trapping Rain Water — LC 42
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [0,1,0,2,1,0,1,3,2,1,2,1] | 6 | Classic |
| 2 | [4,2,0,3,2,5] | 9 | Deep valley |
| 3 | [1,2,3,4] | 0 | Increasing (no water) |

### 18. Remove Duplicates from Sorted Array — LC 26
| # | Input | Expected (k) | Tests |
|---|-------|----------|-------|
| 1 | [1,1,2] | 2 | Basic |
| 2 | [0,0,1,1,1,2,2,3,3,4] | 5 | Many dups |
| 3 | [1] | 1 | Single |

### 19. Remove Duplicates II (Keep Two) — LC 80
| # | Input | Expected (k) | Tests |
|---|-------|----------|-------|
| 1 | [1,1,1,2,2,3] | 5 | Triple run |
| 2 | [0,0,1,1,1,1,2,3,3] | 7 | Mixed runs |
| 3 | [1] | 1 | Single |

### 20. Replace with Greatest on Right — LC 1299
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [17,18,5,4,6,1] | [18,6,6,6,1,-1] | Basic |
| 2 | [400] | [-1] | Single |
| 3 | [2,1] | [1,-1] | Two elems |

### 21. Sorted Squares — LC 977
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [-4,-1,0,3,10] | [0,1,9,16,100] | Negatives |
| 2 | [-5,-3,2,3] | [4,9,9,25] | Mixed |
| 3 | [0] | [0] | Single |

### 22. Rotate Array — LC 189
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5,6,7], k=3 | [5,6,7,1,2,3,4] | Basic |
| 2 | [1], k=0 | [1] | No rotation |
| 3 | [-1], k=2 | [-1] | k > n |

### 23. Next Permutation — LC 31
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] | [1,3,2] | Basic |
| 2 | [3,2,1] | [1,2,3] | Last permutation wraps |
| 3 | [1,1,5] | [1,5,1] | Duplicates |

### 24. Sort Colors (re-solve) — LC 75
Same cases as #12.

### 25. Remove Element (re-solve) — LC 27
Same cases as #6.

---

## Part B — 10 Complex Test Cases (Complexity Validation)

> Run these with [TestRunner.kt](../testrunner/TestRunner.kt). Each case has a **time budget** — if your solution exceeds it, your complexity is likely too high.

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Trapping Rain Water | N=100,000: `[3,0,3]` pattern repeated → `IntArray(100_000) { if (it % 3 == 1) 0 else 3 }` | 100,000 × 1 = **100,000** | < 100 ms | O(N²) hangs |
| 2 | Trapping Rain Water | N=200,000 strictly increasing `IntArray(200_000) { it }` | **0** | < 100 ms | O(N²) hangs |
| 3 | Three Sum | N=3,000 all zeros | **[[0,0,0]]** (1 triplet) | < 500 ms | O(N³) hangs |
| 4 | Three Sum | N=2,000: `IntArray(2000) { it - 1000 }` (consecutive -999..999) | 0 triplets (no 3 distinct sum to 0 with these? verify: -999+0+999=0 → many triplets; count = pairs (a,-a,0) → 999 triplets) | < 1 s | O(N³) hangs |
| 5 | Container With Most Water | N=100,000: `IntArray(100_000) { 100_000 - it }` (decreasing) | (N-1) × 1 = **99,999** | < 100 ms | O(N²) hangs |
| 6 | Sort Colors | N=1,000,000 random 0/1/2 (`Random(42).nextInt(3)`) | Sorted array (verify with `isSorted`) | < 300 ms | O(N²) hangs |
| 7 | Merge Sorted Array | m=n=500,000: nums1 = 1..500000 (padded), nums2 = 500001..1000000 | 1..1,000,000 sorted | < 300 ms | Wrong direction merge |
| 8 | Sorted Squares | N=500,000: `IntArray(500_000) { it - 250_000 }` | Squares sorted ascending | < 300 ms | O(N log N) sort vs O(N) two-pointer |
| 9 | Rotate Array | N=1,000,000, k=999,999 | Array shifted right by 1 | < 200 ms | Extra-space O(N·k) or O(k·N) |
| 10 | Next Permutation | N=500,000: `IntArray(500_000) { 500_000 - it }` (last permutation) | Reversed → 1..500000 ascending | < 200 ms | O(N²) scan |

### ⏱️ Complexity Rules of Thumb
- **O(N) / O(N log N)** on 10⁵–10⁶ elements → finishes in well under 1 second
- **O(N²)** on 10⁵ elements → ~10¹⁰ operations → **10+ seconds (too slow!)**
- **O(N³)** on 3,000 elements → ~2.7×10¹⁰ → **hangs**

### How to Verify Case 4 (Three Sum consecutive)
Brute-force check with a small slice: for `[-2,-1,0,1,2]` the triplets are [-2,0,2], [-1,0,1] → 2 triplets. Generalize: for -(k)..k, triplets (a, 0, -a) for a=1..k → **k triplets**. For N=2000 (k=999): **999 triplets**.
