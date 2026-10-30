package testrunner24

/**
 * TestRunner24.kt — entry point for the 24-Week DSA Plan (271 problems).
 *
 * Each W24WeekNNTests class DELEGATES to the original testrunner/WeekNNTests.kt
 * classes, so implement each solution stub ONCE in testrunner/ and both the old
 * 11-week runner (testrunner.TestRunnerKt) and this 24-week runner pass.
 *
 * Build & run (from PatternWise_Prep/):
 *   kotlinc "testrunner/"*.kt "testrunner24/"*.kt -d out && kotlin -cp out testrunner24.TestRunner24Kt
 *
 * TIP: comment out weeks you've already finished to keep output short —
 * only the active weeks below will run.
 */
fun main() {
    println("╔══════════════════════════════════════════════╗")
    println("║   DSA 24-WEEK PLAN — TEST RUNNER (W1 → W24)  ║")
    println("╚══════════════════════════════════════════════╝")

    W24Week01Tests().runAll()
    W24Week02Tests().runAll()
    W24Week03Tests().runAll()
    W24Week04Tests().runAll()
    W24Week05Tests().runAll()
    W24Week06Tests().runAll()
    W24Week07Tests().runAll()
    W24Week08Tests().runAll()
    W24Week09Tests().runAll()
    W24Week10Tests().runAll()
    W24Week11Tests().runAll()
    W24Week12Tests().runAll()
    W24Week13Tests().runAll()
    W24Week14Tests().runAll()
    W24Week15Tests().runAll()
    W24Week16Tests().runAll()
    W24Week17Tests().runAll()
    W24Week18Tests().runAll()
    W24Week19Tests().runAll()
    W24Week20Tests().runAll()
    W24Week21Tests().runAll()
    W24Week22Tests().runAll()
    W24Week23Tests().runAll()
    W24Week24Tests().runAll()

    println("\n🏁 24-week run complete — update Progress_Tracker.md & Mock_Test_Log.md!")
}
