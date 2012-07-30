package grails.plugins.springbatch.ui

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.StepExecution

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

    public static StepExecutionModel fromService(JobService jobService, StepExecution stepExecution) {
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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        StepExecutionModel that = (StepExecutionModel) o

        if (duration != that.duration) return false
        if (exitStatus != that.exitStatus) return false
        if (id != that.id) return false
        if (jobExecutionId != that.jobExecutionId) return false
        if (name != that.name) return false
        if (reads != that.reads) return false
        if (skips != that.skips) return false
        if (startDateTime != that.startDateTime) return false
        if (status != that.status) return false
        if (writes != that.writes) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (jobExecutionId != null ? jobExecutionId.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (startDateTime != null ? startDateTime.hashCode() : 0)
        result = 31 * result + (duration != null ? duration.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (reads != null ? reads.hashCode() : 0)
        result = 31 * result + (writes != null ? writes.hashCode() : 0)
        result = 31 * result + (skips != null ? skips.hashCode() : 0)
        result = 31 * result + (exitStatus != null ? exitStatus.hashCode() : 0)
        return result
    }
}
