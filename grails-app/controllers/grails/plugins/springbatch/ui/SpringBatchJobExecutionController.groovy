package grails.plugins.springbatch.ui

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobExecutionException

class SpringBatchJobExecutionController {

    def springBatchUiService
	def springBatchService

    static defaultAction = 'show'
	
	def show(Long id) {
		if(!id) {
			flash.error = "Please supply a job execution id"
			redirect(controller: "springBatchJob", action: "list")
		} else {
			[jobExecution: springBatchUiService.jobExecutionModel(id),
				modelInstances: springBatchUiService.getStepExecutionModels(id, params)]
		}
	}

	def restart(Long id) {
		if(!id) {
			flash.error = "Please supply a job execution id"
			redirect(controller: "springBatchJob", action: "list")
		} else {
			try{
				springBatchService.restart(id)
				flash.message = "Restarted Job Execution"
			}catch (JobExecutionException jee){
				flash.error = jee.message
			}
			redirect(action: "show", id:id)
		}
	}

	def stop(Long id) {
		if(!id) {
			flash.error = "Please supply a job execution id"
			redirect(controller: "springBatchJob", action: "list")
		} else {
			try{
				springBatchService.stop(id)
				flash.message = "Stopped Job Execution"
			}catch (JobExecutionException jee){
				flash.error = jee.message
			}
			redirect(action: "show", id:id)
		}
	}
}
