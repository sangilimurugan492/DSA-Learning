# 🧪 Week 09 Practice — Advanced Graphs + Intervals (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 9 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Network Delay Time — LC 743
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | times=[[2,1,1],[2,3,1],[3,4,1]], n=4, k=2 | 2 | Basic chain |
| 2 | times=[[1,2,1]], n=2, k=2 | -1 | Unreachable node |
| 3 | times=[[1,2,1],[2,1,1]], n=2, k=1 | 1 | Cycle, both reached |

### 2. Cheapest Flights Within K Stops — LC 787
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=4, flights=[[0,1,100],[1,2,100],[0,2,500]], src=0, dst=2, k=1 | 200 | Cheaper longer path |
| 2 | same, k=0 | 500 | No stops allowed |
| 3 | n=3, flights=[[0,1,2],[1,2,1]], src=1, dst=2, k=1 | 1 | src not 0 |

### 3. Path With Minimum Effort — LC 1631
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2,2],[3,8,2],[5,3,5]] | 2 | Basic |
| 2 | [[1,2,3],[3,8,4],[5,3,5]] | 1 | Alternative route |
| 3 | [[1,2,1,1,1],[1,2,1,2,1],[1,2,1,2,1],[1,2,1,2,1],[1,1,1,2,1]] | 0 | Zig-zag zero-diff |

### 4. Path With Maximum Probability — LC 1514
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=3, edges=[[0,1],[1,2],[0,2]], succ=[0.5,0.5,0.2], start=0, end=2 | 0.25 | Two-hop beats direct |
| 2 | same, succ=[0.5,0.5,0.3] | 0.3 | Direct wins |
| 3 | n=1, edges=[], start=0, end=0 | 1.0 | Self |

### 5. Swim in Rising Water — LC 778
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[0,2],[1,3]] | 3 | Basic |
| 2 | [[0,1,2,3,4],[24,23,22,21,5],[12,13,14,15,16],[11,17,18,19,20],[10,9,8,7,6]] | 16 | Snake path |
| 3 | [[0]] | 0 | Single cell |

### 6. Redundant Connection — LC 684
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2],[1,3],[2,3]] | [2,3] | Basic |
| 2 | [[1,2],[2,3],[3,4],[1,4],[1,5]] | [1,4] | Later cycle edge |
| 3 | [[3,4],[1,2],[2,4],[3,5],[2,5]] | [2,5] | Unsorted input |

### 7. Redundant Connection II — LC 685
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2],[1,3],[2,3]] | [2,3] | 2-parent + cycle |
| 2 | [[1,2],[2,3],[3,4],[4,1],[1,5]] | [4,1] | Cycle only |
| 3 | [[2,1],[3,1],[4,1]] | [4,1] | 2-parent, no cycle |

### 8. Number of Operations to Make Network Connected — LC 1319
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=4, connections=[[1,2],[1,3],[2,3]] | 1 | One spare cable |
| 2 | n=6, connections=[[1,2],[1,3],[2,3],[4,5],[4,5]] | -1 | Not enough cables |
| 3 | n=4, connections=[[1,2],[2,3],[3,4]] | 0 | Already connected |

### 9. Most Stones Removed — LC 947
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]] | 5 | One component |
| 2 | [[0,0],[0,2],[1,1],[2,0],[2,2]] | 3 | Diagonals |
| 3 | [[0,0]] | 0 | Single stone |

### 10. Accounts Merge — LC 721
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [["John","johnsmith@mail.com","john_newyork@mail.com"],["John","johnsmith@mail.com","john00@mail.com"]] | 2 merged accounts | Shared email |
| 2 | [["Mary","mary@mail.com"],["Mary","marym@mail.com"]] | 2 | Same name, different person |
| 3 | [["Gabe","gabe0@mail.com","gabe1@mail.com"],["Gabe","gabe3@mail.com","gabe1@mail.com"]] | 1 merged | Chain merge |

### 11. Regions Cut By Slashes — LC 959
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [" /","/ "] | 2 | Splitting |
| 2 | [" /","  "] | 1 | No full split |
| 3 | ["/\\","\\/"] | 5 | Diamond regions |

### 12. Min Cost to Connect All Points — LC 1584
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,1],[3,4],[-1,0]] | 20 | Basic |
| 2 | [[3,12],[-2,5],[-4,1]] | 18 | Manhattan MST |
| 3 | [[0,0]] | 0 | Single point |

### 13. Course Schedule IV — LC 1462
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=2, pre=[[1,0]], q=[[0,1],[1,0]] | [false,true] | Direction |
| 2 | n=2, pre=[], q=[[1,0],[0,1]] | [false,false] | No edges |
| 3 | n=3, pre=[[1,2],[2,0]], q=[[1,0]] | [true] | Transitive |

### 14. Alien Dictionary — LC 269
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | ["wrt","wrf","er","ett","rftt"] | "wertf" | Classic |
| 2 | ["z","x"] | "zx" | Simple |
| 3 | ["z","x","z"] | "" | Cycle → invalid |


### 15. Minimum Height Trees — LC 310
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=4, edges=[[1,0],[1,2],[1,3]] | [1] | Star |
| 2 | n=6, edges=[[3,0],[3,1],[3,2],[3,4],[5,4]] | [3,4] | Two centroids |
| 3 | n=1, edges=[] | [0] | Single node |

### 16. Merge Intervals — LC 56
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,3],[2,6],[8,10],[15,18]] | [[1,6],[8,10],[15,18]] | Basic merge |
| 2 | [[1,4],[4,5]] | [[1,5]] | Touching intervals |
| 3 | [[1,4],[0,4]] | [[0,4]] | Unsorted input |

### 17. Insert Interval — LC 57
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,3],[6,9]], new=[2,5] | [[1,5],[6,9]] | Basic merge |
| 2 | [[1,2],[3,5],[6,7],[8,10],[12,16]], new=[4,8] | [[1,2],[3,10],[12,16]] | Chain merge |
| 3 | [], new=[5,7] | [[5,7]] | Empty list |

### 18. Non-overlapping Intervals — LC 435
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2],[2,3],[3,4],[1,3]] | 1 | Remove [1,3] |
| 2 | [[1,2],[1,2],[1,2]] | 2 | Duplicates |
| 3 | [[1,2],[2,3]] | 0 | Touching is fine |

### 19. Meeting Rooms II — LC 253
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[0,30],[5,10],[15,20]] | 2 | Basic |
| 2 | [[7,10],[2,4]] | 1 | No overlap |
| 3 | [[1,5],[2,4],[3,6]] | 3 | Triple overlap |

### 20. Interval List Intersections — LC 986
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | A=[[0,2],[5,10],[13,23],[24,25]], B=[[1,5],[8,12],[15,24],[25,26]] | [[1,2],[5,5],[8,10],[15,23],[24,24],[25,25]] | Classic |
| 2 | A=[[1,3]], B=[[4,6]] | [] | No overlap |
| 3 | A=[[1,10]], B=[[2,3],[5,7]] | [[2,3],[5,7]] | Contained |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Case | Size | Validates |
|---|------|------|-----------|
| 1 | Network Delay: star graph from node 0, weights 1 | n=5,000 | Dijkstra O(E log V) |
| 2 | Network Delay: linear chain 0→1→…→n | n=50,000 | No recursion overflow / O(E) |
| 3 | Redundant Connection: chain + back edge at end | n=20,000 | DSU with path compression |
| 4 | Accounts Merge: 10k accounts sharing chains | ~30k emails | DSU + grouping |
| 5 | Min Cost Connect Points: random 2D points | n=3,000 | Prim's O(n²) |
| 6 | Course Schedule IV: chain prerequisites | n=100, q=10k | Floyd-Warshall closure |
| 7 | Alien Dictionary: 26-letter chain a→b→…→z + noise words | 5k words | Kahn's linear |
| 8 | Merge Intervals: 100k already-sorted intervals | n=100,000 | O(n log n) sort + merge |
| 9 | Meeting Rooms II: 50k overlapping meetings | n=50,000 | Heap reuse |
| 10 | Interval Intersections: two 50k lists fully overlapping | n=50,000 each | O(n+m) two pointers |

### 📝 Notes
- Log any SLOW ⚠️ result — usually a hidden O(n²) in an intended O(n log n) path.
- Dijkstra without "skip stale heap entries" check fails test #1/#2.
- DSU without path compression fails test #3.
