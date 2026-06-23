package linked_list.single

/**
 * https://leetcode.com/problems/add-two-numbers/
 * Add two numbers represented by linked lists (digits stored in reverse order).
 * Example: (2→4→3) + (5→6→4) = 7→0→8  (342 + 465 = 807)
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Top 10 most asked LL problem)
 */

fun main() {
    val l1 = ListNode(2, null).apply { next = ListNode(4, null).apply { next = ListNode(3, null) } }
    val l2 = ListNode(5, null).apply { next = ListNode(6, null).apply { next = ListNode(4, null) } }
    var result = addTwoNumbers1(l1, l2)
    while (result != null) { print("${result.`val`} → "); result = result.next }
    println("null")
}

/**
 * ITERATIVE: O(max(M,N)) time, O(max(M,N)) space
 * Traverse both lists, sum digit by digit with carry.
 */
fun addTwoNumbers1(l1: ListNode?, l2: ListNode?): ListNode? {
    val dummy = ListNode(0, null)
    var curr = dummy
    var carry = 0
    var p = l1
    var q = l2

    while (p != null || q != null || carry != 0) {
        val x = p?.`val` ?: 0
        val y = q?.`val` ?: 0
        val sum = x + y + carry
        carry = sum / 10
        curr.next = ListNode(sum % 10, null)
        curr = curr.next!!
        p = p?.next
        q = q?.next
    }

    return dummy.next
}

/**
 * RECURSIVE: O(max(M,N)) time, O(max(M,N)) stack space
 */
fun addTwoNumbersRecursive(l1: ListNode?, l2: ListNode?, carry: Int = 0): ListNode? {
    if (l1 == null && l2 == null && carry == 0) return null

    val x = l1?.`val` ?: 0
    val y = l2?.`val` ?: 0
    val sum = x + y + carry

    return ListNode(sum % 10, null).apply {
        next = addTwoNumbersRecursive(l1?.next, l2?.next, sum / 10)
    }
}
