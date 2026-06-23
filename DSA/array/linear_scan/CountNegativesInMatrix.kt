package array.linear_scan

fun main() {

    val count = countNegativesBF(arrayOf(
        intArrayOf(4,3,2,-1),
        intArrayOf(3,2,1,-1),
        intArrayOf(1,1,-1,-2),
        intArrayOf(-1,-1,-2,-3)
    ))
    val opCount = countNegativesOP(arrayOf(
        intArrayOf(4,3,2,-1),
        intArrayOf(3,2,1,-1),
        intArrayOf(1,1,-1,-2),
        intArrayOf(-1,-1,-2,-3)
    ))

    println("Negative Count BruteForce $count")
    println("Negative Count Optimal $opCount")
}

/**
 * TimeComplexity O(M*N)
 * Space Complexity O(M+N)
 */

fun countNegativesBF(grid: Array<IntArray>): Int {
    var negativeCount = 0
    for (i in grid.indices) {
        for (j in grid[i].indices) {
            if (grid[i][j] < 0) {
                negativeCount++
            }
        }
    }
    return negativeCount
}

/**
 * TimeComplexity O(M+N)
 * Space Complexity O(M+N)
 */

fun countNegativesOP(grid: Array<IntArray>): Int {
    val rows = grid.size
    val cols = grid[0].size

    var count = 0
    var r = rows - 1 // Start at the last row
    var c = 0        // Start at the first column

    while (r >= 0 && c < cols) {
        if (grid[r][c] < 0) {
            // If grid[r][c] is negative, all elements to the
            // right in this row are also negative.
            count += (cols - c)
            // Move up to the next row
            r--
        } else {
            // If the number is non-negative, move to the right column
            c++
        }
    }

    return count
}