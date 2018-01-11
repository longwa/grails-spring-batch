package scheduling

import grails.plugins.springbatch.SimpleScheduleService

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class Schedule {
	
	private static final Logger LOGGER = Logger.getLogger(Schedule)
	
	@Autowired
	SimpleScheduleService simpleScheduleService
	
	@Scheduled(fixedDelay=10000L)
	void simpleAsyncJobTrigger(){
		simpleScheduleService.trigger('simpleAsyncJob')
	}
	
	@Scheduled(fixedDelay=10000L)
	void simpleSyncJob1Trigger(){
		simpleScheduleService.trigger('simpleSyncJob1', false)
	}
	
	@Scheduled(fixedDelay=10000L)
	void simpleSyncJob2Trigger(){
		simpleScheduleService.trigger('simpleSyncJob2', false)
	}
}
