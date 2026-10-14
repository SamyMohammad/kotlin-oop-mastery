package m99_pipeline.exercises

import common.check
import common.runCheckSuite

interface PipelineContract {
    fun double(x: Int): Int
}

fun runChecks(impl: PipelineContract) = runCheckSuite("m99 checks") {
    check("double(2) == 4") { impl.double(2) == 4 }
}
