import springBatch.PrintStartMessageTasklet
import springBatch.PrintStartMessageTasklet2

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleUiJob') {
        batch.step(id: 'logStart') {
            batch.tasklet(ref: 'printStartMessage')
        }
    }

    printStartMessage(PrintStartMessageTasklet) { bean ->
        bean.autowire = "byName"
    }

    printStartMessage2(PrintStartMessageTasklet2) { bean ->
        bean.autowire = "byName"
    }

}