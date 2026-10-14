package common

/*
 * Tiny check runner for exercises. A stub that calls TODO() must print
 * FAIL, never crash the run - so every check is isolated in check().
 */

private var passed = 0
private var failed = 0

fun check(name: String, block: () -> Boolean) {
    val result = try {
        if (block()) "PASS" else "FAIL"
    } catch (e: NotImplementedError) {
        "FAIL (not implemented yet)"
    } catch (e: Throwable) {
        "FAIL (${e::class.simpleName}: ${e.message})"
    }
    if (result == "PASS") passed++ else failed++
    println("  [$result] $name")
}

fun runCheckSuite(title: String, body: () -> Unit) {
    passed = 0
    failed = 0
    section(title)
    body()
    println()
    println("  Result: $passed passed, $failed failed")
    if (failed == 0) println("  ALL PASS - module mastered.")
}
