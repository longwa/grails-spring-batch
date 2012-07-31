package grails.plugins.springbatch.ui

import org.springframework.batch.admin.service.JobService
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
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
}