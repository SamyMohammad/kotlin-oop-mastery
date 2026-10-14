package tools

import java.io.File

/*
 * Build-time verifier (run via Gradle tasks, not by learners).
 *  outputs   : run each lesson twice, require identical output, diff with expected-output/
 *  update    : regenerate expected-output/
 *  exercises : Exercises.kt main must not throw and must print FAIL or PASS lines;
 *              Solutions.kt main must print no FAIL and "ALL PASS".
 */

private val lessonFile = Regex("""L\d\d_\w+\.kt""")

fun normalize(text: String): String =
    text.replace("\r\n", "\n").lines().joinToString("\n") { it.trimEnd() }.trimEnd() + "\n"

/*
 * Each run gets a fresh JVM: `object` init blocks, lazy values and companion
 * state must print the same on every run, which a shared JVM would break.
 */
private fun capture(className: String): String {
    val java = File(System.getProperty("java.home"), "bin/java").path
    val errFile = File.createTempFile("verify", ".err").apply { deleteOnExit() }
    val process = ProcessBuilder(
        java, "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8",
        "-cp", System.getProperty("java.class.path"), className,
    ).redirectError(errFile).start()
    val out = process.inputStream.readBytes().toString(Charsets.UTF_8)
    if (process.waitFor() != 0) {
        val firstError = errFile.readLines().firstOrNull { it.isNotBlank() } ?: "no stderr"
        throw IllegalStateException("$className exited ${process.exitValue()}: $firstError")
    }
    return normalize(out)
}

private fun moduleDirs(root: File): List<File> =
    File(root, "src/main/kotlin").listFiles { f -> f.isDirectory && f.name.matches(Regex("""m\d\d_\w+""")) }
        ?.sortedBy { it.name } ?: emptyList()

private fun lessons(root: File): List<Pair<String, String>> =
    moduleDirs(root).flatMap { dir ->
        dir.listFiles { f -> f.isFile && f.name.matches(lessonFile) }!!.sortedBy { it.name }.map { f ->
            val stem = f.nameWithoutExtension
            "${dir.name}.${stem}Kt" to "${dir.name}__$stem.txt"
        }
    }

fun main(args: Array<String>) {
    val mode = args[0]
    val root = File(args[1])
    val expectedDir = File(root, "expected-output").apply { mkdirs() }
    val errors = mutableListOf<String>()

    when (mode) {
        "outputs", "update" -> for ((cls, file) in lessons(root)) {
            val first = runCatching { capture(cls) }.getOrElse { errors += "$cls threw $it"; continue }
            val second = capture(cls)
            if (first != second) { errors += "$cls output is not deterministic"; continue }
            val target = File(expectedDir, file)
            if (mode == "update") target.writeText(first)
            else if (!target.exists()) errors += "missing expected-output/$file"
            else if (normalize(target.readText()) != first) errors += "output differs: $file"
        }
        "exercises" -> for (dir in moduleDirs(root)) {
            if (!File(dir, "exercises/Exercises.kt").exists()) { errors += "${dir.name}: no exercises"; continue }
            val stub = runCatching { capture("${dir.name}.exercises.ExercisesKt") }
                .getOrElse { errors += "${dir.name}: Exercises main crashed: $it"; continue }
            if ("[FAIL" !in stub && "[PASS]" !in stub) errors += "${dir.name}: Exercises printed no checks"
            val sol = runCatching { capture("${dir.name}.exercises.solutions.SolutionsKt") }
                .getOrElse { errors += "${dir.name}: Solutions main crashed: $it"; continue }
            if ("[FAIL" in sol || "ALL PASS" !in sol) errors += "${dir.name}: Solutions not all PASS"
        }
        "selftest" -> {
            check(normalize("a  \r\nb\r\n\r\n") == "a\nb\n") { "normalize failed" }
            println("selftest ok")
        }
        else -> error("unknown mode $mode")
    }

    if (errors.isNotEmpty()) {
        errors.forEach { System.err.println("VERIFY: $it") }
        throw IllegalStateException("${errors.size} verification error(s)")
    }
    println("verify $mode: ok")
}
