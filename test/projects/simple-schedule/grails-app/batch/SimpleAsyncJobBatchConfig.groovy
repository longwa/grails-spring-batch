import springbatchtest.PrintMessageTasklet
import springbatchtest.WaitTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleAsyncJob') {
        batch.step(id: 'simpleAsyncJobStart', next:'simpleAsyncJobWait') {
            batch.tasklet(ref: 'simpleAsyncJobPrintStartMessage')
        }
		batch.step(id: 'simpleAsyncJobWait', next:'simpleAsyncJobEnd') {
			batch.tasklet(ref: 'simpleAsyncJobWait10Seconds')
		}
        batch.step(id: 'simpleAsyncJobEnd' ) {
            batch.tasklet(ref: 'simpleAsyncJobPrintEndMessage')
        }
    }

    simpleAsyncJobPrintStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Simple Async Job'
    }
	
	simpleAsyncJobWait10Seconds(WaitTasklet) { bean ->
		bean.autowire = "byName"
		waitTimeInSeconds = 10
	}

    simpleAsyncJobPrintEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Simple Async Job'
    }
}