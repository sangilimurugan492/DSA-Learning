package array.matrix.set_matrix_zeroes

/**
 * https://leetcode.com/problems/set-matrix-zeroes/
 *
 * Given an m x n integer matrix, if an element is 0, set its entire row and column to 0's.
 * You must do it in place.
 *
 * Example 1:
 *
 * Input: matrix = [[1,1,1],[1,0,1],[1,1,1]]
 * Output: [[1,0,1],[0,0,0],[1,0,1]]
 *
 * Example 2:
 *
 * Input: matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
 * Output: [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Meta, Amazon, Microsoft)
 *
 * Key Insight: Use the first row and first column as markers to record which rows/cols need
 * to be zeroed. Use two extra variables to track if first row/col themselves need zeroing.
 */
fun main() {
    val matrix1 = arrayOf(intArrayOf(1, 1, 1), intArrayOf(1, 0, 1), intArrayOf(1, 1, 1))
    setZeroesExtraSpace(matrix1)
    matrix1.forEach { println(it.toList()) }

    println("---")

    val matrix2 = arrayOf(intArrayOf(0, 1, 2, 0), intArrayOf(3, 4, 5, 2), intArrayOf(1, 3, 1, 5))
    setZeroesOptimal(matrix2)
    matrix2.forEach { println(it.toList()) }
}

/**
 * Time Complexity O(M * N)
 * Space Complexity O(M + N) - using row and col marker arrays
 */
fun setZeroesExtraSpace(matrix: Array<IntArray>) {
    val rows = matrix.size
    val cols = matrix[0].size
    val rowMarker = BooleanArray(rows)
    val colMarker = BooleanArray(cols)

    // First pass: mark rows and cols that have zeros
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (matrix[i][j] == 0) {
                rowMarker[i] = true
                colMarker[j] = true
            }
        }
    }

    // Second pass: set zeros based on markers
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (rowMarker[i] || colMarker[j]) {
                matrix[i][j] = 0
            }
        }
    }
}

/**
 * Time Complexity O(M * N)
 * Space Complexity O(1) - using first row/col as markers
 *
 * Approach:
 * 1. Use first row and first column as marker arrays
 * 2. Track separately if first row and first column need to be zeroed
 * 3. First pass: mark zeros in first row/col
 * 4. Second pass: set zeros based on markers (skip first row/col)
 * 5. Finally: zero first row/col if needed
 *
 * Trace for [[1,1,1],[1,0,1],[1,1,1]]:
 * - After marking: firstRowZero=false, firstColZero=false
 * - matrix[0][1]=0 (col marker), matrix[1][0]=0 (row marker)
 * - Set zeros based on markers → correct result
 */
fun setZeroesOptimal(matrix: Array<IntArray>) {
    val rows = matrix.size
    val cols = matrix[0].size
    var firstRowZero = false
    var firstColZero = false

    // Check if first row needs to be zeroed
    for (j in 0 until cols) {
        if (matrix[0][j] == 0) {
            firstRowZero = true
            break
        }
    }

    // Check if first column needs to be zeroed
    for (i in 0 until rows) {
        if (matrix[i][0] == 0) {
            firstColZero = true
            break
        }
    }

    // Use first row/col as markers
    for (i in 1 until rows) {
        for (j in 1 until cols) {
            if (matrix[i][j] == 0) {
                matrix[i][0] = 0  // Mark row
                matrix[0][j] = 0  // Mark col
            }
        }
    }

    // Set zeros based on markers (skip first row/col)
    for (i in 1 until rows) {
        for (j in 1 until cols) {
            if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                matrix[i][j] = 0
            }
        }
    }

    // Zero first row if needed
    if (firstRowZero) {
        for (j in 0 until cols) {
            matrix[0][j] = 0
        }
    }

    // Zero first column if needed
    if (firstColZero) {
        for (i in 0 until rows) {
            matrix[i][0] = 0
        }
    }
}
