package grails.plugins.springbatch

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException

class SpringBatchService {
	
	static transactional = false
	
	/**
	 * Asynchronous Job launcher - default - used for controllers
	 */
	JobLauncher jobLauncher

	/**
	 * Synchronous Job launcher - used for quartz jobs
	 */
	JobLauncher syncJobLauncher
	
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
		Set<Long> executions = jobOperator.getRunningExecutions(jobName);
		log.info("Attempting to stop ${executions.size()} job executions for $jobName")
		
		executions.each{
			try{
				jobOperator.stop(it)
			}catch(Exception e){
				log.debug(e)
			}
		}
	}
	
	
	/**
	 *
	 * @param jobName
	 * @param async true if from controller, false if from quartz
	 */
	Map launch(String jobName, JobParameters jobParams, boolean async = true) {
		Job job
		try{
			job = jobRegistry.getJob(jobName)
		}catch(NoSuchJobException nsje){
			return [success: false, message:"Did not find Spring Batch Job: $jobName"]
		}
		
		JobLauncher selectedLauncher = async ? jobLauncher : syncJobLauncher
	
		try{
			selectedLauncher.run(job, jobParams)
		}catch(JobInstanceAlreadyCompleteException jiace){
			return[success: false, message: jiace.message]
		}
		
		return [success: true, message:"Spring Batch Job($jobName) launched from EtlService"]
	}
	
}
