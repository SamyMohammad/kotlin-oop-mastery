package m99_pipeline.exercises.solutions

import m99_pipeline.exercises.PipelineContract
import m99_pipeline.exercises.runChecks

object PipelineSolution : PipelineContract {
    override fun double(x: Int): Int = x * 2
}

fun main() = runChecks(PipelineSolution)
