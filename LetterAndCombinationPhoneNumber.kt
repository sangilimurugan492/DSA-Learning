package array_traversals

/**
 * https://leetcode.com/problems/letter-combinations-of-a-phone-number/
 */
fun main() {
    println(letterCombinationsBF("23"))
    println(letterCombinationsOP("23"))
}

fun letterCombinationsBF(digits: String): List<String> {
    if (digits.isEmpty()) return emptyList()
    val mapping = arrayOf("0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz")
    var result = mutableListOf("")

    for (digit in digits) {
        val nextResult = mutableListOf<String>()
        val letters = mapping[digit - '0']
        for (combination in result) {
            for (letter in letters) {
                nextResult.add(combination + letter)
            }
        }
        result = nextResult
    }
    return result
}

private val mapping = arrayOf(
    "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
)

fun letterCombinationsOP(digits: String): List<String> {
    if (digits.isEmpty()) return emptyList()
    val result = mutableListOf<String>()

    fun backtrack(index: Int, path: StringBuilder) {
        // Base case: current combination is complete
        if (index == digits.length) {
            result.add(path.toString())
            return
        }

        val letters = mapping[digits[index] - '0']
        for (letter in letters) {
            path.append(letter)          // Choose
            backtrack(index + 1, path)   // Explore
            path.deleteAt(path.length - 1) // Backtrack (Un-choose)
        }
    }

    backtrack(0, StringBuilder())
    return result
}

