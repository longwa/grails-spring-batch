package grails.plugins.springbatch.model

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.core.BatchStatus

@EqualsAndHashCode
class JobInstanceModel {

    Long id
    String jobName

    Integer jobExecutionCount
    BatchStatus lastJobExecutionStatus

	List<JobExecutionModel> executions

	boolean isStoppable() {
		return lastJobExecutionStatus == BatchStatus.STARTED
	}

	Map getJobParameters() {
		executions ? executions.last().jobParameters : [:]
	}
}
