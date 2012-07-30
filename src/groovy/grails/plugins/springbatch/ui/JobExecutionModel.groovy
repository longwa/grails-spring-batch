package grails.plugins.springbatch.ui

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.JobExecution

class JobExecutionModel {

    Long id
    Long instanceId
    String name
    Date startDateTime
    Long duration
    BatchStatus status
    ExitStatus exitStatus

    List<StepExecutionModel> stepExecutions

    public static JobExecutionModel fromService(JobService jobService, JobExecution jobExecution) {
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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JobExecutionModel that = (JobExecutionModel) o

        if (duration != that.duration) return false
        if (exitStatus != that.exitStatus) return false
        if (id != that.id) return false
        if (instanceId != that.instanceId) return false
        if (name != that.name) return false
        if (startDateTime != that.startDateTime) return false
        if (status != that.status) return false
        if (stepExecutions != that.stepExecutions) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (startDateTime != null ? startDateTime.hashCode() : 0)
        result = 31 * result + (duration != null ? duration.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (exitStatus != null ? exitStatus.hashCode() : 0)
        result = 31 * result + (stepExecutions != null ? stepExecutions.hashCode() : 0)
        return result
    }
}
