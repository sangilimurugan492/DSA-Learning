package linked_list.single

/**
 * https://leetcode.com/problems/intersection-of-two-linked-lists/
 * Find the node where two linked lists intersect (by reference, not value).
 * FAANG Importance: ⭐⭐⭐⭐⭐
 */

fun main() {
    // Create intersecting lists: A=[4,1,8,4,5], B=[5,6,1,8,4,5] intersect at node 8
    val shared = ListNode(8, null).apply { next = ListNode(4, null).apply { next = ListNode(5, null) } }
    val headA = ListNode(4, null).apply { next = ListNode(1, null).apply { next = shared } }
    val headB = ListNode(5, null).apply { next = ListNode(6, null).apply { next = ListNode(1, null).apply { next = shared } } }
    println(getIntersectionNodeBruteForce(headA, headB)?.`val`)  // 8
    println(getIntersectionNodeLength(headA, headB)?.`val`)      // 8
    println(getIntersectionNodeTwoPointer(headA, headB)?.`val`)  // 8
}

/** BRUTE FORCE: O(M×N) — compare every node of A with every node of B */
fun getIntersectionNodeBruteForce(headA: ListNode?, headB: ListNode?): ListNode? {
    var a = headA
    while (a != null) {
        var b = headB
        while (b != null) {
            if (a === b) return a
            b = b.next
        }
        a = a.next
    }
    return null
}

/** BETTER: O(M+N) time, O(M) space — HashSet of A's nodes, check B */
fun getIntersectionNodeHashSet(headA: ListNode?, headB: ListNode?): ListNode? {
    val visited = mutableSetOf<ListNode>()
    var a = headA
    while (a != null) { visited.add(a); a = a.next }
    var b = headB
    while (b != null) { if (b in visited) return b; b = b.next }
    return null
}

/** OPTIMAL: O(M+N) time, O(1) space — Align lengths by advancing longer list */
fun getIntersectionNodeLength(headA: ListNode?, headB: ListNode?): ListNode? {
    var lenA = 0; var a = headA; while (a != null) { lenA++; a = a.next }
    var lenB = 0; var b = headB; while (b != null) { lenB++; b = b.next }
    a = headA; b = headB
    if (lenA > lenB) repeat(lenA - lenB) { a = a!!.next }
    else repeat(lenB - lenA) { b = b!!.next }
//    while (a != null && b != null) { if (a === b) return a; a = a.next; b = b.next }
    return null
}

/** ELEGANT: O(M+N) time, O(1) space — Two pointers switch lists after reaching end */
fun getIntersectionNodeTwoPointer(headA: ListNode?, headB: ListNode?): ListNode? {
    var a = headA
    var b = headB
    while (a !== b) {
        a = if (a == null) headB else a.next
        b = if (b == null) headA else b.next
    }
    return a
}
