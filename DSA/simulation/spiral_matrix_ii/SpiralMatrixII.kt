package simulation.spiral_matrix_ii

/**
 * https://leetcode.com/problems/spiral-matrix/?envType=problem-list-v2&envId=simulation
 * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * Output: [1,2,3,6,9,8,7,4,5]
 */
fun main() {
    generateMatrix(3).forEachIndexed{ i, re ->
        re.forEach {
            print("$it ")
        }
        println()
    }
}

fun generateMatrix(n: Int): Array<IntArray> {
    val result = Array(n) { IntArray(n) }
    if (n == 1)  {
        result[0][0] = 1
    }

    var top = 0
    var bottom = n - 1
    var left = 0
    var right = n - 1
    var count = 1

    while (top <= bottom && left <= right) {
        // 1. Move Right
        for (i in left..right) {
            result[top][i] = count
            count++
        }
        top++

        // 2. Move Down
        for (i in top..bottom) {
            result[i][right] = count
            count++
        }
        right--

        // 3. Move Left (Check if row still exists)
        if (top <= bottom) {
            for (i in right downTo left) {
                result[bottom][i] = count
                count++
            }
            bottom--
        }

        // 4. Move Up (Check if column still exists)
        if (left <= right) {
            for (i in bottom downTo top) {
                result[i][left] = count
                count++
            }
            left++
        }
    }

    return result
}

