package array.matrix

/**
 * https://leetcode.com/problems/rotate-image/
 *
 * You are given an n x n 2D matrix representing an image, rotate the image by 90 degrees
 * clockwise. You must rotate the image in-place.
 *
 * Example 1:
 *
 * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * Output: [[7,4,1],[8,5,2],[9,6,3]]
 *
 * Example 2:
 *
 * Input: matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]
 * Output: [[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Asked at Amazon, Apple, Microsoft — classic matrix problem)
 *
 * Key Insight: 90° clockwise rotation = Transpose + Reverse each row
 * Or equivalently: matrix[i][j] → matrix[j][n-1-i] (rotate in layers)
 */
fun main() {
    val matrix1 = arrayOf(intArrayOf(1,2,3), intArrayOf(4,5,6), intArrayOf(7,8,9))
    rotateTransposeReverse(matrix1)
    matrix1.forEach { println(it.toList()) }

    println("---")

    val matrix2 = arrayOf(intArrayOf(1,2,3), intArrayOf(4,5,6), intArrayOf(7,8,9))
    rotateLayerByLayer(matrix2)
    matrix2.forEach { println(it.toList()) }
}

/**
 * Time Complexity O(N²)
 * Space Complexity O(1)
 *
 * Approach: Transpose + Reverse each row
 *
 * Step 1: Transpose (swap matrix[i][j] with matrix[j][i])
 * [1,2,3]     [1,4,7]
 * [4,5,6]  →  [2,5,8]
 * [7,8,9]     [3,6,9]
 *
 * Step 2: Reverse each row
 * [1,4,7]     [7,4,1]
 * [2,5,8]  →  [8,5,2]
 * [3,6,9]     [9,6,3]
 */
fun rotateTransposeReverse(matrix: Array<IntArray>) {
    val n = matrix.size

    // Step 1: Transpose
    for (i in 0 until n) {
        for (j in i + 1 until n) {
            val temp = matrix[i][j]
            matrix[i][j] = matrix[j][i]
            matrix[j][i] = temp
        }
    }

    // Step 2: Reverse each row
    for (i in 0 until n) {
        var left = 0
        var right = n - 1
        while (left < right) {
            val temp = matrix[i][left]
            matrix[i][left] = matrix[i][right]
            matrix[i][right] = temp
            left++
            right--
        }
    }
}

/**
 * Time Complexity O(N²)
 * Space Complexity O(1)
 *
 * Approach: Rotate layer by layer (4-way swap)
 *
 * For each layer (outer to inner), rotate groups of 4 elements:
 * top-left → top-right → bottom-right → bottom-left → top-left
 *
 * For position (i, j) in layer:
 * matrix[i][j] → matrix[j][n-1-i] → matrix[n-1-i][n-1-j] → matrix[n-1-j][i] → matrix[i][j]
 */
fun rotateLayerByLayer(matrix: Array<IntArray>) {
    val n = matrix.size

    for (layer in 0 until n / 2) {
        val first = layer
        val last = n - 1 - layer

        for (i in first until last) {
            val offset = i - first

            // Save top
            val top = matrix[first][i]

            // Left → Top
            matrix[first][i] = matrix[last - offset][first]

            // Bottom → Left
            matrix[last - offset][first] = matrix[last][last - offset]

            // Right → Bottom
            matrix[last][last - offset] = matrix[i][last]

            // Top (saved) → Right
            matrix[i][last] = top
        }
    }
}
