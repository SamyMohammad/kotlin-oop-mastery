package m00_mental_model.exercises

/*
 * Module 00 quiz: predict the output BEFORE running the lessons.
 * Replace each TODO() with your answer as a String, then run this file.
 */
object MyQuiz : M00Quiz {
    /** A Cart with one "Pen" costing 500 cents. What does total() print? */
    override fun q1(): String = TODO()

    /** Is an Int also an Any? Answer exactly as println prints a Boolean. */
    override fun q2(): String = TODO()

    /** A function with no return type returns Unit. What does println print for it? */
    override fun q3(): String = TODO()

    /** The pillar that protects an object's state behind its methods. */
    override fun q4(): String = TODO()

    /** fail() is declared `: Nothing` and throws. Which exception class does L03 throw? */
    override fun q5(): String = TODO()
}

fun main() = runChecks(MyQuiz)
