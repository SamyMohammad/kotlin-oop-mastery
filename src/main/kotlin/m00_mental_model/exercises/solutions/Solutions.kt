package m00_mental_model.exercises.solutions

import m00_mental_model.exercises.M00Quiz
import m00_mental_model.exercises.runChecks

object QuizSolution : M00Quiz {
    // total() returns a Long; println prints it without a unit or decimals
    override fun q1() = "500"

    // Every non-null value is an Any, so `is Any` is always true
    override fun q2() = "true"

    // Unit is a real object; its toString() is "kotlin.Unit"
    override fun q3() = "kotlin.Unit"

    // Encapsulation = state is private, only methods can change it
    override fun q4() = "encapsulation"

    // fail() in L03 throws IllegalStateException; its return type Nothing means "never returns"
    override fun q5() = "IllegalStateException"
}

fun main() = runChecks(QuizSolution)
