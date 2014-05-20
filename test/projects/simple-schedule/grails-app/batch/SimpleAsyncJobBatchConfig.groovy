import springbatchtest.PrintMessageTasklet
import springbatchtest.WaitTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleAsyncJob') {
        batch.step(id: 'jobStart', next:'jobWait') {
            batch.tasklet(ref: 'simpleAsyncJobPrintStartMessage')
        }
		batch.step(id: 'jobWait', next:'jobEnd') {
			batch.tasklet(ref: 'simpleAsyncJobWait10Seconds')
		}
        batch.step(id: 'jobEnd' ) {
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