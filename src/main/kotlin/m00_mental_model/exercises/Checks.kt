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
