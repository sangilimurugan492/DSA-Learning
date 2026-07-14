package array.greedy.meeting_rooms

/**
 * Meeting Rooms I & II — LeetCode #252, #253
 * https://leetcode.com/problems/meeting-rooms/
 * https://leetcode.com/problems/meeting-rooms-ii/
 *
 * Problem:
 * -------
 * Meeting Rooms I: Determine if a person could attend all meetings (no overlaps).
 * Meeting Rooms II: Find the minimum number of conference rooms required.
 *
 * Example:  [[0,30],[5,10],[15,20]] → I: false, II: 2 rooms
 *           [[7,10],[2,4]] → I: true, II: 1 room
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic interval problem — sweep line pattern)
 *
 * Two approaches for Meeting Rooms II:
 * 1. Min-Heap of end times: O(N log N) — reuse rooms that ended
 * 2. Sweep Line: O(N log N) — events: +1 for start, -1 for end, track max count
 */

fun main() {
    val intervals1 = arrayOf(intArrayOf(0, 30), intArrayOf(5, 10), intArrayOf(15, 20))
    val intervals2 = arrayOf(intArrayOf(7, 10), intArrayOf(2, 4))

    println("=== Meeting Rooms I ===")
    println("canAttendMeetings(${intervals1.map { it.toList() }}) = ${canAttendMeetings(intervals1)}")
    println("canAttendMeetings(${intervals2.map { it.toList() }}) = ${canAttendMeetings(intervals2)}")

    println("\n=== Meeting Rooms II: Min-Heap ===")
    println("minMeetingRooms(${intervals1.map { it.toList() }}) = ${minMeetingRoomsHeap(intervals1)}")
    println("minMeetingRooms(${intervals2.map { it.toList() }}) = ${minMeetingRoomsHeap(intervals2)}")

    println("\n=== Meeting Rooms II: Sweep Line ===")
    println("minMeetingRooms(${intervals1.map { it.toList() }}) = ${minMeetingRooms(intervals1)}")
    println("minMeetingRooms(${intervals2.map { it.toList() }}) = ${minMeetingRooms(intervals2)}")
}

// ═══════════════════════════════════════════════════════════════════════════════
// MEETING ROOMS I — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Sort by start time. If any start < previous end → overlap → false.
 *
 * Time Complexity:  O(N log N).
 * Space Complexity: O(1).
 */
fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
    intervals.sortBy { it[0] }
    for (i in 1 until intervals.size) {
        if (intervals[i][0] < intervals[i - 1][1]) return false
    }
    return true
}

// ═══════════════════════════════════════════════════════════════════════════════
// MEETING ROOMS II — METHOD 1: MIN-HEAP — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * MIN-HEAP — Sort by start. Min-heap tracks end times of ongoing meetings.
 * If earliest ending meeting is done (heap.peek() ≤ start), reuse that room.
 *
 * Time Complexity:  O(N log N).
 * Space Complexity: O(N).
 */
fun minMeetingRoomsHeap(intervals: Array<IntArray>): Int {
    intervals.sortBy { it[0] }
    val minHeap = java.util.PriorityQueue<Int>()

    for (interval in intervals) {
        if (minHeap.isNotEmpty() && minHeap.peek() <= interval[0]) {
            minHeap.poll()  // Reuse room.
        }
        minHeap.offer(interval[1])
    }
    return minHeap.size
}

// ═══════════════════════════════════════════════════════════════════════════════
// MEETING ROOMS II — METHOD 2: SWEEP LINE — O(N log N)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * SWEEP LINE — Create events: +1 for start, -1 for end. Sort by time.
 * Sweep through, track running count. Max count = min rooms.
 *
 * Time Complexity:  O(N log N).
 * Space Complexity: O(N).
 */
fun minMeetingRooms(intervals: Array<IntArray>): Int {
    val events = mutableListOf<Pair<Int, Int>>()
    for (interval in intervals) {
        events.add(Pair(interval[0], 1))   // start: +1
        events.add(Pair(interval[1], -1))  // end: -1
    }
    events.sortBy { it.first * 2 + if (it.second == 1) 1 else 0 }

    var count = 0
    var maxRooms = 0
    for ((_, delta) in events) {
        count += delta
        maxRooms = maxOf(maxRooms, count)
    }
    return maxRooms
}
