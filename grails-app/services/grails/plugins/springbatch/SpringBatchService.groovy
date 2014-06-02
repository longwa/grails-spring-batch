package grails.plugins.springbatch

import groovy.sql.Sql

import javax.sql.DataSource

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsContextEvent
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.launch.NoSuchJobException
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent

class SpringBatchService implements  ApplicationListener {
	
	static transactional = false
	
	GrailsApplication grailsApplication
	
	/**
	 * true when the grails app has added dynamic methods to classes
	 */
	boolean ready = false
	
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
	
	/**
	 * Fetch the JobInstance by id
	 */
	JobInstance jobInstance(Long jobInstanceId){
		jobService.getJobInstance(jobInstanceId)
	}
	
	/**
	 * Fetch the JobExecution by id
	 */
	JobExecution jobExecution(Long jobExecutionId){
		jobService.getJobExecution(jobExecutionId)
	}
	
	/**
	 * Fetch the JobExecution by id
	 */
	StepExecution stepExecution(Long jobExecutionId, Long stepExecutionId){
		jobService.getStepExecution(jobExecutionId, stepExecutionId)
	}
	
	Collection<StepExecution> previousStepExecutions(String jobName, String stepName, int start, int max){
		jobService.listStepExecutionsForStep(jobName, stepName, start, max)
	}
	
	/**
	 * Restart the JobExecution
	 */
	void restart(Long jobExecutionId){
		jobService.restart(jobExecutionId)
	}

	/**
	 * Stop the JobExecution
	 */
	void stop(Long jobExecutionId){
		jobService.stop(jobExecutionId)
	}

	/**
	 * Stop all JobExecutions for a Job by name
	 */
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
	
	/**
	 * True if there are any running executions for the Job
	 */
	boolean hasRunningExecutions(String jobName) {
		
		try {
			//Set<Long> executions = jobOperator.getRunningExecutions(jobName)
			//return !(executions?.isEmpty())
			
			def result = batchSql.firstRow(
"""select count(bje.job_execution_id) as "executionCount" 
from ${batchTablePrefix}job_execution bje
  inner join ${batchTablePrefix}job_instance bji on bje.job_instance_id = bji.job_instance_id
where bji.job_name = ?
  and bje.status in ('STARTING', 'STARTED')""".toString(), [jobName])
			return result['executionCount'] > 0
						
		}catch(Exception nsje) {
			log.info("Failed to acquire running executions for $jobName", nsje)
			return false
		}
	}
	
	/**
	 * Launch a job
	 *
	 * @param jobName
	 * @param canBeConcurrent - when set to false, will look to see if this job
	 * is already executing and abort launch.  When true, it will always launch a new job
	 * @param jobParams
	 * @param jobLauncherName - initially configured choices "jobLauncher" and "syncJobLauncher"
	 */
	Map launch(String jobName, boolean canBeConcurrent=true, 
			JobParameters jobParams = null, String jobLauncherName = null) {
		
		// Is the app ready?
		if(!ready) {
			return [success:false, 
					message:"Attempted to launch $jobName, but app is not ready or job processing is disabled.",
					failurePriority: 'low']
		}
		
		// Select Job
		Job job
		try{
			job = jobRegistry.getJob(jobName)
		}catch(NoSuchJobException nsje){
			return [success: false, message:"Did not find Spring Batch Job: $jobName",
					failurePriority: 'high']
		}
		
		// Can we run more than one at a time?
		if(!canBeConcurrent && hasRunningExecutions(jobName)) {
			return [success: false, 
					message:"Attempted to launch $jobName, but it is currently running.  Aborting launch.",
					failurePriority: 'low']
		}
		
		// Select Job Launcher
		JobLauncher selectedLauncher 
		if(jobLauncherName) {
			selectedLauncher = grailsApplication.mainContext.getBean(jobLauncherName)
		}
		if(!selectedLauncher) {
			selectedLauncher = grailsApplication.mainContext.getBean(defaultJobLauncher)
			log.debug "JobLaucher $jobLauncherName selected but not found, trying $defaultJobLauncher"
		}
		if(!selectedLauncher) {
			return [success: false, message:"Invalid jobLauncher $jobLauncherName selected",
					failurePriority: 'high']
		}
		
		// Select Job Parameters
		if(!jobParams) {
			log.debug "No job parameters provided for job, defaulting to date"
			jobParams = mapToJobParameters(defaultJobParameters())
		}
		
		// Run the Job
		try{
			selectedLauncher.run(job, jobParams)
		}catch(JobInstanceAlreadyCompleteException jiace){
			return[success: false, message: jiace.message, failurePriority: 'high']
		}
		
		return [success: true, message:"Spring Batch Job($jobName) launched from EtlService"]
	}
	
	/**
	 * Job parameters to use by default
	 */
	Map defaultJobParameters() {
		['date': new JobParameter(new Date().getDateTimeString())]
	}
	
	/**
	 * Turn map of job parameters into Spring Batch JobParametersModel
	 */
	JobParameters mapToJobParameters(Map map) {
		return new JobParameters(map)
	}
	
	/**
	 * For job x, give me the last time it ran and whether it failed or not
	 * 
	 * This method gives some of the same info as the SpringBatchUiService.jobModel,
	 * but might be more useful for automated monitoring.
	 */
	Map jobStatus(String jobName) {
		List<JobExecution> mostRecentJobExecutions = jobService.listJobExecutionsForJob(jobName, 0, 1)
		if(!mostRecentJobExecutions) {
			return [success:false]
		}
		
		JobExecution mostRecent = mostRecentJobExecutions[0]
		
		boolean success = !mostRecent.status.isUnsuccessful()
		boolean running = mostRecent.status.isRunning()
		Date executionStartTime = mostRecent.startTime
		Date executionEndTime = mostRecent.endTime
		
		return [success:success, running:running, executionStartTime:executionStartTime, executionEndTime: executionEndTime]
	}

	/**
	 * Spring event listener to detect when the app is ready for job launching
	 */
	@Override
	void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof GrailsContextEvent) {
			ready = true
		}
		if(event instanceof ContextClosedEvent) {
			ready = false
			log.info("No longer accepting Batch Jobs, shutting down.")
		}
	}
	
	
	private DataSource _batchDataSource
	private DataSource getBatchDataSource() {
		if(!_batchDataSource) {
			_batchDataSource = grailsApplication.mainContext.getBean(grailsApplication.config.plugin.springBatch.dataSource)
		}
		return _batchDataSource
	}
	private String _batchTablePrefix
	private String getBatchTablePrefix() {
		if(null==_batchTablePrefix) {
			_batchTablePrefix = grailsApplication.config.plugin.springBatch.tablePrefix ? (grailsApplication.config.plugin.springBatch.tablePrefix + '_') : 'BATCH_'
		}
		return _batchTablePrefix
	}
	
	private Sql getBatchSql() {
		return new Sql(batchDataSource)
	}
}
