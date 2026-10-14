# Kotlin OOP Mastery — Design Spec

Date: 2026-10-14
Status: Draft — awaiting user review

## 1. Goal

A self-study project that takes the learner from zero to mastery of every
object-oriented concept in Kotlin. Mastery = the learner can explain each
concept, predict the output of lesson code, solve the exercises, and recognise
the concept inside real Jetpack Compose / androidx APIs.

### Decisions already made (from brainstorming)

| Topic | Decision |
|---|---|
| Location | New sibling folder `StudioProjects/kotlin-oop-mastery`, own git repo |
| Runtime | Plain Kotlin JVM console project. Compose examples live in the `.md` files only (not runnable) |
| Doc language | Egyptian Arabic prose, English technical terms, following the user's bidi rules |
| Practice | Per-module exercises with `TODO()` stubs + a `Checks.kt` runner printing PASS/FAIL + separate solutions |
| Structure | Concept-ordered modules, one evolving e-commerce domain (`shop`), "In Compose" section in every doc |
| Reference repo | Mirrors conventions of `StudioProjects/flow-viewmodel-lessons` |

### Out of scope

- Any Android or Compose runtime / dependency.
- Unit-test frameworks (checks are plain `main()` functions).
- Coroutines / Flow (covered by `flow-viewmodel-lessons`).

## 2. Curriculum (19 modules, 00–18)

| # | Package | Kotlin concepts | Compose anchor (in md) |
|---|---|---|---|
| 00 | `m00_mental_model` | 4 pillars, objects vs data+functions, `Any` / `Unit` / `Nothing` hierarchy | Composables are functions; the APIs beneath them are objects |
| 01 | `m01_classes` | primary/secondary constructors, `init` order, properties, custom get/set, `field`, `lateinit`, `lazy` | `Product` model rendered by a `ProductCard` |
| 02 | `m02_encapsulation` | `private` / `protected` / `internal` / `public`, `private set`, backing property | `_uiState` / `uiState` in a ViewModel |
| 03 | `m03_inheritance` | `open`, `override`, `super`, final-by-default, constructor chaining, open-call-in-constructor pitfall | `ComponentActivity`, `ViewModel` subclasses |
| 04 | `m04_abstract` | abstract members, template method | `Painter` (`onDraw`), `Modifier.Node` |
| 05 | `m05_interfaces` | default methods, interface properties, multiple interfaces, diamond resolution, `fun interface` | `Modifier`, `Shape`, `Arrangement.Horizontal`, `Indication` |
| 06 | `m06_polymorphism` | dynamic dispatch, overloading, `is` / `as` / smart cast | `BitmapPainter` vs `VectorPainter`, custom `Shape` |
| 07 | `m07_data_classes` | `equals` / `hashCode` / `copy` / `componentN`, immutability | `UiState.copy()`, stability, recomposition skipping |
| 08 | `m08_sealed_enums` | sealed class vs sealed interface, enums with members, exhaustive `when` | Loading/Success/Error screens, navigation routes |
| 09 | `m09_object_companion` | singletons, factories, constants | `Modifier` companion, `MaterialTheme` object |
| 10 | `m10_value_classes` | `@JvmInline value class`, type safety without runtime cost | `Dp`, `Color`, `TextUnit` |
| 11 | `m11_nested_inner_anonymous` | nested vs `inner`, `object :` expressions | anonymous `PreviewParameterProvider` |
| 12 | `m12_delegation` | class delegation `by`, property delegates, `getValue` / `setValue`, `observable` | `by remember { mutableStateOf() }`, `by viewModels()` |
| 13 | `m13_generics` | `in` / `out`, constraints, `reified`, star projection | `State<out T>`, `LazyColumn` `items<T>` |
| 14 | `m14_extensions_receivers` | extension functions/properties, lambda with receiver, DSL | `Modifier.xxx()`, `RowScope.weight`, `BoxScope.align` |
| 15 | `m15_operators_equality` | operator overloading, `==` vs `===`, `toString` | `Dp + Dp`, `Offset` math, `Modifier.then` |
| 16 | `m16_composition` | has-a vs is-a, slot APIs | `Scaffold` slots, wrapper Composables |
| 17 | `m17_solid` | SRP, OCP, LSP, ISP, DIP — each as before/after refactor | stateless vs stateful Composables, injected repository |
| 18 | `m18_patterns` | Strategy, Factory, Builder/DSL, Observer, Decorator, Adapter, State, Repository | `Modifier` chain = Decorator, `StateFlow` = Observer |

Modules 00–16 have classic exercises. Modules 17–18 have refactor exercises
on the shop domain (stub = "bad" code, check = behaviour preserved + new
requirement satisfied).

Module 00 has no `exercises/` folder beyond a short quiz-style `Checks.kt`
(predict-the-output questions).

## 3. Folder layout

```
kotlin-oop-mastery/
├── README.md                     roadmap, module table, how to run, study order
├── build.gradle.kts              kotlin("jvm") 2.2.10, JVM 17, no dependencies
├── settings.gradle.kts           rootProject.name = "kotlin-oop-mastery"
├── gradle/, gradlew, gradlew.bat copied from flow-viewmodel-lessons
├── .gitignore
├── docs/
│   ├── 00_oop_mental_model.md … 18_design_patterns.md
│   ├── cheatsheet.md
│   ├── reference/compose_signatures.md   verified androidx signatures (research output)
│   └── superpowers/specs/, superpowers/plans/
├── expected-output/
│   └── m01__L01_Constructors.txt …       <package>__<LessonFile>.txt
├── tools/
│   ├── check_bidi.py                     md bidi lint
│   └── (output diff implemented as a Gradle task)
└── src/main/kotlin/
    ├── common/Log.kt                     section(title), log(msg), check(name, cond)
    └── mNN_<name>/
        ├── shop/                         this module's snapshot of the shop domain (when used)
        ├── L01_<Topic>.kt                fun main()
        ├── L02_<Topic>.kt
        └── exercises/
            ├── Exercises.kt              TODO() stubs, KDoc describing each task
            ├── Checks.kt                 fun main(): runs checks against Exercises
            └── solutions/Solutions.kt
```

### Shop domain

Each module owns a **snapshot** copy of the shop code it needs under
`mNN_<name>/shop/`, so earlier modules remain readable as they were taught.
Evolution highlights: plain classes (m01) → encapsulated `Cart` (m02) →
`Payment` hierarchy (m03–m06) → `data class Product` (m07) → `sealed OrderStatus`
(m08) → `Price` value class (m10) → delegated `Cart` (m12) → `Repository<T>`
(m13) → refactored to SOLID (m17) → `PaymentStrategy`, `OrderState` etc. (m18).

### Checks runner switching

`Checks.kt` targets the stubs by default. A single top-level
`val USE_SOLUTIONS = false` flag in each `Checks.kt` switches to `Solutions.kt`
(same function names in a different package, chosen via an interface or a
function-reference table). Stub run → FAIL with "not implemented yet" (catching
`NotImplementedError`), never a crash.

## 4. Doc anatomy (every `docs/NN_*.md`)

1. **المشكلة الأول** — shop scenario showing why the concept exists.
2. **الـ concept بالتفصيل** — syntax, rules, what Kotlin generates under the hood
   (Java equivalent / decompiled view where useful), Kotlin vs Java differences.
3. **امشي مع الكود** — links to each `L0x_*.kt`, expected output explained line by line.
4. **في Compose** — 2–4 real APIs dissected with signatures copied from
   `docs/reference/compose_signatures.md`, then one complete Compose screen using
   the concept, labelled as not runnable in this project.
5. **أخطاء شائعة** — bug → fix pairs.
6. **أسئلة Interview** — 5–8 questions with answers in `<details>` blocks.
7. **Self-check** — checklist; module mastered when all ticked and `Checks.kt` is all PASS.

### Bidi rules (from user CLAUDE.md, enforced by `tools/check_bidi.py`)

- No bidi control characters (U+200E, U+200F, U+202A–U+202E, U+2066–U+2069) or their escape text.
- Every line containing Arabic starts with an Arabic word (headings, bullets,
  table cells included — after markdown markers like `#`, `-`, `|`, `1.`).
- English terms, filenames and paths in backticks, never at the very start or end of an Arabic line.
- Multi-line or symbol-heavy content in fenced code blocks; `->` / `=>` / `→`
  never appear in Arabic prose (use `←`).
- Lines entirely in English are allowed.

Note: the existing repo uses a leading RLM (U+200F) in some lines; this project
does **not** — the user's CLAUDE.md forbids it.

## 5. Lesson code conventions

- One concept per lesson file, 60–150 lines, `fun main()` at the bottom.
- Output via `common/Log.kt` helpers so output is deterministic (no timestamps,
  no thread names, no hash codes or identity-based output unless the lesson is
  about identity — then printed as `same`/`different`, not raw hashes).
- Comments explain *why*, in English (code stays English; Arabic lives in md).
- No external dependencies.

## 6. Verification gates (all must pass before a module is "done")

1. **Compiles** — `./gradlew build` green.
2. **Lessons run** — a Gradle task `verifyOutputs` runs every lesson `main()`
   and diffs against `expected-output/`; any diff fails the task.
3. **Exercises solvable** — every `Checks.kt` all PASS with `USE_SOLUTIONS = true`,
   and prints FAIL (no crash) against stubs.
4. **Compose accuracy** — every signature in an "In Compose" section exists in
   `docs/reference/compose_signatures.md`, which was verified against current
   androidx docs via context7.
5. **Bidi lint** — `python tools/check_bidi.py docs README.md` exits 0.
6. **Router review** — main session reviews each module's diff and outputs.

## 7. Build order

1. **Scaffold** — Gradle project, `Log.kt`, `.gitignore`, README skeleton,
   `check_bidi.py`, `verifyOutputs` task, one sample lesson to prove the pipeline.
2. **Research** — `researcher` agent collects verified Compose/androidx signatures
   for all 19 modules → `docs/reference/compose_signatures.md`.
3. **Modules** — `implementer` agents in parallel batches by independent package:
   00–04, 05–08, 09–12, 13–16, 17–18. Each task: lessons + expected output +
   exercises/checks/solutions + md doc, verified by gates 1–5.
4. **Finish** — `cheatsheet.md`, README roadmap complete, all gates re-run, commit.

## 8. Success criteria

- 19 modules, each with ≥2 runnable lessons, an md doc following the anatomy, and passing gates.
- `./gradlew build verifyOutputs` green; bidi lint clean.
- Every module's `Checks.kt` all PASS on solutions.
- README gives a clear study path from module 00 to 18.
