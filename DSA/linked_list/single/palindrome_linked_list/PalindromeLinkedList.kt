package linked_list.single.palindrome_linked_list

import linked_list.single.list_node.ListNode

/**
 * https://leetcode.com/problems/palindrome-linked-list/
 * Given head of singly linked list, determine if it is a palindrome.
 * Example: [1,2,2,1] → true | [1,2] → false
 * FAANG Importance: ⭐⭐⭐⭐⭐ (Combines Fast/Slow + Reverse)
 */

fun main() {
    val head1 = ListNode(1).apply { next = ListNode(2).apply { next = ListNode(2).apply { next = ListNode(1) } } }
    println(isPalindromeArrayList(head1))  // true
    val head2 = ListNode(1).apply { next = ListNode(2) }
    println(isPalindromeOptimal(head2))    // false
}

/** BRUTE FORCE: O(N) time, O(N) space — copy to array, two-pointer check */
fun isPalindromeArrayList(head: ListNode?): Boolean {
    val values = mutableListOf<Int>()
    var curr = head
    while (curr != null) { values.add(curr.`val`); curr = curr.next }
    var left = 0; var right = values.size - 1
    while (left < right) {
        if (values[left] != values[right]) return false
        left++; right--
    }
    return true
}

/**
 * OPTIMAL: O(N) time, O(1) space — Find middle, reverse second half, compare
 * 1. Fast/slow to find middle
 * 2. Reverse from middle to end
 * 3. Compare first half with reversed second half
 */
fun isPalindromeOptimal(head: ListNode?): Boolean {
    // Step 1: Find middle
    var slow = head; var fast = head
    while (fast != null && fast.next != null) { slow = slow!!.next; fast = fast.next!!.next }

    // Step 2: Reverse second half
    var prev: ListNode? = null; var curr = slow
    while (curr != null) { val next = curr.next; curr.next = prev; prev = curr; curr = next }

    // Step 3: Compare
    var first = head; var second = prev
    while (second != null) {
        if (first!!.`val` != second.`val`) return false
        first = first.next; second = second.next
    }
    return true
}
