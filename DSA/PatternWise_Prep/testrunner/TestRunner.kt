package testrunner

/**
 * TestRunner.kt — Core engine + shared helpers + main entry point.
 *
 * Each week lives in its own file: Week01Tests.kt … Week08Tests.kt
 * (each contains that week's test class AND that week's solution stubs).
 *
 * How to use:
 *   1. Open the week file you are working on and implement its solution stubs.
 *   2. In main() below, comment/uncomment the week(s) you want to run.
 *   3. Run main() — each test prints PASS ✅ / FAIL ❌ / SKIP ⏭️ (not implemented yet) + time.
 *   4. Inside a week class you can also comment/uncomment individual test calls in runAll().
 */
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// ═══════════════════════════════════════════════════════════
// CORE ENGINE
// ═══════════════════════════════════════════════════════════

object TestRunner {
    private var passed = 0
    private var failed = 0
    private var skipped = 0

    fun <T> check(name: String, budgetMs: Long = 1000, block: () -> T, verify: (T) -> Boolean) {
        var result: T? = null
        val time = try {
            var t = 0L
            try {
                measureTimeMillis { result = block() }.also { t = it }
            } catch (e: NotImplementedError) {
                skipped++
                println("[SKIP ⏭️ ] $name — solution not implemented yet")
                return
            }
            t
        } catch (e: Exception) {
            failed++
            println("[FAIL ❌] $name — threw ${e::class.simpleName}: ${e.message}")
            return
        }
        val ok = verify(result!!)
        if (ok) passed++ else failed++
        val verdict = when {
            !ok -> "FAIL ❌"
            time > budgetMs -> "PASS (SLOW ⚠️ ${time}ms > ${budgetMs}ms budget — check complexity!)"
            else -> "PASS ✅"
        }
        println("[$verdict] $name — ${time}ms")
    }

    fun summary() {
        println("\n━━━ Summary: $passed passed, $failed failed, $skipped skipped ━━━")
    }
}

// ═══════════════════════════════════════════════════════════
// SHARED HELPERS & NODE CLASSES
// ═══════════════════════════════════════════════════════════

class ListNode(var `val`: Int) {
    var next: ListNode? = null
    constructor(`val`: Int, next: ListNode?) : this(`val`) { this.next = next }
}

class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
    constructor(`val`: Int, left: TreeNode?) : this(`val`) { this.left = left }
}

fun toList(vararg values: Int): ListNode? {
    val dummy = ListNode(0); var cur = dummy
    for (v in values) { cur.next = ListNode(v); cur = cur.next!! }
    return dummy.next
}

fun toValues(head: ListNode?): List<Int> {
    val out = mutableListOf<Int>(); var cur = head
    while (cur != null) { out.add(cur.`val`); cur = cur.next }
    return out
}

fun toTree(vararg values: Int?): TreeNode? {
    if (values.isEmpty() || values[0] == null) return null
    val root = TreeNode(values[0]!!); val queue = ArrayDeque<TreeNode>(); queue.add(root); var i = 1
    while (i < values.size) {
        val node = queue.removeFirst()
        if (i < values.size) { val l = values[i++]; if (l != null) { node.left = TreeNode(l); queue.add(node.left!!) } }
        if (i < values.size) { val r = values[i++]; if (r != null) { node.right = TreeNode(r); queue.add(node.right!!) } }
    }
    return root
}

fun isSorted(a: IntArray): Boolean = (0 until a.size - 1).all { a[it] <= a[it + 1] }
fun shuffledRange(n: Int): IntArray = IntArray(n) { it + 1 }.also { it.shuffle(Random(42)) }

// ═══════════════════════════════════════════════════════════
// MAIN — comment/uncomment the weeks you want to run
// ═══════════════════════════════════════════════════════════

fun main() {
    // ▼▼▼ UNCOMMENT the week(s) you want to run ▼▼▼
    Week01Tests().runAll()   // Week 1 — Two Pointer
//    Week02Tests().runAll()   // Week 2 — Sliding Window + Prefix Sum
//    Week03Tests().runAll()   // Week 3 — HashMap + Stack
//    Week04Tests().runAll()   // Week 4 — Linked List
//    Week05Tests().runAll()   // Week 5 — Tree + Heap + Trie
//    Week06Tests().runAll()   // Week 6 — Greedy + Binary Search + Bit
//    Week07Tests().runAll()   // Week 7 — Backtracking + DP
//    Week08Tests().runAll()   // Week 8 — Graph + Simulation + Matrix
    // ▲▲▲ COMMENT OUT the weeks you don't want ▲▲▲

    TestRunner.summary()
}
