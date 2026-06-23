package array.matrix

/**
 * https://leetcode.com/problems/spiral-matrix/
 *
 * Given an m x n matrix, return all elements of the matrix in spiral order.
 *
 * Example 1:
 *
 * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * Output: [1,2,3,6,9,8,7,4,5]
 *
 * Example 2:
 *
 * Input: matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
 * Output: [1,2,3,4,8,12,11,10,9,5,6,7]
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Asked at Meta, Amazon, Microsoft)
 *
 * Key Insight: Use four boundaries (top, bottom, left, right). Traverse in order:
 * left→right, top→bottom, right→left, bottom→top. Shrink boundaries after each direction.
 */
fun main() {
    val matrix1 = arrayOf(intArrayOf(1,2,3), intArrayOf(4,5,6), intArrayOf(7,8,9))
    println(spiralOrder(matrix1))

    val matrix2 = arrayOf(intArrayOf(1,2,3,4), intArrayOf(5,6,7,8), intArrayOf(9,10,11,12))
    println(spiralOrder(matrix2))
}

/**
 * Time Complexity O(M × N)
 * Space Complexity O(1) excluding output
 */
fun spiralOrder(matrix: Array<IntArray>): List<Int> {
    val result = mutableListOf<Int>()
    if (matrix.isEmpty()) return result

    var top = 0
    var bottom = matrix.size - 1
    var left = 0
    var right = matrix[0].size - 1

    while (top <= bottom && left <= right) {
        // Traverse left → right (top row)
        for (col in left..right) {
            result.add(matrix[top][col])
        }
        top++

        // Traverse top → bottom (right column)
        for (row in top..bottom) {
            result.add(matrix[row][right])
        }
        right--

        if (top <= bottom) {
            // Traverse right → left (bottom row)
            for (col in right downTo left) {
                result.add(matrix[bottom][col])
            }
            bottom--
        }

        if (left <= right) {
            // Traverse bottom → top (left column)
            for (row in bottom downTo top) {
                result.add(matrix[row][left])
            }
            left++
        }
    }

    return result
}
