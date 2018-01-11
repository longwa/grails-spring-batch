import springbatchtest.ExceptionalTasklet
import springbatchtest.PrintMessageTasklet
import springbatchtest.WaitTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'exceptionalJob') {
        batch.step(id: 'exceptionalJobStart', next:'exceptionalJobWait') {
            batch.tasklet(ref: 'exceptionalJobPrintStartMessage')
        }
		batch.step(id: 'exceptionalJobWait', next:'exceptionalJobExceptional') {
			batch.tasklet(ref: 'exceptionalJobWait10Seconds')
		}
		batch.step(id: 'exceptionalJobExceptional', next:'exceptionalJobEnd') {
			batch.tasklet(ref: 'exceptionalJobExceptionalTask')
		}
        batch.step(id: 'exceptionalJobEnd' ) {
            batch.tasklet(ref: 'exceptionalJobPrintEndMessage')
        }
    }

    exceptionalJobPrintStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Exceptional Job'
    }
	
	exceptionalJobWait10Seconds(WaitTasklet) { bean ->
		bean.autowire = "byName"
		waitTimeInSeconds = 10
	}
	
	exceptionalJobExceptionalTask(ExceptionalTasklet) { bean ->
		bean.autowire = "byName"
	}

    exceptionalJobPrintEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Exceptional Job'
    }
}