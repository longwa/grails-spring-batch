import springbatchtest.PrintMessageTasklet
import springbatchtest.WaitTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleAsyncJob') {
        batch.step(id: 'jobStart', next:'jobWait') {
            batch.tasklet(ref: 'printStartMessage')
        }
		batch.step(id: 'jobWait', next:'jobEnd') {
			batch.tasklet(ref: 'wait10Seconds')
		}
        batch.step(id: 'jobEnd' ) {
            batch.tasklet(ref: 'printEndMessage')
        }
    }

    printStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Simple Async Job'
    }
	
	wait10Seconds(WaitTasklet) { bean ->
		bean.autowire = "byName"
		waitTimeInSeconds = 10
	}

    printEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Simple Async Job'
    }
}