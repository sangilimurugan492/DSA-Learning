package sorting

fun main() {

    val arrS = charArrayOf('z', 'b', 'g', 'e', 'a')
    val arrI = intArrayOf(65, 23, 2, 75, 2)
    arrS.sort()
    arrS.forEach {
        print("$it , ")
    }

    println()
    arrI.sort()
    arrI.forEach {
        print("$it , ")
    }
}