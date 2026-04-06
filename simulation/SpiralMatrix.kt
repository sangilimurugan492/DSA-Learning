package simulation

/**
 * https://leetcode.com/problems/spiral-matrix/?envType=problem-list-v2&envId=simulation
 * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * Output: [1,2,3,6,9,8,7,4,5]
 */
fun main() {
    spiralOrder(arrayOf(intArrayOf(1,2,3), intArrayOf(4,5,6), intArrayOf(7,8,9))).forEach {
        print("$it ")
    }
}

fun spiralOrder(matrix: Array<IntArray>): List<Int> {
    val result = mutableListOf<Int>()
    if (matrix.isEmpty()) return result

    var top = 0
    var bottom = matrix.size - 1
    var left = 0
    var right = matrix[0].size - 1

    while (top <= bottom && left <= right) {
        // 1. Move Right
        for (i in left..right) {
            result.add(matrix[top][i])
        }
        top++

        // 2. Move Down
        for (i in top..bottom) {
            result.add(matrix[i][right])
        }
        right--

        // 3. Move Left (Check if row still exists)
        if (top <= bottom) {
            for (i in right downTo left) {
                result.add(matrix[bottom][i])
            }
            bottom--
        }

        // 4. Move Up (Check if column still exists)
        if (left <= right) {
            for (i in bottom downTo top) {
                result.add(matrix[i][left])
            }
            left++
        }
    }

    return result
}

