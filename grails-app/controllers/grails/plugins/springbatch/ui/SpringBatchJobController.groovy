package grails.plugins.springbatch.ui

import org.springframework.batch.core.JobParameters

class SpringBatchJobController {

    def springBatchUiService
    def springBatchService
	
    static defaultAction = 'list'

    def list() {
        [modelInstances : springBatchUiService.getJobModels(params)]
    }
	
	def show(String id) {
		if(!id) {
			flash.error = "Please supply a job name"
			redirect(controller: "springBatchJob", action: "list")
		} else {
			[job: springBatchUiService.jobModel(id),
				jobModelInstances: springBatchUiService.getJobInstanceModels(id, params)]
		}
	}

	def launch(String id){
		JobParameters jobParams = springBatchUiService.buildJobParametersFromRequest(params)
		
		Map result = springBatchService.launch(id, jobParams, Boolean.valueOf(params.async))

		if(result.success){
			flash.message = result.message
		}else{
			flash.error = result.message
		}
		
		String action = (params.a=='l')?'list':'show'
		
		redirect(action:action, id:id)
	}

	def stopAllExecutions(String id) {
		if(!id) {
			flash.error = 'No job selected to stop'
			redirect(action:"list")
		} else {
			springBatchService.stopAllJobExecutions(id)
			
			flash.message = "Stopped all Job Executions for Job $id"
			redirect(action:'show', id:id)
		}
	}
}
