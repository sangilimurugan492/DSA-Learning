package array.greedy


fun main() {
    println(minGroups(arrayOf(intArrayOf(5,10),intArrayOf(6,8),intArrayOf(1,5),intArrayOf(2,3),intArrayOf(1,10))))
}
fun minGroups(intervals: Array<IntArray>): Int {
        val n = intervals.size
        val starts = IntArray(1_000_001)
        val ends = IntArray(1_000_001)
        var maxEnd = 0
        for (i in 0 until n) {
            val (start, end) = intervals[i]
            starts[start]++
            ends[end]++
            if (end > maxEnd) {
                maxEnd = end
            }
        }
        var maxOverlap = 0
        var overlap = 0
        for (i in 1..maxEnd) {
            val s = starts[i]
            if (s > 0) {
                overlap += starts[i]
                if (overlap > maxOverlap) {
                    maxOverlap = overlap
                }
            }
            overlap -= ends[i]
        }
        return maxOverlap
}