import springbatchtest.PrintMessageTasklet

beans {
    xmlns batch:"http://www.springframework.org/schema/batch"

    batch.job(id: 'multiStepJob') {
        batch.step(id: 'jobStart', next:'step1') {
            batch.tasklet(ref: 'printStartMessage')
        }
        batch.step(id: 'step1', next: 'step2') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step2', next: 'step3') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step3', next: 'step4') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step4', next: 'step5') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step5', next: 'step6') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step6', next: 'step7') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step7', next: 'step8') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step8', next: 'step9') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step9', next: 'step10') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'step10', next: 'jobEnd') {
            batch.tasklet(ref: 'printMessage')
        }
        batch.step(id: 'jobEnd' ) {
            batch.tasklet(ref: 'printEndMessage')
        }
    }

    printStartMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Starting Job'
    }

    printMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Running a step'
    }

    printEndMessage(PrintMessageTasklet) { bean ->
        bean.autowire = "byName"
		mesg = 'Finishing Job'
    }
}