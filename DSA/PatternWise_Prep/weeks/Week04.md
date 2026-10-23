# 📅 Week 4 — Linked List (Single + Double + Circular)

> **Dates:** Nov 6 (Fri) – Nov 12 (Thu)
> **Pattern Focus:** Singly Linked List, Doubly Linked List, Circular Linked List, classic LL patterns (fast/slow, reversal, dummy head)
> **Reminder:** Try each problem 10–25 min WITHOUT hints. Reveal hints one at a time. Compare with your solutions in `DSA/linked_list/` only AFTER solving.

---

> 🧪 **Practice & Test Cases:** [Week04_Practice.md](../practice/Week04_Practice.md) — sample test cases for every problem + 10 complex complexity-validation cases (use [TestRunner.kt](../testrunner/TestRunner.kt) for timing).

## 🗓️ Day Plan


| Day | Date | Problems | Time |
|-----|------|----------|------|
| Fri | Nov 6 | #1–#8 (Easy single LL) | 2 hrs |
| Sat | Nov 7 | #9–#16 (Easy DLL/CLL + medium single) | 2 hrs |
| Sun | Nov 8 | #17–#24 (Medium single LL) | 2 hrs |
| Mon | Nov 9 | #25–#32 (Design + DLL medium) | 2 hrs |
| Wed | Nov 11 | #33–#37 (Hard LL) + revision | 2 hrs |
| Thu | Nov 12 | 🧪 Mock Test 4 + Review | 3 hrs |

---

## 🟢 FRIDAY, Nov 6 — Easy Single Linked List

### 1. Reverse Linked List — 🟢
**LeetCode #206** | Solution: `DSA/linked_list/single/ReverseLinkedList.kt`

**Problem:** Reverse a singly linked list.

<details><summary>💡 Hint 1</summary>
Iterative: prev/curr pointers. Recursive also possible.
</details>

<details><summary>💡 Hint 2</summary>
At each step: save next, flip curr.next to prev, move prev and curr forward.
</details>

<details><summary>💡 Hint 3</summary>
1. prev = null, curr = head
2. While curr != null: next = curr.next; curr.next = prev; prev = curr; curr = next
3. Return prev
</details>

```kotlin
fun reverseList(head: ListNode?): ListNode? {
    // TODO: prev/curr, flip pointers
    return null
}
```

---

### 2. Middle of Linked List — 🟢
**LeetCode #876** | Solution: `DSA/linked_list/single/MiddleOfLinkedList.kt`

**Problem:** Return the middle node (for even length, the second middle).

<details><summary>💡 Hint 1</summary>
Fast & Slow pointers.
</details>

<details><summary>💡 Hint 2</summary>
Slow moves 1, fast moves 2. When fast reaches the end → slow is at the middle.
</details>

<details><summary>💡 Hint 3</summary>
while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next }; return slow.
</details>

```kotlin
fun middleNode(head: ListNode?): ListNode? {
    // TODO: Fast & slow
    return null
}
```

---

### 3. Linked List Cycle — 🟢
**LeetCode #141** | Solution: `DSA/linked_list/single/LinkedListCycle.kt`

**Problem:** Does the list have a cycle?

<details><summary>💡 Hint 1</summary>
Floyd's Tortoise & Hare.
</details>

<details><summary>💡 Hint 2</summary>
Slow +1, fast +2. If they ever meet → cycle. If fast hits null → no cycle.
</details>

<details><summary>💡 Hint 3</summary>
while (fast != null && fast.next != null): slow = slow.next; fast = fast.next.next; if slow == fast → true. Return false.
</details>

```kotlin
fun hasCycle(head: ListNode?): Boolean {
    // TODO: Tortoise & hare
    return false
}
```

---

### 4. Merge Two Sorted Lists — 🟢
**LeetCode #21** | Solution: `DSA/linked_list/single/MergeTwoSortedLists.kt`

**Problem:** Merge two sorted lists into one sorted list.

<details><summary>💡 Hint 1</summary>
Dummy head + tail pointer.
</details>

<details><summary>💡 Hint 2</summary>
Compare the two current nodes; attach the smaller to tail; advance that list. Attach the remainder at the end.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy = ListNode(0), tail = dummy
2. While l1 != null && l2 != null: if l1.val <= l2.val → tail.next = l1, l1 = l1.next else tail.next = l2, l2 = l2.next; tail = tail.next
3. tail.next = l1 ?: l2; return dummy.next
</details>

```kotlin
fun mergeTwoLists(l1: ListNode?, l2: ListNode?): ListNode? {
    // TODO: Dummy head, attach smaller each time
    return null
}
```

---

### 5. Remove Duplicates from Sorted List — 🟢
**LeetCode #83** | Solution: `DSA/linked_list/single/RemoveDuplicatesFromSortedList.kt`

**Problem:** Delete duplicates so each value appears once (sorted list).

<details><summary>💡 Hint 1</summary>
Single pass — compare curr with curr.next.
</details>

<details><summary>💡 Hint 2</summary>
If values equal → skip the next node (curr.next = curr.next.next); else advance curr.
</details>

<details><summary>💡 Hint 3</summary>
var curr = head; while curr?.next != null: if curr.val == curr.next.val → curr.next = curr.next.next else curr = curr.next.
</details>

```kotlin
fun deleteDuplicates(head: ListNode?): ListNode? {
    // TODO: Skip equal neighbors
    return null
}
```

---

### 6. Linked List Binary to Integer — 🟢
**LeetCode #1290** | Solution: `DSA/linked_list/single/LinkedListBinaryToInteger.kt`

**Problem:** List represents a binary number (MSB first). Return its decimal value.

<details><summary>💡 Hint 1</summary>
As you traverse: result = result × 2 + bit.
</details>

<details><summary>💡 Hint 2</summary>
Same as building a number digit by digit in base 2.
</details>

<details><summary>💡 Hint 3</summary>
var result = 0; while (n != null) { result = result * 2 + n.`val`; n = n.next }; return result.
</details>

```kotlin
fun getDecimalValue(head: ListNode?): Int {
    // TODO: result = result * 2 + bit
    return 0
}
```

---

### 7. Intersection of Two Linked Lists — 🟢
**LeetCode #160** | Solution: `DSA/linked_list/single/IntersectionOfTwoLinkedLists.kt`

**Problem:** Find the node where two lists merge (or null).

<details><summary>💡 Hint 1</summary>
Two pointers switching lists — or length difference alignment.
</details>

<details><summary>💡 Hint 2</summary>
Pointer A walks list A then list B; pointer B walks list B then list A. Both travel the same total distance → meet at the intersection.
</details>

<details><summary>💡 Hint 3</summary>
1. a = headA, b = headB
2. While a !== b: a = if (a == null) headB else a.next; b = if (b == null) headA else b.next
3. Return a
</details>

```kotlin
fun getIntersectionNode(headA: ListNode?, headB: ListNode?): ListNode? {
    // TODO: Switch lists at the end
    return null
}
```

---

### 8. Delete Node in Linked List — 🟢
**LeetCode #237** | Solution: `DSA/linked_list/single/DeleteNodeInLinkedList.kt`

**Problem:** Delete a node given only access to THAT node (not the head).

<details><summary>💡 Hint 1</summary>
You can't delete it directly — copy the next node's value into it, then skip the next node.
</details>

<details><summary>💡 Hint 2</summary>
node.val = node.next.val; node.next = node.next.next.
</details>

<details><summary>💡 Hint 3</summary>
Two lines: copy value, bypass next.
</details>

```kotlin
fun deleteNode(node: ListNode?) {
    // TODO: Copy next value, skip next
}
```

---

## 🟢 SATURDAY, Nov 7 — DLL/CLL Basics + Medium Single

### 9. Reverse Doubly Linked List — 🟢
**Solution:** `DSA/linked_list/double/ReverseDoublyLinkedList.kt`

**Problem:** Reverse a doubly linked list.

<details><summary>💡 Hint 1</summary>
Swap prev and next for every node.
</details>

<details><summary>💡 Hint 2</summary>
Traverse swapping pointers; the last node visited becomes the new head.
</details>

<details><summary>💡 Hint 3</summary>
For each node: temp = node.prev; node.prev = node.next; node.next = temp; move to node.prev (which is old next). New head = last node.
</details>

```kotlin
fun reverseDoubly(head: DListNode?): DListNode? {
    // TODO: Swap prev/next at each node
    return null
}
```

---

### 10. Delete Node in DLL — 🟢
**Solution:** `DSA/linked_list/double/DeleteNodeInDoublyLinkedList.kt`

**Problem:** Delete a given node from a doubly linked list.

<details><summary>💡 Hint 1</summary>
Fix the neighbors' pointers around the node.
</details>

<details><summary>💡 Hint 2</summary>
node.prev.next = node.next; node.next.prev = node.prev. Handle null head/tail edges.
</details>

<details><summary>💡 Hint 3</summary>
1. If node.prev != null → node.prev.next = node.next else head = node.next
2. If node.next != null → node.next.prev = node.prev
</details>

```kotlin
fun deleteNode(head: DListNode?, node: DListNode?): DListNode? {
    // TODO: Rewire prev/next, handle head
    return null
}
```

---

### 11. Insert Node in DLL — 🟢
**Solution:** `DSA/linked_list/double/InsertNodeInDoublyLinkedList.kt`

**Problem:** Insert a node at a given position in a DLL.

<details><summary>💡 Hint 1</summary>
Traverse to position, wire 4 pointers.
</details>

<details><summary>💡 Hint 2</summary>
new.prev = curr; new.next = curr.next; curr.next.prev = new; curr.next = new.
</details>

<details><summary>💡 Hint 3</summary>
Handle insert at head (position 0) and tail (curr.next == null) as special cases.
</details>

```kotlin
fun insertNode(head: DListNode?, node: DListNode?, position: Int): DListNode? {
    // TODO: Wire 4 pointers, handle edges
    return null
}
```

---

### 12. Remove Duplicates from Sorted DLL — 🟢
**Solution:** `DSA/linked_list/double/RemoveDuplicatesFromSortedDLL.kt`

**Problem:** Remove duplicates from a sorted DLL.

<details><summary>💡 Hint 1</summary>
Same as singly version — skip equal neighbors.
</details>

<details><summary>💡 Hint 2</summary>
If curr.val == curr.next.val → bypass curr.next (fix prev pointer too).
</details>

<details><summary>💡 Hint 3</summary>
While curr.next != null: if equal → curr.next = curr.next.next; if curr.next != null → curr.next.prev = curr; else curr = curr.next.
</details>

```kotlin
fun removeDuplicates(head: DListNode?): DListNode? {
    // TODO: Bypass duplicates, fix prev pointers
    return null
}
```

---

### 13. Circular Linked List — 🟢
**Solution:** `DSA/linked_list/circular/CircularLinkedList.kt`

**Problem:** Implement insert/delete/traverse for a circular linked list.

<details><summary>💡 Hint 1</summary>
Tail.next points back to head.
</details>

<details><summary>💡 Hint 2</summary>
Keep a tail pointer — head = tail.next. Insert at head/tail is O(1).
</details>

<details><summary>💡 Hint 3</summary>
1. insertAtEnd: new.next = head; tail.next = new; tail = new
2. traverse: start at head, loop until back to head
</details>

```kotlin
class CircularLinkedList {
    // TODO: tail pointer, head = tail.next
    fun insertAtEnd(value: Int) {}
    fun delete(value: Int) {}
}
```

---

### 14. Remove Nth from End — 🟡
**LeetCode #19** | Solution: `DSA/linked_list/single/RemoveNthNodeFromLast.kt`

**Problem:** Remove the nth node from the end in one pass.

<details><summary>💡 Hint 1</summary>
Two pointers with a GAP of n.
</details>

<details><summary>💡 Hint 2</summary>
Advance fast n+1 steps (from dummy), then move both until fast is null. slow.next is the node to remove.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy → head. fast = dummy, slow = dummy
2. Advance fast n+1 times. While fast != null: fast = fast.next, slow = slow.next
3. slow.next = slow.next.next; return dummy.next
</details>

```kotlin
fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
    // TODO: Dummy + gap of n
    return null
}
```

---

### 15. Remove Duplicates II — 🟡
**LeetCode #82** | Solution: `DSA/linked_list/single/RemoveDuplicatesFromSortedListII.kt`

**Problem:** Delete ALL nodes with duplicate values (keep only distinct values).

<details><summary>💡 Hint 1</summary>
Dummy head + skip runs of duplicates.
</details>

<details><summary>💡 Hint 2</summary>
While curr.next and curr.next.next have equal values → advance curr.next past the entire run.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy → head, curr = dummy
2. While curr.next != null && curr.next.next != null:
3. If equal values → skip all nodes with that value; else curr = curr.next
</details>

```kotlin
fun deleteDuplicates(head: ListNode?): ListNode? {
    // TODO: Dummy head, skip duplicate runs
    return null
}
```

---

### 16. Reorder List — 🟡
**LeetCode #143** | Solution: `DSA/linked_list/single/ReorderList.kt`

**Problem:** Reorder L0→L1→…→Ln as L0→Ln→L1→Ln-1→…

<details><summary>💡 Hint 1</summary>
Three steps: find middle, reverse second half, merge alternately.
</details>

<details><summary>💡 Hint 2</summary>
Fast/slow for middle. Reverse from middle. Then weave nodes one from each half.
</details>

<details><summary>💡 Hint 3</summary>
1. mid = middle(head); second = reverse(mid.next); mid.next = null
2. Merge: first = head; while second != null: save nexts; first.next = second; second.next = savedFirstNext; advance both
</details>

```kotlin
fun reorderList(head: ListNode?) {
    // TODO: Middle → reverse → merge
}
```

---

## 🟡 SUNDAY, Nov 8 — Medium Single Linked List

### 17. Swap Nodes in Pairs — 🟡
**LeetCode #24** | Solution: `DSA/linked_list/single/SwapNodesInPairs.kt`

**Problem:** Swap every two adjacent nodes (do not modify values).

<details><summary>💡 Hint 1</summary>
Dummy head; rewire 3 pointers per pair.
</details>

<details><summary>💡 Hint 2</summary>
prev → a → b → rest becomes prev → b → a → rest. Then prev = a.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy → head, prev = dummy
2. While prev.next != null && prev.next.next != null:
3. a = prev.next, b = a.next; a.next = b.next; b.next = a; prev.next = b; prev = a
</details>

```kotlin
fun swapPairs(head: ListNode?): ListNode? {
    // TODO: Dummy, rewire each pair
    return null
}
```

---

### 18. Rotate Linked List — 🟡
**LeetCode #61** | Solution: `DSA/linked_list/single/RotateLinkedList.kt`

**Problem:** Rotate the list right by k places.

<details><summary>💡 Hint 1</summary>
k = k % length. Make it circular, then break at the right spot.
</details>

<details><summary>💡 Hint 2</summary>
Connect tail to head. New tail is at (length - k - 1) steps from head. New head is the next node.
</details>

<details><summary>💡 Hint 3</summary>
1. Find length and tail. k %= length; if k == 0 → return head
2. tail.next = head; walk (length - k - 1) steps → newTail; head = newTail.next; newTail.next = null
</details>

```kotlin
fun rotateRight(head: ListNode?, k: Int): ListNode? {
    // TODO: Close the ring, break at length - k
    return null
}
```

---

### 19. Partition List — 🟡
**LeetCode #86** | Solution: `DSA/linked_list/single/PatiotionList.kt`

**Problem:** Partition around x: all nodes < x before all nodes >= x, preserving relative order.

<details><summary>💡 Hint 1</summary>
Two dummy lists (before/after), then join.
</details>

<details><summary>💡 Hint 2</summary>
Walk the list appending to the appropriate list. End: beforeTail.next = afterHead.next; afterTail.next = null.
</details>

<details><summary>💡 Hint 3</summary>
1. beforeDummy, afterDummy, two tails
2. For each node: if val < x → append to before list else after list
3. Join and terminate
</details>

```kotlin
fun partition(head: ListNode?, x: Int): ListNode? {
    // TODO: Two dummy lists, join
    return null
}
```

---

### 20. Linked List Cycle II — 🟡
**LeetCode #142** | Solution: `DSA/linked_list/single/LinkedListCycleII.kt`

**Problem:** Return the node where the cycle begins (or null).

<details><summary>💡 Hint 1</summary>
Floyd's — after meeting, reset one pointer to head; move both +1.
</details>

<details><summary>💡 Hint 2</summary>
The meeting point and head are equidistant from the cycle start.
</details>

<details><summary>💡 Hint 3</summary>
1. Detect meeting (slow/fast). If no cycle → null
2. slow = head; while slow != fast: slow = slow.next, fast = fast.next
3. Return slow
</details>

```kotlin
fun detectCycle(head: ListNode?): ListNode? {
    // TODO: Floyd's + reset to head
    return null
}
```

---

### 21. Palindrome Linked List — 🟡
**LeetCode #234** | Solution: `DSA/linked_list/single/PalindromeLinkedList.kt`

**Problem:** Is the list a palindrome? O(1) space.

<details><summary>💡 Hint 1</summary>
Find middle, reverse second half, compare halves.
</details>

<details><summary>💡 Hint 2</summary>
Fast/slow middle → reverse from mid → compare node values pairwise → (optionally restore).
</details>

<details><summary>💡 Hint 3</summary>
1. second = reverse(middle(head))
2. While second != null: if first.val != second.val → false; advance both
3. Return true
</details>

```kotlin
fun isPalindrome(head: ListNode?): Boolean {
    // TODO: Middle → reverse → compare
    return false
}
```

---

### 22. Add Two Numbers — 🟡
**LeetCode #2** | Solution: `DSA/linked_list/single/AddTwoNumbers.kt`

**Problem:** Two numbers stored in reverse order; return their sum as a list.

<details><summary>💡 Hint 1</summary>
Elementary addition with carry. Dummy head.
</details>

<details><summary>💡 Hint 2</summary>
Loop while l1 or l2 or carry != 0. digit = (v1 + v2 + carry) % 10; carry = / 10.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy, tail, carry = 0
2. While (l1 != null || l2 != null || carry > 0): sum = (l1?.val ?: 0) + (l2?.val ?: 0) + carry
3. tail.next = ListNode(sum % 10); carry = sum / 10; advance
</details>

```kotlin
fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? {
    // TODO: Digit-by-digit with carry
    return null
}
```

---

### 23. Copy List with Random Pointer — 🟡
**LeetCode #138** | Solution: `DSA/linked_list/single/CopyListWithRandomPointer.kt`

**Problem:** Deep copy a list where each node also has a random pointer.

<details><summary>💡 Hint 1</summary>
Interleave: original → copy → original → copy …
</details>

<details><summary>💡 Hint 2</summary>
Pass 1: insert copies after originals. Pass 2: set copy.random = original.random.next. Pass 3: split lists.
</details>

<details><summary>💡 Hint 3</summary>
1. For each node: copy = Node(node.val); copy.next = node.next; node.next = copy; node = copy.next
2. For each original: original.next.random = original.random?.next
3. Unweave into two lists
</details>

```kotlin
fun copyRandomList(head: Node?): Node? {
    // TODO: Interleave, wire randoms, split
    return null
}
```

---

### 24. Convert Sorted List to BST — 🟡
**LeetCode #109** | Solution: `DSA/linked_list/single/ConvertSortedListToBinarySearchTree.kt`

**Problem:** Convert a sorted list to a height-balanced BST.

<details><summary>💡 Hint 1</summary>
Middle element = root. Recurse on left/right halves.
</details>

<details><summary>💡 Hint 2</summary>
Find middle (fast/slow), disconnect left half from middle, root = middle, recurse.
</details>

<details><summary>💡 Hint 3</summary>
1. If head == null → null. If head.next == null → TreeNode(head.val)
2. mid = middle (keep prev to cut: prev.next = null)
3. root = TreeNode(mid.val); root.left = recurse(head); root.right = recurse(mid.next)
</details>

```kotlin
fun sortedListToBST(head: ListNode?): TreeNode? {
    // TODO: Middle as root, recurse halves
    return null
}
```

---

## 🟡 MONDAY, Nov 9 — Design + DLL Medium

### 25. Design Hash Set — 🟡
**LeetCode #705** | Solution: `DSA/linked_list/single/DesignHashSet.kt`

**Problem:** Design a HashSet without built-in libraries.

<details><summary>💡 Hint 1</summary>
Array of buckets + chaining (each bucket = list).
</details>

<details><summary>💡 Hint 2</summary>
hash(key) = key % capacity. Bucket holds keys. add/remove/contains scan the bucket.
</details>

<details><summary>💡 Hint 3</summary>
1. buckets = Array(capacity) { mutableListOf<Int>() }
2. add: if !bucket.contains(key) → bucket.add(key)
3. remove: bucket.remove(key); contains: bucket.contains(key)
</details>

```kotlin
class MyHashSet() {
    // TODO: Buckets + chaining
    fun add(key: Int) {}
    fun remove(key: Int) {}
    fun contains(key: Int): Boolean = false
}
```

---

### 26. My Linked List (Design) — 🟡
**LeetCode #707** | Solution: `DSA/linked_list/single/MyLinkedList.kt`

**Problem:** Design a linked list with get, addAtHead, addAtTail, addAtIndex, deleteAtIndex.

<details><summary>💡 Hint 1</summary>
Dummy head simplifies all index operations.
</details>

<details><summary>💡 Hint 2</summary>
addAtIndex(i): walk to node BEFORE index i, insert. deleteAtIndex(i): walk to node before, bypass.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy = Node(0), size = 0
2. get(i): walk i+1 steps from dummy, return .val
3. add/delete: walk to index-1 node (from dummy), rewire, adjust size
</details>

```kotlin
class MyLinkedList() {
    // TODO: Dummy head + size
    fun get(index: Int): Int = -1
    fun addAtHead(value: Int) {}
    fun addAtTail(value: Int) {}
    fun addAtIndex(index: Int, value: Int) {}
    fun deleteAtIndex(index: Int) {}
}
```

---

### 27. LRU Cache — 🟡
**LeetCode #146** | Solution: `DSA/linked_list/double/LeastRecentlyUsedLRU.kt`

**Problem:** Design a cache with get/put in O(1), evicting the least recently used.

<details><summary>💡 Hint 1</summary>
HashMap + doubly linked list. Map: key → node. List: recency order.
</details>

<details><summary>💡 Hint 2</summary>
Most recent at head, least at tail. get: move node to head. put: insert at head; if over capacity → remove tail node and its map entry.
</details>

<details><summary>💡 Hint 3</summary>
1. Node: key, value, prev, next. Dummy head & tail.
2. removeNode(node), addToFront(node) helpers
3. get: map[key]?.let { remove(it); addFront(it); it.value } ?: -1
4. put: if exists → update + move front; else add; evict tail.prev if size > cap
</details>

```kotlin
class LRUCache(capacity: Int) {
    // TODO: HashMap + DLL with dummy head/tail
    fun get(key: Int): Int = -1
    fun put(key: Int, value: Int) {}
}
```

---

### 28. Flatten Multilevel DLL — 🟡
**LeetCode #430** | Solution: `DSA/linked_list/double/FlatternDoubleLLMultiLevel.kt`

**Problem:** DLL where nodes may have a child list — flatten to a single-level DLL (depth-first).

<details><summary>💡 Hint 1</summary>
DFS: when a node has a child, flatten the child chain and splice it in.
</details>

<details><summary>💡 Hint 2</summary>
When node.child != null: save next; connect node → child; find child's tail; tail → savedNext. Recurse.
</details>

<details><summary>💡 Hint 3</summary>
1. For each node: if child != null:
2. next = node.next; childTail = flatten(child's chain end)
3. node.next = child; child.prev = node; childTail.next = next; next?.prev = childTail
4. node.child = null; continue from next
</details>

```kotlin
fun flatten(head: DListNode?): DListNode? {
    // TODO: Splice child chains in place
    return null
}
```

---

### 29. Find Pairs with Given Sum (Sorted DLL) — 🟡
**LeetCode #1670-style** | Solution: `DSA/linked_list/double/FindPairsWithGivenSum.kt`

**Problem:** In a sorted DLL, count pairs with a given sum.

<details><summary>💡 Hint 1</summary>
Two Pointer — left at head, right at tail.
</details>

<details><summary>💡 Hint 2</summary>
sum > target → right = right.prev; sum < target → left = left.next; equal → count++, move both.
</details>

<details><summary>💡 Hint 3</summary>
While left != right && left.prev != right: compare and move pointers accordingly.
</details>

```kotlin
fun countPairs(head: DListNode?, target: Int): Int {
    // TODO: Head/tail pointers on DLL
    return 0
}
```

---

### 30. Josephus Problem — 🟡
**Solution:** `DSA/linked_list/circular/JosephusProblem.kt`

**Problem:** n people in a circle; every kth is eliminated. Find the survivor (or elimination order).

<details><summary>💡 Hint 1</summary>
Simulate with a circular list, or use the recurrence.
</details>

<details><summary>💡 Hint 2</summary>
Recurrence: J(1) = 0; J(n) = (J(n-1) + k) % n. Answer (0-based) → +1 for 1-based.
</details>

<details><summary>💡 Hint 3</summary>
var survivor = 0; for i in 2..n: survivor = (survivor + k) % i; return survivor + 1.
</details>

```kotlin
fun josephus(n: Int, k: Int): Int {
    // TODO: Recurrence or circular simulation
    return 0
}
```

---

### 31. Sorted Insert in Circular LL — 🟡
**Solution:** `DSA/linked_list/circular/SortedInsertInCircularLinkedList.kt`

**Problem:** Insert a value into a sorted circular list, keeping it sorted.

<details><summary>💡 Hint 1</summary>
Three cases: insert before head, insert at end (max), insert in middle.
</details>

<details><summary>💡 Hint 2</summary>
Walk while curr.next != head && curr.next.val < value. Insert after curr. Handle new value < head (new head) and > tail.
</details>

<details><summary>💡 Hint 3</summary>
1. If list empty → node.next = node, return node
2. If value < head.val → insert before head (find tail, tail.next = node, node.next = head, node = head)
3. Else walk and insert after the last node smaller than value
</details>

```kotlin
fun sortedInsert(head: ListNode?, value: Int): ListNode? {
    // TODO: 3 cases: before head, middle, after tail
    return null
}
```

---

### 32. Split Circular LL — 🟡
**Solution:** `DSA/linked_list/circular/SplitCircularLinkedList.kt`

**Problem:** Split a circular list into two halves (two circular lists).

<details><summary>💡 Hint 1</summary>
Find middle (fast/slow adapted for circular) and tail.
</details>

<details><summary>💡 Hint 2</summary>
head1 = head. head2 = middle.next. tail.next = head2; middle.next = head1.
</details>

<details><summary>💡 Hint 3</summary>
1. slow/fast until fast reaches tail (fast.next != head && fast.next.next != head)
2. head2 = slow.next; slow.next = head (close first)
3. Find tail of original; tail.next = head2 (close second)
</details>

```kotlin
fun splitCircularList(head: ListNode?): Pair<ListNode?, ListNode?> {
    // TODO: Middle + tail, close both circles
    return Pair(null, null)
}
```

---

## 🟢 WEDNESDAY, Nov 11 — Hard Linked List + Revision

### 33. Reverse Linked List II — 🟡
**LeetCode #92** | Solution: `DSA/linked_list/single/ReverseLinkedListII.kt`

**Problem:** Reverse nodes from position left to right (1-based).

<details><summary>💡 Hint 1</summary>
Walk to node before left; reverse the segment like Reverse Linked List; reconnect.
</details>

<details><summary>💡 Hint 2</summary>
prev = node before left. Then standard reversal for (right - left + 1) nodes. Fix boundary connections.
</details>

<details><summary>💡 Hint 3</summary>
1. dummy → head; walk prev to left-1
2. curr = prev.next; reverse (right-left) times using standard prev/curr reversal
3. Reconnect: prev.next.next = curr.next... (wire carefully)
</details>

```kotlin
fun reverseBetween(head: ListNode?, left: Int, right: Int): ListNode? {
    // TODO: Reverse a segment, reconnect
    return null
}
```

---

### 34. LFU Cache — 🔴
**LeetCode #460** | Solution: `DSA/linked_list/double/LeastFrequentlyUsedLFU.kt`

**Problem:** Cache evicting the LEAST frequently used (LRU tiebreak) in O(1).

<details><summary>💡 Hint 1</summary>
HashMaps: key → node, freq → DLL of nodes at that frequency. Track minFreq.
</details>

<details><summary>💡 Hint 2</summary>
On access: move node to freq+1 list; if minFreq list becomes empty → minFreq++. Evict from tail of minFreq list.
</details>

<details><summary>💡 Hint 3</summary>
1. freqMap: freq → DLL (head = most recent)
2. get: move node up a freq bucket, update minFreq
3. put: insert at freq 1; evict tail of freqMap[minFreq] when full
</details>

```kotlin
class LFUCache(capacity: Int) {
    // TODO: freqMap of DLLs + keyMap + minFreq
    fun get(key: Int): Int = -1
    fun put(key: Int, value: Int) {}
}
```

---

### 35. Merge K Sorted Lists — 🔴
**LeetCode #23** | Solution: `DSA/linked_list/single/MergeKSortedLists.kt`

**Problem:** Merge k sorted lists into one.

<details><summary>💡 Hint 1</summary>
Min-heap of the current heads of all lists.
</details>

<details><summary>💡 Hint 2</summary>
Push all heads. Pop smallest → append to result; push its next. Repeat.
</details>

<details><summary>💡 Hint 3</summary>
1. heap = PriorityQueue<ListNode>(compareBy { it.`val` })
2. Push all non-null heads. dummy/tail
3. While heap not empty: node = poll; tail.next = node; tail = node; if node.next != null → push(node.next)
</details>

```kotlin
fun mergeKLists(lists: Array<ListNode?>): ListNode? {
    // TODO: Min-heap of heads
    return null
}
```

---

### 36. Sort List — 🔴
**LeetCode #148** | Solution: `DSA/linked_list/single/SortList.kt`

**Problem:** Sort a linked list in O(n log n).

<details><summary>💡 Hint 1</summary>
Merge Sort — split at middle, sort halves, merge.
</details>

<details><summary>💡 Hint 2</summary>
Fast/slow to split (cut the link!). Recursively sort both halves. Merge with the dummy-head merge.
</details>

<details><summary>💡 Hint 3</summary>
1. If head == null || head.next == null → head
2. Split into two halves (prev.next = null to cut)
3. return merge(sortList(h1), sortList(h2))
</details>

```kotlin
fun sortList(head: ListNode?): ListNode? {
    // TODO: Split → sort → merge
    return null
}
```

---

### 37. Reverse in K-Group — 🔴
**LeetCode #25** | Solution: `DSA/linked_list/single/ReverseLinkedListKGroup.kt`

**Problem:** Reverse nodes in groups of k; if the last group is short, leave it as-is.

<details><summary>💡 Hint 1</summary>
Count k nodes ahead; if fewer than k remain → stop. Reverse the group; connect to the next group's result.
</details>

<details><summary>💡 Hint 2</summary>
groupNext = node after the k-group. Reverse the group normally. groupTail (old head).next = reverseKGroup(groupNext, k).
</details>

<details><summary>💡 Hint 3</summary>
1. Check k nodes exist from head; if not → return head
2. Reverse k nodes (standard reversal), remember oldHead
3. oldHead.next = reverseKGroup(curr, k); return newHead
</details>

```kotlin
fun reverseKGroup(head: ListNode?, k: Int): ListNode? {
    // TODO: Check k, reverse, recurse on rest
    return null
}
```

---

## 🧪 THURSDAY, Nov 12 — Mock Test 4 (3 hrs)

**Part 1 — Timed Test (2 hrs, no hints for first 30 min):**

| # | Problem | Pattern | Difficulty | Time Limit |
|---|---------|---------|-----------|-----------|
| 1 | Merge Two Sorted Lists | Dummy Head | 🟢 | 15 min |
| 2 | Linked List Cycle II | Floyd's | 🟡 | 20 min |
| 3 | Reorder List | Middle+Reverse+Merge | 🟡 | 25 min |
| 4 | LRU Cache | HashMap + DLL | 🟡 | 30 min |
| 5 | Reverse in K-Group | Hard Reversal | 🔴 | 35 min |

**Part 2 — Review (1 hr):** Score, log in [Mock_Test_Log.md](../Mock_Test_Log.md), re-solve failures, update tracker.

---

## ✅ Week 4 Completion Checklist

- [ ] Mon: 8 easy single LL problems
- [ ] Tue: 8 DLL/CLL + medium single
- [ ] Wed: 8 medium single LL
- [ ] Thu: 8 design + DLL medium
- [ ] Sat: 5 hard LL problems
- [ ] Sun: Mock Test 4 completed & logged
- [ ] Progress Tracker updated
