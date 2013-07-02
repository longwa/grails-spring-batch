package grails.plugins.springbatch.ui

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters

@EqualsAndHashCode
class JobInstanceModel {

    Long id
    Integer jobExecutionCount
    BatchStatus lastJobExecutionStatus
    JobParameters jobParameters

    List<JobExecutionModel> executions

    static JobInstanceModel fromService(JobService jobService, JobInstance jobInstance) {
        def jobExecutions = jobService.getJobExecutionsForJobInstance(jobInstance.jobName, jobInstance.id)
        return new JobInstanceModel(
            id: jobInstance.id,
            jobExecutionCount: jobExecutions.size(),
            lastJobExecutionStatus: jobExecutions[0]?.status,
            jobParameters: jobInstance.jobParameters,
            executions: jobExecutions
        )
    }
	
	boolean isStoppable(){
		return lastJobExecutionStatus == BatchStatus.STARTED
	}
}
