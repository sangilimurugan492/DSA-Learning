package stack

/**
 * https://leetcode.com/problems/daily-temperatures/
 * Given array of temperatures, return array showing days to wait for warmer temp.
 * Example: [73,74,75,71,69,72,76,73] → [1,1,4,2,1,1,0,0]
 * FAANG Importance: ⭐⭐⭐⭐⭐ (#1 Monotonic Stack problem)
 */

fun main() {
    println(dailyTemperaturesBruteForce(intArrayOf(73, 74, 75, 71, 69, 72, 76, 73)).toList())
    println("---")
    println(dailyTemperaturesMonotonicStack(intArrayOf(73, 74, 75, 71, 69, 72, 76, 73)).toList())
}

/**
 * BRUTE FORCE: O(N²) — for each day, scan forward for warmer day
 */
fun dailyTemperaturesBruteForce(temperatures: IntArray): IntArray {
    val result = IntArray(temperatures.size)
    for (i in temperatures.indices) {
        for (j in i + 1 until temperatures.size) {
            if (temperatures[j] > temperatures[i]) {
                result[i] = j - i
                break
            }
        }
    }
    return result
}

/**
 * OPTIMAL: O(N) Monotonic Decreasing Stack
 * Stack stores indices of days waiting for warmer temperature.
 * When we find a warmer day, pop all colder days and calculate their answer.
 *
 * Trace for [73,74,75,71,69,72,76,73]:
 * i=0: stack=[] → push 0. stack=[0]
 * i=1: 74>73 → pop 0, result[0]=1. push 1. stack=[1]
 * i=2: 75>74 → pop 1, result[1]=1. push 2. stack=[2]
 * i=3: 71<75 → push 3. stack=[2,3]
 * i=4: 69<71 → push 4. stack=[2,3,4]
 * i=5: 72>69 → pop 4, result[4]=1. 72>71 → pop 3, result[3]=2. push 5. stack=[2,5]
 * i=6: 76>72 → pop 5, result[5]=1. 76>75 → pop 2, result[2]=4. push 6. stack=[6]
 * i=7: 73<76 → push 7. stack=[6,7]
 * Result: [1,1,4,2,1,1,0,0] ✅
 */
fun dailyTemperaturesMonotonicStack(temperatures: IntArray): IntArray {
    val result = IntArray(temperatures.size)
    val stack = ArrayDeque<Int>()  // stores indices, decreasing temperatures

    for (i in temperatures.indices) {
        while (stack.isNotEmpty() && temperatures[i] > temperatures[stack.last()]) {
            val prevIdx = stack.removeLast()
            result[prevIdx] = i - prevIdx
        }
        stack.addLast(i)
    }
    return result
}
