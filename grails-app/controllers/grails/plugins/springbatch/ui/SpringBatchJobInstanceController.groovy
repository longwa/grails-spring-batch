package grails.plugins.springbatch.ui

class SpringBatchJobInstanceController {

    def springBatchUiService

    static defaultAction = 'show'

	def show(String jobName, Long id) {
		if(!jobName) {
			flash.error = "Please supply a job name"
			redirect(controller: "springBatchJob", action: "list")
			return
		}
		if(!id) {
			flash.error = "Please supply a job instance id"
			redirect(controller: "springBatchJob", action: "show", id: jobName)
			return
		}
	
		[jobInstance: springBatchUiService.jobInstanceModel(id), 
			modelInstances: springBatchUiService.getJobExecutionModels(jobName, id, params)]
	}
}
