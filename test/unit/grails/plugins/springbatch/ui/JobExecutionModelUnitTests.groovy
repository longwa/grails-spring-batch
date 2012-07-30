package grails.plugins.springbatch.ui

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution

@TestMixin(GrailsUnitTestMixin)
class JobExecutionModelUnitTests {

    @Test
    public void testFromService() {

        def jobServiceMock = mockFor(JobService)
        JobInstance jobInstance = new JobInstance(1, null, "simpleJob")
        JobExecution jobExecution = new JobExecution(jobInstance, 1)
        jobExecution.startTime = new Date()
        jobExecution.endTime = dateWithDuration(new Date(), 10000)
        jobExecution.status = BatchStatus.COMPLETED
        jobExecution.exitStatus = ExitStatus.COMPLETED

        StepExecution stepExecution = new StepExecution("step1", jobExecution)
        stepExecution.startTime = new Date()
        stepExecution.endTime = dateWithDuration(stepExecution.startTime, 20000)
        jobExecution.addStepExecutions([stepExecution])

        def jobExecutionModel = JobExecutionModel.fromService(jobServiceMock.createMock() as JobService, jobExecution)

        assert jobExecutionModel
        assert jobExecution.id == jobExecutionModel.id
        assert jobExecution.jobInstance.id == jobExecutionModel.instanceId
        assert jobExecution.jobInstance.jobName == jobExecutionModel.name
        assert jobExecution.startTime == jobExecutionModel.startDateTime
        assert jobExecution.endTime == dateWithDuration(jobExecutionModel.startDateTime, jobExecutionModel.duration)
        assert jobExecution.status == jobExecutionModel.status
        assert jobExecution.exitStatus == jobExecutionModel.exitStatus

        assert jobExecution.stepExecutions.collect {
          StepExecutionModel.fromService(jobServiceMock.createMock() as JobService, it)
        } == jobExecutionModel.stepExecutions

        jobServiceMock.verify()
    }

    private Date dateWithDuration(Date date, long duration) {
        new Date(date.time + duration)
    }
}
