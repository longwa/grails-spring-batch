import springbatchtest.PrintStartMessageTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleJob2') {
        batch.step(id: 'logStart') {
            batch.tasklet(ref: 'printStartMessage')
        }
    }

    printStartMessage(PrintStartMessageTasklet) { bean ->
        bean.autowire = "byName"
    }

}