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
		if(!ready) {
			log.info("Attempted to trigger, but app is not fully initialized")
			return
		}
		
		LOGGER.info("Triggering SimpleAsyncJob...")
		
		try{
			springBatchService.launch("simpleAsyncJob", null, 'jobLauncher')
		}catch(Exception e){
			LOGGER.error("Job Failure", e)
		}
		
		LOGGER.info("Completing trigger of SimpleAsyncJob...")
	}
	
	boolean ready = false
}
