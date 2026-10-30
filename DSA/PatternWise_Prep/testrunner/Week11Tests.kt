package testrunner

/**
 * Week11Tests.kt — Week 11: Monotonic Stack + Design + Trie
 *
 * Part A — 20 problems × 3 cases from practice/Week11_Practice.md (runAll).
 * Part B — 10 timed stress cases (complexityAll, only via TestRunner main / --partb).
 * Solution stubs sit at the bottom of the file; they throw TODO() so each case is
 * reported as [SKIP ⏭️] until you implement it, then it runs for real.
 * No name collisions with Weeks 6–10 (checked).
 */

class Week11Tests {
    fun runAll() {
        println("\n━━━━━━━ WEEK 11 — Monotonic Stack + Design + Trie ━━━━━━━")
        dailyTemps(); dailyTemps2(); dailyTemps3(); nextGreater(); nextGreater2(); nextGreater3()
        histogram(); histogram2(); histogram3(); subMins(); subMins2(); subMins3()
        stockSpan(); stockSpan2(); stockSpan3(); removeK(); removeK2(); removeK3()
        pattern132(); pattern132b(); pattern132c(); subRanges(); subRanges2(); subRanges3()
        maxChunks(); maxChunks2(); maxChunks3(); removeDup(); removeDup2(); removeDup3()
        lru(); lru2(); lru3(); lfu(); lfu2(); lfu3()
        minStack(); minStack2(); minStack3(); randomSet(); randomSet2(); randomSet3()
        timeMap(); timeMap2(); timeMap3(); addSearch(); addSearch2(); addSearch3()
        wordSearch(); wordSearch2(); wordSearch3(); maxXor(); maxXor2(); maxXor3()
        calculator(); calculator2(); calculator3(); decode(); decode2(); decode3()
        // Part B runs via TestRunner main with --partb (do NOT write "complexityAll()" here)
    }

    // ── Part A — practice-sheet cases ───────────────────────────────────────

    // 1) Daily Temperatures: days until a warmer temperature.
    fun dailyTemps() = TestRunner.check("W11 #1 Daily Temperatures", block = { w11DailyTemperatures(intArrayOf(73,74,75,71,69,72,76,73)) }, verify = { it.contentEquals(intArrayOf(1,1,4,2,1,1,0,0)) })
    fun dailyTemps2() = TestRunner.check("W11 #1b Daily Temperatures increasing", block = { w11DailyTemperatures(intArrayOf(30,40,50,60)) }, verify = { it.contentEquals(intArrayOf(1,1,1,0)) })
    fun dailyTemps3() = TestRunner.check("W11 #1c Daily Temperatures basic", block = { w11DailyTemperatures(intArrayOf(30,60,90)) }, verify = { it.contentEquals(intArrayOf(1,1,0)) })

    // 2) Next Greater Element II (circular array).
    fun nextGreater() = TestRunner.check("W11 #2 Next Greater II", block = { w11NextGreaterElements(intArrayOf(1,2,1)) }, verify = { it.contentEquals(intArrayOf(2,-1,2)) })
    fun nextGreater2() = TestRunner.check("W11 #2b Next Greater II", block = { w11NextGreaterElements(intArrayOf(1,2,3,4,3)) }, verify = { it.contentEquals(intArrayOf(2,3,4,-1,4)) })
    fun nextGreater3() = TestRunner.check("W11 #2c Next Greater II all equal", block = { w11NextGreaterElements(intArrayOf(5,5,5)) }, verify = { it.contentEquals(intArrayOf(-1,-1,-1)) })

    // 3) Largest Rectangle in Histogram.
    fun histogram() = TestRunner.check("W11 #3 Largest Rectangle", block = { w11LargestRectangleArea(intArrayOf(2,1,5,6,2,3)) }, verify = { it == 10 })
    fun histogram2() = TestRunner.check("W11 #3b Largest Rectangle basic", block = { w11LargestRectangleArea(intArrayOf(2,4)) }, verify = { it == 4 })
    fun histogram3() = TestRunner.check("W11 #3c Largest Rectangle flat", block = { w11LargestRectangleArea(intArrayOf(1,1,1,1)) }, verify = { it == 4 })

    // 4) Sum of Subarray Minimums.
    fun subMins() = TestRunner.check("W11 #4 Sum Subarray Mins", block = { sumSubarrayMins(intArrayOf(3,1,2,4)) }, verify = { it == 17 })
    fun subMins2() = TestRunner.check("W11 #4b Sum Subarray Mins", block = { sumSubarrayMins(intArrayOf(11,81,94,43,3)) }, verify = { it == 444 })
    fun subMins3() = TestRunner.check("W11 #4c Sum Subarray Mins single", block = { sumSubarrayMins(intArrayOf(2)) }, verify = { it == 2 })

    // 5) Online Stock Span: collect spans from a stream of prices.
    fun stockSpan() = TestRunner.check("W11 #5 Stock Span", block = {
        val s = StockSpanner(); intArrayOf(100,80,60,70,60,75,85).map { s.next(it) }.toIntArray()
    }, verify = { it.contentEquals(intArrayOf(1,1,1,2,1,4,6)) })
    fun stockSpan2() = TestRunner.check("W11 #5b Stock Span equal prices", block = {
        val s = StockSpanner(); intArrayOf(50,50,50).map { s.next(it) }.toIntArray()
    }, verify = { it.contentEquals(intArrayOf(1,2,3)) })
    fun stockSpan3() = TestRunner.check("W11 #5c Stock Span single", block = {
        val s = StockSpanner(); intArrayOf(10).map { s.next(it) }.toIntArray()
    }, verify = { it.contentEquals(intArrayOf(1)) })

    private fun chars(vararg rows: String) = Array(rows.size) { rows[it].toCharArray() }

    // 6) Remove K Digits (monotonic stack, lexicographically smallest).
    fun removeK() = TestRunner.check("W11 #6 Remove K Digits", block = { removeKdigits("1432219", 3) }, verify = { it == "1219" })
    fun removeK2() = TestRunner.check("W11 #6b Remove K Digits leading zeros", block = { removeKdigits("10200", 1) }, verify = { it == "200" })
    fun removeK3() = TestRunner.check("W11 #6c Remove K Digits all removed", block = { removeKdigits("9", 1) }, verify = { it == "0" })

    // 7) 132 Pattern.
    fun pattern132() = TestRunner.check("W11 #7 132 Pattern false", block = { find132pattern(intArrayOf(1,2,3,4)) }, verify = { !it })
    fun pattern132b() = TestRunner.check("W11 #7b 132 Pattern true", block = { find132pattern(intArrayOf(3,1,4,2)) }, verify = { it })
    fun pattern132c() = TestRunner.check("W11 #7c 132 Pattern negatives", block = { find132pattern(intArrayOf(-1,3,2,0)) }, verify = { it })

    // 8) Sum of Subarray Ranges (returns Long).
    fun subRanges() = TestRunner.check("W11 #8 Subarray Ranges", block = { subArrayRanges(intArrayOf(1,2,3)) }, verify = { it == 4L })
    fun subRanges2() = TestRunner.check("W11 #8b Subarray Ranges ties", block = { subArrayRanges(intArrayOf(1,3,3)) }, verify = { it == 4L })
    fun subRanges3() = TestRunner.check("W11 #8c Subarray Ranges single", block = { subArrayRanges(intArrayOf(1)) }, verify = { it == 0L })

    // 9) Max Chunks To Make Sorted.
    fun maxChunks() = TestRunner.check("W11 #9 Max Chunks one", block = { maxChunksToSorted(intArrayOf(4,3,2,1,0)) }, verify = { it == 1 })
    fun maxChunks2() = TestRunner.check("W11 #9b Max Chunks many", block = { maxChunksToSorted(intArrayOf(1,0,2,3,4)) }, verify = { it == 4 })
    fun maxChunks3() = TestRunner.check("W11 #9c Max Chunks single", block = { maxChunksToSorted(intArrayOf(0)) }, verify = { it == 1 })

    // 10) Remove All Adjacent Duplicates II (keep k-1 of each run).
    fun removeDup() = TestRunner.check("W11 #10 Remove Adjacent Dups II", block = { removeDuplicates("abcd", 2) }, verify = { it == "abcd" })
    fun removeDup2() = TestRunner.check("W11 #10b Remove Adjacent Dups II cascade", block = { removeDuplicates("deeedbbcccbdaa", 3) }, verify = { it == "aa" })
    fun removeDup3() = TestRunner.check("W11 #10c Remove Adjacent Dups II", block = { removeDuplicates("pbbcggttciiippooaais", 2) }, verify = { it == "ps" })

    // 11) LRU Cache — get results in order.
    fun lru() = TestRunner.check("W11 #11 LRU Cache", block = {
        val c = LRUCacheW11(2)
        c.put(1, 1); c.put(2, 2); val g1 = c.get(1); c.put(3, 3); val g2 = c.get(2)
        c.put(4, 4); val g3 = c.get(1); val g4 = c.get(3); val g5 = c.get(4)
        listOf(g1, g2, g3, g4, g5)
    }, verify = { it == listOf(1, -1, -1, 3, 4) })
    fun lru2() = TestRunner.check("W11 #11b LRU Cache cap=1", block = {
        val c = LRUCacheW11(1)
        c.put(2, 1); val g1 = c.get(2); c.put(3, 2); val g2 = c.get(2)
        listOf(g1, g2)
    }, verify = { it == listOf(1, -1) })
    fun lru3() = TestRunner.check("W11 #11c LRU Cache update refresh", block = {
        val c = LRUCacheW11(2)
        c.put(1, 1); c.put(2, 2); c.put(1, 10); c.get(1)
    }, verify = { it == 10 })

    // 12) LFU Cache — get results in order (freq tie → LRU eviction).
    fun lfu() = TestRunner.check("W11 #12 LFU Cache", block = {
        val c = LFUCache(2)
        c.put(1, 1); c.put(2, 2); val g1 = c.get(1); c.put(3, 3); val g2 = c.get(2)
        val g3 = c.get(3); c.put(4, 4); val g4 = c.get(1); val g5 = c.get(3); val g6 = c.get(4)
        listOf(g1, g2, g3, g4, g5, g6)
    }, verify = { it == listOf(1, -1, 3, -1, 3, 4) })
    fun lfu2() = TestRunner.check("W11 #12b LFU Cache cap=0", block = {
        val c = LFUCache(0); c.put(1, 1); c.get(1)
    }, verify = { it == -1 })
    fun lfu3() = TestRunner.check("W11 #12c LFU Cache tie → LRU", block = {
        val c = LFUCache(2)
        c.put(1, 1); c.put(2, 2); c.put(1, 10); c.put(3, 3); c.get(2)
    }, verify = { it == -1 })

    // 13) Min Stack (getMin on empty → Int.MAX_VALUE by design).
    fun minStack() = TestRunner.check("W11 #13 Min Stack", block = {
        val s = MinStack(); s.push(-2); s.push(0); s.push(-3)
        val g1 = s.getMin(); s.pop(); val g2 = s.top(); val g3 = s.getMin()
        listOf(g1, g2, g3)
    }, verify = { it == listOf(-3, 0, -2) })
    fun minStack2() = TestRunner.check("W11 #13b Min Stack equal mins", block = {
        val s = MinStack(); s.push(1); s.push(1); s.getMin()
    }, verify = { it == 1 })
    fun minStack3() = TestRunner.check("W11 #13c Min Stack empty-safe", block = {
        val s = MinStack(); s.push(5); s.pop(); s.getMin()
    }, verify = { it == Int.MAX_VALUE })

    // 14) Insert Delete GetRandom O(1).
    fun randomSet() = TestRunner.check("W11 #14 RandomizedSet", block = {
        val s = RandomizedSet()
        val i1 = s.insert(1); val r2 = s.remove(2); val i2 = s.insert(2); val g1 = s.getRandom()
        val r1 = s.remove(1); val g2 = s.getRandom()
        listOf(i1, r2, i2, g1, r1, g2)
    }, verify = { it[0] == true && it[1] == false && it[2] == true && (it[3] == 1 || it[3] == 2) && it[4] == true && it[5] == 2 })
    fun randomSet2() = TestRunner.check("W11 #14b RandomizedSet duplicate", block = {
        val s = RandomizedSet(); listOf(s.insert(1), s.insert(1))
    }, verify = { it == listOf(true, false) })
    fun randomSet3() = TestRunner.check("W11 #14c RandomizedSet missing", block = {
        val s = RandomizedSet(); s.remove(5)
    }, verify = { !it })

    // 15) Time Based Key-Value Store.
    fun timeMap() = TestRunner.check("W11 #15 TimeMap", block = {
        val t = TimeMap(); t.set("foo", "bar", 1); listOf(t.get("foo", 1), t.get("foo", 3))
    }, verify = { it == listOf("bar", "bar") })
    fun timeMap2() = TestRunner.check("W11 #15b TimeMap snapshots", block = {
        val t = TimeMap(); t.set("foo", "bar", 1); t.set("foo", "bar2", 4)
        listOf(t.get("foo", 5), t.get("foo", 3))
    }, verify = { it == listOf("bar2", "bar") })
    fun timeMap3() = TestRunner.check("W11 #15c TimeMap missing key", block = {
        val t = TimeMap(); t.get("baz", 1)
    }, verify = { it == "" })

    // 16) Design Add and Search Words (wildcard '.').
    fun addSearch() = TestRunner.check("W11 #16 Add & Search Words", block = {
        val d = WordDictionary(); d.addWord("bad"); d.addWord("dad"); d.addWord("mad")
        listOf(d.search("pad"), d.search("bad"), d.search(".ad"), d.search("b.."))
    }, verify = { it == listOf(false, true, true, true) })
    fun addSearch2() = TestRunner.check("W11 #16b Add & Search Words single", block = {
        val d = WordDictionary(); d.addWord("a"); d.search("a")
    }, verify = { it })
    fun addSearch3() = TestRunner.check("W11 #16c Add & Search length mismatch", block = {
        val d = WordDictionary(); d.addWord("bad"); d.addWord("dad"); d.search(".a")
    }, verify = { !it })

    // 17) Word Search II (Trie-guided DFS; any order).
    fun wordSearch() = TestRunner.check("W11 #17 Word Search II", block = {
        w11FindWords(chars("oaan", "etae", "ihkr", "iflv"), listOf("oath", "pea", "eat", "rain"))
    }, verify = { it.toSet() == setOf("oath", "eat") })
    fun wordSearch2() = TestRunner.check("W11 #17b Word Search II order-free", block = {
        w11FindWords(chars("ab"), listOf("ba"))
    }, verify = { it == listOf("ba") })
    fun wordSearch3() = TestRunner.check("W11 #17c Word Search II no reuse", block = {
        w11FindWords(chars("abcd"), listOf("abcb"))
    }, verify = { it.isEmpty() })

    // 18) Maximum XOR of Two Numbers (bit trie).
    fun maxXor() = TestRunner.check("W11 #18 Maximum XOR", block = { findMaximumXOR(intArrayOf(3,10,5,25,2,8)) }, verify = { it == 28 })
    fun maxXor2() = TestRunner.check("W11 #18b Maximum XOR classic", block = { findMaximumXOR(intArrayOf(14,70,53,83,49,91,36,80,92,51,66,70)) }, verify = { it == 127 })
    fun maxXor3() = TestRunner.check("W11 #18c Maximum XOR single", block = { findMaximumXOR(intArrayOf(0)) }, verify = { it == 0 })

    // 19) Basic Calculator (+, -, parens, spaces).
    fun calculator() = TestRunner.check("W11 #19 Basic Calculator", block = { calculate("1 + 1") }, verify = { it == 2 })
    fun calculator2() = TestRunner.check("W11 #19b Basic Calculator spaces", block = { calculate(" 2-1 + 2 ") }, verify = { it == 3 })
    fun calculator3() = TestRunner.check("W11 #19c Basic Calculator nested", block = { calculate("(1+(4+5+2)-3)+(6+8)") }, verify = { it == 23 })

    // 20) Decode String.
    fun decode() = TestRunner.check("W11 #20 Decode String", block = { decodeString("3[a]2[bc]") }, verify = { it == "aaabcbc" })
    fun decode2() = TestRunner.check("W11 #20b Decode String nested", block = { decodeString("3[a2[c]]") }, verify = { it == "accaccacc" })
    fun decode3() = TestRunner.check("W11 #20c Decode String sequential", block = { decodeString("2[abc]3[cd]ef") }, verify = { it == "abcabccdcdcdef" })

    // ── Part B — complexity validation (complexityAll) ──────────────────────

    // 1) 100k strictly-decreasing temps then one hot day → span absorption ends at n-1.
    fun dailyTempsBig() = TestRunner.check("W11-B1 Daily Temperatures n=100k", budgetMs = 300,
        block = {
            val n = 100_000
            w11DailyTemperatures(IntArray(n) { if (it == n - 1) n else n - 1 - it })
        }, verify = { it.size == 100_000 && it[0] == 99_999 && it[99_998] == 1 && it[99_999] == 0 })

    // 2) 100k circular all-descending: index 0 is the global max → -1, everyone else wraps to it.
    fun nextGreaterBig() = TestRunner.check("W11-B2 Next Greater II circular n=100k", budgetMs = 400,
        block = { w11NextGreaterElements(IntArray(100_000) { 100_000 - it }) },
        verify = { it[0] == -1 && it.drop(1).all { v -> v == 100_000 } })

    // 3) Mountain histogram (two 50k peaks, n=100k): best area = 25000*50002 = 1,250,050,000.
    fun histogramBig() = TestRunner.check("W11-B3 Largest Rectangle mountain n=100k", budgetMs = 400,
        block = {
            val n = 100_000
            val half = 50_000
            val h = IntArray(n) { i -> if (i < half) i + 1 else n - i }
            w11LargestRectangleArea(h)
        }, verify = { it == 1_250_050_000 })

    // 4) 50k all-equal bars: answer = (n*(n+1)/2) mod 1e9+7 = 250,024,993 (ties counted once).
    fun subMinsBig() = TestRunner.check("W11-B4 Sum Subarray Mins all-equal n=50k", budgetMs = 300,
        block = { sumSubarrayMins(IntArray(50_000) { 1 }) }, verify = { it == 250_024_993 })

    // 5) 100k strictly-increasing prices: every span pops the whole stack → span = i+1.
    fun stockSpanBig() = TestRunner.check("W11-B5 Stock Span increasing n=100k", budgetMs = 300,
        block = {
            val s = StockSpanner()
            var last = 0
            for (i in 1..100_000) last = s.next(i)
            last
        }, verify = { it == 100_000 })

    // 6) 100k non-decreasing digits, k=50k: no pops during scan → drop last k chars.
    fun removeKBig() = TestRunner.check("W11-B6 Remove K Digits n=100k", budgetMs = 300,
        block = {
            val num = "112233445566778899".repeat(5_556).substring(0, 100_000)
            removeKdigits(num, 50_000)
        }, verify = { it.length == 50_000 })

    // 7) LRU Cache: 100k sequential puts on distinct keys (cap 10k) → only last 10k survive.
    fun lruBig() = TestRunner.check("W11-B7 LRU Cache 100k ops", budgetMs = 300,
        block = {
            val c = LRUCacheW11(10_000)
            for (i in 0 until 100_000) c.put(i, i)
            listOf(c.get(99_999), c.get(99_000), c.get(0))
        }, verify = { it == listOf(99_999, 99_000, -1) })

    // 8) LFU Cache: 50k equal-frequency churn on cap=2 → minFreq/LRU tie maintenance.
    fun lfuBig() = TestRunner.check("W11-B8 LFU Cache churn 50k ops", budgetMs = 300,
        block = {
            val c = LFUCache(2)
            for (i in 0 until 25_000) { c.put(1, i); c.put(2, i) }
            val g1 = c.get(1); val g2 = c.get(2)
            c.put(3, 5); val g3 = c.get(2); val g4 = c.get(1); val g5 = c.get(3)
            listOf(g1, g2, g3, g4, g5)
        }, verify = { it == listOf(24_999, 24_999, 24_999, -1, 5) })

    // 9) TimeMap: 100k sets on one key → binary search per get.
    fun timeMapBig() = TestRunner.check("W11-B9 TimeMap 100k sets", budgetMs = 300,
        block = {
            val t = TimeMap()
            for (i in 1..100_000) t.set("k", "v$i", i)
            listOf(t.get("k", 100_000), t.get("k", 50_000), t.get("k", 0))
        }, verify = { it == listOf("v100000", "v50000", "") })

    // 10) Basic Calculator: 10k nested parens "((((…1…))))" → stack depth OK.
    fun calculatorBig() = TestRunner.check("W11-B10 Calculator depth 10k", budgetMs = 400,
        block = { calculate("(".repeat(10_000) + "1" + ")".repeat(10_000)) }, verify = { it == 1 })

    fun complexityAll() {
        println("\n━━━━━━━ WEEK 11 Part B — complexity validation ━━━━━━━")
        dailyTempsBig(); nextGreaterBig(); histogramBig(); subMinsBig(); stockSpanBig()
        removeKBig(); lruBig(); lfuBig(); timeMapBig(); calculatorBig()
    }
}

// ═══════════════════════════════════════════════════════════
// 👇 WEEK 11 SOLUTIONS — implement as you solve 👇
// ═══════════════════════════════════════════════════════════
//
// Each stub throws TODO() so every case above reports [SKIP ⏭️] instead of a wrong
// answer. Replace the TODO() with your implementation and the case runs for real.
// MinStack contract: getMin()/top() on an empty stack → Int.MAX_VALUE / 0 sentinel.

// ── Monotonic Stack ─────────────────────────────────────────────────────────
fun w11DailyTemperatures(temperatures: IntArray): IntArray = TODO("W11 #1 — decreasing stack of indices")
fun w11NextGreaterElements(nums: IntArray): IntArray = TODO("W11 #2 — two-pass circular stack")
fun w11LargestRectangleArea(heights: IntArray): Int = TODO("W11 #3 — increasing stack + sentinel")
fun sumSubarrayMins(arr: IntArray): Int = TODO("W11 #4 — contribution counting, mod 1e9+7")
fun removeKdigits(num: String, k: Int): String = TODO("W11 #6 — increasing stack, trim leftovers")
fun find132pattern(nums: IntArray): Boolean = TODO("W11 #7 — right-to-left stack keeps the '2'")
fun subArrayRanges(nums: IntArray): Long = TODO("W11 #8 — sum of max contributions minus min contributions")
fun maxChunksToSorted(arr: IntArray): Int = TODO("W11 #9 — running max equals index → cut")
fun removeDuplicates(s: String, k: Int): String = TODO("W11 #10 — (char, count) stack")

// ── Design ──────────────────────────────────────────────────────────────────
class StockSpanner() {
    fun next(price: Int): Int = TODO("W11 #5 — absorb spans from the stack")
}

class LRUCacheW11(val capacity: Int) {
    fun get(key: Int): Int = TODO("W11 #11 — move node to front, return value")
    fun put(key: Int, value: Int) { TODO("W11 #11 — evict tail when over capacity") }
}

class LFUCache(val capacity: Int) {
    fun get(key: Int): Int = TODO("W11 #12 — bump freq, return value")
    fun put(key: Int, value: Int) { TODO("W11 #12 — evict lowest freq, tie → LRU") }
}

class MinStack() {
    fun push(`val`: Int) { TODO("W11 #13 — push alongside running min") }
    fun pop() { TODO("W11 #13 — pop both stacks") }
    fun top(): Int = TODO("W11 #13 — stack.peek")
    fun getMin(): Int = TODO("W11 #13 — min-stack.peek")
}

class RandomizedSet() {
    fun insert(`val`: Int): Boolean = TODO("W11 #14 — list + map index swap-remove")
    fun remove(`val`: Int): Boolean = TODO("W11 #14 — swap with last, pop")
    fun getRandom(): Int = TODO("W11 #14 — random index into list")
}

class TimeMap() {
    fun set(key: String, value: String, timestamp: Int) { TODO("W11 #15 — append (ts, value) per key") }
    fun get(key: String, timestamp: Int): String = TODO("W11 #15 — binary search latest ts ≤ given")
}

// ── Trie + Strings ──────────────────────────────────────────────────────────
class WordDictionary() {
    fun addWord(word: String) { TODO("W11 #16 — insert into trie") }
    fun search(word: String): Boolean = TODO("W11 #16 — DFS with '.' wildcard")
}

fun w11FindWords(board: Array<CharArray>, words: List<String>): List<String> = TODO("W11 #17 — trie + backtracking DFS")
fun findMaximumXOR(nums: IntArray): Int = TODO("W11 #18 — bit trie, greedy XOR descent")
fun calculate(s: String): Int = TODO("W11 #19 — running result/sign or paren stack")
fun decodeString(s: String): String = TODO("W11 #20 — count stack + string stack")
