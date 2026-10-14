# Kotlin OOP Mastery Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a self-study Kotlin JVM project that teaches every OOP concept in Kotlin across 19 modules (00–18), each with runnable lessons, auto-checked exercises, and a deep Arabic `.md` doc with real Jetpack Compose examples.

**Architecture:** Plain Kotlin JVM Gradle project, no dependencies. One package per module (`mNN_<name>`), each with lesson files (`fun main()`), a module-local `shop/` snapshot of the e-commerce domain, and `exercises/` (contract + checks, stubs, solutions). A Kotlin tool (`tools/Verify.kt`) runs every lesson and diffs against `expected-output/`, and runs every exercise main to prove stubs fail cleanly and solutions pass. A Python script lints the Arabic markdown for bidi rules.

**Tech Stack:** Kotlin 2.2.10 (JVM 17), Gradle 9.4.1 wrapper (copied from `flow-viewmodel-lessons`), Python 3 (lint script only).

**Spec:** `docs/superpowers/specs/2026-10-14-kotlin-oop-mastery-design.md`

## Global Constraints

- Project root: `C:\Users\Lenovo\StudioProjects\kotlin-oop-mastery` (git repo, branch `main`, already contains the spec).
- `kotlin("jvm") version "2.2.10"`, `JavaVersion.VERSION_17`, `JvmTarget.JVM_17`, **zero** `dependencies {}` entries.
- No Android or Compose dependency. Compose code appears **only** inside `.md` fenced blocks, each preceded by the Arabic note line `ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.` directly above the block.
- Code, identifiers, and code comments in English. Arabic lives only in `.md` files and `README.md`.
- Lesson output is deterministic: no timestamps, no thread names, no raw `hashCode()`/identity hashes (print `same`/`different` instead), no `Random` without a fixed seed, no `HashMap`/`HashSet` iteration order in output (use `listOf`/`linkedMapOf`/`sortedBy`).
- Printed text is plain ASCII (Windows console turns arrows/box chars into `?`). Use `->` in printed strings, never `→`.
- Each lesson file: one concept, 60–150 lines, `fun main()` at the bottom, must not call `exitProcess`.
- Each module: 2–4 lessons (`L01_…`, `L02_…`), files named `L0N_PascalTopic.kt`, package `mNN_<name>`.
- Expected-output file name: `<package>__<LessonFileNameWithoutExt>.txt`, e.g. `m01_classes__L01_Constructors.txt`.
- Exercise layout per module (modules 00–18): `exercises/Checks.kt` (contract interfaces + `fun runChecks(impl)`, student never edits), `exercises/Exercises.kt` (stubs using `TODO()` + `fun main() = runChecks(...)`), `exercises/solutions/Solutions.kt` (working impl + `fun main() = runChecks(...)`).
  - Deviation from spec §3 noted: the spec's `USE_SOLUTIONS` flag is replaced by this two-`main` pattern (the same pattern `flow-viewmodel-lessons` uses). The spec allows "an interface or a function-reference table"; this is the interface variant. Running `Exercises.kt` checks your work; running `Solutions.kt` checks the reference.
- Markdown bidi rules (enforced by `tools/check_bidi.py`):
  - No characters U+200E, U+200F, U+202A–U+202E, U+2066–U+2069, and no escape text like `\u200f` or `&rlm;`.
  - Every line containing Arabic letters starts (after markdown markers `#`, `>`, `-`, `*`, `+`, `|`, `1.`, spaces, `**`) with an Arabic letter.
  - Every table cell containing Arabic starts with an Arabic letter.
  - `->`, `=>`, `→` never appear on a line containing Arabic (outside code fences). Use `←`.
  - Lines fully in English are allowed. Code fences are skipped by the lint.
- Doc file names: `docs/NN_<topic>.md` exactly as listed in Task 3–21.
- Commit after every task with a message ending in `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.

## Review Focus

1. **Stub run crashes instead of printing FAIL** — `TODO()` throws `NotImplementedError`; any check not wrapped by `check()` kills the run. Expected: every check prints `FAIL (not implemented yet)` and the run ends with a summary. Pinned by `Verify.kt` exercise mode (Task 1) which fails if `ExercisesKt.main` throws.
2. **Non-deterministic lesson output** (identity hashes, `HashSet` order, `toString()` of a non-data class like `Product@1b6d3586`). Expected: two runs produce byte-identical output. Pinned by `Verify.kt` running every lesson **twice**, each in a fresh JVM (so `object` init prints are not mistaken for drift), and failing on mismatch (Task 1).
3. **Windows line endings / encoding** — git `core.autocrlf` turned the spec into CRLF. Expected: expected-output comparison ignores `\r\n` vs `\n` and trailing whitespace. Pinned by `normalize()` in `Verify.kt` and `verifySelfTest` (Task 1).
   Also: exercise checks must not use `kotlin-reflect`-only APIs (`KClass.isData`, `isValue`, `isInner`, `supertypes` throw without the library). Checks use Java reflection or `isInstance` instead.
4. **Arabic line starting with a backticked English term or bold English** (`**`sealed`** ...`). Expected: lint rejects it. Pinned by `tools/test_check_bidi.py` cases (Task 1).
5. **Invented Compose API** in an "In Compose" section. Expected: every Compose symbol quoted in a doc exists in `docs/reference/compose_signatures.md`. Pinned by `tools/check_compose_refs.py` (Task 2) and run in every module task's verification step.

---

## File Map

```
kotlin-oop-mastery/
├── .gitignore, .gitattributes, build.gradle.kts, settings.gradle.kts, gradle.properties
├── gradlew, gradlew.bat, gradle/wrapper/*            (copied)
├── README.md                                          Task 1 skeleton, Task 22 final
├── docs/NN_*.md                                       Tasks 3–21
├── docs/cheatsheet.md                                 Task 22
├── docs/reference/compose_signatures.md               Task 2
├── expected-output/*.txt                              generated + reviewed per module
├── tools/check_bidi.py, tools/test_check_bidi.py      Task 1
├── tools/check_compose_refs.py                        Task 2
└── src/main/kotlin/
    ├── common/Log.kt, common/Checks.kt                Task 1
    ├── tools/Verify.kt                                Task 1
    └── mNN_<name>/...                                 Tasks 3–21
```

---

### Task 1: Scaffold, helpers, verification tooling

**Files:**
- Create: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `.gitignore`, `.gitattributes`
- Copy: `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties` from `C:\Users\Lenovo\StudioProjects\flow-viewmodel-lessons`
- Create: `src/main/kotlin/common/Log.kt`, `src/main/kotlin/common/Checks.kt`, `src/main/kotlin/tools/Verify.kt`
- Create: `tools/check_bidi.py`, `tools/test_check_bidi.py`
- Create: `src/main/kotlin/m99_pipeline/L01_Pipeline.kt` (temporary pipeline proof, deleted in Step 12)
- Create: `README.md` (skeleton)

**Interfaces:**
- Produces: `common.section(title: String)`, `common.log(message: Any?)`, `common.note(text: String)`, `common.check(name: String, block: () -> Boolean)`, `common.runCheckSuite(title: String, body: () -> Unit)`; Gradle tasks `verifyOutputs`, `updateOutputs`, `verifyExercises`, `verifyAll`.

- [ ] **Step 1: Copy wrapper and write Gradle files**

```bash
cd /c/Users/Lenovo/StudioProjects/kotlin-oop-mastery
cp ../flow-viewmodel-lessons/gradlew ../flow-viewmodel-lessons/gradlew.bat .
mkdir -p gradle/wrapper && cp ../flow-viewmodel-lessons/gradle/wrapper/* gradle/wrapper/
```

`settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "kotlin-oop-mastery"
```

`build.gradle.kts`:
```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Same Kotlin version as flow-viewmodel-lessons, already cached on this machine
    kotlin("jvm") version "2.2.10"
}

// No dependencies on purpose: OOP is pure Kotlin. Compose lives in the .md docs only.

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

fun verifyTask(name: String, mode: String, desc: String) =
    tasks.register<JavaExec>(name) {
        group = "verification"
        description = desc
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("tools.VerifyKt")
        args(mode, project.projectDir.absolutePath)
    }

verifyTask("verifyOutputs", "outputs", "Run every lesson and diff against expected-output/")
verifyTask("updateOutputs", "update", "Regenerate expected-output/ from current lesson output")
verifyTask("verifyExercises", "exercises", "Stubs must print FAIL without crashing; solutions must be all PASS")
verifyTask("verifySelfTest", "selftest", "Self-test of the verifier's output normalization")

tasks.register("verifyAll") {
    group = "verification"
    dependsOn("build", "verifySelfTest", "verifyOutputs", "verifyExercises")
}
```

`gradle.properties`:
```
org.gradle.jvmargs=-Xmx1g -Dfile.encoding=UTF-8
kotlin.code.style=official
```

`.gitignore`:
```
.gradle/
build/
out/
.idea/
.kotlin/
*.iml
local.properties
__pycache__/
```

`.gitattributes` (stop CRLF churn on Windows):
```
* text=auto eol=lf
*.bat text eol=crlf
*.jar binary
```

- [ ] **Step 2: Write `common/Log.kt`**

```kotlin
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
```

- [ ] **Step 3: Write `common/Checks.kt`**

```kotlin
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
```

- [ ] **Step 4: Write `tools/Verify.kt`**

```kotlin
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
```

- [ ] **Step 5: Write the pipeline-proof module**

`src/main/kotlin/m99_pipeline/L01_Pipeline.kt`:
```kotlin
package m99_pipeline

import common.log
import common.section

fun main() {
    section("Pipeline check")
    log("hello")
}
```

`src/main/kotlin/m99_pipeline/exercises/Checks.kt`:
```kotlin
package m99_pipeline.exercises

import common.check
import common.runCheckSuite

interface PipelineContract {
    fun double(x: Int): Int
}

fun runChecks(impl: PipelineContract) = runCheckSuite("m99 checks") {
    check("double(2) == 4") { impl.double(2) == 4 }
}
```

`src/main/kotlin/m99_pipeline/exercises/Exercises.kt`:
```kotlin
package m99_pipeline.exercises

object MyPipeline : PipelineContract {
    override fun double(x: Int): Int = TODO()
}

fun main() = runChecks(MyPipeline)
```

`src/main/kotlin/m99_pipeline/exercises/solutions/Solutions.kt`:
```kotlin
package m99_pipeline.exercises.solutions

import m99_pipeline.exercises.PipelineContract
import m99_pipeline.exercises.runChecks

object PipelineSolution : PipelineContract {
    override fun double(x: Int): Int = x * 2
}

fun main() = runChecks(PipelineSolution)
```

- [ ] **Step 6: Run the verifier before expected output exists — must fail**

Run: `./gradlew build verifyOutputs`
Expected: FAIL with `VERIFY: missing expected-output/m99_pipeline__L01_Pipeline.txt`

- [ ] **Step 7: Generate expected output and re-verify**

Run: `./gradlew updateOutputs verifyOutputs verifyExercises`
Expected: `verify outputs: ok` and `verify exercises: ok`. Open `expected-output/m99_pipeline__L01_Pipeline.txt` and confirm it contains the header and `  hello`.

Run: `./gradlew verifySelfTest`
Expected: `selftest ok` and `verify selftest: ok`

- [ ] **Step 8: Write the failing bidi lint tests**

`tools/test_check_bidi.py`:
```python
import unittest
from check_bidi import lint_text

class BidiLint(unittest.TestCase):
    def errs(self, text):
        return lint_text(text, "t.md")

    def test_clean_arabic_line(self):
        self.assertEqual(self.errs("الـ `class` هو القالب.\n"), [])

    def test_english_only_line_ok(self):
        self.assertEqual(self.errs("Kotlin classes are final by default.\n"), [])

    def test_rlm_rejected(self):
        self.assertTrue(self.errs("\u200fالكلاس\n"))

    def test_escape_text_rejected(self):
        self.assertTrue(self.errs("الكلاس \\u200f هنا\n"))

    def test_line_starting_with_backtick_rejected(self):
        self.assertTrue(self.errs("`sealed` يعني مقفول\n"))

    def test_bold_english_start_rejected(self):
        self.assertTrue(self.errs("- **Modifier** هو interface\n"))

    def test_heading_and_bullet_markers_skipped(self):
        self.assertEqual(self.errs("## الوراثة\n- الكلاس `open`\n1. أول خطوة\n"), [])

    def test_arrow_in_arabic_rejected(self):
        self.assertTrue(self.errs("الكلاس -> الأوبجكت\n"))

    def test_left_arrow_allowed(self):
        self.assertEqual(self.errs("الكلاس ← الأوبجكت\n"), [])

    def test_code_fence_skipped(self):
        self.assertEqual(self.errs("```\n`x` -> الكود\n```\n"), [])

    def test_table_cell_must_start_arabic(self):
        self.assertTrue(self.errs("| `open` كلاس | تمام |\n"))
        self.assertEqual(self.errs("| الكلمة `open` | تمام |\n"), [])

    def test_details_summary_line(self):
        self.assertEqual(self.errs("<details><summary>الإجابة</summary>\n"), [])

    def test_checkbox_marker_skipped(self):
        self.assertEqual(self.errs("- [ ] ملف `Exercises.kt` بيطبع ALL PASS\n"), [])

if __name__ == "__main__":
    unittest.main()
```

Run: `cd tools && python -m unittest test_check_bidi -v`
Expected: FAIL with `ModuleNotFoundError: No module named 'check_bidi'`

- [ ] **Step 9: Implement `tools/check_bidi.py`**

```python
"""Lint Arabic markdown so it renders correctly in left-to-right chat/IDE views.

Usage: python tools/check_bidi.py docs README.md
Exit code 1 when any violation is found.
"""
import re
import sys
from pathlib import Path

ARABIC = re.compile(r"[\u0600-\u06FF]")
CONTROL = re.compile(r"[\u200E\u200F\u202A-\u202E\u2066-\u2069]")
ESCAPES = re.compile(r"\\u200[ef]|\\u202[a-e]|\\u206[6-9]|&rlm;|&lrm;", re.IGNORECASE)
ARROWS = ("->", "=>", "\u2192")
LEADING = re.compile(r"^(\s*(#{1,6}|>|[-*+]|\d+\.|\[[ xX]\])\s+)*\s*")
HTML_TAGS = re.compile(r"^(<[^>]+>)+")


def first_strong_is_arabic(fragment: str) -> bool:
    s = LEADING.sub("", fragment, count=1)
    s = HTML_TAGS.sub("", s).lstrip()
    s = s.lstrip("*_ ").lstrip()
    return bool(s) and bool(ARABIC.match(s[0]))


def lint_text(text: str, name: str) -> list[str]:
    errors = []
    in_fence = False
    for n, line in enumerate(text.splitlines(), 1):
        where = f"{name}:{n}"
        if line.strip().startswith("```"):
            in_fence = not in_fence
            continue
        if CONTROL.search(line):
            errors.append(f"{where}: bidi control character")
        if ESCAPES.search(line):
            errors.append(f"{where}: bidi escape text")
        if in_fence or not ARABIC.search(line):
            continue
        if any(a in line for a in ARROWS):
            errors.append(f"{where}: arrow on Arabic line, use \u2190")
        if line.lstrip().startswith("|"):
            for cell in line.strip().strip("|").split("|"):
                if ARABIC.search(cell) and not first_strong_is_arabic(cell.strip()):
                    errors.append(f"{where}: table cell must start with Arabic: {cell.strip()[:30]}")
        elif not first_strong_is_arabic(line):
            errors.append(f"{where}: Arabic line must start with an Arabic word")
    return errors


def main(paths: list[str]) -> int:
    files = []
    for p in map(Path, paths):
        files += sorted(p.rglob("*.md")) if p.is_dir() else [p]
    files = [f for f in files if "superpowers" not in f.parts]
    errors = []
    for f in files:
        errors += lint_text(f.read_text(encoding="utf-8"), str(f))
    for e in errors:
        print(e)
    print(f"check_bidi: {len(files)} files, {len(errors)} errors")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:] or ["docs", "README.md"]))
```

Run: `cd tools && python -m unittest test_check_bidi -v`
Expected: all 13 tests PASS. If `test_details_summary_line` fails, fix `HTML_TAGS` stripping, not the test.

- [ ] **Step 10: Write README skeleton**

`README.md` (Arabic, bidi-clean). Sections: title `# Kotlin OOP Mastery`, `## إزاي تفتحه وتشغّله` (IntelliJ Open folder, Gradle sync, green arrow next to `fun main()`), `## إزاي تذاكر كل module` (read the md ← run lessons ← solve `Exercises.kt` ← run it until ALL PASS ← compare with `Solutions.kt`), `## الـ modules` with a table header only (filled in Task 22), and a fenced block listing the verification commands:
```
./gradlew verifyAll
python tools/check_bidi.py docs README.md
python tools/check_compose_refs.py
```

Run: `python tools/check_bidi.py README.md`
Expected: `check_bidi: 1 files, 0 errors`

- [ ] **Step 11: Commit scaffold (with pipeline module)**

```bash
git add -A
git commit -m "build: scaffold Gradle project, helpers and verification tooling

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

- [ ] **Step 12: Remove the pipeline-proof module and commit**

```bash
git rm -r -q src/main/kotlin/m99_pipeline expected-output/m99_pipeline__L01_Pipeline.txt
./gradlew build
git commit -m "chore: drop pipeline proof module

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```
Expected: build green (verifyOutputs with zero lessons prints `verify outputs: ok`).

---

### Task 2: Compose signature reference + reference checker

**Files:**
- Create: `docs/reference/compose_signatures.md`
- Create: `tools/check_compose_refs.py`

**Interfaces:**
- Produces: `docs/reference/compose_signatures.md` — one `### <Symbol>` heading per API (heading text is the exact symbol, e.g. `### Modifier`, `### Dp`, `### RowScope.weight`), each followed by a ```kotlin block with the real signature, the artifact (`androidx.compose.ui:ui`, etc.), and a one-line English note on which OOP concept it demonstrates. Module docs reference these symbols inside an "In Compose" section using inline code.

- [ ] **Step 1: Research signatures (dispatch `researcher` agent)**

Use context7 (`resolve-library-id` "jetpack compose" / "androidx compose", then `query-docs`) and developer.android.com reference pages. Collect the real current signature for every symbol below. Record nothing that was not found in a source; if a symbol cannot be verified, replace it with a verified alternative demonstrating the same concept and note the swap.

Symbols (grouped by module):
- m00: `@Composable`, `Composer`
- m01: `Text` (composable signature), `Card`
- m02: `ViewModel`, `MutableStateFlow`, `StateFlow`, `asStateFlow`
- m03: `ComponentActivity`, `ViewModel.onCleared`
- m04: `Painter` (abstract `intrinsicSize`, `onDraw`), `Modifier.Node`
- m05: `Modifier` (interface + `then`, `foldIn`, `Element`), `Shape` (`createOutline`), `Arrangement.Horizontal`, `Indication`
- m06: `BitmapPainter`, `VectorPainter`, `GenericShape`, `RoundedCornerShape`, `Image`
- m07: `@Immutable`, `@Stable`
- m08: `NavHost`, `CircularProgressIndicator`, `TextFieldValue` (counter-example: plain class), sealed UiState pattern (no API, mark as pattern)
- m09: `Modifier.Companion`, `MaterialTheme`, `Dp.Companion.Unspecified`
- m10: `Dp`, `Color`, `TextUnit`, `.dp` extension
- m11: `PreviewParameterProvider`, `@PreviewParameter`
- m12: `remember`, `mutableStateOf`, `MutableState`, `State.getValue` (delegate operator), `viewModels`, `collectAsStateWithLifecycle`
- m13: `State<out T>`, `LazyListScope.items`, `LazyColumn`
- m14: `RowScope.weight`, `BoxScope.align`, `Modifier.padding`, `Row`, `Box`
- m15: `Dp.plus`, `Offset` (`plus`, `minus`, `times`), `Modifier.then`
- m16: `Scaffold` (slot parameters `topBar`, `bottomBar`, `content`), `Button` (`content: @Composable RowScope.() -> Unit`), `Icon`
- m17: `hiltViewModel` is out of scope (no DI lib) — use constructor injection pattern; `CompositionLocal`, `staticCompositionLocalOf`, `CompositionLocalProvider`, `Modifier.clip`
- m18: `snapshotFlow`, `derivedStateOf`, `Modifier.composed` (note deprecated status if so), `LazyListState`, `rememberLazyListState`

- [ ] **Step 2: Write `docs/reference/compose_signatures.md`**

English file (no Arabic, so bidi lint passes trivially). Header line: `Verified on 2026-10-14 against <sources>.` Then one `### Symbol` section per symbol, format:

````markdown
### Modifier
Artifact: `androidx.compose.ui:ui`
Concept: interface with a companion object that is itself the empty Modifier (m05, m09)
```kotlin
interface Modifier {
    fun <R> foldIn(initial: R, operation: (R, Element) -> R): R
    // ...
    infix fun then(other: Modifier): Modifier = ...
    interface Element : Modifier { ... }
    companion object : Modifier { ... }
}
```
````

- [ ] **Step 3: Write `tools/check_compose_refs.py`**

```python
"""Every `Symbol` cited in a doc's "In Compose" section must exist in the reference.

A citation is inline code inside the section that starts at a heading containing
"Compose" and ends at the next heading of the same or higher level.
Only PascalCase symbols, dotted symbols (RowScope.weight) and @Annotations are checked.
"""
import re
import sys
from pathlib import Path

REF = Path("docs/reference/compose_signatures.md")
CITED = re.compile(r"`(@?[A-Z][A-Za-z0-9]*(?:\.[A-Za-z][A-Za-z0-9]*)*)`")
HEADING = re.compile(r"^(#{1,6})\s+(.*)")

# Kotlin/stdlib/domain names allowed in Compose sections without a reference entry
ALLOW = {"String", "Int", "Long", "Boolean", "Unit", "List", "Any", "Nothing",
         "Product", "Cart", "Order", "Payment", "Price", "UiState", "ProductCard",
         "OrderStatus", "CartScreen", "ProductListScreen", "ShopViewModel"}


def known_symbols() -> set[str]:
    names = set()
    for line in REF.read_text(encoding="utf-8").splitlines():
        m = HEADING.match(line)
        if m and m.group(1) == "###":
            names.add(m.group(2).strip())
    return names


def cited_in_compose_sections(text: str) -> list[tuple[int, str]]:
    out, level, in_fence = [], None, False
    for n, line in enumerate(text.splitlines(), 1):
        if line.strip().startswith("```"):
            in_fence = not in_fence
            continue
        m = HEADING.match(line) if not in_fence else None
        if m:
            depth = len(m.group(1))
            if "Compose" in m.group(2):
                level = depth
            elif level is not None and depth <= level:
                level = None
            continue
        if level is not None and not in_fence:
            out += [(n, s) for s in CITED.findall(line)]
    return out


def main() -> int:
    known = known_symbols()
    errors = []
    for doc in sorted(Path("docs").glob("[0-9][0-9]_*.md")):
        for n, sym in cited_in_compose_sections(doc.read_text(encoding="utf-8")):
            if sym not in known and sym not in ALLOW and sym.split(".")[0] not in ALLOW:
                errors.append(f"{doc}:{n}: `{sym}` not in {REF}")
    for e in errors:
        print(e)
    print(f"check_compose_refs: {len(known)} reference symbols, {len(errors)} errors")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
```

- [ ] **Step 4: Verify**

Run: `python tools/check_compose_refs.py`
Expected: `check_compose_refs: <N> reference symbols, 0 errors` with N ≥ 40.

- [ ] **Step 5: Commit**

```bash
git add docs/reference tools/check_compose_refs.py
git commit -m "docs: add verified Compose signature reference and checker

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

## Module Task Template (applies to Tasks 3–21)

Every module task follows these steps. The per-task sections below give the module-specific content: lessons, shop snapshot, exercise contracts (verbatim), checks (verbatim), and doc requirements.

**Steps for every module task:**

- [ ] **Step A: Write `exercises/Checks.kt` exactly as given in the task** (contract + `runChecks`).
- [ ] **Step B: Write `exercises/Exercises.kt`** — implement every contract member with `TODO()`; a KDoc above each member restates the task in English (one or two sentences); end with `fun main() = runChecks(<StubObject>)`.
- [ ] **Step C: Verify stubs fail cleanly.** Run: `./gradlew build verifyExercises` — Expected: FAIL with `VERIFY: mNN_x: Solutions main crashed` (solutions not written yet), and **no** `Exercises main crashed` line.
- [ ] **Step D: Write `exercises/solutions/Solutions.kt`** implementing the contract; `fun main() = runChecks(<SolutionObject>)`.
- [ ] **Step E: Run** `./gradlew build verifyExercises` — Expected: `verify exercises: ok`.
- [ ] **Step F: Write the shop snapshot** (`mNN_x/shop/*.kt`, package `mNN_x.shop`) and the lessons listed. Each lesson prints with `section`/`log`/`note` only.
- [ ] **Step G: Generate and review output.** Run: `./gradlew updateOutputs verifyOutputs`. Open every new `expected-output/mNN_x__*.txt` and confirm each line matches what the lesson's comments claim (this is the lesson's test — a wrong expectation here teaches a wrong fact). Run `git diff --stat expected-output` and confirm only this module's files changed.
- [ ] **Step H: Write `docs/NN_<topic>.md`** following the spec §4 anatomy with these exact Arabic headings, in order:
  ```
  # <Module title in English>
  ## المشكلة الأول
  ## الـ concept بالتفصيل
  ## امشي مع الكود
  ## في Compose
  ## أخطاء شائعة
  ## أسئلة Interview
  ## Self-check
  ```
  - "امشي مع الكود": one `###` per lesson file linking it as `[L01_X.kt](../src/main/kotlin/mNN_x/L01_X.kt)` and quoting its expected output in a fenced block, then explaining it.
  - "في Compose": cite only symbols present in `docs/reference/compose_signatures.md`; each Compose code block is preceded by the line `ملاحظة: الكود ده Compose للشرح بس، مش بيتشغل في المشروع ده.`
  - "أسئلة Interview": 5–8 items, each answer in `<details><summary>الإجابة</summary>` … `</details>`.
  - "Self-check": `- [ ]` checklist, last item `- [ ] ملف Exercises.kt بيطبع ALL PASS`.
  - Depth floor: the doc is at least 250 lines.
- [ ] **Step I: Verify everything.**
  Run: `./gradlew verifyAll && python tools/check_bidi.py docs README.md && python tools/check_compose_refs.py`
  Expected: `BUILD SUCCESSFUL`, `0 errors`, `0 errors`.
- [ ] **Step J: Commit** `git add -A && git commit -m "feat(mNN): <module title> lessons, exercises and doc"` with the co-author trailer.

---

### Task 3: m00 — OOP mental model

**Files:** `src/main/kotlin/m00_mental_model/{L01_ProceduralVsObjects.kt, L02_FourPillarsTour.kt, L03_TypeHierarchy.kt}`, `exercises/{Checks.kt, Exercises.kt, solutions/Solutions.kt}`, `docs/00_oop_mental_model.md`

**Lessons:**
- `L01_ProceduralVsObjects`: same cart total written as (a) loose `MutableList<Pair<String, Long>>` + top-level functions, (b) a `Cart` class owning its list. Print both totals (equal), then show the procedural version accepting an invalid negative price while `Cart.add` rejects it (`require`, caught and printed).
- `L02_FourPillarsTour`: one tiny example each — encapsulation (`private` balance), inheritance (`open class Payment` → `CardPayment`), polymorphism (list of `Payment` calling `describe()`), abstraction (`interface Shipping`). Print one line per pillar.
- `L03_TypeHierarchy`: `Any` (everything is `Any`, print `x is Any` for Int/String/Cart), `Unit` (function returning Unit prints `kotlin.Unit`), `Nothing` (`fun fail(msg: String): Nothing = throw ...` used in an elvis; show type inference makes the expression non-null), `Any?` vs `Any`.

**`exercises/Checks.kt`:**
```kotlin
package m00_mental_model.exercises

import common.check
import common.runCheckSuite

/** Predict-the-output quiz. Answer each question with the exact text the code prints. */
interface M00Quiz {
    /** println(Cart().apply { add("Pen", 500) }.total()) where total is the sum of prices -> ? */
    fun q1(): String
    /** println((42 as Any) is Any) -> ? */
    fun q2(): String
    /** fun hello() { }  println(hello()) -> ? */
    fun q3(): String
    /** Which pillar hides internal state behind methods? One word, lowercase. */
    fun q4(): String
    /** val s: String = null ?: fail("x")  where fail returns Nothing. What is thrown? Simple class name. */
    fun q5(): String
}

fun runChecks(quiz: M00Quiz) = runCheckSuite("m00 quiz") {
    check("q1 total") { quiz.q1() == "500" }
    check("q2 Any") { quiz.q2() == "true" }
    check("q3 Unit") { quiz.q3() == "kotlin.Unit" }
    check("q4 pillar") { quiz.q4() == "encapsulation" }
    check("q5 Nothing") { quiz.q5() == "IllegalStateException" }
}
```
(L03 must define `fail` as `throw IllegalStateException(msg)` so q5 is learnable.)

**Doc `docs/00_oop_mental_model.md`:** objects = state + behaviour + identity; class vs object; the four pillars with a one-line shop example each; Kotlin type hierarchy diagram in a code block (`Any?` at top, `Nothing` at bottom); Kotlin defaults vs Java (final by default, no `static`, properties not fields). In Compose: `@Composable` functions are not objects, yet `Composer` and the APIs under them are — preview of m05/m10/m12.

---

### Task 4: m01 — Classes & objects

**Files:** `src/main/kotlin/m01_classes/{L01_Constructors.kt, L02_InitOrder.kt, L03_Properties.kt, L04_LateinitAndLazy.kt}`, `shop/Product.kt`, `exercises/*`, `docs/01_classes_and_objects.md`

**Shop snapshot `shop/Product.kt`:** `class Product(val id: Int, val name: String, priceCents: Long)` with `init { require(priceCents >= 0) }`, `var priceCents = priceCents; private set`, `fun applyDiscount(percent: Int)`.

**Lessons:**
- `L01_Constructors`: primary constructor with `val`/`var`/plain params; default args; named args; secondary constructor delegating with `this(...)`; print which constructor ran.
- `L02_InitOrder`: class with property initializers and two `init` blocks interleaved; each prints when it runs; show top-to-bottom order; show that a property declared after an `init` is still `null`/`0` when read inside it (use a `String?` to keep it visible).
- `L03_Properties`: custom getter (computed `priceLabel`), custom setter with `field` validating stock ≥ 0, `private set`, `const val` in companion vs `val`; show getter recomputes each access (counter).
- `L04_LateinitAndLazy`: `lateinit var` + `::x.isInitialized`; catching `UninitializedPropertyAccessException` and printing its simple name; `by lazy` printing "computing slug" exactly once across two accesses.

**`exercises/Checks.kt`:**
```kotlin
package m01_classes.exercises

import common.check
import common.runCheckSuite

interface ProductSpec {
    val id: Int
    val name: String
    val priceCents: Long
    /** "EGP 12.50" for 1250 cents; always two decimals. */
    val priceLabel: String
    /** Setting a negative value is ignored (keeps the old value). Starts at 0. */
    var stock: Int
    /** name lowercased, spaces replaced by '-'. Computed once (lazy). */
    val slug: String
}

interface M01Factory {
    /** Throws IllegalArgumentException when priceCents < 0 or name is blank. */
    fun product(id: Int, name: String, priceCents: Long): ProductSpec
    /** Secondary-constructor style: id defaults to -1. */
    fun draft(name: String, priceCents: Long): ProductSpec
    /** How many times the slug computation ran in total, across all products created by this factory. */
    fun slugComputations(): Int
}

fun runChecks(f: M01Factory) = runCheckSuite("m01 checks") {
    check("price label formats cents") { f.product(1, "Pen", 1250).priceLabel == "EGP 12.50" }
    check("price label pads zero") { f.product(1, "Pen", 5).priceLabel == "EGP 0.05" }
    check("negative price rejected") {
        runCatching { f.product(1, "Pen", -1) }.exceptionOrNull() is IllegalArgumentException
    }
    check("blank name rejected") {
        runCatching { f.product(1, " ", 10) }.exceptionOrNull() is IllegalArgumentException
    }
    check("draft id is -1") { f.draft("Mug", 100).id == -1 }
    check("stock ignores negatives") {
        val p = f.product(1, "Pen", 10); p.stock = 5; p.stock = -3; p.stock == 5
    }
    check("slug format") { f.product(1, "Blue Pen XL", 10).slug == "blue-pen-xl" }
    check("slug computed once") {
        val before = f.slugComputations()
        val p = f.product(2, "Cup", 10); p.slug; p.slug; p.slug
        f.slugComputations() - before == 1
    }
}
```

**Doc:** constructors (primary/secondary/default/named), parameter vs property, init order with diagram, backing field `field` and when it exists, `private set`, computed vs stored property, `lateinit` rules (non-null, var, non-primitive), `lazy` thread-safety modes, Java equivalent of a Kotlin class (getters/setters generated). In Compose: a `Product` model rendered by a `ProductCard` composable using `Card` and `Text`; why UI models should be stable/read-only (pointer to m07).

---

### Task 5: m02 — Encapsulation & visibility

**Files:** `src/main/kotlin/m02_encapsulation/{L01_VisibilityModifiers.kt, L02_PrivateSetAndBackingProperty.kt, L03_InvariantsAndDefensiveCopies.kt}`, `shop/Cart.kt`, `exercises/*`, `docs/02_encapsulation.md`

**Shop snapshot `shop/Cart.kt`:** `class Cart` with `private val _items = mutableListOf<CartLine>()`, `val items: List<CartLine> get() = _items.toList()`, `fun add(productId: Int, qty: Int)`, `fun remove(productId: Int)`, `val totalQty`. `data class CartLine(val productId: Int, val qty: Int)` (data class used here only as a value holder; explained in m07).

**Lessons:**
- `L01_VisibilityModifiers`: `public`/`private`/`protected`/`internal` on members and top-level declarations; a subclass accessing `protected`; comment explaining `internal` = same Gradle module; `private` top-level = same file.
- `L02_PrivateSetAndBackingProperty`: `var count: Int private set`; `_items`/`items` pattern; show that exposing `MutableList` as `List` still allows a cast `(cart.items as MutableList).add(...)` when not copied — print the corruption — then fix with `toList()`.
- `L03_InvariantsAndDefensiveCopies`: `Cart.add` rejects qty ≤ 0 and merges lines with same productId; mutating the list you passed in after constructing does not affect the object (constructor copies).

**`exercises/Checks.kt`:**
```kotlin
package m02_encapsulation.exercises

import common.check
import common.runCheckSuite

interface Wallet {
    /** Read-only from outside. Starts at the value passed to the factory. */
    val balanceCents: Long
    /** Adds money. amount <= 0 -> IllegalArgumentException. */
    fun deposit(amount: Long)
    /** Returns false (and changes nothing) when amount > balance or amount <= 0. */
    fun withdraw(amount: Long): Boolean
    /** Every successful deposit/withdraw as "+500" / "-200", oldest first. Must be a copy. */
    val history: List<String>
}

interface M02Factory {
    /** initialCents < 0 -> IllegalArgumentException */
    fun wallet(initialCents: Long): Wallet
}

fun runChecks(f: M02Factory) = runCheckSuite("m02 checks") {
    check("starts with initial balance") { f.wallet(1000).balanceCents == 1000L }
    check("negative initial rejected") {
        runCatching { f.wallet(-1) }.exceptionOrNull() is IllegalArgumentException
    }
    check("deposit adds") { f.wallet(0).apply { deposit(500) }.balanceCents == 500L }
    check("deposit zero rejected") {
        runCatching { f.wallet(0).deposit(0) }.exceptionOrNull() is IllegalArgumentException
    }
    check("withdraw too much refused") {
        val w = f.wallet(100); !w.withdraw(200) && w.balanceCents == 100L
    }
    check("history records in order") {
        val w = f.wallet(0); w.deposit(500); w.withdraw(200); w.withdraw(9999)
        w.history == listOf("+500", "-200")
    }
    check("history is a defensive copy") {
        val w = f.wallet(0); w.deposit(1)
        (w.history as? MutableList<String>)?.add("+999")
        w.history == listOf("+1")
    }
}
```

**Doc:** encapsulation = protecting invariants, not "making things private"; four modifiers table with scope; Kotlin vs Java defaults (public vs package-private, no package-private in Kotlin); `private set`; backing property convention `_x`/`x`; read-only `List` ≠ immutable list; defensive copies. In Compose: `ViewModel` exposing `private val _uiState = MutableStateFlow(...)` and `val uiState: StateFlow<...> = _uiState.asStateFlow()`; why the screen must not be able to `emit`.

---

### Task 6: m03 — Inheritance

**Files:** `src/main/kotlin/m03_inheritance/{L01_OpenAndOverride.kt, L02_ConstructorChaining.kt, L03_OpenCallInConstructor.kt, L04_AnyMembers.kt}`, `shop/Payment.kt`, `exercises/*`, `docs/03_inheritance.md`

**Shop snapshot `shop/Payment.kt`:** `open class Payment(val amountCents: Long) { open fun fee(): Long = 0; open fun describe() = "Payment of $amountCents" }`, `class CardPayment(amount: Long, val last4: String) : Payment(amount)` overriding `fee()` = 2% (integer cents, rounded down), `class CashOnDelivery(amount: Long) : Payment(amount)` fee = 1500.

**Lessons:**
- `L01_OpenAndOverride`: classes final by default (comment shows the compile error text for extending a final class); `open` members; `override` is itself open unless `final override`; calling `super.describe()`.
- `L02_ConstructorChaining`: subclass primary constructor passing to super; subclass with only secondary constructors calling `super(...)`; print order: parent init then child init.
- `L03_OpenCallInConstructor`: parent `init` calls an open function overridden in the child that reads a child property initialized in the child — prints `null`/`0`. Then the fix (don't call open members from constructors; use a final template or lazy).
- `L04_AnyMembers`: override `toString`; `equals`/`hashCode` left default prints `different` for two equal-looking objects; `is` check up the hierarchy.

**`exercises/Checks.kt`:**
```kotlin
package m03_inheritance.exercises

import common.check
import common.runCheckSuite

/** Base type the learner subclasses. Do not change. */
open class Shipment(val weightGrams: Int) {
    init { require(weightGrams > 0) { "weight must be positive" } }
    /** Base price: 20 EGP (2000 cents) + 1 cent per gram. */
    open fun costCents(): Long = 2000L + weightGrams
    open fun label(): String = "Standard ${weightGrams}g"
}

interface M03Factory {
    /** Subclass: cost = base cost * 2. label = "Express ${weight}g". */
    fun express(weightGrams: Int): Shipment
    /** Subclass of the express subclass: cost = express cost + 5000. label = express label + " (fragile)". Must call super. */
    fun fragileExpress(weightGrams: Int): Shipment
    /** Subclass: free when weight <= 1000g, otherwise base cost. label unchanged from base. */
    fun freeSmall(weightGrams: Int): Shipment
}

fun runChecks(f: M03Factory) = runCheckSuite("m03 checks") {
    check("express doubles cost") { f.express(1000).costCents() == 6000L }
    check("express label") { f.express(500).label() == "Express 500g" }
    check("fragile builds on express") { f.fragileExpress(1000).costCents() == 11000L }
    check("fragile label via super") { f.fragileExpress(10).label() == "Express 10g (fragile)" }
    check("free small is free") { f.freeSmall(1000).costCents() == 0L }
    check("free small falls back") { f.freeSmall(1001).costCents() == 3001L }
    check("free small keeps base label") { f.freeSmall(10).label() == "Standard 10g" }
    check("subclasses are Shipments") { listOf(f.express(1), f.freeSmall(1)).all { it is Shipment } }
    check("base validation still runs") {
        runCatching { f.express(0) }.exceptionOrNull() is IllegalArgumentException
    }
}
```

**Doc:** is-a relationship; `open`/`final`/`override`/`final override`; why Kotlin is final by default (fragile base class); constructor chaining and init order across the hierarchy; the open-call-in-constructor trap; `Any` members (`equals`, `hashCode`, `toString`); Java comparison. In Compose: `ComponentActivity` subclass with `onCreate` overriding and calling `super.onCreate`; a `ViewModel` subclass overriding `onCleared`; note that Compose UI itself avoids inheritance (pointer to m16).

---

### Task 7: m04 — Abstract classes

**Files:** `src/main/kotlin/m04_abstract/{L01_AbstractMembers.kt, L02_TemplateMethod.kt, L03_AbstractVsOpen.kt}`, `shop/Discount.kt`, `exercises/*`, `docs/04_abstract_classes.md`

**Shop snapshot `shop/Discount.kt`:** `abstract class Discount(val code: String) { abstract fun amountOff(subtotalCents: Long): Long; fun apply(subtotalCents: Long): Long = (subtotalCents - amountOff(subtotalCents)).coerceAtLeast(0) }`, `PercentDiscount`, `FixedDiscount`.

**Lessons:**
- `L01_AbstractMembers`: abstract class cannot be instantiated (comment shows compiler error), abstract property + abstract function, concrete members alongside, constructor in abstract class.
- `L02_TemplateMethod`: `abstract class CheckoutStep` with final `run()` calling `validate()`, `execute()`, `log()` hooks; two subclasses; print the fixed sequence.
- `L03_AbstractVsOpen`: same design three ways (open class with default, abstract class, interface) — print what each forces subclasses to do; table in comments.

**`exercises/Checks.kt`:**
```kotlin
package m04_abstract.exercises

import common.check
import common.runCheckSuite

/** Template: the learner must NOT change this class. */
abstract class Report(private val title: String) {
    protected abstract fun rows(): List<String>
    protected open fun footer(): String = "end"
    fun render(): String = buildString {
        appendLine("# $title")
        rows().forEach { appendLine("- $it") }
        append(footer())
    }
}

interface M04Factory {
    /** rows: one per order "order <id>" in given order; footer: "total: <n> orders" */
    fun ordersReport(orderIds: List<Int>): Report
    /** rows: "<name>: <qty>" only for qty < 5 (low stock); default footer. */
    fun lowStockReport(stock: Map<String, Int>): Report
}

fun runChecks(f: M04Factory) = runCheckSuite("m04 checks") {
    check("orders report renders") {
        f.ordersReport(listOf(7, 9)).render() == "# Orders\n- order 7\n- order 9\ntotal: 2 orders"
    }
    check("orders report empty") { f.ordersReport(emptyList()).render() == "# Orders\ntotal: 0 orders" }
    check("low stock filters") {
        f.lowStockReport(linkedMapOf("Pen" to 2, "Mug" to 10, "Cap" to 4)).render() ==
            "# Low stock\n- Pen: 2\n- Cap: 4\nend"
    }
}
```

**Doc:** abstract vs open vs interface (table); abstract properties; template method pattern; when abstract class beats interface (shared state/constructor); abstract classes in Java vs Kotlin. In Compose: `Painter` is an abstract class (`intrinsicSize`, `onDraw`) — write a custom `Painter` example; `Modifier.Node` as abstract base for modifier implementations.

---

### Task 8: m05 — Interfaces

**Files:** `src/main/kotlin/m05_interfaces/{L01_InterfaceBasics.kt, L02_DefaultMethodsAndProperties.kt, L03_DiamondConflict.kt, L04_FunInterface.kt}`, `shop/Shipping.kt`, `exercises/*`, `docs/05_interfaces.md`

**Shop snapshot `shop/Shipping.kt`:** `interface ShippingMethod { val name: String; fun costCents(weightGrams: Int): Long; fun eta(): String = "3-5 days" }`, implementations `StandardShipping`, `PickupPoint`.

**Lessons:**
- `L01_InterfaceBasics`: declare, implement, multiple interfaces on one class, interface as a type for a list.
- `L02_DefaultMethodsAndProperties`: default method; abstract property; property with getter (no backing field allowed — comment); overriding a default.
- `L03_DiamondConflict`: two interfaces with the same default method; class must override and choose `super<A>.x()` / `super<B>.x()`; print both.
- `L04_FunInterface`: `fun interface PriceRule { fun apply(cents: Long): Long }`; SAM lambda conversion; compare with a plain function type `(Long) -> Long` and say when to prefer each.

**`exercises/Checks.kt`:**
```kotlin
package m05_interfaces.exercises

import common.check
import common.runCheckSuite

interface Named { val displayName: String; fun greet(): String = "Hi, $displayName" }
interface Auditable { fun greet(): String = "audited"; fun auditTag(): String }

fun interface Validator { fun isValid(input: String): Boolean }

interface M05Factory {
    /** A class implementing BOTH Named and Auditable. displayName = name; auditTag = "user:<name>";
     *  greet() must return Named's greeting + " / " + Auditable's greeting. */
    fun user(name: String): Pair<Named, Auditable>
    /** Validator via SAM lambda: true when input has exactly 11 digits and starts with "01". */
    fun egyptPhoneValidator(): Validator
    /** Combine validators: valid only when all are valid. Empty list -> always valid. */
    fun allOf(validators: List<Validator>): Validator
}

fun runChecks(f: M05Factory) = runCheckSuite("m05 checks") {
    check("one object, two interfaces") { val (a, b) = f.user("Sara"); a === b }
    check("displayName") { f.user("Sara").first.displayName == "Sara" }
    check("diamond resolved") { f.user("Sara").first.greet() == "Hi, Sara / audited" }
    check("audit tag") { f.user("Sara").second.auditTag() == "user:Sara" }
    check("phone valid") { f.egyptPhoneValidator().isValid("01012345678") }
    check("phone too short") { !f.egyptPhoneValidator().isValid("0101234567") }
    check("phone wrong prefix") { !f.egyptPhoneValidator().isValid("02012345678") }
    check("allOf empty") { f.allOf(emptyList()).isValid("anything") }
    check("allOf combines") {
        val v = f.allOf(listOf(Validator { it.isNotEmpty() }, Validator { it.length < 3 }))
        v.isValid("ab") && !v.isValid("abcd") && !v.isValid("")
    }
}
```

**Doc:** interface = contract/capability; default methods; properties in interfaces (no state); multiple inheritance of behaviour and diamond resolution with `super<T>`; `fun interface` vs function type; interface vs abstract class decision guide. In Compose: `Modifier` is an interface (`then`, `Element`); `Shape` with `createOutline` — custom `TicketShape` example; `Arrangement.Horizontal` custom implementation; `Indication`.

---

### Task 9: m06 — Polymorphism

**Files:** `src/main/kotlin/m06_polymorphism/{L01_DynamicDispatch.kt, L02_Overloading.kt, L03_SmartCasts.kt}`, `shop/Notifications.kt`, `exercises/*`, `docs/06_polymorphism.md`

**Shop snapshot `shop/Notifications.kt`:** `interface Notifier { fun send(to: String, msg: String): String }`, `SmsNotifier`, `EmailNotifier`, `PushNotifier` each returning a formatted string.

**Lessons:**
- `L01_DynamicDispatch`: `List<Notifier>` loop — runtime type chooses method; static type vs runtime type printed; extension functions are resolved statically (show an extension on the base and subclass both named `kind()` — prints the base one when declared type is base). This last point is the core "gotcha".
- `L02_Overloading`: overloads by parameter type/count; overload resolution happens at compile time (pass a `Notifier` declared as base to overloaded `describe(n: Notifier)` / `describe(n: SmsNotifier)` — base chosen); default params vs overloads; `@JvmOverloads` mention.
- `L03_SmartCasts`: `is`, `!is`, `as`, `as?`, smart cast in `if`/`when`; smart cast fails on `var` property (comment shows error, fix with local `val`).

**`exercises/Checks.kt`:**
```kotlin
package m06_polymorphism.exercises

import common.check
import common.runCheckSuite

interface Shape2D { fun area(): Double; fun name(): String }

interface M06Factory {
    fun circle(r: Double): Shape2D       // name "circle", area PI*r*r
    fun rect(w: Double, h: Double): Shape2D // name "rect"
    fun square(side: Double): Shape2D    // name "square", MUST be a subclass of the rect class
    /** Sum of areas, polymorphically (no type checks). */
    fun totalArea(shapes: List<Shape2D>): Double
    /** "circle r=<r>" for circles, "rect <w>x<h>" for rects AND squares, using smart casts on your own classes.
     *  Numbers printed with Kotlin's default Double.toString(). Anything else -> "unknown". */
    fun describe(shape: Any): String
}

fun runChecks(f: M06Factory) = runCheckSuite("m06 checks") {
    fun close(a: Double, b: Double) = kotlin.math.abs(a - b) < 1e-9
    check("circle area") { close(f.circle(1.0).area(), Math.PI) }
    check("square is a rect") { f.rect(1.0, 1.0)::class.isInstance(f.square(2.0)) }
    check("square name overrides") { f.square(2.0).name() == "square" }
    check("total area") { close(f.totalArea(listOf(f.rect(2.0, 3.0), f.square(2.0))), 10.0) }
    check("describe circle") { f.describe(f.circle(1.5)) == "circle r=1.5" }
    check("describe square as rect") { f.describe(f.square(2.0)) == "rect 2.0x2.0" }
    check("describe unknown") { f.describe("hello") == "unknown" }
}
```

**Doc:** subtype polymorphism and dynamic dispatch (vtable explanation in plain words); static vs runtime type; overloading = compile-time; extensions = static dispatch; smart casts and their limits; `as?` safe cast; Liskov preview (m17). In Compose: `Image(painter = ...)` accepts any `Painter` — `BitmapPainter` vs `VectorPainter`, `RoundedCornerShape` vs `GenericShape` passed as `Shape`; the composable never checks the runtime type.

---

### Task 10: m07 — Data classes

**Files:** `src/main/kotlin/m07_data_classes/{L01_GeneratedMembers.kt, L02_CopyAndImmutability.kt, L03_DataClassPitfalls.kt}`, `shop/Product.kt` (now `data class`), `exercises/*`, `docs/07_data_classes.md`

**Lessons:**
- `L01_GeneratedMembers`: regular class vs data class side by side — `==`, `toString`, `hashCode` equality printed as `same`/`different`, `componentN` destructuring, `Set` dedup.
- `L02_CopyAndImmutability`: `copy()` with named args; immutable `val` + `List` state updates via copy; `===` shows new instance; shallow copy trap with a `MutableList` property (mutate through copy affects original).
- `L03_DataClassPitfalls`: only primary-constructor properties take part in equals (body property ignored); mutable `var` in a data class used as `HashSet` key then mutated → `contains` false; data class can't be `open`/`abstract`.

**`exercises/Checks.kt`:**
```kotlin
package m07_data_classes.exercises

import common.check
import common.runCheckSuite

/** Given. The learner works with this type. */
data class CartUiState(
    val items: List<Pair<String, Int>> = emptyList(), // name to qty
    val isLoading: Boolean = false,
    val error: String? = null,
)

interface M07Ops {
    /** Returns a new state with qty for name increased by 1 (added with qty 1 if absent, appended at end). Clears error. */
    fun increment(state: CartUiState, name: String): CartUiState
    /** Returns a new state with isLoading=false and error set. Items unchanged. */
    fun fail(state: CartUiState, message: String): CartUiState
    /** Total quantity using destructuring in the loop/lambda. */
    fun totalQty(state: CartUiState): Int
}

fun runChecks(ops: M07Ops) = runCheckSuite("m07 checks") {
    val s0 = CartUiState(isLoading = true, error = "old")
    check("increment adds new") { ops.increment(CartUiState(), "Pen").items == listOf("Pen" to 1) }
    check("increment existing") {
        ops.increment(CartUiState(listOf("Pen" to 1, "Mug" to 2)), "Pen").items == listOf("Pen" to 2, "Mug" to 2)
    }
    check("increment clears error") { ops.increment(s0, "Pen").error == null }
    check("original untouched") { ops.increment(s0, "Pen"); s0 == CartUiState(isLoading = true, error = "old") }
    check("fail sets error") { ops.fail(s0, "boom") == CartUiState(isLoading = false, error = "boom") }
    check("total qty") { ops.totalQty(CartUiState(listOf("A" to 2, "B" to 3))) == 5 }
}
```

**Doc:** what the compiler generates (show the equivalent hand-written Java/Kotlin); structural vs referential equality; `copy` is shallow; immutability strategies; rules/restrictions; data class vs regular class vs value class (pointer m10). In Compose: `UiState` as data class + `_uiState.update { it.copy(...) }`; stability — data class with only `val` of stable types is skippable; `List` parameter is unstable → `@Immutable` annotation or immutable collections; show recomposition skipping explanation.

---

### Task 11: m08 — Sealed types & enums

**Files:** `src/main/kotlin/m08_sealed_enums/{L01_Enums.kt, L02_SealedClasses.kt, L03_SealedInterfaces.kt, L04_ExhaustiveWhen.kt}`, `shop/OrderStatus.kt`, `exercises/*`, `docs/08_sealed_and_enums.md`

**Shop snapshot `shop/OrderStatus.kt`:** `sealed interface OrderStatus` with `data object Placed`, `data class Shipped(val trackingId: String)`, `data class Delivered(val signedBy: String)`, `data class Cancelled(val reason: String)`.

**Lessons:**
- `L01_Enums`: enum with properties, constructor, methods, abstract method per constant; `entries`, `valueOf`, `ordinal`; enum implementing an interface.
- `L02_SealedClasses`: sealed class `UiState` (Loading/Success/Error) with state; why not enum (each case carries different data); `data object` and its `toString`.
- `L03_SealedInterfaces`: one class implementing two sealed interfaces; sealed hierarchy across files in same package (comment).
- `L04_ExhaustiveWhen`: `when` as expression without `else`; adding a new subtype breaks compile (comment shows message); `else` hides future cases (bad).

**`exercises/Checks.kt`:**
```kotlin
package m08_sealed_enums.exercises

import common.check
import common.runCheckSuite

sealed interface PaymentResult {
    data class Success(val transactionId: String) : PaymentResult
    data class Declined(val reason: String) : PaymentResult
    data object NetworkError : PaymentResult
}

enum class Tier(val minSpendCents: Long) { BRONZE(0), SILVER(100_000), GOLD(500_000) }

interface M08Ops {
    /** Exhaustive when, NO else branch:
     *  Success -> "Paid (<id>)", Declined -> "Declined: <reason>", NetworkError -> "Try again" */
    fun message(result: PaymentResult): String
    /** Highest tier whose minSpendCents <= spent, using Tier.entries. */
    fun tierFor(spentCents: Long): Tier
    /** Next tier or null when GOLD. */
    fun next(tier: Tier): Tier?
}

fun runChecks(ops: M08Ops) = runCheckSuite("m08 checks") {
    check("success msg") { ops.message(PaymentResult.Success("T1")) == "Paid (T1)" }
    check("declined msg") { ops.message(PaymentResult.Declined("no funds")) == "Declined: no funds" }
    check("network msg") { ops.message(PaymentResult.NetworkError) == "Try again" }
    check("tier bronze") { ops.tierFor(99_999) == Tier.BRONZE }
    check("tier silver edge") { ops.tierFor(100_000) == Tier.SILVER }
    check("tier gold") { ops.tierFor(1_000_000) == Tier.GOLD }
    check("next tier") { ops.next(Tier.BRONZE) == Tier.SILVER && ops.next(Tier.GOLD) == null }
}
```

**Doc:** enum vs sealed decision table; sealed class vs sealed interface; `data object`; exhaustiveness as a compiler-checked contract; state machines (order status transitions). In Compose: `when (state) { is Loading -> CircularProgressIndicator() ... }` screen; navigation routes as sealed types used with `NavHost`; why `else ->` in UI `when` is a bug magnet.

---

### Task 12: m09 — `object` & `companion object`

**Files:** `src/main/kotlin/m09_object_companion/{L01_ObjectSingleton.kt, L02_CompanionObject.kt, L03_FactoriesAndConstants.kt}`, `shop/ProductFactory.kt`, `exercises/*`, `docs/09_object_and_companion.md`

**Lessons:**
- `L01_ObjectSingleton`: `object AppConfig` lazily initialized on first access (init block prints once); same instance (`same`); object implementing an interface.
- `L02_CompanionObject`: companion members accessed via class name; named companion; companion implementing an interface (`Product.Companion` as a parser); `@JvmStatic` mention; companion is an object (can be passed around).
- `L03_FactoriesAndConstants`: private constructor + companion `fun create(...)` returning `Result`/nullable; `const val` vs `val` in companion (compile-time constant rules); extension on companion `fun Product.Companion.sample()`.

**`exercises/Checks.kt`:**
```kotlin
package m09_object_companion.exercises

import common.check
import common.runCheckSuite

/** Contract for a class with a private constructor and companion factory. */
interface CouponLike { val code: String; val percent: Int }

interface CouponParser {
    /** "SAVE10" -> code "SAVE10", percent 10. Format: 4+ uppercase letters followed by 1-2 digits (1..90).
     *  Invalid -> null. */
    fun parse(raw: String): CouponLike?
}

interface M09Factory {
    /** Return your Coupon class's companion object (it must implement CouponParser). */
    fun parser(): CouponParser
    /** Return your singleton object that counts parse() calls across the whole program. Same instance every call. */
    fun counter(): Any
    /** Number of parse() calls recorded by the singleton so far. */
    fun parseCalls(): Int
}

fun runChecks(f: M09Factory) = runCheckSuite("m09 checks") {
    check("valid coupon") { f.parser().parse("SAVE10")?.let { it.code == "SAVE10" && it.percent == 10 } == true }
    check("lowercase invalid") { f.parser().parse("save10") == null }
    check("percent over 90 invalid") { f.parser().parse("SAVE95") == null }
    check("percent zero invalid") { f.parser().parse("SAVE0") == null }
    check("short prefix invalid") { f.parser().parse("AB10") == null }
    check("parser is a companion") { f.parser()::class.java.simpleName == "Companion" }
    check("singleton identity") { f.counter() === f.counter() }
    check("counter counts") {
        val before = f.parseCalls(); f.parser().parse("SAVE10"); f.parser().parse("x")
        f.parseCalls() - before == 2
    }
}
```

**Doc:** `object` declaration vs object expression (m11); initialization (thread-safe, lazy on first access); companion vs Java `static`; named companions; companion implementing interfaces; factories with private constructors; constants. In Compose: `Modifier` companion object is the empty modifier (that's why `Modifier.padding(...)` starts with `Modifier`); `MaterialTheme` is an `object` with `colorScheme`/`typography` getters; `Dp.Unspecified` lives in `Dp.Companion`.

---

### Task 13: m10 — Value classes

**Files:** `src/main/kotlin/m10_value_classes/{L01_TypeSafetyProblem.kt, L02_ValueClassBasics.kt, L03_BoxingRules.kt}`, `shop/Price.kt`, `exercises/*`, `docs/10_value_classes.md`

**Shop snapshot `shop/Price.kt`:** `@JvmInline value class Price(val cents: Long)` with `init { require(cents >= 0) }`, `operator fun plus`, `fun format()`. `@JvmInline value class ProductId(val value: Int)`.

**Lessons:**
- `L01_TypeSafetyProblem`: `fun addToCart(productId: Int, qty: Int)` called with arguments swapped — compiles and is wrong; print the bug.
- `L02_ValueClassBasics`: `ProductId` / `Quantity` fix the swap (comment shows compile error); `init` validation; members and interfaces on value classes.
- `L03_BoxingRules`: when it's boxed (nullable, generic, interface type) vs unboxed — explained in comments; `===` is forbidden (comment); equality uses underlying value; print `same`.

**`exercises/Checks.kt`:**
```kotlin
package m10_value_classes.exercises

import common.check
import common.runCheckSuite

interface MoneyOps<M> {
    /** Create money from cents; negative -> IllegalArgumentException. */
    fun of(cents: Long): M
    fun plus(a: M, b: M): M
    /** Apply percent discount, rounding down to whole cents. percent outside 0..100 -> IllegalArgumentException */
    fun discount(m: M, percent: Int): M
    /** "EGP 12.50" */
    fun format(m: M): String
    /** Return M's class, e.g. Money::class. The suite verifies it is a value class. */
    fun type(): kotlin.reflect.KClass<*>
}

fun <M> runChecks(ops: MoneyOps<M>) = runCheckSuite("m10 checks") {
    // Value classes compile a static "box-impl" method; KClass.isValue would need kotlin-reflect.
    check("is a value class") { ops.type().java.declaredMethods.any { it.name == "box-impl" } }
    check("negative rejected") { runCatching { ops.of(-1) }.exceptionOrNull() is IllegalArgumentException }
    check("plus") { ops.format(ops.plus(ops.of(150), ops.of(250))) == "EGP 4.00" }
    check("discount rounds down") { ops.format(ops.discount(ops.of(999), 10)) == "EGP 8.99" }
    check("bad percent") { runCatching { ops.discount(ops.of(1), 101) }.exceptionOrNull() is IllegalArgumentException }
    check("equality by value") { ops.of(5) == ops.of(5) }
}
```

**Doc:** primitive obsession; `@JvmInline value class` rules (one property, `val`, no identity); boxing table; mangled function names on JVM (mention why); value class vs data class vs typealias. In Compose: `Dp`, `Color`, `TextUnit` are value classes — `16.dp` is a `Dp`, not a `Float`, so `padding(16)` does not compile; `Color` packs ARGB into a `Long` (ULong) — zero allocation in hot drawing code.

---

### Task 14: m11 — Nested, inner & anonymous

**Files:** `src/main/kotlin/m11_nested_inner_anonymous/{L01_NestedVsInner.kt, L02_ObjectExpressions.kt, L03_LocalClassesAndCapture.kt}`, `exercises/*`, `docs/11_nested_inner_anonymous.md`

**Lessons:**
- `L01_NestedVsInner`: nested class can't see outer instance (comment error); `inner` can, `this@Outer`; memory note: inner holds outer reference (leak risk, the Android Activity example).
- `L02_ObjectExpressions`: `object : Comparator<Product>`; anonymous object implementing two interfaces; anonymous object with extra members only usable as local/private type.
- `L03_LocalClassesAndCapture`: local class/function capturing a local `var` and mutating it; lambda vs object expression capture.

**`exercises/Checks.kt`:**
```kotlin
package m11_nested_inner_anonymous.exercises

import common.check
import common.runCheckSuite

interface ClickListener { fun onClick(): String }

interface M11Factory {
    /** Return an anonymous object (object expression) implementing Comparator<String>
     *  that sorts by length, then alphabetically. */
    fun byLengthThenAlpha(): Comparator<String>
    /** Screen with title; returns a listener from an INNER class that reads the outer title:
     *  onClick() -> "clicked on <title>" and reflects later title changes. */
    fun screen(title: String): Pair<(String) -> Unit, ClickListener>
    /** Returns a listener (object expression) counting its own clicks: "click #1", "click #2", ... */
    fun counting(): ClickListener
}

fun runChecks(f: M11Factory) = runCheckSuite("m11 checks") {
    check("comparator order") {
        listOf("pear", "fig", "apple", "kiwi").sortedWith(f.byLengthThenAlpha()) == listOf("fig", "kiwi", "pear", "apple")
    }
    check("comparator is anonymous") { f.byLengthThenAlpha()::class.java.isAnonymousClass }
    check("inner sees outer") { f.screen("Cart").second.onClick() == "clicked on Cart" }
    check("inner sees updates") {
        val (setTitle, l) = f.screen("Cart"); setTitle("Checkout"); l.onClick() == "clicked on Checkout"
    }
    check("listener is inner class") {
        val c = f.screen("x").second::class.java
        c.isMemberClass && !java.lang.reflect.Modifier.isStatic(c.modifiers)
    }
    check("counting listener") { val c = f.counting(); c.onClick(); c.onClick() == "click #2" }
}
```

**Doc:** nested (static in Java) vs inner; `this@Outer`; leak risk of inner/anonymous classes holding Activity/Context; object expressions vs lambdas vs `fun interface`; local classes. In Compose: anonymous `object : PreviewParameterProvider<Product>` vs a named class with `@PreviewParameter`; callbacks as lambdas instead of anonymous listener objects (why Compose APIs take `() -> Unit`).

---

### Task 15: m12 — Delegation

**Files:** `src/main/kotlin/m12_delegation/{L01_ClassDelegation.kt, L02_PropertyDelegates.kt, L03_CustomDelegate.kt, L04_StandardDelegates.kt}`, `shop/LoggingCart.kt`, `exercises/*`, `docs/12_delegation.md`

**Lessons:**
- `L01_ClassDelegation`: `class LoggingCart(private val inner: CartApi) : CartApi by inner` overriding one method only; compare to inheritance; the "override not seen by delegate" gotcha (delegate calls its own method, not the override).
- `L02_PropertyDelegates`: `by lazy`, `by Delegates.observable` printing old->new, `by Delegates.vetoable` rejecting negatives, `by map` (`val name by map`).
- `L03_CustomDelegate`: write `class Trimmed : ReadWriteProperty<Any?, String>` and an operator-based `getValue`/`setValue` version; `provideDelegate` brief.
- `L04_StandardDelegates`: delegate to another property (`var old by this::new`); `notNull()`.

**`exercises/Checks.kt`:**
```kotlin
package m12_delegation.exercises

import common.check
import common.runCheckSuite

interface Inventory {
    fun stockOf(item: String): Int
    fun reserve(item: String, qty: Int): Boolean
}

/** Given in-memory inventory. */
class MemoryInventory(private val stock: MutableMap<String, Int>) : Inventory {
    override fun stockOf(item: String) = stock[item] ?: 0
    override fun reserve(item: String, qty: Int): Boolean {
        val have = stockOf(item); if (qty <= 0 || have < qty) return false
        stock[item] = have - qty; return true
    }
}

interface Profile {
    /** Always stored trimmed and lowercased (custom delegate). */
    var email: String
    /** Every change recorded as "old->new" (observable). Initial "". */
    var nickname: String
    val nicknameChanges: List<String>
}

interface M12Factory {
    /** Inventory using class delegation `by` to the given inventory; overrides ONLY reserve to also log
     *  "reserve <item> x<qty>: <true|false>" into the returned list. */
    fun auditing(inner: Inventory): Pair<Inventory, List<String>>
    fun profile(): Profile
}

fun runChecks(f: M12Factory) = runCheckSuite("m12 checks") {
    check("delegates stockOf") { f.auditing(MemoryInventory(mutableMapOf("pen" to 3))).first.stockOf("pen") == 3 }
    check("logs reserve") {
        val (inv, log) = f.auditing(MemoryInventory(mutableMapOf("pen" to 3)))
        inv.reserve("pen", 2); inv.reserve("pen", 2)
        log == listOf("reserve pen x2: true", "reserve pen x2: false")
    }
    check("email normalized") { f.profile().apply { email = "  Sara@Mail.COM " }.email == "sara@mail.com" }
    check("nickname changes") {
        val p = f.profile(); p.nickname = "s"; p.nickname = "sara"
        p.nicknameChanges == listOf("->s", "s->sara")
    }
}
```

**Doc:** composition via `by` vs inheritance; what the compiler generates for class delegation; delegate gotcha; property delegation protocol (`getValue`/`setValue` operators, `ReadOnlyProperty`, `ReadWriteProperty`); standard delegates; `provideDelegate`. In Compose: `var count by remember { mutableStateOf(0) }` works because `State` has a `getValue` operator extension and `MutableState` a `setValue`; `val vm: ShopViewModel by viewModels()`; `val state by vm.uiState.collectAsStateWithLifecycle()`.

---

### Task 16: m13 — Generics & variance

**Files:** `src/main/kotlin/m13_generics/{L01_GenericClassesAndFunctions.kt, L02_Constraints.kt, L03_Variance.kt, L04_ReifiedAndStar.kt}`, `shop/Repository.kt`, `exercises/*`, `docs/13_generics_and_variance.md`

**Shop snapshot `shop/Repository.kt`:** `interface Entity { val id: Int }`, `interface Repository<T : Entity> { fun save(item: T); fun byId(id: Int): T?; fun all(): List<T> }`, `class InMemoryRepository<T : Entity> : Repository<T>` (ordered by insertion, `linkedMapOf`).

**Lessons:**
- `L01_GenericClassesAndFunctions`: `Box<T>`, generic function, type inference, `Result<T>`-like `Outcome<T>`.
- `L02_Constraints`: `T : Comparable<T>` max function; multiple bounds with `where`; `T : Entity` repository.
- `L03_Variance`: why `MutableList<Dog>` is not `MutableList<Animal>` (comment error); `out` producer (`Source<out T>`), `in` consumer (`Comparator<in T>`), use-site `Array<out Any>`; `List<out E>` in stdlib.
- `L04_ReifiedAndStar`: type erasure (`is List<String>` illegal, comment); `inline fun <reified T> List<*>.filterIsType()`; star projection `List<*>`.

**`exercises/Checks.kt`:**
```kotlin
package m13_generics.exercises

import common.check
import common.runCheckSuite

interface Producer<out T> { fun produce(): T }
interface Consumer<in T> { fun consume(item: T): String }

open class Item(val name: String)
class Book(name: String, val pages: Int) : Item(name)

interface M13Ops {
    /** Generic stack: push, pop (null when empty), peek, size. */
    fun <T> stack(): GenericStack<T>
    /** Largest element by natural order; null for empty list. Constraint T : Comparable<T>. */
    fun <T : Comparable<T>> maxOf(items: List<T>): T?
    /** Keep only elements of type T (reified). */
    fun booksOnly(items: List<Any>): List<Book>
    /** Must compile thanks to variance: a Producer<Book> used where Producer<Item> is expected. Return produce().name */
    fun nameFrom(p: Producer<Item>): String
    /** A Consumer<Item> passed where Consumer<Book> is expected. Return c.consume(book). */
    fun feed(c: Consumer<Book>, book: Book): String
}

interface GenericStack<T> { fun push(x: T); fun pop(): T?; fun peek(): T?; val size: Int }

fun runChecks(ops: M13Ops) = runCheckSuite("m13 checks") {
    check("stack lifo") {
        val s = ops.stack<Int>(); s.push(1); s.push(2); s.pop() == 2 && s.peek() == 1 && s.size == 1
    }
    check("stack empty pop") { ops.stack<String>().pop() == null }
    check("maxOf") { ops.maxOf(listOf(3, 9, 2)) == 9 && ops.maxOf(emptyList<String>()) == null }
    check("booksOnly") { ops.booksOnly(listOf(Item("pen"), Book("kotlin", 300), "x")).map { it.name } == listOf("kotlin") }
    check("covariance") {
        val p: Producer<Book> = object : Producer<Book> { override fun produce() = Book("b", 1) }
        ops.nameFrom(p) == "b"
    }
    check("contravariance") {
        val c: Consumer<Item> = object : Consumer<Item> { override fun consume(item: Item) = "got ${item.name}" }
        ops.feed(c, Book("k", 1)) == "got k"
    }
}
```

**Doc:** generic classes/functions; bounds and `where`; invariance and why; declaration-site `out`/`in` with PECS-style mnemonic ("producer out, consumer in"); use-site projections; erasure; `reified` with `inline`; star projection; Java wildcards comparison. In Compose: `State<out T>` is covariant (a `State<Book>` passes as `State<Item>`); `LazyListScope.items(items: List<T>, key, itemContent)` is generic; a generic `ItemsList<T>` composable example.

---

### Task 17: m14 — Extensions & receivers

**Files:** `src/main/kotlin/m14_extensions_receivers/{L01_ExtensionFunctions.kt, L02_ExtensionProperties.kt, L03_LambdaWithReceiver.kt, L04_ScopedDsl.kt}`, `exercises/*`, `docs/14_extensions_and_receivers.md`

**Lessons:**
- `L01_ExtensionFunctions`: `fun Long.toEgp()`; extensions don't access private members (comment); static resolution reminder (m06); member wins over extension with same signature; nullable receiver `fun String?.orDash()`.
- `L02_ExtensionProperties`: `val Product.isExpensive get() = ...`; no backing field (comment).
- `L03_LambdaWithReceiver`: `buildString`, `apply`, `with`; write `fun receipt(block: ReceiptBuilder.() -> Unit): String`.
- `L04_ScopedDsl`: scope-limited extension: `class RowScopeLike { fun Item.weight(w: Int) }` — `weight` usable only inside the scope (comment error outside); `@DslMarker` to stop outer-scope leakage.

**`exercises/Checks.kt`:**
```kotlin
package m14_extensions_receivers.exercises

import common.check
import common.runCheckSuite

interface M14Ops {
    /** Long cents -> "EGP 12.50" as an extension you write; this delegates to it. */
    fun formatCents(cents: Long): String
    /** String? -> "-" when null or blank, else trimmed. Extension on nullable receiver. */
    fun orDash(s: String?): String
    /** Build with your DSL:
     *  order { line("Pen", 2, 500); line("Mug", 1, 3000); discountPercent(10) }
     *  Render: "Pen x2 = EGP 10.00\nMug x1 = EGP 30.00\nTotal = EGP 36.00"
     *  (total after discount, rounded down). Only `line` and `discountPercent` exist inside the block. */
    fun renderSampleOrder(): String
}

fun runChecks(ops: M14Ops) = runCheckSuite("m14 checks") {
    check("format cents") { ops.formatCents(1250) == "EGP 12.50" && ops.formatCents(7) == "EGP 0.07" }
    check("orDash null") { ops.orDash(null) == "-" }
    check("orDash blank") { ops.orDash("   ") == "-" }
    check("orDash trims") { ops.orDash("  hi ") == "hi" }
    check("dsl render") {
        ops.renderSampleOrder() == "Pen x2 = EGP 10.00\nMug x1 = EGP 30.00\nTotal = EGP 36.00"
    }
}
```

**Doc:** extensions compile to static functions (show Java signature); resolution rules; member vs extension; nullable receivers; extension properties; function types with receiver `T.() -> R`; scope functions table (`let`/`run`/`with`/`apply`/`also`); type-safe builders and `@DslMarker`. In Compose: every `Modifier.padding(...)` is an extension on `Modifier`; `RowScope.weight` and `BoxScope.align` exist only inside `Row {}`/`Box {}` because `content: @Composable RowScope.() -> Unit`; write a custom `Modifier.shimmerBorder()`-style extension using only referenced APIs.

---

### Task 18: m15 — Operators & equality

**Files:** `src/main/kotlin/m15_operators_equality/{L01_OperatorOverloading.kt, L02_IndexAndInvoke.kt, L03_Equality.kt}`, `shop/Price.kt` (with operators), `exercises/*`, `docs/15_operators_and_equality.md`

**Lessons:**
- `L01_OperatorOverloading`: `plus`, `minus`, `times`, `unaryMinus`, `compareTo` on `Price`; `+=` resolution (`plusAssign` vs `plus` on `var`).
- `L02_IndexAndInvoke`: `get`/`set` on `Cart` (`cart["pen"]`), `contains` (`in`), `invoke` (callable object), `rangeTo`, `component1/2` for destructuring a non-data class.
- `L03_Equality`: `==` → `equals`, `===` identity; hand-written `equals`/`hashCode` contract; breaking the contract → `HashSet` duplicate; `compareTo` consistent with `equals`.

**`exercises/Checks.kt`:**
```kotlin
package m15_operators_equality.exercises

import common.check
import common.runCheckSuite

/** Implement these operators on your own Vec class (NOT a data class - write equals/hashCode yourself). */
interface Vec { val x: Int; val y: Int }

interface M15Ops {
    fun vec(x: Int, y: Int): Vec
    /** a + b using your operator */
    fun add(a: Vec, b: Vec): Vec
    /** a * k using your operator */
    fun scale(a: Vec, k: Int): Vec
    /** -a using your unaryMinus */
    fun negate(a: Vec): Vec
    /** compare by squared length using your compareTo; returns sign -1/0/1 */
    fun compare(a: Vec, b: Vec): Int
    /** your Vec's toString: "Vec(1, 2)" */
    fun show(a: Vec): String
}

fun runChecks(ops: M15Ops) = runCheckSuite("m15 checks") {
    check("not a data class") { ops.vec(0, 0)::class.java.methods.none { it.name == "copy" || it.name == "component1" } }
    check("equals by value") { ops.vec(1, 2) == ops.vec(1, 2) && ops.vec(1, 2) != ops.vec(2, 1) }
    check("hashCode consistent") { setOf(ops.vec(1, 2), ops.vec(1, 2)).size == 1 }
    check("identity differs") { ops.vec(1, 2) !== ops.vec(1, 2) }
    check("plus") { ops.add(ops.vec(1, 2), ops.vec(3, 4)) == ops.vec(4, 6) }
    check("times") { ops.scale(ops.vec(1, -2), 3) == ops.vec(3, -6) }
    check("unaryMinus") { ops.negate(ops.vec(1, -2)) == ops.vec(-1, 2) }
    check("compareTo") { ops.compare(ops.vec(3, 4), ops.vec(5, 0)) == 0 && ops.compare(ops.vec(1, 1), ops.vec(2, 2)) == -1 }
    check("toString") { ops.show(ops.vec(1, 2)) == "Vec(1, 2)" }
}
```

**Doc:** operator conventions table (operator ↔ function name); `operator` keyword required; assignment operators resolution; indexed access; `invoke`; equality operators and the equals/hashCode/compareTo contracts; when NOT to overload operators. In Compose: `Dp.plus` (`8.dp + 4.dp`), `Offset` arithmetic in drag handling (`offset += dragAmount`), `Modifier.then` as an infix combinator.

---

### Task 19: m16 — Composition over inheritance

**Files:** `src/main/kotlin/m16_composition/{L01_InheritanceExplosion.kt, L02_ComposeBehaviours.kt, L03_SlotStyleApi.kt}`, `exercises/*`, `docs/16_composition_over_inheritance.md`

**Lessons:**
- `L01_InheritanceExplosion`: `Notifier` hierarchy needing combos (Sms+Logging, Email+Retry+Logging…) → class count printed growing combinatorially.
- `L02_ComposeBehaviours`: same features as small objects composed at runtime (`RetryingNotifier(LoggingNotifier(SmsNotifier()))`); has-a vs is-a.
- `L03_SlotStyleApi`: a text "screen" renderer taking lambdas for slots: `renderScreen(header = { ... }, body = { ... }, footer = null)` — pure-Kotlin mirror of Compose slot APIs.

**`exercises/Checks.kt`:**
```kotlin
package m16_composition.exercises

import common.check
import common.runCheckSuite

fun interface PriceRule { fun apply(cents: Long): Long }

interface M16Ops {
    /** 10% off, rounded down */
    fun tenPercentOff(): PriceRule
    /** subtract fixed amount, floor at 0 */
    fun minus(cents: Long): PriceRule
    /** Compose rules left to right WITHOUT inheritance. */
    fun chain(vararg rules: PriceRule): PriceRule
    /** Slot API: header and footer are optional. Output lines joined with "\n":
     *  "[header]" line only if header != null, then body(), then "[footer]" line only if footer != null.
     *  header/footer lambdas return the text inside the brackets. */
    fun layout(header: (() -> String)?, body: () -> String, footer: (() -> String)?): String
}

fun runChecks(ops: M16Ops) = runCheckSuite("m16 checks") {
    check("ten percent") { ops.tenPercentOff().apply(999) == 899L }
    check("minus floors") { ops.minus(500).apply(300) == 0L }
    check("chain order") { ops.chain(ops.tenPercentOff(), ops.minus(100)).apply(1000) == 800L }
    check("chain other order") { ops.chain(ops.minus(100), ops.tenPercentOff()).apply(1000) == 810L }
    check("chain empty is identity") { ops.chain().apply(123) == 123L }
    check("layout full") { ops.layout({ "Cart" }, { "2 items" }, { "Pay" }) == "[Cart]\n2 items\n[Pay]" }
    check("layout no slots") { ops.layout(null, { "only" }, null) == "only" }
}
```

**Doc:** is-a vs has-a; fragile base class and class explosion; composition + interfaces + delegation (link m12); when inheritance is still right (true taxonomy, framework base classes). In Compose: Compose has no `View` inheritance tree — you build UI by composing functions; `Scaffold(topBar = {...}, bottomBar = {...}) { padding -> ... }` slot API; `Button(content = { Icon(...); Text(...) })`; wrapper composables (`PrimaryButton` wrapping `Button`) instead of subclassing.

---

### Task 20: m17 — SOLID

**Files:** `src/main/kotlin/m17_solid/{L01_SingleResponsibility.kt, L02_OpenClosed.kt, L03_LiskovSubstitution.kt, L04_InterfaceSegregation.kt, L05_DependencyInversion.kt}`, `shop/` (before/after classes as needed, package `m17_solid.shop`), `exercises/*`, `docs/17_solid.md`

(Lessons allowed: 5 — exception to the 2–4 rule because SOLID has 5 principles; spec says 2–4 per module but each principle needs one runnable before/after. Keep each ≤ 150 lines.)

**Lessons (each prints BEFORE behaviour and AFTER behaviour):**
- `L01_SingleResponsibility`: `OrderManager` that validates, prices, saves, and emails → split into `OrderValidator`, `PriceCalculator`, `OrderRepository`, `ReceiptSender`.
- `L02_OpenClosed`: `when(type)` discount calculator needing edits per new type → `DiscountPolicy` interface; adding a policy without touching the calculator.
- `L03_LiskovSubstitution`: `Rectangle`/`Square` setter violation and `ReadOnlyList` cast violation → fix with immutable shapes / separate types; print the broken assertion then the fixed one.
- `L04_InterfaceSegregation`: fat `PaymentGateway` (pay, refund, subscribe, report) forcing `TODO()` in `CashGateway` → split into `Payer`, `Refunder`, `Subscriber`.
- `L05_DependencyInversion`: `CheckoutService` creating `MySqlOrders()` internally → depends on `OrderStore` interface via constructor; swap in a `FakeOrderStore`.

**`exercises/Checks.kt`:** refactor exercise. The stub file contains the "bad" class (written out in full by the implementer, see below) and the contract requires the learner to provide refactored pieces.
```kotlin
package m17_solid.exercises

import common.check
import common.runCheckSuite

/** Abstractions the refactor must use (DIP + ISP). */
interface OrderStore { fun save(id: Int, totalCents: Long); fun all(): List<Pair<Int, Long>> }
interface Notifier { fun notify(message: String) }
fun interface DiscountPolicy { fun discount(subtotalCents: Long): Long }

interface Checkout { fun place(id: Int, lineCents: List<Long>): Long }

interface M17Factory {
    /** Build a checkout with injected dependencies (no `new` of concrete stores inside).
     *  place(): subtotal = sum(lineCents); empty lines -> IllegalArgumentException;
     *  total = subtotal - policy.discount(subtotal) floored at 0; store.save(id, total);
     *  notifier.notify("Order <id>: <total>"); returns total. */
    fun checkout(store: OrderStore, notifier: Notifier, policy: DiscountPolicy): Checkout
    /** Policies added WITHOUT modifying checkout (OCP). */
    fun noDiscount(): DiscountPolicy
    fun overThousandGets100Off(): DiscountPolicy  // subtotal > 100_000 -> 10_000 off, else 0
}

class FakeStore : OrderStore {
    val saved = mutableListOf<Pair<Int, Long>>()
    override fun save(id: Int, totalCents: Long) { saved += id to totalCents }
    override fun all() = saved.toList()
}
class FakeNotifier : Notifier { val sent = mutableListOf<String>(); override fun notify(message: String) { sent += message } }

fun runChecks(f: M17Factory) = runCheckSuite("m17 checks") {
    check("places and saves") {
        val s = FakeStore(); val n = FakeNotifier()
        f.checkout(s, n, f.noDiscount()).place(1, listOf(500, 700)) == 1200L && s.saved == listOf(1 to 1200L)
    }
    check("notifies") {
        val n = FakeNotifier(); f.checkout(FakeStore(), n, f.noDiscount()).place(7, listOf(100))
        n.sent == listOf("Order 7: 100")
    }
    check("policy applied") { f.checkout(FakeStore(), FakeNotifier(), f.overThousandGets100Off()).place(1, listOf(150_000)) == 140_000L }
    check("policy threshold") { f.checkout(FakeStore(), FakeNotifier(), f.overThousandGets100Off()).place(1, listOf(100_000)) == 100_000L }
    check("custom policy without touching checkout") {
        f.checkout(FakeStore(), FakeNotifier(), DiscountPolicy { it * 2 }).place(1, listOf(10)) == 0L
    }
    check("empty order rejected") {
        runCatching { f.checkout(FakeStore(), FakeNotifier(), f.noDiscount()).place(1, emptyList()) }
            .exceptionOrNull() is IllegalArgumentException
    }
}
```
`Exercises.kt` additionally contains, above the stubs, a commented-out `class LegacyCheckout` (≈40 lines) that does all of the above with a hardcoded `MySqlOrders()` and `EmailSender()` and a `when(couponType: String)` — the learner refactors it into the contract.

**Doc:** each principle: definition in one sentence, the smell that signals the violation, before/after from the lesson, and a test that the refactor enables. Common misreadings (SRP ≠ "one method"; OCP ≠ "never edit code"). In Compose: SRP — stateless `CartContent(state, onAdd)` vs stateful `CartScreen(vm)` (state hoisting); OCP — `Modifier` chains extend behaviour without changing composables; LSP — any `Shape` works in `clip()`; ISP — narrow lambdas (`onAdd: () -> Unit`) instead of passing the whole ViewModel; DIP — ViewModel depends on a `Repository` interface, provided via constructor; `CompositionLocal` (`staticCompositionLocalOf`, `CompositionLocalProvider`) as UI-level dependency inversion.

---

### Task 21: m18 — Design patterns

**Files:** `src/main/kotlin/m18_patterns/{L01_StrategyAndFactory.kt, L02_BuilderAndDsl.kt, L03_ObserverAndState.kt, L04_DecoratorAndAdapter.kt}`, `shop/` (package `m18_patterns.shop`), `exercises/*`, `docs/18_design_patterns.md`

**Lessons:**
- `L01_StrategyAndFactory`: `PaymentStrategy` (card, wallet, cash) chosen at runtime; `PaymentFactory.from(method: String)` returning sealed result for unknown.
- `L02_BuilderAndDsl`: classic builder for `Order` vs Kotlin named/default args (why builders are rarer in Kotlin) vs DSL builder (link m14).
- `L03_ObserverAndState`: hand-written `Observable<T>` with `subscribe`/`unsubscribe` (pure Kotlin, no coroutines); State pattern: `OrderState` sealed interface whose subtypes return the next state for `pay()`, `ship()`, `cancel()`; illegal transitions print a rejection.
- `L04_DecoratorAndAdapter`: Decorator: `PriceCalculator` wrapped by `TaxDecorator(DiscountDecorator(base))`; Adapter: legacy `OldShippingApi.calc(kg: Double): Double` adapted to `ShippingMethod` (cents, grams). Repository pattern recap pointing to m13.

**`exercises/Checks.kt`:**
```kotlin
package m18_patterns.exercises

import common.check
import common.runCheckSuite

sealed interface OrderState { val name: String }

interface OrderMachine {
    val state: OrderState
    /** Allowed: Created -pay-> Paid -ship-> Shipped -deliver-> Delivered; Created|Paid -cancel-> Cancelled.
     *  Returns true when the transition happened, false (state unchanged) otherwise. */
    fun pay(): Boolean
    fun ship(): Boolean
    fun deliver(): Boolean
    fun cancel(): Boolean
}

interface PriceSource { fun priceCents(): Long }

/** Legacy API to adapt - do not change. */
class LegacyFx { fun usdToEgp(usd: Double): Double = usd * 50.0 }

interface M18Factory {
    /** State pattern: new machine in "Created". state.name is one of Created, Paid, Shipped, Delivered, Cancelled. */
    fun machine(): OrderMachine
    /** Decorator: wraps source, adds 14% VAT rounded down. */
    fun withVat(source: PriceSource): PriceSource
    /** Decorator: wraps source, subtracts fixed cents, floor 0. */
    fun withCoupon(source: PriceSource, offCents: Long): PriceSource
    /** Adapter: a PriceSource whose priceCents() is LegacyFx.usdToEgp(usd) * 100, rounded down. */
    fun fromUsd(fx: LegacyFx, usd: Double): PriceSource
    /** Observer: subscribe returns an unsubscribe function; publish delivers to current subscribers in subscription order. */
    fun <T> observable(): Pair<((T) -> Unit) -> (() -> Unit), (T) -> Unit>
}

fun runChecks(f: M18Factory) = runCheckSuite("m18 checks") {
    check("happy path") { val m = f.machine(); m.pay() && m.ship() && m.deliver() && m.state.name == "Delivered" }
    check("cannot ship unpaid") { val m = f.machine(); !m.ship() && m.state.name == "Created" }
    check("cancel after ship refused") { val m = f.machine(); m.pay(); m.ship(); !m.cancel() && m.state.name == "Shipped" }
    check("cancel from paid") { val m = f.machine(); m.pay(); m.cancel() && m.state.name == "Cancelled" }
    val base = object : PriceSource { override fun priceCents() = 10_000L }
    check("vat decorator") { f.withVat(base).priceCents() == 11_400L }
    check("stacked decorators order") { f.withVat(f.withCoupon(base, 1_000)).priceCents() == 10_260L }
    check("adapter") { f.fromUsd(LegacyFx(), 2.5).priceCents() == 12_500L }
    check("observer") {
        val (subscribe, publish) = f.observable<String>()
        val got = mutableListOf<String>()
        val off = subscribe { got += "a:$it" }; subscribe { got += "b:$it" }
        publish("1"); off(); publish("2")
        got == listOf("a:1", "b:1", "b:2")
    }
}
```

**Doc:** for each pattern: problem, Kotlin-idiomatic form (often simpler than GoF: strategies as lambdas/`fun interface`, builders replaced by named args, singletons by `object`), shop example, Compose example. In Compose: `Modifier` chain is a Decorator chain; `StateFlow`/`State` + recomposition is Observer (`snapshotFlow`, `derivedStateOf`); sealed `UiState` + `when` is the State pattern; `remember*State` factory functions (`rememberLazyListState` returning `LazyListState`) as factories; ViewModel + Repository.

---

### Task 22: Cheatsheet, README roadmap, final verification

**Files:**
- Create: `docs/cheatsheet.md`
- Modify: `README.md` (fill module table)

- [ ] **Step 1: Write `docs/cheatsheet.md`** — Arabic, bidi-clean, one section per module (00–18): a 3–5 line Kotlin snippet in a code fence + 2–3 Arabic bullet lines with the key rule + link to the module doc. Final section: decision tables (interface vs abstract vs open; enum vs sealed; data vs value vs regular class; inheritance vs composition vs delegation).

- [ ] **Step 2: Fill README module table** — columns `الـ module`, `الموضوع`, `أهم فكرة`, `ملف الشرح`; one row per module 00–18, each cell starting with an Arabic word (e.g. `الدرس 00`), linking `docs/NN_*.md`. Add "ترتيب المذاكرة المقترح" paragraph: 00–08 core, 09–15 Kotlin power features, 16–18 design.

- [ ] **Step 3: Full verification**

Run:
```bash
./gradlew clean verifyAll
python tools/check_bidi.py docs README.md
python tools/check_compose_refs.py
cd tools && python -m unittest test_check_bidi && cd ..
```
Expected: `BUILD SUCCESSFUL`; `verify outputs: ok`; `verify exercises: ok`; both lints `0 errors`; unit tests OK. Confirm 19 module packages: `ls src/main/kotlin | grep -c '^m[0-9]'` → `19`. Confirm each doc ≥ 250 lines: `wc -l docs/[0-9][0-9]_*.md`.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "docs: add cheatsheet and complete README roadmap

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

## Execution notes for the router

- Task 1 → Task 2 sequential. Tasks 3–21 are independent packages; run in batches of 4 (`3–6`, `7–10`, `11–14`, `15–18`, `19–21`). Each implementer touches only its own `src/main/kotlin/mNN_*`, `expected-output/mNN_*`, and `docs/NN_*.md` — never `README.md`, `common/`, `tools/`, or another module.
- `updateOutputs` regenerates ALL expected files; parallel implementers must check `git diff --stat expected-output` shows only their module before committing (Step G). If two batches race on the same working tree, run module tasks in separate git worktrees or sequentially within a batch's commit step.
- Router review per module: read the exercises Checks.kt diff against the plan verbatim, skim expected-output for correctness of the claimed facts, read the doc's "في Compose" section against the reference file.
