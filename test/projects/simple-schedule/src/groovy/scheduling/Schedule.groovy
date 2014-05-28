package scheduling

import grails.plugins.springbatch.SpringBatchService

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Configuration
@EnableScheduling
class Schedule {
	
	private static final Logger LOGGER = Logger.getLogger(Schedule)
	
	@Autowired
	SpringBatchService springBatchService
	
	@Autowired
	GrailsApplication grailsApplication
	
	@Scheduled(fixedDelay=30000L)
	void simpleAsyncJobTrigger(){
		trigger('simpleAsyncJob', 'jobLauncher')
	}
	
	@Scheduled(fixedDelay=30000L)
	void simpleSyncJob1Trigger(){
		trigger('simpleSyncJob1', 'syncJobLauncher')
	}
	
	@Scheduled(fixedDelay=30000L)
	void simpleSyncJob2Trigger(){
		trigger('simpleSyncJob2', 'syncJobLauncher')
	}
	
	void trigger(String jobName, String launcherName) {
		if(!ready) {
			LOGGER.info("Attempted to trigger $jobName, but app is not fully initialized")
			return
		}
		if(grailsApplication.config.scheduler.disabled) {
			LOGGER.info("Attempted to trigger $jobName, but scheduler is disabled")
			return
		}
		
		LOGGER.info("Triggering $jobName")
		
		try{
			springBatchService.launch(jobName, null, launcherName)
		}catch(Exception e){
			LOGGER.error("Job Failure", e)
		}
		
		LOGGER.info("Completing trigger of $jobName")
	}
	
	boolean ready = false
}
