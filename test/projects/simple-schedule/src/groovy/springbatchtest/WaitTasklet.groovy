package springbatchtest

import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext

class WaitTasklet implements Tasklet {
	
	int waitTimeInSeconds
	
    RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        Thread.sleep(waitTimeInSeconds * 1000L)
        return RepeatStatus.FINISHED
    }
}
