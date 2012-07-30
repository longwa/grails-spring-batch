package grails.plugins.springbatch.ui

import org.springframework.batch.admin.service.JobService

class JobModel {

    String name
    Integer executionCount
    Boolean launchable
    Boolean incrementable

    List<JobInstanceModel> instances

    public static JobModel fromService(JobService jobService, String name) {
        return new JobModel(
            name: name,
            executionCount: jobService.countJobExecutionsForJob(name),
            launchable: jobService.isLaunchable(name),
            incrementable: jobService.isIncrementable(name)
        )
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        JobModel jobModel = (JobModel) o

        if (executionCount != jobModel.executionCount) return false
        if (incrementable != jobModel.incrementable) return false
        if (instances != jobModel.instances) return false
        if (launchable != jobModel.launchable) return false
        if (name != jobModel.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (name != null ? name.hashCode() : 0)
        result = 31 * result + (executionCount != null ? executionCount.hashCode() : 0)
        result = 31 * result + (launchable != null ? launchable.hashCode() : 0)
        result = 31 * result + (incrementable != null ? incrementable.hashCode() : 0)
        result = 31 * result + (instances != null ? instances.hashCode() : 0)
        return result
    }
}
