package grails.plugins.springbatch.ui

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution

@EqualsAndHashCode
class JobExecutionModel {

    Long id
    Long instanceId
    String name
    Date startDateTime
    Long duration
    BatchStatus status
    ExitStatus exitStatus

    List<StepExecutionModel> stepExecutions

    static JobExecutionModel fromService(JobService jobService, JobExecution jobExecution) {
        return new JobExecutionModel(
            id: jobExecution.id,
            instanceId:  jobExecution.jobInstance.id,
            name:  jobExecution.jobInstance.jobName,
            startDateTime: jobExecution.startTime,
            duration: SpringBatchUiUtilities.getDuration(jobExecution.startTime, jobExecution.endTime),
            status: jobExecution.status,
            exitStatus: jobExecution.exitStatus,
            stepExecutions: jobExecution.stepExecutions.collect { StepExecutionModel.fromService(jobService, it) }
        )
    }
}
