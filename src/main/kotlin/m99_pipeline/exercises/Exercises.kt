package m99_pipeline.exercises

object MyPipeline : PipelineContract {
    override fun double(x: Int): Int = TODO()
}

fun main() = runChecks(MyPipeline)
