package grails.plugins.springbatch.ui

import grails.converters.JSON

import org.springframework.batch.core.JobParameters

class SpringBatchJobController {

    def springBatchUiService
    def springBatchService
	
    static defaultAction = 'list'

	def status() {
		if(!params.job) {
			render ([success:false, message:'No job submitted for status check']) as JSON
			return
		}
		
		Map statuses = [:]
		
		if(params.job instanceof String) {
			String job = params.job
			statuses.put( job, springBatchService.jobStatus(job))
		}else if(params.job instanceof String[]) {
			params.job.each{ String job ->
				statuses.put( job, springBatchService.jobStatus(job))
			}
		}else {
			render ([success:false, message:"Class: ${params.job.class.name}"])
			return
		}
		
		render ([success:true, data:statuses])
	}
	
    def list() {
        [modelInstances : springBatchUiService.getJobModels(params),
			ready: springBatchService.ready]
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
		
		boolean canBeConcurrent = params.canBeConcurrent ? 
				params.canBeConcurrent.toBoolean() : true
		
		Map result = springBatchService.launch(id, canBeConcurrent, jobParams, 
				params.jobLauncherName)

		if(result.success){
			flash.message = result.message
		}else{
			flash.error = result.message
		}
		
		String action = (params.a=='l')?'list':'show'
		
		redirect(action:action, id:id)
	}
	
	def enableLaunching() {
		springBatchService.ready = true
		redirect action:'list'
	}
	
	def disableLaunching() {
		springBatchService.ready = false
		redirect action:'list'
	}

	def stopAllExecutions(String id) {
		if(!id) {
			springBatchService.stopAllJobExecutions()
			
			flash.message = 'Stopping all Job Executions for all Jobs'
			redirect(action:"list")
		} else {
			springBatchService.stopAllJobExecutions(id)
			
			flash.message = "Stopped all Job Executions for Job $id"
			redirect(action:'show', id:id)
		}
	}
}
