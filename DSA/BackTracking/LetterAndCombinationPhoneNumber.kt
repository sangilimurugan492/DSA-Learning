package BackTracking

/**
 * https://leetcode.com/problems/letter-combinations-of-a-phone-number/
 *
 * Given a string containing digits 2-9, return all possible letter combinations
 * that the number could represent (like a phone keypad).
 *
 * Example: digits = "23" → ["ad","ae","af","bd","be","bf","cd","ce","cf"]
 *   2 → "abc", 3 → "def"
 *   All combinations: a+d, a+e, a+f, b+d, b+e, b+f, c+d, c+e, c+f
 *
 * FAANG Importance: ⭐⭐⭐⭐ (Classic backtracking — product of choices)
 *
 * ─────────────────────────────────────────────────────────────
 * STEP-BY-STEP UNDERSTANDING:
 * ─────────────────────────────────────────────────────────────
 * This is the simplest backtracking problem — a "product of choices".
 * For each digit, we choose ONE letter from its mapping. No constraints!
 *
 * It's essentially computing the Cartesian product of all letter sets.
 * If digits = "23", mapping = ["abc", "def"], result = 3 × 3 = 9 combinations.
 *
 * Two approaches:
 * 1. ITERATIVE: Start with [""], for each digit, expand all combinations
 * 2. BACKTRACKING: At each position, try each letter, recurse to next digit
 *
 * Connection to other problems:
 *   Letter Combinations → Simplest backtracking (no constraints, no pruning)
 *   Subsets → Backtracking with include/exclude choice
 *   Combination Sum → Backtracking with constraint (sum == target)
 *   Permutations → Backtracking where ALL elements must be used
 *
 * This problem is to backtracking what "Two Sum" is to hashing — the gateway!
 * ─────────────────────────────────────────────────────────────
 */

fun main() {
    println("=== Letter Combinations of Phone Number ===")
    println("Iterative '23':  ${letterCombinationsIterative("23")}")
    println("Backtrack '23':  ${letterCombinations("23")}")
    println("Backtrack '':    ${letterCombinations("")}")
    println("Backtrack '2':   ${letterCombinations("2")}")
}

private val mapping = arrayOf(
    "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
)

/**
 * APPROACH 1: Backtracking
 * Time Complexity: O(4^N × N) — 4^N combinations (worst case: all 7/9), each takes O(N)
 * Space Complexity: O(N) — recursion depth
 *
 * Trace for "23":
 * backtrack(0, "")
 *   letter='a': backtrack(1, "a")
 *     letter='d': backtrack(2, "ad") → ADD "ad" ✅
 *     letter='e': backtrack(2, "ae") → ADD "ae" ✅
 *     letter='f': backtrack(2, "af") → ADD "af" ✅
 *   letter='b': backtrack(1, "b")
 *     letter='d': backtrack(2, "bd") → ADD "bd" ✅
 *     letter='e': backtrack(2, "be") → ADD "be" ✅
 *     letter='f': backtrack(2, "bf") → ADD "bf" ✅
 *   letter='c': backtrack(1, "c")
 *     letter='d': backtrack(2, "cd") → ADD "cd" ✅
 *     letter='e': backtrack(2, "ce") → ADD "ce" ✅
 *     letter='f': backtrack(2, "cf") → ADD "cf" ✅
 *
 * Total: 3 × 3 = 9 combinations ✅
 */
fun letterCombinations(digits: String): List<String> {
    if (digits.isEmpty()) return emptyList()
    val result = mutableListOf<String>()

    fun backtrack(index: Int, path: StringBuilder) {
        // Base case: all digits processed
        if (index == digits.length) {
            result.add(path.toString())
            return
        }

        val letters = mapping[digits[index] - '0']
        for (letter in letters) {
            path.append(letter)              // CHOOSE
            backtrack(index + 1, path)       // EXPLORE
            path.deleteAt(path.length - 1)   // UNDO (backtrack)
        }
    }

    backtrack(0, StringBuilder())
    return result
}

/**
 * APPROACH 2: Iterative (BFS-like)
 * Time Complexity: O(4^N × N)
 * Space Complexity: O(4^N) — storing all combinations
 *
 * Trace for "23":
 * Start: [""]
 * Process '2' (abc): ["a", "b", "c"]
 * Process '3' (def): ["ad","ae","af","bd","be","bf","cd","ce","cf"]
 *
 * Each digit multiplies the number of combinations by its letter count.
 */
fun letterCombinationsIterative(digits: String): List<String> {
    if (digits.isEmpty()) return emptyList()
    val digitMapping = arrayOf("0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz")
    var result = mutableListOf("")

    for (digit in digits) {
        val nextResult = mutableListOf<String>()
        val letters = digitMapping[digit - '0']
        for (combination in result) {
            for (letter in letters) {
                nextResult.add(combination + letter)
            }
        }
        result = nextResult
    }
    return result
}
