package grails.plugins.springbatch.ui

import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParameters
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.JobInstance

class JobInstanceModel {

    Long id
    Integer jobExecutionCount
    BatchStatus lastJobExecutionStatus
    JobParameters jobParameters

    List<JobExecutionModel> executions

    public static JobInstanceModel fromService(JobService jobService, JobInstance jobInstance) {
        def jobExecutions = jobService.getJobExecutionsForJobInstance(jobInstance.jobName, jobInstance.id)
        return new JobInstanceModel(
            id: jobInstance.id,
            jobExecutionCount: jobExecutions.size(),
            lastJobExecutionStatus: jobExecutions[0]?.status,
            jobParameters: jobInstance.jobParameters,
            executions: jobExecutions
        )
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JobInstanceModel that = (JobInstanceModel) o

        if (executions != that.executions) return false
        if (id != that.id) return false
        if (jobExecutionCount != that.jobExecutionCount) return false
        if (jobParameters != that.jobParameters) return false
        if (lastJobExecutionStatus != that.lastJobExecutionStatus) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (jobExecutionCount != null ? jobExecutionCount.hashCode() : 0)
        result = 31 * result + (lastJobExecutionStatus != null ? lastJobExecutionStatus.hashCode() : 0)
        result = 31 * result + (jobParameters != null ? jobParameters.hashCode() : 0)
        result = 31 * result + (executions != null ? executions.hashCode() : 0)
        return result
    }
}
