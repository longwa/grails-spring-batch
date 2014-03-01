package grails.plugins.springbatch.ui

import grails.plugins.springbatch.model.StepExecutionModel;
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin

import org.junit.Test
import org.springframework.batch.admin.service.JobService
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.StepExecution

@TestMixin(GrailsUnitTestMixin)
class StepExecutionModelUnitTests {

    @Test
    void testFromService() {

        def jobServiceMock = mockFor(JobService)

        JobExecution jobExecution = new JobExecution(2)
        StepExecution stepExecution = new StepExecution("testStep1", jobExecution, 1)
        stepExecution.startTime = new Date()
        stepExecution.endTime = dateWithDuration(new Date(), 15000)
        stepExecution.status = BatchStatus.ABANDONED
        stepExecution.readCount = 3
        stepExecution.writeCount = 5
        stepExecution.writeSkipCount = 7
        stepExecution.readSkipCount = 9
        stepExecution.processSkipCount = 11
        stepExecution.exitStatus = ExitStatus.UNKNOWN


        StepExecutionModel stepExecutionModel = StepExecutionModel.fromService(jobServiceMock as JobService, stepExecution)

        assert stepExecutionModel
        assert stepExecution.id == stepExecutionModel.id
        assert stepExecution.jobExecutionId == stepExecutionModel.jobExecutionId
        assert stepExecution.stepName == stepExecutionModel.name
        assert stepExecution.startTime == stepExecutionModel.startDateTime
        assert stepExecution.endTime == dateWithDuration(stepExecutionModel.startDateTime, stepExecutionModel.duration)
        assert stepExecution.status == stepExecutionModel.status
        assert stepExecution.readCount == stepExecutionModel.reads
        assert stepExecution.writeCount == stepExecutionModel.writes
        assert stepExecution.skipCount == stepExecutionModel.skips
        assert stepExecution.exitStatus == stepExecutionModel.exitStatus

        jobServiceMock.verify()
    }

    private Date dateWithDuration(Date date, long duration) {
        new Date(date.time + duration)
    }
}
