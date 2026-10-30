package testrunner

/**
 * Week04Tests.kt — Week 4: Linked List
 * Test class + solution stubs. Implement the stubs at the bottom as you solve.
 *
 * Test numbering (W4 #1 … #37) matches weeks/Week04.md; the input/expected pairs come
 * from practice/Week04_Practice.md (Part A) — every problem has all 3 cases wired up —
 * and the 10 timed cases (W4-B1 … W4-B10) come from its Part B. Unimplemented stubs use
 * TODO(), so the runner reports them as [SKIP ⏭️] instead of failing.
 *
 * Naming: LC reuses signatures across the week, so variants are suffixed to keep every
 * top-level name unique (deleteDuplicatesLL / deleteDuplicatesII / removeDuplicatesDLL,
 * deleteNodeLL / deleteNodeDLL, W4LFUCache). #26 case 3 is restated as a self-contained
 * scenario because the doc chained it onto the case-1 state.
 */

class Week04Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 4 — Linked List ━━━━━━━")
        // Mon — singly linked basics (#1–#8)
        reverseList(); middleNode(); hasCycle(); mergeTwoLists(); deleteDuplicatesLL()
        getDecimalValue(); getIntersectionNode(); deleteNodeLL()
        // Tue — DLL / CLL + singly medium (#9–#16)
        reverseDoubly(); deleteNodeDLL(); insertNodeDLL(); removeDuplicatesDLL(); circularLinkedList()
        removeNthFromEnd(); deleteDuplicatesII(); reorderList()
        // Wed — medium singly (#17–#24)
        swapPairs(); rotateRight(); partition(); detectCycle(); isPalindromeLL()
        addTwoNumbers(); copyRandomList(); sortedListToBST()
        // Thu — design + hard (#25–#37)
        myHashSet(); myLinkedList(); lruCache(); flattenMultiLevel(); countPairs(); josephus()
        sortedInsert(); splitCircularList(); reverseBetween(); lfuCache()
        mergeKLists(); sortList(); reverseKGroup()
        // Complexity validation (Part B, cases 1–10) — uncomment to time-check:
        // complexityAll()
    }

    fun complexityAll() {
        reverseListBig(); mergeKListsBig(); sortListBig(); lruCacheBig(); isPalindromeLLBig()
        addTwoNumbersBig(); reverseKGroupBig(); detectCycleBig(); removeNthFromEndBig(); copyRandomListBig()
    }

    // ── test helpers ────────────────────────────────────────────────────────────────
    private fun buildDLL(vararg values: Int): DListNode? {
        var head: DListNode? = null; var tail: DListNode? = null
        for (v in values) {
            val n = DListNode(v)
            if (head == null) { head = n; tail = n } else { tail!!.next = n; n.prev = tail; tail = n }
        }
        return head
    }

    private fun dllValues(head: DListNode?): List<Int> {
        val out = mutableListOf<Int>(); var c = head
        while (c != null) { out.add(c.value); c = c.next }
        return out
    }

    private fun dllTail(head: DListNode?): DListNode? { var t = head ?: return null; while (t.next != null) t = t.next!!; return t }
    private fun dllFind(head: DListNode?, value: Int): DListNode? { var c = head; while (c != null && c.value != value) c = c.next; return c }

    private fun buildMList(vararg values: Int): MNode? {
        var head: MNode? = null; var tail: MNode? = null
        for (v in values) {
            val n = MNode(v)
            if (head == null) { head = n; tail = n } else { tail!!.next = n; n.prev = tail; tail = n }
        }
        return head
    }

    private fun mValues(head: MNode?): List<Int> { val out = mutableListOf<Int>(); var c = head; while (c != null) { out.add(c.value); c = c.next }; return out }

    /** Traverse a circular list exactly once (the cap guards against a broken stub looping forever). */
    private fun cllValues(head: ListNode?): List<Int> {
        if (head == null) return emptyList()
        val out = mutableListOf<Int>(); var c: ListNode? = head
        do { out.add(c!!.`val`); c = c.next } while (c !== head && c != null && out.size < 10_000)
        return out
    }

    /** Build a circular singly linked list and return its head. */
    private fun cllOf(vararg values: Int): ListNode? {
        val head = toList(*values) ?: return null
        var t = head; while (t.next != null) t = t.next!!; t.next = head
        return head
    }

    private fun inorder(root: TreeNode?, out: MutableList<Int>) {
        if (root == null) return
        inorder(root.left, out); out.add(root.`val`); inorder(root.right, out)
    }

    /** Build 1→2→…→n and return the head (used by the Part B stress cases). */
    private fun rangeList(n: Int): ListNode? {
        val dummy = ListNode(0); var tail = dummy
        for (i in 1..n) { tail.next = ListNode(i); tail = tail.next!! }
        return dummy.next
    }

    /** Value of the node at zero-based [index], or null past the end. */
    private fun valueAt(head: ListNode?, index: Int): Int? {
        var c = head; var i = 0
        while (c != null) { if (i == index) return c.`val`; c = c.next; i++ }
        return null
    }

    /** Walk a non-circular list and return its size (keeps a broken stub from looping forever). */
    private fun sizeOf(head: ListNode?): Int {
        var c = head; var n = 0
        while (c != null && n < 5_000_000) { n++; c = c.next }
        return n
    }

    private fun treeHeight(root: TreeNode?): Int = if (root == null) 0 else 1 + maxOf(treeHeight(root.left), treeHeight(root.right))


    // ── MON — singly linked basics (#1–#8) ──────────────────────────────────────────
    fun reverseList() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4,5]	[5,4,3,2,1]	Basic
         * 2	[1,2]	[2,1]	Two nodes
         * 3	null	[]	Empty
         */
        TestRunner.check("W4 #1 Reverse Linked List, basic", block = { toValues(reverseList(toList(1, 2, 3, 4, 5))) }, verify = { it == listOf(5, 4, 3, 2, 1) })
        TestRunner.check("W4 #1b Reverse Linked List, two nodes", block = { toValues(reverseList(toList(1, 2))) }, verify = { it == listOf(2, 1) })
        TestRunner.check("W4 #1c Reverse Linked List, empty", block = { toValues(reverseList(null)) }, verify = { it == emptyList<Int>() })
    }
    fun middleNode() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4,5]	[3,4,5]	Odd — exact middle
         * 2	[1,2,3,4,5,6]	[4,5,6]	Even — second middle
         * 3	[1]	[1]	Single node
         */
        TestRunner.check("W4 #2 Middle of Linked List, odd", block = { toValues(middleNode(toList(1, 2, 3, 4, 5))) }, verify = { it == listOf(3, 4, 5) })
        TestRunner.check("W4 #2b Middle of Linked List, even → second middle", block = { toValues(middleNode(toList(1, 2, 3, 4, 5, 6))) }, verify = { it == listOf(4, 5, 6) })
        TestRunner.check("W4 #2c Middle of Linked List, single", block = { toValues(middleNode(toList(1))) }, verify = { it == listOf(1) })
    }
    fun hasCycle() {
        /**
         * #	Input	Expected	Tests
         * 1	[3,2,0,-4], tail → index 1	true	Cycle at index 1
         * 2	[1,2], tail → head	true	Full cycle
         * 3	[1] (no cycle)	false	No cycle
         */
        TestRunner.check("W4 #3 Linked List Cycle, cycle at index 1", block = { val h = toList(3, 2, 0, -4)!!; var t = h; while (t.next != null) t = t.next!!; t.next = h.next; hasCycle(h) }, verify = { it })
        TestRunner.check("W4 #3b Linked List Cycle, full cycle", block = { val h = toList(1, 2)!!; var t = h; while (t.next != null) t = t.next!!; t.next = h; hasCycle(h) }, verify = { it })
        TestRunner.check("W4 #3c Linked List Cycle, no cycle", block = { hasCycle(toList(1)) }, verify = { !it })
    }
    fun mergeTwoLists() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,4] + [1,3,4]	[1,1,2,3,4,4]	Basic
         * 2	null + null	[]	Both empty
         * 3	null + [0]	[0]	One empty
         */
        TestRunner.check("W4 #4 Merge Two Sorted Lists, basic", block = { toValues(mergeTwoLists(toList(1, 2, 4), toList(1, 3, 4))) }, verify = { it == listOf(1, 1, 2, 3, 4, 4) })
        TestRunner.check("W4 #4b Merge Two Sorted Lists, both empty", block = { toValues(mergeTwoLists(null, null)) }, verify = { it == emptyList<Int>() })
        TestRunner.check("W4 #4c Merge Two Sorted Lists, one empty", block = { toValues(mergeTwoLists(null, toList(0))) }, verify = { it == listOf(0) })
    }
    fun deleteDuplicatesLL() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,1,2]	[1,2]	Basic
         * 2	[1,1,2,3,3]	[1,2,3]	Multiple runs
         * 3	null	[]	Empty
         */
        TestRunner.check("W4 #5 Remove Duplicates (Sorted LL), basic", block = { toValues(deleteDuplicatesLL(toList(1, 1, 2))) }, verify = { it == listOf(1, 2) })
        TestRunner.check("W4 #5b Remove Duplicates (Sorted LL), multiple runs", block = { toValues(deleteDuplicatesLL(toList(1, 1, 2, 3, 3))) }, verify = { it == listOf(1, 2, 3) })
        TestRunner.check("W4 #5c Remove Duplicates (Sorted LL), empty", block = { toValues(deleteDuplicatesLL(null)) }, verify = { it == emptyList<Int>() })
    }
    fun getDecimalValue() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,0,1]	5	Basic
         * 2	[0]	0	Zero
         * 3	[1,1,1]	7	All ones
         */
        TestRunner.check("W4 #6 Linked List Binary to Integer, basic", block = { getDecimalValue(toList(1, 0, 1)) }, verify = { it == 5 })
        TestRunner.check("W4 #6b Linked List Binary to Integer, zero", block = { getDecimalValue(toList(0)) }, verify = { it == 0 })
        TestRunner.check("W4 #6c Linked List Binary to Integer, all ones", block = { getDecimalValue(toList(1, 1, 1)) }, verify = { it == 7 })
    }
    fun getIntersectionNode() {
        /**
         * #	Input	Expected	Tests
         * 1	[4,1] + [5,6,1] → shared [8,4,5]	8 (shared val)	Classic
         * 2	[2,6,4] + [1,5]	null	Disjoint
         * 3	[1] + [1] (same node)	1 (shared val)	Same node
         */
        TestRunner.check("W4 #7 Intersection of Two Linked Lists, classic", block = {
            val shared = toList(8, 4, 5)!!
            val a = toList(4, 1)!!; var t = a; while (t.next != null) t = t.next!!; t.next = shared
            val b = toList(5, 6, 1)!!; var u = b; while (u.next != null) u = u.next!!; u.next = shared
            getIntersectionNode(a, b)?.`val`
        }, verify = { it == 8 })
        TestRunner.check("W4 #7b Intersection of Two Linked Lists, disjoint", block = { getIntersectionNode(toList(2, 6, 4), toList(1, 5)) }, verify = { it == null })
        TestRunner.check("W4 #7c Intersection of Two Linked Lists, same node", block = { val n = toList(1); getIntersectionNode(n, n)?.`val` }, verify = { it == 1 })
    }
    fun deleteNodeLL() {
        /**
         * #	Input	Expected	Tests
         * 1	[4,5,1,9], delete 5	[4,1,9]	Middle
         * 2	[4,5,1,9], delete 1	[4,5,9]	Later node
         * 3	[1,2], delete head	[2]	Near tail
         */
        TestRunner.check("W4 #8 Delete Node in LL, middle", block = { val h = toList(4, 5, 1, 9)!!; deleteNodeLL(h.next!!); toValues(h) }, verify = { it == listOf(4, 1, 9) })
        TestRunner.check("W4 #8b Delete Node in LL, later node", block = { val h = toList(4, 5, 1, 9)!!; deleteNodeLL(h.next!!.next!!); toValues(h) }, verify = { it == listOf(4, 5, 9) })
        TestRunner.check("W4 #8c Delete Node in LL, near tail", block = { val h = toList(1, 2)!!; deleteNodeLL(h); toValues(h) }, verify = { it == listOf(2) })
    }

    // ── TUE — doubly linked / circular (#9–#13) ─────────────────────────────────────
    fun reverseDoubly() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3]	[3,2,1]	Basic
         * 2	[1,2]	[2,1]	Two nodes
         * 3	null	[]	Empty
         */
        TestRunner.check("W4 #9 Reverse Doubly Linked List, basic", block = { dllValues(reverseDoubly(buildDLL(1, 2, 3))) }, verify = { it == listOf(3, 2, 1) })
        TestRunner.check("W4 #9b Reverse Doubly Linked List, two nodes", block = { dllValues(reverseDoubly(buildDLL(1, 2))) }, verify = { it == listOf(2, 1) })
        TestRunner.check("W4 #9c Reverse Doubly Linked List, empty", block = { dllValues(reverseDoubly(null)) }, verify = { it == emptyList<Int>() })
    }
    fun deleteNodeDLL() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3], delete 2	[1,3]	Middle
         * 2	[1,2], delete head	[2]	Head
         * 3	[1,2], delete tail	[1]	Tail
         */
        TestRunner.check("W4 #10 Delete Node in DLL, middle", block = { val h = buildDLL(1, 2, 3); dllValues(deleteNodeDLL(h, dllFind(h, 2))) }, verify = { it == listOf(1, 3) })
        TestRunner.check("W4 #10b Delete Node in DLL, head", block = { val h = buildDLL(1, 2); dllValues(deleteNodeDLL(h, h)) }, verify = { it == listOf(2) })
        TestRunner.check("W4 #10c Delete Node in DLL, tail", block = { val h = buildDLL(1, 2); dllValues(deleteNodeDLL(h, dllTail(h))) }, verify = { it == listOf(1) })
    }
    fun insertNodeDLL() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,3], insert 2 at 1	[1,2,3]	Middle
         * 2	null, insert 1 at 0	[1]	Into empty
         * 3	[2], insert 1 at 0	[1,2]	At head
         */
        TestRunner.check("W4 #11 Insert Node in DLL, middle", block = { val h = buildDLL(1, 3); dllValues(insertNodeDLL(h, DListNode(2), 1)) }, verify = { it == listOf(1, 2, 3) })
        TestRunner.check("W4 #11b Insert Node in DLL, into empty list", block = { dllValues(insertNodeDLL(null, DListNode(1), 0)) }, verify = { it == listOf(1) })
        TestRunner.check("W4 #11c Insert Node in DLL, at head", block = { val h = buildDLL(2); dllValues(insertNodeDLL(h, DListNode(1), 0)) }, verify = { it == listOf(1, 2) })
    }
    fun removeDuplicatesDLL() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,1,2]	[1,2]	Basic
         * 2	[1,1,2,3,3]	[1,2,3]	Multiple runs
         * 3	[5,5,5]	[5]	All equal
         */
        TestRunner.check("W4 #12 Remove Duplicates (Sorted DLL), basic", block = { dllValues(removeDuplicatesDLL(buildDLL(1, 1, 2))) }, verify = { it == listOf(1, 2) })
        TestRunner.check("W4 #12b Remove Duplicates (Sorted DLL), multiple runs", block = { dllValues(removeDuplicatesDLL(buildDLL(1, 1, 2, 3, 3))) }, verify = { it == listOf(1, 2, 3) })
        TestRunner.check("W4 #12c Remove Duplicates (Sorted DLL), all equal", block = { dllValues(removeDuplicatesDLL(buildDLL(5, 5, 5))) }, verify = { it == listOf(5) })
    }
    fun circularLinkedList() {
        /**
         * #	Input	Expected	Tests
         * 1	insertAtEnd 1, 2, 3 → toList()	[1,2,3]	Insert 1-2-3
         * 2	insertAtEnd 1 → toList()	[1]	Single node
         * 3	new CircularLinkedList().toList()	[]	Empty
         */
        TestRunner.check("W4 #13 Circular Linked List, insert 1-2-3", block = { val c = CircularLinkedList(); c.insertAtEnd(1); c.insertAtEnd(2); c.insertAtEnd(3); c.toList() }, verify = { it == listOf(1, 2, 3) })
        TestRunner.check("W4 #13b Circular Linked List, single node", block = { val c = CircularLinkedList(); c.insertAtEnd(1); c.toList() }, verify = { it == listOf(1) })
        TestRunner.check("W4 #13c Circular Linked List, empty", block = { CircularLinkedList().toList() }, verify = { it == emptyList<Int>() })
    }

    // ── TUE — medium singly (#14–#16) ───────────────────────────────────────────────
    fun removeNthFromEnd() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4,5], n=2	[1,2,3,5]	Basic
         * 2	[1], n=1	[]	Only node
         * 3	[1,2], n=1	[1]	Tail
         */
        TestRunner.check("W4 #14 Remove Nth From End, basic", block = { toValues(removeNthFromEnd(toList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(1, 2, 3, 5) })
        TestRunner.check("W4 #14b Remove Nth From End, only node", block = { toValues(removeNthFromEnd(toList(1), 1)) }, verify = { it == emptyList<Int>() })
        TestRunner.check("W4 #14c Remove Nth From End, tail", block = { toValues(removeNthFromEnd(toList(1, 2), 1)) }, verify = { it == listOf(1) })
    }
    fun deleteDuplicatesII() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,3,4,4,5]	[1,2,5]	Classic
         * 2	[1,1,1,2,3]	[2,3]	Leading run
         * 3	[1,1]	[]	All duplicate
         */
        TestRunner.check("W4 #15 Remove Duplicates II, classic", block = { toValues(deleteDuplicatesII(toList(1, 2, 3, 3, 4, 4, 5))) }, verify = { it == listOf(1, 2, 5) })
        TestRunner.check("W4 #15b Remove Duplicates II, leading run", block = { toValues(deleteDuplicatesII(toList(1, 1, 1, 2, 3))) }, verify = { it == listOf(2, 3) })
        TestRunner.check("W4 #15c Remove Duplicates II, all duplicate", block = { toValues(deleteDuplicatesII(toList(1, 1))) }, verify = { it == emptyList<Int>() })
    }
    fun reorderList() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4]	[1,4,2,3]	Even
         * 2	[1,2,3,4,5]	[1,5,2,4,3]	Odd
         * 3	[1]	[1]	Single
         */
        TestRunner.check("W4 #16 Reorder List, even", block = { val h = toList(1, 2, 3, 4)!!; reorderList(h); toValues(h) }, verify = { it == listOf(1, 4, 2, 3) })
        TestRunner.check("W4 #16b Reorder List, odd", block = { val h = toList(1, 2, 3, 4, 5)!!; reorderList(h); toValues(h) }, verify = { it == listOf(1, 5, 2, 4, 3) })
        TestRunner.check("W4 #16c Reorder List, single", block = { val h = toList(1)!!; reorderList(h); toValues(h) }, verify = { it == listOf(1) })
    }

    // ── WED — medium singly (#17–#24) ───────────────────────────────────────────────
    fun swapPairs() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4]	[2,1,4,3]	Basic
         * 2	null	[]	Empty
         * 3	[1]	[1]	Odd length
         */
        TestRunner.check("W4 #17 Swap Nodes in Pairs, basic", block = { toValues(swapPairs(toList(1, 2, 3, 4))) }, verify = { it == listOf(2, 1, 4, 3) })
        TestRunner.check("W4 #17b Swap Nodes in Pairs, empty", block = { toValues(swapPairs(null)) }, verify = { it == emptyList<Int>() })
        TestRunner.check("W4 #17c Swap Nodes in Pairs, odd length", block = { toValues(swapPairs(toList(1))) }, verify = { it == listOf(1) })
    }
    fun rotateRight() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4,5], k=2	[4,5,1,2,3]	Basic
         * 2	[0,1,2], k=4	[2,0,1]	k > length
         * 3	[1], k=0	[1]	Single node
         */
        TestRunner.check("W4 #18 Rotate Linked List, basic", block = { toValues(rotateRight(toList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(4, 5, 1, 2, 3) })
        TestRunner.check("W4 #18b Rotate Linked List, k > length", block = { toValues(rotateRight(toList(0, 1, 2), 4)) }, verify = { it == listOf(2, 0, 1) })
        TestRunner.check("W4 #18c Rotate Linked List, single node", block = { toValues(rotateRight(toList(1), 0)) }, verify = { it == listOf(1) })
    }
    fun partition() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,4,3,2,5,2], x=3	[1,2,2,4,3,5]	Basic
         * 2	null, x=3	[]	Empty
         * 3	[2,1], x=2	[1,2]	Needs reorder
         */
        TestRunner.check("W4 #19 Partition List, basic", block = { toValues(partition(toList(1, 4, 3, 2, 5, 2), 3)) }, verify = { it == listOf(1, 2, 2, 4, 3, 5) })
        TestRunner.check("W4 #19b Partition List, empty", block = { toValues(partition(null, 3)) }, verify = { it == emptyList<Int>() })
        TestRunner.check("W4 #19c Partition List, needs reorder", block = { toValues(partition(toList(2, 1), 2)) }, verify = { it == listOf(1, 2) })
    }
    fun detectCycle() {
        /**
         * #	Input	Expected	Tests
         * 1	[3,2,0,-4], tail → index 1	2 (val at entry)	Cycle at index 1
         * 2	[1] (no cycle)	null	No cycle
         * 3	[1] self-loop	1	Self loop
         */
        TestRunner.check("W4 #20 Linked List Cycle II, cycle at index 1", block = { val h = toList(3, 2, 0, -4)!!; var t = h; while (t.next != null) t = t.next!!; t.next = h.next; detectCycle(h)?.`val` }, verify = { it == 2 })
        TestRunner.check("W4 #20b Linked List Cycle II, no cycle", block = { detectCycle(toList(1)) }, verify = { it == null })
        TestRunner.check("W4 #20c Linked List Cycle II, self loop", block = { val h = toList(1)!!; h.next = h; detectCycle(h)?.`val` }, verify = { it == 1 })
    }
    fun isPalindromeLL() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,2,1]	true	Even palindrome
         * 2	[1,2]	false	Not a palindrome
         * 3	null	true	Empty
         */
        TestRunner.check("W4 #21 Palindrome Linked List, even palindrome", block = { isPalindromeLL(toList(1, 2, 2, 1)) }, verify = { it })
        TestRunner.check("W4 #21b Palindrome Linked List, not a palindrome", block = { isPalindromeLL(toList(1, 2)) }, verify = { !it })
        TestRunner.check("W4 #21c Palindrome Linked List, empty", block = { isPalindromeLL(null) }, verify = { it })
    }
    fun addTwoNumbers() {
        /**
         * #	Input	Expected	Tests
         * 1	[2,4,3] + [5,6,4]	[7,0,8]	Basic
         * 2	[0] + [0]	[0]	Zeros
         * 3	[9,9,9,9,9,9,9] + [9,9,9,9]	[8,9,9,9,0,0,0,1]	Carry cascade
         */
        TestRunner.check("W4 #22 Add Two Numbers, basic", block = { toValues(addTwoNumbers(toList(2, 4, 3), toList(5, 6, 4))) }, verify = { it == listOf(7, 0, 8) })
        TestRunner.check("W4 #22b Add Two Numbers, zeros", block = { toValues(addTwoNumbers(toList(0), toList(0))) }, verify = { it == listOf(0) })
        TestRunner.check("W4 #22c Add Two Numbers, carry cascade", block = { toValues(addTwoNumbers(toList(9, 9, 9, 9, 9, 9, 9), toList(9, 9, 9, 9))) }, verify = { it == listOf(8, 9, 9, 9, 0, 0, 0, 1) })
    }
    fun copyRandomList() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3], randoms [3,2,-1]	[1,2,3], randoms [3,2,-1], fresh nodes	Basic
         * 2	[7,13,11], no randoms	[7,13,11]	No randoms
         * 3	[1], random → itself	[1], random [1], fresh nodes	Self random
         */
        TestRunner.check("W4 #23 Copy List with Random Pointer, basic", block = {
            val a = RNode(1); val b = RNode(2); val c = RNode(3)
            a.next = b; b.next = c; a.random = c; b.random = b
            val cp = copyRandomList(a)
            val vals = mutableListOf<Int>(); val rvals = mutableListOf<Int>()
            var cur: RNode? = cp
            while (true) {
                val node = cur ?: break           // elvis — node is non-null RNode, no smart cast needed
                vals.add(node.value); rvals.add(node.random?.value ?: -1)
                cur = node.next
            }
            Triple(vals, rvals, cp !== a)
        }, verify = { it.first == listOf(1, 2, 3) && it.second == listOf(3, 2, -1) && it.third })
        TestRunner.check("W4 #23b Copy List with Random Pointer, no randoms", block = {
            val a = RNode(7); val b = RNode(13); val c = RNode(11); a.next = b; b.next = c
            val cp = copyRandomList(a)
            val vals = mutableListOf<Int>()
            var cur: RNode? = cp
//            while (cur != null) { val node = cur; vals.add(node.value); cur = node.next }
            vals
        }, verify = { it == listOf(7, 13, 11) })
        TestRunner.check("W4 #23c Copy List with Random Pointer, self random", block = {
            val a = RNode(1); a.random = a
            val cp = copyRandomList(a)
            Triple(cp?.value, cp?.random?.value, cp !== a && cp?.random !== a)
        }, verify = { it.first == 1 && it.second == 1 && it.third })
    }
    fun sortedListToBST() {
        /**
         * #	Input	Expected	Tests
         * 1	[-10,-3,0,5,9]	inorder [-10,-3,0,5,9], height-balanced	Basic
         * 2	null	null	Empty
         * 3	[1]	inorder [1]	Single
         */
        TestRunner.check("W4 #24 Convert Sorted List to BST, basic", block = {
            val root = sortedListToBST(toList(-10, -3, 0, 5, 9))
            val out = mutableListOf<Int>(); inorder(root, out)
            out to (kotlin.math.abs(treeHeight(root?.left) - treeHeight(root?.right)) <= 1)
        }, verify = { it.first == listOf(-10, -3, 0, 5, 9) && it.second })
        TestRunner.check("W4 #24b Convert Sorted List to BST, empty", block = { sortedListToBST(null) }, verify = { it == null })
        TestRunner.check("W4 #24c Convert Sorted List to BST, single", block = {
            val out = mutableListOf<Int>(); inorder(sortedListToBST(toList(1)), out); out
        }, verify = { it == listOf(1) })
    }

    // ── THU — design (#25–#27) ──────────────────────────────────────────────────────
    fun myHashSet() {
        /**
         * #	Input	Expected	Tests
         * 1	add(1), add(2), contains(1)	true	Add + contains
         * 2	add(2), remove(2), contains(2)	false	Remove
         * 3	remove(100), contains(100)	false	Remove missing key
         */
        TestRunner.check("W4 #25 Design Hash Set, add + contains", block = { val s = MyHashSet(); s.add(1); s.add(2); s.contains(1) }, verify = { it })
        TestRunner.check("W4 #25b Design Hash Set, remove", block = { val s = MyHashSet(); s.add(2); s.remove(2); s.contains(2) }, verify = { !it })
        TestRunner.check("W4 #25c Design Hash Set, remove missing key", block = { val s = MyHashSet(); s.remove(100); s.contains(100) }, verify = { !it })
    }
    fun myLinkedList() {
        /**
         * #	Input	Expected	Tests
         * 1	addAtHead(1), addAtTail(3), addAtIndex(1,2), get(1)	2	Head/tail/index
         * 2	addAtHead(5), get(0)	5	Head into empty
         * 3	…addAtIndex(1,2), deleteAtIndex(0), get(0)	2	Delete head
         */
        TestRunner.check("W4 #26 My Linked List, head/tail/index", block = { val l = MyLinkedList(); l.addAtHead(1); l.addAtTail(3); l.addAtIndex(1, 2); l.get(1) }, verify = { it == 2 })
        TestRunner.check("W4 #26b My Linked List, head into empty", block = { val l = MyLinkedList(); l.addAtHead(5); l.get(0) }, verify = { it == 5 })
        TestRunner.check("W4 #26c My Linked List, delete head", block = { val l = MyLinkedList(); l.addAtHead(1); l.addAtTail(3); l.addAtIndex(1, 2); l.deleteAtIndex(0); l.get(0) }, verify = { it == 2 })
    }
    fun lruCache() {
        /**
         * #	Input	Expected	Tests
         * 1	cap=2: put(1,1), put(2,2), get(1), put(3,3), get(2)	[1, -1]	Evicts LRU
         * 2	cap=1: put(2,1), get(2)	1	Capacity one
         * 3	cap=2: put(1,1), put(2,2), get(1), put(3,3), get(2), get(3)	[1, -1, 3]	Get refreshes recency
         */
        TestRunner.check("W4 #27 LRU Cache, evicts LRU", block = { val c = LRUCache(2); c.put(1, 1); c.put(2, 2); val a = c.get(1); c.put(3, 3); val b = c.get(2); listOf(a, b) }, verify = { it == listOf(1, -1) })
        TestRunner.check("W4 #27b LRU Cache, capacity one", block = { val c = LRUCache(1); c.put(2, 1); c.get(2) }, verify = { it == 1 })
        TestRunner.check("W4 #27c LRU Cache, get refreshes recency", block = { val c = LRUCache(2); c.put(1, 1); c.put(2, 2); val a = c.get(1); c.put(3, 3); listOf(a, c.get(2), c.get(3)) }, verify = { it == listOf(1, -1, 3) })
    }

    // ── THU — DLL / CLL / hard (#28–#34) ────────────────────────────────────────────
    fun flattenMultiLevel() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3] with child [7,8] on 3	[1,2,3,7,8]	Child at tail
         * 2	[1,2] (already flat)	[1,2]	Already flat
         * 3	1→child 2→child 3	[1,2,3]	Nested chain
         */
        TestRunner.check("W4 #28 Flatten Multilevel DLL, child at tail", block = {
            val h = buildMList(1, 2, 3)!!; var t = h; while (t.next != null) t = t.next!!; t.child = buildMList(7, 8)
            mValues(flattenMultiLevel(h))
        }, verify = { it == listOf(1, 2, 3, 7, 8) })
        TestRunner.check("W4 #28b Flatten Multilevel DLL, already flat", block = { mValues(flattenMultiLevel(buildMList(1, 2))) }, verify = { it == listOf(1, 2) })
        TestRunner.check("W4 #28c Flatten Multilevel DLL, nested chain", block = {
            val inner = MNode(3); val mid = MNode(2); mid.child = inner; val top = MNode(1); top.child = mid
            mValues(flattenMultiLevel(top))
        }, verify = { it == listOf(1, 2, 3) })
    }
    fun countPairs() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4,5], target=6	2	Basic
         * 2	[2,2,3], target=5	1	Duplicates
         * 3	[1], target=2	0	Single node
         */
        TestRunner.check("W4 #29 Pairs with Given Sum (Sorted DLL), basic", block = { countPairs(buildDLL(1, 2, 3, 4, 5), 6) }, verify = { it == 2 })
        TestRunner.check("W4 #29b Pairs with Given Sum (Sorted DLL), duplicates", block = { countPairs(buildDLL(2, 2, 3), 5) }, verify = { it == 1 })
        TestRunner.check("W4 #29c Pairs with Given Sum (Sorted DLL), single node", block = { countPairs(buildDLL(1), 2) }, verify = { it == 0 })
    }
    fun josephus() {
        /**
         * #	Input	Expected	Tests
         * 1	n=5, k=2	3	Basic
         * 2	n=1, k=1	1	Single person
         * 3	n=7, k=3	4	Larger k
         */
        TestRunner.check("W4 #30 Josephus Problem, n=5 k=2", block = { josephus(5, 2) }, verify = { it == 3 })
        TestRunner.check("W4 #30b Josephus Problem, n=1 k=1", block = { josephus(1, 1) }, verify = { it == 1 })
        TestRunner.check("W4 #30c Josephus Problem, n=7 k=3", block = { josephus(7, 3) }, verify = { it == 4 })
    }
    fun sortedInsert() {
        /**
         * #	Input	Expected	Tests
         * 1	[2,4], insert 3	[2,3,4] (sorted)	Middle
         * 2	[2,4], insert 1	[1,2,4] (sorted)	Before head
         * 3	[2,4], insert 5	[2,4,5] (sorted)	After tail
         */
        TestRunner.check("W4 #31 Sorted Insert in Circular LL, middle", block = { cllValues(sortedInsert(cllOf(2, 4), 3)).sorted() }, verify = { it == listOf(2, 3, 4) })
        TestRunner.check("W4 #31b Sorted Insert in Circular LL, before head", block = { cllValues(sortedInsert(cllOf(2, 4), 1)).sorted() }, verify = { it == listOf(1, 2, 4) })
        TestRunner.check("W4 #31c Sorted Insert in Circular LL, after tail", block = { cllValues(sortedInsert(cllOf(2, 4), 5)).sorted() }, verify = { it == listOf(2, 4, 5) })
    }
    fun splitCircularList() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4]	[1,2] + [3,4]	Even
         * 2	[1,2,3]	[1,2] + [3]	Odd
         * 3	[1]	[1] + []	Single
         */
        TestRunner.check("W4 #32 Split Circular LL, even", block = { val p = splitCircularList(cllOf(1, 2, 3, 4)); Pair(cllValues(p.first), cllValues(p.second)) }, verify = { it == Pair(listOf(1, 2), listOf(3, 4)) })
        TestRunner.check("W4 #32b Split Circular LL, odd", block = { val p = splitCircularList(cllOf(1, 2, 3)); Pair(cllValues(p.first), cllValues(p.second)) }, verify = { it == Pair(listOf(1, 2), listOf(3)) })
        TestRunner.check("W4 #32c Split Circular LL, single", block = { val p = splitCircularList(cllOf(1)); Pair(cllValues(p.first), cllValues(p.second)) }, verify = { it == Pair(listOf(1), emptyList<Int>()) })
    }
    fun reverseBetween() {
        /**
         * #	Input	Expected	Tests
         * 1	[1,2,3,4,5], left=2 right=4	[1,4,3,2,5]	Basic
         * 2	[5], left=1 right=1	[5]	Single node
         * 3	[1,2], left=1 right=2	[2,1]	Whole list
         */
        TestRunner.check("W4 #33 Reverse Linked List II, basic", block = { toValues(reverseBetween(toList(1, 2, 3, 4, 5), 2, 4)) }, verify = { it == listOf(1, 4, 3, 2, 5) })
        TestRunner.check("W4 #33b Reverse Linked List II, single node", block = { toValues(reverseBetween(toList(5), 1, 1)) }, verify = { it == listOf(5) })
        TestRunner.check("W4 #33c Reverse Linked List II, whole list", block = { toValues(reverseBetween(toList(1, 2), 1, 2)) }, verify = { it == listOf(2, 1) })
    }
    fun lfuCache() {
        /**
         * #	Input	Expected	Tests
         * 1	cap=2: put(1,1), put(2,2), get(1), put(3,3), get(2)	[1, -1]	Evicts least frequent
         * 2	cap=2: put(1,1), put(2,2), put(2,2), get(2)	2	Frequency updates
         * 3	cap=2: put(1,1), put(2,2), put(3,3), get(1)	-1	Tie breaks on LRU
         */
        TestRunner.check("W4 #34 LFU Cache, evicts least frequent", block = { val c = W4LFUCache(2); c.put(1, 1); c.put(2, 2); val a = c.get(1); c.put(3, 3); listOf(a, c.get(2)) }, verify = { it == listOf(1, -1) })
        TestRunner.check("W4 #34b LFU Cache, frequency updates", block = { val c = W4LFUCache(2); c.put(1, 1); c.put(2, 2); c.put(2, 2); c.get(2) }, verify = { it == 2 })
        TestRunner.check("W4 #34c LFU Cache, tie breaks on LRU", block = { val c = W4LFUCache(2); c.put(1, 1); c.put(2, 2); c.put(3, 3); c.get(1) }, verify = { it == -1 })
    }


    // ── THU — hard (#35–#37) ────────────────────────────────────────────────────────
    fun mergeKLists() {
        /**
         * #	Input	Expected	Tests
         * 1	[[1,4,5],[1,3,4],[2,6]]	[1,1,2,3,4,4,5,6]	Three lists
         * 2	[]	[]	Empty input
         * 3	[null, [1]]	[1]	Null entries
         */
        TestRunner.check("W4 #35 Merge K Sorted Lists, three lists", block = { toValues(mergeKLists(arrayOf(toList(1, 4, 5), toList(1, 3, 4), toList(2, 6)))) }, verify = { it == listOf(1, 1, 2, 3, 4, 4, 5, 6) })
        TestRunner.check("W4 #35b Merge K Sorted Lists, empty input", block = { toValues(mergeKLists(emptyArray<ListNode?>())) }, verify = { it == emptyList<Int>() })
        TestRunner.check("W4 #35c Merge K Sorted Lists, null entries", block = { toValues(mergeKLists(arrayOf(null, toList(1)))) }, verify = { it == listOf(1) })
    }
    fun sortList() {
        TestRunner.check("W4 #36 Sort List, basic", block = { toValues(sortList(toList(4, 2, 1, 3))) }, verify = { it == listOf(1, 2, 3, 4) })
        TestRunner.check("W4 #36b Sort List, negatives", block = { toValues(sortList(toList(-1, 5, 0, -7, 3))) }, verify = { it == listOf(-7, -1, 0, 3, 5) })
        TestRunner.check("W4 #36c Sort List, empty", block = { toValues(sortList(null)) }, verify = { it == emptyList<Int>() })
    }
    fun reverseKGroup() {
        TestRunner.check("W4 #37 Reverse in K-Group, k=2", block = { toValues(reverseKGroup(toList(1, 2, 3, 4, 5), 2)) }, verify = { it == listOf(2, 1, 4, 5, 3) })
        TestRunner.check("W4 #37b Reverse in K-Group, k=3", block = { toValues(reverseKGroup(toList(1, 2, 3, 4, 5), 3)) }, verify = { it == listOf(3, 2, 1, 4, 5) })
        TestRunner.check("W4 #37c Reverse in K-Group, k > length", block = { toValues(reverseKGroup(toList(1, 2), 3)) }, verify = { it == listOf(1, 2) })
    }

    // ── PART B — complexity validation (timed; see practice/Week04_Practice.md) ──────
    // O(n) single pass over 1M nodes.
    fun reverseListBig() = TestRunner.check("W4-B1 Reverse 1M nodes", budgetMs = 1000, block = {
        val r = reverseList(rangeList(1_000_000))
        Triple(sizeOf(r), valueAt(r, 0), valueAt(r, 999_999))
    }, verify = { it.first == 1_000_000 && it.second == 1_000_000 && it.third == 1 })

    // O(N log K) heap merge: 500 lists × 2000 nodes = 1M nodes, disjoint ranges.
    fun mergeKListsBig() = TestRunner.check("W4-B2 Merge 500 sorted lists (1M nodes)", budgetMs = 2000, block = {
        val shifted = Array(500) { i ->
            val dummy = ListNode(0); var tail = dummy
            for (j in 1..2_000) { tail.next = ListNode(i * 2_000 + j); tail = tail.next!! }
            dummy.next
        }
        var prev = 0; var ok = true; var n = 0; var c = mergeKLists(shifted)
        while (c != null && n < 1_000_001) { if (c.`val` != prev + 1) ok = false; prev = c.`val`; n++; c = c.next }
        Triple(n, prev, ok)
    }, verify = { it.first == 1_000_000 && it.second == 1_000_000 && it.third })

    // O(n log n) merge sort — 100k shuffled nodes must come out strictly ascending.
    fun sortListBig() = TestRunner.check("W4-B3 Sort List 100k shuffled", budgetMs = 1000, block = {
        val vals = (1..100_000).toMutableList(); vals.shuffle(java.util.Random(42))
        val dummy = ListNode(0); var tail = dummy
        vals.forEach { tail.next = ListNode(it); tail = tail.next!! }
        var prev = 0; var ok = true; var n = 0; var c = sortList(dummy.next)
        while (c != null && n < 100_001) { if (c.`val` <= prev) ok = false; prev = c.`val`; n++; c = c.next }
        Triple(n, prev, ok)
    }, verify = { it.first == 100_000 && it.second == 100_000 && it.third })

    // O(1) get/put — 1M puts on a capacity-5000 cache keeps only the last 5000 keys.
    fun lruCacheBig() = TestRunner.check("W4-B4 LRU Cache 1M ops", budgetMs = 1500, block = {
        val c = LRUCache(5_000)
        for (i in 0 until 1_000_000) c.put(i % 10_000, i)
        listOf(c.get(9_999), c.get(0))
    }, verify = { it == listOf(999_999, -1) })

    // O(n) time / O(1) space (reverse half) — 500k-node palindrome.
    fun isPalindromeLLBig() = TestRunner.check("W4-B5 Palindrome LL 500k nodes", budgetMs = 800, block = {
        val dummy = ListNode(0); var tail = dummy
        for (i in 1..250_000) { tail.next = ListNode(i); tail = tail.next!! }
        for (i in 250_000 downTo 1) { tail.next = ListNode(i); tail = tail.next!! }
        isPalindromeLL(dummy.next)
    }, verify = { it })

    // O(max(m,n)) digit addition — 200k-digit + 200k-digit carries all the way.
    fun addTwoNumbersBig() = TestRunner.check("W4-B6 Add Two Numbers 200k digits", budgetMs = 800, block = {
        val a = ListNode(0); val b = ListNode(0); var ta = a; var tb = b
        repeat(200_000) { ta.next = ListNode(9); ta = ta.next!!; tb.next = ListNode(9); tb = tb.next!! }
        val sum = addTwoNumbers(a.next, b.next)
        Triple(sizeOf(sum), valueAt(sum, 0), valueAt(sum, 200_000))
    }, verify = { it.first == 200_001 && it.second == 8 && it.third == 1 })

    // O(n) group reversal — 1M nodes in groups of 1000.
    fun reverseKGroupBig() = TestRunner.check("W4-B7 Reverse K-Group 1M nodes k=1000", budgetMs = 1500, block = {
        val r = reverseKGroup(rangeList(1_000_000), 1_000)
        Triple(sizeOf(r), valueAt(r, 0) to valueAt(r, 999), valueAt(r, 1_000) to valueAt(r, 1_999))
    }, verify = { it.first == 1_000_000 && it.second == (1_000 to 1) && it.third == (2_000 to 1_001) })

    // O(n) Floyd — 1M nodes with the cycle entry at index 500k.
    fun detectCycleBig() = TestRunner.check("W4-B8 Detect Cycle 1M nodes", budgetMs = 1000, block = {
        val head = rangeList(1_000_000)
        var c = head; var entry: ListNode? = null; var i = 0
        while (c != null) { if (i == 500_000) entry = c; if (c.next == null) break; c = c.next; i++ }
        c!!.next = entry
        detectCycle(head)?.`val`
    }, verify = { it == 500_001 })

    // O(n) two-pointer gap — 1M nodes, drop the last one.
    fun removeNthFromEndBig() = TestRunner.check("W4-B9 Remove Nth From End 1M nodes", budgetMs = 1000, block = {
        val r = removeNthFromEnd(rangeList(1_000_000), 1)
        Triple(sizeOf(r), valueAt(r, 0), valueAt(r, 999_998))
    }, verify = { it.first == 999_999 && it.second == 1 && it.third == 999_999 })

    // O(n) deep copy with random pointers — 100k nodes, no shared objects.
    fun copyRandomListBig() = TestRunner.check("W4-B10 Copy Random List 100k nodes", budgetMs = 1000, block = {
        val nodes = Array(100_000) { RNode(it) }
        for (i in 0 until 100_000) { if (i < 99_999) nodes[i].next = nodes[i + 1]; nodes[i].random = nodes[(i * 7 + 3) % 100_000] }
        var shared = 0; var wrongRandom = 0; var n = 0; var c = copyRandomList(nodes[0])
        while (c != null && n < 100_000) {
            if (c.random == null || c.random!!.value != ((n * 7 + 3) % 100_000)) wrongRandom++
            if (c === nodes[n]) shared++
            n++; c = c.next
        }
        Triple(n + if (c == null) 0 else 1, shared, wrongRandom)
    }, verify = { it.first == 100_000 && it.second == 0 && it.third == 0 })
}


// ═══════════════════════════════════════════════════════════
// 👇 WEEK 4 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

// Node types used by the doubly-linked / random-pointer / multilevel problems. Declared
// here (and not in TestRunner.kt) because the weeks are compiled one file at a time.
class DListNode(var value: Int) { var prev: DListNode? = null; var next: DListNode? = null }
class RNode(var value: Int) { var next: RNode? = null; var random: RNode? = null }
class MNode(var value: Int) { var prev: MNode? = null; var next: MNode? = null; var child: MNode? = null }

// TODO() throws NotImplementedError, which TestRunner reports as SKIP — so the suite stays
// green while you work through the week; replace the body with your solution and it runs.

// #1 Reverse Linked List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5]	[5,4,3,2,1]	Basic
 * 2	[1,2]	[2,1]	Two nodes
 * 3	null	[]	Empty
 */
fun reverseList(head: ListNode?): ListNode? = TODO("W4 #1 — prev/curr pointer flip")

// #2 Middle of Linked List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5]	[3,4,5]	Odd
 * 2	[1,2,3,4,5,6]	[4,5,6]	Even → second middle
 * 3	[1]	[1]	Single
 */
fun middleNode(head: ListNode?): ListNode? = TODO("W4 #2 — fast/slow pointers")

// #3 Linked List Cycle
/**
 * #	Input	Expected	Tests
 * 1	[3,2,0,-4], tail → index 1	true	Cycle at index 1
 * 2	[1,2], tail → head	true	Full cycle
 * 3	[1]	false	No cycle
 */
fun hasCycle(head: ListNode?): Boolean = TODO("W4 #3 — Floyd's tortoise and hare")

// #4 Merge Two Sorted Lists
/**
 * #	Input	Expected	Tests
 * 1	[1,2,4] + [1,3,4]	[1,1,2,3,4,4]	Basic
 * 2	null + null	[]	Both empty
 * 3	null + [0]	[0]	One empty
 */
fun mergeTwoLists(l1: ListNode?, l2: ListNode?): ListNode? = TODO("W4 #4 — dummy node, splice the smaller head")

// #5 Remove Duplicates from Sorted List
/**
 * #	Input	Expected	Tests
 * 1	[1,1,2]	[1,2]	Basic
 * 2	[1,1,2,3,3]	[1,2,3]	Multiple runs
 * 3	null	[]	Empty
 */
fun deleteDuplicatesLL(head: ListNode?): ListNode? = TODO("W4 #5 — skip nodes equal to next")

// #6 Linked List Binary to Integer
/**
 * #	Input	Expected	Tests
 * 1	[1,0,1]	5	Basic
 * 2	[0]	0	Zero
 * 3	[1,1,1]	7	All ones
 */
fun getDecimalValue(head: ListNode?): Int = TODO("W4 #6 — value = value * 2 + bit")

// #7 Intersection of Two Linked Lists
/**
 * #	Input	Expected	Tests
 * 1	[4,1] + [5,6,1], shared [8,4,5]	8 (val at intersection)	Classic
 * 2	[2,6,4] + [1,5] (disjoint)	null	Disjoint
 * 3	same node [1]	1	Same node
 */
fun getIntersectionNode(headA: ListNode?, headB: ListNode?): ListNode? = TODO("W4 #7 — two pointers, switch heads on null")

// #8 Delete Node in a Linked List (only the node is given)
/**
 * #	Input	Expected	Tests
 * 1	[4,5,1,9], delete 5	[4,1,9]	Middle
 * 2	[4,5,1,9], delete 1	[4,5,9]	Later node
 * 3	[1,2], delete head	[2]	Near tail
 */
fun deleteNodeLL(node: ListNode?) { TODO("W4 #8 — copy next into node, bypass next") }

// #9 Reverse Doubly Linked List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3]	[3,2,1]	Basic
 * 2	[1,2]	[2,1]	Two nodes
 * 3	null	[]	Empty
 */
fun reverseDoubly(head: DListNode?): DListNode? = TODO("W4 #9 — swap prev/next on every node, return old tail")

// #10 Delete Node in DLL
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3], delete 2	[1,3]	Middle
 * 2	[1,2], delete head	[2]	Head
 * 3	[1,2], delete tail	[1]	Tail
 */
fun deleteNodeDLL(head: DListNode?, node: DListNode?): DListNode? = TODO("W4 #10 — relink prev/next, fix head")

// #11 Insert Node in DLL at zero-based position
/**
 * #	Input	Expected	Tests
 * 1	[1,3], insert 2 at pos 1	[1,2,3]	Middle
 * 2	null, insert 1 at pos 0	[1]	Into empty list
 * 3	[2], insert 1 at pos 0	[1,2]	At head
 */
fun insertNodeDLL(head: DListNode?, node: DListNode?, position: Int): DListNode? = TODO("W4 #11 — walk to position, splice node in")

// #12 Remove Duplicates from Sorted DLL
/**
 * #	Input	Expected	Tests
 * 1	[1,1,2]	[1,2]	Basic
 * 2	[1,1,2,3,3]	[1,2,3]	Multiple runs
 * 3	[5,5,5]	[5]	All equal
 */
fun removeDuplicatesDLL(head: DListNode?): DListNode? = TODO("W4 #12 — unlink runs of equal values, free the dupes")

// #13 Circular Linked List (design)
/**
 * #	Input	Expected	Tests
 * 1	insertAtEnd 1, 2, 3 → toList()	[1,2,3]	Insert 1-2-3
 * 2	insertAtEnd 1 → toList()	[1]	Single node
 * 3	new CircularLinkedList().toList()	[]	Empty
 */
class CircularLinkedList {
    fun insertAtEnd(value: Int) { TODO("W4 #13 — keep tail.next pointing at head") }
    fun delete(value: Int) { TODO("W4 #13 — unlink, fix head/tail, break the ring on the last node") }
    fun toList(): List<Int> = TODO("W4 #13 — walk once around the ring")
}

// #14 Remove Nth Node From End
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5], n=2	[1,2,3,5]	Basic
 * 2	[1], n=1	[]	Only node
 * 3	[1,2], n=1	[1]	Tail
 */
fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? = TODO("W4 #14 — dummy + two pointers with an n-gap")

// #15 Remove Duplicates from Sorted List II (drop every node in a duplicate run)
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,3,4,4,5]	[1,2,5]	Classic
 * 2	[1,1,1,2,3]	[2,3]	Leading run
 * 3	[1,1]	[]	All duplicate
 */
fun deleteDuplicatesII(head: ListNode?): ListNode? = TODO("W4 #15 — dummy + skip whole runs")

// #16 Reorder List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4]	[1,4,2,3]	Even
 * 2	[1,2,3,4,5]	[1,5,2,4,3]	Odd
 * 3	[1]	[1]	Single
 */
fun reorderList(head: ListNode?) { TODO("W4 #16 — find middle, reverse second half, interleave") }

// #17 Swap Nodes in Pairs
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4]	[2,1,4,3]	Basic
 * 2	null	[]	Empty
 * 3	[1]	[1]	Odd length
 */
fun swapPairs(head: ListNode?): ListNode? = TODO("W4 #17 — swap each adjacent pair, link to the rest")

// #18 Rotate List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5], k=2	[4,5,1,2,3]	Basic
 * 2	[0,1,2], k=4	[2,0,1]	k > length
 * 3	[1], k=0	[1]	Single node
 */
fun rotateRight(head: ListNode?, k: Int): ListNode? = TODO("W4 #18 — close the ring, break at n − k%n")

// #19 Partition List
/**
 * #	Input	Expected	Tests
 * 1	[1,4,3,2,5,2], x=3	[1,2,2,4,3,5]	Basic
 * 2	null, x=3	[]	Empty
 * 3	[2,1], x=2	[1,2]	Needs reorder
 */
fun partition(head: ListNode?, x: Int): ListNode? = TODO("W4 #19 — two lists (<x / >=x), concatenate")

// #20 Linked List Cycle II
/**
 * #	Input	Expected	Tests
 * 1	[3,2,0,-4], tail → index 1	2 (val at entry)	Cycle at index 1
 * 2	[1] (no cycle)	null	No cycle
 * 3	[1] self-loop	1	Self loop
 */
fun detectCycle(head: ListNode?): ListNode? = TODO("W4 #20 — Floyd meet, then re-walk from head")

// #21 Palindrome Linked List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,2,1]	true	Even palindrome
 * 2	[1,2]	false	Not a palindrome
 * 3	null	true	Empty
 */
fun isPalindromeLL(head: ListNode?): Boolean = TODO("W4 #21 — reverse the second half and compare")

// #22 Add Two Numbers
/**
 * #	Input	Expected	Tests
 * 1	[2,4,3] + [5,6,4]	[7,0,8]	Basic
 * 2	[0] + [0]	[0]	Zeros
 * 3	[9,9,9,9,9,9,9] + [9,9,9,9]	[8,9,9,9,0,0,0,1]	Carry cascade
 */
fun addTwoNumbers(l1: ListNode?, l2: ListNode?): ListNode? = TODO("W4 #22 — digit-by-digit with carry")

// #23 Copy List with Random Pointer
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3], randoms [3,2,-1]	[1,2,3], randoms [3,2,-1], fresh nodes	Basic
 * 2	[7,13,11], no randoms	[7,13,11]	No randoms
 * 3	[1], random → itself	[1], random [1], fresh nodes	Self random
 */
fun copyRandomList(head: RNode?): RNode? = TODO("W4 #23 — interleave clones, or hashmap original→clone")

// #24 Convert Sorted List to Binary Search Tree
/**
 * #	Input	Expected	Tests
 * 1	[-10,-3,0,5,9]	inorder [-10,-3,0,5,9], height-balanced	Basic
 * 2	null	null	Empty
 * 3	[1]	inorder [1]	Single
 */
fun sortedListToBST(head: ListNode?): TreeNode? = TODO("W4 #24 — slow/fast middle as root, recurse on halves")


// #25 Design Hash Set
/**
 * #	Input	Expected	Tests
 * 1	add(1), add(2), contains(1)	true	Add + contains
 * 2	add(2), remove(2), contains(2)	false	Remove
 * 3	remove(100), contains(100)	false	Remove missing key
 */
class MyHashSet {
    fun add(key: Int) { TODO("W4 #25 — bucket array + chaining (or a bitset)") }
    fun remove(key: Int) { TODO("W4 #25 — remove from its bucket") }
    fun contains(key: Int): Boolean = TODO("W4 #25 — probe the bucket")
}

// #26 Design Linked List
/**
 * #	Input	Expected	Tests
 * 1	addAtHead(1), addAtTail(3), addAtIndex(1,2), get(1)	2	Head/tail/index
 * 2	addAtHead(5), get(0)	5	Head into empty
 * 3	…addAtIndex(1,2), deleteAtIndex(0), get(0)	2	Delete head
 */
class MyLinkedList {
    fun get(index: Int): Int = TODO("W4 #26 — walk to index, −1 when out of range")
    fun addAtHead(value: Int) { TODO("W4 #26 — link a new node in front of head") }
    fun addAtTail(value: Int) { TODO("W4 #26 — append after tail") }
    fun addAtIndex(index: Int, value: Int) { TODO("W4 #26 — clamp index, splice node in") }
    fun deleteAtIndex(index: Int) { TODO("W4 #26 — unlink node at index") }
}

// #27 LRU Cache
/**
 * #	Input	Expected	Tests
 * 1	cap=2: put(1,1), put(2,2), get(1), put(3,3), get(2)	[1, -1]	Evicts LRU
 * 2	cap=1: put(2,1), get(2)	1	Capacity one
 * 3	cap=2: put(1,1), put(2,2), get(1), put(3,3), get(2), get(3)	[1, -1, 3]	Get refreshes recency
 */
class LRUCache(private val capacity: Int) {
    fun get(key: Int): Int = TODO("W4 #27 — hashmap + move node to MRU end")
    fun put(key: Int, value: Int) { TODO("W4 #27 — insert/update, evict LRU when over capacity") }
}

// #28 Flatten a Multilevel Doubly Linked List
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3] with child [7,8] on 3	[1,2,3,7,8]	Child at tail
 * 2	[1,2] (already flat)	[1,2]	Already flat
 * 3	1→child 2→child 3	[1,2,3]	Nested chain
 */
fun flattenMultiLevel(head: MNode?): MNode? = TODO("W4 #28 — DFS: splice each child list into the gap")

// #29 Count Pairs with Given Sum in a Sorted DLL
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5], target=6	2	Basic
 * 2	[2,2,3], target=5	1	Duplicates
 * 3	[1], target=2	0	Single node
 */
fun countPairs(head: DListNode?, target: Int): Int = TODO("W4 #29 — left/right pointers from both ends")

// #30 Josephus Problem
/**
 * #	Input	Expected	Tests
 * 1	n=5, k=2	3	Basic
 * 2	n=1, k=1	1	Single person
 * 3	n=7, k=3	4	Larger k
 */
fun josephus(n: Int, k: Int): Int = TODO("W4 #30 — iterative survivor = (survivor + k) % i, 1-based")

// #31 Sorted Insert in a Circular Linked List
/**
 * #	Input	Expected	Tests
 * 1	[2,4], insert 3	[2,3,4] (sorted)	Middle
 * 2	[2,4], insert 1	[1,2,4] (sorted)	Before head
 * 3	[2,4], insert 5	[2,4,5] (sorted)	After tail
 */
fun sortedInsert(head: ListNode?, value: Int): ListNode? = TODO("W4 #31 — insert in the value range, fix head when wrapping")

// #32 Split a Circular Linked List in Half
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4]	[1,2] + [3,4]	Even
 * 2	[1,2,3]	[1,2] + [3]	Odd
 * 3	[1]	[1] + []	Single
 */
fun splitCircularList(head: ListNode?): Pair<ListNode?, ListNode?> = TODO("W4 #32 — slow/fast to the middle, cut and re-close both rings")

// #33 Reverse Linked List II
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5], left=2 right=4	[1,4,3,2,5]	Basic
 * 2	[5], left=1 right=1	[5]	Single node
 * 3	[1,2], left=1 right=2	[2,1]	Whole list
 */
fun reverseBetween(head: ListNode?, left: Int, right: Int): ListNode? = TODO("W4 #33 — dummy + reverse the [left, right] slice")

// #34 LFU Cache (W4-prefixed to avoid clashing with the Week 11 stub of the same name)
/**
 * #	Input	Expected	Tests
 * 1	cap=2: put(1,1), put(2,2), get(1), put(3,3), get(2)	[1, -1]	Evicts least frequent
 * 2	cap=2: put(1,1), put(2,2), put(2,2), get(2)	2	Frequency updates
 * 3	cap=2: put(1,1), put(2,2), put(3,3), get(1)	-1	Tie breaks on LRU
 */
class W4LFUCache(private val capacity: Int) {
    fun get(key: Int): Int = TODO("W4 #34 — hashmap + freq→LRU buckets, bump minFreq")
    fun put(key: Int, value: Int) { TODO("W4 #34 — evict the least frequent (LRU on ties)") }
}

// #35 Merge k Sorted Lists
/**
 * #	Input	Expected	Tests
 * 1	[[1,4,5],[1,3,4],[2,6]]	[1,1,2,3,4,4,5,6]	Three lists
 * 2	[]	[]	Empty input
 * 3	[null, [1]]	[1]	Null entries
 */
fun mergeKLists(lists: Array<ListNode?>): ListNode? = TODO("W4 #35 — min-heap of heads, or divide and conquer")

// #36 Sort List
/**
 * #	Input	Expected	Tests
 * 1	[4,2,1,3]	[1,2,3,4]	Basic
 * 2	[-1,5,0,-7,3]	[-7,-1,0,3,5]	Negatives
 * 3	null	[]	Empty
 */
fun sortList(head: ListNode?): ListNode? = TODO("W4 #36 — top-down merge sort with slow/fast split")

// #37 Reverse Nodes in k-Group
/**
 * #	Input	Expected	Tests
 * 1	[1,2,3,4,5], k=2	[2,1,4,5,3]	k=2
 * 2	[1,2,3,4,5], k=3	[3,2,1,4,5]	k=3
 * 3	[1,2], k=3	[1,2]	k > length
 */
fun reverseKGroup(head: ListNode?, k: Int): ListNode? = TODO("W4 #37 — count k, reverse the group, recurse on the rest")

