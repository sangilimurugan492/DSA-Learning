package simulation.text_justification

/**
 * https://leetcode.com/problems/text-justification/
 *
 * Given an array of strings words and a width maxWidth, format the text such that
 * each line has exactly maxWidth characters and is fully (left and right) justified.
 *
 * Rules:
 * - Greedily pack as many words as possible in each line.
 * - Pad extra spaces ' ' when needed so each line has exactly maxWidth characters.
 * - Distribute extra spaces as evenly as possible between words on the same line.
 *   If the number of spaces does not divide evenly, the left slots get more spaces.
 * - Last line is left-justified (no extra space between words, pad spaces at end).
 * - A line with only one word is left-justified.
 *
 * Example 1:
 * Input: words = ["This", "is", "an", "example", "of", "text", "justification."], maxWidth = 16
 * Output: ["This    is    an","example  of text","justification.  "]
 *
 * Example 2:
 * Input: words = ["What","must","be","acknowledgment","shall","be"], maxWidth = 16
 * Output: ["What   must   be","acknowledgment  ","shall be        "]
 */
fun main() {
    val result = fullJustify(arrayOf("This", "is", "an", "example", "of", "text", "justification."), 16)
    result.forEach { println("[$it]") }
}

fun fullJustify(words: Array<String>, maxWidth: Int): List<String> {
    val result = mutableListOf<String>()
    var i = 0

    while (i < words.size) {
        var j = i + 1
        var lineLength = words[i].length

        // Determine how many words fit in this line
        while (j < words.size && lineLength + 1 + words[j].length <= maxWidth) {
            lineLength += 1 + words[j].length
            j++
        }

        val sb = StringBuilder()
        val numWords = j - i
        val numSpaces = maxWidth - (lineLength - (numWords - 1))

        // Case 1: Last line or only one word in the line (Left Justify)
        if (j == words.size || numWords == 1) {
            for (k in i until j) {
                sb.append(words[k])
                if (k < j - 1) sb.append(" ")
            }
            while (sb.length < maxWidth) sb.append(" ")
        }
        // Case 2: Fully justify (Distribute spaces)
        else {
            val spaceSlots = numWords - 1
            val baseSpaces = numSpaces / spaceSlots
            val extraSpaces = numSpaces % spaceSlots

            for (k in i until j) {
                sb.append(words[k])
                if (k < j - 1) {
                    val spacesToApply = baseSpaces + (if (k - i < extraSpaces) 1 else 0)
                    repeat(spacesToApply) { sb.append(" ") }
                }
            }
        }

        result.add(sb.toString())
        i = j
    }

    return result
}