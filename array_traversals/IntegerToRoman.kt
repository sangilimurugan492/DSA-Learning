package array_traversals

fun main() {
    println(intToRomanBF(3457))
    println(intToRomanOP(3457))
}

fun intToRomanBF(num: Int): String {
    val thousands = arrayOf("", "M", "MM", "MMM")
    val hundreds = arrayOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
    val tens = arrayOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
    val ones = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

    return thousands[num / 1000] +
            hundreds[(num % 1000) / 100] +
            tens[(num % 100) / 10] +
            ones[num % 10]
}


fun intToRomanOP(num: Int): String {
    var n = num
    val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

    val sb = StringBuilder()
    for (i in values.indices) {
        // Repeat the symbol as many times as its value fits into n
        while (n >= values[i]) {
            sb.append(symbols[i])
            n -= values[i]
        }
    }
    return sb.toString()
}