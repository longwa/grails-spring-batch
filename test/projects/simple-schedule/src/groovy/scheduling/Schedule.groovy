package scheduling

import grails.plugins.springbatch.SpringBatchService

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class Schedule {
	
	private static final Logger LOGGER = Logger.getLogger(Schedule)
	
	@Autowired
	SpringBatchService springBatchService
	
	@Autowired
	GrailsApplication grailsApplication
	
	@Scheduled(fixedDelay=10000L)
	void simpleAsyncJobTrigger(){
		trigger('simpleAsyncJob')
	}
	
	@Scheduled(fixedDelay=10000L)
	void simpleSyncJob1Trigger(){
		trigger('simpleSyncJob1', false)
	}
	
	@Scheduled(fixedDelay=10000L)
	void simpleSyncJob2Trigger(){
		trigger('simpleSyncJob2', false)
	}
	
	void trigger(String jobName, boolean canBeConcurrent = true, String launcherName = null) {
		if(isDisabled()) {
			LOGGER.info("Attempted to trigger $jobName, but scheduler is disabled")
			return
		}
		
		LOGGER.info("Triggering $jobName")
		
		try{
			Map result = springBatchService.launch(jobName, canBeConcurrent, null, launcherName)
			
			if(result.success) {
				LOGGER.info("Completed trigger of $jobName.  Result: $result")
			}else {
				if(result.failurePriority == 'high') {
					LOGGER.error("Job Launch Failed.  Result: $result")
				}else {
					LOGGER.warn("Job Launch Failed.  Result: $result")
				}
			}
		}catch(Exception e){
			LOGGER.error("Job Launch Failed with Exception.  ", e)
		}
	}

	void setStatus(boolean enabled) {
		grailsApplication.config.scheduler.disabled = !enabled
	}
	
	boolean isDisabled() {
		return grailsApplication.config.scheduler.disabled
	}
}
