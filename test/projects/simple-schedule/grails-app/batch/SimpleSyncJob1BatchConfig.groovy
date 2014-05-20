import springbatchtest.PrintMessageTasklet
import springbatchtest.WaitTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleSyncJob1') {
        batch.step(id: 'jobStart', next:'jobWait') {
            batch.tasklet(ref: 'simpleSyncJob1PrintStartMessage')
        }
		batch.step(id: 'jobWait', next:'jobEnd') {
			batch.tasklet(ref: 'simpleSyncJob1Wait10Seconds')
		}
        batch.step(id: 'jobEnd' ) {
            batch.tasklet(ref: 'simpleSyncJob1PrintEndMessage')
        }
    }

    simpleSyncJob1PrintStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Simple Sync Job 1'
    }
	
	simpleSyncJob1Wait10Seconds(WaitTasklet) { bean ->
		bean.autowire = "byName"
		waitTimeInSeconds = 10
	}

    simpleSyncJob1PrintEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Simple Sync Job 1'
    }
}