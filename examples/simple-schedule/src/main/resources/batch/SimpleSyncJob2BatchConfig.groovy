import springbatchtest.PrintMessageTasklet
import springbatchtest.WaitTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'simpleSyncJob2') {
        batch.step(id: 'simpleSyncJob2Start', next:'simpleSyncJob2Wait') {
            batch.tasklet(ref: 'simpleSyncJob2PrintStartMessage')
        }
		batch.step(id: 'simpleSyncJob2Wait', next:'simpleSyncJob2End') {
			batch.tasklet(ref: 'simpleSyncJob2Wait10Seconds')
		}
        batch.step(id: 'simpleSyncJob2End' ) {
            batch.tasklet(ref: 'simpleSyncJob2PrintEndMessage')
        }
    }

    simpleSyncJob2PrintStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Simple Sync Job 2'
    }
	
	simpleSyncJob2Wait10Seconds(WaitTasklet) { bean ->
		bean.autowire = "byName"
		waitTimeInSeconds = 10
	}

    simpleSyncJob2PrintEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Simple Sync Job 2'
    }
}