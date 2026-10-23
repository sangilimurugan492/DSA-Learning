# 🧪 Week 04 Practice — Linked List (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 4 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).
> **Helper:** Use `toList(vararg values)` / `toValues(head)` from [TestRunner.kt](../testrunner/TestRunner.kt) to build/verify lists.

---

## Part A — Sample Test Cases (Correctness)

### 1. Reverse Linked List — LC 206
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5] | [5,4,3,2,1] | Basic |
| 2 | [1,2] | [2,1] | Two nodes |
| 3 | [] | [] | Empty |

### 2. Middle of Linked List — LC 876
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5] | [3,4,5] (node value 3) | Odd |
| 2 | [1,2,3,4,5,6] | [4,5,6] (node value 4) | Even → second middle |
| 3 | [1] | [1] | Single |

### 3. Linked List Cycle — LC 141
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,2,0,-4] with cycle at index 1 | true | Classic |
| 2 | [1,2] with cycle at index 0 | true | Full cycle |
| 3 | [1] no cycle | false | Single |

### 4. Merge Two Sorted Lists — LC 21
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,4], [1,3,4] | [1,1,2,3,4,4] | Basic |
| 2 | [], [] | [] | Both empty |
| 3 | [], [0] | [0] | One empty |

### 5. Remove Duplicates from Sorted List — LC 83
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,2] | [1,2] | Basic |
| 2 | [1,1,2,3,3] | [1,2,3] | Multiple runs |
| 3 | [] | [] | Empty |

### 6. Linked List Binary to Integer — LC 1290
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,0,1] | 5 | Basic |
| 2 | [0] | 0 | Zero |
| 3 | [1,1,1] | 7 | All ones |

### 7. Intersection of Two Linked Lists — LC 160
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | A=[4,1,8,4,5], B=[5,6,1,8,4,5] (intersect at 8) | node 8 | Classic |
| 2 | A=[2,6,4], B=[1,5] (no intersect) | null | Disjoint |
| 3 | A=[1], B=[1] (same node) | node 1 | Same single |

### 8. Delete Node in Linked List — LC 237
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,5,1,9], delete node 5 | [4,1,9] | Middle |
| 2 | [4,5,1,9], delete node 1 | [4,5,9] | Later node |
| 3 | [1,2], delete node 1 | [2] | Near tail |

### 9. Reverse Doubly Linked List
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] | [3,2,1] | Basic |
| 2 | [1] | [1] | Single |
| 3 | [] | [] | Empty |

### 10. Delete Node in DLL
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3], delete 2 | [1,3] | Middle |
| 2 | [1,2], delete 1 (head) | [2] | Head |
| 3 | [1,2], delete 2 (tail) | [1] | Tail |

### 11. Insert Node in DLL
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,3], insert 2 at pos 1 | [1,2,3] | Middle |
| 2 | [], insert 1 at pos 0 | [1] | Empty |
| 3 | [2], insert 1 at pos 0 | [1,2] | Head |

### 12. Remove Duplicates from Sorted DLL
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,1,2] | [1,2] | Basic |
| 2 | [1,1,2,3,3] | [1,2,3] | Multiple runs |
| 3 | [5,5,5] | [5] | All same |

### 13. Circular Linked List
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | Insert 1,2,3 → traverse | 1,2,3 then back to 1 | Basic |
| 2 | Insert 1 → traverse | 1 then back to 1 | Single |
| 3 | Empty → traverse | (no output) | Empty |

### 14. Remove Nth from End — LC 19
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5], n=2 | [1,2,3,5] | Classic |
| 2 | [1], n=1 | [] | Single |
| 3 | [1,2], n=1 | [1] | Tail |

### 15. Remove Duplicates II — LC 82
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,3,4,4,5] | [1,2,5] | Classic |
| 2 | [1,1,1,2,3] | [2,3] | Leading run |
| 3 | [1,1] | [] | All removed |

### 16. Reorder List — LC 143
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4] | [1,4,2,3] | Even |
| 2 | [1,2,3,4,5] | [1,5,2,4,3] | Odd |
| 3 | [1] | [1] | Single |

### 17. Swap Nodes in Pairs — LC 24
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4] | [2,1,4,3] | Basic |
| 2 | [] | [] | Empty |
| 3 | [1] | [1] | Odd count |

### 18. Rotate Linked List — LC 61
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5], k=2 | [4,5,1,2,3] | Classic |
| 2 | [0,1,2], k=4 | [2,0,1] | k > n |
| 3 | [1], k=99 | [1] | Single |

### 19. Partition List — LC 86
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,4,3,2,5,2], x=3 | [1,2,2,4,3,5] | Classic |
| 2 | [2,1], x=2 | [1,2] | All below |
| 3 | [] , x=0 | [] | Empty |

### 20. Linked List Cycle II — LC 142
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [3,2,0,-4] cycle at index 1 | node 2 | Classic |
| 2 | [1,2] cycle at index 0 | node 1 | Full cycle |
| 3 | [1] no cycle | null | No cycle |

### 21. Palindrome Linked List — LC 234
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,2,1] | true | Even |
| 2 | [1,2] | false | Not palindrome |
| 3 | [1] | true | Single |

### 22. Add Two Numbers — LC 2
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,4,3] + [5,6,4] | [7,0,8] | 342+465=807 |
| 2 | [0] + [0] | [0] | Zeros |
| 3 | [9,9,9,9,9,9,9] + [9,9,9,9] | [8,9,9,9,0,0,0,1] | Carry chain |

### 23. Copy List with Random Pointer — LC 138
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[7,null],[13,0],[11,4],[10,2],[1,0]] | Deep copy, same values/randoms | Classic |
| 2 | [[1,1],[2,1]] | Deep copy | Random to next |
| 3 | [[3,null],[3,0],[3,null]] | Deep copy | Self randoms |

### 24. Convert Sorted List to BST — LC 109
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [-10,-3,0,5,9] | Height-balanced BST (inorder = list) | Classic |
| 2 | [] | Empty tree | Empty |
| 3 | [1] | Single root | Single |

### 25. Design Hash Set — LC 705
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | add(1), add(2), contains(1), contains(3), remove(2), contains(2) | true, false, false | Classic |
| 2 | add(0), contains(0) | true | Zero key |
| 3 | remove(100) (never added) | no crash | Missing key |

### 26. My Linked List (Design) — LC 707
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | addAtHead(1), addAtTail(3), addAtIndex(1,2), get(1) | 2 | Classic |
| 2 | get(0) after addAtHead(5) | 5 | Head |
| 3 | deleteAtIndex(0), get(0) | 3 (after case 1 state) | Delete head |

### 27. LRU Cache — LC 146
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | LRUCache(2); put(1,1); put(2,2); get(1); put(3,3); get(2) | 1, -1 | Eviction |
| 2 | put(2,1) after put(2,2) → get(2) | 1 | Update refreshes |
| 3 | LRUCache(1); put(1,1); put(2,2); get(1) | -1 | Capacity 1 |

### 28. Flatten Multilevel DLL — LC 430
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3] with 3→child [7,8] | [1,2,3,7,8] | Classic |
| 2 | [1,2] no children | [1,2] | Flat |
| 3 | [1] with child [2] with child [3] | [1,2,3] | Nested chain |

### 29. Find Pairs with Given Sum (Sorted DLL)
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5], target=6 | (1,5),(2,4) | Basic |
| 2 | [2,2,3], target=5 | (2,3) | Duplicates |
| 3 | [1], target=2 | none | Single |

### 30. Josephus Problem
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=5, k=2 | 3 | Classic |
| 2 | n=1, k=1 | 1 | Single |
| 3 | n=7, k=3 | 4 | Classic |

### 31. Sorted Insert in Circular LL
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [2,4], insert 3 | [2,3,4] | Middle |
| 2 | [2,4], insert 1 | [1,2,4] | Before head |
| 3 | [2,4], insert 5 | [2,4,5] | After tail |

### 32. Split Circular LL
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4] | [1,2] and [3,4] | Even |
| 2 | [1,2,3] | [1,2] and [3] | Odd |
| 3 | [1] | [1] and [] | Single |

### 33. Reverse Linked List II — LC 92
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5], left=2, right=4 | [1,4,3,2,5] | Classic |
| 2 | [5], left=1, right=1 | [5] | Single |
| 3 | [1,2], left=1, right=2 | [2,1] | Whole list |

### 34. LFU Cache — LC 460
| # | Operations | Expected | Tests |
|---|-----------|----------|-------|
| 1 | LFUCache(2); put(1,1); put(2,2); get(1); put(3,3); get(2) | 1, -1 | Evict LFU (2) |
| 2 | put(2,2) twice → get(2) | 2 | Freq update |
| 3 | LFU(2); put(1,1); put(2,2); put(3,3); get(1) | -1 | Tie → evict LRU |

### 35. Merge K Sorted Lists — LC 23
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,4,5],[1,3,4],[2,6]] | [1,1,2,3,4,4,5,6] | Classic |
| 2 | [] | [] | Empty |
| 3 | [[], [1]] | [1] | Some empty |

### 36. Sort List — LC 148
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [4,2,1,3] | [1,2,3,4] | Basic |
| 2 | [-1,5,3,4,0] | [-1,0,3,4,5] | Negatives |
| 3 | [] | [] | Empty |

### 37. Reverse in K-Group — LC 25
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [1,2,3,4,5], k=2 | [2,1,4,5,3] | Classic |
| 2 | [1,2,3,4,5], k=3 | [3,2,1,4,5] | Leftover group |
| 3 | [1], k=1 | [1] | k=1 |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Reverse Linked List | N=1,000,000 nodes: 1..1,000,000 | Reversed list (verify head=1,000,000 & tail=1) | < 300 ms | Recursion → StackOverflow |
| 2 | Merge K Sorted Lists | K=500 lists, each 2,000 nodes (values interleaved) | 1..1,000,000 sorted | < 1 s | One-by-one merge O(NK) |
| 3 | Sort List | N=100,000: shuffled 1..100,000 | Sorted | < 500 ms | Insertion sort O(N²) |
| 4 | LRU Cache | Capacity 5,000; 1,000,000 mixed get/put ops on keys 0..9,999 | No crash; spot-check known keys | < 2 s | O(N) list scan per op |
| 5 | Palindrome Linked List | N=500,000: 1..250,000 then 250,000..1 | true | < 300 ms | O(N) extra array vs O(1) in-place |
| 6 | Add Two Numbers | Two lists of 500,000 digits each (all 9s) | 500,001-digit result | < 300 ms | Digit-by-digit correctness + carry |
| 7 | Reverse in K-Group | N=1,000,000, k=1,000 | Each group of 1,000 reversed | < 500 ms | Recursion depth / O(N·k) |
| 8 | Linked List Cycle II | N=1,000,000 nodes with cycle starting at index 500,000 | Node 500,001 (1-based) | < 300 ms | HashSet O(N) memory OK; O(N²) scan hangs |
| 9 | Remove Nth from End | N=1,000,000, n=1 | List without last node | < 200 ms | Two-pass vs one-pass |
| 10 | Copy List with Random Pointer | N=500,000 nodes, random → random node | Deep copy passes equality check | < 500 ms | O(N) HashMap vs O(N²) search |

### 📝 Notes
- **Case 1:** Recursive reversal of 10⁶ nodes will throw StackOverflowError — this case *detects* recursion where iteration is required.
- **Case 2:** Build K lists where list i contains values `i, i+500, i+1000, ...` so the merged result is exactly 1..1,000,000.
- **Case 4:** Interleave gets and puts with `Random(42)`; verify by tracking a small model (e.g., a HashMap you maintain alongside) for a sample of keys.
- **Case 8:** With cycle at index 500,000, Floyd's meeting point math gives entry = node at index 500,000 (0-based) → value 500,001.
