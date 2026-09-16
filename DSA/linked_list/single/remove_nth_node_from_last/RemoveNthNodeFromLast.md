# Remove Nth Node From End of List — Detailed Explanation

> **LeetCode** | https://leetcode.com/problems/remove-nth-node-from-end-of-list/
> **Topic:** linked_list — single — remove_nth_node_from_last
> **Difficulty:** Medium

---

## 📋 Problem Statement

Given the `head` of a linked list, remove the **nᵗʰ node from the end** of the list and return its head.

**Example 1:**

```
Input:  head = [1, 2, 3, 4, 5],  n = 2
Output: [1, 2, 3, 5]
```

The 2ⁿᵈ node from the end is `4`, so removing it gives `[1, 2, 3, 5]`.

**Example 2:**

```
Input:  head = [1],  n = 1
Output: []
```

**Example 3:**

```
Input:  head = [1, 2],  n = 1
Output: [1]
```

**Constraints:**
- The number of nodes in the list is `sz`.
- `1 <= sz <= 30`
- `0 <= Node.val <= 100`
- `1 <= n <= sz`

---

## 🧩 Method 1: Two-Pass (Count then Remove)

### Core Idea

1. **First pass** — traverse the entire list to count the total number of nodes (`length`).
2. Compute the index of the node to remove from the **front**: `position = length - n`.
3. **Second pass** — traverse to the node just before `position` and skip the target node.

A **dummy node** is used before `head` to cleanly handle the edge case where the head itself needs to be removed (i.e., `n == length`).

### Implementation

```kotlin
fun removeNthFromEndTwoPass(head: ListNode?, n: Int): ListNode? {
    // 1. Create a dummy node to handle edge cases (like removing the head)
    val dummy = ListNode(0, null)
    dummy.next = head

    // 2. First pass — count total nodes
    var length = 0
    var curr: ListNode? = head
    while (curr != null) {
        length++
        curr = curr.next
    }

    // 3. Compute position from the front (0-indexed from dummy)
    val position = length - n

    // 4. Second pass — move to the node just before the target
    curr = dummy
    for (i in 0 until position) {
        curr = curr?.next
    }

    // 5. Skip the target node
    curr?.next = curr?.next?.next

    return dummy.next
}
```

### Complexity

| Metric | Value  |
|--------|--------|
| **Time**  | O(N) — two passes over the list |
| **Space** | O(1) |

---

## 🧩 Method 2: Optimal — Two Pointers (Fast & Slow)

### Core Idea

Use two pointers (`fast` and `slow`) both starting at a **dummy** node:

1. Move `fast` **n + 1** steps ahead. This creates a gap of exactly `n` nodes between `fast` and `slow`.
2. Move both `fast` and `slow` one step at a time until `fast` reaches `null` (past the end).
3. At this point `slow` is positioned on the node **just before** the target — skip the target with `slow.next = slow.next!!.next`.
4. Return `dummy.next`.

> **Why n + 1?** Moving `fast` `n + 1` steps ahead ensures that when `fast` reaches `null`, `slow` lands on the node **before** the one to be removed (not on the target itself), allowing a simple pointer relink.

### Walkthrough (head = [1,2,3,4,5], n = 2)

```
Initial:  dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
          ^fast ^slow

Step 1 — Move fast n+1 = 3 steps:
          dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
          ^slow        ^fast

Step 2 — Move both until fast == null:
          dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
                         ^slow        ^fast
          dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
                              ^slow         ^fast(null)

Step 3 — Skip target (slow.next = slow.next.next):
          dummy -> 1 -> 2 -> 3 ------> 5 -> null
```

Result: `[1, 2, 3, 5]` ✅

### Implementation

```kotlin
fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    // 1. Create a dummy node to handle edge cases (like removing the head)
    val dummy = ListNode(0, null)
    dummy.next = head

    var fast: ListNode? = dummy
    var slow: ListNode? = dummy

    // 2. Move fast pointer so that there is a gap of n+1 nodes between fast and slow
    for (i in 0..n) {
        fast = fast?.next
    }

    // 3. Move both until fast reaches the end
    while (fast != null) {
        fast = fast.next
        slow = slow?.next
    }

    // 4. Skip the nth node
    slow?.next = slow?.next?.next

    return dummy.next
}
```

### Complexity

| Metric | Value  |
|--------|--------|
| **Time**  | O(N) — single pass |
| **Space** | O(1) |

---

## 🔑 Key Takeaways

1. **Dummy node** — always use a dummy/sentinel node before `head` to avoid special-casing head removal.
2. **Two-pointer gap technique** — moving `fast` `n + 1` steps ahead guarantees `slow` stops just before the target, enabling a clean relink.
3. **Single pass vs. two pass** — the two-pointer method achieves the same O(N) time but in a single traversal, avoiding an explicit length count.
4. **Edge cases** — single-node list (`n = 1`), removing the head (`n = length`), and removing the tail (`n = 1`) are all handled by the dummy + two-pointer approach.

---

## 📚 Related Problems

| Problem | LeetCode | Difficulty |
|---------|----------|------------|
| Remove Nth Node From End of List | [https://leetcode.com/problems/remove-nth-node-from-end-of-list/](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | Medium |
| Delete Node in a Linked List | [https://leetcode.com/problems/delete-node-in-a-linked-list/](https://leetcode.com/problems/delete-node-in-a-linked-list/) | Medium |
| Middle of the Linked List | [https://leetcode.com/problems/middle-of-the-linked-list/](https://leetcode.com/problems/middle-of-the-linked-list/) | Easy |
| Linked List Cycle | [https://leetcode.com/problems/linked-list-cycle/](https://leetcode.com/problems/linked-list-cycle/) | Easy |
