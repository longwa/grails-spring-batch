package springbatchtest

import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext

class PrintMessageTasklet implements Tasklet {
	
	String mesg
	
    RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        println mesg
        return RepeatStatus.FINISHED
    }
}
