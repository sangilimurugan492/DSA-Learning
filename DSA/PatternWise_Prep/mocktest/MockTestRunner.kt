package mocktest

/**
 * MockTestRunner.kt — Standalone mock-test harness. Zero class dependencies.
 *
 * Self-contained: needs NO project files or imports — only the Kotlin stdlib.
 * Its package is mocktest; week mock files (WeekNNMock.kt)
 * in the same folder declare the same package and reuse this engine directly.
 *
 * How to use:
 *   1. Write a mock test as one line in main() below:
 *        mockCheck("Mock #1 Two Sum", block = { twoSum(...).sorted() }, verify = { it == listOf(0, 1) })
 *   2. Implement the solution below it (plain top-level fun, no class needed).
 *      Keep it as TODO() until you solve it — the harness reports SKIP ⏭️.
 *   3. Run main(). Optional filter: run only matching tests via
 *        kotlin -cp out MockTestRunnerKt twoSum        (name-substring filter)
 *
 * Verdicts per test: PASS ✅ / PASS-SLOW ⚠️ (over budgetMs) / FAIL ❌ / SKIP ⏭️.
 * Process exit code = number of failures (script/CI friendly).
 *
 * Compile & run from terminal:
 *   kotlinc MockTestRunner.kt -d out
 *   kotlin -cp out MockTestRunnerKt [optional name filter]
 */
import kotlin.random.Random
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

// ═══════════════════════════════════════════════════════════
// CORE ENGINE (top-level — no object/class required)
// ═══════════════════════════════════════════════════════════

private var passed = 0
private var failed = 0
private var skipped = 0
private var nameFilter: String? = null

fun <T> mockCheck(name: String, budgetMs: Long = 1000, block: () -> T, verify: (T) -> Boolean) {
    nameFilter?.let { if (!name.lowercase().contains(it)) return }
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
        time > budgetMs -> "PASS-SLOW ⚠️ ${time}ms > ${budgetMs}ms budget — check complexity!"
        else -> "PASS ✅"
    }
    println("[$verdict] $name — ${time}ms")
}

fun mockSummary() {
    println("\n━━━ Mock Summary: $passed passed, $failed failed, $skipped skipped (${passed + failed + skipped} total) ━━━")
}

/** Exit code = failure count. Call at the end of main() if you script this. */
fun mockExit() {
    if (failed > 0) exitProcess(failed.coerceAtMost(125))
}

/** Set the name filter from CLI args (call at the top of any mock main()). */
fun mockInit(args: Array<String>) {
    nameFilter = args.firstOrNull()?.lowercase()
}

// ═══════════════════════════════════════════════════════════
// SHARED HELPERS (bundled — no external files needed)
// ═══════════════════════════════════════════════════════════

class MockListNode(var `val`: Int) {
    var next: MockListNode? = null
    constructor(`val`: Int, next: MockListNode?) : this(`val`) { this.next = next }
}

class MockTreeNode(var `val`: Int) {
    var left: MockTreeNode? = null
    var right: MockTreeNode? = null
    constructor(`val`: Int, left: MockTreeNode?) : this(`val`) { this.left = left }
}

fun mockList(vararg values: Int): MockListNode? {
    val dummy = MockListNode(0); var cur = dummy
    for (v in values) { cur.next = MockListNode(v); cur = cur.next!! }
    return dummy.next
}

fun mockListValues(head: MockListNode?): List<Int> {
    val out = mutableListOf<Int>(); var cur = head
    while (cur != null) { out.add(cur.`val`); cur = cur.next }
    return out
}

fun mockTree(vararg values: Int?): MockTreeNode? {
    if (values.isEmpty() || values[0] == null) return null
    val root = MockTreeNode(values[0]!!); val queue = ArrayDeque<MockTreeNode>(); queue.add(root); var i = 1
    while (i < values.size) {
        val node = queue.removeFirst()
        if (i < values.size) { val l = values[i++]; if (l != null) { node.left = MockTreeNode(l); queue.add(node.left!!) } }
        if (i < values.size) { val r = values[i++]; if (r != null) { node.right = MockTreeNode(r); queue.add(node.right!!) } }
    }
    return root
}

fun mockIsSorted(a: IntArray): Boolean = (0 until a.size - 1).all { a[it] <= a[it + 1] }
fun mockShuffledRange(n: Int): IntArray = IntArray(n) { it + 1 }.also { it.shuffle(Random(42)) }

// ═══════════════════════════════════════════════════════════
// MAIN — add one mockCheck() line per mock problem
// ═══════════════════════════════════════════════════════════

fun main(args: Array<String>) {
    nameFilter = args.firstOrNull()?.lowercase()

    // ▼▼▼ YOUR MOCK TESTS — one line each ▼▼▼
    mockCheck("Sample #1 — passing example", block = { mockListValues(mockList(1, 2, 3)).reversed() }, verify = { it == listOf(3, 2, 1) })
    mockCheck("Sample #2 — TODO stub shows SKIP", block = { twoSumMock(intArrayOf(2, 7, 11, 15), 9).sorted() }, verify = { it == listOf(0, 1) })
    mockCheck("Sample #3 — budget check", budgetMs = 5, block = { mockShuffledRange(200_000).max() }, verify = { it == 200_000 })
    // ▲▲▲ COMMENT OUT tests you don't want ▲▲▲

    mockSummary()
    mockExit()
}

// ═══════════════════════════════════════════════════════════
// 👇 YOUR MOCK SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════

fun twoSumMock(nums: IntArray, target: Int): IntArray = TODO()
