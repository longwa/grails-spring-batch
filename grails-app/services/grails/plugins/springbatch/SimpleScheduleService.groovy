package grails.plugins.springbatch

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This service facilitates usage of the SpringBatchService with headless clients
 * like scheduling.  It will log the return values of the SpringBatchService with
 * sensible defaults, and should wrap the job in a try/catch to avoid bubbling
 * exceptions up to the scheduler.
 * 
 * Optimal usage (Example with Spring @Scheduled annotation):
 * 
 * <pre>
 * {@code
 * @Scheduled(fixedDelay=10000L)
 * void simpleTrigger(){
 *     simpleScheduleService.trigger('myJob')
 * }
 * }
 * </pre>
 *
 */
class SimpleScheduleService {
	
	GrailsApplication grailsApplication
	
	SpringBatchService springBatchService
	
	static transactional = false
	
	/**
	 * Arguments are simple passthroughs to SpringBatchService.launch(
	 */
	void trigger(String jobName, boolean canBeConcurrent = true, String launcherName = null) {
		if(isDisabled()) {
			log.info("Attempted to trigger $jobName, but scheduler is disabled")
			return
		}
		
		log.info("Triggering $jobName")
		
		try{
			Map result = springBatchService.launch(jobName, canBeConcurrent, null, launcherName)
			
			if(result.success) {
				log.info("Completed trigger of $jobName.  Result: $result")
			}else {
				if(result.failurePriority == 'high') {
					log.error("Job Launch Failed.  Result: $result")
				}else {
					log.warn("Job Launch Failed.  Result: $result")
				}
			}
		}catch(Exception e){
			log.error("Job Launch Failed with Exception.  ", e)
		}
	}

	/**
	 * In addition to setting the scheduler status at startup with the config
	 * value:  grailsApplication.config.scheduler.disabled, you can also 
	 * manipulate it at runtime
	 * @param enabled
	 */
	void setStatus(boolean enabled) {
		grailsApplication.config.scheduler.disabled = !enabled
	}
	
	boolean isDisabled() {
		return grailsApplication.config.scheduler.disabled
	}
}
