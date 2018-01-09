package groovy.springbatchtest

import groovy.util.logging.Commons
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener

@Commons
class BasicStepListener implements StepExecutionListener {
    @Override
    void beforeStep(StepExecution stepExecution) {
        println "beforeStep in basic step listener"
        stepExecution.executionContext.putString("testValue", "FOO")
    }

    @Override
    ExitStatus afterStep(StepExecution stepExecution) {
        println "after step in basic step listener"
        return null
    }
}
