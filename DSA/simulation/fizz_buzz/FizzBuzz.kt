package array_traversals.simulation

fun main() {
    fizzBuzz(30).forEach{ it ->
        println(it)
    }
}

fun fizzBuzz(n: Int): List<String> {
        val resultList = mutableListOf<String>()
        for (i in 1 .. n) {
            if (i % 3 == 0 && i % 5 == 0)
                resultList.add("FizzBuzz")
        else if (i % 3 == 0)
                resultList.add("Fizz")
        else if (i % 5 == 0)
                resultList.add("Buzz")
            else
                resultList.add("$i")
        }
    return resultList
}