package grails.plugins.springbatch.ui

class SpringBatchJobExecutionController {

    def springBatchUiService

    static defaultAction = 'show'
	
	def show(Long id) {
		if(!id) {
			flash.error = "Please supply a job execution id"
			redirect(controller: "springBatchJob", action: "list")
		} else {
			[jobExecution: springBatchUiService.jobExecutionModel(id),
				modelInstances: springBatchUiService.getStepExecutionModels(id, params),
				jobExecutionId:id]
		}
	}
}
