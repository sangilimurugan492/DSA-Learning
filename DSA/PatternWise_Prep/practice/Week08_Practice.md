# 🧪 Week 08 Practice — Graph + Simulation + Matrix + String Parsing (Test Cases & Complexity Validation)

> **Part A:** Sample test cases for every Week 8 problem.
> **Part B:** 10 complex test cases to validate time complexity. Use [TestRunner.kt](../testrunner/TestRunner.kt).

---

## Part A — Sample Test Cases (Correctness)

### 1. Add Binary — LC 67
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "11", "1" | "100" | Basic |
| 2 | "1010", "1011" | "10101" | Carry |
| 3 | "0", "0" | "0" | Zeros |

### 2. Add Strings — LC 415
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "11", "123" | "134" | Basic |
| 2 | "456", "77" | "533" | Carry |
| 3 | "0", "0" | "0" | Zeros |

### 3. Add Digits (Digital Root) — LC 258
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | 38 | 2 | 3+8=11→1+1=2 |
| 2 | 0 | 0 | Zero |
| 3 | 9 | 9 | Single digit |

### 4. Fizz Buzz — LC 412
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=3 | [1,2,"Fizz"] | Basic |
| 2 | n=5 | [1,2,"Fizz",4,"Buzz"] | Buzz |
| 3 | n=15 | ...,"FizzBuzz" at 15 | Both |

### 5. Number of Islands — LC 200
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [["1","1","0"],["1","0","0"],["0","0","1"]] | 2 | Two islands |
| 2 | [["1","1","1","1","0"]] | 1 | Single row |
| 3 | [["0"]] | 0 | All water |

### 6. Clone Graph — LC 133
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | 4-node cycle graph | Deep copy, same structure | Classic |
| 2 | 1 node, no neighbors | Deep copy | Single |
| 3 | null | null | Empty |

### 7. Pacific Atlantic Water Flow — LC 417
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2,2,3,5],[3,2,3,4,4],[2,4,5,3,1],[6,7,1,4,5],[5,1,1,2,4]] | [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]] | Classic |
| 2 | [[2,1],[1,2]] | [[0,0],[0,1],[1,0],[1,1]] | All flow |
| 3 | [[1]] | [[0,0]] | Single cell |

### 8. Rotting Oranges — LC 994
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[2,1,1],[1,1,0],[0,1,1]] | 4 | Classic |
| 2 | [[2,1,1],[0,1,1],[1,0,1]] | -1 | Unreachable |
| 3 | [[0,2]] | 0 | Already rotten |

### 9. Dijkstra's Algorithm
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | Graph: 0→(1,4), 0→(2,1), 2→(1,2); src=0 | dist=[0,3,1] | Classic relaxation |
| 2 | Single node, no edges | dist=[0] | Single |
| 3 | Disconnected node | dist=[..,∞] | Unreachable |

### 10. Course Schedule — LC 207
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | numCourses=2, prereq=[[1,0]] | true | Linear chain |
| 2 | numCourses=2, prereq=[[1,0],[0,1]] | false | Cycle |
| 3 | numCourses=1, prereq=[] | true | Single |

### 11. Course Schedule II — LC 210
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | numCourses=4, prereq=[[1,0],[2,0],[3,1],[3,2]] | [0,2,1,3] (or [0,1,2,3]) | Valid order |
| 2 | numCourses=2, prereq=[[1,0],[0,1]] | [] | Cycle |
| 3 | numCourses=1, prereq=[] | [0] | Single |

### 12. Number of Provinces — LC 547
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | isConnected=[[1,1,0],[1,1,0],[0,0,1]] | 2 | Two groups |
| 2 | [[1,0,0],[0,1,0],[0,0,1]] | 3 | All separate |
| 3 | [[1,1],[1,1]] | 1 | All connected |

### 13. Graph Valid Tree — LC 261
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=5, edges=[[0,1],[0,2],[0,3],[0,4]] | true | Star |
| 2 | n=5, edges=[[0,1],[1,2],[2,3],[1,3]] | false | Cycle |
| 3 | n=1, edges=[] | true | Single node |

### 14. Set Matrix Zeroes — LC 73
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,1,1],[1,0,1],[1,1,1]] | [[1,0,1],[0,0,0],[1,0,1]] | Classic |
| 2 | [[0,1],[1,0]] | [[0,0],[0,0]] | Corner zeros |
| 3 | [[1]] | [[1]] | No zeros |

### 15. Spiral Matrix — LC 54
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2,3],[4,5,6],[7,8,9]] | [1,2,3,6,9,8,7,4,5] | Classic |
| 2 | [[1,2,3,4]] | [1,2,3,4] | Single row |
| 3 | [[1],[2],[3]] | [1,2,3] | Single column |

### 16. Rotate Image — LC 48
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[1,2,3],[4,5,6],[7,8,9]] | [[7,4,1],[8,5,2],[9,6,3]] | Classic |
| 2 | [[1]] | [[1]] | Single |
| 3 | [[1,2],[3,4]] | [[3,1],[4,2]] | 2×2 |

### 17. Game of Life — LC 289
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | [[0,1,0],[0,0,1],[1,1,1],[0,0,0]] | [[0,0,0],[1,0,1],[0,1,1],[0,1,0]] | Classic |
| 2 | [[1]] | [[0]] | Lonely cell dies |
| 3 | [[1,1],[1,1]] | [[1,1],[1,1]] | Stable block |

### 18. String to Integer (atoi) — LC 8
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "42" | 42 | Basic |
| 2 | "   -42" | -42 | Leading spaces + sign |
| 3 | "4193 with words" | 4193 | Trailing junk |
| 4 | "-91283472332" | -2147483648 | Clamping |

### 19. Integer to Roman — LC 12
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | 3 | "III" | Basic |
| 2 | 58 | "LVIII" | Classic |
| 3 | 1994 | "MCMXCIV" | Subtractive |

### 20. ZigZag Conversion — LC 6
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | "PAYPALISHIRING", numRows=3 | "PAHNAPLSIIGYIR" | Classic |
| 2 | "PAYPALISHIRING", numRows=4 | "PINALSIGYAHIPI" | Classic |
| 3 | "A", numRows=1 | "A" | Single |

### 21. Spiral Matrix II — LC 59
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | n=3 | [[1,2,3],[8,9,4],[7,6,5]] | Classic |
| 2 | n=1 | [[1]] | Single |
| 3 | n=4 | spiral 1..16 | Even |

### 22. Word Ladder — LC 127
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | begin="hit", end="cog", list=["hot","dot","dog","lot","log","cog"] | 5 | Classic |
| 2 | same but no "cog" in list | 0 | End missing |
| 3 | begin="a", end="a" | 1 | Same word |

### 23. Text Justification — LC 68
| # | Input | Expected | Tests |
|---|-------|----------|-------|
| 1 | words=["This","is","an","example","of","text","justification."], maxWidth=16 | ["This    is    an","example  of text","justification.  "] | Classic |
| 2 | words=["What","must","be","acknowledgment","shall","be"], maxWidth=16 | ["What   must   be","acknowledgment  ","shall be        "] | Last line left |
| 3 | words=["a"], maxWidth=5 | ["a    "] | Single word |

---

## Part B — 10 Complex Test Cases (Complexity Validation)

| # | Problem | Input (generated) | Expected | Budget | Detects |
|---|---------|-------------------|----------|--------|---------|
| 1 | Number of Islands | 1,000×1,000 grid, all "1" | **1** | < 500 ms | DFS recursion → StackOverflow; also O(N×M) per cell re-scan |
| 2 | Number of Islands | 1,000×1,000 checkerboard (alternating 1/0) | **500,000** | < 500 ms | O((N×M)²) flood fill |
| 3 | Rotting Oranges | 1,000×1,000 grid: one rotten corner, rest fresh | Minutes = 1,998 (Manhattan distance to far corner) | < 500 ms | DFS per orange O(N²M²) |
| 4 | Course Schedule | numCourses=100,000; chain 0→1→2→...→99,999 | true | < 500 ms | DFS recursion → StackOverflow |
| 5 | Course Schedule | numCourses=100,000; 50,000 disjoint pairs with cycle | false | < 500 ms | Repeated visits without state |
| 6 | Number of Provinces | n=1,000 nodes in one chain | **1** | < 200 ms | O(N²) matrix scan ok; nested DFS revisits |
| 7 | Set Matrix Zeroes | 1,000×1,000 matrix, single 0 at center | Row 500 & col 500 all zero | < 300 ms | Extra O(N×M) space vs O(1) markers |
| 8 | Spiral Matrix | 1,000×1,000 matrix filled 1..1,000,000 | First 10 = 1..10, last = 500,500 (center) | < 300 ms | Index math errors on boundaries |
| 9 | Word Ladder | begin="aaaa", end="zzzz", wordList = all 4-letter combos? too big — use 5,000 random 4-letter words | BFS result (cross-check vs bidirectional) | < 1 s | Generating all 26^4 mutations per word |
| 10 | Pacific Atlantic | 1,000×1,000 matrix: increasing toward top-right | Cells on top row + right col (2,000−1 cells) | < 500 ms | DFS from every cell O((NM)²) |

### 📝 Notes
- **Case 1:** All-land grid: recursive DFS flood fill on 10⁶ cells will likely StackOverflow — use iterative stack/queue or BFS.
- **Case 3:** One rotten at (0,0), fresh elsewhere: BFS spreads 1 layer/minute; farthest cell (999,999) is 999+999 = 1,998 minutes away.
- **Case 4:** Chain of 100,000 courses → recursion depth 100,000 crashes; Kahn's (BFS topological sort) is safe.
- **Case 8:** Center of even 1,000×1,000 spiral: value at (499,499) — verify with small n=4 (center values 6,7,10,11) pattern; simplest check: output length = 1,000,000 and first/last values.
- **Case 9:** 26^4 = 456,976 possible mutations per word — precompute adjacency or use pattern wildcards ("h*t") as BFS neighbors.
- **Case 10:** Matrix where `h[r][c] = r + c` increases toward bottom-right: only top row reaches Pacific, right col reaches Atlantic → 1,999 cells total (top-right counted once).
