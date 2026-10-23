# 🧪 Week 11 Practice — Monotonic Stack + Design + Trie (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 11 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Daily Temperatures — LC 739
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [73,74,75,71,69,72,76,73] | [1,1,4,2,1,1,0,0] | Classic |
| 2 | [30,40,50,60] | [1,1,1,0] | Increasing |
| 3 | [30,60,90] | [1,1,0] | Basic |

### 2. Next Greater Element II — LC 503
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,1] | [2,-1,2] | Circular wrap |
| 2 | [1,2,3,4,3] | [2,3,4,-1,4] | Classic |
| 3 | [5,5,5] | [-1,-1,-1] | All equal |

### 3. Largest Rectangle in Histogram — LC 84
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,1,5,6,2,3] | 10 | Classic |
| 2 | [2,4] | 4 | Basic |
| 3 | [1,1,1,1] | 4 | Flat |

### 4. Sum of Subarray Minimums — LC 907
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,1,2,4] | 17 | Classic |
| 2 | [11,81,94,43,3] | 444 | Classic |
| 3 | [2] | 2 | Single |

### 5. Online Stock Span — LC 901
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [100,80,60,70,60,75,85] | [1,1,1,2,1,4,6] | Classic |
| 2 | [50,50,50] | [1,2,3] | Equal prices |
| 3 | [10] | [1] | Single |

### 6. Remove K Digits — LC 402
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "1432219", k=3 | "1219" | Classic |
| 2 | "10200", k=1 | "200" | Leading zeros |
| 3 | "9", k=1 | "0" | All removed |

### 7. 132 Pattern — LC 456
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4] | false | Increasing only |
| 2 | [3,1,4,2] | true | Classic |
| 3 | [-1,3,2,0] | true | Negative |

### 8. Sum of Subarray Ranges — LC 2104
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] | 4 | Classic |
| 2 | [1,3,3] | 4 | Classic |
| 3 | [1] | 0 | Single |

### 9. Max Chunks To Make Sorted — LC 769
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,3,2,1,0] | 1 | One chunk |
| 2 | [1,0,2,3,4] | 4 | Many chunks |
| 3 | [0] | 1 | Single |

### 10. Remove All Adjacent Duplicates II — LC 1209
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "abcd", k=2 | "abcd" | No removals |
| 2 | "deeedbbcccbdaa", k=3 | "aa" | Classic cascade |
| 3 | "pbbcggttciiippooaais", k=2 | "ps" | Classic |

### 11. LRU Cache — LC 146
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | cap=2; put(1,1), put(2,2), get(1), put(3,3), get(2), put(4,4), get(1), get(3), get(4) | -1, -1, 3, 4 | Classic |
| 2 | cap=1; put(2,1), get(2), put(3,2), get(2) | 1, -1 | Eviction |
| 3 | cap=2; put(1,1), put(2,2), put(1,10), get(1) | 10 | Update keeps fresh |

### 12. LFU Cache — LC 460
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | cap=2; put(1,1), put(2,2), get(1), put(3,3), get(2), get(3), put(4,4), get(1), get(3), get(4) | -1, 3, -1, 4 | Classic |
| 2 | cap=0; put(1,1), get(1) | -1 | Degenerate |
| 3 | cap=2; put(1,1), put(2,2), put(1,10), put(3,3), get(2) | -1 | Tie → LRU |

### 13. Min Stack — LC 155
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | push(-2), push(0), push(-3), getMin, pop, top, getMin | -3, 0, -2 | Classic |
| 2 | push(1), push(1), getMin | 1 | Equal mins |
| 3 | push(5), pop, getMin | error-safe | Empty check |

### 14. Insert Delete GetRandom O(1) — LC 380
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | insert(1), remove(2), insert(2), getRandom, remove(1), getRandom | true, false, true, 2, true, 2 (or 1→gone) | Classic |
| 2 | insert(1), insert(1) | true, false | Duplicate |
| 3 | remove(5) | false | Missing |

### 15. Time Based Key-Value Store — LC 981
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | set(foo,bar,1); get(foo,1); get(foo,3) | "bar","bar" | Exact + latest |
| 2 | set(foo,bar2,4); get(foo,5); get(foo,3) | "bar2","bar" | Snapshots |
| 3 | get(baz,1) | "" | Missing key |

### 16. Design Add and Search Words — LC 211
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | add(bad), add(dad), add(mad); search(pad), search(bad), search(.ad), search(b..) | false,true,true,true | Wildcards |
| 2 | add(a); search(a) | true | Single char |
| 3 | search(.a) on {bad,dad} | false | Length mismatch |

### 17. Word Search II — LC 212
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | board=[["o","a","a","n"],["e","t","a","e"],["i","h","k","r"],["i","f","l","v"]], words=["oath","pea","eat","rain"] | ["oath","eat"] | Classic |
| 2 | board=[["a","b"]], words=["ba"] | ["ba"] | Order-independent |
| 3 | words=["abcb"] on 1×4 board "abcd" | [] | Reuse not allowed |

### 18. Maximum XOR of Two Numbers — LC 421
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,10,5,25,2,8] | 28 | 5^25 |
| 2 | [14,70,53,83,49,91,36,80,92,51,66,70] | 127 | Classic |
| 3 | [0] | 0 | Single |

### 19. Basic Calculator — LC 224
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "1 + 1" | 2 | Basic |
| 2 | " 2-1 + 2 " | 3 | Spaces |
| 3 | "(1+(4+5+2)-3)+(6+8)" | 23 | Nested |

### 20. Decode String — LC 394
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "3[a]2[bc]" | "aaabcbc" | Basic |
| 2 | "3[a2[c]]" | "accaccacc" | Nested |
| 3 | "2[abc]3[cd]ef" | "abcabccdcdcdef" | Sequential |


---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Case | Size | Validates |
|---|------|------|-----------|
| 1 | Daily Temperatures: strictly decreasing then hot | n=100,000 | Amortized O(n) stack |
| 2 | Next Greater II: circular all-descending | n=100,000 | Two-pass circular |
| 3 | Largest Rectangle: increasing then decreasing bars | n=100,000 | Sentinel handling |
| 4 | Sum Subarray Mins: all-equal array | n=50,000 | Tie-breaking (no double count) |
| 5 | Stock Span: strictly increasing prices | n=100,000 | Span absorption |
| 6 | Remove K Digits: "1122334455…", k=large | n=100,000 | Leftover pops |
| 7 | LRU Cache: interleaved get/put on distinct keys | 100k ops | O(1) per op |
| 8 | LFU Cache: same-frequency churn | 50k ops | minFreq maintenance |
| 9 | TimeMap: 100k sets on one key | n=100,000 | Binary search O(log n) |
| 10 | Basic Calculator: deeply nested parens "((((…1…))))" | depth=10,000 | Stack depth OK |

### 📝 Notes
- SLOW ⚠️ on #7/#8 → per-op cost is O(n) somewhere (e.g., linear scan of list instead of node moves).
- SLOW ⚠️ on #1–#5 → check "skip stale entries" and while-loop conditions.
