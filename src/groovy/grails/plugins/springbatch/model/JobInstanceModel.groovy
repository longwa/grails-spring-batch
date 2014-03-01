package grails.plugins.springbatch.model

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters

@EqualsAndHashCode
class JobInstanceModel {

    Long id
    String jobName
	
    Integer jobExecutionCount
    BatchStatus lastJobExecutionStatus

	List<JobExecutionModel> executions
	
	// :TODO JobParameters
	
	boolean isStoppable(){
		return lastJobExecutionStatus == BatchStatus.STARTED
	}
}
