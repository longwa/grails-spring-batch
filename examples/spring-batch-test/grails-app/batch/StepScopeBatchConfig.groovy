import groovy.springbatchtest.BasicStepListener
import org.springframework.batch.item.database.HibernateCursorItemReader
import groovy.springbatchtest.DummyWriter

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    basicStepListener(BasicStepListener) { bean ->
        bean.scope = "step"
    }

    stepItemReader(HibernateCursorItemReader) { bean ->
        bean.scope = "step"
        sessionFactory = ref('sessionFactory')
        queryString = 'from SpringBatch'
    }

    stepItemWriter(DummyWriter) { bean ->
        bean.scope = "step"
    }

    batch.job(id: 'stepScopeJob') {
        batch.step(id: 'step1') {
            batch.tasklet() {
                batch.chunk(reader: 'stepItemReader', writer: 'stepItemWriter', 'commit-interval': 1)
                batch.listeners() {
                    batch.listener(ref: 'basicStepListener')
                }
            }
        }
    }
}