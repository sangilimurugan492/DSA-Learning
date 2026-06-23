package array_traversals.simulation

fun main() {
    println(addDigits(54))
    println(addDigitsOP(54))
}


fun addDigits(num: Int): Int {
    var n = num
    while (n > 9) {
        n = calculateDigits(n)
    }
    return n
}

fun calculateDigits(num : Int) : Int {
    var sum = 0
    var n = num
    while (n > 0) {
        sum += n % 10
        n /= 10
    }

    return sum
}

fun addDigitsOP(num: Int): Int {
    return when {
        num == 0 -> 0
        num % 9 == 0 -> 9
        else -> num % 9
    }
}