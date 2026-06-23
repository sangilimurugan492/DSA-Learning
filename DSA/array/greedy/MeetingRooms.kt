package array.greedy

/**
 * https://leetcode.com/problems/meeting-rooms/
 * https://leetcode.com/problems/meeting-rooms-ii/
 *
 * Meeting Rooms I: Given an array of meeting time intervals, determine if a person
 * could attend all meetings (no overlaps).
 *
 * Meeting Rooms II: Given an array of meeting time intervals, find the minimum
 * number of conference rooms required.
 *
 * Example 1: intervals = [[0,30],[5,10],[15,20]]
 *   Meeting Rooms I: false (0-30 overlaps with 5-10)
 *   Meeting Rooms II: 2 rooms needed
 *
 * Example 2: intervals = [[7,10],[2,4]]
 *   Meeting Rooms I: true (no overlap)
 *   Meeting Rooms II: 1 room needed
 *
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Classic interval problem — sweep line pattern)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * Meeting Rooms I: Sort by start time, check if any interval overlaps previous.
 *   If intervals[i].start < intervals[i-1].end → overlap → return false
 *
 * Meeting Rooms II: Two approaches:
 *
 * APPROACH 1: Min-Heap of end times
 *   1. Sort intervals by start time
 *   2. For each interval, if earliest ending meeting is done (heap.peek() ≤ start),
 *      reuse that room (poll). Then add current end time.
 *   3. Heap size = number of rooms needed.
 *
 * APPROACH 2: Sweep Line (OPTIMAL — O(N log N))
 *   1. Create events: (time, +1) for start, (time, -1) for end
 *   2. Sort events by time (end before start at same time)
 *   3. Sweep through, track running count. Max count = min rooms.
 *
 * WHY sweep line? It's the most general pattern for interval problems.
 * Same pattern used for: employee free time, car pooling, etc.
 *
 * Connection to other problems:
 *   - Merge Intervals: same sort-by-start pattern
 *   - Non-overlapping Intervals: same overlap detection
 *   - Car Pooling: same sweep line pattern
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Meeting Rooms ===")
    val intervals1 = arrayOf(intArrayOf(0, 30), intArrayOf(5, 10), intArrayOf(15, 20))
    println("Can attend all [0,30],[5,10],[15,20]: ${canAttendMeetings(intervals1)}")  // false
    println("Min rooms [0,30],[5,10],[15,20]: ${minMeetingRooms(intervals1)}")  // 2

    println("---")
    val intervals2 = arrayOf(intArrayOf(7, 10), intArrayOf(2, 4))
    println("Can attend all [7,10],[2,4]: ${canAttendMeetings(intervals2)}")  // true
    println("Min rooms [7,10],[2,4]: ${minMeetingRooms(intervals2)}")  // 1

    println("---")
    val intervals3 = arrayOf(intArrayOf(1, 5), intArrayOf(8, 9), intArrayOf(8, 9))
    println("Min rooms [1,5],[8,9],[8,9]: ${minMeetingRooms(intervals3)}")  // 2
}

/**
 * Meeting Rooms I — Can attend all meetings?
 * Time Complexity: O(N log N) — sort
 * Space Complexity: O(1) or O(N) for sort
 *
 * Sort by start time, check if any start < previous end.
 *
 * Trace for [[0,30],[5,10],[15,20]]:
 * Sorted: [[0,30],[5,10],[15,20]]
 * i=1: start=5 < prevEnd=30 → OVERLAP → false ✅
 */
fun canAttendMeetings(intervals: Array<IntArray>): Boolean {
    intervals.sortBy { it[0] }
    for (i in 1 until intervals.size) {
        if (intervals[i][0] < intervals[i - 1][1]) return false
    }
    return true
}

/**
 * Meeting Rooms II — Min rooms needed (Sweep Line)
 * Time Complexity: O(N log N) — sort events
 * Space Complexity: O(N) — events array
 *
 * Trace for [[0,30],[5,10],[15,20]]:
 * Events: (0,+1), (5,+1), (10,-1), (15,+1), (20,-1), (30,-1)
 * Sorted: (0,+1), (5,+1), (10,-1), (15,+1), (20,-1), (30,-1)
 *
 * Sweep:
 *   t=0:  count=0+1=1, max=1
 *   t=5:  count=1+1=2, max=2  ← need 2 rooms!
 *   t=10: count=2-1=1, max=2
 *   t=15: count=1+1=2, max=2
 *   t=20: count=2-1=1, max=2
 *   t=30: count=1-1=0, max=2
 *
 * Answer: 2 ✅
 */
fun minMeetingRooms(intervals: Array<IntArray>): Int {
    // Create sweep line events
    val events = mutableListOf<Pair<Int, Int>>()  // (time, delta)
    for (interval in intervals) {
        events.add(Pair(interval[0], 1))   // start: +1
        events.add(Pair(interval[1], -1))   // end: -1
    }
    // Sort by time; if same time, end before start (end=0, start=1)
    events.sortBy { it.first * 2 + if (it.second == 1) 1 else 0 }

    var count = 0
    var maxRooms = 0
    for ((_, delta) in events) {
        count += delta
        maxRooms = maxOf(maxRooms, count)
    }
    return maxRooms
}

/**
 * Meeting Rooms II — Min-Heap approach
 * Time Complexity: O(N log N)
 * Space Complexity: O(N)
 *
 * Sort by start time. Min-heap tracks end times of ongoing meetings.
 * If earliest ending meeting is done, reuse that room.
 *
 * Trace for [[0,30],[5,10],[15,20]]:
 * Sorted: [[0,30],[5,10],[15,20]]
 *
 * [0,30]: heap empty → push 30. heap=[30]. rooms=1
 * [5,10]: heap.peek()=30 > 5 → can't reuse → push 10. heap=[10,30]. rooms=2
 * [15,20]: heap.peek()=10 ≤ 15 → reuse! pop 10, push 20. heap=[20,30]. rooms=2
 *
 * Answer: 2 ✅
 */
fun minMeetingRoomsHeap(intervals: Array<IntArray>): Int {
    intervals.sortBy { it[0] }
    val minHeap = java.util.PriorityQueue<Int>()

    for (interval in intervals) {
        if (minHeap.isNotEmpty() && minHeap.peek() <= interval[0]) {
            minHeap.poll()  // reuse room
        }
        minHeap.offer(interval[1])
    }
    return minHeap.size
}
