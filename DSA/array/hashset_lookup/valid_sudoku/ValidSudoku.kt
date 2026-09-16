package array.hashset_lookup.valid_sudoku

/**
 * https://leetcode.com/problems/valid-sudoku/
 *
 * Determine if a 9 x 9 Sudoku board is valid. Only the filled cells need to be validated
 * according to the following rules:
 * - Each row must contain the digits 1-9 without repetition
 * - Each column must contain the digits 1-9 without repetition
 * - Each of the nine 3 x 3 sub-boxes must contain the digits 1-9 without repetition
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Google, Amazon, Apple)
 *
 * Key Insight: Use HashSets for each row, column, and 3x3 box.
 * Box index = (row / 3) * 3 + (col / 3) — maps each cell to its 3x3 box.
 */
fun main() {
    val board = arrayOf(
        charArrayOf('5','3','.','.','7','.','.','.','.'),
        charArrayOf('6','.','.','1','9','5','.','.','.'),
        charArrayOf('.','9','8','.','.','.','.','6','.'),
        charArrayOf('8','.','.','.','6','.','.','.','3'),
        charArrayOf('4','.','.','8','.','3','.','.','1'),
        charArrayOf('7','.','.','.','2','.','.','.','6'),
        charArrayOf('.','6','.','.','.','.','2','8','.'),
        charArrayOf('.','.','.','4','1','9','.','.','5'),
        charArrayOf('.','.','.','.','8','.','.','7','9')
    )

    println("Brute Force: ${isValidSudokuBruteForce(board)}")
    println("Optimal (HashSet): ${isValidSudoku(board)}")
    println("Optimal (Bitmask): ${isValidSudokuBitmask(board)}")
}

/**
 * Time Complexity O(N²) where N = 9
 * Space Complexity O(N)
 *
 * Approach: Brute Force — Three separate passes
 *
 * Pass 1: Validate each row (check for duplicates using a HashSet)
 * Pass 2: Validate each column (check for duplicates using a HashSet)
 * Pass 3: Validate each 3x3 sub-box (check for duplicates using a HashSet)
 *
 * Each pass iterates all 81 cells, so total = 3 × 81 = 243 checks.
 * Simple but does 3 passes instead of 1.
 *
 * Space is O(N) because only one HashSet of up to 9 elements is used at a time.
 */
fun isValidSudokuBruteForce(board: Array<CharArray>): Boolean {
    // Pass 1: Check each row
    for (i in 0 until 9) {
        val seen = mutableSetOf<Char>()
        for (j in 0 until 9) {
            val c = board[i][j]
            if (c == '.') continue
            if (c in seen) return false
            seen.add(c)
        }
    }

    // Pass 2: Check each column
    for (j in 0 until 9) {
        val seen = mutableSetOf<Char>()
        for (i in 0 until 9) {
            val c = board[i][j]
            if (c == '.') continue
            if (c in seen) return false
            seen.add(c)
        }
    }

    // Pass 3: Check each 3x3 sub-box
    for (boxRow in 0 until 3) {
        for (boxCol in 0 until 3) {
            val seen = mutableSetOf<Char>()
            for (i in 0 until 3) {
                for (j in 0 until 3) {
                    val c = board[boxRow * 3 + i][boxCol * 3 + j]
                    if (c == '.') continue
                    if (c in seen) return false
                    seen.add(c)
                }
            }
        }
    }

    return true
}

/**
 * Time Complexity O(N²) where N = 9
 * Space Complexity O(N²)
 *
 * Approach: Optimal — Single pass with HashSets
 *
 * Maintain 9 sets for rows, 9 sets for columns, and 9 sets for 3x3 boxes.
 * In a single pass, for each filled cell, check if the digit already exists
 * in the corresponding row set, column set, or box set.
 *
 * Box index = (row / 3) * 3 + (col / 3)
 *
 * This does 1 pass (81 cells) instead of 3 passes.
 */
fun isValidSudoku(board: Array<CharArray>): Boolean {
    val rows = Array(9) { mutableSetOf<Char>() }
    val cols = Array(9) { mutableSetOf<Char>() }
    val boxes = Array(9) { mutableSetOf<Char>() }

    for (i in 0 until 9) {
        for (j in 0 until 9) {
            val c = board[i][j]
            if (c == '.') continue

            val boxIndex = (i / 3) * 3 + (j / 3)

            if (c in rows[i] || c in cols[j] || c in boxes[boxIndex]) {
                return false
            }

            rows[i].add(c)
            cols[j].add(c)
            boxes[boxIndex].add(c)
        }
    }

    return true
}

/**
 * Time Complexity O(N²) where N = 9
 * Space Complexity O(N)
 *
 * Approach: Optimal — Single pass with Bitmasks
 *
 * Instead of HashSets, use integer bitmasks to track seen digits.
 * Each row, column, and box gets a single Int where bit `d` represents digit `d`.
 *
 * - Check: (mask and (1 shl d)) != 0 → digit already seen
 * - Mark:  mask = mask or (1 shl d)
 *
 * This replaces 27 HashSets with 3 IntArrays of size 9 — much more memory efficient.
 */
fun isValidSudokuBitmask(board: Array<CharArray>): Boolean {
    val rows = IntArray(9)
    val cols = IntArray(9)
    val boxes = IntArray(9)

    for (i in 0 until 9) {
        for (j in 0 until 9) {
            val c = board[i][j]
            if (c == '.') continue

            val digit = c - '0'           // 1..9
            val bit = 1 shl digit          // set the bit for this digit
            val boxIndex = (i / 3) * 3 + (j / 3)

            // Check if this digit already seen in row, col, or box
            if ((rows[i] and bit) != 0 ||
                (cols[j] and bit) != 0 ||
                (boxes[boxIndex] and bit) != 0
            ) {
                return false
            }

            // Mark digit as seen
            rows[i] = rows[i] or bit
            cols[j] = cols[j] or bit
            boxes[boxIndex] = boxes[boxIndex] or bit
        }
    }

    return true
}
