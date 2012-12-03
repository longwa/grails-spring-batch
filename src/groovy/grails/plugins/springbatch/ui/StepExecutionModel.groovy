package grails.plugins.springbatch.ui

import groovy.transform.EqualsAndHashCode

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution

@EqualsAndHashCode
class StepExecutionModel {

    Long id
    Long jobExecutionId
    String name
    Date startDateTime
    Long duration
    BatchStatus status
    Integer reads
    Integer writes
    Integer skips
    ExitStatus exitStatus

    static StepExecutionModel fromService(JobService jobService, StepExecution stepExecution) {
        return new StepExecutionModel(
            id: stepExecution.id,
            jobExecutionId: stepExecution.jobExecutionId,
            name: stepExecution.stepName,
            startDateTime: stepExecution.startTime,
            duration: SpringBatchUiUtilities.getDuration(stepExecution.startTime, stepExecution.endTime),
            status: stepExecution.status,
            reads: stepExecution.readCount,
            writes: stepExecution.writeCount,
            skips: stepExecution.skipCount,
            exitStatus: stepExecution.exitStatus
        )
    }
}
