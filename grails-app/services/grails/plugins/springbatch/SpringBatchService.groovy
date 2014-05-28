package grails.plugins.springbatch

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException

class SpringBatchService {
	
	static transactional = false
	
	def grailsApplication
	
	/**
	 * Asynchronous Job launcher - default - used for controllers
	 */
	JobLauncher jobLauncher
	
	/**
	 * Synchronous Job launcher - used for quartz jobs
	 */
	JobLauncher syncJobLauncher
	
	/**
	 * The spring bean name of the the job launcher to use by default
	 */
	String defaultJobLauncher = 'jobLauncher'
	
	
	JobRegistry jobRegistry
	
	JobService jobService
	JobOperator jobOperator
	
	
	JobInstance jobInstance(Long jobInstanceId){
		jobService.getJobInstance(jobInstanceId)
	}
	
	JobExecution jobExecution(Long jobExecutionId){
		jobService.getJobExecution(jobExecutionId)
	}
	
	void restart(Long jobExecutionId){
		jobService.restart(jobExecutionId)
	}

	void stop(Long jobExecutionId){
		jobService.stop(jobExecutionId)
	}

	void stopAllJobExecutions(String jobName){
		Set<Long> executions = jobOperator.getRunningExecutions(jobName)
		log.info("Attempting to stop ${executions.size()} job executions for $jobName")
		
		executions.each{
			try{
				jobOperator.stop(it)
			}catch(Exception e){
				log.debug(e)
			}
		}
	}
	
	boolean hasRunningExecutions(String jobName) {
		try {
			Set<Long> executions = jobOperator.getRunningExecutions(jobName)
			return !(executions?.isEmpty())
		}catch(NoSuchJobException nsje) {
			return false
		}
	}
	
	/**
	 *
	 * @param jobName
	 * @param jobLauncherName - initially configured choices "jobLauncher" and "syncJobLauncher"
	 */
	Map launch(String jobName, JobParameters jobParams, 
			String jobLauncherName = "jobLauncher") {
		
		Job job
		try{
			job = jobRegistry.getJob(jobName)
		}catch(NoSuchJobException nsje){
			return [success: false, message:"Did not find Spring Batch Job: $jobName"]
		}
		
		JobLauncher selectedLauncher 
		if(jobLauncherName) {
			selectedLauncher = grailsApplication.mainContext.getBean(jobLauncherName)
		}
		if(!selectedLauncher) {
			selectedLauncher = grailsApplication.mainContext.getBean(defaultJobLauncher)
			log.info "JobLaucher $jobLauncherName selected but not found, trying $defaultJobLauncher"
		}
		if(!selectedLauncher) {
			return [success: false, message:"Invalid jobLauncher $jobLauncherName selected"]
		}
		
		if(!jobParams) {
			log.info "No job parameters provided for job, defaulting to date"
			jobParams = mapToJobParameters(defaultJobParameters())
		}
		
		try{
			selectedLauncher.run(job, jobParams)
		}catch(JobInstanceAlreadyCompleteException jiace){
			return[success: false, message: jiace.message]
		}
		
		return [success: true, message:"Spring Batch Job($jobName) launched from EtlService"]
	}
	
	Map defaultJobParameters() {
		['date': new JobParameter(new Date().getDateTimeString())]
	}
	
	JobParameters mapToJobParameters(Map map) {
		return new JobParameters(map)
	}
}
