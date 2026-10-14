package common

/*
 * Output helpers used by every lesson.
 *
 * Output must be deterministic (expected-output/ diffs it), so no timestamps
 * and no thread names. Printed text is plain ASCII on purpose: the Windows
 * console can turn arrows or box characters into "?".
 */

/** Big header for a demo inside a lesson. */
fun section(title: String) {
    println()
    println("=".repeat(60))
    println("  $title")
    println("=".repeat(60))
}

fun log(message: Any?) = println("  $message")

/** A "look at this" line printed after a demo. */
fun note(text: String) = println("   >> $text")
