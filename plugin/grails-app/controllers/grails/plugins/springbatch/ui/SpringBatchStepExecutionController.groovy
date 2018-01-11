package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.StepExecutionModel

class SpringBatchStepExecutionController {

    def springBatchUiService

    static defaultAction = 'show'
	
	def show(Long id) {
		Long jobExecutionId
		
		if(params.jobExecutionId && params.jobExecutionId.isLong()) {
			jobExecutionId = params.jobExecutionId.toLong()
		}
		
		if(!id || !jobExecutionId) {
			flash.error = "Please supply a Step execution id and a Job execution id"
			redirect(controller: "springBatchJob", action: "list")
		} else {
			StepExecutionModel stepExecution = springBatchUiService.stepExecutionModel(jobExecutionId, id)
			def modelInstances = []
			if(stepExecution) {
				modelInstances = springBatchUiService.previousStepExecutionModels(
						stepExecution, params)
			}
			
			[stepExecution: stepExecution, modelInstances:modelInstances]
		}
	}
}
