import springBatch.PrintStartMessageTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleUiJob') {
        batch.step(id: 'logStart') {
            batch.tasklet(ref: 'printStartMessage2')
        }
    }

    printStartMessage(PrintStartMessageTasklet) { bean ->
        bean.autowire = "byName"
    }

    printStartMessage2(PrintStartMessageTasklet) { bean ->
        bean.autowire = "byName"
    }

}