package groovy.springbatchtest

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.item.ItemWriter

class DummyWriter implements ItemWriter, StepExecutionListener {
    int count = 0

    @Override
    void beforeStep(StepExecution stepExecution) {
        println "before step in dummy writer (count $count runs)"
        count++
    }

    @Override
    ExitStatus afterStep(StepExecution stepExecution) {
        println "after step in dummy writer"
        return null
    }

    @Override
    void write(List items) throws Exception {
        println "writing $items"
    }
}
