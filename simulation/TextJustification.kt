package simulation

fun main() {
    fullJustify(arrayOf("sdfgsdf"), 10)
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